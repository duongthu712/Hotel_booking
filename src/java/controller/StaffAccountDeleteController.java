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

/**
 * StaffAccountDeleteController.java Delete staff account (soft delete - set
 * is_active = 0)
 *
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-06-07
 */
public class StaffAccountDeleteController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        if (staff == null) {
            response.sendRedirect("login");
            return;
        }

        try {
            int staffId = Integer.parseInt(request.getParameter("staffId"));

            if (staffId == staff.getStaffId()) {
                session.setAttribute("errorMessage", "Không thể xoá tài khoản của chính mình.");
                response.sendRedirect("StaffAccountList");
                return;
            }

            StaffAccountDAO staffDao = new StaffAccountDAO();
            staffDao.deleteStaffAcc(staffId);

            session.setAttribute("successMessage", "Xoá nhân viên thành công.");

        } catch (Exception ex) {
            System.out.println("StaffAccountDeleteController:" + ex.getMessage());
            session.setAttribute("errorMessage", ex.getMessage());
        }
        response.sendRedirect("StaffAccountList");
    }

   
    @Override
    public String getServletInfo() {
        return "Staff Management Controller";
    }

}
