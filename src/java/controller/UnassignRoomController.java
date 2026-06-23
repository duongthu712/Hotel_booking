package controller;

import dao.RoomDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "UnassignRoomController", urlPatterns = {"/unassign-room"})
public class UnassignRoomController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        try {
            // Lấy 2 tham số quan trọng từ nút [Hủy Gán] gửi lên
            int bookingId = Integer.parseInt(request.getParameter("bookingId"));
            int roomNumber = Integer.parseInt(request.getParameter("roomNumber"));

            RoomDAO roomDAO = new RoomDAO();
            
            // Gọi hàm hủy gán phòng dưới DAO
            boolean isSuccess = roomDAO.unassignRoom(bookingId, roomNumber);

            if (isSuccess) {
                request.getSession().setAttribute("notification", "success");
                request.getSession().setAttribute("notificationType", "success");
            } else {
                request.getSession().setAttribute("notification", "error");
                request.getSession().setAttribute("notificationType", "error");
            }

            // Hủy xong thì quay ngược lại đúng trang Chi tiết đơn đặt phòng đó
            response.sendRedirect(request.getContextPath() + "/assign-room?bookingId=" + bookingId);

        } catch (Exception e) {
            System.out.println("Lỗi doPost UnassignRoomController: " + e.getMessage());
            e.printStackTrace();
            
            request.getSession().setAttribute("notification", "error");
            request.getSession().setAttribute("notificationType", "error");
            
            response.sendRedirect(request.getContextPath() + "/checkin");
        }
    }
}