package controller;

import dao.HotelInfoDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.HotelNews;
import model.StaffAccount;

/**
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-06-14
 */
public class HotelNewsCreateController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int hotelId = 1;
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String imageUrl = request.getParameter("imageUrl");

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

        HotelNews news = new HotelNews();
        news.setHotelId(hotelId);
        news.setTitle(title.trim());
        news.setContent(content.trim());
        news.setImageUrl(imageUrl.trim());
        news.setActive(true);
        news.setCreatedBy(staff.getStaffId());

        try {
            HotelInfoDAO dao = new HotelInfoDAO();
            dao.createNews(news);
            session.setAttribute("successMessage", "Thêm bài viết \"" + title.trim() + "\" thành công.");
        } catch (Exception e) {
            session.setAttribute("errorMessage", e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/HotelInfo");
    }

    @Override
    public String getServletInfo() {
        return "Hotel News Create Controller";
    }
}