package dao;

import dal.DBContext;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Service;
import model.ServiceType;

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
    public List<Service> getAllHotelServices() {
        List<Service> roomServices = new ArrayList<Service>();
        try {
            String strSQL = """
                           select * from HotelServices
                           """;
            stm = connection.prepareStatement(strSQL);
            rs = stm.executeQuery();

            while (rs.next()) {
                int serviceId = rs.getInt("hotel_service_id");
                BigDecimal price = rs.getBigDecimal("unit_price");
                String serviceName = rs.getString("service_name");
                String description = rs.getString("description");
                boolean active = rs.getBoolean("is_active");

                Service newService = new Service(serviceId, serviceName, 
                        description, price, active, ServiceType.HOTEL);
                roomServices.add(newService);
            }
        } catch (Exception ex) {
            System.out.println("GetHotelServices:" + ex.getMessage());
        }
        return roomServices;
    }

    /**
     * Find a service by its ID
     *
     * @param serviceId
     * @return Service object if found, if not found return null
     */
    public Service getHotelServicesById(int serviceId) {
        Service roomService = null;
        try {
            String strSQL = """
                          select * from HotelServices rs
                          where rs.hotel_service_id = ?
                          """;
            stm = connection.prepareCall(strSQL);
            stm.setInt(1, serviceId);
            rs = stm.executeQuery();

            while (rs.next()) {
                BigDecimal price = rs.getBigDecimal("unit_price");
                String serviceName = rs.getString("service_name");
                String description = rs.getString("description");
                boolean active = rs.getBoolean("is_active");

                roomService = new Service(serviceId, serviceName, description, 
                        price, active, ServiceType.HOTEL);
            }
        } catch (Exception ex) {
            System.out.println("GetHotelServices:" + ex.getMessage());
        }
        return roomService;
    }

    /**
     * Adding new service in system
     *
     * @param hotelService
     * @return service object if add successfully, null if unsuccess
     */
    public Service createHotelService(Service hotelService) {
        //Check service is exist
        Service found = getHotelServicesById(hotelService.getServiceId());
        if (found != null) {
            return null;
        }

        try {
            String strSQL = """
                            insert into HotelServices ([service_name], 
                            [description], unit_price, is_active) 
                            values (?, ?, ?, ?)
                            """;
            stm = connection.prepareCall(strSQL);

            stm.setString(1, hotelService.getServiceName());
            stm.setString(2, hotelService.getDescription());
            stm.setBigDecimal(3, hotelService.getUnitPrice());
            stm.setBoolean(4, hotelService.isActive());

            stm.execute();
        } catch (Exception ex) {
            System.out.println("CreateHotelServices:" + ex.getMessage());
        }
        return hotelService;
    }

    /**
     * Update the information for an existing hotel service.
     *
     * @param roomService
     * @return The Service object will be null after the update if the ID is
     * successful, or null if the ID is not found
     */
    public Service updateHotelService(Service roomService) {
        Service found = getHotelServicesById(roomService.getServiceId());
        if (found == null) {
            return null;
        }

        try {
            String strSQL = """
                           update HotelServices 
                           set [service_name] = ?, 
                           [description] =?, 
                           unit_price =?, 
                           is_active = ? 
                           where hotel_service_id = ?
                           """;
            stm = connection.prepareCall(strSQL);

            stm.setString(1, roomService.getServiceName());
            stm.setString(2, roomService.getDescription());
            stm.setBigDecimal(3, roomService.getUnitPrice());
            stm.setBoolean(4, roomService.isActive());
            stm.setInt(5, roomService.getServiceId());

            stm.execute();
        } catch (Exception ex) {
            System.out.println("UpdateHotelServices:" + ex.getMessage());
        }
        return roomService;
    }

    /**
     * Remove a service from the system based on its ID.
     *
     * @param serviceId
     * @return The Service object is deleted if successful, null if the service
     * is not found.
     */
    public Service delete(int serviceId) {
        Service found = getHotelServicesById(serviceId);
        if (found == null) {
            return null;
        }

        try {
            String strSQL = """
                           delete HotelServices where hotel_service_id = ?
                           """;
            stm = connection.prepareCall(strSQL);
            stm.setInt(1, serviceId);
            stm.execute();
        } catch (Exception ex) {
            System.out.println("DeleteHotelServices:" + ex.getMessage());
        }
        return found;
    }
}
