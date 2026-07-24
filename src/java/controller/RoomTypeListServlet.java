/**
 * Author: ThuDNM-HE204370
 * Date created: 11/06/2026
 * Purpose: Controller logic for RoomTypeListServlet.
 */
package controller;

import dao.RoomTypeDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.RoomType;

@WebServlet(name = "RoomTypeListServlet", urlPatterns = {"/roomtypelist"})
public class RoomTypeListServlet extends HttpServlet {
   
    private final RoomTypeDAO roomTypeDAO = new RoomTypeDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<RoomType> list = roomTypeDAO.getAllRoomTypesForManager();
        
        
        request.setAttribute("roomTypesList", list);
        
        request.getRequestDispatcher("/view/manager/room-type-management.jsp").forward(request, response);
    } 

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Room Type List Controller for Manager";
    }
}