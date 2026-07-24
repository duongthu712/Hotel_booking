package dao;

import dal.DBContext;
import dto.BookingCheckInView;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.Booking;
import java.sql.Types;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Statement;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import model.Guest;
import java.sql.ResultSetMetaData;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

public class BookingDAO extends DBContext {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int SQL_DATE_TEXT_LENGTH = 10;
    private static final int SQL_DATETIME_TEXT_LENGTH = 16;

    private static final int MIN_VALID_ROOM_COUNT = 1;
    private static final int NO_ASSIGNED_ROOMS = 0;
    private static final int MONEY_SCALE = 2;

    private static final int CHECKIN_HOUR = 14;
    private static final int CHECKIN_MINUTE = 0;

    private static final long FREE_CANCEL_HOURS = 72L;
    private static final long THIRTY_PERCENT_CANCEL_HOURS = 48L;
    private static final long FIFTY_PERCENT_CANCEL_HOURS = 24L;

    private static final String UNASSIGNED_ROOM_TEXT = "Chưa gán";

    private static final BigDecimal FREE_CANCEL_RATE = BigDecimal.ZERO;
    private static final BigDecimal THIRTY_PERCENT_CANCEL_RATE = new BigDecimal("0.30");
    private static final BigDecimal FIFTY_PERCENT_CANCEL_RATE = new BigDecimal("0.50");
    private static final BigDecimal SEVENTY_PERCENT_CANCEL_RATE = new BigDecimal("0.70");

    private static final RoundingMode MONEY_ROUNDING_MODE = RoundingMode.HALF_UP;

    private static final Set<String> UNASSIGNED_ROOM_KEYWORDS
            = Set.of("chưa gán", "chua gan", "null");
    
    //Linh
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
                if (rs.next()) {
                    return rs.getString("type_name");
                }
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
                if (rs.next()) {
                    return rs.getString("bed_type");
                }
            }
        }
        return "";
    }

    public String getGuestEmailByBookingId(int bookingId) throws Exception {
        String sql = """
                     select g.email 
                     from Guests g join Bookings b on g.guest_id = b.guest_id 
                     where b.booking_id = ?
                     """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("email");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy email khách hàng.");
        }
        return "";
    }

    public String getGuestNameByBookingId(int bookingId) throws Exception {
        String sql = """
                     select g.full_name 
                     from Guests g join Bookings b on g.guest_id = b.guest_id 
                     where b.booking_id = ?
                     """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("full_name");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy tên khách hàng.");
        }
        return "";
    }

    public String getBookingCodeByBookingId(int bookingId) throws Exception {
        String sql = """
                     select booking_code 
                     from Bookings 
                     where booking_id = ?
                     """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("booking_code");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy được mã đặt phòng.");
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
        booking.setNumGuests(rs.getInt("num_guests"));
        booking.setNumChildren(rs.getInt("num_children"));
        booking.setBookedPricePerNight(rs.getBigDecimal("booked_price_per_night"));

        Date checkinDate = rs.getDate("checkin_date");
        booking.setCheckinDate(checkinDate != null ? checkinDate.toLocalDate() : null);

        Date checkoutDate = rs.getDate("checkout_date");
        booking.setCheckoutDate(checkoutDate != null ? checkoutDate.toLocalDate() : null);

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
        booking.setActualCheckinTime(
                actualCheckin != null ? actualCheckin.toLocalDateTime() : null
        );

        Timestamp actualCheckout = rs.getTimestamp("actual_checkout_time");
        booking.setActualCheckoutTime(
                actualCheckout != null ? actualCheckout.toLocalDateTime() : null
        );

        Timestamp createdAt = rs.getTimestamp("created_at");
        booking.setCreateAt(createdAt != null ? createdAt.toLocalDateTime() : null);

        return booking;
    }

    // Author: ThuDNM-HE204370
    // Lấy đơn lên để cập nhật thêm thông tin lúc check in
    public boolean updateCheckInAdvance(int bookingId, int currentGuestId, String fullName, String phone, String email,
            String idNumber, String nationality, String dobStr, int numGuests, boolean isDifferentGuest) {

        // ĐÃ SỬA: Thêm email vào SQL INSERT và UPDATE
        String insertNewGuestSql = "INSERT INTO Guests (full_name, phone, email, id_number, nationality, date_of_birth) VALUES (?, ?, ?, ?, ?, ?)";
        String updateBookingGuestSql = "UPDATE Bookings SET guest_id = ?, num_guests = ?, actual_checkin_time = GETDATE() WHERE booking_id = ?";
        // ĐÃ SỬA: Thêm email = ? vào câu lệnh này
        String updateOldGuestSql = "UPDATE Guests SET full_name = ?, phone = ?, email = ?, id_number = ?, nationality = ?, date_of_birth = ? WHERE guest_id = ?";
        String updateBookingOnlySql = "UPDATE Bookings SET num_guests = ?, actual_checkin_time = GETDATE() WHERE booking_id = ?";
        try {
            connection.setAutoCommit(false);

            if (isDifferentGuest) {
                int newGuestId = -1;
                try (PreparedStatement psInsert = connection.prepareStatement(insertNewGuestSql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                    psInsert.setNString(1, fullName.trim());
                    psInsert.setString(2, phone != null ? phone.trim() : null);
                    psInsert.setString(3, email != null ? email.trim() : null);
                    psInsert.setString(4, idNumber.trim());
                    psInsert.setNString(5, nationality.trim());
                    if (dobStr != null && !dobStr.trim().isEmpty()) {
                        psInsert.setDate(6, java.sql.Date.valueOf(dobStr));
                    } else {
                        psInsert.setNull(6, java.sql.Types.DATE);
                    }
                    psInsert.executeUpdate();

                    try (ResultSet rsKeys = psInsert.getGeneratedKeys()) {
                        if (rsKeys.next()) {
                            newGuestId = rsKeys.getInt(1);
                        }
                    }
                }

                try (PreparedStatement psUpdateBooking = connection.prepareStatement(updateBookingGuestSql)) {
                    psUpdateBooking.setInt(1, newGuestId);
                    psUpdateBooking.setInt(2, numGuests);
                    psUpdateBooking.setInt(3, bookingId);
                    psUpdateBooking.executeUpdate();
                }

            } else {
                // Nhánh cập nhật thông tin khách hiện tại (đã thêm email)
                try (PreparedStatement psUpdateOld = connection.prepareStatement(updateOldGuestSql)) {
                    psUpdateOld.setNString(1, fullName.trim());
                    psUpdateOld.setString(2, phone != null ? phone.trim() : null);
                    psUpdateOld.setString(3, email != null ? email.trim() : null); // CẬP NHẬT: Email
                    psUpdateOld.setString(4, idNumber.trim());
                    psUpdateOld.setNString(5, nationality.trim());
                    if (dobStr != null && !dobStr.trim().isEmpty()) {
                        psUpdateOld.setDate(6, java.sql.Date.valueOf(dobStr));
                    } else {
                        psUpdateOld.setNull(6, java.sql.Types.DATE);
                    }
                    psUpdateOld.setInt(7, currentGuestId);
                    psUpdateOld.executeUpdate();
                }

                try (PreparedStatement psUpdateBookingOnly = connection.prepareStatement(updateBookingOnlySql)) {
                    psUpdateBookingOnly.setInt(1, numGuests);
                    psUpdateBookingOnly.setInt(2, bookingId);
                    psUpdateBookingOnly.executeUpdate();
                }
            }

            connection.commit();
            return true;

        } catch (Exception e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // Author: ThuDNM-HE204370
    // Dùng cho thanh search của check in 
    public BookingCheckInView getBookingForCheckIn(String bookingCode) {
        String sql = "SELECT b.booking_id, b.booking_code, b.num_rooms, b.num_guests, b.num_children AS booking_num_children, b.payment_status, b.deposit_amount, "
                + "b.[status], b.actual_checkin_time, CONVERT(VARCHAR(10), b.checkin_date, 120) AS checkin_date, "
                + "CONVERT(VARCHAR(8), b.expected_checkin_time, 108) AS expected_checkin_time, "
                + "CONVERT(VARCHAR(19), b.auto_cancel_deadline, 120) AS auto_cancel_deadline, "
                + "b.cancellation_reason AS call_note, "
                + "g.guest_id, g.full_name, g.phone, g.email, g.id_number, g.date_of_birth, g.nationality, "
                + "rt.room_type_id, rt.type_name, rt.capacity, rt.num_guests AS max_adults, rt.num_children AS max_children, "
                + "r.request_type, r.request_details, r.status AS request_status, "
                + "CONVERT(VARCHAR(19), r.requested_checkin, 120) AS requested_checkin, "
                + "ISNULL(br_count.total_assigned, 0) AS assigned_rooms_count, "
                + "ISNULL(br_count.assigned_room_numbers, '') AS assigned_room_list "
                + "FROM Bookings b "
                + "LEFT JOIN Guests g ON b.guest_id = g.guest_id "
                + "INNER JOIN RoomTypes rt ON b.room_type_id = rt.room_type_id "
                + "LEFT JOIN GuestRequests r ON b.booking_id = r.booking_id AND r.[status] = N'Đã phê duyệt' "
                + "LEFT JOIN ("
                + "    SELECT br.booking_id, COUNT(*) AS total_assigned, "
                + "           STRING_AGG(rm.room_number, ', ') AS assigned_room_numbers "
                + "    FROM BookingRooms br "
                + "    JOIN Rooms rm ON br.room_id = rm.room_id "
                + "    GROUP BY br.booking_id"
                + ") br_count ON b.booking_id = br_count.booking_id "
                + "WHERE b.booking_code = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, bookingCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BookingCheckInView b = new BookingCheckInView();
                    b.setBookingId(rs.getInt("booking_id"));
                    b.setBookingCode(rs.getString("booking_code"));
                    b.setNumRooms(rs.getInt("num_rooms"));
                    b.setNumGuests(rs.getInt("num_guests"));
                    b.setNumChildren(rs.getInt("booking_num_children"));
                    b.setPaymentStatus(rs.getString("payment_status"));
                    b.setDepositAmount(rs.getBigDecimal("deposit_amount"));
                    b.setStatus(rs.getString("status"));
                    b.setActualCheckInTime(rs.getString("actual_checkin_time"));
                    b.setCheckinDate(rs.getString("checkin_date"));

                    // Điền dữ liệu vào 3 thuộc tính hẹn giờ mới
                    b.setExpectedCheckInTime(rs.getString("expected_checkin_time"));
                    b.setAutoCancelDeadline(rs.getString("auto_cancel_deadline"));
                    b.setCallNote(rs.getNString("call_note"));

                    b.setGuestId(rs.getInt("guest_id"));
                    b.setGuestFullName(rs.getString("full_name"));
                    b.setGuestPhone(rs.getString("phone"));
                    b.setGuestEmail(rs.getString("email"));
                    b.setIdNumber(rs.getString("id_number"));
                    b.setDateOfBirth(rs.getDate("date_of_birth"));
                    b.setNationality(rs.getString("nationality"));
                    b.setRoomTypeId(rs.getInt("room_type_id"));
                    b.setRoomTypeName(rs.getString("type_name"));
                    b.setCapacity(rs.getInt("capacity"));

                    b.setMaxAdults(rs.getInt("max_adults"));
                    b.setMaxChildren(rs.getInt("max_children"));

                    b.setRequestType(rs.getString("request_type"));
                    b.setRequestDetails(rs.getString("request_details"));
                    b.setRequestStatus(rs.getString("request_status"));
                    b.setRequestedCheckIn(rs.getString("requested_checkin"));
                    b.setAssignedRoomsCount(rs.getInt("assigned_rooms_count"));
                    b.setAssignedRoomList(rs.getString("assigned_room_list"));

                    return b;
                }
            }
        } catch (Exception e) {
            System.out.println("Lỗi getBookingForCheckIn: " + e.getMessage());
        }
        return null;
    }

    // Author: ThuDNM-HE204370
    // Hiện list check in ngày hôm nay
    public List<BookingCheckInView> getBookingsToday() {
        List<BookingCheckInView> list = new ArrayList<>();
        java.time.LocalDate today = java.time.LocalDate.now();
        String todayStr = today.toString();

        String sql = "SELECT b.booking_id, b.booking_code, b.num_rooms, b.num_guests, b.num_children, b.payment_status, b.deposit_amount, "
                + "b.[status], b.actual_checkin_time, "
                + "CONVERT(VARCHAR(8), b.expected_checkin_time, 108) AS expected_checkin_time, "
                + "CONVERT(VARCHAR(19), b.auto_cancel_deadline, 120) AS auto_cancel_deadline, "
                + "b.cancellation_reason AS call_note, "
                + "g.guest_id, g.full_name, g.phone, g.email, g.id_number, g.date_of_birth, g.nationality, "
                + "rt.type_name, rt.capacity, "
                + "r.request_type, r.request_details, r.status AS request_status, "
                + "CONVERT(VARCHAR(19), r.requested_checkin, 120) AS requested_checkin "
                + "FROM Bookings b "
                + "LEFT JOIN Guests g ON b.guest_id = g.guest_id "
                + "INNER JOIN RoomTypes rt ON b.room_type_id = rt.room_type_id "
                + "LEFT JOIN GuestRequests r ON b.booking_id = r.booking_id "
                + "                           AND r.[status] = N'Đã phê duyệt' "
                + "                           AND r.request_type IN (N'Nhận phòng sớm', N'Nhận phòng muộn') "
                + "WHERE b.checkin_date = ? "
                + "  AND b.[status] IN (N'Đã xác nhận', N'Đã nhận phòng')";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, todayStr);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BookingCheckInView b = new BookingCheckInView();
                    b.setBookingId(rs.getInt("booking_id"));
                    b.setBookingCode(rs.getString("booking_code"));
                    b.setNumRooms(rs.getInt("num_rooms"));
                    b.setNumGuests(rs.getInt("num_guests"));
                    b.setNumChildren(rs.getInt("num_children"));
                    b.setPaymentStatus(rs.getString("payment_status"));
                    b.setDepositAmount(rs.getBigDecimal("deposit_amount"));
                    b.setStatus(rs.getString("status"));
                    b.setActualCheckInTime(rs.getString("actual_checkin_time"));

                    // Điền dữ liệu hẹn giờ ra ngoài danh sách hiển thị
                    b.setExpectedCheckInTime(rs.getString("expected_checkin_time"));
                    b.setAutoCancelDeadline(rs.getString("auto_cancel_deadline"));
                    b.setCallNote(rs.getNString("call_note"));

                    b.setGuestId(rs.getInt("guest_id"));
                    b.setGuestFullName(rs.getString("full_name"));
                    b.setGuestPhone(rs.getString("phone"));
                    b.setGuestEmail(rs.getString("email"));
                    b.setIdNumber(rs.getString("id_number"));
                    b.setDateOfBirth(rs.getDate("date_of_birth"));
                    b.setNationality(rs.getString("nationality"));
                    b.setRoomTypeName(rs.getString("type_name"));
                    b.setCapacity(rs.getInt("capacity"));
                    b.setRequestType(rs.getString("request_type"));
                    b.setRequestDetails(rs.getString("request_details"));
                    b.setRequestStatus(rs.getString("request_status"));
                    b.setRequestedCheckIn(rs.getString("requested_checkin"));
                    list.add(b);
                }
            }
        } catch (Exception e) {
            System.out.println("Lỗi getBookingsToday: " + e.getMessage());
        }
        return list;
    }

    // Author: ThuDNM-HE204370
    // Hủy đơn trực tiếp nếu quá giờ check in hoặc lễ tân hủy
    public boolean cancelBooking(int bookingId) {
        String updateInvoiceSql = """
                UPDATE Invoices
                SET room_charges = 0,
                    consumable_charges = 0,
                    amenity_damages = 0,
                    total_amount = (SELECT ISNULL(deposit_amount, 0) FROM Bookings WHERE booking_id = ?),
                    remaining_amount = 0,
                    payment_status = N'Đã thanh toán'
                WHERE booking_id = ?
                """;

        String updateBookingSql = "UPDATE Bookings SET [status] = N'Đã hủy' WHERE booking_id = ?";

        try {
            connection.setAutoCommit(false);
            
            // Cập nhật hóa đơn
            try (PreparedStatement ps1 = connection.prepareStatement(updateInvoiceSql)) {
                ps1.setInt(1, bookingId);
                ps1.setInt(2, bookingId);
                ps1.executeUpdate();
            }

            // Cập nhật trạng thái booking
            try (PreparedStatement ps2 = connection.prepareStatement(updateBookingSql)) {
                ps2.setInt(1, bookingId);
                int rows = ps2.executeUpdate();
                
                connection.commit();
                return rows > 0;
            }
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (Exception rollbackEx) {
                System.out.println("Lỗi rollback cancelBooking: " + rollbackEx.getMessage());
            }
            System.out.println("Lỗi cancelBooking: " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (Exception autoCommitEx) {
                System.out.println("Lỗi reset autoCommit: " + autoCommitEx.getMessage());
            }
        }
        return false;
    }

    // Author: ThuDNM-HE204370
    // Cập nhật trạng thái booking
    public boolean updateStatus(int bookingId, String status) {
        String sql = """
                     UPDATE Bookings
                     SET [status] = ?,
                         actual_checkin_time =
                             CASE
                                 WHEN ? = N'Đã nhận phòng'
                                      AND actual_checkin_time IS NULL
                                 THEN GETDATE()
                                 ELSE actual_checkin_time
                             END,
                         actual_checkout_time =
                             CASE
                                 WHEN ? = N'Đã trả phòng'
                                      AND actual_checkout_time IS NULL
                                 THEN GETDATE()
                                 ELSE actual_checkout_time
                             END
                     WHERE booking_id = ?
                     """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setNString(1, status);
            ps.setNString(2, status);
            ps.setNString(3, status);
            ps.setInt(4, bookingId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Author: ThuDNM-HE204370
    // Đếm số lượng phòng thực tế đã gán vào bảng BookingRooms
    public int countRoomsAssigned(int bookingId) {
        String sql = "SELECT COUNT(*) FROM BookingRooms WHERE booking_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Author: ThuDNM-HE204370
    // Kiểm tra xem có phòng nào đã gán nhưng chưa được chia khách vào ở hay không
    public boolean hasEmptyRoomWithoutGuests(int bookingId) {
        String sql = "SELECT COUNT(*) FROM BookingRooms br "
                + "WHERE br.booking_id = ? "
                + "AND NOT EXISTS ( "
                + "    SELECT 1 FROM GuestStays gs "
                + "    WHERE gs.booking_room_id = br.booking_room_id "
                + ")";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Author: ThuDNM-HE204370
    public boolean updateExpectedCheckInTime(int bookingId, String expectedTimeStr, String note) {
        String sql = """
        UPDATE Bookings
        SET expected_checkin_time = ?,
            auto_cancel_deadline =
                CASE
                    WHEN ? IS NULL
                        THEN CAST(CAST(checkin_date AS VARCHAR(10)) + ' 18:00:00' AS DATETIME)
                    WHEN CAST(? AS TIME) >= '18:00:00'
                        THEN CAST(CAST(checkin_date AS VARCHAR(10)) + ' 23:59:00' AS DATETIME)
                    ELSE
                        CAST(CAST(checkin_date AS VARCHAR(10)) + ' 18:00:00' AS DATETIME)
                END,
            cancellation_reason = ?
        WHERE booking_id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            if (expectedTimeStr != null && !expectedTimeStr.trim().isEmpty()) {
                java.sql.Time time = java.sql.Time.valueOf(expectedTimeStr + ":00");

                ps.setTime(1, time);     // expected_checkin_time
                ps.setTime(2, time);     // ? IS NULL
                ps.setTime(3, time);     // CAST(? AS TIME)
            } else {
                ps.setNull(1, java.sql.Types.TIME);
                ps.setNull(2, java.sql.Types.TIME);
                ps.setNull(3, java.sql.Types.TIME);
            }

            ps.setNString(4, note != null ? note.trim() : null);
            ps.setInt(5, bookingId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Lỗi updateExpectedCheckInTime: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Author: ThuDNM-HE204370
    //Hàm tự động hủy đơn No-show
    public int autoCancelExpiredBookings() {
        String updateInvoicesSql = """
                     UPDATE i
                     SET i.room_charges = 0,
                         i.consumable_charges = 0,
                         i.amenity_damages = 0,
                         i.total_amount = ISNULL(b.deposit_amount, 0),
                         i.remaining_amount = 0,
                         i.payment_status = N'Đã thanh toán'
                     FROM Invoices i
                     JOIN Bookings b ON i.booking_id = b.booking_id
                     WHERE b.[status] = N'Đã xác nhận' 
                       AND CAST(b.checkin_date AS DATE) < CAST(GETDATE() AS DATE)
                     """;

        String updateBookingsSql = """
                     UPDATE Bookings 
                     SET [status] = N'Đã hủy', 
                         cancellation_reason = ISNULL(cancellation_reason, '') + N' [Hệ thống]: Tự động hủy do khách không đến nhận phòng trong ngày check-in (No-show).'
                     WHERE [status] = N'Đã xác nhận' 
                       AND CAST(checkin_date AS DATE) < CAST(GETDATE() AS DATE)
                     """;

        try {
            connection.setAutoCommit(false);
            try (PreparedStatement ps1 = connection.prepareStatement(updateInvoicesSql); PreparedStatement ps2 = connection.prepareStatement(updateBookingsSql)) {

                ps1.executeUpdate();
                int canceledCount = ps2.executeUpdate();

                connection.commit();
                return canceledCount;
            } catch (Exception e) {
                connection.rollback();
                System.out.println("Lỗi autoCancelExpiredBookings: " + e.getMessage());
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    // Giang
    PreparedStatement stm;
    ResultSet rs;

    // Tìm khách cũ theo email và số điện thoại
    public int findGuestId(String email, String phone) {
        try {
            String sql = """
                         SELECT guest_id
                         FROM Guests
                         WHERE email = ? AND phone = ?
                         """;
            stm = connection.prepareStatement(sql);
            stm.setString(1, email);
            stm.setString(2, phone);
            rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt("guest_id");
            }
        } catch (Exception e) {
            System.out.println("findGuestId: " + e.getMessage());
        }
        return 0;
    }

    // Tạo khách hàng mới
    public int createGuest(String fullName, String email, String phone,
            String idNumber, LocalDate dateOfBirth) {
        try {
            String sql = """
                         INSERT INTO Guests
                         (full_name, email, phone, id_number, date_of_birth)
                         VALUES (?, ?, ?, ?, ?)
                         """;
            stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stm.setString(1, fullName);
            stm.setString(2, email);
            stm.setString(3, phone);
            if (idNumber == null || idNumber.trim().isEmpty()) {
                stm.setNull(4, Types.VARCHAR);
            } else {
                stm.setString(4, idNumber);
            }
            if (dateOfBirth == null) {
                stm.setNull(5, Types.DATE);
            } else {
                stm.setDate(5, Date.valueOf(dateOfBirth));
            }
            stm.executeUpdate();
            rs = stm.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("createGuest: " + e.getMessage());
        }

        return 0;
    }

    // Kiểm tra mã booking đã tồn tại
    public boolean isBookingCodeExist(String bookingCode) {
        try {
            String sql = """
                         SELECT COUNT(*) AS total
                         FROM Bookings
                         WHERE booking_code = ?
                         """;
            stm = connection.prepareStatement(sql);
            stm.setString(1, bookingCode);
            rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt("total") > 0;
            }
        } catch (Exception e) {
            System.out.println("isBookingCodeExist: " + e.getMessage());
        }
        return false;
    }

    // Tạo booking mới và bắt đầu giữ phòng 15 phút
    public int createBooking(Booking booking) {
        String sql = """
                 INSERT INTO Bookings
                 (
                     booking_code,
                     guest_id,
                     staff_id,
                     room_type_id,
                     booked_price_per_night,
                     num_rooms,
                     num_guests,
                     num_children,
                     checkin_date,
                     checkout_date,
                     [source],
                     [status],
                     deposit_amount,
                     payment_status
                 )
                 VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                 """;

        try (PreparedStatement ps = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, booking.getBookingCode());
            ps.setInt(2, booking.getGuestId());

            if (booking.getStaffId() == null) {
                ps.setNull(3, Types.INTEGER);
            } else {
                ps.setInt(3, booking.getStaffId());
            }

            ps.setInt(4, booking.getRoomTypeId());
            ps.setBigDecimal(5, booking.getBookedPricePerNight());
            ps.setInt(6, booking.getNumRooms());
            ps.setInt(7, booking.getNumGuests());
            ps.setInt(8, booking.getNumChildren());
            ps.setDate(9, Date.valueOf(booking.getCheckinDate()));
            ps.setDate(10, Date.valueOf(booking.getCheckoutDate()));
            ps.setNString(11, booking.getSource());
            ps.setNString(12, booking.getStatus());
            ps.setBigDecimal(13, booking.getDepositAmount());
            ps.setNString(14, booking.getPaymentStatus());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                return 0;
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("createBooking: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    // Lấy booking theo mã booking
    public Booking getBookingByCode(String bookingCode) {
        cancelExpiredBookings();

        String sql = """
                 SELECT *
                 FROM Bookings
                 WHERE booking_code = ?
                 """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, bookingCode);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Booking booking = new Booking();

                    booking.setBookingId(rs.getInt("booking_id"));
                    booking.setBookingCode(rs.getString("booking_code"));
                    booking.setGuestId(rs.getInt("guest_id"));

                    int staffId = rs.getInt("staff_id");
                    booking.setStaffId(rs.wasNull() ? null : staffId);

                    booking.setRoomTypeId(rs.getInt("room_type_id"));
                    booking.setBookedPricePerNight(rs.getBigDecimal("booked_price_per_night"));
                    booking.setNumRooms(rs.getInt("num_rooms"));
                    booking.setNumGuests(rs.getInt("num_guests"));
                    booking.setNumChildren(rs.getInt("num_children"));

                    Date checkinDate = rs.getDate("checkin_date");
                    booking.setCheckinDate(
                            checkinDate != null ? checkinDate.toLocalDate() : null
                    );

                    Date checkoutDate = rs.getDate("checkout_date");
                    booking.setCheckoutDate(
                            checkoutDate != null ? checkoutDate.toLocalDate() : null
                    );

                    booking.setSource(rs.getString("source"));
                    booking.setStatus(rs.getString("status"));
                    booking.setPaymentStatus(rs.getString("payment_status"));
                    booking.setDepositAmount(rs.getBigDecimal("deposit_amount"));

                    Timestamp createdAt = rs.getTimestamp("created_at");
                    booking.setCreateAt(
                            createdAt != null ? createdAt.toLocalDateTime() : null
                    );

                    Timestamp confirmedAt = rs.getTimestamp("confirmed_at");
                    booking.setConfirmedAt(
                            confirmedAt != null ? confirmedAt.toLocalDateTime() : null
                    );

                    Timestamp cancelledAt = rs.getTimestamp("cancelled_at");
                    booking.setCancelledAt(
                            cancelledAt != null ? cancelledAt.toLocalDateTime() : null
                    );

                    booking.setCancellationReason(rs.getString("cancellation_reason"));

                    Timestamp actualCheckin = rs.getTimestamp("actual_checkin_time");
                    booking.setActualCheckinTime(
                            actualCheckin != null ? actualCheckin.toLocalDateTime() : null
                    );

                    Timestamp actualCheckout = rs.getTimestamp("actual_checkout_time");
                    booking.setActualCheckoutTime(
                            actualCheckout != null ? actualCheckout.toLocalDateTime() : null
                    );

                    return booking;
                }
            }

        } catch (SQLException e) {
            System.out.println("getBookingByCode: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // Hủy booking online quá 15 phút nhưng chưa gửi minh chứng
    public int cancelExpiredBookings() {
        String sql = """
             UPDATE b
             SET b.[status] = N'Đã hủy',
                 b.cancelled_at = ISNULL(b.cancelled_at, GETDATE()),
                 b.cancellation_reason = N'Quá thời hạn thanh toán 15 phút',
                 b.staff_id = NULL
             FROM Bookings b
             WHERE b.[status] = N'Chờ xử lý'
               AND b.payment_status = N'Chưa thanh toán'
               AND b.[source] = N'Đặt phòng trực tuyến'
               AND b.created_at IS NOT NULL
               AND DATEADD(MINUTE, 15, b.created_at) <= GETDATE()
               AND NOT EXISTS (
                   SELECT 1
                   FROM DepositPayments dp
                   WHERE dp.booking_id = b.booking_id
               )
             """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            return ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("cancelExpiredBookings error: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    // Kiểm tra booking đã gửi minh chứng chưa
    public boolean hasDepositPayment(int bookingId) {
        try {
            String sql = """
                         SELECT COUNT(*) AS total
                         FROM DepositPayments
                         WHERE booking_id = ?
                         """;

            stm = connection.prepareStatement(sql);
            stm.setInt(1, bookingId);
            rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt("total") > 0;
            }
        } catch (Exception e) {
            System.out.println("hasDepositPayment: " + e.getMessage());
        }

        return false;
    }

    // Lưu minh chứng và cập nhật booking thành đã đặt cọc
    // Lưu minh chứng đặt cọc kiểu cũ, giữ lại để tránh lỗi nếu chỗ khác còn gọi 3 tham số
    public boolean createDepositPayment(
            int bookingId,
            BigDecimal amount,
            String paymentProofUrl) {

        return createDepositPayment(
                bookingId,
                amount,
                paymentProofUrl,
                ""
        );
    }

// Lưu minh chứng đặt cọc gồm ảnh và mã giao dịch/mã tham chiếu
    public boolean createDepositPayment(
            int bookingId,
            BigDecimal amount,
            String paymentProofUrl,
            String transactionReference) {

        String insertPaymentSql = """
                          INSERT INTO DepositPayments
                          (booking_id, amount, payment_proof_url,
                           notes, verification_status)
                          VALUES (?, ?, ?, ?, N'Chờ xử lý')
                          """;

        String updateBookingSql = """
                          UPDATE Bookings
                          SET payment_status = N'Đã đặt cọc'
                          WHERE booking_id = ?
                            AND [status] = N'Chờ xử lý'
                            AND payment_status = N'Chưa thanh toán'
                          """;

        boolean oldAutoCommit = true;

        try {
            oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            int insertedRows;

            try (PreparedStatement insertStm
                    = connection.prepareStatement(insertPaymentSql)) {

                insertStm.setInt(1, bookingId);
                insertStm.setBigDecimal(2, amount);
                insertStm.setString(3, paymentProofUrl);

                if (transactionReference == null
                        || transactionReference.trim().isEmpty()) {

                    insertStm.setNString(4, null);
                } else {
                    insertStm.setNString(
                            4,
                            "Mã giao dịch/Mã tham chiếu: "
                            + transactionReference.trim()
                    );
                }

                insertedRows = insertStm.executeUpdate();
            }

            if (insertedRows == 0) {
                connection.rollback();
                return false;
            }

            int updatedRows;

            try (PreparedStatement updateStm
                    = connection.prepareStatement(updateBookingSql)) {

                updateStm.setInt(1, bookingId);
                updatedRows = updateStm.executeUpdate();
            }

            if (updatedRows == 0) {
                connection.rollback();
                return false;
            }

            connection.commit();
            return true;

        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }

            System.out.println("createDepositPayment: " + e.getMessage());
            e.printStackTrace();

        } finally {
            try {
                connection.setAutoCommit(oldAutoCommit);
            } catch (SQLException e) {
                System.out.println(
                        "createDepositPayment autoCommit: "
                        + e.getMessage()
                );
            }
        }

        return false;
    }

    // Tra cứu booking bằng mã booking và email
    public Booking getBookingByCodeAndEmail(String bookingCode, String email) {
        cancelExpiredBookings();

        String sql = """
                 SELECT b.*
                 FROM Bookings b
                 JOIN Guests g ON b.guest_id = g.guest_id
                 WHERE b.booking_code = ?
                   AND LOWER(g.email) = LOWER(?)
                 """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, bookingCode);
            ps.setString(2, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapBooking(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("getBookingByCodeAndEmail: " + e.getMessage());
        }

        return null;
    }

    // Lấy thông tin khách hàng theo booking
    public Guest getGuestByBookingId(int bookingId) {
        String sql = """
                 SELECT g.*
                 FROM Guests g
                 JOIN Bookings b ON g.guest_id = b.guest_id
                 WHERE b.booking_id = ?
                 """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Guest guest = new Guest();

                    guest.setGuestId(rs.getInt("guest_id"));
                    guest.setFullName(rs.getString("full_name"));
                    guest.setEmail(rs.getString("email"));
                    guest.setPhone(rs.getString("phone"));
                    guest.setIdNumber(rs.getString("id_number"));
                    guest.setNationality(rs.getString("nationality"));

                    Date dateOfBirth = rs.getDate("date_of_birth");

                    if (dateOfBirth != null) {
                        guest.setDateOfBirth(dateOfBirth.toLocalDate());
                    }

                    return guest;
                }
            }
        } catch (SQLException e) {
            System.out.println("getGuestByBookingId: " + e.getMessage());
        }

        return null;
    }

    // Lấy trạng thái xác minh minh chứng đặt cọc
    public String getDepositVerificationStatus(int bookingId) {
        String sql = """
                 SELECT verification_status
                 FROM DepositPayments
                 WHERE booking_id = ?
                 """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("verification_status");
                }
            }
        } catch (SQLException e) {
            System.out.println("getDepositVerificationStatus: " + e.getMessage());
        }

        return null;
    }

    // Xử lý lưu đơn đặt phòng Walk-in trực tiếp tại quầy
    public boolean createWalkinBooking(Booking booking, String fullName, String email, String phone,
            String idNumber, LocalDate dateOfBirth) {

        String insertGuestSql = """
                                INSERT INTO Guests
                                (full_name, email, phone, id_number, date_of_birth)
                                VALUES (?, ?, ?, ?, ?)
                                """;

        String insertBookingSql = """
                                 INSERT INTO Bookings
                                 (
                                     booking_code, guest_id, staff_id, room_type_id,
                                     booked_price_per_night, num_rooms, num_guests, num_children,
                                     checkin_date, checkout_date, [source], [status],
                                     deposit_amount, payment_status
                                 )
                                 VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                                 """;

        boolean oldAutoCommit = true;
        try {
            // 1. Kiểm soát Transaction để đảm bảo dữ liệu toàn vẹn
            oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            int guestId = 0;

            // 2. Luôn tạo mới thông tin khách hàng từ Form nhập liệu
            try (PreparedStatement psGuest = connection.prepareStatement(insertGuestSql, Statement.RETURN_GENERATED_KEYS)) {
                psGuest.setString(1, fullName);
                psGuest.setString(2, email);
                psGuest.setString(3, phone);

                if (idNumber == null || idNumber.trim().isEmpty()) {
                    psGuest.setNull(4, Types.VARCHAR);
                } else {
                    psGuest.setString(4, idNumber);
                }

                if (dateOfBirth == null) {
                    psGuest.setNull(5, Types.DATE);
                } else {
                    psGuest.setDate(5, Date.valueOf(dateOfBirth));
                }

                int guestRows = psGuest.executeUpdate();
                if (guestRows == 0) {
                    connection.rollback();
                    return false;
                }

                try (ResultSet gKeys = psGuest.getGeneratedKeys()) {
                    if (gKeys.next()) {
                        guestId = gKeys.getInt(1);
                    }
                }
            }

            if (guestId == 0) {
                connection.rollback();
                return false;
            }

            // 3. Thêm mới đơn Booking gắn với guestId vừa sinh ra
            try (PreparedStatement psBooking = connection.prepareStatement(insertBookingSql)) {
                psBooking.setString(1, booking.getBookingCode());
                psBooking.setInt(2, guestId);

                if (booking.getStaffId() == null) {
                    psBooking.setNull(3, Types.INTEGER);
                } else {
                    psBooking.setInt(3, booking.getStaffId());
                }

                psBooking.setInt(4, booking.getRoomTypeId());
                psBooking.setBigDecimal(5, booking.getBookedPricePerNight());
                psBooking.setInt(6, booking.getNumRooms());
                psBooking.setInt(7, booking.getNumGuests());
                psBooking.setInt(8, booking.getNumChildren());
                psBooking.setDate(9, Date.valueOf(booking.getCheckinDate()));
                psBooking.setDate(10, Date.valueOf(booking.getCheckoutDate()));

                // Cố định thông tin nguồn và trạng thái cho luồng tại quầy
                psBooking.setNString(11, "Đặt phòng tại quầy");
                psBooking.setNString(12, "Đã nhận phòng");

                psBooking.setBigDecimal(13, booking.getDepositAmount());
                psBooking.setNString(14, booking.getPaymentStatus());

                int bookingRows = psBooking.executeUpdate();
                if (bookingRows == 0) {
                    connection.rollback();
                    return false;
                }
            }

            // Xác nhận lưu mọi thay đổi vào Database nếu cả 2 bước thành công
            connection.commit();
            return true;

        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            System.out.println("createWalkinBooking: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(oldAutoCommit);
            } catch (SQLException e) {
                System.out.println("createWalkinBooking autoCommit reset: " + e.getMessage());
            }
        }
        return false;
    }

    //booking list - GiangTTT
    public List<Map<String, Object>> getBookingList(
            String keyword, String status, String paymentStatus, String source,
            Integer roomTypeId, Integer staffId, String roomNumber,
            String dateFilter, String sort, int page, int pageSize) {

        // Lấy danh sách booking theo bộ lọc, sắp xếp và phân trang.
        List<Map<String, Object>> bookingList = new ArrayList<>();
        cancelExpiredBookings();

        if (page < DEFAULT_PAGE) {
            page = DEFAULT_PAGE;
        }

        if (pageSize <= 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        String sql = """
            SELECT
                b.booking_id AS bookingId,
                b.booking_code AS bookingCode,
                ISNULL(g.full_name, N'Khách vãng lai') AS guestName,
                ISNULL(g.email, '') AS guestEmail,
                ISNULL(g.phone, '') AS guestPhone,
                rt.type_name AS roomTypeName,
                b.num_rooms AS numRooms,
                ISNULL(roomData.roomNumbers, N'%s') AS roomNumbers,
                ISNULL(roomData.assignedRoomCount, 0) AS assignedRoomCount,
                CONVERT(VARCHAR(%d), b.checkin_date, 103) AS checkinDateText,
                CONVERT(VARCHAR(%d), b.checkout_date, 103) AS checkoutDateText,
                CONVERT(VARCHAR(%d), b.actual_checkin_time, 120) AS actualCheckinTime,
                CONVERT(VARCHAR(%d), b.actual_checkout_time, 120) AS actualCheckoutTime,
                b.[source] AS source,
                LTRIM(RTRIM(b.[status])) AS bookingStatus,
                LTRIM(RTRIM(b.payment_status)) AS paymentStatus,
                ISNULL(requestData.pendingRequestCount, 0) AS pendingRequestCount,
                ISNULL(requestData.approvedRequestCount, 0) AS approvedRequestCount,
                latestRequest.request_type AS latestRequestType,
                latestRequest.[status] AS latestRequestStatus,
                ISNULL(s.full_name, N'Chưa có') AS staffName,

                CASE
                    WHEN LTRIM(RTRIM(b.[status])) = N'Đã xác nhận'
                         AND ISNULL(roomData.assignedRoomCount, 0) = 0
                    THEN 1
                    ELSE 0
                END AS canCancel,

                CASE
                    WHEN LTRIM(RTRIM(b.[status])) = N'Đã xác nhận'
                    THEN 1
                    ELSE 0
                END AS canCheckin,

                CASE
                    WHEN LTRIM(RTRIM(b.[status])) = N'Đã nhận phòng'
                    THEN 1
                    ELSE 0
                END AS canCheckout,

                CASE
                    WHEN ISNULL(requestData.pendingRequestCount, 0) > 0
                    THEN 1
                    ELSE 0
                END AS canProcessRequest

            FROM Bookings b
            LEFT JOIN Guests g ON b.guest_id = g.guest_id
            INNER JOIN RoomTypes rt ON b.room_type_id = rt.room_type_id
            LEFT JOIN StaffAccounts s ON b.staff_id = s.staff_id

            OUTER APPLY (
                SELECT
                    STUFF((
                        SELECT N', ' + CAST(r2.room_number AS NVARCHAR(20))
                        FROM BookingRooms br2
                        INNER JOIN Rooms r2 ON br2.room_id = r2.room_id
                        WHERE br2.booking_id = b.booking_id
                        ORDER BY r2.room_number
                        FOR XML PATH(''), TYPE
                    ).value('.', 'NVARCHAR(MAX)'), 1, 2, N'') AS roomNumbers,

                    (
                        SELECT COUNT(*)
                        FROM BookingRooms br3
                        WHERE br3.booking_id = b.booking_id
                    ) AS assignedRoomCount
            ) roomData

            OUTER APPLY (
                SELECT
                    SUM(CASE WHEN gr.[status] = N'Chờ xử lý' THEN 1 ELSE 0 END) AS pendingRequestCount,
                    SUM(CASE WHEN gr.[status] = N'Đã phê duyệt' THEN 1 ELSE 0 END) AS approvedRequestCount
                FROM GuestRequests gr
                WHERE gr.booking_id = b.booking_id
            ) requestData

            OUTER APPLY (
                SELECT TOP 1
                    gr.request_type,
                    gr.[status]
                FROM GuestRequests gr
                WHERE gr.booking_id = b.booking_id
                ORDER BY gr.submitted_at DESC, gr.request_id DESC
            ) latestRequest
            """.formatted(
                UNASSIGNED_ROOM_TEXT,
                SQL_DATE_TEXT_LENGTH,
                SQL_DATE_TEXT_LENGTH,
                SQL_DATETIME_TEXT_LENGTH,
                SQL_DATETIME_TEXT_LENGTH
        );

        List<String> conditions = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            conditions.add("""
                (
                    b.booking_code LIKE ?
                    OR g.full_name LIKE ?
                    OR g.phone LIKE ?
                    OR g.email LIKE ?
                )
                """.trim());

            String searchKeyword = "%" + keyword.trim() + "%";

            parameters.add(searchKeyword);
            parameters.add(searchKeyword);
            parameters.add(searchKeyword);
            parameters.add(searchKeyword);
        }

        if (status != null && !status.trim().isEmpty()) {
            conditions.add("LTRIM(RTRIM(b.[status])) = ?");
            parameters.add(status.trim());
        }

        if (paymentStatus != null && !paymentStatus.trim().isEmpty()) {
            conditions.add("LTRIM(RTRIM(b.payment_status)) = ?");
            parameters.add(paymentStatus.trim());
        }

        if (source != null && !source.trim().isEmpty()) {
            conditions.add("LTRIM(RTRIM(b.[source])) = ?");
            parameters.add(source.trim());
        }

        if (roomTypeId != null && roomTypeId > 0) {
            conditions.add("b.room_type_id = ?");
            parameters.add(roomTypeId);
        }

        if (staffId != null && staffId > 0) {
            conditions.add("b.staff_id = ?");
            parameters.add(staffId);
        }

        if (roomNumber != null && !roomNumber.trim().isEmpty()) {
            String normalizedRoomNumber = roomNumber.trim().toLowerCase();

            if (UNASSIGNED_ROOM_KEYWORDS.contains(normalizedRoomNumber)) {
                conditions.add("""
                    NOT EXISTS (
                        SELECT 1
                        FROM BookingRooms br
                        WHERE br.booking_id = b.booking_id
                    )
                    """.trim());
            } else {
                conditions.add("""
                    EXISTS (
                        SELECT 1
                        FROM BookingRooms br
                        INNER JOIN Rooms r ON br.room_id = r.room_id
                        WHERE br.booking_id = b.booking_id
                          AND CAST(r.room_number AS NVARCHAR(20)) LIKE ?
                    )
                    """.trim());

                parameters.add("%" + roomNumber.trim() + "%");
            }
        }

        if (dateFilter != null && !dateFilter.trim().isEmpty()) {
            try {
                Date selectedDate = Date.valueOf(dateFilter.trim());

                conditions.add("(b.checkin_date = ? OR b.checkout_date = ?)");
                parameters.add(selectedDate);
                parameters.add(selectedDate);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid booking date filter: " + dateFilter);
            }
        }

        if (!conditions.isEmpty()) {
            sql += "\nWHERE " + String.join("\n  AND ", conditions);
        }

        sql += "\nORDER BY " + getBookingListSortSql(sort);
        sql += "\nOFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        int offset = (page - DEFAULT_PAGE) * pageSize;

        parameters.add(offset);
        parameters.add(pageSize);

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setBookingListParams(statement, parameters);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    bookingList.add(mapBookingListRow(resultSet));
                }
            }
        } catch (Exception e) {
            System.out.println("getBookingList error: " + e.getMessage());
            e.printStackTrace();
        }

        return bookingList;
    }

    private List<String> buildBookingListConditions(
            String keyword, String status, String paymentStatus, String source,
            Integer roomTypeId, Integer staffId, String roomNumber,
            String dateFilter, List<Object> parameters) {

        // Tạo điều kiện lọc và danh sách tham số tương ứng.
        List<String> conditions = new ArrayList<>();

        if (hasText(keyword)) {
            conditions.add("""
                (
                    b.booking_code LIKE ?
                    OR g.full_name LIKE ?
                    OR g.phone LIKE ?
                    OR g.email LIKE ?
                )
                """.trim());

            String searchKeyword = "%" + keyword.trim() + "%";

            parameters.add(searchKeyword);
            parameters.add(searchKeyword);
            parameters.add(searchKeyword);
            parameters.add(searchKeyword);
        }

        if (hasText(status)) {
            conditions.add("LTRIM(RTRIM(b.[status])) = ?");
            parameters.add(status.trim());
        }

        if (hasText(paymentStatus)) {
            conditions.add("LTRIM(RTRIM(b.payment_status)) = ?");
            parameters.add(paymentStatus.trim());
        }

        if (hasText(source)) {
            conditions.add("LTRIM(RTRIM(b.[source])) = ?");
            parameters.add(source.trim());
        }

        if (roomTypeId != null && roomTypeId > 0) {
            conditions.add("b.room_type_id = ?");
            parameters.add(roomTypeId);
        }

        if (staffId != null && staffId > 0) {
            conditions.add("b.staff_id = ?");
            parameters.add(staffId);
        }

        addRoomNumberCondition(conditions, parameters, roomNumber);
        addDateCondition(conditions, parameters, dateFilter);

        return conditions;
    }

    private void addRoomNumberCondition(List<String> conditions, List<Object> parameters, String roomNumber) {
        // Thêm điều kiện tìm booking theo số phòng hoặc trạng thái chưa gán.
        if (!hasText(roomNumber)) {
            return;
        }

        String normalizedRoomNumber = roomNumber.trim().toLowerCase();

        if (UNASSIGNED_ROOM_KEYWORDS.contains(normalizedRoomNumber)) {
            conditions.add("""
                NOT EXISTS (
                    SELECT 1
                    FROM BookingRooms br
                    WHERE br.booking_id = b.booking_id
                )
                """.trim());

            return;
        }

        conditions.add("""
            EXISTS (
                SELECT 1
                FROM BookingRooms br
                INNER JOIN Rooms r ON br.room_id = r.room_id
                WHERE br.booking_id = b.booking_id
                  AND CAST(r.room_number AS NVARCHAR(20)) LIKE ?
            )
            """.trim());

        parameters.add("%" + roomNumber.trim() + "%");
    }

    private void addDateCondition(List<String> conditions, List<Object> parameters, String dateFilter) {
        // Thêm điều kiện lọc theo ngày nhận hoặc trả phòng dự kiến.
        if (!hasText(dateFilter)) {
            return;
        }

        try {
            Date selectedDate = Date.valueOf(dateFilter.trim());

            conditions.add("(b.checkin_date = ? OR b.checkout_date = ?)");
            parameters.add(selectedDate);
            parameters.add(selectedDate);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid booking date filter: " + dateFilter);
        }
    }

    private String getBookingListSortSql(String sort) {
        // Trả về câu lệnh sắp xếp tương ứng với lựa chọn của người dùng.
        if ("oldest".equals(sort)) {
            return "b.created_at ASC, b.booking_id ASC";
        }

        if ("checkinAsc".equals(sort)) {
            return "b.checkin_date ASC, b.booking_id DESC";
        }

        if ("checkoutAsc".equals(sort)) {
            return "b.checkout_date ASC, b.booking_id DESC";
        }

        return "b.created_at DESC, b.booking_id DESC";
    }

    private void setBookingListParams(PreparedStatement statement, List<Object> parameters)
            throws SQLException {

        // Gán toàn bộ tham số vào PreparedStatement theo đúng thứ tự.
        for (int index = 0; index < parameters.size(); index++) {
            Object value = parameters.get(index);
            int parameterIndex = index + 1;

            if (value instanceof Integer) {
                statement.setInt(parameterIndex, (Integer) value);
            } else if (value instanceof Date) {
                statement.setDate(parameterIndex, (Date) value);
            } else {
                statement.setString(parameterIndex, String.valueOf(value));
            }
        }
    }

    private boolean hasText(String value) {
        // Kiểm tra chuỗi có nội dung hợp lệ hay không.
        return value != null && !value.trim().isEmpty();
    }

    public int countBookingList(String keyword, String status, String paymentStatus,
            String source, Integer roomTypeId, Integer staffId, String roomNumber, String dateFilter) {

        cancelExpiredBookings();

        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT COUNT(*) AS total ");
        sql.append(" FROM Bookings b ");
        sql.append(" LEFT JOIN Guests g ON b.guest_id = g.guest_id ");
        sql.append(" INNER JOIN RoomTypes rt ON b.room_type_id = rt.room_type_id ");
        sql.append(" LEFT JOIN StaffAccounts s ON b.staff_id = s.staff_id ");
        sql.append(" WHERE 1 = 1 ");

        List<Object> params = new ArrayList<>();

        appendBookingListFilters(sql, params, keyword, status, paymentStatus, source,
                roomTypeId, staffId, roomNumber, dateFilter);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            setBookingListParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }

        } catch (Exception e) {
            System.out.println("countBookingList error: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public List<Map<String, Object>> getRoomTypesForBookingFilter() {
        String sql = ""
                + " SELECT "
                + "     room_type_id AS roomTypeId, "
                + "     type_name AS typeName "
                + " FROM RoomTypes "
                + " WHERE is_active = 1 "
                + " ORDER BY type_name ";

        return getBookingListSimpleRows(sql);
    }

    public List<Map<String, Object>> getStaffForBookingFilter() {
        String sql = ""
                + " SELECT "
                + "     staff_id AS staffId, "
                + "     full_name AS fullName "
                + " FROM StaffAccounts "
                + " WHERE is_active = 1 "
                + "   AND [role] = N'Lễ tân' "
                + " ORDER BY full_name ";

        return getBookingListSimpleRows(sql);
    }

    private void appendBookingListFilters(StringBuilder sql, List<Object> params, String keyword,
            String status, String paymentStatus, String source, Integer roomTypeId, Integer staffId,
            String roomNumber, String dateFilter) {

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND ( ");
            sql.append("     b.booking_code LIKE ? ");
            sql.append("     OR g.full_name LIKE ? ");
            sql.append("     OR g.phone LIKE ? ");
            sql.append("     OR g.email LIKE ? ");
            sql.append(" ) ");

            String kw = "%" + keyword.trim() + "%";

            params.add(kw);
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }

        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND b.[status] = ? ");
            params.add(status.trim());
        }

        if (paymentStatus != null && !paymentStatus.trim().isEmpty()) {
            sql.append(" AND b.payment_status = ? ");
            params.add(paymentStatus.trim());
        }

        if (source != null && !source.trim().isEmpty()) {
            sql.append(" AND b.[source] = ? ");
            params.add(source.trim());
        }

        if (roomTypeId != null && roomTypeId > 0) {
            sql.append(" AND b.room_type_id = ? ");
            params.add(roomTypeId);
        }

        if (staffId != null && staffId > 0) {
            sql.append(" AND b.staff_id = ? ");
            params.add(staffId);
        }

        if (roomNumber != null && !roomNumber.trim().isEmpty()) {
            String rn = roomNumber.trim().toLowerCase();

            if (rn.equals("chưa gán") || rn.equals("chua gan") || rn.equals("null")) {
                sql.append(" AND NOT EXISTS ( ");
                sql.append("     SELECT 1 ");
                sql.append("     FROM BookingRooms br ");
                sql.append("     WHERE br.booking_id = b.booking_id ");
                sql.append(" ) ");
            } else {
                sql.append(" AND EXISTS ( ");
                sql.append("     SELECT 1 ");
                sql.append("     FROM BookingRooms br ");
                sql.append("     INNER JOIN Rooms r ON br.room_id = r.room_id ");
                sql.append("     WHERE br.booking_id = b.booking_id ");
                sql.append("       AND CAST(r.room_number AS NVARCHAR(20)) LIKE ? ");
                sql.append(" ) ");

                params.add("%" + roomNumber.trim() + "%");
            }
        }

        if (dateFilter != null && !dateFilter.trim().isEmpty()) {
            Date d = Date.valueOf(dateFilter.trim());

            sql.append(" AND (b.checkin_date = ? OR b.checkout_date = ?) ");

            params.add(d);
            params.add(d);
        }
    }

    private List<Map<String, Object>> getBookingListSimpleRows(String sql) {
        List<Map<String, Object>> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapBookingListRow(rs));
            }

        } catch (Exception e) {
            System.out.println("getBookingListSimpleRows error: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    private Map<String, Object> mapBookingListRow(ResultSet rs) throws SQLException {
        Map<String, Object> row = new HashMap<>();
        ResultSetMetaData meta = rs.getMetaData();

        for (int i = 1; i <= meta.getColumnCount(); i++) {
            row.put(meta.getColumnLabel(i), rs.getObject(i));
        }

        return row;
    }

// STAFF BOOKING DETAIL POPUP
    public Map<String, Object> getStaffBookingDetailForPopup(int bookingId) {
        cancelExpiredBookings();

        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT ");
        sql.append("     b.booking_id AS bookingId, ");
        sql.append("     b.booking_code AS bookingCode, ");
        sql.append("     b.[status] AS bookingStatus, ");
        sql.append("     b.payment_status AS paymentStatus, ");
        sql.append("     b.[source] AS source, ");

        sql.append("     ISNULL(g.full_name, N'Khách vãng lai') AS guestName, ");
        sql.append("     ISNULL(g.phone, '') AS guestPhone, ");
        sql.append("     ISNULL(g.email, '') AS guestEmail, ");

        sql.append("     rt.type_name AS roomTypeName, ");
        sql.append("     b.num_rooms AS numRooms, ");
        sql.append("     b.num_guests AS numGuests, ");
        sql.append("     b.num_children AS numChildren, ");
        sql.append("     ISNULL(roomData.roomNumbers, N'").append(UNASSIGNED_ROOM_TEXT).append("') AS roomNumbers, ");

        sql.append("     CONVERT(VARCHAR(").append(SQL_DATE_TEXT_LENGTH).append("), b.checkin_date, 103) AS checkinDateText, ");
        sql.append("     CONVERT(VARCHAR(").append(SQL_DATE_TEXT_LENGTH).append("), b.checkout_date, 103) AS checkoutDateText, ");

        sql.append("     CONVERT(VARCHAR(").append(SQL_DATETIME_TEXT_LENGTH).append("), b.actual_checkin_time, 120) AS actualCheckinTime, ");
        sql.append("     CONVERT(VARCHAR(").append(SQL_DATETIME_TEXT_LENGTH).append("), b.actual_checkout_time, 120) AS actualCheckoutTime, ");

        sql.append("     b.booked_price_per_night AS bookedPricePerNight, ");
        sql.append("     ISNULL(b.deposit_amount, 0) AS depositAmount, ");

        sql.append("     CAST((b.booked_price_per_night * b.num_rooms * ");
        sql.append("         CASE ");
        sql.append("             WHEN DATEDIFF(DAY, b.checkin_date, b.checkout_date) <= 0 ");
        sql.append("             THEN 1 ");
        sql.append("             ELSE DATEDIFF(DAY, b.checkin_date, b.checkout_date) ");
        sql.append("         END ");
        sql.append("     ) AS DECIMAL(15,2)) AS estimatedTotal, ");

        sql.append("     ISNULL(s.full_name, N'Chưa có') AS staffName ");

        sql.append(" FROM Bookings b ");
        sql.append(" LEFT JOIN Guests g ON b.guest_id = g.guest_id ");
        sql.append(" INNER JOIN RoomTypes rt ON b.room_type_id = rt.room_type_id ");
        sql.append(" LEFT JOIN StaffAccounts s ON b.staff_id = s.staff_id ");

        sql.append(" OUTER APPLY ( ");
        sql.append("     SELECT ");
        sql.append("         STUFF(( ");
        sql.append("             SELECT N', ' + CAST(r2.room_number AS NVARCHAR(20)) ");
        sql.append("             FROM BookingRooms br2 ");
        sql.append("             INNER JOIN Rooms r2 ON br2.room_id = r2.room_id ");
        sql.append("             WHERE br2.booking_id = b.booking_id ");
        sql.append("             ORDER BY r2.room_number ");
        sql.append("             FOR XML PATH(''), TYPE ");
        sql.append("         ).value('.', 'NVARCHAR(MAX)'), 1, 2, N'') AS roomNumbers ");
        sql.append(" ) roomData ");

        sql.append(" WHERE b.booking_id = ? ");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            ps.setInt(1, bookingId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapStaffBookingDetailRow(rs);
                }
            }

        } catch (Exception e) {
            System.out.println("getStaffBookingDetailForPopup error: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public List<Map<String, Object>> getStaffBookingRecentRequests(int bookingId) {
        List<Map<String, Object>> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT TOP 5 ");
        sql.append("     gr.request_id AS requestId, ");
        sql.append("     gr.request_type AS requestType, ");
        sql.append("     gr.request_details AS requestDetails, ");
        sql.append("     gr.[status] AS requestStatus, ");
        sql.append("     CONVERT(VARCHAR(10), gr.submitted_at, 103) AS submittedAtText, ");
        sql.append("     CONVERT(VARCHAR(16), gr.requested_checkin, 120) AS requestedCheckinText, ");
        sql.append("     CONVERT(VARCHAR(16), gr.requested_checkout, 120) AS requestedCheckoutText, ");
        sql.append("     ISNULL(rt.type_name, '') AS targetRoomTypeName, ");
        sql.append("     ISNULL(gr.response_notes, '') AS responseNotes ");
        sql.append(" FROM GuestRequests gr ");
        sql.append(" LEFT JOIN RoomTypes rt ON gr.target_room_type_id = rt.room_type_id ");
        sql.append(" WHERE gr.booking_id = ? ");
        sql.append(" ORDER BY gr.submitted_at DESC, gr.request_id DESC ");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            ps.setInt(1, bookingId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapStaffBookingDetailRow(rs));
                }
            }

        } catch (Exception e) {
            System.out.println("getStaffBookingRecentRequests error: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    private Map<String, Object> mapStaffBookingDetailRow(ResultSet rs) throws SQLException {
        Map<String, Object> row = new HashMap<>();
        ResultSetMetaData meta = rs.getMetaData();

        for (int i = 1; i <= meta.getColumnCount(); i++) {
            row.put(meta.getColumnLabel(i), rs.getObject(i));
        }

        return row;
    }

// COUNTER REQUEST - TẠO & PHÊ DUYỆT TẠI QUẦY
    public Map<String, Object> getCounterRequestBookingInfo(int bookingId) {
        cancelExpiredBookings();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT ");
        sql.append("     b.booking_id AS bookingId, ");
        sql.append("     b.booking_code AS bookingCode, ");
        sql.append("     b.guest_id AS guestId, ");
        sql.append("     ISNULL(g.full_name, N'Khách vãng lai') AS guestName, ");
        sql.append("     ISNULL(g.phone, '') AS guestPhone, ");
        sql.append("     ISNULL(g.email, '') AS guestEmail, ");
        sql.append("     rt.type_name AS roomTypeName, ");
        sql.append("     b.room_type_id AS roomTypeId, ");
        sql.append("     b.num_rooms AS numRooms, ");
        sql.append("     b.num_guests AS numGuests, ");
        sql.append("     b.num_children AS numChildren, ");
        sql.append("     b.booked_price_per_night AS bookedPricePerNight, ");
        sql.append("     ISNULL(b.deposit_amount, 0) AS depositAmount, ");
        sql.append("     b.payment_status AS paymentStatus, ");
        sql.append("     b.[status] AS bookingStatus, ");
        sql.append("     CONVERT(VARCHAR(10), b.checkin_date, 103) AS checkinDateText, ");
        sql.append("     CONVERT(VARCHAR(10), b.checkout_date, 103) AS checkoutDateText, ");
        sql.append("     CONVERT(VARCHAR(10), b.checkin_date, 120) AS checkinDateSql, ");
        sql.append("     CONVERT(VARCHAR(10), b.checkout_date, 120) AS checkoutDateSql, ");
        sql.append("     DATEDIFF(DAY, b.checkin_date, b.checkout_date) AS totalNights, ");
        sql.append("     (b.booked_price_per_night * b.num_rooms * DATEDIFF(DAY, b.checkin_date, b.checkout_date)) AS estimatedRoomAmount, ");
        sql.append("     ISNULL(( ");
        sql.append("         SELECT COUNT(1) ");
        sql.append("         FROM BookingRooms br ");
        sql.append("         WHERE br.booking_id = b.booking_id ");
        sql.append("     ), 0) AS assignedRoomCount ");
        sql.append(" FROM Bookings b ");
        sql.append(" LEFT JOIN Guests g ON b.guest_id = g.guest_id ");
        sql.append(" INNER JOIN RoomTypes rt ON b.room_type_id = rt.room_type_id ");
        sql.append(" WHERE b.booking_id = ? ");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            ps.setInt(1, bookingId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCounterRequestRow(rs);
                }
            }

        } catch (Exception e) {
            System.out.println("getCounterRequestBookingInfo error: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public boolean applyCounterUpgradeRoomTypeRequest(
            int bookingId,
            int targetRoomTypeId,
            String note) {

        String getBookingSql = ""
                + " SELECT "
                + "     b.booking_id, "
                + "     b.num_rooms, "
                + "     b.booked_price_per_night AS oldPrice, "
                + "     b.checkin_date, "
                + "     b.checkout_date, "
                + "     b.[status] AS bookingStatus, "
                + "     rt.type_name AS oldRoomTypeName "
                + " FROM Bookings b "
                + " INNER JOIN RoomTypes rt ON b.room_type_id = rt.room_type_id "
                + " WHERE b.booking_id = ? ";

        String getRoomTypeSql = ""
                + " SELECT "
                + "     type_name, "
                + "     base_price "
                + " FROM RoomTypes "
                + " WHERE room_type_id = ? "
                + "   AND is_active = 1 ";

        String updateBookingSql = ""
                + " UPDATE Bookings "
                + " SET room_type_id = ?, "
                + "     booked_price_per_night = ? "
                + " WHERE booking_id = ? "
                + "   AND [status] = N'Đã xác nhận' ";

        String insertRequestSql = ""
                + " INSERT INTO GuestRequests ( "
                + "     booking_id, guest_id, request_type, request_details, target_room_type_id, "
                + "     [status], submitted_at, processed_at, response_notes "
                + " ) "
                + " SELECT "
                + "     b.booking_id, b.guest_id, N'Đổi hạng phòng', ?, ?, "
                + "     N'Đã phê duyệt', GETDATE(), GETDATE(), ? "
                + " FROM Bookings b "
                + " WHERE b.booking_id = ? ";

        try {
            connection.setAutoCommit(false);

            int numRooms = 0;
            BigDecimal oldPrice = BigDecimal.ZERO;
            String oldRoomTypeName = "";
            String bookingStatus = "";
            LocalDate checkinDate = null;
            LocalDate checkoutDate = null;

            try (PreparedStatement ps = connection.prepareStatement(getBookingSql)) {
                ps.setInt(1, bookingId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        numRooms = rs.getInt("num_rooms");
                        oldPrice = rs.getBigDecimal("oldPrice");
                        oldRoomTypeName = rs.getNString("oldRoomTypeName");
                        bookingStatus = rs.getNString("bookingStatus");

                        java.sql.Date sqlCheckinDate = rs.getDate("checkin_date");
                        java.sql.Date sqlCheckoutDate = rs.getDate("checkout_date");

                        if (sqlCheckinDate != null) {
                            checkinDate = sqlCheckinDate.toLocalDate();
                        }

                        if (sqlCheckoutDate != null) {
                            checkoutDate = sqlCheckoutDate.toLocalDate();
                        }
                    }
                }
            }

            if (!"Đã xác nhận".equals(bookingStatus)) {
                connection.rollback();
                return false;
            }

            BigDecimal newPrice = null;
            String newRoomTypeName = "";

            try (PreparedStatement ps = connection.prepareStatement(getRoomTypeSql)) {
                ps.setInt(1, targetRoomTypeId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        newPrice = rs.getBigDecimal("base_price");
                        newRoomTypeName = rs.getNString("type_name");
                    }
                }
            }

            if (newPrice == null) {
                connection.rollback();
                return false;
            }

            int chargeableNights = calculateUpgradeChargeableNightsForDao(
                    bookingStatus,
                    checkinDate,
                    checkoutDate
            );

            BigDecimal priceDiff = newPrice.subtract(oldPrice);

            BigDecimal upgradeAmount = priceDiff
                    .multiply(BigDecimal.valueOf(numRooms))
                    .multiply(BigDecimal.valueOf(chargeableNights))
                    .setScale(2, RoundingMode.CEILING.HALF_UP);

            String priceDiffText;
            String upgradeAmountText;
            String responseAmountText;

            if (priceDiff.compareTo(BigDecimal.ZERO) > 0) {
                priceDiffText = "Chênh lệch tăng: " + priceDiff + " đ/đêm";
            } else if (priceDiff.compareTo(BigDecimal.ZERO) < 0) {
                priceDiffText = "Chênh lệch giảm: " + priceDiff.abs() + " đ/đêm";
            } else {
                priceDiffText = "Không chênh lệch đơn giá";
            }

            if (upgradeAmount.compareTo(BigDecimal.ZERO) > 0) {
                upgradeAmountText = "Khách cần thanh toán thêm: " + upgradeAmount + " đ";
                responseAmountText = "Khách cần thanh toán thêm: " + upgradeAmount + " đ.";
            } else if (upgradeAmount.compareTo(BigDecimal.ZERO) < 0) {
                upgradeAmountText = "Khách được giảm/hoàn lại: " + upgradeAmount.abs() + " đ";
                responseAmountText = "Khách được giảm/hoàn lại: " + upgradeAmount.abs() + " đ.";
            } else {
                upgradeAmountText = "Không phát sinh chênh lệch tiền";
                responseAmountText = "Không phát sinh chênh lệch tiền.";
            }

            int updatedRows;

            try (PreparedStatement ps = connection.prepareStatement(updateBookingSql)) {
                ps.setInt(1, targetRoomTypeId);
                ps.setBigDecimal(2, newPrice);
                ps.setInt(3, bookingId);

                updatedRows = ps.executeUpdate();
            }

            if (updatedRows <= 0) {
                connection.rollback();
                return false;
            }

            String detail = "Đổi hạng phòng tại quầy. "
                    + "Hạng cũ: " + oldRoomTypeName + ". "
                    + "Hạng mới: " + newRoomTypeName + ". "
                    + "Giá cũ: " + oldPrice + " đ/đêm. "
                    + "Giá mới: " + newPrice + " đ/đêm. "
                    + priceDiffText + ". "
                    + "Số phòng: " + numRooms + ". "
                    + "Số đêm tính đổi hạng: " + chargeableNights + ". "
                    + upgradeAmountText + ".";

            if (note != null && !note.trim().isEmpty()) {
                detail += " Ghi chú: " + note.trim();
            }

            String responseNote = "Đã xử lý nâng hạng tại quầy. "
                    + "Tổng tiền phát sinh: " + upgradeAmount + " đ.";

            try (PreparedStatement ps = connection.prepareStatement(insertRequestSql)) {
                ps.setNString(1, detail);
                ps.setInt(2, targetRoomTypeId);
                ps.setNString(3, responseNote);
                ps.setInt(4, bookingId);

                ps.executeUpdate();
            }

            connection.commit();
            return true;

        } catch (Exception e) {
            rollbackCounterRequestQuietly();
            System.out.println("applyCounterUpgradeRoomTypeRequest error: " + e.getMessage());
            e.printStackTrace();
            return false;

        } finally {
            restoreCounterRequestAutoCommit();
        }
    }

    private int calculateUpgradeChargeableNightsForDao(
            String bookingStatus,
            LocalDate checkinDate,
            LocalDate checkoutDate) {

        if (!"Đã xác nhận".equals(bookingStatus)) {
            return 0;
        }

        if (checkinDate == null || checkoutDate == null) {
            return 0;
        }

        if (!checkoutDate.isAfter(checkinDate)) {
            return 0;
        }

        return (int) ChronoUnit.DAYS.between(checkinDate, checkoutDate);
    }

    public boolean applyCounterExtendStayRequest(int bookingId, Date newCheckoutDate, String note) {
        // Gia hạn ngày trả phòng cho booking đã xác nhận hoặc đã nhận phòng.
        if (newCheckoutDate == null) {
            return false;
        }

        String updateBookingSql = """
            UPDATE Bookings
            SET checkout_date = ?
            WHERE booking_id = ?
              AND LTRIM(RTRIM([status])) IN (N'Đã xác nhận', N'Đã nhận phòng')
              AND checkout_date < ?
            """;

        String insertRequestSql = """
            INSERT INTO GuestRequests (
                booking_id,
                guest_id,
                request_type,
                request_details,
                requested_checkout,
                [status],
                submitted_at,
                processed_at,
                response_notes
            )
            SELECT
                b.booking_id,
                b.guest_id,
                N'Gia hạn phòng',
                ?,
                ?,
                N'Đã phê duyệt',
                GETDATE(),
                GETDATE(),
                N'Yêu cầu gia hạn được tạo và phê duyệt tại quầy'
            FROM Bookings b
            WHERE b.booking_id = ?
              AND LTRIM(RTRIM(b.[status])) IN (N'Đã xác nhận', N'Đã nhận phòng')
            """;

        try {
            connection.setAutoCommit(false);

            int updatedRows;

            try (PreparedStatement statement = connection.prepareStatement(updateBookingSql)) {
                statement.setDate(1, newCheckoutDate);
                statement.setInt(2, bookingId);
                statement.setDate(3, newCheckoutDate);

                updatedRows = statement.executeUpdate();
            }

            if (updatedRows <= 0) {
                connection.rollback();
                return false;
            }

            String detail = "Gia hạn ngày ở đến " + newCheckoutDate;

            if (note != null && !note.trim().isEmpty()) {
                detail += ". Ghi chú: " + note.trim();
            }

            int insertedRows;

            try (PreparedStatement statement = connection.prepareStatement(insertRequestSql)) {
                statement.setNString(1, detail);
                statement.setDate(2, newCheckoutDate);
                statement.setInt(3, bookingId);

                insertedRows = statement.executeUpdate();
            }

            if (insertedRows <= 0) {
                connection.rollback();
                return false;
            }

            connection.commit();
            return true;
        } catch (Exception e) {
            rollbackCounterRequestQuietly();
            System.out.println("applyCounterExtendStayRequest error: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            restoreCounterRequestAutoCommit();
        }
    }

    public boolean applyCounterCancelBookingRequest(int bookingId, int cancelRooms, String note) {
        // Hủy toàn bộ hoặc một phần booking đã xác nhận và chưa được gán phòng.
        String getBookingSql = """
            SELECT
                b.booking_id,
                b.guest_id,
                b.num_rooms,
                b.checkin_date,
                ISNULL(b.deposit_amount, 0) AS depositAmount,
                LTRIM(RTRIM(b.[status])) AS bookingStatus,
                ISNULL((
                    SELECT COUNT(*)
                    FROM BookingRooms br
                    WHERE br.booking_id = b.booking_id
                ), 0) AS assignedRoomCount
            FROM Bookings b WITH (UPDLOCK, ROWLOCK)
            WHERE b.booking_id = ?
            """;

        String updateFullCancelSql = """
            UPDATE b
            SET b.[status] = N'Đã hủy',
                b.cancelled_at = GETDATE(),
                b.cancellation_reason = ?,
                b.deposit_amount = ?
            FROM Bookings b
            WHERE b.booking_id = ?
              AND LTRIM(RTRIM(b.[status])) = N'Đã xác nhận'
              AND NOT EXISTS (
                  SELECT 1
                  FROM BookingRooms br
                  WHERE br.booking_id = b.booking_id
              )
            """;

        String updatePartialCancelSql = """
            UPDATE b
            SET b.num_rooms = b.num_rooms - ?,
                b.deposit_amount = ?
            FROM Bookings b
            WHERE b.booking_id = ?
              AND b.num_rooms > ?
              AND LTRIM(RTRIM(b.[status])) = N'Đã xác nhận'
              AND NOT EXISTS (
                  SELECT 1
                  FROM BookingRooms br
                  WHERE br.booking_id = b.booking_id
              )
            """;

        String insertRequestSql = """
            INSERT INTO GuestRequests (
                booking_id,
                guest_id,
                request_type,
                request_details,
                [status],
                submitted_at,
                processed_at,
                response_notes
            )
            SELECT
                b.booking_id,
                b.guest_id,
                N'Yêu cầu khác',
                ?,
                N'Đã phê duyệt',
                GETDATE(),
                GETDATE(),
                ?
            FROM Bookings b
            WHERE b.booking_id = ?
            """;

        try {
            connection.setAutoCommit(false);

            int currentRooms = 0;
            int assignedRoomCount = 0;
            String bookingStatus = "";
            BigDecimal depositAmount = BigDecimal.ZERO;
            LocalDate checkinDate = null;

            try (PreparedStatement statement = connection.prepareStatement(getBookingSql)) {
                statement.setInt(1, bookingId);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        currentRooms = resultSet.getInt("num_rooms");
                        assignedRoomCount = resultSet.getInt("assignedRoomCount");
                        bookingStatus = resultSet.getNString("bookingStatus");
                        depositAmount = resultSet.getBigDecimal("depositAmount");

                        if (bookingStatus != null) {
                            bookingStatus = bookingStatus.trim();
                        }

                        if (depositAmount == null) {
                            depositAmount = BigDecimal.ZERO;
                        }

                        Date sqlCheckinDate = resultSet.getDate("checkin_date");

                        if (sqlCheckinDate != null) {
                            checkinDate = sqlCheckinDate.toLocalDate();
                        }
                    }
                }
            }

            if (currentRooms < MIN_VALID_ROOM_COUNT) {
                connection.rollback();
                return false;
            }

            if (!"Đã xác nhận".equals(bookingStatus) || assignedRoomCount != NO_ASSIGNED_ROOMS) {
                connection.rollback();
                return false;
            }

            if (cancelRooms < MIN_VALID_ROOM_COUNT || cancelRooms > currentRooms) {
                connection.rollback();
                return false;
            }

            boolean fullCancel = cancelRooms == currentRooms;
            BigDecimal feeRate = getCancelFeeRateByCheckin(checkinDate);
            BigDecimal currentRoomsValue = BigDecimal.valueOf(currentRooms);
            BigDecimal cancelRoomsValue = BigDecimal.valueOf(cancelRooms);

            BigDecimal depositPerRoom = depositAmount.divide(currentRoomsValue, MONEY_SCALE, MONEY_ROUNDING_MODE);
            BigDecimal cancelDeposit = depositPerRoom.multiply(cancelRoomsValue).setScale(MONEY_SCALE, MONEY_ROUNDING_MODE);
            BigDecimal cancelFee = cancelDeposit.multiply(feeRate).setScale(MONEY_SCALE, MONEY_ROUNDING_MODE);
            BigDecimal refundAmount = cancelDeposit.subtract(cancelFee).setScale(MONEY_SCALE, MONEY_ROUNDING_MODE);

            BigDecimal newDepositAmount;

            if (fullCancel) {
                newDepositAmount = cancelFee;
            } else {
                newDepositAmount = depositAmount.subtract(refundAmount).setScale(MONEY_SCALE, MONEY_ROUNDING_MODE);
            }

            if (newDepositAmount.compareTo(BigDecimal.ZERO) < 0) {
                newDepositAmount = BigDecimal.ZERO;
            }

            int updatedRows;

            if (fullCancel) {
                String cancellationReason = note == null || note.trim().isEmpty()
                        ? "Hủy booking tại quầy."
                        : note.trim();

                try (PreparedStatement statement = connection.prepareStatement(updateFullCancelSql)) {
                    statement.setNString(1, cancellationReason);
                    statement.setBigDecimal(2, newDepositAmount);
                    statement.setInt(3, bookingId);

                    updatedRows = statement.executeUpdate();
                }
            } else {
                try (PreparedStatement statement = connection.prepareStatement(updatePartialCancelSql)) {
                    statement.setInt(1, cancelRooms);
                    statement.setBigDecimal(2, newDepositAmount);
                    statement.setInt(3, bookingId);
                    statement.setInt(4, cancelRooms);

                    updatedRows = statement.executeUpdate();
                }
            }

            if (updatedRows <= 0) {
                connection.rollback();
                return false;
            }

            String cancelType = fullCancel ? "Hủy toàn bộ booking" : "Hủy một phần booking";
            String detail = cancelType + " tại quầy. Số phòng hủy: " + cancelRooms + "/" + currentRooms + ".";

            if (note != null && !note.trim().isEmpty()) {
                detail += " Lý do: " + note.trim();
            }

            String responseNote = "Đã xử lý hủy tại quầy. Loại xử lý: " + cancelType
                    + ". Số phòng hủy: " + cancelRooms + "/" + currentRooms
                    + ". Phí hủy: " + cancelFee + " đ"
                    + ". Tiền hoàn khách: " + refundAmount + " đ.";

            int insertedRows;

            try (PreparedStatement statement = connection.prepareStatement(insertRequestSql)) {
                statement.setNString(1, detail);
                statement.setNString(2, responseNote);
                statement.setInt(3, bookingId);

                insertedRows = statement.executeUpdate();
            }

            if (insertedRows <= 0) {
                connection.rollback();
                return false;
            }

            connection.commit();
            return true;
        } catch (Exception e) {
            rollbackCounterRequestQuietly();
            System.out.println("applyCounterCancelBookingRequest error: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            restoreCounterRequestAutoCommit();
        }
    }

    private BigDecimal getCancelFeeRateByCheckin(LocalDate checkinDate) {
        // Xác định tỷ lệ phí hủy dựa trên thời gian còn lại trước giờ nhận phòng.
        if (checkinDate == null) {
            return SEVENTY_PERCENT_CANCEL_RATE;
        }

        LocalDateTime checkinDeadline = checkinDate.atTime(CHECKIN_HOUR, CHECKIN_MINUTE);
        long hoursBeforeCheckin = ChronoUnit.HOURS.between(LocalDateTime.now(), checkinDeadline);

        if (hoursBeforeCheckin >= FREE_CANCEL_HOURS) {
            return FREE_CANCEL_RATE;
        }

        if (hoursBeforeCheckin >= THIRTY_PERCENT_CANCEL_HOURS) {
            return THIRTY_PERCENT_CANCEL_RATE;
        }

        if (hoursBeforeCheckin >= FIFTY_PERCENT_CANCEL_HOURS) {
            return FIFTY_PERCENT_CANCEL_RATE;
        }

        return SEVENTY_PERCENT_CANCEL_RATE;
    }

    public boolean applyCounterOtherRequest(int bookingId, String note) {
        // Tạo yêu cầu khác cho booking đã xác nhận hoặc đã nhận phòng.
        String requestNote = note == null ? "" : note.trim();

        if (requestNote.isEmpty()) {
            return false;
        }

        String insertRequestSql = """
            INSERT INTO GuestRequests (
                booking_id,
                guest_id,
                request_type,
                request_details,
                [status],
                submitted_at,
                processed_at,
                response_notes
            )
            SELECT
                b.booking_id,
                b.guest_id,
                N'Yêu cầu khác',
                ?,
                N'Đã phê duyệt',
                GETDATE(),
                GETDATE(),
                N'Yêu cầu được tạo và phê duyệt tại quầy'
            FROM Bookings b
            WHERE b.booking_id = ?
              AND LTRIM(RTRIM(b.[status])) IN (N'Đã xác nhận', N'Đã nhận phòng')
            """;

        try (PreparedStatement statement = connection.prepareStatement(insertRequestSql)) {
            statement.setNString(1, requestNote);
            statement.setInt(2, bookingId);

            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("applyCounterOtherRequest error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Map<String, Object> mapCounterRequestRow(ResultSet rs)
            throws SQLException {

        Map<String, Object> row = new HashMap<>();
        ResultSetMetaData meta = rs.getMetaData();

        for (int i = 1; i <= meta.getColumnCount(); i++) {
            row.put(meta.getColumnLabel(i), rs.getObject(i));
        }

        return row;
    }

    private void rollbackCounterRequestQuietly() {
        try {
            connection.rollback();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void restoreCounterRequestAutoCommit() {
        try {
            connection.setAutoCommit(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Map<String, Object>> getRoomTypesForCounterUpgrade(int currentRoomTypeId) {
        List<Map<String, Object>> list = new ArrayList<>();

        String sql = ""
                + " SELECT "
                + "     room_type_id AS roomTypeId, "
                + "     type_name AS typeName, "
                + "     base_price AS basePrice "
                + " FROM RoomTypes "
                + " WHERE is_active = 1 "
                + "   AND room_type_id <> ? "
                + " ORDER BY base_price ASC, type_name ASC ";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, currentRoomTypeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();

                    row.put("roomTypeId", rs.getInt("roomTypeId"));
                    row.put("typeName", rs.getNString("typeName"));
                    row.put("basePrice", rs.getBigDecimal("basePrice"));

                    list.add(row);
                }
            }

        } catch (Exception e) {
            System.out.println("getRoomTypesForCounterUpgrade error: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    public List<Map<String, Object>> getPublicBookingRequests(int bookingId) {
        List<Map<String, Object>> list = new ArrayList<>();

        String sql = """
             SELECT
                 gr.request_id AS requestId,
                 gr.request_type AS requestType,
                 gr.request_details AS requestDetails,
                 gr.[status] AS requestStatus,
                 CONVERT(VARCHAR(10), gr.submitted_at, 103) AS submittedAtText,
                 CONVERT(VARCHAR(16), gr.requested_checkin, 120) AS requestedCheckinText,
                 CONVERT(VARCHAR(16), gr.requested_checkout, 120) AS requestedCheckoutText,
                 ISNULL(rt.type_name, '') AS targetRoomTypeName,
                 ISNULL(gr.response_notes, '') AS responseNotes
             FROM GuestRequests gr
             LEFT JOIN RoomTypes rt ON gr.target_room_type_id = rt.room_type_id
             WHERE gr.booking_id = ?
             ORDER BY gr.submitted_at DESC, gr.request_id DESC
             """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();

                    row.put("requestId", rs.getInt("requestId"));
                    row.put("requestType", rs.getNString("requestType"));
                    row.put("requestDetails", rs.getNString("requestDetails"));
                    row.put("requestStatus", rs.getNString("requestStatus"));
                    row.put("submittedAtText", rs.getString("submittedAtText"));
                    row.put("requestedCheckinText", rs.getString("requestedCheckinText"));
                    row.put("requestedCheckoutText", rs.getString("requestedCheckoutText"));
                    row.put("targetRoomTypeName", rs.getNString("targetRoomTypeName"));
                    row.put("responseNotes", rs.getNString("responseNotes"));

                    list.add(row);
                }
            }

        } catch (Exception e) {
            System.out.println("getPublicBookingRequests error: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    public List<Map<String, Object>> getPublicBookingChanges(int bookingId) {
        List<Map<String, Object>> list = new ArrayList<>();

        String sql = """
             SELECT
                 gr.request_id AS requestId,
                 gr.request_type AS requestType,
                 gr.request_details AS requestDetails,
                 gr.[status] AS requestStatus,
                 CONVERT(VARCHAR(10), gr.processed_at, 103) AS processedAtText,
                 ISNULL(rt.type_name, '') AS targetRoomTypeName,
                 ISNULL(gr.response_notes, '') AS responseNotes
             FROM GuestRequests gr
             LEFT JOIN RoomTypes rt ON gr.target_room_type_id = rt.room_type_id
             WHERE gr.booking_id = ?
               AND gr.[status] IN (N'Đã phê duyệt', N'Đã từ chối')
             ORDER BY gr.processed_at DESC, gr.request_id DESC
             """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();

                    row.put("requestId", rs.getInt("requestId"));
                    row.put("requestType", rs.getNString("requestType"));
                    row.put("requestDetails", rs.getNString("requestDetails"));
                    row.put("requestStatus", rs.getNString("requestStatus"));
                    row.put("processedAtText", rs.getString("processedAtText"));
                    row.put("targetRoomTypeName", rs.getNString("targetRoomTypeName"));
                    row.put("responseNotes", rs.getNString("responseNotes"));

                    list.add(row);
                }
            }

        } catch (Exception e) {
            System.out.println("getPublicBookingChanges error: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }
}
