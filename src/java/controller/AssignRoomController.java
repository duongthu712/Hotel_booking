package controller;

import dao.RoomDAO;
import dto.RoomStatusView;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet(name = "AssignRoomController", urlPatterns = {"/assign-room"})
public class AssignRoomController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String bookingIdParam = request.getParameter("bookingId");
            if (bookingIdParam != null && !bookingIdParam.isEmpty()) {
                int bookingId = Integer.parseInt(bookingIdParam);
                request.setAttribute("targetBookingId", bookingId);
            }

            RoomDAO roomDAO = new RoomDAO();
            List<RoomStatusView> roomMatrix = roomDAO.getAllRoomStatusViews();
            
            request.setAttribute("roomMatrix", roomMatrix);
            request.getRequestDispatcher("/view/receptionist/assign-room.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/view/receptionist/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int bookingId = Integer.parseInt(request.getParameter("bookingId"));
            int selectedRoomNumber = Integer.parseInt(request.getParameter("selectedRoomNumber")); 

            RoomDAO roomDAO = new RoomDAO();
            boolean isSuccess = roomDAO.assignRoomAndCheckIn(bookingId, selectedRoomNumber);

            if (isSuccess) {
                response.sendRedirect(request.getContextPath() + "/checkin?message=AssignSuccess");
            } else {
                response.sendRedirect(request.getContextPath() + "/assign-room?bookingId=" + bookingId + "&error=AssignFailed");
            }
            
        } catch (Exception e) {
            String bookingIdParam = request.getParameter("bookingId");
            String encodedError = java.net.URLEncoder.encode(e.getMessage() != null ? e.getMessage() : "Unknown Error", "UTF-8");
            response.sendRedirect(request.getContextPath() + "/assign-room?bookingId=" + bookingIdParam + "&error=" + encodedError);
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}