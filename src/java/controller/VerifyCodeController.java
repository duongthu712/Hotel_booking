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

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("pendingResetEmail") == null) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }

        request.getRequestDispatcher("/view/auth/verify-code.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("pendingResetEmail") == null) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }

        String email = (String) session.getAttribute("pendingResetEmail");
        String action = request.getParameter("action");

        StaffAccountDAO dao = new StaffAccountDAO();

        if ("resend".equalsIgnoreCase(action)) {
            try {
                String newCode = generateCode();
                LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(RESET_CODE_EXPIRY_MINUTES);

                dao.saveResetCode(email, newCode, expiryTime);

                try {
                    EmailUtil.sendResetCode(email, newCode);
                    request.setAttribute("message", "Mã xác minh mới đã được gửi đến email của bạn.");
                } catch (Exception mailError) {
                    mailError.printStackTrace();
                    request.setAttribute("error", "Không thể gửi email. Vui lòng thử lại sau.");
                }

                request.getRequestDispatcher("/view/auth/verify-code.jsp").forward(request, response);
                return;

            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Không thể gửi lại mã. Vui lòng thử lại.");
                request.getRequestDispatcher("/view/auth/verify-code.jsp").forward(request, response);
                return;
            }
        }

        String code = request.getParameter("code");

        if (code == null || code.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập mã xác minh.");
            request.getRequestDispatcher("/view/auth/verify-code.jsp").forward(request, response);
            return;
        }

        code = code.trim();

        boolean valid = dao.isValidResetCode(email, code);

        if (!valid) {
            request.setAttribute("error", "Mã xác minh không hợp lệ hoặc đã hết hạn.");
            request.getRequestDispatcher("/view/auth/verify-code.jsp").forward(request, response);
            return;
        }

        session.setAttribute("resetEmail", email);
        session.removeAttribute("pendingResetEmail");

        response.sendRedirect(request.getContextPath() + "/reset-password");
    }

    private String generateCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder("LMH");

        for (int i = 0; i < 5; i++) {
            int index = random.nextInt(chars.length());
            code.append(chars.charAt(index));
        }

        return code.toString();
    }

    @Override
    public String getServletInfo() {
        return "Verify Code Controller";
    }
}
