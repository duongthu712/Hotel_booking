/**
 * Author: ThuDNM-HE204370
 * Date created: 22/06/2026
 * Purpose: Used to return and display validation results (number of staying/future bookings) when the Manager wants to delete/deactivate a room type (used in the warning Pop-up on the Room Type Management page).
 */
package dto;
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