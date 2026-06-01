/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import dal.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.HotelImage;
/**
 *
 * @author Minh Thu
 */
public class HotelImageDAO extends DBContext{
  public String getLatestHotelBackgroundImage(int hotelId) {
        String sql = """
            SELECT TOP 1 image_url
            FROM HotelImages
            WHERE hotel_id = ?
            ORDER BY image_id DESC
        """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, hotelId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("image_url");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
}
