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
import model.BookingService;
import model.Guest;
import model.GuestStay;
import model.Invoice;
import model.Room;
import model.RoomAmenityDamage;
import model.RoomType;
import model.StaffAccount;

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

    public LocalDateTime getDepositVerifiedAt(int bookingId) throws Exception {
        String sql = """
                 select verified_at from DepositPayments 
                 where booking_id = ? and verification_status = N'Đã phê duyệt'
                 """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    java.sql.Timestamp ts = rs.getTimestamp("verified_at");
                    return ts != null ? ts.toLocalDateTime() : null;
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy ngày cọc.");
        }
        return null;
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

    public List<BookingService> getBookingServicesByBookingId(int bookingId) throws Exception {
        List<BookingService> list = new ArrayList<>();
        String sql = """
                     select bs.*
                     from BookingServices bs
                     where bs.booking_id = ?
                     order by bs.added_at
                     """;

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    BookingService bs = new BookingService();
                    bs.setBookingServiceId(rs.getInt("booking_service_id"));
                    bs.setBookingId(rs.getInt("booking_id"));
                    bs.setRoomTypeServiceId(rs.getInt("room_type_service_id"));
                    bs.setUnitPrice(rs.getBigDecimal("unit_price"));
                    bs.setQuantityUsed(rs.getInt("quantity_used"));
                    bs.setTotalPrice(rs.getBigDecimal("total_price"));
                    bs.setAddedAt(rs.getTimestamp("added_at") != null
                            ? rs.getTimestamp("added_at").toLocalDateTime() : null);
                    list.add(bs);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy danh sách dịch vụ đã sử dụng.");
        }
        return list;
    }

    public List<RoomAmenityDamage> getRoomAmenityDamagesByBookingId(int bookingId) throws Exception {
        List<RoomAmenityDamage> list = new ArrayList<>();
        String sql = """
                     select rad.*
                     from RoomAmenityDamages rad
                     where rad.booking_id = ?
                     order by rad.added_at
                     """;

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    RoomAmenityDamage rad = new RoomAmenityDamage();
                    rad.setDamageId(rs.getInt("damage_id"));
                    rad.setBookingId(rs.getInt("booking_id"));
                    rad.setAmenityId(rs.getInt("amenity_id"));
                    rad.setQuantityDamaged(rs.getInt("quantity_damaged"));
                    rad.setTotalPrice(rs.getBigDecimal("total_price"));
                    rad.setAddedAt(rs.getTimestamp("added_at") != null
                            ? rs.getTimestamp("added_at").toLocalDateTime() : null);
                    list.add(rad);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy danh sách tiện nghi hư hỏng.");
        }
        return list;
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

    public void createInvoice(Invoice invoice, List<BookingService> services,
            List<RoomAmenityDamage> damages, List<Room> rooms) throws Exception {
        connection.setAutoCommit(false);
        try {
            String invoiceSql = """
            insert into Invoices (booking_id, room_charges, consumable_charges, 
                amenity_damages, deposit_deducted, total_amount, remaining_amount,
                payment_status, payment_method, paid_at, created_by)
            values (?, ?, ?, ?, ?, ?, 0, N'Đã thanh toán', ?, GETDATE(), ?)
            """;

            try (PreparedStatement stm = connection.prepareStatement(invoiceSql,
                    java.sql.Statement.RETURN_GENERATED_KEYS)) {
                stm.setInt(1, invoice.getBookingId());
                stm.setBigDecimal(2, invoice.getRoomCharges());
                stm.setBigDecimal(3, invoice.getConsumableCharges());
                stm.setBigDecimal(4, invoice.getAmenityDamages());
                stm.setBigDecimal(5, invoice.getDepositDeducted());
                stm.setBigDecimal(6, invoice.getTotalAmount());
                stm.setString(7, invoice.getPaymentMethod());
                stm.setInt(8, invoice.getCreatedBy());
                stm.executeUpdate();
            }

            if (services != null && !services.isEmpty()) {
                String serviceSql = """
                insert into BookingServices (booking_id, room_type_service_id, unit_price, 
                    quantity_used, total_price, added_at)
                values (?, ?, ?, ?, ?, GETDATE())
                """;
                try (PreparedStatement stm = connection.prepareStatement(serviceSql)) {
                    for (BookingService s : services) {
                        stm.setInt(1, s.getBookingId());
                        stm.setInt(2, s.getRoomTypeServiceId());
                        stm.setBigDecimal(3, s.getUnitPrice());
                        stm.setInt(4, s.getQuantityUsed());
                        stm.setBigDecimal(5, s.getTotalPrice());
                        stm.addBatch();
                    }
                    stm.executeBatch();
                }
            }

            if (damages != null && !damages.isEmpty()) {
                String damageSql = """
                insert into RoomAmenityDamages (booking_id, amenity_id, quantity_damaged, 
                    total_price, added_at)
                values (?, ?, ?, ?, GETDATE())
                """;
                try (PreparedStatement stm = connection.prepareStatement(damageSql)) {
                    for (RoomAmenityDamage d : damages) {
                        stm.setInt(1, d.getBookingId());
                        stm.setInt(2, d.getAmenityId());
                        stm.setInt(3, d.getQuantityDamaged());
                        stm.setBigDecimal(4, d.getTotalPrice());
                        stm.addBatch();
                    }
                    stm.executeBatch();
                }
            }

            updateBookingStatus(invoice.getBookingId());
            updateRoomStatus(rooms, damages);

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new Exception("Lỗi hệ thống: Không thể tạo hóa đơn. " + e.getMessage());
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void completeInvoicePayment(int invoiceId, int bookingId, String paymentMethod,
            int staffId, List<RoomAmenityDamage> damages) throws Exception {
        connection.setAutoCommit(false);
        try {
            String updateInvoiceSql = """
                update Invoices 
                set payment_status = N'Đã thanh toán', 
                    payment_method = ?,
                    paid_at = GETDATE(),
                    created_by = ?,
                     remaining_amount = 0                 
                where invoice_id = ?
                """;
            try (PreparedStatement stm = connection.prepareStatement(updateInvoiceSql)) {
                stm.setString(1, paymentMethod);
                stm.setInt(2, staffId);
                stm.setInt(3, invoiceId);
                stm.executeUpdate();
            }

            updateBookingStatus(bookingId);

            List<Room> rooms = getRoomsByBookingId(bookingId);
            updateRoomStatus(rooms, damages);

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new Exception("Lỗi hệ thống: Không thể xác nhận thanh toán. " + e.getMessage());
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public Invoice getInvoiceByBookingId(int bookingId) throws Exception {
        String sql = """
                     select * from Invoices where booking_id = ?
                     """;
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

    private void updateBookingStatus(int bookingId) throws Exception {
        String sql = """
                     update Bookings 
                     set status = N'Đã trả phòng', payment_status = N'Đã thanh toán'
                     where booking_id = ?
                     """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể cập nhật trạng thái booking.");
        }
    }

    private void updateRoomStatus(List<Room> rooms, List<RoomAmenityDamage> damages) throws Exception {
        boolean hasDamage = damages != null && !damages.isEmpty();
        String roomStatus = hasDamage ? "Đang bảo trì" : "Đang dọn dẹp";
        String sql = "update Rooms set status = ? where room_number = ?";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            for (Room room : rooms) {
                stm.setString(1, roomStatus);
                stm.setInt(2, room.getRoomNumber());
                stm.addBatch();
            }
            stm.executeBatch();
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể cập nhật trạng thái phòng.");
        }
    }

    private Invoice mapInvoice(ResultSet rs) throws SQLException {
        Invoice inv = new Invoice();
        inv.setInvoiceId(rs.getInt("invoice_id"));
        inv.setBookingId(rs.getInt("booking_id"));
        inv.setRoomCharges(rs.getBigDecimal("room_charges"));
        inv.setConsumableCharges(rs.getBigDecimal("consumable_charges"));
        inv.setAmenityDamages(rs.getBigDecimal("amenity_damages"));
        inv.setDepositDeducted(rs.getBigDecimal("deposit_deducted"));
        inv.setTotalAmount(rs.getBigDecimal("total_amount"));
        inv.setRemainingAmount(rs.getBigDecimal("remaining_amount"));
        inv.setPaymentStatus(rs.getString("payment_status"));
        inv.setPaymentMethod(rs.getString("payment_method"));
        inv.setPaidAt(rs.getTimestamp("paid_at") != null ? rs.getTimestamp("paid_at").toLocalDateTime() : null);
        inv.setCreatedBy(rs.getInt("created_by"));
        return inv;
    }

    //Billing
    public List<Map<String, Object>> searchInvoices(String keyword, LocalDate fromDate,
            LocalDate toDate, String status) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
        select i.invoice_id, i.booking_id, b.booking_code, i.room_charges, i.consumable_charges,
               i.amenity_damages, i.deposit_deducted, i.total_amount, i.remaining_amount,
               i.payment_status, i.payment_method, i.paid_at
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
            sql.append(" and i.paid_at >= ?");
            params.add(java.sql.Date.valueOf(fromDate));
        }
        if (toDate != null) {
            sql.append(" and i.paid_at < ?");
            params.add(java.sql.Date.valueOf(toDate.plusDays(1)));
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" and i.payment_status = ?");
            params.add(status);
        }
        sql.append(" order by i.paid_at desc, i.invoice_id desc");

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
                    map.put("roomCharges", rs.getBigDecimal("room_charges"));
                    map.put("consumableCharges", rs.getBigDecimal("consumable_charges"));
                    map.put("amenityDamages", rs.getBigDecimal("amenity_damages"));
                    map.put("depositDeducted", rs.getBigDecimal("deposit_deducted"));
                    map.put("totalAmount", rs.getBigDecimal("total_amount"));
                    map.put("remainingAmount", rs.getBigDecimal("remaining_amount"));
                    map.put("paymentStatus", rs.getString("payment_status"));
                    map.put("paymentMethod", rs.getString("payment_method"));
                    map.put("paidAt", rs.getTimestamp("paid_at") != null
                            ? rs.getTimestamp("paid_at").toLocalDateTime()
                                    .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                            : "-");
                    list.add(map);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể tìm kiếm hóa đơn. " + e.getMessage());
        }
        return list;
    }

    public int countInvoices(String keyword, LocalDate fromDate, LocalDate toDate, String status) throws Exception {
        StringBuilder sql = new StringBuilder("""
        select count(*) as total
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
            sql.append(" and i.paid_at >= ?");
            params.add(java.sql.Date.valueOf(fromDate));
        }
        if (toDate != null) {
            sql.append(" and i.paid_at < ?");
            params.add(java.sql.Date.valueOf(toDate.plusDays(1)));
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" and i.payment_status = ?");
            params.add(status);
        }

        try (PreparedStatement stm = connection.prepareStatement(sql.toString())) {
            int idx = 1;
            for (Object p : params) {
                stm.setObject(idx++, p);
            }
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể đếm hóa đơn. " + e.getMessage());
        }
        return 0;
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
            sql.append(" and paid_at >= ?");
            params.add(java.sql.Date.valueOf(fromDate));
        }
        if (toDate != null) {
            sql.append(" and paid_at < ?");
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

    public Map<String, Object> getInvoiceDetailById(int invoiceId) throws Exception {
        String sql = """
        select i.*, b.booking_code, sa.full_name as staff_name
        from Invoices i
        join Bookings b on i.booking_id = b.booking_id
        left join StaffAccounts sa on i.created_by = sa.staff_id
        where i.invoice_id = ?
        """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, invoiceId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("invoice", mapInvoice(rs));
                    map.put("paidAtFormatted", rs.getTimestamp("paid_at") != null
                            ? rs.getTimestamp("paid_at").toLocalDateTime()
                                    .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                            : "-");
                    map.put("bookingCode", rs.getString("booking_code"));
                    map.put("staffName", rs.getString("staff_name"));
                    return map;
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy chi tiết hóa đơn. " + e.getMessage());
        }
        return null;
    }
}
