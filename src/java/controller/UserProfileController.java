package controller;

import dao.StaffAccountDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.StaffAccount;

public class UserProfileController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("staff") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.getRequestDispatcher("/view/auth/user-profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("staff") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");

        if (fullName == null || fullName.trim().isEmpty()
                || email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Full name and email are required.");
            request.getRequestDispatcher("/view/auth/user-profile.jsp").forward(request, response);
            return;
        }

        fullName = fullName.trim();
        email = email.trim();
        phone = phone != null ? phone.trim() : "";
        StaffAccountDAO dao = new StaffAccountDAO();

        dao.updateProfile(staff.getStaffId(), fullName, email, phone);
        StaffAccount updatedStaff = dao.getStaffById(staff.getStaffId());

        if (updatedStaff != null) {
            session.setAttribute("staff", updatedStaff);
            session.setAttribute("staffId", updatedStaff.getStaffId());
            session.setAttribute("staffRole", updatedStaff.getRole());
            request.setAttribute("message", "Profile updated successfully.");
        } else {
            request.setAttribute("error", "Cannot update profile.");
        }

        request.getRequestDispatcher("/view/auth/user-profile.jsp").forward(request, response);
    }
}