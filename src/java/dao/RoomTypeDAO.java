package dao;

import dal.DBContext;
import model.RoomType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomTypeDAO extends DBContext {

    // Lấy các loại phòng để hiện lên thanh search cho khách
    public List<RoomType> getAllRoomTypes() {
        List<RoomType> list = new ArrayList<>();
        String sql = "SELECT rt.room_type_id, rt.type_name, rt.capacity, rt.bed_type, rt.bed_count, rt.area_sqm, rt.base_price, rt.is_active, "
                + "MIN(rti.image_url) as minImageUrl "
                + "FROM RoomTypes rt "
                + "LEFT JOIN RoomTypeImages rti ON rt.room_type_id = rti.room_type_id "
                + "WHERE rt.is_active = 1 "
                + "GROUP BY rt.room_type_id, rt.type_name, rt.capacity, rt.bed_type, rt.bed_count, rt.area_sqm, rt.base_price, rt.is_active "
                + "ORDER BY rt.room_type_id ASC"; // Bổ sung sắp xếp thứ tự đồng bộ
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

    // Kết quả sau khi khách thực hiện search tìm phòng trống
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
                + "      SELECT br.room_number "
                + "      FROM BookingRooms br "
                + "      JOIN Bookings b ON br.booking_id = b.booking_id "
                + "      WHERE b.[status] != N'Đã hủy' "
                + "      AND NOT (b.checkout_date <= ? OR b.checkin_date >= ?) "
                + " ) "
                + ") >= ? "
                + " GROUP BY rt.room_type_id, rt.type_name, rt.description, rt.capacity, "
                + " rt.bed_type, rt.bed_count, rt.area_sqm, rt.base_price, rt.is_active "
                + " ORDER BY rt.room_type_id ASC"; // Bổ sung sắp xếp thứ tự đồng bộ

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

    // 1. Lấy toàn bộ danh sách phòng phục vụ trang quản trị Manager (Đóng băng thứ tự ORDER BY tránh lệch pha ID)
    public List<RoomType> getAllRoomTypesForManager() {
        List<RoomType> list = new ArrayList<>();

        // Chỉ truy vấn bảng chính và bắt buộc ép thứ tự tăng dần theo room_type_id
        String sqlRoom = "SELECT room_type_id, type_name, description, capacity, bed_type, bed_count, area_sqm, base_price, is_active "
                + "FROM RoomTypes "
                + "ORDER BY room_type_id ASC";

        String sqlImages = "SELECT image_url FROM RoomTypeImages WHERE room_type_id = ?";

        String sqlServices = "SELECT rts.room_type_service_id, rts.service_id, rts.quantity, rts.is_free, s.service_name, s.unit_price "
                + "FROM RoomTypeServices rts "
                + "LEFT JOIN RoomServices s ON rts.service_id = s.service_id "
                + "WHERE rts.room_type_id = ?";

        try {
            if (connection == null) {
                System.out.println(">>> DAO ERROR: Connection dang bi NULL!");
                return list;
            }

            try (PreparedStatement psRoom = connection.prepareStatement(sqlRoom); ResultSet rsRoom = psRoom.executeQuery()) {

                while (rsRoom.next()) {
                    RoomType rt = new RoomType();
                    int roomTypeId = rsRoom.getInt("room_type_id");

                    rt.setRoomTypeId(roomTypeId);
                    rt.setTypeName(rsRoom.getString("type_name"));
                    rt.setDescription(rsRoom.getString("description"));
                    rt.setCapacity(rsRoom.getInt("capacity"));
                    rt.setBedType(rsRoom.getString("bed_type"));
                    rt.setBedCount(rsRoom.getInt("bed_count"));
                    rt.setAreaSqm(rsRoom.getBigDecimal("area_sqm"));
                    rt.setBasePrice(rsRoom.getBigDecimal("base_price"));
                    rt.setActive(rsRoom.getBoolean("is_active"));

                    // --- LUỒNG LẤY DANH SÁCH ẢNH ALBUM ---
                    List<String> imagesList = new ArrayList<>();
                    try (PreparedStatement psImg = connection.prepareStatement(sqlImages)) {
                        psImg.setInt(1, roomTypeId);
                        try (ResultSet rsImg = psImg.executeQuery()) {
                            while (rsImg.next()) {
                                imagesList.add(rsImg.getString("image_url"));
                            }
                        }
                    }
                    rt.setImageUrl(imagesList);

                    // --- LUỒNG LẤY DANH SÁCH DỊCH VỤ ĐI KÈM (Cột is_free kiểu INT) ---
                    List<model.RoomTypeService> servicesList = new ArrayList<>();
                    try (PreparedStatement psSer = connection.prepareStatement(sqlServices)) {
                        psSer.setInt(1, roomTypeId);
                        try (ResultSet rsSer = psSer.executeQuery()) {
                            while (rsSer.next()) {
                                if (rsSer.getObject("service_id") != null) {
                                    model.RoomTypeService rts = new model.RoomTypeService();
                                    rts.setRoomTypeServiceId(rsSer.getInt("room_type_service_id"));
                                    rts.setQuantity(rsSer.getInt("quantity"));
                                    rts.setIsFree(rsSer.getInt("is_free")); // Nhận số lượng nguyên INT chuẩn từ DB mới

                                    model.RoomService s = new model.RoomService();
                                    s.setServiceId(rsSer.getInt("service_id"));
                                    s.setServiceName(rsSer.getString("service_name"));
                                    s.setUnitPrice(rsSer.getBigDecimal("unit_price"));

                                    rts.setRoomService(s);
                                    servicesList.add(rts);
                                }
                            }
                        }
                    }
                    rt.setRoomTypeServices(servicesList);

                    list.add(rt);
                }
            }
        } catch (Exception e) {
            System.out.println(">>> LỖI LOGIC TẠI getAllRoomTypesForManager: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // 2. Thêm mới một loại phòng vào hệ thống kèm theo danh sách hình ảnh và dịch vụ
    public boolean insertRoomType(RoomType rt, List<String> imageList, List<model.RoomTypeService> serviceList) {
        // SỬA: Loại bỏ cột image_description không có trong DB
        String insertRoomTypeSql = "INSERT INTO RoomTypes (type_name, description, capacity, bed_type, bed_count, area_sqm, base_price, is_active) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String insertImageSql = "INSERT INTO RoomTypeImages (room_type_id, image_url) VALUES (?, ?)";
        String insertServiceSql = "INSERT INTO RoomTypeServices (room_type_id, service_id, quantity, is_free) VALUES (?, ?, ?, ?)";

        PreparedStatement psRoom = null;
        PreparedStatement psImg = null;
        PreparedStatement psSer = null;
        ResultSet generatedKeys = null;

        try {
            connection.setAutoCommit(false); // Kích hoạt Transaction

            psRoom = connection.prepareStatement(insertRoomTypeSql, Statement.RETURN_GENERATED_KEYS);
            psRoom.setString(1, rt.getTypeName());
            psRoom.setString(2, rt.getDescription());
            psRoom.setInt(3, rt.getCapacity());
            psRoom.setString(4, rt.getBedType());
            psRoom.setInt(5, rt.getBedCount());
            psRoom.setBigDecimal(6, rt.getAreaSqm());
            psRoom.setBigDecimal(7, rt.getBasePrice());
            psRoom.setBoolean(8, rt.isActive());

            int affectedRows = psRoom.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating room type failed, no rows affected.");
            }

            int generatedId = 0;
            generatedKeys = psRoom.getGeneratedKeys();
            if (generatedKeys.next()) {
                generatedId = generatedKeys.getInt(1);
            }

            if (generatedId > 0) {
                // 1. Chèn album ảnh hàng loạt (Batch)
                if (imageList != null && !imageList.isEmpty()) {
                    psImg = connection.prepareStatement(insertImageSql);
                    for (String imgUrl : imageList) {
                        if (imgUrl != null && !imgUrl.trim().isEmpty()) {
                            psImg.setInt(1, generatedId);
                            psImg.setString(2, imgUrl.trim());
                            psImg.addBatch();
                        }
                    }
                    psImg.executeBatch();
                }

                // 2. Chèn danh sách cấu hình dịch vụ phòng hàng loạt (Batch)
                if (serviceList != null && !serviceList.isEmpty()) {
                    psSer = connection.prepareStatement(insertServiceSql);
                    for (model.RoomTypeService rts : serviceList) {
                        psSer.setInt(1, generatedId);
                        psSer.setInt(2, rts.getServiceId());
                        psSer.setInt(3, rts.getQuantity());
                        psSer.setInt(4, rts.getIsFree()); // INT tương thích hoàn toàn với DB
                        psSer.addBatch();
                    }
                    psSer.executeBatch();
                }

                connection.commit(); // Thành công toàn bộ thì commit dữ liệu
                return true;
            } else {
                throw new SQLException("Creating room type failed, no ID obtained.");
            }

        } catch (Exception e) {
            try {
                if (connection != null) {
                    connection.rollback(); // Hoàn tác nếu có bất kỳ lỗi nào xảy ra trong chuỗi tác vụ
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            // Giải phóng tài nguyên và đưa AutoCommit về lại trạng thái mặc định
            try {
                if (generatedKeys != null) {
                    generatedKeys.close();
                }
                if (psRoom != null) {
                    psRoom.close();
                }
                if (psImg != null) {
                    psImg.close();
                }
                if (psSer != null) {
                    psSer.close();
                }
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // 3. Cập nhật thông tin chi tiết loại phòng, dọn sạch và ghi đè danh sách hình ảnh, dịch vụ mới (Đồng bộ is_free dạng INT)
    public boolean updateRoomType(RoomType rt, List<String> newImageList, List<model.RoomTypeService> newServiceList) {
        String updateRoomTypeSql = "UPDATE RoomTypes SET type_name = ?, description = ?, capacity = ?, bed_type = ?, "
                + "bed_count = ?, area_sqm = ?, base_price = ?, is_active = ? "
                + "WHERE room_type_id = ?";
        String deleteOldImagesSql = "DELETE FROM RoomTypeImages WHERE room_type_id = ?";
        String insertImageSql = "INSERT INTO RoomTypeImages (room_type_id, image_url, image_description) VALUES (?, ?, ?)";
        String deleteOldServicesSql = "DELETE FROM RoomTypeServices WHERE room_type_id = ?";
        String insertServiceSql = "INSERT INTO RoomTypeServices (room_type_id, service_id, quantity, is_free) VALUES (?, ?, ?, ?)";

        PreparedStatement psRoom = null;
        PreparedStatement psDelImg = null;
        PreparedStatement psInsImg = null;
        PreparedStatement psDelSer = null;
        PreparedStatement psInsSer = null;

        try {
            connection.setAutoCommit(false); // Kích hoạt Transaction dữ liệu

            // Cập nhật thông tin văn bản nền của hạng phòng
            psRoom = connection.prepareStatement(updateRoomTypeSql);
            psRoom.setString(1, rt.getTypeName());
            psRoom.setString(2, rt.getDescription());
            psRoom.setInt(3, rt.getCapacity());
            psRoom.setString(4, rt.getBedType());
            psRoom.setInt(5, rt.getBedCount());
            psRoom.setBigDecimal(6, rt.getAreaSqm());
            psRoom.setBigDecimal(7, rt.getBasePrice());
            psRoom.setBoolean(8, rt.isActive());
            psRoom.setInt(9, rt.getRoomTypeId());
            psRoom.executeUpdate();

            // Xóa toàn bộ dữ liệu ảnh cũ và cập nhật đè danh sách ảnh mới chốt từ form
            psDelImg = connection.prepareStatement(deleteOldImagesSql);
            psDelImg.setInt(1, rt.getRoomTypeId());
            psDelImg.executeUpdate();

            if (newImageList != null && !newImageList.isEmpty()) {
                psInsImg = connection.prepareStatement(insertImageSql);
                for (String imgUrl : newImageList) {
                    if (imgUrl != null && !imgUrl.trim().isEmpty()) {
                        psInsImg.setInt(1, rt.getRoomTypeId());
                        psInsImg.setString(2, imgUrl.trim());
                        psInsImg.setString(3, "Image for " + rt.getTypeName());
                        psInsImg.addBatch();
                    }
                }
                psInsImg.executeBatch();
            }

            // Xóa cấu hình dịch vụ cũ của phòng và chèn lại loạt mảng dịch vụ vừa cấu hình lại từ admin
            psDelSer = connection.prepareStatement(deleteOldServicesSql);
            psDelSer.setInt(1, rt.getRoomTypeId());
            psDelSer.executeUpdate();

            if (newServiceList != null && !newServiceList.isEmpty()) {
                psInsSer = connection.prepareStatement(insertServiceSql);
                for (model.RoomTypeService rts : newServiceList) {
                    psInsSer.setInt(1, rt.getRoomTypeId());
                    psInsSer.setInt(2, rts.getServiceId());
                    psInsSer.setInt(3, rts.getQuantity());
                    psInsSer.setInt(4, rts.getIsFree()); // Ghi nhận trực tiếp giá trị số nguyên INT xuống DB
                    psInsSer.addBatch();
                }
                psInsSer.executeBatch();
            }

            connection.commit();
            return true;
        } catch (Exception e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
                if (psRoom != null) {
                    psRoom.close();
                }
                if (psDelImg != null) {
                    psDelImg.close();
                }
                if (psInsImg != null) {
                    psInsImg.close();
                }
                if (psDelSer != null) {
                    psDelSer.close();
                }
                if (psInsSer != null) {
                    psInsSer.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // 4. Xóa mềm loại phòng bằng cách cập nhật trạng thái hoạt động (is_active = 0) tránh lỗi ràng buộc dữ liệu lịch sử đặt phòng
    public boolean deleteRoomType(int roomTypeId) {
        String sql = "UPDATE RoomTypes SET is_active = 0 WHERE room_type_id = ?";
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, roomTypeId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
