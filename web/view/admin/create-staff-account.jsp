<%-- 
    Document   : create-staff-account
    Created on : May 27, 2026, 10:54:17 PM
    Author     : Minh Thu
    Editor     : LinhLTHE200306
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.StaffAccount"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/common.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/staff-management.css" type="text/css">

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Thêm nhân viên mới</title>
    </head>
    <body>
        <%@ include file="/view/staff/header.jsp" %>
        <%@ include file="/view/staff/navbar.jsp" %>

        <main class="content-container">
             <c:if test="${not empty sessionScope.errorMessage}">
                <div class="alert-message alert-error">
                    ${sessionScope.errorMessage}
                </div>
                <c:remove var="errorMessage" scope="session"/>
            </c:if>

            <c:if test="${not empty sessionScope.successMessage}">
                <div class="alert-message alert-success">
                    ${sessionScope.successMessage}
                </div>
                <c:remove var="successMessage" scope="session"/>
            </c:if>

            <form action="StaffAccountCreate" method="post" class="create-form-grid">
                <div class="form-group">
                    <label class="input-label">Tên đăng nhập*</label>
                    <input class="service-popup-input-field" type="text" name="username" value="${username}" placeholder="Nhập tên đăng nhập..." required>
                </div>

                <div class="form-group">
                    <label class="input-label">Mật khẩu*</label>
                    <input class="service-popup-input-field" type="password" name="password" placeholder="Nhập mật khẩu..." required>
                </div>

                <div class="form-group">
                    <label class="input-label">Họ tên*</label>
                    <input class="service-popup-input-field" type="text" name="fullName" value="${fullName}" placeholder="Nhập họ tên..." required>
                </div>

                <div class="form-group">
                    <label class="input-label">Email*</label>
                    <input class="service-popup-input-field" type="email" name="email" value="${email}" placeholder="Nhập email..." required>
                </div>

                <div class="form-group">
                    <label class="input-label">Số điện thoại</label>
                    <input class="service-popup-input-field" type="text" name="phone" value="${phone}" placeholder="Nhập số điện thoại...">
                </div>

                <div class="form-group">
                    <label class="input-label">Chức vụ*</label>
                    <select class="service-popup-input-field" name="role" required>
                        <option value="">Chọn chức vụ</option>
                        <option value="Lễ tân" ${role == 'Lễ tân' ? 'selected' : ''}>Lễ tân</option>
                        <option value="Quản lý" ${role == 'Quản lý' ? 'selected' : ''}>Quản lý</option>
                        <option value="Quản trị viên" ${role == 'Quản trị viên' ? 'selected' : ''}>Quản trị viên</option>
                    </select>
                </div>

                <div class="service-popup-action">
                    <button type="submit" class="btn-submit">Tạo nhân viên</button>
                </div>
            </form>
        </main>
    </body>
</html>