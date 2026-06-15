package controller;

import dao.HotelInfoDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import model.HotelImage;
import model.HotelInfo;
import model.HotelNews;
import model.StaffAccount;

/**
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-06-14
 */
public class HotelNewsEditController extends HttpServlet {

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

        try {
            int newsId = Integer.parseInt(request.getParameter("newsId"));

            HotelInfoDAO dao = new HotelInfoDAO();
            HotelNews newsToEdit = dao.getNewsById(newsId);

            HotelInfo hotelInfo = dao.getHotelInfoById(hotelId);
            HotelImage banner = dao.getBannerByHotelId(hotelId);
            List<HotelImage> smallImages = dao.getSmallImagesByHotelId(hotelId);
            List<HotelNews> newsList = dao.getAllNewsByHotelId(hotelId);

            request.setAttribute("hotelInfo", hotelInfo);
            request.setAttribute("bannerImage", banner);
            request.setAttribute("smallImages", smallImages);
            request.setAttribute("newsList", newsList);
            request.setAttribute("newsToEdit", newsToEdit);
            request.setAttribute("openEditModal", true);

            request.getRequestDispatcher("/view/manager/hotel-info-management.jsp").forward(request, response);

        } catch (Exception e) {
            session.setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/HotelInfo");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String newsIdStr = request.getParameter("newsId");
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String imageUrl = request.getParameter("imageUrl");
        String activeStr = request.getParameter("active");

        if (title == null || title.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Tiêu đề bài viết không được để trống.");
            response.sendRedirect(request.getContextPath() + "/HotelInfo");
            return;
        }
        if (content == null || content.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Nội dung bài viết không được để trống.");
            response.sendRedirect(request.getContextPath() + "/HotelInfo");
            return;
        }

        int newsId = Integer.parseInt(newsIdStr);
        boolean isActive = "true".equals(activeStr);

        HotelNews news = new HotelNews();
        news.setNewsId(newsId);
        news.setTitle(title != null ? title.trim() : null);
        news.setContent(content != null ? content.trim() : null);
        news.setImageUrl(imageUrl != null && !imageUrl.trim().isEmpty() ? imageUrl.trim() : null);
        news.setActive(isActive);

        try {
            HotelInfoDAO dao = new HotelInfoDAO();
            dao.updateNews(news);
            session.setAttribute("successMessage", "Cập nhật bài viết \"" + title.trim() + "\" thành công.");
        } catch (Exception e) {
            session.setAttribute("errorMessage", e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/HotelInfo");
    }

    @Override
    public String getServletInfo() {
        return "Hotel News Edit Controller";
    }
}
