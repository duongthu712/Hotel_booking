/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dao.RoomTypeDAO;
import dao.HotelInfoDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.RoomType;
import model.HotelInfo;

/**
 *
 * @author Minh Thu
 */
@WebServlet(name = "RoomDetailServlet", urlPatterns = {"/room-detail"})
public class RoomDetailServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        try {
            // 1. Lấy ID hạng phòng từ URL gửi sang
            String idStr = request.getParameter("id");
            if (idStr != null) {
                int roomTypeId = Integer.parseInt(idStr);
                
                // 2. Gọi DAO lấy dữ liệu phòng chi tiết
                RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
                RoomType room = roomTypeDAO.getRoomTypeById(roomTypeId);
                
                // 3. Đẩy thông tin phòng vào request
                request.setAttribute("room", room);
            }
            
            // 4. ĐỒNG BỘ: Gọi thông tin khách sạn ID = 1 để nạp vào Footer
            HotelInfoDAO hotelInfoDAO = new HotelInfoDAO();
            HotelInfo hotelInfo = hotelInfoDAO.getHotelDetails(1);
            request.setAttribute("hotelInfo", hotelInfo);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 5. Chuyển tiếp sang giao diện chi tiết
        request.getRequestDispatcher("/view/public/room-detail.jsp").forward(request, response);
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