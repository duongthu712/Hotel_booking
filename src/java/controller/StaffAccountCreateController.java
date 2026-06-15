package controller;

import dao.StaffAccountDAO;
import dal.PasswordUtil;
import java.io.IOException;
import java.net.URLEncoder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.StaffAccount;

/**
 * @author LinhLTHE200306
 * @version 1.1
 * @since 2026-06-13
 */
public class StaffAccountCreateController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        response.sendRedirect(request.getContextPath() + "/StaffAccountList");
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

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String role = request.getParameter("role");

        String pageParam = request.getParameter("page");
        String searchTextFilter = request.getParameter("searchText");
        String roleFilterParam = request.getParameter("roleFilter");

        if (pageParam == null || pageParam.trim().isEmpty()) pageParam = "1";
        if (searchTextFilter == null) searchTextFilter = "";
        if (roleFilterParam == null) roleFilterParam = "ALL";

        String errorMsg = null;

        if (username == null || username.trim().isEmpty()) {
            errorMsg = "Tên đăng nhập không được để trống.";
        } else if (username.trim().length() < 3 || username.trim().length() > 20) {
            errorMsg = "Tên đăng nhập phải từ 3 đến 20 ký tự.";
        } else if (!username.trim().matches("^[a-zA-Z0-9._]+$")) {
            errorMsg = "Tên đăng nhập chỉ được chứa chữ cái, số, dấu chấm hoặc dấu gạch dưới.";
        } 
        
        else if (password == null || password.isEmpty()) {
            errorMsg = "Mật khẩu không được để trống.";
        } else if (password.length() < 6) {
            errorMsg = "Mật khẩu phải có ít nhất 6 ký tự.";
        }

        if (errorMsg == null) {
            errorMsg = dal.InputValidationUtil.validateStaffInput(fullName, email, phone);
        }

        String formattedFullName = dal.InputValidationUtil.capitalizeWords(fullName);

        if (errorMsg != null) {
            forwardWithError(request, response, errorMsg, username, formattedFullName, email, phone, role, 
                             pageParam, searchTextFilter, roleFilterParam);
            return;
        }
        
        try {
            StaffAccountDAO staffDao = new StaffAccountDAO();

            if (staffDao.getStaffByUsername(username.trim()) != null) {
                forwardWithError(request, response, "Tên đăng nhập đã tồn tại.", username, formattedFullName, email, phone, role, 
                                 pageParam, searchTextFilter, roleFilterParam);
                return;
            }

            if (staffDao.getStaffByEmail(email.trim()) != null) {
                forwardWithError(request, response, "Email đã được sử dụng.", username, formattedFullName, email, phone, role, 
                                 pageParam, searchTextFilter, roleFilterParam);
                return;
            }

            String passwordHash = PasswordUtil.hashPassword(password);

            StaffAccount newStaff = new StaffAccount();
            newStaff.setUsername(username.trim());
            newStaff.setPasswordHash(passwordHash);
            newStaff.setFullName(formattedFullName); 
            newStaff.setEmail(email.trim());
            newStaff.setPhone(phone.trim());
            newStaff.setRole(role);
            newStaff.setActive(true);

            staffDao.createStaff(newStaff);

            session.setAttribute("successMessage", "Tạo nhân viên mới thành công.");
            
            String redirectUrl = request.getContextPath() + "/StaffAccountList"
                    + "?page=" + pageParam
                    + "&searchText=" + URLEncoder.encode(searchTextFilter, "UTF-8")
                    + "&roleFilter=" + URLEncoder.encode(roleFilterParam, "UTF-8");
            
            response.sendRedirect(redirectUrl);

        } catch (Exception e) {
            forwardWithError(request, response, "Lỗi hệ thống: " + e.getMessage(), username, formattedFullName, email, phone, role, 
                             pageParam, searchTextFilter, roleFilterParam);
        }
    }

    private void forwardWithError(HttpServletRequest request, HttpServletResponse response, String errorMsg,
                                  String username, String fullName, String email, String phone, String role,
                                  String page, String searchText, String roleFilter) 
                                  throws ServletException, IOException {
        
        request.setAttribute("errorMessage", errorMsg);
        request.setAttribute("keepUsername", username);
        request.setAttribute("keepFullName", fullName);
        request.setAttribute("keepEmail", email);
        request.setAttribute("keepPhone", phone);
        request.setAttribute("keepRole", role);
        request.setAttribute("openCreateModal", true);request.setAttribute("currentPage", page);
        request.setAttribute("searchText", searchText);
        request.setAttribute("roleFilter", roleFilter);

        request.getRequestDispatcher("/StaffAccountList").forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Staff Management Controller";
    }
}