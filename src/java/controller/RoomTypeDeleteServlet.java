/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dao.RoomTypeDAO;
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
@WebServlet(name = "RoomTypeDeleteServlet", urlPatterns = {"/roomtypedelete"})
public class RoomTypeDeleteServlet extends HttpServlet {
   
    private final RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet RoomTypeDeleteServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet RoomTypeDeleteServlet at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    } 

   
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        try {
            //Lấy ID hạng phòng cần xóa từ tham số URL truyền sang
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/roomtypelist");
                return;
            }
            int roomTypeId = Integer.parseInt(idStr);

            boolean isDeleted = roomTypeDAO.deleteRoomType(roomTypeId);

            // 3. Điều hướng quay về trang danh sách kèm tín hiệu trạng thái thành công/thất bại
            if (isDeleted) {
                // Xóa mềm thành công -> Redirect về kèm status=deleted để file JS chung nổ SweetAlert2 xanh
                response.sendRedirect(request.getContextPath() + "/roomtypelist?status=deleted");
            } else {
                response.sendRedirect(request.getContextPath() + "/roomtypelist?status=delete_failed");
            }

        } catch (Exception e) {
            System.out.println(" LỖI TẠI RoomTypeDeleteServlet: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/roomtypelist?status=delete_error");
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
        processRequest(request, response);
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
