<%-- 
    Document   : service
    Document   : serviceManagement
    Created on : May 27, 2026, 10:50:44 PM
    Author     : Minh Thu
    Editer     : LinhLTHE200306
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="model.StaffAccount" %>
<%@ include file="/view/staff/header.jsp" %>
<%@ include file="/view/staff/navbar.jsp" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý dịch vụ</title>
        <link rel="stylesheet" href="css/service-managerment.css">
    </head>
    <body>
        <main class="conent-container">
            <div class="header-action">
                <h1 class="header-title">Quản lý dịch vụ</h1>
                <button id="btn-create" class="btn-primary">Thêm dịch vụ mới</button>          
            </div>

            <div class="filter-bar">
                <p class="filter-title">Bộ lọc danh mục</p>
                <a href="serviceList?filterType=ALL" class="filter-btn active">Tất cả</a>
                <a href="serviceList?filterType=HOTEL" class="filter-btn">Dịch vụ khách sạn</a>
                <a href="serviceList?filterType=ROOM" class="filter-btn">Dịch vụ phòng</a>
            </div>

            <div>
                <table class="data-table">
                    <thead class="data-table-thead">
                        <tr>
                            <th>Tên dịch vụ</th>
                            <th>Phân loại</th>
                            <th>Mô tả</th>
                            <th>Đơn giá</th>
                            <th>Trạng thái</th>
                            <th>Hành động</th>
                        </tr>
                    </thead>
                    <tbody class="data-table-tbody">
                        <c:forEach var="srv" items="${serviceList}">
                            <tr>
                                <td>${srv.getServiceName()}</td>
                                <td>${srv.getType()}</td>
                                <td>${srv.getDescription()}</td>
                                <td>${srv.getUnitPrice()}</td>
                                <td>${srv.isActive() ? 'ACTIVE':'INACTIVE'}</td>
                                <td>
                                    <a href="serviceEdit?serviceId=${srv.getServiceId()}">Sửa</a>
                                    <a href="serviceDelete?serviceId=${srv.getServiceId()}">Xoá</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </main>

        <div class="service-popup" id="service-modal">
            <h2 class="service-popup-title" id="modal-title">Thêm dịch vụ mới</h2>
            <form action="" method="POST" id="service-form">
                <input type="hidden" name="serviceId" id="serviceId">
                <input class="service-popup-input-field" type="text" name="serviceName" id="serviceName" placeholder="Tên dịch vụ" required>
                <select class="service-popup-input-field" name="type" id="type">
                    <option value="HOTEL">Khách sạn</option>
                    <option value="ROOM">Phòng</option>
                </select>

                <textarea class="service-popup-input-field" name="description" id="description" placeholder="Mô tả"></textarea>
                <input class="service-popup-input-field" type="number" name="unitPrice" id="unitPrice" placeholder="Đơn giá" required>

                <select class="service-popup-input-field" name="active" id="active">
                    <button class="btn-submit" ></button>
                </select>

                <div class="service-popup-action">
                    <button class="btn-submit" type="submit">Xác nhận lưu</button>
                    <button class="btn-close" type="button" id="btn-close">Huỷ</button>
                </div>
            </form>
        </div>
    </body>
</html>
