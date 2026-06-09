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
    private String imageUrl;
    private int hotelId;
    private String caption;
    private String imageType;

    public HotelImage() {
    }

    public HotelImage(int imageId, String imageUrl, int hotelId, String caption, String imageType) {
        this.imageId = imageId;
        this.imageUrl = imageUrl;
        this.hotelId = hotelId;
        this.caption = caption;
        this.imageType = imageType;
    }

    // Getters and Setters
    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }
}
