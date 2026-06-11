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

public class ChangePasswordController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("staff") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        StaffAccount staff = (StaffAccount) session.getAttribute("staff");

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        currentPassword = currentPassword != null ? currentPassword.trim() : "";
        newPassword = newPassword != null ? newPassword.trim() : "";
        confirmPassword = confirmPassword != null ? confirmPassword.trim() : "";

        request.setAttribute("showPasswordForm", true);

        if (currentPassword.isEmpty()
                || newPassword.isEmpty()
                || confirmPassword.isEmpty()) {
            request.setAttribute("passwordError", "Vui lòng nhập đầy đủ các trường mật khẩu.");
            request.getRequestDispatcher("/view/auth/user-profile.jsp").forward(request, response);
            return;
        }

        if (newPassword.length() < 6) {
            request.setAttribute("passwordError", "Mật khẩu mới phải có ít nhất 6 ký tự.");
            request.getRequestDispatcher("/view/auth/user-profile.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("passwordError", "Mật khẩu xác nhận không khớp.");
            request.getRequestDispatcher("/view/auth/user-profile.jsp").forward(request, response);
            return;
        }

        StaffAccountDAO dao = new StaffAccountDAO();

        StaffAccount checkedStaff = dao.loginWithHashCheck(staff.getUsername(), currentPassword);

        if (checkedStaff == null) {
            request.setAttribute("passwordError", "Mật khẩu hiện tại không đúng.");
            request.getRequestDispatcher("/view/auth/user-profile.jsp").forward(request, response);
            return;
        }

        if (currentPassword.equals(newPassword)) {
            request.setAttribute("passwordError", "Mật khẩu mới không được trùng với mật khẩu hiện tại.");
            request.getRequestDispatcher("/view/auth/user-profile.jsp").forward(request, response);
            return;
        }

        String newPasswordHash = PasswordUtil.hashPassword(newPassword);
        boolean updated = dao.updatePasswordByStaffId(staff.getStaffId(), newPasswordHash);

        if (!updated) {
            request.setAttribute("passwordError", "Không thể đổi mật khẩu. Vui lòng thử lại.");
            request.getRequestDispatcher("/view/auth/user-profile.jsp").forward(request, response);
            return;
        }

        request.removeAttribute("showPasswordForm");
        request.setAttribute("passwordMessage", "Đổi mật khẩu thành công.");
        request.getRequestDispatcher("/view/auth/user-profile.jsp").forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Change Password Controller";
    }
}