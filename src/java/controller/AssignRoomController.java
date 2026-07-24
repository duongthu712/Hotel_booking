/**
 * Author: ThuDNM-HE204370
 * Date created: 07/06/2026
 * Purpose: Controller logic for AssignRoomController.
 */
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

@WebServlet(name = "AssignRoomController", urlPatterns = {"/assign-room"})
public class AssignRoomController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        RoomDAO roomDAO = new RoomDAO();
        BookingDAO bookingDAO = new BookingDAO();
        int displayRoomTypeId = 0; 

        String filterRoomTypeName = request.getParameter("filterRoomTypeName");
        String filterFloor = request.getParameter("filterFloor");
        String bookingIdParam = request.getParameter("bookingId");
        String overrideTypeParam = request.getParameter("overriddenRoomTypeId");
        List<RoomStatusView> allRoomTypes = roomDAO.getAllActiveRoomTypesForDropdown();
        request.setAttribute("allRoomTypes", allRoomTypes);

        if (bookingIdParam != null && !bookingIdParam.trim().isEmpty()) {
            try {
                int bookingId = Integer.parseInt(bookingIdParam);
                BookingCheckInView targetBooking = roomDAO.getBookingForCheckInById(bookingId);

                if (targetBooking != null) {
                    // Bước 1: Lưu tên hạng phòng gốc vào một thuộc tính riêng trước khi bị hàm override ghi đè
                    request.setAttribute("originalRoomTypeName", targetBooking.getRoomTypeName());

                    // Mặc định ban đầu: hiển thị theo hạng phòng khách đặt gốc
                    displayRoomTypeId = targetBooking.getRoomTypeId();

                    // NẾU CÓ YÊU CẦU ĐỔI HẠNG (Do phòng hỏng hoặc chủ động upgrade từ lễ tân)
                    if (overrideTypeParam != null && !overrideTypeParam.trim().isEmpty()) {
                        displayRoomTypeId = Integer.parseInt(overrideTypeParam);
                        request.setAttribute("isOverriddenType", true);
                        
                        // Ghi đè định mức người lớn/trẻ em vào targetBooking để hiển thị đồng bộ ở form nhập số khách bên phải
                        roomDAO.overrideBookingCapacityWithType(targetBooking, displayRoomTypeId);
                        
                        // Bước 2: Tìm tên hạng phòng mới tương ứng từ list allRoomTypes để truyền riêng sang JSP
                        for (RoomStatusView type : allRoomTypes) {
                            if (type.getRoomTypeId() == displayRoomTypeId) {
                                request.setAttribute("newRoomTypeName", type.getRoomTypeName());
                                break;
                            }
                        }
                    }
                    
                    request.setAttribute("currentDisplayTypeId", displayRoomTypeId);
                    request.setAttribute("targetBooking", targetBooking);
                    request.setAttribute("targetBookingId", bookingId);

                    int assignedRoomsCount = bookingDAO.countRoomsAssigned(bookingId);
                    request.setAttribute("assignedRoomsCount", assignedRoomsCount);
                }
            } catch (Exception e) {
                System.out.println("Lỗi doGet AssignRoomController: " + e.getMessage());
            }
        }

        List<String> activeRoomTypes = roomDAO.getAllActiveRoomTypeNames();
        List<Integer> existingFloors = roomDAO.getAllExistingFloors();
        request.setAttribute("activeRoomTypes", activeRoomTypes);
        request.setAttribute("existingFloors", existingFloors);

        // Truyền displayRoomTypeId đã được phân tách luồng để load đúng danh sách phòng trống tương ứng
        List<RoomStatusView> roomMatrix = roomDAO.getAllRoomStatusViews(displayRoomTypeId, filterRoomTypeName, filterFloor);
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
            String selectedRoomParam = request.getParameter("selectedRoomId");

            if (selectedRoomParam == null || selectedRoomParam.isEmpty()) {
                request.getSession().setAttribute("notification", "error");
                request.getSession().setAttribute("notificationType", "error");
                response.sendRedirect(request.getContextPath() + "/assign-room?bookingId=" + bookingId);
                return;
            }

            int roomId = Integer.parseInt(selectedRoomParam);

            int adultsCount = Integer.parseInt(request.getParameter("currentRoomAdults"));
            int childrenCount = Integer.parseInt(request.getParameter("currentRoomChildren"));
            int totalGuestsInRoom = adultsCount + childrenCount; 

            String[] fullNames = request.getParameterValues("stayFullName");
            String[] phones = request.getParameterValues("stayPhone");
            String[] idNumbers = request.getParameterValues("stayIdNumber");

            var currentBooking = roomDAO.getBookingForCheckInById(bookingId);
            int totalRequiredRooms = (currentBooking != null) ? currentBooking.getNumRooms() : 1;

            // Lưu thông tin phòng và danh sách khách xuống Database
            boolean isSuccess = roomDAO.processRoomAssignment(bookingId, roomId, fullNames, phones, idNumbers, totalRequiredRooms);

            if (isSuccess) {
                request.getSession().setAttribute("notification", "success");
                request.getSession().setAttribute("notificationType", "success");

                // Đếm số lượng phòng thực tế đã gán thành công của đơn này
                int assignedCount = bookingDAO.countRoomsAssigned(bookingId);

                if (assignedCount < totalRequiredRooms) {
                    // Nếu đơn đặt nhiều phòng và chưa gán đủ, ở lại trang để lễ tân chọn tiếp phòng tiếp theo
                    response.sendRedirect(request.getContextPath() + "/assign-room?bookingId=" + bookingId);
                } else {
                    // Nếu đã gán đủ số lượng phòng của đơn, chuyển hướng về danh sách check-in chung
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