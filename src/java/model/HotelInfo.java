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
    private String phone;
    private String email;
    private List<String> imageUrl;
    private List<String> imageCaption;

    //Method addImage into hotel image list
    public void addImage(String url, String caption) {
        if (imageUrl == null) {
            imageUrl = new ArrayList<>();
            imageCaption = new ArrayList<>();
        }
        imageUrl.add(url);
        imageCaption.add(caption);
    }

    //Method clearImages clears all image urls and captions in the list
    public void clearImages() {
        if (imageUrl != null) {
            imageUrl.clear();
        }
        if (imageCaption != null) {
            imageCaption.clear();
        }
    }

    //Constructor
    public HotelInfo() {

    }

    public HotelInfo(int hotelId, String hotelName, String description, String checkinTime, String checkoutTime, String address, String phone, String email) {
        this.hotelId = hotelId;
        this.hotelName = hotelName;
        this.description = description;
        this.checkinTime = checkinTime;
        this.checkoutTime = checkoutTime;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }

    //Getter & Setter
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

    public List<String> getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(List<String> imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getImageCaption() {
        return imageCaption;
    }

    public void setImageCaption(List<String> imageCaption) {
        this.imageCaption = imageCaption;
    }
}
