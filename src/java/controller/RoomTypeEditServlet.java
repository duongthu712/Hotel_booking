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
            //Lấy id hạng phòng cần edit
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/roomtypelist");
                return;
            }
            int roomTypeId = Integer.parseInt(idStr);

            // Lấy toàn bộ thông tin về hạng phòng có id đc chọn
            RoomType roomTypeGoc = roomTypeDAO.getRoomTypeById(roomTypeId);
            if (roomTypeGoc == null) {
                response.sendRedirect(request.getContextPath() + "/roomtypelist");
                return;
            }
            request.setAttribute("roomType", roomTypeGoc);

            // Nạp danh sách Dịch vụ và Tiện nghi toàn hệ thống để render các hàng checkbox chọn
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
        request.setCharacterEncoding("UTF-8");

        // 1. Đọc dữ liệu từ form
        int roomTypeId = Integer.parseInt(request.getParameter("roomTypeId"));
        String typeName = request.getParameter("typeName");
        String description = request.getParameter("description");
        int capacity = Integer.parseInt(request.getParameter("capacity"));
        String bedType = request.getParameter("bedType");
        int bedCount = Integer.parseInt(request.getParameter("bedCount"));
        BigDecimal areaSqm = new BigDecimal(request.getParameter("areaSqm"));
        BigDecimal basePrice = new BigDecimal(request.getParameter("basePrice"));
        boolean isActive = request.getParameter("isActive") != null;

        RoomType rt = new RoomType(roomTypeId, typeName, description, capacity, bedType, bedCount, areaSqm, basePrice, isActive);

        // 2. Validation 
        boolean hasError = false;
        String errorMessage = "";

        // Check trùng tên (loại trừ chính nó)
        if (roomTypeDAO.isRoomTypeNameExistForEdit(typeName, roomTypeId)) {
            hasError = true;
            errorMessage = "Tên hạng phòng \"" + typeName + "\" đã tồn tại!";
            request.setAttribute("status", "duplicate");
        }
        // Check constraints
        if (bedCount < 1 || bedCount > 20) {
            hasError = true;
            errorMessage = "Số lượng giường phải từ 1 đến 20.";
        }
        if (areaSqm.compareTo(new BigDecimal("999.99")) > 0) {
            hasError = true;
            errorMessage = "Diện tích phòng tối đa 999.99 m².";
        }

        // 3. Gom Data Phụ (Ảnh, Dịch vụ, Tiện nghi) để giữ trạng thái trên form
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

        String[] selectedServiceIds = request.getParameterValues("selectedServices");
        List<RoomTypeService> serviceList = new ArrayList<>();
        if (selectedServiceIds != null) {
            for (String sId : selectedServiceIds) {
                int id = Integer.parseInt(sId);
                int qty = Integer.parseInt(request.getParameter("quantity_" + id));
                int isFree = Integer.parseInt(request.getParameter("isFree_" + id));
                if (isFree > qty) {
                    hasError = true;
                    errorMessage = "Số lượng miễn phí không được vượt quá số lượng trang bị!";
                }
                RoomTypeService rts = new RoomTypeService();
                rts.setServiceId(id);
                rts.setQuantity(qty);
                rts.setIsFree(isFree);
                serviceList.add(rts);
            }
        }
        rt.setRoomTypeServices(serviceList);

        String[] selectedAmenityIds = request.getParameterValues("selectedAmenities");
        List<RoomAmenity> amenityList = new ArrayList<>();
        if (selectedAmenityIds != null) {
            for (String aId : selectedAmenityIds) {
                RoomAmenity ra = new RoomAmenity();
                ra.setAmenityId(Integer.parseInt(aId));
                ra.setDescription(request.getParameter("quantity_amenity_" + aId));
                amenityList.add(ra);
            }
        }
        rt.setRoomAmenities(amenityList);

        // 4. Nếu lỗi -> Forward về Edit (Giữ nguyên dữ liệu)
        if (hasError) {
            request.setAttribute("errorMessage", errorMessage);
            request.setAttribute("roomType", rt);
            try {
                request.setAttribute("availableServices", serviceDAO.getAllRoomServices());
                request.setAttribute("availableAmenities", amenityDAO.getAllRoomAmenities());
            } catch (Exception e) {
                e.printStackTrace();
            }
            request.getRequestDispatcher("view/manager/edit-room-type.jsp").forward(request, response);
            return;
        }

        // 5. Nếu không lỗi -> Gọi DAO update
        if (roomTypeDAO.updateRoomType(rt, imageList, serviceList, amenityList)) {
            response.sendRedirect(request.getContextPath() + "/roomtypelist?status=updated");
        } else {
            request.setAttribute("errorMessage", "Hệ thống gặp lỗi kết nối DB khi cập nhật!");
            request.getRequestDispatcher("view/manager/edit-room-type.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
