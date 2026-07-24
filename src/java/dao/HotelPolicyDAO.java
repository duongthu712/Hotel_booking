/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dal.DBContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.HotelPolicy;

/**
 * Last update: 04/06/2026
 *
 * @author Minh Thu
 */
public class HotelPolicyDAO extends DBContext {

    // Author: ThuDNM-HE204370
    public List<HotelPolicy> getAllActivePolicies() {
        List<HotelPolicy> list = new ArrayList<>();
        String sql = "SELECT policy_id, policy_name, [description], policy_type, is_active "
                + "FROM HotelPolicies WHERE is_active = 1";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                HotelPolicy hp = new HotelPolicy(
                        rs.getInt("policy_id"),
                        rs.getString("policy_name"),
                        rs.getString("description"),
                        rs.getString("policy_type"),
                        rs.getBoolean("is_active")
                );
                list.add(hp);
            }
            // Đóng kết nối tránh leak dữ liệu
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    //LinhLTHE200306
    public List<HotelPolicy> getAllHotelPolicies() throws Exception {
        List<HotelPolicy> list = new ArrayList<>();
        String strSQL = """
                        select * 
                        from HotelPolicies 
                        order by policy_id desc
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL); ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("policy_id");
                String name = rs.getString("policy_name");
                String description = rs.getString("description").trim();
                String type = rs.getString("policy_type");
                boolean active = rs.getBoolean("is_active");

                HotelPolicy newPolicy = new HotelPolicy(id, name, description, type, active);
                list.add(newPolicy);
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể truy xuất danh sách chính sách.");
        }
        return list;
    }

    public HotelPolicy getHotelPolicyById(int policyId) throws Exception {
        String strSQL = """
                        select * 
                        from HotelPolicies 
                        where policy_id = ? 
                        order by policy_id desc
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setInt(1, policyId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("policy_id");
                    String name = rs.getString("policy_name");
                    String description = rs.getString("description").trim();
                    String type = rs.getString("policy_type");
                    boolean active = rs.getBoolean("is_active");

                    HotelPolicy policy = new HotelPolicy(id, name, description, type, active);
                    return policy;
                } else {
                    throw new Exception("Chính sách này không tồn tại.");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Vui lòng thử lại sau.");
        }
    }

    public HotelPolicy getHotelPolicyByName(String policyName) throws Exception {
        String strSQL = """
                        select * 
                        from HotelPolicies 
                        where policy_name = ? 
                        order by policy_id desc
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setString(1, policyName);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("policy_id");
                    String name = rs.getString("policy_name");
                    String description = rs.getString("description") != null ? rs.getString("description").trim() : "";
                    String type = rs.getString("policy_type");
                    boolean active = rs.getBoolean("is_active");

                    HotelPolicy policy = new HotelPolicy(id, name, description, type, active);
                    return policy;
                } else {
                    throw new Exception("Chính sách này không tồn tại.");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Vui lòng thử lại sau.");
        }
    }

    public List<HotelPolicy> getFilteredPolicies(String keyword, String policyType) throws Exception {
        List<HotelPolicy> list = new ArrayList<>();
        StringBuilder strSQL = new StringBuilder("""
                        select * 
                        from HotelPolicies 
                        where 1 = 1 
                        """);
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            strSQL.append(" and policy_name like ? ");
            params.add("%" + keyword.trim() + "%");
        }
        if (policyType != null && !policyType.trim().isEmpty() && !policyType.trim().equalsIgnoreCase("all")) {
            strSQL.append(" and policy_type = ? ");
            params.add(policyType.trim());
        }
        strSQL.append(" order by policy_id desc");

        try (PreparedStatement stm = connection.prepareStatement(strSQL.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stm.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("policy_id");
                    String name = rs.getString("policy_name");
                    String description = rs.getString("description").trim();
                    String type = rs.getString("policy_type");
                    boolean active = rs.getBoolean("is_active");

                    HotelPolicy policy = new HotelPolicy(id, name, description, type, active);
                    list.add(policy);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể tìm kiếm chính sách.");
        }
        return list;
    }

    public HotelPolicy createHotelPolicy(HotelPolicy policy) throws Exception {
        try {
            getHotelPolicyByName(policy.getPolicyName());
            throw new Exception("Tên chính sách này đã tồn tại.");
        } catch (Exception e) {
            if (!e.getMessage().equals("Chính sách này không tồn tại.")) {
                throw e;
            }
        }

        // Không insert hotel_id, để DB tự set mặc định = 1 (hệ thống 1 khách sạn)
        String strSQL = """
                        insert into HotelPolicies (policy_name, policy_type, [description], is_active) 
                        values (?, ?, ?, ?)
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stm.setString(1, policy.getPolicyName());
            stm.setString(2, policy.getPolicyType());
            stm.setString(3, policy.getDescription());
            stm.setBoolean(4, policy.isIs_active());

            if (stm.executeUpdate() > 0) {
                try (ResultSet generatedKeys = stm.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        policy.setPolicyId(generatedKeys.getInt(1));
                    }
                }
                return policy;
            } else {
                throw new Exception("Thêm chính sách thất bại.");
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể tạo mới chính sách.");
        }
    }

    public HotelPolicy updateHotelPolicy(HotelPolicy policy) throws Exception {
        HotelPolicy found = getHotelPolicyById(policy.getPolicyId());
        if (found == null) {
            throw new Exception("Chính sách này không tồn tại, không thể cập nhật.");
        }
        String strSQL = """
                        update HotelPolicies 
                        set policy_name = ?, 
                        policy_type = ?, 
                        [description] = ?, 
                        is_active = ? 
                        where policy_id = ?
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setString(1, policy.getPolicyName());
            stm.setString(2, policy.getPolicyType());
            stm.setString(3, policy.getDescription());
            stm.setBoolean(4, policy.isIs_active());
            stm.setInt(5, policy.getPolicyId());

            if (stm.executeUpdate() > 0) {
                return policy;
            } else {
                throw new Exception("Cập nhật thất bại.");
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể cập nhật.");
        }
    }

    public HotelPolicy delete(int policyId) throws Exception {
        HotelPolicy found = getHotelPolicyById(policyId);
        String strSQL = """
                        delete 
                        from HotelPolicies 
                        where policy_id = ?
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setInt(1, policyId);
            if (stm.executeUpdate() > 0) {
                return found;
            } else {
                throw new Exception("Không thể xóa chính sách.");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 547) {
                throw new Exception("Không thể xóa vì chính sách đang được sử dụng.");
            }
            throw new Exception("Lỗi hệ thống: Không thể xóa.");
        }
    }

    public List<String> getAllPolicyTypes() throws Exception {
        List<String> types = new ArrayList<>();
        String strSQL = "select distinct policy_type from HotelPolicies order by policy_type";

        try (PreparedStatement stm = connection.prepareStatement(strSQL); ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                String type = rs.getString("policy_type");
                if (type != null && !type.trim().isEmpty()) {
                    types.add(type.trim());
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy danh sách loại chính sách.");
        }
        return types;
    }

}
