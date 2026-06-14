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
 * @version 1.0
 * @since 2026-06-09
 */
public class RoomServiceCreateController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "login");
            return;
        }

        String serviceName = request.getParameter("serviceName");
        String description = request.getParameter("description");
        String unitPriceStr = request.getParameter("unitPrice");        
        String imageUrl = request.getParameter("imageUrl");
        String activeStr = request.getParameter("active");
       
        String page = request.getParameter("page");
        String keyword = request.getParameter("keyword");

        String errorMsg = dal.InputValidationUtil.validateServiceInput(serviceName, unitPriceStr);

        if (errorMsg != null) {
            try {
                RoomServiceDAO dao = new RoomServiceDAO();
                
                List<RoomService> serviceList = dao.getAllRoomServices();
                
                request.setAttribute("serviceList", serviceList);
                request.setAttribute("errorMessage", errorMsg);
                
                request.setAttribute("openCreateModal", "true"); 
                
                request.setAttribute("keepServiceName", serviceName);
                request.setAttribute("keepDescription", description);
                request.setAttribute("keepUnitPrice", unitPriceStr);
                request.setAttribute("keepImageUrl", imageUrl);
                request.setAttribute("keepActive", activeStr);
                
                request.setAttribute("currentPage", page != null ? page : "1");
                request.setAttribute("keyword", keyword);

                request.getRequestDispatcher("/view/manager/room-service-management.jsp").forward(request, response);
                return;
            } catch (Exception ex) {
                session.setAttribute("errorMessage", ex.getMessage());
                response.sendRedirect(buildRedirectUrl(request, page, keyword));
                return;
            }
        }

        BigDecimal unitPrice = new BigDecimal(unitPriceStr.trim());
        boolean isActive = "true".equals(activeStr);
        RoomService newService = new RoomService(0, serviceName, description, unitPrice, isActive);

        try {
            RoomServiceDAO dao = new RoomServiceDAO();
            dao.createRoomService(newService);
            session.setAttribute("successMessage", "Thêm dịch vụ \"" + serviceName.trim() + "\" thành công.");
        } catch (Exception e) {
            session.setAttribute("errorMessage",  e.getMessage());
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
        return "Room Service Create Controller";
    }

}
