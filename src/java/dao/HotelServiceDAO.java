package dao;

import dal.DBContext;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.HotelService;
import java.sql.SQLException;

/**
 * HotelServiceDAO.java Data Processing Operator layer for hotel services
 * Provides CRUD with HotelServices table
 *
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-06-02
 */
public class HotelServiceDAO extends DBContext {

    public List<HotelService> getAllHotelServices() throws Exception {
        List<HotelService> list = new ArrayList<HotelService>();
        String strSQL = """
                        select * 
                        from HotelServices 
                        order by hotel_service_id desc
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL); ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("hotel_service_id");
                BigDecimal price = rs.getBigDecimal("unit_price");
                String description = rs.getString("description").trim();
                String name = rs.getString("service_name");
                boolean active = rs.getBoolean("is_active");
                String imgUrl = rs.getString("image_url");

                HotelService newHotelService = new HotelService(id, name, 1, description, price, imgUrl, active);
                list.add(newHotelService);
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 547) {
                throw new Exception("Không thể xóa dịch vụ này vì đang được sử dụng trong các đơn đặt phòng.");
            } else {
                throw new Exception("Lỗi hệ thống: Vui lòng thử lại sau.");
            }
        }
        return list;
    }

    public HotelService getHotelServicesById(int serviceId) throws Exception {
        String strSQL = """
                        select * 
                        from HotelServices 
                        where hotel_service_id = ? 
                        order by hotel_service_id desc
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setInt(1, serviceId);

            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    BigDecimal price = rs.getBigDecimal("unit_price");
                    String name = rs.getString("service_name");
                    String description = rs.getString("description").trim();
                    boolean active = rs.getBoolean("is_active");
                    String imgUrl = rs.getString("image_url");

                    return new HotelService(serviceId, name, 1, description, price, imgUrl, active);
                } else {
                    throw new Exception("Dịch vụ này không tồn tại trong hệ thống.");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Vui lòng thử lại sau.");
        }
    }

    public HotelService getHotelServicesByName(String serviceName) throws Exception {
        String strSQL = """
                        select * 
                        from HotelServices 
                        where service_name = ?
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setString(1, serviceName);

            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("hotel_service_id");
                    BigDecimal price = rs.getBigDecimal("unit_price");
                    String description = rs.getString("description").trim();
                    boolean active = rs.getBoolean("is_active");
                    String imgUrl = rs.getString("image_url");

                    return new HotelService(id, serviceName, 1, description, price, imgUrl, active);
                } else {
                    throw new Exception("Dịch vụ này không tồn tại trong hệ thống.");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Vui lòng thử lại sau.");
        }
    }

    private boolean isServiceNameExists(String serviceName) throws SQLException {
        String sql = """
                     select 1 
                     from HotelServices 
                     where service_name = ?
                     """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, serviceName);
            try (ResultSet rs = stm.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<HotelService> searchHotelServicesByName(String keyword) throws Exception {
        List<HotelService> list = new ArrayList<>();
        String strSQL = """
                        select * 
                        from HotelServices 
                        where service_name like ? 
                        order by hotel_service_id desc
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setString(1, "%" + keyword + "%");
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("hotel_service_id");

                    BigDecimal price = rs.getBigDecimal("unit_price");
                    String description = rs.getString("description").trim();
                    boolean active = rs.getBoolean("is_active");
                    String imgUrl = rs.getString("image_url");
                    String name = rs.getString("service_name");
                    HotelService service = new HotelService(id, name, 1, description, price, imgUrl, active);
                    list.add(service);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể tìm kiếm dịch vụ.");
        }
        return list;
    }

    public HotelService createHotelService(HotelService hotelService) throws Exception {
        if (isServiceNameExists(hotelService.getServiceName())) {
            throw new Exception("Tên dịch vụ này đã tồn tại, vui lòng chọn tên khác.");
        }

        String strSQL = """
                           insert into HotelServices 
                        ([service_name], [description], unit_price, is_active, image_url)  
                           values (?, ?, ?, ?, ?)
                           """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stm.setString(1, hotelService.getServiceName());
            stm.setString(2, hotelService.getDescription());
            stm.setBigDecimal(3, hotelService.getUnitPrice());
            stm.setBoolean(4, hotelService.isActive());
            stm.setString(5, hotelService.getImageUrl());

            if (stm.executeUpdate() > 0) {
                try (ResultSet generatedKeys = stm.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        hotelService.setHotelServiceId(generatedKeys.getInt(1));
                    }
                }
                return hotelService;
            } else {
                throw new Exception("Thêm dịch vụ thất bại.");
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể tạo mới dịch vụ.");
        }
    }

    public HotelService updateHotelService(HotelService hotelService) throws Exception {
        HotelService found = getHotelServicesById(hotelService.getHotelServiceId());
        if (found == null) {
            throw new Exception("Dịch vụ này không tồn tại, không thể cập nhật.");
        }

        String strSQL = """
                        update hotelservices 
                        set [service_name] = ?, 
                        [description] = ?, 
                        unit_price = ?, 
                        is_active = ?, 
                        image_url = ? 
                        where hotel_service_id = ?
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setString(1, hotelService.getServiceName());
            stm.setString(2, hotelService.getDescription());
            stm.setBigDecimal(3, hotelService.getUnitPrice());
            stm.setBoolean(4, hotelService.isActive());
            stm.setString(5, hotelService.getImageUrl());
            stm.setInt(6, hotelService.getHotelServiceId());

            int rowCount = stm.executeUpdate();
            if (rowCount > 0) {
                return hotelService;
            } else {
                throw new Exception("Cập nhật dịch vụ thất bại.");
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể cập nhật dịch vụ.");
        }
    }

    public HotelService delete(int serviceId) throws Exception {
        HotelService found = getHotelServicesById(serviceId);
        if (found == null) {
            throw new Exception("Dịch vụ cần xóa không tồn tại.");
        }

        String strSQL = """
                        delete 
                        from hotelservices 
                        where hotel_service_id = ?
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setInt(1, serviceId);

            int rowCount = stm.executeUpdate();
            if (rowCount > 0) {
                return found;
            } else {
                throw new Exception("Không thể xóa dịch vụ này.");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 547) {
                throw new Exception("Không thể xóa vì dịch vụ này đang được sử dụng trong các đơn đặt phòng.");
            }
            throw new Exception("Lỗi hệ thống: Không thể thực hiện thao tác xóa.");
        }
    }
}
