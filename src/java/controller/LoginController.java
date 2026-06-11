package controller;

import dao.StaffAccountDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.StaffAccount;

public class LoginController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet LoginController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet LoginController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String showLogin = request.getParameter("showLogin");

        if ("true".equals(showLogin)) {
            request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("staff") != null) {
            StaffAccount staff = (StaffAccount) session.getAttribute("staff");
            redirectByRole(request, response, staff);
            return;
        }

        request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        System.out.println("===== LOGIN DEBUG =====");
        System.out.println("username = " + username);
        System.out.println("password = " + password);

        StaffAccountDAO dao = new StaffAccountDAO();
        StaffAccount staff = dao.loginWithHashCheck(username, password);

        System.out.println("staff = " + staff);

        if (staff != null) {
            HttpSession session = request.getSession();

            session.setAttribute("staff", staff);
            session.setAttribute("staffId", staff.getStaffId());
            session.setAttribute("staffRole", staff.getRole());

            redirectByRole(request, response, staff);
            return;
        }

        request.setAttribute("error", "Invalid username or password");
        request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
    }

    private void redirectByRole(HttpServletRequest request, HttpServletResponse response, StaffAccount staff)
            throws ServletException, IOException {

        String role = staff.getRole();

        if (role == null) {
            request.setAttribute("error", "Your account does not have a valid role.");
            request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
            return;
        }

        role = role.trim();

        if (role.equalsIgnoreCase("Receptionist") || role.equalsIgnoreCase("Lễ tân")) {
            response.sendRedirect(request.getContextPath() + "/view/receptionist/dashboard.jsp");

        } else if (role.equalsIgnoreCase("Manager") || role.equalsIgnoreCase("Quản lý")) {
            response.sendRedirect(request.getContextPath() + "/view/manager/dashboard.jsp");

        } else if (role.equalsIgnoreCase("Administrator")
                || role.equalsIgnoreCase("Admin")
                || role.equalsIgnoreCase("Quản trị viên")) {
            response.sendRedirect(request.getContextPath() + "/StaffAccountList");

        } else {
            request.setAttribute("error", "Invalid role: " + role);
            request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Login Controller";
    }
}