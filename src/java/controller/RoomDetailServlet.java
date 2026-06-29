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

    private static final String ROOM_DETAIL_PAGE
            = "/view/public/room-detail.jsp";

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
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
            request.setAttribute(
                    "error",
                    "Không tìm thấy loại phòng."
            );

            forward(request, response);
            return;
        }

        RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
        RoomType room
                = roomTypeDAO.getRoomDetailById(roomTypeId);

        if (room == null) {
            request.setAttribute(
                    "error",
                    "Không tìm thấy loại phòng."
            );

            forward(request, response);
            return;
        }

        String checkInRaw = getParam(request, "checkIn");
        String checkOutRaw = getParam(request, "checkOut");

        String numRoomsRaw = getParam(request, "numRooms");

        if (numRoomsRaw == null) {
            numRoomsRaw = getParam(
                    request,
                    "roomQuantity"
            );
        }

        int numRooms = Math.max(
                parseIntOrDefault(numRoomsRaw, 1),
                1
        );

        /*
         * numGuests được giữ nguyên tên parameter để không ảnh hưởng
         * các trang hiện tại, nhưng được hiểu là số người lớn.
         */
        int numAdults = Math.max(
                parseIntOrDefault(
                        getParam(request, "numGuests"),
                        1
                ),
                1
        );

        int numChildren = Math.max(
                parseIntOrDefault(
                        getParam(request, "numChildren"),
                        0
                ),
                0
        );

        LocalDate checkIn = parseDate(checkInRaw);
        LocalDate checkOut = parseDate(checkOutRaw);

        boolean hasValidDate = false;

        String dateError = "";
        String guestRoomWarning = "";
        String guestRoomError = "";

        int availableRooms = -1;
        long nights = 0;

        boolean hasDateInput
                = checkInRaw != null
                || checkOutRaw != null;

        if (hasDateInput) {
            if (checkIn == null || checkOut == null) {
                dateError
                        = "Vui lòng chọn đầy đủ ngày nhận phòng "
                        + "và ngày trả phòng.";

            } else if (checkIn.isBefore(today)) {
                dateError
                        = "Ngày nhận phòng không được nhỏ hơn "
                        + "ngày hiện tại.";

            } else if (!checkOut.isAfter(checkIn)) {
                dateError
                        = "Ngày trả phòng phải sau ngày nhận phòng.";

            } else {
                hasValidDate = true;

                availableRooms
                        = roomTypeDAO.getAvailableRoomCount(
                                roomTypeId,
                                checkIn.toString(),
                                checkOut.toString()
                        );

                nights = ChronoUnit.DAYS.between(
                        checkIn,
                        checkOut
                );

                if (numRooms > availableRooms) {
                    dateError = availableRooms == 0
                            ? "Hạng phòng này đã hết phòng "
                            + "trong thời gian bạn chọn."
                            : "Chỉ còn "
                            + availableRooms
                            + " phòng khả dụng trong thời gian "
                            + "bạn chọn.";
                }
            }
        }

        /*
         * Số người lớn tối đa của một phòng.
         * Nếu dữ liệu cũ bị bằng 0 thì tạm dùng capacity.
         */
        int adultsPerRoom = room.getNumGuests() > 0
                ? room.getNumGuests()
                : Math.max(room.getCapacity(), 1);

        /*
         * Số trẻ em tối đa của một phòng.
         */
        int childrenPerRoom
                = Math.max(room.getNumChildren(), 0);

        /*
         * Tổng số người tối đa của một phòng.
         */
        int capacityPerRoom
                = Math.max(room.getCapacity(), 1);

        int maxAdultsByRooms
                = adultsPerRoom * numRooms;

        int maxChildrenByRooms
                = childrenPerRoom * numRooms;

        int maxGuestsByRooms
                = capacityPerRoom * numRooms;

        int totalGuests = numAdults + numChildren;

        /*
         * Kiểm tra giới hạn người lớn.
         */
        if (numAdults > maxAdultsByRooms) {
            guestRoomError
                    = "Số người lớn không được vượt quá "
                    + maxAdultsByRooms
                    + " người đối với "
                    + numRooms
                    + " phòng đã chọn.";

        /*
         * Kiểm tra giới hạn trẻ em.
         */
        } else if (numChildren > maxChildrenByRooms) {
            guestRoomError
                    = "Số trẻ em không được vượt quá "
                    + maxChildrenByRooms
                    + " trẻ đối với "
                    + numRooms
                    + " phòng đã chọn.";

        /*
         * Kiểm tra tổng số người.
         */
        } else if (totalGuests > maxGuestsByRooms) {
            guestRoomError
                    = "Tổng số người lớn và trẻ em "
                    + "không được vượt quá "
                    + maxGuestsByRooms
                    + " người đối với "
                    + numRooms
                    + " phòng đã chọn.";
        }

        /*
         * Mỗi phòng nên có ít nhất một người lớn.
         */
        if (guestRoomError.isEmpty()
                && numAdults < numRooms) {

            guestRoomWarning
                    = "Số người lớn đang nhỏ hơn số phòng đặt. "
                    + "Mỗi phòng nên có ít nhất một người lớn.";
        }

        request.setAttribute("room", room);

        request.setAttribute(
                "checkIn",
                checkIn == null
                        ? ""
                        : checkIn.toString()
        );

        request.setAttribute(
                "checkOut",
                checkOut == null
                        ? ""
                        : checkOut.toString()
        );

        request.setAttribute("numRooms", numRooms);

        // Giữ tên numGuests để tương thích JSP hiện tại
        request.setAttribute("numGuests", numAdults);

        request.setAttribute(
                "numChildren",
                numChildren
        );

        request.setAttribute(
                "availableRooms",
                availableRooms
        );

        request.setAttribute("nights", nights);

        request.setAttribute(
                "hasValidDate",
                hasValidDate
        );

        request.setAttribute("dateError", dateError);

        request.setAttribute(
                "guestRoomWarning",
                guestRoomWarning
        );

        request.setAttribute(
                "guestRoomError",
                guestRoomError
        );

        request.setAttribute(
                "today",
                today.toString()
        );

        /*
         * Giữ maxGuestsByRooms cho JSP hiện tại.
         * Đây là tổng sức chứa theo số phòng.
         */
        request.setAttribute(
                "maxGuestsByRooms",
                maxGuestsByRooms
        );

        request.setAttribute(
                "maxAdultsByRooms",
                maxAdultsByRooms
        );

        request.setAttribute(
                "maxChildrenByRooms",
                maxChildrenByRooms
        );

        forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        doGet(request, response);
    }

    private void setDefaultAttributes(
            HttpServletRequest request,
            LocalDate today) {

        request.setAttribute("checkIn", "");
        request.setAttribute("checkOut", "");

        request.setAttribute("numRooms", 1);

        // Người lớn mặc định
        request.setAttribute("numGuests", 1);

        // Trẻ em mặc định
        request.setAttribute("numChildren", 0);

        request.setAttribute(
                "availableRooms",
                -1
        );

        request.setAttribute("nights", 0L);

        request.setAttribute(
                "hasValidDate",
                false
        );

        request.setAttribute("dateError", "");

        request.setAttribute(
                "guestRoomWarning",
                ""
        );

        request.setAttribute(
                "guestRoomError",
                ""
        );

        request.setAttribute(
                "today",
                today.toString()
        );

        request.setAttribute(
                "maxGuestsByRooms",
                1
        );

        request.setAttribute(
                "maxAdultsByRooms",
                1
        );

        request.setAttribute(
                "maxChildrenByRooms",
                0
        );
    }

    private void forward(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(
                ROOM_DETAIL_PAGE
        ).forward(request, response);
    }

    private String getParam(
            HttpServletRequest request,
            String name) {

        String value = request.getParameter(name);

        return value == null
                || value.trim().isEmpty()
                ? null
                : value.trim();
    }

    private LocalDate parseDate(String value) {
        try {
            return value == null
                    ? null
                    : LocalDate.parse(value);

        } catch (Exception e) {
            return null;
        }
    }

    private Integer parseInt(String value) {
        try {
            return value == null
                    ? null
                    : Integer.parseInt(value);

        } catch (Exception e) {
            return null;
        }
    }

    private int parseIntOrDefault(
            String value,
            int defaultValue) {

        Integer number = parseInt(value);

        return number == null
                ? defaultValue
                : number;
    }
}