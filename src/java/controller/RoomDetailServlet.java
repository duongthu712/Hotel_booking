package controller;

import dao.BookingDAO;
import dao.RoomTypeDAO;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.RoomType;

public class RoomDetailServlet extends HttpServlet {

    private static final String ROOM_DETAIL_PAGE = "/view/public/room-detail.jsp";
    private static final String DISPLAY_DATE_PATTERN = "dd/MM/yyyy";
    private static final ZoneId HOTEL_TIME_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    private static final int SAME_DAY_CUTOFF_HOUR = 14;
    private static final int SAME_DAY_CUTOFF_MINUTE = 0;
    private static final int MIN_ROOM_COUNT = 1;
    private static final int MIN_ADULT_COUNT = 1;
    private static final int MIN_CHILD_COUNT = 0;
    private static final int NO_AVAILABLE_ROOMS = 0;
    private static final int DEFAULT_AVAILABLE_ROOMS = -1;

    private static final long DEFAULT_NIGHTS = 0L;
    private static final long NEXT_DAY_OFFSET = 1L;

    private static final LocalTime SAME_DAY_CUTOFF = LocalTime.of(SAME_DAY_CUTOFF_HOUR, SAME_DAY_CUTOFF_MINUTE);
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER = DateTimeFormatter.ofPattern(DISPLAY_DATE_PATTERN);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Kiểm tra thông tin phòng, thời gian lưu trú và số lượng khách.
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        LocalDateTime currentDateTime = LocalDateTime.now(HOTEL_TIME_ZONE);
        LocalDate currentDate = currentDateTime.toLocalDate();
        LocalDate minimumCheckInDate = getMinimumCheckInDate(currentDateTime);

        setDefaultAttributes(request, currentDate, minimumCheckInDate);

        String roomTypeIdRaw = getParam(request, "roomTypeId");

        if (roomTypeIdRaw == null) {
            roomTypeIdRaw = getParam(request, "id");
        }

        Integer roomTypeId = parseInt(roomTypeIdRaw);

        if (roomTypeId == null || roomTypeId < MIN_ROOM_COUNT) {
            request.setAttribute("error", "Không tìm thấy loại phòng.");
            forward(request, response);
            return;
        }

        RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
        RoomType room = roomTypeDAO.getRoomDetailById(roomTypeId);

        if (room == null) {
            request.setAttribute("error", "Không tìm thấy loại phòng.");
            forward(request, response);
            return;
        }

        String checkInRaw = getParam(request, "checkIn");
        String checkOutRaw = getParam(request, "checkOut");
        String numRoomsRaw = getParam(request, "numRooms");

        if (numRoomsRaw == null) {
            numRoomsRaw = getParam(request, "roomQuantity");
        }

        int numRooms = Math.max(parseIntOrDefault(numRoomsRaw, MIN_ROOM_COUNT), MIN_ROOM_COUNT);
        int numAdults = Math.max(parseIntOrDefault(getParam(request, "numGuests"), MIN_ADULT_COUNT), MIN_ADULT_COUNT);
        int numChildren = Math.max(parseIntOrDefault(getParam(request, "numChildren"), MIN_CHILD_COUNT), MIN_CHILD_COUNT);

        LocalDate checkIn = parseDate(checkInRaw);
        LocalDate checkOut = parseDate(checkOutRaw);

        boolean hasDateInput = checkInRaw != null || checkOutRaw != null;
        boolean hasFormInput = hasDateInput || getParam(request, "numRooms") != null
                || getParam(request, "roomQuantity") != null
                || getParam(request, "numGuests") != null
                || getParam(request, "numChildren") != null;

        boolean hasValidDate = false;
        String dateError = "";
        String guestRoomWarning = "";
        String guestRoomError = "";

        int availableRooms = DEFAULT_AVAILABLE_ROOMS;
        long nights = DEFAULT_NIGHTS;

        if (hasDateInput) {
            if (checkIn == null || checkOut == null) {
                dateError = "Vui lòng chọn đầy đủ ngày nhận phòng và ngày trả phòng.";
            } else if (checkIn.isBefore(minimumCheckInDate)) {
                dateError = "Ngày nhận phòng sớm nhất là "
                        + minimumCheckInDate.format(DISPLAY_DATE_FORMATTER) + ".";
            } else if (!checkOut.isAfter(checkIn)) {
                dateError = "Ngày trả phòng phải sau ngày nhận phòng.";
            } else {
                BookingDAO bookingDAO = new BookingDAO();
                bookingDAO.cancelExpiredBookings();

                availableRooms = roomTypeDAO.getAvailableRoomCount(roomTypeId, checkIn.toString(), checkOut.toString());
                nights = ChronoUnit.DAYS.between(checkIn, checkOut);

                if (numRooms > availableRooms) {
                    dateError = availableRooms == NO_AVAILABLE_ROOMS
                            ? "Hạng phòng này đã hết phòng trong thời gian bạn chọn."
                            : "Chỉ còn " + availableRooms + " phòng khả dụng trong thời gian bạn chọn.";
                } else {
                    hasValidDate = true;
                }
            }
        }

        int adultsPerRoom = room.getNumGuests() >= MIN_ADULT_COUNT
                ? room.getNumGuests()
                : Math.max(room.getCapacity(), MIN_ADULT_COUNT);

        int childrenPerRoom = Math.max(room.getNumChildren(), MIN_CHILD_COUNT);
        int capacityPerRoom = Math.max(room.getCapacity(), MIN_ADULT_COUNT);

        int maxAdultsByRooms = adultsPerRoom * numRooms;
        int maxChildrenByRooms = childrenPerRoom * numRooms;
        int maxGuestsByRooms = capacityPerRoom * numRooms;
        int totalGuests = numAdults + numChildren;

        if (numAdults > maxAdultsByRooms) {
            guestRoomError = "Số người lớn không được vượt quá "
                    + maxAdultsByRooms + " người đối với " + numRooms + " phòng đã chọn.";
        } else if (numChildren > maxChildrenByRooms) {
            guestRoomError = "Số trẻ em không được vượt quá "
                    + maxChildrenByRooms + " trẻ đối với " + numRooms + " phòng đã chọn.";
        } else if (totalGuests > maxGuestsByRooms) {
            guestRoomError = "Tổng số người lớn và trẻ em không được vượt quá "
                    + maxGuestsByRooms + " người đối với " + numRooms + " phòng đã chọn.";
        }

        if (guestRoomError.isEmpty() && numAdults < numRooms) {
            guestRoomWarning = "Số người lớn đang nhỏ hơn số phòng đặt. "
                    + "Mỗi phòng nên có ít nhất một người lớn.";
        }

        LocalDate minimumCheckOutDate = checkIn == null
                ? minimumCheckInDate.plusDays(NEXT_DAY_OFFSET)
                : checkIn.plusDays(NEXT_DAY_OFFSET);

        request.setAttribute("room", room);
        request.setAttribute("checkIn", checkIn == null ? "" : checkIn.toString());
        request.setAttribute("checkOut", checkOut == null ? "" : checkOut.toString());
        request.setAttribute("numRooms", numRooms);
        request.setAttribute("numGuests", numAdults);
        request.setAttribute("numChildren", numChildren);
        request.setAttribute("availableRooms", availableRooms);
        request.setAttribute("nights", nights);
        request.setAttribute("hasValidDate", hasValidDate);
        request.setAttribute("dateError", dateError);
        request.setAttribute("guestRoomWarning", guestRoomWarning);
        request.setAttribute("guestRoomError", guestRoomError);
        request.setAttribute("today", currentDate.toString());
        request.setAttribute("minCheckInDate", minimumCheckInDate.toString());
        request.setAttribute("minCheckOutDate", minimumCheckOutDate.toString());
        request.setAttribute("maxGuestsByRooms", maxGuestsByRooms);
        request.setAttribute("maxAdultsByRooms", maxAdultsByRooms);
        request.setAttribute("maxChildrenByRooms", maxChildrenByRooms);
        request.setAttribute("clearQueryAfterLoad", hasFormInput);

        forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Xử lý POST bằng cùng logic kiểm tra với GET.
        doGet(request, response);
    }

    private LocalDate getMinimumCheckInDate(LocalDateTime currentDateTime) {
        // Xác định ngày nhận phòng sớm nhất theo mốc 14 giờ.
        LocalDate currentDate = currentDateTime.toLocalDate();
        LocalTime currentTime = currentDateTime.toLocalTime();

        return currentTime.isBefore(SAME_DAY_CUTOFF)
                ? currentDate
                : currentDate.plusDays(NEXT_DAY_OFFSET);
    }

    private void setDefaultAttributes(HttpServletRequest request, LocalDate currentDate, LocalDate minimumCheckInDate) {
        // Khởi tạo dữ liệu mặc định cho trang chi tiết phòng.
        request.setAttribute("checkIn", "");
        request.setAttribute("checkOut", "");
        request.setAttribute("numRooms", MIN_ROOM_COUNT);
        request.setAttribute("numGuests", MIN_ADULT_COUNT);
        request.setAttribute("numChildren", MIN_CHILD_COUNT);
        request.setAttribute("availableRooms", DEFAULT_AVAILABLE_ROOMS);
        request.setAttribute("nights", DEFAULT_NIGHTS);
        request.setAttribute("hasValidDate", false);
        request.setAttribute("dateError", "");
        request.setAttribute("guestRoomWarning", "");
        request.setAttribute("guestRoomError", "");
        request.setAttribute("today", currentDate.toString());
        request.setAttribute("minCheckInDate", minimumCheckInDate.toString());
        request.setAttribute("minCheckOutDate", minimumCheckInDate.plusDays(NEXT_DAY_OFFSET).toString());
        request.setAttribute("maxGuestsByRooms", MIN_ADULT_COUNT);
        request.setAttribute("maxAdultsByRooms", MIN_ADULT_COUNT);
        request.setAttribute("maxChildrenByRooms", MIN_CHILD_COUNT);
        request.setAttribute("clearQueryAfterLoad", false);
    }

    private void forward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Chuyển dữ liệu sang trang chi tiết phòng.
        request.getRequestDispatcher(ROOM_DETAIL_PAGE).forward(request, response);
    }

    private String getParam(HttpServletRequest request, String name) {
        // Lấy và làm sạch giá trị parameter.
        String value = request.getParameter(name);
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private LocalDate parseDate(String value) {
        // Chuyển chuỗi ngày sang LocalDate.
        try {
            return value == null ? null : LocalDate.parse(value);
        } catch (Exception e) {
            return null;
        }
    }

    private Integer parseInt(String value) {
        // Chuyển chuỗi thành số nguyên.
        try {
            return value == null ? null : Integer.parseInt(value);
        } catch (Exception e) {
            return null;
        }
    }

    private int parseIntOrDefault(String value, int defaultValue) {
        // Trả về số nguyên hoặc giá trị mặc định.
        Integer number = parseInt(value);
        return number == null ? defaultValue : number;
    }

    @Override
    public String getServletInfo() {
        // Trả về mô tả của servlet.
        return "Room Detail Servlet";
    }
}