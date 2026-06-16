package controller;

import dao.DepositPaymentDAO;
import dal.EmailUtil;
import dao.BookingDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Booking;
import model.DepositPayment;
import model.StaffAccount;

/**
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-06-15
 */
public class DepositPaymentRejectController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String depositIdStr = request.getParameter("depositId");
        String notes = request.getParameter("notes");
        String page = request.getParameter("page");
        String keyword = request.getParameter("keyword");
        String status = request.getParameter("status");

        if (depositIdStr == null || depositIdStr.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Không tìm thấy khoản thanh toán cần từ chối");
            response.sendRedirect(buildRedirectUrl(request, page, keyword, status));
            return;
        }

        try {
            int depositId = Integer.parseInt(depositIdStr.trim());
            DepositPaymentDAO dpdao = new DepositPaymentDAO();
            BookingDAO bdao = new BookingDAO();
            DepositPayment payment = dpdao.getPaymentById(depositId);

            dpdao.rejectPayment(depositId, staff.getStaffId(), notes);

            //Send email
            try {
                String guestEmail = bdao.getGuestEmailByBookingId(payment.getBookingId());
                String guestName = bdao.getGuestNameByBookingId(payment.getBookingId());

                BookingDAO bookingDao = new BookingDAO();
                Booking booking = bookingDao.getBookingById(payment.getBookingId());
                String roomType = bookingDao.getRoomTypeNameById(booking.getRoomTypeId());
                String bedType = bookingDao.getBedTypeById(booking.getRoomTypeId());

                EmailUtil.sendDepositVerification(
                        guestEmail,
                        guestName,
                        booking.getBookingCode(),
                        roomType,
                        bedType,
                        booking.getCheckinDate(),
                        booking.getCheckoutDate(),
                        booking.getNumRooms(),
                        booking.getNumGuests(),
                        false,
                        notes
                );
            } catch (Exception e) {
                System.out.println("Gửi email thất bại: " + e.getMessage());
            }

            session.setAttribute("successMessage", "Hoàn tất từ chối thanh toán");
        } catch (Exception e) {
            session.setAttribute("errorMessage", e.getMessage());
        }

        response.sendRedirect(buildRedirectUrl(request, page, keyword, status));
    }

    private String buildRedirectUrl(HttpServletRequest request, String page, String keyword, String status) {
        StringBuilder url = new StringBuilder(request.getContextPath() + "/DepositPaymentList");
        url.append("?page=").append(page != null && !page.isEmpty() ? page : "1");

        if (keyword != null && !keyword.trim().isEmpty()) {
            try {
                url.append("&keyword=").append(java.net.URLEncoder.encode(keyword.trim(), "UTF-8"));
            } catch (java.io.UnsupportedEncodingException e) {
                url.append("&keyword=").append(keyword.trim());
            }
        }

        if (status != null && !status.trim().isEmpty() && !status.equals("all")) {
            url.append("&status=").append(status.trim());
        }

        return url.toString();
    }

    @Override
    public String getServletInfo() {
        return "Deposit Payment Reject Controller";
    }
}
