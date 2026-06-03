package controller;

import dal.EmailUtil;
import dao.StaffAccountDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Random;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class VerifyCodeController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet VerifyCodeController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet VerifyCodeController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

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

        String action = request.getParameter("action");
        String email = (String) session.getAttribute("pendingResetEmail");

        StaffAccountDAO dao = new StaffAccountDAO();

        if ("resend".equals(action)) {
            try {
                String newCode = generateCode();
                LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(10);

                dao.saveResetCode(email, newCode, expiryTime);

                try {
                    EmailUtil.sendResetCode(email, newCode);
                    request.setAttribute("message", "A new reset code has been sent to your email.");
                } catch (Exception mailError) {
                    mailError.printStackTrace();

                    // Dòng này giúp demo tiếp nếu mail lỗi
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
        int code = 100000 + new Random().nextInt(900000);
        return String.valueOf(code);
    }

    @Override
    public String getServletInfo() {
        return "Verify Code Controller";
    }
}