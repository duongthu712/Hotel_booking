package model;

import java.util.ArrayList;
import java.util.List;

public class HotelInfo {

   private int hotelId;
    private String hotelName;
    private String description;
    private String checkinTime; 
    private String checkoutTime;
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

    public HotelInfo(int hotelId, String hotelName, String description, String checkinTime, String checkoutTime, String address, String addressUrl, String phone, String email) {
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

    public String getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(String checkinTime) {
        this.checkinTime = checkinTime;
    }

    public String getCheckoutTime() {
        return checkoutTime;
    }

    public void setCheckoutTime(String checkoutTime) {
        this.checkoutTime = checkoutTime;
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
