package dao;

import dal.DBContext;
import model.RoomAmenity;
import model.RoomService;
import model.RoomType;
import model.RoomTypeService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomTypeDAO extends DBContext {

    private static final int HOLD_MINUTES = 15;

    // Lấy các loại phòng để hiện lên thanh search cho khách. Da thay doi SQL
    public List<RoomType> getAllRoomTypes() {
        List<RoomType> list = new ArrayList<>();

        String sql
                = "SELECT rt.room_type_id, rt.type_name, rt.capacity, "
                + "rt.bed_type, rt.bed_count, rt.area_sqm, "
                + "rt.base_price, rt.is_active, "
                + "img.image_url AS firstImageUrl "
                + "FROM RoomTypes rt "
                + "OUTER APPLY ( "
                + "    SELECT TOP 1 rti.image_url "
                + "    FROM RoomTypeImages rti "
                + "    WHERE rti.room_type_id = rt.room_type_id "
                + "    ORDER BY rti.image_id ASC "
                + ") img "
                + "WHERE rt.is_active = 1 "
                + "ORDER BY rt.room_type_id ASC";

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

                String firstImage = rs.getString("firstImageUrl");

                if (firstImage != null && !firstImage.trim().isEmpty()) {
                    rt.addImage(firstImage, "");
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
    public List<RoomType> searchRoomTypesByQuantity(
            String checkIn,
            String checkOut,
            int roomQuantity,
            String roomTypeId) {

        List<RoomType> list = new ArrayList<>();

        String sql
                = "SELECT rt.room_type_id, rt.type_name, rt.description, "
                + "rt.capacity, rt.bed_type, rt.bed_count, rt.area_sqm, "
                + "rt.base_price, rt.is_active, "
                + "img.image_url AS firstImageUrl "
                + "FROM RoomTypes rt "
                + "OUTER APPLY ( "
                + "    SELECT TOP 1 rti.image_url "
                + "    FROM RoomTypeImages rti "
                + "    WHERE rti.room_type_id = rt.room_type_id "
                + "    ORDER BY rti.image_id ASC "
                + ") img "
                + "WHERE rt.is_active = 1 ";

        if (roomTypeId != null && !roomTypeId.equals("all")) {
            sql += "AND rt.room_type_id = ? ";
        }

        sql += "AND ( "
                + "    (SELECT COUNT(*) "
                + "     FROM Rooms r "
                + "     WHERE r.room_type_id = rt.room_type_id "
                + "     AND r.[status] != N'Đang bảo trì') "
                + "    - "
                + "    ISNULL(( "
                + "        SELECT SUM(b.num_rooms) "
                + "        FROM Bookings b "
                + "        WHERE b.room_type_id = rt.room_type_id "
                + "        AND b.[status] != N'Đã hủy' "
                + "        AND NOT (b.checkout_date <= ? OR b.checkin_date >= ?) "
                + "        AND ( "
                + "            b.[status] != N'Chờ xử lý' "
                + "            OR ISNULL(b.[source], '') != N'Đặt phòng trực tuyến' "
                + "            OR DATEADD(MINUTE, ?, b.created_at) > GETDATE() "
                + "            OR EXISTS ( "
                + "                SELECT 1 "
                + "                FROM DepositPayments dp "
                + "                WHERE dp.booking_id = b.booking_id "
                + "            ) "
                + "        ) "
                + "    ), 0) "
                + ") >= ? "
                + "ORDER BY rt.room_type_id ASC";

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = connection.prepareStatement(sql);
            int index = 1;

            if (roomTypeId != null && !roomTypeId.equals("all")) {
                ps.setInt(index++, Integer.parseInt(roomTypeId));
            }

            ps.setString(index++, checkIn);
            ps.setString(index++, checkOut);
            ps.setInt(index++, 15);
            ps.setInt(index++, roomQuantity);

            rs = ps.executeQuery();

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

                String firstImage = rs.getString("firstImageUrl");

                if (firstImage != null && !firstImage.trim().isEmpty()) {
                    rt.addImage(firstImage, "");
                }

                list.add(rt);
            }

        } catch (Exception e) {
            System.out.println("searchRoomTypesByQuantity: " + e.getMessage());
            e.printStackTrace();

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }

                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    // 1. Lấy toàn bộ danh sách phòng phục vụ trang quản trị Manager (Đã đồng bộ map vào List<RoomAmenity>)
    public List<RoomType> getAllRoomTypesForManager() {
        List<RoomType> list = new ArrayList<>();

        String sqlRoom = "SELECT room_type_id, type_name, description, capacity, bed_type, bed_count, area_sqm, base_price, is_active "
                + "FROM RoomTypes "
                + "ORDER BY room_type_id ASC";

        String sqlImages = "SELECT image_url FROM RoomTypeImages WHERE room_type_id = ?";

        String sqlServices = "SELECT rts.room_type_service_id, rts.service_id, rts.quantity, rts.is_free, s.service_name, s.unit_price "
                + "FROM RoomTypeServices rts "
                + "LEFT JOIN RoomServices s ON rts.service_id = s.service_id "
                + "WHERE rts.room_type_id = ?";

        // CHỐT SỬA LUỒNG AMENITIES: JOIN thẳng bảng liên kết và bảng gốc để lấy dữ liệu tiện nghi
        String sqlAmenities = "SELECT rta.quantity, ra.amenity_id, ra.amenity_name, ra.unit_price "
                + "FROM RoomTypeAmenities rta "
                + "INNER JOIN RoomAmenities ra ON rta.amenity_id = ra.amenity_id "
                + "WHERE rta.room_type_id = ?";

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

                    List<String> imagesList = new ArrayList<>();

                    try (PreparedStatement psImg
                            = connection.prepareStatement(sqlImages)) {

                        psImg.setInt(1, roomTypeId);

                        try (ResultSet rsImg = psImg.executeQuery()) {
                            while (rsImg.next()) {
                                imagesList.add(
                                        rsImg.getString("image_url")
                                );
                            }
                        }
                    }

                    rt.setImageUrl(imagesList);

                    // --- LUỒNG LẤY DANH SÁCH DỊCH VỤ ĐI KÈM ---
                    List<model.RoomTypeService> servicesList = new ArrayList<>();
                    try (PreparedStatement psSer = connection.prepareStatement(sqlServices)) {
                        psSer.setInt(1, roomTypeId);

                        try (ResultSet rsSer = psSer.executeQuery()) {
                            while (rsSer.next()) {
                                if (rsSer.getObject("service_id") != null) {
                                    model.RoomTypeService rts = new model.RoomTypeService();
                                    rts.setRoomTypeServiceId(rsSer.getInt("room_type_service_id"));
                                    rts.setServiceId(rsSer.getInt("service_id")); // THÊM DÒNG NÀY
                                    rts.setQuantity(rsSer.getInt("quantity"));
                                    rts.setIsFree(rsSer.getInt("is_free"));

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

                    // Gán danh sách dịch vụ vào loại phòng để đưa ra JSP
                    rt.setRoomTypeServices(servicesList);

                    // --- LUỒNG LẤY DANH SÁCH TIỆN NGHI (Khớp chuẩn List<RoomAmenity> trong Model của Vũ) ---
                    List<model.RoomAmenity> amenitiesList = new ArrayList<>();
                    try (PreparedStatement psAmen = connection.prepareStatement(sqlAmenities)) {
                        psAmen.setInt(1, roomTypeId);
                        try (ResultSet rsAmen = psAmen.executeQuery()) {
                            while (rsAmen.next()) {
                                model.RoomAmenity ra = new model.RoomAmenity();
                                ra.setAmenityId(rsAmen.getInt("amenity_id"));
                                ra.setAmenityName(rsAmen.getString("amenity_name"));
                                ra.setUnitPrice(rsAmen.getBigDecimal("unit_price"));

                                /* MẸO CHIẾN THUẬT: Nhét số lượng quantity từ bảng trung gian vào trường description 
                                   để đem ra ngoài JSP hiển thị mà hoàn toàn không cần sửa file Model */
                                ra.setDescription(String.valueOf(rsAmen.getInt("quantity")));
                                ra.setActive(true);

                                amenitiesList.add(ra);
                            }
                        }
                    }
                    rt.setRoomAmenities(amenitiesList); // Gán trực tiếp vào List<RoomAmenity> gốc của Vũ

                    list.add(rt);
                }
            }

        } catch (Exception e) {
            System.out.println(
                    ">>> LỖI LOGIC TẠI getAllRoomTypesForManager: "
                    + e.getMessage()
            );
            e.printStackTrace();
        }

        return list;
    }

    public boolean insertRoomType(RoomType rt, List<String> imageList, List<model.RoomTypeService> serviceList, List<model.RoomAmenity> amenityList) {
        String insertRoomTypeSql = "INSERT INTO RoomTypes (type_name, description, capacity, bed_type, bed_count, area_sqm, base_price, is_active) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String insertImageSql = "INSERT INTO RoomTypeImages (room_type_id, image_url) VALUES (?, ?)";
        String insertServiceSql = "INSERT INTO RoomTypeServices (room_type_id, service_id, quantity, is_free) VALUES (?, ?, ?, ?)";
        String insertAmenitySql = "INSERT INTO RoomTypeAmenities (room_type_id, amenity_id, quantity) VALUES (?, ?, ?)";

        PreparedStatement psRoom = null;
        PreparedStatement psImg = null;
        PreparedStatement psSer = null;
        PreparedStatement psAmen = null;
        ResultSet generatedKeys = null;

        try {
            if (connection == null) {
                System.out.println(">>> DAO ERROR: Connection dang NULL tai insertRoomType!");
                return false;
            }

            connection.setAutoCommit(false); // Kích hoạt Transaction hóa bảo mật dữ liệu

            psRoom = connection.prepareStatement(
                    insertRoomTypeSql,
                    Statement.RETURN_GENERATED_KEYS
            );

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
                // Chèn album ảnh
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

                // Chèn danh sách dịch vụ phòng
                if (serviceList != null && !serviceList.isEmpty()) {
                    psSer = connection.prepareStatement(insertServiceSql);
                    for (model.RoomTypeService rts : serviceList) {
                        psSer.setInt(1, generatedId);
                        psSer.setInt(2, rts.getServiceId());
                        psSer.setInt(3, rts.getQuantity());
                        psSer.setInt(4, rts.getIsFree());
                        psSer.addBatch();
                    }
                    psSer.executeBatch();
                }

                // Chèn danh sách tiện nghi phòng
                if (amenityList != null && !amenityList.isEmpty()) {
                    psAmen = connection.prepareStatement(insertAmenitySql);
                    for (model.RoomAmenity ra : amenityList) {
                        psAmen.setInt(1, generatedId);
                        psAmen.setInt(2, ra.getAmenityId());

                        int qty = 1;
                        try {
                            qty = Integer.parseInt(ra.getDescription());
                        } catch (Exception ex) {
                        }

                        psAmen.setInt(3, qty);
                        psAmen.addBatch();
                    }
                    psAmen.executeBatch();
                }

                connection.commit();
                return true;

            } else {
                throw new SQLException("Creating room type failed, no ID obtained.");
            }

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
                if (psAmen != null) {
                    psAmen.close();
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

    // 3. Cập nhật thông tin chi tiết loại phòng, dọn sạch và ghi đè danh sách mới
    public boolean updateRoomType(RoomType rt, List<String> newImageList, List<model.RoomTypeService> newServiceList, List<model.RoomAmenity> newAmenityList) {
        String updateRoomTypeSql = "UPDATE RoomTypes SET type_name = ?, description = ?, capacity = ?, bed_type = ?, "
                + "bed_count = ?, area_sqm = ?, base_price = ?, is_active = ? "
                + "WHERE room_type_id = ?";
        String deleteOldImagesSql = "DELETE FROM RoomTypeImages WHERE room_type_id = ?";
        String insertImageSql = "INSERT INTO RoomTypeImages (room_type_id, image_url) VALUES (?, ?)";

        String deleteOldServicesSql = "DELETE FROM RoomTypeServices WHERE room_type_id = ?";
        String insertServiceSql = "INSERT INTO RoomTypeServices (room_type_id, service_id, quantity, is_free) VALUES (?, ?, ?, ?)";

        String deleteOldAmenitiesSql = "DELETE FROM RoomTypeAmenities WHERE room_type_id = ?";
        String insertAmenitySql = "INSERT INTO RoomTypeAmenities (room_type_id, amenity_id, quantity) VALUES (?, ?, ?)";

        PreparedStatement psRoom = null;
        PreparedStatement psDelImg = null;
        PreparedStatement psInsImg = null;
        PreparedStatement psDelSer = null;
        PreparedStatement psInsSer = null;
        PreparedStatement psDelAmen = null;
        PreparedStatement psInsAmen = null;

        try {
            connection.setAutoCommit(false);

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

            // Làm sạch và ghi đè ảnh
            psDelImg = connection.prepareStatement(deleteOldImagesSql);
            psDelImg.setInt(1, rt.getRoomTypeId());
            psDelImg.executeUpdate();

            if (newImageList != null
                    && !newImageList.isEmpty()) {

                psInsImg = connection.prepareStatement(
                        insertImageSql
                );

                for (String imgUrl : newImageList) {
                    if (imgUrl != null && !imgUrl.trim().isEmpty()) {
                        psInsImg.setInt(1, rt.getRoomTypeId());
                        psInsImg.setString(2, imgUrl.trim());
                        psInsImg.addBatch();
                    }
                }

                psInsImg.executeBatch();
            }

            // Làm sạch và ghi đè dịch vụ
            psDelSer = connection.prepareStatement(deleteOldServicesSql);
            psDelSer.setInt(1, rt.getRoomTypeId());
            psDelSer.executeUpdate();

            if (newServiceList != null && !newServiceList.isEmpty()) {
                psInsSer = connection.prepareStatement(insertServiceSql);
                for (model.RoomTypeService rts : newServiceList) {
                    psInsSer.setInt(1, rt.getRoomTypeId());
                    psInsSer.setInt(2, rts.getServiceId());
                    psInsSer.setInt(3, rts.getQuantity());
                    psInsSer.setInt(4, rts.getIsFree());
                    psInsSer.addBatch();
                }

                psInsSer.executeBatch();
            }

            // Làm sạch và ghi đè mảng tiện nghi mặc định mới
            psDelAmen = connection.prepareStatement(deleteOldAmenitiesSql);
            psDelAmen.setInt(1, rt.getRoomTypeId());
            psDelAmen.executeUpdate();

            if (newAmenityList != null && !newAmenityList.isEmpty()) {
                psInsAmen = connection.prepareStatement(insertAmenitySql);
                for (model.RoomAmenity ra : newAmenityList) {
                    psInsAmen.setInt(1, rt.getRoomTypeId());
                    psInsAmen.setInt(2, ra.getAmenityId());

                    int qty = 1;
                    try {
                        qty = Integer.parseInt(ra.getDescription());
                    } catch (Exception ex) {
                    }

                    psInsAmen.setInt(3, qty);
                    psInsAmen.addBatch();
                }
                psInsAmen.executeBatch();
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
                if (psDelAmen != null) {
                    psDelAmen.close();
                }
                if (psInsAmen != null) {
                    psInsAmen.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    // 4. Xóa mềm loại phòng (is_active = 0)
    public boolean deleteRoomType(int roomTypeId) {
        String sql
                = "UPDATE RoomTypes SET is_active = 0 "
                + "WHERE room_type_id = ?";

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

    public boolean isRoomTypeNameExist(String typeName) {
        // Nếu chuỗi rỗng thì coi như không tồn tại, để bộ lọc required của form tự xử lý
        if (typeName == null || typeName.trim().isEmpty()) {
            return false;
        }

        // Sử dụng LOWER và TRIM ở cả 2 đầu để bọc lót, tránh việc Admin cố tình gõ cách hoặc viết hoa chữ cái đầu
        String sql = "SELECT COUNT(*) FROM RoomTypes WHERE LOWER(TRIM(type_name)) = LOWER(TRIM(?))";
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Kiểm tra an toàn xem connection của class DAO có đang hoạt động không
            if (connection == null) {
                System.out.println(">>> DAO ERROR: Connection đang bị NULL tại isRoomTypeNameExist!");
                return false;
            }

            ps = connection.prepareStatement(sql);
            ps.setString(1, typeName.trim());
            rs = ps.executeQuery();

            if (rs.next()) {
                // Nếu kết quả COUNT(*) trả về lớn hơn 0, nghĩa là tên này đã bị trùng trong DB
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            System.out.println(">>> LỖI LOGIC TẠI HÀM isRoomTypeNameExist: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Luôn luôn đóng ResultSet và PreparedStatement để giải phóng tài nguyên cho SQL Server
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean isRoomTypeNameExistForEdit(String typeName, int currentRoomTypeId) {
        if (typeName == null || typeName.trim().isEmpty()) {
            return false;
        }

        // THÊM: AND room_type_id != ? để bỏ qua chính nó khi quét trùng
        String sql = "SELECT COUNT(*) FROM RoomTypes WHERE LOWER(TRIM(type_name)) = LOWER(TRIM(?)) AND room_type_id != ?";
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            if (connection == null) {
                System.out.println(">>> DAO ERROR: Connection đang bị NULL tại isRoomTypeNameExistForEdit!");
                return false;
            }

            ps = connection.prepareStatement(sql);
            ps.setString(1, typeName.trim());
            ps.setInt(2, currentRoomTypeId); // Khóa ID hiện tại lại
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            System.out.println(">>> LỖI LOGIC TẠI HÀM isRoomTypeNameExistForEdit: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // =========================================================================
    // 6. LẤY CHI TIẾT MỘT HẠNG PHÒNG THEO ID (Bắt buộc phải có để gánh luồng Edit doGet)
    public RoomType getRoomTypeById(int roomTypeId) {
        String sqlRoom = "SELECT room_type_id, type_name, description, capacity, bed_type, bed_count, area_sqm, base_price, is_active "
                + "FROM RoomTypes WHERE room_type_id = ?";

        String sqlImages = "SELECT image_url FROM RoomTypeImages WHERE room_type_id = ? ORDER BY image_id ASC";

        String sqlServices = "SELECT rts.room_type_service_id, rts.service_id, rts.quantity, rts.is_free, s.service_name, s.unit_price "
                + "FROM RoomTypeServices rts "
                + "LEFT JOIN RoomServices s ON rts.service_id = s.service_id "
                + "WHERE rts.room_type_id = ?";

        String sqlAmenities = "SELECT rta.quantity, ra.amenity_id, ra.amenity_name, ra.unit_price "
                + "FROM RoomTypeAmenities rta "
                + "INNER JOIN RoomAmenities ra ON rta.amenity_id = ra.amenity_id "
                + "WHERE rta.room_type_id = ?";

        PreparedStatement psRoom = null;
        ResultSet rsRoom = null;

        try {
            if (connection == null) {
                System.out.println(">>> DAO ERROR: Connection đang bị NULL tại getRoomTypeById!");
                return null;
            }

            psRoom = connection.prepareStatement(sqlRoom);
            psRoom.setInt(1, roomTypeId);
            rsRoom = psRoom.executeQuery();

            if (rsRoom.next()) {
                RoomType rt = new RoomType();
                rt.setRoomTypeId(rsRoom.getInt("room_type_id"));
                rt.setTypeName(rsRoom.getString("type_name"));
                rt.setDescription(rsRoom.getString("description"));
                rt.setCapacity(rsRoom.getInt("capacity"));
                rt.setBedType(rsRoom.getString("bed_type"));
                rt.setBedCount(rsRoom.getInt("bed_count"));
                rt.setAreaSqm(rsRoom.getBigDecimal("area_sqm"));
                rt.setBasePrice(rsRoom.getBigDecimal("base_price"));
                rt.setActive(rsRoom.getBoolean("is_active"));

                // --- ĐỌC DANH SÁCH ẢNH TRUYỀN VÀO OBJECT ---
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

                // --- ĐỌC DANH SÁCH DỊCH VỤ ĐI KÈM ---
                List<model.RoomTypeService> servicesList = new ArrayList<>();
                try (PreparedStatement psSer = connection.prepareStatement(sqlServices)) {
                    psSer.setInt(1, roomTypeId);
                    try (ResultSet rsSer = psSer.executeQuery()) {
                        while (rsSer.next()) {
                            if (rsSer.getObject("service_id") != null) {
                                model.RoomTypeService rts = new model.RoomTypeService();
                                rts.setRoomTypeServiceId(rsSer.getInt("room_type_service_id"));
                                rts.setServiceId(rsSer.getInt("service_id")); // THÊM DÒNG NÀY
                                rts.setQuantity(rsSer.getInt("quantity"));
                                rts.setIsFree(rsSer.getInt("is_free"));

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

                // --- ĐỌC DANH SÁCH TIỆN NGHI ĐI KÈM (Mẹo nhét qty vào description để mang ra JSP hiển thị) ---
                List<model.RoomAmenity> amenitiesList = new ArrayList<>();
                try (PreparedStatement psAmen = connection.prepareStatement(sqlAmenities)) {
                    psAmen.setInt(1, roomTypeId);
                    try (ResultSet rsAmen = psAmen.executeQuery()) {
                        while (rsAmen.next()) {
                            model.RoomAmenity ra = new model.RoomAmenity();
                            ra.setAmenityId(rsAmen.getInt("amenity_id"));
                            ra.setAmenityName(rsAmen.getString("amenity_name"));
                            ra.setUnitPrice(rsAmen.getBigDecimal("unit_price"));
                            ra.setDescription(String.valueOf(rsAmen.getInt("quantity"))); // Ép số lượng thành chuỗi
                            ra.setActive(true);

                            amenitiesList.add(ra);
                        }
                    }
                }
                rt.setRoomAmenities(amenitiesList);

                return rt; // Trả về thực thể hạng phòng đầy đủ bộ phận cấu thành
            }
        } catch (Exception e) {
            System.out.println(">>> LỖI KHÔNG LẤY ĐƯỢC CHI TIẾT HẠNG PHÒNG THEO ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rsRoom != null) {
                    rsRoom.close();
                }
                if (psRoom != null) {
                    psRoom.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // =========================================================================
    // PUBLIC ROOM DETAIL: Lấy đầy đủ dữ liệu hạng phòng đang hoạt động để hiển thị cho khách
    public RoomType getRoomDetailById(int roomTypeId) {
        String sqlRoom = "SELECT room_type_id, type_name, description, capacity, bed_type, bed_count, area_sqm, base_price, is_active "
                + "FROM RoomTypes WHERE room_type_id = ? AND is_active = 1";

        String sqlImages = "SELECT image_url FROM RoomTypeImages WHERE room_type_id = ? ORDER BY image_id ASC";

        String sqlServices = "SELECT rts.room_type_service_id, rts.service_id, rts.quantity, rts.is_free, s.service_name, s.unit_price "
                + "FROM RoomTypeServices rts "
                + "LEFT JOIN RoomServices s ON rts.service_id = s.service_id "
                + "WHERE rts.room_type_id = ?";

        String sqlAmenities = "SELECT rta.quantity, ra.amenity_id, ra.amenity_name, ra.unit_price "
                + "FROM RoomTypeAmenities rta "
                + "INNER JOIN RoomAmenities ra ON rta.amenity_id = ra.amenity_id "
                + "WHERE rta.room_type_id = ?";

        PreparedStatement psRoom = null;
        ResultSet rsRoom = null;

        try {
            if (connection == null) {
                System.out.println(">>> DAO ERROR: Connection đang bị NULL tại getRoomDetailById!");
                return null;
            }

            psRoom = connection.prepareStatement(sqlRoom);
            psRoom.setInt(1, roomTypeId);
            rsRoom = psRoom.executeQuery();

            if (rsRoom.next()) {
                RoomType rt = new RoomType();
                rt.setRoomTypeId(rsRoom.getInt("room_type_id"));
                rt.setTypeName(rsRoom.getString("type_name"));
                rt.setDescription(rsRoom.getString("description"));
                rt.setCapacity(rsRoom.getInt("capacity"));
                rt.setBedType(rsRoom.getString("bed_type"));
                rt.setBedCount(rsRoom.getInt("bed_count"));
                rt.setAreaSqm(rsRoom.getBigDecimal("area_sqm"));
                rt.setBasePrice(rsRoom.getBigDecimal("base_price"));
                rt.setActive(rsRoom.getBoolean("is_active"));

                // --- ĐỌC DANH SÁCH ẢNH CỦA PUBLIC ROOM DETAIL ---
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

                // --- ĐỌC TẤT CẢ DỊCH VỤ, TỔNG SỐ LƯỢNG VÀ SỐ LƯỢNG MIỄN PHÍ ---
                List<model.RoomTypeService> servicesList = new ArrayList<>();
                try (PreparedStatement psSer = connection.prepareStatement(sqlServices)) {
                    psSer.setInt(1, roomTypeId);
                    try (ResultSet rsSer = psSer.executeQuery()) {
                        while (rsSer.next()) {
                            if (rsSer.getObject("service_id") != null) {
                                model.RoomTypeService rts = new model.RoomTypeService();
                                rts.setRoomTypeServiceId(rsSer.getInt("room_type_service_id"));
                                rts.setServiceId(rsSer.getInt("service_id"));
                                rts.setQuantity(rsSer.getInt("quantity"));
                                rts.setIsFree(rsSer.getInt("is_free"));

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

                // --- ĐỌC DANH SÁCH TIỆN NGHI CỦA PUBLIC ROOM DETAIL ---
                List<model.RoomAmenity> amenitiesList = new ArrayList<>();
                try (PreparedStatement psAmen = connection.prepareStatement(sqlAmenities)) {
                    psAmen.setInt(1, roomTypeId);
                    try (ResultSet rsAmen = psAmen.executeQuery()) {
                        while (rsAmen.next()) {
                            model.RoomAmenity ra = new model.RoomAmenity();
                            ra.setAmenityId(rsAmen.getInt("amenity_id"));
                            ra.setAmenityName(rsAmen.getString("amenity_name"));
                            ra.setUnitPrice(rsAmen.getBigDecimal("unit_price"));
                            ra.setDescription(String.valueOf(rsAmen.getInt("quantity")));
                            ra.setActive(true);

                            amenitiesList.add(ra);
                        }
                    }
                }
                rt.setRoomAmenities(amenitiesList);

                return rt;
            }
        } catch (Exception e) {
            System.out.println(">>> LỖI KHÔNG LẤY ĐƯỢC DỮ LIỆU PUBLIC ROOM DETAIL: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rsRoom != null) {
                    rsRoom.close();
                }
                if (psRoom != null) {
                    psRoom.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // 7. Đếm số phòng còn trống theo hạng phòng và khoảng ngày (phục vụ public Room Detail)
    // Đếm số phòng còn trống theo hạng phòng và khoảng ngày
    public int getAvailableRoomCount(int roomTypeId, String checkIn, String checkOut) {
        int availableRooms = 0;

        String sql = "SELECT ( "
                + "    (SELECT COUNT(*) "
                + "     FROM Rooms r "
                + "     WHERE r.room_type_id = ? "
                + "     AND r.[status] != N'Đang bảo trì') "
                + "    - "
                + "    ISNULL(( "
                + "        SELECT SUM(b.num_rooms) "
                + "        FROM Bookings b "
                + "        WHERE b.room_type_id = ? "
                + "        AND b.[status] != N'Đã hủy' "
                + "        AND NOT (b.checkout_date <= ? OR b.checkin_date >= ?) "
                + "        AND ( "
                + "            b.[status] != N'Chờ xử lý' "
                + "            OR ISNULL(b.[source], '') != N'Đặt phòng trực tuyến' "
                + "            OR DATEADD(MINUTE, ?, b.created_at) > GETDATE() "
                + "            OR EXISTS ( "
                + "                SELECT 1 "
                + "                FROM DepositPayments dp "
                + "                WHERE dp.booking_id = b.booking_id "
                + "            ) "
                + "        ) "
                + "    ), 0) "
                + ") AS available_rooms";

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            if (connection == null) {
                System.out.println("getAvailableRoomCount: Connection đang bị null.");
                return 0;
            }

            ps = connection.prepareStatement(sql);
            ps.setInt(1, roomTypeId);
            ps.setInt(2, roomTypeId);
            ps.setString(3, checkIn);
            ps.setString(4, checkOut);
            ps.setInt(5, HOLD_MINUTES);

            rs = ps.executeQuery();

            if (rs.next()) {
                availableRooms = rs.getInt("available_rooms");
            }

        } catch (Exception e) {
            System.out.println("getAvailableRoomCount: " + e.getMessage());
            e.printStackTrace();

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }

                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return Math.max(availableRooms, 0);
    }

}
