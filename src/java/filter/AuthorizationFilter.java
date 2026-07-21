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
import java.util.Locale;
import java.util.Set;

public class AuthorizationFilter implements Filter {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_MANAGER = "MANAGER";
    private static final String ROLE_RECEPTIONIST = "RECEPTIONIST";

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

    /**
     * Các URL không yêu cầu đăng nhập.
     * Tất cả URL trong filter đều viết chữ thường vì request path sẽ được
     * chuẩn hóa bằng toLowerCase().
     */
    private void initPublicUrls() {
        Collections.addAll(
                publicUrls,
                "/",
                "/home",

                // Authentication
                "/login",
                "/logout",
                "/forgot-password",
                "/verify-code",
                "/reset-password",
                "/access-denied",

                // Authentication JSP
                "/view/auth/login.jsp",
                "/view/auth/forgot-password.jsp",
                "/view/auth/reset-password.jsp",

                // Public room and booking flow
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

                // Public feedback and policy pages
                "/feedback-list",
                "/feedback-submission",
                "/submit-feedback",
                "/policies",
                "/guest-request"
        );
    }

    /**
     * Chỉ Administrator/Admin/Quản trị viên được truy cập.
     */
    private void initAdminUrls() {
        Collections.addAll(
                adminUrls,
                "/admin-dashboard",

                // Các mapping thật trong web.xml
                "/staffaccountlist",
                "/staffaccountdetail",
                "/staffaccountedit",
                "/staffaccountcreate",
                "/staffaccountdelete",

                // Các tên cũ nếu trong project vẫn còn link tới
                "/staff-list",
                "/staff-create",
                "/staff-edit",
                "/staff-delete",
                "/staff-detail",

                // JSP nội bộ
                "/view/admin/staff-management.jsp",
                "/view/admin/dashboard.jsp",
                "/view/admin/staff-list.jsp",
                "/view/admin/staff-form.jsp",
                "/view/admin/staff-detail.jsp"
        );
    }

    /**
     * Chỉ Manager/Quản lý được truy cập.
     */
    private void initManagerUrls() {
        Collections.addAll(
                managerUrls,
                // Dashboard and report
                "/managerdashboard",
                "/manager-dashboard",
                "/mdashboardpdf",
                "/report",
                "/revenue-report",

                // Room management
                "/roomlist",
                "/roomedit",
                "/roomcreate",
                "/roomdelete",
                "/room-management",

                // Room type management
                "/roomtypelist",
                "/create-roomtype",
                "/add-room-type",
                "/edit-room-type",
                "/room-type-management",

                // Room service management
                "/roomservicelist",
                "/roomservicecreate",
                "/roomserviceedit",
                "/roomservicedelete",
                "/room-service-management",

                // Hotel service management
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

                // Amenity management
                "/roomamenitylist",
                "/roomamenitycreate",
                "/roomamenityedit",
                "/roomamenitydelete",
                "/room-amenity-management",

                // Hotel information
                "/hotelinfo",
                "/hotelinfoupdate",
                "/hotelimageupdate",
                "/hotelnewscreate",
                "/hotelnewsedit",
                "/hotelnewsdelete",
                "/hotel-info-management",

                // Policy management
                "/policylist",
                "/policycreate",
                "/policyedit",
                "/policydelete",
                "/policy-management",

                // Feedback management
                "/feedback-management",
                "/report-feedback",

                // JSP nội bộ
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

    /**
     * Chỉ Receptionist/Lễ tân được truy cập.
     */
    private void initReceptionistUrls() {
        Collections.addAll(
                receptionistUrls,
                "/receptionist-dashboard",

                // Check-in, check-out and room assignment
                "/assign-room",
                "/unassign-room",
                "/check-in",
                "/check-out",

                // Counter and walk-in workflows
                "/counter-request",
                "/walk-in-booking",
                "/processrequest",
                "/process-request",
                "/request-processing",

                // Payment, billing and invoice
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

                // JSP nội bộ
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

    /**
     * Manager và Receptionist đều được truy cập.
     */
    private void initSharedUrls() {
        Collections.addAll(
                managerReceptionistUrls,
                "/booking-list",
                "/staff-booking-detail",

                "/view/receptionist/booking-list.jsp",
                "/view/receptionist/staff-booking-detail.jsp"
        );
    }

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

//        request.setCharacterEncoding("UTF-8");
//        response.setCharacterEncoding("UTF-8");

        String path = getNormalizedRequestPath(request);

        // CSS, JavaScript, ảnh, font... luôn được tải.
        if (isStaticResource(path)) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        // Trang công khai không cần kiểm tra session.
        if (isPublicUrl(path)) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        Set<String> allowedRoles = getAllowedRoles(path);

        /*
         * URL chưa được khai báo trong filter thì cho servlet xử lý tiếp.
         * Cách này tránh làm hỏng các chức năng khác của project.
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

    /**
     * Lấy URI, bỏ context path, bỏ jsessionid và chuyển thành chữ thường.
     */
    private String getNormalizedRequestPath(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String requestUri = request.getRequestURI();

        if (requestUri == null || requestUri.trim().isEmpty()) {
            return "/";
        }

        if (contextPath != null
                && !contextPath.isEmpty()
                && requestUri.startsWith(contextPath)) {
            requestUri = requestUri.substring(contextPath.length());
        }

        int sessionIdIndex = requestUri.indexOf(";jsessionid=");
        if (sessionIdIndex >= 0) {
            requestUri = requestUri.substring(0, sessionIdIndex);
        }

        if (requestUri.isEmpty()) {
            requestUri = "/";
        }

        if (requestUri.length() > 1 && requestUri.endsWith("/")) {
            requestUri = requestUri.substring(0, requestUri.length() - 1);
        }

        return requestUri.toLowerCase(Locale.ROOT);
    }

    private boolean isStaticResource(String path) {
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
        if (publicUrls.contains(path)) {
            return true;
        }

        // Các JSP thuộc khu vực công khai.
        return path.startsWith("/view/public/")
                || path.startsWith("/view/user/")
                || path.startsWith("/view/common/");
    }

    private Set<String> getAllowedRoles(String path) {
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

    /**
     * LoginController hiện lưu:
     * session.setAttribute("staff", staff);
     * session.setAttribute("staffRole", staff.getRole());
     */
    private String getUserRoleFromSession(HttpSession session) {
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
        try {
            Method method = object.getClass().getMethod(getterName);
            Object value = method.invoke(object);
            return value == null ? null : String.valueOf(value);
        } catch (Exception exception) {
            return null;
        }
    }

    /**
     * Chuẩn hóa các tên role trong database về ba role dùng trong filter.
     */
    private String normalizeRole(String role) {
        if (role == null) {
            return "";
        }

        String normalized = role.trim().toUpperCase(Locale.ROOT);

        if (normalized.startsWith("ROLE_")) {
            normalized = normalized.substring(5).trim();
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

    private void redirectToLogin(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.sendRedirect(request.getContextPath() + "/login");
    }

    private void redirectToAccessDenied(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.sendRedirect(request.getContextPath() + "/access-denied");
    }

    @Override
    public void destroy() {
        publicUrls.clear();
        adminUrls.clear();
        managerUrls.clear();
        receptionistUrls.clear();
        managerReceptionistUrls.clear();
    }
}