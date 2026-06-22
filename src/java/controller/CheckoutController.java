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
import model.Booking;
import model.StaffAccount;

/**
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-06-21
 */
public class CheckoutController extends HttpServlet {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss");
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
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

        String actualCheckoutStr = request.getParameter("actualCheckoutTime");
        LocalDateTime actualCheckoutDateTime;

        try {
            actualCheckoutDateTime = (actualCheckoutStr != null && !actualCheckoutStr.isEmpty())
                    ? LocalDateTime.parse(actualCheckoutStr)
                    : LocalDateTime.now();
        } catch (Exception e) {
            actualCheckoutDateTime = LocalDateTime.now();
        }

        request.setAttribute("currentDateTimeDisplay", actualCheckoutDateTime.format(DISPLAY_FORMATTER));
        request.setAttribute("currentDateTimeISO", actualCheckoutDateTime.format(ISO_FORMATTER));

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
                    request.setAttribute("guest", dao.getGuestByBookingId(bookingId));
                    request.setAttribute("roomType", dao.getRoomTypeByBookingId(bookingId));
                    request.setAttribute("roomImageUrl", dao.getRoomTypeImgByTypeId(dao.getRoomTypeByBookingId(bookingId).getRoomTypeId()));
                    request.setAttribute("bookingRooms", dao.getBookingRoomsByBookingId(bookingId));
                    request.setAttribute("guestStays", dao.getGuestStaysByBookingId(bookingId));
                    request.setAttribute("hotelInfo", hdao.getHotelInfoById(1));

                    // Tính toán check-in time
                    request.setAttribute("formattedCheckinTime", (booking.getActualCheckinTime() != null)
                            ? booking.getActualCheckinTime().format(TIME_24H) : "14:00:00");

                    // Tính số đêm và tiền
                    long nights = Math.max(1, ChronoUnit.DAYS.between(booking.getCheckinDate(), actualCheckoutDateTime.toLocalDate()));
                    double pricePerNight = booking.getBookedPricePerNight() != null ? booking.getBookedPricePerNight().doubleValue() : 0;
                    double roomCharges = nights * pricePerNight * booking.getNumRooms();

                    request.setAttribute("selectedBooking", booking);
                    request.setAttribute("nights", nights);
                    request.setAttribute("roomCharges", roomCharges);
                }
            } else if (keyword != null && !keyword.trim().isEmpty()) {
                allBookings = dao.searchActiveBookings(keyword.trim());
            } else {
                allBookings = dao.searchActiveBookingsByCheckoutDate(LocalDate.now());
            }

            int totalRecords = allBookings.size();
            int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);
            page = Math.max(1, Math.min(page, totalPages > 0 ? totalPages : 1));

            List<Booking> pagedList = (totalRecords > 0) ? allBookings.subList((page - 1) * recordsPerPage, Math.min(page * recordsPerPage, totalRecords)) : new ArrayList<>();

            request.setAttribute("guestMap", dao.getGuestsByBookings(pagedList));
            request.setAttribute("bookingList", pagedList);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("keyword", keyword);

            request.getRequestDispatcher("/view/receptionist/check-out.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/view/receptionist/check-out.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        doGet(request, response);
    }
}
