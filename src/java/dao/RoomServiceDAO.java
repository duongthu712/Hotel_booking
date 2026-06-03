package dao;

import dal.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import model.RoomService;

/**
 * Last update 01/06/2026 Class RoomServiceDAO include getAll(), getById(int
 * serviceId), create(RoomService service), update(RoomService service),
 * delete(int serviceId)
 *
 * @author LinhLTHE200306
 */
public class RoomServiceDAO extends DBContext {

    PreparedStatement stm;
    ResultSet rs;

    //
    public List<RoomService> getAllRoomTypes() {
        List<RoomService> roomServices = new ArrayList<RoomService>();
        try {
            String strSQL = """
                           select * from RoomServices
                           """;
            stm = connection.prepareStatement(strSQL);
            rs = stm.executeQuery();

            while (rs.next()) {
                int serviceId = rs.getInt("service_id");
                double price = rs.getDouble("unit_price");

                String serviceName = rs.getString("service_name");
                String description = rs.getString("description");
                boolean active = rs.getBoolean("is_active");

                RoomService newRoomService = new RoomService(serviceId, serviceName, description, price, active, serviceId)
                roomServices.add(newRoomService);
            }
        } catch (Exception ex) {
            System.out.println("GetRoomServices:" + ex.getMessage());
        }
        return roomServices;
    }

    public RoomService getRoomServicesById(int serviceId) {
        RoomService roomService = null;
        try {
            String strSQL = """
                          select * from RoomServices rs
                          where rs.service_id = ?
                          """;
            stm = connection.prepareCall(strSQL);
            stm.setInt(1, serviceId);
            rs = stm.executeQuery();
            while (rs.next()) {
                double price = rs.getDouble("unit_price");
                String serviceName = rs.getString("service_name");
                String description = rs.getString("description");
                boolean active = rs.getBoolean("is_active");

                roomService = new RoomService(serviceId, price, serviceName, description, active);
            }
        } catch (Exception ex) {
            System.out.println("GetRoomServices:" + ex.getMessage());
        }
        return roomService;
    }

    public RoomService createRoomService(RoomService roomService) {
        RoomService found = getRoomServicesById(roomService.getServiceId());
        if (found != null) {
            return null;
        }

        try {
            String strSQL = """
                            insert into RoomServices ([service_name], [description], unit_price) 
                            values (N'?', N'?',?)
                            """;
            stm = connection.prepareCall(strSQL);

            stm.setString(1, roomService.getServiceName());
            stm.setString(2, roomService.getDescription());
            stm.setDouble(3, roomService.getPrice());
            stm.execute();
        } catch (Exception ex) {
            System.out.println("CreateRoomServices:" + ex.getMessage());
        }
        return roomService;
    }

    public boolean updateRoomService(RoomService roomService) {
        Account found = GetAccountById(account.AccountId);
        if (found == null) return null;
        
        try {
            String strSQL = "update Accounts "
                    + "set password = ?, "
                    + "roleId = ? "
                    + "where accountId = ?";
            stm = connection.prepareCall(strSQL);
            stm.setString(1, account.Password);
            stm.setInt(2, account.RoleId);
            stm.setString(3, account.AccountId);
            stm.execute();
        } catch (Exception ex) {
            System.out.println("UpdateAccount:" + ex.getMessage());
        }
        return account;
    }

    public boolean delete(int serviceId) {

    }
}
