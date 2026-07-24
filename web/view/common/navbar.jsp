<%-- 
    Author: ThuDNM-HE204370 
    Date created: 23/06/2026 
    Purpose: Navbar component.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="dao.HotelInfoDAO" %>
<%
    HotelInfoDAO dao = new HotelInfoDAO();
    String hotelName = dao.getHotelName();
    %>
<nav class="navbar">
    <a href="${pageContext.request.contextPath}/" class="navbar-logo" style="text-decoration: none"><%= hotelName %></a>

    <ul class="navbar-menu">
        <li>
            <a href="${pageContext.request.contextPath}/search">PHÒNG NGHỈ</a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/view/user/booking-detail.jsp">ĐƠN ĐẶT CỦA TÔI</a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/feedback-list">ĐÁNH GIÁ</a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/policies">CHÍNH SÁCH</a>
        </li>
    </ul>

    <div class="navbar-actions">
        <a href="${pageContext.request.contextPath}/view/auth/login.jsp" class="staff-login">
            ĐĂNG NHẬP NHÂN VIÊN
        </a>>
    </div>
</nav>