<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Verify Code</title>
</head>
<body>

    <h1>Verify Code</h1>

    <% if (request.getAttribute("error") != null) { %>
        <p style="color:red;"><%= request.getAttribute("error") %></p>
    <% } %>

    <% if (request.getAttribute("message") != null) { %>
        <p style="color:green;"><%= request.getAttribute("message") %></p>
    <% } %>

    <form action="<%= request.getContextPath() %>/verify-code" method="post">
        <input type="hidden" name="action" value="verify">

        <label>Enter reset code:</label><br>
        <input type="text" name="code" maxlength="6" required>
        <br><br>

        <button type="submit">Verify Code</button>
    </form>

    <br>

    <form action="<%= request.getContextPath() %>/verify-code" method="post">
        <input type="hidden" name="action" value="resend">
        <button type="submit">Send code again</button>
    </form>

</body>
</html>