package controller;

import dal.InputValidationUtil;
import dao.RoomDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import model.Room;
import model.StaffAccount;

/**
 * RoomEditController.java Update room information
 *
 * @author LinhLTHE200306
 * @version 2.2
 * @since 2026-06-28
 */
public class RoomEditController extends HttpServlet {

    private static final List<String> VALID_STATUSES = Arrays.asList(
            "Phòng trống", "Phòng có khách", "Đang dọn dẹp", "Đang bảo trì"
    );

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
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

            RoomDAO rDao = new RoomDAO();
            Room editRoom = rDao.getRoomByNumber(Integer.parseInt(roomNumberStr.trim()));
            session.setAttribute("editRoom", editRoom);

        } catch (Exception e) {
            session.setAttribute("errorMessage", e.getMessage());
        }

        response.sendRedirect(buildRedirectUrl(request.getContextPath(), page, roomTypeId, keyword));
    }

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
        String filterRoomTypeId = request.getParameter("filterRoomTypeId");
        String keyword = request.getParameter("keyword");

        try {
            // Parse input
            String roomNumberStr = request.getParameter("roomNumber");
            if (roomNumberStr == null || roomNumberStr.trim().isEmpty()) {
                throw new Exception("Số phòng không hợp lệ.");
            }
            int roomNumber = Integer.parseInt(roomNumberStr.trim());

            String newRoomNumberStr = request.getParameter("newRoomNumber");
            int newRoomNumber = (newRoomNumberStr != null && !newRoomNumberStr.trim().isEmpty())
                    ? Integer.parseInt(newRoomNumberStr.trim())
                    : roomNumber;

            String newStatus = request.getParameter("status");
            if (newStatus == null || newStatus.trim().isEmpty()) {
                throw new Exception("Trạng thái không được để trống.");
            }
            if (!VALID_STATUSES.contains(newStatus.trim())) {
                throw new Exception("Trạng thái không hợp lệ.");
            }

            String roomTypeIdStr = request.getParameter("roomTypeId");
            if (roomTypeIdStr == null || roomTypeIdStr.trim().isEmpty()) {
                throw new Exception("Hạng phòng không hợp lệ.");
            }
            int roomTypeId = Integer.parseInt(roomTypeIdStr.trim());

            RoomDAO rDao = new RoomDAO();
            Room oldRoom = rDao.getRoomByNumber(roomNumber);

            String error = InputValidationUtil.validateEditRoom(roomNumber, newRoomNumber, oldRoom.getFloor(), oldRoom.getStatus(), newStatus.trim(), oldRoom.getRoomTypeId(), roomTypeId, rDao);

            if (error != null) {
                throw new Exception(error);
            }

            Room updatedRoom = new Room(oldRoom.getRoomId(), newRoomNumber, oldRoom.getFloor(), newStatus.trim(), roomTypeId);
            rDao.updateRoom(oldRoom.getRoomId(), updatedRoom);

            session.setAttribute("successMessage", "Cập nhật phòng " + roomNumber + " thành công.");

        } catch (Exception e) {
            session.setAttribute("errorMessage", e.getMessage());
        }

        response.sendRedirect(buildRedirectUrl(request.getContextPath(), page, filterRoomTypeId, keyword));
    }

    private String buildRedirectUrl(String contextPath, String page, String filterRoomTypeId, String keyword) {
        StringBuilder url = new StringBuilder(contextPath + "/RoomList");
        url.append("?page=").append(page != null && !page.isEmpty() ? page : "1");

        if (filterRoomTypeId != null && !filterRoomTypeId.isEmpty()) {
            url.append("&roomTypeId=").append(filterRoomTypeId);
        }
        if (keyword != null && !keyword.isEmpty()) {
            try {
                url.append("&keyword=").append(URLEncoder.encode(keyword, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                url.append("&keyword=").append(keyword);
            }
        }

        return url.toString();
    }

    @Override
    public String getServletInfo() {
        return "Room Edit Controller";
    }
}