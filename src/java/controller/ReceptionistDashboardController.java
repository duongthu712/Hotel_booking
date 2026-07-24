/**
 * Author: ThuDNM-HE204370
 * Date created: 04/06/2026
 * Purpose: Controller logic for ReceptionistDashboardController.
 */
package controller;

import dao.ReceptionistDashboardDAO;
import dto.ReceptionistDashboard;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Minh Thu
 */
@WebServlet(name = "ReceptionistDashboardController", urlPatterns = {"/receptionist-dashboard"})
public class ReceptionistDashboardController extends HttpServlet {
   
   
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ReceptionistDashboardController</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ReceptionistDashboardController at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    } 

  
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
       ReceptionistDashboardDAO dashboardDAO = new ReceptionistDashboardDAO();
       ReceptionistDashboard dashboardData = dashboardDAO.getDashboardDataToday();
       request.setAttribute("dashboardData", dashboardData);
       request.getRequestDispatcher("/view/receptionist/dashboard.jsp").forward(request, response);
    } 

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

   
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
