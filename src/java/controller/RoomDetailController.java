package controller;

import dao.RoomDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import model.GuestStay;
import model.Room;
import model.StaffAccount;

/**
 * RoomDetailController.java Display room detail for manager
 *
 * @author LinhLTHE200306
 * @version 2.0
 * @since 2026-06-10
 */
public class RoomDetailController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        if (staff == null) {
            response.sendRedirect("view/auth/login.jsp");
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
            Room selectedRoom = rDao.getRoomByNumber(roomNumber);
            List<GuestStay> guestList = new ArrayList<>();

            if ("Phòng có khách".equals(selectedRoom.getStatus())) {
                guestList = rDao.getGuestsByRoomNumber(roomNumber);

            }

            session.setAttribute("selectedRoom", selectedRoom);
            session.setAttribute("guestList", guestList);

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
                url.append("&keyword=").append(URLEncoder.encode(keyword, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                url.append("&keyword=").append(keyword);
            }
        }

        return url.toString();
    }

    @Override
    public String getServletInfo() {
        return "Room Detail Controller";
    }
}
