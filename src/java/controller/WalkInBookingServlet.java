package controller;

import dao.WalkinBookingDAO;
import dto.AvailableRoomTypeView;
import model.Booking;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "WalkInBookingServlet", urlPatterns = {"/walk-in-booking"})
public class WalkInBookingServlet extends HttpServlet {

    // Xử lý yêu cầu hiển thị trang và tìm kiếm danh sách phòng trống
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        WalkinBookingDAO walkinDAO = new WalkinBookingDAO();

        // Lấy danh sách hạng phòng ban đầu để đổ vào thẻ <select> dropdown
        List<AvailableRoomTypeView> dropdownRoomTypes = walkinDAO.getAllRoomTypes();
        request.setAttribute("dropdownRoomTypes", dropdownRoomTypes);

        // Thu thập tham số tìm kiếm từ bộ lọc
        String checkIn = request.getParameter("checkInDate");
        String checkOut = request.getParameter("checkOutDate");
        String roomTypeIdParam = request.getParameter("roomTypeId");
        String numRoomsParam = request.getParameter("numRooms");

        List<AvailableRoomTypeView> allRoomTypes;

        // Điều kiện bắt buộc: Có nhập Ngày đến và Ngày đi
        if (checkIn != null && !checkIn.isEmpty() && checkOut != null && !checkOut.isEmpty()) {

            int roomTypeId = 0;
            if (roomTypeIdParam != null && !roomTypeIdParam.isEmpty()) {
                roomTypeId = Integer.parseInt(roomTypeIdParam);
            }

            // Chuẩn hóa số lượng phòng cần thuê (Mặc định tối thiểu là 1 phòng)
            int numRooms = 1;
            if (numRoomsParam != null && !numRoomsParam.isEmpty()) {
                try {
                    numRooms = Integer.parseInt(numRoomsParam);
                    if (numRooms < 1) {
                        numRooms = 1;
                    }
                } catch (NumberFormatException e) {
                    numRooms = 1;
                }
            }

            // Gọi hàm xử lý tìm kiếm hạng phòng khả dụng từ DAO
            allRoomTypes = walkinDAO.searchAvailableRooms(checkIn, checkOut, roomTypeId, numRooms);
            request.setAttribute("isSearching", true);

        } else {
            // Lần đầu load trang: Chưa bấm tìm kiếm
            allRoomTypes = dropdownRoomTypes;
            request.setAttribute("isSearching", false);
        }

        request.setAttribute("allRoomTypes", allRoomTypes);
        request.getRequestDispatcher("/view/receptionist/walk-in-booking.jsp").forward(request, response);
    }

    // Xử lý tiếp nhận dữ liệu lưu đơn đặt phòng và rẽ nhánh tài chính
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        WalkinBookingDAO walkinDAO = new WalkinBookingDAO();

        // 1. Đọc thông tin chi tiết của khách hàng mới từ Form nhập liệu
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String idNumber = request.getParameter("idNumber");
        String dobParam = request.getParameter("dateOfBirth");

        LocalDate dateOfBirth = null;
        if (dobParam != null && !dobParam.isEmpty()) {
            dateOfBirth = LocalDate.parse(dobParam);
        }

        // 2. Thu thập các thông tin cấu hình phòng thuê từ Form ẩn
        int roomTypeId = Integer.parseInt(request.getParameter("roomTypeId"));
        String checkInStr = request.getParameter("checkInDate");
        String checkOutStr = request.getParameter("checkOutDate");
        int numRooms = Integer.parseInt(request.getParameter("numRooms"));
        String paymentMethod = request.getParameter("paymentMethod");

        // Giả lập ID tài khoản của lễ tân đang thực hiện giao dịch trực ca (Gắn cố định theo tài khoản mẫu)
        int staffId = 4;

        LocalDate checkInDate = LocalDate.parse(checkInStr);
        LocalDate checkOutDate = LocalDate.parse(checkOutStr);

        // 3. Kiểm tra mốc thời gian xem khách lưu trú luôn hôm nay hay đặt cho tương lai
        LocalDate today = LocalDate.now();
        boolean isStayNow = checkInDate.equals(today);

        // 4. Khởi tạo đối tượng model và đóng gói dữ liệu Booking để chuẩn bị truyền xuống DAO
        Booking booking = new Booking();
        booking.setStaffId(staffId);
        booking.setRoomTypeId(roomTypeId);
        booking.setNumRooms(numRooms);
        booking.setCheckinDate(checkInDate);
        booking.setCheckoutDate(checkOutDate);

        // Lấy đơn giá gốc của hạng phòng truyền từ thuộc tính ẩn trên giao diện
        BigDecimal basePrice = new BigDecimal(request.getParameter("basePrice"));
        booking.setBookedPricePerNight(basePrice);

        // Thiết lập các chỉ số số lượng khách lưu trú mặc định ban đầu
        booking.setNumGuests(1);
        booking.setNumChildren(0);

        // ĐỒNG BỘ MÃ BOOKING: Sinh mã ngẫu nhiên duy nhất bắt đầu bằng LMHW (8 ký tự ngẫu nhiên sau)
        String uniqueCode;
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();

        do {
            StringBuilder sb = new StringBuilder("LMHB");
            for (int i = 0; i < 8; i++) {
                int index = random.nextInt(characters.length());
                sb.append(characters.charAt(index));
            }
            uniqueCode = sb.toString();
        } while (walkinDAO.isBookingCodeExist(uniqueCode));
        booking.setBookingCode(uniqueCode);

        try {
            // 5. Gọi hàm xử lý luồng Transaction nghiệp vụ tổng tại tầng DAO
            boolean isSuccess = walkinDAO.createWalkinBookingProcess(booking, fullName, email, phone, idNumber, dateOfBirth, isStayNow);

            // 6. Điều hướng giao diện dựa trên kết quả thực thi thành công
            // 6. Điều hướng giao diện dựa trên kết quả thực thi thành công
            if (isSuccess) {
                if (isStayNow) {
                    // Khách ở luôn hôm nay -> Quay lại trang kèm status ở luôn để hiện Pop-up trước, tắt Pop-up sẽ nhảy sang Check-in
                    response.sendRedirect(request.getContextPath() + "/walk-in-booking?status=stay_now_success&code=" + uniqueCode);
                } else {
                    // Khách đặt tương lai -> Quay lại trang kèm status tương lai để hiện Pop-up, tắt Pop-up sẽ ở lại trang
                    response.sendRedirect(request.getContextPath() + "/walk-in-booking?status=future_success&code=" + uniqueCode);
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/walk-in-booking?status=failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/walk-in-booking?status=error");
        }
    }
}
