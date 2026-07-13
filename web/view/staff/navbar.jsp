<%-- 
    Document   : navbar for staff
    Created on : Jun 6, 2026, 10:45:57 AM
    Author     : LinhLTHE200306
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.StaffAccount" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/staff-navbar.css" type="text/css">

<nav class="staff-navbar">
    <%
        StaffAccount navAcc = (StaffAccount) session.getAttribute("staff");

        if (navAcc != null) {
            String role = navAcc.getRoleEn();
    %>

    <% if ("MANAGER".equals(role)) { %>

    <a href="${pageContext.request.contextPath}/ManagerDashboard">Tổng quan</a>
    <a href="${pageContext.request.contextPath}/HotelInfo">Thông tin chung</a>
    <a href="${pageContext.request.contextPath}/roomtypelist">Loại phòng</a>
    <a href="${pageContext.request.contextPath}/RoomList">Phòng</a>
    <a href="${pageContext.request.contextPath}/RoomServiceList">Dịch vụ phòng</a>
    <a href="${pageContext.request.contextPath}/RoomAmenityList">Tiện nghi phòng</a>
    <a href="${pageContext.request.contextPath}/HotelServiceList">Dịch vụ khách sạn</a>
    <a href="${pageContext.request.contextPath}/PolicyList">Chính sách</a>
<!--    <a href="${pageContext.request.contextPath}/feedback-list">Đánh giá</a>-->
    <%}else if("RECEPTIONIST".equals(role)){%>
    <a href="${pageContext.request.contextPath}/receptionist-dashboard">Tổng quan</a>
    <a href="${pageContext.request.contextPath}/assign-room">Sơ đồ phòng</a>
    <a href="${pageContext.request.contextPath}/checkin">Nhận phòng</a>
    <a href="${pageContext.request.contextPath}/Checkout">Trả phòng</a>
    <a href="${pageContext.request.contextPath}/booking-list">Danh sách đặt phòng</a>
    <a href="${pageContext.request.contextPath}/walk-in-booking">Đặt phòng tại quầy</a>
    <a href="${pageContext.request.contextPath}/process-request">Xử lý yêu cầu</a>
    <a href="${pageContext.request.contextPath}/DepositPaymentList">Danh sách đặt cọc</a>
    <a href="${pageContext.request.contextPath}/BillingList">Xem hóa đơn</a>

    <%}else if("ADMIN".equals(role)){%>

    <a href="${pageContext.request.contextPath}/StaffAccountList">Quản lý tài khoản nhân viên</a>
    <%}%>
    <%}%>

</nav>

<script src="${pageContext.request.contextPath}/view/assets/javascript/staff-navbar.js"></script>