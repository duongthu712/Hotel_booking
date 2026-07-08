package controller;

import dao.StaffAccountDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.StaffAccount;

public class LoginController extends HttpServlet {

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

        StaffAccountDAO dao = new StaffAccountDAO();
        StaffAccount staff = dao.loginWithHashCheck(username, password);
        
        if (staff != null) {
            if (!staff.isActive()) {
                request.setAttribute("error", "Tài khoản đã bị khóa.");
                request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
                return;
            }

            HttpSession session = request.getSession();
            session.setMaxInactiveInterval(60 * 60);

            session.setAttribute("staff", staff);
            session.setAttribute("staffId", staff.getStaffId());
            session.setAttribute("staffRole", staff.getRole());

            redirectByRole(request, response, staff);
            return;
        }

        request.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng.");
        request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
    }

    private void redirectByRole(HttpServletRequest request, HttpServletResponse response, StaffAccount staff)
            throws ServletException, IOException {

        String role = staff.getRole();

        if (role.equalsIgnoreCase("Lễ tân")) {
            response.sendRedirect(request.getContextPath() + "/receptionist-dashboard");
        } else if (role.equalsIgnoreCase("Quản lý")) {
            response.sendRedirect(request.getContextPath() + "/ManagerDashboard");

        } else if (role.equalsIgnoreCase("Administrator")
                || role.equalsIgnoreCase("Admin")
                || role.equalsIgnoreCase("Quản trị viên")) {
            response.sendRedirect(request.getContextPath() + "/StaffAccountList");

        } else {
            request.setAttribute("error", "Vai trò không hợp lệ: " + role);
            request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Login Controller";
    }
}
