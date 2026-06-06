<%-- 
    Document   : navbar for staff
    Created on : Jun 6, 2026, 10:45:57 AM
    Author     : LinhLTHE200306
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.StaffAccount" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/staff-navbar.css" type="text/css">

<nav class="staff-navbar">
    <% StaffAccount navAcc = (StaffAccount)session.getAttribute("staff");
    if (navAcc != null) {   
    String role = navAcc.getRoleEn();
    %>

    <% if ("MANAGER".equals(role)) { %>

    <a href="managerDashboard">Tổng quan</a>
    <a href="hotelInfor">Thông tin khách sạn</a>
    <a href="roomTypeManagement">Quản lý loại phòng</a>
    <a href="roomManagement">Quản lý phòng</a>
    <a href="feedback">Đánh giá</a>
    <a href="serviceManagement">Quản lý dịch vụ</a>
    <a href="policyManagement">Quản lý chính sách</a>
    <%}else if("RECEPTIONIST".equals(role)){%>

    <%}else if("ADMIN".equals(role)){%>
    <%}%>
    <%}%>
</nav>