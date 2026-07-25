package controller;

import dao.StaffAccountDAO;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.StaffAccount;

public class UserProfileController extends HttpServlet {

    private static final String EMAIL_VALID = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String PHONE_VALID = "^0(3|5|7|8|9)[0-9]{8}$";
    private static final String NAME_VALID = "^[\\p{L}\\s'.-]+$";

    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_EMAIL_LENGTH = 100;
    private static final int PHONE_LENGTH = 10;
    private static final int PHONE_PREFIX_LENGTH = 2;

    private static final String STAFF_SESSION_KEY = "staff";
    private static final String STAFF_ID_SESSION_KEY = "staffId";
    private static final String STAFF_ROLE_SESSION_KEY = "staffRole";

    private static final String LOGIN_PATH = "/login";
    private static final String PROFILE_PATH = "/profile";
    private static final String PROFILE_PAGE = "/view/auth/user-profile.jsp";

    private static final String PROFILE_ERROR_FLASH_KEY = "profileFlashError";
    private static final String PROFILE_MESSAGE_FLASH_KEY = "profileFlashMessage";
    private static final String PROFILE_FULL_NAME_FLASH_KEY = "profileFlashFullName";
    private static final String PROFILE_EMAIL_FLASH_KEY = "profileFlashEmail";
    private static final String PROFILE_PHONE_FLASH_KEY = "profileFlashPhone";

    private static final String PASSWORD_ERROR_FLASH_KEY = "passwordFlashError";
    private static final String PASSWORD_MESSAGE_FLASH_KEY = "passwordFlashMessage";
    private static final String PASSWORD_SHOW_FORM_FLASH_KEY = "passwordFlashShowForm";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Hiển thị hồ sơ nhân viên cùng các thông báo tạm thời.
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(STAFF_SESSION_KEY) == null) {
            response.sendRedirect(request.getContextPath() + LOGIN_PATH);
            return;
        }

        moveFlashAttribute(session, request, PROFILE_ERROR_FLASH_KEY, "profileError");
        moveFlashAttribute(session, request, PROFILE_MESSAGE_FLASH_KEY, "profileMessage");
        moveFlashAttribute(session, request, PROFILE_FULL_NAME_FLASH_KEY, "fullNameValue");
        moveFlashAttribute(session, request, PROFILE_EMAIL_FLASH_KEY, "emailValue");
        moveFlashAttribute(session, request, PROFILE_PHONE_FLASH_KEY, "phoneValue");

        moveFlashAttribute(session, request, PASSWORD_ERROR_FLASH_KEY, "passwordError");
        moveFlashAttribute(session, request, PASSWORD_MESSAGE_FLASH_KEY, "passwordMessage");
        moveFlashAttribute(session, request, PASSWORD_SHOW_FORM_FLASH_KEY, "showPasswordForm");

        request.getRequestDispatcher(PROFILE_PAGE).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Kiểm tra và cập nhật thông tin hồ sơ nhân viên.
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(STAFF_SESSION_KEY) == null) {
            response.sendRedirect(request.getContextPath() + LOGIN_PATH);
            return;
        }

        try {
            StaffAccount staff = (StaffAccount) session.getAttribute(STAFF_SESSION_KEY);

            String oldFullName = normalizeFullName(staff.getFullName());
            String oldEmail = normalizeEmail(staff.getEmail());
            String oldPhone = normalizePhone(staff.getPhone());

            String fullName = normalizeFullName(request.getParameter("fullName"));
            String email = normalizeEmail(request.getParameter("email"));
            String phone = normalizePhone(request.getParameter("phone"));

            if (fullName.isEmpty()) {
                redirectWithProfileError(request, response, session, "Họ và tên không được để trống.", oldFullName, oldEmail, oldPhone);
                return;
            }

            if (fullName.length() < MIN_NAME_LENGTH || fullName.length() > MAX_NAME_LENGTH) {
                redirectWithProfileError(request, response, session, "Họ và tên phải từ " + MIN_NAME_LENGTH + " đến " + MAX_NAME_LENGTH + " ký tự.", oldFullName, oldEmail, oldPhone);
                return;
            }

            if (!fullName.matches(NAME_VALID)) {
                redirectWithProfileError(request, response, session, "Họ và tên chỉ được chứa chữ cái, khoảng trắng và một số dấu hợp lệ.", oldFullName, oldEmail, oldPhone);
                return;
            }

            if (email.isEmpty()) {
                redirectWithProfileError(request, response, session, "Email không được để trống.", oldFullName, oldEmail, oldPhone);
                return;
            }

            if (email.length() > MAX_EMAIL_LENGTH) {
                redirectWithProfileError(request, response, session, "Email không được vượt quá " + MAX_EMAIL_LENGTH + " ký tự.", oldFullName, oldEmail, oldPhone);
                return;
            }

            if (!email.matches(EMAIL_VALID)) {
                redirectWithProfileError(request, response, session, "Email không đúng định dạng.", oldFullName, oldEmail, oldPhone);
                return;
            }

            if (email.startsWith(".") || email.contains("..")) {
                redirectWithProfileError(request, response, session, "Email không hợp lệ.", oldFullName, oldEmail, oldPhone);
                return;
            }

            if (!phone.isEmpty()) {
                if (!phone.matches("[0-9]+")) {
                    redirectWithProfileError(request, response, session, "Số điện thoại chỉ được chứa chữ số.", oldFullName, oldEmail, oldPhone);
                    return;
                }

                if (phone.length() != PHONE_LENGTH) {
                    redirectWithProfileError(request, response, session, "Số điện thoại phải gồm đúng " + PHONE_LENGTH + " chữ số.", oldFullName, oldEmail, oldPhone);
                    return;
                }

                if (!phone.matches(PHONE_VALID)) {
                    redirectWithProfileError(request, response, session, "Số điện thoại không đúng định dạng. Số điện thoại Việt Nam phải bắt đầu bằng 03, 05, 07, 08 hoặc 09.", oldFullName, oldEmail, oldPhone);
                    return;
                }

                if (phone.matches("^(\\d)\\1+$")) {
                    redirectWithProfileError(request, response, session, "Số điện thoại không hợp lệ.", oldFullName, oldEmail, oldPhone);
                    return;
                }

                String subscriberPart = phone.substring(PHONE_PREFIX_LENGTH);

                if (subscriberPart.matches("^(\\d)\\1+$")) {
                    redirectWithProfileError(request, response, session, "Số điện thoại không hợp lệ.", oldFullName, oldEmail, oldPhone);
                    return;
                }

                if (isInvalidPhoneNumber(phone)) {
                    redirectWithProfileError(request, response, session, "Số điện thoại không hợp lệ.", oldFullName, oldEmail, oldPhone);
                    return;
                }
            }

            if (fullName.equals(oldFullName) && email.equals(oldEmail) && phone.equals(oldPhone)) {
                redirectWithProfileError(request, response, session, "Thông tin không có thay đổi.", oldFullName, oldEmail, oldPhone);
                return;
            }

            StaffAccountDAO staffAccountDAO = new StaffAccountDAO();

            if (!email.equals(oldEmail) && staffAccountDAO.isValueExistsForOtherStaff("email", email, staff.getStaffId())) {
                redirectWithProfileError(request, response, session, "Email đã được sử dụng bởi tài khoản khác.", oldFullName, oldEmail, oldPhone);
                return;
            }

            if (!phone.isEmpty() && !phone.equals(oldPhone)
                    && staffAccountDAO.isValueExistsForOtherStaff("phone", phone, staff.getStaffId())) {

                redirectWithProfileError(request, response, session, "Số điện thoại đã được sử dụng bởi tài khoản khác.", oldFullName, oldEmail, oldPhone);
                return;
            }

            boolean updated = staffAccountDAO.updateProfile(staff.getStaffId(), fullName, email, phone);

            if (!updated) {
                redirectWithProfileError(request, response, session, "Không thể cập nhật hồ sơ. Vui lòng thử lại.", oldFullName, oldEmail, oldPhone);
                return;
            }

            StaffAccount updatedStaff = staffAccountDAO.getStaffById(staff.getStaffId());

            if (updatedStaff == null) {
                redirectWithProfileError(request, response, session, "Cập nhật thành công nhưng không thể tải lại hồ sơ.", oldFullName, oldEmail, oldPhone);
                return;
            }

            session.setAttribute(STAFF_SESSION_KEY, updatedStaff);
            session.setAttribute(STAFF_ID_SESSION_KEY, updatedStaff.getStaffId());
            session.setAttribute(STAFF_ROLE_SESSION_KEY, updatedStaff.getRole());

            session.removeAttribute(PROFILE_ERROR_FLASH_KEY);
            session.removeAttribute(PROFILE_FULL_NAME_FLASH_KEY);
            session.removeAttribute(PROFILE_EMAIL_FLASH_KEY);
            session.removeAttribute(PROFILE_PHONE_FLASH_KEY);
            session.setAttribute(PROFILE_MESSAGE_FLASH_KEY, "Cập nhật hồ sơ thành công.");

            redirectToProfile(request, response);
        } catch (Exception exception) {
            Logger.getLogger(UserProfileController.class.getName()).log(Level.SEVERE, null, exception);

            if (!response.isCommitted()) {
                session.setAttribute(PROFILE_ERROR_FLASH_KEY, "Đã xảy ra lỗi khi cập nhật hồ sơ. Vui lòng thử lại.");
                redirectToProfile(request, response);
            }
        }
    }

    private String normalizeFullName(String value) {
        // Chuẩn hóa họ tên trước khi kiểm tra và lưu dữ liệu.
        return value == null ? "" : value.trim().replaceAll("\\s+", " ");
    }

    private String normalizeEmail(String value) {
        // Chuẩn hóa email trước khi kiểm tra và lưu dữ liệu.
        return value == null ? "" : value.trim().toLowerCase();
    }

    private String normalizePhone(String value) {
        // Chuẩn hóa số điện thoại trước khi kiểm tra và lưu dữ liệu.
        return value == null ? "" : value.trim().replaceAll("\\s+", "");
    }

    private boolean isInvalidPhoneNumber(String phone) {
        // Kiểm tra các số điện thoại mẫu không hợp lệ.
        return phone.equals("0123456789")
                || phone.equals("0987654321")
                || phone.equals("0900000000")
                || phone.equals("0911111111")
                || phone.equals("0922222222")
                || phone.equals("0933333333")
                || phone.equals("0944444444")
                || phone.equals("0955555555")
                || phone.equals("0966666666")
                || phone.equals("0977777777")
                || phone.equals("0988888888")
                || phone.equals("0999999999");
    }

    private void redirectWithProfileError(HttpServletRequest request, HttpServletResponse response, HttpSession session,
            String errorMessage, String fullName, String email, String phone) throws IOException {

        // Lưu lỗi và dữ liệu hồ sơ tạm thời rồi chuyển về trang hồ sơ.
        session.removeAttribute(PROFILE_MESSAGE_FLASH_KEY);
        session.setAttribute(PROFILE_ERROR_FLASH_KEY, errorMessage);
        session.setAttribute(PROFILE_FULL_NAME_FLASH_KEY, fullName);
        session.setAttribute(PROFILE_EMAIL_FLASH_KEY, email);
        session.setAttribute(PROFILE_PHONE_FLASH_KEY, phone);

        redirectToProfile(request, response);
    }

    private void moveFlashAttribute(HttpSession session, HttpServletRequest request, String sessionKey, String requestKey) {
        // Chuyển dữ liệu tạm từ session sang request và xóa khỏi session.
        Object value = session.getAttribute(sessionKey);

        if (value != null) {
            request.setAttribute(requestKey, value);
            session.removeAttribute(sessionKey);
        }
    }

    private void redirectToProfile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Chuyển POST thành GET để tránh trình duyệt gửi lại form.
        String redirectUrl = response.encodeRedirectURL(request.getContextPath() + PROFILE_PATH);

        response.setStatus(HttpServletResponse.SC_SEE_OTHER);
        response.setHeader("Location", redirectUrl);
    }

    @Override
    public String getServletInfo() {
        // Trả về mô tả của servlet hồ sơ nhân viên.
        return "User Profile Controller";
    }
}