package controller;

import dao.RoomAmenityDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
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
            response.sendRedirect(request.getContextPath() + "login");
            return;
        }

        String amenityName = request.getParameter("amenityName");
        String description = request.getParameter("description");
        String unitPriceStr = request.getParameter("unitPrice");
        String activeStr = request.getParameter("active");
        

        if (amenityName == null || amenityName.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Tên dịch vụ không được để trống.");
            response.sendRedirect(request.getContextPath() + "/RoomAmenityList");
            return;
        }

        if (unitPriceStr == null || unitPriceStr.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Đơn giá không được để trống.");
            response.sendRedirect(request.getContextPath() + "/RoomAmenityList");
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
        RoomAmenity newAmenity = new RoomAmenity(0, amenityName, description, unitPrice, isActive);

        try {
            RoomAmenityDAO dao = new RoomAmenityDAO();
            dao.createRoomAmenity(newAmenity);
            session.setAttribute("successMessage", "Thêm dịch vụ \"" + amenityName.trim() + "\" thành công.");
        } catch (Exception e) {
            session.setAttribute("errorMessage", e.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/RoomAmenityList");
    }

    @Override
    public String getServletInfo() {
        return "Room Amenity Create Controller";
    }

}
