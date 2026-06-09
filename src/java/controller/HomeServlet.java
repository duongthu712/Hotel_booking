///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
// */
//package controller;
//
//import dao.HotelInfoDAO;
//import java.io.IOException;
//import java.util.List;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import model.RoomService;    // Import lớp thực thể thông tin khách sạn
//
///**
// *
// * @author Minh Thu
// */
//@WebServlet(name = "HomeServlet", urlPatterns = {"/home", ""})
//public class HomeServlet extends HttpServlet {
//
//    /**
//     * * Processes requests for both HTTP <code>GET</code> and
//     * <code>POST</code> methods.
//     *
//     * @param request servlet request
//     * @param response servlet response
//     * @throws ServletException if a servlet-specific error occurs
//     * @throws IOException if an I/O error occurs
//     */
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        // Gọi chung một hàm xử lý dữ liệu cho cả GET và POST
//        processRequest(request, response);
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        processRequest(request, response);
//    }
//
//    private void processRequest(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        try {
//            // 1. Khởi tạo DAO xử lý thông tin dịch vụ
//            HotelInfoDAO hotelInfoDAO = new HotelInfoDAO();
//
//            // 2. 🔥 ĐÃ SỬA: Lấy danh sách dịch vụ theo Model RoomService mới gộp
//            // Dùng để hiển thị khối "Dịch vụ & tiện nghi" ở giữa trang chủ
//            List<RoomService> servicesList = hotelInfoDAO.getActiveHotelServices();
//            request.setAttribute("services", servicesList);
//            
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        // 3. Chuyển tiếp dữ liệu đến trang hiển thị chính thức
//        // (Lúc này Filter đã tự động nạp 'hotelInfo' chạy ngầm cho Footer và ảnh nền Hero rồi)
//        request.getRequestDispatcher("/view/public/homepage.jsp").forward(request, response);
//    }
//
//
//    
//}
