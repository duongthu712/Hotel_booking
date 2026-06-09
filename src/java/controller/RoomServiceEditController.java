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
public class RoomServiceEditController extends HttpServlet {

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
            int serviceId = Integer.parseInt(request.getParameter("serviceId"));

            RoomServiceDAO dao = new RoomServiceDAO();
            RoomService service = dao.getRoomServicesById(serviceId);
            List<RoomService> serviceList = dao.getAllRoomServices();

            request.setAttribute("serviceList", serviceList);
            request.setAttribute("serviceToEdit", service);
            request.setAttribute("page", request.getParameter("page"));
            request.setAttribute("keyword", request.getParameter("keyword"));
            request.getRequestDispatcher("/view/manager/room-service-management.jsp").forward(request, response);
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
            response.sendRedirect("view/auth/login.jsp");
            return;
        }

        String serviceIdStr = request.getParameter("serviceId");
        String serviceName = request.getParameter("serviceName");
        String description = request.getParameter("description");
        String unitPriceStr = request.getParameter("unitPrice");
        String activeStr = request.getParameter("active");
        String page = request.getParameter("page");
        String keyword = request.getParameter("keyword");

        if (serviceName == null || serviceName.trim().isEmpty()) {
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
            response.sendRedirect(request.getContextPath() + "/RoomServiceList");
            return;
        }

        boolean isActive = "true".equals(activeStr);
        RoomService updatedService = new RoomService(
                Integer.parseInt(serviceIdStr),
                serviceName.trim(),
                description.trim(),
                unitPrice,
                isActive
        );

        try {
            RoomServiceDAO dao = new RoomServiceDAO();
            dao.updateRoomService(updatedService);
            session.setAttribute("successMessage", "Cập nhật dịch vụ \"" + serviceName.trim() + "\" thành công.");
        } catch (Exception e) {
            session.setAttribute("errorMessage", e.getMessage());
        }

        response.sendRedirect(buildRedirectUrl(request, page, keyword));
    }

    private String buildRedirectUrl(HttpServletRequest request, String page, String keyword) {
        StringBuilder url = new StringBuilder(request.getContextPath() + "/RoomServiceList");
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
        return "Room Service Edit Controller";
    }

}
