package controller;

import dal.EmailUtil;
import dao.BookingDAO;
import dao.RoomTypeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import model.Booking;
import model.RoomType;

public class BookingPaymentController extends HttpServlet {

    private static final int HOLD_MINUTES = 15;

    private static final DateTimeFormatter DATE_FORMATTER
            = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String bookingCode = getValue(request, "bookingCode");

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

        String bookingCode = getValue(request, "bookingCode");

        if (bookingCode.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/search");
            return;
        }

        String paymentProofUrl = getValue(request, "paymentProofUrl");
        String transactionReference = getValue(request, "transactionReference");

        BookingDAO bookingDAO = new BookingDAO();

        try {
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

            if (bookingDAO.hasDepositPayment(booking.getBookingId())) {
                response.sendRedirect(
                        request.getContextPath()
                        + "/booking-success?bookingCode="
                        + booking.getBookingCode()
                );
                return;
            }

            if ("Đã hủy".equals(booking.getStatus())
                    || isHoldExpired(booking)) {

                bookingDAO.cancelExpiredBookings();

                showPaymentPage(
                        request,
                        response,
                        bookingCode,
                        "Đơn đặt phòng đã hết thời gian giữ phòng."
                );
                return;
            }

            String error = validatePaymentInformation(
                    paymentProofUrl,
                    transactionReference
            );

            if (error != null) {
                showPaymentPage(request, response, bookingCode, error);
                return;
            }

            boolean created = bookingDAO.createDepositPayment(
                    booking.getBookingId(),
                    booking.getDepositAmount(),
                    paymentProofUrl,
                    transactionReference
            );

            if (!created) {
                showPaymentPage(
                        request,
                        response,
                        bookingCode,
                        "Không thể gửi thông tin giao dịch. "
                        + "Vui lòng thử lại."
                );
                return;
            }

            jakarta.servlet.http.HttpSession session = request.getSession(false);

            if (session != null) {
                Object emailObject = session.getAttribute("bookingEmail_" + booking.getBookingCode());
                Object guestNameObject = session.getAttribute("bookingGuestName_" + booking.getBookingCode());

                if (emailObject != null && guestNameObject != null) {
                    try {
                        EmailUtil.sendPaymentSubmitted(emailObject.toString(), guestNameObject.toString(),
                                booking.getBookingCode(), transactionReference);

                        session.removeAttribute("bookingEmail_" + booking.getBookingCode());
                        session.removeAttribute("bookingGuestName_" + booking.getBookingCode());
                    } catch (Exception e) {
                        getServletContext().log("Không thể gửi email thanh toán " + booking.getBookingCode(), e);
                    }
                }
            }

            response.sendRedirect(
                    request.getContextPath()
                    + "/booking-success?bookingCode="
                    + booking.getBookingCode()
            );

        } catch (Exception e) {
            showPaymentPage(
                    request,
                    response,
                    bookingCode,
                    "Đã xảy ra lỗi hệ thống. Vui lòng thử lại."
            );
        }
    }

    private void showPaymentPage(HttpServletRequest request,
            HttpServletResponse response,
            String bookingCode,
            String error)
            throws ServletException, IOException {

        BookingDAO bookingDAO = new BookingDAO();
        RoomTypeDAO roomTypeDAO = new RoomTypeDAO();

        try {
            bookingDAO.cancelExpiredBookings();

            Booking booking = bookingDAO.getBookingByCode(bookingCode);

            if (booking == null) {
                request.setAttribute("error", "Không tìm thấy đơn đặt phòng.");

                request.getRequestDispatcher("/view/user/booking-payment.jsp")
                        .forward(request, response);
                return;
            }

            boolean hasPayment
                    = bookingDAO.hasDepositPayment(booking.getBookingId());

            if (!hasPayment && isHoldExpired(booking)) {
                bookingDAO.cancelExpiredBookings();
                booking = bookingDAO.getBookingByCode(bookingCode);
            }

            RoomType roomType
                    = roomTypeDAO.getRoomDetailById(booking.getRoomTypeId());

            setPaymentPageData(request, booking, roomType, hasPayment, error);

            request.getRequestDispatcher("/view/user/booking-payment.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            request.setAttribute(
                    "error",
                    "Đã xảy ra lỗi hệ thống. Vui lòng thử lại."
            );

            request.getRequestDispatcher("/view/user/booking-payment.jsp")
                    .forward(request, response);
        }
    }

    private void setPaymentPageData(HttpServletRequest request,
            Booking booking,
            RoomType roomType,
            boolean hasPayment,
            String error) {

        long numberOfNights = 0;

        LocalDate checkIn = booking.getCheckinDate();
        LocalDate checkOut = booking.getCheckoutDate();

        if (checkIn != null && checkOut != null
                && checkOut.isAfter(checkIn)) {

            numberOfNights
                    = checkOut.toEpochDay() - checkIn.toEpochDay();
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        if (booking.getBookedPricePerNight() != null) {
            totalAmount = booking.getBookedPricePerNight()
                    .multiply(BigDecimal.valueOf(booking.getNumRooms()))
                    .multiply(BigDecimal.valueOf(numberOfNights));
        }

        long remainingSeconds
                = calculateRemainingSeconds(booking, hasPayment);

        long serverNowMillis
                = System.currentTimeMillis();

        long expiresAtMillis
                = serverNowMillis;

        if (booking.getCreateAt() != null) {
            expiresAtMillis = booking.getCreateAt()
                    .plusMinutes(HOLD_MINUTES)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();
        }

        request.setAttribute("booking", booking);
        request.setAttribute("roomType", roomType);
        request.setAttribute("numberOfNights", numberOfNights);
        request.setAttribute("totalAmount", totalAmount);
        request.setAttribute("remainingSeconds", remainingSeconds);
        request.setAttribute("expiresAtMillis", expiresAtMillis);
        request.setAttribute("serverNowMillis", serverNowMillis);
        request.setAttribute("hasPayment", hasPayment);
        request.setAttribute("error", error);

        if (checkIn != null) {
            request.setAttribute("checkInText", checkIn.format(DATE_FORMATTER));
        }

        if (checkOut != null) {
            request.setAttribute(
                    "checkOutText",
                    checkOut.format(DATE_FORMATTER)
            );
        }
    }

    private String validatePaymentInformation(
            String paymentProofUrl,
            String transactionReference) {

        if (paymentProofUrl.isEmpty()) {
            return "Vui lòng tải ảnh minh chứng chuyển khoản.";
        }

        if (paymentProofUrl.length() > 255) {
            return "Link ảnh minh chứng không được vượt quá 255 ký tự.";
        }

        if (!paymentProofUrl.startsWith("http://")
                && !paymentProofUrl.startsWith("https://")) {

            return "Link ảnh minh chứng không hợp lệ.";
        }

        if (transactionReference.isEmpty()) {
            return "Vui lòng nhập mã giao dịch hoặc mã tham chiếu.";
        }

        if (transactionReference.length() < 4) {
            return "Mã giao dịch hoặc mã tham chiếu không hợp lệ.";
        }

        if (transactionReference.length() > 100) {
            return "Mã giao dịch hoặc mã tham chiếu không được vượt quá 100 ký tự.";
        }

        return null;
    }

    private long calculateRemainingSeconds(
            Booking booking,
            boolean hasPayment) {

        if (hasPayment || booking.getCreateAt() == null) {
            return 0;
        }

        long seconds = Duration.between(
                java.time.LocalDateTime.now(),
                booking.getCreateAt().plusMinutes(HOLD_MINUTES)
        ).getSeconds();

        return Math.max(seconds, 0);
    }

    private boolean isHoldExpired(Booking booking) {
        if (booking.getCreateAt() == null) {
            return false;
        }

        return java.time.LocalDateTime.now()
                .isAfter(booking.getCreateAt().plusMinutes(HOLD_MINUTES));
    }

    private String getValue(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null ? "" : value.trim();
    }
}
