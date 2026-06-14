/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

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

        // 1. Đón nhận thông tin tìm kiếm từ thanh search-bar gửi sang
        String checkIn = request.getParameter("checkIn");
        String checkOut = request.getParameter("checkOut");
        String roomQuantityStr = request.getParameter("roomQuantity");
        String roomTypeId = request.getParameter("roomTypeId");

        RoomTypeDAO roomTypeDAO = new RoomTypeDAO();

        List<RoomType> allRoomTypesList = roomTypeDAO.getAllRoomTypes();
        request.setAttribute("allRoomTypesList", allRoomTypesList);

        List<RoomType> list;

        //Lúc chưa nhập ngày checkin checkout
        if (checkIn == null || checkOut == null || checkIn.trim().isEmpty() || checkOut.trim().isEmpty()) {
            list = allRoomTypesList;
        } else {
            int roomQuantity = 1;
            if (roomQuantityStr != null && !roomQuantityStr.trim().isEmpty()) {
                try {
                    roomQuantity = Integer.parseInt(roomQuantityStr.trim());
                } catch (NumberFormatException e) {
                    roomQuantity = 1;
                }
            }

            if (roomTypeId == null) {
                roomTypeId = "all";
            }

            list = roomTypeDAO.searchRoomTypesByQuantity(checkIn, checkOut, roomQuantity, roomTypeId);
        }

        request.setAttribute("availableRoomTypes", list);

        request.getRequestDispatcher("/view/public/search-result.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}