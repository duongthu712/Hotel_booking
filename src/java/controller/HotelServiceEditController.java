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
 * @version 1.1
 * @since 2026-06-09
 */
public class HotelServiceEditController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");

        if (staff == null) {
            response.sendRedirect("login");
            return;
        }

        try {
            int serviceId = Integer.parseInt(request.getParameter("serviceId"));

            HotelServiceDAO dao = new HotelServiceDAO();
            HotelService service = dao.getHotelServicesById(serviceId);
            
            List<HotelService> serviceList = dao.getAllHotelServices();

            request.setAttribute("serviceList", serviceList);
            request.setAttribute("serviceToEdit", service);
            request.setAttribute("currentPage", request.getParameter("page"));
            request.setAttribute("keyword", request.getParameter("keyword"));
            request.getRequestDispatcher("/view/manager/hotel-service-management.jsp").forward(request, response);
        } catch (Exception e) {
            session.setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/HotelServiceList");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        if (staff == null) {
            response.sendRedirect("login");
            return;
        }

        String serviceIdStr = request.getParameter("serviceId");
        String serviceName = request.getParameter("serviceName");
        String description = request.getParameter("description");
        String unitPriceStr = request.getParameter("unitPrice");
        String activeStr = request.getParameter("active");
        String imageUrl = request.getParameter("imageUrl");
        
        String page = request.getParameter("page");
        String keyword = request.getParameter("keyword");

        String errorMsg = dal.InputValidationUtil.validateServiceInput(serviceName, unitPriceStr);

        if (errorMsg != null) {
            try {
                HotelServiceDAO dao = new HotelServiceDAO();
                int serviceId = Integer.parseInt(serviceIdStr);
                
                BigDecimal tempPrice = BigDecimal.ZERO;
                try {
                    tempPrice = new BigDecimal(unitPriceStr.trim());
                } catch (Exception e) {}
                
                boolean isActive = "true".equals(activeStr);
                HotelService serviceToEditTemp = new HotelService(serviceId, serviceName, description, tempPrice, imageUrl, isActive);
                
                List<HotelService> serviceList = dao.getAllHotelServices();
                
                request.setAttribute("serviceList", serviceList);
                request.setAttribute("serviceToEdit", serviceToEditTemp);
                request.setAttribute("errorMessage", errorMsg);
                request.setAttribute("currentPage", page);
                request.setAttribute("keyword", keyword);
                
                request.getRequestDispatcher("/view/manager/hotel-service-management.jsp").forward(request, response);
                return;
            } catch (Exception ex) {
                session.setAttribute("errorMessage", ex.getMessage());
                response.sendRedirect(buildRedirectUrl(request, page, keyword));
                return;
            }
        }

        int serviceId = Integer.parseInt(serviceIdStr);
        BigDecimal unitPrice = new BigDecimal(unitPriceStr.trim());
        boolean isActive = "true".equals(activeStr);
        
        HotelService updatedService = new HotelService(serviceId, serviceName, description, unitPrice, imageUrl, isActive);

        try {
            HotelServiceDAO dao = new HotelServiceDAO();
            dao.updateHotelService(updatedService);
            session.setAttribute("successMessage", "Cập nhật dịch vụ \"" + serviceName.trim() + "\" thành công.");
        } catch (Exception e) {
            session.setAttribute("errorMessage", e.getMessage());
        }

        response.sendRedirect(buildRedirectUrl(request, page, keyword));
    }

    private String buildRedirectUrl(HttpServletRequest request, String page, String keyword) {
        StringBuilder url = new StringBuilder(request.getContextPath() + "/HotelServiceList");
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
        return "Hotel Service Edit Controller";
    }
}