package dto;

import java.util.List;
import java.util.Map;
import dto.BookingCheckInView; // Import đúng class View của ông
import model.GuestRequest;       // Import đúng model Request của ông

public class ReceptionistDashboard {

    // 1. Khối chứa số liệu đếm nhanh cho các ô Widget ở trên cùng
    private int totalArrivalsToday;
    private int totalDeparturesToday;
    private int totalInHouseGuests;
    private int pendingRequests;
    private Map<String, Integer> roomStatusCount;

    // 2. Khối chứa 3 danh sách dữ liệu thực tế để ông duyệt c:forEach đổ ra bảng
    private List<BookingCheckInView> checkInTodayList;  // List 1: Check-in hôm nay
    private List<BookingCheckInView> checkOutTodayList; // List 2: Check-out hôm nay
    private List<GuestRequest> pendingRequestsList;     // List 3: Yêu cầu chờ xử lý

    public int getTotalArrivalsToday() {
        return totalArrivalsToday;
    }

    public void setTotalArrivalsToday(int totalArrivalsToday) {
        this.totalArrivalsToday = totalArrivalsToday;
    }

    public int getTotalDeparturesToday() {
        return totalDeparturesToday;
    }

    public void setTotalDeparturesToday(int totalDeparturesToday) {
        this.totalDeparturesToday = totalDeparturesToday;
    }

    public int getTotalInHouseGuests() {
        return totalInHouseGuests;
    }

    public void setTotalInHouseGuests(int totalInHouseGuests) {
        this.totalInHouseGuests = totalInHouseGuests;
    }

    public int getPendingRequests() {
        return pendingRequests;
    }

    public void setPendingRequests(int pendingRequests) {
        this.pendingRequests = pendingRequests;
    }

    public Map<String, Integer> getRoomStatusCount() {
        return roomStatusCount;
    }

    public void setRoomStatusCount(Map<String, Integer> roomStatusCount) {
        this.roomStatusCount = roomStatusCount;
    }

    // Getter/Setter cho 3 List danh sách
    public List<BookingCheckInView> getCheckInTodayList() {
        return checkInTodayList;
    }

    public void setCheckInTodayList(List<BookingCheckInView> checkInTodayList) {
        this.checkInTodayList = checkInTodayList;
    }

    public List<BookingCheckInView> getCheckOutTodayList() {
        return checkOutTodayList;
    }

    public void setCheckOutTodayList(List<BookingCheckInView> checkOutTodayList) {
        this.checkOutTodayList = checkOutTodayList;
    }

    public List<GuestRequest> getPendingRequestsList() {
        return pendingRequestsList;
    }

    public void setPendingRequestsList(List<GuestRequest> pendingRequestsList) {
        this.pendingRequestsList = pendingRequestsList;
    }
}
