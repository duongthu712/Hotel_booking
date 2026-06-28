package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Last update 16:55 27/06/2026
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
    private LocalDateTime createdAt;
    private int createdBy;

    public HotelNews() {
    }

    public HotelNews(int newsId, int hotelId, String title, String content, String imageUrl, boolean active, LocalDateTime createdAt, int createdBy) {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getCreatedAtFormatted() {
    if (createdAt == null) {
        return "";
    }
    return createdAt.format(
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    );
}
}
