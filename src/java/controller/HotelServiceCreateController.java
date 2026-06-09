package controller;

import dao.HotelServiceDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import model.HotelService;
import model.StaffAccount;

/**
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-06-09
 */
public class HotelServiceCreateController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/view/auth/login.jsp");
            return;
        }

        String serviceName = request.getParameter("serviceName");
        String description = request.getParameter("description");
        String unitPriceStr = request.getParameter("unitPrice");
        String imageUrl = request.getParameter("imageUrl");
        String activeStr = request.getParameter("active");
        

        if (serviceName == null || serviceName.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Tên dịch vụ không được để trống.");
            response.sendRedirect(request.getContextPath() + "HotelServiceList");
            return;
        }

        if (unitPriceStr == null || unitPriceStr.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Đơn giá không được để trống.");
            response.sendRedirect(request.getContextPath() + "HotelServiceList");
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
            response.sendRedirect(request.getContextPath() + "HotelServiceList");
            return;
        }

        boolean isActive = "true".equals(activeStr);
        HotelService newService = new HotelService(0, serviceName, description, unitPrice, imageUrl, isActive);

        try {
            HotelServiceDAO dao = new HotelServiceDAO();
            dao.createHotelService(newService);
            session.setAttribute("successMessage", "Thêm dịch vụ \"" + serviceName.trim() + "\" thành công.");
        } catch (Exception e) {
            session.setAttribute("errorMessage", e.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "HotelServiceList");
    }

    @Override
    public String getServletInfo() {
        return "Hotel Service Create Controller";
    }

}
