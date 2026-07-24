package controller;

import dal.EmailUtil;
import dao.StaffAccountDAO;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.StaffAccount;

public class ForgotPasswordController extends HttpServlet {

    private static final int RESET_CODE_EXPIRY_MINUTES = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Hiển thị trang quên mật khẩu.
        request.getRequestDispatcher("/view/auth/forgot-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Kiểm tra email, tạo mã xác minh và gửi mã cho nhân viên.
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");

        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập địa chỉ email.");
            request.getRequestDispatcher("/view/auth/forgot-password.jsp").forward(request, response);
            return;
        }

        email = email.trim();

        StaffAccountDAO dao = new StaffAccountDAO();
        HttpSession session = request.getSession();

        try {
            StaffAccount staff = dao.getStaffByEmail(email);

            if (staff == null) {
                session.removeAttribute("pendingResetEmail");
                session.removeAttribute("verifyMessage");

                request.setAttribute("error", "Email không tồn tại hoặc tài khoản đã bị khóa.");
                request.getRequestDispatcher("/view/auth/forgot-password.jsp").forward(request, response);
                return;
            }

            String code = generateCode();
            LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(RESET_CODE_EXPIRY_MINUTES);

            dao.saveResetCode(email, code, expiryTime);

            try {
                EmailUtil.sendResetCode(email, code);
            } catch (Exception mailError) {
                mailError.printStackTrace();

                session.removeAttribute("pendingResetEmail");
                session.removeAttribute("verifyMessage");

                request.setAttribute("error",
                        "Không thể gửi mã xác minh. Vui lòng kiểm tra cấu hình email và thử lại.");
                request.getRequestDispatcher("/view/auth/forgot-password.jsp").forward(request, response);
                return;
            }

            session.setAttribute("pendingResetEmail", email);
            session.setAttribute("verifyMessage", "Mã xác minh đã được gửi đến email của bạn.");

            response.sendRedirect(request.getContextPath() + "/verify-code");

        } catch (Exception exception) {
            exception.printStackTrace();

            session.removeAttribute("pendingResetEmail");
            session.removeAttribute("verifyMessage");

            request.setAttribute("error", "Lỗi hệ thống. Vui lòng thử lại.");
            request.getRequestDispatcher("/view/auth/forgot-password.jsp").forward(request, response);
        }
    }

    private String generateCode() {

        // Tạo mã xác minh gồm tiền tố LMH và 5 ký tự ngẫu nhiên.
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
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

        // Trả về mô tả của servlet.
        return "Forgot Password Controller";
    }
}
