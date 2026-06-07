package dao;

import dal.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.GuestStay;
import model.Room;

/**
 * RoomDAO.java
 *
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-06-07
 */

public class RoomDAO extends DBContext {

    PreparedStatement stm;
    ResultSet rs;

    /**
     * Get all rooms
     *
     * @return List of room
     */
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        try {
            String strSQL = """
                            select *
                            from Rooms
                            order by floor, room_number
                            """;
            stm = connection.prepareStatement(strSQL);
            rs = stm.executeQuery();

            while (rs.next()) {
                Room room = new Room(
                        rs.getInt("room_number"),
                        rs.getInt("floor"),
                        rs.getString("status"),
                        rs.getInt("room_type_id")
                );
                rooms.add(room);
            }
        } catch (Exception ex) {
            System.out.println("GetAllRooms:" + ex.getMessage());
        }
        return rooms;
    }

    /**
     * Get room by room number
     *
     * @param roomNumber
     * @return Room object if found, null if not found
     */
    public Room getRoomByNumber(int roomNumber) {
        Room room = null;
        try {
            String strSQL = """
                            select *
                            from Rooms
                            where room_number = ?
                            """;

            stm = connection.prepareStatement(strSQL);
            stm.setInt(1, roomNumber);
            rs = stm.executeQuery();

            while (rs.next()) {
                room = new Room(
                        rs.getInt("room_number"),
                        rs.getInt("floor"),
                        rs.getString("status"),
                        rs.getInt("room_type_id")
                );
            }
        } catch (Exception ex) {
            System.out.println("GetRoomByNumber:" + ex.getMessage());
        }
        return room;
    }

    /**
     * Search and filter rooms
     *
     * @param floor
     * @param roomTypeId
     * @param keyword
     * @return List room after filter
     */
    public List<Room> searchAndFilterRooms(Integer floor, Integer roomTypeId, String keyword) {
        List<Room> rooms = new ArrayList<>();
        try {
            String strSQL = """
                            select *
                            from Rooms
                            where 1 = 1
                            """;
            if (floor != null) {
                strSQL += " and floor = ?";
            }
            if (roomTypeId != null) {
                strSQL += " and room_type_id = ?";
            }
            if (keyword != null && !keyword.trim().isEmpty()) {
                strSQL += " and cast(room_number as varchar) like ?";
            }
            strSQL += " order by floor, room_number";

            stm = connection.prepareStatement(strSQL);

            int index = 1;
            if (floor != null) {
                stm.setInt(index++, floor);
            }
            if (roomTypeId != null) {
                stm.setInt(index++, roomTypeId);
            }
            if (keyword != null && !keyword.trim().isEmpty()) {
                stm.setString(index++, "%" + keyword.trim() + "%");
            }
            rs = stm.executeQuery();

            while (rs.next()) {
                Room room = new Room(
                        rs.getInt("room_number"),
                        rs.getInt("floor"),
                        rs.getString("status"),
                        rs.getInt("room_type_id")
                );
                rooms.add(room);
            }
        } catch (Exception ex) {
            System.out.println("SearchAndFilterRooms:" + ex.getMessage());
        }
        return rooms;
    }

    /**
     * Update room information
     *
     * Rule: - Room Number: cannot edit - Floor: cannot edit - Status: editable
     * - Room Type: editable only if Controller allows
     *
     * @param room
     * @return Room object after update, null if room not found
     */
    public Room updateRoom(Room room) {
        Room found = getRoomByNumber(room.getRoomNumber());
        if (found == null) {
            return null;
        }
        try {
            String strSQL = """
                            update Rooms
                            set status = ?,
                                room_type_id = ?
                            where room_number = ?
                            """;

            stm = connection.prepareStatement(strSQL);

            stm.setString(1, room.getStatus());
            stm.setInt(2, room.getRoomTypeId());
            stm.setInt(3, room.getRoomNumber());
            stm.execute();
        } catch (Exception ex) {
            System.out.println("UpdateRoom:" + ex.getMessage());
        }
        return room;
    }

    /**
     * Get guests currently staying in room
     *
     * @param roomNumber
     * @return List GuestStay
     */
    public List<GuestStay> getGuestsByRoomNumber(int roomNumber) {
        List<GuestStay> guestList = new ArrayList<>();
        try {
            String strSQL = """
                            select
                            gs.stay_id, 
                            gs.booking_room_id, 
                            gs.full_name, 
                            gs.phone, 
                            gs.id_number 
                            from GuestStays gs 
                            inner join BookingRooms br on gs.booking_room_id = br.booking_room_id 
                            inner join Bookings b on br.booking_id = b.booking_id 
                            where br.room_number = ? 
                            and b.status = N'Đã xác nhận' 
                            and CAST(GETDATE() AS DATE) 
                            between b.checkin_date and b.checkout_date
                            """;

            stm = connection.prepareStatement(strSQL);
            stm.setInt(1, roomNumber);
            rs = stm.executeQuery();

            while (rs.next()) {
                GuestStay guest = new GuestStay(
                        rs.getInt("stay_id"),
                        rs.getInt("booking_room_id"),
                        rs.getString("full_name"),
                        rs.getString("phone"),
                        rs.getString("id_number")
                );
                guestList.add(guest);
            }
        } catch (Exception ex) {
            System.out.println("GetGuestsByRoomNumber:" + ex.getMessage());
        }
        return guestList;
    }

    /**
     * Get all floors for filter dropdown
     *
     * @return List floor number
     */
    public List<Integer> getAllFloors() {
        List<Integer> floors = new ArrayList<>();
        try {
            String strSQL = """
                            select distinct floor
                            from Rooms
                            order by floor
                            """;
            stm = connection.prepareStatement(strSQL);
            rs = stm.executeQuery();
            while (rs.next()) {
                floors.add(rs.getInt("floor"));
            }
        } catch (Exception ex) {
            System.out.println("GetAllFloors:" + ex.getMessage());
        }
        return floors;
    }
}
