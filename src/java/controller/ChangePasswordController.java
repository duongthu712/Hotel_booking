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
    private static final String PROFILE_PAGE = "/view/auth/user-profile.jsp";
    private static final String LOGIN_PATH = "/login";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Kiểm tra và cập nhật mật khẩu mới cho nhân viên đang đăng nhập.
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

        request.setAttribute("showPasswordForm", true);

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            forwardWithError(request, response, "Vui lòng nhập đầy đủ các trường mật khẩu.");
            return;
        }

        if (newPassword.length() < MIN_PASSWORD_LENGTH) {
            forwardWithError(request, response,
                    "Mật khẩu mới phải có ít nhất " + MIN_PASSWORD_LENGTH + " ký tự.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            forwardWithError(request, response, "Mật khẩu xác nhận không khớp.");
            return;
        }

        StaffAccountDAO staffAccountDAO = new StaffAccountDAO();
        StaffAccount checkedStaff = staffAccountDAO.loginWithHashCheck(
                staff.getUsername(), currentPassword);

        if (checkedStaff == null) {
            forwardWithError(request, response, "Mật khẩu hiện tại không đúng.");
            return;
        }

        if (currentPassword.equals(newPassword)) {
            forwardWithError(request, response,
                    "Mật khẩu mới không được trùng với mật khẩu hiện tại.");
            return;
        }

        String newPasswordHash = PasswordUtil.hashPassword(newPassword);
        boolean updated = staffAccountDAO.updatePasswordByStaffId(
                staff.getStaffId(), newPasswordHash);

        if (!updated) {
            forwardWithError(request, response,
                    "Không thể đổi mật khẩu. Vui lòng thử lại.");
            return;
        }

        request.removeAttribute("showPasswordForm");
        request.setAttribute("passwordMessage", "Đổi mật khẩu thành công.");
        request.getRequestDispatcher(PROFILE_PAGE).forward(request, response);
    }

    private String getParameter(HttpServletRequest request, String parameterName) {
        // Lấy và loại bỏ khoảng trắng ở đầu, cuối của tham số.
        String value = request.getParameter(parameterName);
        return value == null ? "" : value.trim();
    }

    private void forwardWithError(HttpServletRequest request, HttpServletResponse response,
            String errorMessage) throws ServletException, IOException {

        // Hiển thị lại trang hồ sơ kèm thông báo lỗi đổi mật khẩu.
        request.setAttribute("passwordError", errorMessage);
        request.getRequestDispatcher(PROFILE_PAGE).forward(request, response);
    }

    @Override
    public String getServletInfo() {
        // Trả về mô tả của servlet đổi mật khẩu.
        return "Change Password Controller";
    }
}
