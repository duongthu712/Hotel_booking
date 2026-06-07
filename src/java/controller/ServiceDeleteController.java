package controller;

import dao.HotelServiceDAO;
import dao.RoomServiceDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.ServiceType;
import model.StaffAccount;

/**
 * ServiceDeleteController.java Delete hotel services and room services.
 *
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-06-05
 */
public class ServiceDeleteController extends HttpServlet {

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
            out.println("<title>Servlet ServiceDeleteController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ServiceDeleteController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
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
        //Check authentication, redirect to login page if not logged in
        HttpSession session = request.getSession();
        StaffAccount  staff = (StaffAccount) session.getAttribute("staff");
        if ( staff == null) {
            response.sendRedirect("Login");
            return;
        }

        //Parse input parameters
        int serviceId = Integer.parseInt(request.getParameter("serviceId"));
        ServiceType type = ServiceType.valueOf(request.getParameter("type"));

        HotelServiceDAO hDao = new HotelServiceDAO();
        RoomServiceDAO rDao = new RoomServiceDAO();

        //Perform deletion by service type
        if (ServiceType.HOTEL.equals(type)) {
            hDao.delete(serviceId);
        } else if (ServiceType.ROOM.equals(type)) {
            rDao.delete(serviceId);
        }

        //Redirect to the list view after operation
        response.sendRedirect("ServiceList");
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Service Management Controller";
    }

}
