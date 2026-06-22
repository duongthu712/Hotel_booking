package controller;

import dao.BookingDAO;
import dao.RoomTypeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import model.Booking;
import model.RoomType;

public class BookingPaymentController extends HttpServlet {

    private static final int HOLD_MINUTES = 15;

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String bookingCode = getParameter(request, "bookingCode");

        if (bookingCode.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/search");
            return;
        }

        showPaymentPage(request, response, bookingCode, null);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String bookingCode = getParameter(request, "bookingCode");

        if (bookingCode.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/search");
            return;
        }

        BookingDAO bookingDAO = new BookingDAO();

        bookingDAO.cancelExpiredBookings();

        Booking booking = bookingDAO.getBookingByCode(bookingCode);

        if (booking == null) {
            showPaymentPage(
                    request,
                    response,
                    bookingCode,
                    "Không tìm thấy đơn đặt phòng."
            );
            return;
        }

        boolean hasPayment = bookingDAO.hasDepositPayment(
                booking.getBookingId());

        if (hasPayment) {
            response.sendRedirect(request.getContextPath()
                    + "/booking-success?bookingCode="
                    + bookingCode);
            return;
        }

        if ("Đã hủy".equals(booking.getStatus())
                || isExpired(booking)) {

            bookingDAO.cancelExpiredBookings();

            showPaymentPage(
                    request,
                    response,
                    bookingCode,
                    "Đơn đặt phòng đã bị hủy do hết thời gian giữ phòng."
            );
            return;
        }

        String paymentProof = getParameter(request, "paymentProof");

        String error = validatePaymentProof(paymentProof);

        if (error != null) {
            showPaymentPage(request, response, bookingCode, error);
            return;
        }

        boolean created = bookingDAO.createDepositPayment(
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

        response.sendRedirect(request.getContextPath()
                + "/booking-success?bookingCode="
                + bookingCode);
    }

    private void showPaymentPage(HttpServletRequest request,
            HttpServletResponse response,
            String bookingCode,
            String error)
            throws ServletException, IOException {

        BookingDAO bookingDAO = new BookingDAO();

        bookingDAO.cancelExpiredBookings();

        Booking booking = bookingDAO.getBookingByCode(bookingCode);

        if (booking == null) {
            request.setAttribute(
                    "error",
                    "Không tìm thấy đơn đặt phòng."
            );

            request.getRequestDispatcher("/view/user/booking-payment.jsp")
                    .forward(request, response);
            return;
        }

        boolean hasPayment = bookingDAO.hasDepositPayment(
                booking.getBookingId());

        if (!hasPayment && isExpired(booking)) {
            bookingDAO.cancelExpiredBookings();

            booking = bookingDAO.getBookingByCode(bookingCode);

            if (booking != null) {
                hasPayment = bookingDAO.hasDepositPayment(
                        booking.getBookingId());
            }
        }

        RoomType roomType = null;

        if (booking != null) {
            RoomTypeDAO roomTypeDAO = new RoomTypeDAO();

            roomType = roomTypeDAO.getRoomDetailById(
                    booking.getRoomTypeId());
        }

        if (booking != null
                && "Đã hủy".equals(booking.getStatus())
                && error == null) {

            error = "Đơn đặt phòng đã bị hủy do hết thời gian giữ phòng.";
        }

        setPaymentPageData(
                request,
                booking,
                roomType,
                hasPayment,
                error
        );

        request.getRequestDispatcher("/view/user/booking-payment.jsp")
                .forward(request, response);
    }

    private void setPaymentPageData(HttpServletRequest request,
            Booking booking,
            RoomType roomType,
            boolean hasPayment,
            String error) {

        long numberOfNights = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
        String checkInText = "";
        String checkOutText = "";

        if (booking != null
                && booking.getCheckinDate() != null
                && booking.getCheckoutDate() != null) {

            numberOfNights = ChronoUnit.DAYS.between(
                    booking.getCheckinDate(),
                    booking.getCheckoutDate()
            );

            if (numberOfNights < 0) {
                numberOfNights = 0;
            }

            totalAmount = booking.getBookedPricePerNight()
                    .multiply(BigDecimal.valueOf(booking.getNumRooms()))
                    .multiply(BigDecimal.valueOf(numberOfNights));

            DateTimeFormatter formatter
                    = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            checkInText = booking.getCheckinDate().format(formatter);
            checkOutText = booking.getCheckoutDate().format(formatter);
        }

        long remainingSeconds = getRemainingSeconds(booking, hasPayment);
        long expiresAtMillis = getExpiresAtMillis(booking);
        long serverNowMillis = System.currentTimeMillis();

        request.setAttribute("booking", booking);
        request.setAttribute("roomType", roomType);
        request.setAttribute("numberOfNights", numberOfNights);
        request.setAttribute("totalAmount", totalAmount);
        request.setAttribute("checkInText", checkInText);
        request.setAttribute("checkOutText", checkOutText);
        request.setAttribute("remainingSeconds", remainingSeconds);
        request.setAttribute("expiresAtMillis", expiresAtMillis);
        request.setAttribute("serverNowMillis", serverNowMillis);
        request.setAttribute("hasPayment", hasPayment);
        request.setAttribute("error", error);
    }

    private boolean isExpired(Booking booking) {
        if (booking == null || booking.getCreateAt() == null) {
            return false;
        }

        LocalDateTime expiresAt
                = booking.getCreateAt().plusMinutes(HOLD_MINUTES);

        return !LocalDateTime.now().isBefore(expiresAt);
    }

    private long getRemainingSeconds(Booking booking, boolean hasPayment) {
        if (booking == null
                || booking.getCreateAt() == null
                || hasPayment
                || "Đã hủy".equals(booking.getStatus())) {

            return 0;
        }

        LocalDateTime expiresAt
                = booking.getCreateAt().plusMinutes(HOLD_MINUTES);

        long seconds = Duration.between(
                LocalDateTime.now(),
                expiresAt
        ).getSeconds();

        return Math.max(seconds, 0);
    }

    private long getExpiresAtMillis(Booking booking) {
        if (booking == null || booking.getCreateAt() == null) {
            return 0;
        }

        return booking.getCreateAt()
                .plusMinutes(HOLD_MINUTES)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }

    private String validatePaymentProof(String paymentProof) {
        if (paymentProof.isEmpty()) {
            return "Vui lòng nhập tên người chuyển và mã giao dịch.";
        }

        if (paymentProof.length() > 100) {
            return "Thông tin giao dịch không được vượt quá 100 ký tự.";
        }

        int separatorIndex = paymentProof.indexOf("-");

        if (separatorIndex <= 0
                || separatorIndex >= paymentProof.length() - 1) {

            return "Vui lòng nhập đúng dạng: Tên người chuyển - Mã giao dịch.";
        }

        String senderName
                = paymentProof.substring(0, separatorIndex).trim();

        String transactionCode
                = paymentProof.substring(separatorIndex + 1).trim();

        if (senderName.length() < 2) {
            return "Tên người chuyển không hợp lệ.";
        }

        if (transactionCode.length() < 4) {
            return "Mã giao dịch hoặc mã tham chiếu không hợp lệ.";
        }

        return null;
    }

    private String getParameter(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null ? "" : value.trim();
    }
}
