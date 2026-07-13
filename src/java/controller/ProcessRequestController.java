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

        if ("detail".equals(action)) {
            String reqIdStr = request.getParameter("requestId");
            if (reqIdStr != null && !reqIdStr.isEmpty()) {
                int requestId = Integer.parseInt(reqIdStr);
                GuestRequestDTO detail = requestDAO.getRequestForProcessing(requestId);
                if (detail != null) {
                    boolean isAvailable = true;

                    // 1. Kiểm tra phòng trống theo từng loại yêu cầu
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

                        // SỬA: Đưa detail.getBookingId() thay cho null để câu lệnh SQL loại trừ chính nó, không tự quét trúng mình gây treo.
                        isAvailable = requestDAO.checkRoomAvailability(
                                detail.getRoomTypeId(),
                                oldCheckout,
                                newCheckOut,
                                detail.getNumRooms(),
                                detail.getBookingId()
                        );
                    }

                    // 2. Đối soát khung thời gian và giá tiền hủy đặt phòng
                    if ("Hủy đặt phòng".equals(detail.getRequestType())) {
                        try {
                            LocalDateTime checkInDeadline = detail.getCheckInDate().atTime(14, 0, 0);
                            LocalDateTime submittedAt = detail.getSubmittedAt();

                            if (submittedAt != null) {
                                Duration duration = Duration.between(submittedAt, checkInDeadline);
                                long totalHoursLeft = duration.toHours();
                                long totalMinutesLeft = duration.toMinutes() % 60;

                                String durationText;
                                double currentRefundRate = 1.0;
                                String feePercentText = "0%";

                                if (totalHoursLeft < 0) {
                                    durationText = "Đã quá mốc giờ Check-in";
                                    currentRefundRate = 0.3;
                                    feePercentText = "70%";
                                } else {
                                    durationText = "~ " + totalHoursLeft + " giờ " + Math.abs(totalMinutesLeft) + " phút";

                                    if (totalHoursLeft >= 72) {
                                        currentRefundRate = 1.0;
                                        feePercentText = "0%";
                                    } else if (totalHoursLeft >= 48) {
                                        currentRefundRate = 0.7; // ĐÃ SỬA: Đồng bộ biến chuẩn tránh Null
                                        feePercentText = "30%";
                                    } else if (totalHoursLeft >= 24) {
                                        currentRefundRate = 0.5;
                                        feePercentText = "50%";
                                    } else {
                                        currentRefundRate = 0.3;
                                        feePercentText = "70%";
                                    }
                                }

                                BigDecimal currentPrice = detail.getCurrentPrice() != null ? detail.getCurrentPrice() : BigDecimal.ZERO;
                                BigDecimal refundAmount = currentPrice.multiply(BigDecimal.valueOf(currentRefundRate));
                                BigDecimal feeAmount = currentPrice.subtract(refundAmount);

                                request.setAttribute("cancelDurationText", durationText);
                                request.setAttribute("cancelFeePercentText", feePercentText);
                                request.setAttribute("cancelRefundAmount", refundAmount);
                                request.setAttribute("cancelFeeAmount", feeAmount);
                                request.setAttribute("hoursLeft", totalHoursLeft);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    request.setAttribute("req", detail);
                    request.setAttribute("isAvailable", isAvailable);
                }
            }
        }

        request.setAttribute("requestList", requestDAO.getRequestsByFilters(type, status));
        request.setAttribute("currentType", type);
        request.setAttribute("currentStatus", status);

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

        String encType = java.net.URLEncoder.encode(type, "UTF-8");
        String encStatus = java.net.URLEncoder.encode(status, "UTF-8");

        if (reqIdStr == null || reqIdStr.isEmpty()) {
            response.sendRedirect("process-request?type=" + encType + "&status=" + encStatus + "&status_msg=error");
            return;
        }

        int requestId = Integer.parseInt(reqIdStr);
        GuestRequestDTO dto = requestDAO.getRequestForProcessing(requestId);

        if (dto == null) {
            response.sendRedirect("process-request?type=" + encType + "&status=" + encStatus + "&status_msg=not_found");
            return;
        }

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

                // SỬA: Thay dto.getCheckInDate() thành dto.getCheckOutDate() để đồng bộ logic với doGet
                isAvailable = requestDAO.checkRoomAvailability(
                        dto.getRoomTypeId(),
                        dto.getCheckOutDate(),
                        newCheckOut,
                        dto.getNumRooms(),
                        dto.getBookingId()
                );
            }
            if (isAvailable) {

                System.out.println("===== START APPROVE =====");

                boolean success = requestDAO.approveRequest(dto, notes);

                System.out.println("approveRequest() = " + success);

                System.out.println("Preparing redirect...");

                response.sendRedirect(
                        "process-request?type=" + encType
                        + "&status=" + encStatus
                        + "&status_msg="
                        + (success ? "approve_success" : "error"));

                System.out.println("Redirect sent.");
            } else {
                response.sendRedirect("process-request?action=detail&requestId=" + requestId + "&type=" + encType + "&status=" + encStatus + "&status_msg=no_room");
            }
        } else {
            requestDAO.rejectRequest(requestId, notes);
            response.sendRedirect("process-request?type=" + encType + "&status=" + encStatus + "&status_msg=reject_success");
        }
    }
}
