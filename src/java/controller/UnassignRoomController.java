/**
 * Author: ThuDNM-HE204370
 * Date created: 08/06/2026
 * Purpose: Controller logic for UnassignRoomController.
 */
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
            int bookingId = Integer.parseInt(request.getParameter("bookingId"));
            int roomId = Integer.parseInt(request.getParameter("roomId"));

            RoomDAO roomDAO = new RoomDAO();
            
            boolean isSuccess = roomDAO.unassignRoom(bookingId, roomId);

            if (isSuccess) {
                request.getSession().setAttribute("notification", "success");
                request.getSession().setAttribute("notificationType", "success");
            } else {
                request.getSession().setAttribute("notification", "error");
                request.getSession().setAttribute("notificationType", "error");
            }

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