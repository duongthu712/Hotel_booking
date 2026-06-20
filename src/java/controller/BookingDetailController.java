package controller;

import dao.BookingDAO;
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

    // Hiển thị trang tra cứu
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        request.getRequestDispatcher("/view/user/booking-detail.jsp")
                .forward(request, response);
    }

    // Tra cứu booking bằng mã booking và email
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String bookingCode = getParameter(request, "bookingCode");
        String email = getParameter(request, "email");

        request.setAttribute("bookingCode", bookingCode);
        request.setAttribute("email", email);

        String error = validateInput(bookingCode, email);

        if (error != null) {
            request.setAttribute("error", error);
            request.getRequestDispatcher("/view/user/booking-detail.jsp")
                    .forward(request, response);
            return;
        }

        try {
            BookingDAO bookingDAO = new BookingDAO();

            // Hủy các booking online đã hết 15 phút nhưng chưa gửi minh chứng
            bookingDAO.cancelExpiredBookings();

            Booking booking = bookingDAO.getBookingByCodeAndEmail(
                    bookingCode,
                    email
            );

            if (booking == null) {
                request.setAttribute(
                        "error",
                        "Email hoặc mã đặt phòng không chính xác."
                );

                request.getRequestDispatcher("/view/user/booking-detail.jsp")
                        .forward(request, response);
                return;
            }

            Guest guest = bookingDAO.getGuestByBookingId(
                    booking.getBookingId()
            );

            if (guest == null) {
                request.setAttribute(
                        "error",
                        "Không thể tải thông tin khách hàng."
                );

                request.getRequestDispatcher("/view/user/booking-detail.jsp")
                        .forward(request, response);
                return;
            }

            RoomTypeDAO roomTypeDAO = new RoomTypeDAO();

            RoomType roomType = roomTypeDAO.getRoomDetailById(
                    booking.getRoomTypeId()
            );

            if (roomType == null) {
                request.setAttribute(
                        "error",
                        "Không thể tải thông tin loại phòng."
                );

                request.getRequestDispatcher("/view/user/booking-detail.jsp")
                        .forward(request, response);
                return;
            }

            String verificationStatus
                    = bookingDAO.getDepositVerificationStatus(
                            booking.getBookingId()
                    );

            if (verificationStatus == null
                    || verificationStatus.trim().isEmpty()) {

                verificationStatus = "Chưa gửi minh chứng";
            }

            long numberOfNights = ChronoUnit.DAYS.between(
                    booking.getCheckinDate(),
                    booking.getCheckoutDate()
            );

            BigDecimal totalAmount = booking.getBookedPricePerNight()
                    .multiply(BigDecimal.valueOf(booking.getNumRooms()))
                    .multiply(BigDecimal.valueOf(numberOfNights));

            DateTimeFormatter dateFormatter
                    = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            DateTimeFormatter dateTimeFormatter
                    = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            String checkInText = booking.getCheckinDate()
                    .format(dateFormatter);

            String checkOutText = booking.getCheckoutDate()
                    .format(dateFormatter);

            String createdAtText = "Chưa có thông tin";

            if (booking.getCreateAt() != null) {
                createdAtText = booking.getCreateAt()
                        .format(dateTimeFormatter);
            }

            String dateOfBirthText = "Chưa cung cấp";

            if (guest.getDateOfBirth() != null) {
                dateOfBirthText = guest.getDateOfBirth()
                        .format(dateFormatter);
            }

            request.setAttribute("searched", true);
            request.setAttribute("booking", booking);
            request.setAttribute("guest", guest);
            request.setAttribute("roomType", roomType);
            request.setAttribute("numberOfNights", numberOfNights);
            request.setAttribute("totalAmount", totalAmount);
            request.setAttribute("checkInText", checkInText);
            request.setAttribute("checkOutText", checkOutText);
            request.setAttribute("createdAtText", createdAtText);
            request.setAttribute("dateOfBirthText", dateOfBirthText);
            request.setAttribute(
                    "verificationStatus",
                    verificationStatus
            );

            request.getRequestDispatcher("/view/user/booking-detail.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            System.out.println(
                    "BookingDetailController: " + e.getMessage()
            );

            e.printStackTrace();

            request.setAttribute(
                    "error",
                    "Không thể tra cứu đơn đặt phòng lúc này."
            );

            request.getRequestDispatcher("/view/user/booking-detail.jsp")
                    .forward(request, response);
        }
    }

    // Kiểm tra dữ liệu tra cứu
    private String validateInput(String bookingCode, String email) {
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
                || !email.matches(
                        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
                )) {

            return "Email không đúng định dạng.";
        }

        return null;
    }

    // Lấy parameter và loại bỏ khoảng trắng đầu cuối
    private String getParameter(
            HttpServletRequest request,
            String parameterName) {

        String value = request.getParameter(parameterName);

        if (value == null) {
            return "";
        }

        return value.trim();
    }
}
