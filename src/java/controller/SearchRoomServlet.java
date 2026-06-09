///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
// */
//package controller;
//
//import dao.RoomTypeDAO;
//import java.io.IOException;
//import java.io.PrintWriter;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.util.List;
//import model.RoomType;
//
///**
// *
// * @author Minh Thu
// */
//@WebServlet(name = "SearchRoomServlet", urlPatterns = {"/search"}) 
//public class SearchRoomServlet extends HttpServlet {
//
//    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        response.setContentType("text/html;charset=UTF-8");
//
//        // 1. Đón nhận thông tin tìm kiếm từ thanh search-bar gửi sang
//        String checkIn = request.getParameter("checkIn");
//        String checkOut = request.getParameter("checkOut");
//        String roomQuantityStr = request.getParameter("roomQuantity");
//        String roomTypeId = request.getParameter("roomTypeId");
//
//        RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
//
//        // Luôn luôn lấy tất cả loại phòng để đổ vào thanh <select> của thanh search
//        List<RoomType> allRoomTypesList = roomTypeDAO.getAllRoomTypes();
//        request.setAttribute("allRoomTypesList", allRoomTypesList);
//
//        List<RoomType> list;
//
//        // 2. Logic kiểm tra điều hướng lọc phòng trống:
//        if (checkIn == null || checkOut == null || checkIn.trim().isEmpty() || checkOut.trim().isEmpty()) {
//            // Nếu khách chưa nhập ngày (Vào trực tiếp từ link hoặc click Khám phá) -> Lấy danh sách gốc sẵn có
//            list = allRoomTypesList;
//        } else {
//            // Xử lý bốc số lượng phòng cần đặt
//            int roomQuantity = 1;
//            if (roomQuantityStr != null && !roomQuantityStr.trim().isEmpty()) {
//                try {
//                    roomQuantity = Integer.parseInt(roomQuantityStr.trim());
//                } catch (NumberFormatException e) {
//                    roomQuantity = 1;
//                }
//            }
//
//            // Xử lý giá trị mặc định cho loại phòng nếu null
//            if (roomTypeId == null) {
//                roomTypeId = "all";
//            }
//
//            // Gọi hàm đếm số lượng phòng trống từ DAO mới
//            list = roomTypeDAO.searchRoomTypesByQuantity(checkIn, checkOut, roomQuantity, roomTypeId);
//        }
//
//        // ====================================================================================
//        // 🔥 ĐÃ XÓA: Đoạn gọi HotelInfoDAO rườm rà ở đây vì đã có FooterDataFilter lo ngầm 
//        // cho cả hệ thống rồi, tránh việc ép SQL Server bắt kết nối thêm một lần vô ích.
//        // ====================================================================================
//
//        // 3. Đẩy list kết quả tìm kiếm sang request attribute để trang kết quả hiển thị
//        request.setAttribute("availableRoomTypes", list);
//
//        // Chuyển tiếp chính xác đến trang giao diện search-result của bạn
//        request.getRequestDispatcher("/view/public/search-result.jsp").forward(request, response);
//    }
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        processRequest(request, response);
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        processRequest(request, response);
//    }
//}