<%-- 
    Document   : navbar
    Created on : May 27, 2026, 10:54:47 PM
    Author     : Minh Thu
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<nav class="navbar">
    <a href="${pageContext.request.contextPath}/" class="navbar-logo" style="text-decoration: none">La Mer</a>

    <ul class="navbar-menu">
        <li>
            <a href="${pageContext.request.contextPath}/search">PHÒNG NGHỈ</a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/view/user/booking-detail.jsp">ĐƠN ĐẶT CỦA TÔI</a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/view/public/feedback-list.jsp">ĐÁNH GIÁ</a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/policies">CHÍNH SÁCH</a>
        </li>
    </ul>

    <div class="navbar-actions">
        <a href="${pageContext.request.contextPath}/view/auth/login.jsp" class="staff-login">
            ĐĂNG NHẬP NHÂN VIÊN
        </a>
    </div>
</nav>