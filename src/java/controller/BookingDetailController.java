package controller;

import dao.BookingDAO;
import dao.FeedbackDAO;
import dao.RoomTypeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import model.Booking;
import model.Guest;
import model.RoomType;

public class BookingDetailController extends HttpServlet {

    private static final DateTimeFormatter DATE_FORMATTER
            = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter DATE_TIME_FORMATTER
            = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final String DEFAULT_VERIFICATION_STATUS = "Chưa gửi minh chứng";
    private static final String WALK_IN_SOURCE = "Đặt phòng tại quầy";

    private static final String STATUS_CHECKED_OUT = "Đã trả phòng";
    private static final String STATUS_CANCELLED = "Đã hủy";
    private static final String STATUS_CHECKED_IN = "Đã nhận phòng";

    private static final int MAX_EMAIL_LENGTH = 100;
    private static final int SAME_DAY_COUNTER_DEPOSIT_LIMIT_HOUR = 14;
    private static final long DEFAULT_NIGHTS = 0;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String bookingCode = request.getParameter("bookingCode");
        String email = request.getParameter("email");
        String status = request.getParameter("status");

        if (bookingCode != null && !bookingCode.trim().isEmpty()) {
            BookingDAO bookingDAO = new BookingDAO();
            Booking booking = bookingDAO.getBookingByCodeAndEmail(bookingCode, email);

            if (booking != null) {
                Guest guest = bookingDAO.getGuestByBookingId(booking.getBookingId());

                RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
                RoomType roomType = roomTypeDAO.getRoomDetailById(booking.getRoomTypeId());

                String verificationStatus = bookingDAO.getDepositVerificationStatus(booking.getBookingId());

                if (verificationStatus == null || verificationStatus.trim().isEmpty()) {
                    verificationStatus = DEFAULT_VERIFICATION_STATUS;
                }

                List<Map<String, Object>> publicRequests
                        = bookingDAO.getPublicBookingRequests(booking.getBookingId());

                List<Map<String, Object>> publicChanges
                        = bookingDAO.getPublicBookingChanges(booking.getBookingId());

                request.setAttribute("publicRequests", publicRequests);
                request.setAttribute("publicChanges", publicChanges);

                FeedbackDAO feedbackDAO = new FeedbackDAO();
                boolean hasFeedback = feedbackDAO.hasFeedback(booking.getBookingId());
                boolean canWriteFeedback = STATUS_CHECKED_OUT.equals(booking.getStatus()) && !hasFeedback;

                request.setAttribute("hasFeedback", hasFeedback);
                request.setAttribute("canWriteFeedback", canWriteFeedback);

                request.setAttribute("booking", booking);
                request.setAttribute("searched", true);
                request.setAttribute("bookingCode", bookingCode);
                request.setAttribute("email", email);
                request.setAttribute("status", status);

                boolean counterSameDayNoDeposit = isCounterSameDayNoDeposit(booking);
                request.setAttribute("counterSameDayNoDeposit", counterSameDayNoDeposit);

                setBookingDetailData(
                        request,
                        booking,
                        guest,
                        roomType,
                        verificationStatus
                );
            }
        }

        request.getRequestDispatcher("/view/user/booking-detail.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String bookingCode = getParameter(request, "bookingCode");
        String email = getParameter(request, "email");

        request.setAttribute("searched", true);
        request.setAttribute("bookingCode", bookingCode);
        request.setAttribute("email", email);

        String error = validateLookupInformation(bookingCode, email);

        if (error != null) {
            request.setAttribute("error", error);

            request.getRequestDispatcher("/view/user/booking-detail.jsp")
                    .forward(request, response);
            return;
        }

        BookingDAO bookingDAO = new BookingDAO();

        bookingDAO.cancelExpiredBookings();

        Booking booking = bookingDAO.getBookingByCodeAndEmail(
                bookingCode, email);

        if (booking == null) {
            request.setAttribute(
                    "error",
                    "Không tìm thấy đơn đặt phòng phù hợp với mã đặt phòng và email.");

            request.getRequestDispatcher("/view/user/booking-detail.jsp")
                    .forward(request, response);
            return;
        }

        Guest guest = bookingDAO.getGuestByBookingId(
                booking.getBookingId());

        RoomTypeDAO roomTypeDAO = new RoomTypeDAO();

        RoomType roomType = roomTypeDAO.getRoomDetailById(
                booking.getRoomTypeId());

        String verificationStatus
                = bookingDAO.getDepositVerificationStatus(
                        booking.getBookingId());

        if (verificationStatus == null
                || verificationStatus.trim().isEmpty()) {

            verificationStatus = DEFAULT_VERIFICATION_STATUS;
        }

        boolean counterSameDayNoDeposit
                = isCounterSameDayNoDeposit(booking);

        request.setAttribute(
                "counterSameDayNoDeposit",
                counterSameDayNoDeposit
        );

        List<Map<String, Object>> publicRequests
                = bookingDAO.getPublicBookingRequests(booking.getBookingId());

        List<Map<String, Object>> publicChanges
                = bookingDAO.getPublicBookingChanges(booking.getBookingId());

        request.setAttribute("publicRequests", publicRequests);
        request.setAttribute("publicChanges", publicChanges);

        FeedbackDAO feedbackDAO = new FeedbackDAO();

        boolean hasFeedback = feedbackDAO.hasFeedback(booking.getBookingId());

        boolean canWriteFeedback
                = STATUS_CHECKED_OUT.equals(booking.getStatus())
                && !hasFeedback;

        request.setAttribute("hasFeedback", hasFeedback);
        request.setAttribute("canWriteFeedback", canWriteFeedback);

        setBookingDetailData(
                request,
                booking,
                guest,
                roomType,
                verificationStatus
        );

        request.getRequestDispatcher("/view/user/booking-detail.jsp")
                .forward(request, response);
    }

    private void setBookingDetailData(HttpServletRequest request,
            Booking booking,
            Guest guest,
            RoomType roomType,
            String verificationStatus) {

        long numberOfNights = DEFAULT_NIGHTS;
        BigDecimal totalAmount = BigDecimal.ZERO;

        String checkInText = "";
        String checkOutText = "";

        String plannedCheckInText = "";
        String plannedCheckOutText = "";
        String actualCheckInText = "";
        String actualCheckOutText = "";

        String createdAtText = "";
        String dateOfBirthText = "";

        if (booking.getCheckinDate() != null) {
            checkInText = booking.getCheckinDate()
                    .format(DATE_FORMATTER);

            plannedCheckInText = checkInText;
        }

        if (booking.getCheckoutDate() != null) {
            checkOutText = booking.getCheckoutDate()
                    .format(DATE_FORMATTER);

            plannedCheckOutText = checkOutText;
        }

        if (booking.getCheckinDate() != null
                && booking.getCheckoutDate() != null) {

            numberOfNights = ChronoUnit.DAYS.between(
                    booking.getCheckinDate(),
                    booking.getCheckoutDate()
            );

            if (numberOfNights < DEFAULT_NIGHTS) {
                numberOfNights = DEFAULT_NIGHTS;
            }
        }

        if (booking.getBookedPricePerNight() != null) {
            totalAmount = booking.getBookedPricePerNight()
                    .multiply(BigDecimal.valueOf(booking.getNumRooms()))
                    .multiply(BigDecimal.valueOf(numberOfNights));
        }

        if (booking.getCreateAt() != null) {
            createdAtText = booking.getCreateAt()
                    .format(DATE_TIME_FORMATTER);
        }

        if (booking.getActualCheckinTime() != null) {
            actualCheckInText = booking.getActualCheckinTime()
                    .format(DATE_TIME_FORMATTER);
        }

        if (booking.getActualCheckoutTime() != null) {
            actualCheckOutText = booking.getActualCheckoutTime()
                    .format(DATE_TIME_FORMATTER);
        }

        if (guest != null && guest.getDateOfBirth() != null) {
            dateOfBirthText = guest.getDateOfBirth()
                    .format(DATE_FORMATTER);
        }

        request.setAttribute("booking", booking);
        request.setAttribute("guest", guest);
        request.setAttribute("roomType", roomType);
        request.setAttribute("verificationStatus", verificationStatus);

        request.setAttribute("numberOfNights", numberOfNights);
        request.setAttribute("totalAmount", totalAmount);

        request.setAttribute("checkInText", checkInText);
        request.setAttribute("checkOutText", checkOutText);

        request.setAttribute("plannedCheckInText", plannedCheckInText);
        request.setAttribute("plannedCheckOutText", plannedCheckOutText);

        request.setAttribute("actualCheckInText", actualCheckInText);
        request.setAttribute("actualCheckOutText", actualCheckOutText);

        request.setAttribute("createdAtText", createdAtText);
        request.setAttribute("dateOfBirthText", dateOfBirthText);
    }

    private boolean isCounterSameDayNoDeposit(Booking booking) {
        if (booking == null) {
            return false;
        }

        if (booking.getSource() == null
                || !WALK_IN_SOURCE.equals(booking.getSource())) {
            return false;
        }

        if (booking.getCheckinDate() == null
                || booking.getCreateAt() == null) {
            return false;
        }

        boolean checkinIsCreateDate = booking.getCheckinDate()
                .equals(booking.getCreateAt().toLocalDate());

        boolean createdBeforeLimitTime = booking.getCreateAt()
                .toLocalTime()
                .isBefore(LocalTime.of(SAME_DAY_COUNTER_DEPOSIT_LIMIT_HOUR, 0));

        return checkinIsCreateDate && createdBeforeLimitTime;
    }

    private String validateLookupInformation(
            String bookingCode, String email) {

        if (bookingCode.isEmpty()) {
            return "Vui lòng nhập mã đặt phòng.";
        }

        if (!bookingCode.matches("^LMHB[A-Za-z0-9]{8}$")) {
            return "Mã đặt phòng không đúng định dạng.";
        }

        if (email.isEmpty()) {
            return "Vui lòng nhập email đặt phòng.";
        }

        if (email.length() > MAX_EMAIL_LENGTH) {
            return "Email không được vượt quá 100 ký tự.";
        }

        if (email.contains(" ")
                || email.startsWith(".")
                || email.contains("..")
                || email.contains(".@")
                || !email.matches(
                        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {

            return "Email không đúng định dạng.";
        }

        return null;
    }

    private String getParameter(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null ? "" : value.trim();
    }
}
