<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
<<<<<<< Updated upstream
    <title>Xác minh mã</title>
=======
    <title>Quên mật khẩu</title>
>>>>>>> Stashed changes

    <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/navbar.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/footer.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/auth.css">
</head>
<body>

    <jsp:include page="/view/common/navbar.jsp" />

    <main class="auth-page">
        <div class="auth-card">
            <div class="auth-label-top">KHÔI PHỤC TÀI KHOẢN</div>

<<<<<<< Updated upstream
            <div class="auth-label-top">XÁC MINH</div>

            <h1>Xác minh mã</h1>

            <p class="auth-subtitle">
                Nhập mã xác minh đã được gửi đến email nhân viên của bạn.
=======
            <h1>Quên mật khẩu</h1>

            <p class="auth-subtitle">
                Nhập email nhân viên để nhận mã xác minh đặt lại mật khẩu.
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
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
                <a href="<%= request.getContextPath() %>/logout">
=======
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
>>>>>>> Stashed changes
                    Quay lại đăng nhập
                </a>
            </div>

            <div class="auth-line"></div>
        </div>
    </main>

    <jsp:include page="/view/common/footer.jsp" />

</body>
</html>