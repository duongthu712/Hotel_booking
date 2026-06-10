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
            response.sendRedirect("view/auth/login.jsp");
            return;
        }

        // Get filter values
        String floorParam = request.getParameter("floor");
        String roomTypeParam = request.getParameter("roomTypeId");
        String keyword = request.getParameter("keyword");

        Integer floor = null;
        Integer roomTypeId = null;

        try {
            if (floorParam != null && !floorParam.isEmpty()) {
                floor = Integer.valueOf(floorParam);
            }
        } catch (NumberFormatException e) {
            floor = null;
        }

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
            if (pageParam != null) {
                page = Integer.parseInt(pageParam);
            }
        } catch (NumberFormatException e) {
            page = 1;
        }
        int recordsPerPage = 10;

        RoomDAO rDao = new RoomDAO();
        RoomTypeDAO rtDao = new RoomTypeDAO();

        try {
            List<Integer> floorList = rDao.getAllFloors();
            List<RoomType> roomTypeList = rtDao.getAllRoomTypes();

            Map<Integer, String> roomTypeMap = new HashMap<>();
            for (RoomType rt : roomTypeList) {
                roomTypeMap.put(rt.getRoomTypeId(), rt.getTypeName());
            }

            List<Room> roomList = rDao.searchAndFilterRooms(floor, roomTypeId, keyword);

            // Pagination logic
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

            // Tính startPage và endPage cho hiển thị 5 trang liền kề
            int maxVisiblePages = 5;
            int startPage, endPage;

            if (totalPages <= maxVisiblePages) {
                startPage = 1;
                endPage = totalPages;
            } else {
                int half = maxVisiblePages / 2;
                if (page <= half + 1) {
                    startPage = 1;
                    endPage = maxVisiblePages;
                } else if (page >= totalPages - half) {
                    startPage = totalPages - maxVisiblePages + 1;
                    endPage = totalPages;
                } else {
                    startPage = page - half;
                    endPage = page + half - (maxVisiblePages % 2 == 0 ? 1 : 0);
                }
            }

            request.setAttribute("roomList", pagedList);
            request.setAttribute("roomTypeList", roomTypeList);
            request.setAttribute("floorList", floorList);
            request.setAttribute("selectedFloor", floor);
            request.setAttribute("selectedRoomTypeId", roomTypeId);
            request.setAttribute("keyword", keyword);
            request.setAttribute("roomTypeMap", roomTypeMap);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("startPage", startPage);
            request.setAttribute("endPage", endPage);

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