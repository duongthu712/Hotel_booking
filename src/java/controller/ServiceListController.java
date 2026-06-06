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
import model.StaffAccount;

/**
 * ServiceListController.java List hotel services and room services.
 *
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-06-03
 */
public class ServiceListController extends HttpServlet {

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
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ServiceListController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ServiceListController at " + request.getContextPath() + "</h1>");
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
        //Check authentication, redirect to login page if not logged in
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        if (staff == null) {
            response.sendRedirect("/view/auth/login.jsp");
            return;
        }

        //Specify filter type and page (default is "ALL")
        String filterType = request.getParameter("filterType");
        if (filterType == null) {
            filterType = "ALL";
        }

        int page = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        int recordsPerPage = 10;

        HotelServiceDAO hDao = new HotelServiceDAO();
        RoomServiceDAO rDao = new RoomServiceDAO();
        List<Service> serviceList = new ArrayList<>();

        //Get data from database
        List<Service> hServices = hDao.getAllHotelServices();
        List<Service> rServices = rDao.getAllRoomServices();

        //Filter data by filterType
        if ("HOTEL".equals(filterType)) {
            serviceList.addAll(hServices);
        } else if ("ROOM".equals(filterType)) {
            serviceList.addAll(rServices);
        } else {
            //Default list all if filterType is "ALL"
            serviceList.addAll(hServices);
            serviceList.addAll(rServices);
        }

        //Cut list 10 item in 1 page
        int totalRecords = serviceList.size();
        int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);

        if (page < 1) {
            page = 1;
        }
        if (page > totalPages && totalPages > 0) {
            page = totalPages;
        }

        int start = (page - 1) * recordsPerPage;
        int end = Math.min(start + recordsPerPage, totalRecords);

        // Danh sách đã cắt để hiển thị
        List<Service> pagedList = serviceList.subList(start, end);

        //Send data to jsp page
        request.setAttribute("serviceList", pagedList);
        request.setAttribute("filterType", filterType);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        RequestDispatcher rd = request.getRequestDispatcher("/view/manager/service-management.jsp");
        rd.forward(request, response);

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
        doGet(request, response);
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
