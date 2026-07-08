/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dal.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Types;
import java.time.LocalDate;
import dal.DBContext;

/**
 *
 * @author Minh Thu
 */
public class GuestRequestDAO extends DBContext {

    // Kiểm tra xem đơn này còn request nào chưa đc duyệt ko
    public boolean hasPendingRequest(int bookingId) {
        String sql = "SELECT COUNT(*) AS total FROM GuestRequests WHERE booking_id = ? AND [status] = N'Chờ xử lý'";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi tại hasPendingRequest: " + e.getMessage());
        }
        return false;
    }

    //Kiểm tra xem hạng phòng mong muốn mới có còn đủ số lượng phòng trống trong khoảng thời gian cũ của khách hay không.
    public boolean checkRoomAvailabilityForChange(int roomTypeId, LocalDate checkIn, LocalDate checkOut, int requiredRooms) {
        String sql = """
            WITH BookedRooms AS (
                SELECT SUM(b.num_rooms) AS total_booked_rooms 
                FROM Bookings b 
                WHERE b.room_type_id = ? 
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
            ps.setDate(2, Date.valueOf(checkOut));
            ps.setDate(3, Date.valueOf(checkIn));
            ps.setInt(4, roomTypeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int availableRooms = rs.getInt("available_rooms");
                    return availableRooms >= requiredRooms;
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi tại checkRoomAvailabilityForChange: " + e.getMessage());
        }
        return false;
    }

    // Dùng lại hàm check đổi hạng chỉ là check out cũ -> check in mới và thêm giờ check out mới
    public boolean checkRoomAvailabilityForExtension(int roomTypeId, LocalDate oldCheckout, LocalDate newCheckout, int requiredRooms) {
        return checkRoomAvailabilityForChange(roomTypeId, oldCheckout, newCheckout, requiredRooms);
    }

    // Thêm yêu cầu vào db
    public boolean insertGuestRequest(int bookingId, int guestId, String requestType, String details,
            LocalDate reqCheckIn, LocalDate reqCheckOut, Integer targetRoomTypeId) {
        String sql = """
            INSERT INTO GuestRequests 
            (booking_id, guest_id, request_type, request_details, requested_checkin, requested_checkout, target_room_type_id, submitted_at, [status])
            VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE(), N'Chờ xử lý')
            """;

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            stm.setInt(2, guestId);
            stm.setNString(3, requestType);
            stm.setNString(4, details);

            // Validate & gán ngày check-in mong muốn (chỉ dùng cho luồng Nhận phòng sớm)
            if (reqCheckIn != null) {
                stm.setDate(5, Date.valueOf(reqCheckIn));
            } else {
                stm.setNull(5, Types.DATE);
            }

            // Validate & gán ngày check-out mong muốn (dùng cho luồng Gia hạn / Trả phòng muộn)
            if (reqCheckOut != null) {
                stm.setDate(6, Date.valueOf(reqCheckOut));
            } else {
                stm.setNull(6, Types.DATE);
            }

            // Validate & gán ID hạng phòng đích (chỉ dùng cho luồng Đổi hạng phòng)
            if (targetRoomTypeId != null && targetRoomTypeId > 0) {
                stm.setInt(7, targetRoomTypeId);
            } else {
                stm.setNull(7, Types.INTEGER);
            }

            return stm.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi tại insertGuestRequest: " + e.getMessage());
        }
        return false;
    }

    public model.Booking getBookingBasicInfoByCode(String bookingCode) {
        String sql = "SELECT b.booking_id, b.booking_code, b.guest_id, b.room_type_id, b.num_rooms, "
                + "b.checkin_date, b.checkout_date, b.booked_price_per_night, rt.type_name, "
                + "g.full_name, g.phone " // Lấy thêm cả số điện thoại từ bảng Guests
                + "FROM Bookings b "
                + "INNER JOIN RoomTypes rt ON b.room_type_id = rt.room_type_id "
                + "LEFT JOIN Guests g ON b.guest_id = g.guest_id "
                + "WHERE b.booking_code = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, bookingCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    model.Booking b = new model.Booking();
                    b.setBookingId(rs.getInt("booking_id"));
                    b.setBookingCode(rs.getString("booking_code"));
                    b.setGuestId(rs.getInt("guest_id"));
                    b.setRoomTypeId(rs.getInt("room_type_id"));
                    b.setNumRooms(rs.getInt("num_rooms"));
                    b.setCheckinDate(rs.getDate("checkin_date").toLocalDate());
                    b.setCheckoutDate(rs.getDate("checkout_date").toLocalDate());
                    b.setBookedPricePerNight(rs.getBigDecimal("booked_price_per_night"));
                    b.setRoomTypeName(rs.getString("type_name"));

                    // Khởi tạo đối tượng Guest và gán dữ liệu lấy từ DB
                    if (rs.getString("full_name") != null) {
                        model.Guest guestObj = new model.Guest();
                        guestObj.setGuestId(rs.getInt("guest_id"));
                        guestObj.setFullName(rs.getString("full_name"));
                        guestObj.setPhone(rs.getString("phone"));
                        b.setGuest(guestObj); // Đẩy đối tượng Guest vào Booking
                    }

                    return b;
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi tại getBookingBasicInfoByCode: " + e.getMessage());
        }
        return null;
    }
}
