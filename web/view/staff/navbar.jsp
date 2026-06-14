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
    <a href="${pageContext.request.contextPath}/HotelInfoList">Thông tin chungn</a>
    <a href="${pageContext.request.contextPath}/RoomTypeList">Loại phòng</a>

    <a href="${pageContext.request.contextPath}/RoomList">Phòng</a>
    <a href="${pageContext.request.contextPath}/RoomServiceList">Dịch vụ phòng</a>
    <a href="${pageContext.request.contextPath}/RoomAmenityList">Tiện nghi phòng</a>
    <a href="${pageContext.request.contextPath}/HotelServiceList">Dịch vụ khách sạn</a>
    <a href="${pageContext.request.contextPath}/PolicyList">Chính sách</a>
    <a href="${pageContext.request.contextPath}/FeedbackList">Đánh giá</a>
    <%}else if("RECEPTIONIST".equals(role)){%>

    <%}else if("ADMIN".equals(role)){%>
    <a href="StaffAccountList">Quản lý tài khoản nhân viên</a>
    <%}%>
    <%}%>

</nav>
    <script src="${pageContext.request.contextPath}/view/assets/javascript/staff-navbar.js"></script>
    


