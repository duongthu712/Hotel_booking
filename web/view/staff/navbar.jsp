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

    <a href="ManagerDashboard">Tổng quan</a>
    <a href="HotelInfoList">Thông tin khách sạn</a>
    <a href="RoomTypeList">Quản lý loại phòng</a>
    <a href="RoomList">Quản lý phòng</a>
    <a href="FeedbackList">Đánh giá</a>
    <a href="ServiceList">Quản lý dịch vụ</a>
    <a href="PolicyList">Quản lý chính sách</a>
    <%}else if("RECEPTIONIST".equals(role)){%>

    <%}else if("ADMIN".equals(role)){%>
    <a href="StaffAccountList">Quản lý tài khoản nhân viên</a>
    <a href="StaffAccountCreate">Tạo tài khoản nhân viên</a>
    <%}%>
    <%}%>

</nav>


