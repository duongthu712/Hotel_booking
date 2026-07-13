package dao;

import dal.DBContext;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-07-08
 */
public class ManagerDashboardDAO extends DBContext {

    // getTodayRevenue — lấy doanh thu hôm nay từ InvoicePayments
    public BigDecimal getTodayRevenue() {
        String sql = """
        SELECT COALESCE(SUM(ip.amount), 0) AS revenue
        FROM InvoicePayments ip
        WHERE CAST(ip.paid_at AS DATE) = CAST(GETDATE() AS DATE)
        """;
        try (PreparedStatement stm = connection.prepareStatement(sql); ResultSet rs = stm.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal("revenue");
            }
        } catch (SQLException e) {
            System.out.println("getTodayRevenue: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }

// getMonthlyRevenue — lấy doanh thu tháng đã chọn
    public BigDecimal getMonthlyRevenue(int month, int year) {
        String sql = """
        SELECT COALESCE(SUM(ip.amount), 0) AS revenue
        FROM InvoicePayments ip
        WHERE MONTH(ip.paid_at) = ?
          AND YEAR(ip.paid_at) = ?
        """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, month);
            stm.setInt(2, year);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("revenue");
                }
            }
        } catch (SQLException e) {
            System.out.println("getMonthlyRevenue: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }

// getDailyRevenue — lấy doanh thu theo từng ngày để vẽ biểu đồ
    public Map<Integer, BigDecimal> getDailyRevenue(int month, int year) {
        Map<Integer, BigDecimal> dailyRevenue = new LinkedHashMap<>();
        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
        for (int d = 1; d <= daysInMonth; d++) {
            dailyRevenue.put(d, BigDecimal.ZERO);
        }

        String sql = """
        SELECT DAY(ip.paid_at) AS day_num,
               COALESCE(SUM(ip.amount), 0) AS daily_total
        FROM InvoicePayments ip
        WHERE MONTH(ip.paid_at) = ?
          AND YEAR(ip.paid_at) = ?
        GROUP BY DAY(ip.paid_at)
        ORDER BY DAY(ip.paid_at)
        """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, month);
            stm.setInt(2, year);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    dailyRevenue.put(rs.getInt("day_num"), rs.getBigDecimal("daily_total"));
                }
            }
        } catch (SQLException e) {
            System.out.println("getDailyRevenue: " + e.getMessage());
        }
        return dailyRevenue;
    }

    // tính tỷ lệ lấp đầy: phòng có khách / tổng phòng đang hoạt động
    public double getOccupancyRate() {
        String sql = """
                     select
                     cast(sum(case when r.status = N'Phòng có khách' then 1 else 0 end) as float) * 100.0 / nullif(count(*), 0) as occupancy_rate
                     from rooms r
                     where r.is_active = 1
                     """;
        try (PreparedStatement stm = connection.prepareStatement(sql); ResultSet rs = stm.executeQuery()) {
            if (rs.next()) {
                double rate = rs.getDouble("occupancy_rate");
                return Double.isNaN(rate) ? 0.0 : Math.round(rate * 10.0) / 10.0;
            }
        } catch (SQLException e) {
            System.out.println("getOccupancyRate: " + e.getMessage());
        }
        return 0.0;
    }

    // đếm tổng số đặt phòng trong tháng đã chọn
    public int getTotalBookings(int month, int year) {
        String sql = """
            select count(*) as total
            from bookings b
            where month(b.created_at) = ?
              and year(b.created_at) = ?
            """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, month);
            stm.setInt(2, year);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            System.out.println("getTotalBookings: " + e.getMessage());
        }
        return 0;
    }

    // lấy 5 đặt phòng gần nhất trong tháng đã chọn
    public List<Map<String, Object>> getRecentBookings(int month, int year) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = """
            select top 5
                b.booking_code,
                g.full_name as guest_name,
                rt.type_name as room_type,
                b.checkin_date,
                b.status
            from bookings b
            left join guests g on b.guest_id = g.guest_id
            join roomtypes rt on b.room_type_id = rt.room_type_id
            where month(b.created_at) = ?
              and year(b.created_at) = ?
            order by b.created_at desc
            """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, month);
            stm.setInt(2, year);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("bookingCode", rs.getString("booking_code"));
                    row.put("guestName", rs.getString("guest_name"));
                    row.put("roomType", rs.getString("room_type"));
                    row.put("checkinDate", rs.getDate("checkin_date"));
                    row.put("status", rs.getString("status"));
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            System.out.println("getRecentBookings: " + e.getMessage());
        }
        return list;
    }

    // lấy 5 đánh giá mới nhất
    public List<Map<String, Object>> getLatestReviews() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = """
            select top 5
                g.full_name as guest_name,
                f.rating,
                f.submitted_at
            from feedback f
            left join guests g on f.guest_id = g.guest_id
            order by f.submitted_at desc
            """;
        try (PreparedStatement stm = connection.prepareStatement(sql); ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("guestName", rs.getString("guest_name"));
                row.put("rating", rs.getInt("rating"));
                row.put("submittedAt", rs.getTimestamp("submitted_at"));
                list.add(row);
            }
        } catch (SQLException e) {
            System.out.println("getLatestReviews: " + e.getMessage());
        }
        return list;
    }

    // lấy tỷ lệ lấp đầy theo từng loại phòng
    public List<Map<String, Object>> getOccupancyByRoomType() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = """
                     select
                     rt.type_name,
                     count(case when r.status = N'phòng có khách' then 1 end) as occupied,
                     count(*) as total,
                     cast(count(case when r.status = N'phòng có khách' then 1 end) as float) * 100.0 / nullif(count(*), 0) as occupancy_pct
                     from roomtypes rt
                     join rooms r on rt.room_type_id = r.room_type_id
                     where r.is_active = 1
                     group by rt.type_name
                     order by rt.type_name
                     """;
        try (PreparedStatement stm = connection.prepareStatement(sql); ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("typeName", rs.getString("type_name"));
                row.put("occupied", rs.getInt("occupied"));
                row.put("total", rs.getInt("total"));
                double pct = rs.getDouble("occupancy_pct");
                row.put("occupancyPct", Double.isNaN(pct) ? 0.0 : Math.round(pct));
                list.add(row);
            }
        } catch (SQLException e) {
            System.out.println("getOccupancyByRoomType: " + e.getMessage());
        }
        return list;
    }
}
