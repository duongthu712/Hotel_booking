/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import java.util.Date;
/**
 *
 * @author Minh Thu
 */
public class HotelNews {

    private int newsId;
    private int hotelId;
    private String title;
    private String content;
    private String imageUrl;
    private boolean active;
    private Date createdAt;
    private int createdBy;

    public HotelNews() {
    }

    public HotelNews(int newsId, int hotelId, String title, String content, String imageUrl, boolean active, Date createdAt, int createdBy) {
        this.newsId = newsId;
        this.hotelId = hotelId;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.active = active;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
    }

    public int getNewsId() {
        return newsId;
    }

    public void setNewsId(int newsId) {
        this.newsId = newsId;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }
}