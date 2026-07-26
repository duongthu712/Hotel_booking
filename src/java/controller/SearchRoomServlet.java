/**
 * Author: ThuDNM-HE204370
 * Date created: 26/06/2026
 * Purpose: Controller logic for SearchRoomServlet.
 */
package controller;

import dao.BookingDAO;
import dao.RoomTypeDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.RoomType;

/**
 *
 * @author Minh Thu
 */
@WebServlet(name = "SearchRoomServlet", urlPatterns = {"/search"})
public class SearchRoomServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        String checkIn = request.getParameter("checkIn");
        String checkOut = request.getParameter("checkOut");
        String roomQuantityStr = request.getParameter("roomQuantity");
        String roomTypeId = request.getParameter("roomTypeId");

        BookingDAO bookingDAO = new BookingDAO();
        bookingDAO.cancelExpiredBookings();

        RoomTypeDAO roomTypeDAO = new RoomTypeDAO();

        List<RoomType> allRoomTypesList = roomTypeDAO.getAllRoomTypes();
        request.setAttribute("allRoomTypesList", allRoomTypesList);

        List<RoomType> list;
        int roomQuantity = 1;

        if (roomQuantityStr != null && !roomQuantityStr.trim().isEmpty()) {
            try {
                roomQuantity = Integer.parseInt(roomQuantityStr.trim());
                if (roomQuantity < 1) roomQuantity = 1;
            } catch (NumberFormatException e) {
                roomQuantity = 1;
            }
        }

        if (roomTypeId == null) {
            roomTypeId = "all";
        }

        // Validate dates
        boolean validDates = false;
        if (checkIn != null && checkOut != null && !checkIn.trim().isEmpty() && !checkOut.trim().isEmpty()) {
            try {
                java.sql.Date inDate = java.sql.Date.valueOf(checkIn);
                java.sql.Date outDate = java.sql.Date.valueOf(checkOut);
                
                java.time.LocalDate localIn = inDate.toLocalDate();
                java.time.LocalDate localOut = outDate.toLocalDate();
                java.time.LocalDate localToday = java.time.LocalDate.now();
                
                // CheckIn must not be in the past, and CheckOut must be after CheckIn
                if (!localIn.isBefore(localToday) && localOut.isAfter(localIn)) {
                    validDates = true;
                } else {
                    validDates = false;
                }
            } catch (IllegalArgumentException e) {
                validDates = false;
            }
        }

        if (!validDates) {
            list = allRoomTypesList;
            checkIn = null;
            checkOut = null;
        } else {
            list = roomTypeDAO.searchRoomTypesByQuantity(
                    checkIn,
                    checkOut,
                    roomQuantity,
                    roomTypeId
            );
        }

        request.setAttribute("availableRoomTypes", list);
        request.setAttribute("validatedCheckIn", checkIn);
        request.setAttribute("validatedCheckOut", checkOut);
        request.setAttribute("validatedRoomQuantity", roomQuantity);
        request.setAttribute("validatedRoomTypeId", roomTypeId);

        request.getRequestDispatcher("/view/public/search-result.jsp")
                .forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        processRequest(request, response);
    }
}
