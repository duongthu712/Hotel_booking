<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quên mật khẩu</title>

    <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/navbar.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/footer.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/auth.css">
</head>
<body>

    <jsp:include page="/view/common/navbar.jsp" />

    <main class="auth-page">
        <div class="auth-card">
            <div class="auth-label-top">KHÔI PHỤC TÀI KHOẢN</div>

            <h1>Quên mật khẩu</h1>

            <p class="auth-subtitle">
                Nhập email nhân viên để nhận mã xác minh đặt lại mật khẩu.
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

            <form class="auth-form" action="<%= request.getContextPath() %>/forgot-password" method="post">
                <div class="auth-form-group">
                    <label>Email</label>
                    <input
                        type="email"
                        name="email"
                        value="<%= request.getParameter("email") != null ? request.getParameter("email") : "" %>"
                        required
                    >
                </div>

                <button type="submit" class="auth-btn">Gửi mã xác minh</button>
            </form>

            <div class="auth-link">
                <a href="<%= request.getContextPath() %>/login">
                    Quay lại đăng nhập
                </a>
            </div>

            <div class="auth-line"></div>
        </div>
    </main>

    <jsp:include page="/view/common/footer.jsp" />

</body>
</html>