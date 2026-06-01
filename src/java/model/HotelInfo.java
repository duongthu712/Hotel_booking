/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Minh Thu
 */
public class HotelInfo {

    private int hotelId;
    private String hotelName;
    private String description;
    private String amenities;
    private String policies;
    private String checkinTime;
    private String checkoutTime;
    private String address;
    private String phone;
    private String email;

    public HotelInfo() {
    }

    public HotelInfo(int hotelId, String hotelName, String description, String amenities, String policies, String checkinTime, String checkoutTime, String address, String phone, String email) {
        this.hotelId = hotelId;
        this.hotelName = hotelName;
        this.description = description;
        this.amenities = amenities;
        this.policies = policies;
        this.checkinTime = checkinTime;
        this.checkoutTime = checkoutTime;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }

    // Viết đầy đủ Getter/Setter cho các thuộc tính
    public int getHotelId() {
        return hotelId;
    }

    public String getHotelName() {
        return hotelName;
    }

    public String getDescription() {
        return description;
    }

    public String getAmenities() {
        return amenities;
    }

    public String getPolicies() {
        return policies;
    }

    public String getCheckinTime() {
        return checkinTime;
    }

    public String getCheckoutTime() {
        return checkoutTime;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public void setPolicies(String policies) {
        this.policies = policies;
    }

    public void setCheckinTime(String checkinTime) {
        this.checkinTime = checkinTime;
    }

    public void setCheckoutTime(String checkoutTime) {
        this.checkoutTime = checkoutTime;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
