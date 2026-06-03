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
 * Last update 03/06/2026 Class HotelServiceDAO include getAll(), getById(int
 * serviceId), create(HotelService service), update(HotelService service),
 * delete(int serviceId)
 *
 * @author LinhLTHE200306
 */
public class HotelServiceDAO extends DBContext {

    PreparedStatement stm;
    ResultSet rs;

    //
    public List<Service> getAllRoomTypes() {
        List<Service> roomServices = new ArrayList<Service>();
        try {
            String strSQL = """
                           select * from HotelServices
                           """;
            stm = connection.prepareStatement(strSQL);
            rs = stm.executeQuery();

            while (rs.next()) {
                int serviceId = rs.getInt("service_id");
                BigDecimal price = rs.getBigDecimal("unit_price");
                String serviceName = rs.getString("service_name");
                String description = rs.getString("description");
                boolean active = rs.getBoolean("is_active");

                Service newService = new Service(serviceId, serviceName, description, price, active, ServiceType.HOTEL);
                roomServices.add(newService);
            }
        } catch (Exception ex) {
            System.out.println("GetHotelServices:" + ex.getMessage());
        }
        return roomServices;
    }

    public Service getHotelServicesById(int serviceId) {
        Service roomService = null;
        try {
            String strSQL = """
                          select * from HotelServices rs
                          where rs.service_id = ?
                          """;
            stm = connection.prepareCall(strSQL);
            stm.setInt(1, serviceId);
            rs = stm.executeQuery();

            while (rs.next()) {
                BigDecimal price = rs.getBigDecimal("unit_price");
                String serviceName = rs.getString("service_name");
                String description = rs.getString("description");
                boolean active = rs.getBoolean("is_active");

                roomService = new Service(serviceId, serviceName, description, price, active, ServiceType.HOTEL);
            }
        } catch (Exception ex) {
            System.out.println("GetHotelServices:" + ex.getMessage());
        }
        return roomService;
    }

    public Service createHotelService(Service roomService) {
        Service found = getHotelServicesById(roomService.getServiceId());
        if (found != null) {
            return null;
        }

        try {
            String strSQL = """
                            insert into HotelServices ([service_name], [description], unit_price, is_active) 
                            values (?, ?, ?, ?)
                            """;
            stm = connection.prepareCall(strSQL);

            stm.setString(1, roomService.getServiceName());
            stm.setString(2, roomService.getDescription());
            stm.setBigDecimal(3, roomService.getUnitPrice());
            stm.setBoolean(4, roomService.isActive());

            stm.execute();
        } catch (Exception ex) {
            System.out.println("CreateHotelServices:" + ex.getMessage());
        }
        return roomService;
    }

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
                           where service_id = ?
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

    public Service delete(int serviceId) {
        Service found = getHotelServicesById(serviceId);
        if (found == null) {
            return null;
        }

        try {
            String strSQL = """
                           delete HotelServices where service_id = ?
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
