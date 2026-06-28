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
import model.StaffAccount;
import dal.InputValidationUtil;

/**
 * @author LinhLTHE200306
 * @version 1.1
 * @since 2026-06-28
 */
public class RoomCreateController extends HttpServlet {

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

        String roomNumberStr = request.getParameter("roomNumber");
        String floorStr = request.getParameter("floor");
        String roomTypeIdStr = request.getParameter("roomTypeId");

        session.setAttribute("keepRoomNumber", roomNumberStr);
        session.setAttribute("keepFloor", floorStr);
        session.setAttribute("keepRoomTypeId", roomTypeIdStr);

        try {
            if (roomNumberStr == null || roomNumberStr.trim().isEmpty()) {
                throw new Exception("Số phòng không được để trống.");
            }
            int roomNumber = Integer.parseInt(roomNumberStr.trim());
            if (roomNumber <= 0) {
                throw new Exception("Số phòng phải là số dương.");
            }

            if (floorStr == null || floorStr.trim().isEmpty()) {
                throw new Exception("Tầng không được để trống.");
            }
            int floor = Integer.parseInt(floorStr.trim());
            if (floor <= 0) {
                throw new Exception("Tầng phải là số dương.");
            }

            if (roomTypeIdStr == null || roomTypeIdStr.trim().isEmpty()) {
                throw new Exception("Hạng phòng không được để trống.");
            }
            int roomTypeId = Integer.parseInt(roomTypeIdStr.trim());

            
            RoomDAO rDao = new RoomDAO();
            RoomTypeDAO rtDao = new RoomTypeDAO();
            String error = InputValidationUtil.validateCreateRoom(roomNumber, floor, roomTypeId, rDao);

            if (error != null) {
                session.setAttribute("errorMessage", error);
                session.setAttribute("openCreateModal", true);
                response.sendRedirect(buildRedirectUrl(request.getContextPath(), page, filterRoomTypeId, keyword));
                return;
            }

            rDao.createRoom(roomNumber, floor, roomTypeId);

            session.removeAttribute("keepRoomNumber");
            session.removeAttribute("keepFloor");
            session.removeAttribute("keepRoomTypeId");

            session.setAttribute("successMessage", "Tạo phòng " + roomNumber + " thành công.");

        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Số phòng, tầng và hạng phòng phải là số hợp lệ.");
            session.setAttribute("openCreateModal", true);
        } catch (Exception e) {
            session.setAttribute("errorMessage", e.getMessage());
            session.setAttribute("openCreateModal", true);
        }

        response.sendRedirect(buildRedirectUrl(request.getContextPath(), page, filterRoomTypeId, keyword));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/RoomList");
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
        return "Room Create Controller";
    }
}