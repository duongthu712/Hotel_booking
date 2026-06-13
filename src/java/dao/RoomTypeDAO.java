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
                + "      AND NOT (b.checkout_date <= ? "
                + "               OR b.checkin_date >= ?) "
                + " ) "
                + ") >= ? "
                + "ORDER BY rt.room_type_id ASC";

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
    
    //Phần của manager giữ nguyên
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

                    List<RoomTypeService> servicesList
                            = new ArrayList<>();

                    try (PreparedStatement psSer
                            = connection.prepareStatement(sqlServices)) {

                        psSer.setInt(1, roomTypeId);

                        try (ResultSet rsSer = psSer.executeQuery()) {
                            while (rsSer.next()) {
                                if (rsSer.getObject("service_id") != null) {
                                    RoomTypeService rts
                                            = new RoomTypeService();

                                    rts.setRoomTypeServiceId(
                                            rsSer.getInt(
                                                    "room_type_service_id"
                                            )
                                    );
                                    rts.setQuantity(
                                            rsSer.getInt("quantity")
                                    );
                                    rts.setIsFree(
                                            rsSer.getInt("is_free")
                                    );

                                    RoomService service
                                            = new RoomService();

                                    service.setServiceId(
                                            rsSer.getInt("service_id")
                                    );
                                    service.setServiceName(
                                            rsSer.getString("service_name")
                                    );
                                    service.setUnitPrice(
                                            rsSer.getBigDecimal("unit_price")
                                    );

                                    rts.setRoomService(service);
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
            System.out.println(
                    ">>> LỖI LOGIC TẠI getAllRoomTypesForManager: "
                    + e.getMessage()
            );
            e.printStackTrace();
        }

        return list;
    }

    public boolean insertRoomType(
            RoomType rt,
            List<String> imageList,
            List<RoomTypeService> serviceList) {

        String insertRoomTypeSql
                = "INSERT INTO RoomTypes "
                + "(type_name, description, capacity, bed_type, "
                + "bed_count, area_sqm, base_price, is_active) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        String insertImageSql
                = "INSERT INTO RoomTypeImages "
                + "(room_type_id, image_url) VALUES (?, ?)";

        String insertServiceSql
                = "INSERT INTO RoomTypeServices "
                + "(room_type_id, service_id, quantity, is_free) "
                + "VALUES (?, ?, ?, ?)";

        PreparedStatement psRoom = null;
        PreparedStatement psImg = null;
        PreparedStatement psSer = null;
        ResultSet generatedKeys = null;

        try {
            connection.setAutoCommit(false);

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
                throw new SQLException(
                        "Creating room type failed, no rows affected."
                );
            }

            int generatedId = 0;
            generatedKeys = psRoom.getGeneratedKeys();

            if (generatedKeys.next()) {
                generatedId = generatedKeys.getInt(1);
            }

            if (generatedId > 0) {
                if (imageList != null && !imageList.isEmpty()) {
                    psImg = connection.prepareStatement(
                            insertImageSql
                    );

                    for (String imgUrl : imageList) {
                        if (imgUrl != null
                                && !imgUrl.trim().isEmpty()) {

                            psImg.setInt(1, generatedId);
                            psImg.setString(2, imgUrl.trim());
                            psImg.addBatch();
                        }
                    }

                    psImg.executeBatch();
                }

                if (serviceList != null
                        && !serviceList.isEmpty()) {

                    psSer = connection.prepareStatement(
                            insertServiceSql
                    );

                    for (RoomTypeService rts : serviceList) {
                        psSer.setInt(1, generatedId);
                        psSer.setInt(2, rts.getServiceId());
                        psSer.setInt(3, rts.getQuantity());
                        psSer.setInt(4, rts.getIsFree());
                        psSer.addBatch();
                    }

                    psSer.executeBatch();
                }

                connection.commit();
                return true;

            } else {
                throw new SQLException(
                        "Creating room type failed, no ID obtained."
                );
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
                if (connection != null) {
                    connection.setAutoCommit(true);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public boolean updateRoomType(
            RoomType rt,
            List<String> newImageList,
            List<RoomTypeService> newServiceList) {

        String updateRoomTypeSql
                = "UPDATE RoomTypes SET type_name = ?, "
                + "description = ?, capacity = ?, bed_type = ?, "
                + "bed_count = ?, area_sqm = ?, base_price = ?, "
                + "is_active = ? WHERE room_type_id = ?";

        String deleteOldImagesSql
                = "DELETE FROM RoomTypeImages "
                + "WHERE room_type_id = ?";

        String insertImageSql
                = "INSERT INTO RoomTypeImages "
                + "(room_type_id, image_url, image_description) "
                + "VALUES (?, ?, ?)";

        String deleteOldServicesSql
                = "DELETE FROM RoomTypeServices "
                + "WHERE room_type_id = ?";

        String insertServiceSql
                = "INSERT INTO RoomTypeServices "
                + "(room_type_id, service_id, quantity, is_free) "
                + "VALUES (?, ?, ?, ?)";

        PreparedStatement psRoom = null;
        PreparedStatement psDelImg = null;
        PreparedStatement psInsImg = null;
        PreparedStatement psDelSer = null;
        PreparedStatement psInsSer = null;

        try {
            connection.setAutoCommit(false);

            psRoom = connection.prepareStatement(
                    updateRoomTypeSql
            );

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

            psDelImg = connection.prepareStatement(
                    deleteOldImagesSql
            );
            psDelImg.setInt(1, rt.getRoomTypeId());
            psDelImg.executeUpdate();

            if (newImageList != null
                    && !newImageList.isEmpty()) {

                psInsImg = connection.prepareStatement(
                        insertImageSql
                );

                for (String imgUrl : newImageList) {
                    if (imgUrl != null
                            && !imgUrl.trim().isEmpty()) {

                        psInsImg.setInt(
                                1,
                                rt.getRoomTypeId()
                        );
                        psInsImg.setString(
                                2,
                                imgUrl.trim()
                        );
                        psInsImg.setString(
                                3,
                                "Image for " + rt.getTypeName()
                        );
                        psInsImg.addBatch();
                    }
                }

                psInsImg.executeBatch();
            }

            psDelSer = connection.prepareStatement(
                    deleteOldServicesSql
            );
            psDelSer.setInt(1, rt.getRoomTypeId());
            psDelSer.executeUpdate();

            if (newServiceList != null
                    && !newServiceList.isEmpty()) {

                psInsSer = connection.prepareStatement(
                        insertServiceSql
                );

                for (RoomTypeService rts : newServiceList) {
                    psInsSer.setInt(
                            1,
                            rt.getRoomTypeId()
                    );
                    psInsSer.setInt(
                            2,
                            rts.getServiceId()
                    );
                    psInsSer.setInt(
                            3,
                            rts.getQuantity()
                    );
                    psInsSer.setInt(
                            4,
                            rts.getIsFree()
                    );
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
    
    // Phần room detail
    public RoomType getRoomTypeById(int roomTypeId) {
        RoomType room = null;

        String sql
                = "SELECT room_type_id, type_name, description, capacity, "
                + "bed_type, bed_count, area_sqm, base_price, is_active "
                + "FROM RoomTypes "
                + "WHERE room_type_id = ? "
                + "AND is_active = 1";

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

                room.setImageUrl(getRoomTypeImagesForDetail(roomTypeId));
                room.setRoomTypeServices(
                        getRoomTypeServicesForDetail(roomTypeId)
                );
                room.setRoomAmenities(
                        getRoomTypeAmenitiesForDetail(roomTypeId)
                );
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return room;
    }

    public List<String> getRoomTypeImagesForDetail(int roomTypeId) {
        List<String> images = new ArrayList<>();

        String sql
                = "SELECT image_url "
                + "FROM RoomTypeImages "
                + "WHERE room_type_id = ? "
                + "ORDER BY image_id ASC";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, roomTypeId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String imageUrl = rs.getString("image_url");

                if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                    images.add(imageUrl.trim());
                }
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return images;
    }

    public List<RoomTypeService> getRoomTypeServicesForDetail(
            int roomTypeId) {

        List<RoomTypeService> list = new ArrayList<>();

        String sql
                = "SELECT rts.room_type_service_id, "
                + "rts.room_type_id, "
                + "rts.service_id, "
                + "rts.quantity, "
                + "rts.is_free, "
                + "rs.service_name, "
                + "rs.description, "
                + "rs.unit_price, "
                + "rs.is_active "
                + "FROM RoomTypeServices rts "
                + "JOIN RoomServices rs "
                + "ON rts.service_id = rs.service_id "
                + "WHERE rts.room_type_id = ? "
                + "AND rs.is_active = 1 "
                + "ORDER BY rts.room_type_service_id ASC";

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

                RoomTypeService roomTypeService = new RoomTypeService();

                roomTypeService.setRoomTypeServiceId(
                        rs.getInt("room_type_service_id")
                );
                roomTypeService.setRoomTypeId(
                        rs.getInt("room_type_id")
                );
                roomTypeService.setServiceId(
                        rs.getInt("service_id")
                );
                roomTypeService.setQuantity(
                        rs.getInt("quantity")
                );
                roomTypeService.setIsFree(
                        rs.getInt("is_free")
                );
                roomTypeService.setRoomService(service);

                list.add(roomTypeService);
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<RoomAmenity> getRoomTypeAmenitiesForDetail(
            int roomTypeId) {

        List<RoomAmenity> list = new ArrayList<>();

        String sql
                = "SELECT ra.amenity_id, "
                + "ra.amenity_name, "
                + "ra.description, "
                + "ra.unit_price, "
                + "ra.is_active "
                + "FROM RoomTypeAmenities rta "
                + "JOIN RoomAmenities ra "
                + "ON rta.amenity_id = ra.amenity_id "
                + "WHERE rta.room_type_id = ? "
                + "AND ra.is_active = 1 "
                + "ORDER BY rta.room_type_amenity_id ASC";

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

    public int getAvailableRoomCount(
            int roomTypeId,
            String checkIn,
            String checkOut) {

        int availableRooms = 0;

        String sql
                = "SELECT COUNT(*) AS available_rooms "
                + "FROM Rooms r "
                + "WHERE r.room_type_id = ? "
                + "AND r.[status] != N'Đang bảo trì' "
                + "AND r.room_number NOT IN ( "
                + "    SELECT br.room_number "
                + "    FROM BookingRooms br "
                + "    JOIN Bookings b "
                + "    ON br.booking_id = b.booking_id "
                + "    WHERE b.[status] != N'Đã hủy' "
                + "    AND NOT (b.checkout_date <= ? "
                + "             OR b.checkin_date >= ?) "
                + ")";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, roomTypeId);
            ps.setString(2, checkIn);
            ps.setString(3, checkOut);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                availableRooms = rs.getInt("available_rooms");
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Math.max(availableRooms, 0);
    }


}