package dao;

import dal.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Booking;
import model.BookingRoom;
import model.Guest;
import model.GuestStay;
import model.Room;
import model.RoomType;

/**
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-06-21
 */
public class CheckoutDAO extends DBContext {

    // Business rule constants for late checkout surcharge
    public static final double LATE_CHECKOUT_BEFORE_RATE = 0.5;
    public static final double LATE_CHECKOUT_AFTER_RATE = 1.0;
    public static final int LATE_CHECKOUT_HOUR = 18;

    public List<Booking> searchActiveBookings(String keyword) throws Exception {
        List<Booking> list = new ArrayList<>();
        String sql = """
                     select b.* 
                     from Bookings b
                     left join Guests g on b.guest_id = g.guest_id
                     where b.status = N'Đã nhận phòng'
                     and (b.booking_code like ? or g.full_name like ?)
                     order by b.checkin_date desc
                     """;

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            String word = "%" + (keyword != null ? keyword.trim() : "") + "%";
            stm.setString(1, word);
            stm.setString(2, word);

            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    Booking booking = mapBooking(rs);
                    list.add(booking);
                }
            }
        } catch (SQLException e) {

            throw new Exception("Lỗi hệ thống: Không thể tìm kiếm booking.");
        }
        return list;
    }

    public Booking getBookingById(int bookingId) throws Exception {
        String sql = """
                     select b.* 
                     from Bookings b 
                     where b.booking_id = ?
                     """;

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);

            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return mapBooking(rs);
                } else {
                    throw new Exception("Không tìm thấy booking.");
                }
            }
        } catch (SQLException e) {

            throw new Exception("Lỗi hệ thống: Không thể lấy thông tin booking.");
        }
    }

    public void updateCheckoutTime(int bookingId, LocalDateTime actualCheckout) throws Exception {
        String sql = """
                     update Bookings 
                     set actual_checkout_time = ? 
                     where booking_id = ?
                     """;

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setTimestamp(1, Timestamp.valueOf(actualCheckout));
            stm.setInt(2, bookingId);
            stm.executeUpdate();
        } catch (SQLException e) {

            throw new Exception("Lỗi hệ thống: Không thể cập nhật thời gian check-out.");
        }
    }

    public void updateRoomStatusAfterCheckout(int roomNumber) throws Exception {
        String sql = "update Rooms set status = N'Đang dọn dẹp' where room_number = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, roomNumber);
            stm.executeUpdate();
        } catch (SQLException e) {

            throw new Exception("Lỗi hệ thống: Không thể cập nhật trạng thái phòng.");
        }
    }

    public double lateCheckoutSurcharge(LocalDateTime expectedCheckout, LocalDateTime actualCheckout, double roomPricePerNight) {
        if (actualCheckout == null || expectedCheckout == null) {
            return 0;
        }

        if (!actualCheckout.isAfter(expectedCheckout)) {
            return 0;
        }

        int hour = actualCheckout.getHour();

        if (hour < LATE_CHECKOUT_HOUR) {
            return roomPricePerNight * LATE_CHECKOUT_BEFORE_RATE;
        } else {
            return roomPricePerNight * LATE_CHECKOUT_AFTER_RATE;
        }
    }

    private Booking mapBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(rs.getInt("booking_id"));
        booking.setBookingCode(rs.getString("booking_code"));
        booking.setGuestId(rs.getInt("guest_id"));
        booking.setRoomTypeId(rs.getInt("room_type_id"));
        booking.setNumRooms(rs.getInt("num_rooms"));
        booking.setNumGuests(rs.getInt("num_guests"));
        booking.setBookedPricePerNight(rs.getBigDecimal("booked_price_per_night"));
        booking.setDepositAmount(rs.getBigDecimal("deposit_amount"));

        Date checkinDate = rs.getDate("checkin_date");
        booking.setCheckinDate(checkinDate != null ? checkinDate.toLocalDate() : null);

        Date checkoutDate = rs.getDate("checkout_date");
        booking.setCheckoutDate(checkoutDate != null ? checkoutDate.toLocalDate() : null);

        booking.setStatus(rs.getString("status"));
        booking.setPaymentStatus(rs.getString("payment_status"));

        Timestamp actualCheckin = rs.getTimestamp("actual_checkin_time");
        booking.setActualCheckinTime(actualCheckin != null ? actualCheckin.toLocalDateTime() : null);

        Timestamp actualCheckout = rs.getTimestamp("actual_checkout_time");
        booking.setActualCheckoutTime(actualCheckout != null ? actualCheckout.toLocalDateTime() : null);

        return booking;
    }

    public Guest getGuestByBookingId(int bookingId) throws Exception {
        String sql = """
                     select g.* 
                     from Guests g
                     join Bookings b on g.guest_id = b.guest_id
                     where b.booking_id = ?
                     """;

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);

            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    Guest guest = new Guest();
                    guest.setGuestId(rs.getInt("guest_id"));
                    guest.setFullName(rs.getString("full_name"));
                    guest.setEmail(rs.getString("email"));
                    guest.setPhone(rs.getString("phone"));
                    guest.setIdNumber(rs.getString("id_number"));
                    guest.setNationality(rs.getString("nationality"));
                    return guest;
                }
            }
        } catch (SQLException e) {

            throw new Exception("Lỗi hệ thống: Không thể lấy thông tin khách.");
        }
        return null;
    }

    public List<Room> getRoomsByBookingId(int bookingId) throws Exception {
        List<Room> list = new ArrayList<>();
        String sql = """
                     select r.room_number, r.floor, r.status, rt.type_name, rt.base_price, rt.bed_type
                     from BookingRooms br
                     join Rooms r on br.room_number = r.room_number
                     join RoomTypes rt on r.room_type_id = rt.room_type_id
                     where br.booking_id = ?
                     """;

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);

            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    Room room = new Room();
                    room.setRoomNumber(rs.getInt("room_number"));
                    room.setFloor(rs.getInt("floor"));
                    room.setStatus(rs.getString("status"));
                    list.add(room);
                }
            }
        } catch (SQLException e) {

            throw new Exception("Lỗi hệ thống: Không thể lấy danh sách phòng.");
        }
        return list;
    }

    public List<GuestStay> getGuestStaysByBookingId(int bookingId) throws Exception {
        List<GuestStay> list = new ArrayList<>();
        String sql = """
                     select gs.*
                     from GuestStays gs
                     join BookingRooms br on gs.booking_room_id = br.booking_room_id
                     where br.booking_id = ?
                     """;

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);

            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    GuestStay stay = new GuestStay();
                    stay.setStayId(rs.getInt("stay_id"));
                    stay.setBookingRoomId(rs.getInt("booking_room_id"));
                    stay.setFullName(rs.getString("full_name"));
                    stay.setPhone(rs.getString("phone"));
                    stay.setIdNumber(rs.getString("id_number"));
                    list.add(stay);
                }
            }
        } catch (SQLException e) {

            throw new Exception("Lỗi hệ thống: Không thể lấy danh sách khách lưu trú.");
        }
        return list;
    }
    
    public List<BookingRoom> getBookingRoomsByBookingId(int bookingId) throws Exception {
    List<BookingRoom> list = new ArrayList<>();

    String sql = """
                 select *
                 from BookingRooms
                 where booking_id = ?
                 order by room_number
                 """;

    try (PreparedStatement stm = connection.prepareStatement(sql)) {
        stm.setInt(1, bookingId);

        try (ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                BookingRoom br = new BookingRoom();
                br.setBookingRoomId(rs.getInt("booking_room_id"));
                br.setBookingId(rs.getInt("booking_id"));
                br.setRoomNumber(rs.getInt("room_number"));
                list.add(br);
            }
        }
    } catch (SQLException e) {
        throw new Exception("Lỗi hệ thống: Không thể lấy BookingRoom.");
    }

    return list;
}

    public RoomType getRoomTypeByBookingId(int bookingId) throws Exception {
        String sql = """
                     select rt.* 
                     from RoomTypes rt
                     join Bookings b on rt.room_type_id = b.room_type_id
                     where b.booking_id = ?
                     """;

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);

            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    RoomType rt = new RoomType();
                    rt.setRoomTypeId(rs.getInt("room_type_id"));
                    rt.setTypeName(rs.getString("type_name"));
                    rt.setDescription(rs.getString("description"));
                    rt.setCapacity(rs.getInt("capacity"));
                    rt.setBedType(rs.getString("bed_type"));
                    rt.setBedCount(rs.getInt("bed_count"));
                    rt.setAreaSqm(rs.getBigDecimal("area_sqm"));
                    rt.setBasePrice(rs.getBigDecimal("base_price"));
                    return rt;
                }
            }
        } catch (SQLException e) {

            throw new Exception("Lỗi hệ thống: Không thể lấy thông tin loại phòng.");
        }
        return null;
    }

    public String getRoomTypeImgByTypeId(int roomTypeId) throws Exception {
        String sql = """
                   select top 1 rt.image_url from RoomTypeImages rt where rt.room_type_id = ?
                   """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, roomTypeId);

            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    String imgUrl = rs.getString("image_url");
                    return imgUrl;
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy ảnh");
        }
        return null;
    }

    public List<Booking> searchActiveBookingsByCheckoutDate(LocalDate checkoutDate) throws Exception {
        List<Booking> list = new ArrayList<>();
        String sql = """
                     select b.*
                     from Bookings b 
                     where b.status = N'Đã nhận phòng'
                     and b.checkout_date = ?
                     order by b.checkin_date desc
                     """;

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setDate(1, java.sql.Date.valueOf(checkoutDate));

            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    Booking booking = mapBooking(rs);
                    list.add(booking);
                }
            }
        } catch (SQLException e) {

            throw new Exception("Lỗi hệ thống: Không thể tìm kiếm booking.");
        }
        return list;
    }

    public Map<Integer, Guest> getGuestsByBookings(List<Booking> bookings) throws Exception {
        Map<Integer, Guest> map = new HashMap<>();
        if (bookings == null || bookings.isEmpty()) {
            return map;
        }

        List<Integer> bookingIds = new ArrayList<>();
        for (Booking b : bookings) {
            bookingIds.add(b.getBookingId());
        }

        String placeholders = String.join(",", Collections.nCopies(bookingIds.size(), "?"));
        String sql = "select b.booking_id, g.full_name, g.phone, g.email "
                + "from Bookings b "
                + "left join Guests g on b.guest_id = g.guest_id "
                + "where b.booking_id in (" + placeholders + ")";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            for (int i = 0; i < bookingIds.size(); i++) {
                stm.setInt(i + 1, bookingIds.get(i));
            }
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    String fullName = rs.getString("full_name");
                    if (fullName != null) {
                        Guest guest = new Guest();
                        guest.setFullName(fullName);
                        guest.setPhone(rs.getString("phone"));
                        guest.setEmail(rs.getString("email"));
                        map.put(rs.getInt("booking_id"), guest);
                    }
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy thông tin khách.");
        }

        return map;
    }

}
