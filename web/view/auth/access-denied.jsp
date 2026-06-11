<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.StaffAccount"%>

<%
    String dashboardUrl = request.getContextPath() + "/login";

    if (session != null && session.getAttribute("staff") != null) {
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");

        if (staff != null && staff.getRole() != null) {
            String role = staff.getRole().trim();

            if (role.equalsIgnoreCase("Lễ tân")) {
                dashboardUrl = request.getContextPath() + "/view/receptionist/dashboard.jsp";
            } else if (role.equalsIgnoreCase("Quản lý")) {
                dashboardUrl = request.getContextPath() + "/view/manager/dashboard.jsp";
            } else if (role.equalsIgnoreCase("Quản trị viên")) {
                dashboardUrl = request.getContextPath() + "/view/admin/staff-management.jsp";
            }
        }
    }
%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Từ chối truy cập</title>

        <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/navbar.css">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/footer.css">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/auth.css">
    </head>
    <body>

        <jsp:include page="/view/common/navbar.jsp" />

        <main class="auth-page">
            <div class="auth-card">
                <div class="auth-label-top">ACCESS DENIED</div>

                <h1>Không có quyền truy cập</h1>

                <p class="auth-subtitle">
                    Tài khoản của bạn không có quyền truy cập chức năng này.
                </p>

                <p class="auth-message-error">
                    Vui lòng quay về đúng màn hình làm việc hoặc đăng xuất để đăng nhập bằng tài khoản khác.
                </p>

                <div class="auth-action-row">
                    <a class="auth-text-link" href="<%= dashboardUrl %>">
                        Quay về dashboard
                    </a>

                    <a class="auth-text-link" href="<%= request.getContextPath() %>/logout">
                        Đăng xuất
                    </a>
                </div>

                <div class="auth-line"></div>
            </div>
        </main>

        <jsp:include page="/view/common/footer.jsp" />

    </body>
</html>