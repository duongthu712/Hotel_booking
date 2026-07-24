package dao;

import dal.DBContext;
import dto.BookingCheckInView;
import dto.RoomStatusView;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.GuestStay;
import model.Room;

/**
 * @author LinhLTHE200306
 * @version 3.0
 * @since 2026-07-23
 */
public class RoomDAO extends DBContext {

    public List<Room> getAllRooms() throws Exception {
        List<Room> rooms = new ArrayList<>();
        String strSQL = """
                        select *
                        from Rooms
                        where is_active = 1
                        order by floor, room_number
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL); ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                rooms.add(new Room(rs.getInt("room_id"), rs.getInt("room_number"),
                        rs.getInt("floor"), rs.getString("status"), rs.getInt("room_type_id")));
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy danh sách phòng.");
        }
        return rooms;
    }

    public Room getRoomById(int roomId) throws Exception {
        String sql = "select * from Rooms where room_id = ? and is_active = 1";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, roomId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return new Room(rs.getInt("room_id"), rs.getInt("room_number"),
                            rs.getInt("floor"), rs.getString("status"), rs.getInt("room_type_id"));
                }
                throw new Exception("Phòng không tồn tại.");
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy thông tin phòng.");
        }
    }

    public Room getRoomByNumber(int roomNumber) throws Exception {
        String strSQL = """
                        select *
                        from Rooms
                        where room_number = ? 
                        and is_active = 1
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setInt(1, roomNumber);

            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    int roomId = rs.getInt("room_id");
                    int floor = rs.getInt("floor");
                    String status = rs.getString("status");
                    int roomTypeId = rs.getInt("room_type_id");

                    return new Room(roomId, roomNumber, floor, status, roomTypeId);
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
        StringBuilder strSQL = new StringBuilder("select * from Rooms where is_active = 1");

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
                    rooms.add(new Room(rs.getInt("room_id"), rs.getInt("room_number"),
                            rs.getInt("floor"), rs.getString("status"), rs.getInt("room_type_id")));
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể tìm kiếm phòng.");
        }
        return rooms;
    }

    public Room updateRoom(int roomId, Room room) throws Exception {
        if (room.getRoomNumber() != 0) {
            String checkSql = """
                select count(*) from Rooms where room_number = ? and room_id != ? and is_active = 1 
                """;
            try (PreparedStatement stm = connection.prepareStatement(checkSql)) {
                stm.setInt(1, room.getRoomNumber());
                stm.setInt(2, roomId);
                try (ResultSet rs = stm.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new Exception("Số phòng " + room.getRoomNumber() + " đã tồn tại trong hệ thống.");
                    }
                }
            } catch (SQLException e) {
                throw new Exception("Lỗi hệ thống: Không thể kiểm tra số phòng.");
            }
        }

        String sql = """
            update Rooms
            set room_number = ?,
                floor = ?,
                status = ?,
                room_type_id = ?
            where room_id = ?
            """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, room.getRoomNumber());
            stm.setInt(2, room.getFloor());
            stm.setString(3, room.getStatus());
            stm.setInt(4, room.getRoomTypeId());
            stm.setInt(5, roomId);
            int rows = stm.executeUpdate();
            if (rows > 0) {
                return room;
            }
            throw new Exception("Cập nhật phòng thất bại.");
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
                        WHERE br.room_id = ? 
                        AND b.status = N'Đã nhận phòng'     
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setInt(1, getRoomByNumber(roomNumber).getRoomId());

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
                         where is_active = 1 
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

    public boolean createRoom(int roomNumber, int floor, int roomTypeId) throws Exception {
        String checkSql = """
            select count(*) from Rooms where room_number = ? and is_active = 1
            """;
        try (PreparedStatement stm = connection.prepareStatement(checkSql)) {
            stm.setInt(1, roomNumber);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new Exception("Số phòng " + roomNumber + " đã tồn tại trong hệ thống.");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể kiểm tra số phòng.");
        }

        String sql = """
            insert into Rooms (room_number, floor, room_type_id, status)
            values (?, ?, ?, N'Phòng trống')
            """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, roomNumber);
            stm.setInt(2, floor);
            stm.setInt(3, roomTypeId);
            return stm.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 547) {
                throw new Exception("Loại phòng không tồn tại trong hệ thống.");
            }
            throw new Exception("Lỗi hệ thống: Không thể tạo phòng.");
        }
    }

    public boolean deleteRoom(int roomId) throws Exception {
        String checkStatusSql = "SELECT status FROM Rooms WHERE room_id = ?";
        try (PreparedStatement stm = connection.prepareStatement(checkStatusSql)) {
            stm.setInt(1, roomId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("status");
                    if ("Phòng có khách".equals(status)) {
                        throw new Exception("Không thể xóa phòng đang có khách lưu trú.");
                    }
                }
            }
        }

        String sql = "UPDATE Rooms SET is_active = 0 WHERE room_id = ?";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, roomId);
            int rows = stm.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể xóa phòng.");
        }
    }

    // Author: ThuDNM-HE204370
    // Gán phòng
   // Gán phòng linh hoạt 
    public boolean processRoomAssignment(int bookingId, int roomId, String[] fullNames, String[] phones, String[] idNumbers, int totalRequiredRooms) throws SQLException {
        String updateRoomSql = "UPDATE Rooms SET [status] = N'Phòng có khách' WHERE room_id = ?";
        String insertBookingRoomSql = "INSERT INTO BookingRooms (booking_id, room_id, assigned_at) VALUES (?, ?, GETDATE())";
        String insertGuestStaySql = "INSERT INTO GuestStays (booking_room_id, full_name, phone, id_number) VALUES (?, ?, ?, ?)";
        String updateBookingSql = "UPDATE Bookings SET [status] = N'Đã nhận phòng', actual_checkin_time = GETDATE() WHERE booking_id = ?";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement psRoom = connection.prepareStatement(updateRoomSql)) {
                psRoom.setInt(1, roomId);
                psRoom.executeUpdate();
            }

            int generatedBookingRoomId = -1;
            try (PreparedStatement psBookingRoom = connection.prepareStatement(insertBookingRoomSql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                psBookingRoom.setInt(1, bookingId);
                psBookingRoom.setInt(2, roomId);
                psBookingRoom.executeUpdate();
                try (ResultSet rsKeys = psBookingRoom.getGeneratedKeys()) {
                    if (rsKeys.next()) {
                        generatedBookingRoomId = rsKeys.getInt(1);
                    }
                }
            }

            if (generatedBookingRoomId != -1 && fullNames != null && fullNames.length > 0) {
                try (PreparedStatement psGuestStay = connection.prepareStatement(insertGuestStaySql)) {
                    for (int i = 0; i < fullNames.length; i++) {
                        
                        // Nếu tên bị bỏ trống, mặc định ép gán thành dấu "-" để database lưu và render đủ số dòng STT
                        String actualName = "-";
                        if (fullNames[i] != null && !fullNames[i].trim().isEmpty()) {
                            actualName = fullNames[i].trim();
                        }

                        psGuestStay.setInt(1, generatedBookingRoomId);
                        psGuestStay.setNString(2, actualName);

                        if (phones != null && i < phones.length && phones[i] != null && !phones[i].trim().isEmpty()) {
                            psGuestStay.setString(3, phones[i].trim());
                        } else {
                            psGuestStay.setNull(3, java.sql.Types.VARCHAR);
                        }

                        if (idNumbers != null && i < idNumbers.length && idNumbers[i] != null && !idNumbers[i].trim().isEmpty()) {
                            psGuestStay.setString(4, idNumbers[i].trim());
                        } else {
                            psGuestStay.setNull(4, java.sql.Types.VARCHAR);
                        }

                        psGuestStay.executeUpdate();
                    }
                }
            } else if (generatedBookingRoomId == -1) {
                throw new SQLException("Không thể tạo liên kết phòng tại BookingRooms.");
            }

            int currentlyAssignedCount = 0;
            String checkCountSql = "SELECT COUNT(*) FROM BookingRooms WHERE booking_id = ?";
            try (PreparedStatement psCheck = connection.prepareStatement(checkCountSql)) {
                psCheck.setInt(1, bookingId);
                try (ResultSet rsCheck = psCheck.executeQuery()) {
                    if (rsCheck.next()) {
                        currentlyAssignedCount = rsCheck.getInt(1);
                    }
                }
            }

            if (currentlyAssignedCount >= totalRequiredRooms) {
                try (PreparedStatement psBooking = connection.prepareStatement(updateBookingSql)) {
                    psBooking.setInt(1, bookingId);
                    psBooking.executeUpdate();
                }
            }

            connection.commit();
            return true;
        } catch (Exception e) {
            System.out.println("Lỗi xử lý Transaction gán phòng cuốn chiếu: " + e.getMessage());
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
            }
        }
    }
    // Author: ThuDNM-HE204370
    // 2. Lấy thông tin chi tiết đơn hàng để hiển thị lên Form Check-in
    public BookingCheckInView getBookingForCheckInById(int bookingId) {
        String sql = "SELECT b.booking_id, b.booking_code, b.num_rooms, b.num_guests, b.num_children, b.payment_status, b.deposit_amount, "
                + "b.[status], b.actual_checkin_time, "
                + "g.guest_id, g.full_name, g.phone, g.email, g.id_number, g.date_of_birth, g.nationality, "
                + "rt.room_type_id, rt.type_name, rt.capacity, "
                + "rt.num_guests AS rt_num_guests, rt.num_children AS rt_num_children, " // Lấy sức chứa gốc của đúng 1 phòng
                + "r.request_type, r.request_details, r.status AS request_status, "
                + "CONVERT(VARCHAR(19), r.requested_checkin, 120) AS requested_checkin "
                + "FROM Bookings b "
                + "LEFT JOIN Guests g ON b.guest_id = g.guest_id "
                + "INNER JOIN RoomTypes rt ON b.room_type_id = rt.room_type_id "
                + "LEFT JOIN GuestRequests r ON b.booking_id = r.booking_id "
                + "WHERE b.booking_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BookingCheckInView b = new BookingCheckInView();
                    b.setBookingId(rs.getInt("booking_id"));
                    b.setBookingCode(rs.getString("booking_code"));
                    b.setNumRooms(rs.getInt("num_rooms"));
                    
                    // Thông tin số lượng khách thực tế của cả đơn hàng
                    b.setNumGuests(rs.getInt("num_guests"));       // Tổng số người lớn đặt đơn
                    b.setNumChildren(rs.getInt("num_children"));   // Tổng số trẻ em đặt đơn
                    
                    b.setPaymentStatus(rs.getString("payment_status"));
                    b.setDepositAmount(rs.getBigDecimal("deposit_amount"));
                    b.setStatus(rs.getString("status"));
                    b.setActualCheckInTime(rs.getString("actual_checkin_time"));
                    b.setGuestId(rs.getInt("guest_id"));
                    b.setGuestFullName(rs.getString("full_name"));
                    b.setGuestPhone(rs.getString("phone"));
                    b.setGuestEmail(rs.getString("email"));
                    b.setIdNumber(rs.getString("id_number"));
                    b.setDateOfBirth(rs.getDate("date_of_birth"));
                    b.setNationality(rs.getString("nationality"));
                    b.setRoomTypeId(rs.getInt("room_type_id"));
                    b.setRoomTypeName(rs.getString("type_name"));
                    b.setCapacity(rs.getInt("capacity"));
                    
                    // Sức chứa tiêu chuẩn của duy nhất 1 phòng phục vụ logic so sánh nhân chuỗi ở UI/JS
                    b.setMaxAdults(rs.getInt("rt_num_guests"));
                    b.setMaxChildren(rs.getInt("rt_num_children"));
                    
                    b.setRequestType(rs.getString("request_type"));
                    b.setRequestDetails(rs.getString("request_details"));
                    b.setRequestStatus(rs.getString("request_status"));
                    b.setRequestedCheckIn(rs.getString("requested_checkin"));
                    return b;
                }
            }
        } catch (Exception e) {
            System.out.println("Lỗi getBookingForCheckInById: " + e.getMessage());
        }
        return null;
    }

    // Author: ThuDNM-HE204370
    // Lấy sơ đồ trạng thái phòng
    public List<RoomStatusView> getAllRoomStatusViews(int targetRoomTypeId, String filterRoomTypeName, String filterFloor) {
        List<RoomStatusView> list = new ArrayList<>();

        String sql = "SELECT r.room_id, r.room_number, r.floor, r.room_type_id, rt.type_name, rt.capacity, r.[status], "
                + "MAX(b.booking_code) AS booking_code, "
                + "STRING_AGG(CAST(g.full_name AS NVARCHAR(MAX)), ', ') AS guest_name, "
                + "STRING_AGG(CAST(ISNULL(g.phone, N'Không có') AS NVARCHAR(MAX)), ', ') AS guest_phone, "
                + "STRING_AGG(CAST(ISNULL(g.id_number, N'Không có') AS NVARCHAR(MAX)), ', ') AS guest_id_number "
                + "FROM Rooms r "
                + "INNER JOIN RoomTypes rt ON r.room_type_id = rt.room_type_id "
                + "LEFT JOIN BookingRooms br ON r.room_id = br.room_id "
                + "LEFT JOIN Bookings b ON br.booking_id = b.booking_id AND b.[status] IN (N'Đã xác nhận', N'Đã nhận phòng') "
                + "LEFT JOIN GuestStays g ON br.booking_room_id = g.booking_room_id "
                + "WHERE 1=1 ";

        if (targetRoomTypeId > 0) {
            sql += " AND r.room_type_id = ? ";
        } else if (filterRoomTypeName != null && !filterRoomTypeName.equals("all") && !filterRoomTypeName.isEmpty()) {
            sql += " AND rt.type_name = ? ";
        }

        if (filterFloor != null && !filterFloor.equals("all") && !filterFloor.isEmpty()) {
            sql += " AND r.floor = ? ";
        }

        sql += " GROUP BY r.room_id, r.room_number, r.floor, r.room_type_id, rt.type_name, rt.capacity, r.[status] "
                + " ORDER BY r.room_number ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int idx = 1;

            if (targetRoomTypeId > 0) {
                ps.setInt(idx++, targetRoomTypeId);
            } else if (filterRoomTypeName != null && !filterRoomTypeName.equals("all") && !filterRoomTypeName.isEmpty()) {
                ps.setString(idx++, filterRoomTypeName);
            }

            if (filterFloor != null && !filterFloor.equals("all") && !filterFloor.isEmpty()) {
                ps.setInt(idx++, Integer.parseInt(filterFloor));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RoomStatusView room = new RoomStatusView();
                    room.setRoomId(rs.getInt("room_id"));
                    room.setRoomNumber(rs.getInt("room_number"));
                    room.setFloor(rs.getInt("floor"));
                    room.setRoomTypeId(rs.getInt("room_type_id"));
                    room.setRoomTypeName(rs.getString("type_name"));
                    room.setStatus(rs.getString("status"));
                    room.setCurrentBookingCode(rs.getString("booking_code"));
                    room.setGuestFullName(rs.getString("guest_name"));
                    room.setCapacity(rs.getInt("capacity"));
                    room.setGuestPhone(rs.getString("guest_phone"));
                    room.setGuestIdNumber(rs.getString("guest_id_number"));
                    list.add(room);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Author: ThuDNM-HE204370
    // Lấy danh sách tên tất cả các hạng phòng đang hoạt động
    public List<String> getAllActiveRoomTypeNames() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT type_name FROM RoomTypes WHERE is_active = 1 ORDER BY type_name ASC";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString("type_name"));
            }
        } catch (Exception e) {
            System.out.println("Lỗi getAllActiveRoomTypeNames: " + e.getMessage());
        }
        return list;
    }

    // Author: ThuDNM-HE204370
    // Lấy danh sách tất cả các tầng thực tế đang có trong bảng Rooms
    public List<Integer> getAllExistingFloors() {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT DISTINCT floor FROM Rooms ORDER BY floor ASC";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getInt("floor"));
            }
        } catch (Exception e) {
            System.out.println("Lỗi getAllExistingFloors: " + e.getMessage());
        }
        return list;
    }

    // Author: ThuDNM-HE204370
    // Lấy lại phòng đã gán
  public boolean unassignRoom(int bookingId, int roomId) throws SQLException {
        String getBookingRoomIdSql = "SELECT booking_room_id FROM BookingRooms WHERE booking_id = ? AND room_id = ?";
        String deleteGuestStaysSql = "DELETE FROM GuestStays WHERE booking_room_id = ?";
        String deleteBookingRoomSql = "DELETE FROM BookingRooms WHERE booking_room_id = ?";
        String updateRoomSql = "UPDATE Rooms SET [status] = N'Phòng trống' WHERE room_id = ?";
        String rollbackBookingStatusSql = "UPDATE Bookings SET [status] = N'Đã xác nhận', actual_checkin_time = NULL WHERE booking_id = ? AND [status] = N'Đã nhận phòng'";

        int targetBookingRoomId = -1;

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement psGet = connection.prepareStatement(getBookingRoomIdSql)) {
                psGet.setInt(1, bookingId);
                psGet.setInt(2, roomId);
                try (ResultSet rs = psGet.executeQuery()) {
                    if (rs.next()) {
                        targetBookingRoomId = rs.getInt("booking_room_id");
                    }
                }
            }

            if (targetBookingRoomId != -1) {
                try (PreparedStatement psDelGuest = connection.prepareStatement(deleteGuestStaysSql)) {
                    psDelGuest.setInt(1, targetBookingRoomId);
                    psDelGuest.executeUpdate();
                }

                try (PreparedStatement psDelRoom = connection.prepareStatement(deleteBookingRoomSql)) {
                    psDelRoom.setInt(1, targetBookingRoomId);
                    psDelRoom.executeUpdate();
                }

                try (PreparedStatement psUpdRoom = connection.prepareStatement(updateRoomSql)) {
                    psUpdRoom.setInt(1, roomId);
                    psUpdRoom.executeUpdate();
                }

                try (PreparedStatement psUpdBooking = connection.prepareStatement(rollbackBookingStatusSql)) {
                    psUpdBooking.setInt(1, bookingId);
                    psUpdBooking.executeUpdate();
                }
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
            }
        }
    }
    // Author: ThuDNM-HE204370
    // BỔ SUNG: Lấy danh sách toàn bộ hạng phòng để phục vụ đổi hạng khi phòng hỏng
    public List<dto.RoomStatusView> getAllActiveRoomTypesForDropdown() {
        List<dto.RoomStatusView> list = new ArrayList<>();
        // Chỉ cần lấy id và tên hạng phòng từ bảng RoomTypes
        String sql = "SELECT room_type_id, type_name FROM RoomTypes WHERE is_active = 1 ORDER BY type_name ASC";
        
        try (PreparedStatement ps = connection.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                dto.RoomStatusView type = new dto.RoomStatusView();
                type.setRoomTypeId(rs.getInt("room_type_id"));
                type.setRoomTypeName(rs.getNString("type_name"));
                list.add(type);
            }
        } catch (Exception e) {
            System.out.println("Lỗi tại getAllActiveRoomTypesForDropdown: " + e.getMessage());
        }
        return list;
    }
    // Author: ThuDNM-HE204370
    // BỔ SUNG: Cập nhật lại sức chứa hiển thị (maxAdults/maxChildren) khi lễ tân chọn hạng phòng khác hạng gốc
    public void overrideBookingCapacityWithType(dto.BookingCheckInView booking, int overriddenRoomTypeId) {
        String sql = "SELECT num_guests, num_children, type_name FROM RoomTypes WHERE room_type_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, overriddenRoomTypeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Ép lại thông tin sức chứa của hạng phòng mới để hiển thị & JS kiểm tra không bị lệch
                    booking.setMaxAdults(rs.getInt("num_guests"));
                    booking.setMaxChildren(rs.getInt("num_children"));
                    booking.setRoomTypeName(rs.getNString("type_name"));
                    booking.setRoomTypeId(overriddenRoomTypeId);
                }
            }
        } catch (Exception e) {
            System.out.println("Lỗi tại overrideBookingCapacityWithType: " + e.getMessage());
        }
    }
}
