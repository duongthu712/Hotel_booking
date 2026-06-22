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

    private static final int DEFAULT_PAGE_SIZE = 10;

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

            LocalDate fromDate = (fromDateStr != null && !fromDateStr.isEmpty())
                    ? LocalDate.parse(fromDateStr) : null;
            LocalDate toDate = (toDateStr != null && !toDateStr.isEmpty())
                    ? LocalDate.parse(toDateStr) : null;

            // Phân trang
            int page = 1;
            List<Map<String, Object>> allInvoices = dao.searchInvoices(keyword, fromDate, toDate, status);
            if (allInvoices == null) {
                allInvoices = new ArrayList<>();
            }

            int totalRecords = allInvoices.size();
            int recordsPerPage = 10;
            int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);

            if (page < 1) {
                page = 1;
            }
            if (page > totalPages && totalPages > 0) {
                page = totalPages;
            }

            int start = (page - 1) * recordsPerPage;
            int end = Math.min(start + recordsPerPage, totalRecords);

            List<Map<String, Object>> invoiceList = totalRecords > 0
                    ? allInvoices.subList(start, end)
                    : allInvoices;

            
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
            request.setAttribute("invoiceList", invoiceList);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalRecords", totalRecords);
            request.setAttribute("pageSize", recordsPerPage);

            request.getRequestDispatcher("/view/receptionist/billing.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/Checkout");
        }
    }
}
