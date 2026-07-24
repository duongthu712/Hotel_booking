/**
 * Author: ThuDNM-HE204370
 * Date created: 07/06/2026
 * Purpose: Used to aggregate and display statistical metrics on the Receptionist Dashboard.
 */
package dto;

import java.util.List;
import java.util.Map;
import dto.BookingCheckInView;
import model.GuestRequest;

public class ReceptionistDashboard {

    // Các chỉ số thống kê (4 ô thông tin trên cùng)
    private int totalArrivalsToday;
    private int totalDeparturesToday;
    private int pendingDeposits;
    private int pendingRequests;
    
    // Key: Tên trạng thái (VD: "Empty", "Occupied"), Value: Số lượng phòng tương ứng
    private Map<String, Integer> roomStatusCount;

   // Danh sách chi tiết (2 bảng thông tin bên dưới)
    private List<BookingCheckInView> checkInTodayList;  // List 1: Check-in hôm nay
    private List<BookingCheckInView> checkOutTodayList; // List 2: Check-out hôm nay

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

    public void setPendingDeposits(int pendingDeposits) {
        this.pendingDeposits = pendingDeposits;
    }

    public int getPendingDeposits() {
        return pendingDeposits;
    }

   
}
