package controller;

import dao.HotelServiceDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import model.HotelService;
import model.StaffAccount;

/**
 * @author LinhLTHE200306
 * @version 1.2
 * @since 2026-06-09
 */
public class HotelServiceCreateController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String serviceName = request.getParameter("serviceName").trim();
        String description = request.getParameter("description").trim();
        String unitPriceStr = request.getParameter("unitPrice");
        String imageUrl = request.getParameter("imageUrl");
        String activeStr = request.getParameter("active");

        String page = request.getParameter("page");
        String keyword = request.getParameter("keyword");

        String errorMsg = dal.InputValidationUtil.validateServiceInput(serviceName, unitPriceStr);

        if (errorMsg != null) {
            session.setAttribute("errorMessage", errorMsg);
            session.setAttribute("openCreateModal", "true");

            session.setAttribute("keepServiceName", serviceName);
            session.setAttribute("keepDescription", description);
            session.setAttribute("keepUnitPrice", unitPriceStr);
            session.setAttribute("keepImageUrl", imageUrl);
            session.setAttribute("keepActive", activeStr);

            response.sendRedirect(buildRedirectUrl(request, page, keyword));
            return;
        }

        try {
            BigDecimal unitPrice = new BigDecimal(unitPriceStr.trim());
            boolean isActive = "true".equals(activeStr);
            HotelService newService = new HotelService(0, serviceName, description, unitPrice, imageUrl, isActive);

            HotelServiceDAO dao = new HotelServiceDAO();
            dao.createHotelService(newService);
            
            session.setAttribute("successMessage", "Thêm dịch vụ \"" + serviceName.trim() + "\" thành công.");
            
        } catch (Exception e) {
            session.setAttribute("errorMessage", e.getMessage());
            session.setAttribute("openCreateModal", "true");

            session.setAttribute("keepServiceName", serviceName);
            session.setAttribute("keepDescription", description);
            session.setAttribute("keepUnitPrice", unitPriceStr);
            session.setAttribute("keepImageUrl", imageUrl);
            session.setAttribute("keepActive", activeStr);
        }

        response.sendRedirect(buildRedirectUrl(request, page, keyword));
    }

    private String buildRedirectUrl(HttpServletRequest request, String page, String keyword) {
        StringBuilder url = new StringBuilder(request.getContextPath() + "/HotelServiceList");
        url.append("?page=").append(page != null && !page.isEmpty() ? page : "1");
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
        return "Hotel Service Create Controller";
    }
}