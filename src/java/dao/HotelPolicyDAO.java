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
 * @author Minh Thu
 */
public class HotelPolicyDAO extends DBContext {

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

    
}