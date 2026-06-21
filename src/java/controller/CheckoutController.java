package controller;

import dao.CheckoutDAO;
import dao.HotelInfoDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.Booking;
import model.BookingRoom;
import model.Guest;
import model.GuestStay;
import model.HotelInfo;
import model.Room;
import model.RoomType;
import model.StaffAccount;

/**
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-06-21
 */
public class CheckoutController extends HttpServlet {

    // Định dạng ISO cho input datetime-local (yyyy-MM-ddTHH:mm)
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    // Định dạng 24h
    private static final DateTimeFormatter TIME_24H = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");

        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String keyword = request.getParameter("keyword");
        String selectedBookingId = request.getParameter("bookingId");

        //thời gian check-out thực tế (mặc định bây giờ)
        String actualCheckoutStr = request.getParameter("actualCheckoutTime");
        LocalDateTime actualCheckoutDateTime;
        if (actualCheckoutStr != null && !actualCheckoutStr.isEmpty()) {
            try {
                actualCheckoutDateTime = LocalDateTime.parse(actualCheckoutStr);
            } catch (Exception e) {
                actualCheckoutDateTime = LocalDateTime.now();
            }
        } else {
            actualCheckoutDateTime = LocalDateTime.now();
        }

        request.setAttribute("currentDateTime", actualCheckoutDateTime.format(ISO_FORMATTER));

        int page = 1;
        try {
            String pageParam = request.getParameter("page");
            if (pageParam != null) {
                page = Integer.parseInt(pageParam);
            }
        } catch (NumberFormatException e) {
            page = 1;
        }
        int recordsPerPage = 10;

        try {
            CheckoutDAO dao = new CheckoutDAO();
            HotelInfoDAO hdao = new HotelInfoDAO();
            List<Booking> allBookings = new ArrayList<>();

            if (selectedBookingId != null && !selectedBookingId.isEmpty()) {
                int bookingId = Integer.parseInt(selectedBookingId);
                Booking booking = dao.getBookingById(bookingId);

                if (booking != null) {
                    allBookings.add(booking);

                    Guest guest = dao.getGuestByBookingId(bookingId);
                    RoomType roomType = dao.getRoomTypeByBookingId(bookingId);
                    String roomImageUrl = dao.getRoomTypeImgByTypeId(roomType.getRoomTypeId());
                    List<BookingRoom> bookingRooms = dao.getBookingRoomsByBookingId(bookingId);
                    request.setAttribute("bookingRooms", bookingRooms);
                    
                    List<GuestStay> guestStays = dao.getGuestStaysByBookingId(bookingId);
                    
                    HotelInfo hotelInfo = hdao.getHotelInfoById(1);

                    String formattedCheckinTime = "14:00:00";
                    if (booking.getActualCheckinTime() != null) {
                        formattedCheckinTime = booking.getActualCheckinTime().format(TIME_24H);
                    }
                    request.setAttribute("formattedCheckinTime", formattedCheckinTime);

                    //số đêm
                    LocalDate checkinDate = booking.getCheckinDate();
                    LocalDate actualCheckoutDate = actualCheckoutDateTime.toLocalDate();
                    long nights = ChronoUnit.DAYS.between(checkinDate, actualCheckoutDate);

                    if (nights <= 0) {
                        nights = 1;
                    }

                    //tiền
                    double pricePerNight = booking.getBookedPricePerNight() != null ? booking.getBookedPricePerNight().doubleValue() : 0;
                    double roomCharges = nights * pricePerNight * booking.getNumRooms();

                    request.setAttribute("hotelInfo", hotelInfo);
                    request.setAttribute("selectedBooking", booking);
                    request.setAttribute("guest", guest);
                    request.setAttribute("roomType", roomType);
                    request.setAttribute("guestStays", guestStays);
                    request.setAttribute("nights", nights);
                    request.setAttribute("roomCharges", roomCharges);
                    request.setAttribute("roomImageUrl", roomImageUrl);
                }
            } else if (keyword != null && !keyword.trim().isEmpty()) {
                allBookings = dao.searchActiveBookings(keyword.trim());
            } else {
                allBookings = dao.searchActiveBookingsByCheckoutDate(LocalDate.now());
            }

            int totalRecords = (allBookings != null) ? allBookings.size() : 0;
            int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);
            page = Math.max(1, Math.min(page, totalPages > 0 ? totalPages : 1));

            int start = (page - 1) * recordsPerPage;
            int end = Math.min(start + recordsPerPage, totalRecords);
            List<Booking> pagedList = (totalRecords > 0) ? allBookings.subList(start, end) : new ArrayList<>();

            Map<Integer, Guest> guestMap = dao.getGuestsByBookings(pagedList);

            request.setAttribute("guestMap", guestMap);
            request.setAttribute("bookingList", pagedList);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("keyword", keyword);

            request.getRequestDispatcher("/view/receptionist/check-out.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
            request.getRequestDispatcher("/view/receptionist/check-out.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
