package dao;

import dal.DBContext;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import model.Booking;
import java.sql.SQLException;

/**
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-06-16
 */
public class BookingDAO extends DBContext {

    public Booking getBookingById(int bookingId) throws Exception {
        String sql = """
                     select b.*, rt.bed_type, rt.type_name 
                     from Bookings b 
                     join RoomTypes rt on b.room_type_id = rt.room_type_id 
                     where b.booking_id = ?
                     """;

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return mapBooking(rs);
                } else {
                    throw new Exception("Không tìm thấy đơn đặt phòng");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: không tìm thấy thông tin đặt phòng");
        }
    }
    
        public String getRoomTypeNameById(int roomTypeId) throws Exception {
        String sql = """
                     select type_name 
                     from RoomTypes 
                     where room_type_id = ?
                     """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, roomTypeId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) return rs.getString("type_name");
            }
        }
        return "";
    }

    public String getBedTypeById(int roomTypeId) throws Exception {
        String sql = """
                     select bed_type 
                     from RoomTypes 
                     where room_type_id = ?
                     """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, roomTypeId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) return rs.getString("bed_type");
            }
        }
        return "";
    }

    private Booking mapBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(rs.getInt("booking_id"));
        booking.setBookingCode(rs.getString("booking_code"));
        booking.setGuestId(rs.getInt("guest_id"));

        int staffId = rs.getInt("staff_id");
        booking.setStaffId(rs.wasNull() ? null : staffId);

        booking.setRoomTypeId(rs.getInt("room_type_id"));
        booking.setNumRooms(rs.getInt("num_rooms"));
        booking.setBookedPricePerNight(rs.getBigDecimal("booked_price_per_night"));

        java.sql.Date checkinDate = rs.getDate("checkin_date");
        booking.setCheckinDate(checkinDate != null ? checkinDate.toLocalDate() : null);

        java.sql.Date checkoutDate = rs.getDate("checkout_date");
        booking.setCheckoutDate(checkoutDate != null ? checkoutDate.toLocalDate() : null);

        booking.setNumGuests(rs.getInt("num_guests"));
        booking.setStatus(rs.getString("status"));
        booking.setPaymentStatus(rs.getString("payment_status"));
        booking.setDepositAmount(rs.getBigDecimal("deposit_amount"));
        booking.setSource(rs.getString("source"));

        Timestamp confirmedAt = rs.getTimestamp("confirmed_at");
        booking.setConfirmedAt(confirmedAt != null ? confirmedAt.toLocalDateTime() : null);

        Timestamp cancelledAt = rs.getTimestamp("cancelled_at");
        booking.setCancelledAt(cancelledAt != null ? cancelledAt.toLocalDateTime() : null);

        booking.setCancellationReason(rs.getString("cancellation_reason"));

        Timestamp actualCheckin = rs.getTimestamp("actual_checkin_time");
        booking.setActualCheckinTime(actualCheckin != null ? actualCheckin.toLocalDateTime() : null);

        Timestamp actualCheckout = rs.getTimestamp("actual_checkout_time");
        booking.setActualCheckoutTime(actualCheckout != null ? actualCheckout.toLocalDateTime() : null);

        Timestamp createdAt = rs.getTimestamp("created_at");
        booking.setCreateAt(createdAt != null ? createdAt.toLocalDateTime() : null);

        return booking;
    }
}
