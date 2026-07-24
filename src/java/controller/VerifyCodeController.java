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

public class VerifyCodeController extends HttpServlet {

    private static final int RESET_CODE_EXPIRY_MINUTES = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Hiển thị trang nhập mã khi phiên xác minh còn tồn tại.
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("pendingResetEmail") == null) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }

        Object verifyMessage = session.getAttribute("verifyMessage");

        if (verifyMessage != null) {
            request.setAttribute("message", verifyMessage.toString());
            session.removeAttribute("verifyMessage");
        }

        request.getRequestDispatcher("/view/auth/verify-code.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Xác minh mã hoặc xử lý yêu cầu gửi lại mã.
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("pendingResetEmail") == null) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }

        String email = (String) session.getAttribute("pendingResetEmail");
        String action = request.getParameter("action");

        StaffAccountDAO dao = new StaffAccountDAO();

        if ("resend".equalsIgnoreCase(action)) {
            handleResendCode(request, response, session, dao, email);
            return;
        }

        String code = request.getParameter("code");

        if (code == null || code.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập mã xác minh.");
            request.getRequestDispatcher("/view/auth/verify-code.jsp").forward(request, response);
            return;
        }

        code = code.trim();

        try {
            boolean valid = dao.isValidResetCode(email, code);

            if (!valid) {
                request.setAttribute("error", "Mã xác minh không hợp lệ hoặc đã hết hạn.");
                request.getRequestDispatcher("/view/auth/verify-code.jsp").forward(request, response);
                return;
            }

            session.setAttribute("resetEmail", email);
            session.removeAttribute("pendingResetEmail");
            session.removeAttribute("verifyMessage");

            response.sendRedirect(request.getContextPath() + "/reset-password");

        } catch (Exception exception) {
            exception.printStackTrace();

            request.setAttribute("error", "Không thể xác minh mã. Vui lòng thử lại.");
            request.getRequestDispatcher("/view/auth/verify-code.jsp").forward(request, response);
        }
    }

    private void handleResendCode(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, StaffAccountDAO dao, String email)
            throws ServletException, IOException {

        // Tạo mã mới, cập nhật thời gian hết hạn và gửi lại email.
        try {
            String newCode = generateCode();
            LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(RESET_CODE_EXPIRY_MINUTES);

            dao.saveResetCode(email, newCode, expiryTime);

            try {
                EmailUtil.sendResetCode(email, newCode);

                session.setAttribute("verifyMessage",
                        "Mã xác minh mới đã được gửi đến email của bạn.");

                response.sendRedirect(request.getContextPath() + "/verify-code");

            } catch (Exception mailError) {
                mailError.printStackTrace();

                request.setAttribute("error", "Không thể gửi email. Vui lòng thử lại sau.");
                request.getRequestDispatcher("/view/auth/verify-code.jsp").forward(request, response);
            }

        } catch (Exception exception) {
            exception.printStackTrace();

            request.setAttribute("error", "Không thể gửi lại mã. Vui lòng thử lại.");
            request.getRequestDispatcher("/view/auth/verify-code.jsp").forward(request, response);
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
        return "Verify Code Controller";
    }
}
