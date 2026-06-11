// 
//package dao;
//
//import dal.DBContext;
//import java.math.BigDecimal;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.util.ArrayList;
//import java.util.List;
//import model.Service;
//import model.ServiceType;
//
///**
// * RoomServiceDAO.java Data Processing Operator layer for room services
// * Provides CRUD with RoomServices table
// *
// * @author LinhLTHE200306
// * @version 1.0
// * @since 2026-06-02
// */
//public class RoomServiceDAO extends DBContext {
//
//    PreparedStatement stm;
//    ResultSet rs;
//
//    /**
//     * Get list all room servives from database
//     *
//     * @return List of service object, return null if not have data
//     */
//    public List<Service> getAllRoomServices() {
//        List<Service> roomServices = new ArrayList<Service>();
//        try {
//            String strSQL = """
//                           select * from RoomServices
//                           """;
//            stm = connection.prepareStatement(strSQL);
//            rs = stm.executeQuery();
//
//            while (rs.next()) {
//                int serviceId = rs.getInt("service_id");
//                BigDecimal price = rs.getBigDecimal("unit_price");
//                String serviceName = rs.getString("service_name");
//                String description = rs.getString("description");
//                boolean active = rs.getBoolean("is_active");
//
//                Service newService = new Service(serviceId, serviceName, 
//                        description, price, active, ServiceType.ROOM);
//                roomServices.add(newService);
//            }
//        } catch (Exception ex) {
//            System.out.println("GetRoomServices:" + ex.getMessage());
//        }
//        return roomServices;
//    }
//
//    /**
//     * Find a service by its ID
//     *
//     * @param serviceId
//     * @return Service object if found, if not found return null
//     */
//    public Service getRoomServicesById(int serviceId) {
//        Service roomService = null;
//        try {
//            String strSQL = """
//                          select * from RoomServices rs
//                          where rs.service_id = ?
//                          """;
//            stm = connection.prepareCall(strSQL);
//            stm.setInt(1, serviceId);
//            rs = stm.executeQuery();
//
//            while (rs.next()) {
//                BigDecimal price = rs.getBigDecimal("unit_price");
//                String serviceName = rs.getString("service_name");
//                String description = rs.getString("description");
//                boolean active = rs.getBoolean("is_active");
//
//                roomService = new Service(serviceId, serviceName, description, 
//                        price, active, ServiceType.ROOM);
//            }
//        } catch (Exception ex) {
//            System.out.println("GetRoomServices:" + ex.getMessage());
//        }
//        return roomService;
//    }
//
//    /**
//     * Adding new service in system
//     *
//     * @param roomService
//     * @return service object if add successfully, null if unsuccess
//     */
//    public Service createRoomService(Service roomService) {
//        //Check service is exist
//        Service found = getRoomServicesById(roomService.getServiceId());
//        if (found != null) {
//            return null;
//        }
//
//        try {
//            String strSQL = """
//                            insert into RoomServices ([service_name], 
//                            [description], unit_price, is_active) 
//                            values (?, ?, ?, ?)
//                            """;
//            stm = connection.prepareCall(strSQL);
//
//            stm.setString(1, roomService.getServiceName());
//            stm.setString(2, roomService.getDescription());
//            stm.setBigDecimal(3, roomService.getUnitPrice());
//            stm.setBoolean(4, roomService.isActive());
//
//            stm.execute();
//        } catch (Exception ex) {
//            System.out.println("CreateRoomServices:" + ex.getMessage());
//        }
//        return roomService;
//    }
//
//    /**
//     * Update the information for an existing room service.
//     *
//     * @param roomService
//     * @return The Service object will be null after the update if the ID is
//     * successful, or null if the ID is not found
//     */
//    public Service updateRoomService(Service roomService) {
//        Service found = getRoomServicesById(roomService.getServiceId());
//        if (found == null) {
//            return null;
//        }
//
//        try {
//            String strSQL = """
//                           update RoomServices 
//                           set [service_name] = ?, 
//                           [description] =?, 
//                           unit_price =?, 
//                           is_active = ? 
//                           where service_id = ?
//                           """;
//            stm = connection.prepareCall(strSQL);
//
//            stm.setString(1, roomService.getServiceName());
//            stm.setString(2, roomService.getDescription());
//            stm.setBigDecimal(3, roomService.getUnitPrice());
//            stm.setBoolean(4, roomService.isActive());
//            stm.setInt(5, roomService.getServiceId());
//
//            stm.execute();
//        } catch (Exception ex) {
//            System.out.println("UpdateRoomServices:" + ex.getMessage());
//        }
//        return roomService;
//    }
//
//    /**
//     * Remove a service from the system based on its ID.
//     *
//     * @param serviceId
//     * @return The Service object is deleted if successful, null if the service
//     * is not found.
//     */
//    public Service delete(int serviceId) {
//        Service found = getRoomServicesById(serviceId);
//        if (found == null) {
//            return null;
//        }
//
//        try {
//            String strSQL = """
//                           delete RoomServices where service_id = ?
//                           """;
//            stm = connection.prepareCall(strSQL);
//            stm.setInt(1, serviceId);
//            stm.execute();
//        } catch (Exception ex) {
//            System.out.println("DeleteRoomServices:" + ex.getMessage());
//        }
//        return found;
//    }
//}
// 
