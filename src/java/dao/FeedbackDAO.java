package dao;

import dal.DBContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Feedback;

public class FeedbackDAO extends DBContext {

    public List<Feedback> getFeedbacksByPage(int page, int pageSize) {
        List<Feedback> list = new ArrayList<>();

        String sql = """
            SELECT 
                f.feedback_id,
                f.booking_id,
                f.guest_id,
                f.rating,
                f.comment,
                f.submitted_at,
                g.full_name
            FROM Feedback f
            LEFT JOIN Guests g ON f.guest_id = g.guest_id
            ORDER BY f.submitted_at DESC
            OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            int offset = (page - 1) * pageSize;

            ps.setInt(1, offset);
            ps.setInt(2, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Feedback f = new Feedback();

                    f.setFeedbackId(rs.getInt("feedback_id"));
                    f.setBookingId(rs.getInt("booking_id"));

                    int guestId = rs.getInt("guest_id");
                    f.setGuestId(rs.wasNull() ? null : guestId);

                    f.setRating(rs.getInt("rating"));
                    f.setComment(rs.getString("comment"));

                    Timestamp submitted = rs.getTimestamp("submitted_at");
                    if (submitted != null) {
                        f.setSubmittedAt(submitted.toLocalDateTime());
                    }

                    f.setGuestName(rs.getString("full_name"));

                    list.add(f);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public double getAverageRating() {
        String sql = "SELECT AVG(CAST(rating AS FLOAT)) FROM Feedback";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getTotalFeedbacks() {
        String sql = "SELECT COUNT(*) FROM Feedback";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int countByRating(int rating) {
        String sql = "SELECT COUNT(*) FROM Feedback WHERE rating = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, rating);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public Map<String, Object> getBookingFeedbackInfo(int bookingId) {

        Map<String, Object> map = new HashMap<>();

        String sql = """
        SELECT
            b.booking_id,
            b.booking_code,
            b.status,
            b.guest_id,
            g.full_name,
            CASE
                WHEN f.feedback_id IS NULL THEN 0
                ELSE 1
            END AS has_feedback
        FROM Bookings b
        LEFT JOIN Guests g
            ON b.guest_id = g.guest_id
        LEFT JOIN Feedback f
            ON b.booking_id = f.booking_id
        WHERE b.booking_id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, bookingId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    map.put("bookingId", rs.getInt("booking_id"));
                    map.put("bookingCode", rs.getString("booking_code"));
                    map.put("status", rs.getString("status"));

                    int guestId = rs.getInt("guest_id");
                    map.put("guestId", rs.wasNull() ? null : guestId);

                    String guestName = rs.getString("full_name");
                    map.put("guestName",
                            guestName == null ? "Khách hàng" : guestName);

                    map.put("hasFeedback", rs.getBoolean("has_feedback"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return map;
    }

    public boolean insertFeedback(int bookingId, Integer guestId, int rating, String comment) {
        String sql = """
        INSERT INTO Feedback (booking_id, guest_id, rating, comment, submitted_at)
        VALUES (?, ?, ?, ?, GETDATE())
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);

            if (guestId == null) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(2, guestId);
            }

            ps.setInt(3, rating);
            ps.setString(4, comment);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Map<String, Object> getBookingFeedbackInfoByCodeAndEmail(String bookingCode, String email) {
        Map<String, Object> map = new HashMap<>();

        String sql = """
        SELECT
            b.booking_id,
            b.booking_code,
            b.status,
            b.guest_id,
            g.full_name,
            g.email,
            CASE 
                WHEN f.feedback_id IS NULL THEN 0
                ELSE 1
            END AS has_feedback
        FROM Bookings b
        JOIN Guests g ON b.guest_id = g.guest_id
        LEFT JOIN Feedback f ON b.booking_id = f.booking_id
        WHERE b.booking_code = ?
          AND g.email = ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, bookingCode);
            ps.setString(2, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    map.put("bookingId", rs.getInt("booking_id"));
                    map.put("bookingCode", rs.getString("booking_code"));
                    map.put("status", rs.getString("status"));

                    int guestId = rs.getInt("guest_id");
                    map.put("guestId", rs.wasNull() ? null : guestId);

                    map.put("guestName", rs.getString("full_name"));
                    map.put("email", rs.getString("email"));
                    map.put("hasFeedback", rs.getBoolean("has_feedback"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return map;
    }

    public boolean hasFeedback(int bookingId) {
        String sql = """
        SELECT COUNT(*) 
        FROM Feedback 
        WHERE booking_id = ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
