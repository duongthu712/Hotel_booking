package controller;

import dao.StaffAccountDAO;
import dal.PasswordUtil;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.StaffAccount;

/**
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-06-07
 */
public class StaffAccountCreateController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        if (staff == null) {
            response.sendRedirect("login");
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

        if (username == null || username.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Tên đăng nhập không được để trống.");
            retainFormFields(session, username, fullName, email, phone, role);
            response.sendRedirect(request.getContextPath() + "/StaffAccountList");
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Mật khẩu không được để trống.");
            retainFormFields(session, username, fullName, email, phone, role);
            response.sendRedirect(request.getContextPath() + "/StaffAccountList");
            return;
        }

        try {
            StaffAccountDAO staffDao = new StaffAccountDAO();

            if (staffDao.getStaffByUsername(username) != null) {
                session.setAttribute("errorMessage", "Tên đăng nhập đã tồn tại.");
                retainFormFields(session, username, fullName, email, phone, role);
                response.sendRedirect(request.getContextPath() + "/StaffAccountList");
                return;
            }

            if (staffDao.getStaffByEmail(email) != null) {
                session.setAttribute("errorMessage", "Email đã được sử dụng.");
                retainFormFields(session, username, fullName, email, phone, role);
                response.sendRedirect(request.getContextPath() + "/StaffAccountList");
                return;
            }

            String passwordHash = PasswordUtil.hashPassword(password);

            StaffAccount newStaff = new StaffAccount();
            newStaff.setUsername(username);
            newStaff.setPasswordHash(passwordHash);
            newStaff.setFullName(fullName);
            newStaff.setEmail(email);
            newStaff.setPhone(phone);
            newStaff.setRole(role);
            newStaff.setActive(true);

            staffDao.createStaff(newStaff);
            
            session.setAttribute("successMessage", "Tạo nhân viên mới thành công.");
            response.sendRedirect(request.getContextPath() + "/StaffAccountList");
            
        } catch (Exception e) {
            session.setAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
            retainFormFields(session, username, fullName, email, phone, role);
            response.sendRedirect(request.getContextPath() + "/StaffAccountList");
        }
    }

    private void retainFormFields(HttpSession session, String username, String fullName, String email, String phone, String role) {
        session.setAttribute("keepUsername", username);
        session.setAttribute("keepFullName", fullName);
        session.setAttribute("keepEmail", email);
        session.setAttribute("keepPhone", phone);
        session.setAttribute("keepRole", role);
        session.setAttribute("openCreateModal", true);
    }

    @Override
    public String getServletInfo() {
        return "Staff Management Controller";
    }
}