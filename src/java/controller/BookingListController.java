package controller;

import dao.BookingDAO;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BookingListController extends HttpServlet {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MIN_TOTAL_PAGES = 1;
    private static final int MIN_PAGE_SIZE = 5;
    private static final int MAX_PAGE_SIZE = 50;
    private static final int PAGINATION_WINDOW_SIZE = 2;
    private static final int BOOKING_TABLE_COLUMN_COUNT = 10;
    private static final int POPUP_CLOSE_DELAY_MS = 150;
    private static final int REQUEST_MENU_SAFE_GAP_PX = 18;

    private static final String ALL_FILTER_VALUE = "all";
    private static final String DEFAULT_SORT = "newest";

    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT
            = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

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
        String sort = normalizeSort(request.getParameter("sort"));
        String dateFilter = normalizeDate(request.getParameter("dateFilter"));

        String filterRoomTypeId = normalizeAllFilter(request.getParameter("roomTypeId"));
        Integer roomTypeId = null;

        if (!isAllFilter(filterRoomTypeId)) {
            roomTypeId = parseInteger(filterRoomTypeId);

            if (roomTypeId == null) {
                filterRoomTypeId = ALL_FILTER_VALUE;
            }
        }

        String filterStaffId = normalizeAllFilter(request.getParameter("filterStaffId"));
        Integer staffId = null;

        if (!isAllFilter(filterStaffId)) {
            staffId = parseInteger(filterStaffId);

            if (staffId == null) {
                filterStaffId = ALL_FILTER_VALUE;
            }
        }

        int page = parseInt(request.getParameter("page"), DEFAULT_PAGE);
        int pageSize = parseInt(request.getParameter("pageSize"), DEFAULT_PAGE_SIZE);

        if (pageSize < MIN_PAGE_SIZE || pageSize > MAX_PAGE_SIZE) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        BookingDAO bookingDAO = new BookingDAO();

        int totalBookings = bookingDAO.countBookingList(
                keyword, status, paymentStatus, source, roomTypeId,
                staffId, roomNumber, dateFilter
        );

        int totalPages = (int) Math.ceil((double) totalBookings / pageSize);

        if (totalPages < MIN_TOTAL_PAGES) {
            totalPages = MIN_TOTAL_PAGES;
        }

        if (page > totalPages) {
            page = totalPages;
        }

        List<Map<String, Object>> bookingList = bookingDAO.getBookingList(
                keyword, status, paymentStatus, source, roomTypeId,
                staffId, roomNumber, dateFilter, sort, page, pageSize
        );

        formatBookingDateTimes(bookingList);

        List<Map<String, Object>> roomTypes = bookingDAO.getRoomTypesForBookingFilter();
        List<Map<String, Object>> staffList = bookingDAO.getStaffForBookingFilter();

        addStringKey(roomTypes, "roomTypeId", "roomTypeIdText");
        addStringKey(staffList, "staffId", "staffIdText");

        String pagingQuery = buildPagingQuery(pageSize, keyword, status, paymentStatus, source, filterRoomTypeId, filterStaffId, roomNumber, dateFilter, sort);

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
        request.setAttribute("dateFilterDisplay", dateFilter);
        request.setAttribute("sort", sort);
        request.setAttribute("pagingQuery", pagingQuery);

        request.setAttribute("paginationWindowSize", PAGINATION_WINDOW_SIZE);
        request.setAttribute("bookingTableColumnCount", BOOKING_TABLE_COLUMN_COUNT);
        request.setAttribute("popupCloseDelayMs", POPUP_CLOSE_DELAY_MS);
        request.setAttribute("requestMenuSafeGapPx", REQUEST_MENU_SAFE_GAP_PX);

        request.getRequestDispatcher("/view/receptionist/booking-list.jsp").forward(request, response);
    }

    private String normalizeAllFilter(String value) {
        value = clean(value);

        if (value == null || value.equalsIgnoreCase(ALL_FILTER_VALUE)) {
            return ALL_FILTER_VALUE;
        }

        return value;
    }

    private boolean isAllFilter(String value) {
        return value == null || value.equalsIgnoreCase(ALL_FILTER_VALUE);
    }

    private String normalizeSort(String value) {
        value = clean(value);

        if ("oldest".equals(value)
                || "checkinAsc".equals(value)
                || "checkoutAsc".equals(value)) {
            return value;
        }

        return DEFAULT_SORT;
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }

        value = value.trim();
        return value.isEmpty() ? null : value;
    }

    private Integer parseInteger(String value) {
        try {
            value = clean(value);

            if (value == null || isAllFilter(value)) {
                return null;
            }

            int number = Integer.parseInt(value);
            return number > 0 ? number : null;

        } catch (NumberFormatException e) {
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
            return result > 0 ? result : defaultValue;

        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private String normalizeDate(String value) {
        value = clean(value);

        if (value == null) {
            return null;
        }

        try {
            return LocalDate.parse(value).toString();
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private void formatBookingDateTimes(List<Map<String, Object>> bookingList) {
        if (bookingList == null) {
            return;
        }

        for (Map<String, Object> booking : bookingList) {
            booking.put("actualCheckinTimeText", formatDateTime(booking.get("actualCheckinTime")));
            booking.put("actualCheckoutTimeText", formatDateTime(booking.get("actualCheckoutTime")));
        }
    }

    private String formatDateTime(Object value) {
        if (value == null) {
            return "-";
        }

        LocalDateTime dateTime = convertToLocalDateTime(value);

        if (dateTime != null) {
            return dateTime.format(DISPLAY_DATE_TIME_FORMAT);
        }

        String text = value.toString().trim();
        return text.isEmpty() ? "-" : text;
    }

    private LocalDateTime convertToLocalDateTime(Object value) {
        if (value instanceof Timestamp) {
            return ((Timestamp) value).toLocalDateTime();
        }

        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }

        if (value instanceof OffsetDateTime) {
            return ((OffsetDateTime) value).toLocalDateTime();
        }

        if (value instanceof Date) {
            return new Timestamp(((Date) value).getTime()).toLocalDateTime();
        }

        String text = value.toString().trim();

        if (text.isEmpty()) {
            return null;
        }

        try {
            return Timestamp.valueOf(text).toLocalDateTime();
        } catch (IllegalArgumentException ignored) {
        }

        try {
            return LocalDateTime.parse(text);
        } catch (DateTimeParseException ignored) {
        }

        try {
            return LocalDateTime.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (DateTimeParseException ignored) {
        }

        return null;
    }

    private void addStringKey(List<Map<String, Object>> list, String sourceKey, String targetKey) {
        if (list == null) {
            return;
        }

        for (Map<String, Object> row : list) {
            Object value = row.get(sourceKey);
            row.put(targetKey, value == null ? "" : String.valueOf(value));
        }
    }

    private String buildPagingQuery(int pageSize, String keyword, String status, String paymentStatus, String source, String filterRoomTypeId, String filterStaffId, String roomNumber, String dateFilter, String sort) {
        StringBuilder query = new StringBuilder();

        appendQueryParam(query, "pageSize", String.valueOf(pageSize));
        appendQueryParam(query, "keyword", keyword);
        appendQueryParam(query, "status", status);
        appendQueryParam(query, "paymentStatus", paymentStatus);
        appendQueryParam(query, "source", source);
        appendQueryParam(query, "roomTypeId", filterRoomTypeId);
        appendQueryParam(query, "filterStaffId", filterStaffId);
        appendQueryParam(query, "roomNumber", roomNumber);
        appendQueryParam(query, "dateFilter", dateFilter);
        appendQueryParam(query, "sort", sort);

        return query.toString();
    }

    private void appendQueryParam(StringBuilder query, String name, String value) {
        if (value == null) {
            value = "";
        }

        if (query.length() > 0) {
            query.append("&");
        }

        query.append(urlEncode(name));
        query.append("=");
        query.append(urlEncode(value));
    }

    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            return "";
        }
    }
}
