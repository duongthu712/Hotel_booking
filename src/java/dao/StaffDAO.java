package dao;

import dal.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.StaffAccount;

public class StaffDAO extends DBContext {

    public StaffAccount authenticateStaff(String username, String password) {

        String sql = """
            SELECT staff_id, username, full_name, email, phone, [role], is_active, created_at
            FROM StaffAccounts
            WHERE username = ? AND password_hash = ? AND is_active = 1
        """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password); // plain text so sánh trực tiếp

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new StaffAccount(
                    rs.getInt("staff_id"),
                    rs.getString("username"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("role"),
                    rs.getBoolean("is_active"),
                    rs.getTimestamp("created_at")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}