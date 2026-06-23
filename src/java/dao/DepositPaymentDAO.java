package dao;

import dal.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.DepositPayment;

/**
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-06-15
 */
public class DepositPaymentDAO extends DBContext {

    public List<DepositPayment> getPendingPayments() throws Exception {
        List<DepositPayment> list = new ArrayList<>();
        String strSQL = """
                        select dp.*, g.full_name, b.booking_code
                        from DepositPayments dp
                        join Bookings b on dp.booking_id = b.booking_id
                        join Guests g on b.guest_id = g.guest_id
                        where dp.verification_status = N'Chờ xử lý'
                        order by dp.submitted_at desc
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL); ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                DepositPayment dp = mapResultSetToDepositPayment(rs);
                list.add(dp);
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy danh sách thanh toán chờ xử lý.");
        }
        return list;
    }

    public List<DepositPayment> getAllPaymentsByStatus(String status) throws Exception {
        List<DepositPayment> list = new ArrayList<>();
        StringBuilder strSQL = new StringBuilder("""
                                                 select dp.*, g.full_name, b.booking_code
                                                 from DepositPayments dp
                                                 join Bookings b on dp.booking_id = b.booking_id
                                                 join Guests g on b.guest_id = g.guest_id
                                                 
                                                 """);

        if (status != null && !status.equals("all")) {
            strSQL.append("where dp.verification_status = ? ");
        }
        strSQL.append("""
                      order by 
                      case dp.verification_status
                      when N'Chờ xử lý' then 1
                      else 2
                       end asc, 
                        dp.submitted_at desc
                      """);

        try (PreparedStatement stm = connection.prepareStatement(strSQL.toString())) {
            if (status != null && !status.equals("all")) {
                stm.setString(1, status);
            }

            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    DepositPayment dp = mapResultSetToDepositPayment(rs);
                    list.add(dp);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy danh sách thanh toán.");
        }
        return list;
    }

    public List<DepositPayment> searchPayments(String keyword) throws Exception {
        List<DepositPayment> list = new ArrayList<>();
        String strSQL = """
                        select dp.*, g.full_name, b.booking_code
                        from DepositPayments dp
                        join Bookings b on dp.booking_id = b.booking_id
                        join Guests g on b.guest_id = g.guest_id
                        where b.booking_code like ? or g.full_name like ?
                        order by dp.submitted_at desc
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            String searchPattern = "%" + (keyword != null ? keyword.trim() : "") + "%";
            stm.setString(1, searchPattern);
            stm.setString(2, searchPattern);

            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    DepositPayment dp = mapResultSetToDepositPayment(rs);
                    list.add(dp);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể tìm kiếm thanh toán.");
        }
        return list;
    }

    public DepositPayment getPaymentById(int depositId) throws Exception {
        String strSQL = """
                        select dp.*, g.full_name, b.booking_code
                        from DepositPayments dp
                        join Bookings b on dp.booking_id = b.booking_id
                        join Guests g on b.guest_id = g.guest_id
                        where dp.deposit_id = ?
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setInt(1, depositId);

            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDepositPayment(rs);
                } else {
                    throw new Exception("Không tìm thấy khoản thanh toán.");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy thông tin thanh toán.");
        }
    }

    public void verifyPayment(int depositId, int staffId, String notes) throws Exception {
    DepositPayment payment = getPaymentById(depositId);
    if (payment == null) throw new Exception("Khoản thanh toán không tồn tại.");
    if (!"Chờ xử lý".equals(payment.getVerificationStatus())) throw new Exception("Khoản thanh toán này đã được xử lý trước đó.");

    connection.setAutoCommit(false);
    try {
        String updatePaymentSQL = """
                update DepositPayments
                set verification_status = N'Đã phê duyệt',
                verified_at = GETDATE(),
                notes = ?,
                verified_by = ?
                where deposit_id = ?
                """;
        try (PreparedStatement stm = connection.prepareStatement(updatePaymentSQL)) {
            stm.setString(1, notes);
            stm.setInt(2, staffId);
            stm.setInt(3, depositId);
            stm.executeUpdate();
        }

        String updateBookingSQL = """
                update Bookings
                set status = N'Đã xác nhận',
                payment_status = N'Đã đặt cọc',
                confirmed_at = GETDATE()
                where booking_id = ?
                """;
        try (PreparedStatement stm = connection.prepareStatement(updateBookingSQL)) {
            stm.setInt(1, payment.getBookingId());
            stm.executeUpdate();
        }

        // Lấy thông tin booking để tính tiền phòng
        String bookingSQL = """
                select booked_price_per_night, num_rooms, checkin_date, checkout_date, deposit_amount
                from Bookings where booking_id = ?
                """;
        java.math.BigDecimal pricePerNight;
        int numRooms;
        java.time.LocalDate checkinDate;
        java.time.LocalDate checkoutDate;
        java.math.BigDecimal depositAmount;

        try (PreparedStatement stm = connection.prepareStatement(bookingSQL)) {
            stm.setInt(1, payment.getBookingId());
            try (ResultSet rs = stm.executeQuery()) {
                if (!rs.next()) throw new Exception("Không tìm thấy booking.");
                pricePerNight = rs.getBigDecimal("booked_price_per_night");
                numRooms      = rs.getInt("num_rooms");
                checkinDate   = rs.getDate("checkin_date").toLocalDate();
                checkoutDate  = rs.getDate("checkout_date").toLocalDate();
                depositAmount = rs.getBigDecimal("deposit_amount");
            }
        }

        long nights = java.time.temporal.ChronoUnit.DAYS.between(checkinDate, checkoutDate);
        java.math.BigDecimal roomCharges = pricePerNight
                .multiply(java.math.BigDecimal.valueOf(nights))
                .multiply(java.math.BigDecimal.valueOf(numRooms));
        java.math.BigDecimal deposit = depositAmount != null ? depositAmount : java.math.BigDecimal.ZERO;
        java.math.BigDecimal remaining = roomCharges.subtract(deposit).max(java.math.BigDecimal.ZERO);

        String invoiceSQL = """
                insert into Invoices (booking_id, room_charges, consumable_charges,
                    amenity_damages, deposit_deducted, total_amount, remaining_amount,
                    payment_status, payment_method, created_by)
                values (?, ?, 0, 0, ?, ?, ?, N'Chưa thanh toán', N'Chuyển khoản', ?)
                """;
        try (PreparedStatement stm = connection.prepareStatement(invoiceSQL)) {
            stm.setInt(1, payment.getBookingId());
            stm.setBigDecimal(2, roomCharges);
            stm.setBigDecimal(3, deposit);
            stm.setBigDecimal(4, roomCharges);
            stm.setBigDecimal(5, remaining);
            stm.setInt(6, staffId);
            stm.executeUpdate();
        }

        connection.commit();
    } catch (SQLException e) {
        connection.rollback();
        throw new Exception("Lỗi hệ thống: Không thể xác nhận thanh toán. " + e.getMessage());
    } finally {
        connection.setAutoCommit(true);
    }
}
    public void rejectPayment(int depositId, int staffId, String notes) throws Exception {
        DepositPayment payment = getPaymentById(depositId);
        if (payment == null) {
            throw new Exception("Khoản thanh toán không tồn tại.");
        }

        if (!"Chờ xử lý".equals(payment.getVerificationStatus())) {
            throw new Exception("Khoản thanh toán này đã được xử lý trước đó.");
        }

        String defaultNotes = "Thanh toán không thành công";
        String finalNotes = (notes == null || notes.trim().isEmpty()) ? defaultNotes : notes.trim();

        connection.setAutoCommit(false);
        try {
            // Update DepositPayments
            String updatePaymentSQL = """
                                      update DepositPayments
                                      set verification_status = N'Đã từ chối',
                                      verified_at = GETDATE(),
                                      notes = ?,
                                      verified_by = ?
                                      where deposit_id = ?
                                      """;

            try (PreparedStatement stm = connection.prepareStatement(updatePaymentSQL)) {
                stm.setString(1, finalNotes);
                stm.setInt(2, staffId);
                stm.setInt(3, depositId);
                stm.executeUpdate();
            }

            // Update Bookings
            String updateBookingSQL = """
                                      update Bookings
                                      set status = N'Đã hủy',
                                      cancelled_at = GETDATE(),
                                      cancellation_reason = ?
                                      where booking_id = ?
                                      """;

            try (PreparedStatement stm = connection.prepareStatement(updateBookingSQL)) {
                stm.setString(1, finalNotes);
                stm.setInt(2, payment.getBookingId());
                stm.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new Exception("Lỗi hệ thống: Không thể từ chối thanh toán.");
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public Map<Integer, String> getVerifiedByNames(List<DepositPayment> payments) throws Exception {
        Map<Integer, String> map = new HashMap<>();
        if (payments == null || payments.isEmpty()) {
            return map;
        }

        List<Integer> depositIds = new ArrayList<>();
        for (DepositPayment dp : payments) {
            depositIds.add(dp.getDepositId());
        }

        String placeholders = String.join(",", Collections.nCopies(depositIds.size(), "?"));
        String sql = "select dp.deposit_id, sa.full_name "
                + "from DepositPayments dp "
                + "left join StaffAccounts sa on dp.verified_by = sa.staff_id "
                + "where dp.deposit_id in (" + placeholders + ")";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            for (int i = 0; i < depositIds.size(); i++) {
                stm.setInt(i + 1, depositIds.get(i));
            }
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("full_name");
                    map.put(rs.getInt("deposit_id"), name != null ? name : "-");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy tên người duyệt.");
        }
        return map;
    }

    private DepositPayment mapResultSetToDepositPayment(ResultSet rs) throws SQLException {
        DepositPayment dp = new DepositPayment();
        dp.setDepositId(rs.getInt("deposit_id"));
        dp.setBookingId(rs.getInt("booking_id"));
        dp.setAmount(rs.getBigDecimal("amount"));
        dp.setPaymentProofUrl(rs.getString("payment_proof_url"));

        Timestamp submittedAt = rs.getTimestamp("submitted_at");
        dp.setSubmittedAt(submittedAt != null ? submittedAt.toLocalDateTime() : null);

        dp.setVerificationStatus(rs.getString("verification_status"));

        Timestamp verifiedAt = rs.getTimestamp("verified_at");
        dp.setVerifiedAt(verifiedAt != null ? verifiedAt.toLocalDateTime() : null);

        dp.setNotes(rs.getString("notes"));
        dp.setVerifiedBy(rs.getInt("verified_by"));

        return dp;
    }
}
