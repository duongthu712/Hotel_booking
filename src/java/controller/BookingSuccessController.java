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
import model.RoomType;

public class BookingSuccessController extends HttpServlet {

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

        BookingDAO bookingDAO = new BookingDAO();
        Booking booking = bookingDAO.getBookingByCode(bookingCode);

        if (booking == null) {
            response.sendRedirect(request.getContextPath() + "/search");
            return;
        }

        if (!bookingDAO.hasDepositPayment(booking.getBookingId())) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/booking-payment?bookingCode="
                    + bookingCode
            );
            return;
        }

        if ("Đã hủy".equals(booking.getStatus())) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/booking-payment?bookingCode="
                    + bookingCode
            );
            return;
        }

        RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
        RoomType roomType = roomTypeDAO.getRoomDetailById(
                booking.getRoomTypeId()
        );

        if (roomType == null) {
            response.sendRedirect(request.getContextPath() + "/search");
            return;
        }

        setSuccessPageData(request, booking, roomType);

        request.getRequestDispatcher("/view/user/booking-success.jsp")
                .forward(request, response);
    }

    private void setSuccessPageData(
            HttpServletRequest request,
            Booking booking,
            RoomType roomType) {

        long numberOfNights = ChronoUnit.DAYS.between(
                booking.getCheckinDate(),
                booking.getCheckoutDate()
        );

        BigDecimal totalAmount = booking.getBookedPricePerNight()
                .multiply(BigDecimal.valueOf(booking.getNumRooms()))
                .multiply(BigDecimal.valueOf(numberOfNights));

        DateTimeFormatter dateFormatter
                = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String checkInText = booking.getCheckinDate()
                .format(dateFormatter);

        String checkOutText = booking.getCheckoutDate()
                .format(dateFormatter);

        request.setAttribute("booking", booking);
        request.setAttribute("roomType", roomType);
        request.setAttribute("numberOfNights", numberOfNights);
        request.setAttribute("totalAmount", totalAmount);
        request.setAttribute("checkInText", checkInText);
        request.setAttribute("checkOutText", checkOutText);
    }

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
