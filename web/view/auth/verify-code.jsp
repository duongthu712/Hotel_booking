<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Verify Code</title>

    <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/navbar.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/footer.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/auth.css">
</head>
<body>

    <jsp:include page="/view/common/navbar.jsp" />

    <main class="auth-page">
        <div class="auth-card">

            <div class="auth-label-top">VERIFICATION</div>

            <h1>Verify code</h1>

            <p class="auth-subtitle">
                Enter the verification code sent to your staff email.
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
                    <label>Code</label>
                    <input type="text" name="code" required>
                </div>

                <button type="submit" class="auth-btn">Verify code</button>
            </form>

            <form id="resendForm" action="<%= request.getContextPath() %>/verify-code" method="post" hidden>
                <input type="hidden" name="action" value="resend">
            </form>

            <div class="auth-link-row">
                <a href="#" onclick="document.getElementById('resendForm').submit(); return false;">
                    Resend code
                </a>
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