package dao;

import dal.DBContext;
import dto.AvailableRoomTypeView;
import model.Booking;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WalkinBookingDAO extends DBContext {

    // Lấy toàn bộ danh sách hạng phòng đang hoạt động
    public List<AvailableRoomTypeView> getAllRoomTypes() {
        List<AvailableRoomTypeView> list = new ArrayList<>();
        String sql = "SELECT room_type_id, type_name, num_guests, num_children, base_price "
                + "FROM RoomTypes WHERE is_active = 1 ORDER BY base_price ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                AvailableRoomTypeView view = new AvailableRoomTypeView(
                        rs.getInt("room_type_id"), rs.getString("type_name"),
                        rs.getInt("num_guests"), rs.getInt("num_children"),
                        rs.getBigDecimal("base_price"), 0
                );
                list.add(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Tìm kiếm phòng trống theo ngày, số lượng phòng cần thuê và hạng phòng cụ thể
    public List<AvailableRoomTypeView> searchAvailableRooms(String checkIn, String checkOut, int roomTypeId, int numRooms, int numGuests, int numChildren) {
        List<AvailableRoomTypeView> list = new ArrayList<>();

        String sql = "WITH BookedRooms AS ("
                + "    SELECT b.room_type_id, SUM(b.num_rooms) AS total_booked_rooms "
                + "    FROM Bookings b "
                + "    WHERE b.status IN (N'Đã xác nhận', N'Đã nhận phòng', N'Chờ xử lý') "
                + "      AND b.checkin_date < ? AND b.checkout_date > ? "
                + "    GROUP BY b.room_type_id "
                + "), "
                + "TotalActiveRooms AS ("
                + "    SELECT room_type_id, COUNT(room_id) AS total_rooms "
                + "    FROM Rooms "
                + "    WHERE is_active = 1 "
                + "    GROUP BY room_type_id "
                + "), "
                + "FinalAvailable AS ("
                + "    SELECT rt.room_type_id, rt.type_name, rt.num_guests AS max_adults, rt.num_children AS max_children, rt.base_price, "
                + "           (ISNULL(tar.total_rooms, 0) - ISNULL(br.total_booked_rooms, 0)) AS available_rooms "
                + "    FROM RoomTypes rt "
                + "    LEFT JOIN TotalActiveRooms tar ON rt.room_type_id = tar.room_type_id "
                + "    LEFT JOIN BookedRooms br ON rt.room_type_id = br.room_type_id "
                + "    WHERE rt.is_active = 1 "
                + ") "
                + "SELECT * FROM FinalAvailable "
                + "WHERE available_rooms >= ? "
                + "  AND (max_adults * ?) >= ? "
                + "  AND (max_children * ?) >= ? ";

        if (roomTypeId > 0) {
            sql += "  AND room_type_id = ? ";
        }
        sql += "ORDER BY base_price ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, checkOut);
            ps.setString(2, checkIn);
            ps.setInt(3, numRooms);

            ps.setInt(4, numRooms); 
            ps.setInt(5, numGuests); 
            ps.setInt(6, numRooms); 
            ps.setInt(7, numChildren); 

            if (roomTypeId > 0) {
                ps.setInt(8, roomTypeId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AvailableRoomTypeView view = new AvailableRoomTypeView(
                            rs.getInt("room_type_id"), rs.getString("type_name"),
                            rs.getInt("max_adults"), rs.getInt("max_children"),
                            rs.getBigDecimal("base_price"), rs.getInt("available_rooms")
                    );
                    list.add(view);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Luôn luôn tạo mới hồ sơ khách hàng cho mỗi lượt đặt quầy
    public int createGuest(String fullName, String email, String phone, String idNumber, LocalDate dateOfBirth) {
        String sql = "INSERT INTO Guests (full_name, email, phone, id_number, date_of_birth) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
            try (ResultSet rs = stm.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.out.println("createGuest: " + e.getMessage());
        }
        return 0;
    }

    // Tự động quét và hủy đơn đặt quầy tương lai quá 15 phút chưa đóng cọc giữ phòng
    public int cancelExpiredBookings() {
        String sql = """
                     UPDATE b
                     SET b.[status] = N'Đã hủy',
                         b.cancelled_at = GETDATE(),
                         b.cancellation_reason = N'Quá thời hạn đóng tiền đặt cọc giữ phòng 15 phút tại quầy'
                     FROM Bookings b
                     WHERE b.[status] = N'Chờ xử lý'
                       AND b.payment_status = N'Chưa thanh toán'
                       AND b.[source] = N'Đặt phòng tại quầy'
                       AND DATEADD(MINUTE, 15, b.created_at) <= GETDATE()
                       AND NOT EXISTS (
                           SELECT 1 FROM DepositPayments dp WHERE dp.booking_id = b.booking_id
                       )
                     """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            return stm.executeUpdate();
        } catch (Exception e) {
            System.out.println("cancelExpiredBookings: " + e.getMessage());
        }
        return 0;
    }

    // Kiểm tra mã booking đã tồn tại trong hệ thống chưa
    public boolean isBookingCodeExist(String bookingCode) {
        String sql = "SELECT COUNT(*) AS total FROM Bookings WHERE booking_code = ?";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, bookingCode);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
        } catch (Exception e) {
            System.out.println("isBookingCodeExist: " + e.getMessage());
        }
        return false;
    }

    // Xử lý tạo đơn đặt phòng tại quầy phân tách luồng ở luôn hoặc tính cọc 30% cho đơn tương lai
    public boolean createWalkinBookingProcess(Booking booking, String fullName, String email, String phone,
            String idNumber, LocalDate dateOfBirth, boolean isStayNow, java.math.BigDecimal roomCharges) {

        cancelExpiredBookings();
        boolean oldAutoCommit = true;

        try {
            oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            // 1. Luôn tạo mới hồ sơ khách hàng
            int guestId = createGuest(fullName, email, phone, idNumber, dateOfBirth);
            if (guestId == 0) {
                connection.rollback();
                return false;
            }
            booking.setGuestId(guestId);

            // 2. Thiết lập trạng thái mặc định cho đơn đặt tại quầy là ĐÃ XÁC NHẬN
            booking.setStatus("Đã xác nhận");

            // 6. Thực thi lưu dữ liệu đơn đặt phòng vào bảng Bookings
            String insertBookingSql = """
                INSERT INTO Bookings
                (booking_code, guest_id, staff_id, room_type_id, booked_price_per_night, num_rooms, num_guests, num_children, checkin_date, checkout_date, [source], [status], deposit_amount, payment_status, confirmed_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, N'Đặt phòng tại quầy', ?, ?, ?, GETDATE())
                """;

            int bookingId = -1;
            try (PreparedStatement psBooking = connection.prepareStatement(insertBookingSql, Statement.RETURN_GENERATED_KEYS)) {
                psBooking.setString(1, booking.getBookingCode());
                psBooking.setInt(2, booking.getGuestId());

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
                psBooking.setNString(11, booking.getStatus());
                psBooking.setBigDecimal(12, booking.getDepositAmount());
                psBooking.setNString(13, booking.getPaymentStatus());

                int bookingRows = psBooking.executeUpdate();
                if (bookingRows == 0) {
                    connection.rollback();
                    return false;
                }

                try (ResultSet gk = psBooking.getGeneratedKeys()) {
                    if (gk.next()) {
                        bookingId = gk.getInt(1);
                    }
                }
            }

            // BỔ SUNG BƯỚC 6.1: TỰ ĐỘNG TẠO HÓA ĐƠN NHÁP (INVOICES) CHO CẢ 2 LUỒNG
            if (bookingId != -1) {
                String insertInvoiceSql = """
                    INSERT INTO Invoices 
                    (booking_id, room_charges, consumable_charges, amenity_damages, total_amount, remaining_amount, payment_status, created_by)
                    VALUES (?, ?, 0, 0, ?, ?, N'Chưa thanh toán', ?)
                    """;
                try (PreparedStatement psInvoice = connection.prepareStatement(insertInvoiceSql)) {
                    psInvoice.setInt(1, bookingId);
                    psInvoice.setBigDecimal(2, roomCharges);
                    psInvoice.setBigDecimal(3, roomCharges); // total_amount ban đầu bằng tiền phòng
                    
                    // remaining_amount = tổng tiền phòng trừ đi tiền cọc giữ phòng (nếu có)
                    java.math.BigDecimal remainingAmount = roomCharges.subtract(booking.getDepositAmount());
                    psInvoice.setBigDecimal(4, remainingAmount);
                    
                    if (booking.getStaffId() == null) {
                        psInvoice.setNull(5, Types.INTEGER);
                    } else {
                        psInvoice.setInt(5, booking.getStaffId());
                    }
                    psInvoice.executeUpdate();
                }
            }

            // 7. Ghi nhận minh chứng tài chính vào DepositPayments nếu luồng Đặt tương lai đóng cọc thành công tại quầy
            if (!isStayNow && bookingId != -1 && booking.getDepositAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
                String insertDepositSQL = """
                    INSERT INTO DepositPayments (booking_id, amount, payment_proof_url, submitted_at, verification_status, verified_at, notes, verified_by)
                    VALUES (?, ?, 'Xac nhan truc tiep tai quay', GETDATE(), N'Đã phê duyệt', GETDATE(), N'Đã đóng cọc giữ phòng trực tiếp tại quầy', ?)
                    """;
                try (PreparedStatement psDeposit = connection.prepareStatement(insertDepositSQL)) {
                    psDeposit.setInt(1, bookingId);
                    psDeposit.setBigDecimal(2, booking.getDepositAmount());
                    psDeposit.setInt(3, booking.getStaffId());
                    psDeposit.executeUpdate();
                }
            }

            connection.commit();
            return true;

        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            System.out.println("createWalkinBookingProcess: " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(oldAutoCommit);
            } catch (SQLException e) {
                System.out.println("createWalkinBookingProcess autoCommit reset: " + e.getMessage());
            }
        }
        return false;
    }
}