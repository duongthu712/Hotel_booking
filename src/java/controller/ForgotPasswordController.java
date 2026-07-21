package controller;

import dal.EmailUtil;
import dao.StaffAccountDAO;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.StaffAccount;

public class ForgotPasswordController extends HttpServlet {

    private static final int RESET_CODE_EXPIRY_MINUTES = 10;

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        RequestDispatcher dispatcher
                = request.getRequestDispatcher("/view/auth/forgot-password.jsp");

        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        HttpSession session = request.getSession();

        // Xóa thông báo cũ
        session.removeAttribute("resetMessage");

        if (email == null || email.trim().isEmpty()) {
            request.setAttribute(
                    "error",
                    "Vui lòng nhập email nhân viên."
            );

            request.getRequestDispatcher("/view/auth/forgot-password.jsp")
                    .forward(request, response);
            return;
        }

        email = email.trim();

        StaffAccountDAO dao = new StaffAccountDAO();

        try {
            // Tìm tài khoản theo email
            StaffAccount staff = dao.getStaffByEmail(email);

            if (staff == null) {
                session.removeAttribute("pendingResetEmail");

                request.setAttribute(
                        "error",
                        "Email không tồn tại hoặc tài khoản đã bị khóa."
                );

                request.getRequestDispatcher("/view/auth/forgot-password.jsp")
                        .forward(request, response);
                return;
            }

            // Tạo mã xác minh
            String resetCode = generateCode();

            LocalDateTime expiryTime = LocalDateTime.now()
                    .plusMinutes(RESET_CODE_EXPIRY_MINUTES);

            // Lưu mã và thời gian hết hạn vào database
            dao.saveResetCode(email, resetCode, expiryTime);

            // Gửi mã qua email
            try {
                EmailUtil.sendResetCode(email, resetCode);
            } catch (Exception mailException) {
                mailException.printStackTrace();

                request.setAttribute(
                        "error",
                        "Không thể gửi mã xác minh. Vui lòng thử lại."
                );

                request.getRequestDispatcher("/view/auth/forgot-password.jsp")
                        .forward(request, response);
                return;
            }

            // Lưu email cần reset vào session
            session.setAttribute("pendingResetEmail", email);

            // Lưu thông báo để trang verify-code hiển thị
            session.setAttribute(
                    "resetMessage",
                    "Mã xác minh đã được gửi đến email của bạn."
            );

            // Chuyển qua controller xác minh mã
            response.sendRedirect(
                    request.getContextPath() + "/verify-code"
            );
            return;

        } catch (Exception exception) {
            exception.printStackTrace();

            request.setAttribute(
                    "error",
                    "Lỗi hệ thống. Vui lòng thử lại."
            );

            request.getRequestDispatcher("/view/auth/forgot-password.jsp")
                    .forward(request, response);
        }
    }

    private String generateCode() {
        String characters
                = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "abcdefghijklmnopqrstuvwxyz"
                + "0123456789";

        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder("LMH");

        for (int i = 0; i < 5; i++) {
            int index = random.nextInt(characters.length());
            code.append(characters.charAt(index));
        }

        return code.toString();
    }

    @Override
    public String getServletInfo() {
        return "Forgot Password Controller";
    }
}
