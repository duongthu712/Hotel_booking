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

public class BookingDAO extends DBContext {

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

    //Thư
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

    // Dùng cho thanh search của check in (Bổ sung giờ hẹn đến)
    public BookingCheckInView getBookingForCheckIn(String bookingCode) {
        String sql = "SELECT b.booking_id, b.booking_code, b.num_rooms, b.num_guests, b.num_children AS booking_num_children, b.payment_status, b.deposit_amount, "
                + "b.[status], b.actual_checkin_time, "
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

    // Hiện list check in ngày hôm nay (ĐÃ TÍCH HỢP HẸN GIỜ ĐẾN)
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

    // Hủy đơn trực tiếp nếu quá giờ check in
    public boolean cancelBooking(int bookingId) {
        String sql = "UPDATE Bookings SET [status] = N'Đã hủy' WHERE booking_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            System.out.println("Lỗi cancelBooking: " + e.getMessage());
        }
        return false;
    }

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

    //Hàm tự động hủy đơn 
    public int autoCancelExpiredBookings() {
        String sql = """
                     UPDATE Bookings 
                     SET [status] = N'Đã hủy', 
                         cancellation_reason = ISNULL(cancellation_reason, '') + N' [Hệ thống]: Tự động hủy do quá hạn deadline check-in.'
                     WHERE [status] = N'Đã xác nhận' 
                       AND auto_cancel_deadline < GETDATE()
                     """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            return ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Lỗi autoCancelExpiredBookings: " + e.getMessage());
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
    public List<Map<String, Object>> getBookingList(String keyword, String status, String paymentStatus,
            String source, Integer roomTypeId, Integer staffId, String roomNumber,
            String dateFilter, String sort, int page, int pageSize) {

        List<Map<String, Object>> list = new ArrayList<>();
        cancelExpiredBookings();

        if (page < 1) {
            page = 1;
        }

        if (pageSize <= 0) {
            pageSize = 10;
        }

        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT ");
        sql.append("     b.booking_id AS bookingId, ");
        sql.append("     b.booking_code AS bookingCode, ");
        sql.append("     ISNULL(g.full_name, N'Khách vãng lai') AS guestName, ");
        sql.append("     ISNULL(g.email, '') AS guestEmail, ");
        sql.append("     ISNULL(g.phone, '') AS guestPhone, ");
        sql.append("     rt.type_name AS roomTypeName, ");
        sql.append("     b.num_rooms AS numRooms, ");
        sql.append("     ISNULL(rd.roomNumbers, N'Chưa gán') AS roomNumbers, ");
        sql.append("     ISNULL(rd.assignedRoomCount, 0) AS assignedRoomCount, ");
        sql.append("     CONVERT(VARCHAR(10), b.checkin_date, 103) AS checkinDateText, ");
        sql.append("     CONVERT(VARCHAR(10), b.checkout_date, 103) AS checkoutDateText, ");
        sql.append("     b.[source] AS source, ");
        sql.append("     b.[status] AS bookingStatus, ");
        sql.append("     b.payment_status AS paymentStatus, ");
        sql.append("     ISNULL(rq.pendingRequestCount, 0) AS pendingRequestCount, ");
        sql.append("     ISNULL(rq.approvedRequestCount, 0) AS approvedRequestCount, ");
        sql.append("     lr.request_type AS latestRequestType, ");
        sql.append("     lr.[status] AS latestRequestStatus, ");
        sql.append("     ISNULL(s.full_name, N'Chưa có') AS staffName, ");

        sql.append("     CASE ");
        sql.append("         WHEN b.[status] IN (N'Chờ xử lý', N'Đã xác nhận') ");
        sql.append("              AND ISNULL(rd.assignedRoomCount, 0) = 0 ");
        sql.append("         THEN 1 ELSE 0 ");
        sql.append("     END AS canCancel, ");

        sql.append("     CASE ");
        sql.append("         WHEN b.[status] = N'Đã xác nhận' ");
        sql.append("         THEN 1 ELSE 0 ");
        sql.append("     END AS canCheckin, ");

        sql.append("     CASE ");
        sql.append("         WHEN b.[status] = N'Đã nhận phòng' ");
        sql.append("         THEN 1 ELSE 0 ");
        sql.append("     END AS canCheckout, ");

        sql.append("     CASE ");
        sql.append("         WHEN ISNULL(rq.pendingRequestCount, 0) > 0 ");
        sql.append("         THEN 1 ELSE 0 ");
        sql.append("     END AS canProcessRequest ");

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
        sql.append("         ).value('.', 'NVARCHAR(MAX)'), 1, 2, N'') AS roomNumbers, ");
        sql.append("         ( ");
        sql.append("             SELECT COUNT(*) ");
        sql.append("             FROM BookingRooms br3 ");
        sql.append("             WHERE br3.booking_id = b.booking_id ");
        sql.append("         ) AS assignedRoomCount ");
        sql.append(" ) rd ");

        sql.append(" OUTER APPLY ( ");
        sql.append("     SELECT ");
        sql.append("         SUM(CASE WHEN gr.[status] = N'Chờ xử lý' THEN 1 ELSE 0 END) AS pendingRequestCount, ");
        sql.append("         SUM(CASE WHEN gr.[status] = N'Đã phê duyệt' THEN 1 ELSE 0 END) AS approvedRequestCount ");
        sql.append("     FROM GuestRequests gr ");
        sql.append("     WHERE gr.booking_id = b.booking_id ");
        sql.append(" ) rq ");

        sql.append(" OUTER APPLY ( ");
        sql.append("     SELECT TOP 1 ");
        sql.append("         gr.request_type, ");
        sql.append("         gr.[status] ");
        sql.append("     FROM GuestRequests gr ");
        sql.append("     WHERE gr.booking_id = b.booking_id ");
        sql.append("     ORDER BY gr.submitted_at DESC, gr.request_id DESC ");
        sql.append(" ) lr ");

        sql.append(" WHERE 1 = 1 ");

        List<Object> params = new ArrayList<>();

        appendBookingListFilters(sql, params, keyword, status, paymentStatus, source,
                roomTypeId, staffId, roomNumber, dateFilter);

        sql.append(" ORDER BY ");
        sql.append(getBookingListSortSql(sort));
        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ");

        params.add((page - 1) * pageSize);
        params.add(pageSize);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            setBookingListParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapBookingListRow(rs));
                }
            }

        } catch (Exception e) {
            System.out.println("getBookingList error: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
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

    private String getBookingListSortSql(String sort) {
        if ("oldest".equals(sort)) {
            return " b.created_at ASC, b.booking_id ASC ";
        }

        if ("checkinAsc".equals(sort)) {
            return " b.checkin_date ASC, b.booking_id DESC ";
        }

        if ("checkoutAsc".equals(sort)) {
            return " b.checkout_date ASC, b.booking_id DESC ";
        }

        return " b.created_at DESC, b.booking_id DESC ";
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

    private void setBookingListParams(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            Object value = params.get(i);
            int index = i + 1;

            if (value instanceof Integer) {
                ps.setInt(index, (Integer) value);
            } else if (value instanceof Date) {
                ps.setDate(index, (Date) value);
            } else {
                ps.setString(index, value.toString());
            }
        }
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
        sql.append("     ISNULL(roomData.roomNumbers, N'Chưa gán') AS roomNumbers, ");
        sql.append("     CONVERT(VARCHAR(10), b.checkin_date, 103) AS checkinDateText, ");
        sql.append("     CONVERT(VARCHAR(10), b.checkout_date, 103) AS checkoutDateText, ");
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

    public boolean applyCounterExtendStayRequest(
            int bookingId,
            java.sql.Date newCheckoutDate,
            String note) {

        String updateBookingSql = ""
                + " UPDATE Bookings "
                + " SET checkout_date = ? "
                + " WHERE booking_id = ? "
                + "   AND [status] IN (N'Chờ xử lý', N'Đã xác nhận', N'Đã nhận phòng') ";

        String insertRequestSql = ""
                + " INSERT INTO GuestRequests ( "
                + "     booking_id, guest_id, request_type, request_details, requested_checkout, "
                + "     [status], submitted_at, processed_at, response_notes "
                + " ) "
                + " SELECT "
                + "     b.booking_id, b.guest_id, N'Gia hạn phòng', ?, ?, "
                + "     N'Đã phê duyệt', GETDATE(), GETDATE(), "
                + "     N'Yêu cầu gia hạn được tạo và phê duyệt tại quầy' "
                + " FROM Bookings b "
                + " WHERE b.booking_id = ? ";

        try {
            connection.setAutoCommit(false);

            int updatedRows;

            try (PreparedStatement ps = connection.prepareStatement(updateBookingSql)) {
                ps.setDate(1, newCheckoutDate);
                ps.setInt(2, bookingId);
                updatedRows = ps.executeUpdate();
            }

            if (updatedRows <= 0) {
                connection.rollback();
                return false;
            }

            String detail = "Gia hạn ngày ở đến " + newCheckoutDate;
            if (note != null && !note.trim().isEmpty()) {
                detail += ". Ghi chú: " + note.trim();
            }

            try (PreparedStatement ps = connection.prepareStatement(insertRequestSql)) {
                ps.setNString(1, detail);
                ps.setDate(2, newCheckoutDate);
                ps.setInt(3, bookingId);
                ps.executeUpdate();
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

    public boolean applyCounterCancelBookingRequest(
            int bookingId,
            int cancelRooms,
            String note) {

        String getBookingSql = ""
                + " SELECT "
                + "     b.booking_id, "
                + "     b.guest_id, "
                + "     b.num_rooms, "
                + "     b.checkin_date, "
                + "     ISNULL(b.deposit_amount, 0) AS depositAmount, "
                + "     LTRIM(RTRIM(b.[status])) AS bookingStatus "
                + " FROM Bookings b WITH (UPDLOCK) "
                + " WHERE b.booking_id = ? ";

        String updateFullCancelSql = ""
                + " UPDATE Bookings "
                + " SET [status] = N'Đã hủy', "
                + "     cancelled_at = GETDATE(), "
                + "     cancellation_reason = ?, "
                + "     deposit_amount = ? "
                + " WHERE booking_id = ? "
                + "   AND LTRIM(RTRIM([status])) IN (N'Chờ xử lý', N'Đã xác nhận') ";

        String updatePartialCancelSql = ""
                + " UPDATE Bookings "
                + " SET num_rooms = num_rooms - ?, "
                + "     deposit_amount = ? "
                + " WHERE booking_id = ? "
                + "   AND num_rooms > ? "
                + "   AND LTRIM(RTRIM([status])) IN (N'Chờ xử lý', N'Đã xác nhận') ";

        String insertRequestSql = ""
                + " INSERT INTO GuestRequests ( "
                + "     booking_id, guest_id, request_type, request_details, "
                + "     [status], submitted_at, processed_at, response_notes "
                + " ) "
                + " SELECT "
                + "     b.booking_id, b.guest_id, N'Yêu cầu khác', ?, "
                + "     N'Đã phê duyệt', GETDATE(), GETDATE(), ? "
                + " FROM Bookings b "
                + " WHERE b.booking_id = ? ";

        try {
            connection.setAutoCommit(false);

            int currentRooms = 0;
            String bookingStatus = "";
            BigDecimal depositAmount = BigDecimal.ZERO;
            LocalDate checkinDate = null;

            try (PreparedStatement ps = connection.prepareStatement(getBookingSql)) {
                ps.setInt(1, bookingId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        currentRooms = rs.getInt("num_rooms");
                        bookingStatus = rs.getNString("bookingStatus");

                        if (bookingStatus != null) {
                            bookingStatus = bookingStatus.trim();
                        }

                        depositAmount = rs.getBigDecimal("depositAmount");

                        if (depositAmount == null) {
                            depositAmount = BigDecimal.ZERO;
                        }

                        java.sql.Date sqlCheckinDate = rs.getDate("checkin_date");

                        if (sqlCheckinDate != null) {
                            checkinDate = sqlCheckinDate.toLocalDate();
                        }
                    }
                }
            }

            if (currentRooms <= 0) {
                connection.rollback();
                return false;
            }

            if (!"Chờ xử lý".equals(bookingStatus)
                    && !"Đã xác nhận".equals(bookingStatus)) {
                connection.rollback();
                return false;
            }

            if (cancelRooms <= 0 || cancelRooms > currentRooms) {
                connection.rollback();
                return false;
            }

            boolean fullCancel = cancelRooms == currentRooms;

            BigDecimal feeRate = getCancelFeeRateByCheckin(checkinDate);

            BigDecimal depositPerRoom = BigDecimal.ZERO;

            if (currentRooms > 0) {
                depositPerRoom = depositAmount.divide(
                        BigDecimal.valueOf(currentRooms),
                        2,
                        RoundingMode.HALF_UP
                );
            }

            BigDecimal cancelDeposit = depositPerRoom
                    .multiply(BigDecimal.valueOf(cancelRooms))
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal cancelFee = cancelDeposit
                    .multiply(feeRate)
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal refundAmount = cancelDeposit
                    .subtract(cancelFee)
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal newDepositAmount;

            if (fullCancel) {
                newDepositAmount = cancelFee;
            } else {
                newDepositAmount = depositAmount
                        .subtract(refundAmount)
                        .setScale(2, RoundingMode.HALF_UP);
            }

            if (newDepositAmount.compareTo(BigDecimal.ZERO) < 0) {
                newDepositAmount = BigDecimal.ZERO;
            }

            int updatedRows;

            if (fullCancel) {
                String cancellationReason = note == null || note.trim().isEmpty()
                        ? "Hủy booking tại quầy."
                        : note.trim();

                try (PreparedStatement ps = connection.prepareStatement(updateFullCancelSql)) {
                    ps.setNString(1, cancellationReason);
                    ps.setBigDecimal(2, newDepositAmount);
                    ps.setInt(3, bookingId);

                    updatedRows = ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = connection.prepareStatement(updatePartialCancelSql)) {
                    ps.setInt(1, cancelRooms);
                    ps.setBigDecimal(2, newDepositAmount);
                    ps.setInt(3, bookingId);
                    ps.setInt(4, cancelRooms);

                    updatedRows = ps.executeUpdate();
                }
            }

            if (updatedRows <= 0) {
                connection.rollback();
                return false;
            }

            String detail;

            if (fullCancel) {
                detail = "Hủy toàn bộ booking tại quầy. Số phòng hủy: "
                        + cancelRooms + "/" + currentRooms + ".";
            } else {
                detail = "Hủy một phần booking tại quầy. Số phòng hủy: "
                        + cancelRooms + "/" + currentRooms + ".";
            }

            if (note != null && !note.trim().isEmpty()) {
                detail += " Lý do: " + note.trim();
            }

            String responseNote = "Đã xử lý hủy tại quầy. "
                    + "Loại xử lý: "
                    + (fullCancel ? "Hủy toàn bộ booking" : "Hủy một phần booking")
                    + ". Số phòng hủy: " + cancelRooms + "/" + currentRooms
                    + ". Phí hủy: " + cancelFee + " đ"
                    + ". Tiền hoàn khách: " + refundAmount + " đ.";

            try (PreparedStatement ps = connection.prepareStatement(insertRequestSql)) {
                ps.setNString(1, detail);
                ps.setNString(2, responseNote);
                ps.setInt(3, bookingId);

                ps.executeUpdate();
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
        if (checkinDate == null) {
            return new BigDecimal("0.70");
        }

        LocalDateTime checkinDeadline = checkinDate.atTime(14, 0);
        long hoursBeforeCheckin = ChronoUnit.HOURS.between(LocalDateTime.now(), checkinDeadline);

        if (hoursBeforeCheckin >= 72) {
            return BigDecimal.ZERO;
        }

        if (hoursBeforeCheckin >= 48) {
            return new BigDecimal("0.30");
        }

        if (hoursBeforeCheckin >= 24) {
            return new BigDecimal("0.50");
        }

        return new BigDecimal("0.70");
    }

    public boolean applyCounterOtherRequest(
            int bookingId,
            String note) {

        String insertRequestSql = ""
                + " INSERT INTO GuestRequests ( "
                + "     booking_id, guest_id, request_type, request_details, "
                + "     [status], submitted_at, processed_at, response_notes "
                + " ) "
                + " SELECT "
                + "     b.booking_id, b.guest_id, N'Yêu cầu khác', ?, "
                + "     N'Đã phê duyệt', GETDATE(), GETDATE(), "
                + "     N'Yêu cầu được tạo và phê duyệt tại quầy' "
                + " FROM Bookings b "
                + " WHERE b.booking_id = ? "
                + "   AND b.[status] NOT IN (N'Đã hủy', N'Đã trả phòng') ";

        try (PreparedStatement ps = connection.prepareStatement(insertRequestSql)) {
            ps.setNString(1, note == null ? "" : note.trim());
            ps.setInt(2, bookingId);

            return ps.executeUpdate() > 0;

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
