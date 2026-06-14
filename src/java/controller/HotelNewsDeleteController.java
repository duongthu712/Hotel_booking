package controller;

import dao.HotelInfoDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.StaffAccount;

/**
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-06-14
 */
public class HotelNewsDeleteController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int newsId = Integer.parseInt(request.getParameter("newsId"));

        try {
            HotelInfoDAO dao = new HotelInfoDAO();
            dao.deleteNews(newsId);
            session.setAttribute("successMessage", "Xóa bài viết thành công.");
        } catch (Exception e) {
            session.setAttribute("errorMessage", e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/HotelInfo");
    }

    @Override
    public String getServletInfo() {
        return "Hotel News Delete Controller";
    }
}