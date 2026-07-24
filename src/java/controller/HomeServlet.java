/**
 * Author: ThuDNM-HE204370
 * Date created: 03/06/2026
 * Purpose: Controller logic for HomeServlet.
 */
package controller;

import dao.HotelInfoDAO;
import dao.RoomTypeDAO;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.HotelImage;
import model.HotelInfo;
import model.HotelNews;
import model.HotelService;
import model.RoomType;

/**
 *
 * @author Minh Thu
 */
@WebServlet(name = "HomeServlet", urlPatterns = {"/home", ""})
public class HomeServlet extends HttpServlet {

    /**
     * * Processes requests for both HTTP <code>GET</code> and
     * <code>POST</code> methods.
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HotelInfoDAO hotelInfoDAO = new HotelInfoDAO();
        RoomTypeDAO roomtypeDAO = new RoomTypeDAO();
        HotelInfo hotelInfo = hotelInfoDAO.getHotelDetails(1);
        List<HotelService> servicesList = hotelInfoDAO.getActiveHotelServices();
        List<HotelNews> top3News = hotelInfoDAO.getTop3LatestNews();
        List<HotelImage> smallImages = hotelInfoDAO.get6SmallImages(1);
        List<RoomType> roomTypeList = roomtypeDAO.getAllRoomTypes();
        request.setAttribute("hotelInfo", hotelInfo);
        request.setAttribute("services", servicesList);
        request.setAttribute("top3News", top3News);
        request.setAttribute("smallImages", smallImages);
        request.setAttribute("allRoomTypesList", roomTypeList);

        request.getRequestDispatcher("/view/public/homepage.jsp").forward(request, response);
    }
}
