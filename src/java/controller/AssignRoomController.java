package controller;

import dao.BookingDAO;
import dao.RoomDAO;
import dto.BookingCheckInView;
import dto.RoomStatusView;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import model.GuestStay;

@WebServlet(name = "AssignRoomController", urlPatterns = {"/assign-room"})
public class AssignRoomController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        RoomDAO roomDAO = new RoomDAO();
        BookingDAO bookingDAO = new BookingDAO();
        int targetRoomTypeId = 0; // Mặc định bằng 0 để xem tổng quan toàn bộ phòng khách sạn

        //Lọc bằng loại phòng và số tầng
        String filterRoomTypeName = request.getParameter("filterRoomTypeName");
        String filterFloor = request.getParameter("filterFloor");

        String bookingIdParam = request.getParameter("bookingId");
        if (bookingIdParam != null && !bookingIdParam.trim().isEmpty()) {
            try {
                int bookingId = Integer.parseInt(bookingIdParam);

                // Lấy thông tin đơn check-in từ hàm có sẵn trong RoomDAO
                BookingCheckInView targetBooking = roomDAO.getBookingForCheckInById(bookingId);

                if (targetBooking != null) {
                    request.setAttribute("targetBooking", targetBooking);
                    request.setAttribute("targetBookingId", bookingId);

                    // Lấy đúng ID hạng phòng để hiển thị ma trận phòng lọc theo hạng của đơn đó
                    targetRoomTypeId = targetBooking.getRoomTypeId();

                    // Đếm số lượng phòng thực tế đã gán trong bảng BookingRooms của đơn này
                    int assignedRoomsCount = bookingDAO.countRoomsAssigned(bookingId);
                    request.setAttribute("assignedRoomsCount", assignedRoomsCount);
                }
            } catch (Exception e) {
                System.out.println("Lỗi doGet AssignRoomController: " + e.getMessage());
            }
        }

        // Bốc danh sách Hạng phòng và Tầng động từ DB để nạp vào thanh Search
        List<String> activeRoomTypes = roomDAO.getAllActiveRoomTypeNames();
        List<Integer> existingFloors = roomDAO.getAllExistingFloors();
        request.setAttribute("activeRoomTypes", activeRoomTypes);
        request.setAttribute("existingFloors", existingFloors);

        // Gọi hàm DAO mới - Truyền thêm 2 tham số filter đầu vào để SQL câu lệnh gộp lọc động
        List<RoomStatusView> roomMatrix = roomDAO.getAllRoomStatusViews(targetRoomTypeId, filterRoomTypeName, filterFloor);
        request.setAttribute("roomMatrix", roomMatrix);

        request.getSession().removeAttribute("notification");
        request.getSession().removeAttribute("notificationType");

        request.getRequestDispatcher("/view/receptionist/assign-room.jsp").forward(request, response);
    }

   @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        RoomDAO roomDAO = new RoomDAO();
        BookingDAO bookingDAO = new BookingDAO();
        
        try {
            int bookingId = Integer.parseInt(request.getParameter("bookingId"));
            String selectedRoomParam = request.getParameter("selectedRoomNumber"); 

            if (selectedRoomParam == null || selectedRoomParam.isEmpty()) {
                request.getSession().setAttribute("notification", "error");
                request.getSession().setAttribute("notificationType", "error");
                response.sendRedirect(request.getContextPath() + "/assign-room?bookingId=" + bookingId);
                return;
            }
            
            int roomNumber = Integer.parseInt(selectedRoomParam);

            // Đọc mảng thông tin khách hàng từ Form động nộp lên
            String[] fullNames = request.getParameterValues("stayFullName");
            String[] phones = request.getParameterValues("stayPhone");
            String[] idNumbers = request.getParameterValues("stayIdNumber");

            // Lấy tổng số phòng của đơn đặt này để tính điều kiện chốt đơn
            var currentBooking = roomDAO.getBookingForCheckInById(bookingId);
            int totalRequiredRooms = (currentBooking != null) ? currentBooking.getNumRooms() : 1;

            // Gọi hàm DAO xử lý Transaction liên hoàn
            boolean isSuccess = roomDAO.processRoomAssignment(bookingId, roomNumber, fullNames, phones, idNumbers, totalRequiredRooms);

            if (isSuccess) {
                request.getSession().setAttribute("notification", "success");
                request.getSession().setAttribute("notificationType", "success");
                
                int assignedCount = bookingDAO.countRoomsAssigned(bookingId);
                if (assignedCount < totalRequiredRooms) {
                    // Nếu chưa gán đủ phòng, ở lại trang gán tiếp phòng thứ 2
                    response.sendRedirect(request.getContextPath() + "/assign-room?bookingId=" + bookingId);
                } else {
                    // Đã gán đủ phòng, đẩy về danh sách check-in chính
                    response.sendRedirect(request.getContextPath() + "/checkin");
                }
            } else {
                request.getSession().setAttribute("notification", "error");
                request.getSession().setAttribute("notificationType", "error");
                response.sendRedirect(request.getContextPath() + "/assign-room?bookingId=" + bookingId);
            }

        } catch (Exception e) {
            System.out.println("Lỗi doPost AssignRoomController: " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("notification", "error");
            request.getSession().setAttribute("notificationType", "error");
            response.sendRedirect(request.getContextPath() + "/assign-room");
        }
    }
}
