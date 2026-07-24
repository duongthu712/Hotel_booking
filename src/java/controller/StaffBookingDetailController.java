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

    private static final int MIN_VALID_BOOKING_ID = 1;

    private static final String BOOKING_LIST_PATH = "/booking-list";
    private static final String BOOKING_DETAIL_PAGE
            = "/view/receptionist/staff-booking-detail.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy thông tin chi tiết booking và các yêu cầu gần đây cho popup.
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        Integer bookingId = parsePositiveInteger(request.getParameter("bookingId"));

        if (bookingId == null) {
            response.sendRedirect(request.getContextPath() + BOOKING_LIST_PATH);
            return;
        }

        BookingDAO bookingDAO = new BookingDAO();
        Map<String, Object> bookingDetail
                = bookingDAO.getStaffBookingDetailForPopup(bookingId);

        if (bookingDetail == null || bookingDetail.isEmpty()) {
            response.sendRedirect(request.getContextPath() + BOOKING_LIST_PATH);
            return;
        }

        List<Map<String, Object>> recentRequests
                = bookingDAO.getStaffBookingRecentRequests(bookingId);

        request.setAttribute("bookingDetail", bookingDetail);
        request.setAttribute("recentRequests", recentRequests);

        request.getRequestDispatcher(BOOKING_DETAIL_PAGE).forward(request, response);
    }

    private Integer parsePositiveInteger(String value) {
        // Chuyển chuỗi thành số nguyên dương hợp lệ.
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            int number = Integer.parseInt(value.trim());
            return number >= MIN_VALID_BOOKING_ID ? number : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getServletInfo() {
        // Trả về mô tả của servlet chi tiết booking.
        return "Staff Booking Detail Controller";
    }
}
