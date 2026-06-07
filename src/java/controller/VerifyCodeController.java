package controller;

import dal.EmailUtil;
import dao.StaffAccountDAO;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.security.SecureRandom;

public class VerifyCodeController extends HttpServlet {

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

        //resend code
        if ("resend".equalsIgnoreCase(action)) {
            try {
                String newCode = generateCode();
                LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(10);
                dao.saveResetCode(email, newCode, expiryTime);

                try {
                    EmailUtil.sendResetCode(email, newCode);
                    request.setAttribute("message", "A new reset code has been sent to your email.");
                } catch (Exception mailError) {
                    mailError.printStackTrace();
                    //mail loi
                    request.setAttribute("message", "Cannot send email. Demo code: " + newCode);
                }
                request.getRequestDispatcher("/view/auth/verify-code.jsp").forward(request, response);
                return;

            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Cannot resend code. Please try again.");
                request.getRequestDispatcher("/view/auth/verify-code.jsp").forward(request, response);
                return;
            }
        }
        String code = request.getParameter("code");

        if (code == null || code.trim().isEmpty()) {
            request.setAttribute("error", "Please enter verification code.");
            request.getRequestDispatcher("/view/auth/verify-code.jsp").forward(request, response);
            return;
        }

        code = code.trim();
        boolean valid = dao.isValidResetCode(email, code);
        if (!valid) {
            request.setAttribute("error", "Invalid or expired code.");
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