<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Xác minh mã</title>

    <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/navbar.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/footer.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/auth.css">
</head>
<body>

    <jsp:include page="/view/common/navbar.jsp" />

    <main class="auth-page">
        <div class="auth-card">

            <div class="auth-label-top">XÁC MINH</div>

            <h1>Xác minh mã</h1>

            <p class="auth-subtitle">
                Nhập mã xác minh đã được gửi đến email nhân viên của bạn.
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

            <form class="auth-form" action="<%= request.getContextPath() %>/verify-code" method="post">

                <div class="auth-form-group">
                    <label>Mã xác minh</label>
                    <input type="text" name="code" required>
                </div>

                <button type="submit" class="auth-btn">Xác minh mã</button>
            </form>

            <form id="resendForm" action="<%= request.getContextPath() %>/verify-code" method="post" hidden>
                <input type="hidden" name="action" value="resend">
            </form>

            <div class="auth-link-row">
                <a href="#" onclick="document.getElementById('resendForm').submit(); return false;">
                    Gửi lại mã
                </a>

                <a href="<%= request.getContextPath() %>/login?showLogin=true">
                    Quay lại đăng nhập
                </a>
            </div>

            <div class="auth-line"></div>

        </div>
    </main>

    <jsp:include page="/view/common/footer.jsp" />

</body>
</html>