package controller;

import dao.BookingDAO;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class StaffBookingDetailController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        int bookingId = parseInt(request.getParameter("bookingId"));

        if (bookingId <= 0) {
            response.sendRedirect(request.getContextPath() + "/booking-list");
            return;
        }

        BookingDAO bookingDAO = new BookingDAO();

        Map<String, Object> bookingDetail
                = bookingDAO.getStaffBookingDetailForPopup(bookingId);

        if (bookingDetail == null || bookingDetail.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/booking-list");
            return;
        }

        List<Map<String, Object>> recentRequests
                = bookingDAO.getStaffBookingRecentRequests(bookingId);

        request.setAttribute("bookingDetail", bookingDetail);
        request.setAttribute("recentRequests", recentRequests);

        request.getRequestDispatcher("/view/receptionist/staff-booking-detail.jsp")
                .forward(request, response);
    }

    private int parseInt(String value) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return 0;
            }

            return Integer.parseInt(value.trim());

        } catch (Exception e) {
            return 0;
        }
    }
}
