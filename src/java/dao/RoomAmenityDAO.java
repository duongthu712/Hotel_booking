package dao;

import dal.DBContext;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.RoomAmenity;
import java.sql.SQLException;

/**
 * @author LinhLTHE200306
 * @version 2.0
 * @since 2026-07-21
 */
public class RoomAmenityDAO extends DBContext {

    public List<RoomAmenity> getAllRoomAmenities() throws Exception {
        List<RoomAmenity> list = new ArrayList<>();
        String strSQL = """
                        select * 
                        from RoomAmenities 
                        order by amenity_id desc
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL); ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("amenity_id");
                String name = rs.getString("amenity_name");
                String description = rs.getString("description").trim();
                BigDecimal price = rs.getBigDecimal("unit_price");
                boolean active = rs.getBoolean("is_active");

                RoomAmenity newRoomAmenity = new RoomAmenity(id, name, description, price, active);
                list.add(newRoomAmenity);
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể truy xuất danh sách tiện nghi.");
        }
        return list;
    }

    public RoomAmenity getRoomAmenityById(int amenityId) throws Exception {
        String strSQL = """
                        select * 
                        from RoomAmenities 
                        where amenity_id = ? 
                        order by amenity_id desc
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setInt(1, amenityId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("amenity_id");
                    String name = rs.getString("amenity_name");
                    String description = rs.getString("description").trim();
                    BigDecimal price = rs.getBigDecimal("unit_price");
                    boolean active = rs.getBoolean("is_active");

                    RoomAmenity roomAmenity = new RoomAmenity(id, name, description, price, active);
                    return roomAmenity;
                } else {
                    throw new Exception("Tiện nghi này không tồn tại.");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Vui lòng thử lại sau.");
        }
    }

    public RoomAmenity getRoomAmenityByFullName(String amenityName) throws Exception {
        String strSQL = """
                        select * 
                        from RoomAmenities 
                        where amenity_name = ? 
                        order by amenity_id desc
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setString(1, amenityName);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("amenity_id");
                    String name = rs.getString("amenity_name");
                    String description = rs.getString("description") != null ? rs.getString("description").trim() : "";
                    BigDecimal price = rs.getBigDecimal("unit_price");
                    boolean active = rs.getBoolean("is_active");

                    RoomAmenity roomAmenity = new RoomAmenity(id, name, description, price, active);
                    return roomAmenity;
                } else {
                    throw new Exception("Tiện nghi này không tồn tại.");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Vui lòng thử lại sau.");
        }
    }

    public List<RoomAmenity> searchRoomAmenitiesByName(String keyword) throws Exception {
        List<RoomAmenity> list = new ArrayList<>();
        String strSQL = """
                        select * 
                        from RoomAmenities 
                        where amenity_name like ? 
                        order by amenity_id desc
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setString(1, "%" + keyword + "%");
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("amenity_id");
                    String name = rs.getString("amenity_name");
                    String description = rs.getString("description").trim();
                    BigDecimal price = rs.getBigDecimal("unit_price");
                    boolean active = rs.getBoolean("is_active");

                    RoomAmenity amenity = new RoomAmenity(id, name, description, price, active);
                    list.add(amenity);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể tìm kiếm tiện nghi.");
        }
        return list;
    }

    public RoomAmenity createRoomAmenity(RoomAmenity roomAmenity) throws Exception {
        try {
            getRoomAmenityByFullName(roomAmenity.getAmenityName());
            throw new Exception("Tên tiện nghi này đã tồn tại.");
        } catch (Exception e) {
            if (!e.getMessage().equals("Tiện nghi này không tồn tại.")) {
                throw e;
            }
        }

        String strSQL = """
                        insert into RoomAmenities ([amenity_name], [description], unit_price, is_active) 
                        values (?, ?, ?, ?)
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stm.setString(1, roomAmenity.getAmenityName());
            stm.setString(2, roomAmenity.getDescription());
            stm.setBigDecimal(3, roomAmenity.getUnitPrice());
            stm.setBoolean(4, roomAmenity.isActive());

            if (stm.executeUpdate() > 0) {
                try (ResultSet generatedKeys = stm.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        roomAmenity.setAmenityId(generatedKeys.getInt(1));
                    }
                }
                return roomAmenity;
            } else {
                throw new Exception("Thêm tiện nghi thất bại.");
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể tạo mới tiện nghi.");
        }
    }

    public RoomAmenity updateRoomAmenity(RoomAmenity roomAmenity) throws Exception {
        if (getRoomAmenityById(roomAmenity.getAmenityId()) == null) {
            throw new Exception("Tiện nghi không tồn tại, không thể cập nhật.");
        }

        String strSQL = """
                        update RoomAmenities 
                        set [amenity_name] = ?, 
                        [description] = ?, 
                        unit_price = ?, 
                        is_active = ? 
                        where amenity_id = ?
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setString(1, roomAmenity.getAmenityName());
            stm.setString(2, roomAmenity.getDescription());
            stm.setBigDecimal(3, roomAmenity.getUnitPrice());
            stm.setBoolean(4, roomAmenity.isActive());
            stm.setInt(5, roomAmenity.getAmenityId());

            if (stm.executeUpdate() > 0) {
                return roomAmenity;
            } else {
                throw new Exception("Cập nhật thất bại.");
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể cập nhật.");
        }
    }

    public boolean isAmenityUsedInRoomTypes(int amenityId) throws Exception {
        String strSQL = """
                        select 1 
                        from RoomTypeAmenities 
                        where amenity_id = ?
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setInt(1, amenityId);
            try (ResultSet rs = stm.executeQuery()) {
                return rs.next(); // true nếu có ít nhất 1 bản ghi
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể kiểm tra ràng buộc hạng phòng.");
        }
    }

    public RoomAmenity delete(int amenityId) throws Exception {

        RoomAmenity found = getRoomAmenityById(amenityId);

        if (isAmenityUsedInRoomTypes(amenityId)) {
            throw new Exception("Không thể xóa vì tiện nghi đang được sử dụng trong hạng phòng.");
        }

        String strSQL = """
                        delete 
                        from RoomAmenities 
                        where amenity_id = ?
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setInt(1, amenityId);
            if (stm.executeUpdate() > 0) {
                return found;
            } else {
                throw new Exception("Không thể xóa tiện nghi.");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 547) {
                throw new Exception("Không thể xóa vì tiện nghi đang được sử dụng.");
            }
            throw new Exception("Lỗi hệ thống: Không thể xóa.");
        }
    }

}
