package filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AuthorizationFilter implements Filter {

    private final Set<String> publicUrls = new HashSet<>();
    private final Set<String> adminUrls = new HashSet<>();
    private final Set<String> managerUrls = new HashSet<>();
    private final Set<String> receptionistUrls = new HashSet<>();
    private final Set<String> managerReceptionistUrls = new HashSet<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        initPublicUrls();
        initAdminUrls();
        initManagerUrls();
        initReceptionistUrls();
        initSharedUrls();
    }

    private void initPublicUrls() {
        publicUrls.add("/login");
        publicUrls.add("/logout");
        publicUrls.add("/forgot-password");
        publicUrls.add("/reset-password");

        publicUrls.add("/view/auth/login.jsp");
        publicUrls.add("/view/auth/forgot-password.jsp");
        publicUrls.add("/view/auth/reset-password.jsp");

        publicUrls.add("/home");
        publicUrls.add("/room-list");
        publicUrls.add("/room-detail");
        publicUrls.add("/booking");
        publicUrls.add("/booking-confirmation");
        publicUrls.add("/deposit-payment");
        publicUrls.add("/submit-feedback");
        publicUrls.add("/feedback-list");
        publicUrls.add("/quick-booking");
    }

    private void initAdminUrls() {
        adminUrls.add("/admin-dashboard");

        adminUrls.add("/staff-list");
        adminUrls.add("/staff-create");
        adminUrls.add("/staff-edit");
        adminUrls.add("/staff-delete");
        adminUrls.add("/staff-detail");

        adminUrls.add("/view/admin/dashboard.jsp");
        adminUrls.add("/view/admin/staff-list.jsp");
        adminUrls.add("/view/admin/staff-form.jsp");
        adminUrls.add("/view/admin/staff-detail.jsp");
        
    }

    private void initManagerUrls() {
        managerUrls.add("/manager-dashboard");

        managerUrls.add("/room-management");
        managerUrls.add("/room-type-management");
        managerUrls.add("/service-management");
        managerUrls.add("/report");
        managerUrls.add("/revenue-report");

        managerUrls.add("/view/manager/dashboard.jsp");
        managerUrls.add("/view/manager/room-management.jsp");
        managerUrls.add("/view/manager/room-type-management.jsp");
        managerUrls.add("/view/manager/service-management.jsp");
        managerUrls.add("/view/manager/report.jsp");
        managerUrls.add("/view/manager/revenue-report.jsp");
    }

    private void initReceptionistUrls() {
        receptionistUrls.add("/receptionist-dashboard");

        receptionistUrls.add("/check-in");
        receptionistUrls.add("/check-out");
        receptionistUrls.add("/counter-request");

        receptionistUrls.add("/view/receptionist/dashboard.jsp");
        receptionistUrls.add("/view/receptionist/check-in.jsp");
        receptionistUrls.add("/view/receptionist/check-out.jsp");
        receptionistUrls.add("/view/receptionist/counter-request.jsp");
    }

    private void initSharedUrls() {
        /*
         * Các URL này Manager và Receptionist đều được vào.
         * Đây là phần sửa chính để tránh lỗi:
         * /booking-list bị check thành Manager-only.
         */
        managerReceptionistUrls.add("/booking-list");
        managerReceptionistUrls.add("/staff-booking-detail");

        managerReceptionistUrls.add("/view/receptionist/booking-list.jsp");
        managerReceptionistUrls.add("/view/receptionist/staff-booking-detail.jsp");
    }

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String path = getRequestPath(request);

        if (isStaticResource(path) || isPublicUrl(path)) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        Set<String> allowedRoles = getAllowedRoles(path);

        /*
         * URL không nằm trong danh sách phân quyền thì cho đi tiếp.
         * Tránh làm vỡ các trang public hoặc servlet chưa khai báo trong filter.
         */
        if (allowedRoles.isEmpty()) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        HttpSession session = request.getSession(false);

        if (session == null) {
            redirectToLogin(request, response);
            return;
        }

        String userRole = getUserRoleFromSession(session);

        if (userRole == null || userRole.trim().isEmpty()) {
            redirectToLogin(request, response);
            return;
        }

        userRole = normalizeRole(userRole);

        if (!allowedRoles.contains(userRole)) {
            sendAccessDenied(response);
            return;
        }

        chain.doFilter(servletRequest, servletResponse);
    }

    private String getRequestPath(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String requestUri = request.getRequestURI();

        if (contextPath != null && !contextPath.isEmpty()) {
            requestUri = requestUri.substring(contextPath.length());
        }

        if (requestUri == null || requestUri.isEmpty()) {
            return "/";
        }

        return requestUri;
    }

    private boolean isStaticResource(String path) {
        return path.startsWith("/view/assets/")
                || path.startsWith("/assets/")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.startsWith("/uploads/")
                || path.endsWith(".css")
                || path.endsWith(".js")
                || path.endsWith(".png")
                || path.endsWith(".jpg")
                || path.endsWith(".jpeg")
                || path.endsWith(".gif")
                || path.endsWith(".svg")
                || path.endsWith(".ico")
                || path.endsWith(".woff")
                || path.endsWith(".woff2")
                || path.endsWith(".ttf");
    }

    private boolean isPublicUrl(String path) {
        return publicUrls.contains(path);
    }

    private Set<String> getAllowedRoles(String path) {
        Set<String> roles = new HashSet<>();

        if (adminUrls.contains(path)) {
            roles.add("ADMIN");
        }

        if (managerUrls.contains(path)) {
            roles.add("MANAGER");
        }

        if (receptionistUrls.contains(path)) {
            roles.add("RECEPTIONIST");
        }

        if (managerReceptionistUrls.contains(path)) {
            roles.add("MANAGER");
            roles.add("RECEPTIONIST");
        }

        return roles;
    }

    private String getUserRoleFromSession(HttpSession session) {
        Object roleObj = session.getAttribute("role");

        if (roleObj != null) {
            return String.valueOf(roleObj);
        }

        Object accountObj = session.getAttribute("account");

        if (accountObj == null) {
            accountObj = session.getAttribute("staff");
        }

        if (accountObj == null) {
            accountObj = session.getAttribute("staffAccount");
        }

        if (accountObj == null) {
            accountObj = session.getAttribute("loggedInStaff");
        }

        if (accountObj == null) {
            accountObj = session.getAttribute("user");
        }

        if (accountObj == null) {
            return null;
        }

        String role = getValueByGetter(accountObj, "getRole");

        if (role != null) {
            return role;
        }

        role = getValueByGetter(accountObj, "getRoleName");

        if (role != null) {
            return role;
        }

        role = getValueByGetter(accountObj, "getUserRole");

        if (role != null) {
            return role;
        }

        return null;
    }

    private String getValueByGetter(Object obj, String getterName) {
        try {
            Method method = obj.getClass().getMethod(getterName);
            Object value = method.invoke(obj);

            if (value == null) {
                return null;
            }

            return String.valueOf(value);

        } catch (Exception e) {
            return null;
        }
    }

    private String normalizeRole(String role) {
        if (role == null) {
            return "";
        }

        role = role.trim();

        if (role.equalsIgnoreCase("admin")) {
            return "ADMIN";
        }

        if (role.equalsIgnoreCase("manager")) {
            return "MANAGER";
        }

        if (role.equalsIgnoreCase("receptionist")) {
            return "RECEPTIONIST";
        }

        if (role.equalsIgnoreCase("lễ tân")) {
            return "RECEPTIONIST";
        }

        if (role.equalsIgnoreCase("quản lý")) {
            return "MANAGER";
        }

        return role.toUpperCase();
    }

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.sendRedirect(request.getContextPath() + "/login");
    }

    private void sendAccessDenied(HttpServletResponse response)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("text/html;charset=UTF-8");

        response.getWriter().write(
                "<!DOCTYPE html>"
                + "<html lang='vi'>"
                + "<head>"
                + "<meta charset='UTF-8'>"
                + "<title>Không có quyền truy cập</title>"
                + "</head>"
                + "<body style='font-family:Segoe UI,Arial,sans-serif;padding:40px;'>"
                + "<h2>Không có quyền truy cập</h2>"
                + "<p>Tài khoản hiện tại không có quyền truy cập chức năng này.</p>"
                + "<a href='javascript:history.back()'>Quay lại</a>"
                + "</body>"
                + "</html>"
        );
    }

    @Override
    public void destroy() {
    }
}
