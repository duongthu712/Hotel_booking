<%-- 
    Document   : hotel-service-management
    Created on : Jun 10, 2026, 1:21:38 AM
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
        <title>Quản lý dịch vụ khách sạn</title>
    </head>

    <body data-edit-mode="${not empty serviceToEdit}"
          data-create-mode="${not empty openCreateModal ? 'true' : 'false'}">
        <%@ include file="/view/staff/header.jsp" %>
        <%@ include file="/view/staff/navbar.jsp" %>
        <main class="conent-container">

            <div class="search-container">
                <form action="HotelServiceList" method="GET" class="search-form">
                    <input type="text" name="keyword" class="search-input" placeholder="Tìm kiếm theo tên dịch vụ..." value="${keyword}">
                    <button type="submit" class="search-btn">Tìm kiếm</button>
                    <a href="HotelServiceList" class="reset-btn">Làm mới</a>
                </form>
                <div class="header-action">
                    <button id="btn-create" class="btn-primary">Thêm dịch vụ khách sạn mới</button>          
                </div>
            </div>

            <c:if test="${not empty errorMessage and (empty openCreateModal or openCreateModal eq 'false') and empty serviceToEdit}">
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
                            <th class="col-name">Tên dịch vụ khách sạn</th>
                            <th class="col-img">Hình ảnh</th>
                            <th class="col-desc">Mô tả</th>
                            <th class="col-price">Đơn giá (VNĐ)</th>
                            <th class="col-status">Trạng thái</th>
                            <th class="col-action">Hành động</th>
                        </tr>
                    </thead>

                    <tbody class="data-table-tbody">
                        <c:forEach var="srv" items="${serviceList}" varStatus="loop">
                            <tr>
                                <td class="col-id">${(currentPage - 1) * 10 + loop.index + 1}</td>
                                <td class="col-name">${srv.getServiceName()}</td>
                                <td class="col-img"><img src="${srv.getImageUrl()}" alt="" ></td>
                                <td class="col-desc">${srv.getDescription()}</td>
                                <td class="col-price"><fmt:formatNumber value="${srv.getUnitPrice()}" type="number" pattern="#,###" />đ</td>
                                <td class="col-status">
                                    <div class="srvAct ${srv.isActive() ? 'status-active' : 'status-inactive'}">
                                        ${srv.isActive() ? 'HOẠT ĐỘNG' : 'TẠM DỪNG'}
                                    </div>
                                </td>
                                <td class="btn-action">
                                    <a class="btn-edit" href="HotelServiceEdit?serviceId=${srv.getHotelServiceId()}&page=${currentPage}&keyword=${keyword}">Sửa</a>
                                    <form action="HotelServiceDelete" method="post" style="display: inline-block; margin: 0;">
                                        <input type="hidden" name="serviceId" value="${srv.getHotelServiceId()}">
                                        <input type="hidden" name="page" value="${currentPage}">
                                        <input type="hidden" name="keyword" value="${keyword}">
                                        <button type="submit" onclick="return confirm('Bạn có chắc muốn xoá dịch vụ ${srv.getServiceName()}?')">Xoá</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                    <c:if test="${empty serviceList}">
                        <tr>
                            <td colspan="7" class="empty-message">
                                Không tìm thấy dịch vụ.
                            </td>
                        </tr>
                    </c:if>
                </table>

                <div class="pagination">
                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <a href="HotelServiceList?page=${i}&keyword=${keyword}" class="${currentPage == i ? 'active' : ''}">${i}</a>
                    </c:forEach>
                </div>
            </div>
        </main>

        <div class="service-popup ${not empty openCreateModal or not empty serviceToEdit ? 'show' : ''}" 
             id="service-modal" 
             ${not empty openCreateModal or not empty serviceToEdit ? 'style="display: flex;"' : ''}>

            <form action="${not empty serviceToEdit ? 'HotelServiceEdit' : 'HotelServiceCreate'}" method="POST" id="service-form" class="popup-content">

                <div class="service-popup-action">
                    <button class="btn-close" type="button" id="btn-close" style="font-size: 35px; margin-top: -25px; right: 5px;">&times;</button>
                </div>
                <h2 class="service-popup-title" id="modal-title">
                    ${not empty serviceToEdit ? 'Chỉnh sửa dịch vụ khách sạn' : 'Thêm dịch vụ khách sạn mới'}
                </h2>

                <input type="hidden" name="serviceId" id="serviceId" value="${serviceToEdit.getHotelServiceId()}">
                <input type="hidden" name="page" value="${currentPage}">
                <input type="hidden" name="keyword" value="${keyword}">

                <c:if test="${not empty errorMessage and (openCreateModal eq 'true' or not empty serviceToEdit)}">
                    <div class="alert-message alert-error">
                        ${errorMessage}
                    </div>
                    <c:remove var="errorMessage" scope="session"/>
                </c:if>

                <div class="form-group">
                    <label class="input-label">Tên dịch vụ*</label>
                    <input class="service-popup-input-field" type="text" name="serviceName" id="serviceName" 
                           placeholder="Nhập tên..." 
                           value="${not empty serviceToEdit ? serviceToEdit.getServiceName() : keepServiceName}" required>
                </div>

                <div class="form-group">
                    <label class="input-label">Hình ảnh</label>
                    <input type="hidden" name="imageId" id="edit-image-id">
                    <div class="form-group">
                        <label class="input-label">Tải ảnh</label>
                        <input type="file" id="upImage">
                    </div>
                    <div class="form-group">
                        <label class="input-label">Link ảnh</label>
                        <input class="service-popup-input-field" type="text" name="imageUrl" id="imageUrl"
                           value="${not empty serviceToEdit ? serviceToEdit.getImageUrl() : keepImageUrl}">
                    </div>
                    
                </div>

                <div class="form-group">
                    <label class="input-label">Đơn giá (VNĐ)*</label>
                    <input class="service-popup-input-field" type="number" step="0.01" name="unitPrice" id="unitPrice" 
                           placeholder="0.00" 
                           value="${not empty serviceToEdit ? serviceToEdit.getUnitPrice() : keepUnitPrice}" required>
                </div>

                <div class="form-group">
                    <label class="input-label">Mô tả ngắn</label>
                    <textarea class="service-popup-input-field" name="description" id="description" 
                              placeholder="Mô tả công năng...">${not empty serviceToEdit ? serviceToEdit.getDescription() : keepDescription}</textarea>
                </div>

                <div class="form-group toggle-row">
                    <label class="input-label">Trạng thái vận hành</label>
                    <label class="toggle-switch">
                        <input type="checkbox" name="active" id="active" value="true" 
                               ${(not empty serviceToEdit and serviceToEdit.active) or (empty serviceToEdit and keepActive ne 'false') ? 'checked' : ''}>
                        <span class="toggle-slider"></span>
                    </label>
                </div>

                <div class="service-popup-action">
                    <button class="btn-submit" type="submit">Xác nhận lưu</button>
                </div>
            </form>
        </div>
        <c:remove var="serviceToEdit" scope="session"/>
        <c:remove var="openEditModal" scope="session"/>
        <c:remove var="openCreateModal" scope="session"/>
        <c:remove var="keepServiceName" scope="session"/>
        <c:remove var="keepDescription" scope="session"/>
        <c:remove var="keepUnitPrice" scope="session"/>
        <c:remove var="keepImageUrl" scope="session"/>
        <c:remove var="keepActive" scope="session"/>
        <script src="<%=request.getContextPath()%>/view/assets/javascript/alert.js"></script>
        <script src="<%=request.getContextPath()%>/view/assets/javascript/hotel-service-management.js"></script>
        <script src="<%=request.getContextPath()%>/view/assets/javascript/upload-img.js"></script>
    </body>
</html>