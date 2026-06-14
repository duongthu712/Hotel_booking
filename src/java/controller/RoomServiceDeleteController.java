package controller;

import dao.RoomServiceDAO;
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
 * @since 2026-06-09
 */
public class RoomServiceDeleteController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int serviceId = Integer.parseInt(request.getParameter("serviceId"));
        String page = request.getParameter("page");
        String keyword = request.getParameter("keyword");

        try {
            RoomServiceDAO dao = new RoomServiceDAO();
            dao.delete(serviceId);
            session.setAttribute("successMessage", "Xoá dịch vụ thành công.");
        } catch (Exception e) {
            session.setAttribute("errorMessage", e.getMessage());
        }

        response.sendRedirect(buildRedirectUrl(request, page, keyword));
    }

    private String buildRedirectUrl(HttpServletRequest request, String page, String keyword) {
        StringBuilder url = new StringBuilder(request.getContextPath() + "/RoomServiceList");
        url.append("?page=").append(page != null ? page : "1");
        if (keyword != null && !keyword.trim().isEmpty()) {
            try {
                url.append("&keyword=").append(java.net.URLEncoder.encode(keyword.trim(), "UTF-8"));
            } catch (java.io.UnsupportedEncodingException e) {
                url.append("&keyword=").append(keyword.trim());
            }
        }
        return url.toString();
    }

    @Override
    public String getServletInfo() {
        return "Room Service Delete Controller";
    }

}
