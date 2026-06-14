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
@WebServlet(name = "RoomTypeEditServlet", urlPatterns = {"/roomtypeedit"})
public class RoomTypeEditServlet extends HttpServlet {

    private final RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
    private final RoomServiceDAO serviceDAO = new RoomServiceDAO();
    private final RoomAmenityDAO amenityDAO = new RoomAmenityDAO();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet RoomTypeEditServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet RoomTypeEditServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // 1. Lấy ID hạng phòng từ URL danh sách truyền sang (/roomtypeedit?id=...)
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/roomtypelist");
                return;
            }
            int roomTypeId = Integer.parseInt(idStr);

            // 2. Lấy toàn bộ thực thể dữ liệu gốc từ DB lên
            RoomType roomTypeGoc = roomTypeDAO.getRoomTypeById(roomTypeId);
            if (roomTypeGoc == null) {
                response.sendRedirect(request.getContextPath() + "/roomtypelist");
                return;
            }
            request.setAttribute("roomType", roomTypeGoc);

            // 3. Nạp danh sách Dịch vụ và Tiện nghi toàn hệ thống để render các hàng checkbox chọn
            List<RoomService> availableServices = serviceDAO.getAllRoomServices();
            List<RoomAmenity> availableAmenities = amenityDAO.getAllRoomAmenities();
            request.setAttribute("availableServices", availableServices);
            request.setAttribute("availableAmenities", availableAmenities);

        } catch (Exception e) {
            throw new ServletException("Lỗi nạp dữ liệu gốc cấu hình hạng phòng", e);
        }

        // Forward sang trang giao diện sửa
        request.getRequestDispatcher("view/manager/edit-room-type.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1. Đọc ID và Tên hạng phòng từ Form gửi lên để làm bộ lọc kiểm tra
        int roomTypeId = Integer.parseInt(request.getParameter("roomTypeId"));
        String typeName = request.getParameter("typeName");

        // 2. LOGIC CHECK TRÙNG NÂNG CẤP: Dùng hàm riêng loại trừ chính ID đang sửa
        if (roomTypeDAO.isRoomTypeNameExistForEdit(typeName, roomTypeId)) {
            request.setAttribute("status", "duplicate");
            request.setAttribute("invalidName", typeName);

            String description = request.getParameter("description");
            int capacity = Integer.parseInt(request.getParameter("capacity"));
            String bedType = request.getParameter("bedType");
            int bedCount = Integer.parseInt(request.getParameter("bedCount"));
            BigDecimal areaSqm = new BigDecimal(request.getParameter("areaSqm"));
            BigDecimal basePrice = new BigDecimal(request.getParameter("basePrice"));
            boolean isActive = request.getParameter("isActive") != null;

            RoomType fakeRoomType = new RoomType(roomTypeId, typeName, description, capacity, bedType, bedCount, areaSqm, basePrice, isActive);
            request.setAttribute("roomType", fakeRoomType);

            try {
                request.setAttribute("availableServices", serviceDAO.getAllRoomServices());
                request.setAttribute("availableAmenities", amenityDAO.getAllRoomAmenities());
            } catch (Exception e) {
                throw new ServletException(e);
            }

            request.getRequestDispatcher("view/manager/edit-room-type.jsp").forward(request, response);
            return;
        }

        // 3. NẾU KHÔNG TRÙNG -> TIẾP TỤC ĐỌC CÁC THÔNG SỐ CƠ BẢN
        String description = request.getParameter("description");
        int capacity = Integer.parseInt(request.getParameter("capacity"));
        String bedType = request.getParameter("bedType");
        int bedCount = Integer.parseInt(request.getParameter("bedCount"));
        BigDecimal areaSqm = new BigDecimal(request.getParameter("areaSqm"));
        BigDecimal basePrice = new BigDecimal(request.getParameter("basePrice"));
        boolean isActive = request.getParameter("isActive") != null;

        RoomType rt = new RoomType(roomTypeId, typeName, description, capacity, bedType, bedCount, areaSqm, basePrice, isActive);

        // --- ĐỌC MẢNG ALBUM ẢNH PHỤ MỚI ---
        String[] imageUrls = request.getParameterValues("imageUrls");
        List<String> imageList = new ArrayList<>();
        if (imageUrls != null) {
            for (String url : imageUrls) {
                if (url != null && !url.trim().isEmpty()) {
                    imageList.add(url.trim());
                }
            }
        }
        rt.setImageUrl(imageList); // Đồng bộ album ảnh phụ vào object phòng

        // --- BƯỚC CHIẾN THUẬT 1: GOM SẠCH DỊCH VỤ TỪ FORM VÀ ĐÁNH DẤU CỜ LỖI ---
        String[] selectedServiceIds = request.getParameterValues("selectedServices");
        List<model.RoomTypeService> serviceList = new ArrayList<>();
        boolean hasServiceError = false;

        if (selectedServiceIds != null) {
            for (String serviceIdStr : selectedServiceIds) {
                int serviceId = Integer.parseInt(serviceIdStr);
                int quantity = Integer.parseInt(request.getParameter("quantity_" + serviceId));
                int isFree = Integer.parseInt(request.getParameter("isFree_" + serviceId));

                // Bật cờ báo lỗi nếu lượng free lố lượng setup nhưng KHÔNG return vội, tiếp tục duyệt để gom data
                if (isFree > quantity) {
                    hasServiceError = true;
                }

                model.RoomTypeService rts = new model.RoomTypeService();
                rts.setServiceId(serviceId);
                rts.setQuantity(quantity);
                rts.setIsFree(isFree);
                serviceList.add(rts);
            }
        }

        // --- BƯỚC CHIẾN THUẬT 2: GOM SẠCH TIỆN NGHI TỪ FORM ---
        String[] selectedAmenityIds = request.getParameterValues("selectedAmenities");
        List<RoomAmenity> amenityList = new ArrayList<>();
        if (selectedAmenityIds != null) {
            for (String amenityIdStr : selectedAmenityIds) {
                int amenityId = Integer.parseInt(amenityIdStr);
                String qtyParam = request.getParameter("quantity_amenity_" + amenityId);
                String quantityStr = (qtyParam != null) ? qtyParam : "1";

                RoomAmenity ra = new RoomAmenity();
                ra.setAmenityId(amenityId);
                ra.setDescription(quantityStr); // Giữ lại số lượng tiện nghi thô đã nhập
                ra.setActive(true);

                amenityList.add(ra);
            }
        }

        // --- BƯỚC CHIẾN THUẬT 3: KIỂM TRA CHẶN LỖI TẬP TRUNG ---
        if (hasServiceError) {
            // Đóng gói toàn bộ mớ data vừa gom được trên form nhét vào xác Object rt để đổ ngược lên JSP cứu vớt checkbox
            rt.setRoomTypeServices(serviceList);
            rt.setRoomAmenities(amenityList);

            request.setAttribute("errorMessage", "Cập nhật thất bại: Số lượng miễn phí không được vượt quá số lượng trang bị sẵn tại phòng!");

            // Nạp lại mảng dịch vụ & tiện nghi nền của hệ thống, gánh SQLException tại chỗ
            try {
                request.setAttribute("availableServices", serviceDAO.getAllRoomServices());
                request.setAttribute("availableAmenities", amenityDAO.getAllRoomAmenities());
            } catch (Exception e) {
                System.out.println(">>> LỖI KHI NẠP LẠI DATA NỀN TẠI SERVLET: " + e.getMessage());
            }

            request.setAttribute("roomType", rt);
            request.getRequestDispatcher("view/manager/edit-room-type.jsp").forward(request, response);
            return; // Chặn đứng luồng ghi vào Database!
        }

        // 4. GỌI DAO THỰC THI TRANSACTION UPDATE KHI MỌI THỨ HỢP LỆ
        boolean isUpdated = false;
        try {
            isUpdated = roomTypeDAO.updateRoomType(rt, imageList, serviceList, amenityList);
        } catch (Exception e) {
            throw new ServletException("Lỗi nghiêm trọng khi thực thi Update Batch Transaction", e);
        }

        // 5. ĐIỀU HƯỚNG KẾT QUẢ VỀ MANAGE LIST
        if (isUpdated) {
            response.sendRedirect(request.getContextPath() + "/roomtypelist?status=updated");
        } else {
            request.setAttribute("errorMessage", "Hệ thống gặp lỗi kết nối DB khi cập nhật hạng phòng!");
            rt.setRoomTypeServices(serviceList);
            rt.setRoomAmenities(amenityList);
            request.setAttribute("roomType", rt);
            try {
                request.setAttribute("availableServices", serviceDAO.getAllRoomServices());
                request.setAttribute("availableAmenities", amenityDAO.getAllRoomAmenities());
            } catch (Exception e) {
                throw new ServletException(e);
            }
            request.getRequestDispatcher("view/manager/edit-room-type.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}