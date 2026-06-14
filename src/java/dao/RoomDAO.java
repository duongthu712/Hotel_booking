package dao;

import dal.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.GuestStay;
import model.Room;

/**
 * RoomDAO.java Data Processing Operator layer for rooms Provides CRUD with
 * Rooms table
 *
 * @author LinhLTHE200306
 * @version 2.0
 * @since 2026-06-10
 */
public class RoomDAO extends DBContext {

    public List<Room> getAllRooms() throws Exception {
        List<Room> rooms = new ArrayList<>();
        String strSQL = """
                        select *
                        from Rooms
                        order by floor, room_number
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL); ResultSet rs = stm.executeQuery()) {

            while (rs.next()) {
                int roomNumber = rs.getInt("room_number");
                int floor = rs.getInt("floor");
                String status = rs.getString("status");
                int roomTypeId = rs.getInt("room_type_id");

                Room room = new Room(roomNumber, floor, status, roomTypeId);
                rooms.add(room);
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy danh sách phòng.");
        }
        return rooms;
    }

    public Room getRoomByNumber(int roomNumber) throws Exception {
        String strSQL = """
                        select *
                        from Rooms
                        where room_number = ?
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setInt(1, roomNumber);

            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    int floor = rs.getInt("floor");
                    String status = rs.getString("status");
                    int roomTypeId = rs.getInt("room_type_id");

                    return new Room(roomNumber, floor, status, roomTypeId);
                } else {
                    throw new Exception("Phòng này không tồn tại trong hệ thống.");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Vui lòng thử lại sau.");
        }
    }

    public List<Room> searchAndFilterRooms(Integer floor, Integer roomTypeId, String keyword) throws Exception {
        List<Room> rooms = new ArrayList<>();
        StringBuilder strSQL = new StringBuilder("""
                            select *
                            from Rooms
                            where 1 = 1
                            """);

        if (floor != null) {
            strSQL.append(" and floor = ?");
        }
        if (roomTypeId != null) {
            strSQL.append(" and room_type_id = ?");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            strSQL.append(" and cast(room_number as varchar) like ?");
        }
        strSQL.append(" order by floor, room_number");

        try (PreparedStatement stm = connection.prepareStatement(strSQL.toString())) {
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

            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    int roomNumber = rs.getInt("room_number");
                    int f = rs.getInt("floor");
                    String status = rs.getString("status");
                    int rtId = rs.getInt("room_type_id");

                    Room room = new Room(roomNumber, f, status, rtId);
                    rooms.add(room);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể tìm kiếm phòng.");
        }
        return rooms;
    }

    public Room updateRoom(Room room) throws Exception {
        Room found = getRoomByNumber(room.getRoomNumber());
        if (found == null) {
            throw new Exception("Phòng này không tồn tại, không thể cập nhật.");
        }

        String strSQL = """
                        update Rooms
                        set status = ?,
                            room_type_id = ?
                        where room_number = ?
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setString(1, room.getStatus());
            stm.setInt(2, room.getRoomTypeId());
            stm.setInt(3, room.getRoomNumber());

            int rowCount = stm.executeUpdate();
            if (rowCount > 0) {
                return room;
            } else {
                throw new Exception("Cập nhật phòng thất bại.");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 547) {
                throw new Exception("Không thể cập nhật vì loại phòng không tồn tại.");
            }
            throw new Exception("Lỗi hệ thống: Không thể cập nhật phòng.");
        }
    }

    public List<GuestStay> getGuestsByRoomNumber(int roomNumber) throws Exception {
        List<GuestStay> guestList = new ArrayList<>();
        String strSQL = """
                        SELECT
                        gs.stay_id,
                        gs.booking_room_id, 
                        gs.full_name, 
                        gs.phone, 
                        gs.id_number 
                        FROM GuestStays gs 
                        INNER JOIN BookingRooms br ON gs.booking_room_id = br.booking_room_id 
                        INNER JOIN Bookings b ON br.booking_id = b.booking_id 
                        WHERE br.room_number = ? 
                        AND b.status = N'Đã nhận phòng'           
                        AND CAST(GETDATE() AS DATE) >= b.checkin_date   
                        AND CAST(GETDATE() AS DATE) < b.checkout_date;
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setInt(1, roomNumber);

            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    int stayId = rs.getInt("stay_id");
                    int bookingRoomId = rs.getInt("booking_room_id");
                    String fullName = rs.getString("full_name");
                    String phone = rs.getString("phone");
                    String idNumber = rs.getString("id_number");

                    GuestStay guest = new GuestStay(stayId, bookingRoomId, fullName, phone, idNumber);
                    guestList.add(guest);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy danh sách khách đang ở phòng.");
        }
        return guestList;
    }

    public List<Integer> getAllFloors() throws Exception {
        List<Integer> floors = new ArrayList<>();
        String strSQL = """
                        select distinct floor
                        from Rooms
                        order by floor
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL); ResultSet rs = stm.executeQuery()) {

            while (rs.next()) {
                floors.add(rs.getInt("floor"));
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy danh sách tầng.");
        }
        return floors;
    }
}
