package controller;

import dao.RoomDAO;
import dao.RoomTypeDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Room;
import model.RoomType;
import model.StaffAccount;

/**
 * RoomEditController.java Update room information
 *
 * @author LinhLTHE200306
 * @version 2.0
 * @since 2026-06-10
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
            response.sendRedirect("login");
            return;
        }

        try {
            String roomNumberStr = request.getParameter("roomNumber");
            if (roomNumberStr == null || roomNumberStr.trim().isEmpty()) {
                throw new Exception("Số phòng không hợp lệ.");
            }
            int roomNumber = Integer.parseInt(roomNumberStr.trim());

            RoomDAO rDao = new RoomDAO();
            RoomTypeDAO rtDao = new RoomTypeDAO();

            Room editRoom = rDao.getRoomByNumber(roomNumber);
            List<RoomType> roomTypeList = rtDao.getAllRoomTypes();

            Map<Integer, String> roomTypeMap = new HashMap<>();
            for (RoomType rt : roomTypeList) {
                roomTypeMap.put(rt.getRoomTypeId(), rt.getTypeName());
            }

            request.setAttribute("editRoom", editRoom);
            request.setAttribute("roomTypeList", roomTypeList);
            request.setAttribute("roomTypeMap", roomTypeMap);
            request.setAttribute("currentPage", request.getParameter("page"));
            request.setAttribute("selectedRoomTypeId", request.getParameter("roomTypeId"));
            request.setAttribute("keyword", request.getParameter("keyword"));

            request.getRequestDispatcher("/view/manager/room-management.jsp").forward(request, response);

        } catch (Exception e) {
            session.setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(buildRedirectUrl(
                    request.getParameter("page"),
                    request.getParameter("roomTypeId"),
                    request.getParameter("keyword")));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        if (staff == null) {
            response.sendRedirect("login");
            return;
        }

        String page = request.getParameter("page");
        String filterRoomTypeId = request.getParameter("filterRoomTypeId");
        String keyword = request.getParameter("keyword");

        try {
            String roomNumberStr = request.getParameter("roomNumber");
            if (roomNumberStr == null || roomNumberStr.trim().isEmpty()) {
                session.setAttribute("errorMessage", "Số phòng không hợp lệ.");
                response.sendRedirect(buildRedirectUrl(page, filterRoomTypeId, keyword));
                return;
            }
            int roomNumber = Integer.parseInt(roomNumberStr.trim());
            
            String newStatus = request.getParameter("status");
            if (newStatus == null || newStatus.trim().isEmpty()) {
                session.setAttribute("errorMessage", "Trạng thái không được để trống.");
                response.sendRedirect(buildRedirectUrl(page, filterRoomTypeId, keyword));
                return;
            }
            if (!VALID_STATUSES.contains(newStatus)) {
                session.setAttribute("errorMessage", "Trạng thái không hợp lệ.");
                response.sendRedirect(buildRedirectUrl(page, filterRoomTypeId, keyword));
                return;
            }

            // Validate roomTypeId từ select
            String roomTypeIdStr = request.getParameter("roomTypeId");
            if (roomTypeIdStr == null || roomTypeIdStr.trim().isEmpty()) {
                session.setAttribute("errorMessage", "Hạng phòng không hợp lệ.");
                response.sendRedirect(buildRedirectUrl(page, filterRoomTypeId, keyword));
                return;
            }
            int roomTypeId = Integer.parseInt(roomTypeIdStr.trim());

            RoomDAO rDao = new RoomDAO();
            Room oldRoom = rDao.getRoomByNumber(roomNumber);
            String oldStatus = oldRoom.getStatus();

            if ("Phòng có khách".equals(oldStatus)) {
                session.setAttribute("errorMessage",
                        "Không thể thay đổi trạng thái phòng đang có khách.");
                response.sendRedirect(buildRedirectUrl(page, filterRoomTypeId, keyword));
                return;
            }

            if (!oldStatus.equals(newStatus)) {
                if ("Phòng có khách".equals(oldStatus)) {
                    session.setAttribute("errorMessage",
                            "Không thể thay đổi trạng thái phòng đang có khách.");
                    response.sendRedirect(buildRedirectUrl(page, filterRoomTypeId, keyword));
                    return;
                }

                if ("Phòng trống".equals(oldStatus)
                        && !"Đang dọn dẹp".equals(newStatus)
                        && !"Đang bảo trì".equals(newStatus)) {
                    session.setAttribute("errorMessage",
                            "Phòng trống chỉ có thể chuyển sang Đang dọn dẹp hoặc Đang bảo trì.");
                    response.sendRedirect(buildRedirectUrl(page, filterRoomTypeId, keyword));
                    return;
                }

                if ("Đang dọn dẹp".equals(oldStatus)
                        && !"Phòng trống".equals(newStatus)
                        && !"Đang bảo trì".equals(newStatus)) {
                    session.setAttribute("errorMessage",
                            "Phòng đang dọn dẹp chỉ có thể chuyển sang Phòng trống hoặc Đang bảo trì.");
                    response.sendRedirect(buildRedirectUrl(page, filterRoomTypeId, keyword));
                    return;
                }

                if ("Đang bảo trì".equals(oldStatus)
                        && !"Phòng trống".equals(newStatus)
                        && !"Đang dọn dẹp".equals(newStatus)) {
                    session.setAttribute("errorMessage",
                            "Phòng đang bảo trì chỉ có thể chuyển sang Phòng trống hoặc Đang dọn dẹp.");
                    response.sendRedirect(buildRedirectUrl(page, filterRoomTypeId, keyword));
                    return;
                }
            }

            if (oldRoom.getRoomTypeId() != roomTypeId && !"Đang bảo trì".equals(oldStatus)) {
                session.setAttribute("errorMessage",
                        "Chỉ được thay đổi hạng phòng khi phòng đang bảo trì.");
                response.sendRedirect(buildRedirectUrl(page, filterRoomTypeId, keyword));
                return;
            }

            Room room = new Room(roomNumber, oldRoom.getFloor(), newStatus, roomTypeId);
            rDao.updateRoom(room);

            session.setAttribute("successMessage", "Cập nhật phòng thành công.");

        } catch (Exception e) {
            session.setAttribute("errorMessage", e.getMessage());
        }

        response.sendRedirect(buildRedirectUrl(page, filterRoomTypeId, keyword));
    }

    private String buildRedirectUrl(String page, String filterRoomTypeId, String keyword) {
        StringBuilder url = new StringBuilder("RoomList");
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
