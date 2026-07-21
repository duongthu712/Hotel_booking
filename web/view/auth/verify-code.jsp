<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    String pendingEmail = null;
    String resetMessage = null;

    if (session != null) {
        pendingEmail = (String) session.getAttribute("pendingResetEmail");
        resetMessage = (String) session.getAttribute("resetMessage");

        // Chỉ hiển thị thông báo gửi mã một lần
        if (resetMessage != null) {
            session.removeAttribute("resetMessage");
        }
    }

    String errorMessage = (String) request.getAttribute("error");
    String successMessage = (String) request.getAttribute("message");

    String enteredCode = request.getParameter("code");

    if (enteredCode == null) {
        enteredCode = "";
    }
%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">

        <meta name="viewport"
              content="width=device-width, initial-scale=1.0">

        <title>Xác minh mã</title>

        <link rel="stylesheet"
              href="<%= request.getContextPath() %>/view/assets/css/navbar.css">

        <link rel="stylesheet"
              href="<%= request.getContextPath() %>/view/assets/css/footer.css">

        <link rel="stylesheet"
              href="<%= request.getContextPath() %>/view/assets/css/auth.css">
    </head>

    <body>

        <jsp:include page="/view/common/navbar.jsp"/>

        <main class="auth-page">
            <div class="auth-card">

                <div class="auth-label-top">
                    XÁC MINH EMAIL
                </div>

                <h1>Nhập mã</h1>

                <% if (pendingEmail != null && !pendingEmail.trim().isEmpty()) { %>

                    <p class="auth-subtitle">
                        Mã xác minh đã được gửi đến
                        <strong><%= pendingEmail %></strong>.
                    </p>

                    <% if (resetMessage != null && !resetMessage.trim().isEmpty()) { %>
                        <p class="auth-message-success">
                            <%= resetMessage %>
                        </p>
                    <% } %>

                    <% if (errorMessage != null && !errorMessage.trim().isEmpty()) { %>
                        <p class="auth-message-error">
                            <%= errorMessage %>
                        </p>
                    <% } %>

                    <% if (successMessage != null && !successMessage.trim().isEmpty()) { %>
                        <p class="auth-message-success">
                            <%= successMessage %>
                        </p>
                    <% } %>

                    <!-- Form xác minh mã -->
                    <form class="auth-form"
                          action="<%= request.getContextPath() %>/verify-code"
                          method="post">

                        <input type="hidden"
                               name="action"
                               value="verify">

                        <div class="auth-form-group">
                            <label for="code">
                                Mã xác minh
                            </label>

                            <input
                                id="code"
                                type="text"
                                name="code"
                                value="<%= enteredCode %>"
                                placeholder="Nhập mã bắt đầu bằng LMH"
                                autocomplete="one-time-code"
                                maxlength="8"
                                required
                            >
                        </div>

                        <button type="submit"
                                class="auth-btn">
                            Xác minh
                        </button>
                    </form>

                    <!-- Form gửi lại mã -->
                    <form class="auth-form"
                          action="<%= request.getContextPath() %>/verify-code"
                          method="post">

                        <input type="hidden"
                               name="action"
                               value="resend">

                        <button type="submit"
                                class="auth-btn auth-btn-secondary">
                            Gửi lại mã
                        </button>
                    </form>

                    <div class="auth-link">
                        <a href="<%= request.getContextPath() %>/forgot-password">
                            Đổi email nhận mã
                        </a>
                    </div>

                <% } else { %>

                    <p class="auth-message-error">
                        Phiên khôi phục mật khẩu không tồn tại hoặc đã hết hạn.
                    </p>

                    <div class="auth-link">
                        <a href="<%= request.getContextPath() %>/forgot-password">
                            Quay lại trang quên mật khẩu
                        </a>
                    </div>

                <% } %>

                <div class="auth-line"></div>

            </div>
        </main>

        <jsp:include page="/view/common/footer.jsp"/>

    </body>
</html>