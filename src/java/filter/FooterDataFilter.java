///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package filter;
//import dao.HotelInfoDAO;
//import model.HotelInfo;
//import java.io.IOException;
//import jakarta.servlet.Filter;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.FilterConfig;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletRequest;
//import jakarta.servlet.ServletResponse;
//import jakarta.servlet.annotation.WebFilter;
///**
// *
// * @author Minh Thu
// */
//@WebFilter(filterName = "FooterDataFilter", urlPatterns = {"/*"})
//public class FooterDataFilter implements Filter {
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        // Khởi tạo (để trống nếu không dùng)
//    }
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//        
//        try {
//            // Kiểm tra nếu chưa có hotelInfo trong request thì mới gọi DB
//            if (request.getAttribute("hotelInfo") == null) {
//                HotelInfoDAO hotelInfoDAO = new HotelInfoDAO();
//                HotelInfo hotelInfo = hotelInfoDAO.getHotelDetails(1);
//                
//                // 1. Đẩy dữ liệu khách sạn vào request phục vụ cho Footer toàn hệ thống
//                request.setAttribute("hotelInfo", hotelInfo);
//                request.setAttribute("hotelDetails", hotelInfo); // Dự phòng nếu trang chủ cũ đang dùng tên biến này
//                
//                // 2. 🔥 ĐÃ SỬA: Trích xuất tấm ảnh CUỐI CÙNG (MỚI NHẤT) trong bộ sưu tập để gán vào 'bgImage'
//                if (hotelInfo != null && hotelInfo.getImageUrl() != null && !hotelInfo.getImageUrl().isEmpty()) {
//                    int lastIndex = hotelInfo.getImageUrl().size() - 1; // Lấy vị trí phần tử cuối cùng
//                    String latestImageUrl = hotelInfo.getImageUrl().get(lastIndex);
//                    
//                    request.setAttribute("bgImage", latestImageUrl);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        // Đi tiếp tới Servlet hoặc trang JSP mục tiêu
//        chain.doFilter(request, response);
//    }
//
//    @Override
//    public void destroy() {
//        // Hủy bộ lọc khi dừng server (để trống nếu không dùng)
//    }
//}
