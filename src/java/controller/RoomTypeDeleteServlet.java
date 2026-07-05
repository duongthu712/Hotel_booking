package controller;

import dao.RoomTypeDAO;
import dto.DeactiveValidationResult;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Minh Thu
 */
@WebServlet(name = "RoomTypeDeleteServlet", urlPatterns = {"/roomtypedelete"})
public class RoomTypeDeleteServlet extends HttpServlet {

    private final RoomTypeDAO roomTypeDAO = new RoomTypeDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // 1. Lấy và kiểm tra ID
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/roomtypelist?status=error");
                return;
            }
            int roomTypeId = Integer.parseInt(idStr);

            // 2. Kiểm tra xung đột dữ liệu (số khách đang ở, số đơn tương lai)
            DeactiveValidationResult result = roomTypeDAO.getDeactiveValidationResult(roomTypeId);

            // 3. Nếu hạng phòng bị chặn
            if (result.isBlocked()) {
                // Redirect về trang danh sách kèm các tham số để JS bắt được
                String redirectUrl = request.getContextPath() + "/roomtypelist?status=conflict"
                        + "&id=" + roomTypeId
                        + "&staying=" + result.getStayingCount()
                        + "&future=" + result.getFutureCount();
                response.sendRedirect(redirectUrl);
                return;
            }

            // 4. Nếu an toàn, thực hiện xóa (hoặc update is_active = 0)
            boolean isDeleted = roomTypeDAO.deleteRoomType(roomTypeId);

            // 5. Điều hướng sau khi xóa thành công
            if (isDeleted) {
                response.sendRedirect(request.getContextPath() + "/roomtypelist?status=deleted");
            } else {
                response.sendRedirect(request.getContextPath() + "/roomtypelist?status=delete_failed");
            }

        } catch (Exception e) {
            System.out.println("LỖI TẠI RoomTypeDeleteServlet: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/roomtypelist?status=delete_error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
