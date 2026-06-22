package dal;

import java.math.BigDecimal;
import java.util.regex.Pattern;

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
}
