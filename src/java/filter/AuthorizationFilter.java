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
import java.util.Set;
import model.StaffAccount;

public class AuthorizationFilter implements Filter {

    private final Set<String> publicUrls = Set.of(
            "/",
            "/home",
            "/login",
            "/logout",
            "/forgot-password",
            "/verify-code",
            "/reset-password",
            "/access-denied",
            "/policies",
            "/room-detail",
            "/booking-form",
            "/quick-booking",
            "/booking-payment",
            "/booking-success",
            "/booking-detail"
    );

    private final Set<String> staffOnlyUrls = Set.of(
            "/profile",
            "/profile/change-password"
    );

    private final Set<String> adminJspPrefixes = Set.of(
            "/view/admin"
    );

    private final Set<String> managerJspPrefixes = Set.of(
            "/view/manager"
    );

    private final Set<String> receptionistJspPrefixes = Set.of(
            "/view/receptionist"
    );

    private final Set<String> adminServletUrls = Set.of(
            "/StaffAccountList",
            "/StaffAccountDetail",
            "/StaffAccountEdit",
            "/StaffAccountCreate",
            "/StaffAccountDelete"
    );

    private final Set<String> managerServletUrls = Set.of(
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
            "/HotelNewsCreate",
            "/HotelNewsEdit",
            "/HotelNewsDelete",
            "/feedback-management",
            "/booking-list"
    );

    private final Set<String> receptionistServletUrls = Set.of(
            "/receptionist-dashboard",
            "/DepositPaymentList",
            "/DepositPaymentVerify",
            "/DepositPaymentReject",
            "/Checkout",
            "/InvoiceCreate",
            "/InvoicePDF",
            "/BillingList",
            "/booking-list"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String contextPath = req.getContextPath();
        String path = req.getRequestURI().substring(contextPath.length());

        path = removePathParameter(path);

        if (isStaticResource(path)) {
            chain.doFilter(request, response);
            return;
        }

        if (publicUrls.contains(path)) {
            chain.doFilter(request, response);
            return;
        }

        if (!path.equals("/logout") && path.endsWith("/logout")) {
            res.sendRedirect(contextPath + "/logout");
            return;
        }

        if (path.equals("/view/admin/staff-management.jsp")) {
            res.sendRedirect(contextPath + "/StaffAccountList");
            return;
        }

        String requiredRole = getRequiredRole(path);
        boolean needStaffLogin = requiredRole != null || staffOnlyUrls.contains(path);

        if (!needStaffLogin) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("staff") == null) {
            res.sendRedirect(contextPath + "/login");
            return;
        }

        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        String currentRole = getCurrentRole(staff);

        if (currentRole.isEmpty()) {
            res.sendRedirect(contextPath + "/access-denied");
            return;
        }

        if (staffOnlyUrls.contains(path)) {
            chain.doFilter(request, response);
            return;
        }

        if (!currentRole.equals(requiredRole)) {
            res.sendRedirect(contextPath + "/access-denied");
            return;
        }

        chain.doFilter(request, response);
    }

    private String getRequiredRole(String path) {
        if (matchesPrefix(path, adminJspPrefixes) || adminServletUrls.contains(path)) {
            return "ADMIN";
        }

        if (matchesPrefix(path, managerJspPrefixes) || managerServletUrls.contains(path)) {
            return "MANAGER";
        }

        if (matchesPrefix(path, receptionistJspPrefixes) || receptionistServletUrls.contains(path)) {
            return "RECEPTIONIST";
        }

        return null;
    }

    private boolean matchesPrefix(String path, Set<String> prefixes) {
        for (String prefix : prefixes) {
            if (path.equals(prefix) || path.startsWith(prefix + "/")) {
                return true;
            }
        }

        return false;
    }

    private String getCurrentRole(StaffAccount staff) {
        if (staff == null) {
            return "";
        }

        String role = "";

        if (staff.getRoleEn() != null && !staff.getRoleEn().trim().isEmpty()) {
            role = staff.getRoleEn().trim();
        } else if (staff.getRole() != null && !staff.getRole().trim().isEmpty()) {
            role = staff.getRole().trim();
        }

        if (role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("Quản trị viên")) {
            return "ADMIN";
        }

        if (role.equalsIgnoreCase("MANAGER") || role.equalsIgnoreCase("Quản lý")) {
            return "MANAGER";
        }

        if (role.equalsIgnoreCase("RECEPTIONIST") || role.equalsIgnoreCase("Lễ tân")) {
            return "RECEPTIONIST";
        }

        return "";
    }

    private String removePathParameter(String path) {
        int semicolonIndex = path.indexOf(";");

        if (semicolonIndex != -1) {
            return path.substring(0, semicolonIndex);
        }

        return path;
    }

    private boolean isStaticResource(String path) {
        return path.startsWith("/view/assets/")
                || path.startsWith("/assets/")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.endsWith(".css")
                || path.endsWith(".js")
                || path.endsWith(".png")
                || path.endsWith(".jpg")
                || path.endsWith(".jpeg")
                || path.endsWith(".gif")
                || path.endsWith(".svg")
                || path.endsWith(".ico");
    }

    @Override
    public void destroy() {
    }
}
