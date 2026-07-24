/**
 * Author: ThuDNM-HE204370
 * Date created: 20/06/2026
 * Purpose: Controller logic for ProcessRequestController.
 */
package controller;

import dao.GuestRequestDAO;
import dto.GuestRequestDTO;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Duration;
import java.math.BigDecimal;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ProcessRequestController", urlPatterns = {"/process-request"})
public class ProcessRequestController extends HttpServlet {

    private final GuestRequestDAO requestDAO = new GuestRequestDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String type = request.getParameter("type") != null ? request.getParameter("type") : "Tất cả";
        String status = request.getParameter("status") != null ? request.getParameter("status") : "Tất cả";
        String searchBookingCode = request.getParameter("searchBookingCode");

        if ("detail".equals(action)) {
            String reqIdStr = request.getParameter("requestId");
            if (reqIdStr != null && !reqIdStr.isEmpty()) {
                int requestId = Integer.parseInt(reqIdStr);
                GuestRequestDTO detail = requestDAO.getRequestForProcessing(requestId);
                if (detail != null) {
                    boolean isAvailable = true;

                    if ("Đổi hạng phòng".equals(detail.getRequestType())) {
                        isAvailable = requestDAO.checkRoomAvailability(
                                detail.getTargetRoomTypeId(), detail.getCheckInDate(),
                                detail.getCheckOutDate(), detail.getNumRooms(), null
                        );
                    } else if ("Gia hạn phòng".equals(detail.getRequestType())) {
                        LocalDate oldCheckout = detail.getCheckOutDate();
                        LocalDate newCheckOut = detail.getRequestedCheckout() != null
                                ? detail.getRequestedCheckout().toLocalDate()
                                : detail.getCheckOutDate();

                        isAvailable = requestDAO.checkRoomAvailability(
                                detail.getRoomTypeId(),
                                oldCheckout,
                                newCheckOut,
                                detail.getNumRooms(),
                                detail.getBookingId()
                        );
                    }

                    if ("Hủy đặt phòng".equals(detail.getRequestType())) {
                        try {
                            double[] financials = calculateFinancials(detail);
                            double totalBookingValue = financials[0];
                            double depositPaid = financials[1];
                            double refundPercent = financials[2];
                            double finalRefund = financials[3];
                            double penaltyFee = financials[4];
                            long totalHoursLeft = (long) financials[5];
                            long totalMinutesLeft = (long) financials[6];

                            String durationText;
                            String feePercentText = ((int)((1 - refundPercent) * 100)) + "%";

                            if (totalHoursLeft < 0) {
                                durationText = "Đã quá mốc giờ Check-in";
                            } else {
                                durationText = "~ " + totalHoursLeft + " giờ " + Math.abs(totalMinutesLeft) + " phút";
                            }

                            request.setAttribute("cancelDurationText", durationText);
                            request.setAttribute("cancelFeePercentText", feePercentText);
                            request.setAttribute("cancelRefundAmount", BigDecimal.valueOf(finalRefund));
                            request.setAttribute("cancelFeeAmount", BigDecimal.valueOf(penaltyFee));
                            request.setAttribute("hoursLeft", totalHoursLeft);
                            request.setAttribute("totalBookingValue", BigDecimal.valueOf(totalBookingValue));
                            request.setAttribute("depositPaid", BigDecimal.valueOf(depositPaid));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    request.setAttribute("req", detail);
                    request.setAttribute("isAvailable", isAvailable);
                }
            }
        }

        request.setAttribute("requestList", requestDAO.getRequestsByFilters(type, status, searchBookingCode));
        request.setAttribute("currentType", type);
        request.setAttribute("currentStatus", status);
        request.setAttribute("searchBookingCode", searchBookingCode);

        request.getRequestDispatcher("/view/receptionist/request-processing.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String reqIdStr = request.getParameter("requestId");
        String notes = request.getParameter("response_notes");
        String type = request.getParameter("type") != null ? request.getParameter("type") : "Tất cả";
        String status = request.getParameter("status") != null ? request.getParameter("status") : "Tất cả";
        String searchBookingCode = request.getParameter("searchBookingCode");

        String encType = java.net.URLEncoder.encode(type, "UTF-8");
        String encStatus = java.net.URLEncoder.encode(status, "UTF-8");
        String extraParams = "&type=" + encType + "&status=" + encStatus 
                           + (searchBookingCode != null && !searchBookingCode.isEmpty() ? "&searchBookingCode=" + searchBookingCode : "");

        if (reqIdStr == null || reqIdStr.isEmpty()) {
            response.sendRedirect("process-request?status_msg=error" + extraParams);
            return;
        }

        int requestId = Integer.parseInt(reqIdStr);
        GuestRequestDTO dto = requestDAO.getRequestForProcessing(requestId);

        if (dto == null) {
            response.sendRedirect("process-request?status_msg=not_found" + extraParams);
            return;
        }

        // Khởi tạo DAO phục vụ cho việc lấy Email chuẩn từ bảng liên kết hệ thống
        dao.BookingDAO bookingDAO = new dao.BookingDAO();

        if ("approve".equals(action)) {
            boolean isAvailable = true;

            if ("Đổi hạng phòng".equals(dto.getRequestType())) {
                isAvailable = requestDAO.checkRoomAvailability(
                        dto.getTargetRoomTypeId(), dto.getCheckInDate(),
                        dto.getCheckOutDate(), dto.getNumRooms(), null
                );
            } else if ("Gia hạn phòng".equals(dto.getRequestType())) {
                LocalDate newCheckOut = dto.getCheckOutDate();
                if (dto.getRequestedCheckout() != null) {
                    newCheckOut = dto.getRequestedCheckout().toLocalDate();
                }

                isAvailable = requestDAO.checkRoomAvailability(
                        dto.getRoomTypeId(),
                        dto.getCheckOutDate(),
                        newCheckOut,
                        dto.getNumRooms(),
                        dto.getBookingId()
                );
            }
            if (isAvailable) {

                double penaltyFee = 0;
                if ("Hủy đặt phòng".equals(dto.getRequestType())) {
                    double[] financials = calculateFinancials(dto);
                    penaltyFee = financials[4];
                }

                boolean success = requestDAO.approveRequest(dto, notes, penaltyFee);
                
                if (success) {
                    try {
                        model.Guest currentGuest = bookingDAO.getGuestByBookingId(dto.getBookingId());
                        if (currentGuest != null && currentGuest.getEmail() != null && !currentGuest.getEmail().trim().isEmpty()) {
                            final String targetEmail = currentGuest.getEmail().trim();
                            final String guestName = dto.getGuestName() != null ? dto.getGuestName() : currentGuest.getFullName();
                            final String bookingCode = dto.getBookingCode();
                            final String reqType = dto.getRequestType();
                            final String finalNotes = notes;

                            new Thread(() -> {
                                try {
                                    dal.EmailUtil.sendRequestVerificationResult(
                                            targetEmail, guestName, bookingCode, reqType, true, finalNotes
                                    );
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }).start();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                response.sendRedirect("process-request?status_msg=" + (success ? "approve_success" : "error") + extraParams);

                System.out.println("Redirect sent.");
            } else {
                response.sendRedirect("process-request?action=detail&requestId=" + requestId + extraParams + "&status_msg=no_room");
            }
        } else {
            boolean success = requestDAO.rejectRequest(requestId, notes);
            
            if (success) {
                try {
                    model.Guest currentGuest = bookingDAO.getGuestByBookingId(dto.getBookingId());
                    if (currentGuest != null && currentGuest.getEmail() != null && !currentGuest.getEmail().trim().isEmpty()) {
                        final String targetEmail = currentGuest.getEmail().trim();
                        final String guestName = dto.getGuestName() != null ? dto.getGuestName() : currentGuest.getFullName();
                        final String bookingCode = dto.getBookingCode();
                        final String reqType = dto.getRequestType();
                        final String finalNotes = notes;

                        new Thread(() -> {
                            try {
                                dal.EmailUtil.sendRequestVerificationResult(
                                        targetEmail, guestName, bookingCode, reqType, false, finalNotes
                                );
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            response.sendRedirect("process-request?status_msg=reject_success" + extraParams);
        }
    }

    private double[] calculateFinancials(dto.GuestRequestDTO detail) {
        try {
            double pricePerNight = detail.getCurrentPrice() != null ? detail.getCurrentPrice().doubleValue() : 0;
            long totalNights = java.time.temporal.ChronoUnit.DAYS.between(detail.getCheckInDate(), detail.getCheckOutDate());
            if (totalNights <= 0) {
                totalNights = 1;
            }

            double totalBookingValue = pricePerNight * totalNights * detail.getNumRooms();
            double depositPaid = totalBookingValue * 0.30; 

            java.time.LocalDateTime checkInDateTime = detail.getCheckInDate().atTime(14, 0, 0);
            java.time.LocalDateTime requestTime = detail.getSubmittedAt();
            if (requestTime == null) {
                requestTime = java.time.LocalDateTime.now();
            }
            
            java.time.Duration duration = java.time.Duration.between(requestTime, checkInDateTime);
            long totalHoursLeft = duration.toHours();

            double refundPercent = 0.30; 
            if (totalHoursLeft >= 72) {
                refundPercent = 1.00;   
            } else if (totalHoursLeft >= 48) {
                refundPercent = 0.70;   
            } else if (totalHoursLeft >= 24) {
                refundPercent = 0.50;   
            }

            double finalRefund = depositPaid * refundPercent; 
            double penaltyFee = depositPaid - finalRefund;     
            
            return new double[]{totalBookingValue, depositPaid, refundPercent, finalRefund, penaltyFee, totalHoursLeft, duration.toMinutes() % 60};
        } catch (Exception e) {
            e.printStackTrace();
            return new double[]{0, 0, 0, 0, 0, 0, 0};
        }
    }
}