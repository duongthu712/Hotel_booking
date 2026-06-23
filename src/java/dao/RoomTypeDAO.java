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
        if (connection == null) {
            System.out.println(" DAO ERROR: Connection dang bi NULL!");
            return list;
        }

        String sqlRoom = "SELECT * FROM RoomTypes ORDER BY room_type_id ASC";
        String sqlImages = "SELECT image_url FROM RoomTypeImages WHERE room_type_id = ?";
        String sqlServices = "SELECT rts.room_type_service_id, rts.service_id, rts.quantity, rts.is_free, s.service_name, s.unit_price "
                + "FROM RoomTypeServices rts LEFT JOIN RoomServices s ON rts.service_id = s.service_id WHERE rts.room_type_id = ?";
        String sqlAmenities = "SELECT rta.quantity, ra.amenity_id, ra.amenity_name, ra.unit_price "
                + "FROM RoomTypeAmenities rta INNER JOIN RoomAmenities ra ON rta.amenity_id = ra.amenity_id WHERE rta.room_type_id = ?";

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

                // Lấy Images
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

                // Lấy Services
                List<model.RoomTypeService> servicesList = new ArrayList<>();
                try (PreparedStatement psSer = connection.prepareStatement(sqlServices)) {
                    psSer.setInt(1, roomTypeId);
                    try (ResultSet rsSer = psSer.executeQuery()) {
                        while (rsSer.next()) {
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
                rt.setRoomTypeServices(servicesList);

                // Lấy Amenities
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
                list.add(rt);
            }
        } catch (SQLException e) {
            System.out.println(">>> LỖI LOGIC TẠI getAllRoomTypesForManager: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // 2. Thêm loại phòng mới
    public boolean insertRoomType(RoomType rt, List<String> imageList, List<model.RoomTypeService> serviceList, List<model.RoomAmenity> amenityList) throws Exception {
        String insertRoomSql = "INSERT INTO RoomTypes (type_name, description, capacity, bed_type, bed_count, area_sqm, base_price, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        // Tắt tự động lưu để quản lý giao dịch
        connection.setAutoCommit(false);
        // 1. Insert RoomType
        PreparedStatement psRoom = connection.prepareStatement(insertRoomSql, Statement.RETURN_GENERATED_KEYS);
        psRoom.setString(1, rt.getTypeName());
        psRoom.setString(2, rt.getDescription());
        psRoom.setInt(3, rt.getCapacity());
        psRoom.setString(4, rt.getBedType());
        psRoom.setInt(5, rt.getBedCount());
        psRoom.setBigDecimal(6, rt.getAreaSqm());
        psRoom.setBigDecimal(7, rt.getBasePrice());
        psRoom.setBoolean(8, rt.isActive());
        psRoom.executeUpdate();
        // Lấy ID vừa tạo
        int generatedId = 0;
        ResultSet rs = psRoom.getGeneratedKeys();
        if (rs.next()) {
            generatedId = rs.getInt(1);
        }
        // 2. Insert Ảnh
        if (imageList != null) {
            PreparedStatement psImg = connection.prepareStatement("INSERT INTO RoomTypeImages (room_type_id, image_url) VALUES (?, ?)");
            for (String url : imageList) {
                psImg.setInt(1, generatedId);
                psImg.setString(2, url);
                psImg.executeUpdate();
            }
        }
        // 3. Insert Dịch vụ
        if (serviceList != null) {
            PreparedStatement psSer = connection.prepareStatement("INSERT INTO RoomTypeServices (room_type_id, service_id, quantity, is_free) VALUES (?, ?, ?, ?)");
            for (model.RoomTypeService rts : serviceList) {
                psSer.setInt(1, generatedId);
                psSer.setInt(2, rts.getServiceId());
                psSer.setInt(3, rts.getQuantity());
                psSer.setInt(4, rts.getIsFree());
                psSer.executeUpdate();
            }
        }
        // 4. Insert Tiện nghi
        if (amenityList != null) {
            PreparedStatement psAmen = connection.prepareStatement("INSERT INTO RoomTypeAmenities (room_type_id, amenity_id, quantity) VALUES (?, ?, ?)");
            for (model.RoomAmenity ra : amenityList) {
                psAmen.setInt(1, generatedId);
                psAmen.setInt(2, ra.getAmenityId());
                psAmen.setInt(3, Integer.parseInt(ra.getDescription()));
                psAmen.executeUpdate();
            }
        }
        // Hoàn tất giao dịch
        connection.commit();
        connection.setAutoCommit(true);
        return true;
    }

    // 3. Cập nhật thông tin chi tiết loại phòng, dọn sạch và ghi đè danh sách mới
    public boolean updateRoomType(RoomType rt, List<String> newImageList, List<model.RoomTypeService> newServiceList, List<model.RoomAmenity> newAmenityList) {
        String updateRoomTypeSql = "UPDATE RoomTypes SET type_name = ?, description = ?, capacity = ?, bed_type = ?, bed_count = ?, area_sqm = ?, base_price = ?, is_active = ? WHERE room_type_id = ?";
        String deleteOldImagesSql = "DELETE FROM RoomTypeImages WHERE room_type_id = ?";
        String insertImageSql = "INSERT INTO RoomTypeImages (room_type_id, image_url) VALUES (?, ?)";
        String deleteOldServicesSql = "DELETE FROM RoomTypeServices WHERE room_type_id = ?";
        String insertServiceSql = "INSERT INTO RoomTypeServices (room_type_id, service_id, quantity, is_free) VALUES (?, ?, ?, ?)";
        String deleteOldAmenitiesSql = "DELETE FROM RoomTypeAmenities WHERE room_type_id = ?";
        String insertAmenitySql = "INSERT INTO RoomTypeAmenities (room_type_id, amenity_id, quantity) VALUES (?, ?, ?)";
        try {
            connection.setAutoCommit(false); // Bắt đầu giao dịch

            // 1. Update thông tin chính của hạng phòng
            try (PreparedStatement ps = connection.prepareStatement(updateRoomTypeSql)) {
                ps.setString(1, rt.getTypeName());
                ps.setString(2, rt.getDescription());
                ps.setInt(3, rt.getCapacity());
                ps.setString(4, rt.getBedType());
                ps.setInt(5, rt.getBedCount());
                ps.setBigDecimal(6, rt.getAreaSqm());
                ps.setBigDecimal(7, rt.getBasePrice());
                ps.setBoolean(8, rt.isActive());
                ps.setInt(9, rt.getRoomTypeId());
                ps.executeUpdate();
            }

            // 2. Làm sạch dữ liệu cũ (Xóa theo thứ tự)
            connection.prepareStatement("DELETE FROM RoomTypeImages WHERE room_type_id = " + rt.getRoomTypeId()).executeUpdate();
            connection.prepareStatement("DELETE FROM RoomTypeServices WHERE room_type_id = " + rt.getRoomTypeId()).executeUpdate();
            connection.prepareStatement("DELETE FROM RoomTypeAmenities WHERE room_type_id = " + rt.getRoomTypeId()).executeUpdate();

            // 3. Ghi đè danh sách mới (Check null cẩn thận)
            if (newImageList != null) {
                try (PreparedStatement ps = connection.prepareStatement("INSERT INTO RoomTypeImages (room_type_id, image_url) VALUES (?, ?)")) {
                    for (String url : newImageList) {
                        if (url != null && !url.trim().isEmpty()) {
                            ps.setInt(1, rt.getRoomTypeId());
                            ps.setString(2, url.trim());
                            ps.addBatch();
                        }
                    }
                    ps.executeBatch();
                }
            }

            if (newServiceList != null) {
                try (PreparedStatement ps = connection.prepareStatement("INSERT INTO RoomTypeServices (room_type_id, service_id, quantity, is_free) VALUES (?, ?, ?, ?)")) {
                    for (model.RoomTypeService rts : newServiceList) {
                        ps.setInt(1, rt.getRoomTypeId());
                        ps.setInt(2, rts.getServiceId());
                        ps.setInt(3, rts.getQuantity());
                        ps.setInt(4, rts.getIsFree());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            if (newAmenityList != null) {
                try (PreparedStatement ps = connection.prepareStatement("INSERT INTO RoomTypeAmenities (room_type_id, amenity_id, quantity) VALUES (?, ?, ?)")) {
                    for (model.RoomAmenity ra : newAmenityList) {
                        ps.setInt(1, rt.getRoomTypeId());
                        ps.setInt(2, ra.getAmenityId());
                        int qty = 1;
                        try {
                            qty = Integer.parseInt(ra.getDescription());
                        } catch (Exception e) {
                            qty = 1;
                        }
                        ps.setInt(3, qty);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            connection.commit(); // Hoàn tất giao dịch
            return true;
        } catch (Exception e) {
            System.err.println("--- LỖI TẠI DAO UPDATE ---");
            e.printStackTrace(); // Dòng này cực kỳ quan trọng để debug!
            try {
                connection.rollback(); // Quay lại trạng thái cũ nếu có lỗi
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 4. Xóa mềm loại phòng (is_active = 0)
    public boolean deleteRoomType(int roomTypeId) {
        String sql = "UPDATE RoomTypes SET is_active = 0 WHERE room_type_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomTypeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

// 1. Kiểm tra trùng tên khi Thêm mới
    public boolean isRoomTypeNameExist(String typeName) {
        if (typeName == null || typeName.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM RoomTypes WHERE LOWER(TRIM(type_name)) = LOWER(TRIM(?))";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, typeName.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println(" LỖI LOGIC TẠI HÀM isRoomTypeNameExist: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

// 2. Kiểm tra trùng tên khi Sửa (bỏ qua chính nó bằng currentRoomTypeId)
    public boolean isRoomTypeNameExistForEdit(String typeName, int currentRoomTypeId) {
        if (typeName == null || typeName.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM RoomTypes WHERE LOWER(TRIM(type_name)) = LOWER(TRIM(?)) AND room_type_id != ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, typeName.trim());
            ps.setInt(2, currentRoomTypeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println(" LỖI LOGIC TẠI HÀM isRoomTypeNameExistForEdit: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // LẤY CHI TIẾT MỘT HẠNG PHÒNG THEO ID để edit 
    public RoomType getRoomTypeById(int roomTypeId) {
        String sqlRoom = "SELECT * FROM RoomTypes WHERE room_type_id = ?";
        String sqlImages = "SELECT image_url FROM RoomTypeImages WHERE room_type_id = ? ORDER BY image_id ASC";
        String sqlServices = "SELECT rts.*, s.service_name, s.unit_price FROM RoomTypeServices rts LEFT JOIN RoomServices s ON rts.service_id = s.service_id WHERE rts.room_type_id = ?";
        String sqlAmenities = "SELECT rta.quantity, ra.* FROM RoomTypeAmenities rta INNER JOIN RoomAmenities ra ON rta.amenity_id = ra.amenity_id WHERE rta.room_type_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sqlRoom)) {
            ps.setInt(1, roomTypeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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

                    // 1. Lấy Ảnh
                    List<String> images = new ArrayList<>();
                    try (PreparedStatement psImg = connection.prepareStatement(sqlImages)) {
                        psImg.setInt(1, roomTypeId);
                        try (ResultSet rsImg = psImg.executeQuery()) {
                            while (rsImg.next()) {
                                images.add(rsImg.getString("image_url"));
                            }
                        }
                    }
                    rt.setImageUrl(images);

                    // 2. Lấy Dịch vụ
                    List<model.RoomTypeService> services = new ArrayList<>();
                    try (PreparedStatement psSer = connection.prepareStatement(sqlServices)) {
                        psSer.setInt(1, roomTypeId);
                        try (ResultSet rsSer = psSer.executeQuery()) {
                            while (rsSer.next()) {
                                model.RoomTypeService rts = new model.RoomTypeService();
                                rts.setRoomTypeServiceId(rsSer.getInt("room_type_service_id"));
                                rts.setServiceId(rsSer.getInt("service_id"));
                                rts.setQuantity(rsSer.getInt("quantity"));
                                rts.setIsFree(rsSer.getInt("is_free"));
                                model.RoomService s = new model.RoomService();
                                s.setServiceName(rsSer.getString("service_name"));
                                s.setUnitPrice(rsSer.getBigDecimal("unit_price"));
                                rts.setRoomService(s);
                                services.add(rts);
                            }
                        }
                    }
                    rt.setRoomTypeServices(services);

                    // 3. Lấy Tiện nghi
                    List<model.RoomAmenity> amenities = new ArrayList<>();
                    try (PreparedStatement psAmen = connection.prepareStatement(sqlAmenities)) {
                        psAmen.setInt(1, roomTypeId);
                        try (ResultSet rsAmen = psAmen.executeQuery()) {
                            while (rsAmen.next()) {
                                model.RoomAmenity ra = new model.RoomAmenity();
                                ra.setAmenityId(rsAmen.getInt("amenity_id"));
                                ra.setAmenityName(rsAmen.getString("amenity_name"));
                                ra.setDescription(String.valueOf(rsAmen.getInt("quantity")));
                                amenities.add(ra);
                            }
                        }
                    }
                    rt.setRoomAmenities(amenities);
                    return rt;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
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

    // Trả về danh sách Hạng phòng kèm Giá tiền, SỐ LƯỢNG PHÒNG TRỐNG và MÔ TẢ
    public List<RoomType> searchAvailableRoomTypesForWalkIn(String checkIn, String checkOut) {
        List<RoomType> list = new ArrayList<>();

        // 1. Thêm rt.description vào câu SELECT
        String sql = "SELECT rt.room_type_id, rt.type_name, rt.base_price, rt.capacity, rt.description, "
                + "img.image_url AS firstImageUrl, "
                + "avail.available_rooms "
                + "FROM RoomTypes rt "
                + "OUTER APPLY ( "
                + "    SELECT TOP 1 rti.image_url FROM RoomTypeImages rti "
                + "    WHERE rti.room_type_id = rt.room_type_id ORDER BY rti.image_id ASC "
                + ") img "
                + "CROSS APPLY ( "
                + "    SELECT ( "
                + "        (SELECT COUNT(*) FROM Rooms r WHERE r.room_type_id = rt.room_type_id AND r.[status] != N'Đang bảo trì') "
                + "        - "
                + "        ISNULL(( "
                + "            SELECT SUM(b.num_rooms) FROM Bookings b "
                + "            WHERE b.room_type_id = rt.room_type_id "
                + "            AND b.[status] != N'Đã hủy' "
                + "            AND NOT (b.checkout_date <= ? OR b.checkin_date >= ?) "
                + "            AND ( "
                + "                b.[status] != N'Chờ xử lý' "
                + "                OR ISNULL(b.[source], '') != N'Đặt phòng trực tuyến' "
                + "                OR DATEADD(MINUTE, ?, b.created_at) > GETDATE() "
                + "                OR EXISTS (SELECT 1 FROM DepositPayments dp WHERE dp.booking_id = b.booking_id) "
                + "            ) "
                + "        ), 0) "
                + "    ) AS available_rooms "
                + ") avail "
                + "WHERE rt.is_active = 1 AND avail.available_rooms > 0 "
                + "ORDER BY rt.base_price ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, checkOut);
            ps.setString(2, checkIn);
            ps.setInt(3, HOLD_MINUTES);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RoomType rt = new RoomType();
                    rt.setRoomTypeId(rs.getInt("room_type_id"));
                    rt.setTypeName(rs.getString("type_name"));
                    rt.setBasePrice(rs.getBigDecimal("base_price"));
                    rt.setCapacity(rs.getInt("capacity"));

                    // 2. Gán giá trị description lấy từ database
                    rt.setDescription(rs.getString("description"));

                    rt.setAvailableRooms(rs.getInt("available_rooms"));

                    String firstImage = rs.getString("firstImageUrl");
                    if (firstImage != null && !firstImage.trim().isEmpty()) {
                        rt.addImage(firstImage, "");
                    }

                    list.add(rt);
                }
            }
        } catch (Exception e) {
            System.out.println("Lỗi searchAvailableRoomTypesForWalkIn: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

}
