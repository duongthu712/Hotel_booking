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
 * Last update 03/06/2026 Class RoomServiceDAO include getAll(), getById(int
 * serviceId), create(RoomService service), update(RoomService service),
 * delete(int serviceId)
 *
 * @author LinhLTHE200306
 */
public class RoomServiceDAO extends DBContext {

    PreparedStatement stm;
    ResultSet rs;

    //
    public List<Service> getAllRoomServices() {
        List<Service> roomServices = new ArrayList<Service>();
        try {
            String strSQL = """
                           select * from RoomServices
                           """;
            stm = connection.prepareStatement(strSQL);
            rs = stm.executeQuery();

            while (rs.next()) {
                int serviceId = rs.getInt("service_id");
                BigDecimal price = rs.getBigDecimal("unit_price");
                String serviceName = rs.getString("service_name");
                String description = rs.getString("description");
                boolean active = rs.getBoolean("is_active");

                Service newService = new Service(serviceId, serviceName, description, price, active, ServiceType.ROOM);
                roomServices.add(newService);
            }
        } catch (Exception ex) {
            System.out.println("GetRoomServices:" + ex.getMessage());
        }
        return roomServices;
    }

    public Service getRoomServicesById(int serviceId) {
        Service roomService = null;
        try {
            String strSQL = """
                          select * from RoomServices rs
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

                roomService = new Service(serviceId, serviceName, description, price, active, ServiceType.ROOM);
            }
        } catch (Exception ex) {
            System.out.println("GetRoomServices:" + ex.getMessage());
        }
        return roomService;
    }

    public Service createRoomService(Service roomService) {
        Service found = getRoomServicesById(roomService.getServiceId());
        if (found != null) {
            return null;
        }

        try {
            String strSQL = """
                            insert into RoomServices ([service_name], [description], unit_price, is_active) 
                            values (?, ?, ?, ?)
                            """;
            stm = connection.prepareCall(strSQL);

            stm.setString(1, roomService.getServiceName());
            stm.setString(2, roomService.getDescription());
            stm.setBigDecimal(3, roomService.getUnitPrice());
            stm.setBoolean(4, roomService.isActive());

            stm.execute();
        } catch (Exception ex) {
            System.out.println("CreateRoomServices:" + ex.getMessage());
        }
        return roomService;
    }

    public Service updateRoomService(Service roomService) {
        Service found = getRoomServicesById(roomService.getServiceId());
        if (found == null) {
            return null;
        }

        try {
            String strSQL = """
                           update RoomServices 
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
            System.out.println("UpdateRoomServices:" + ex.getMessage());
        }
        return roomService;
    }

    public Service delete(int serviceId) {
        Service found = getRoomServicesById(serviceId);
        if (found == null) {
            return null;
        }

        try {
            String strSQL = """
                           delete RoomServices where service_id = ?
                           """;
            stm = connection.prepareCall(strSQL);
            stm.setInt(1, serviceId);
            stm.execute();
        } catch (Exception ex) {
            System.out.println("DeleteRoomServices:" + ex.getMessage());
        }
        return found;
    }
}
