package dao;

import dal.DBContext;
import dal.PasswordUtil;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import model.StaffAccount;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StaffAccountDAO extends DBContext {

    PreparedStatement stm;
    ResultSet rs;

    public StaffAccount getStaffById(int staffId) throws Exception {
        String strSQL = """
                    select * 
                    from StaffAccounts 
                    where staff_id = ? and deleted_at is null
                    """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setInt(1, staffId);

            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return mapStaff(rs);
                } else {
                    throw new Exception("Nhân viên này không tồn tại hoặc đã bị xóa.");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Vui lòng thử lại sau.");
        }
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

        try {
            String sql = """
                         SELECT * 
                         FROM StaffAccounts 
                         WHERE username = ? 
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

            if (PasswordUtil.checkPassword(password, staff.getPasswordHash())) {
                return staff;
            }

        } catch (Exception e) {
            System.out.println("loginWithHashCheck: " + e.getMessage());
        }

        return null;
    }

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

        return false;
    }

    public boolean updatePasswordByStaffId(int staffId, String newPasswordHash) {
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

            return rs.next();

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
        Timestamp createdAt = rs.getTimestamp("created_at");
        staff.setCreatedAt(createdAt == null ? null : createdAt.toLocalDateTime());

        Timestamp resetExpiry = rs.getTimestamp("reset_expiry");
        staff.setResetExpiry(resetExpiry == null ? null : resetExpiry.toLocalDateTime());
        staff.setResetCode(rs.getString("reset_code"));
        staff.setResetUsed(rs.getBoolean("reset_used"));

        return staff;
    }

    //LinhLTHE200306
    public List<StaffAccount> getAllStaffAcc() throws Exception {
        List<StaffAccount> list = new ArrayList<>();
        String strSQL = """
                        select * 
                        from StaffAccounts 
                        where deleted_at is null
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL); ResultSet rs = stm.executeQuery()) {

            while (rs.next()) {
                StaffAccount staff = mapStaff(rs);
                list.add(staff);
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy danh sách nhân viên.");
        }
        return list;
    }

    public List<StaffAccount> searchStaffAccByName(String keyword) throws Exception {
        List<StaffAccount> list = new ArrayList<>();
        String strSQL = """
                        select * 
                        from StaffAccounts 
                        where deleted_at is null and full_name like ?
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setString(1, "%" + keyword + "%");

            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    list.add(mapStaff(rs));
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể tìm kiếm nhân viên.");
        }
        return list;
    }

    public List<StaffAccount> searchStaffAccByMail(String keyword) throws Exception {
        List<StaffAccount> list = new ArrayList<>();
        String strSQL = """
                        select * 
                        from StaffAccounts 
                        where deleted_at is null and email like ?
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setString(1, "%" + keyword + "%");

            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    list.add(mapStaff(rs));
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể tìm kiếm nhân viên.");
        }
        return list;
    }

    public List<StaffAccount> searchStaffAccByRole(String role) throws Exception {
        List<StaffAccount> list = new ArrayList<>();
        String strSQL = """
                        select * 
                        from StaffAccounts 
                        where deleted_at is null and [role] = ?
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setString(1, role);

            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    list.add(mapStaff(rs));
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể tìm kiếm nhân viên theo chức vụ.");
        }
        return list;
    }

    public StaffAccount updateStaffAcc(StaffAccount staff) throws Exception {
        StaffAccount found = getStaffById(staff.getStaffId());
        if (found == null) {
            throw new Exception("Nhân viên này không tồn tại, không thể cập nhật.");
        }

        String checkEmailSQL = """
                               select staff_id 
                               from StaffAccounts 
                               where email = ? and staff_id != ? and deleted_at is null
                               """;
        try (PreparedStatement checkStm = connection.prepareStatement(checkEmailSQL)) {
            checkStm.setString(1, staff.getEmail());
            checkStm.setInt(2, staff.getStaffId());
            try (ResultSet rs = checkStm.executeQuery()) {
                if (rs.next()) {
                    throw new Exception("Email này đã được sử dụng bởi một nhân viên khác.");
                }
            }
        }

        String strSQL = """
                        update StaffAccounts 
                        set full_name = ?, 
                            phone = ?, 
                        email = ?, 
                            [role] = ?,
                        is_active = ? 
                        where staff_id = ? and deleted_at is null
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setString(1, staff.getFullName());
            stm.setString(2, staff.getPhone());
            stm.setString(3, staff.getEmail());
            stm.setString(4, staff.getRole());
            stm.setBoolean(5, staff.isActive());
            stm.setInt(6, staff.getStaffId());

            int rowCount = stm.executeUpdate();
            if (rowCount > 0) {
                return staff;
            } else {
                throw new Exception("Cập nhật nhân viên thất bại.");
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể cập nhật nhân viên.");
        }
    }

    public StaffAccount deleteStaffAcc(int staffId) throws Exception {
        StaffAccount found = getStaffById(staffId);
        if (found == null) {
            throw new Exception("Nhân viên cần xóa không tồn tại.");
        }

        String strSQL = """
                        update StaffAccounts  
                        set deleted_at = GETDATE(),  
                            is_active = 0 
                        where staff_id = ? and deleted_at is null 
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setInt(1, staffId);

            int rowCount = stm.executeUpdate();
            if (rowCount > 0) {
                return found;
            } else {
                throw new Exception("Không thể xóa nhân viên này.");
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể thực hiện thao tác xóa.");
        }
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

    public StaffAccount getStaffByUsername(String username) {
        StaffAccount staff = null;

        try {
            String sql = """
                         SELECT * 
                         FROM StaffAccounts 
                         WHERE username = ? 
                           AND deleted_at is null 
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
}
