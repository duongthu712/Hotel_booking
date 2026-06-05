package dao;

import dal.DBContext;
import dal.PasswordUtil;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.StaffAccount;

public class StaffAccountDAO extends DBContext {

    PreparedStatement stm;
    ResultSet rs;

    public StaffAccount getStaffById(int staffId) {
        StaffAccount staff = null;

        try {
            String sql = """
                         SELECT *
                         FROM StaffAccounts
                         WHERE staff_id = ?
                           AND is_active = 1
                         """;

            stm = connection.prepareStatement(sql);
            stm.setInt(1, staffId);

            rs = stm.executeQuery();

            if (rs.next()) {
                staff = mapStaff(rs);
            }

        } catch (Exception e) {
            System.out.println("getStaffById: " + e.getMessage());
        }

        return staff;
    }

    public StaffAccount getStaffByIdIncludeInactive(int staffId) {
        StaffAccount staff = null;

        try {
            String sql = """
                         SELECT *
                         FROM StaffAccounts
                         WHERE staff_id = ?
                         """;

            stm = connection.prepareStatement(sql);
            stm.setInt(1, staffId);

            rs = stm.executeQuery();

            if (rs.next()) {
                staff = mapStaff(rs);
            }

        } catch (Exception e) {
            System.out.println("getStaffByIdIncludeInactive: " + e.getMessage());
        }

        return staff;
    }

    public StaffAccount getStaffByUsername(String username) {
        StaffAccount staff = null;

        try {
            String sql = """
                         SELECT *
                         FROM StaffAccounts
                         WHERE username = ?
                           AND is_active = 1
                         """;

            stm = connection.prepareStatement(sql);
            stm.setString(1, username);

            rs = stm.executeQuery();

            if (rs.next()) {
                staff = mapStaff(rs);
            }

        } catch (Exception e) {
            System.out.println("getStaffByUsername: " + e.getMessage());
        }

        return staff;
    }

    public StaffAccount getStaffByEmail(String email) {
        StaffAccount staff = null;

        try {
            String sql = """
                         SELECT *
                         FROM StaffAccounts
                         WHERE email = ?
                           AND is_active = 1
                         """;

            stm = connection.prepareStatement(sql);
            stm.setString(1, email);

            rs = stm.executeQuery();

            if (rs.next()) {
                staff = mapStaff(rs);
            }

        } catch (Exception e) {
            System.out.println("getStaffByEmail: " + e.getMessage());
        }

        return staff;
    }

    public StaffAccount loginWithHashCheck(String username, String password) {
        StaffAccount staff = getStaffByUsername(username);

        if (staff == null) {
            System.out.println("LOGIN DAO: staff not found by username = " + username);
            return null;
        }

        System.out.println("LOGIN DAO: found staff = " + staff.getUsername());
        System.out.println("LOGIN DAO: input password = " + password);
        System.out.println("LOGIN DAO: stored password = " + staff.getPasswordHash());

        if (password == null || staff.getPasswordHash() == null) {
            return null;
        }

        if (password.equals(staff.getPasswordHash())) {
            return staff;
        }

        try {
            if (PasswordUtil.checkPassword(password, staff.getPasswordHash())) {
                return staff;
            }
        } catch (Exception e) {
            System.out.println("LOGIN DAO: hash check error = " + e.getMessage());
        }

        return null;
    }

    public List<StaffAccount> getStaffAccounts() {
        List<StaffAccount> list = new ArrayList<>();

        try {
            String sql = """
                         SELECT *
                         FROM StaffAccounts
                         ORDER BY staff_id DESC
                         """;

            stm = connection.prepareStatement(sql);
            rs = stm.executeQuery();

            while (rs.next()) {
                StaffAccount staff = mapStaff(rs);
                list.add(staff);
            }

        } catch (Exception e) {
            System.out.println("getStaffAccounts: " + e.getMessage());
        }

        return list;
    }

    public List<StaffAccount> searchStaff(String searchText, String role) {
        List<StaffAccount> list = new ArrayList<>();

        try {
            String sql = """
                         SELECT *
                         FROM StaffAccounts
                         WHERE 1 = 1
                         """;

            if (searchText != null && !searchText.trim().isEmpty()) {
                sql += """
                       AND (
                           username LIKE ?
                           OR full_name LIKE ?
                           OR email LIKE ?
                           OR phone LIKE ?
                       )
                       """;
            }

            if (role != null && !role.equals("ALL") && !role.trim().isEmpty()) {
                sql += " AND [role] = ?";
            }

            sql += " ORDER BY staff_id DESC";

            stm = connection.prepareStatement(sql);

            int index = 1;

            if (searchText != null && !searchText.trim().isEmpty()) {
                String keyword = "%" + searchText.trim() + "%";
                stm.setString(index++, keyword);
                stm.setString(index++, keyword);
                stm.setString(index++, keyword);
                stm.setString(index++, keyword);
            }

            if (role != null && !role.equals("ALL") && !role.trim().isEmpty()) {
                stm.setString(index++, role);
            }

            rs = stm.executeQuery();

            while (rs.next()) {
                StaffAccount staff = mapStaff(rs);
                list.add(staff);
            }

        } catch (Exception e) {
            System.out.println("searchStaff: " + e.getMessage());
        }

        return list;
    }

    public void createStaff(StaffAccount staff) {
        try {
            String sql = """
                         INSERT INTO StaffAccounts
                         (username, password_hash, full_name, email, phone, [role], is_active)
                         VALUES (?, ?, ?, ?, ?, ?, ?)
                         """;

            stm = connection.prepareStatement(sql);

            stm.setString(1, staff.getUsername());
            stm.setString(2, staff.getPasswordHash());
            stm.setString(3, staff.getFullName());
            stm.setString(4, staff.getEmail());
            stm.setString(5, staff.getPhone());
            stm.setString(6, staff.getRole());
            stm.setBoolean(7, staff.isActive());

            stm.executeUpdate();

        } catch (Exception e) {
            System.out.println("createStaff: " + e.getMessage());
        }
    }

    public void updateStaff(StaffAccount staff) {
        try {
            String sql = """
                         UPDATE StaffAccounts
                         SET username = ?,
                             full_name = ?,
                             email = ?,
                             phone = ?,
                             [role] = ?,
                             is_active = ?
                         WHERE staff_id = ?
                         """;

            stm = connection.prepareStatement(sql);

            stm.setString(1, staff.getUsername());
            stm.setString(2, staff.getFullName());
            stm.setString(3, staff.getEmail());
            stm.setString(4, staff.getPhone());
            stm.setString(5, staff.getRole());
            stm.setBoolean(6, staff.isActive());
            stm.setInt(7, staff.getStaffId());

            stm.executeUpdate();

        } catch (Exception e) {
            System.out.println("updateStaff: " + e.getMessage());
        }
    }

    public void updateStaffStatus(int staffId, boolean active) {
        try {
            String sql = """
                         UPDATE StaffAccounts
                         SET is_active = ?
                         WHERE staff_id = ?
                         """;

            stm = connection.prepareStatement(sql);

            stm.setBoolean(1, active);
            stm.setInt(2, staffId);

            stm.executeUpdate();

        } catch (Exception e) {
            System.out.println("updateStaffStatus: " + e.getMessage());
        }
    }

    public void updatePasswordByStaffId(int staffId, String newPasswordHash) {
        try {
            String sql = """
                         UPDATE StaffAccounts
                         SET password_hash = ?
                         WHERE staff_id = ?
                           AND is_active = 1
                         """;

            stm = connection.prepareStatement(sql);

            stm.setString(1, newPasswordHash);
            stm.setInt(2, staffId);

            stm.executeUpdate();

        } catch (Exception e) {
            System.out.println("updatePasswordByStaffId: " + e.getMessage());
        }
    }

    public void saveResetCode(String email, String code, LocalDateTime expiryTime) {
        try {
            String sql = """
                         UPDATE StaffAccounts
                         SET reset_code = ?,
                             reset_expiry = ?,
                             reset_used = 0
                         WHERE email = ?
                           AND is_active = 1
                         """;

            stm = connection.prepareStatement(sql);

            stm.setString(1, code);
            stm.setTimestamp(2, Timestamp.valueOf(expiryTime));
            stm.setString(3, email);

            stm.executeUpdate();

        } catch (Exception e) {
            System.out.println("saveResetCode: " + e.getMessage());
        }
    }

    public boolean isValidResetCode(String email, String code) {
        try {
            String sql = """
                         SELECT *
                         FROM StaffAccounts
                         WHERE email = ?
                           AND reset_code = ?
                           AND reset_used = 0
                           AND reset_expiry > GETDATE()
                           AND is_active = 1
                         """;

            stm = connection.prepareStatement(sql);

            stm.setString(1, email);
            stm.setString(2, code);

            rs = stm.executeQuery();

            if (rs.next()) {
                return true;
            }

        } catch (Exception e) {
            System.out.println("isValidResetCode: " + e.getMessage());
        }

        return false;
    }

    public void updatePasswordAndClearReset(String email, String newPasswordHash) {
        try {
            String sql = """
                         UPDATE StaffAccounts
                         SET password_hash = ?,
                             reset_code = NULL,
                             reset_expiry = NULL,
                             reset_used = 1
                         WHERE email = ?
                           AND is_active = 1
                         """;

            stm = connection.prepareStatement(sql);

            stm.setString(1, newPasswordHash);
            stm.setString(2, email);

            stm.executeUpdate();

        } catch (Exception e) {
            System.out.println("updatePasswordAndClearReset: " + e.getMessage());
        }
    }

    public void updateProfile(int staffId, String fullName, String email, String phone) {
        try {
            String sql = """
                         UPDATE StaffAccounts
                         SET full_name = ?,
                             email = ?,
                             phone = ?
                         WHERE staff_id = ?
                           AND is_active = 1
                         """;

            stm = connection.prepareStatement(sql);

            stm.setString(1, fullName);
            stm.setString(2, email);
            stm.setString(3, phone);
            stm.setInt(4, staffId);

            stm.executeUpdate();

        } catch (Exception e) {
            System.out.println("updateProfile: " + e.getMessage());
        }
    }

    private StaffAccount mapStaff(ResultSet rs) throws Exception {
        StaffAccount staff = new StaffAccount();

        staff.setStaffId(rs.getInt("staff_id"));
        staff.setUsername(rs.getString("username"));
        staff.setPasswordHash(rs.getString("password_hash"));
        staff.setFullName(rs.getString("full_name"));
        staff.setEmail(rs.getString("email"));
        staff.setPhone(rs.getString("phone"));
        staff.setRole(rs.getString("role"));
        staff.setActive(rs.getBoolean("is_active"));
        staff.setCreatedAt(rs.getTimestamp("created_at"));
        staff.setResetCode(rs.getString("reset_code"));
        staff.setResetExpiry(rs.getTimestamp("reset_expiry"));
        staff.setResetUsed(rs.getBoolean("reset_used"));

        return staff;
    }
}