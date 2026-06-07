package controller;

import dao.StaffAccountDAO;
import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import model.StaffAccount;

/**
 * StaffAccountListController.java Display staff management page
 *
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-06-07
 */
public class StaffAccountListController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet StaffAccountListController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet StaffAccountListController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        if (staff == null) {
            response.sendRedirect("/view/auth/login.jsp");
            return;
        }

        // Get filter values
        String searchText = request.getParameter("searchText");
        String roleFilter = request.getParameter("roleFilter");

        if (searchText == null) {
            searchText = "";
        }
        if (roleFilter == null) {
            roleFilter = "ALL";
        }

        // Get page
        int page = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        int recordsPerPage = 10;

        StaffAccountDAO staffDao = new StaffAccountDAO();
        List<StaffAccount> staffList;

        // Filter data
        if ("ALL".equals(roleFilter) && searchText.isEmpty()) {
            staffList = staffDao.getStaffAccounts();
        } else {
            staffList = staffDao.searchStaff(searchText, "ALL".equals(roleFilter) ? null : roleFilter);
        }

        // Sort by ID ascending
        staffList.sort((a, b) -> Integer.compare(a.getStaffId(), b.getStaffId()));

        // Pagination
        int totalRecords = staffList.size();
        int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);

        if (page < 1) {
            page = 1;
        }
        if (page > totalPages && totalPages > 0) {
            page = totalPages;
        }

        int start = (page - 1) * recordsPerPage;
        int end = Math.min(start + recordsPerPage, totalRecords);

        List<StaffAccount> pagedList = staffList.subList(start, end);

        // Send data to jsp page
        request.setAttribute("staffList", pagedList);
        request.setAttribute("searchText", searchText);
        request.setAttribute("roleFilter", roleFilter);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        RequestDispatcher rd = request.getRequestDispatcher("/view/admin/staff-management.jsp");
        rd.forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Staff Management Controller";
    }
}
