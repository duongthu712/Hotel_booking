/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import model.Service;
import dao.HotelServiceDAO;
import dao.RoomServiceDAO;
import java.util.ArrayList;
import java.util.Map;
import model.ServiceType;
import model.StaffAccount;

/**
 *
 * @author admin
 */
public class ServiceListController extends HttpServlet {
   
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
            out.println("<title>Servlet ServiceListController</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ServiceListController at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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
        StaffAccount account = (StaffAccount) session.getAttribute("account");
        if (account == null) {
            response.sendRedirect("Login");
        } else {
            HotelServiceDAO hDao = new HotelServiceDAO();
            RoomServiceDAO rDao = new RoomServiceDAO();
            List<Service> hServices = hDao.getAllHotelServices();
            List<Service> rServices = rDao.getAllRoomServices();
            List<Service> services = new ArrayList<>();
            services.addAll(hServices);
            services.addAll(rServices);

            RequestDispatcher rd = request.getRequestDispatcher("views/manager/ServiceManagement.jsp");
            request.setAttribute("services", services);
            rd.forward(request, response);
        }
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
        StaffAccount account = (StaffAccount) session.getAttribute("account");
        if (account == null) {
            response.sendRedirect("Login");
        } else {
            HotelServiceDAO hDao = new HotelServiceDAO();
            RoomServiceDAO rDao = new RoomServiceDAO();
            List<Service> hServices = hDao.getAllHotelServices();
            List<Service> rServices = rDao.getAllRoomServices();
            List<Service> services = new ArrayList<>();
            services.addAll(hServices);
            services.addAll(rServices);

            RequestDispatcher rd = request.getRequestDispatcher("views/manager/ServiceManagement.jsp");
            request.setAttribute("accounts", accounts);
            request.setAttribute("roles", roles);
            request.setAttribute("searchText", searchText);
            request.setAttribute("roleId", roleId);
            rd.forward(request, response);
        }
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
