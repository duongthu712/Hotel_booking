/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import dal.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.HotelService;
import model.HotelInfo;
/**
 *
 * @author Minh Thu
 */
public class HotelInfoDAO extends DBContext{
    public List<HotelService> getActiveHotelServices() {
        List<HotelService> list = new ArrayList<>();
        String sql = "SELECT hotel_service_id, hotel_id, service_name, description, unit_price, is_active " +
                     "FROM HotelServices WHERE is_active = 1";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new HotelService(
                    rs.getInt("hotel_service_id"),
                    rs.getInt("hotel_id"),
                    rs.getString("service_name"),
                    rs.getString("description"),
                    rs.getBigDecimal("unit_price"),
                    rs.getInt("is_active") == 1
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Lấy thông tin tổng quan khách sạn (bao gồm chuỗi chính sách chính)
    public HotelInfo getHotelDetails(int hotelId) {
        String sql = "SELECT hotel_id, hotel_name, description, amenities, policies, " +
                     "CONVERT(VARCHAR, checkin_time, 108) AS checkin, CONVERT(VARCHAR, checkout_time, 108) AS checkout, " +
                     "address, phone, email FROM HotelInfo WHERE hotel_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, hotelId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new HotelInfo(
                    rs.getInt("hotel_id"),
                    rs.getString("hotel_name"),
                    rs.getString("description"),
                    rs.getString("amenities"),
                    rs.getString("policies"),
                    rs.getString("checkin"),
                    rs.getString("checkout"),
                    rs.getString("address"),
                    rs.getString("phone"),
                    rs.getString("email")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
