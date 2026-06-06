<%-- 
    Document   : header
    Created on : Jun 6, 2026, 10:45:29 AM
    Author     : LinhLTHE200306
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.StaffAccount" %>

<% StaffAccount headeAcc = (StaffAccount)session.getAttribute("staff");%>
<% if (headeAcc != null) { %>
<header>
    <div>
        <p>La Mer</p>
        <p><%= headeAcc.getRole()%></p>
    </div>
    <div>
        <p><%= headeAcc.getFullName()%></p>
        <form action="logout" method="POST">
            <button type="submit">Đăng xuất</button>
        </form>
    </div>
</header>
<%}%>

