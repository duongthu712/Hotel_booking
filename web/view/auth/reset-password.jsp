<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Reset Password</title>

    <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/navbar.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/footer.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/auth.css">
</head>
<body>

    <jsp:include page="/view/common/navbar.jsp" />

    <main class="auth-page">
        <div class="auth-card">

            <div class="auth-label-top">NEW PASSWORD</div>

            <h1>Reset password</h1>

            <p class="auth-subtitle">
                Create a new password for your staff account.
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
                    <label>New password</label>
                    <input type="password" name="newPassword" required>
                </div>

                <div class="auth-form-group">
                    <label>Confirm password</label>
                    <input type="password" name="confirmPassword" required>
                </div>

                <button type="submit" class="auth-btn">Reset password</button>
            </form>

            <div class="auth-link">
                <a href="<%= request.getContextPath() %>/logout">
                    Back to sign in
                </a>
            </div>

            <div class="auth-line"></div>

        </div>
    </main>

    <jsp:include page="/view/common/footer.jsp" />

</body>
</html>