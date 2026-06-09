package dao;

import dal.DBContext;
import model.RoomType;
import model.RoomTypeService;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomTypeDAO extends DBContext {

    // Lấy các loại phòng để hiện lên thanh search
   public List<RoomType> getAllRoomTypes() {
        List<RoomType> list = new ArrayList<>();
        String sql = "SELECT rt.room_type_id, rt.type_name, rt.capacity, rt.bed_type, rt.bed_count, rt.area_sqm, rt.base_price, rt.is_active, " +
                     "MIN(rti.image_url) as minImageUrl " +
                     "FROM RoomTypes rt " +
                     "LEFT JOIN RoomTypeImages rti ON rt.room_type_id = rti.room_type_id " +
                     "WHERE rt.is_active = 1 " +
                     "GROUP BY rt.room_type_id, rt.type_name, rt.capacity, rt.bed_type, rt.bed_count, rt.area_sqm, rt.base_price, rt.is_active";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RoomType rt = new RoomType();
                rt.setRoomTypeId(rs.getInt("room_type_id"));
                rt.setTypeName(rs.getString("type_name"));
                rt.setCapacity(rs.getInt("capacity"));
                rt.setBedType(rs.getString("bed_type"));
                rt.setBedCount(rs.getInt("bed_count"));
                rt.setAreaSqm(rs.getBigDecimal("area_sqm"));
                rt.setBasePrice(rs.getBigDecimal("base_price"));
                rt.setActive(rs.getBoolean("is_active"));
                
                String minImg = rs.getString("minImageUrl");
                if (minImg != null) {
                    rt.addImage(minImg, "");
                }
                
                list.add(rt);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
   
   //Kết quả sau khi search
    public List<RoomType> searchRoomTypesByQuantity(String checkIn, String checkOut, int roomQuantity, String roomTypeId) {
        List<RoomType> list = new ArrayList<>();
        String sql = "SELECT rt.room_type_id, rt.type_name, rt.description, rt.capacity, "
                + "rt.bed_type, rt.bed_count, rt.area_sqm, rt.base_price, rt.is_active, "
                + "MIN(rti.image_url) as minImageUrl "
                + "FROM RoomTypes rt "
                + "LEFT JOIN RoomTypeImages rti ON rt.room_type_id = rti.room_type_id "
                + "WHERE rt.is_active = 1 ";

        if (roomTypeId != null && !roomTypeId.equals("all")) {
            sql += " AND rt.room_type_id = ? ";
        }

        sql += " AND (SELECT COUNT(*) "
                + " FROM Rooms r "
                + " WHERE r.room_type_id = rt.room_type_id "
                + " AND r.[status] != N'Đang bảo trì' "
                + " AND r.room_number NOT IN ( "
                + "     SELECT br.room_number "
                + "     FROM BookingRooms br "
                + "     JOIN Bookings b ON br.booking_id = b.booking_id "
                + "     WHERE b.[status] != N'Đã hủy' "
                + "     AND NOT (b.checkout_date <= ? OR b.checkin_date >= ?) "
                + " ) "
                + ") >= ? "
                + " GROUP BY rt.room_type_id, rt.type_name, rt.description, rt.capacity, "
                + " rt.bed_type, rt.bed_count, rt.area_sqm, rt.base_price, rt.is_active";

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
                rt.setAreaSqm(rs.getBigDecimal("area_sqm"));
                rt.setBasePrice(rs.getBigDecimal("base_price"));
                rt.setActive(rs.getBoolean("is_active"));

                String minImg = rs.getString("minImageUrl");
                if (minImg != null) {
                    rt.addImage(minImg, "");
                }

                list.add(rt);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
