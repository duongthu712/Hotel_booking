package controller;

import dao.HotelInfoDAO;
import dal.InputValidationUtil;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalTime;
import model.HotelInfo;
import model.StaffAccount;

/**
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-06-14
 */
public class HotelInfoUpdateController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int hotelId = 1;
        String hotelName = request.getParameter("hotelName");
        String description = request.getParameter("description");
        String checkinTime = request.getParameter("checkinTime");
        String checkoutTime = request.getParameter("checkoutTime");
        String address = request.getParameter("address");
        String addressUrl = request.getParameter("addressUrl");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");

        if (hotelName == null || hotelName.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Tên khách sạn không được để trống.");
            response.sendRedirect(request.getContextPath() + "/HotelInfo");
            return;
        }
        if (address == null || address.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Địa chỉ không được để trống.");
            response.sendRedirect(request.getContextPath() + "/HotelInfo");
            return;
        }
        if (phone == null || phone.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Số điện thoại không được để trống.");
            response.sendRedirect(request.getContextPath() + "/HotelInfo");
            return;
        }
        if (email == null || email.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Email không được để trống.");
            response.sendRedirect(request.getContextPath() + "/HotelInfo");
            return;
        }

        String errorMsg = InputValidationUtil.validateHotelInput(email, phone);
        if (errorMsg != null) {
            session.setAttribute("errorMessage", errorMsg);
            response.sendRedirect(request.getContextPath() + "/HotelInfo");
            return;
        }

        HotelInfo hotelInfo = new HotelInfo();
        hotelInfo.setHotelId(hotelId);
        hotelInfo.setHotelName(hotelName != null ? hotelName.trim() : null);
        hotelInfo.setDescription(description != null ? description.trim() : null);
        hotelInfo.setCheckinTime((checkinTime != null && !checkinTime.isEmpty()) ? LocalTime.parse(checkinTime) : LocalTime.of(14, 0));
        hotelInfo.setCheckoutTime((checkoutTime != null && !checkoutTime.isEmpty()) ? LocalTime.parse(checkoutTime) : LocalTime.of(12, 0));
        hotelInfo.setAddress(address != null ? address.trim() : null);
        hotelInfo.setAddressUrl(addressUrl != null && !addressUrl.trim().isEmpty() ? addressUrl.trim() : null);
        hotelInfo.setPhone(phone != null ? phone.trim() : null);
        hotelInfo.setEmail(email != null ? email.trim() : null);

        try {
            HotelInfoDAO dao = new HotelInfoDAO();
            dao.updateHotelInfo(hotelInfo);
            session.setAttribute("successMessage", "Cập nhật thông tin khách sạn thành công.");
            session.setAttribute("hotelInfo", hotelInfo);
        } catch (Exception e) {
            session.setAttribute("errorMessage", e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/HotelInfo");
    }

    @Override
    public String getServletInfo() {
        return "Hotel Info Update Controller";
    }
}
