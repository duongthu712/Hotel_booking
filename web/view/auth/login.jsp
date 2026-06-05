<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Staff Login</title>

    <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/navbar.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/footer.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/auth.css">
</head>
<body>

    <jsp:include page="/view/common/navbar.jsp" />

    <main class="auth-page">
        <div class="auth-card">

            <div class="auth-label-top">STAFF PORTAL</div>

            <h1>Sign in</h1>

            <p class="auth-subtitle">
                Reception, management and administration access.
            </p>

            <% if (request.getAttribute("error") != null) { %>
                <p class="auth-message-error">
                    <%= request.getAttribute("error") %>
                </p>
            <% } %>

            <% if ("success".equals(request.getParameter("reset"))) { %>
                <p class="auth-message-success">
                    Password reset successfully. Please login again.
                </p>
            <% } %>

            <form class="auth-form" action="<%= request.getContextPath() %>/login" method="post">

                <div class="auth-form-group">
                    <label>Username</label>
                    <input type="text" name="username" required>
                </div>

                <div class="auth-form-group">
                    <label>Password</label>
                    <input type="password" name="password" required>
                </div>

                <button type="submit" class="auth-btn">Sign in</button>
            </form>

            <div class="auth-link">
                <a href="<%= request.getContextPath() %>/forgot-password">
                    Forgot password?
                </a>
            </div>

            <div class="auth-line"></div>

        </div>
    </main>

    <jsp:include page="/view/common/footer.jsp" />

</body>
</html>