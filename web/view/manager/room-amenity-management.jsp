<%-- 
    Document   : room-amenity-management
    Created on : Jun 10, 2026, 2:17:49 AM
    Author     : LinhLTHE200306
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page import="model.StaffAccount" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/common.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/service-management.css" type="text/css">
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý tiện nghi phòng</title>
    </head>
    <body data-edit-mode="${not empty amenityToEdit}">
        <%@ include file="/view/staff/header.jsp" %>
        <%@ include file="/view/staff/navbar.jsp" %>
        <main class="conent-container">
            <div class="header-action">
                <h1 class="header-title">Quản lý tiện nghi phòng</h1>
                <button id="btn-create" class="btn-primary">Thêm tiện nghi phòng mới</button>          
            </div>

            <div class="search-container">
                <form action="RoomAmenityList" method="GET" class="search-form">
                    <input type="text" name="keyword" class="search-input" placeholder="Tìm kiếm theo tên tiện nghi..." value="${keyword}">
                    <button type="submit" class="search-btn">Tìm kiếm</button>
                    <a href="RoomAmenityList" class="reset-btn">Làm mới</a>
                </form>
            </div>

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



            <div>
                <table class="data-table">
                    <thead class="data-table-thead">
                        <tr>
                            <th class="col-name">Tên tiện nghi phòng</th>
                            <th class="col-desc">Mô tả</th>
                            <th class="col-price">Đơn giá (VNĐ)</th>
                            <th class="col-status">Trạng thái</th>
                            <th class="col-action">Hành động</th>
                        </tr>
                    </thead>

                    <tbody class="data-table-tbody">
                        <c:forEach var="srv" items="${amenityList}">
                            <tr>
                                <td class="col-name">${srv.getAmenityName()}</td>
                                <td class="col-desc">${srv.getDescription()}</td>
                                <td class="col-price"><fmt:formatNumber value="${srv.getUnitPrice()}" type="number" pattern="#,###" />đ</td>
                                <td class="col-status">
                                    <div class="srvAct ${srv.isActive() ? 'status-active' : 'status-inactive'}">
                                        ${srv.isActive() ? 'ACTIVE' : 'INACTIVE'}
                                    </div>
                                </td>
                                <td class="btn-action">
                                    <a class="btn-edit" href="RoomAmenityEdit?amenityId=${srv.getAmenityId()}&page=${currentPage}&keyword=${keyword}"">Sửa</a>
                                    <form action="RoomAmenityDelete" method="post">
                                        <input type="hidden" name="amenityId" value="${srv.getAmenityId()}">
                                        <input type="hidden" name="page" value="${currentPage}">
                                        <input type="hidden" name="keyword" value="${keyword}">
                                        <button type="submit">Xoá</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <div class="pagination">
                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <a href="RoomAmenityList?page=${i}" class="${currentPage == i ? 'active' : ''}">${i}</a>
                    </c:forEach>
                </div>
            </div>
        </main>

        <div class="amenity-popup" id="amenity-modal">
            <form action="" method="POST" id="amenity-form" class="popup-content">
                <h2 class="amenity-popup-title" id="modal-title">Thêm tiện nghi phòng mới</h2>

                <input type="hidden" name="amenityId" id="amenityId" value="${amenityToEdit.getAmenityId()}">
                <input type="hidden" name="page" value="${page}">
                <input type="hidden" name="keyword" value="${keyword}">

                <div class="form-group">
                    <label class="input-label">Tên tiện nghi phòng</label>
                    <input class="amenity-popup-input-field" type="text" name="amenityName" id="amenityName" 
                           placeholder="Nhập tên..." value="${amenityToEdit.getAmenityName()}" required>
                </div>

                <div class="form-group">
                    <label class="input-label">Đơn giá (VNĐ)</label>
                    <input class="amenity-popup-input-field" type="number" name="unitPrice" id="unitPrice" 
                           placeholder="0.00" value="${amenityToEdit.getUnitPrice()}" required>
                </div>

                <div class="form-group">
                    <label class="input-label">Mô tả ngắn</label>
                    <textarea class="amenity-popup-input-field" name="description" id="description" 
                              placeholder="Mô tả công năng...">${amenityToEdit.getDescription()}</textarea>
                </div>

                <div class="form-group toggle-row">
                    <label class="input-label">Trạng thái vận hành</label>
                    <label class="toggle-switch">
                        <input type="checkbox" name="active" id="active" value="true" ${amenityToEdit.active ? 'checked' : ''}>
                        <span class="toggle-slider"></span>
                    </label>
                </div>

                <div class="amenity-popup-action">
                    <button class="btn-close" type="button" id="btn-close">Huỷ</button>
                    <button class="btn-submit" type="submit">Xác nhận lưu</button>
                </div>
            </form>
        </div>
        <script src="<%=request.getContextPath()%>/view/assets/javascript/room-amenity-management.js"></script></body>
</html>

