//package controller;
//
//import dao.RoomDAO;
//import dao.RoomTypeDAO;
//import jakarta.servlet.RequestDispatcher;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.List;
//import model.Room;
//import model.RoomType;
//import model.StaffAccount;
//
///**
// * RoomEditController.java
// *
// * Update room information
// *
// * @author LinhLTHE200306
// * @version 1.0
// * @since 2026-06-07
// */
//public class RoomEditController extends HttpServlet {
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
//            out.println("<title>Servlet RoomEditController</title>");
//            out.println("</head>");
//            out.println("<body>");
//            out.println("<h1>Servlet RoomEditController at " + request.getContextPath() + "</h1>");
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
//
//        if (staff == null) {
//            response.sendRedirect("/view/auth/login.jsp");
//            return;
//        }
//
//        try {
//            int roomNumber = Integer.parseInt(request.getParameter("roomNumber"));
//            RoomDAO rDao = new RoomDAO();
//            RoomTypeDAO rtDao = new RoomTypeDAO();
//
//            Room editRoom = rDao.getRoomByNumber(roomNumber);
//            List <RoomType> roomTypeList = rtDao.getAllRoomTypes();
//
//            request.setAttribute("editRoom", editRoom);
//            request.setAttribute("roomTypeList", roomTypeList);
//
//            RequestDispatcher rd = request.getRequestDispatcher("/RoomList");
//            rd.forward(request, response);
//
//        } catch (Exception ex) {
//            System.out.println("RoomEditController:" + ex.getMessage());
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
//        //Check authentication
//        HttpSession session = request.getSession();
//        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
//        if (staff == null) {
//            response.sendRedirect("/view/auth/login.jsp");
//            return;
//        }
//
//        try {
//            int roomNumber = Integer.parseInt(request.getParameter("roomNumber"));
//            String status = request.getParameter("status");
//            int roomTypeId = Integer.parseInt(request.getParameter("roomTypeId"));
//            RoomDAO rDao = new RoomDAO();
//            Room oldRoom = rDao.getRoomByNumber(roomNumber);
//
//            if (oldRoom == null) {
//                response.sendRedirect("RoomList");
//                return;
//            }
//
//            if (!"Đang bảo trì".equals(oldRoom.getStatus()) && oldRoom.getRoomTypeId() != roomTypeId) {
//                session.setAttribute("error", "Chỉ được thay đổi hạng phòng khi phòng đang bảo trì.");
//                response.sendRedirect("RoomList");
//                return;
//            }
//            Room room = new Room(roomNumber, oldRoom.getFloor(), status, roomTypeId);
//
//            rDao.updateRoom(room);
//            session.setAttribute("success", "Cập nhật phòng thành công.");
//        } catch (Exception ex) {
//            System.out.println("RoomEditController:" + ex.getMessage());
//            session.setAttribute("error", "Có lỗi xảy ra khi cập nhật phòng.");
//        }
//        response.sendRedirect("RoomList");
//    }
//
//    /**
//     * Returns a short description of the servlet.
//     *
//     * @return a String containing servlet description
//     */
//    @Override
//    public String getServletInfo() {
//        return "Room Management Controller";
//    }
//
//}
