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

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        processRequest(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        processRequest(request, response);
    }

    private void processRequest(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String idRaw = firstNonBlank(
                request.getParameter("roomTypeId"),
                request.getParameter("id")
        );

        if (idRaw == null) {
            response.sendRedirect(
                    request.getContextPath() + "/home"
            );
            return;
        }

        try {
            int roomTypeId = Integer.parseInt(idRaw);

            /*
             * Hứng dữ liệu được truyền từ Search Result.
             * Hỗ trợ nhiều tên param để không bắt người làm search phải sửa.
             */
            String checkInRaw = firstNonBlank(
                    request.getParameter("checkIn"),
                    request.getParameter("checkin"),
                    request.getParameter("checkInDate")
            );

            String checkOutRaw = firstNonBlank(
                    request.getParameter("checkOut"),
                    request.getParameter("checkout"),
                    request.getParameter("checkOutDate")
            );

            String roomQuantityRaw = firstNonBlank(
                    request.getParameter("numRooms"),
                    request.getParameter("roomQuantity"),
                    request.getParameter("quantity")
            );

            String guestQuantityRaw = firstNonBlank(
                    request.getParameter("numGuests"),
                    request.getParameter("guestQuantity"),
                    request.getParameter("guests")
            );

            LocalDate today = LocalDate.now();

            LocalDate checkIn = parseDateOrNull(checkInRaw);
            LocalDate checkOut = parseDateOrNull(checkOutRaw);

            int selectedRooms = parseIntOrDefault(
                    roomQuantityRaw,
                    1
            );

            int numGuests = parseIntOrDefault(
                    guestQuantityRaw,
                    1
            );

            selectedRooms = Math.max(selectedRooms, 1);
            numGuests = Math.max(numGuests, 1);

            RoomTypeDAO roomTypeDAO = new RoomTypeDAO();

            RoomType room
                    = roomTypeDAO.getRoomTypeById(roomTypeId);

            if (room == null) {
                request.setAttribute(
                        "error",
                        "Không tìm thấy loại phòng hoặc loại phòng "
                        + "đã ngừng hoạt động."
                );

                setCommonAttributes(
                        request,
                        today,
                        "",
                        "",
                        selectedRooms,
                        numGuests,
                        -1,
                        0L,
                        false,
                        "",
                        "",
                        "",
                        1
                );

                request.getRequestDispatcher(
                        "/view/public/room-detail.jsp"
                ).forward(request, response);

                return;
            }

            boolean hasValidDate = false;
            String dateError = "";
            String guestRoomWarning = "";
            String guestRoomError = "";

            long nights = 0;
            int availableRooms = -1;

            boolean hasAnyDateInput
                    = checkInRaw != null || checkOutRaw != null;

            if (hasAnyDateInput) {
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

                    if (nights < 1) {
                        nights = 1;
                    }

                    if (selectedRooms > availableRooms) {
                        dateError = availableRooms == 0
                                ? "Hạng phòng này đã hết phòng "
                                + "trong thời gian bạn chọn."
                                : "Chỉ còn " + availableRooms
                                + " phòng khả dụng trong thời gian "
                                + "bạn chọn.";
                    }
                }
            }

            int maxGuestsByRooms
                    = room.getCapacity() * selectedRooms;

            if (numGuests > maxGuestsByRooms) {
                numGuests = maxGuestsByRooms;

                guestRoomError
                        = "Số khách đã được điều chỉnh vì vượt quá "
                        + "sức chứa tối đa của số phòng đã chọn.";
            }

            if (numGuests < selectedRooms) {
                guestRoomWarning
                        = "Số khách đang nhỏ hơn số phòng đặt. "
                        + "Nếu bạn đặt hộ người khác thì vẫn có thể "
                        + "tiếp tục.";
            }

            request.setAttribute("room", room);

            setCommonAttributes(
                    request,
                    today,
                    checkIn == null ? "" : checkIn.toString(),
                    checkOut == null ? "" : checkOut.toString(),
                    selectedRooms,
                    numGuests,
                    availableRooms,
                    nights,
                    hasValidDate,
                    dateError,
                    guestRoomWarning,
                    guestRoomError,
                    maxGuestsByRooms
            );

            request.getRequestDispatcher(
                    "/view/public/room-detail.jsp"
            ).forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(
                    request.getContextPath() + "/home"
            );

        } catch (Exception e) {
            e.printStackTrace();

            request.setAttribute(
                    "error",
                    "Có lỗi xảy ra khi tải chi tiết phòng."
            );

            request.setAttribute(
                    "today",
                    LocalDate.now().toString()
            );

            request.getRequestDispatcher(
                    "/view/public/room-detail.jsp"
            ).forward(request, response);
        }
    }

    private void setCommonAttributes(
            HttpServletRequest request,
            LocalDate today,
            String checkIn,
            String checkOut,
            int numRooms,
            int numGuests,
            int availableRooms,
            long nights,
            boolean hasValidDate,
            String dateError,
            String guestRoomWarning,
            String guestRoomError,
            int maxGuestsByRooms) {

        request.setAttribute("checkIn", checkIn);
        request.setAttribute("checkOut", checkOut);
        request.setAttribute("numRooms", numRooms);
        request.setAttribute("numGuests", numGuests);
        request.setAttribute("availableRooms", availableRooms);
        request.setAttribute("nights", nights);
        request.setAttribute("hasValidDate", hasValidDate);
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
        request.setAttribute(
                "maxGuestsByRooms",
                maxGuestsByRooms
        );
    }

    private LocalDate parseDateOrNull(String value) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return null;
            }

            return LocalDate.parse(value.trim());

        } catch (Exception e) {
            return null;
        }
    }

    private int parseIntOrDefault(
            String value,
            int defaultValue) {

        try {
            if (value == null || value.trim().isEmpty()) {
                return defaultValue;
            }

            return Integer.parseInt(value.trim());

        } catch (Exception e) {
            return defaultValue;
        }
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }

        for (String value : values) {
            if (value != null
                    && !value.trim().isEmpty()) {

                return value.trim();
            }
        }

        return null;
    }

    @Override
    public String getServletInfo() {
        return "Public room type detail servlet";
    }
}