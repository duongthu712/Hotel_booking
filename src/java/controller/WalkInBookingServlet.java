package controller;

import dao.RoomTypeDAO;
import model.RoomType; 
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "WalkInBookingServlet", urlPatterns = {"/walk-in-booking"})
public class WalkInBookingServlet extends HttpServlet {

 @Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
    
    // Lấy danh sách tổng (có Description)
    List<RoomType> allRoomTypes = roomTypeDAO.getAllRoomTypesForManager();

    String checkIn = request.getParameter("checkInDate");
    String checkOut = request.getParameter("checkOutDate");

    if (checkIn != null && !checkIn.isEmpty() && checkOut != null && !checkOut.isEmpty()) {
        List<RoomType> availableList = roomTypeDAO.searchAvailableRoomTypesForWalkIn(checkIn, checkOut);
        
        // Map số lượng trống vào danh sách tổng
        for (RoomType rt : allRoomTypes) {
            rt.setAvailableRooms(-1); // -1 nghĩa là chưa tìm
            for (RoomType avail : availableList) {
                if (rt.getRoomTypeId() == avail.getRoomTypeId()) {
                    rt.setAvailableRooms(avail.getAvailableRooms());
                    break;
                }
            }
        }
        request.setAttribute("isSearching", true);
    }

    request.setAttribute("allRoomTypes", allRoomTypes);
    request.getRequestDispatcher("/view/receptionist/walk-in-booking.jsp").forward(request, response);
}

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}