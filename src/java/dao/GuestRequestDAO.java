package dao;

import dal.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Types;
import java.time.LocalDate;

public class GuestRequestDAO extends DBContext {

    // 1. Kiểm tra đơn hàng có yêu cầu đang chờ xử lý không
    public boolean hasPendingRequest(int bookingId) {
        String sql = "SELECT COUNT(*) AS total FROM GuestRequests WHERE booking_id = ? AND [status] = N'Chờ xử lý'";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) return rs.getInt("total") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 2. Hàm kiểm tra phòng trống (Dùng chung cho Đổi hạng phòng và Gia hạn)
    // bookingIdToExclude: dùng để loại trừ đơn hiện tại khi gia hạn
    public boolean checkRoomAvailability(int roomTypeId, LocalDate checkIn, LocalDate checkOut, int requiredRooms, Integer bookingIdToExclude) {
        String sql = """
            WITH BookedRooms AS (
                SELECT SUM(b.num_rooms) AS total_booked_rooms 
                FROM Bookings b 
                WHERE b.room_type_id = ? 
                  AND b.booking_id <> ISNULL(?, -1) 
                  AND b.status IN (N'Đã xác nhận', N'Đã nhận phòng', N'Chờ xử lý') 
                  AND b.checkin_date < ? 
                  AND b.checkout_date > ?
            ), 
            TotalActiveRooms AS (
                SELECT COUNT(room_id) AS total_rooms 
                FROM Rooms 
                WHERE room_type_id = ? AND is_active = 1
            )
            SELECT (ISNULL(tar.total_rooms, 0) - ISNULL(br.total_booked_rooms, 0)) AS available_rooms
            FROM TotalActiveRooms tar, BookedRooms br
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomTypeId);
            if (bookingIdToExclude != null) ps.setInt(2, bookingIdToExclude);
            else ps.setNull(2, Types.INTEGER);
            ps.setDate(3, Date.valueOf(checkOut));
            ps.setDate(4, Date.valueOf(checkIn));
            ps.setInt(5, roomTypeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("available_rooms") >= requiredRooms;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 3. Insert Request
    public boolean insertGuestRequest(int bookingId, int guestId, String requestType, String details,
                                     LocalDate reqCheckIn, LocalDate reqCheckOut, Integer targetRoomTypeId) {
        String sql = "INSERT INTO GuestRequests (booking_id, guest_id, request_type, request_details, requested_checkin, requested_checkout, target_room_type_id, submitted_at, [status]) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE(), N'Chờ xử lý')";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            stm.setInt(2, guestId);
            stm.setNString(3, requestType);
            stm.setNString(4, details);
            stm.setObject(5, reqCheckIn != null ? Date.valueOf(reqCheckIn) : null);
            stm.setObject(6, reqCheckOut != null ? Date.valueOf(reqCheckOut) : null);
            stm.setObject(7, targetRoomTypeId);

            return stm.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 4. Lấy thông tin Booking
    public model.Booking getBookingBasicInfoByCode(String bookingCode) {
        String sql = "SELECT b.*, rt.type_name, g.full_name, g.phone FROM Bookings b "
                   + "INNER JOIN RoomTypes rt ON b.room_type_id = rt.room_type_id "
                   + "LEFT JOIN Guests g ON b.guest_id = g.guest_id WHERE b.booking_code = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, bookingCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    model.Booking b = new model.Booking();
                    b.setBookingId(rs.getInt("booking_id"));
                    b.setBookingCode(rs.getString("booking_code"));
                    b.setStatus(rs.getString("status"));
                    b.setGuestId(rs.getInt("guest_id"));
                    b.setRoomTypeId(rs.getInt("room_type_id"));
                    b.setNumRooms(rs.getInt("num_rooms"));
                    b.setCheckinDate(rs.getDate("checkin_date").toLocalDate());
                    b.setCheckoutDate(rs.getDate("checkout_date").toLocalDate());
                    b.setBookedPricePerNight(rs.getBigDecimal("booked_price_per_night"));
                    b.setRoomTypeName(rs.getString("type_name"));
                    return b;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}