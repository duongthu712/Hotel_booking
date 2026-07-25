package filter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AuthorizationFilter implements Filter {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_MANAGER = "MANAGER";
    private static final String ROLE_RECEPTIONIST = "RECEPTIONIST";
    private static final String ROLE_PREFIX = "ROLE_";

    private static final String ROOT_PATH = "/";
    private static final String SESSION_ID_PARAMETER = ";jsessionid=";
    private static final String LOGIN_PATH = "/login";
    private static final String ACCESS_DENIED_PATH = "/access-denied";

    private static final String CACHE_CONTROL_VALUE = "no-cache, no-store, must-revalidate, max-age=0";
    private static final String PRAGMA_VALUE = "no-cache";
    private static final long EXPIRED_DATE = 0L;

    private final Set<String> publicUrls = new HashSet<>();
    private final Set<String> adminUrls = new HashSet<>();
    private final Set<String> managerUrls = new HashSet<>();
    private final Set<String> receptionistUrls = new HashSet<>();
    private final Set<String> managerReceptionistUrls = new HashSet<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Khởi tạo danh sách URL theo từng nhóm quyền.
        initPublicUrls();
        initAdminUrls();
        initManagerUrls();
        initReceptionistUrls();
        initSharedUrls();
    }

    private void initPublicUrls() {
        // Khai báo các URL không yêu cầu đăng nhập.
        Collections.addAll(
                publicUrls,
                "/",
                "/home",
                "/login",
                "/logout",
                "/forgot-password",
                "/verify-code",
                "/reset-password",
                "/access-denied",
                "/view/auth/login.jsp",
                "/view/auth/forgot-password.jsp",
                "/view/auth/reset-password.jsp",
                "/room-list",
                "/room-detail",
                "/booking",
                "/booking-form",
                "/quick-booking",
                "/booking-confirmation",
                "/booking-payment",
                "/booking-success",
                "/deposit-payment",
                "/booking-detail",
                "/feedback-list",
                "/feedback-submission",
                "/submit-feedback",
                "/policies",
                "/guest-request"
        );
    }

    private void initAdminUrls() {
        // Khai báo các URL chỉ dành cho quản trị viên.
        Collections.addAll(
                adminUrls,
                "/admin-dashboard",
                "/staffaccountlist",
                "/staffaccountdetail",
                "/staffaccountedit",
                "/staffaccountcreate",
                "/staffaccountdelete",
                "/staff-list",
                "/staff-create",
                "/staff-edit",
                "/staff-delete",
                "/staff-detail",
                "/view/admin/staff-management.jsp",
                "/view/admin/dashboard.jsp",
                "/view/admin/staff-list.jsp",
                "/view/admin/staff-form.jsp",
                "/view/admin/staff-detail.jsp"
        );
    }

    private void initManagerUrls() {
        // Khai báo các URL chỉ dành cho quản lý.
        Collections.addAll(
                managerUrls,
                "/managerdashboard",
                "/manager-dashboard",
                "/mdashboardpdf",
                "/report",
                "/revenue-report",
                "/roomlist",
                "/roomedit",
                "/roomcreate",
                "/roomdelete",
                "/room-management",
                "/roomtypelist",
                "/create-roomtype",
                "/add-room-type",
                "/edit-room-type",
                "/room-type-management",
                "/roomservicelist",
                "/roomservicecreate",
                "/roomserviceedit",
                "/roomservicedelete",
                "/room-service-management",
                "/servicelist",
                "/servicecreate",
                "/serviceedit",
                "/servicedelete",
                "/hotelservicelist",
                "/hotelservicecreate",
                "/hotelserviceedit",
                "/hotelservicedelete",
                "/service-management",
                "/hotel-service-management",
                "/roomamenitylist",
                "/roomamenitycreate",
                "/roomamenityedit",
                "/roomamenitydelete",
                "/room-amenity-management",
                "/hotelinfo",
                "/hotelinfoupdate",
                "/hotelimageupdate",
                "/hotelnewscreate",
                "/hotelnewsedit",
                "/hotelnewsdelete",
                "/hotel-info-management",
                "/policylist",
                "/policycreate",
                "/policyedit",
                "/policydelete",
                "/policy-management",
                "/feedback-management",
                "/report-feedback",
                "/view/manager/dashboard.jsp",
                "/view/manager/add-room-type.jsp",
                "/view/manager/edit-room-type.jsp",
                "/view/manager/add-service.jsp",
                "/view/manager/feedback-management.jsp",
                "/view/manager/hotel-info-management.jsp",
                "/view/manager/hotel-service-management.jsp",
                "/view/manager/policy-management.jsp",
                "/view/manager/report-feedback.jsp",
                "/view/manager/room-amenity-management.jsp",
                "/view/manager/room-management.jsp",
                "/view/manager/room-service-management.jsp",
                "/view/manager/room-type-management.jsp"
        );
    }

    private void initReceptionistUrls() {
        // Khai báo các URL chỉ dành cho lễ tân.
        Collections.addAll(
                receptionistUrls,
                "/receptionist-dashboard",
                "/assign-room",
                "/unassign-room",
                "/check-in",
                "/check-out",
                "/counter-request",
                "/walk-in-booking",
                "/processrequest",
                "/process-request",
                "/request-processing",
                "/depositpaymentlist",
                "/depositpaymentverify",
                "/depositpaymentreject",
                "/payment-verification",
                "/checkout",
                "/invoicecreate",
                "/invoicepdf",
                "/billinglist",
                "/billing",
                "/invoice",
                "/view/receptionist/dashboard.jsp",
                "/view/receptionist/assign-room.jsp",
                "/view/receptionist/billing.jsp",
                "/view/receptionist/check-in.jsp",
                "/view/receptionist/check-out.jsp",
                "/view/receptionist/counter-request.jsp",
                "/view/receptionist/invoice.jsp",
                "/view/receptionist/payment-verification.jsp",
                "/view/receptionist/request-processing.jsp",
                "/view/receptionist/walk-in-booking.jsp"
        );
    }

    private void initSharedUrls() {
        // Khai báo các URL dùng chung cho quản lý và lễ tân.
        Collections.addAll(
                managerReceptionistUrls,
                "/booking-list",
                "/staff-booking-detail",
                "/view/receptionist/booking-list.jsp",
                "/view/receptionist/staff-booking-detail.jsp"
        );
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        // Kiểm tra session, quyền truy cập và ngăn cache đối với trang nội bộ.
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String path = getNormalizedRequestPath(request);

        if (isStaticResource(path)) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        if (isPublicUrl(path)) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        disableCache(response);

        Set<String> allowedRoles = getAllowedRoles(path);

        if (allowedRoles.isEmpty()) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        HttpSession session = request.getSession(false);

        if (session == null) {
            redirectToLogin(request, response);
            return;
        }

        String role = getUserRoleFromSession(session);
        String normalizedRole = normalizeRole(role);

        if (normalizedRole.isEmpty()) {
            redirectToLogin(request, response);
            return;
        }

        if (!allowedRoles.contains(normalizedRole)) {
            redirectToAccessDenied(request, response);
            return;
        }

        chain.doFilter(servletRequest, servletResponse);
    }

    private void disableCache(HttpServletResponse response) {
        // Ngăn trình duyệt lưu lại các trang yêu cầu đăng nhập.
        response.setHeader("Cache-Control", CACHE_CONTROL_VALUE);
        response.setHeader("Pragma", PRAGMA_VALUE);
        response.setDateHeader("Expires", EXPIRED_DATE);
    }

    private String getNormalizedRequestPath(HttpServletRequest request) {
        // Chuẩn hóa URI để so sánh với danh sách URL trong filter.
        String contextPath = request.getContextPath();
        String requestUri = request.getRequestURI();

        if (requestUri == null || requestUri.trim().isEmpty()) {
            return ROOT_PATH;
        }

        if (contextPath != null && !contextPath.isEmpty() && requestUri.startsWith(contextPath)) {
            requestUri = requestUri.substring(contextPath.length());
        }

        int sessionIdIndex = requestUri.indexOf(SESSION_ID_PARAMETER);

        if (sessionIdIndex >= 0) {
            requestUri = requestUri.substring(0, sessionIdIndex);
        }

        if (requestUri.isEmpty()) {
            requestUri = ROOT_PATH;
        }

        if (requestUri.length() > ROOT_PATH.length() && requestUri.endsWith(ROOT_PATH)) {
            requestUri = requestUri.substring(0, requestUri.length() - ROOT_PATH.length());
        }

        return requestUri.toLowerCase(Locale.ROOT);
    }

    private boolean isStaticResource(String path) {
        // Kiểm tra request có phải tài nguyên tĩnh hay không.
        return path.startsWith("/view/assets/")
                || path.startsWith("/assets/")
                || path.startsWith("/css/")
                || path.startsWith("/javascript/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.startsWith("/image/")
                || path.startsWith("/uploads/")
                || path.startsWith("/fonts/")
                || path.endsWith(".css")
                || path.endsWith(".js")
                || path.endsWith(".png")
                || path.endsWith(".jpg")
                || path.endsWith(".jpeg")
                || path.endsWith(".gif")
                || path.endsWith(".webp")
                || path.endsWith(".svg")
                || path.endsWith(".ico")
                || path.endsWith(".woff")
                || path.endsWith(".woff2")
                || path.endsWith(".ttf")
                || path.endsWith(".map");
    }

    private boolean isPublicUrl(String path) {
        // Kiểm tra URL có thuộc khu vực công khai hay không.
        if (publicUrls.contains(path)) {
            return true;
        }

        return path.startsWith("/view/public/")
                || path.startsWith("/view/user/")
                || path.startsWith("/view/common/");
    }

    private Set<String> getAllowedRoles(String path) {
        // Lấy danh sách role được phép truy cập URL hiện tại.
        Set<String> roles = new HashSet<>();

        if (adminUrls.contains(path) || path.startsWith("/view/admin/")) {
            roles.add(ROLE_ADMIN);
        }

        if (managerUrls.contains(path) || path.startsWith("/view/manager/")) {
            roles.add(ROLE_MANAGER);
        }

        if (receptionistUrls.contains(path)) {
            roles.add(ROLE_RECEPTIONIST);
        }

        if (managerReceptionistUrls.contains(path)) {
            roles.add(ROLE_MANAGER);
            roles.add(ROLE_RECEPTIONIST);
        }

        return roles;
    }

    private String getUserRoleFromSession(HttpSession session) {
        // Lấy role từ các session attribute đang được sử dụng trong hệ thống.
        Object roleObject = session.getAttribute("staffRole");

        if (roleObject == null) {
            roleObject = session.getAttribute("role");
        }

        if (roleObject == null) {
            roleObject = session.getAttribute("roleName");
        }

        if (roleObject == null) {
            roleObject = session.getAttribute("userRole");
        }

        if (roleObject != null) {
            return String.valueOf(roleObject);
        }

        Object accountObject = session.getAttribute("staff");

        if (accountObject == null) {
            accountObject = session.getAttribute("account");
        }

        if (accountObject == null) {
            accountObject = session.getAttribute("staffAccount");
        }

        if (accountObject == null) {
            accountObject = session.getAttribute("loggedInStaff");
        }

        if (accountObject == null) {
            accountObject = session.getAttribute("user");
        }

        if (accountObject == null) {
            return null;
        }

        String role = getValueByGetter(accountObject, "getRole");

        if (role != null && !role.trim().isEmpty()) {
            return role;
        }

        role = getValueByGetter(accountObject, "getRoleName");

        if (role != null && !role.trim().isEmpty()) {
            return role;
        }

        return getValueByGetter(accountObject, "getUserRole");
    }

    private String getValueByGetter(Object object, String getterName) {
        // Gọi getter của object bằng reflection.
        try {
            Method method = object.getClass().getMethod(getterName);
            Object value = method.invoke(object);

            return value == null ? null : String.valueOf(value);
        } catch (Exception exception) {
            return null;
        }
    }

    private String normalizeRole(String role) {
        // Chuẩn hóa tên role trong database về role dùng trong filter.
        if (role == null) {
            return "";
        }

        String normalized = role.trim().toUpperCase(Locale.ROOT);

        if (normalized.startsWith(ROLE_PREFIX)) {
            normalized = normalized.substring(ROLE_PREFIX.length()).trim();
        }

        switch (normalized) {
            case "ADMIN":
            case "ADMINISTRATOR":
            case "QUẢN TRỊ VIÊN":
            case "QUAN TRI VIEN":
                return ROLE_ADMIN;

            case "MANAGER":
            case "QUẢN LÝ":
            case "QUAN LY":
                return ROLE_MANAGER;

            case "RECEPTIONIST":
            case "LỄ TÂN":
            case "LE TAN":
                return ROLE_RECEPTIONIST;

            default:
                return normalized;
        }
    }

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Chuyển người dùng chưa đăng nhập về trang login.
        response.sendRedirect(request.getContextPath() + LOGIN_PATH);
    }

    private void redirectToAccessDenied(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Chuyển người dùng không đủ quyền về trang từ chối truy cập.
        response.sendRedirect(request.getContextPath() + ACCESS_DENIED_PATH);
    }

    @Override
    public void destroy() {
        // Giải phóng dữ liệu URL khi filter bị hủy.
        publicUrls.clear();
        adminUrls.clear();
        managerUrls.clear();
        receptionistUrls.clear();
        managerReceptionistUrls.clear();
    }
}