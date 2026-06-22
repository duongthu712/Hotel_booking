package controller;

import dao.RoomTypeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import model.RoomType;

public class RoomDetailServlet extends HttpServlet {

    private static final String ROOM_DETAIL_PAGE = "/view/public/room-detail.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        LocalDate today = LocalDate.now();
        setDefaultAttributes(request, today);

        String idRaw = getParam(request, "roomTypeId");
        if (idRaw == null) {
            idRaw = getParam(request, "id");
        }

        Integer roomTypeId = parseInt(idRaw);
        if (roomTypeId == null) {
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

        int numRooms = Math.max(parseIntOrDefault(numRoomsRaw, 1), 1);
        int numGuests = Math.max(parseIntOrDefault(getParam(request, "numGuests"), 1), 1);

        LocalDate checkIn = parseDate(checkInRaw);
        LocalDate checkOut = parseDate(checkOutRaw);

        boolean hasValidDate = false;
        String dateError = "";
        String guestRoomWarning = "";
        String guestRoomError = "";
        int availableRooms = -1;
        long nights = 0;

        boolean hasDateInput = checkInRaw != null || checkOutRaw != null;

        if (hasDateInput) {
            if (checkIn == null || checkOut == null) {
                dateError = "Vui lòng chọn đầy đủ ngày nhận phòng và ngày trả phòng.";
            } else if (checkIn.isBefore(today)) {
                dateError = "Ngày nhận phòng không được nhỏ hơn ngày hiện tại.";
            } else if (!checkOut.isAfter(checkIn)) {
                dateError = "Ngày trả phòng phải sau ngày nhận phòng.";
            } else {
                hasValidDate = true;
                availableRooms = roomTypeDAO.getAvailableRoomCount(
                        roomTypeId,
                        checkIn.toString(),
                        checkOut.toString()
                );

                nights = ChronoUnit.DAYS.between(checkIn, checkOut);

                if (numRooms > availableRooms) {
                    dateError = availableRooms == 0
                            ? "Hạng phòng này đã hết phòng trong thời gian bạn chọn."
                            : "Chỉ còn " + availableRooms + " phòng khả dụng trong thời gian bạn chọn.";
                }
            }
        }

        int maxGuestsByRooms = Math.max(room.getCapacity() * numRooms, 1);

        if (numGuests > maxGuestsByRooms) {
            numGuests = maxGuestsByRooms;
            guestRoomError = "Số khách đã được điều chỉnh vì vượt quá sức chứa tối đa của số phòng đã chọn.";
        }

        if (numGuests < numRooms) {
            guestRoomWarning = "Số khách đang nhỏ hơn số phòng đặt. Nếu bạn đặt hộ người khác thì vẫn có thể tiếp tục.";
        }

        request.setAttribute("room", room);
        request.setAttribute("checkIn", checkIn == null ? "" : checkIn.toString());
        request.setAttribute("checkOut", checkOut == null ? "" : checkOut.toString());
        request.setAttribute("numRooms", numRooms);
        request.setAttribute("numGuests", numGuests);
        request.setAttribute("availableRooms", availableRooms);
        request.setAttribute("nights", nights);
        request.setAttribute("hasValidDate", hasValidDate);
        request.setAttribute("dateError", dateError);
        request.setAttribute("guestRoomWarning", guestRoomWarning);
        request.setAttribute("guestRoomError", guestRoomError);
        request.setAttribute("today", today.toString());
        request.setAttribute("maxGuestsByRooms", maxGuestsByRooms);

        forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        doGet(request, response);
    }

    private void setDefaultAttributes(HttpServletRequest request, LocalDate today) {
        request.setAttribute("checkIn", "");
        request.setAttribute("checkOut", "");
        request.setAttribute("numRooms", 1);
        request.setAttribute("numGuests", 1);
        request.setAttribute("availableRooms", -1);
        request.setAttribute("nights", 0L);
        request.setAttribute("hasValidDate", false);
        request.setAttribute("dateError", "");
        request.setAttribute("guestRoomWarning", "");
        request.setAttribute("guestRoomError", "");
        request.setAttribute("today", today.toString());
        request.setAttribute("maxGuestsByRooms", 1);
    }

    private void forward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(ROOM_DETAIL_PAGE).forward(request, response);
    }

    private String getParam(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private LocalDate parseDate(String value) {
        try {
            return value == null ? null : LocalDate.parse(value);
        } catch (Exception e) {
            return null;
        }
    }

    private Integer parseInt(String value) {
        try {
            return value == null ? null : Integer.parseInt(value);
        } catch (Exception e) {
            return null;
        }
    }

    private int parseIntOrDefault(String value, int defaultValue) {
        Integer number = parseInt(value);
        return number == null ? defaultValue : number;
    }
}
