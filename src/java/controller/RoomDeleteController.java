package controller;

import dao.RoomDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.Room;
import model.StaffAccount;

/**
 * RoomDeleteController.java
 * Soft delete room (set is_active = 0)
 * @author LinhLTHE200306
 * @since 2026-06-28
 */
public class RoomDeleteController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");

        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String page = request.getParameter("page");
        String roomTypeId = request.getParameter("roomTypeId");
        String keyword = request.getParameter("keyword");

        try {
            String roomNumberStr = request.getParameter("roomNumber");

            if (roomNumberStr == null || roomNumberStr.trim().isEmpty()) {
                throw new Exception("Số phòng không hợp lệ.");
            }

            int roomNumber = Integer.parseInt(roomNumberStr.trim());

            RoomDAO rDao = new RoomDAO();
            Room room = rDao.getRoomByNumber(roomNumber);

            if (room == null) {
                throw new Exception("Phòng không tồn tại.");
            }

            if ("Phòng có khách".equals(room.getStatus())) {
                throw new Exception("Không thể xoá phòng đang có khách lưu trú.");
            }

            boolean deleted = rDao.deleteRoom(room.getRoomId());

            if (!deleted) {
                throw new Exception("Xoá phòng thất bại.");
            }
            session.setAttribute("successMessage", "Xoá phòng " + roomNumber + " thành công.");
        } catch (Exception e) {
            session.setAttribute("errorMessage", e.getMessage());
        }

        response.sendRedirect(buildRedirectUrl(page, roomTypeId, keyword));
    }

    private String buildRedirectUrl(String page, String roomTypeId, String keyword) {
        StringBuilder url = new StringBuilder("RoomList");
        url.append("?page=").append(page != null && !page.isEmpty() ? page : "1");

        if (roomTypeId != null && !roomTypeId.isEmpty()) {
            url.append("&roomTypeId=").append(roomTypeId);
        }

        if (keyword != null && !keyword.isEmpty()) {
            try {
                url.append("&keyword=").append(java.net.URLEncoder.encode(keyword, "UTF-8"));
            } catch (Exception e) {
                url.append("&keyword=").append(keyword);
            }
        }

        return url.toString();
    }

    @Override
    public String getServletInfo() {
        return "Room Delete Controller";
    }
}