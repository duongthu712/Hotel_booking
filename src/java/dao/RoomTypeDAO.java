package dao;

import dal.DBContext;
import model.RoomAmenity;
import model.RoomService;
import model.RoomType;
import model.RoomTypeService;
<<<<<<< Updated upstream
import model.Service;
import model.ServiceType;
import java.sql.*;
=======

import java.sql.PreparedStatement;
import java.sql.ResultSet;
>>>>>>> Stashed changes
import java.util.ArrayList;
import java.util.List;

public class RoomTypeDAO extends DBContext {

<<<<<<< Updated upstream
    // 1. LẤY TOÀN BỘ DANH SÁCH PHÒNG
    public List<RoomType> getAllRoomTypes() {
        List<RoomType> list = new ArrayList<>();
        String sql = "SELECT rt.room_type_id, rt.type_name, rt.description, rt.capacity, "
                + "       rt.bed_type, rt.bed_count, rt.area_sqm, rt.base_price, rt.is_active, "
                + "       MIN(rti.image_url) as minImageUrl "
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
=======
    public List<RoomType> getAllRoomTypes() {
        List<RoomType> list = new ArrayList<>();

        String sql = """
                     SELECT rt.room_type_id,
                            rt.type_name,
                            rt.description,
                            rt.capacity,
                            rt.bed_type,
                            rt.bed_count,
                            rt.area_sqm,
                            rt.base_price,
                            rt.is_active,
                            img.image_url AS firstImageUrl
                     FROM RoomTypes rt
                     OUTER APPLY (
                         SELECT TOP 1 rti.image_url
                         FROM RoomTypeImages rti
                         WHERE rti.room_type_id = rt.room_type_id
                         ORDER BY rti.image_id ASC
                     ) img
                     WHERE rt.is_active = 1
                     ORDER BY rt.room_type_id ASC
                     """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                RoomType rt = new RoomType();

>>>>>>> Stashed changes
                rt.setRoomTypeId(rs.getInt("room_type_id"));
                rt.setTypeName(rs.getString("type_name"));
                rt.setDescription(rs.getString("description"));
                rt.setCapacity(rs.getInt("capacity"));
                rt.setBedType(rs.getString("bed_type"));
                rt.setBedCount(rs.getInt("bed_count"));
                rt.setAreaSqm(rs.getBigDecimal("area_sqm"));
                rt.setBasePrice(rs.getBigDecimal("base_price"));
                rt.setActive(rs.getBoolean("is_active"));

                String firstImg = rs.getString("firstImageUrl");
                rt.clearImages();

                if (firstImg != null && !firstImg.trim().isEmpty()) {
                    rt.addImage(firstImg, "");
                }

                // Query CHỈ lấy dịch vụ miễn phí (is_free = 1)
                List<RoomTypeService> roomTypeServicesList = new ArrayList<>();
                String sqlService = "SELECT rts.room_type_service_id, rts.room_type_id, rts.service_id, rts.quantity, rts.is_free, "
                        + "       s.service_name, s.[description] as svc_desc, s.unit_price, s.is_active as svc_active "
                        + "FROM RoomTypeServices rts "
                        + "JOIN RoomServices s ON rts.service_id = s.service_id "
                        + "WHERE rts.room_type_id = ? AND rts.is_free = 1";

                PreparedStatement psSvc = connection.prepareStatement(sqlService);
                psSvc.setInt(1, rt.getRoomTypeId());
                ResultSet rsSvc = psSvc.executeQuery();
                while (rsSvc.next()) {
                    Service s = new Service(rsSvc.getInt("service_id"), rsSvc.getString("service_name"), rsSvc.getString("svc_desc"), rsSvc.getBigDecimal("unit_price"), rsSvc.getBoolean("svc_active"), ServiceType.ROOM);
                    RoomTypeService rtsObj = new RoomTypeService(rsSvc.getInt("room_type_service_id"), rsSvc.getInt("room_type_id"), rsSvc.getInt("service_id"), rsSvc.getInt("quantity"), rsSvc.getBoolean("is_free"), s);
                    roomTypeServicesList.add(rtsObj);
                }
                rsSvc.close();
                psSvc.close();
                rt.setRoomTypeServices(roomTypeServicesList);
                list.add(rt);
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

<<<<<<< Updated upstream
    // 2. TÌM KIẾM PHÒNG TRỐNG THEO NGÀY
    public List<RoomType> searchRoomTypesByQuantity(String checkIn, String checkOut, int roomQuantity, String roomTypeId) {
        List<RoomType> list = new ArrayList<>();
        String sql = "SELECT rt.room_type_id, rt.type_name, rt.description, rt.capacity, "
                + "       rt.bed_type, rt.bed_count, rt.area_sqm, rt.base_price, rt.is_active, "
                + "       MIN(rti.image_url) as minImageUrl "
                + "FROM RoomTypes rt "
                + "LEFT JOIN RoomTypeImages rti ON rt.room_type_id = rti.room_type_id "
                + "WHERE rt.is_active = 1 ";
        if (roomTypeId != null && !roomTypeId.equals("all")) {
            sql += " AND rt.room_type_id = ? ";
        }
        sql += " AND (SELECT COUNT(*) FROM Rooms r WHERE r.room_type_id = rt.room_type_id AND r.[status] != N'Đang bảo trì' AND r.room_number NOT IN (SELECT br.room_number FROM BookingRooms br JOIN Bookings b ON br.booking_id = b.booking_id WHERE b.[status] != N'Đã hủy' AND NOT (b.checkout_date <= ? OR b.checkin_date >= ?))) >= ? "
                + " GROUP BY rt.room_type_id, rt.type_name, rt.description, rt.capacity, rt.bed_type, rt.bed_count, rt.area_sqm, rt.base_price, rt.is_active";
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
=======
    public List<RoomType> searchRoomTypesByQuantity(String checkIn, String checkOut, int roomQuantity, String roomTypeId) {
        List<RoomType> list = new ArrayList<>();

        String sql = """
                     SELECT rt.room_type_id,
                            rt.type_name,
                            rt.description,
                            rt.capacity,
                            rt.bed_type,
                            rt.bed_count,
                            rt.area_sqm,
                            rt.base_price,
                            rt.is_active,
                            img.image_url AS firstImageUrl
                     FROM RoomTypes rt
                     OUTER APPLY (
                         SELECT TOP 1 rti.image_url
                         FROM RoomTypeImages rti
                         WHERE rti.room_type_id = rt.room_type_id
                         ORDER BY rti.image_id ASC
                     ) img
                     WHERE rt.is_active = 1
                     """;

        if (roomTypeId != null && !roomTypeId.equals("all")) {
            sql += " AND rt.room_type_id = ? ";
        }

        sql += """
                AND (
                    SELECT COUNT(*)
                    FROM Rooms r
                    WHERE r.room_type_id = rt.room_type_id
                      AND r.[status] != N'Đang bảo trì'
                ) -
                (
                    SELECT ISNULL(SUM(b.num_rooms), 0)
                    FROM Bookings b
                    WHERE b.room_type_id = rt.room_type_id
                      AND b.[status] != N'Đã hủy'
                      AND NOT (b.checkout_date <= ? OR b.checkin_date >= ?)
                ) >= ?
                ORDER BY rt.room_type_id ASC
                """;

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

>>>>>>> Stashed changes
                rt.setRoomTypeId(rs.getInt("room_type_id"));
                rt.setTypeName(rs.getString("type_name"));
                rt.setDescription(rs.getString("description"));
                rt.setCapacity(rs.getInt("capacity"));
                rt.setBedType(rs.getString("bed_type"));
                rt.setBedCount(rs.getInt("bed_count"));
                rt.setAreaSqm(rs.getBigDecimal("area_sqm"));
                rt.setBasePrice(rs.getBigDecimal("base_price"));
                rt.setActive(rs.getBoolean("is_active"));

<<<<<<< Updated upstream
                String minImg = rs.getString("minImageUrl");
                if (minImg != null) {
                    rt.addImage(minImg, "");
                }

                // Query CHỈ lấy dịch vụ miễn phí (is_free = 1)
                List<RoomTypeService> roomTypeServicesList = new ArrayList<>();
                String sqlService = "SELECT rts.room_type_service_id, rts.room_type_id, rts.service_id, rts.quantity, rts.is_free, "
                        + "       s.service_name, s.[description] as svc_desc, s.unit_price, s.is_active as svc_active "
                        + "FROM RoomTypeServices rts "
                        + "JOIN RoomServices s ON rts.service_id = s.service_id "
                        + "WHERE rts.room_type_id = ? AND rts.is_free = 1";
                PreparedStatement psSvc = connection.prepareStatement(sqlService);
                psSvc.setInt(1, rt.getRoomTypeId());
                ResultSet rsSvc = psSvc.executeQuery();
                while (rsSvc.next()) {
                    Service s = new Service(rsSvc.getInt("service_id"), rsSvc.getString("service_name"), rsSvc.getString("svc_desc"), rsSvc.getBigDecimal("unit_price"), rsSvc.getBoolean("svc_active"), ServiceType.ROOM);
                    RoomTypeService rtsObj = new RoomTypeService(rsSvc.getInt("room_type_service_id"), rsSvc.getInt("room_type_id"), rsSvc.getInt("service_id"), rsSvc.getInt("quantity"), rsSvc.getBoolean("is_free"), s);
                    roomTypeServicesList.add(rtsObj);
                }
                rsSvc.close();
                psSvc.close();
                rt.setRoomTypeServices(roomTypeServicesList);
                list.add(rt);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 3. LẤY TOÀN BỘ DANH SÁCH LOẠI PHÒNG KÈM THEO FULL BỘ ẢNH (GALLERY)
    public List<RoomType> getAllRoomTypesForManager() {
        List<RoomType> list = new ArrayList<>();
        String sql = "SELECT * FROM RoomTypes";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RoomType rt = new RoomType(
                    rs.getInt("room_type_id"),
                    rs.getString("type_name"),
                    rs.getString("description"),
                    rs.getInt("capacity"),
                    rs.getString("bed_type"),
                    rs.getInt("bed_count"),
                    rs.getBigDecimal("area_sqm"),
                    rs.getBigDecimal("base_price"),
                    rs.getBoolean("is_active")
                );

                String sqlImg = "SELECT image_url FROM RoomTypeImages WHERE room_type_id = ?";
                PreparedStatement psImg = connection.prepareStatement(sqlImg);
                psImg.setInt(1, rt.getRoomTypeId());
                ResultSet rsImg = psImg.executeQuery();
                while (rsImg.next()) {
                    rt.addImage(rsImg.getString("image_url"), "");
                }
                rsImg.close(); psImg.close();

                list.add(rt);
            }
            rs.close(); ps.close();
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 4. LẤY CHI TIẾT ĐỐI TƯỢNG LOẠI PHÒNG THEO ID KÈM DANH SÁCH ẢNH THẬT
    public RoomType getRoomTypeById(int id) {
        String sql = "SELECT * FROM RoomTypes WHERE room_type_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                RoomType rt = new RoomType(
                    rs.getInt("room_type_id"),
                    rs.getString("type_name"),
                    rs.getString("description"),
                    rs.getInt("capacity"),
                    rs.getString("bed_type"),
                    rs.getInt("bed_count"),
                    rs.getBigDecimal("area_sqm"),
                    rs.getBigDecimal("base_price"),
                    rs.getBoolean("is_active")
                );
                
                String sqlImg = "SELECT image_url FROM RoomTypeImages WHERE room_type_id = ?";
                PreparedStatement psImg = connection.prepareStatement(sqlImg);
                psImg.setInt(1, rt.getRoomTypeId());
                ResultSet rsImg = psImg.executeQuery();
                while (rsImg.next()) {
                    rt.addImage(rsImg.getString("image_url"), "");
                }
                rsImg.close(); psImg.close();
                return rt;
            }
            rs.close(); ps.close();
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // 🔥 5. THÊM MỚI LOẠI PHÒNG: LƯU ĐỒNG THỜI CẢ THÔNG TIN VÀ TOÀN BỘ MẢNG ẢNH
    public boolean insertRoomType(RoomType rt) {
        String sqlRoom = "INSERT INTO RoomTypes (type_name, description, capacity, bed_type, bed_count, area_sqm, base_price, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlImg = "INSERT INTO RoomTypeImages (room_type_id, image_url, caption) VALUES (?, ?, ?)";
        try {
            // Bật cơ chế Transaction kiểm soát dữ liệu kép
            connection.setAutoCommit(false);

            // Thực hiện chèn thông tin phòng chính và yêu cầu trả về ID tự tăng vừa sinh ra
            PreparedStatement psRoom = connection.prepareStatement(sqlRoom, Statement.RETURN_GENERATED_KEYS);
            psRoom.setString(1, rt.getTypeName());
            psRoom.setString(2, rt.getDescription());
            psRoom.setInt(3, rt.getCapacity());
            psRoom.setString(4, rt.getBedType());
            psRoom.setInt(5, rt.getBedCount());
            psRoom.setBigDecimal(6, rt.getAreaSqm());
            psRoom.setBigDecimal(7, rt.getBasePrice());
            psRoom.setBoolean(8, rt.isActive());
            psRoom.executeUpdate();

            // Nhặt ID tự tăng của bản ghi vừa chèn thành công
            ResultSet generatedKeys = psRoom.getGeneratedKeys();
            int newRoomTypeId = 0;
            if (generatedKeys.next()) {
                newRoomTypeId = generatedKeys.getInt(1);
            }
            generatedKeys.close(); psRoom.close();

            // Tiến hành chèn toàn bộ mảng ảnh đi kèm vào bảng liên kết RoomTypeImages
            if (newRoomTypeId > 0 && rt.getImageUrl() != null && !rt.getImageUrl().isEmpty()) {
                PreparedStatement psImg = connection.prepareStatement(sqlImg);
                for (String url : rt.getImageUrl()) {
                    if (url != null && !url.trim().isEmpty()) {
                        psImg.setInt(1, newRoomTypeId);
                        psImg.setString(2, url.trim());
                        psImg.setString(3, "");
                        psImg.addBatch(); // Gom hàng chờ xử lý hàng loạt
                    }
                }
                psImg.executeBatch();
                psImg.close();
            }

            connection.commit(); // Tất cả mượt mà thì chính thức ghi dữ liệu xuống ổ đĩa
            connection.setAutoCommit(true);
            return true;
        } catch (Exception e) {
            try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        }
        return false;
    }

    //  6. CẬP NHẬT LOẠI PHÒNG: UPDATE THÔNG TIN PHÒNG VÀ ĐỔI HẾT BỘ ẢNH MỚI (THAY THẾ ẢNH)
    public boolean updateRoomType(RoomType rt) {
        String sqlRoom = "UPDATE RoomTypes SET type_name = ?, description = ?, capacity = ?, bed_type = ?, bed_count = ?, area_sqm = ?, base_price = ?, is_active = ? WHERE room_type_id = ?";
        String sqlDeleteImgs = "DELETE FROM RoomTypeImages WHERE room_type_id = ?";
        String sqlInsertImgs = "INSERT INTO RoomTypeImages (room_type_id, image_url, caption) VALUES (?, ?, ?)";
        try {
            connection.setAutoCommit(false);

            // 6.1. Cập nhật thông số cơ bản trong bảng RoomTypes
            PreparedStatement psRoom = connection.prepareStatement(sqlRoom);
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
            psRoom.close();

            // 6.2. Xóa sạch toàn bộ ảnh cũ của phòng này để chuẩn bị thay thế bộ ảnh mới
            PreparedStatement psDel = connection.prepareStatement(sqlDeleteImgs);
            psDel.setInt(1, rt.getRoomTypeId());
            psDel.executeUpdate();
            psDel.close();

            // 6.3. Ghi đè toàn bộ mảng liên kết danh sách ảnh mới do Admin sửa đổi
            if (rt.getImageUrl() != null && !rt.getImageUrl().isEmpty()) {
                PreparedStatement psIns = connection.prepareStatement(sqlInsertImgs);
                for (String url : rt.getImageUrl()) {
                    if (url != null && !url.trim().isEmpty()) {
                        psIns.setInt(1, rt.getRoomTypeId());
                        psIns.setString(2, url.trim());
                        psIns.setString(3, "");
                        psIns.addBatch();
                    }
                }
                psIns.executeBatch();
                psIns.close();
            }

            connection.commit(); // Hoàn thành quy trình nghiệp vụ kép an toàn
            connection.setAutoCommit(true);
            return true;
        } catch (Exception e) {
            try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        }
        return false;
    }

    // 7. THAY ĐỔI TRẠNG THÁI ACTIVE / ARCHIVED (XÓA MỀM)
    public boolean toggleStatus(int id, boolean currentStatus) {
        String sql = "UPDATE RoomTypes SET is_active = ? WHERE room_type_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setBoolean(1, !currentStatus);
            ps.setInt(2, id);
            int row = ps.executeUpdate();
            ps.close();
            return row > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
    
=======
                String firstImg = rs.getString("firstImageUrl");
                rt.clearImages();

                if (firstImg != null && !firstImg.trim().isEmpty()) {
                    rt.addImage(firstImg, "");
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

    public RoomType getRoomTypeById(int roomTypeId) {
        RoomType room = null;

        String sql = """
                     SELECT room_type_id,
                            type_name,
                            description,
                            capacity,
                            bed_type,
                            bed_count,
                            area_sqm,
                            base_price,
                            is_active
                     FROM RoomTypes
                     WHERE room_type_id = ?
                       AND is_active = 1
                     """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, roomTypeId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                room = new RoomType();

                room.setRoomTypeId(rs.getInt("room_type_id"));
                room.setTypeName(rs.getString("type_name"));
                room.setDescription(rs.getString("description"));
                room.setCapacity(rs.getInt("capacity"));
                room.setBedType(rs.getString("bed_type"));
                room.setBedCount(rs.getInt("bed_count"));
                room.setAreaSqm(rs.getBigDecimal("area_sqm"));
                room.setBasePrice(rs.getBigDecimal("base_price"));
                room.setActive(rs.getBoolean("is_active"));

                room.setImageUrl(getRoomTypeImages(roomTypeId));
                room.setRoomTypeServices(getRoomTypeServices(roomTypeId));
                room.setRoomAmenities(getRoomTypeAmenities(roomTypeId));
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return room;
    }

    public List<String> getRoomTypeImages(int roomTypeId) {
        List<String> images = new ArrayList<>();

        String sql = """
                     SELECT image_url
                     FROM RoomTypeImages
                     WHERE room_type_id = ?
                     ORDER BY image_id ASC
                     """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, roomTypeId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                images.add(rs.getString("image_url"));
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return images;
    }

    public List<RoomTypeService> getRoomTypeServices(int roomTypeId) {
        List<RoomTypeService> list = new ArrayList<>();

        String sql = """
                     SELECT rts.room_type_service_id,
                            rts.room_type_id,
                            rts.service_id,
                            rts.quantity,
                            rts.is_free,
                            rs.service_name,
                            rs.description,
                            rs.unit_price,
                            rs.is_active
                     FROM RoomTypeServices rts
                     JOIN RoomServices rs ON rts.service_id = rs.service_id
                     WHERE rts.room_type_id = ?
                       AND rs.is_active = 1
                     ORDER BY rts.is_free DESC, rs.service_name ASC
                     """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, roomTypeId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                RoomService service = new RoomService();

                service.setServiceId(rs.getInt("service_id"));
                service.setServiceName(rs.getString("service_name"));
                service.setDescription(rs.getString("description"));
                service.setUnitPrice(rs.getBigDecimal("unit_price"));
                service.setActive(rs.getBoolean("is_active"));

                RoomTypeService rts = new RoomTypeService();

                rts.setRoomTypeServiceId(rs.getInt("room_type_service_id"));
                rts.setRoomTypeId(rs.getInt("room_type_id"));
                rts.setServiceId(rs.getInt("service_id"));
                rts.setQuantity(rs.getInt("quantity"));
                rts.setIsFree(rs.getBoolean("is_free"));
                rts.setRoomService(service);

                list.add(rts);
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<RoomAmenity> getRoomTypeAmenities(int roomTypeId) {
        List<RoomAmenity> list = new ArrayList<>();

        String sql = """
                     SELECT ra.amenity_id,
                            ra.amenity_name,
                            ra.description,
                            ra.unit_price,
                            ra.is_active,
                            rta.quantity
                     FROM RoomTypeAmenities rta
                     JOIN RoomAmenities ra ON rta.amenity_id = ra.amenity_id
                     WHERE rta.room_type_id = ?
                       AND ra.is_active = 1
                     ORDER BY ra.amenity_name ASC
                     """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, roomTypeId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                RoomAmenity amenity = new RoomAmenity();

                amenity.setAmenityId(rs.getInt("amenity_id"));
                amenity.setAmenityName(rs.getString("amenity_name"));
                amenity.setDescription(rs.getString("description"));
                amenity.setUnitPrice(rs.getBigDecimal("unit_price"));
                amenity.setActive(rs.getBoolean("is_active"));

                list.add(amenity);
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public int getTotalRoomCountByType(int roomTypeId) {
        int total = 0;

        String sql = """
                     SELECT COUNT(*) AS total_rooms
                     FROM Rooms
                     WHERE room_type_id = ?
                       AND [status] != N'Đang bảo trì'
                     """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, roomTypeId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                total = rs.getInt("total_rooms");
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return total;
    }

    public int getAvailableRoomCount(int roomTypeId, String checkIn, String checkOut) {
        int available = 0;

        String sql = """
                     SELECT
                         total.total_rooms - booked.booked_rooms AS available_rooms
                     FROM
                     (
                         SELECT COUNT(*) AS total_rooms
                         FROM Rooms
                         WHERE room_type_id = ?
                           AND [status] != N'Đang bảo trì'
                     ) total
                     CROSS JOIN
                     (
                         SELECT ISNULL(SUM(num_rooms), 0) AS booked_rooms
                         FROM Bookings
                         WHERE room_type_id = ?
                           AND [status] != N'Đã hủy'
                           AND NOT (
                               checkout_date <= ?
                               OR checkin_date >= ?
                           )
                     ) booked
                     """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, roomTypeId);
            ps.setInt(2, roomTypeId);
            ps.setString(3, checkIn);
            ps.setString(4, checkOut);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                available = rs.getInt("available_rooms");
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (available < 0) {
            available = 0;
        }

        return available;
    }
>>>>>>> Stashed changes
}
