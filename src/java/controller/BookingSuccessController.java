package controller;

import dao.BookingDAO;
import dao.RoomTypeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.Booking;
import model.RoomType;

public class BookingSuccessController extends HttpServlet {

    // Hiển thị kết quả gửi minh chứng
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

        bookingCode = bookingCode.trim();

        BookingDAO bookingDAO = new BookingDAO();
        Booking booking = bookingDAO.getBookingByCode(bookingCode);

        if (booking == null) {
            response.sendRedirect(request.getContextPath() + "/search");
            return;
        }

        // Chưa gửi minh chứng thì không được mở trang thành công
        if (!bookingDAO.hasDepositPayment(booking.getBookingId())) {
            response.sendRedirect(request.getContextPath()
                    + "/booking-payment?bookingCode=" + bookingCode);
            return;
        }

        // Booking đã bị hủy thì quay lại trang payment để hiển thị thông báo
        if ("Đã hủy".equals(booking.getStatus())) {
            response.sendRedirect(request.getContextPath()
                    + "/booking-payment?bookingCode=" + bookingCode);
            return;
        }

        RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
        RoomType roomType = roomTypeDAO.getRoomDetailById(booking.getRoomTypeId());

        if (roomType == null) {
            response.sendRedirect(request.getContextPath() + "/search");
            return;
        }

        request.setAttribute("booking", booking);
        request.setAttribute("roomType", roomType);

        request.getRequestDispatcher("/view/user/booking-success.jsp")
                .forward(request, response);
    }
}
