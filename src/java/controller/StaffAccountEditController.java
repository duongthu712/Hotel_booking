package controller;

import dao.StaffAccountDAO;
import java.io.IOException;
import java.net.URLEncoder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import model.StaffAccount;

/**
 * @author LinhLTHE200306
 * @version 1.2
 * @since 2026-06-28
 */
public class StaffAccountEditController extends HttpServlet {

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
            int staffId = Integer.parseInt(request.getParameter("staffId"));

            StaffAccountDAO staffDao = new StaffAccountDAO();
            StaffAccount editStaff = staffDao.getStaffById(staffId);

            session.setAttribute("editStaff", editStaff);

            String page = request.getParameter("page");
            String searchText = request.getParameter("searchText");
            String roleFilter = request.getParameter("roleFilter");

            StringBuilder url = new StringBuilder(
                    request.getContextPath() + "/StaffAccountList?page="
                    + (page != null ? page : "1"));

            if (searchText != null && !searchText.trim().isEmpty()) {
                url.append("&searchText=")
                        .append(java.net.URLEncoder.encode(searchText.trim(), "UTF-8"));
            }

            if (roleFilter != null && !roleFilter.trim().isEmpty()) {
                url.append("&roleFilter=")
                        .append(java.net.URLEncoder.encode(roleFilter.trim(), "UTF-8"));
            }

            response.sendRedirect(url.toString());

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
            response.sendRedirect(request.getContextPath() + "/login");
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

        boolean isActive = "true".equals(activeStr);

        if (staff.getStaffId() == staffId && !staff.getRole().equals(role)) {
            forwardWithError(request, response, staffId,
                    "Bạn không thể tự thay đổi chức vụ của bản thân.",
                    fullName, email, phone, role, isActive, page, searchText, roleFilter);
            return;
        }

        if (staff.getStaffId() == staffId && !isActive) {
            forwardWithError(request, response, staffId,
                    "Bạn không thể tự khóa tài khoản của bản thân.",
                    fullName, email, phone, role, isActive, page, searchText, roleFilter);
            return;
        }

        String errorMsg = dal.InputValidationUtil.validateStaffInput(fullName, email, phone);
        if (errorMsg != null) {
            forwardWithError(request, response, staffId, errorMsg,
                    fullName, email, phone, role, isActive, page, searchText, roleFilter);
            return;
        }

        String formattedFullName = dal.InputValidationUtil.capitalizeWords(fullName);

        try {
            StaffAccountDAO staffDao = new StaffAccountDAO();
            StaffAccount existingStaff = staffDao.getStaffById(staffId);

            if (existingStaff == null) {
                forwardWithError(request, response, staffId,
                        "Không tìm thấy nhân viên.",
                        fullName, email, phone, role, isActive, page, searchText, roleFilter);
                return;
            }

            StaffAccount emailOwner = staffDao.getStaffByEmail(email.trim());
            if (emailOwner != null && emailOwner.getStaffId() != staffId) {
                forwardWithError(request, response, staffId,
                        "Email đã được sử dụng bởi nhân viên khác.",
                        fullName, email, phone, role, isActive, page, searchText, roleFilter);
                return;
            }

            StaffAccount updatedStaff = new StaffAccount();
            updatedStaff.setStaffId(staffId);
            updatedStaff.setUsername(existingStaff.getUsername());
            updatedStaff.setPasswordHash(existingStaff.getPasswordHash());
            updatedStaff.setFullName(formattedFullName);
            updatedStaff.setEmail(email.trim());
            updatedStaff.setPhone(phone != null ? phone.trim() : "");
            updatedStaff.setRole(role);
            updatedStaff.setActive(isActive);
            updatedStaff.setCreatedAt(existingStaff.getCreatedAt());
            updatedStaff.setResetCode(existingStaff.getResetCode());
            updatedStaff.setResetExpiry(existingStaff.getResetExpiry());
            updatedStaff.setResetUsed(existingStaff.isResetUsed());

            staffDao.updateStaffAcc(updatedStaff);
            session.setAttribute("successMessage", "Cập nhật nhân viên thành công.");

            response.sendRedirect(buildRedirectUrl(request, page, searchText, roleFilter));

        } catch (Exception e) {
            forwardWithError(request, response, staffId,
                    "Lỗi hệ thống: " + e.getMessage(),
                    fullName, email, phone, role, isActive, page, searchText, roleFilter);
        }
    }

    private void forwardWithError(HttpServletRequest request, HttpServletResponse response,
            int staffId, String errorMsg,
            String fullName, String email, String phone, String role, boolean isActive,
            String page, String searchText, String roleFilter)
            throws ServletException, IOException {

        try {
            StaffAccountDAO staffDao = new StaffAccountDAO();
            StaffAccount editStaff = staffDao.getStaffById(staffId);

            if (editStaff != null) {
                editStaff.setFullName(fullName);
                editStaff.setEmail(email);
                editStaff.setPhone(phone);
                editStaff.setRole(role);
                editStaff.setActive(isActive);
            }

            List<StaffAccount> staffList = staffDao.getAllStaffAcc();
            request.setAttribute("staffList", staffList);
            request.setAttribute("editStaff", editStaff);
        } catch (Exception e) {
        }

        request.setAttribute("errorMessage", errorMsg);
        request.setAttribute("currentPage", page);
        request.setAttribute("searchText", searchText);
        request.setAttribute("roleFilter", roleFilter);

        request.getRequestDispatcher("/StaffAccountList").forward(request, response);
    }

    private String buildRedirectUrl(HttpServletRequest request, String page, String searchText, String roleFilter) {
        StringBuilder url = new StringBuilder(request.getContextPath() + "/StaffAccountList");
        url.append("?page=").append(page != null ? page : "1");
        try {
            if (searchText != null && !searchText.trim().isEmpty()) {
                url.append("&searchText=").append(URLEncoder.encode(searchText.trim(), "UTF-8"));
            }
            if (roleFilter != null && !roleFilter.trim().isEmpty()) {
                url.append("&roleFilter=").append(URLEncoder.encode(roleFilter.trim(), "UTF-8"));
            }
        } catch (java.io.UnsupportedEncodingException e) {
        }
        return url.toString();
    }

    @Override
    public String getServletInfo() {
        return "Staff Management Controller";
    }
}
