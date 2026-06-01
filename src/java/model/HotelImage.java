/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Minh Thu
 */
public class HotelImage {

    private int imageId;
    private int hotelId;
    private String imageUrl;
    private String caption;

    public HotelImage() {
    }

    public HotelImage(int imageId, int hotelId, String imageUrl, String caption) {
        this.imageId = imageId;
        this.hotelId = hotelId;
        this.imageUrl = imageUrl;
        this.caption = caption;
    }

    // Các hàm Getter và Setter ở đây...
    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
