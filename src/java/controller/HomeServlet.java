/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.HotelImageDAO;
import dao.HotelInfoDAO; // Import thêm DAO xử lý dịch vụ và thông tin khách sạn
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Service; // Import lớp thực thể dịch vụ
import model.HotelInfo;    // Import lớp thực thể thông tin khách sạn

/**
 *
 * @author Minh Thu
 */
@WebServlet(name = "HomeServlet", urlPatterns = {"/home", ""})
public class HomeServlet extends HttpServlet {

    /**
     * * Processes requests for both HTTP <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Gọi chung một hàm xử lý dữ liệu cho cả GET và POST
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Khởi tạo các đối tượng DAO xử lý dữ liệu
        HotelImageDAO hotelImageDAO = new HotelImageDAO();
        HotelInfoDAO hotelInfoDAO = new HotelInfoDAO();

        // Giả định hotel_id của La Mer Hotel mặc định là 1 theo cấu trúc DB của bạn
        int hotelId = 1;

        String bgImage = hotelImageDAO.getLatestHotelBackgroundImage(hotelId);
        request.setAttribute("bgImage", bgImage);

        // 3. Lấy danh sách dịch vụ cao cấp từ bảng HotelServices
        List<Service> servicesList = hotelInfoDAO.getActiveHotelServices();
        request.setAttribute("services", servicesList);

        // 4. Lấy thông tin tổng quan, chính sách và giờ giấc từ bảng HotelInfo
        HotelInfo hotelDetails = hotelInfoDAO.getHotelDetails(hotelId);
        request.setAttribute("hotelDetails", hotelDetails);

        // 5. Chuyển tiếp dữ liệu đến trang hiển thị chính thức
        request.getRequestDispatcher("/view/public/homepage.jsp").forward(request, response);
    }
}
