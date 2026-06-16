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
    public List<dto.RoomStatusView> getAllRoomStatusViews() throws Exception {
        List<dto.RoomStatusView> list = new ArrayList<>();
        String strSQL = """
                        SELECT 
                            r.room_number, 
                            r.[floor], 
                            r.[status] AS room_status, 
                            rt.room_type_id, 
                            rt.[type_name] AS room_type_name, 
                            b.booking_id, 
                            b.booking_code, 
                            ISNULL(STRING_AGG(CAST(gs.full_name AS NVARCHAR(MAX)), ', '), N'') AS guests_in_room 
                        FROM Rooms r 
                        JOIN RoomTypes rt ON r.room_type_id = rt.room_type_id 
                        LEFT JOIN BookingRooms br ON r.room_number = br.room_number 
                        LEFT JOIN Bookings b ON br.booking_id = b.booking_id AND b.[status] = N'Đã nhận phòng' 
                        LEFT JOIN GuestStays gs ON br.booking_room_id = gs.booking_room_id 
                        GROUP BY 
                            r.room_number, r.[floor], r.[status], 
                            rt.room_type_id, rt.[type_name], b.booking_id, b.booking_code 
                        ORDER BY r.[floor], r.room_number ASC
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL); 
             ResultSet rs = stm.executeQuery()) {

            while (rs.next()) {
                dto.RoomStatusView view = new dto.RoomStatusView();
                view.setRoomNumber(rs.getInt("room_number"));
                view.setFloor(rs.getInt("floor"));
                view.setStatus(rs.getString("room_status"));
                view.setRoomTypeId(rs.getInt("room_type_id"));
                view.setRoomTypeName(rs.getString("room_type_name"));
                
                int bookingId = rs.getInt("booking_id");
                if (!rs.wasNull()) {
                    view.setCurrentBookingId(bookingId);
                    view.setCurrentBookingCode(rs.getString("booking_code"));
                    view.setGuestFullName(rs.getString("guests_in_room"));
                } else {
                    view.setCurrentBookingId(null);
                    view.setCurrentBookingCode("");
                    view.setGuestFullName("");
                }
                list.add(view);
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy ma trận trạng thái phòng. " + e.getMessage());
        }
        return list;
    }

    public boolean assignRoomAndCheckIn(int bookingId, int roomNumber) throws Exception {
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps3 = null;
        
        try {
            // Tắt auto-commit để kích hoạt Transaction, đảm bảo an toàn tuyệt đối cho DB
            connection.setAutoCommit(false); 

            // 1. Ghi nhận số phòng cụ thể vào đơn đặt ở bảng trung gian BookingRooms
            String sqlBookingRoom = "INSERT INTO BookingRooms (booking_id, room_number, assigned_at) VALUES (?, ?, GETDATE())";
            ps1 = connection.prepareStatement(sqlBookingRoom);
            ps1.setInt(1, bookingId);
            ps1.setInt(2, roomNumber);
            ps1.executeUpdate();

            // 2. Chuyển trạng thái của chính phòng này sang 'Phòng có khách'
            String sqlUpdateRoom = "UPDATE Rooms SET [status] = N'Phòng có khách' WHERE room_number = ?";
            ps2 = connection.prepareStatement(sqlUpdateRoom);
            ps2.setInt(1, roomNumber);
            ps2.executeUpdate();

            // 3. Cập nhật trạng thái của tổng đơn đặt phòng sang 'Đã nhận phòng'
            String sqlUpdateBooking = "UPDATE Bookings SET [status] = N'Đã nhận phòng', actual_checkin_time = GETDATE() WHERE booking_id = ?";
            ps3 = connection.prepareStatement(sqlUpdateBooking);
            ps3.setInt(1, bookingId);
            ps3.executeUpdate();

            // Tất cả mượt mà -> Commit xuống DB thực tế
            connection.commit(); 
            return true;
            
        } catch (SQLException e) {
            if (connection != null) {
                try { 
                    connection.rollback(); // Có biến cố xảy ra -> Trả lại dữ liệu nguyên vẹn ban đầu
                } catch (SQLException ex) { 
                    ex.printStackTrace(); 
                }
            }
            throw new Exception("Lỗi hệ thống: Quá trình gán phòng thất bại. " + e.getMessage());
        } finally {
            // Đóng các Statement tạm để giải phóng tài nguyên server
            try { if (ps1 != null) ps1.close(); if (ps2 != null) ps2.close(); if (ps3 != null) ps3.close(); } catch (SQLException e) {}
            // Bật lại trạng thái autoCommit cho các hàm CRUD thông thường khác chạy sau không bị ảnh hưởng
            try { connection.setAutoCommit(true); } catch (SQLException e) {}
        }
    }
    
}
