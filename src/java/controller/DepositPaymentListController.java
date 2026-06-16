package controller;

import dao.BookingDAO;
import dao.DepositPaymentDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.DepositPayment;
import model.StaffAccount;

/**
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-06-15
 */
public class DepositPaymentListController extends HttpServlet {

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
        String status = request.getParameter("status");

        int page = 1;
        try {
            String pageParam = request.getParameter("page");
            if (pageParam != null) {
                page = Integer.parseInt(pageParam);
            }
        } catch (NumberFormatException e) {
            page = 1;
        }

        int recordsPerPage = 10;

        try {
            DepositPaymentDAO dpdao = new DepositPaymentDAO();
            BookingDAO bdao = new BookingDAO();
            List<DepositPayment> allPayments;

            if (keyword != null && !keyword.trim().isEmpty()) {
                allPayments = dpdao.searchPayments(keyword.trim());
            } else if (status == null || status.isEmpty()) {
                allPayments = dpdao.getPendingPayments();
            } else {
                allPayments = dpdao.getAllPaymentsByStatus(status);
            }

            if (allPayments == null) {
                allPayments = new ArrayList<>();
            }

            // Create maps for bookingCode and guestName
            Map<Integer, String> bookingCodeMap = new HashMap<>();
            Map<Integer, String> guestNameMap = new HashMap<>();
            
            for (DepositPayment payment : allPayments) {
                int bookingId = payment.getBookingId();
                if (!bookingCodeMap.containsKey(bookingId)) {
                    String bookingCode = bdao.getBookingCodeByBookingId(bookingId);
                    String guestName = bdao.getGuestNameByBookingId(bookingId);
                    bookingCodeMap.put(bookingId, bookingCode);
                    guestNameMap.put(bookingId, guestName);
                }
            }

            // Pagination
            int totalRecords = allPayments.size();
            int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);
            if (page < 1) page = 1;
            if (page > totalPages && totalPages > 0) page = totalPages;

            int start = (page - 1) * recordsPerPage;
            int end = Math.min(start + recordsPerPage, totalRecords);
            List<DepositPayment> pagedList;

            if (totalRecords > 0) {
                pagedList = allPayments.subList(start, end);
            } else {
                pagedList = new ArrayList<>();
            }

            request.setAttribute("paymentList", pagedList);
            request.setAttribute("bookingCodeMap", bookingCodeMap);
            request.setAttribute("guestNameMap", guestNameMap);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages > 0 ? totalPages : 1);
            request.setAttribute("keyword", keyword);
            request.setAttribute("status", status);

            request.getRequestDispatcher("/view/receptionist/payment-verification.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
            request.setAttribute("paymentList", new ArrayList<>());
            request.setAttribute("currentPage", 1);
            request.setAttribute("totalPages", 1);
            request.setAttribute("keyword", keyword);
            request.setAttribute("status", status);
            
            request.getRequestDispatcher("/view/receptionist/payment-verification.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Deposit Payment List Controller";
    }
}