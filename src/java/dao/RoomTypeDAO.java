package dao;

import dal.DBContext;
import model.RoomType;
import model.RoomService; // Sử dụng đúng Model RoomService mới đặt tên
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Minh Thu
 */
public class RoomTypeDAO extends DBContext {

    // 1. LẤY TOÀN BỘ DANH SÁCH + DỊCH VỤ PHÒNG ĐỘNG
    public List<RoomType> getAllRoomTypes() {
        List<RoomType> list = new ArrayList<>();
        
        String sql = "SELECT rt.room_type_id, rt.type_name, rt.description, rt.capacity, "
                   + "       rt.bed_type, rt.bed_count, rt.area_sqm, rt.base_price, rt.is_active, "
                   + "       MIN(rti.image_url) as imageUrl "
                   + "FROM RoomTypes rt "
                   + "LEFT JOIN RoomTypeImages rti ON rt.room_type_id = rti.room_type_id "
                   + "WHERE rt.is_active = 1 "
                   + "GROUP BY rt.room_type_id, rt.type_name, rt.description, rt.capacity, "
                   + "         rt.bed_type, rt.bed_count, rt.area_sqm, rt.base_price, rt.is_active";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RoomType rt = new RoomType();
                rt.setRoomTypeId(rs.getInt("room_type_id"));
                rt.setTypeName(rs.getString("type_name"));
                rt.setDescription(rs.getString("description"));
                rt.setCapacity(rs.getInt("capacity"));
                rt.setBedType(rs.getString("bed_type"));
                rt.setBedCount(rs.getInt("bed_count"));
                rt.setImageUrl(rs.getString("imageUrl"));
                rt.setAreaSqm(rs.getBigDecimal("area_sqm"));
                rt.setBasePrice(rs.getBigDecimal("base_price"));
                
                // --- ĐOẠN TRUY VẤN ROOM SERVICE CHO TỪNG HẠNG PHÒNG ---
                List<RoomService> roomServicesList = new ArrayList<>();
                String sqlService = "SELECT s.service_name, rts.quantity "
                                  + "FROM RoomTypeServices rts "
                                  + "JOIN [Services] s ON rts.service_id = s.service_id "
                                  + "WHERE rts.room_type_id = ?";
                
                PreparedStatement psSvc = connection.prepareStatement(sqlService);
                psSvc.setInt(1, rt.getRoomTypeId());
                ResultSet rsSvc = psSvc.executeQuery();
                while (rsSvc.next()) {
                    RoomService rsObj = new RoomService();
                    rsObj.setServiceName(rsSvc.getString("service_name"));
                    rsObj.setQuantity(rsSvc.getInt("quantity"));
                    roomServicesList.add(rsObj);
                }
                rsSvc.close();
                psSvc.close();
                
                rt.setRoomServices(roomServicesList); // Gắn danh sách đã đặt tên cẩn thận vào RoomType
                // ------------------------------------------------------
                
                list.add(rt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. TÌM KIẾM THEO NGÀY ĐẶT PHÒNG + DỊCH VỤ PHÒNG ĐỘNG
    public List<RoomType> searchRoomTypesByQuantity(String checkIn, String checkOut, int roomQuantity, String roomTypeId) {
        List<RoomType> list = new ArrayList<>();
        
        String sql = "SELECT rt.room_type_id, rt.type_name, rt.description, rt.capacity, "
                   + "       rt.bed_type, rt.bed_count, rt.area_sqm, rt.base_price, rt.is_active, "
                   + "       MIN(rti.image_url) as imageUrl "
                   + "FROM RoomTypes rt "
                   + "LEFT JOIN RoomTypeImages rti ON rt.room_type_id = rti.room_type_id "
                   + "WHERE rt.is_active = 1 ";

        if (roomTypeId != null && !roomTypeId.equals("all")) {
            sql += " AND rt.room_type_id = ? ";
        }

        sql += " AND ( "
             + "    SELECT COUNT(*) FROM Rooms r "
             + "    WHERE r.room_type_id = rt.room_type_id "
             + "      AND r.[status] != N'Đang bảo trì' "
             + "      AND r.room_number NOT IN ( "
             + "          SELECT br.room_number FROM BookingRooms br "
             + "          JOIN Bookings b ON br.booking_id = b.booking_id "
             + "          WHERE b.[status] != N'Đã hủy' "
             + "            AND NOT (b.checkout_date <= ? OR b.checkin_date >= ?) "
             + "      ) "
             + " ) >= ? "
             + " GROUP BY rt.room_type_id, rt.type_name, rt.description, rt.capacity, "
             + "          rt.bed_type, rt.bed_count, rt.area_sqm, rt.base_price, rt.is_active";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            int index = 1;

            if (roomTypeId != null && !roomTypeId.equals("all")) {
                ps.setInt(index++, Integer.parseInt(roomTypeId));
            }
            
            ps.setString(index++, checkIn);
            ps.setString(index++, checkOut);
            ps.setInt(index++, roomQuantity); 

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RoomType rt = new RoomType();
                rt.setRoomTypeId(rs.getInt("room_type_id"));
                rt.setTypeName(rs.getString("type_name"));
                rt.setDescription(rs.getString("description"));
                rt.setCapacity(rs.getInt("capacity"));
                rt.setBedType(rs.getString("bed_type"));
                rt.setBedCount(rs.getInt("bed_count"));
                rt.setImageUrl(rs.getString("imageUrl"));
                rt.setAreaSqm(rs.getBigDecimal("area_sqm"));
                rt.setBasePrice(rs.getBigDecimal("base_price"));
                
                // --- ĐOẠN TRUY VẤN ROOM SERVICE CHO TỪNG HẠNG PHÒNG ---
                List<RoomService> roomServicesList = new ArrayList<>();
                String sqlService = "SELECT s.service_name, rts.quantity "
                                  + "FROM RoomTypeServices rts "
                                  + "JOIN [Services] s ON rts.service_id = s.service_id "
                                  + "WHERE rts.room_type_id = ?";
                
                PreparedStatement psSvc = connection.prepareStatement(sqlService);
                psSvc.setInt(1, rt.getRoomTypeId());
                ResultSet rsSvc = psSvc.executeQuery();
                while (rsSvc.next()) {
                    RoomService rsObj = new RoomService();
                    rsObj.setServiceName(rsSvc.getString("service_name"));
                    rsObj.setQuantity(rsSvc.getInt("quantity"));
                    roomServicesList.add(rsObj);
                }
                rsSvc.close();
                psSvc.close();
                
                rt.setRoomServices(roomServicesList);
                // ------------------------------------------------------
                
                list.add(rt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public RoomType getRoomTypeById(int roomTypeId) {
    String sql = "SELECT rt.room_type_id, rt.type_name, rt.description, rt.capacity, "
               + "       rt.bed_type, rt.bed_count, rt.area_sqm, rt.base_price, rt.is_active, "
               + "       MIN(rti.image_url) as imageUrl "
               + "FROM RoomTypes rt "
               + "LEFT JOIN RoomTypeImages rti ON rt.room_type_id = rti.room_type_id "
               + "WHERE rt.room_type_id = ? AND rt.is_active = 1 "
               + "GROUP BY rt.room_type_id, rt.type_name, rt.description, rt.capacity, "
               + "         rt.bed_type, rt.bed_count, rt.area_sqm, rt.base_price, rt.is_active";
    try {
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, roomTypeId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            RoomType rt = new RoomType();
            rt.setRoomTypeId(rs.getInt("room_type_id"));
            rt.setTypeName(rs.getString("type_name"));
            rt.setDescription(rs.getString("description"));
            rt.setCapacity(rs.getInt("capacity"));
            rt.setBedType(rs.getString("bed_type"));
            rt.setBedCount(rs.getInt("bed_count"));
            rt.setImageUrl(rs.getString("imageUrl"));
            rt.setAreaSqm(rs.getBigDecimal("area_sqm"));
            rt.setBasePrice(rs.getBigDecimal("base_price"));

            // --- LẤY LUÔN CẢ ROOM SERVICES ĐỘNG NHƯ MÀN TRƯỚC ---
            List<RoomService> roomServicesList = new ArrayList<>();
            String sqlService = "SELECT s.service_name, rts.quantity "
                              + "FROM RoomTypeServices rts "
                              + "JOIN [Services] s ON rts.service_id = s.service_id "
                              + "WHERE rts.room_type_id = ?";
            PreparedStatement psSvc = connection.prepareStatement(sqlService);
            psSvc.setInt(1, rt.getRoomTypeId());
            ResultSet rsSvc = psSvc.executeQuery();
            while (rsSvc.next()) {
                RoomService rsObj = new RoomService();
                rsObj.setServiceName(rsSvc.getString("service_name"));
                rsObj.setQuantity(rsSvc.getInt("quantity"));
                roomServicesList.add(rsObj);
            }
            rsSvc.close();
            psSvc.close();
            
            rt.setRoomServices(roomServicesList);
            // ----------------------------------------------------

            return rt;
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}
}