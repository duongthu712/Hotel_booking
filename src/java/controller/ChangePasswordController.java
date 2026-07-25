package controller;

import dal.PasswordUtil;
import dao.StaffAccountDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.StaffAccount;

public class ChangePasswordController extends HttpServlet {

    private static final int MIN_PASSWORD_LENGTH = 6;

    private static final String STAFF_SESSION_KEY = "staff";
    private static final String LOGIN_PATH = "/login";
    private static final String PROFILE_PATH = "/profile#password-section";

    private static final String PASSWORD_ERROR_FLASH_KEY = "passwordFlashError";
    private static final String PASSWORD_MESSAGE_FLASH_KEY = "passwordFlashMessage";
    private static final String PASSWORD_SHOW_FORM_FLASH_KEY = "passwordFlashShowForm";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Kiểm tra và cập nhật mật khẩu cho nhân viên đang đăng nhập.
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(STAFF_SESSION_KEY) == null) {
            response.sendRedirect(request.getContextPath() + LOGIN_PATH);
            return;
        }

        StaffAccount staff = (StaffAccount) session.getAttribute(STAFF_SESSION_KEY);

        String currentPassword = getParameter(request, "currentPassword");
        String newPassword = getParameter(request, "newPassword");
        String confirmPassword = getParameter(request, "confirmPassword");

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            redirectWithError(request, response, session, "Vui lòng nhập đầy đủ các trường mật khẩu.");
            return;
        }

        if (newPassword.length() < MIN_PASSWORD_LENGTH) {
            redirectWithError(request, response, session, "Mật khẩu mới phải có ít nhất " + MIN_PASSWORD_LENGTH + " ký tự.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectWithError(request, response, session, "Mật khẩu xác nhận không khớp.");
            return;
        }

        StaffAccountDAO staffAccountDAO = new StaffAccountDAO();
        StaffAccount checkedStaff = staffAccountDAO.loginWithHashCheck(staff.getUsername(), currentPassword);

        if (checkedStaff == null) {
            redirectWithError(request, response, session, "Mật khẩu hiện tại không đúng.");
            return;
        }

        if (currentPassword.equals(newPassword)) {
            redirectWithError(request, response, session, "Mật khẩu mới không được trùng với mật khẩu hiện tại.");
            return;
        }

        String newPasswordHash = PasswordUtil.hashPassword(newPassword);
        boolean updated = staffAccountDAO.updatePasswordByStaffId(staff.getStaffId(), newPasswordHash);

        if (!updated) {
            redirectWithError(request, response, session, "Không thể đổi mật khẩu. Vui lòng thử lại.");
            return;
        }

        session.removeAttribute(PASSWORD_ERROR_FLASH_KEY);
        session.removeAttribute(PASSWORD_SHOW_FORM_FLASH_KEY);
        session.setAttribute(PASSWORD_MESSAGE_FLASH_KEY, "Đổi mật khẩu thành công.");

        redirectToProfile(request, response);
    }

    private String getParameter(HttpServletRequest request, String parameterName) {
        // Lấy và loại bỏ khoảng trắng ở đầu và cuối parameter.
        String value = request.getParameter(parameterName);
        return value == null ? "" : value.trim();
    }

    private void redirectWithError(HttpServletRequest request, HttpServletResponse response, HttpSession session, String errorMessage)
            throws IOException {

        // Lưu thông báo lỗi tạm thời rồi chuyển về trang hồ sơ.
        session.removeAttribute(PASSWORD_MESSAGE_FLASH_KEY);
        session.setAttribute(PASSWORD_ERROR_FLASH_KEY, errorMessage);
        session.setAttribute(PASSWORD_SHOW_FORM_FLASH_KEY, true);

        redirectToProfile(request, response);
    }

    private void redirectToProfile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Chuyển POST thành GET để tránh trình duyệt gửi lại form.
        String redirectUrl = response.encodeRedirectURL(request.getContextPath() + PROFILE_PATH);

        response.setStatus(HttpServletResponse.SC_SEE_OTHER);
        response.setHeader("Location", redirectUrl);
    }

    @Override
    public String getServletInfo() {
        // Trả về mô tả của servlet đổi mật khẩu.
        return "Change Password Controller";
    }
}