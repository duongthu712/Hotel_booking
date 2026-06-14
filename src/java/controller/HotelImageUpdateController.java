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
 * HotelImageUpdateController.java
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-06-14
 */
public class HotelImageUpdateController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String imageIdStr = request.getParameter("imageId");
        String imageUrl = request.getParameter("imageUrl");

        if (imageIdStr == null || imageIdStr.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Không tìm thấy ảnh cần cập nhật.");
            response.sendRedirect(request.getContextPath() + "/HotelInfo");
            return;
        }

        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Link ảnh không được để trống.");
            response.sendRedirect(request.getContextPath() + "/HotelInfo");
            return;
        }

        try {
            int imageId = Integer.parseInt(imageIdStr.trim());
            HotelInfoDAO dao = new HotelInfoDAO();
            dao.updateImage(imageId, imageUrl.trim(), null);
            session.setAttribute("successMessage", "Cập nhật ảnh thành công.");
        } catch (Exception e) {
            session.setAttribute("errorMessage", e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/HotelInfo");
    }

    @Override
    public String getServletInfo() {
        return "Hotel Image Update Controller";
    }
}