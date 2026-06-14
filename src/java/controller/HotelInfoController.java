package controller;

import dao.HotelInfoDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import model.HotelImage;
import model.HotelInfo;
import model.HotelNews;
import model.StaffAccount;

/**
 * HotelInfoController.java
 * Main controller for Hotel Information Management
 * Loads hotel info, images, and news list for the management page
 *
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-06-14
 */
public class HotelInfoController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int hotelId = 1;
        String keyword = request.getParameter("keyword");
        String status = request.getParameter("status");

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

        try {
            HotelInfoDAO dao = new HotelInfoDAO();

            HotelInfo hotelInfo = dao.getHotelInfoById(hotelId);
            request.setAttribute("hotelInfo", hotelInfo);

            HotelImage banner = dao.getBannerByHotelId(hotelId);
            request.setAttribute("bannerImage", banner);

            List<HotelImage> smallImages = dao.getSmallImagesByHotelId(hotelId);
            request.setAttribute("smallImages", smallImages);

            List<HotelNews> allNews;
            if ((keyword != null && !keyword.trim().isEmpty()) || 
                (status != null && !status.equals("all") && !status.isEmpty())) {
                allNews = dao.searchNewsByTitle(hotelId, 
                    keyword != null ? keyword.trim() : null, 
                    status != null ? status : "all");
            } else {
                allNews = dao.getAllNewsByHotelId(hotelId);
            }

            int totalRecords = allNews.size();
            int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);
            if (page < 1) page = 1;
            if (page > totalPages && totalPages > 0) page = totalPages;

            int start = (page - 1) * recordsPerPage;
            int end = Math.min(start + recordsPerPage, totalRecords);
            List<HotelNews> pagedList;

            if (totalRecords > 0) {
                pagedList = allNews.subList(start, end);
            } else {
                pagedList = new ArrayList<>();
            }

            request.setAttribute("newsList", pagedList);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages > 0 ? totalPages : 1);
            request.setAttribute("keyword", keyword);
            request.setAttribute("status", status);

            request.getRequestDispatcher("/view/manager/hotel-info-management.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/view/manager/hotel-info-management.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Hotel Information Management Controller";
    }
}