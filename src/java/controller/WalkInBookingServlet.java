/**
 * Author: ThuDNM-HE204370
 * Date created: 17/06/2026
 * Purpose: Controller logic for WalkInBookingServlet.
 */
package controller;

import dao.WalkinBookingDAO;
import dto.AvailableRoomTypeView;
import model.Booking;
import dal.EmailUtil;
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if ("calculate".equals(action)) {
            String checkInStr = request.getParameter("checkInDate");
            String checkOutStr = request.getParameter("checkOutDate");
            int numRooms = 1;
            try {
                numRooms = Integer.parseInt(request.getParameter("numRooms"));
            } catch (NumberFormatException e) {
            }
            BigDecimal basePrice = BigDecimal.ZERO;
            try {
                basePrice = new BigDecimal(request.getParameter("basePrice"));
            } catch (Exception e) {
            }

            long nights = 1;
            boolean isStayNow = false;
            if (checkInStr != null && !checkInStr.isEmpty() && checkOutStr != null && !checkOutStr.isEmpty()) {
                LocalDate checkInDate = LocalDate.parse(checkInStr);
                LocalDate checkOutDate = LocalDate.parse(checkOutStr);
                nights = java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
                if (nights <= 0) nights = 1;

                LocalDate today = LocalDate.now();
                isStayNow = checkInDate.equals(today);
            }

            BigDecimal roomCharges = basePrice.multiply(BigDecimal.valueOf(nights)).multiply(BigDecimal.valueOf(numRooms));
            BigDecimal depositAmount = BigDecimal.ZERO;
            if (!isStayNow) {
                depositAmount = roomCharges.multiply(new BigDecimal("0.30")).setScale(0, java.math.RoundingMode.HALF_UP);
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(String.format("{\"nights\":%d, \"roomCharges\":%s, \"depositAmount\":%s, \"isStayNow\":%b}", 
                    nights, roomCharges.toPlainString(), depositAmount.toPlainString(), isStayNow));
            return;
        }

        WalkinBookingDAO walkinDAO = new WalkinBookingDAO();

        // Lấy danh sách hạng phòng ban đầu để đổ vào thẻ <select> dropdown của thanh tìm kiếm
        List<AvailableRoomTypeView> dropdownRoomTypes = walkinDAO.getAllRoomTypes();
        request.setAttribute("dropdownRoomTypes", dropdownRoomTypes);

        // Thu thập tham số tìm kiếm từ bộ lọc ở phía đầu trang
        String checkIn = request.getParameter("checkInDate");
        String checkOut = request.getParameter("checkOutDate");
        String roomTypeIdParam = request.getParameter("roomTypeId");
        String numRoomsParam = request.getParameter("numRooms");
        String numGuestsParam = request.getParameter("numGuests");
        String numChildrenParam = request.getParameter("numChildren");

        int numGuestsSearch = 1; // Mặc định nếu chưa nhập gì
        if (numGuestsParam != null && !numGuestsParam.isEmpty()) {
            try {
                numGuestsSearch = Integer.parseInt(numGuestsParam);
            } catch (NumberFormatException e) {
            }
        }

        int numChildrenSearch = 0; // Mặc định nếu chưa nhập gì
        if (numChildrenParam != null && !numChildrenParam.isEmpty()) {
            try {
                numChildrenSearch = Integer.parseInt(numChildrenParam);
            } catch (NumberFormatException e) {
            }
        }
        List<AvailableRoomTypeView> allRoomTypes;

        // Điều kiện bắt buộc thực thi filter: Có nhập đầy đủ Ngày đến và Ngày đi
        if (checkIn != null && !checkIn.isEmpty() && checkOut != null && !checkOut.isEmpty()) {

            int roomTypeId = 0;
            if (roomTypeIdParam != null && !roomTypeIdParam.isEmpty()) {
                roomTypeId = Integer.parseInt(roomTypeIdParam);
            }

            // Chuẩn hóa số lượng phòng cần thuê (Mặc định tối thiểu luôn là 1 phòng)
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

            // Gọi hàm xử lý tìm kiếm hạng phòng khả dụng từ dữ liệu database (Đã tối ưu hóa thầu đủ 6 tham số)
            allRoomTypes = walkinDAO.searchAvailableRooms(checkIn, checkOut, roomTypeId, numRooms, numGuestsSearch, numChildrenSearch);
            request.setAttribute("isSearching", true);

        } else {
            // Lần đầu load trang: Chưa kích hoạt nút bấm tìm kiếm
            allRoomTypes = dropdownRoomTypes;
            request.setAttribute("isSearching", false);
        }

        request.setAttribute("allRoomTypes", allRoomTypes);
        request.getRequestDispatcher("/view/receptionist/walk-in-booking.jsp").forward(request, response);
    }

    // Xử lý tiếp nhận dữ liệu lưu đơn đặt phòng và gửi thông báo xác nhận đến email của khách hàng
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        WalkinBookingDAO walkinDAO = new WalkinBookingDAO();

        // 1. Đọc thông tin chi tiết của khách hàng mới từ các ô Form nhập liệu
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String idNumber = request.getParameter("idNumber");
        String dobParam = request.getParameter("dateOfBirth");

        LocalDate dateOfBirth = null;
        if (dobParam != null && !dobParam.isEmpty()) {
            dateOfBirth = LocalDate.parse(dobParam);
        }

        // 2. Thu thập các thông tin cấu hình phòng thuê gửi lên trực tiếp từ Form nhập động công khai
        int roomTypeId = Integer.parseInt(request.getParameter("roomTypeId"));
        String checkInStr = request.getParameter("checkInDate");
        String checkOutStr = request.getParameter("checkOutDate");
        int numRooms = Integer.parseInt(request.getParameter("numRooms"));
        String paymentMethod = request.getParameter("paymentMethod");

        // ĐỌC ĐỘNG SỐ LƯỢNG KHÁCH ĐI CÙNG (Đồng bộ xử lý tính toán)
        int numGuests = Integer.parseInt(request.getParameter("numGuests"));
        int numChildren = Integer.parseInt(request.getParameter("numChildren"));

        // Giả lập ID tài khoản của lễ tân đang thực hiện giao dịch trực ca (Gắn cố định theo tài khoản mẫu)
        int staffId = 4;

        LocalDate checkInDate = LocalDate.parse(checkInStr);
        LocalDate checkOutDate = LocalDate.parse(checkOutStr);

        // 3. KIỂM TRA SỨC CHỨA CHẶN TRƯỚC TẠI TẦNG BACK-END (BẢO VỆ CHỐNG TRÀN DỮ LIỆU)
        int maxAdultsPerRoom = 2; // Biến dự phòng mặc định nếu có sự cố lọt lưới
        int maxChildrenPerRoom = 0;

        List<AvailableRoomTypeView> dropdownRoomTypes = walkinDAO.getAllRoomTypes();
        for (AvailableRoomTypeView type : dropdownRoomTypes) {
            if (type.getRoomTypeId() == roomTypeId) {
                maxAdultsPerRoom = type.getMaxAdults();
                maxChildrenPerRoom = type.getMaxChildren();
                break;
            }
        }

        int totalMaxAdults = maxAdultsPerRoom * numRooms;
        int totalMaxChildren = maxChildrenPerRoom * numRooms;

        // Nếu phát hiện lễ tân cố tình lách qua JS để nhập quá số người quy định của hệ thống
        if (numGuests > totalMaxAdults || numChildren > totalMaxChildren) {
            response.sendRedirect(request.getContextPath() + "/walk-in-booking?status=over_capacity_error"
                    + "&checkInDate=" + checkInStr + "&checkOutDate=" + checkOutStr + "&numRooms=" + numRooms + "&roomTypeId=" + roomTypeId);
            return; // Ngắt dòng xử lý ngay lập tức
        }

        // 4. Kiểm tra mốc thời gian xem khách làm thủ tục lưu trú luôn hôm nay hay đặt giữ chỗ cho tương lai
        LocalDate today = LocalDate.now();
        boolean isStayNow = checkInDate.equals(today);

        // Tính toán số đêm lưu trú chuẩn nghiệp vụ từ ngày đến và ngày đi
        long nights = java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        if (nights <= 0) {
            nights = 1;
        }

        // Lấy đơn giá gốc của hạng phòng truyền từ thuộc tính ẩn trên giao diện
        BigDecimal basePrice = new BigDecimal(request.getParameter("basePrice"));
        
        // Tính tổng tiền phòng = Đơn giá x Số đêm x Số phòng
        BigDecimal roomCharges = basePrice.multiply(BigDecimal.valueOf(nights)).multiply(BigDecimal.valueOf(numRooms));

        // Phân tách thiết lập tài chính theo luồng chọn
        BigDecimal depositAmount = BigDecimal.ZERO;
        String paymentStatus = "Chưa thanh toán";
        // Tính tiền cọc
        if (!isStayNow) {
            depositAmount = roomCharges.multiply(new BigDecimal("0.30")).setScale(0, java.math.RoundingMode.HALF_UP);
            paymentStatus = "Đã đặt cọc";
        }

        // 5. Khởi tạo đối tượng model và đóng gói dữ liệu Booking để truyền xuống DAO
        Booking booking = new Booking();
        booking.setStaffId(staffId);
        booking.setRoomTypeId(roomTypeId);
        booking.setNumRooms(numRooms);
        booking.setCheckinDate(checkInDate);
        booking.setCheckoutDate(checkOutDate);
        booking.setNumGuests(numGuests);
        booking.setNumChildren(numChildren);

        booking.setBookedPricePerNight(basePrice);
        booking.setDepositAmount(depositAmount);
        booking.setPaymentStatus(paymentStatus);

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
            // 6. Gọi hàm xử lý luồng Transaction nghiệp vụ tổng tại tầng DAO
            boolean isSuccess = walkinDAO.createWalkinBookingProcess(booking, fullName, email, phone, idNumber, dateOfBirth, isStayNow, roomCharges);

            // 7. Điều hướng giao diện dựa trên kết quả thực thi thành công và tự động kích hoạt gửi mail
            if (isSuccess) {
                
                // Gửi Email xác nhận lập đơn tại quầy cho khách hàng (bọc khối try-catch biệt lập để an toàn dữ liệu)
                try {
                    EmailUtil.sendWalkInBookingConfirmed(
                        email, 
                        fullName, 
                        phone, 
                        idNumber, 
                        dateOfBirth, 
                        uniqueCode, 
                        checkInDate, 
                        checkOutDate, 
                        numRooms, 
                        numGuests, 
                        numChildren, 
                        booking.getDepositAmount(), 
                        isStayNow
                    );
                } catch (Exception mailEx) {
                    System.out.println("Lỗi hệ thống gửi email xác nhận đặt phòng tại quầy: " + mailEx.getMessage());
                    mailEx.printStackTrace();
                }

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