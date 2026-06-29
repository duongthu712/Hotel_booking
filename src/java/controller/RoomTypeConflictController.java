package controller;

import dao.RoomTypeDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "RoomTypeConflictController", urlPatterns = {"/roomtypeconflict"})
public class RoomTypeConflictController extends HttpServlet {

    private final RoomTypeDAO dao = new RoomTypeDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");

        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/roomtypelist");
            return;
        }

        try {
            int roomTypeId = Integer.parseInt(idStr);
            
            request.setAttribute("stayingList", dao.getStayingGuestsByRoomType(roomTypeId));
            request.setAttribute("futureBookings", dao.getFutureBookingsByRoomType(roomTypeId));
            request.setAttribute("roomTypeName", dao.getRoomTypeNameById(roomTypeId));

            request.getRequestDispatcher("/view/manager/room-type-conflict.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/roomtypelist");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Xử lý POST để xác nhận ngừng kinh doanh (Deactive)
        String action = request.getParameter("action");
        String idStr = request.getParameter("id");

        if ("confirmDeactive".equals(action) && idStr != null) {
            int roomTypeId = Integer.parseInt(idStr);
            boolean success = dao.deleteRoomType(roomTypeId);
            
            if (success) {
                response.sendRedirect("roomtypelist?status=deleted");
            } else {
                response.sendRedirect("roomtypelist?status=delete_failed");
            }
        } else {
            // Nếu không phải action này, gọi mặc định doGet
            doGet(request, response);
        }
    }
}