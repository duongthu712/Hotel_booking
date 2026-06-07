package controller;

import dal.EmailUtil;
import dao.StaffAccountDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Random;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.StaffAccount;

public class ForgotPasswordController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ForgotPasswordController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ForgotPasswordController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        RequestDispatcher rd = request.getRequestDispatcher("/view/auth/forgot-password.jsp");
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        String email = request.getParameter("email");
        StaffAccountDAO dao = new StaffAccountDAO();

        try {
            StaffAccount staff = dao.getStaffByEmail(email);
            HttpSession session = request.getSession();

            if (staff == null) {
                session.removeAttribute("pendingResetEmail");

                request.setAttribute("error", "Email does not exist or account is inactive.");
                request.getRequestDispatcher("/view/auth/forgot-password.jsp").forward(request, response);
                return;
            }

            String code = generateCode();
            LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(10);
            dao.saveResetCode(email, code, expiryTime);
            
            try {
                EmailUtil.sendResetCode(email, code);
                request.setAttribute("message", "A reset code has been sent to your email.");
            } catch (Exception mailError) {
                mailError.printStackTrace();
            }
            session.setAttribute("pendingResetEmail", email);
            request.getRequestDispatcher("/view/auth/verify-code.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "System error. Please try again.");
            request.getRequestDispatcher("/view/auth/forgot-password.jsp").forward(request, response);
        }
    }

    private String generateCode() {
        int code = 100000 + new Random().nextInt(900000);
        return String.valueOf(code);
    }

    @Override
    public String getServletInfo() {
        return "Forgot Password Controller";
    }
}