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
import model.HotelImage;
import model.HotelInfo;
import model.HotelNews;
import model.HotelService;

/**
 *
 * @author Minh Thu
 */
public class HotelInfoDAO extends DBContext {

    // Lấy các service còn hoạt động để hiện trên homepage 
    public List<HotelService> getActiveHotelServices() {
        List<HotelService> list = new ArrayList<>();
        String sql = "SELECT hotel_service_id, service_name, hotel_id, [description], unit_price, image_url, is_active "
                + "FROM HotelServices WHERE is_active = 1";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                HotelService service = new HotelService(
                        rs.getInt("hotel_service_id"),
                        rs.getNString("service_name"),
                        rs.getInt("hotel_id"),
                        rs.getNString("description"),
                        rs.getBigDecimal("unit_price"),
                        rs.getString("image_url"),
                        rs.getBoolean("is_active")
                );
                list.add(service);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy thông tin khách sạn để hiện trên homepage 
    public HotelInfo getHotelDetails(int hotelId) {
        HotelInfo info = null;
        try {
            String sqlHotel = "SELECT hotel_id, hotel_name, [description], "
                    + "CAST(checkin_time AS TIME(0)) AS checkin, "
                    + "CAST(checkout_time AS TIME(0)) AS checkout, "
                    + "[address], address_url, phone, email FROM HotelInfo WHERE hotel_id = ?";
            PreparedStatement stm = connection.prepareStatement(sqlHotel);
            stm.setInt(1, hotelId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                info = new HotelInfo(
                        rs.getInt("hotel_id"),
                        rs.getNString("hotel_name"),
                        rs.getNString("description"),
                        rs.getString("checkin"),
                        rs.getString("checkout"),
                        rs.getNString("address"),
                        rs.getString("address_url"),
                        rs.getString("phone"),
                        rs.getString("email")
                );

                // Lấy ảnh background
                String sqlImages = "SELECT TOP 1 image_id, image_url, hotel_id, caption, image_type "
                        + "FROM HotelImages WHERE hotel_id = ? AND image_type = N'Ảnh nền' "
                        + "ORDER BY image_id DESC";
                PreparedStatement stmImg = connection.prepareStatement(sqlImages);
                stmImg.setInt(1, hotelId);
                ResultSet rsImages = stmImg.executeQuery();
                while (rsImages.next()) {
                    HotelImage img = new HotelImage(
                            rsImages.getInt("image_id"),
                            rsImages.getString("image_url"),
                            rsImages.getInt("hotel_id"),
                            rsImages.getNString("caption"),
                            rsImages.getNString("image_type")
                    );
                    info.addImage(img);
                }
                rsImages.close();
                stmImg.close();
            }
            rs.close();
            stm.close();
        } catch (Exception e) {
            System.out.println("getHotelDetails: " + e.getMessage());
        }
        return info;
    }

    // Lấy 3 bài báo mới nhất
    public List<HotelNews> getTop3LatestNews() {
        List<HotelNews> list = new ArrayList<>();
        String sql = "SELECT TOP 3 news_id, hotel_id, title, content, image_url, is_active, created_at, created_by "
                + "FROM HotelNews WHERE is_active = 1 ORDER BY created_at DESC";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                HotelNews news = new HotelNews(
                        rs.getInt("news_id"),
                        rs.getInt("hotel_id"),
                        rs.getNString("title"),
                        rs.getNString("content"),
                        rs.getString("image_url"),
                        rs.getBoolean("is_active"),
                        rs.getTimestamp("created_at"),
                        rs.getInt("created_by")
                );
                list.add(news);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy 6 ảnh để hiển thị ở mục Không gian lưu trú
    public List<HotelImage> get6SmallImages(int hotelId) {
        List<HotelImage> list = new ArrayList<>();
        String sql = "SELECT TOP 6 image_id, image_url, hotel_id, caption, image_type "
                + "FROM HotelImages WHERE hotel_id = ? AND image_type = N'Ảnh nhỏ' "
                + "ORDER BY image_id DESC";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, hotelId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                HotelImage img = new HotelImage(
                        rs.getInt("image_id"),
                        rs.getString("image_url"),
                        rs.getInt("hotel_id"),
                        rs.getNString("caption"),
                        rs.getNString("image_type")
                );
                list.add(img);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
