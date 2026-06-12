<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    String pendingEmail = "";

    if (session != null && session.getAttribute("pendingResetEmail") != null) {
        pendingEmail = (String) session.getAttribute("pendingResetEmail");
    }
%>

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
                <div class="auth-label-top">XÁC MINH EMAIL</div>

                <h1>Nhập mã</h1>

                <p class="auth-subtitle">
                    Mã xác minh đã được gửi đến email của bạn.
                </p>

                <% if (pendingEmail != null && !pendingEmail.isEmpty()) { %>

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
                        <input
                            type="text"
                            name="code"
                            value="<%= request.getParameter("code") != null ? request.getParameter("code") : "" %>"
                            required
                            >
                    </div>

                    <button type="submit" class="auth-btn">Xác minh</button>
                </form>

                <form class="auth-form" action="<%= request.getContextPath() %>/verify-code" method="post">
                    <input type="hidden" name="action" value="resend">
                    <button type="submit" class="auth-btn auth-btn-secondary">Gửi lại mã</button>
                </form>

                <div class="auth-link">
                    <a href="<%= request.getContextPath() %>/forgot-password">
                        Đổi email nhận mã
                    </a>
                </div>

                <div class="auth-line"></div>
            </div>
        </main>

        <jsp:include page="/view/common/footer.jsp" />

    </body>
</html>