<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">

        <meta name="viewport"
              content="width=device-width, initial-scale=1.0">

        <title>Đăng nhập nhân viên</title>

        <link rel="stylesheet"
              href="<%= request.getContextPath() %>/view/assets/css/navbar.css">

        <link rel="stylesheet"
              href="<%= request.getContextPath() %>/view/assets/css/footer.css">

        <link rel="stylesheet"
              href="<%= request.getContextPath() %>/view/assets/css/auth.css">
    </head>

    <body>

        <jsp:include page="/view/common/navbar.jsp" />

        <main class="auth-page">
            <div class="auth-card">

                <div class="auth-label-top">
                    CỔNG NHÂN VIÊN
                </div>

                <h1>Đăng nhập</h1>

                <p class="auth-subtitle">
                    Dành cho lễ tân, quản lý và quản trị viên.
                </p>

                <% if (request.getAttribute("error") != null) { %>
                <p class="auth-message-error">
                    <%= request.getAttribute("error") %>
                </p>
                <% } %>

                <% if (request.getAttribute("success") != null) { %>
                <p class="auth-message-success">
                    <%= request.getAttribute("success") %>
                </p>
                <% } %>

                <form
                    class="auth-form"
                    action="<%= request.getContextPath() %>/login"
                    method="post"
                    >

                    <div class="auth-form-group">
                        <label for="username">
                            Tên đăng nhập
                        </label>

                        <input
                            id="username"
                            type="text"
                            name="username"
                            value="<%=
                                request.getAttribute("username") != null
                                ? request.getAttribute("username")
                                : ""
                            %>"
                            autocomplete="username"
                            required
                            >
                    </div>

                    <div class="auth-form-group">
                        <label for="password">
                            Mật khẩu
                        </label>

                        <input
                            id="password"
                            type="password"
                            name="password"
                            autocomplete="current-password"
                            required
                            >
                    </div>

                    <button type="submit" class="auth-btn">
                        Đăng nhập
                    </button>
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