package dao;

import dal.DBContext;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.DepositPayment;

/**
 * DepositPaymentDAO.java - Data Access Object for DepositPayments Handles CRUD
 * and verification operations for deposit payments
 *
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
        strSQL.append("order by dp.submitted_at desc");

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
        if (payment == null) {
            throw new Exception("Khoản thanh toán không tồn tại.");
        }

        if (!"Chờ xử lý".equals(payment.getVerificationStatus())) {
            throw new Exception("Khoản thanh toán này đã được xử lý trước đó.");
        }

        connection.setAutoCommit(false);
        try {
            // Update DepositPayments
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

            // Update Bookings
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

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new Exception("Lỗi hệ thống: Không thể xác nhận thanh toán.");
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
