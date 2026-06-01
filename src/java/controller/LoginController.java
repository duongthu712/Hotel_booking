package controller;

import dao.StaffDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import model.StaffAccount;

public class LoginController extends HttpServlet {

    private final StaffDAO staffDAO = new StaffDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("logout".equals(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) session.invalidate();

            response.sendRedirect(request.getContextPath()
                    + "/view/auth/login.jsp?msg=Logged out successfully");
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

        // authenticate trực tiếp từ DAO
        StaffAccount user = staffDAO.authenticateStaff(username, password);

        if (user != null) {

            HttpSession session = request.getSession(true);
            session.setAttribute("staffUser", user);

            String role = user.getRole();

            if ("Administrator".equals(role)) {
                response.sendRedirect(request.getContextPath() + "/view/admin/staff-management.jsp");

            } else if ("Manager".equals(role)) {
                response.sendRedirect(request.getContextPath() + "/view/manager/dashboard.jsp");

            } else if ("Receptionist".equals(role)) {
                response.sendRedirect(request.getContextPath() + "/view/receptionist/dashboard.jsp");

            } else {
                session.invalidate();
                response.sendRedirect(request.getContextPath() + "/view/auth/login.jsp?msg=Invalid role");
            }

        } else {
            request.setAttribute("errorMessage",
                "Invalid username, password or inactive account.");
            request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
        }
    }
}