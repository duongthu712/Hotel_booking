///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
// */
//package controller;
//
//import dao.RoomDAO;
//import dao.RoomTypeDAO;
//import jakarta.servlet.RequestDispatcher;
//import java.io.IOException;
//import java.io.PrintWriter;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import model.GuestStay;
//import model.Room;
//import model.RoomType;
//import model.StaffAccount;
//
///**
// * RoomDetailController.java Display room detail for manager
// *
// * @author LinhLTHE200306
// * @version 1.0
// * @since 2026-06-07
// */
//public class RoomDetailController extends HttpServlet {
//
//    /**
//     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
//     * methods.
//     *
//     * @param request servlet request
//     * @param response servlet response
//     * @throws ServletException if a servlet-specific error occurs
//     * @throws IOException if an I/O error occurs
//     */
//    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        response.setContentType("text/html;charset=UTF-8");
//        try (PrintWriter out = response.getWriter()) {
//            /* TODO output your page here. You may use following sample code. */
//            out.println("<!DOCTYPE html>");
//            out.println("<html>");
//            out.println("<head>");
//            out.println("<title>Servlet RoomDetailController</title>");
//            out.println("</head>");
//            out.println("<body>");
//            out.println("<h1>Servlet RoomDetailController at " + request.getContextPath() + "</h1>");
//            out.println("</body>");
//            out.println("</html>");
//        }
//    }
//
//    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
//    /**
//     * Handles the HTTP <code>GET</code> method.
//     *
//     * @param request servlet request
//     * @param response servlet response
//     * @throws ServletException if a servlet-specific error occurs
//     * @throws IOException if an I/O error occurs
//     */
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        HttpSession session = request.getSession();
//        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
//        if (staff == null) {
//            response.sendRedirect("/view/auth/login.jsp");
//            return;
//        }
//
//        try {
//            int roomNumber = Integer.parseInt(request.getParameter("roomNumber"));
//            RoomDAO rDao = new RoomDAO();
//
//            Room selectedRoom = rDao.getRoomByNumber(roomNumber);
//            List <GuestStay> guestList = new ArrayList<>();
//
//            if (selectedRoom != null && "Phòng có khách".equals(selectedRoom.getStatus())) {
//                guestList = rDao.getGuestsByRoomNumber(roomNumber);
//            }
//
//            request.setAttribute("selectedRoom", selectedRoom);
//            request.setAttribute("guestList", guestList);
//
//            RequestDispatcher rd = request.getRequestDispatcher("/RoomList");
//            rd.forward(request, response);
//
//        } catch (Exception ex) {
//            System.out.println("RoomDetailController:" + ex.getMessage());
//            response.sendRedirect("RoomList");
//        }
//    }
//
//    /**
//     * Handles the HTTP <code>POST</code> method.
//     *
//     * @param request servlet request
//     * @param response servlet response
//     * @throws ServletException if a servlet-specific error occurs
//     * @throws IOException if an I/O error occurs
//     */
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        processRequest(request, response);
//    }
//
//    /**
//     * Returns a short description of the servlet.
//     *
//     * @return a String containing servlet description
//     */
//    @Override
//    public String getServletInfo() {
//        return "Short description";
//    }// </editor-fold>
//
//}
