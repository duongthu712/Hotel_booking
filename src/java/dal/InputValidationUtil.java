package dal;

import dao.RoomDAO;
import dao.RoomTypeDAO;
import java.math.BigDecimal;
import java.util.regex.Pattern;
import model.RoomType;

/**
 * @author LinhLTHE200306
 * @version 2.5
 * @since 2026-06-13
 */
public class InputValidationUtil {

    private static final Pattern EMAIL_PATTERN
            = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    private static final Pattern PHONE_PATTERN
            = Pattern.compile("^(03|05|07|08|09)\\d{8}$");

    private static final Pattern NAME_PATTERN
            = Pattern.compile("^[\\p{L}\\s]+$");

    public static String validateStaffInput(String fullName, String email, String phone) {

        if (!NAME_PATTERN.matcher(fullName).matches()) {
            return "Họ và tên không được chứa số hoặc ký tự đặc biệt.";
        }

        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return "Định dạng email không hợp lệ.";
        }

        if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
            return "Số điện thoại phải gồm 10 chữ số và bắt đầu bằng đầu số hợp lệ (03/05/07/08/09).";
        }

        return null;
    }

    public static String capitalizeWords(String str) {
        if (str == null || str.trim().isEmpty()) {
            return "";
        }

        String[] words = str.trim().toLowerCase().split("\\s+");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        return result.toString().trim();
    }

    public static String validateServiceInput(String serviceName, String unitPriceRaw) {

        String trimmedName = serviceName.trim();
        if (trimmedName.length() < 2 || trimmedName.length() > 100) {
            return "Tên dịch vụ phải có độ dài từ 2 đến 100 ký tự.";
        }

        if (!trimmedName.matches("^[\\p{L}\\p{N}\\s_\\-]+$")) {
            return "Tên dịch vụ không được chứa ký tự đặc biệt.";
        }

        try {
            BigDecimal price = new BigDecimal(unitPriceRaw.trim());

            if (price.compareTo(BigDecimal.ZERO) < 0) {
                return "Giá dịch vụ phải lớn hơn hoặc bằng 0.";
            }

            BigDecimal maxLimit = new BigDecimal("9999999999993.99");
            if (price.compareTo(maxLimit) > 0) {
                return "Giá dịch vụ quá lớn, vui lòng kiểm tra lại.";
            }

        } catch (NumberFormatException e) {
            return "Giá dịch vụ phải là một số hợp lệ.";
        }

        return null;
    }

    public static String validateHotelInput(String email, String phone) {
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return "Định dạng email không hợp lệ.";
        }

        if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
            return "Số điện thoại phải gồm 10 chữ số và bắt đầu bằng đầu số hợp lệ (03/05/07/08/09).";
        }

        return null;
    }

    public static String validateCreateRoom(int roomNumber, int floor, int roomTypeId,
            RoomDAO roomDao) {

        //Số phòng phải có 3-4 chữ số (format khách sạn: 101, 205, 1001...)
        // Tầng + số phòng phải khớp: phòng 101 phải ở tầng 1
        int expectedFloor = roomNumber / 100;
        if (floor != expectedFloor) {
            return "Số phòng " + roomNumber + " không khớp với tầng " + floor
                    + " (phòng " + roomNumber + " thuộc tầng " + expectedFloor + ")";
        }

        // Số phòng không được trùng
        try {
            roomDao.getRoomByNumber(roomNumber);
            return "Số phòng " + roomNumber + " đã tồn tại";
        } catch (Exception e) {
            // Phòng chưa tồn tại
        }

        //Số phòng trong 1 tầng không quá 100 (101-199)
        int roomInFloor = roomNumber % 100;
        if (roomInFloor > 99 || roomInFloor < 1) {
            return "Số phòng trong tầng phải từ 01-99 (ví dụ: 101, 102... 199)";
        }

        return null;
    }

    public static String validateEditRoom(int roomNumber, int newRoomNumber, int floor,
            String oldStatus, String newStatus, int oldRoomTypeId, int newRoomTypeId,
            RoomDAO roomDao) {

        if ("Phòng có khách".equals(oldStatus)) {
            return "Không thể thay đổi trạng thái phòng đang có khách";
        }

        if (!oldStatus.equals(newStatus)) {
            if ("Phòng trống".equals(oldStatus)
                    && !"Đang dọn dẹp".equals(newStatus)
                    && !"Đang bảo trì".equals(newStatus)) {
                return "Phòng trống chỉ có thể chuyển sang Đang dọn dẹp hoặc Đang bảo trì";
            }

            if ("Đang dọn dẹp".equals(oldStatus)
                    && !"Phòng trống".equals(newStatus)
                    && !"Đang bảo trì".equals(newStatus)) {
                return "Phòng đang dọn dẹp chỉ có thể chuyển sang Phòng trống hoặc Đang bảo trì";
            }

            if ("Đang bảo trì".equals(oldStatus)
                    && !"Phòng trống".equals(newStatus)
                    && !"Đang dọn dẹp".equals(newStatus)) {
                return "Phòng đang bảo trì chỉ có thể chuyển sang Phòng trống hoặc Đang dọn dẹp";
            }
        }

        if (oldRoomTypeId != newRoomTypeId && !"Đang bảo trì".equals(oldStatus)) {
            return "Chỉ được thay đổi hạng phòng khi phòng đang bảo trì";
        }

        if (newRoomNumber != roomNumber) {
            return validateCreateRoom(newRoomNumber, floor, newRoomTypeId, roomDao);
        }

        return null;
    }

    private static final String[] VALID_POLICY_TYPES = {
        "Nhận/Trả phòng", "Hủy đặt phòng", "Vật nuôi", "Hút thuốc", "Thanh toán", "Khác"
    };

    public static String validatePolicyInput(String policyName, String policyType, String description) {

        String trimmedName = policyName.trim();
        if (trimmedName.length() < 2 || trimmedName.length() > 100) {
            return "Tên chính sách phải có độ dài từ 2 đến 100 ký tự.";
        }

        if (!trimmedName.matches("^[\\p{L}\\p{N}\\s_\\-/]+$")) {
            return "Tên chính sách không được chứa ký tự đặc biệt.";
        }

        if (policyType == null || policyType.trim().isEmpty()) {
            return "Vui lòng chọn loại chính sách.";
        }

        boolean validType = false;
        for (String type : VALID_POLICY_TYPES) {
            if (type.equals(policyType.trim())) {
                validType = true;
                break;
            }
        }
        if (!validType) {
            return "Loại chính sách không hợp lệ.";
        }

        if (description == null || description.trim().isEmpty()) {
            return "Nội dung chính sách không được để trống.";
        }

        if (description.trim().length() < 5) {
            return "Nội dung chính sách phải có ít nhất 5 ký tự.";
        }

        return null;
    }

}
