package dao;

import dal.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Feedback;

public class FeedbackDAO extends DBContext {

    public List<Feedback> getFeedbacksByPage(int page, int pageSize) {
        List<Feedback> list = new ArrayList<>();

        if (page < 1) {
            page = 1;
        }

        if (pageSize <= 0) {
            pageSize = 10;
        }

        String sql = """
            SELECT f.feedback_id, f.booking_id, f.guest_id, f.rating, f.comment, f.submitted_at,
                   f.is_visible, f.hidden_at, f.hidden_reason, g.full_name, g.email, b.booking_code
            FROM Feedback f
            INNER JOIN Bookings b ON f.booking_id = b.booking_id
            LEFT JOIN Guests g ON f.guest_id = g.guest_id
            WHERE f.is_visible = 1
            ORDER BY f.submitted_at DESC
            OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int offset = (page - 1) * pageSize;

            ps.setInt(1, offset);
            ps.setInt(2, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapFeedback(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("getFeedbacksByPage: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    public double getAverageRating() {
        String sql = """
            SELECT ISNULL(AVG(CAST(rating AS FLOAT)), 0) AS average_rating
            FROM Feedback
            WHERE is_visible = 1
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("average_rating");
            }

        } catch (SQLException e) {
            System.out.println("getAverageRating: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public int getTotalFeedbacks() {
        String sql = """
            SELECT COUNT(*) AS total_feedbacks
            FROM Feedback
            WHERE is_visible = 1
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total_feedbacks");
            }

        } catch (SQLException e) {
            System.out.println("getTotalFeedbacks: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public int countByRating(int rating) {
        String sql = """
            SELECT COUNT(*) AS total_feedbacks
            FROM Feedback
            WHERE rating = ? AND is_visible = 1
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, rating);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_feedbacks");
                }
            }

        } catch (SQLException e) {
            System.out.println("countByRating: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public Map<String, Object> getBookingFeedbackInfo(int bookingId) {
        Map<String, Object> map = new HashMap<>();

        String sql = """
            SELECT b.booking_id, b.booking_code, b.[status], b.guest_id,
                   g.full_name, g.email,
                   CASE WHEN f.feedback_id IS NULL THEN 0 ELSE 1 END AS has_feedback
            FROM Bookings b
            LEFT JOIN Guests g ON b.guest_id = g.guest_id
            LEFT JOIN Feedback f ON b.booking_id = f.booking_id
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
                    map.put("guestName", guestName == null ? "Khách hàng" : guestName);

                    map.put("email", rs.getString("email"));
                    map.put("hasFeedback", rs.getBoolean("has_feedback"));
                }
            }

        } catch (SQLException e) {
            System.out.println("getBookingFeedbackInfo: " + e.getMessage());
            e.printStackTrace();
        }

        return map;
    }

    public Map<String, Object> getBookingFeedbackInfoByCodeAndEmail(
            String bookingCode,
            String email) {

        Map<String, Object> map = new HashMap<>();

        String sql = """
            SELECT b.booking_id, b.booking_code, b.[status], b.guest_id,
                   g.full_name, g.email,
                   CASE WHEN f.feedback_id IS NULL THEN 0 ELSE 1 END AS has_feedback
            FROM Bookings b
            INNER JOIN Guests g ON b.guest_id = g.guest_id
            LEFT JOIN Feedback f ON b.booking_id = f.booking_id
            WHERE b.booking_code = ? AND LOWER(g.email) = LOWER(?)
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

                    String guestName = rs.getString("full_name");
                    map.put("guestName", guestName == null ? "Khách hàng" : guestName);

                    map.put("email", rs.getString("email"));
                    map.put("hasFeedback", rs.getBoolean("has_feedback"));
                }
            }

        } catch (SQLException e) {
            System.out.println("getBookingFeedbackInfoByCodeAndEmail: " + e.getMessage());
            e.printStackTrace();
        }

        return map;
    }

    public boolean insertFeedback(
            int bookingId,
            Integer guestId,
            int rating,
            String comment) {

        String sql = """
            INSERT INTO Feedback
            (booking_id, guest_id, rating, comment, submitted_at, is_visible)
            VALUES (?, ?, ?, ?, GETDATE(), 1)
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);

            if (guestId == null) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(2, guestId);
            }

            ps.setInt(3, rating);

            if (comment == null || comment.trim().isEmpty()) {
                ps.setNull(4, Types.NVARCHAR);
            } else {
                ps.setNString(4, comment.trim());
            }

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("insertFeedback: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean hasFeedback(int bookingId) {
        String sql = """
            SELECT COUNT(*) AS total_feedbacks
            FROM Feedback
            WHERE booking_id = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_feedbacks") > 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("hasFeedback: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public List<Feedback> getManagerFeedbacks(
            String keyword,
            Integer rating,
            Boolean visible,
            String sort,
            int page,
            int pageSize) {

        List<Feedback> list = new ArrayList<>();

        if (page < 1) {
            page = 1;
        }

        if (pageSize <= 0) {
            pageSize = 10;
        }

        StringBuilder sql = new StringBuilder();

        sql.append("""
            SELECT f.feedback_id, f.booking_id, f.guest_id, f.rating, f.comment, f.submitted_at,
                   f.is_visible, f.hidden_at, f.hidden_reason, g.full_name, g.email, b.booking_code
            FROM Feedback f
            INNER JOIN Bookings b ON f.booking_id = b.booking_id
            LEFT JOIN Guests g ON f.guest_id = g.guest_id
            WHERE 1 = 1
            """);

        List<Object> parameters = new ArrayList<>();

        appendManagerFilters(sql, parameters, keyword, rating, visible);
        appendManagerSort(sql, sort);

        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ");

        parameters.add((page - 1) * pageSize);
        parameters.add(pageSize);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            setParameters(ps, parameters);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapFeedback(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("getManagerFeedbacks: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    public int countManagerFeedbacks(
            String keyword,
            Integer rating,
            Boolean visible) {

        StringBuilder sql = new StringBuilder();

        sql.append("""
            SELECT COUNT(*) AS total_feedbacks
            FROM Feedback f
            INNER JOIN Bookings b ON f.booking_id = b.booking_id
            LEFT JOIN Guests g ON f.guest_id = g.guest_id
            WHERE 1 = 1
            """);

        List<Object> parameters = new ArrayList<>();

        appendManagerFilters(sql, parameters, keyword, rating, visible);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            setParameters(ps, parameters);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_feedbacks");
                }
            }

        } catch (SQLException e) {
            System.out.println("countManagerFeedbacks: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public Map<String, Object> getManagerFeedbackStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        String sql = """
            SELECT COUNT(*) AS total_feedbacks,
                   ISNULL(AVG(CAST(rating AS FLOAT)), 0) AS average_rating,
                   SUM(CASE WHEN is_visible = 1 THEN 1 ELSE 0 END) AS visible_feedbacks,
                   SUM(CASE WHEN is_visible = 0 THEN 1 ELSE 0 END) AS hidden_feedbacks,
                   SUM(CASE WHEN rating = 5 THEN 1 ELSE 0 END) AS rating5,
                   SUM(CASE WHEN rating = 4 THEN 1 ELSE 0 END) AS rating4,
                   SUM(CASE WHEN rating = 3 THEN 1 ELSE 0 END) AS rating3,
                   SUM(CASE WHEN rating = 2 THEN 1 ELSE 0 END) AS rating2,
                   SUM(CASE WHEN rating = 1 THEN 1 ELSE 0 END) AS rating1
            FROM Feedback
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                statistics.put("totalFeedbacks", rs.getInt("total_feedbacks"));
                statistics.put("averageRating", rs.getDouble("average_rating"));
                statistics.put("visibleFeedbacks", rs.getInt("visible_feedbacks"));
                statistics.put("hiddenFeedbacks", rs.getInt("hidden_feedbacks"));
                statistics.put("rating5", rs.getInt("rating5"));
                statistics.put("rating4", rs.getInt("rating4"));
                statistics.put("rating3", rs.getInt("rating3"));
                statistics.put("rating2", rs.getInt("rating2"));
                statistics.put("rating1", rs.getInt("rating1"));
            }

        } catch (SQLException e) {
            System.out.println("getManagerFeedbackStatistics: " + e.getMessage());
            e.printStackTrace();
        }

        return statistics;
    }

    public Feedback getFeedbackById(int feedbackId) {
        String sql = """
            SELECT f.feedback_id, f.booking_id, f.guest_id, f.rating, f.comment, f.submitted_at,
                   f.is_visible, f.hidden_at, f.hidden_reason, g.full_name, g.email, b.booking_code
            FROM Feedback f
            INNER JOIN Bookings b ON f.booking_id = b.booking_id
            LEFT JOIN Guests g ON f.guest_id = g.guest_id
            WHERE f.feedback_id = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, feedbackId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapFeedback(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("getFeedbackById: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public boolean hideFeedback(int feedbackId, String hiddenReason) {
        String sql = """
            UPDATE Feedback
            SET is_visible = 0, hidden_at = GETDATE(), hidden_reason = ?
            WHERE feedback_id = ? AND is_visible = 1
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (hiddenReason == null || hiddenReason.trim().isEmpty()) {
                ps.setNull(1, Types.NVARCHAR);
            } else {
                ps.setNString(1, hiddenReason.trim());
            }

            ps.setInt(2, feedbackId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("hideFeedback: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean showFeedback(int feedbackId) {
        String sql = """
            UPDATE Feedback
            SET is_visible = 1, hidden_at = NULL, hidden_reason = NULL
            WHERE feedback_id = ? AND is_visible = 0
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, feedbackId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("showFeedback: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    private void appendManagerFilters(
            StringBuilder sql,
            List<Object> parameters,
            String keyword,
            Integer rating,
            Boolean visible) {

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("""
                AND (
                    LOWER(ISNULL(g.full_name, N'')) LIKE ?
                    OR LOWER(ISNULL(g.email, '')) LIKE ?
                    OR LOWER(ISNULL(b.booking_code, '')) LIKE ?
                    OR LOWER(ISNULL(f.comment, N'')) LIKE ?
                )
                """);

            String searchValue = "%" + keyword.trim().toLowerCase() + "%";

            parameters.add(searchValue);
            parameters.add(searchValue);
            parameters.add(searchValue);
            parameters.add(searchValue);
        }

        if (rating != null && rating >= 1 && rating <= 5) {
            sql.append(" AND f.rating = ? ");
            parameters.add(rating);
        }

        if (visible != null) {
            sql.append(" AND f.is_visible = ? ");
            parameters.add(visible);
        }
    }

    private void appendManagerSort(StringBuilder sql, String sort) {
        if ("oldest".equals(sort)) {
            sql.append(" ORDER BY f.submitted_at ASC, f.feedback_id ASC ");
        } else if ("rating-high".equals(sort)) {
            sql.append(" ORDER BY f.rating DESC, f.submitted_at DESC ");
        } else if ("rating-low".equals(sort)) {
            sql.append(" ORDER BY f.rating ASC, f.submitted_at DESC ");
        } else {
            sql.append(" ORDER BY f.submitted_at DESC, f.feedback_id DESC ");
        }
    }

    private void setParameters(
            PreparedStatement ps,
            List<Object> parameters)
            throws SQLException {

        for (int i = 0; i < parameters.size(); i++) {
            Object value = parameters.get(i);
            int index = i + 1;

            if (value instanceof Integer) {
                ps.setInt(index, (Integer) value);
            } else if (value instanceof Boolean) {
                ps.setBoolean(index, (Boolean) value);
            } else {
                ps.setNString(index, String.valueOf(value));
            }
        }
    }

    private Feedback mapFeedback(ResultSet rs) throws SQLException {
        Feedback feedback = new Feedback();

        feedback.setFeedbackId(rs.getInt("feedback_id"));
        feedback.setBookingId(rs.getInt("booking_id"));

        int guestId = rs.getInt("guest_id");
        feedback.setGuestId(rs.wasNull() ? null : guestId);

        feedback.setRating(rs.getInt("rating"));
        feedback.setComment(rs.getString("comment"));

        Timestamp submittedAt = rs.getTimestamp("submitted_at");

        if (submittedAt != null) {
            feedback.setSubmittedAt(submittedAt.toLocalDateTime());
        }

        feedback.setVisible(rs.getBoolean("is_visible"));

        Timestamp hiddenAt = rs.getTimestamp("hidden_at");

        if (hiddenAt != null) {
            feedback.setHiddenAt(hiddenAt.toLocalDateTime());
        }

        feedback.setHiddenReason(rs.getString("hidden_reason"));

        String guestName = rs.getString("full_name");
        feedback.setGuestName(guestName == null ? "Khách hàng" : guestName);

        feedback.setGuestEmail(rs.getString("email"));
        feedback.setBookingCode(rs.getString("booking_code"));

        return feedback;
    }
}