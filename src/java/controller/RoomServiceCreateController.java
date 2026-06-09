package controller;

import dao.RoomServiceDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
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
            response.sendRedirect(request.getContextPath() + "/view/auth/login.jsp");
            return;
        }

        String serviceName = request.getParameter("serviceName");
        String description = request.getParameter("description");
        String unitPriceStr = request.getParameter("unitPrice");
        String activeStr = request.getParameter("active");
        

        if (serviceName == null || serviceName.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Tên dịch vụ không được để trống.");
            response.sendRedirect(request.getContextPath() + "/RoomServiceList");
            return;
        }

        if (unitPriceStr == null || unitPriceStr.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Đơn giá không được để trống.");
            response.sendRedirect(request.getContextPath() + "/RoomServiceList");
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
            response.sendRedirect(request.getContextPath() + "/RoomServiceList");
            return;
        }

        boolean isActive = "true".equals(activeStr);
        RoomService newService = new RoomService(0, serviceName, description, unitPrice, isActive);

        try {
            RoomServiceDAO dao = new RoomServiceDAO();
            dao.createRoomService(newService);
            session.setAttribute("successMessage", "Thêm dịch vụ \"" + serviceName.trim() + "\" thành công.");
        } catch (Exception e) {
            session.setAttribute("errorMessage", e.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/RoomServiceList");
    }

    @Override
    public String getServletInfo() {
        return "Room Service Create Controller";
    }

}
