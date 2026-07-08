/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

/**
 * Dùng để chứa kết quả kiểm tra trước khi ngừng kinh doanh (deactive) một hạng phòng.
 * @author Minh Thu
 */
public class DeactiveValidationResult {
    private int stayingCount;
    private int futureCount;
    private boolean blocked;

    public DeactiveValidationResult(int stayingCount, int futureCount) {
        this.stayingCount = stayingCount;
        this.futureCount = futureCount;
        // Nếu có bất kỳ phòng nào đang ở hoặc đơn nào trong tương lai thì bị chặn
        this.blocked = (stayingCount > 0 || futureCount > 0);
    }

    public int getStayingCount() {
        return stayingCount;
    }

    public void setStayingCount(int stayingCount) {
        this.stayingCount = stayingCount;
    }

    public int getFutureCount() {
        return futureCount;
    }

    public void setFutureCount(int futureCount) {
        this.futureCount = futureCount;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}