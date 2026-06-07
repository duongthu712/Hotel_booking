package controller;

import dao.StaffAccountDAO;
import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.StaffAccount;

/**
 * StaffAccountEditController.java Update staff information
 *
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-06-07
 */
public class StaffAccountEditController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet StaffAccountEditController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet StaffAccountEditController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        if (staff == null) {
            response.sendRedirect("/view/auth/login.jsp");
            return;
        }

        try {
            int staffId = Integer.parseInt(request.getParameter("staffId"));
            StaffAccountDAO staffDao = new StaffAccountDAO();

            StaffAccount editStaff = staffDao.getStaffByIdIncludeInactive(staffId);

            request.setAttribute("editStaff", editStaff);
            RequestDispatcher rd = request.getRequestDispatcher("/StaffAccountList");
            rd.forward(request, response);

        } catch (Exception ex) {
            System.out.println("StaffAccountEditController:" + ex.getMessage());
            response.sendRedirect("StaffAccountList");
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        if (staff == null) {
            response.sendRedirect("/view/auth/login.jsp");
            return;
        }

        try {
            int staffId = Integer.parseInt(request.getParameter("staffId"));
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String role = request.getParameter("role");
            boolean active = "true".equals(request.getParameter("active"));

            StaffAccountDAO staffDao = new StaffAccountDAO();

            // Dùng getStaffByIdIncludeInactive để lấy cả inactive
            StaffAccount existingStaff = staffDao.getStaffByIdIncludeInactive(staffId);

            if (existingStaff == null) {
                session.setAttribute("error", "Không tìm thấy nhân viên.");
                response.sendRedirect("StaffAccountList");
                return;
            }

            StaffAccount updatedStaff = new StaffAccount();
            updatedStaff.setStaffId(staffId);
            updatedStaff.setUsername(existingStaff.getUsername());
            updatedStaff.setPasswordHash(existingStaff.getPasswordHash());
            updatedStaff.setFullName(fullName);
            updatedStaff.setEmail(email);
            updatedStaff.setPhone(phone);
            updatedStaff.setRole(role);
            updatedStaff.setActive(active);
            updatedStaff.setCreatedAt(existingStaff.getCreatedAt());
            updatedStaff.setResetCode(existingStaff.getResetCode());
            updatedStaff.setResetExpiry(existingStaff.getResetExpiry());
            updatedStaff.setResetUsed(existingStaff.isResetUsed());

            staffDao.updateStaff(updatedStaff);
            session.setAttribute("success", "Cập nhật nhân viên thành công.");

        } catch (Exception ex) {
            System.out.println("StaffAccountEditController:" + ex.getMessage());
            session.setAttribute("error", "Có lỗi xảy ra khi cập nhật nhân viên.");
        }
        response.sendRedirect("StaffAccountList");
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Staff Management Controller";
    }

}
