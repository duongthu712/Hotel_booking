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
import model.Service;
import model.ServiceType;
import model.HotelInfo;

/**
 *
 * @author Minh Thu
 */
public class HotelInfoDAO extends DBContext {

    public List<Service> getActiveHotelServices() {
        List<Service> list = new ArrayList<>();
        String sql = "SELECT hotel_service_id, service_name, [description], unit_price, is_active " +
                     "FROM HotelServices WHERE is_active = 1";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
           ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Service service = new Service(
                    rs.getInt("hotel_service_id"),
                    rs.getString("service_name"),
                    rs.getString("description"),
                    rs.getBigDecimal("unit_price"),
                    rs.getBoolean("is_active"),
                    ServiceType.HOTEL
                );
                list.add(service);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public HotelInfo getHotelDetails(int hotelId) {
        HotelInfo info = null;
        try {
            String sqlHotel = "SELECT hotel_id, hotel_name, [description], \n"
                    + "                         CAST(checkin_time AS TIME(0)) AS checkin, \n"
                    + "                          CAST(checkout_time AS TIME(0)) AS checkout, \n"
                    + "                          [address], phone, email FROM HotelInfo WHERE hotel_id = ?";
            PreparedStatement stm = connection.prepareStatement(sqlHotel);
            stm.setInt(1, hotelId);
           ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                info = new HotelInfo(
                        rs.getInt("hotel_id"),
                        rs.getString("hotel_name"),
                        rs.getString("description"),
                        rs.getString("checkin"),
                        rs.getString("checkout"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("email")
                );
                String sqlImages = "SELECT image_url, caption FROM HotelImages WHERE hotel_id  =?";
                 PreparedStatement stmImg = connection.prepareStatement(sqlImages);
                stmImg.setInt(1, hotelId);
                ResultSet rsImages = stmImg.executeQuery();
                while (rsImages.next()) {
                    String url = rsImages.getString("image_url");
                    String caption = rsImages.getString("caption");
                    info.addImage(url, caption);
                }
                rsImages.close();
                stmImg.close();
            }
        } catch (Exception e) {
            System.out.println("getHotelDetails: " + e.getMessage());
        }
        return info;
    }
      


}
