<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.StaffAccount"%>

<%
    StaffAccount staff = (StaffAccount) session.getAttribute("staff");

    String fullName = "Không xác định";
    String role = "Không xác định";
    String dashboardUrl = request.getContextPath() + "/login";

    if (staff != null) {
        if (staff.getFullName() != null && !staff.getFullName().trim().isEmpty()) {
            fullName = staff.getFullName();
        }

        if (staff.getRole() != null && !staff.getRole().trim().isEmpty()) {
            role = staff.getRole().trim();

            if (role.equalsIgnoreCase("Quản trị viên")) {
                dashboardUrl = request.getContextPath() + "/view/admin/staff-management.jsp";
            } else if (role.equalsIgnoreCase("Quản lý")) {
                dashboardUrl = request.getContextPath() + "/view/manager/dashboard.jsp";
            } else if (role.equalsIgnoreCase("Lễ tân")) {
                dashboardUrl = request.getContextPath() + "/view/receptionist/dashboard.jsp";
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
                Bạn đang đăng nhập với vai trò <strong><%= role %></strong>.
                Vui lòng quay lại đúng trang làm việc theo vai trò của bạn hoặc đăng xuất để sử dụng tài khoản khác.
            </p>

            <div class="auth-info-box">
                <p><strong>Nhân viên:</strong> <%= fullName %></p>
                <p><strong>Vai trò hiện tại:</strong> <%= role %></p>
            </div>

                <div class="auth-link-row" style="display: flex; justify-content: center; gap: 36px; margin-top: 28px;">
                    <a href="<%= dashboardUrl %>"
                       style="font-size: 16px; font-weight: bold; color: #073842;">
                        Về trang làm việc của tôi
                    </a>

                    <a href="<%= request.getContextPath() %>/logout"
                       style="font-size: 16px; font-weight: bold; color: #073842;">
                        Đăng xuất
                    </a>
                </div>

            <div class="auth-line"></div>
        </div>
    </main>

    <jsp:include page="/view/common/footer.jsp" />

</body>
</html>