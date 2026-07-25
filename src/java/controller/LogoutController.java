package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LogoutController extends HttpServlet {

    private static final String LOGIN_PATH = "/login?showLogin=true";
    private static final String SESSION_COOKIE_NAME = "JSESSIONID";
    private static final String CACHE_CONTROL_VALUE = "no-cache, no-store, must-revalidate, max-age=0";
    private static final String PRAGMA_VALUE = "no-cache";

    private static final int DELETE_COOKIE_MAX_AGE = 0;
    private static final long EXPIRED_DATE = 0L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Hủy phiên đăng nhập và chuyển người dùng về trang đăng nhập.
        logout(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Xử lý đăng xuất bằng phương thức POST.
        logout(request, response);
    }

    private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Hủy session, xóa cookie phiên và ngăn trình duyệt lưu trang đăng xuất.
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        deleteSessionCookie(request, response);
        disableCache(response);

        response.sendRedirect(request.getContextPath() + LOGIN_PATH);
    }

    private void deleteSessionCookie(HttpServletRequest request, HttpServletResponse response) {
        // Xóa cookie JSESSIONID của ứng dụng.
        String cookiePath = request.getContextPath();

        if (cookiePath == null || cookiePath.isEmpty()) {
            cookiePath = "/";
        }

        Cookie sessionCookie = new Cookie(SESSION_COOKIE_NAME, "");
        sessionCookie.setPath(cookiePath);
        sessionCookie.setHttpOnly(true);
        sessionCookie.setMaxAge(DELETE_COOKIE_MAX_AGE);

        response.addCookie(sessionCookie);
    }

    private void disableCache(HttpServletResponse response) {
        // Ngăn trình duyệt lưu lại phản hồi đăng xuất.
        response.setHeader("Cache-Control", CACHE_CONTROL_VALUE);
        response.setHeader("Pragma", PRAGMA_VALUE);
        response.setDateHeader("Expires", EXPIRED_DATE);
    }

    @Override
    public String getServletInfo() {
        // Trả về mô tả của servlet đăng xuất.
        return "Logout Controller";
    }
}