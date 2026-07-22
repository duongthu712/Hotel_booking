package dao;

import dal.DBContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Booking;
import model.BookingRoom;
import model.Guest;
import model.GuestStay;
import model.Invoice;
import model.InvoicePayment;
import model.Room;
import model.RoomType;

/**
 * @author LinhLTHE200306
 * @version 5.0
 * @since 2026-07-12
 */
public class CheckoutDAO extends DBContext {

    public static final double LATE_CHECKOUT_BEFORE_RATE = 0.5;
    public static final double LATE_CHECKOUT_AFTER_RATE = 1.0;
    public static final int LATE_CHECKOUT_HOUR = 18;

    // ========== BOOKING ==========
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
                }
                throw new Exception("Không tìm thấy booking.");
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy thông tin booking.");
        }
    }

    public String getBookingCodeById(int bookingId) throws Exception {
        String sql = "select booking_code from Bookings where booking_id = ?";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("booking_code");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy mã booking.");
        }
        return null;
    }

    private Booking mapBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(rs.getInt("booking_id"));
        booking.setBookingCode(rs.getString("booking_code"));
        booking.setGuestId(rs.getInt("guest_id"));
        booking.setStaffId(rs.getInt("staff_id"));
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
        booking.setSource(rs.getString("source"));
        return booking;
    }

    // ========== GUEST ==========
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

    // ========== ROOM ==========
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
        String sql = "select top 1 image_url from RoomTypeImages where room_type_id = ?";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, roomTypeId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("image_url");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy ảnh.");
        }
        return null;
    }

    public List<Room> getRoomsByBookingId(int bookingId) throws Exception {
        List<Room> list = new ArrayList<>();
        String sql = """
                select r.*
                from BookingRooms br
                join Rooms r on br.room_id = r.room_id
                where br.booking_id = ?
                """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    Room room = new Room();
                    room.setRoomId(rs.getInt("room_id"));
                    room.setRoomNumber(rs.getInt("room_number"));
                    room.setFloor(rs.getInt("floor"));
                    room.setStatus(rs.getString("status"));
                    room.setRoomTypeId(rs.getInt("room_type_id"));
                    list.add(room);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy danh sách phòng.");
        }
        return list;
    }

    public List<Room> getRoomsByRoomIds(List<Integer> roomIds) throws Exception {
        List<Room> list = new ArrayList<>();
        if (roomIds == null || roomIds.isEmpty()) {
            return list;
        }
        String placeholders = String.join(",", Collections.nCopies(roomIds.size(), "?"));
        String sql = "select * from Rooms where room_id in (" + placeholders + ")";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            for (int i = 0; i < roomIds.size(); i++) {
                stm.setInt(i + 1, roomIds.get(i));
            }
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    Room room = new Room();
                    room.setRoomId(rs.getInt("room_id"));
                    room.setRoomNumber(rs.getInt("room_number"));
                    room.setFloor(rs.getInt("floor"));
                    room.setStatus(rs.getString("status"));
                    room.setRoomTypeId(rs.getInt("room_type_id"));
                    list.add(room);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy thông tin phòng.");
        }
        return list;
    }

    public int getAssignedRoomsCount(int bookingId) throws Exception {
        String sql = "select count(*) from BookingRooms where booking_id = ?";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể đếm số phòng assign.");
        }
        return 0;
    }

    // ========== BOOKING ROOMS ==========
    public List<BookingRoom> getBookingRoomsByBookingId(int bookingId) throws Exception {
        List<BookingRoom> list = new ArrayList<>();
        String sql = """
                select br.*, r.room_number
                from BookingRooms br
                join Rooms r on br.room_id = r.room_id
                where br.booking_id = ?
                order by r.room_number
                """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    BookingRoom br = new BookingRoom();
                    br.setBookingRoomId(rs.getInt("booking_room_id"));
                    br.setBookingId(rs.getInt("booking_id"));
                    br.setRoomId(rs.getInt("room_id"));
                    list.add(br);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy BookingRoom.");
        }
        return list;
    }

    public List<Map<String, Object>> getRoomDetailsByRoomIds(List<Integer> roomIds) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        if (roomIds == null || roomIds.isEmpty()) {
            return list;
        }
        String placeholders = String.join(",", Collections.nCopies(roomIds.size(), "?"));
        String sql = "select br.booking_room_id, br.booking_id, br.room_id, r.room_number "
                + "from BookingRooms br "
                + "join Rooms r on br.room_id = r.room_id "
                + "where br.room_id in (" + placeholders + ") "
                + "and br.checkout_status = N'Chưa checkout'";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            for (int i = 0; i < roomIds.size(); i++) {
                stm.setInt(i + 1, roomIds.get(i));
            }
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("bookingRoomId", rs.getInt("booking_room_id"));
                    map.put("bookingId", rs.getInt("booking_id"));
                    map.put("roomId", rs.getInt("room_id"));
                    map.put("roomNumber", rs.getInt("room_number"));
                    list.add(map);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy thông tin phòng đã chọn.");
        }
        return list;
    }

    public List<Map<String, Object>> getRoomsForCheckout(String keyword) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            select br.booking_room_id, br.booking_id, br.room_id,
                   r.room_number, b.checkout_date, b.checkin_date, b.booking_code,
                   -- Lấy tên khách lưu trú trong phòng (top 1 từ GuestStays)
                   (select top 1 gs.full_name 
                    from GuestStays gs 
                    where gs.booking_room_id = br.booking_room_id) as guest_name
            from BookingRooms br
            join Bookings b on br.booking_id = b.booking_id
            join Rooms r on br.room_id = r.room_id
            where br.checkout_status = N'Chưa checkout'
            and b.status = N'Đã nhận phòng'
            """);
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" and (cast(r.room_number as varchar) like ? or exists (select 1 from GuestStays gs2 where gs2.booking_room_id = br.booking_room_id and gs2.full_name like ?))");
        } else {
            sql.append(" and cast(b.checkout_date as date) = cast(getdate() as date)");
        }
        sql.append(" order by b.checkout_date asc, r.room_number asc");
        try (PreparedStatement stm = connection.prepareStatement(sql.toString())) {
            if (keyword != null && !keyword.trim().isEmpty()) {
                String word = "%" + keyword.trim() + "%";
                stm.setString(1, word);
                stm.setString(2, word);
            }
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("bookingRoomId", rs.getInt("booking_room_id"));
                    map.put("bookingId", rs.getInt("booking_id"));
                    map.put("roomId", rs.getInt("room_id"));
                    map.put("roomNumber", rs.getInt("room_number"));
                    map.put("checkoutDate", rs.getDate("checkout_date") != null
                            ? rs.getDate("checkout_date").toLocalDate() : null);
                    map.put("checkinDate", rs.getDate("checkin_date") != null
                            ? rs.getDate("checkin_date").toLocalDate() : null);
                    map.put("bookingCode", rs.getString("booking_code"));
                    // Tên khách từ GuestStays (có thể null nếu chưa nhập)
                    String guestName = rs.getString("guest_name");
                    map.put("guestName", guestName != null ? guestName : "Chưa có thông tin");
                    list.add(map);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy danh sách phòng chờ checkout.");
        }
        return list;
    }

    public boolean isAllRoomsCheckedOut(int bookingId) throws Exception {
        String sql = """
                select count(*) from BookingRooms
                where booking_id = ? and checkout_status = N'Chưa checkout'
                """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể kiểm tra trạng thái checkout.");
        }
        return false;
    }

    public List<Map<String, Object>> getRoomNumbersByBookingId(int bookingId) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = """
            select br.booking_room_id, br.booking_id, br.room_id, r.room_number
            from BookingRooms br
            join Rooms r on br.room_id = r.room_id
            where br.booking_id = ?
            order by r.room_number
            """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("bookingRoomId", rs.getInt("booking_room_id"));
                    map.put("bookingId", rs.getInt("booking_id"));
                    map.put("roomId", rs.getInt("room_id"));
                    map.put("roomNumber", rs.getInt("room_number"));
                    list.add(map);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy danh sách phòng.");
        }
        return list;
    }

    // ========== GUEST STAYS ==========
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

    // ========== SERVICES & DAMAGES ==========
    public List<Map<String, Object>> getRoomTypeServicesWithDetails(int roomTypeId) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = """
                select rts.room_type_service_id, rts.service_id, rts.quantity, rts.is_free,
                       rs.service_name, rs.description, rs.unit_price
                from RoomTypeServices rts
                join RoomServices rs on rts.service_id = rs.service_id
                where rts.room_type_id = ? and rs.is_active = 1
                order by rs.service_name
                """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, roomTypeId);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("roomTypeServiceId", rs.getInt("room_type_service_id"));
                    map.put("serviceId", rs.getInt("service_id"));
                    map.put("serviceName", rs.getString("service_name"));
                    map.put("description", rs.getString("description"));
                    map.put("unitPrice", rs.getBigDecimal("unit_price"));
                    map.put("quantity", rs.getInt("quantity"));
                    map.put("isFree", rs.getInt("is_free"));
                    list.add(map);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy danh sách dịch vụ phòng.");
        }
        return list;
    }

    public List<Map<String, Object>> getRoomTypeAmenitiesWithDetails(int roomTypeId) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = """
                select rta.room_type_amenity_id, rta.amenity_id, rta.quantity,
                       ra.amenity_name, ra.description, ra.unit_price
                from RoomTypeAmenities rta
                join RoomAmenities ra on rta.amenity_id = ra.amenity_id
                where rta.room_type_id = ? and ra.is_active = 1
                order by ra.amenity_name
                """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, roomTypeId);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("roomTypeAmenityId", rs.getInt("room_type_amenity_id"));
                    map.put("amenityId", rs.getInt("amenity_id"));
                    map.put("amenityName", rs.getString("amenity_name"));
                    map.put("description", rs.getString("description"));
                    map.put("unitPrice", rs.getBigDecimal("unit_price"));
                    map.put("quantity", rs.getInt("quantity"));
                    list.add(map);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy danh sách tiện nghi phòng.");
        }
        return list;
    }

    public List<Map<String, Object>> getBookingServicesWithNameByBookingId(int bookingId) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = """
                select bs.*, rs.service_name
                from BookingServices bs
                join RoomTypeServices rts on bs.room_type_service_id = rts.room_type_service_id
                join RoomServices rs on rts.service_id = rs.service_id
                where bs.booking_id = ?
                order by bs.added_at
                """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("bookingServiceId", rs.getInt("booking_service_id"));
                    map.put("bookingId", rs.getInt("booking_id"));
                    map.put("roomId", rs.getInt("room_id"));
                    map.put("roomTypeServiceId", rs.getInt("room_type_service_id"));
                    map.put("serviceName", rs.getString("service_name"));
                    map.put("unitPrice", rs.getBigDecimal("unit_price"));
                    map.put("quantityUsed", rs.getInt("quantity_used"));
                    map.put("totalPrice", rs.getBigDecimal("total_price"));
                    map.put("addedAt", rs.getTimestamp("added_at") != null
                            ? rs.getTimestamp("added_at").toLocalDateTime() : null);
                    list.add(map);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy danh sách dịch vụ đã sử dụng.");
        }
        return list;
    }

    public List<Map<String, Object>> getRoomAmenityDamagesWithNameByBookingId(int bookingId) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = """
                select rad.*, ra.amenity_name
                from RoomAmenityDamages rad
                join RoomAmenities ra on rad.amenity_id = ra.amenity_id
                where rad.booking_id = ?
                order by rad.added_at
                """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("damageId", rs.getInt("damage_id"));
                    map.put("bookingId", rs.getInt("booking_id"));
                    map.put("roomId", rs.getInt("room_id"));
                    map.put("amenityId", rs.getInt("amenity_id"));
                    map.put("amenityName", rs.getString("amenity_name"));
                    map.put("quantityDamaged", rs.getInt("quantity_damaged"));
                    map.put("totalPrice", rs.getBigDecimal("total_price"));
                    map.put("addedAt", rs.getTimestamp("added_at") != null
                            ? rs.getTimestamp("added_at").toLocalDateTime() : null);
                    list.add(map);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy danh sách tiện nghi hư hỏng.");
        }
        return list;
    }

    public void insertBookingService(int bookingId, int roomTypeServiceId,
            BigDecimal unitPrice, int quantity, BigDecimal totalPrice) throws Exception {
        String sql = """
            insert into BookingServices (booking_id, room_id, room_type_service_id, unit_price, quantity_used, total_price)
            values (?, (select top 1 room_id from BookingRooms where booking_id = ?), ?, ?, ?, ?)
            """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            stm.setInt(2, bookingId);
            stm.setInt(3, roomTypeServiceId);
            stm.setBigDecimal(4, unitPrice);
            stm.setInt(5, quantity);
            stm.setBigDecimal(6, totalPrice);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể thêm dịch vụ.");
        }
    }

    public void insertRoomAmenityDamage(int bookingId, int amenityId,
            int quantity, BigDecimal totalPrice) throws Exception {
        String sql = """
            insert into RoomAmenityDamages (booking_id, room_id, amenity_id, quantity_damaged, total_price)
            values (?, (select top 1 room_id from BookingRooms where booking_id = ?), ?, ?, ?)
            """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            stm.setInt(2, bookingId);
            stm.setInt(3, amenityId);
            stm.setInt(4, quantity);
            stm.setBigDecimal(5, totalPrice);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể thêm hư hỏng.");
        }
    }

    public BigDecimal sumAllBookingServices(int bookingId) throws Exception {
        String sql = "select isnull(sum(total_price), 0) as total from BookingServices where booking_id = ?";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể tổng hợp dịch vụ.");
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal sumAllRoomAmenityDamages(int bookingId) throws Exception {
        String sql = "select isnull(sum(total_price), 0) as total from RoomAmenityDamages where booking_id = ?";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể tổng hợp hư hỏng.");
        }
        return BigDecimal.ZERO;
    }

    // ========== DEPOSIT ==========
    public LocalDateTime getDepositVerifiedAt(int bookingId) throws Exception {
        String sql = """
                select verified_at from DepositPayments
                where booking_id = ? and verification_status = N'Đã phê duyệt'
                """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = rs.getTimestamp("verified_at");
                    return ts != null ? ts.toLocalDateTime() : null;
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy ngày cọc.");
        }
        return null;
    }

    // ========== INVOICE ==========
    public Invoice getInvoiceByBookingId(int bookingId) throws Exception {
        String sql = "select * from Invoices where booking_id = ?";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return mapInvoice(rs);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy hóa đơn.");
        }
        return null;
    }

    public Invoice getUnpaidInvoiceByBookingId(int bookingId) throws Exception {
        String sql = """
                select * from Invoices
                where booking_id = ? and payment_status = N'Chưa thanh toán'
                """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return mapInvoice(rs);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể kiểm tra hóa đơn.");
        }
        return null;
    }

    /**
     * Lấy danh sách phòng đã checkout với checkout_at
     */
    private List<Map<String, Object>> getCheckedOutRoomDetails(int bookingId) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = """
        select br.room_id, br.checkout_at
        from BookingRooms br
        where br.booking_id = ? and br.checkout_status = N'Đã checkout'
        """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("roomId", rs.getInt("room_id"));
                    Timestamp ts = rs.getTimestamp("checkout_at");
                    map.put("checkoutAt", ts != null ? ts.toLocalDateTime() : null);
                    list.add(map);
                }
            }
        }
        return list;
    }

    /**
     * Tính lại TOÀN BỘ room_charges gồm 2 phần: 1) Phòng ĐÃ THỰC SỰ checkout:
     * tính đúng theo checkout_at thực tế (số đêm thực tế + phụ phí trễ giờ nếu
     * có). 2) Phòng ĐÃ ASSIGN nhưng CHƯA checkout: tạm tính theo giá ước tính
     * ban đầu (số đêm dự kiến theo booking) (Phòng chưa từng assign - coi như
     * đã huỷ ) Dùng để GHI ĐÈ Invoices.room_charges trong processCheckout,
     * tránh cộng dồn lên trên giá trị ước tính ban đầu gây tính tiền phòng 2
     * lần.
     */
    private BigDecimal recalcRoomChargesForBooking(int bookingId, Booking booking) throws Exception {
        List<Map<String, Object>> checkedOutRooms = getCheckedOutRoomDetails(bookingId);
        LocalDateTime expectedCheckout = booking.getCheckoutDate().atTime(12, 0);

        BigDecimal total = BigDecimal.ZERO;

        // 1) Phòng đã checkout thật
        for (Map<String, Object> room : checkedOutRooms) {
            LocalDateTime checkoutAt = (LocalDateTime) room.get("checkoutAt");
            if (checkoutAt == null) {
                checkoutAt = LocalDateTime.now();
            }

            long nights = Math.max(1, ChronoUnit.DAYS.between(
                    booking.getCheckinDate(), checkoutAt.toLocalDate()));

            BigDecimal roomCharge = booking.getBookedPricePerNight()
                    .multiply(BigDecimal.valueOf(nights));

            double lateCharge = lateCheckoutSurcharge(
                    expectedCheckout, checkoutAt, booking.getBookedPricePerNight().doubleValue());

            total = total.add(roomCharge).add(BigDecimal.valueOf(lateCharge));
        }

        // 2) Phòng đã assign nhưng chưa checkout: tạm tính theo giá ước tính ban đầu
        int notYetCheckedOutRooms = countRemainingRooms(bookingId);
        if (notYetCheckedOutRooms > 0) {
            long plannedNights = Math.max(1, ChronoUnit.DAYS.between(
                    booking.getCheckinDate(), booking.getCheckoutDate()));
            BigDecimal estimatedPerRoom = booking.getBookedPricePerNight()
                    .multiply(BigDecimal.valueOf(plannedNights));
            total = total.add(estimatedPerRoom.multiply(BigDecimal.valueOf(notYetCheckedOutRooms)));
        }

        return total;
    }

    public double lateCheckoutSurcharge(LocalDateTime expectedCheckout,
            LocalDateTime actualCheckout, double roomPricePerNight) {
        if (actualCheckout == null || expectedCheckout == null) {
            return 0;
        }
        if (!actualCheckout.isAfter(expectedCheckout)) {
            return 0;
        }
        int hour = actualCheckout.getHour();
        return hour < LATE_CHECKOUT_HOUR
                ? roomPricePerNight * LATE_CHECKOUT_BEFORE_RATE
                : roomPricePerNight * LATE_CHECKOUT_AFTER_RATE;
    }

    /**
     * Recalculate invoice sau khi thêm service/damage
     */
    public void recalculateInvoice(int bookingId) throws Exception {
        String sql = """
        update Invoices
        set consumable_charges = (
                select isnull(sum(total_price), 0)
                from BookingServices where booking_id = ?),
            amenity_damages = (
                select isnull(sum(total_price), 0)
                from RoomAmenityDamages where booking_id = ?),
            total_amount = room_charges
                + (select isnull(sum(total_price), 0) from BookingServices where booking_id = ?)
                + (select isnull(sum(total_price), 0) from RoomAmenityDamages where booking_id = ?),
            remaining_amount = room_charges
                + (select isnull(sum(total_price), 0) from BookingServices where booking_id = ?)
                + (select isnull(sum(total_price), 0) from RoomAmenityDamages where booking_id = ?)
                - (select isnull(sum(amount), 0) from InvoicePayments
                   where invoice_id = (select invoice_id from Invoices where booking_id = ?)
                   and (note = N'Tiền đặt cọc' OR note like N'Thanh toán trước - Phòng%'))
                - (select isnull(sum(amount), 0) from InvoicePayments
                   where invoice_id = (select invoice_id from Invoices where booking_id = ?)
                   and note not like N'Thanh toán trước - Phòng%' and note != N'Tiền đặt cọc' and note != N'Tiền phạt hủy phòng')
        where booking_id = ?
        """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            stm.setInt(2, bookingId);
            stm.setInt(3, bookingId);
            stm.setInt(4, bookingId);
            stm.setInt(5, bookingId);
            stm.setInt(6, bookingId);
            stm.setInt(7, bookingId);
            stm.setInt(8, bookingId);
            stm.setInt(9, bookingId);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể recalculate hóa đơn.");
        }
    }

    private Invoice mapInvoice(ResultSet rs) throws SQLException {
        Invoice inv = new Invoice();
        inv.setInvoiceId(rs.getInt("invoice_id"));
        inv.setBookingId(rs.getInt("booking_id"));
        inv.setRoomCharges(rs.getBigDecimal("room_charges"));
        inv.setConsumableCharges(rs.getBigDecimal("consumable_charges"));
        inv.setAmenityDamages(rs.getBigDecimal("amenity_damages"));
        inv.setTotalAmount(rs.getBigDecimal("total_amount"));
        inv.setRemainingAmount(rs.getBigDecimal("remaining_amount"));
        inv.setPaymentStatus(rs.getString("payment_status"));
        inv.setCreatedBy(rs.getInt("created_by"));
        return inv;
    }

    // ========== INVOICE PAYMENTS ==========
    public List<InvoicePayment> getInvoicePaymentsByInvoiceId(int invoiceId) throws Exception {
        List<InvoicePayment> list = new ArrayList<>();
        String sql = """
                select ip.*, sa.full_name as collector_name
                from InvoicePayments ip
                left join StaffAccounts sa on ip.collected_by = sa.staff_id
                where ip.invoice_id = ?
                order by ip.paid_at asc
                """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, invoiceId);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    InvoicePayment p = new InvoicePayment();
                    p.setPaymentId(rs.getInt("payment_id"));
                    p.setInvoiceId(rs.getInt("invoice_id"));
                    p.setAmount(rs.getBigDecimal("amount"));
                    p.setPaymentMethod(rs.getString("payment_method"));
                    p.setPaidAt(rs.getTimestamp("paid_at") != null
                            ? rs.getTimestamp("paid_at").toLocalDateTime() : null);
                    p.setCollectedBy(rs.getInt("collected_by"));
                    p.setNote(rs.getString("note"));
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy lịch sử thanh toán.");
        }
        return list;
    }

    /**
     * Thu tiền thêm tại quầy - KHÔNG tính cọc vào remaining
     */
    public void addInvoicePayment(int invoiceId, int bookingId, BigDecimal amount,
            String paymentMethod, String note, int staffId) throws Exception {
        connection.setAutoCommit(false);
        try {
            String insertSql = """
                    insert into InvoicePayments (invoice_id, amount, payment_method, collected_by, note)
                    values (?, ?, ?, ?, ?)
                    """;
            try (PreparedStatement stm = connection.prepareStatement(insertSql)) {
                stm.setInt(1, invoiceId);
                stm.setBigDecimal(2, amount);
                stm.setString(3, paymentMethod);
                stm.setInt(4, staffId);
                stm.setString(5, note);
                stm.executeUpdate();
            }

            String updateSql = """
                                update Invoices
                                set remaining_amount = total_amount 
                                - (select isnull(sum(amount), 0) from InvoicePayments 
                                   where invoice_id = ? and note = N'Tiền đặt cọc')
                                - (select isnull(sum(amount), 0) from InvoicePayments 
                                   where invoice_id = ? and note like N'Thanh toán trước - Phòng%')
                                - (select isnull(sum(amount), 0) from InvoicePayments 
                                   where invoice_id = ? and note not like N'Thanh toán trước - Phòng%' and note != N'Tiền đặt cọc' and note != N'Tiền phạt hủy phòng')
                                where invoice_id = ?
                                """;
            try (PreparedStatement stm = connection.prepareStatement(updateSql)) {
                stm.setInt(1, invoiceId);
                stm.setInt(2, invoiceId);
                stm.setInt(3, invoiceId);
                stm.setInt(4, invoiceId);
                stm.executeUpdate();
            }

            // Nếu remaining <= 0 → đánh dấu đã thanh toán
            String checkSql = "select remaining_amount from Invoices where invoice_id = ?";
            try (PreparedStatement stm = connection.prepareStatement(checkSql)) {
                stm.setInt(1, invoiceId);
                try (ResultSet rs = stm.executeQuery()) {
                    if (rs.next() && rs.getBigDecimal("remaining_amount").compareTo(BigDecimal.ZERO) <= 0) {
                        String paidSql = """
                                update Invoices set payment_status = N'Đã thanh toán' where invoice_id = ?
                                """;
                        try (PreparedStatement ps = connection.prepareStatement(paidSql)) {
                            ps.setInt(1, invoiceId);
                            ps.executeUpdate();
                        }
                        String bookingPaidSql = """
                                update Bookings set payment_status = N'Đã thanh toán' where booking_id = ?
                                """;
                        try (PreparedStatement ps = connection.prepareStatement(bookingPaidSql)) {
                            ps.setInt(1, bookingId);
                            ps.executeUpdate();
                        }
                    }
                }
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new Exception("Lỗi hệ thống: Không thể thu tiền. " + e.getMessage());
        } finally {
            connection.setAutoCommit(true);
        }
    }

    // ========== CHECKOUT ==========
    public void processCheckout(int bookingId, List<Integer> roomIds, int staffId) throws Exception {
        connection.setAutoCommit(false);
        try {
            Booking booking = getBookingById(bookingId);

            Invoice invoice = getInvoiceByBookingId(bookingId);
            if (invoice == null) {
                throw new Exception("Không tìm thấy hóa đơn cho booking này.");
            }

            // Reset invoice về chưa thanh toán nếu cần
            if ("Đã thanh toán".equals(invoice.getPaymentStatus())) {
                String resetSql = """
                update Invoices
                set payment_status = N'Chưa thanh toán'
                where invoice_id = ?
                """;
                try (PreparedStatement stm = connection.prepareStatement(resetSql)) {
                    stm.setInt(1, invoice.getInvoiceId());
                    stm.executeUpdate();
                }
            }

            // ========== 2. TÍNH TIỀN CỌC CHO LẦN CHECKOUT NÀY ==========
            int numRoomsBooking = booking.getNumRooms();
            BigDecimal totalDeposit = booking.getDepositAmount() != null
                    ? booking.getDepositAmount() : BigDecimal.ZERO;

            int checkedOutRoomsCount = getCheckedOutRoomsCount(bookingId);
            int remainingRoomsBefore = countRemainingRooms(bookingId);
            boolean isLastCheckout = (remainingRoomsBefore - roomIds.size()) == 0;

            BigDecimal depositPerRoom = BigDecimal.ZERO;
            if (numRoomsBooking > 0) {
                depositPerRoom = totalDeposit
                        .divide(BigDecimal.valueOf(numRoomsBooking), 2, RoundingMode.HALF_UP);
            }

            // Phòng đã đặt nhưng CHƯA TỪNG được assign vào BookingRooms coi như đã huỷ.
            // Phần cọc tương ứng của các phòng này phải được giữ lại làm phạt huỷ phòng,
            // KHÔNG được cấn trừ vào công nợ (khác với phần lẻ do làm tròn của các phòng
            // đã thực sự ở và checkout, phần lẻ đó phải được trả lại đủ 100%).
            int assignedRoomsCount = getAssignedRoomsCount(bookingId);
            int unassignedRooms = Math.max(0, numRoomsBooking - assignedRoomsCount);
            BigDecimal cancelPenaltyDeposit = depositPerRoom.multiply(BigDecimal.valueOf(unassignedRooms));
            BigDecimal depositForAssignedRooms = totalDeposit.subtract(cancelPenaltyDeposit);

            BigDecimal depositThisCheckout = depositPerRoom
                    .multiply(BigDecimal.valueOf(roomIds.size()));

            BigDecimal totalUsedDeposit = depositPerRoom
                    .multiply(BigDecimal.valueOf(checkedOutRoomsCount));
            // Số cọc còn lại dành cho các phòng ĐÃ ASSIGN (không tính phần phạt huỷ phòng
            // của các phòng chưa từng assign)
            BigDecimal depositRemainingForAssigned = depositForAssignedRooms.subtract(totalUsedDeposit);

            if (isLastCheckout) {
                // Trả hết phần cọc còn lại của các phòng đã assign + đã checkout, kể cả
                // số lẻ phát sinh do làm tròn. Phần cancelPenaltyDeposit (nếu có) vẫn còn
                // nguyên trong bản ghi 'Tiền đặt cọc' gốc và sẽ được đổi thành
                // 'Tiền phạt hủy phòng' ở bước 8 bên dưới.
                depositThisCheckout = depositRemainingForAssigned;
            }

            // ========== 3. LẤY SỐ PHÒNG TRƯỚC KHI UPDATE STATUS ==========
            // Lấy room numbers từ DB trước khi update checkout_status
            List<Integer> roomNumbers = new ArrayList<>();
            String getRoomNumbersSql = """
            select r.room_number 
            from BookingRooms br
            join Rooms r on br.room_id = r.room_id
            where br.booking_id = ? and br.room_id in ("""
                    + String.join(",", Collections.nCopies(roomIds.size(), "?")) + ")";

            try (PreparedStatement stm = connection.prepareStatement(getRoomNumbersSql)) {
                stm.setInt(1, bookingId);
                for (int i = 0; i < roomIds.size(); i++) {
                    stm.setInt(i + 2, roomIds.get(i));
                }
                try (ResultSet rs = stm.executeQuery()) {
                    while (rs.next()) {
                        roomNumbers.add(rs.getInt("room_number"));
                    }
                }
            }

            String roomNote = "Thanh toán trước - Phòng " + roomNumbers.toString()
                    .replace("[", "").replace("]", "");

            // ========== 4. UPDATE BookingRooms ==========
            String placeholders = String.join(",", Collections.nCopies(roomIds.size(), "?"));
            String updateBrSql = "update BookingRooms set checkout_status = N'Đã checkout', "
                    + "checkout_at = GETDATE() "
                    + "where room_id in (" + placeholders + ") "
                    + "and booking_id = ? "
                    + "and checkout_status = N'Chưa checkout'";
            try (PreparedStatement stm = connection.prepareStatement(updateBrSql)) {
                for (int i = 0; i < roomIds.size(); i++) {
                    stm.setInt(i + 1, roomIds.get(i));
                }
                stm.setInt(roomIds.size() + 1, bookingId);
                stm.executeUpdate();
            }

            // ========== 5. Update room status ==========
            updateRoomStatusAfterCheckout(bookingId, roomIds);

            // ========== 6. GHI ĐÈ room_charges (tính lại từ đầu, KHÔNG cộng dồn) ==========
            // room_charges lúc tạo invoice (duyệt cọc / walk-in) chỉ là số ước tính ban đầu.
            // Ở đây ta tính lại chính xác: phòng đã checkout dùng số tiền thật (theo
            // checkout_at thực tế), phòng chưa checkout dùng giá ước tính ban đầu -
            // để tổng tiền phòng không bị tụt xuống giữa chừng khi mới checkout 1 phần
            // số phòng, đồng thời không bị cộng dồn 2 lần khi checkout xong hết.
            BigDecimal roomChargesTotal = recalcRoomChargesForBooking(bookingId, booking);
            String updateInvoiceSql = """
            update Invoices
            set room_charges = ?
            where booking_id = ?
            """;
            try (PreparedStatement stm = connection.prepareStatement(updateInvoiceSql)) {
                stm.setBigDecimal(1, roomChargesTotal);
                stm.setInt(2, bookingId);
                stm.executeUpdate();
            }

            // ========== 7. Ghi nhận cọc đã sử dụng ==========
            if (depositThisCheckout.compareTo(BigDecimal.ZERO) > 0) {
                String insertPaymentSql = """
                insert into InvoicePayments (invoice_id, amount, payment_method, collected_by, note)
                values (?, ?, N'Chuyển khoản', ?, ?)
                """;
                try (PreparedStatement stm = connection.prepareStatement(insertPaymentSql)) {
                    stm.setInt(1, invoice.getInvoiceId());
                    stm.setBigDecimal(2, depositThisCheckout);
                    stm.setInt(3, staffId);
                    stm.setString(4, roomNote);
                    stm.executeUpdate();
                }

                String updateDepositSql = """
                update InvoicePayments
                set amount = amount - ?
                where invoice_id = ? and note = N'Tiền đặt cọc'
                """;
                try (PreparedStatement stm = connection.prepareStatement(updateDepositSql)) {
                    stm.setBigDecimal(1, depositThisCheckout);
                    stm.setInt(2, invoice.getInvoiceId());
                    stm.executeUpdate();
                }

                String deleteZeroDepositSql = """
                delete from InvoicePayments
                where invoice_id = ? and note = N'Tiền đặt cọc' and amount <= 0
                """;
                try (PreparedStatement stm = connection.prepareStatement(deleteZeroDepositSql)) {
                    stm.setInt(1, invoice.getInvoiceId());
                    stm.executeUpdate();
                }
            }

            // ========== 8. Nếu là checkout cuối ==========
            if (isLastCheckout) {
                String checkDepositSql = """
                select payment_id, amount from InvoicePayments
                where invoice_id = ? and note = N'Tiền đặt cọc' and amount > 0
                """;
                try (PreparedStatement stm = connection.prepareStatement(checkDepositSql)) {
                    stm.setInt(1, invoice.getInvoiceId());
                    try (ResultSet rs = stm.executeQuery()) {
                        if (rs.next()) {
                            int paymentId = rs.getInt("payment_id");
                            String updateNoteSql = """
                            update InvoicePayments set note = N'Tiền phạt hủy phòng'
                            where payment_id = ?
                            """;
                            try (PreparedStatement ps = connection.prepareStatement(updateNoteSql)) {
                                ps.setInt(1, paymentId);
                                ps.executeUpdate();
                            }
                        }
                    }
                }

                String deleteZeroSql = """
                delete from InvoicePayments
                where invoice_id = ? and note = N'Tiền đặt cọc' and amount = 0
                """;
                try (PreparedStatement stm = connection.prepareStatement(deleteZeroSql)) {
                    stm.setInt(1, invoice.getInvoiceId());
                    stm.executeUpdate();
                }

                String updateBookingSql = """
                update Bookings
                set status = N'Đã trả phòng',
                    actual_checkout_time = GETDATE()
                where booking_id = ?
                """;
                try (PreparedStatement stm = connection.prepareStatement(updateBookingSql)) {
                    stm.setInt(1, bookingId);
                    stm.executeUpdate();
                }
            }

            // ========== 9. Recalculate remaining ==========
            recalculateInvoice(bookingId);

            // ========== 10. Nếu remaining <= 0 → đánh dấu đã thanh toán ==========
            if (isLastCheckout) {
                String checkRemainingSql = "select remaining_amount from Invoices where invoice_id = ?";

                try (PreparedStatement stm = connection.prepareStatement(checkRemainingSql)) {
                    stm.setInt(1, invoice.getInvoiceId());
                    try (ResultSet rs = stm.executeQuery()) {
                        if (rs.next() && rs.getBigDecimal("remaining_amount").compareTo(BigDecimal.ZERO) <= 0) {
                            String paidSql = "update Invoices set payment_status = N'Đã thanh toán' where invoice_id = ?";
                            try (PreparedStatement ps = connection.prepareStatement(paidSql)) {
                                ps.setInt(1, invoice.getInvoiceId());
                                ps.executeUpdate();
                            }
                            String bookingPaidSql = "update Bookings set payment_status = N'Đã thanh toán' where booking_id = ?";
                            try (PreparedStatement ps = connection.prepareStatement(bookingPaidSql)) {
                                ps.setInt(1, bookingId);
                                ps.executeUpdate();
                            }
                        }
                    }
                }
            }

            connection.commit();

        } catch (Exception e) {
            connection.rollback();
            throw new Exception("Lỗi hệ thống: Không thể xử lý checkout. " + e.getMessage());
        } finally {
            connection.setAutoCommit(true);
        }
    }

    /**
     * Lấy dịch vụ đã sử dụng của 1 phòng cụ thể trong booking
     */
    public List<Map<String, Object>> getBookingServicesByRoomId(int bookingId, int roomId) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = """
            select bs.*, rs.service_name
            from BookingServices bs
            join RoomTypeServices rts on bs.room_type_service_id = rts.room_type_service_id
            join RoomServices rs on rts.service_id = rs.service_id
            where bs.booking_id = ? and bs.room_id = ?
            order by bs.added_at
            """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            stm.setInt(2, roomId);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("bookingServiceId", rs.getInt("booking_service_id"));
                    map.put("bookingId", rs.getInt("booking_id"));
                    map.put("roomId", rs.getInt("room_id"));
                    map.put("roomTypeServiceId", rs.getInt("room_type_service_id"));
                    map.put("serviceName", rs.getString("service_name"));
                    map.put("unitPrice", rs.getBigDecimal("unit_price"));
                    map.put("quantityUsed", rs.getInt("quantity_used"));
                    map.put("totalPrice", rs.getBigDecimal("total_price"));
                    map.put("addedAt", rs.getTimestamp("added_at") != null
                            ? rs.getTimestamp("added_at").toLocalDateTime() : null);
                    list.add(map);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy dịch vụ của phòng.");
        }
        return list;
    }

    /**
     * Lấy tiện nghi hư hỏng của 1 phòng cụ thể trong booking
     */
    public List<Map<String, Object>> getRoomAmenityDamagesByRoomId(int bookingId, int roomId) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = """
            select rad.*, ra.amenity_name
            from RoomAmenityDamages rad
            join RoomAmenities ra on rad.amenity_id = ra.amenity_id
            where rad.booking_id = ? and rad.room_id = ?
            order by rad.added_at
            """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            stm.setInt(2, roomId);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("damageId", rs.getInt("damage_id"));
                    map.put("bookingId", rs.getInt("booking_id"));
                    map.put("roomId", rs.getInt("room_id"));
                    map.put("amenityId", rs.getInt("amenity_id"));
                    map.put("amenityName", rs.getString("amenity_name"));
                    map.put("quantityDamaged", rs.getInt("quantity_damaged"));
                    map.put("totalPrice", rs.getBigDecimal("total_price"));
                    map.put("addedAt", rs.getTimestamp("added_at") != null
                            ? rs.getTimestamp("added_at").toLocalDateTime() : null);
                    list.add(map);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy hư hỏng của phòng.");
        }
        return list;
    }

    /**
     * Tính room charge cho 1 phòng cụ thể (dùng để hiển thị trên invoice.jsp)
     */
    public BigDecimal calculateRoomChargeForRoom(int bookingId, int roomId, int staffId) throws Exception {
        Booking booking = getBookingById(bookingId);

        // Lấy checkout_at của phòng này
        String sql = "select checkout_at from BookingRooms where booking_id = ? and room_id = ? and checkout_status = N'Đã checkout'";
        LocalDateTime checkoutAt = null;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            stm.setInt(2, roomId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = rs.getTimestamp("checkout_at");
                    checkoutAt = ts != null ? ts.toLocalDateTime() : LocalDateTime.now();
                }
            }
        }

        if (checkoutAt == null) {
            checkoutAt = LocalDateTime.now();
        }

        // Số đêm
        long nights = Math.max(1, ChronoUnit.DAYS.between(booking.getCheckinDate(), checkoutAt.toLocalDate()));

        // Tiền phòng cho phòng này
        BigDecimal roomCharge = booking.getBookedPricePerNight().multiply(BigDecimal.valueOf(nights));

        // Late charge cho phòng này
        LocalDateTime expectedCheckout = booking.getCheckoutDate().atTime(12, 0);
        double latePerRoom = lateCheckoutSurcharge(expectedCheckout, checkoutAt, booking.getBookedPricePerNight().doubleValue());

        return roomCharge.add(BigDecimal.valueOf(latePerRoom));
    }

    /**
     * Tính deposit đã dùng cho số phòng đã checkout (tính đến hiện tại)
     */
    public BigDecimal calculateDepositUsedSoFar(int bookingId) throws Exception {
        Booking booking = getBookingById(bookingId);
        int checkedOutCount = getCheckedOutRoomsCount(bookingId);
        int numRooms = booking.getNumRooms();
        BigDecimal totalDeposit = booking.getDepositAmount() != null ? booking.getDepositAmount() : BigDecimal.ZERO;

        if (numRooms <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal depositPerRoom = totalDeposit.divide(BigDecimal.valueOf(numRooms), 2, RoundingMode.HALF_UP);
        return depositPerRoom.multiply(BigDecimal.valueOf(checkedOutCount));
    }

    private int getCheckedOutRoomsCount(int bookingId) throws Exception {
        String sql = """
            select count(*) from BookingRooms
            where booking_id = ? and checkout_status = N'Đã checkout'
            """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể đếm phòng đã checkout.");
        }
        return 0;
    }

    private BigDecimal getDepositRemainingAmount(int invoiceId) throws Exception {
        String sql = """
            select isnull(sum(amount), 0) as total
            from InvoicePayments
            where invoice_id = ? and note = N'Tiền đặt cọc'
            """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, invoiceId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy số cọc còn lại.");
        }
        return BigDecimal.ZERO;
    }

    private int countRemainingRooms(int bookingId) throws Exception {
        String sql = """
            select count(*) from BookingRooms
            where booking_id = ? and checkout_status = N'Chưa checkout'
            """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể đếm phòng còn lại.");
        }
        return 0;
    }

    public void updateRoomStatusAfterCheckout(int bookingId, List<Integer> roomIds) throws Exception {
        if (roomIds == null || roomIds.isEmpty()) {
            return;
        }
        String placeholders = String.join(",", Collections.nCopies(roomIds.size(), "?"));

        String checkDamageSql = "select distinct room_id from RoomAmenityDamages "
                + "where booking_id = ? and room_id in (" + placeholders + ")";
        List<Integer> damagedRoomIds = new ArrayList<>();
        try (PreparedStatement stm = connection.prepareStatement(checkDamageSql)) {
            stm.setInt(1, bookingId);
            for (int i = 0; i < roomIds.size(); i++) {
                stm.setInt(i + 2, roomIds.get(i));
            }
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    damagedRoomIds.add(rs.getInt("room_id"));
                }
            }
        }

        String updateSql = "update Rooms set status = ? where room_id = ?";
        try (PreparedStatement stm = connection.prepareStatement(updateSql)) {
            for (int roomId : roomIds) {
                stm.setString(1, damagedRoomIds.contains(roomId) ? "Đang bảo trì" : "Đang dọn dẹp");
                stm.setInt(2, roomId);
                stm.addBatch();
            }
            stm.executeBatch();
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể cập nhật trạng thái phòng.");
        }
    }

    public void insertBookingService(int bookingId, int roomId, int roomTypeServiceId,
            BigDecimal unitPrice, int quantity, BigDecimal totalPrice) throws Exception {
        String sql = """
        insert into BookingServices (booking_id, room_id, room_type_service_id, unit_price, quantity_used, total_price)
        values (?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            stm.setInt(2, roomId);
            stm.setInt(3, roomTypeServiceId);
            stm.setBigDecimal(4, unitPrice);
            stm.setInt(5, quantity);
            stm.setBigDecimal(6, totalPrice);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể thêm dịch vụ.");
        }
    }

    public void insertRoomAmenityDamage(int bookingId, int roomId, int amenityId,
            int quantity, BigDecimal totalPrice) throws Exception {
        String sql = """
        insert into RoomAmenityDamages (booking_id, room_id, amenity_id, quantity_damaged, total_price)
        values (?, ?, ?, ?, ?)
        """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            stm.setInt(2, roomId);
            stm.setInt(3, amenityId);
            stm.setInt(4, quantity);
            stm.setBigDecimal(5, totalPrice);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể thêm hư hỏng.");
        }
    }

    // ========== BILLING ==========
    public List<Map<String, Object>> searchInvoices(String keyword, LocalDate fromDate,
            LocalDate toDate, String status) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
                select i.invoice_id, i.booking_id, b.booking_code, b.status as booking_status,
                       i.room_charges, i.consumable_charges, i.amenity_damages,
                       i.total_amount, i.remaining_amount, i.payment_status,
                       (select isnull(sum(amount), 0) from InvoicePayments 
                          where invoice_id = i.invoice_id 
                          and note != N'Tiền đặt cọc') as total_paid
                from Invoices i
                join Bookings b on i.booking_id = b.booking_id
                where 1 = 1
                """);
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" and b.booking_code like ?");
            params.add("%" + keyword.trim() + "%");
        }
        if (fromDate != null) {
            sql.append(" and exists (select 1 from InvoicePayments ip where ip.invoice_id = i.invoice_id and ip.paid_at >= ?)");
            params.add(java.sql.Date.valueOf(fromDate));
        }
        if (toDate != null) {
            sql.append(" and exists (select 1 from InvoicePayments ip where ip.invoice_id = i.invoice_id and ip.paid_at < ?)");
            params.add(java.sql.Date.valueOf(toDate.plusDays(1)));
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" and i.payment_status = ?");
            params.add(status);
        }
        sql.append(" order by i.invoice_id desc");
        try (PreparedStatement stm = connection.prepareStatement(sql.toString())) {
            int idx = 1;
            for (Object p : params) {
                stm.setObject(idx++, p);
            }
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("invoiceId", rs.getInt("invoice_id"));
                    map.put("bookingId", rs.getInt("booking_id"));
                    map.put("bookingCode", rs.getString("booking_code"));
                    map.put("bookingStatus", rs.getString("booking_status"));
                    map.put("roomCharges", rs.getBigDecimal("room_charges"));
                    map.put("consumableCharges", rs.getBigDecimal("consumable_charges"));
                    map.put("amenityDamages", rs.getBigDecimal("amenity_damages"));
                    map.put("totalAmount", rs.getBigDecimal("total_amount"));
                    map.put("remainingAmount", rs.getBigDecimal("remaining_amount"));
                    map.put("totalPaid", rs.getBigDecimal("total_paid"));
                    map.put("paymentStatus", rs.getString("payment_status"));
                    list.add(map);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể tìm kiếm hóa đơn. " + e.getMessage());
        }
        return list;
    }

    public Map<String, Object> getInvoiceDetailById(int invoiceId) throws Exception {
        String sql = """
            select i.*, b.booking_code, b.status as booking_status, sa.full_name as staff_name
            from Invoices i
            join Bookings b on i.booking_id = b.booking_id
            left join StaffAccounts sa on i.created_by = sa.staff_id
            where i.invoice_id = ?
            """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, invoiceId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    int bookingId = rs.getInt("booking_id");
                    Map<String, Object> map = new HashMap<>();
                    map.put("invoice", mapInvoice(rs));
                    map.put("bookingCode", rs.getString("booking_code"));
                    map.put("bookingStatus", rs.getString("booking_status"));
                    map.put("staffName", rs.getString("staff_name"));
                    map.put("payments", getInvoicePaymentsByInvoiceId(invoiceId));

                    // ✅ THÊM 2 DÒNG NÀY ĐỂ LẤY CHI TIẾT DỊCH VỤ VÀ HƯ HỎNG
                    map.put("services", getBookingServicesWithNameByBookingId(bookingId));
                    map.put("damages", getRoomAmenityDamagesWithNameByBookingId(bookingId));

                    return map;
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy chi tiết hóa đơn. " + e.getMessage());
        }
        return null;
    }

    public Map<String, Object> getInvoiceStats(LocalDate fromDate, LocalDate toDate) throws Exception {
        Map<String, Object> stats = new HashMap<>();
        StringBuilder sql = new StringBuilder("""
                select
                    count(*) as total_count,
                    sum(case when payment_status = N'Đã thanh toán' then 1 else 0 end) as paid_count,
                    sum(case when payment_status = N'Chưa thanh toán' then 1 else 0 end) as unpaid_count,
                    sum(case when payment_status = N'Đã thanh toán' then total_amount else 0 end) as revenue
                from Invoices
                where 1 = 1
                """);
        List<Object> params = new ArrayList<>();
        if (fromDate != null) {
            sql.append(" and exists (select 1 from InvoicePayments ip where ip.invoice_id = invoice_id and ip.paid_at >= ?)");
            params.add(java.sql.Date.valueOf(fromDate));
        }
        if (toDate != null) {
            sql.append(" and exists (select 1 from InvoicePayments ip where ip.invoice_id = invoice_id and ip.paid_at < ?)");
            params.add(java.sql.Date.valueOf(toDate.plusDays(1)));
        }
        try (PreparedStatement stm = connection.prepareStatement(sql.toString())) {
            int idx = 1;
            for (Object p : params) {
                stm.setObject(idx++, p);
            }
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    stats.put("totalCount", rs.getInt("total_count"));
                    stats.put("paidCount", rs.getInt("paid_count"));
                    stats.put("unpaidCount", rs.getInt("unpaid_count"));
                    stats.put("revenue", rs.getBigDecimal("revenue"));
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy thống kê hóa đơn. " + e.getMessage());
        }
        return stats;
    }

}
