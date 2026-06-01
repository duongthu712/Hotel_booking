<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>La Mer Hotel - Staff Authentication</title>
    <style>
        body { font-family: 'Segoe UI', Arial, sans-serif; background-color: #0f2027; background: linear-gradient(to right, #2c5364, #203a43, #0f2027); display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }
        .login-card { background: #ffffff; padding: 40px; border-radius: 10px; box-shadow: 0 10px 25px rgba(0,0,0,0.3); width: 100%; max-width: 400px; box-sizing: border-box; }
        .brand-title { text-align: center; color: #102a43; font-size: 28px; margin-bottom: 5px; font-weight: bold; letter-spacing: 1px; }
        .brand-subtitle { text-align: center; color: #627d98; font-size: 14px; margin-bottom: 30px; text-transform: uppercase; }
        .form-group { display: flex; flex-direction: column; margin-bottom: 20px; }
        label { font-size: 13px; font-weight: 600; color: #334e68; margin-bottom: 6px; }
        input { padding: 12px; border: 1px solid #bcccdc; border-radius: 5px; font-size: 15px; background-color: #f0f4f8; transition: all 0.2s; }
        input:focus { border-color: #102a43; background-color: #fff; outline: none; }
        .btn-login { background-color: #102a43; color: white; padding: 14px; border: none; border-radius: 5px; font-size: 16px; font-weight: bold; cursor: pointer; width: 100%; margin-top: 10px; transition: background 0.2s; }
        .btn-login:hover { background-color: #243e56; }
        .alert { padding: 12px; border-radius: 5px; font-size: 14px; margin-bottom: 20px; font-weight: 500; text-align: center; }
        .alert-danger { color: #a61b24; background-color: #fce8e6; border: 1px solid #f1b0b7; }
        .alert-info { color: #0c5460; background-color: #d1ecf1; border: 1px solid #bee5eb; }
    </style>
</head>
<body>

    <div class="login-card">
        <div class="brand-title">LA MER HOTEL</div>
        <div class="brand-subtitle">Staff Portal Access</div>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">${errorMessage}</div>
        </c:if>

        <c:if test="${not empty param.msg}">
            <div class="alert alert-info">${param.msg}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/auth/login" method="POST">
            <div class="form-group">
                <label>Username</label>
                <input type="text" name="username" placeholder="Enter your staff username" required autocomplete="off">
            </div>

            <div class="form-group">
                <label>Password</label>
                <input type="password" name="password" placeholder="Enter your password" required>
            </div>

            <button type="submit" class="btn-login">Sign In</button>
        </form>
    </div>

</body>
</html>