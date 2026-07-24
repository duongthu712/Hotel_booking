package controller;

import dal.PasswordUtil;
import dao.StaffAccountDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ResetPasswordController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Hiển thị trang đặt lại mật khẩu khi người dùng đã xác minh mã.
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("resetEmail") == null) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }

        request.getRequestDispatcher("/view/auth/reset-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Kiểm tra và cập nhật mật khẩu mới cho tài khoản.
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("resetEmail") == null) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }

        String email = (String) session.getAttribute("resetEmail");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        newPassword = newPassword != null ? newPassword.trim() : "";
        confirmPassword = confirmPassword != null ? confirmPassword.trim() : "";

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ mật khẩu mới.");
            request.getRequestDispatcher("/view/auth/reset-password.jsp").forward(request, response);
            return;
        }

        if (newPassword.length() < 6) {
            request.setAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự.");
            request.getRequestDispatcher("/view/auth/reset-password.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp.");
            request.getRequestDispatcher("/view/auth/reset-password.jsp").forward(request, response);
            return;
        }

        try {
            String newPasswordHash = PasswordUtil.hashPassword(newPassword);

            StaffAccountDAO dao = new StaffAccountDAO();
            dao.updatePasswordAndClearReset(email, newPasswordHash);

            session.removeAttribute("resetEmail");
            session.removeAttribute("pendingResetEmail");

            session.setAttribute("loginSuccessMessage",
                    "Đặt lại mật khẩu thành công. Vui lòng đăng nhập lại.");

            response.sendRedirect(request.getContextPath() + "/login");

        } catch (Exception exception) {
            exception.printStackTrace();

            request.setAttribute("error", "Không thể đặt lại mật khẩu. Vui lòng thử lại.");
            request.getRequestDispatcher("/view/auth/reset-password.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {

        // Trả về mô tả của servlet.
        return "Reset Password Controller";
    }
}
