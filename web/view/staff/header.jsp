<%-- 
    Document   : header
    Created on : Jun 6, 2026, 10:45:29 AM
    Author     : LinhLTHE200306
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.StaffAccount" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/staff-header.css" type="text/css">

<% StaffAccount headAcc = (StaffAccount)session.getAttribute("staff");%>
<% if (headAcc != null) { %>
<header class="staff-header">
    <div class="staff-header-info">
        <p class="staff-header-logo">La Mer</p>
        <p class="staff-header-role"><%= headAcc.getRole()%></p>
    </div>
    <div class="staff-header-user">
        <a href="userProfile.jsp" class="staff-name"><%= headAcc.getFullName()%></a>
        <form action="logout" method="POST">
            <button type="submit" class="logout-btn">Đăng xuất</button>
        </form>
    </div>
</header>

<%}%>