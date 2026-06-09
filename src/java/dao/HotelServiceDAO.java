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

    PreparedStatement stm;
    ResultSet rs;

    /**
     * Get list all hotel servives from database
     *
     * @return List of service object, return null if not have data
     */
    public List<HotelService> getAllHotelServices() throws Exception {
        List<HotelService> list = new ArrayList<HotelService>();
        String strSQL = """
                        select * from HotelServices
                        """;
        try (PreparedStatement stm = connection.prepareStatement(strSQL); ResultSet rs = stm.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("hotel_service_id");
                BigDecimal price = rs.getBigDecimal("unit_price");
<<<<<<< Updated upstream
                String serviceName = rs.getString("service_name");
                String description = rs.getString("description").trim();
=======
                String name = rs.getString("service_name");
                String description = rs.getString("description");
>>>>>>> Stashed changes
                boolean active = rs.getBoolean("is_active");
                String imgUrl = rs.getString("image_url");

                HotelService newHotelService = new HotelService(id, name, description, price, imgUrl, active);
                list.add(newHotelService);
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 547) {
                throw new Exception("Không thể xóa dịch vụ này vì đang được sử dụng trong các đơn đặt phòng.");
            } else {
                throw new Exception("Lỗi hệ thống: " + e.getMessage());
            }
        }
        return list;
    }

    /**
     * Find a service by its ID
     *
     * @param serviceId
     * @return HotelService object if found, if not found return null
     */
    public HotelService getHotelHotelServicesById(int serviceId) {
        HotelService roomHotelService = null;
        try {
            String strSQL = """
                          select * from HotelHotelServices rs
                          where rs.hotel_service_id = ?
                          """;
            stm = connection.prepareCall(strSQL);
            stm.setInt(1, serviceId);
            rs = stm.executeQuery();

            while (rs.next()) {
                BigDecimal price = rs.getBigDecimal("unit_price");
                String serviceName = rs.getString("service_name");
                String description = rs.getString("description").trim();
                boolean active = rs.getBoolean("is_active");

                roomHotelService = new HotelService(serviceId, serviceName, description,
                        price, active, HotelServiceType.HOTEL);
            }
        } catch (Exception ex) {
            System.out.println("GetHotelHotelServices:" + ex.getMessage());
        }
        return roomHotelService;
    }

    /**
     * Adding new service in system
     *
     * @param hotelHotelService
     * @return service object if add successfully, null if unsuccess
     */
    public HotelService createHotelHotelService(HotelService hotelHotelService) {
        //Check service is exist
        HotelService found = getHotelHotelServicesById(hotelHotelService.getHotelServiceId());
        if (found != null) {
            return null;
        }

        try {
            String strSQL = """
                            insert into HotelHotelServices ([service_name], 
                            [description], unit_price, is_active) 
                            values (?, ?, ?, ?)
                            """;
            stm = connection.prepareCall(strSQL);

            stm.setString(1, hotelHotelService.getHotelServiceName());
            stm.setString(2, hotelHotelService.getDescription());
            stm.setBigDecimal(3, hotelHotelService.getUnitPrice());
            stm.setBoolean(4, hotelHotelService.isActive());

            stm.execute();
        } catch (Exception ex) {
            System.out.println("CreateHotelHotelServices:" + ex.getMessage());
        }
        return hotelHotelService;
    }

    /**
     * Update the information for an existing hotel service.
     *
     * @param roomHotelService
     * @return The HotelService object will be null after the update if the ID
     * is successful, or null if the ID is not found
     */
    public HotelService updateHotelHotelService(HotelService roomHotelService) {
        HotelService found = getHotelHotelServicesById(roomHotelService.getHotelServiceId());
        if (found == null) {
            return null;
        }

        try {
            String strSQL = """
                           update HotelHotelServices 
                           set [service_name] = ?, 
                           [description] =?, 
                           unit_price =?, 
                           is_active = ? 
                           where hotel_service_id = ?
                           """;
            stm = connection.prepareCall(strSQL);

            stm.setString(1, roomHotelService.getHotelServiceName());
            stm.setString(2, roomHotelService.getDescription());
            stm.setBigDecimal(3, roomHotelService.getUnitPrice());
            stm.setBoolean(4, roomHotelService.isActive());
            stm.setInt(5, roomHotelService.getHotelServiceId());

            stm.execute();
        } catch (Exception ex) {
            System.out.println("UpdateHotelHotelServices:" + ex.getMessage());
        }
        return roomHotelService;
    }

    /**
     * Remove a service from the system based on its ID.
     *
     * @param serviceId
     * @return The HotelService object is deleted if successful, null if the
     * service is not found.
     */
    public HotelService delete(int serviceId) {
        HotelService found = getHotelHotelServicesById(serviceId);
        if (found == null) {
            return null;
        }

        try {
            String strSQL = """
                           delete HotelHotelServices where hotel_service_id = ?
                           """;
            stm = connection.prepareCall(strSQL);
            stm.setInt(1, serviceId);
            stm.execute();
        } catch (Exception ex) {
            System.out.println("DeleteHotelHotelServices:" + ex.getMessage());
        }
        return found;
    }
}
