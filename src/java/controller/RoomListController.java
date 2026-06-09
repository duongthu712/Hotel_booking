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
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import model.Room;
//import model.RoomType;
//import model.StaffAccount;
//
///**
// * RoomListController.java Display room management page for manager
// *
// * @author LinhLTHE200306
// * @version 1.0
// * @since 2026-06-07
// */
//public class RoomListController extends HttpServlet {
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
//            out.println("<title>Servlet RoomListController</title>");
//            out.println("</head>");
//            out.println("<body>");
//            out.println("<h1>Servlet RoomListController at " + request.getContextPath() + "</h1>");
//            out.println("</body>");
//            out.println("</html>");
//        }
//    }
//
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
//
//        //Check authentication
//        HttpSession session = request.getSession();
//        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
//        if (staff == null) {
//            response.sendRedirect("/view/auth/login.jsp");
//            return;
//        }
//
//        //Get filter values
//        String floorParam = request.getParameter("floor");
//        String roomTypeParam = request.getParameter("roomTypeId");
//        String keyword = request.getParameter("keyword");
//
//        Integer floor = null;
//        Integer roomTypeId = null;
//
//        try {
//            if (floorParam != null && !floorParam.isEmpty()) {
//                floor = Integer.valueOf(floorParam);
//            }
//        } catch (Exception e) {
//        }
//
//        try {
//            if (roomTypeParam != null && !roomTypeParam.isEmpty()) {
//                roomTypeId = Integer.valueOf(roomTypeParam);
//            }
//        } catch (Exception e) {
//        }
//
//        RoomDAO rDao = new RoomDAO();
//        RoomTypeDAO rtDao = new RoomTypeDAO();
//
//        List<Integer> floorList = rDao.getAllFloors();
//        List<RoomType> roomTypeList = rtDao.getAllRoomTypes();
//
//        Map<Integer, String> roomTypeMap = new HashMap<>();
//        for (RoomType rt : roomTypeList) {
//            roomTypeMap.put(rt.getRoomTypeId(), rt.getTypeName());
//        }
//
//        List<Room> roomList = rDao.searchAndFilterRooms(floor, roomTypeId, keyword);
//
//        request.setAttribute("roomList", roomList);
//        request.setAttribute("roomTypeList", roomTypeList);
//        request.setAttribute("floorList", floorList);
//        request.setAttribute("selectedFloor", floor);
//        request.setAttribute("selectedRoomTypeId", roomTypeId);
//        request.setAttribute("keyword", keyword);
//        request.setAttribute("roomTypeMap", roomTypeMap);
//        RequestDispatcher rd = request.getRequestDispatcher("/view/manager/room-management.jsp");
//        rd.forward(request, response);
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
//    protected void doPost(HttpServletRequest request,
//            HttpServletResponse response)
//            throws ServletException, IOException {
//        doGet(request, response);
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
//}
