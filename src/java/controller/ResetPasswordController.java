package controller;

import dal.PasswordUtil;
import dao.StaffAccountDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ResetPasswordController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ResetPasswordController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ResetPasswordController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
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
        
        request.setCharacterEncoding("UTF-8");

        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("resetEmail") == null) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }

        String email = (String) session.getAttribute("resetEmail");

        if (newPassword == null || confirmPassword == null
                || newPassword.trim().isEmpty()
                || confirmPassword.trim().isEmpty()) {

            request.setAttribute("error", "Please fill in all fields.");
            request.getRequestDispatcher("/view/auth/reset-password.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match.");
            request.getRequestDispatcher("/view/auth/reset-password.jsp").forward(request, response);
            return;
        }

        if (newPassword.length() < 6) {
            request.setAttribute("error", "Password must be at least 6 characters.");
            request.getRequestDispatcher("/view/auth/reset-password.jsp").forward(request, response);
            return;
        }

        try {
            StaffAccountDAO dao = new StaffAccountDAO();

            String newPasswordHash = PasswordUtil.hashPassword(newPassword);

            dao.updatePasswordAndClearReset(email, newPasswordHash);

            session.removeAttribute("resetEmail");

            response.sendRedirect(request.getContextPath() + "/login?reset=success&showLogin=true");

        } catch (Exception e) {
            e.printStackTrace();

            request.setAttribute("error", "System error. Please try again.");
            request.getRequestDispatcher("/view/auth/reset-password.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Reset Password Controller";
    }
}