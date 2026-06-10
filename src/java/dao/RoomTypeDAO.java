//package dao;
//
//import dal.DBContext;
//import model.RoomType;
//import model.RoomTypeService;
//import model.RoomService;          
//import model.ServiceType;      
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class RoomTypeDAO extends DBContext {
//
//    // 1. LẤY TOÀN BỘ DANH SÁCH PHÒNG
//    public List<RoomType> getAllRoomTypes() {
//        List<RoomType> list = new ArrayList<>();
//        String sql = "SELECT rt.room_type_id, rt.type_name, rt.description, rt.capacity, "
//                   + "       rt.bed_type, rt.bed_count, rt.area_sqm, rt.base_price, rt.is_active, "
//                   + "       MIN(rti.image_url) as minImageUrl "
//                   + "FROM RoomTypes rt "
//                   + "LEFT JOIN RoomTypeImages rti ON rt.room_type_id = rti.room_type_id "
//                   + "WHERE rt.is_active = 1 "
//                   + "GROUP BY rt.room_type_id, rt.type_name, rt.description, rt.capacity, "
//                   + "         rt.bed_type, rt.bed_count, rt.area_sqm, rt.base_price, rt.is_active";
//        try {
//            PreparedStatement ps = connection.prepareStatement(sql);
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                RoomType rt = new RoomType();
//                rt.setRoomTypeId(rs.getInt("room_type_id"));
//                rt.setTypeName(rs.getString("type_name"));
//                rt.setDescription(rs.getString("description"));
//                rt.setCapacity(rs.getInt("capacity"));
//                rt.setBedType(rs.getString("bed_type"));
//                rt.setBedCount(rs.getInt("bed_count"));
//                rt.setAreaSqm(rs.getBigDecimal("area_sqm"));
//                rt.setBasePrice(rs.getBigDecimal("base_price"));
//                rt.setActive(rs.getBoolean("is_active"));
//                
//                String minImg = rs.getString("minImageUrl");
//                if (minImg != null) rt.addImage(minImg, "");
//                
//                // Query CHỈ lấy dịch vụ miễn phí (is_free = 1)
//                List<RoomTypeService> roomTypeServicesList = new ArrayList<>();
//                String sqlService = "SELECT rts.room_type_service_id, rts.room_type_id, rts.service_id, rts.quantity, rts.is_free, "
//                                  + "       s.service_name, s.[description] as svc_desc, s.unit_price, s.is_active as svc_active "
//                                  + "FROM RoomTypeServices rts "
//                                  + "JOIN RoomServices s ON rts.service_id = s.service_id " 
//                                  + "WHERE rts.room_type_id = ? AND rts.is_free = 1";
//                
//                PreparedStatement psSvc = connection.prepareStatement(sqlService);
//                psSvc.setInt(1, rt.getRoomTypeId());
//                ResultSet rsSvc = psSvc.executeQuery();
//                while (rsSvc.next()) {
//                    RoomService s = new RoomService(rsSvc.getInt("service_id"), rsSvc.getString("service_name"), rsSvc.getString("svc_desc"), rsSvc.getBigDecimal("unit_price"), rsSvc.getBoolean("svc_active"), ServiceType.ROOM);
//                    RoomTypeService rtsObj = new RoomTypeService(rsSvc.getInt("room_type_service_id"), rsSvc.getInt("room_type_id"), rsSvc.getInt("service_id"), rsSvc.getInt("quantity"), rsSvc.getBoolean("is_free"), s);
//                    roomTypeServicesList.add(rtsObj);
//                }
//                rsSvc.close(); psSvc.close();
//                rt.setRoomTypeServices(roomTypeServicesList);
//                list.add(rt);
//            }
//            rs.close(); ps.close();
//        } catch (Exception e) { e.printStackTrace(); }
//        return list;
//    }
//
//    // 2. TÌM KIẾM PHÒNG TRỐNG THEO NGÀY
//    public List<RoomType> searchRoomTypesByQuantity(String checkIn, String checkOut, int roomQuantity, String roomTypeId) {
//        List<RoomType> list = new ArrayList<>();
//        String sql = "SELECT rt.room_type_id, rt.type_name, rt.description, rt.capacity, "
//                   + "       rt.bed_type, rt.bed_count, rt.area_sqm, rt.base_price, rt.is_active, "
//                   + "       MIN(rti.image_url) as minImageUrl "
//                   + "FROM RoomTypes rt "
//                   + "LEFT JOIN RoomTypeImages rti ON rt.room_type_id = rti.room_type_id "
//                   + "WHERE rt.is_active = 1 ";
//        if (roomTypeId != null && !roomTypeId.equals("all")) sql += " AND rt.room_type_id = ? ";
//        sql += " AND (SELECT COUNT(*) FROM Rooms r WHERE r.room_type_id = rt.room_type_id AND r.[status] != N'Đang bảo trì' AND r.room_number NOT IN (SELECT br.room_number FROM BookingRooms br JOIN Bookings b ON br.booking_id = b.booking_id WHERE b.[status] != N'Đã hủy' AND NOT (b.checkout_date <= ? OR b.checkin_date >= ?))) >= ? "
//             + " GROUP BY rt.room_type_id, rt.type_name, rt.description, rt.capacity, rt.bed_type, rt.bed_count, rt.area_sqm, rt.base_price, rt.is_active";
//        try {
//            PreparedStatement ps = connection.prepareStatement(sql);
//            int index = 1;
//            if (roomTypeId != null && !roomTypeId.equals("all")) ps.setInt(index++, Integer.parseInt(roomTypeId));
//            ps.setString(index++, checkIn); ps.setString(index++, checkOut); ps.setInt(index++, roomQuantity); 
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                RoomType rt = new RoomType();
//                rt.setRoomTypeId(rs.getInt("room_type_id"));
//                rt.setTypeName(rs.getString("type_name"));
//                rt.setDescription(rs.getString("description"));
//                rt.setCapacity(rs.getInt("capacity"));
//                rt.setBedType(rs.getString("bed_type"));
//                rt.setBedCount(rs.getInt("bed_count"));
//                rt.setAreaSqm(rs.getBigDecimal("area_sqm"));
//                rt.setBasePrice(rs.getBigDecimal("base_price"));
//                rt.setActive(rs.getBoolean("is_active"));
//                
//                String minImg = rs.getString("minImageUrl");
//                if (minImg != null) rt.addImage(minImg, "");
//                
//                // Query CHỈ lấy dịch vụ miễn phí (is_free = 1)
//                List<RoomTypeService> roomTypeServicesList = new ArrayList<>();
//                String sqlService = "SELECT rts.room_type_service_id, rts.room_type_id, rts.service_id, rts.quantity, rts.is_free, "
//                                  + "       s.service_name, s.[description] as svc_desc, s.unit_price, s.is_active as svc_active "
//                                  + "FROM RoomTypeServices rts "
//                                  + "JOIN RoomServices s ON rts.service_id = s.service_id "
//                                  + "WHERE rts.room_type_id = ? AND rts.is_free = 1";
//                PreparedStatement psSvc = connection.prepareStatement(sqlService);
//                psSvc.setInt(1, rt.getRoomTypeId());
//                ResultSet rsSvc = psSvc.executeQuery();
//                while (rsSvc.next()) {
//                    RoomService s = new RoomService(rsSvc.getInt("service_id"), rsSvc.getString("service_name"), rsSvc.getString("svc_desc"), rsSvc.getBigDecimal("unit_price"), rsSvc.getBoolean("svc_active"), ServiceType.ROOM);
//                    RoomTypeService rtsObj = new RoomTypeService(rsSvc.getInt("room_type_service_id"), rsSvc.getInt("room_type_id"), rsSvc.getInt("service_id"), rsSvc.getInt("quantity"), rsSvc.getBoolean("is_free"), s);
//                    roomTypeServicesList.add(rtsObj);
//                }
//                rsSvc.close(); psSvc.close();
//                rt.setRoomTypeServices(roomTypeServicesList);
//                list.add(rt);
//            }
//            rs.close(); ps.close();
//        } catch (Exception e) { e.printStackTrace(); }
//        return list;
//    }
//}
