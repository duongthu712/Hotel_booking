package controller;

import dao.BookingDAO;
import dao.RoomTypeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Random;
import model.Booking;
import model.RoomType;

public class BookingFormController extends HttpServlet {

    private static final double DEPOSIT_RATE = 0.30;
    private static final int CODE_LENGTH = 8;
    private static final String CODE_CHARACTERS
            = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        if (request.getServletPath().equals("/quick-booking")) {
            showQuickBooking(request, response);
        } else {
            showBookingForm(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        if (request.getServletPath().equals("/quick-booking")) {
            processQuickBooking(request, response);
        } else {
            processBooking(request, response);
        }
    }

    private void showQuickBooking(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
        List<RoomType> roomTypes = roomTypeDAO.getAllRoomTypes();
        LocalDate today = LocalDate.now();

        request.setAttribute("roomTypes", roomTypes);
        request.setAttribute("today", today);
        request.setAttribute("checkIn", today);
        request.setAttribute("checkOut", today.plusDays(1));
        request.setAttribute("selectedRoomTypeId", 0);
        request.setAttribute("numRooms", 1);
        request.setAttribute("numGuests", 1);

        request.getRequestDispatcher("/view/user/quick-booking.jsp")
                .forward(request, response);
    }

    private void processQuickBooking(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
        BookingDAO bookingDAO = new BookingDAO();

        bookingDAO.cancelExpiredBookings();

        String checkInString = getValue(request, "checkIn");
        String checkOutString = getValue(request, "checkOut");

        int roomTypeId = parseInt(request.getParameter("roomTypeId"));
        int numRooms = parseInt(request.getParameter("numRooms"));
        int numGuests = parseInt(request.getParameter("numGuests"));

        LocalDate checkIn = parseDate(checkInString);
        LocalDate checkOut = parseDate(checkOutString);

        RoomType roomType = null;
        int availableRooms = 0;

        if (roomTypeId > 0) {
            roomType = roomTypeDAO.getRoomDetailById(roomTypeId);
        }

        if (roomType != null && checkIn != null
                && checkOut != null && checkOut.isAfter(checkIn)) {

            availableRooms = roomTypeDAO.getAvailableRoomCount(
                    roomTypeId, checkInString, checkOutString);
        }

        String error = validateRoomSelection(
                roomType, checkIn, checkOut,
                numRooms, numGuests, availableRooms);

        if (error != null) {
            request.setAttribute("error", error);
            request.setAttribute("roomTypes", roomTypeDAO.getAllRoomTypes());
            request.setAttribute("today", LocalDate.now());
            request.setAttribute("checkIn", checkInString);
            request.setAttribute("checkOut", checkOutString);
            request.setAttribute("selectedRoomTypeId", roomTypeId);
            request.setAttribute("numRooms", numRooms > 0 ? numRooms : 1);
            request.setAttribute("numGuests", numGuests > 0 ? numGuests : 1);

            request.getRequestDispatcher("/view/user/quick-booking.jsp")
                    .forward(request, response);
            return;
        }

        String url = request.getContextPath()
                + "/booking-form?roomTypeId=" + roomTypeId
                + "&checkIn=" + checkIn
                + "&checkOut=" + checkOut
                + "&numRooms=" + numRooms
                + "&numGuests=" + numGuests;

        response.sendRedirect(url);
    }

    private void showBookingForm(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
        BookingDAO bookingDAO = new BookingDAO();

        bookingDAO.cancelExpiredBookings();

        int roomTypeId = parseInt(request.getParameter("roomTypeId"));

        String checkInString = getValue(request, "checkIn");
        String checkOutString = getValue(request, "checkOut");
        String numRoomsString = getValue(request, "numRooms");

        if (numRoomsString.isEmpty()) {
            numRoomsString = getValue(request, "roomQuantity");
        }

        int numRooms = parseInt(numRoomsString);
        int numGuests = parseInt(request.getParameter("numGuests"));

        if (numGuests <= 0) {
            numGuests = 1;
        }

        LocalDate checkIn = parseDate(checkInString);
        LocalDate checkOut = parseDate(checkOutString);
        RoomType roomType = roomTypeDAO.getRoomDetailById(roomTypeId);

        if (roomType == null || checkIn == null || checkOut == null) {
            response.sendRedirect(request.getContextPath() + "/search");
            return;
        }

        int availableRooms = 0;

        if (checkOut.isAfter(checkIn)) {
            availableRooms = roomTypeDAO.getAvailableRoomCount(
                    roomTypeId, checkInString, checkOutString);
        }

        String error = validateRoomSelection(
                roomType, checkIn, checkOut,
                numRooms, numGuests, availableRooms);

        setBookingFormData(request, roomType, checkIn, checkOut,
                numRooms, numGuests, availableRooms);

        request.setAttribute("error", error);

        request.getRequestDispatcher("/view/user/booking-form.jsp")
                .forward(request, response);
    }

    private void processBooking(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String fullName = getValue(request, "fullName");
        String email = getValue(request, "email");
        String phone = getValue(request, "phone");
        String idNumber = getValue(request, "idNumber");
        String dateOfBirthString = getValue(request, "dateOfBirth");
        
        int roomTypeId = parseInt(request.getParameter("roomTypeId"));
        int numRooms = parseInt(request.getParameter("numRooms"));
        int numGuests = parseInt(request.getParameter("numGuests"));

        LocalDate checkIn = parseDate(request.getParameter("checkIn"));
        LocalDate checkOut = parseDate(request.getParameter("checkOut"));
        LocalDate dateOfBirth = parseDate(dateOfBirthString);

        RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
        BookingDAO bookingDAO = new BookingDAO();

        bookingDAO.cancelExpiredBookings();

        RoomType roomType = roomTypeDAO.getRoomDetailById(roomTypeId);

        if (roomType == null || checkIn == null || checkOut == null) {
            response.sendRedirect(request.getContextPath() + "/search");
            return;
        }

        int availableRooms = 0;

        if (checkOut.isAfter(checkIn)) {
            availableRooms = roomTypeDAO.getAvailableRoomCount(
                    roomTypeId, checkIn.toString(), checkOut.toString());
        }

        String error = validateCustomerInformation(fullName, email, phone, idNumber,dateOfBirth);

        if (error == null) {
            error = validateRoomSelection(
                    roomType, checkIn, checkOut,
                    numRooms, numGuests, availableRooms);
        }

        setBookingFormData(request, roomType, checkIn, checkOut,
                numRooms, numGuests, availableRooms);

        request.setAttribute("fullName", fullName);
        request.setAttribute("email", email);
        request.setAttribute("phone", phone);
        request.setAttribute("idNumber", idNumber);
        request.setAttribute("dateOfBirth", dateOfBirthString);

        if (error != null) {
            request.setAttribute("error", error);

            request.getRequestDispatcher("/view/user/booking-form.jsp")
                    .forward(request, response);
            return;
        }

        int guestId = bookingDAO.findGuestId(email, phone);

        if (guestId == 0) {
            guestId = bookingDAO.createGuest(
                    fullName, email, phone, idNumber, dateOfBirth);
        }

        if (guestId == 0) {
            request.setAttribute(
                    "error", "Không thể lưu thông tin khách hàng.");

            request.getRequestDispatcher("/view/user/booking-form.jsp")
                    .forward(request, response);
            return;
        }

        long numberOfNights
                = checkOut.toEpochDay() - checkIn.toEpochDay();

        long pricePerNight = roomType.getBasePrice().longValue();
        long totalMoney = pricePerNight * numRooms * numberOfNights;
        long depositMoney = Math.round(totalMoney * DEPOSIT_RATE);

        Booking booking = new Booking();

        booking.setBookingCode(generateBookingCode(bookingDAO));
        booking.setGuestId(guestId);
        booking.setStaffId(null);
        booking.setRoomTypeId(roomTypeId);
        booking.setBookedPricePerNight(
                BigDecimal.valueOf(pricePerNight));
        booking.setNumRooms(numRooms);
        booking.setNumGuests(numGuests);
        booking.setCheckinDate(checkIn);
        booking.setCheckoutDate(checkOut);
        booking.setSource("Đặt phòng trực tuyến");
        booking.setStatus("Chờ xử lý");
        booking.setDepositAmount(
                BigDecimal.valueOf(depositMoney));
        booking.setPaymentStatus("Chưa thanh toán");

        int bookingId = bookingDAO.createBooking(booking);

        if (bookingId == 0) {
            request.setAttribute(
                    "error", "Không thể tạo đơn đặt phòng.");

            request.getRequestDispatcher("/view/user/booking-form.jsp")
                    .forward(request, response);
            return;
        }

        booking.setBookingId(bookingId);

        response.sendRedirect(request.getContextPath()
                + "/booking-payment?bookingCode="
                + booking.getBookingCode());
    }

    private void setBookingFormData(HttpServletRequest request,
            RoomType roomType, LocalDate checkIn, LocalDate checkOut,
            int numRooms, int numGuests, int availableRooms) {

        long numberOfNights
                = checkOut.toEpochDay() - checkIn.toEpochDay();

        if (numberOfNights < 0) {
            numberOfNights = 0;
        }

        long pricePerNight = roomType.getBasePrice().longValue();
        long totalMoney = pricePerNight * numRooms * numberOfNights;
        long depositMoney = Math.round(totalMoney * DEPOSIT_RATE);

        request.setAttribute("roomType", roomType);
        request.setAttribute("checkIn", checkIn);
        request.setAttribute("checkOut", checkOut);
        request.setAttribute("numRooms", numRooms);
        request.setAttribute("numGuests", numGuests);
        request.setAttribute("numberOfNights", numberOfNights);
        request.setAttribute("availableRooms", availableRooms);
        request.setAttribute(
                "totalAmount", BigDecimal.valueOf(totalMoney));
        request.setAttribute(
                "depositAmount", BigDecimal.valueOf(depositMoney));
    }

    private String validateCustomerInformation(
            String fullName, String email, String phone,
            String idNumber, LocalDate dateOfBirth) {

        if (fullName.isEmpty()) {
            return "Vui lòng nhập họ và tên.";
        } else if (fullName.length() < 2 || fullName.length() > 100) {
            return "Họ và tên phải có từ 2 đến 100 ký tự.";
        } else if (!fullName.matches(
                "^[\\p{L}]+(?: [\\p{L}]+)*$")) {

            return "Họ và tên chỉ được chứa chữ cái và một khoảng trắng giữa các từ.";
        }

        if (email.isEmpty()) {
            return "Vui lòng nhập email.";
        } else if (email.length() > 50) {
            return "Email không được vượt quá 50 ký tự.";
        } else if (email.contains(" ")
                || email.startsWith(".")
                || email.contains("..")
                || email.contains(".@")
                || !email.matches(
                        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {

            return "Email không đúng định dạng.";
        }

        if (phone.isEmpty()) {
            return "Vui lòng nhập số điện thoại.";
        } else if (!phone.matches("\\d+")) {
            return "Số điện thoại chỉ được chứa chữ số.";
        } else if (phone.length() != 10) {
            return "Số điện thoại phải gồm đúng 10 chữ số.";
        } else if (!phone.matches("^(03|05|07|08|09)\\d{8}$")) {
            return "Số điện thoại không đúng đầu số Việt Nam.";
        } else if (phone.matches("^0(\\d)\\1{8}$")) {
            return "Số điện thoại không hợp lệ.";
        }

        if (!idNumber.isEmpty() && idNumber.length() > 50) {
            return "Số CMND/CCCD/Hộ chiếu không được vượt quá 50 ký tự.";
        } else if (!idNumber.isEmpty()
                && !idNumber.matches("^[A-Za-z0-9]+$")) {

            return "Số CMND/CCCD/Hộ chiếu chỉ được chứa chữ cái và chữ số.";
        }

        // Ngày sinh không bắt buộc, chỉ kiểm tra khi khách có nhập
        if (dateOfBirth != null) {
            if (dateOfBirth.isAfter(LocalDate.now())) {
                return "Ngày sinh không được lớn hơn ngày hiện tại.";
            }

            int age = Period.between(
                    dateOfBirth, LocalDate.now()).getYears();

            if (age < 18) {
                return "Khách đặt phòng phải đủ 18 tuổi.";
            }
        }

        return null;
    }

    private String validateRoomSelection(
            RoomType roomType, LocalDate checkIn,
            LocalDate checkOut, int numRooms,
            int numGuests, int availableRooms) {

        if (roomType == null) {
            return "Vui lòng chọn hạng phòng.";
        } else if (checkIn == null || checkOut == null) {
            return "Vui lòng chọn ngày nhận phòng và ngày trả phòng.";
        } else if (checkIn.isBefore(LocalDate.now())) {
            return "Ngày nhận phòng không được nhỏ hơn ngày hiện tại.";
        } else if (!checkOut.isAfter(checkIn)) {
            return "Ngày trả phòng phải sau ngày nhận phòng.";
        } else if (numRooms <= 0) {
            return "Số lượng phòng phải lớn hơn 0.";
        } else if (availableRooms <= 0) {
            return "Hạng phòng này hiện đã hết phòng.";
        } else if (numRooms > availableRooms) {
            return "Hiện chỉ còn "
                    + availableRooms + " phòng khả dụng.";
        } else if (numGuests <= 0) {
            return "Số lượng khách phải lớn hơn 0.";
        }

        int maximumGuests = roomType.getCapacity() * numRooms;

        if (numGuests > maximumGuests) {
            return "Số khách vượt quá sức chứa tối đa. "
                    + numRooms + " phòng chỉ được tối đa "
                    + maximumGuests + " khách.";
        }

        return null;
    }

    private String generateBookingCode(BookingDAO bookingDAO) {
        Random random = new Random();
        String bookingCode;

        do {
            bookingCode = "LMHB";

            for (int i = 0; i < CODE_LENGTH; i++) {
                int index
                        = random.nextInt(CODE_CHARACTERS.length());

                bookingCode += CODE_CHARACTERS.charAt(index);
            }

        } while (bookingDAO.isBookingCodeExist(bookingCode));

        return bookingCode;
    }

    private String getValue(
            HttpServletRequest request, String name) {

        String value = request.getParameter(name);
        return value == null ? "" : value.trim();
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(
                    value == null ? "" : value.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private LocalDate parseDate(String value) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return null;
            }

            return LocalDate.parse(value.trim());

        } catch (Exception e) {
            return null;
        }
    }
}
