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
import java.sql.Statement;
import java.sql.Date;
import model.Guest;

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

    // Thư
    
    // Lấy đơn lên để cập nhật thêm thông tin lúc check in
    public boolean updateCheckInAdvance(int bookingId, int currentGuestId, String fullName, String phone, String email,
            String idNumber, String nationality, String dobStr, int numGuests, boolean isDifferentGuest) {
        String insertNewGuestSql = "INSERT INTO Guests (full_name, phone, email, id_number, nationality, date_of_birth) VALUES (?, ?, ?, ?, ?, ?)";
        String updateBookingGuestSql = "UPDATE Bookings SET guest_id = ?, num_guests = ?, actual_checkin_time = GETDATE() WHERE booking_id = ?";
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
                try (PreparedStatement psUpdateOld = connection.prepareStatement(updateOldGuestSql)) {
                    psUpdateOld.setNString(1, fullName.trim());
                    psUpdateOld.setString(2, phone != null ? phone.trim() : null);
                    psUpdateOld.setString(3, email != null ? email.trim() : null);
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

    // Dùng cho thanh search của check in 
   public BookingCheckInView getBookingForCheckIn(String bookingCode) {
        String sql = "SELECT b.booking_id, b.booking_code, b.num_rooms, b.num_guests, b.payment_status, b.deposit_amount, "
                + "b.[status], b.actual_checkin_time, "
                + "g.guest_id, g.full_name, g.phone, g.email, g.id_number, g.date_of_birth, g.nationality, "
                + "rt.type_name, rt.capacity, "
                + "r.request_type, r.request_details, r.status AS request_status, "
                + "CONVERT(VARCHAR(19), r.requested_checkin, 120) AS requested_checkin "
                + "FROM Bookings b "
                + "LEFT JOIN Guests g ON b.guest_id = g.guest_id "
                + "INNER JOIN RoomTypes rt ON b.room_type_id = rt.room_type_id "
                + "LEFT JOIN GuestRequests r ON b.booking_id = r.booking_id "
                + "                           AND r.[status] = N'Đã phê duyệt' " // 🚀 CHẶN LẶP DÒNG TÌM KIẾM
                + "                           AND r.request_type IN (N'Nhận phòng sớm', N'Nhận phòng muộn') "
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
                    b.setPaymentStatus(rs.getString("payment_status"));
                    b.setDepositAmount(rs.getBigDecimal("deposit_amount"));
                    b.setStatus(rs.getString("status"));
                    b.setActualCheckInTime(rs.getString("actual_checkin_time"));
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
                    return b;
                }
            }
        } catch (Exception e) {
            System.out.println("Lỗi getBookingForCheckIn: " + e.getMessage());
        }
        return null;
    }

    // Hiện list check in ngày hôm nay
    public List<BookingCheckInView> getBookingsToday() {
        List<BookingCheckInView> list = new ArrayList<>();
        java.time.LocalDate today = java.time.LocalDate.now();
        String todayStr = today.toString();

       String sql = "SELECT b.booking_id, b.booking_code, b.num_rooms, b.num_guests, b.payment_status, b.deposit_amount, "
        + "b.[status], b.actual_checkin_time, "
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
                    b.setPaymentStatus(rs.getString("payment_status"));
                    b.setDepositAmount(rs.getBigDecimal("deposit_amount"));
                    b.setStatus(rs.getString("status"));
                    b.setActualCheckInTime(rs.getString("actual_checkin_time"));
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

    // Cập nhật trạng thái booking.
// Nếu chuyển sang Đã nhận phòng thì lưu thời gian check-in thực tế.
// Nếu chuyển sang Đã trả phòng thì lưu thời gian check-out thực tế.
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

    // 1. Hàm đếm số lượng phòng thực tế đã gán vào bảng BookingRooms
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

// 2. Hàm kiểm tra xem có phòng nào đã gán nhưng chưa được chia khách vào ở hay không
    public boolean hasEmptyRoomWithoutGuests(int bookingId) {
        // Câu lệnh này tìm xem có phòng nào thuộc booking_id này mà KHÔNG xuất hiện trong bảng GuestStays
        String sql = "SELECT COUNT(*) FROM BookingRooms br "
                + "LEFT JOIN GuestStays gs ON br.booking_room_id = gs.booking_room_id "
                + "WHERE br.booking_id = ? AND gs.stay_id IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Nếu > 0 tức là có phòng đang bị bỏ trống không có khách
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
        try {
            String sql = """
                     UPDATE b
                     SET b.[status] = N'Đã hủy',
                         b.cancelled_at = GETDATE(),
                         b.cancellation_reason = N'Quá thời hạn thanh toán 15 phút'
                     FROM Bookings b
                     WHERE b.[status] = N'Chờ xử lý'
                       AND b.payment_status = N'Chưa thanh toán'
                       AND b.[source] = N'Đặt phòng trực tuyến'
                       AND DATEADD(MINUTE, 15, b.created_at) <= GETDATE()
                       AND NOT EXISTS (
                           SELECT 1
                           FROM DepositPayments dp
                           WHERE dp.booking_id = b.booking_id
                       )
                     """;

            stm = connection.prepareStatement(sql);
            return stm.executeUpdate();

        } catch (Exception e) {
            System.out.println("cancelExpiredBookings: " + e.getMessage());
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
}
