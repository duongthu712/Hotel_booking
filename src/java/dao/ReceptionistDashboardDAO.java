package dao;

import dal.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import dto.BookingCheckInView;
import model.GuestRequest;
import dto.ReceptionistDashboard;

public class ReceptionistDashboardDAO extends DBContext {

    // ==========================================================================
    // HÀM CHÍNH: TỔNG HỢP TOÀN BỘ SỐ LIỆU & 3 LIST ĐẨY LÊN DASHBOARD
    // ==========================================================================
    public ReceptionistDashboard getDashboardDataToday() {
        ReceptionistDashboard model = new ReceptionistDashboard();
        
        // 1. Câu lệnh đếm nhanh số liệu hiển thị ở các ô Widget tổng quan trên đầu
String sqlBookings = "SELECT "
        // 1. Chờ Check-in hôm nay: Chỉ đếm số đơn 'Đã xác nhận' dự kiến đến trong ngày
        + "SUM(CASE WHEN checkin_date = CAST(GETDATE() AS DATE) AND [status] = N'Đã xác nhận' THEN 1 ELSE 0 END) as arrivals, "
        
        // 2. Dự kiến Check-out hôm nay: Chỉ đếm số đơn 'Đã nhận phòng' dự kiến đi trong ngày
        + "SUM(CASE WHEN checkout_date = CAST(GETDATE() AS DATE) AND [status] = N'Đã nhận phòng' THEN 1 ELSE 0 END) as departures, "
        
        // 3. 🚀 ĐÂY LÀ ĐOẠN ĐÁNH TRÚNG THỦ PHẠM: Khách lưu trú phải đếm trực tiếp số phòng có chữ N'Phòng có khách' từ bảng Rooms
        + "(SELECT COUNT(*) FROM Rooms WHERE [status] = N'Phòng có khách') as in_house "
        
        + "FROM Bookings";

        String sqlRooms = "SELECT [status], COUNT(*) as quantity FROM Rooms GROUP BY [status]";

        String sqlRequestsCount = "SELECT COUNT(*) FROM GuestRequests WHERE [status] = N'Chờ xử lý'";

        try {
            // Thực thi đếm số liệu đơn đặt phòng trong ngày
            try (PreparedStatement ps = connection.prepareStatement(sqlBookings);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    model.setTotalArrivalsToday(rs.getInt("arrivals"));
                    model.setTotalDeparturesToday(rs.getInt("departures"));
                    model.setTotalInHouseGuests(rs.getInt("in_house"));
                }
            }

            // 🚀 ĐÃ SỬA: Đồng bộ chuẩn đét 100% các Key theo Check Constraint trong DB của ông
            Map<String, Integer> roomMap = new HashMap<>();
            roomMap.put("Phòng trống", 0);
            roomMap.put("Phòng có khách", 0);
            roomMap.put("Đang dọn dẹp", 0);
            roomMap.put("Đang bảo trì", 0); 
            
            try (PreparedStatement ps = connection.prepareStatement(sqlRooms);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    roomMap.put(rs.getString("status"), rs.getInt("quantity"));
                }
            }
            model.setRoomStatusCount(roomMap);

            // Thực thi lấy số lượng yêu cầu đặc biệt đang xếp hàng chờ xử lý
            try (PreparedStatement ps = connection.prepareStatement(sqlRequestsCount);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    model.setPendingRequests(rs.getInt(1));
                }
            }

            // 2. Chạy 3 hàm bổ trợ bốc dữ liệu chi tiết gán trực tiếp vào Model
            model.setCheckInTodayList(this.getCheckInToday());
            model.setCheckOutTodayList(this.getCheckOutToday());
            model.setPendingRequestsList(this.getPendingRequests());

            return model;
        } catch (Exception e) {
            System.out.println("Lỗi tổng hợp dữ liệu tại ReceptionistDashboardDAO: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // ==========================================================================
    // HÀM BỔ TRỢ 1: LẤY LIST CHECK-IN HÔM NAY KÈM YÊU CẦU TIẾP ĐÓN ĐÃ DUYỆT
    // ==========================================================================
    public List<BookingCheckInView> getCheckInToday() {
        List<BookingCheckInView> list = new ArrayList<>();
        String sql = "SELECT b.booking_id, b.booking_code, b.num_rooms, b.num_guests, b.payment_status, b.deposit_amount, "
                   + "g.guest_id, g.full_name, g.phone, g.email, rt.room_type_id, rt.type_name, rt.capacity, b.[status], "
                   + "r.request_type, r.request_details "
                   + "FROM Bookings b "
                   + "INNER JOIN Guests g ON b.guest_id = g.guest_id "
                   + "INNER JOIN RoomTypes rt ON b.room_type_id = rt.room_type_id "
                   + "LEFT JOIN GuestRequests r ON b.booking_id = r.booking_id "
                   + "                           AND r.[status] = N'Đã phê duyệt' "
                   + "                           AND r.request_type IN (N'Nhận phòng sớm', N'Nhận phòng muộn') "
                   + "WHERE b.checkin_date = CAST(GETDATE() AS DATE) AND b.[status] = N'Đã xác nhận' "
                   + "ORDER BY b.booking_code ASC";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                BookingCheckInView view = new BookingCheckInView();
                view.setBookingId(rs.getInt("booking_id"));
                view.setBookingCode(rs.getString("booking_code"));
                view.setNumRooms(rs.getInt("num_rooms"));
                view.setNumGuests(rs.getInt("num_guests"));
                view.setPaymentStatus(rs.getString("payment_status"));
                view.setDepositAmount(rs.getBigDecimal("deposit_amount"));
                view.setGuestId(rs.getInt("guest_id"));
                view.setGuestFullName(rs.getString("full_name"));
                view.setGuestPhone(rs.getString("phone"));
                view.setGuestEmail(rs.getString("email"));
                view.setRoomTypeId(rs.getInt("room_type_id"));
                view.setRoomTypeName(rs.getString("type_name"));
                view.setCapacity(rs.getInt("capacity"));
                view.setStatus(rs.getString("status"));
                
                // Trả về riêng biệt: request_type chứa nhãn Badge, request_details chứa mốc giờ
                view.setRequestType(rs.getString("request_type"));
                view.setRequestDetails(rs.getString("request_details"));
                list.add(view);
            }
        } catch (Exception e) {
            System.out.println("Lỗi hàm getCheckInToday: " + e.getMessage());
        }
        return list;
    }

    // ==========================================================================
    // HÀM BỔ TRỢ 2: LẤY LIST CHECK-OUT HÔM NAY - CHỈ ĐÍNH KÈM YÊU CẦU TRẢ PHÒNG MUỘN ĐÃ DUYỆT
    // ==========================================================================
    public List<BookingCheckInView> getCheckOutToday() {
        List<BookingCheckInView> list = new ArrayList<>();
        String sql = "SELECT b.booking_id, b.booking_code, b.num_rooms, b.num_guests, b.payment_status, b.deposit_amount, "
                   + "g.guest_id, g.full_name, g.phone, g.email, rt.room_type_id, rt.type_name, rt.capacity, b.[status], "
                   + "r.request_type, r.request_details "
                   + "FROM Bookings b "
                   + "INNER JOIN Guests g ON b.guest_id = g.guest_id "
                   + "INNER JOIN RoomTypes rt ON b.room_type_id = rt.room_type_id "
                   + "LEFT JOIN GuestRequests r ON b.booking_id = r.booking_id "
                   + "                           AND r.[status] = N'Đã phê duyệt' "
                   + "                           AND r.request_type = N'Trả phòng muộn' " 
                   + "WHERE b.checkout_date = CAST(GETDATE() AS DATE) AND b.[status] = N'Đã nhận phòng' "
                   + "ORDER BY b.booking_code ASC";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                BookingCheckInView view = new BookingCheckInView();
                view.setBookingId(rs.getInt("booking_id"));
                view.setBookingCode(rs.getString("booking_code"));
                view.setNumRooms(rs.getInt("num_rooms"));
                view.setNumGuests(rs.getInt("num_guests"));
                view.setPaymentStatus(rs.getString("payment_status"));
                view.setDepositAmount(rs.getBigDecimal("deposit_amount"));
                view.setGuestId(rs.getInt("guest_id"));
                view.setGuestFullName(rs.getString("full_name"));
                view.setGuestPhone(rs.getString("phone"));
                view.setGuestEmail(rs.getString("email"));
                view.setRoomTypeId(rs.getInt("room_type_id"));
                view.setRoomTypeName(rs.getString("type_name"));
                view.setCapacity(rs.getInt("capacity"));
                view.setStatus(rs.getString("status"));
                
                // Trả về riêng biệt: request_type chứa nhãn Badge, request_details chứa mốc giờ
                view.setRequestType(rs.getString("request_type"));
                view.setRequestDetails(rs.getString("request_details"));
                list.add(view);
            }
        } catch (Exception e) {
            System.out.println("Lỗi hàm getCheckOutToday: " + e.getMessage());
        }
        return list;
    }

    // ==========================================================================
    // HÀM BỔ TRỢ 3: LẤY LIST TẤT CẢ YÊU CẦU ĐẶC BIỆT ĐANG CHỜ XỬ LÝ (N'CHỜ XỬ LÝ')
    // ==========================================================================
    public List<GuestRequest> getPendingRequests() {
        List<GuestRequest> list = new ArrayList<>();
        String sql = "SELECT r.request_id, r.booking_id, r.guest_id, g.full_name AS guest_name, "
                   + "r.request_type, r.request_details, r.status, r.submitted_at "
                   + "FROM GuestRequests r "
                   + "INNER JOIN Guests g ON r.guest_id = g.guest_id "
                   + "WHERE r.[status] = N'Chờ xử lý' "
                   + "ORDER BY r.submitted_at ASC";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                GuestRequest r = new GuestRequest();
                r.setRequestId(rs.getInt("request_id"));
                r.setBookingId(rs.getInt("booking_id"));
                r.setGuestId(rs.getInt("guest_id"));
                r.setRequestType(rs.getString("request_type"));
                r.setRequestDetails(rs.getString("request_details"));
                r.setStatus(rs.getString("status"));
                
                if (rs.getTimestamp("submitted_at") != null) {
                    r.setSubmittedAt(rs.getTimestamp("submitted_at").toLocalDateTime());
                }
                
                // Mượn tạm trường responseNotes làm nơi chứa tạm Tên Khách Hàng đổ ra bảng 3
                r.setResponseNotes(rs.getString("guest_name")); 
                
                list.add(r);
            }
        } catch (Exception e) {
            System.out.println("Lỗi hàm getPendingRequests: " + e.getMessage());
        }
        return list;
    }
}