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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import model.Booking;
import model.Guest;
import model.RoomType;

public class BookingDetailController extends HttpServlet {

    private static final DateTimeFormatter DATE_FORMATTER
            = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter DATE_TIME_FORMATTER
            = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

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

            verificationStatus = "Chưa gửi minh chứng";
        }

        FeedbackDAO feedbackDAO = new FeedbackDAO();

        boolean hasFeedback = feedbackDAO.hasFeedback(booking.getBookingId());

        boolean canWriteFeedback
                = "Đã trả phòng".equals(booking.getStatus())
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

        long numberOfNights = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;

        String checkInText = "";
        String checkOutText = "";
        String timelineCheckInText = "";
        String timelineCheckOutText = "";
        String createdAtText = "";
        String dateOfBirthText = "";

        if (booking.getCheckinDate() != null) {
            checkInText = booking.getCheckinDate()
                    .format(DATE_FORMATTER);

            timelineCheckInText = checkInText;
        }

        if (booking.getCheckoutDate() != null) {
            checkOutText = booking.getCheckoutDate()
                    .format(DATE_FORMATTER);

            timelineCheckOutText = checkOutText;
        }

        if (booking.getCheckinDate() != null
                && booking.getCheckoutDate() != null) {

            numberOfNights = ChronoUnit.DAYS.between(
                    booking.getCheckinDate(),
                    booking.getCheckoutDate()
            );

            if (numberOfNights < 0) {
                numberOfNights = 0;
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
            timelineCheckInText = booking.getActualCheckinTime()
                    .format(DATE_TIME_FORMATTER);
        }

        if (booking.getActualCheckoutTime() != null) {
            timelineCheckOutText = booking.getActualCheckoutTime()
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

        request.setAttribute("timelineCheckInText", timelineCheckInText);
        request.setAttribute("timelineCheckOutText", timelineCheckOutText);

        request.setAttribute("createdAtText", createdAtText);
        request.setAttribute("dateOfBirthText", dateOfBirthText);
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

        if (email.length() > 100) {
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
