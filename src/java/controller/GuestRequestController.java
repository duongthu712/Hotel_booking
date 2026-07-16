package controller;

import dao.GuestRequestDAO;
import dao.RoomTypeDAO;
import dao.BookingDAO;
import model.Booking;
import model.Guest;
import model.RoomType;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "GuestRequestController", urlPatterns = {"/guest-request"})
public class GuestRequestController extends HttpServlet {

    private final GuestRequestDAO requestDAO = new GuestRequestDAO();
    private final RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String bookingCode = request.getParameter("bookingCode");

        // 1. Kiểm tra mã đặt phòng
        if (bookingCode == null || bookingCode.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/booking-detail?status=invalid_code");
            return;
        }

        Booking booking = requestDAO.getBookingBasicInfoByCode(bookingCode);

        // 2. Xử lý trường hợp không tìm thấy booking
        if (booking == null) {
            response.sendRedirect(request.getContextPath() + "/booking-detail?status=not_found");
            return;
        }

        // 3. Bảo mật: Chặn nếu đã có request "Chờ xử lý"
        if (requestDAO.hasPendingRequest(booking.getBookingId())) {
            Guest guest = bookingDAO.getGuestByBookingId(booking.getBookingId());
            RoomType roomType = roomTypeDAO.getRoomDetailById(booking.getRoomTypeId());
            String status = bookingDAO.getDepositVerificationStatus(booking.getBookingId());

            request.setAttribute("booking", booking);
            request.setAttribute("guest", guest);
            request.setAttribute("bookingStatus", booking.getStatus());
            request.setAttribute("roomType", roomType);
            request.setAttribute("verificationStatus", (status == null) ? "Chưa gửi minh chứng" : status);
            request.setAttribute("searched", true);
            request.setAttribute("status", "duplicate_pending_error");

            request.getRequestDispatcher("/view/user/booking-detail.jsp").forward(request, response);
            return;
        }

        // 4. Hiển thị form gửi yêu cầu
        List<RoomType> roomTypesList = roomTypeDAO.getAllRoomTypes();
        request.setAttribute("booking", booking);
        request.setAttribute("roomTypesList", roomTypesList);

        request.getRequestDispatcher("/view/user/request-submission.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Lấy tham số chung 
            int bookingId = Integer.parseInt(request.getParameter("bookingId"));
            int guestId = Integer.parseInt(request.getParameter("guestId"));
            String requestType = request.getParameter("requestType");
            String bookingCode = request.getParameter("bookingCode");
            String email = request.getParameter("email");
            String emailParam = (email != null && !email.trim().isEmpty()) ? "&email=" + email : "";

            // Lấy thông tin mới nhất từ DB
            Booking booking = requestDAO.getBookingBasicInfoByCode(bookingCode);
            String bStatus = booking.getStatus();

            // Phân quyền theo trạng thái đơn hàng
            if ("Đã trả phòng".equals(bStatus) || "Đã hủy".equals(bStatus)) {
                response.sendRedirect(request.getContextPath() + "/booking-detail?status=request_not_allowed");
                return;
            }

            if (!isAuthorized(requestType, bStatus)) {
                response.sendRedirect(request.getContextPath() + "/booking-detail?status=cannot_" + requestType.toLowerCase());
                return;
            }

            if (requestDAO.hasPendingRequest(bookingId)) {
                response.sendRedirect(request.getContextPath() + "/booking-detail?bookingCode=" + bookingCode + emailParam + "&status=duplicate_pending_error");
                return;
            }

            boolean isSuccess = processRequest(request, booking, bookingId, guestId, requestType);

            if (isSuccess) {
                

                if (email != null && !email.trim().isEmpty()) {
                    final String targetEmail = email.trim();
                    final String finalRequestType = requestType;
                    final String finalBookingCode = bookingCode;
                    
                    // Trích xuất lý do động chuẩn theo các tham số nguyên bản từ form JSP
                    String emailReason = "";
                    if ("Đổi hạng phòng".equals(requestType)) {
                        emailReason = request.getParameter("reason_details");
                        try {
                            int targetId = Integer.parseInt(request.getParameter("targetRoomTypeId"));
                            RoomType rt = roomTypeDAO.getRoomDetailById(targetId);
                            if (rt != null) {
                                emailReason = "[Mong muốn đổi sang hạng phòng: " + rt.getTypeName() + "] - Lý do: " + emailReason;
                            }
                        } catch (Exception ignored) {}
                    } else if ("Gia hạn phòng".equals(requestType)) {
                        emailReason = request.getParameter("reason_details_extend");
                        String newCheckoutStr = request.getParameter("newCheckoutDate");
                        emailReason = "[Mong muốn gia hạn đến ngày: " + newCheckoutStr + "] - Lý do: " + emailReason;
                    } else if ("Hủy đặt phòng".equals(requestType)) {
                        emailReason = request.getParameter("reason_details_cancel");
                        if (emailReason == null || emailReason.trim().isEmpty()) {
                            emailReason = request.getParameter("reason_details");
                        }
                        if (emailReason == null || emailReason.trim().isEmpty()) {
                            emailReason = "Khách hàng gửi yêu cầu hủy đơn hàng.";
                        }
                    }
                    
                    final String finalReason = emailReason;

                    // Tạo Thread chạy ngầm xử lý gửi mail bất đồng bộ
                    new Thread(() -> {
                        try {
                            Guest currentGuest = bookingDAO.getGuestByBookingId(bookingId);
                            String guestName = (currentGuest != null) ? currentGuest.getFullName() : "Quý khách";
                            
                            dal.EmailUtil.sendRequestSubmittedNotification(
                                    targetEmail, 
                                    guestName, 
                                    finalBookingCode, 
                                    finalRequestType, 
                                    finalReason
                            );
                        } catch (Exception e) {
                            e.printStackTrace(); // Ghi nhận lỗi SMTP ra console nếu phát sinh, không block Client redirect
                        }
                    }).start();
                }
         

                response.sendRedirect(request.getContextPath() + "/booking-detail?bookingCode=" + bookingCode + emailParam + "&status=request_success");
            } else {
                // Thất bại: Giữ người dùng ở lại trang /guest-request để sửa lỗi
                String encodedType = java.net.URLEncoder.encode(requestType, "UTF-8");
                if ("Hủy đặt phòng".equals(requestType)) {
                    response.sendRedirect(request.getContextPath() + "/guest-request?bookingCode=" + bookingCode + emailParam + "&status=cancel_failed&failedType=" + encodedType);
                } else {
                    response.sendRedirect(request.getContextPath() + "/guest-request?bookingCode=" + bookingCode + emailParam + "&status=request_failed_no_room&failedType=" + encodedType);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/booking-detail?status=system_error");
        }
    }

    // Những requets đc làm trong các tình huống 
    private boolean isAuthorized(String requestType, String status) {
        return switch (requestType) {
            case "Hủy đặt phòng" ->
                status.equals("Chờ xử lý") || status.equals("Đã xác nhận");
            case "Đổi hạng phòng" ->
                status.equals("Đã xác nhận");
            case "Gia hạn phòng" ->
                status.equals("Đã xác nhận") || status.equals("Đã nhận phòng");
            default ->
                false;
        };
    }

    // Xử lý request 
    private boolean processRequest(HttpServletRequest req, Booking booking, int bookingId, int guestId, String type) {
        switch (type) {
            case "Đổi hạng phòng": {
                int numRooms = Integer.parseInt(req.getParameter("numRooms"));
                int targetRoomTypeId = Integer.parseInt(req.getParameter("targetRoomTypeId"));
                LocalDate checkIn = LocalDate.parse(req.getParameter("checkInDate"));
                LocalDate checkOut = LocalDate.parse(req.getParameter("oldCheckoutDate"));

                if (requestDAO.checkRoomAvailability(targetRoomTypeId, checkIn, checkOut, numRooms, bookingId)) {
                    return requestDAO.insertGuestRequest(bookingId, guestId, type, req.getParameter("reason_details"), null, null, targetRoomTypeId);
                }
                return false;
            }

            case "Gia hạn phòng": {
                int numRooms = Integer.parseInt(req.getParameter("numRooms"));
                LocalDate oldCheckout = booking.getCheckoutDate();
                LocalDate newCheckout = LocalDate.parse(req.getParameter("newCheckoutDate"));

                if (requestDAO.checkRoomAvailability(booking.getRoomTypeId(), oldCheckout, newCheckout, numRooms, null)) {
                    return requestDAO.insertGuestRequest(bookingId, guestId, type, req.getParameter("reason_details_extend"), null, newCheckout, null);
                }
                return false;
            }

            case "Hủy đặt phòng": {
                String cancelReason = req.getParameter("reason_details_cancel");
                if (cancelReason == null || cancelReason.trim().isEmpty()) {
                    cancelReason = req.getParameter("reason_details");
                }
                if (cancelReason == null || cancelReason.trim().isEmpty()) {
                    cancelReason = "Khách hàng gửi yêu cầu hủy đơn hàng.";
                }

                int safeGuestId = (guestId > 0) ? guestId : 1;

                // Thực hiện ghi nhận yêu cầu hủy đặt phòng trực tiếp vào DB
                return requestDAO.insertGuestRequest(bookingId, safeGuestId, type, cancelReason, null, null, null);
            }

            default:
                return false;
        }
    }
}