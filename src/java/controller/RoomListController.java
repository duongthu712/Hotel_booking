package controller;

import dao.RoomDAO;
import dao.RoomTypeDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.GuestStay;
import model.Room;
import model.RoomType;
import model.StaffAccount;

/**
 * RoomListController.java
 * Display room management page for manager
 *
 * @author LinhLTHE200306
 * @version 2.0
 * @since 2026-06-10
 */
public class RoomListController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        if (staff == null) {
            response.sendRedirect("login");
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

        // Pagination
        int page = 1;
        try {
            String pageParam = request.getParameter("page");
            if (pageParam != null && !pageParam.isEmpty()) {
                page = Integer.parseInt(pageParam);
            }
        } catch (NumberFormatException e) {
            page = 1;
        }
        int recordsPerPage = 10;

        RoomDAO rDao = new RoomDAO();
        RoomTypeDAO rtDao = new RoomTypeDAO();

        try {
            List<RoomType> roomTypeList = rtDao.getAllRoomTypes();

            Map<Integer, String> roomTypeMap = new HashMap<>();
            for (RoomType rt : roomTypeList) {
                roomTypeMap.put(rt.getRoomTypeId(), rt.getTypeName());
            }

            List<Room> roomList = rDao.searchAndFilterRooms(null, roomTypeId, keyword);

            int totalRecords = roomList.size();
            int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);
            if (page < 1) {
                page = 1;
            }
            if (page > totalPages && totalPages > 0) {
                page = totalPages;
            }

            int start = (page - 1) * recordsPerPage;
            int end = Math.min(start + recordsPerPage, totalRecords);
            List<Room> pagedList;

            if (totalRecords > 0) {
                pagedList = roomList.subList(start, end);
            } else {
                pagedList = roomList;
            }

            Room selectedRoom = (Room) session.getAttribute("selectedRoom");
            List<GuestStay> guestList = (List<GuestStay>) session.getAttribute("guestList");

            if (selectedRoom != null) {
                request.setAttribute("selectedRoom", selectedRoom);
                request.setAttribute("guestList", guestList);
                session.removeAttribute("selectedRoom");
                session.removeAttribute("guestList");
            }

            Room editRoom = (Room) session.getAttribute("editRoom");
            if (editRoom != null) {
                request.setAttribute("editRoom", editRoom);
                session.removeAttribute("editRoom");
            }

            request.setAttribute("roomList", pagedList);
            request.setAttribute("roomTypeList", roomTypeList);
            request.setAttribute("roomTypeMap", roomTypeMap);
            request.setAttribute("selectedRoomTypeId", roomTypeId);
            request.setAttribute("keyword", keyword);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);

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