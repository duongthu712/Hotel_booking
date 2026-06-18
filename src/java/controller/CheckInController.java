package controller;

import dao.BookingDAO;
import dto.BookingCheckInView;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet(name = "CheckInController", urlPatterns = {"/checkin"})
public class CheckInController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        BookingDAO bookingDAO = new BookingDAO();
        List<BookingCheckInView> listToday = bookingDAO.getBookingsToday();
        request.setAttribute("listToday", listToday);
        
        int totalCheckInToday = listToday.size();
        int checkedInCount = 0;
        int notCheckedInCount = 0;
        
        for (BookingCheckInView b : listToday) {
            if ("Đã nhận phòng".equals(b.getStatus())) {
                checkedInCount++;
            } else if ("Đã xác nhận".equals(b.getStatus())) {
                notCheckedInCount++;
            }
        }
        
        request.setAttribute("totalCheckInToday", totalCheckInToday);
        request.setAttribute("checkedInCount", checkedInCount);
        request.setAttribute("notCheckedInCount", notCheckedInCount);
        
        String searchBookingCode = request.getParameter("searchBookingCode");
        if (searchBookingCode != null && !searchBookingCode.trim().isEmpty()) {
            BookingCheckInView booking = bookingDAO.getBookingForCheckIn(searchBookingCode.trim());
            if (booking != null) {
                request.setAttribute("booking", booking);
            } else {
                request.setAttribute("errorMsg", "Không tìm thấy mã đơn đặt phòng này!");
            }
        }
        
        request.getRequestDispatcher("/view/receptionist/check-in.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        BookingDAO bookingDAO = new BookingDAO();

        try {
            int bookingId = Integer.parseInt(request.getParameter("bookingId"));
            String bookingCode = request.getParameter("bookingCode");
            String action = request.getParameter("action"); 

            if ("cancel".equals(action)) {
                boolean cancelSuccess = bookingDAO.cancelBooking(bookingId);
                if (cancelSuccess) {
                    response.sendRedirect(request.getContextPath() + "/checkin");
                } else {
                    request.setAttribute("errorMsg", "Thao tác thất bại: Không thể cập nhật trạng thái hủy đơn.");
                    BookingCheckInView booking = bookingDAO.getBookingForCheckIn(bookingCode);
                    request.setAttribute("booking", booking);
                    doGet(request, response);
                }
                return; 
            }

            int currentGuestId = Integer.parseInt(request.getParameter("currentGuestId"));
            int numGuests = Integer.parseInt(request.getParameter("numGuests"));
            int maxCapacity = Integer.parseInt(request.getParameter("maxCapacity"));
            int numRooms = Integer.parseInt(request.getParameter("numRooms"));

            if (numGuests > (maxCapacity * numRooms)) {
                request.setAttribute("errorMsg", "Lỗi: Số khách thực tế vượt quá tổng sức chứa của hạng phòng!");
                BookingCheckInView booking = bookingDAO.getBookingForCheckIn(bookingCode);
                request.setAttribute("booking", booking);
                doGet(request, response);
                return;
            }

            String idFullName = request.getParameter("idFullName");
            String idPhone = request.getParameter("idPhone") != null ? request.getParameter("idPhone").trim() : "";
            String idEmail = request.getParameter("idEmail");
            String idNumber = request.getParameter("idNumber") != null ? request.getParameter("idNumber").trim() : "";
            String dateOfBirth = request.getParameter("dateOfBirth");
            String nationality = request.getParameter("nationality");

            if (!idPhone.isEmpty() && !idPhone.matches("^(0[3|5|7|8|9])[0-9]{8}$")) {
                request.setAttribute("errorMsg", "Thao tác thất bại: Số điện thoại không đúng định dạng!");
                BookingCheckInView booking = bookingDAO.getBookingForCheckIn(bookingCode);
                request.setAttribute("booking", booking);
                doGet(request, response);
                return;
            }

            if (!idNumber.matches("^([0-9]{12}|[A-Z][0-9]{7,8})$")) {
                request.setAttribute("errorMsg", "Thao tác thất bại: Số CCCD hoặc Hộ chiếu không hợp lệ!");
                BookingCheckInView booking = bookingDAO.getBookingForCheckIn(bookingCode);
                request.setAttribute("booking", booking);
                doGet(request, response);
                return;
            }

            if (dateOfBirth == null || dateOfBirth.trim().isEmpty()) {
                request.setAttribute("errorMsg", "Thao tác thất bại: Vui lòng chọn ngày sinh!");
                BookingCheckInView booking = bookingDAO.getBookingForCheckIn(bookingCode);
                request.setAttribute("booking", booking);
                doGet(request, response);
                return;
            }
            
            java.time.LocalDate dob = java.time.LocalDate.parse(dateOfBirth);
            java.time.LocalDate today = java.time.LocalDate.now();
            long yearsBetween = java.time.temporal.ChronoUnit.YEARS.between(dob, today);
            
            if (yearsBetween < 18) {
                request.setAttribute("errorMsg", "Từ chối thủ tục: Khách hàng đại diện phải từ 18 tuổi trở lên!");
                BookingCheckInView booking = bookingDAO.getBookingForCheckIn(bookingCode);
                request.setAttribute("booking", booking);
                doGet(request, response);
                return;
            }
            
            boolean isDifferentGuest = "true".equals(request.getParameter("isDifferentGuest"));

            boolean success = bookingDAO.updateCheckInAdvance(
                    bookingId, currentGuestId, idFullName, idPhone, idEmail,
                    idNumber, nationality, dateOfBirth, numGuests, isDifferentGuest
            );

            if (success) {
                response.sendRedirect(request.getContextPath() + "/checkin?searchBookingCode=" + bookingCode);
            } else {
                request.setAttribute("errorMsg", "Có lỗi xảy ra trong quá trình cập nhật cơ sở dữ liệu.");
                doGet(request, response);
            }

        } catch (Exception e) {
            request.setAttribute("errorMsg", "Lỗi xử lý dữ liệu đầu vào hoặc sai định dạng ngày tháng!");
            doGet(request, response);
        }
    }
}