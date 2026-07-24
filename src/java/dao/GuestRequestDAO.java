package dao;

import dal.DBContext;
import dto.GuestRequestDTO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GuestRequestDAO extends DBContext {

    // Author: ThuDNM-HE204370
    // 1. Kiểm tra đơn hàng có yêu cầu đang chờ xử lý không
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
            e.printStackTrace();
        }
        return false;
    }

    // Author: ThuDNM-HE204370
    // 2. Hàm kiểm tra phòng trống (Dùng chung cho Đổi hạng phòng và Gia hạn)
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
                WHERE room_type_id = ? AND is_active = 1 AND [status] != N'Đang bảo trì'
            )
            SELECT (ISNULL(tar.total_rooms, 0) - ISNULL(br.total_booked_rooms, 0)) AS available_rooms
            FROM TotalActiveRooms tar, BookedRooms br
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomTypeId);
            if (bookingIdToExclude != null) {
                ps.setInt(2, bookingIdToExclude);
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setDate(3, Date.valueOf(checkIn));  // Khớp với b.checkin_date < ?
            ps.setDate(4, Date.valueOf(checkOut)); // Khớp với b.checkout_date > ?
            ps.setInt(5, roomTypeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("available_rooms") >= requiredRooms;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    // Author: ThuDNM-HE204370
// Thêm vào bảng request
    public boolean insertGuestRequest(int bookingId, int guestId, String requestType, String details,
            LocalDate reqCheckIn, LocalDate reqCheckOut, Integer targetRoomTypeId) {
        String sql = "INSERT INTO GuestRequests (booking_id, guest_id, request_type, request_details, requested_checkin, requested_checkout, target_room_type_id, submitted_at, [status]) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE(), N'Chờ xử lý')";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, bookingId);

            // XỬ LÝ KHÓA NGOẠI: Nếu là đơn Walk-in (guestId = 0 hoặc null), ép về ID khách đầu tiên hợp lệ trong DB (Ví dụ: 1)
            stm.setInt(2, guestId > 0 ? guestId : 1);

            stm.setNString(3, requestType);
            stm.setNString(4, details);

            // XỬ LÝ KIỂU DATETIME: Sử dụng java.sql.Timestamp thay cho java.sql.Date để khớp cấu trúc DATETIME của DB
            if ("Hủy đặt phòng".equals(requestType)) {
                stm.setNull(5, java.sql.Types.TIMESTAMP);
                stm.setNull(6, java.sql.Types.TIMESTAMP);
                // Đơn hủy không có hạng phòng mong muốn, gán Types.INTEGER về NULL cho cột target_room_type_id (vì cột này trong DB của bạn cho phép NULL)
                stm.setNull(7, java.sql.Types.INTEGER);
            } else {
                // Đổi hạng và Gia hạn truyền đúng định dạng quy đổi sang Timestamp thời gian
                stm.setTimestamp(5, reqCheckIn != null ? java.sql.Timestamp.valueOf(reqCheckIn.atStartOfDay()) : null);
                stm.setTimestamp(6, reqCheckOut != null ? java.sql.Timestamp.valueOf(reqCheckOut.atStartOfDay()) : null);
                if (targetRoomTypeId != null) {
                    stm.setInt(7, targetRoomTypeId);
                } else {
                    stm.setNull(7, java.sql.Types.INTEGER);
                }
            }

            return stm.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Author: ThuDNM-HE204370
 // Lấy đơn booking cho receptionist xử lý
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

                    // Điểm mấu chốt chống sập: Đọc guest_id ra, nếu là NULL (hoặc bằng 0) gán mặc định bằng 1
                    int gId = rs.getInt("guest_id");
                    b.setGuestId(gId > 0 ? gId : 1);

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

    // Author: ThuDNM-HE204370
    // 5. Từ chối yêu cầu
    public boolean rejectRequest(int requestId, String notes) {
        String sql = "UPDATE GuestRequests SET [status] = N'Đã từ chối', response_notes = ?, processed_at = GETDATE() WHERE request_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, notes);
            ps.setInt(2, requestId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Author: ThuDNM-HE204370
    // 6. Lấy thông tin chi tiết một yêu cầu để xử lý (DÙNG CHO TRANG CHI TIẾT)
    public dto.GuestRequestDTO getRequestForProcessing(int requestId) {
        String sql = """
    SELECT gr.*, b.booking_code, b.num_rooms, b.checkin_date, b.checkout_date, b.room_type_id,
           g.full_name, g.phone,
           rt_curr.type_name as curr_name, rt_curr.base_price as curr_price,
           rt_targ.type_name as targ_name, rt_targ.base_price as targ_price
    FROM GuestRequests gr
    LEFT JOIN Bookings b ON gr.booking_id = b.booking_id
    LEFT JOIN Guests g ON gr.guest_id = g.guest_id
    LEFT JOIN RoomTypes rt_curr ON b.room_type_id = rt_curr.room_type_id
    LEFT JOIN RoomTypes rt_targ ON gr.target_room_type_id = rt_targ.room_type_id
    WHERE gr.request_id = ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dto.GuestRequestDTO req = new dto.GuestRequestDTO();

                    // 1. Dữ liệu từ GuestRequests
                    req.setRequestId(rs.getInt("request_id"));
                    req.setBookingId(rs.getInt("booking_id"));
                    req.setRequestType(rs.getString("request_type"));
                    req.setTargetRoomTypeId(rs.getObject("target_room_type_id") != null ? rs.getInt("target_room_type_id") : null);
                    req.setRequestDetails(rs.getString("request_details") != null ? rs.getString("request_details") : "");
                    // CHỐNG NULL: Kiểm tra requested_checkout
                    java.sql.Timestamp reqCheckoutTs = rs.getTimestamp("requested_checkout");
                    if (reqCheckoutTs != null) {
                        req.setRequestedCheckout(reqCheckoutTs.toLocalDateTime());
                    } else {
                        req.setRequestedCheckout(null);
                    }

                    // CHỐNG NULL: Định dạng thời gian gửi đơn submitted_at và gán đối tượng LocalDateTime
                    java.sql.Timestamp submittedAtTs = rs.getTimestamp("submitted_at");
                    if (submittedAtTs != null) {
                        java.time.LocalDateTime ldt = submittedAtTs.toLocalDateTime();

                        // ĐÃ SỬA: Gán đối tượng ldt gốc vào DTO để phục vụ tính toán khoảng cách giờ ở tầng xử lý duyệt đơn
                        req.setSubmittedAt(ldt);

                        req.setFormattedTime(ldt.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
                        req.setFormattedDate(ldt.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    } else {
                        req.setFormattedTime("N/A");
                        req.setFormattedDate("");
                        req.setSubmittedAt(null);
                    }

                    // 2. Dữ liệu từ Bookings
                    req.setBookingCode(rs.getString("booking_code") != null ? rs.getString("booking_code") : "N/A");
                    req.setNumRooms(rs.getInt("num_rooms"));

                    if (rs.getDate("checkin_date") != null) {
                        req.setCheckInDate(rs.getDate("checkin_date").toLocalDate());
                    }
                    if (rs.getDate("checkout_date") != null) {
                        req.setCheckOutDate(rs.getDate("checkout_date").toLocalDate());
                    }
                    req.setRoomTypeId(rs.getInt("room_type_id"));

                    // 3. Dữ liệu từ Guests
                    req.setGuestName(rs.getString("full_name") != null ? rs.getString("full_name") : "Khách vãng lai");
                    req.setGuestPhone(rs.getString("phone") != null ? rs.getString("phone") : "N/A");

                    // 4. Dữ liệu từ RoomTypes
                    req.setCurrentRoomTypeName(rs.getString("curr_name") != null ? rs.getString("curr_name") : "Chưa xác định");
                    req.setCurrentPrice(rs.getBigDecimal("curr_price") != null ? rs.getBigDecimal("curr_price") : java.math.BigDecimal.ZERO);
                    req.setTargetRoomTypeName(rs.getString("targ_name") != null ? rs.getString("targ_name") : "");
                    req.setTargetPrice(rs.getBigDecimal("targ_price") != null ? rs.getBigDecimal("targ_price") : java.math.BigDecimal.ZERO);

                    return req;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Author: ThuDNM-HE204370
    // 7. Duyệt phê duyệt yêu cầu 
    public boolean approveRequest(dto.GuestRequestDTO dto, String notes, double penaltyFee) {
        String updateReq = "UPDATE GuestRequests SET [status] = N'Đã phê duyệt', response_notes = ?, processed_at = GETDATE() WHERE request_id = ?";

        try {
            connection.setAutoCommit(false);

            // 1. Update trạng thái request thành Đã phê duyệt
            try (PreparedStatement ps = connection.prepareStatement(updateReq)) {
                ps.setString(1, notes);
                ps.setInt(2, dto.getRequestId());
                ps.executeUpdate();
            }

            // 2. Cập nhật bảng Booking và Invoice tương ứng theo từng loại nghiệp vụ
            if ("Đổi hạng phòng".equals(dto.getRequestType())) {
                String sqlUpdateBooking = "UPDATE Bookings SET room_type_id = ? WHERE booking_id = ?";
                try (PreparedStatement ps = connection.prepareStatement(sqlUpdateBooking)) {
                    ps.setInt(1, dto.getTargetRoomTypeId());
                    ps.setInt(2, dto.getBookingId());
                    ps.executeUpdate();
                }
            } else if ("Gia hạn phòng".equals(dto.getRequestType())) {
                String sqlUpdateBooking = "UPDATE Bookings SET checkout_date = ? WHERE booking_id = ?";
                try (PreparedStatement ps = connection.prepareStatement(sqlUpdateBooking)) {
                    LocalDate newCheckOutDate = dto.getRequestedCheckout() != null
                            ? dto.getRequestedCheckout().toLocalDate() : dto.getCheckOutDate();

                    ps.setDate(1, Date.valueOf(newCheckOutDate));
                    ps.setInt(2, dto.getBookingId());
                    ps.executeUpdate();
                }
            } else if ("Hủy đặt phòng".equals(dto.getRequestType())) {
                // 2.a. Cập nhật trạng thái Booking gốc sang Đã hủy
                String sqlUpdateBooking = "UPDATE Bookings SET [status] = N'Đã hủy', cancelled_at = GETDATE() WHERE booking_id = ?";
                try (PreparedStatement ps = connection.prepareStatement(sqlUpdateBooking)) {
                    ps.setInt(1, dto.getBookingId());
                    ps.executeUpdate();
                }

                // 2.b. Cập nhật khớp cấu trúc bảng Invoices trong DB của bạn (penaltyFee tính từ Controller)
                String sqlUpdateInvoice = "UPDATE Invoices "
                        + "SET room_charges = 0, "
                        + "    consumable_charges = 0, "
                        + "    amenity_damages = 0, "
                        + "    total_amount = ?, "
                        + "    remaining_amount = 0, "
                        + "    payment_status = N'Đã thanh toán' "
                        + "WHERE booking_id = ?";
                try (PreparedStatement ps = connection.prepareStatement(sqlUpdateInvoice)) {
                    ps.setDouble(1, penaltyFee);
                    ps.setInt(2, dto.getBookingId());
                    ps.executeUpdate();
                }
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // 8. Lấy danh sách yêu cầu lọc theo Bộ lọc 
    public List<dto.GuestRequestDTO> getRequestsByFilters(String type, String status, String searchBookingCode) {
        List<dto.GuestRequestDTO> list = new ArrayList<>();

        String sql = "SELECT gr.*, b.booking_code, g.full_name, rt.type_name "
                + "FROM GuestRequests gr "
                + "JOIN Bookings b ON gr.booking_id = b.booking_id "
                + "JOIN Guests g ON gr.guest_id = g.guest_id "
                + "LEFT JOIN RoomTypes rt ON gr.target_room_type_id = rt.room_type_id "
                + ("Tất cả".equals(type) ? " WHERE 1=1 " : " WHERE gr.request_type = ? ")
                + ("Tất cả".equals(status) ? "" : " AND gr.[status] = ? ")
                + ((searchBookingCode != null && !searchBookingCode.trim().isEmpty()) ? " AND b.booking_code LIKE ? " : "")
                + "ORDER BY gr.submitted_at DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int idx = 1;
            if (!"Tất cả".equals(type)) {
                ps.setNString(idx++, type);
            }
            if (!"Tất cả".equals(status)) {
                ps.setNString(idx++, status);
            }
            if (searchBookingCode != null && !searchBookingCode.trim().isEmpty()) {
                ps.setString(idx++, "%" + searchBookingCode.trim() + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dto.GuestRequestDTO req = new dto.GuestRequestDTO();

                    req.setRequestId(rs.getInt("request_id"));
                    req.setBookingId(rs.getInt("booking_id"));
                    req.setGuestId(rs.getInt("guest_id"));
                    req.setRequestType(rs.getString("request_type"));
                    req.setRequestDetails(rs.getString("request_details"));
                    req.setStatus(rs.getString("status"));

                    if (rs.getTimestamp("submitted_at") != null) {
                        java.time.LocalDateTime ldt = rs.getTimestamp("submitted_at").toLocalDateTime();
                        req.setSubmittedAt(ldt);
                        req.setFormattedTime(ldt.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
                        req.setFormattedDate(ldt.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    } else {
                        req.setFormattedTime("N/A");
                        req.setFormattedDate("");
                    }

                    req.setBookingCode(rs.getString("booking_code"));
                    req.setGuestName(rs.getString("full_name"));
                    req.setTargetRoomTypeName(rs.getString("type_name"));

                    int targetRoomTypeId = rs.getInt("target_room_type_id");
                    if (!rs.wasNull()) {
                        req.setTargetRoomTypeId(targetRoomTypeId);
                    }

                    list.add(req);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
