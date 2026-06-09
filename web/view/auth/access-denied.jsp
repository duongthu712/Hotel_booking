<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Từ chối truy cập</title>

    <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/navbar.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/footer.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/auth.css">
</head>
<body>

    <jsp:include page="/view/common/navbar.jsp" />

    <main class="auth-page">
        <div class="auth-card">
            <div class="auth-label-top">ACCESS DENIED</div>

            <h1>Không có quyền truy cập</h1>

            <p class="auth-subtitle">
                Tài khoản của bạn không có quyền truy cập chức năng này.
            </p>

            <p class="auth-message-error">
                Vui lòng quay lại đúng trang chức năng theo vai trò của bạn hoặc đăng nhập bằng tài khoản có quyền phù hợp.
            </p>

            <div class="auth-link-row">
                <a href="<%= request.getContextPath() %>/login">Quay lại đăng nhập</a>
                <a href="<%= request.getContextPath() %>/home">Về trang chủ</a>
            </div>

            <div class="auth-line"></div>
        </div>
    </main>

    <jsp:include page="/view/common/footer.jsp" />

</body>
</html>