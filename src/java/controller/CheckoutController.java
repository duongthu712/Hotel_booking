package controller;

import dao.CheckoutDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.StaffAccount;

/**
 * @author LinhLTHE200306
 * @version 3.0
 * @since 2026-06-30
 */
public class CheckoutController extends HttpServlet {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");

        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String keyword = request.getParameter("keyword");

        try {
            CheckoutDAO dao = new CheckoutDAO();
            List<Map<String, Object>> roomList = dao.getRoomsForCheckout(keyword);

            // Format date cho từng room
            for (Map<String, Object> room : roomList) {
                Object checkinDate = room.get("checkinDate");
                if (checkinDate instanceof LocalDate) {
                    room.put("checkinDate", ((LocalDate) checkinDate).format(DATE_FORMATTER));
                }
                Object checkoutDate = room.get("checkoutDate");
                if (checkoutDate instanceof LocalDate) {
                    room.put("checkoutDate", ((LocalDate) checkoutDate).format(DATE_FORMATTER));
                }
            }

            request.setAttribute("roomList", roomList);
            request.setAttribute("keyword", keyword);
            request.setAttribute("today", LocalDate.now().format(DATE_FORMATTER));

            request.getRequestDispatcher("/view/receptionist/check-out.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/view/receptionist/check-out.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");

        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Nhận 1 roomId duy nhất từ form
        String selectedRoomId = request.getParameter("selectedRoom");
        if (selectedRoomId == null || selectedRoomId.isEmpty()) {
            session.setAttribute("errorMessage", "Vui lòng chọn một phòng để checkout.");
            response.sendRedirect(request.getContextPath() + "/Checkout");
            return;
        }

        CheckoutDAO dao = new CheckoutDAO();

        try {
            int roomId = Integer.parseInt(selectedRoomId);

            // Lấy thông tin phòng để biết bookingId
            List<Map<String, Object>> roomDetails = dao.getRoomDetailsByRoomIds(
                    java.util.Collections.singletonList(roomId));
            if (roomDetails.isEmpty()) {
                session.setAttribute("errorMessage", "Không tìm thấy thông tin phòng.");
                response.sendRedirect(request.getContextPath() + "/Checkout");
                return;
            }

            int bookingId = (Integer) roomDetails.get(0).get("bookingId");

            // Lưu roomId đang checkout vào session
            session.setAttribute("checkoutRoomId_" + bookingId, roomId);

            try {
                dao.processCheckout(bookingId, java.util.Collections.singletonList(roomId), staff.getStaffId());
                session.setAttribute("checkoutRoomCount_" + bookingId, 1);
                response.sendRedirect(request.getContextPath() + "/InvoiceCreate?bookingId=" + bookingId);
            } catch (Exception ex) {
                ex.printStackTrace();
                String bookingCode = dao.getBookingCodeById(bookingId);
                session.setAttribute("errorMessage", "Lỗi checkout đơn " + bookingCode + ": " + ex.getMessage());
                response.sendRedirect(request.getContextPath() + "/Checkout");
            }

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/Checkout");
        }
    }
}
