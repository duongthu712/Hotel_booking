package controller;

import dao.StaffAccountDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import model.StaffAccount;

/**
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-06-07
 */
public class StaffAccountEditController extends HttpServlet {

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
            int staffId = Integer.parseInt(request.getParameter("staffId"));

            StaffAccountDAO staffDao = new StaffAccountDAO();
            StaffAccount editStaff = staffDao.getStaffAccById(staffId);
            List<StaffAccount> staffList = staffDao.getAllStaffAcc();

            request.setAttribute("staffList", staffList);
            request.setAttribute("editStaff", editStaff);
            request.setAttribute("page", request.getParameter("page"));
            request.setAttribute("searchText", request.getParameter("searchText"));
            request.setAttribute("roleFilter", request.getParameter("roleFilter"));
            request.getRequestDispatcher("/view/admin/staff-management.jsp").forward(request, response);
        } catch (Exception e) {
            session.setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/StaffAccountList");
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

        int staffId = Integer.parseInt(request.getParameter("staffId"));
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String role = request.getParameter("role");
        String activeStr = request.getParameter("active");

        String page = request.getParameter("page");
        String searchText = request.getParameter("searchText");
        String roleFilter = request.getParameter("roleFilter");

        if (staff.getStaffId() == staffId && !staff.getRole().equals(role)) {
            session.setAttribute("errorMessage", "Bạn không thể tự thay đổi chức vụ của bản thân.");
            response.sendRedirect(buildRedirectUrl(request, page, searchText, roleFilter));
            return;
        }

        boolean isActive = "true".equals(activeStr);

        try {
            StaffAccountDAO staffDao = new StaffAccountDAO();
            StaffAccount existingStaff = staffDao.getStaffAccById(staffId);

            if (existingStaff == null) {
                session.setAttribute("errorMessage", "Không tìm thấy nhân viên.");
                response.sendRedirect(buildRedirectUrl(request, page, searchText, roleFilter));
                return;
            }

            StaffAccount updatedStaff = new StaffAccount();
            updatedStaff.setStaffId(staffId);
            updatedStaff.setUsername(existingStaff.getUsername());
            updatedStaff.setPasswordHash(existingStaff.getPasswordHash());
            updatedStaff.setFullName(fullName);
            updatedStaff.setEmail(email);
            updatedStaff.setPhone(phone);
            updatedStaff.setRole(role);
            updatedStaff.setActive(isActive);
            updatedStaff.setCreatedAt(existingStaff.getCreatedAt());
            updatedStaff.setResetCode(existingStaff.getResetCode());
            updatedStaff.setResetExpiry(existingStaff.getResetExpiry());
            updatedStaff.setResetUsed(existingStaff.isResetUsed());

            staffDao.updateStaffAcc(updatedStaff);
            session.setAttribute("successMessage", "Cập nhật nhân viên thành công.");
        } catch (Exception e) {
            session.setAttribute("errorMessage", e.getMessage());
        }

        response.sendRedirect(buildRedirectUrl(request, page, searchText, roleFilter));
    }

    private String buildRedirectUrl(HttpServletRequest request, String page, String searchText, String roleFilter) {
        StringBuilder url = new StringBuilder(request.getContextPath() + "/StaffAccountList");
        url.append("?page=").append(page != null ? page : "1");
        if (searchText != null && !searchText.trim().isEmpty()) {
            try {
                url.append("&searchText=").append(java.net.URLEncoder.encode(searchText.trim(), "UTF-8"));
            } catch (java.io.UnsupportedEncodingException e) {
                url.append("&searchText=").append(searchText.trim());
            }
        }
        if (roleFilter != null && !roleFilter.trim().isEmpty()) {
            try {
                url.append("&roleFilter=").append(java.net.URLEncoder.encode(roleFilter.trim(), "UTF-8"));
            } catch (java.io.UnsupportedEncodingException e) {
                url.append("&roleFilter=").append(roleFilter.trim());
            }
        }
        return url.toString();
    }

    @Override
    public String getServletInfo() {
        return "Staff Management Controller";
    }
}