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
        <title>Quản lý dịch vụ phòng</title>
    </head>
    <body data-edit-mode="${not empty serviceToEdit}">
        <%@ include file="/view/staff/header.jsp" %>
        <%@ include file="/view/staff/navbar.jsp" %>
        <main class="conent-container">
            
                          
            

            <div class="search-container">
                <form action="RoomServiceList" method="GET" class="search-form">
                    <input type="text" name="keyword" class="search-input" placeholder="Tìm kiếm theo tên dịch vụ..." value="${keyword}">
                    <button type="submit" class="search-btn">Tìm kiếm</button>
                    <a href="RoomServiceList" class="reset-btn">Làm mới</a>
                </form>
                    <div class="header-action">
                    <button id="btn-create" class="btn-primary">Thêm dịch vụ phòng mới</button>
                    </div>
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
                            <th class="col-name">Tên dịch vụ phòng</th>
                            <th class="col-desc">Mô tả</th>
                            <th class="col-price">Đơn giá (VNĐ)</th>
                            <th class="col-status">Trạng thái</th>
                            <th class="col-action">Hành động</th>
                        </tr>
                    </thead>

                    <tbody class="data-table-tbody">
                        <c:forEach var="srv" items="${serviceList}">
                            <tr>
                                <td class="col-name">${srv.getServiceName()}</td>
                                <td class="col-desc">${srv.getDescription()}</td>
                                <td class="col-price"><fmt:formatNumber value="${srv.getUnitPrice()}" type="number" pattern="#,###" />đ</td>
                                <td class="col-status">
                                    <div class="srvAct ${srv.isActive() ? 'status-active' : 'status-inactive'}">
                                        ${srv.isActive() ? 'ACTIVE' : 'INACTIVE'}
                                    </div>
                                </td>
                                <td class="btn-action">
                                    <a class="btn-edit" 
                                       href="RoomServiceEdit?serviceId=${srv.getServiceId()}&page=${currentPage}&keyword=${keyword}">
                                        Sửa</a>
                                    
                                    <form action="RoomServiceDelete" method="post">
                                        <input type="hidden" name="serviceId" value="${srv.getServiceId()}">
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
                        <a href="RoomServiceList?page=${i}" class="${currentPage == i ? 'active' : ''}">${i}</a>
                    </c:forEach>
                </div>
            </div>
        </main>

        <div class="service-popup" id="service-modal">
            <form action="" method="POST" id="service-form" class="popup-content">
                <h2 class="service-popup-title" id="modal-title">Thêm dịch vụ phòng mới</h2>

                <input type="hidden" name="serviceId" id="serviceId" value="${serviceToEdit.getServiceId()}">
                <input type="hidden" name="page" value="${page}">
                <input type="hidden" name="keyword" value="${keyword}">

                <div class="form-group">
                    <label class="input-label">Tên dịch vụ phòng</label>
                    <input class="service-popup-input-field" type="text" name="serviceName" id="serviceName" 
                           placeholder="Nhập tên..." value="${serviceToEdit.getServiceName()}" required>
                </div>

                <div class="form-group">
                    <label class="input-label">Đơn giá (VNĐ)</label>
                    <input class="service-popup-input-field" type="number" name="unitPrice" id="unitPrice" 
                           placeholder="0.00" value="${serviceToEdit.getUnitPrice()}" required>
                </div>

                <div class="form-group">
                    <label class="input-label">Mô tả ngắn</label>
                    <textarea class="service-popup-input-field" name="description" id="description" 
                              placeholder="Mô tả công năng...">${serviceToEdit.getDescription()}</textarea>
                </div>

                <div class="form-group toggle-row">
                    <label class="input-label">Trạng thái vận hành</label>
                    <label class="toggle-switch">
                        <input type="checkbox" name="active" id="active" value="true" ${serviceToEdit.active ? 'checked' : ''}>
                        <span class="toggle-slider"></span>
                    </label>
                </div>

                <div class="service-popup-action">
                    <button class="btn-close" type="button" id="btn-close">Huỷ</button>
                    <button class="btn-submit" type="submit">Xác nhận lưu</button>
                </div>
            </form>
        </div>
        <script src="<%=request.getContextPath()%>/view/assets/javascript/room-service-management.js"></script></body>
</html>

