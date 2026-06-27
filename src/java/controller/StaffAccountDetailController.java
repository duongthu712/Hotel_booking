package controller;

import dao.StaffAccountDAO;
import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.StaffAccount;

/**
 * StaffAccountDetailController.java Display staff detail popup
 *
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-06-07
 */
public class StaffAccountDetailController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int staffId = Integer.parseInt(request.getParameter("staffId"));
            StaffAccountDAO staffDao = new StaffAccountDAO();

            StaffAccount selectedStaff = staffDao.getStaffAccById(staffId);

            request.setAttribute("selectedStaff", selectedStaff);
            RequestDispatcher rd = request.getRequestDispatcher("/StaffAccountList");
            rd.forward(request, response);

        } catch (Exception ex) {
            System.out.println("StaffAccountDetailController:" + ex.getMessage());
            response.sendRedirect("StaffAccountList");
        }
    }

    @Override
    public String getServletInfo() {
        return "Staff Management Controller";
    }// </editor-fold>

}
