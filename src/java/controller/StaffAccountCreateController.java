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
            response.sendRedirect("view/auth/login.jsp");
            return;
        }

        request.getRequestDispatcher("/view/admin/create-staff-account.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        if (staff == null) {
            response.sendRedirect("view/auth/login.jsp");
            return;
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String role = request.getParameter("role");

        if (username == null || username.trim().isEmpty()) {
            request.setAttribute("error", "Tên đăng nhập không được để trống.");
            retainFormFields(request, username, fullName, email, phone, role);
            request.getRequestDispatcher("/view/admin/create-staff-account.jsp").forward(request, response);
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Mật khẩu không được để trống.");
            retainFormFields(request, username, fullName, email, phone, role);
            request.getRequestDispatcher("/view/admin/create-staff-account.jsp").forward(request, response);
            return;
        }

        try {
            StaffAccountDAO staffDao = new StaffAccountDAO();

            if (staffDao.getStaffByUsername(username) != null) {
                request.setAttribute("error", "Tên đăng nhập đã tồn tại.");
                retainFormFields(request, username, fullName, email, phone, role);
                request.getRequestDispatcher("/view/admin/create-staff-account.jsp").forward(request, response);
                return;
            }

            if (staffDao.getStaffByEmail(email) != null) {
                request.setAttribute("error", "Email đã được sử dụng.");
                retainFormFields(request, username, fullName, email, phone, role);
                request.getRequestDispatcher("/view/admin/create-staff-account.jsp").forward(request, response);
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
            request.setAttribute("error", e.getMessage());
            retainFormFields(request, username, fullName, email, phone, role);
            request.getRequestDispatcher("/view/admin/create-staff-account.jsp").forward(request, response);
        }
    }

    private void retainFormFields(HttpServletRequest request, String username, String fullName, String email, String phone, String role) {
        request.setAttribute("username", username);
        request.setAttribute("fullName", fullName);
        request.setAttribute("email", email);
        request.setAttribute("phone", phone);
        request.setAttribute("role", role);
    }

    @Override
    public String getServletInfo() {
        return "Staff Management Controller";
    }
}