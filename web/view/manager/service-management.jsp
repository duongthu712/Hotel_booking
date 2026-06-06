<%-- 
    Document   : service
    Document   : serviceManagement
    Created on : May 27, 2026, 10:50:44 PM
    Author     : Minh Thu
    Editer     : LinhLTHE200306
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page import="model.StaffAccount" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/service-management.css" type="text/css">

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý dịch vụ</title>
    </head>
    <body data-edit-mode="${not empty serviceToEdit}">
        <%@ include file="/view/staff/header.jsp" %>
        <%@ include file="/view/staff/navbar.jsp" %>
        <main class="conent-container">
            <div class="header-action">
                <h1 class="header-title">Quản lý dịch vụ</h1>
                <button id="btn-create" class="btn-primary">Thêm dịch vụ mới</button>          
            </div>

            <div class="filter-bar">
                <p class="filter-title">Bộ lọc danh mục:</p>

                <a href="ServiceList?filterType=ALL" 
                   class="filter-btn ${filterType == 'ALL' ? 'active' : ''}">
                    Tất cả</a>

                <a href="ServiceList?filterType=HOTEL" 
                   class="filter-btn ${filterType == 'HOTEL' ? 'active' : ''}">
                    Dịch vụ khách sạn</a>

                <a href="ServiceList?filterType=ROOM" 
                   class="filter-btn ${filterType == 'ROOM' ? 'active' : ''}">
                    Dịch vụ phòng</a>
            </div>

            <div>
                <table class="data-table">
                    <thead class="data-table-thead">
                        <tr>
                            <th class="col-name">Tên dịch vụ</th>
                            <th class="col-type">Phân loại</th>
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
                                <td class="col-type"><div class="srvType">${srv.getType()}</div></td>
                                <td class="col-desc">${srv.getDescription()}</td>
                                <td class="col-price"><fmt:formatNumber value="${srv.getUnitPrice()}" type="number" pattern="#,###" />đ</td>
                                <td class="col-status">
                                    <div class="srvAct ${srv.isActive() ? 'status-active' : 'status-inactive'}">
                                        ${srv.isActive() ? 'ACTIVE' : 'INACTIVE'}
                                    </div>
                                </td>
                                <td class="btn-action">
                                    <a class="btn-edit" href="ServiceEdit?serviceId=${srv.getServiceId()}&type=${srv.getType()}">Sửa</a>
                                    <form action="ServiceDelete" method="post">
                                        <input type="hidden" name="serviceId" 
                                               value="${srv.getServiceId()}">
                                        <input type="hidden" name="type" 
                                               value="${srv.getType()}">
                                        <button type="submit">Xoá</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <div class="pagination">
                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <a href="ServiceList?filterType=${filterType}&page=${i}" 
                           class="${currentPage == i ? 'active' : ''}">
                            ${i}
                        </a>
                    </c:forEach>
                </div>
            </div>
        </main>

        <div class="service-popup" id="service-modal">
            <form action="" method="POST" id="service-form" class="popup-content">
                <h2 class="service-popup-title" id="modal-title">Thêm dịch vụ mới</h2>

                <input type="hidden" name="serviceId" id="serviceId" value="${serviceToEdit.getServiceId()}">

                <div class="form-group">
                    <label class="input-label">Tên dịch vụ *</label>
                    <input class="service-popup-input-field" type="text" name="serviceName" id="serviceName" 
                           placeholder="Nhập tên..." value="${serviceToEdit.getServiceName()}" required>
                </div>

                <div class="form-group">
                    <label class="input-label">Phân loại danh mục (Service Type) *</label>
                    <select class="service-popup-input-field" name="type" id="type">
                        <option value="HOTEL" ${serviceToEdit.getType() == 'HOTEL' ? 'selected' : ''}>Hotel Service</option>
                        <option value="ROOM" ${serviceToEdit.getType() == 'ROOM' ? 'selected' : ''}>Room Service</option>
                    </select>
                </div>

                <div class="form-group">
                    <label class="input-label">Đơn giá (VNĐ) *</label>
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
        <script src="<%=request.getContextPath()%>/view/assets/javascript/service-management.js"></script></body>
</html>
