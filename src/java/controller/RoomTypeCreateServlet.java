/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.RoomAmenityDAO;
import dao.RoomServiceDAO;
import dao.RoomTypeDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import model.RoomAmenity;
import model.RoomService;
import model.RoomType;
import model.RoomTypeService;

/**
 *
 * @author Minh Thu
 */

@WebServlet(name = "RoomTypeCreateServlet", urlPatterns = {"/createroomtype"})
public class RoomTypeCreateServlet extends HttpServlet {

    private final RoomTypeDAO roomTypeDAO = new RoomTypeDAO();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet RoomTypeCreateServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet RoomTypeCreateServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // 1. Nạp danh sách Dịch vụ sẵn có cho giao diện chọn
            RoomServiceDAO serviceDAO = new RoomServiceDAO();
            List<RoomService> availableServices = serviceDAO.getAllRoomServices();
            request.setAttribute("availableServices", availableServices);

            // 2. Nạp danh sách Tiện nghi (Amenities) sẵn có cho giao diện chọn
            RoomAmenityDAO amenityDAO = new RoomAmenityDAO();
            List<RoomAmenity> availableAmenities = amenityDAO.getAllRoomAmenities();
            request.setAttribute("availableAmenities", availableAmenities);

        } catch (Exception e) {
            throw new ServletException("Lỗi lấy danh sách cấu hình dịch vụ hoặc tiện nghi", e);
        }

        request.getRequestDispatcher("view/manager/add-room-type.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String typeName = request.getParameter("typeName");

        // --- KHỞI TẠO ĐỐI TƯỢNG BAN ĐẦU ĐỂ HỨNG DỮ LIỆU TỪ FORM ---
        String description = request.getParameter("description");
        
        // Tránh lỗi NumberFormatException nếu admin để trống
        String capacityStr = request.getParameter("capacity");
        int capacity = (capacityStr != null && !capacityStr.isEmpty()) ? Integer.parseInt(capacityStr) : 1;
        
        String bedType = request.getParameter("bedType");
        
        String bedCountStr = request.getParameter("bedCount");
        int bedCount = (bedCountStr != null && !bedCountStr.isEmpty()) ? Integer.parseInt(bedCountStr) : 1;
        
        String areaStr = request.getParameter("areaSqm");
        BigDecimal areaSqm = (areaStr != null && !areaStr.isEmpty()) ? new BigDecimal(areaStr) : BigDecimal.ZERO;
        
        String priceStr = request.getParameter("basePrice");
        BigDecimal basePrice = (priceStr != null && !priceStr.isEmpty()) ? new BigDecimal(priceStr) : BigDecimal.ZERO;
        
        boolean isActive = request.getParameter("isActive") != null;

        RoomType rt = new RoomType(0, typeName, description, capacity, bedType, bedCount, areaSqm, basePrice, isActive);

        // --- ĐỌC DANH SÁCH ALBUM ẢNH ---
        String[] imageUrls = request.getParameterValues("imageUrls");
        List<String> imageList = new ArrayList<>();
        if (imageUrls != null) {
            for (String url : imageUrls) {
                if (url != null && !url.trim().isEmpty()) {
                    imageList.add(url.trim());
                }
            }
        }
        rt.setImageUrl(imageList);

        // --- BƯỚC CHIẾN THUẬT 1: GOM SẠCH DỊCH VỤ VÀ ĐÁNH DẤU CỜ LỖI VALIDATE ---
        String[] selectedServiceIds = request.getParameterValues("selectedServices");
        List<RoomTypeService> serviceList = new ArrayList<>();
        boolean hasServiceError = false;

        if (selectedServiceIds != null) {
            for (String serviceIdStr : selectedServiceIds) {
                int serviceId = Integer.parseInt(serviceIdStr);
                
                String sQtyStr = request.getParameter("quantity_" + serviceId);
                int quantity = (sQtyStr != null && !sQtyStr.isEmpty()) ? Integer.parseInt(sQtyStr) : 1;
                
                String sFreeStr = request.getParameter("isFree_" + serviceId);
                int isFree = (sFreeStr != null && !sFreeStr.isEmpty()) ? Integer.parseInt(sFreeStr) : 0;

                // Bật cờ báo lỗi nếu lượng free vượt lố lượng setup trang bị
                if (isFree > quantity) {
                    hasServiceError = true;
                }

                RoomTypeService rts = new RoomTypeService();
                rts.setServiceId(serviceId);
                rts.setQuantity(quantity);
                rts.setIsFree(isFree);

                serviceList.add(rts);
            }
        }
        rt.setRoomTypeServices(serviceList); // Đóng gói vào đối tượng rt

        // --- BƯỚC CHIẾN THUẬT 2: GOM SẠCH TIỆN NGHI TỪ FORM ---
        String[] selectedAmenityIds = request.getParameterValues("selectedAmenities");
        List<RoomAmenity> amenityList = new ArrayList<>();
        if (selectedAmenityIds != null) {
            for (String amenityIdStr : selectedAmenityIds) {
                int amenityId = Integer.parseInt(amenityIdStr);
                String qtyParam = request.getParameter("quantity_amenity_" + amenityId);
                String quantityStr = (qtyParam != null && !qtyParam.isEmpty()) ? qtyParam : "1";

                RoomAmenity ra = new RoomAmenity();
                ra.setAmenityId(amenityId);
                ra.setDescription(quantityStr);
                ra.setActive(true);

                amenityList.add(ra);
            }
        }
        rt.setRoomAmenities(amenityList); // Đóng gói vào đối tượng rt

        // --- CHỐT CHẶN KIỂM TRA LỖI 1: NẾU TRÙNG TÊN HẠNG PHÒNG ---
        if (roomTypeDAO.isRoomTypeNameExist(typeName)) {
            request.setAttribute("status", "duplicate");
            request.setAttribute("invalidName", typeName);
            request.setAttribute("roomType", rt);

            try {
                RoomServiceDAO serviceDAO = new RoomServiceDAO();
                RoomAmenityDAO amenityDAO = new RoomAmenityDAO();
                request.setAttribute("availableServices", serviceDAO.getAllRoomServices());
                request.setAttribute("availableAmenities", amenityDAO.getAllRoomAmenities());
            } catch (Exception e) {
                throw new ServletException(e);
            }

            request.getRequestDispatcher("view/manager/add-room-type.jsp").forward(request, response);
            return;
        }

        // --- CHỐT CHẶN KIỂM TRA LỖI 2: NẾU LƯỢNG FREE > SETUP (GIỮ NGUYÊN 100% FORM) ---
        if (hasServiceError) {
            request.setAttribute("errorMessage", "Thêm mới thất bại: Số lượng miễn phí không được vượt quá số lượng trang bị sẵn tại phòng!");
            request.setAttribute("roomType", rt); // Đẩy nguyên trạng đối tượng rt chứa mọi thông tin vừa gõ về lại JSP

            try {
                RoomServiceDAO serviceDAO = new RoomServiceDAO();
                RoomAmenityDAO amenityDAO = new RoomAmenityDAO();
                request.setAttribute("availableServices", serviceDAO.getAllRoomServices());
                request.setAttribute("availableAmenities", amenityDAO.getAllRoomAmenities());
            } catch (Exception e) {
                System.out.println(">>> LỖI KHI NẠP LẠI DATA NỀN TẠI SERVLET ADD: " + e.getMessage());
            }

            request.getRequestDispatcher("view/manager/add-room-type.jsp").forward(request, response);
            return; // Chặn đứng luồng ghi vào DB!
        }

        // --- GỌI DAO THỰC THI INSERT TRANSACTION KHI MỌI THỨ HỢP LỆ ---
        boolean isInserted = false;
        try {
            isInserted = roomTypeDAO.insertRoomType(rt, imageList, serviceList, amenityList);
        } catch (Exception e) {
            throw new ServletException("Lỗi lưu dữ liệu phòng vào database", e);
        }

        // --- ĐIỀU HƯỚNG KẾT QUẢ ---
        if (isInserted) {
            response.sendRedirect(request.getContextPath() + "/roomtypelist?status=success");
        } else {
            request.setAttribute("errorMessage", "Hệ thống gặp lỗi trong quá trình lưu dữ liệu phòng!");
            request.setAttribute("roomType", rt);
            try {
                RoomServiceDAO serviceDAO = new RoomServiceDAO();
                RoomAmenityDAO amenityDAO = new RoomAmenityDAO();
                request.setAttribute("availableServices", serviceDAO.getAllRoomServices());
                request.setAttribute("availableAmenities", amenityDAO.getAllRoomAmenities());
            } catch (Exception e) {
                throw new ServletException("Lỗi nạp lại dữ liệu cấu hình", e);
            }
            request.getRequestDispatcher("view/manager/add-room-type.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}