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

    private static final String REQUEST_TYPE_EXTEND = "extend";
    private static final String REQUEST_TYPE_UPGRADE = "upgrade";
    private static final String REQUEST_TYPE_CANCEL = "cancel";
    private static final String REQUEST_TYPE_OTHER = "other";

    private static final String STATUS_PENDING = "Chờ xử lý";
    private static final String STATUS_CONFIRMED = "Đã xác nhận";
    private static final String STATUS_CHECKED_IN = "Đã nhận phòng";
    private static final String STATUS_CHECKED_OUT = "Đã trả phòng";
    private static final String STATUS_CANCELLED = "Đã hủy";

    private static final int DEFAULT_INT_VALUE = 0;
    private static final int DEFAULT_CANCEL_ROOMS = 1;
    private static final int EMPTY_CANCEL_ROOMS = 0;

    private static final int STANDARD_CHECKIN_HOUR = 14;
    private static final int STANDARD_CHECKIN_MINUTE = 0;

    private static final int FREE_CANCEL_HOURS = 72;
    private static final int LOW_FEE_CANCEL_HOURS = 48;
    private static final int MEDIUM_FEE_CANCEL_HOURS = 24;

    private static final BigDecimal FREE_CANCEL_FEE_RATE = BigDecimal.ZERO;
    private static final BigDecimal LOW_CANCEL_FEE_RATE = new BigDecimal("0.30");
    private static final BigDecimal MEDIUM_CANCEL_FEE_RATE = new BigDecimal("0.50");
    private static final BigDecimal HIGH_CANCEL_FEE_RATE = new BigDecimal("0.70");

    private static final int MONEY_SCALE = 2;
    private static final RoundingMode MONEY_ROUNDING_MODE = RoundingMode.HALF_UP;

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
            requestType = REQUEST_TYPE_OTHER;
        }

        preparePageData(request, bookingDAO, booking, requestType, null, null, DEFAULT_INT_VALUE);

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
                requestType = REQUEST_TYPE_OTHER;
            }

            preparePageData(request, bookingDAO, booking, requestType, null, note, DEFAULT_INT_VALUE);
            forwardBack(request, response);
            return;
        }

        boolean success;

        if (REQUEST_TYPE_EXTEND.equals(requestType)) {
            success = handleExtendStay(request, response, bookingDAO, booking, note);
        } else if (REQUEST_TYPE_UPGRADE.equals(requestType)) {
            success = handleUpgradeRoomType(request, response, bookingDAO, booking, note);
        } else if (REQUEST_TYPE_CANCEL.equals(requestType)) {
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
            preparePageData(request, bookingDAO, booking, REQUEST_TYPE_EXTEND, newCheckoutDateRaw, note, DEFAULT_INT_VALUE);
            forwardBack(request, response);
            return false;
        }

        if (currentCheckoutDate == null || !newCheckoutDate.isAfter(currentCheckoutDate)) {
            request.setAttribute("error", "Ngày check-out mới phải sau ngày check-out hiện tại.");
            preparePageData(request, bookingDAO, booking, REQUEST_TYPE_EXTEND, newCheckoutDateRaw, note, DEFAULT_INT_VALUE);
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
            preparePageData(request, bookingDAO, booking, REQUEST_TYPE_EXTEND, newCheckoutDateRaw, note, DEFAULT_INT_VALUE);
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

        if (targetRoomTypeId <= DEFAULT_INT_VALUE) {
            request.setAttribute("error", "Vui lòng chọn hạng phòng mới.");
            preparePageData(request, bookingDAO, booking, REQUEST_TYPE_UPGRADE, null, note, DEFAULT_INT_VALUE);
            forwardBack(request, response);
            return false;
        }

        if (targetRoomTypeId == currentRoomTypeId) {
            request.setAttribute("error", "Hạng phòng mới không được trùng với hạng phòng hiện tại.");
            preparePageData(request, bookingDAO, booking, REQUEST_TYPE_UPGRADE, null, note, DEFAULT_INT_VALUE);
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
            preparePageData(request, bookingDAO, booking, REQUEST_TYPE_UPGRADE, null, note, DEFAULT_INT_VALUE);
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

        if (cancelRooms <= DEFAULT_INT_VALUE || cancelRooms > totalRooms) {
            request.setAttribute("error", "Số phòng muốn hủy không hợp lệ.");
            preparePageData(request, bookingDAO, booking, REQUEST_TYPE_CANCEL, null, note, cancelRooms);
            forwardBack(request, response);
            return false;
        }

        if (note == null || note.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập lý do hủy.");
            preparePageData(request, bookingDAO, booking, REQUEST_TYPE_CANCEL, null, note, cancelRooms);
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
            preparePageData(request, bookingDAO, booking, REQUEST_TYPE_CANCEL, null, note, cancelRooms);
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
            preparePageData(request, bookingDAO, booking, REQUEST_TYPE_OTHER, null, note, DEFAULT_INT_VALUE);
            forwardBack(request, response);
            return false;
        }

        boolean success = bookingDAO.applyCounterOtherRequest(bookingId, note);

        if (!success) {
            request.setAttribute("error", "Không thể tạo yêu cầu khác.");
            preparePageData(request, bookingDAO, booking, REQUEST_TYPE_OTHER, null, note, DEFAULT_INT_VALUE);
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

        if (totalNights <= DEFAULT_INT_VALUE) {
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

        long extraNights = DEFAULT_INT_VALUE;

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

        if (displayCancelRooms <= DEFAULT_INT_VALUE) {
            displayCancelRooms = DEFAULT_CANCEL_ROOMS;
        }

        if (numRooms > DEFAULT_INT_VALUE && displayCancelRooms > numRooms) {
            displayCancelRooms = numRooms;
        }

        if (numRooms <= DEFAULT_INT_VALUE) {
            displayCancelRooms = EMPTY_CANCEL_ROOMS;
        }

        LocalDate checkinDate = parseDate(String.valueOf(booking.get("checkinDateSql")));
        LocalDateTime checkinDeadline = null;

        if (checkinDate != null) {
            checkinDeadline = checkinDate.atTime(STANDARD_CHECKIN_HOUR, STANDARD_CHECKIN_MINUTE);
        }

        long hoursBeforeCheckin = DEFAULT_INT_VALUE;

        if (checkinDeadline != null) {
            hoursBeforeCheckin = ChronoUnit.HOURS.between(LocalDateTime.now(), checkinDeadline);

            if (hoursBeforeCheckin < DEFAULT_INT_VALUE) {
                hoursBeforeCheckin = DEFAULT_INT_VALUE;
            }
        }

        BigDecimal feeRate = getCancelFeeRate(hoursBeforeCheckin);
        String policyText = getCancelPolicyText(hoursBeforeCheckin);

        BigDecimal refundRate = BigDecimal.ONE.subtract(feeRate);

        BigDecimal depositPerRoom = BigDecimal.ZERO;

        if (numRooms > DEFAULT_INT_VALUE) {
            depositPerRoom = deposit.divide(
                    BigDecimal.valueOf(numRooms),
                    MONEY_SCALE,
                    MONEY_ROUNDING_MODE
            );
        }

        BigDecimal cancelRoomsValue = BigDecimal.valueOf(displayCancelRooms);
        BigDecimal defaultCancelDeposit = depositPerRoom.multiply(cancelRoomsValue);

        BigDecimal defaultCancelFee = defaultCancelDeposit
                .multiply(feeRate)
                .setScale(MONEY_SCALE, MONEY_ROUNDING_MODE);

        BigDecimal defaultRefundAmount = defaultCancelDeposit
                .subtract(defaultCancelFee)
                .setScale(MONEY_SCALE, MONEY_ROUNDING_MODE);

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

    private BigDecimal getCancelFeeRate(long hoursBeforeCheckin) {
        if (hoursBeforeCheckin >= FREE_CANCEL_HOURS) {
            return FREE_CANCEL_FEE_RATE;
        }

        if (hoursBeforeCheckin >= LOW_FEE_CANCEL_HOURS) {
            return LOW_CANCEL_FEE_RATE;
        }

        if (hoursBeforeCheckin >= MEDIUM_FEE_CANCEL_HOURS) {
            return MEDIUM_CANCEL_FEE_RATE;
        }

        return HIGH_CANCEL_FEE_RATE;
    }

    private String getCancelPolicyText(long hoursBeforeCheckin) {
        if (hoursBeforeCheckin >= FREE_CANCEL_HOURS) {
            return "Miễn phí hủy vì còn ít nhất "
                    + FREE_CANCEL_HOURS
                    + " giờ trước "
                    + STANDARD_CHECKIN_HOUR
                    + ":00 ngày check-in.";
        }

        if (hoursBeforeCheckin >= LOW_FEE_CANCEL_HOURS) {
            return "Hủy trước ít nhất "
                    + LOW_FEE_CANCEL_HOURS
                    + " giờ: phí hủy "
                    + formatPercent(LOW_CANCEL_FEE_RATE)
                    + " tiền cọc phần phòng hủy.";
        }

        if (hoursBeforeCheckin >= MEDIUM_FEE_CANCEL_HOURS) {
            return "Hủy trước ít nhất "
                    + MEDIUM_FEE_CANCEL_HOURS
                    + " giờ: phí hủy "
                    + formatPercent(MEDIUM_CANCEL_FEE_RATE)
                    + " tiền cọc phần phòng hủy.";
        }

        return "Hủy dưới "
                + MEDIUM_FEE_CANCEL_HOURS
                + " giờ: phí hủy "
                + formatPercent(HIGH_CANCEL_FEE_RATE)
                + " tiền cọc phần phòng hủy.";
    }

    private String formatPercent(BigDecimal rate) {
        return rate.multiply(BigDecimal.valueOf(100))
                .stripTrailingZeros()
                .toPlainString()
                + "%";
    }

    private void putRequestPermissionAttributes(
            HttpServletRequest request,
            Map<String, Object> booking) {

        String status = String.valueOf(booking.get("bookingStatus"));

        boolean canCancel = STATUS_PENDING.equals(status)
                || STATUS_CONFIRMED.equals(status);

        boolean canExtend = STATUS_CONFIRMED.equals(status)
                || STATUS_CHECKED_IN.equals(status);

        boolean canUpgrade = STATUS_CONFIRMED.equals(status);

        boolean canOther = STATUS_CONFIRMED.equals(status)
                || STATUS_CHECKED_IN.equals(status);

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

        if (STATUS_PENDING.equals(status)) {
            return REQUEST_TYPE_CANCEL;
        }

        if (STATUS_CONFIRMED.equals(status)) {
            return REQUEST_TYPE_EXTEND;
        }

        if (STATUS_CHECKED_IN.equals(status)) {
            return REQUEST_TYPE_EXTEND;
        }

        return null;
    }

    private String normalizeRequestTypeNullable(String value) {
        value = clean(value);

        if (value == null) {
            return null;
        }

        if (REQUEST_TYPE_EXTEND.equals(value)
                || REQUEST_TYPE_UPGRADE.equals(value)
                || REQUEST_TYPE_CANCEL.equals(value)
                || REQUEST_TYPE_OTHER.equals(value)) {
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
                return DEFAULT_INT_VALUE;
            }

            return Integer.parseInt(value);

        } catch (Exception e) {
            return DEFAULT_INT_VALUE;
        }
    }

    private int asInt(Object value) {
        try {
            if (value == null) {
                return DEFAULT_INT_VALUE;
            }

            return Integer.parseInt(String.valueOf(value));

        } catch (Exception e) {
            return DEFAULT_INT_VALUE;
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

        if (STATUS_CANCELLED.equals(status) || STATUS_CHECKED_OUT.equals(status)) {
            return false;
        }

        if (STATUS_PENDING.equals(status)) {
            return REQUEST_TYPE_CANCEL.equals(requestType);
        }

        if (STATUS_CONFIRMED.equals(status)) {
            return REQUEST_TYPE_EXTEND.equals(requestType)
                    || REQUEST_TYPE_UPGRADE.equals(requestType)
                    || REQUEST_TYPE_CANCEL.equals(requestType)
                    || REQUEST_TYPE_OTHER.equals(requestType);
        }

        if (STATUS_CHECKED_IN.equals(status)) {
            return REQUEST_TYPE_EXTEND.equals(requestType)
                    || REQUEST_TYPE_OTHER.equals(requestType);
        }

        return false;
    }

    private int calculateUpgradeChargeableNights(Map<String, Object> booking, int totalNights) {
        String status = String.valueOf(booking.get("bookingStatus"));

        if (!STATUS_CONFIRMED.equals(status)) {
            return DEFAULT_INT_VALUE;
        }

        LocalDate checkinDate = parseDate(String.valueOf(booking.get("checkinDateSql")));
        LocalDate checkoutDate = parseDate(String.valueOf(booking.get("checkoutDateSql")));

        if (checkinDate != null && checkoutDate != null && checkoutDate.isAfter(checkinDate)) {
            return (int) ChronoUnit.DAYS.between(checkinDate, checkoutDate);
        }

        return totalNights;
    }
}
