package controller;

import dao.BookingDAO;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BookingListController extends HttpServlet {

    private static final int DEFAULT_PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String keyword = clean(request.getParameter("keyword"));
        String status = clean(request.getParameter("status"));
        String paymentStatus = clean(request.getParameter("paymentStatus"));
        String source = clean(request.getParameter("source"));
        String roomNumber = clean(request.getParameter("roomNumber"));
        String sort = clean(request.getParameter("sort"));

        String dateFilterRaw = clean(request.getParameter("dateFilter"));
        String dateFilter = normalizeDate(dateFilterRaw);

        /*
         * Hạng phòng vẫn dùng roomTypeId, nhưng option tất cả là "all".
         */
        String filterRoomTypeId = clean(request.getParameter("roomTypeId"));
        if (filterRoomTypeId == null || filterRoomTypeId.equalsIgnoreCase("all")) {
            filterRoomTypeId = "all";
        }

        Integer roomTypeId = null;
        if (!filterRoomTypeId.equalsIgnoreCase("all")) {
            roomTypeId = parseInteger(filterRoomTypeId);
            if (roomTypeId == null) {
                filterRoomTypeId = "all";
            }
        }

        String filterStaffId = clean(request.getParameter("filterStaffId"));
        if (filterStaffId == null || filterStaffId.equalsIgnoreCase("all")) {
            filterStaffId = "all";
        }

        Integer staffId = null;
        if (!filterStaffId.equalsIgnoreCase("all")) {
            staffId = parseInteger(filterStaffId);
            if (staffId == null) {
                filterStaffId = "all";
            }
        }

        int page = parseInt(request.getParameter("page"), 1);
        int pageSize = parseInt(request.getParameter("pageSize"), DEFAULT_PAGE_SIZE);

        BookingDAO bookingDAO = new BookingDAO();

        int totalBookings = bookingDAO.countBookingList(
                keyword,
                status,
                paymentStatus,
                source,
                roomTypeId,
                staffId,
                roomNumber,
                dateFilter
        );

        int totalPages = (int) Math.ceil((double) totalBookings / pageSize);

        if (totalPages == 0) {
            totalPages = 1;
        }

        if (page > totalPages) {
            page = totalPages;
        }

        List<Map<String, Object>> bookingList = bookingDAO.getBookingList(
                keyword,
                status,
                paymentStatus,
                source,
                roomTypeId,
                staffId,
                roomNumber,
                dateFilter,
                sort,
                page,
                pageSize
        );

        List<Map<String, Object>> roomTypes = bookingDAO.getRoomTypesForBookingFilter();
        List<Map<String, Object>> staffList = bookingDAO.getStaffForBookingFilter();

        addStringKey(roomTypes, "roomTypeId", "roomTypeIdText");
        addStringKey(staffList, "staffId", "staffIdText");

        request.setAttribute("bookingList", bookingList);
        request.setAttribute("roomTypes", roomTypes);
        request.setAttribute("staffList", staffList);

        request.setAttribute("totalBookings", totalBookings);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", pageSize);

        request.setAttribute("keyword", keyword);
        request.setAttribute("status", status);
        request.setAttribute("paymentStatus", paymentStatus);
        request.setAttribute("source", source);

        request.setAttribute("filterRoomTypeId", filterRoomTypeId);
        request.setAttribute("filterStaffId", filterStaffId);

        request.setAttribute("roomNumber", roomNumber);
        request.setAttribute("dateFilterDisplay", dateFilterRaw);
        request.setAttribute("sort", sort);

        request.getRequestDispatcher("/view/receptionist/booking-list.jsp")
                .forward(request, response);
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }

        value = value.trim();

        if (value.isEmpty()) {
            return null;
        }

        return value;
    }

    private Integer parseInteger(String value) {
        try {
            value = clean(value);

            if (value == null) {
                return null;
            }

            if (value.equalsIgnoreCase("all")) {
                return null;
            }

            int number = Integer.parseInt(value);

            if (number <= 0) {
                return null;
            }

            return number;

        } catch (Exception e) {
            return null;
        }
    }

    private int parseInt(String value, int defaultValue) {
        try {
            value = clean(value);

            if (value == null) {
                return defaultValue;
            }

            int result = Integer.parseInt(value);

            if (result <= 0) {
                return defaultValue;
            }

            return result;

        } catch (Exception e) {
            return defaultValue;
        }
    }

    private String normalizeDate(String value) {
        try {
            value = clean(value);

            if (value == null) {
                return null;
            }

            String[] parts = value.split("/");

            if (parts.length != 3) {
                return null;
            }

            String day = parts[0].trim();
            String month = parts[1].trim();
            String year = parts[2].trim();

            if (day.length() == 1) {
                day = "0" + day;
            }

            if (month.length() == 1) {
                month = "0" + month;
            }

            if (year.length() != 4) {
                return null;
            }

            return year + "-" + month + "-" + day;

        } catch (Exception e) {
            return null;
        }
    }

    private void addStringKey(List<Map<String, Object>> list, String sourceKey, String targetKey) {
        if (list == null) {
            return;
        }

        for (Map<String, Object> row : list) {
            Object value = row.get(sourceKey);

            if (value == null) {
                row.put(targetKey, "");
            } else {
                row.put(targetKey, String.valueOf(value));
            }
        }
    }
}
