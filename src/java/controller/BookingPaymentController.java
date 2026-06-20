package controller;

import dao.BookingDAO;
import dao.RoomTypeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import model.Booking;
import model.RoomType;

public class BookingPaymentController extends HttpServlet {

    private static final int HOLD_MINUTES = 15;

    // Hiển thị trang thanh toán
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String bookingCode = request.getParameter("bookingCode");

        if (bookingCode == null || bookingCode.trim().isEmpty()) {
            response.sendRedirect(
                    request.getContextPath() + "/search"
            );
            return;
        }

        showPaymentPage(
                request,
                response,
                bookingCode.trim(),
                null
        );
    }

    // Nhận mã giao dịch thanh toán
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String bookingCode
                = request.getParameter("bookingCode");

        if (bookingCode == null
                || bookingCode.trim().isEmpty()) {

            response.sendRedirect(
                    request.getContextPath() + "/search"
            );
            return;
        }

        bookingCode = bookingCode.trim();

        BookingDAO bookingDAO = new BookingDAO();

        // Hủy những booking đã hết 15 phút giữ phòng
        bookingDAO.cancelExpiredBookings();

        Booking booking
                = bookingDAO.getBookingByCode(bookingCode);

        if (booking == null) {
            showPaymentPage(
                    request,
                    response,
                    bookingCode,
                    "Không tìm thấy đơn đặt phòng."
            );
            return;
        }

        if ("Đã hủy".equals(booking.getStatus())) {
            showPaymentPage(
                    request,
                    response,
                    bookingCode,
                    "Đơn đặt phòng đã bị hủy do hết thời gian giữ phòng."
            );
            return;
        }

        // Đã gửi mã giao dịch thì chuyển sang trang hoàn tất
        if (bookingDAO.hasDepositPayment(
                booking.getBookingId())) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/booking-success?bookingCode="
                    + bookingCode
            );
            return;
        }

        if (booking.getCreateAt() == null) {
            showPaymentPage(
                    request,
                    response,
                    bookingCode,
                    "Không thể xác định thời gian giữ phòng."
            );
            return;
        }

        LocalDateTime expiresAt
                = booking.getCreateAt()
                        .plusMinutes(HOLD_MINUTES);

        long remainingSeconds
                = Duration.between(
                        LocalDateTime.now(),
                        expiresAt
                ).getSeconds();

        // Kiểm tra lại thời gian ở server
        if (remainingSeconds <= 0) {
            bookingDAO.cancelExpiredBookings();

            showPaymentPage(
                    request,
                    response,
                    bookingCode,
                    "Đơn đặt phòng đã bị hủy do hết thời gian giữ phòng."
            );
            return;
        }

        /*
         * Trường paymentProof trước đây là file ảnh.
         * Bây giờ trường này dùng để nhập mã giao dịch.
         */
        String paymentProof
                = request.getParameter("paymentProof");

        if (paymentProof == null
                || paymentProof.trim().isEmpty()) {

            showPaymentPage(
                    request,
                    response,
                    bookingCode,
                    "Vui lòng nhập mã giao dịch."
            );
            return;
        }

        paymentProof = paymentProof.trim();

        if (paymentProof.length() < 4) {
            showPaymentPage(
                    request,
                    response,
                    bookingCode,
                    "Mã giao dịch không hợp lệ."
            );
            return;
        }

        if (paymentProof.length() > 100) {
            showPaymentPage(
                    request,
                    response,
                    bookingCode,
                    "Mã giao dịch không được vượt quá 100 ký tự."
            );
            return;
        }

        /*
         * Method này giữ nguyên:
         * 1. INSERT DepositPayments
         * 2. Lưu mã giao dịch vào trường proof
         * 3. UPDATE Bookings.payment_status = N'Đã đặt cọc'
         * 4. COMMIT cùng transaction
         */
        boolean created
                = bookingDAO.createDepositPayment(
                        booking.getBookingId(),
                        booking.getDepositAmount(),
                        paymentProof
                );

        if (!created) {
            showPaymentPage(
                    request,
                    response,
                    bookingCode,
                    "Không thể lưu mã giao dịch thanh toán."
            );
            return;
        }

        response.sendRedirect(
                request.getContextPath()
                + "/booking-success?bookingCode="
                + bookingCode
        );
    }

    // Chuẩn bị dữ liệu cho trang thanh toán
    private void showPaymentPage(
            HttpServletRequest request,
            HttpServletResponse response,
            String bookingCode,
            String error)
            throws ServletException, IOException {

        BookingDAO bookingDAO = new BookingDAO();

        bookingDAO.cancelExpiredBookings();

        Booking booking
                = bookingDAO.getBookingByCode(bookingCode);

        if (booking == null) {
            request.setAttribute(
                    "error",
                    "Không tìm thấy đơn đặt phòng."
            );

            request.getRequestDispatcher(
                    "/view/user/booking-payment.jsp"
            ).forward(request, response);

            return;
        }

        RoomTypeDAO roomTypeDAO = new RoomTypeDAO();

        RoomType roomType
                = roomTypeDAO.getRoomDetailById(
                        booking.getRoomTypeId()
                );

        boolean hasPayment
                = bookingDAO.hasDepositPayment(
                        booking.getBookingId()
                );

        long remainingSeconds = 0;

        if (booking.getCreateAt() != null
                && !"Đã hủy".equals(booking.getStatus())
                && !hasPayment) {

            LocalDateTime expiresAt
                    = booking.getCreateAt()
                            .plusMinutes(HOLD_MINUTES);

            remainingSeconds
                    = Duration.between(
                            LocalDateTime.now(),
                            expiresAt
                    ).getSeconds();

            if (remainingSeconds < 0) {
                remainingSeconds = 0;
            }
        }

        if (!hasPayment
                && remainingSeconds <= 0
                && !"Đã hủy".equals(booking.getStatus())) {

            bookingDAO.cancelExpiredBookings();

            booking
                    = bookingDAO.getBookingByCode(
                            bookingCode
                    );
        }

        if ("Đã hủy".equals(booking.getStatus())
                && error == null) {

            error
                    = "Đơn đặt phòng đã bị hủy do hết thời gian giữ phòng.";
        }

        request.setAttribute("booking", booking);
        request.setAttribute("roomType", roomType);
        request.setAttribute(
                "remainingSeconds",
                remainingSeconds
        );
        request.setAttribute("hasPayment", hasPayment);
        request.setAttribute("error", error);

        request.getRequestDispatcher(
                "/view/user/booking-payment.jsp"
        ).forward(request, response);
    }
}
