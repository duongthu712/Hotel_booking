package controller;

import dao.CheckoutDAO;
import java.io.IOException;
import java.time.LocalDate;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.StaffAccount;

/**
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-06-22
 */
public class BillingListController extends HttpServlet {

    private static final int RECORDS_PER_PAGE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            CheckoutDAO dao = new CheckoutDAO();

            String keyword = request.getParameter("keyword");
            String status = request.getParameter("status");
            String fromDateStr = request.getParameter("fromDate");
            String toDateStr = request.getParameter("toDate");

            String bookingIdParam = request.getParameter("bookingId");
            if ((keyword == null || keyword.trim().isEmpty())
                    && bookingIdParam != null && !bookingIdParam.trim().isEmpty()) {
                keyword = bookingIdParam.trim();
            }

            LocalDate fromDate = null;
            LocalDate toDate = null;
            try {
                if (fromDateStr != null && !fromDateStr.isEmpty()) {
                    fromDate = LocalDate.parse(fromDateStr);
                }
            } catch (Exception ignored) {
            }
            try {
                if (toDateStr != null && !toDateStr.isEmpty()) {
                    toDate = LocalDate.parse(toDateStr);
                }
            } catch (Exception ignored) {
            }

            int page = 1;
            try {
                String pageParam = request.getParameter("page");
                if (pageParam != null && !pageParam.isEmpty()) {
                    page = Integer.parseInt(pageParam);
                }
            } catch (NumberFormatException ignored) {
            }

            List<Map<String, Object>> allInvoices = dao.searchInvoices(keyword, fromDate, toDate, status);
            if (allInvoices == null) {
                allInvoices = new ArrayList<>();
            }

            int totalRecords = allInvoices.size();
            int totalPages = totalRecords == 0 ? 1
                    : (int) Math.ceil((double) totalRecords / RECORDS_PER_PAGE);

            if (page < 1) {
                page = 1;
            }
            if (page > totalPages) {
                page = totalPages;
            }

            int start = (page - 1) * RECORDS_PER_PAGE;
            int end = Math.min(start + RECORDS_PER_PAGE, totalRecords);
            List<Map<String, Object>> invoiceList = totalRecords > 0
                    ? allInvoices.subList(start, end)
                    : new ArrayList<>();

            Map<String, Object> selectedInvoice = null;
            String invoiceIdStr = request.getParameter("invoiceId");
            if (invoiceIdStr != null && !invoiceIdStr.isEmpty()) {
                try {
                    selectedInvoice = dao.getInvoiceDetailById(Integer.parseInt(invoiceIdStr));
                } catch (NumberFormatException ignored) {
                }
            }

            request.setAttribute("invoiceList", invoiceList);
            request.setAttribute("selectedInvoice", selectedInvoice);
            request.setAttribute("keyword", keyword);
            request.setAttribute("fromDate", fromDateStr);
            request.setAttribute("toDate", toDateStr);
            request.setAttribute("status", status);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalRecords", totalRecords);
            request.setAttribute("pageSize", RECORDS_PER_PAGE);

            request.getRequestDispatcher("/view/receptionist/billing.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/Checkout");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Billing List Controller";
    }
}
