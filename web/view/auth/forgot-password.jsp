<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Forgot Password</title>
</head>
<body>

    <h1>Forgot Password</h1>

    <% if (request.getAttribute("error") != null) { %>
        <p style="color:red;"><%= request.getAttribute("error") %></p>
    <% } %>

    <% if (request.getAttribute("message") != null) { %>
        <p style="color:green;"><%= request.getAttribute("message") %></p>
    <% } %>

    <form action="<%= request.getContextPath() %>/forgot-password" method="post">
        <label>Enter your staff email:</label><br>
        <input type="email" name="email" required>
        <br><br>

        <button type="submit">Send Code</button>
    </form>

    <br>

    <a href="<%= request.getContextPath() %>/login?showLogin=true">Back to login</a>

</body>
</html>