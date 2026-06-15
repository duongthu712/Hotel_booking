package controller;

import dao.HotelServiceDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import model.HotelService;
import model.StaffAccount;

/**
 * @author LinhLTHE200306
 * @version 1.1
 * @since 2026-06-09
 */
public class HotelServiceListController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/login");
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
        HotelServiceDAO rDao = new HotelServiceDAO();

        try {
            List<HotelService> hotelServiceList;
            if (keyword != null && !keyword.trim().isEmpty()) {
                hotelServiceList = rDao.searchHotelServicesByName(keyword.trim());
            } else {
                hotelServiceList = rDao.getAllHotelServices();
            }

            if (hotelServiceList == null) {
                hotelServiceList = new ArrayList<>();
            }

            int totalRecords = hotelServiceList.size();
            int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);
            if (page < 1) {
                page = 1;
            }
            if (page > totalPages && totalPages > 0) {
                page = totalPages;
            }

            int start = (page - 1) * recordsPerPage;
            int end = Math.min(start + recordsPerPage, totalRecords);
            List<HotelService> pagedList;

            if (totalRecords > 0) {
                pagedList = hotelServiceList.subList(start, end);
            } else {
                pagedList = hotelServiceList;
            }

            request.setAttribute("serviceList", pagedList);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("keyword", keyword);
            
            request.getRequestDispatcher("/view/manager/hotel-service-management.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
            request.setAttribute("serviceList", new ArrayList<>());
            request.setAttribute("currentPage", 1);
            request.setAttribute("totalPages", 1);
            request.setAttribute("keyword", keyword);
            
            request.getRequestDispatcher("/view/manager/hotel-service-management.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Hotel Service List Controller";
    }
}