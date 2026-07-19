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

    /*
     * URL công khai, không cần đăng nhập.
     */
    private final Set<String> publicUrls = new HashSet<>();

    /*
     * URL dành cho tất cả nhân viên:
     * ADMIN, MANAGER, RECEPTIONIST.
     */
    private final Set<String> staffUrls = new HashSet<>();

    /*
     * URL riêng theo từng role.
     */
    private final Set<String> adminUrls = new HashSet<>();
    private final Set<String> managerUrls = new HashSet<>();
    private final Set<String> receptionistUrls = new HashSet<>();

    /*
     * URL dùng chung cho Manager và Receptionist.
     */
    private final Set<String> managerReceptionistUrls = new HashSet<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        initPublicUrls();
        initStaffUrls();
        initAdminUrls();
        initManagerUrls();
        initReceptionistUrls();
        initManagerReceptionistUrls();
    }

    /**
     * Các trang public không yêu cầu đăng nhập.
     */
    private void initPublicUrls() {
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
                "/policies",

                "/room-list",
                "/room-detail",

                "/booking",
                "/booking-form",
                "/quick-booking",
                "/booking-confirmation",
                "/booking-payment",
                "/deposit-payment",
                "/booking-success",
                "/booking-detail",

                "/submit-feedback",
                "/feedback-list",

                "/view/auth/login.jsp",
                "/view/auth/forgot-password.jsp",
                "/view/auth/reset-password.jsp"
        );
    }

    /**
     * Các chức năng mà tất cả nhân viên đã đăng nhập đều được dùng.
     */
    private void initStaffUrls() {
        Collections.addAll(
                staffUrls,
                "/profile",
                "/profile/change-password"
        );
    }

    /**
     * Các chức năng chỉ dành cho Admin.
     */
    private void initAdminUrls() {
        Collections.addAll(
                adminUrls,
                "/admin-dashboard",

                "/staff-list",
                "/staff-create",
                "/staff-edit",
                "/staff-delete",
                "/staff-detail",

                "/StaffAccountList",
                "/StaffAccountDetail",
                "/StaffAccountEdit",
                "/StaffAccountCreate",
                "/StaffAccountDelete",

                "/view/admin/dashboard.jsp",
                "/view/admin/staff-list.jsp",
                "/view/admin/staff-form.jsp",
                "/view/admin/staff-detail.jsp"
        );
    }

    /**
     * Các chức năng chỉ dành cho Manager.
     */
    private void initManagerUrls() {
        Collections.addAll(
                managerUrls,
                "/manager-dashboard",

                "/room-management",
                "/room-type-management",
                "/service-management",
                "/report",
                "/revenue-report",

                "/HotelInfo",
                "/HotelInfoUpdate",
                "/HotelImageUpdate",

                "/roomtypelist",
                "/create-roomtype",

                "/RoomList",
                "/RoomEdit",
                "/RoomDetail",

                "/RoomServiceList",
                "/RoomServiceCreate",
                "/RoomServiceEdit",
                "/RoomServiceDelete",

                "/RoomAmenityList",
                "/RoomAmenityCreate",
                "/RoomAmenityEdit",
                "/RoomAmenityDelete",

                "/HotelServiceList",
                "/HotelServiceCreate",
                "/HotelServiceEdit",
                "/HotelServiceDelete",

                "/ServiceList",
                "/ServiceCreate",
                "/ServiceEdit",
                "/ServiceDelete",

                "/FeedbackList",
                "/feedback-management",

                "/HotelNewsCreate",
                "/HotelNewsEdit",
                "/HotelNewsDelete",

                "/view/manager/dashboard.jsp",
                "/view/manager/room-management.jsp",
                "/view/manager/room-type-management.jsp",
                "/view/manager/service-management.jsp",
                "/view/manager/report.jsp",
                "/view/manager/revenue-report.jsp"
        );
    }

    /**
     * Các chức năng chỉ dành cho Receptionist.
     */
    private void initReceptionistUrls() {
        Collections.addAll(
                receptionistUrls,
                "/receptionist-dashboard",

                "/check-in",
                "/check-out",
                "/counter-request",

                "/DepositPaymentList",
                "/DepositPaymentVerify",
                "/DepositPaymentReject",

                "/Checkout",
                "/InvoiceCreate",
                "/InvoicePDF",
                "/BillingList",

                "/view/receptionist/dashboard.jsp",
                "/view/receptionist/check-in.jsp",
                "/view/receptionist/check-out.jsp",
                "/view/receptionist/counter-request.jsp"
        );
    }

    /**
     * Các chức năng Manager và Receptionist đều có thể truy cập.
     */
    private void initManagerReceptionistUrls() {
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
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest request
                = (HttpServletRequest) servletRequest;

        HttpServletResponse response
                = (HttpServletResponse) servletResponse;

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String path = getRequestPath(request);

        /*
         * Cho phép tài nguyên tĩnh và URL public đi qua.
         */
        if (isStaticResource(path) || isPublicUrl(path)) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        Set<String> allowedRoles = getAllowedRoles(path);

        /*
         * URL chưa được khai báo phân quyền thì cho đi tiếp.
         *
         * Cách này tránh làm hỏng các servlet hoặc trang public
         * chưa được thêm vào AuthorizationFilter.
         */
        if (allowedRoles.isEmpty()) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        HttpSession session = request.getSession(false);

        /*
         * URL cần quyền nhưng chưa đăng nhập.
         */
        if (session == null) {
            redirectToLogin(request, response);
            return;
        }

        String userRole = getUserRoleFromSession(session);

        /*
         * Có session nhưng không lấy được role.
         */
        if (userRole == null || userRole.trim().isEmpty()) {
            redirectToLogin(request, response);
            return;
        }

        userRole = normalizeRole(userRole);

        /*
         * Đã đăng nhập nhưng không có quyền truy cập.
         */
        if (!allowedRoles.contains(userRole)) {
            sendAccessDenied(response);
            return;
        }

        chain.doFilter(servletRequest, servletResponse);
    }

    /**
     * Lấy đường dẫn request và loại bỏ context path.
     *
     * Ví dụ:
     * /Hotel_booking_project/booking-list
     * thành:
     * /booking-list
     */
    private String getRequestPath(HttpServletRequest request) {
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

        if (requestUri.isEmpty()) {
            return "/";
        }

        /*
         * Chuẩn hóa URL có dấu "/" ở cuối.
         *
         * Ví dụ:
         * /booking-list/
         * thành:
         * /booking-list
         */
        if (requestUri.length() > 1 && requestUri.endsWith("/")) {
            requestUri = requestUri.substring(
                    0,
                    requestUri.length() - 1
            );
        }

        return requestUri;
    }

    /**
     * Kiểm tra tài nguyên tĩnh.
     */
    private boolean isStaticResource(String path) {
        if (path == null) {
            return false;
        }

        String lowerPath = path.toLowerCase();

        return lowerPath.startsWith("/view/assets/")
                || lowerPath.startsWith("/assets/")
                || lowerPath.startsWith("/css/")
                || lowerPath.startsWith("/js/")
                || lowerPath.startsWith("/images/")
                || lowerPath.startsWith("/uploads/")
                || lowerPath.endsWith(".css")
                || lowerPath.endsWith(".js")
                || lowerPath.endsWith(".png")
                || lowerPath.endsWith(".jpg")
                || lowerPath.endsWith(".jpeg")
                || lowerPath.endsWith(".gif")
                || lowerPath.endsWith(".svg")
                || lowerPath.endsWith(".ico")
                || lowerPath.endsWith(".woff")
                || lowerPath.endsWith(".woff2")
                || lowerPath.endsWith(".ttf")
                || lowerPath.endsWith(".map");
    }

    /**
     * Kiểm tra URL public.
     */
    private boolean isPublicUrl(String path) {
        return publicUrls.contains(path);
    }

    /**
     * Xác định những role được truy cập URL.
     */
    private Set<String> getAllowedRoles(String path) {
        Set<String> roles = new HashSet<>();

        /*
         * URL dành cho toàn bộ nhân viên.
         */
        if (staffUrls.contains(path)) {
            roles.add("ADMIN");
            roles.add("MANAGER");
            roles.add("RECEPTIONIST");
        }

        /*
         * URL riêng của Admin.
         */
        if (adminUrls.contains(path)
                || path.startsWith("/view/admin/")) {

            roles.add("ADMIN");
        }

        /*
         * URL riêng của Manager.
         */
        if (managerUrls.contains(path)
                || path.startsWith("/view/manager/")) {

            roles.add("MANAGER");
        }

        /*
         * URL riêng của Receptionist.
         */
        if (receptionistUrls.contains(path)
                || path.startsWith("/view/receptionist/")) {

            roles.add("RECEPTIONIST");
        }

        /*
         * URL dùng chung cho Manager và Receptionist.
         *
         * Nếu URL là JSP trong thư mục receptionist, đoạn phía trên
         * sẽ thêm RECEPTIONIST. Đoạn này tiếp tục thêm MANAGER.
         */
        if (managerReceptionistUrls.contains(path)) {
            roles.add("MANAGER");
            roles.add("RECEPTIONIST");
        }

        return roles;
    }

    /**
     * Lấy role từ session.
     *
     * Hỗ trợ nhiều tên session attribute để phù hợp với
     * các phần đăng nhập khác nhau trong dự án.
     */
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

        if (role != null && !role.trim().isEmpty()) {
            return role;
        }

        role = getValueByGetter(accountObj, "getRoleName");

        if (role != null && !role.trim().isEmpty()) {
            return role;
        }

        role = getValueByGetter(accountObj, "getUserRole");

        if (role != null && !role.trim().isEmpty()) {
            return role;
        }

        return null;
    }

    /**
     * Gọi getter bằng reflection để tránh phụ thuộc trực tiếp
     * vào một model Account cụ thể.
     */
    private String getValueByGetter(
            Object object,
            String getterName
    ) {
        try {
            Method method = object.getClass().getMethod(getterName);
            Object value = method.invoke(object);

            if (value == null) {
                return null;
            }

            return String.valueOf(value);

        } catch (Exception exception) {
            return null;
        }
    }

    /**
     * Chuẩn hóa tên role.
     */
    private String normalizeRole(String role) {
        if (role == null) {
            return "";
        }

        String normalizedRole = role.trim();

        if (normalizedRole.equalsIgnoreCase("admin")) {
            return "ADMIN";
        }

        if (normalizedRole.equalsIgnoreCase("manager")) {
            return "MANAGER";
        }

        if (normalizedRole.equalsIgnoreCase("receptionist")) {
            return "RECEPTIONIST";
        }

        if (normalizedRole.equalsIgnoreCase("lễ tân")
                || normalizedRole.equalsIgnoreCase("le tan")) {

            return "RECEPTIONIST";
        }

        if (normalizedRole.equalsIgnoreCase("quản lý")
                || normalizedRole.equalsIgnoreCase("quan ly")) {

            return "MANAGER";
        }

        return normalizedRole.toUpperCase();
    }

    /**
     * Chuyển người dùng chưa đăng nhập về trang login.
     */
    private void redirectToLogin(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        response.sendRedirect(
                request.getContextPath() + "/login"
        );
    }

    /**
     * Trả về trang thông báo không có quyền.
     */
    private void sendAccessDenied(
            HttpServletResponse response
    ) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("text/html;charset=UTF-8");

        response.getWriter().write(
                "<!DOCTYPE html>"
                + "<html lang='vi'>"
                + "<head>"
                + "<meta charset='UTF-8'>"
                + "<meta name='viewport' "
                + "content='width=device-width, initial-scale=1.0'>"
                + "<title>Không có quyền truy cập</title>"
                + "</head>"
                + "<body style='"
                + "font-family:Segoe UI,Arial,sans-serif;"
                + "padding:40px;"
                + "text-align:center;"
                + "'>"
                + "<h2>Không có quyền truy cập</h2>"
                + "<p>Tài khoản hiện tại không có quyền "
                + "truy cập chức năng này.</p>"
                + "<button onclick='history.back()' "
                + "style='"
                + "padding:10px 18px;"
                + "border:none;"
                + "border-radius:6px;"
                + "cursor:pointer;"
                + "'>"
                + "Quay lại"
                + "</button>"
                + "</body>"
                + "</html>"
        );
    }

    @Override
    public void destroy() {
        publicUrls.clear();
        staffUrls.clear();
        adminUrls.clear();
        managerUrls.clear();
        receptionistUrls.clear();
        managerReceptionistUrls.clear();
    }
}