package controller;

import dao.StaffAccountDAO;
import dal.PasswordUtil;
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
 * StaffAccountCreateController.java
 * Create new staff account
 *
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-06-07
 */

public class StaffAccountCreateController extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
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
            out.println("<title>Servlet StaffAccountCreateController</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet StaffAccountCreateController at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    } 

    /** 
     * Handles the HTTP <code>GET</code> method.
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

        RequestDispatcher rd = request.getRequestDispatcher("/view/admin/create-staff-account.jsp");
        rd.forward(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
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
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String role = request.getParameter("role");

            StaffAccountDAO staffDao = new StaffAccountDAO();

            // Check duplicate
            if (staffDao.getStaffByUsername(username) != null) {
                request.setAttribute("error", "Tên đăng nhập đã tồn tại.");
                request.setAttribute("username", username);
                request.setAttribute("fullName", fullName);
                request.setAttribute("email", email);
                request.setAttribute("phone", phone);
                request.setAttribute("role", role);
                RequestDispatcher rd = request.getRequestDispatcher("/view/admin/create-staff-account.jsp");
                rd.forward(request, response);
                return;
            }

            if (staffDao.getStaffByEmail(email) != null) {
                request.setAttribute("error", "Email đã được sử dụng.");
                request.setAttribute("username", username);
                request.setAttribute("fullName", fullName);
                request.setAttribute("email", email);
                request.setAttribute("phone", phone);
                request.setAttribute("role", role);
                RequestDispatcher rd = request.getRequestDispatcher("/view/admin/create-staff-account.jsp");
                rd.forward(request, response);
                return;
            }

            // Hash password with SHA-256 using PasswordUtil
            String passwordHash = PasswordUtil.hashPassword(password);

            StaffAccount newStaff = new StaffAccount();
            newStaff.setUsername(username);
            newStaff.setPasswordHash(passwordHash);
            newStaff.setFullName(fullName);
            newStaff.setEmail(email);
            newStaff.setPhone(phone);
            newStaff.setRole(role);
            newStaff.setActive(true);

            staffDao.createStaff(newStaff);
            session.setAttribute("success", "Tạo nhân viên mới thành công.");

        } catch (Exception ex) {
            System.out.println("StaffAccountCreateController:" + ex.getMessage());
            session.setAttribute("error", "Có lỗi xảy ra khi tạo nhân viên.");
        }
        response.sendRedirect("StaffAccountList");
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Staff Management Controller";
    }// </editor-fold>

}
