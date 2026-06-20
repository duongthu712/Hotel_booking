<%-- 
    Document   : room-service-management
    Created on : Jun 9, 2026, 10:15:17 PM
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

    <body data-edit-mode="${not empty amenityToEdit}"
          data-create-mode="${not empty openCreateModal ? 'true' : 'false'}">
        <%@ include file="/view/staff/header.jsp" %>
        <%@ include file="/view/staff/navbar.jsp" %>
        <main class="conent-container">

            <div class="search-container">
                <form action="RoomAmenityList" method="GET" class="search-form">
                    <input type="text" name="keyword" class="search-input" placeholder="Tìm kiếm theo tên tiện nghi..." value="${keyword}">
                    <button type="submit" class="search-btn">Tìm kiếm</button>
                    <a href="RoomAmenityList" class="reset-btn">Làm mới</a>
                </form>
                <div class="header-action">
                    <button id="btn-create" class="btn-primary">Thêm tiện nghi phòng mới</button>          
                </div>
            </div>

            <c:if test="${not empty errorMessage and (empty openCreateModal or openCreateModal eq 'false') and empty amenityToEdit}">
                <div class="alert-message alert-error">
                    ${errorMessage}
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
                            <th class="col-id">STT</th>
                            <th class="col-name">Tên tiện nghi phòng</th>
                            <th class="col-desc">Mô tả</th>
                            <th class="col-price">Đơn giá (VNĐ)</th>
                            <th class="col-status">Trạng thái</th>
                            <th class="col-action">Hành động</th>
                        </tr>
                    </thead>

                    <tbody class="data-table-tbody">
                        <c:forEach var="srv" items="${amenityList}" varStatus="loop">
                            <tr>
                                <td class="col-id">${(currentPage - 1) * 10 + loop.index + 1}</td>
                                <td class="col-name">${srv.getAmenityName()}</td>
                                <td class="col-desc">${srv.getDescription()}</td>
                                <td class="col-price"><fmt:formatNumber value="${srv.getUnitPrice()}" type="number" pattern="#,###" />đ</td>
                                <td class="col-status">
                                    <div class="srvAct ${srv.isActive() ? 'status-active' : 'status-inactive'}">
                                        ${srv.isActive() ? 'HOẠT ĐỘNG' : 'TẠM DỪNG'}
                                    </div>
                                </td>
                                <td class="btn-action">
                                    <a class="btn-edit" href="RoomAmenityEdit?amenityId=${srv.getAmenityId()}&page=${currentPage}&keyword=${keyword}">Sửa</a>
                                    <form action="RoomAmenityDelete" method="post" style="display: inline-block; margin: 0;">
                                        <input type="hidden" name="amenityId" value="${srv.getAmenityId()}">
                                        <input type="hidden" name="page" value="${currentPage}">
                                        <input type="hidden" name="keyword" value="${keyword}">
                                        <button type="submit" onclick="return confirm('Bạn có chắc muốn xoá tiện nghi ${srv.getAmenityName()}?')">Xoá</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                    <c:if test="${empty amenityList}">
                        <tr>
                            <td colspan="7" class="empty-message">
                                Không tìm thấy tiện nghi.
                            </td>
                        </tr>
                    </c:if>
                </table>

                <div class="pagination">
                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <a href="RoomAmenityList?page=${i}&keyword=${keyword}" class="${currentPage == i ? 'active' : ''}">${i}</a>
                    </c:forEach>
                </div>
            </div>
        </main>

        <div class="service-popup ${not empty openCreateModal or not empty amenityToEdit ? 'show' : ''}" 
             id="service-modal" 
             ${not empty openCreateModal or not empty amenityToEdit ? 'style="display: flex;"' : ''}>

            <form action="${not empty amenityToEdit ? 'RoomAmenityEdit' : 'RoomAmenityCreate'}" method="POST" id="service-form" class="popup-content">

                <h2 class="service-popup-title" id="modal-title">
                    ${not empty amenityToEdit ? 'Chỉnh sửa tiện nghi phòng' : 'Thêm tiện nghi phòng mới'}
                </h2>

                <input type="hidden" name="amenityId" id="amenityId" value="${amenityToEdit.getAmenityId()}">
                <input type="hidden" name="page" value="${currentPage}">
                <input type="hidden" name="keyword" value="${keyword}">

                <c:if test="${not empty errorMessage and (openCreateModal eq 'true' or not empty amenityToEdit)}">
                    <div class="alert-message alert-error">
                        ${errorMessage}
                    </div>
                    <c:remove var="errorMessage" scope="session"/>
                </c:if>

                <div class="form-group">
                    <label class="input-label">Tên tiện nghi*</label>
                    <input class="service-popup-input-field" type="text" name="serviceName" id="serviceName" 
                           placeholder="Nhập tên..." 
                           value="${not empty amenityToEdit ? amenityToEdit.getAmenityName() : keepServiceName}" required>
                </div>

                <div class="form-group">
                    <label class="input-label">Đơn giá (VNĐ)*</label>
                    <input class="service-popup-input-field" type="number" step="0.01" name="unitPrice" id="unitPrice" 
                           placeholder="0.00" 
                           value="${not empty amenityToEdit ? amenityToEdit.getUnitPrice() : keepUnitPrice}" required>
                </div>

                <div class="form-group">
                    <label class="input-label">Mô tả ngắn</label>
                    <textarea class="service-popup-input-field" name="description" id="description" 
                              placeholder="Mô tả công năng...">${not empty amenityToEdit ? amenityToEdit.getDescription() : keepDescription}</textarea>
                </div>

                <div class="form-group toggle-row">
                    <label class="input-label">Trạng thái vận hành</label>
                    <label class="toggle-switch">
                        <input type="checkbox" name="active" id="active" value="true" 
                               ${(not empty amenityToEdit and amenityToEdit.active) or (empty amenityToEdit and keepActive ne 'false') ? 'checked' : ''}>
                        <span class="toggle-slider"></span>
                    </label>
                </div>

                <div class="service-popup-action">
                    <button class="btn-close" type="button" id="btn-close">Huỷ</button>
                    <button class="btn-submit" type="submit">Xác nhận lưu</button>
                </div>
            </form>
        </div>
        <c:remove var="amenityToEdit" scope="session"/>
        <c:remove var="openEditModal" scope="session"/>
        <c:remove var="openCreateModal" scope="session"/>
        <c:remove var="keepServiceName" scope="session"/>
        <c:remove var="keepDescription" scope="session"/>
        <c:remove var="keepUnitPrice" scope="session"/>
        <c:remove var="keepImageUrl" scope="session"/>
        <c:remove var="keepActive" scope="session"/>

        <script src="<%=request.getContextPath()%>/view/assets/javascript/room-service-management.js"></script>
    </body>
</html>