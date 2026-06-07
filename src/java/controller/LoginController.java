package controller;

import dao.StaffAccountDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.PrintWriter;
import model.StaffAccount;

public class LoginController extends HttpServlet {
    
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
            out.println("<title>Servlet LoginController</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet LoginController at " + request.getContextPath () + "</h1>");
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

        StaffAccountDAO dao = new StaffAccountDAO();
        StaffAccount staff = dao.loginWithHashCheck(username, password);

        if (staff != null) {
            HttpSession session = request.getSession();
            //session remian in 1hr
            session.setMaxInactiveInterval(60 * 60);

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
        if (role.equalsIgnoreCase("Lễ tân")) {
            response.sendRedirect(request.getContextPath() + "/view/receptionist/dashboard.jsp");
        } else if (role.equalsIgnoreCase("Quản lý")) {
            response.sendRedirect(request.getContextPath() + "/view/manager/dashboard.jsp");
        } else if (role.equalsIgnoreCase("Quản trị viên")) {
            response.sendRedirect(request.getContextPath() + "/view/admin/staff-management.jsp");
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