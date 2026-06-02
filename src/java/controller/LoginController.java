package controller;

import dao.StaffAccountDAO;
import model.StaffAccount;
import dal.PasswordUtil;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LoginController extends HttpServlet {

    private final StaffAccountDAO staffDAO = new StaffAccountDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            StaffAccount staff = staffDAO.getStaffByEmail(email);

            if (staff == null || !PasswordUtil.checkPassword(password, staff.getPasswordHash())) {
                request.setAttribute("error", "Invalid email or password.");
                request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
                return;
            }

            HttpSession session = request.getSession();
            session.setAttribute("staff", staff);

            response.sendRedirect(request.getContextPath() + "/staff/dashboard.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "System error. Please try again.");
            request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
        }
    }
}