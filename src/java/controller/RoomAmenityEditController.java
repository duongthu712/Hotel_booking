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
public class RoomAmenityEditController extends HttpServlet {

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
            int amenityId = Integer.parseInt(request.getParameter("amenityId"));

            RoomAmenityDAO dao = new RoomAmenityDAO();
            RoomAmenity amenity = dao.getRoomAmenityById(amenityId);
            
            List<RoomAmenity> amenityList = dao.getAllRoomAmenities();

            request.setAttribute("amenityList", amenityList);
            request.setAttribute("amenityToEdit", amenity);
            request.setAttribute("currentPage", request.getParameter("page"));
            request.setAttribute("keyword", request.getParameter("keyword"));
            request.getRequestDispatcher("/view/manager/room-amenity-management.jsp").forward(request, response);
        } catch (Exception e) {
            session.setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/RoomAmenityList");
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

        String amenityIdStr = request.getParameter("amenityId");
        String amenityName = request.getParameter("amenityName");
        String description = request.getParameter("description");
        String unitPriceStr = request.getParameter("unitPrice");
        String activeStr = request.getParameter("active");
        String imageUrl = request.getParameter("imageUrl");
        
        String page = request.getParameter("page");
        String keyword = request.getParameter("keyword");

        String errorMsg = dal.InputValidationUtil.validateServiceInput(amenityName, unitPriceStr);

        if (errorMsg != null) {
            try {
                RoomAmenityDAO dao = new RoomAmenityDAO();
                int amenityId = Integer.parseInt(amenityIdStr);
                
                BigDecimal tempPrice = BigDecimal.ZERO;
                try {
                    tempPrice = new BigDecimal(unitPriceStr.trim());
                } catch (Exception e) {}
                
                boolean isActive = "true".equals(activeStr);
                RoomAmenity amenityToEditTemp = new RoomAmenity(amenityId, amenityName, description, tempPrice, isActive);
                
                List<RoomAmenity> amenityList = dao.getAllRoomAmenities();
                
                request.setAttribute("amenityList", amenityList);
                request.setAttribute("amenityToEdit", amenityToEditTemp);
                request.setAttribute("errorMessage", errorMsg);
                request.setAttribute("currentPage", page);
                request.setAttribute("keyword", keyword);
                
                request.getRequestDispatcher("/view/manager/room-amenity-management.jsp").forward(request, response);
                return;
            } catch (Exception ex) {
                session.setAttribute("errorMessage", ex.getMessage());
                response.sendRedirect(buildRedirectUrl(request, page, keyword));
                return;
            }
        }

        int amenityId = Integer.parseInt(amenityIdStr);
        BigDecimal unitPrice = new BigDecimal(unitPriceStr.trim());
        boolean isActive = "true".equals(activeStr);
        
        RoomAmenity updatedAmenity = new RoomAmenity(amenityId, amenityName, description, unitPrice, isActive);

        try {
            RoomAmenityDAO dao = new RoomAmenityDAO();
            dao.updateRoomAmenity(updatedAmenity);
            session.setAttribute("successMessage", "Cập nhật tiện nghi \"" + amenityName.trim() + "\" thành công.");
        } catch (Exception e) {
            session.setAttribute("errorMessage", e.getMessage());
        }

        response.sendRedirect(buildRedirectUrl(request, page, keyword));
    }

    private String buildRedirectUrl(HttpServletRequest request, String page, String keyword) {
        StringBuilder url = new StringBuilder(request.getContextPath() + "/RoomAmenityList");
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
        return "Room Amenity Edit Controller";
    }
}