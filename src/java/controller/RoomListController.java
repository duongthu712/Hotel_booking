package controller;

import dao.RoomDAO;
import dao.RoomTypeDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import model.GuestStay;
import model.Room;
import model.RoomType;
import model.StaffAccount;

/**
 * @author LinhLTHE200306
 * @version 3.0
 * @since 2026-06-28
 */
public class RoomListController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String roomTypeParam = request.getParameter("roomTypeId");
        String keyword = request.getParameter("keyword");

        Integer roomTypeId = null;
        try {
            if (roomTypeParam != null && !roomTypeParam.isEmpty()) {
                roomTypeId = Integer.valueOf(roomTypeParam);
            }
        } catch (NumberFormatException e) {
            roomTypeId = null;
        }

        RoomDAO rDao = new RoomDAO();
        RoomTypeDAO rtDao = new RoomTypeDAO();

        try {
            List<RoomType> roomTypeList = rtDao.getAllRoomTypes();

            Map<Integer, String> roomTypeMap = new HashMap<>();
            for (RoomType rt : roomTypeList) {
                roomTypeMap.put(rt.getRoomTypeId(), rt.getTypeName());
            }

            List<Room> allRooms = rDao.searchAndFilterRooms(null, roomTypeId, keyword);

            Map<Integer, List<Room>> floorMap = new TreeMap<>();
            for (Room room : allRooms) {
                int floor = room.getFloor();
                floorMap.computeIfAbsent(floor, k -> new ArrayList<>()).add(room);
            }

            for (List<Room> rooms : floorMap.values()) {
                rooms.sort(Comparator.comparingInt(Room::getRoomNumber));
            }

            // Detail modal
            Room selectedRoom = (Room) session.getAttribute("selectedRoom");
            List<GuestStay> guestList = (List<GuestStay>) session.getAttribute("guestList");

            if (selectedRoom != null) {
                request.setAttribute("selectedRoom", selectedRoom);
                request.setAttribute("guestList", guestList);
                session.removeAttribute("selectedRoom");
                session.removeAttribute("guestList");
            }

            // Edit modal
            Room editRoom = (Room) session.getAttribute("editRoom");
            if (editRoom != null) {
                request.setAttribute("editRoom", editRoom);
                session.removeAttribute("editRoom");
            }

            // Create modal
            Boolean openCreateModal = (Boolean) session.getAttribute("openCreateModal");
            if (openCreateModal != null) {
                request.setAttribute("openCreateModal", openCreateModal);
                session.removeAttribute("openCreateModal");
            }

            // Giữ giá trị form khi lỗi
            request.setAttribute("keepRoomNumber", session.getAttribute("keepRoomNumber"));
            request.setAttribute("keepFloor", session.getAttribute("keepFloor"));
            request.setAttribute("keepRoomTypeId", session.getAttribute("keepRoomTypeId"));
            session.removeAttribute("keepRoomNumber");
            session.removeAttribute("keepFloor");
            session.removeAttribute("keepRoomTypeId");

            request.setAttribute("floorMap", floorMap);
            request.setAttribute("roomTypeList", roomTypeList);
            request.setAttribute("roomTypeMap", roomTypeMap);
            request.setAttribute("selectedRoomTypeId", roomTypeId);
            request.setAttribute("keyword", keyword);

            request.getRequestDispatcher("/view/manager/room-management.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/view/manager/room-management.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Room Management Controller";
    }
}