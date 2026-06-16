package controller;

import dao.BookingDAO;
import dao.RoomTypeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import model.Booking;
import model.RoomType;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 6 * 1024 * 1024
)
public class BookingPaymentController extends HttpServlet {

    private static final int HOLD_MINUTES = 15;

    // Hiển thị trang thanh toán
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String bookingCode = request.getParameter("bookingCode");

        if (bookingCode == null || bookingCode.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/search");
            return;
        }

        showPaymentPage(request, response, bookingCode.trim(), null);
    }

    // Nhận minh chứng thanh toán
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String bookingCode = request.getParameter("bookingCode");

        if (bookingCode == null || bookingCode.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/search");
            return;
        }

        bookingCode = bookingCode.trim();

        BookingDAO bookingDAO = new BookingDAO();
        bookingDAO.cancelExpiredBookings();

        Booking booking = bookingDAO.getBookingByCode(bookingCode);

        if (booking == null) {
            showPaymentPage(request, response, bookingCode, "Không tìm thấy đơn đặt phòng.");
            return;
        }

        if ("Đã hủy".equals(booking.getStatus())) {
            showPaymentPage(request, response, bookingCode,
                    "Đơn đặt phòng đã bị hủy do hết thời gian giữ phòng.");
            return;
        }

        // Đã gửi minh chứng thì chuyển sang trang hoàn tất
        if (bookingDAO.hasDepositPayment(booking.getBookingId())) {
            response.sendRedirect(request.getContextPath()
                    + "/booking-success?bookingCode=" + bookingCode);
            return;
        }

        if (booking.getCreateAt() == null) {
            showPaymentPage(request, response, bookingCode,
                    "Không thể xác định thời gian giữ phòng.");
            return;
        }

        LocalDateTime expiresAt = booking.getCreateAt().plusMinutes(HOLD_MINUTES);
        long remainingSeconds = Duration.between(LocalDateTime.now(), expiresAt).getSeconds();

        // Kiểm tra lại thời gian ở server
        if (remainingSeconds <= 0) {
            bookingDAO.cancelExpiredBookings();

            showPaymentPage(request, response, bookingCode,
                    "Đơn đặt phòng đã bị hủy do hết thời gian giữ phòng.");
            return;
        }

        Part paymentProof;

        try {
            paymentProof = request.getPart("paymentProof");
        } catch (IllegalStateException e) {
            showPaymentPage(request, response, bookingCode,
                    "File minh chứng không được vượt quá 5MB.");
            return;
        }

        if (paymentProof == null || paymentProof.getSize() == 0) {
            showPaymentPage(request, response, bookingCode,
                    "Vui lòng tải minh chứng thanh toán.");
            return;
        }

        if (paymentProof.getSize() > 5L * 1024 * 1024) {
            showPaymentPage(request, response, bookingCode,
                    "File minh chứng không được vượt quá 5MB.");
            return;
        }

        String originalName = paymentProof.getSubmittedFileName();

        if (originalName == null || originalName.trim().isEmpty()) {
            showPaymentPage(request, response, bookingCode,
                    "File minh chứng không hợp lệ.");
            return;
        }

        originalName = originalName.trim();

        int dotIndex = originalName.lastIndexOf(".");
        String extension = dotIndex >= 0 ? originalName.substring(dotIndex).toLowerCase() : "";

        if (!extension.equals(".jpg") && !extension.equals(".jpeg")
                && !extension.equals(".png")) {

            showPaymentPage(request, response, bookingCode,
                    "Minh chứng chỉ chấp nhận file JPG, JPEG hoặc PNG.");
            return;
        }

        String uploadPath = getServletContext().getRealPath("/uploads/deposits");

        if (uploadPath == null) {
            showPaymentPage(request, response, bookingCode,
                    "Không thể xác định thư mục lưu minh chứng.");
            return;
        }

        File uploadFolder = new File(uploadPath);

        if (!uploadFolder.exists() && !uploadFolder.mkdirs()) {
            showPaymentPage(request, response, bookingCode,
                    "Không thể tạo thư mục lưu minh chứng.");
            return;
        }

        String fileName = bookingCode + "_" + System.currentTimeMillis() + extension;
        File savedFile = new File(uploadFolder, fileName);

        try {
            paymentProof.write(savedFile.getAbsolutePath());
        } catch (Exception e) {
            showPaymentPage(request, response, bookingCode,
                    "Không thể lưu file minh chứng.");
            return;
        }

        String paymentProofUrl = "/uploads/deposits/" + fileName;

        /*
         * Method này phải:
         * 1. INSERT DepositPayments
         * 2. UPDATE Bookings.payment_status = N'Đã đặt cọc'
         * 3. COMMIT cùng transaction
         */
        boolean created = bookingDAO.createDepositPayment(
                booking.getBookingId(),
                booking.getDepositAmount(),
                paymentProofUrl
        );

        if (!created) {
            if (savedFile.exists()) {
                savedFile.delete();
            }

            showPaymentPage(request, response, bookingCode,
                    "Không thể lưu minh chứng thanh toán.");
            return;
        }

        response.sendRedirect(request.getContextPath()
                + "/booking-success?bookingCode=" + bookingCode);
    }

    // Chuẩn bị dữ liệu cho trang thanh toán
    private void showPaymentPage(HttpServletRequest request, HttpServletResponse response,
            String bookingCode, String error) throws ServletException, IOException {

        BookingDAO bookingDAO = new BookingDAO();
        bookingDAO.cancelExpiredBookings();

        Booking booking = bookingDAO.getBookingByCode(bookingCode);

        if (booking == null) {
            request.setAttribute("error", "Không tìm thấy đơn đặt phòng.");
            request.getRequestDispatcher("/view/user/booking-payment.jsp")
                    .forward(request, response);
            return;
        }

        RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
        RoomType roomType = roomTypeDAO.getRoomDetailById(booking.getRoomTypeId());

        boolean hasPayment = bookingDAO.hasDepositPayment(booking.getBookingId());
        long remainingSeconds = 0;

        if (booking.getCreateAt() != null
                && !"Đã hủy".equals(booking.getStatus())
                && !hasPayment) {

            LocalDateTime expiresAt = booking.getCreateAt().plusMinutes(HOLD_MINUTES);
            remainingSeconds = Duration.between(LocalDateTime.now(), expiresAt).getSeconds();

            if (remainingSeconds < 0) {
                remainingSeconds = 0;
            }
        }

        if (!hasPayment && remainingSeconds <= 0
                && !"Đã hủy".equals(booking.getStatus())) {

            bookingDAO.cancelExpiredBookings();
            booking = bookingDAO.getBookingByCode(bookingCode);
        }

        if ("Đã hủy".equals(booking.getStatus()) && error == null) {
            error = "Đơn đặt phòng đã bị hủy do hết thời gian giữ phòng.";
        }

        request.setAttribute("booking", booking);
        request.setAttribute("roomType", roomType);
        request.setAttribute("remainingSeconds", remainingSeconds);
        request.setAttribute("hasPayment", hasPayment);
        request.setAttribute("error", error);

        request.getRequestDispatcher("/view/user/booking-payment.jsp")
                .forward(request, response);
    }
}
