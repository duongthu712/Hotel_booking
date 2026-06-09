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

    <a href="${pageContext.request.contextPath}/dashboard.jsp">Tổng quan</a>
    <a href="${pageContext.request.contextPath}/HotelInfoList">Thông tin khách sạn</a>
    <a href="${pageContext.request.contextPath}/RoomTypeList">Quản lý loại phòng</a>
    <a href="${pageContext.request.contextPath}/RoomList">Quản lý phòng</a>
    <a href="${pageContext.request.contextPath}/RoomServiceList">Quản lý dịch vụ phòng</a>
    <a href="${pageContext.request.contextPath}/RoomAmenityList">Quản lý tiện nghi phòng</a>
    <a href="${pageContext.request.contextPath}/HotelServiceList">Quản lý dịch vụ khách sạn</a>
    <a href="${pageContext.request.contextPath}/PolicyList">Quản lý chính sách</a>
    <a href="${pageContext.request.contextPath}/FeedbackList">Đánh giá</a>
    <%}else if("RECEPTIONIST".equals(role)){%>

    <%}else if("ADMIN".equals(role)){%>
    <a href="StaffAccountList">Quản lý tài khoản nhân viên</a>
    <a href="StaffAccountCreate">Tạo tài khoản nhân viên</a>
    <%}%>
    <%}%>

</nav>


