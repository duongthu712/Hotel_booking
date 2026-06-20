package controller;

import dao.RoomServiceDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import model.RoomService;
import model.StaffAccount;

/**
 * @author LinhLTHE200306
 * @version 1.2
 * @since 2026-06-09
 */
public class RoomServiceEditController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");

        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int serviceId = Integer.parseInt(request.getParameter("serviceId"));

            String page = request.getParameter("page");
            String keyword = request.getParameter("keyword");

            RoomServiceDAO dao = new RoomServiceDAO();
            RoomService service = dao.getRoomServicesById(serviceId);

            session.setAttribute("serviceToEdit", service);

            response.sendRedirect(buildRedirectUrl(request, page, keyword));

        } catch (Exception e) {
            session.setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/RoomServiceList");
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

        String serviceIdStr = request.getParameter("serviceId");
        String serviceName = request.getParameter("serviceName").trim();
        String description = request.getParameter("description").trim();
        String unitPriceStr = request.getParameter("unitPrice");
        String activeStr = request.getParameter("active");
        
        String page = request.getParameter("page");
        String keyword = request.getParameter("keyword");

        String errorMsg = dal.InputValidationUtil.validateServiceInput(serviceName, unitPriceStr);

        if (errorMsg != null) {
            session.setAttribute("errorMessage", errorMsg);
            
            session.setAttribute("openEditModal", "true");
            
            int serviceId = Integer.parseInt(serviceIdStr);
            BigDecimal tempPrice = BigDecimal.ZERO;
            try {
                tempPrice = new BigDecimal(unitPriceStr.trim());
            } catch (Exception e) {}
            boolean isActive = "true".equals(activeStr);
            RoomService serviceToEditTemp = new RoomService(serviceId, serviceName, description, tempPrice, isActive);
            
            session.setAttribute("serviceToEdit", serviceToEditTemp);

            response.sendRedirect(buildRedirectUrl(request, page, keyword));
            return;
        }

        try {
            int serviceId = Integer.parseInt(serviceIdStr);
            BigDecimal unitPrice = new BigDecimal(unitPriceStr.trim());
            boolean isActive = "true".equals(activeStr);
            
            RoomService updatedService = new RoomService(serviceId, serviceName, description, unitPrice, isActive);

            RoomServiceDAO dao = new RoomServiceDAO();
            dao.updateRoomService(updatedService);
            session.setAttribute("successMessage", "Cập nhật dịch vụ \"" + serviceName.trim() + "\" thành công.");
        } catch (Exception e) {
            session.setAttribute("errorMessage", e.getMessage());
            session.setAttribute("openEditModal", "true");
            
            int serviceId = Integer.parseInt(serviceIdStr);
            BigDecimal tempPrice = BigDecimal.ZERO;
            try { tempPrice = new BigDecimal(unitPriceStr.trim()); } catch (Exception ex) {}
            RoomService serviceToEditTemp = new RoomService(serviceId, serviceName, description, tempPrice, "true".equals(activeStr));
            session.setAttribute("serviceToEdit", serviceToEditTemp);
        }

        response.sendRedirect(buildRedirectUrl(request, page, keyword));
    }

    private String buildRedirectUrl(HttpServletRequest request, String page, String keyword) {
        StringBuilder url = new StringBuilder(request.getContextPath() + "/RoomServiceList");
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
        return "Room Service Edit Controller";
    }
}