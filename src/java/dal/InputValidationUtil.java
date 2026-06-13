package dal;

import java.util.regex.Pattern;

/**
 * Lớp tiện ích kiểm tra tính hợp lệ và chuẩn hóa dữ liệu đầu vào
 * @author LinhLTHE200306
 * @version 2.5
 * @since 2026-06-13
 */
public class InputValidationUtil {

    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
        
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^(03|05|07|08|09)\\d{8}$");

    private static final Pattern NAME_PATTERN = 
        Pattern.compile("^[\\p{L}\\s]+$");


    public static String validateStaffInput(String fullName, String email, String phone) {
        
        if (fullName == null || fullName.trim().isEmpty()) {
            return "Họ và tên không được để trống.";
        }
        if (!NAME_PATTERN.matcher(fullName).matches()) {
            return "Họ và tên không được chứa số hoặc ký tự đặc biệt.";
        }

        if (email == null || email.trim().isEmpty()) {
            return "Email không được để trống.";
        }
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return "Định dạng email không hợp lệ.";
        }

        if (phone == null || phone.trim().isEmpty()) {
            return "Số điện thoại không được để trống.";
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
}