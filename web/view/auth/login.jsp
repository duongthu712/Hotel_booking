<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Staff Login</title>
</head>
<body>

    <h1>Staff Login</h1>

    <% if (request.getAttribute("error") != null) { %>
        <p style="color:red;"><%= request.getAttribute("error") %></p>
    <% } %>

    <% if ("success".equals(request.getParameter("reset"))) { %>
        <p style="color:green;">Password reset successfully. Please login again.</p>
    <% } %>

    <form action="<%= request.getContextPath() %>/login" method="post">
        <label>Username:</label><br>
        <input type="text" name="username" required>
        <br><br>

        <label>Password:</label><br>
        <input type="password" name="password" required>
        <br><br>

        <button type="submit">Login</button>
    </form>

    <br>

    <a href="<%= request.getContextPath() %>/forgot-password">Forgot password?</a>

</body>
</html>