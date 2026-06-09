package dao;

import dal.DBContext;
import dal.PasswordUtil;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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
<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
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
        StaffAccount staff = null;
<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
        try {
            String sql = """
                         SELECT *
                         FROM StaffAccounts
                         WHERE username = ?
<<<<<<< Updated upstream
                           AND is_active = 1
                         """;
            stm = connection.prepareStatement(sql);
            stm.setString(1, username);
            rs = stm.executeQuery();
            if (rs.next()) {
                staff = mapStaff(rs);
            }
            if (staff == null) {
                return null;
            }
            if (password == null || staff.getPasswordHash() == null) {
                return null;
            }
            if (password.equals(staff.getPasswordHash())) {
                return staff;
            }
=======
                         """;

            stm = connection.prepareStatement(sql);
            stm.setString(1, username);

            rs = stm.executeQuery();

            if (rs.next()) {
                staff = mapStaff(rs);
            }

            if (staff == null) {
                return null;
            }

            if (password == null || staff.getPasswordHash() == null) {
                return null;
            }

            if (password.equals(staff.getPasswordHash())) {
                return staff;
            }

>>>>>>> Stashed changes
            if (PasswordUtil.checkPassword(password, staff.getPasswordHash())) {
                return staff;
            }

        } catch (Exception e) {
            System.out.println("loginWithHashCheck: " + e.getMessage());
        }
        return null;
    }

<<<<<<< Updated upstream
    public void updateProfile(int staffId, String fullName, String email, String phone) {
=======
    public boolean isValueExistsForOtherStaff(String field, String value, int currentStaffId) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        String column;

        switch (field) {
            case "email":
                column = "email";
                break;
            case "phone":
                column = "phone";
                break;
            case "username":
                column = "username";
                break;
            default:
                return false;
        }

        try {
            String sql = "SELECT staff_id "
                    + "FROM StaffAccounts "
                    + "WHERE " + column + " = ? "
                    + "AND staff_id <> ?";

            stm = connection.prepareStatement(sql);
            stm.setString(1, value.trim());
            stm.setInt(2, currentStaffId);

            rs = stm.executeQuery();

            return rs.next();

        } catch (Exception e) {
            System.out.println("isValueExistsForOtherStaff: " + e.getMessage());
        }

        return false;
    }

    public boolean updateProfile(int staffId, String fullName, String email, String phone) {
>>>>>>> Stashed changes
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

            return stm.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("updateProfile: " + e.getMessage());
        }
<<<<<<< Updated upstream
    }

    public void updatePasswordByStaffId(int staffId, String newPasswordHash) {
=======

        return false;
    }

    public boolean updatePasswordByStaffId(int staffId, String newPasswordHash) {
>>>>>>> Stashed changes
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

            return stm.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("updatePasswordByStaffId: " + e.getMessage());
        }

        return false;
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