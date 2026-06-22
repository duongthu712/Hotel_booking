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

    private final Set<String> adminUrls = Set.of(
            "/view/admin",
            "/StaffAccountList",
            "/StaffAccountDetail",
            "/StaffAccountEdit",
            "/StaffAccountCreate",
            "/StaffAccountDelete"
    );

    private final Set<String> managerUrls = Set.of(
            "/view/manager",
            "/dashboard.jsp",
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
            "/PolicyList",
            "/FeedbackList",
            "/HotelNewsCreate",
            "/HotelNewsEdit",
            "/HotelNewsDelete"
    );

    private final Set<String> receptionistUrls = Set.of(
            "/view/receptionist",
            "/receptionist/dashboard",
            "/receptionist/checkout",
            "/receptionist/bookings",
            "/receptionist/walkin",
            "/receptionist/requests",
            "/assign-room",
            "/billing",
            "/booking-list",
            "/checkin",
            "/check-in",
            "/check-out",
            "/invoice",
            "/payment-verification",
            "/request-processing",
            "/walk-in-booking",
            "/DepositPaymentList",
            "/DepositPaymentVerify",
            "/DepositPaymentReject"
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
        String uri = req.getRequestURI();
        String path = uri.substring(contextPath.length());

        int semicolonIndex = path.indexOf(";");
        if (semicolonIndex != -1) {
            path = path.substring(0, semicolonIndex);
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

        if (requiredRole == null) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("staff") == null) {
            res.sendRedirect(contextPath + "/login");
            return;
        }

        StaffAccount staff = (StaffAccount) session.getAttribute("staff");

        if (staff == null || staff.getRoleEn() == null) {
            res.sendRedirect(contextPath + "/access-denied");
            return;
        }

        String role = staff.getRoleEn().trim();

        if (!role.equalsIgnoreCase(requiredRole)) {
            res.sendRedirect(contextPath + "/access-denied");
            return;
        }

        chain.doFilter(request, response);
    }

    private String getRequiredRole(String path) {
        if (matches(path, adminUrls)) {
            return "ADMIN";
        }

        if (matches(path, managerUrls)) {
            return "MANAGER";
        }

        if (matches(path, receptionistUrls)) {
            return "RECEPTIONIST";
        }

        return null;
    }

    private boolean matches(String path, Set<String> urls) {
        for (String url : urls) {
            if (path.equals(url) || path.startsWith(url + "/")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {
    }
}
