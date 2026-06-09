<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đăng nhập nhân viên</title>

    <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/navbar.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/footer.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/auth.css">
</head>
<body>

    <jsp:include page="/view/common/navbar.jsp" />

    <main class="auth-page">
        <div class="auth-card">
            <div class="auth-label-top">CỔNG NHÂN VIÊN</div>

            <h1>Đăng nhập</h1>

            <p class="auth-subtitle">
                Dành cho lễ tân, quản lý và quản trị viên.
            </p>

            <% if (request.getAttribute("error") != null) { %>
                <p class="auth-message-error">
                    <%= request.getAttribute("error") %>
                </p>
            <% } %>

            <% if ("success".equals(request.getParameter("reset"))) { %>
                <p class="auth-message-success">
                    Đặt lại mật khẩu thành công. Vui lòng đăng nhập lại.
                </p>
            <% } %>

            <form class="auth-form" action="<%= request.getContextPath() %>/login" method="post">
                <div class="auth-form-group">
                    <label>Tên đăng nhập</label>
<<<<<<< Updated upstream
                    <input type="text" name="username" required>
=======
                    <input 
                        type="text" 
                        name="username" 
                        value="<%= request.getParameter("username") != null ? request.getParameter("username") : "" %>"
                        required
                    >
>>>>>>> Stashed changes
                </div>

                <div class="auth-form-group">
                    <label>Mật khẩu</label>
<<<<<<< Updated upstream
                    <input type="password" name="password" required>
=======
                    <input 
                        type="password" 
                        name="password" 
                        required
                    >
>>>>>>> Stashed changes
                </div>

                <button type="submit" class="auth-btn">Đăng nhập</button>
            </form>

            <div class="auth-link">
                <a href="<%= request.getContextPath() %>/forgot-password">
                    Quên mật khẩu?
                </a>
            </div>

            <div class="auth-line"></div>
        </div>
    </main>

    <jsp:include page="/view/common/footer.jsp" />

</body>
</html>