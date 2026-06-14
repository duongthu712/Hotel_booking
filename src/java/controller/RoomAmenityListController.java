package controller;

import dao.RoomAmenityDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import model.RoomAmenity;
import model.StaffAccount;

/**
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-06-10
 */
public class RoomAmenityListController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        if (staff == null) {
            response.sendRedirect("login");
            return;
        }

        String keyword = request.getParameter("keyword");
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
        RoomAmenityDAO rDao = new RoomAmenityDAO();

        try {
            List<RoomAmenity> roomAmenityList;
            if (keyword != null && !keyword.trim().isEmpty()) {
                roomAmenityList = rDao.searchRoomAmenitiesByName(keyword.trim());
            } else {
                roomAmenityList = rDao.getAllRoomAmenities();
            }

            if (roomAmenityList == null) {
                roomAmenityList = new ArrayList<>();
            }

            int totalRecords = roomAmenityList.size();
            int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);
            if (page < 1) {
                page = 1;
            }
            if (page > totalPages && totalPages > 0) {
                page = totalPages;
            }

            int start = (page - 1) * recordsPerPage;
            int end = Math.min(start + recordsPerPage, totalRecords);
            List<RoomAmenity> pagedList;

            if (totalRecords > 0) {
                pagedList = roomAmenityList.subList(start, end);
            } else {
                pagedList = roomAmenityList;
            }

            request.setAttribute("amenityList", pagedList);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("keyword", keyword);
            
            request.getRequestDispatcher("/view/manager/room-amenity-management.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
            request.setAttribute("amenityList", new ArrayList<>());
            request.setAttribute("currentPage", 1);
            request.setAttribute("totalPages", 1);
            request.setAttribute("keyword", keyword);
            
            request.getRequestDispatcher("/view/manager/room-amenity-management.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Room Amenity List Controller";
    }
}
