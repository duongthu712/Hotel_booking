package controller;

import dao.GuestRequestDAO;
import dao.RoomTypeDAO;
import model.Booking;
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String bookingCode = request.getParameter("bookingCode");
        String email = request.getParameter("email"); // Lấy email từ URL tra cứu ban đầu nếu có

        if (bookingCode == null || bookingCode.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/booking-detail?status=invalid_code");
            return;
        }

        Booking booking = requestDAO.getBookingBasicInfoByCode(bookingCode);

        // BẢO MẬT: Chặn đứng ngay từ luồng GET nếu đơn thật đang có request "Chờ xử lý"
        if (booking != null && requestDAO.hasPendingRequest(booking.getBookingId())) {

            // 1. TẠI ĐÂY: Phải load lại TẤT CẢ dữ liệu mà trang booking-detail.jsp yêu cầu
            // Bạn cần khởi tạo các DAO để lấy dữ liệu giống như BookingDetailController
            dao.BookingDAO bookingDAO = new dao.BookingDAO();
            dao.RoomTypeDAO roomTypeDAO = new dao.RoomTypeDAO();

            model.Guest guest = bookingDAO.getGuestByBookingId(booking.getBookingId());
            model.RoomType roomType = roomTypeDAO.getRoomDetailById(booking.getRoomTypeId());
            String status = bookingDAO.getDepositVerificationStatus(booking.getBookingId());

            // 2. Set đầy đủ các attribute mà trang booking-detail.jsp đang sử dụng
            request.setAttribute("booking", booking);
            request.setAttribute("guest", guest);
            request.setAttribute("roomType", roomType);
            request.setAttribute("verificationStatus", (status == null) ? "Chưa gửi minh chứng" : status);
            request.setAttribute("searched", true); // Bắt buộc để jsp hiển thị nội dung

            // 3. Đẩy flag lỗi để JSP hiện SweetAlert
            request.setAttribute("status", "duplicate_pending_error");

            // 4. Forward sang booking-detail.jsp
            request.getRequestDispatcher("/view/user/booking-detail.jsp").forward(request, response);
            return;
        }

        if (booking == null) {
            booking = new Booking();
            booking.setBookingCode(bookingCode);
            booking.setBookingId(1);
            booking.setStatus("Đã xác nhận");
            booking.setRoomTypeName("Hạng phòng tiêu chuẩn");
            booking.setCheckinDate(java.time.LocalDate.now());
            booking.setCheckoutDate(java.time.LocalDate.now().plusDays(1));
            booking.setBookedPricePerNight(java.math.BigDecimal.valueOf(1000000));
            booking.setNumRooms(1);
            booking.setGuestId(1);
        }

        List<RoomType> roomTypesList = roomTypeDAO.getAllRoomTypes();

        request.setAttribute("booking", booking);
        request.setAttribute("roomTypesList", roomTypesList);

        request.getRequestDispatcher("/view/user/request-submission.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int bookingId = Integer.parseInt(request.getParameter("bookingId"));
            int guestId = Integer.parseInt(request.getParameter("guestId"));
            int roomTypeId = Integer.parseInt(request.getParameter("roomTypeId"));
            int numRooms = Integer.parseInt(request.getParameter("numRooms"));

            String requestType = request.getParameter("requestType");
            String bookingCode = request.getParameter("bookingCode");
            String email = request.getParameter("email"); // Hứng thẻ ẩn email từ requestForm bên JSP đẩy lên

            String reasonDetails = "";
            if ("Đổi hạng phòng".equals(requestType)) {
                reasonDetails = request.getParameter("reason_details");
            } else if ("Gia hạn phòng".equals(requestType)) {
                reasonDetails = request.getParameter("reason_details_extend");
            } else if ("Hủy đặt phòng".equals(requestType)) {
                reasonDetails = request.getParameter("reason_details_cancel");
            }

            // Xây dựng chuỗi tham số an toàn bảo lưu thông tin tra cứu
            String emailParam = (email != null && !email.trim().isEmpty()) ? "&email=" + email : "";

            // CHẶN HOÀN TOÀN TẦNG POST: Tránh spam liên tiếp
            if (requestDAO.hasPendingRequest(bookingId)) {
                response.sendRedirect(request.getContextPath() + "/booking-detail?bookingCode=" + bookingCode + emailParam + "&status=duplicate_pending_error");
                return;
            }

            boolean isSuccess = false;

            switch (requestType) {
                case "Đổi hạng phòng":
                    int targetRoomTypeId = Integer.parseInt(request.getParameter("targetRoomTypeId"));
                    LocalDate checkIn = LocalDate.parse(request.getParameter("checkInDate"));
                    LocalDate checkOut = LocalDate.parse(request.getParameter("oldCheckoutDate"));

                    if (requestDAO.checkRoomAvailabilityForChange(targetRoomTypeId, checkIn, checkOut, numRooms)) {
                        isSuccess = requestDAO.insertGuestRequest(bookingId, guestId, requestType, reasonDetails, null, null, targetRoomTypeId);
                    }
                    break;

                case "Gia hạn phòng":
                    LocalDate oldCheckoutDate = LocalDate.parse(request.getParameter("oldCheckoutDate"));
                    LocalDate newCheckout = LocalDate.parse(request.getParameter("checkOutDate"));

                    if (newCheckout.isAfter(oldCheckoutDate)) {
                        if (requestDAO.checkRoomAvailabilityForExtension(roomTypeId, oldCheckoutDate, newCheckout, numRooms)) {
                            isSuccess = requestDAO.insertGuestRequest(bookingId, guestId, requestType, reasonDetails, null, newCheckout, null);
                        }
                    } else {
                        isSuccess = requestDAO.insertGuestRequest(bookingId, guestId, requestType, reasonDetails, null, newCheckout, null);
                    }
                    break;

                case "Hủy đặt phòng":
                    isSuccess = requestDAO.insertGuestRequest(bookingId, guestId, requestType, reasonDetails, null, null, null);
                    break;
            }

            if (isSuccess) {
                response.sendRedirect(request.getContextPath() + "/booking-detail?bookingCode=" + bookingCode + emailParam + "&status=request_success");
            } else {
                response.sendRedirect(request.getContextPath() + "/booking-detail?bookingCode=" + bookingCode + emailParam + "&status=request_failed");
            }

        } catch (Exception e) {
            System.out.println("Loi he thong tai GuestRequestController: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/booking-detail?status=system_error");
        }
    }
}
