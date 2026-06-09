/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Minh Thu
 */
public class RoomTypeImage {

    private int imageId;
    private String imageUrl;
    private int roomTypeId;

    public RoomTypeImage() {
    }

    public RoomTypeImage(int imageId, String imageUrl, int roomTypeId) {
        this.imageId = imageId;
        this.imageUrl = imageUrl;
        this.roomTypeId = roomTypeId;
    }

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

    public int getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(int roomTypeId) {
        this.roomTypeId = roomTypeId;
    }
}
