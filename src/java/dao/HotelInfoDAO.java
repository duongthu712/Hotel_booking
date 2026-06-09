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
import model.RoomService;
import model.ServiceType;
import model.HotelInfo;
/**
 *
 * @author Minh Thu
 */
public class HotelInfoDAO extends DBContext{
 // 1. Lấy danh sách dịch vụ đang hoạt động (Bảng HotelServices thực tế)
    public List<RoomService> getActiveHotelServices() {
        List<RoomService> list = new ArrayList<>();
        String sql = "SELECT hotel_service_id, service_name, [description], unit_price, is_active " +
                     "FROM HotelServices WHERE is_active = 1";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RoomService service = new RoomService(
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

    // 2. Lấy thông tin tổng quan khách sạn kèm theo danh sách ẢNH ĐỘNG từ bảng HotelImages
    public HotelInfo getHotelDetails(int hotelId) {
        HotelInfo info = null;
        
        // 👉 CÂU LỆNH 1: Lấy thông tin cơ bản của khách sạn
        String sqlHotel = "SELECT hotel_id, hotel_name, [description], " +
                          "CONVERT(VARCHAR, checkin_time, 108) AS checkin, " +
                          "CONVERT(VARCHAR, checkout_time, 108) AS checkout, " +
                          "[address], phone, email FROM HotelInfo WHERE hotel_id = ?";
        
        // 👉 CÂU LỆNH 2: Lấy tất cả ảnh và chú thích thuộc về khách sạn đó
        String sqlImages = "SELECT image_url, caption FROM HotelImages WHERE hotel_id = ?";
        
        try {
            // Bước A: Chạy lệnh lấy thông tin text
            PreparedStatement psHotel = connection.prepareStatement(sqlHotel);
            psHotel.setInt(1, hotelId);
            ResultSet rsHotel = psHotel.executeQuery();
            
            if (rsHotel.next()) {
                // Khởi tạo đối tượng bằng Constructor có tham số mới tinh của bạn
                info = new HotelInfo(
                    rsHotel.getInt("hotel_id"),
                    rsHotel.getString("hotel_name"),
                    rsHotel.getString("description"),
                    rsHotel.getString("checkin"),
                    rsHotel.getString("checkout"),
                    rsHotel.getString("address"),
                    rsHotel.getString("phone"),
                    rsHotel.getString("email")
                );
                
                // Bước B: Chạy lệnh lấy danh sách ảnh nạp vào đối tượng vừa tạo
                PreparedStatement psImages = connection.prepareStatement(sqlImages);
                psImages.setInt(1, hotelId);
                ResultSet rsImages = psImages.executeQuery();
                
                while (rsImages.next()) {
                    String url = rsImages.getString("image_url");
                    String caption = rsImages.getString("caption");
                    
                    // Sử dụng hàm addImage() thông minh bạn vừa viết trong Model
                    info.addImage(url, caption);
                }
                
                rsImages.close();
                psImages.close();
            }
            
            rsHotel.close();
            psHotel.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info; // Trả về đối tượng đầy đủ cả thông tin lẫn bộ sưu tập ảnh
    }
}
