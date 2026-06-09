package dao;

import dal.DBContext;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.RoomService;
import java.sql.SQLException;

/**
 * RoomServiceDAO.java Data Processing Operator layer for room services Provides
 * CRUD with RoomServices table
 *
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-06-02
 */
public class RoomServiceDAO extends DBContext {

    public List<RoomService> getAllRoomServices() throws Exception {
        List<RoomService> list = new ArrayList<>();
        String strSQL = """
                        select * 
                        from RoomServices
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL);
             ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("service_id");
                String name = rs.getString("service_name");
                String description = rs.getString("description").trim();
                BigDecimal price = rs.getBigDecimal("unit_price");
                boolean active = rs.getBoolean("is_active");

                RoomService newRoomService = new RoomService(id, name, description, price, active);
                list.add(newRoomService);
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể truy xuất danh sách dịch vụ.");
        }
        return list;
    }

    public RoomService getRoomServicesById(int serviceId) throws Exception {
        String strSQL = """
                        select * 
                        from RoomServices 
                        where service_id = ?
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setInt(1, serviceId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("service_id");
                    String name = rs.getString("service_name");
                    String description = rs.getString("description").trim();
                    BigDecimal price = rs.getBigDecimal("unit_price");
                    boolean active = rs.getBoolean("is_active");

                    RoomService roomService = new RoomService(id, name, description, price, active);
                    return roomService;
                } else {
                    throw new Exception("Dịch vụ này không tồn tại.");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Vui lòng thử lại sau.");
        }
    }

    public RoomService getRoomServicesByName(String serviceName) throws Exception {
        String strSQL = """
                        select * 
                        from RoomServices 
                        where service_name = ?
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setString(1, serviceName);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("service_id");
                    String name = rs.getString("service_name");
                    String description = rs.getString("description") != null ? rs.getString("description").trim() : "";
                    BigDecimal price = rs.getBigDecimal("unit_price");
                    boolean active = rs.getBoolean("is_active");

                    RoomService roomService = new RoomService(id, name, description, price, active);
                    return roomService;
                } else {
                    throw new Exception("Dịch vụ này không tồn tại.");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Vui lòng thử lại sau.");
        }
    }

    public RoomService createRoomService(RoomService roomService) throws Exception {
        try {
            getRoomServicesByName(roomService.getServiceName());
            throw new Exception("Tên dịch vụ này đã tồn tại.");
        } catch (Exception e) {
            if (!e.getMessage().equals("Dịch vụ này không tồn tại.")) {
                throw e;
            }
        }

        String strSQL = """
                        insert into RoomServices ([service_name], [description], unit_price, is_active) 
                        values (?, ?, ?, ?)
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stm.setString(1, roomService.getServiceName());
            stm.setString(2, roomService.getDescription());
            stm.setBigDecimal(3, roomService.getUnitPrice());
            stm.setBoolean(4, roomService.isActive());

            if (stm.executeUpdate() > 0) {
                try (ResultSet generatedKeys = stm.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        roomService.setServiceId(generatedKeys.getInt(1));
                    }
                }
                return roomService;
            } else {
                throw new Exception("Thêm dịch vụ thất bại.");
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể tạo mới dịch vụ.");
        }
    }

    public RoomService updateRoomService(RoomService roomService) throws Exception {
        if (getRoomServicesById(roomService.getServiceId()) == null) {
            throw new Exception("Dịch vụ không tồn tại, không thể cập nhật.");
        }

        String strSQL = """
                        update RoomServices 
                        set [service_name] = ?, 
                        [description] = ?, 
                        unit_price = ?, 
                        is_active = ? 
                        where service_id = ?
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setString(1, roomService.getServiceName());
            stm.setString(2, roomService.getDescription());
            stm.setBigDecimal(3, roomService.getUnitPrice());
            stm.setBoolean(4, roomService.isActive());
            stm.setInt(5, roomService.getServiceId());

            if (stm.executeUpdate() > 0) {
                return roomService;
            } else {
                throw new Exception("Cập nhật thất bại.");
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể cập nhật.");
        }
    }

    public RoomService delete(int serviceId) throws Exception {
        RoomService found = getRoomServicesById(serviceId);
        String strSQL = """
                        delete 
                        from RoomServices 
                        where service_id = ?
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setInt(1, serviceId);
            if (stm.executeUpdate() > 0) {
                return found;
            } else {
                throw new Exception("Không thể xóa dịch vụ.");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 547) {
                throw new Exception("Không thể xóa vì dịch vụ đang được sử dụng.");
            }
            throw new Exception("Lỗi hệ thống: Không thể xóa.");
        }
    }
}