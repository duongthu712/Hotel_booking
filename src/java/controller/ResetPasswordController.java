package controller;

import dao.StaffAccountDAO;
import dal.PasswordUtil;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ResetPasswordController extends HttpServlet {

<<<<<<< Updated upstream
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>ResetPasswordController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>ResetPasswordController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

=======
>>>>>>> Stashed changes
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

<<<<<<< Updated upstream
            request.setAttribute("error", "Vui lòng nhập đầy đủ thông tin.");
            request.getRequestDispatcher("/view/auth/reset-password.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp.");
=======
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ mật khẩu mới.");
>>>>>>> Stashed changes
            request.getRequestDispatcher("/view/auth/reset-password.jsp").forward(request, response);
            return;
        }

        if (newPassword.length() < 6) {
<<<<<<< Updated upstream
            request.setAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự.");
=======
            request.setAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự.");
>>>>>>> Stashed changes
            request.getRequestDispatcher("/view/auth/reset-password.jsp").forward(request, response);
            return;
        }

<<<<<<< Updated upstream
        try {
            StaffAccountDAO dao = new StaffAccountDAO();

            String newPasswordHash = PasswordUtil.hashPassword(newPassword);

            dao.updatePasswordAndClearReset(email, newPasswordHash);

            session.removeAttribute("resetEmail");

            response.sendRedirect(request.getContextPath() + "/login?reset=success&showLogin=true");

        } catch (Exception e) {
            e.printStackTrace();

            request.setAttribute("error", "Lỗi hệ thống. Vui lòng thử lại.");
=======
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp.");
>>>>>>> Stashed changes
            request.getRequestDispatcher("/view/auth/reset-password.jsp").forward(request, response);
            return;
        }

        String newPasswordHash = PasswordUtil.hashPassword(newPassword);

        StaffAccountDAO dao = new StaffAccountDAO();
        dao.updatePasswordAndClearReset(email, newPasswordHash);

        session.removeAttribute("resetEmail");

        response.sendRedirect(request.getContextPath() + "/login?reset=success");
    }

    @Override
    public String getServletInfo() {
        return "Reset Password Controller";
    }
}