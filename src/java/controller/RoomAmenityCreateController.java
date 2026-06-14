package controller;

import dao.RoomAmenityDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import model.RoomAmenity;
import model.StaffAccount;

/**
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-06-10
 */
public class RoomAmenityCreateController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String amenityName = request.getParameter("amenityName");
        String description = request.getParameter("description");
        String unitPriceStr = request.getParameter("unitPrice");        
        String imageUrl = request.getParameter("imageUrl");
        String activeStr = request.getParameter("active");
       
        String page = request.getParameter("page");
        String keyword = request.getParameter("keyword");

        String errorMsg = dal.InputValidationUtil.validateServiceInput(amenityName, unitPriceStr);

        if (errorMsg != null) {
            try {
                RoomAmenityDAO dao = new RoomAmenityDAO();
                
                List<RoomAmenity> amenityList = dao.getAllRoomAmenities();
                
                request.setAttribute("amenityList", amenityList);
                request.setAttribute("errorMessage", errorMsg);
                
                request.setAttribute("openCreateModal", "true"); 
                
                request.setAttribute("keepAmenityName", amenityName);
                request.setAttribute("keepDescription", description);
                request.setAttribute("keepUnitPrice", unitPriceStr);
                request.setAttribute("keepImageUrl", imageUrl);
                request.setAttribute("keepActive", activeStr);
                
                request.setAttribute("currentPage", page != null ? page : "1");
                request.setAttribute("keyword", keyword);

                request.getRequestDispatcher("/view/manager/room-amenity-management.jsp").forward(request, response);
                return;
            } catch (Exception ex) {
                session.setAttribute("errorMessage", ex.getMessage());
                response.sendRedirect(buildRedirectUrl(request, page, keyword));
                return;
            }
        }

        BigDecimal unitPrice = new BigDecimal(unitPriceStr.trim());
        boolean isActive = "true".equals(activeStr);
        RoomAmenity newAmenity = new RoomAmenity(0, amenityName, description, unitPrice, isActive);

        try {
            RoomAmenityDAO dao = new RoomAmenityDAO();
            dao.createRoomAmenity(newAmenity);
            session.setAttribute("successMessage", "Thêm tiện ích \"" + amenityName.trim() + "\" thành công.");
        } catch (Exception e) {
            session.setAttribute("errorMessage",  e.getMessage());
        }
        
        response.sendRedirect(buildRedirectUrl(request, page, keyword));
    }

    private String buildRedirectUrl(HttpServletRequest request, String page, String keyword) {
        StringBuilder url = new StringBuilder(request.getContextPath() + "/RoomAmenityList");
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
        return "Room Amenity Create Controller";
    }

}
