package controller;

import dao.BookingDAO;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CounterRequestController extends HttpServlet {

    private static final DateTimeFormatter DATE_FORMATTER
            = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        int bookingId = parseInt(request.getParameter("bookingId"));
        String requestedType = normalizeRequestTypeNullable(request.getParameter("type"));

        BookingDAO bookingDAO = new BookingDAO();
        Map<String, Object> booking = bookingDAO.getCounterRequestBookingInfo(bookingId);

        if (booking == null || booking.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/booking-list");
            return;
        }

        if (requestedType != null && !isRequestAllowed(booking, requestedType)) {
            request.setAttribute("error", "Yêu cầu này không hợp lệ với trạng thái booking hiện tại.");
        }

        String requestType = resolveDefaultRequestType(booking, requestedType);

        if (requestType == null) {
            request.setAttribute("error", "Trạng thái booking hiện tại không thể tạo yêu cầu.");
            requestType = "other";
        }

        preparePageData(request, bookingDAO, booking, requestType, null, null, 0);

        request.getRequestDispatcher("/view/receptionist/counter-request.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        int bookingId = parseInt(request.getParameter("bookingId"));
        String requestType = normalizeRequestTypeNullable(request.getParameter("requestType"));
        String note = clean(request.getParameter("note"));

        BookingDAO bookingDAO = new BookingDAO();
        Map<String, Object> booking = bookingDAO.getCounterRequestBookingInfo(bookingId);

        if (booking == null || booking.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/booking-list");
            return;
        }

        if (requestType == null || !isRequestAllowed(booking, requestType)) {
            request.setAttribute("error", "Không thể xử lý yêu cầu này vì trạng thái booking không phù hợp.");

            if (requestType == null) {
                requestType = resolveDefaultRequestType(booking, null);
            }

            if (requestType == null) {
                requestType = "other";
            }

            preparePageData(request, bookingDAO, booking, requestType, null, note, 0);
            forwardBack(request, response);
            return;
        }

        boolean success;

        if ("extend".equals(requestType)) {
            success = handleExtendStay(request, response, bookingDAO, booking, note);
        } else if ("upgrade".equals(requestType)) {
            success = handleUpgradeRoomType(request, response, bookingDAO, booking, note);
        } else if ("cancel".equals(requestType)) {
            success = handleCancelBooking(request, response, bookingDAO, booking, note);
        } else {
            success = handleOtherRequest(request, response, bookingDAO, booking, note);
        }

        if (!success) {
            return;
        }

        closePopupAndReload(response);
    }

    private boolean handleExtendStay(
            HttpServletRequest request,
            HttpServletResponse response,
            BookingDAO bookingDAO,
            Map<String, Object> booking,
            String note)
            throws ServletException, IOException {

        String newCheckoutDateRaw = clean(request.getParameter("newCheckoutDate"));

        LocalDate currentCheckoutDate = parseDate(String.valueOf(booking.get("checkoutDateSql")));
        LocalDate newCheckoutDate = parseDate(newCheckoutDateRaw);

        if (newCheckoutDate == null) {
            request.setAttribute("error", "Vui lòng chọn ngày check-out mới.");
            preparePageData(request, bookingDAO, booking, "extend", newCheckoutDateRaw, note, 0);
            forwardBack(request, response);
            return false;
        }

        if (currentCheckoutDate == null || !newCheckoutDate.isAfter(currentCheckoutDate)) {
            request.setAttribute("error", "Ngày check-out mới phải sau ngày check-out hiện tại.");
            preparePageData(request, bookingDAO, booking, "extend", newCheckoutDateRaw, note, 0);
            forwardBack(request, response);
            return false;
        }

        boolean success = bookingDAO.applyCounterExtendStayRequest(
                asInt(booking.get("bookingId")),
                java.sql.Date.valueOf(newCheckoutDate),
                note
        );

        if (!success) {
            request.setAttribute("error", "Không thể áp dụng yêu cầu gia hạn.");
            preparePageData(request, bookingDAO, booking, "extend", newCheckoutDateRaw, note, 0);
            forwardBack(request, response);
            return false;
        }

        return true;
    }

    private boolean handleUpgradeRoomType(
            HttpServletRequest request,
            HttpServletResponse response,
            BookingDAO bookingDAO,
            Map<String, Object> booking,
            String note)
            throws ServletException, IOException {

        int bookingId = asInt(booking.get("bookingId"));
        int currentRoomTypeId = asInt(booking.get("roomTypeId"));
        int targetRoomTypeId = parseInt(request.getParameter("targetRoomTypeId"));

        if (targetRoomTypeId <= 0) {
            request.setAttribute("error", "Vui lòng chọn hạng phòng mới.");
            preparePageData(request, bookingDAO, booking, "upgrade", null, note, 0);
            forwardBack(request, response);
            return false;
        }

        if (targetRoomTypeId == currentRoomTypeId) {
            request.setAttribute("error", "Hạng phòng mới không được trùng với hạng phòng hiện tại.");
            preparePageData(request, bookingDAO, booking, "upgrade", null, note, 0);
            forwardBack(request, response);
            return false;
        }

        boolean success = bookingDAO.applyCounterUpgradeRoomTypeRequest(
                bookingId,
                targetRoomTypeId,
                note
        );

        if (!success) {
            request.setAttribute("error", "Không thể áp dụng yêu cầu nâng cấp hạng phòng.");
            preparePageData(request, bookingDAO, booking, "upgrade", null, note, 0);
            forwardBack(request, response);
            return false;
        }

        return true;
    }

    private boolean handleCancelBooking(
            HttpServletRequest request,
            HttpServletResponse response,
            BookingDAO bookingDAO,
            Map<String, Object> booking,
            String note)
            throws ServletException, IOException {

        int bookingId = asInt(booking.get("bookingId"));
        int totalRooms = asInt(booking.get("numRooms"));
        int cancelRooms = parseInt(request.getParameter("cancelRooms"));

        if (cancelRooms <= 0 || cancelRooms > totalRooms) {
            request.setAttribute("error", "Số phòng muốn hủy không hợp lệ.");
            preparePageData(request, bookingDAO, booking, "cancel", null, note, cancelRooms);
            forwardBack(request, response);
            return false;
        }

        if (note == null || note.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập lý do hủy.");
            preparePageData(request, bookingDAO, booking, "cancel", null, note, cancelRooms);
            forwardBack(request, response);
            return false;
        }

        boolean success = bookingDAO.applyCounterCancelBookingRequest(
                bookingId,
                cancelRooms,
                note
        );

        if (!success) {
            request.setAttribute("error", "Không thể hủy booking. Chỉ hủy được booking ở trạng thái Chờ xử lý hoặc Đã xác nhận.");
            preparePageData(request, bookingDAO, booking, "cancel", null, note, cancelRooms);
            forwardBack(request, response);
            return false;
        }

        return true;
    }

    private boolean handleOtherRequest(
            HttpServletRequest request,
            HttpServletResponse response,
            BookingDAO bookingDAO,
            Map<String, Object> booking,
            String note)
            throws ServletException, IOException {

        int bookingId = asInt(booking.get("bookingId"));

        if (note == null || note.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập nội dung yêu cầu.");
            preparePageData(request, bookingDAO, booking, "other", null, note, 0);
            forwardBack(request, response);
            return false;
        }

        boolean success = bookingDAO.applyCounterOtherRequest(bookingId, note);

        if (!success) {
            request.setAttribute("error", "Không thể tạo yêu cầu khác.");
            preparePageData(request, bookingDAO, booking, "other", null, note, 0);
            forwardBack(request, response);
            return false;
        }

        return true;
    }

    private void preparePageData(
            HttpServletRequest request,
            BookingDAO bookingDAO,
            Map<String, Object> booking,
            String requestType,
            String selectedCheckoutDate,
            String note,
            int selectedCancelRooms) {

        request.setAttribute("booking", booking);
        request.setAttribute("requestType", requestType);
        request.setAttribute("note", note);
        request.setAttribute("newCheckoutDate", selectedCheckoutDate);

        putRequestPermissionAttributes(request, booking);

        int currentRoomTypeId = asInt(booking.get("roomTypeId"));
        int numRooms = asInt(booking.get("numRooms"));
        int totalNights = asInt(booking.get("totalNights"));

        if (totalNights <= 0) {
            LocalDate checkin = parseDate(String.valueOf(booking.get("checkinDateSql")));
            LocalDate checkout = parseDate(String.valueOf(booking.get("checkoutDateSql")));

            if (checkin != null && checkout != null && checkout.isAfter(checkin)) {
                totalNights = (int) ChronoUnit.DAYS.between(checkin, checkout);
            }
        }

        request.setAttribute("totalNights", totalNights);

        BigDecimal currentPrice = asBigDecimal(booking.get("bookedPricePerNight"));

        List<Map<String, Object>> roomTypes
                = bookingDAO.getRoomTypesForCounterUpgrade(currentRoomTypeId);

        int upgradeChargeableNights = calculateUpgradeChargeableNights(booking, totalNights);
        request.setAttribute("upgradeChargeableNights", upgradeChargeableNights);

        for (Map<String, Object> rt : roomTypes) {
            BigDecimal newPrice = asBigDecimal(rt.get("basePrice"));
            BigDecimal priceDiff = newPrice.subtract(currentPrice);

            BigDecimal upgradeTotal = priceDiff
                    .multiply(BigDecimal.valueOf(numRooms))
                    .multiply(BigDecimal.valueOf(upgradeChargeableNights));

            rt.put("priceDiff", priceDiff);
            rt.put("upgradeTotal", upgradeTotal);
        }

        request.setAttribute("availableRoomTypes", roomTypes);

        prepareExtendData(request, booking, selectedCheckoutDate);
        prepareCancelData(request, booking, selectedCancelRooms);
    }

    private void prepareExtendData(
            HttpServletRequest request,
            Map<String, Object> booking,
            String selectedCheckoutDate) {

        LocalDate currentCheckoutDate = parseDate(String.valueOf(booking.get("checkoutDateSql")));
        LocalDate displayCheckoutDate;

        if (selectedCheckoutDate != null && !selectedCheckoutDate.trim().isEmpty()) {
            displayCheckoutDate = parseDate(selectedCheckoutDate);
        } else if (currentCheckoutDate != null) {
            displayCheckoutDate = currentCheckoutDate.plusDays(1);
        } else {
            displayCheckoutDate = null;
        }

        String defaultNewCheckoutDate = "";

        if (displayCheckoutDate != null) {
            defaultNewCheckoutDate = displayCheckoutDate.format(DATE_FORMATTER);
        }

        request.setAttribute("defaultNewCheckoutDate", defaultNewCheckoutDate);

        long extraNights = 0;

        if (currentCheckoutDate != null
                && displayCheckoutDate != null
                && displayCheckoutDate.isAfter(currentCheckoutDate)) {
            extraNights = ChronoUnit.DAYS.between(currentCheckoutDate, displayCheckoutDate);
        }

        BigDecimal price = asBigDecimal(booking.get("bookedPricePerNight"));
        int numRooms = asInt(booking.get("numRooms"));

        BigDecimal estimatedExtraAmount = price
                .multiply(BigDecimal.valueOf(numRooms))
                .multiply(BigDecimal.valueOf(extraNights));

        request.setAttribute("extraNights", extraNights);
        request.setAttribute("estimatedExtraAmount", estimatedExtraAmount);
    }

    private void prepareCancelData(
            HttpServletRequest request,
            Map<String, Object> booking,
            int selectedCancelRooms) {

        int numRooms = asInt(booking.get("numRooms"));
        BigDecimal deposit = asBigDecimal(booking.get("depositAmount"));

        int displayCancelRooms = selectedCancelRooms;

        if (displayCancelRooms <= 0) {
            displayCancelRooms = 1;
        }

        if (numRooms > 0 && displayCancelRooms > numRooms) {
            displayCancelRooms = numRooms;
        }

        if (numRooms <= 0) {
            displayCancelRooms = 0;
        }

        LocalDate checkinDate = parseDate(String.valueOf(booking.get("checkinDateSql")));
        LocalDateTime checkinDeadline = null;

        if (checkinDate != null) {
            checkinDeadline = checkinDate.atTime(14, 0);
        }

        long hoursBeforeCheckin = 0;

        if (checkinDeadline != null) {
            hoursBeforeCheckin = ChronoUnit.HOURS.between(LocalDateTime.now(), checkinDeadline);

            if (hoursBeforeCheckin < 0) {
                hoursBeforeCheckin = 0;
            }
        }

        BigDecimal feeRate;
        String policyText;

        if (hoursBeforeCheckin >= 72) {
            feeRate = BigDecimal.ZERO;
            policyText = "Miễn phí hủy vì còn ít nhất 72 giờ trước 14:00 ngày check-in.";
        } else if (hoursBeforeCheckin >= 48) {
            feeRate = new BigDecimal("0.30");
            policyText = "Hủy trước ít nhất 48 giờ: phí hủy 30% tiền cọc phần phòng hủy.";
        } else if (hoursBeforeCheckin >= 24) {
            feeRate = new BigDecimal("0.50");
            policyText = "Hủy trước ít nhất 24 giờ: phí hủy 50% tiền cọc phần phòng hủy.";
        } else {
            feeRate = new BigDecimal("0.70");
            policyText = "Hủy dưới 24 giờ: phí hủy 70% tiền cọc phần phòng hủy.";
        }

        BigDecimal refundRate = BigDecimal.ONE.subtract(feeRate);

        BigDecimal depositPerRoom = BigDecimal.ZERO;

        if (numRooms > 0) {
            depositPerRoom = deposit.divide(BigDecimal.valueOf(numRooms), 2, RoundingMode.HALF_UP);
        }

        BigDecimal cancelRoomsValue = BigDecimal.valueOf(displayCancelRooms);

        BigDecimal defaultCancelDeposit = depositPerRoom.multiply(cancelRoomsValue);
        BigDecimal defaultCancelFee = defaultCancelDeposit.multiply(feeRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal defaultRefundAmount = defaultCancelDeposit.subtract(defaultCancelFee).setScale(2, RoundingMode.HALF_UP);

        request.setAttribute("selectedCancelRooms", displayCancelRooms);
        request.setAttribute("hoursBeforeCheckin", hoursBeforeCheckin);
        request.setAttribute("cancelFeeRate", feeRate);
        request.setAttribute("cancelRefundRate", refundRate);
        request.setAttribute("cancelPolicyText", policyText);
        request.setAttribute("depositPerRoom", depositPerRoom);
        request.setAttribute("defaultCancelDeposit", defaultCancelDeposit);
        request.setAttribute("defaultCancelFee", defaultCancelFee);
        request.setAttribute("defaultRefundAmount", defaultRefundAmount);
    }

    private void putRequestPermissionAttributes(
            HttpServletRequest request,
            Map<String, Object> booking) {

        String status = String.valueOf(booking.get("bookingStatus"));

        boolean canCancel = "Chờ xử lý".equals(status)
                || "Đã xác nhận".equals(status);

        boolean canExtend = "Đã xác nhận".equals(status)
                || "Đã nhận phòng".equals(status);

        boolean canUpgrade = "Đã xác nhận".equals(status);

        boolean canOther = "Đã xác nhận".equals(status)
                || "Đã nhận phòng".equals(status);

        request.setAttribute("canCancelRequest", canCancel);
        request.setAttribute("canExtendRequest", canExtend);
        request.setAttribute("canUpgradeRequest", canUpgrade);
        request.setAttribute("canOtherRequest", canOther);
    }

    private void forwardBack(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/view/receptionist/counter-request.jsp")
                .forward(request, response);
    }

    private void closePopupAndReload(HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html;charset=UTF-8");

        response.getWriter().write(
                "<!DOCTYPE html>"
                + "<html><head><meta charset='UTF-8'></head><body>"
                + "<script>"
                + "if (window.parent && window.parent.closeBookingPopup) {"
                + "window.parent.closeBookingPopup(true);"
                + "} else {"
                + "window.location.href='booking-list';"
                + "}"
                + "</script>"
                + "</body></html>"
        );
    }

    private String resolveDefaultRequestType(
            Map<String, Object> booking,
            String requestedType) {

        if (requestedType != null && isRequestAllowed(booking, requestedType)) {
            return requestedType;
        }

        String status = String.valueOf(booking.get("bookingStatus"));

        if ("Chờ xử lý".equals(status)) {
            return "cancel";
        }

        if ("Đã xác nhận".equals(status)) {
            return "extend";
        }

        if ("Đã nhận phòng".equals(status)) {
            return "extend";
        }

        return null;
    }

    private String normalizeRequestTypeNullable(String value) {
        value = clean(value);

        if (value == null) {
            return null;
        }

        if ("extend".equals(value)
                || "upgrade".equals(value)
                || "cancel".equals(value)
                || "other".equals(value)) {
            return value;
        }

        return null;
    }

    private LocalDate parseDate(String value) {
        try {
            value = clean(value);

            if (value == null || "null".equalsIgnoreCase(value)) {
                return null;
            }

            return LocalDate.parse(value, DATE_FORMATTER);

        } catch (Exception e) {
            return null;
        }
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

    private int parseInt(String value) {
        try {
            value = clean(value);

            if (value == null) {
                return 0;
            }

            return Integer.parseInt(value);

        } catch (Exception e) {
            return 0;
        }
    }

    private int asInt(Object value) {
        try {
            if (value == null) {
                return 0;
            }

            return Integer.parseInt(String.valueOf(value));

        } catch (Exception e) {
            return 0;
        }
    }

    private BigDecimal asBigDecimal(Object value) {
        try {
            if (value == null) {
                return BigDecimal.ZERO;
            }

            if (value instanceof BigDecimal) {
                return (BigDecimal) value;
            }

            return new BigDecimal(String.valueOf(value));

        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private boolean isRequestAllowed(Map<String, Object> booking, String requestType) {
        String status = String.valueOf(booking.get("bookingStatus"));

        if ("Đã hủy".equals(status) || "Đã trả phòng".equals(status)) {
            return false;
        }

        if ("Chờ xử lý".equals(status)) {
            return "cancel".equals(requestType);
        }

        if ("Đã xác nhận".equals(status)) {
            return "extend".equals(requestType)
                    || "upgrade".equals(requestType)
                    || "cancel".equals(requestType)
                    || "other".equals(requestType);
        }

        if ("Đã nhận phòng".equals(status)) {
            return "extend".equals(requestType)
                    || "other".equals(requestType);
        }

        return false;
    }

    private int calculateUpgradeChargeableNights(Map<String, Object> booking, int totalNights) {
        String status = String.valueOf(booking.get("bookingStatus"));

        if (!"Đã xác nhận".equals(status)) {
            return 0;
        }

        LocalDate checkinDate = parseDate(String.valueOf(booking.get("checkinDateSql")));
        LocalDate checkoutDate = parseDate(String.valueOf(booking.get("checkoutDateSql")));

        if (checkinDate != null && checkoutDate != null && checkoutDate.isAfter(checkinDate)) {
            return (int) ChronoUnit.DAYS.between(checkinDate, checkoutDate);
        }

        return totalNights;
    }
}