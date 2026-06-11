<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đặt lại mật khẩu</title>

    <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/navbar.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/footer.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/auth.css">
</head>
<body>

    <jsp:include page="/view/common/navbar.jsp" />

    <main class="auth-page">
        <div class="auth-card">
            <div class="auth-label-top">TẠO MẬT KHẨU MỚI</div>

            <h1>Đặt lại mật khẩu</h1>

            <p class="auth-subtitle">
                Nhập mật khẩu mới để hoàn tất quá trình khôi phục tài khoản.
            </p>

            <% if (request.getAttribute("error") != null) { %>
                <p class="auth-message-error">
                    <%= request.getAttribute("error") %>
                </p>
            <% } %>

            <% if (request.getAttribute("message") != null) { %>
                <p class="auth-message-success">
                    <%= request.getAttribute("message") %>
                </p>
            <% } %>

            <form class="auth-form" action="<%= request.getContextPath() %>/reset-password" method="post">
                <div class="auth-form-group">
                    <label>Mật khẩu mới</label>
                    <input
                        type="password"
                        name="newPassword"
                        required
                    >
                </div>

                <div class="auth-form-group">
                    <label>Nhập lại mật khẩu mới</label>
                    <input
                        type="password"
                        name="confirmPassword"
                        required
                    >
                </div>

                <button type="submit" class="auth-btn">Đặt lại mật khẩu</button>
            </form>

            <div class="auth-link">
                <a href="<%= request.getContextPath() %>/logout">
                    Quay lại đăng nhập
                </a>
            </div>

            <div class="auth-line"></div>
        </div>
    </main>

    <jsp:include page="/view/common/footer.jsp" />

</body>
</html>