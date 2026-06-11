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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        String idRaw = request.getParameter("roomTypeId");

        if (idRaw == null || idRaw.trim().isEmpty()) {
            idRaw = request.getParameter("id");
        }

        if (idRaw == null || idRaw.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        try {
            int roomTypeId = Integer.parseInt(idRaw);

            String checkInRaw = request.getParameter("checkIn");
            String checkOutRaw = request.getParameter("checkOut");

            LocalDate today = LocalDate.now();
            LocalDate checkIn = parseDateOrNull(checkInRaw);
            LocalDate checkOut = parseDateOrNull(checkOutRaw);

            int selectedRooms = parseIntOrDefault(request.getParameter("numRooms"), 1);
            int numGuests = parseIntOrDefault(request.getParameter("numGuests"), 1);

            if (selectedRooms < 1) {
                selectedRooms = 1;
            }

            if (numGuests < 1) {
                numGuests = 1;
            }

            boolean hasValidDate = false;
            String dateError = "";
            String guestRoomWarning = "";
            String guestRoomError = "";

            long nights = 0;
            int availableRooms = -1;

            RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
            RoomType room = roomTypeDAO.getRoomTypeById(roomTypeId);

            if (room == null) {
                request.setAttribute("error", "Không tìm thấy loại phòng hoặc loại phòng đã ngừng hoạt động.");
                request.getRequestDispatcher("/view/public/room-detail.jsp").forward(request, response);
                return;
            }

            int capacityPerRoom = room.getCapacity();
            int maxGuestsByRooms = capacityPerRoom * selectedRooms;

            boolean userSubmittedDates = (checkInRaw != null && !checkInRaw.trim().isEmpty())
                    || (checkOutRaw != null && !checkOutRaw.trim().isEmpty());

            if (userSubmittedDates) {
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

                    if (availableRooms < 0) {
                        availableRooms = 0;
                    }

                    if (selectedRooms > availableRooms && availableRooms > 0) {
                        selectedRooms = availableRooms;
                    }

                    nights = ChronoUnit.DAYS.between(checkIn, checkOut);
                    if (nights < 1) {
                        nights = 1;
                    }
                }
            }

            maxGuestsByRooms = capacityPerRoom * selectedRooms;

            if (numGuests > maxGuestsByRooms) {
                numGuests = maxGuestsByRooms;
                guestRoomError = "Số khách đã được điều chỉnh vì vượt quá sức chứa tối đa của số phòng đã chọn.";
            }

            if (numGuests < selectedRooms) {
                guestRoomWarning = "Số khách đang nhỏ hơn số phòng đặt. Nếu bạn đặt hộ người khác thì vẫn có thể tiếp tục.";
            }

            request.setAttribute("room", room);
            request.setAttribute("checkIn", checkIn == null ? "" : checkIn.toString());
            request.setAttribute("checkOut", checkOut == null ? "" : checkOut.toString());
            request.setAttribute("numRooms", selectedRooms);
            request.setAttribute("numGuests", numGuests);
            request.setAttribute("availableRooms", availableRooms);
            request.setAttribute("nights", nights);
            request.setAttribute("hasValidDate", hasValidDate);
            request.setAttribute("dateError", dateError);
            request.setAttribute("guestRoomWarning", guestRoomWarning);
            request.setAttribute("guestRoomError", guestRoomError);
            request.setAttribute("today", today.toString());
            request.setAttribute("maxGuestsByRooms", maxGuestsByRooms);

            request.getRequestDispatcher("/view/public/room-detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/home");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra khi tải chi tiết phòng.");
            request.getRequestDispatcher("/view/public/room-detail.jsp").forward(request, response);
        }
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

    private int parseIntOrDefault(String value, int defaultValue) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return defaultValue;
            }
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
