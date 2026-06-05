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
import java.math.BigDecimal;
import model.Service;
import model.ServiceType;
import model.StaffAccount;

/**
 * ServiceEditController.java Edit hotel services and room services.
 *
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-06-03
 */
public class ServiceEditController extends HttpServlet {

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
            out.println("<title>Servlet ServiceEditController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ServiceEditController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

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
        processRequest(request, response);
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
        StaffAccount account = (StaffAccount) session.getAttribute("account");
        if (account == null) {
            response.sendRedirect("Login");
            return;
        }
        
        //Parse input
        int serviceId = Integer.parseInt(request.getParameter("serviceId"));
        String serviceName = request.getParameter("serviceName");
        String description = request.getParameter("description");
        BigDecimal unitPrice = new BigDecimal(request.getParameter("unitPrice"));
        boolean active = Boolean.parseBoolean(request.getParameter("active"));
        ServiceType type = ServiceType.valueOf(request.getParameter("type"));

        //Create new Service object with updated details
        Service service = new Service(serviceId, serviceName, description, unitPrice, active, type);

        //Update service in database by its type
        HotelServiceDAO hDao = new HotelServiceDAO();
        RoomServiceDAO rDao = new RoomServiceDAO();

        if (ServiceType.HOTEL.equals(type)) {
            hDao.updateHotelService(service);
        } else if (ServiceType.ROOM.equals(type)) {
            rDao.updateRoomService(service);
        }
        
        //Redirect back to the service list page
        response.sendRedirect("serviceList");
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
