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
            // Lấy đầy đủ dịch vụ hiện có để gán cho các phòng
            RoomServiceDAO serviceDAO = new RoomServiceDAO();
            List<RoomService> availableServices = serviceDAO.getAllRoomServices();
            request.setAttribute("availableServices", availableServices);

            // Lấy đầy đủ tiện nghi hiện có để gán cho các phòng
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

        boolean hasError = false;
        String errorMessage = "";

        // 1. Đọc tham số
        String typeName = request.getParameter("typeName");
        String description = request.getParameter("description");
        String capacityStr = request.getParameter("capacity");
        int numGuests = Integer.parseInt(request.getParameter("num_guests"));
        int numChildren = Integer.parseInt(request.getParameter("num_children"));
        String bedType = request.getParameter("bedType");
        String bedCountStr = request.getParameter("bedCount");
        int bedCount = (bedCountStr != null && !bedCountStr.isEmpty()) ? Integer.parseInt(bedCountStr) : 1;
        String areaStr = request.getParameter("areaSqm");
        BigDecimal areaSqm = (areaStr != null && !areaStr.isEmpty()) ? new BigDecimal(areaStr) : BigDecimal.ZERO;
        String priceStr = request.getParameter("basePrice");
        BigDecimal basePrice = (priceStr != null && !priceStr.isEmpty()) ? new BigDecimal(priceStr) : BigDecimal.ZERO;
        boolean isActive = request.getParameter("isActive") != null;

        // Tạo hạng phòng mới với những thông tin cơ bản
        RoomType rt = new RoomType(0, typeName, description, numGuests, numChildren, bedType, bedCount, areaSqm, basePrice, isActive);

        // Check trùng tên
        if (!hasError && roomTypeDAO.isRoomTypeNameExist(typeName)) {
            hasError = true;
            errorMessage = "Tên hạng phòng \"" + typeName + "\" đã tồn tại!";
            request.setAttribute("status", "duplicate");
        }
        // Ràng buộc dưới 50 người
        if ((numGuests + numChildren) >= 50) {
            hasError = true;
            errorMessage = "Tổng số khách phải dưới 50 người!";
            request.setAttribute("status", "error");
        }

        // Check giường nằm trong khoảng từ 1-20
        if (bedCount < 1 || bedCount > 20) {
            hasError = true;
            errorMessage = "Số lượng giường phải nằm trong khoảng từ 1 đến 20.";
        }
        // Check diện tích < 999.99
        if (areaSqm.compareTo(new BigDecimal("999.99")) > 0) {
            hasError = true;
            errorMessage = "Diện tích phòng vượt quá giới hạn hệ thống (tối đa 999.99 m²).";
        }

        // Lấy dữ liệu list ảnh 
        String[] imageUrls = request.getParameterValues("imageUrls");
        List<String> imageList = new ArrayList<>();
        if (imageUrls != null) {
            for (String url : imageUrls) {
                if (url != null && !url.trim().isEmpty()) {
                    imageList.add(url.trim());
                }
            }
        }
        // Cập nhật thêm ảnh vào hạng phòng mới
        rt.setImageUrl(imageList);

        // Lấy id của list dịch vụ đã nhập
        String[] selectedServiceIds = request.getParameterValues("selectedServices");
        List<RoomTypeService> serviceList = new ArrayList<>();
        if (selectedServiceIds != null) {
            for (String sId : selectedServiceIds) {
                int id = Integer.parseInt(sId);
                String qtyStr = request.getParameter("quantity_" + id);
                int qty = (qtyStr != null && !qtyStr.isEmpty()) ? Integer.parseInt(qtyStr) : 1;

                String freeStr = request.getParameter("isFree_" + id);
                int isFree = (freeStr != null && !freeStr.isEmpty()) ? Integer.parseInt(freeStr) : 0;

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
        // Cập nhật thêm dịch vụ vào hạng phòng mới
        rt.setRoomTypeServices(serviceList);

        // Lấy id của list tiện nghi đã nhập
        String[] selectedAmenityIds = request.getParameterValues("selectedAmenities");
        List<RoomAmenity> amenityList = new ArrayList<>(); // Khai báo tại đây
        if (selectedAmenityIds != null) {
            for (String aId : selectedAmenityIds) {
                RoomAmenity ra = new RoomAmenity();
                ra.setAmenityId(Integer.parseInt(aId));
                ra.setDescription(request.getParameter("quantity_amenity_" + aId));
                ra.setActive(true);
                amenityList.add(ra);
            }
        }
        rt.setRoomAmenities(amenityList);

        // Nạp dữ liệu đầy đủ dịch vụ và tiện nghi khi trang lỗi để người dùng quay lại sửa đc
        if (hasError) {
            request.setAttribute("errorMessage", errorMessage);
            request.setAttribute("roomType", rt);

            try {
                request.setAttribute("availableServices", new RoomServiceDAO().getAllRoomServices());
                request.setAttribute("availableAmenities", new RoomAmenityDAO().getAllRoomAmenities());
            } catch (Exception e) {
                throw new ServletException("Lỗi nạp lại dữ liệu nền", e);
            }

            request.getRequestDispatcher("view/manager/add-room-type.jsp").forward(request, response);
            return;
        }

        // 6. Ghi vào DB nếu không có lỗi
        try {
            if (roomTypeDAO.insertRoomType(rt, imageList, serviceList, amenityList)) {
                response.sendRedirect(request.getContextPath() + "/roomtypelist?status=success");
            } else {
                throw new Exception("Lỗi khi thêm vào database");
            }
        } catch (Exception e) {
            throw new ServletException("Lỗi lưu dữ liệu phòng vào database", e);
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
