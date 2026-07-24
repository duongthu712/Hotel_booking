<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    String pendingEmail = "";

    if (session.getAttribute("pendingResetEmail") != null) {
        pendingEmail = (String) session.getAttribute("pendingResetEmail");
    }
%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <title>Xác minh mã</title>

        <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/navbar.css">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/footer.css">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/auth.css">

        <style>
            .verify-actions {
                display: flex;
                align-items: center;
                justify-content: center;
                gap: 30px;
                margin-top: 22px;
            }

            .verify-actions a {
                color: #003f4a;
                font-family: inherit;
                font-size: 16px;
                text-decoration: none;
                cursor: pointer;
            }

            .verify-actions a:hover {
                text-decoration: underline;
            }
        </style>
    </head>

    <body>
        <jsp:include page="/view/common/navbar.jsp" />

        <main class="auth-page">
            <div class="auth-card">
                <div class="auth-label-top">XÁC MINH EMAIL</div>

                <h1>Nhập mã</h1>

                <p class="auth-subtitle">
                    Nhập mã xác minh đã được gửi đến email của bạn.
                </p>

                <% if (!pendingEmail.isEmpty()) { %>

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

                <form class="auth-form"
                      action="<%= request.getContextPath() %>/verify-code"
                      method="post">

                    <div class="auth-form-group">
                        <label for="code">Mã xác minh</label>

                        <input id="code"
                               type="text"
                               name="code"
                               placeholder="Nhập mã xác minh"
                               value="<%= request.getParameter("code") != null
                                           ? request.getParameter("code") : "" %>"
                               autocomplete="off"
                               required>
                    </div>

                    <button type="submit" class="auth-btn">
                        Xác minh
                    </button>
                </form>

                <form id="resendCodeForm"
                      action="<%= request.getContextPath() %>/verify-code"
                      method="post"
                      style="display: none;">

                    <input type="hidden" name="action" value="resend">
                </form>

                <div class="verify-actions">
                    <a href="#"
                       onclick="document.getElementById('resendCodeForm').submit(); return false;">
                        Gửi lại mã
                    </a>

                    <a href="<%= request.getContextPath() %>/forgot-password">
                        Đổi email nhận mã
                    </a>
                </div>

                <% } else { %>

                <p class="auth-message-error">
                    Phiên xác minh không tồn tại hoặc đã hết hạn.
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

        <jsp:include page="/view/common/footer.jsp" />
    </body>
</html>