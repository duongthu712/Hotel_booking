package controller;

import dao.StaffAccountDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;
import model.StaffAccount;

/**
 * StaffAccountListController.java - Display staff management page
 *
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-06-07
 */
public class StaffAccountListController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String searchText = request.getParameter("searchText");
        String roleFilter = request.getParameter("roleFilter");

        if (searchText == null) {
            searchText = "";
        }
        if (roleFilter == null) {
            roleFilter = "ALL";
        }

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
        StaffAccountDAO staffDao = new StaffAccountDAO();

        try {
            List<StaffAccount> staffList;

            if (!searchText.trim().isEmpty() && !"ALL".equals(roleFilter)) {
                staffList = staffDao.searchStaffAccByName(searchText);
                staffList.addAll(staffDao.searchStaffAccByMail(searchText));
                final String filterRole = roleFilter;
                staffList = staffList.stream().filter(s -> filterRole.equals(s.getRole())).collect(Collectors.toList());
            } else if (!searchText.trim().isEmpty()) {
                staffList = staffDao.searchStaffAccByName(searchText);
                staffList.addAll(staffDao.searchStaffAccByMail(searchText));
            } else if (!"ALL".equals(roleFilter)) {
                staffList = staffDao.searchStaffAccByRole(roleFilter);
            } else {
                staffList = staffDao.getAllStaffAcc();
            }

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

            List<StaffAccount> pagedList;
            if (totalRecords > 0) {
                pagedList = staffList.subList(start, end);
            } else {
                pagedList = staffList;
            }

            request.setAttribute("staffList", pagedList);
            request.setAttribute("searchText", searchText);
            request.setAttribute("roleFilter", roleFilter);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);

            StaffAccount editStaff = (StaffAccount) session.getAttribute("editStaff");

            if (editStaff != null) {
                request.setAttribute("editStaff", editStaff);
                session.removeAttribute("editStaff");
            }

            request.getRequestDispatcher("/view/admin/staff-management.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/view/admin/staff-management.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Staff Management Controller";
    }
}
