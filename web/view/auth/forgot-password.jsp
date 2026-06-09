<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
<<<<<<< Updated upstream
    <title>Quên mật khẩu</title>
=======
    <title>Đăng nhập nhân viên</title>
>>>>>>> Stashed changes

    <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/navbar.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/footer.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/auth.css">
</head>
<body>

    <jsp:include page="/view/common/navbar.jsp" />

    <main class="auth-page">
        <div class="auth-card">
<<<<<<< Updated upstream
            <div class="auth-label-top">KHÔI PHỤC MẬT KHẨU</div>

            <h1>Quên mật khẩu</h1>

            <p class="auth-subtitle">
                Nhập email nhân viên của bạn để nhận mã xác minh.
=======
            <div class="auth-label-top">CỔNG NHÂN VIÊN</div>

            <h1>Đăng nhập</h1>

            <p class="auth-subtitle">
                Dành cho lễ tân, quản lý và quản trị viên.
>>>>>>> Stashed changes
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

<<<<<<< Updated upstream
            <form class="auth-form" action="<%= request.getContextPath() %>/forgot-password" method="post">
=======
            <form class="auth-form" action="<%= request.getContextPath() %>/login" method="post">
>>>>>>> Stashed changes
                <div class="auth-form-group">
                    <label>Tên đăng nhập</label>
                    <input 
                        type="text" 
                        name="username" 
                        value="<%= request.getParameter("username") != null ? request.getParameter("username") : "" %>"
                        required
                    >
                </div>

<<<<<<< Updated upstream
                <button type="submit" class="auth-btn">Gửi mã</button>
            </form>

            <div class="auth-link">
                <a href="<%= request.getContextPath() %>/logout">
                    Quay lại đăng nhập
=======
                <div class="auth-form-group">
                    <label>Mật khẩu</label>
                    <input 
                        type="password" 
                        name="password" 
                        required
                    >
                </div>

                <button type="submit" class="auth-btn">Đăng nhập</button>
            </form>

            <div class="auth-link">
                <a href="<%= request.getContextPath() %>/forgot-password">
                    Quên mật khẩu?
>>>>>>> Stashed changes
                </a>
            </div>

            <div class="auth-line"></div>
        </div>
    </main>

    <jsp:include page="/view/common/footer.jsp" />

</body>
</html>