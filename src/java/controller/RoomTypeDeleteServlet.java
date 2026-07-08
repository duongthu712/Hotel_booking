package controller;

import dao.RoomTypeDAO;
import dto.DeactiveValidationResult;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "RoomTypeDeleteServlet", urlPatterns = {"/roomtypedelete"})
public class RoomTypeDeleteServlet extends HttpServlet {

    private final RoomTypeDAO roomTypeDAO = new RoomTypeDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/roomtypelist?status=error");
                return;
            }
            int roomTypeId = Integer.parseInt(idStr);

            // 1. Kiểm tra xem có tham số xác nhận xóa từ Pop-up gửi lên hay không
            String confirm = request.getParameter("confirm");
            
            // 2. Lấy dữ liệu kiểm tra từ cơ sở dữ liệu
            DeactiveValidationResult result = roomTypeDAO.getDeactiveValidationResult(roomTypeId);
            
            // 3. Nếu đã bấm nút "Đồng ý" từ Pop-up (confirm=true) HOẶC phòng hoàn toàn trống đơn
            if ("true".equals(confirm) || !result.isBlocked()) {
                boolean isDeleted = roomTypeDAO.deleteRoomType(roomTypeId);
                if (isDeleted) {
                    if (result.getStayingCount() > 0 || result.getFutureCount() > 0) {
                        response.sendRedirect(request.getContextPath() + "/roomtypelist?status=deleted_with_orders"
                                + "&staying=" + result.getStayingCount()
                                + "&future=" + result.getFutureCount());
                    } else {
                        response.sendRedirect(request.getContextPath() + "/roomtypelist?status=deleted");
                    }
                } else {
                    response.sendRedirect(request.getContextPath() + "/roomtypelist?status=delete_failed");
                }
                return;
            }

            // 4. Lần đầu nhấn nút Xóa ngoài danh sách & phát hiện có đơn -> Bắn param để JS mở Pop-up hỏi
            if (result.isBlocked()) {
                String redirectUrl = request.getContextPath() + "/roomtypelist?status=conflict"
                        + "&id=" + roomTypeId
                        + "&staying=" + result.getStayingCount()
                        + "&future=" + result.getFutureCount();
                response.sendRedirect(redirectUrl);
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