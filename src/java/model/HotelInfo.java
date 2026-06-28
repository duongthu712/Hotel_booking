package model;

import java.time.LocalTime; 
import java.util.ArrayList;
import java.util.List;

/**
 * Last update 16:50 27/06/2026
 *
 * @author LinhLTHE200306
 */
public class HotelInfo {

    private int hotelId;
    private String hotelName;
    private String description;
    private LocalTime checkinTime; // Chuyển từ String sang LocalTime
    private LocalTime checkoutTime; // Chuyển từ String sang LocalTime
    private String address;
    private String addressUrl;
    private String phone;
    private String email;
    private List<HotelImage> images;

    public void addImage(HotelImage image) {
        if (this.images == null) {
            this.images = new ArrayList<>();
        }
        this.images.add(image);
    }

    public void clearImages() {
        if (this.images != null) {
            this.images.clear();
        }
    }

    public HotelInfo() {
    }

    public HotelInfo(int hotelId, String hotelName, String description, LocalTime checkinTime, LocalTime checkoutTime, String address, String addressUrl, String phone, String email) {
        this.hotelId = hotelId;
        this.hotelName = hotelName;
        this.description = description;
        this.checkinTime = checkinTime;
        this.checkoutTime = checkoutTime;
        this.address = address;
        this.addressUrl = addressUrl;
        this.phone = phone;
        this.email = email;
    }

    public LocalTime getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(LocalTime checkinTime) {
        this.checkinTime = checkinTime;
    }

    public LocalTime getCheckoutTime() {
        return checkoutTime;
    }

    public void setCheckoutTime(LocalTime checkoutTime) {
        this.checkoutTime = checkoutTime;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressUrl() {
        return addressUrl;
    }

    public void setAddressUrl(String addressUrl) {
        this.addressUrl = addressUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<HotelImage> getImages() {
        return images;
    }

    public void setImages(List<HotelImage> images) {
        this.images = images;
    }
}
