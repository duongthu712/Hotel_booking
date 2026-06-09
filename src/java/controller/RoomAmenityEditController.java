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
            response.sendRedirect("view/auth/login.jsp");
            return;
        }

        try {
            int amenityId = Integer.parseInt(request.getParameter("amenityId"));

            RoomAmenityDAO dao = new RoomAmenityDAO();
            RoomAmenity amenity = dao.getRoomAmenityById(amenityId);
            List<RoomAmenity> amenityList = dao.getAllRoomAmenities();

            request.setAttribute("amenityList", amenityList);
            request.setAttribute("amenityToEdit", amenity);
            request.setAttribute("page", request.getParameter("page"));
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
            response.sendRedirect("view/auth/login.jsp");
            return;
        }

        String amenityIdStr = request.getParameter("amenityId");
        String amenityName = request.getParameter("amenityName");
        String description = request.getParameter("description");
        String unitPriceStr = request.getParameter("unitPrice");
        String activeStr = request.getParameter("active");
        String page = request.getParameter("page");
        String keyword = request.getParameter("keyword");

        if (amenityName == null || amenityName.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Tên dịch vụ không được để trống.");
            response.sendRedirect(buildRedirectUrl(request, page, keyword));
            return;
        }

        BigDecimal unitPrice;
        try {
            unitPrice = new BigDecimal(unitPriceStr.trim());
            if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Đơn giá không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/RoomAmenityList");
            return;
        }

        boolean isActive = "true".equals(activeStr);
        RoomAmenity updatedAmenity = new RoomAmenity(
                Integer.parseInt(amenityIdStr),
                amenityName.trim(),
                description.trim(),
                unitPrice,
                isActive
        );

        try {
            RoomAmenityDAO dao = new RoomAmenityDAO();
            dao.updateRoomAmenity(updatedAmenity);
            session.setAttribute("successMessage", "Cập nhật dịch vụ \"" + amenityName.trim() + "\" thành công.");
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
