<%-- 
    Document   : room-management
    Created on : May 27, 2026, 10:50:23 PM
    Author     : Minh Thu
    Editor     : LinhLTHE200306
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.StaffAccount"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/room-management.css" type="text/css">

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Quản lý phòng</title>
    </head>

    <body data-edit-mode="${not empty editRoom}" data-detail-mode="${not empty selectedRoom}">
        <%@ include file="/view/staff/header.jsp" %>
        <%@ include file="/view/staff/navbar.jsp" %>

        <main class="content-container">
            <div class="header-action">
                <h1 class="header-title">
                    Quản lý phòng
                </h1>
            </div>

            <form action="RoomList"  method="GET" class="filter-bar">
                <select name="floor" class="filter-input">
                    <option value="">Tất cả tầng</option>
                    <c:forEach var="f" items="${floorList}">
                        <option value="${f}"${selectedFloor == f ? 'selected' : ''}>Tầng ${f}</option>
                    </c:forEach>
                </select>

                <select name="roomTypeId" class="filter-input">
                    <option value="">Tất cả hạng phòng</option>
                    <c:forEach var="rt" items="${roomTypeList}">
                        <option value="${rt.getRoomTypeId()}"${selectedRoomTypeId == rt.getRoomTypeId() ? 'selected' : ''}>
                            ${rt.getTypeName()}
                        </option>
                    </c:forEach>
                </select>

                <input type="text" name="keyword" class="filter-input" placeholder="Tìm số phòng..." value="${keyword}">
                <button type="submit" class="btn-filter">Tìm kiếm</button>
            </form>

            <div class="room-map">
                <c:forEach begin="1" end="5" var="floor">
                    <div class="floor-section">
                        <h2 class="floor-title">
                            Tầng ${floor}
                        </h2>
                        <div class="room-grid">
                            <c:forEach var="room" items="${roomList}">
                                <c:if test="${room.getFloor() == floor}">
                                    <div class="room-card">
                                        <div class="room-number">
                                            Phòng ${room.getRoomNumber()}
                                        </div>
                                        <div class="room-type">
                                            ${roomTypeMap[room.getRoomTypeId()]}
                                        </div>
                                        <div class="room-status">
                                            <c:choose>
                                                <c:when test="${room.getStatus() == 'Phòng trống'}">
                                                    <span class="status-empty">
                                                        ${room.getStatus()}
                                                    </span>
                                                </c:when>

                                                <c:when test="${room.getStatus() == 'Phòng có khách'}">
                                                    <span class="status-occupied">
                                                        ${room.getStatus()}
                                                    </span>
                                                </c:when>

                                                <c:when test="${room.getStatus() == 'Đang dọn dẹp'}">
                                                    <span class="status-cleaning">
                                                        ${room.getStatus()}
                                                    </span>
                                                </c:when>

                                                <c:otherwise>
                                                    <span class="status-maintenance">
                                                        ${room.getStatus()}
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>

                                        <div class="room-action">
                                            <a class="btn-detail" href="RoomDetail?roomNumber=${room.getRoomNumber()}">Chi tiết</a>
                                            <a class="btn-edit" href="RoomEdit?roomNumber=${room.getRoomNumber()}">Sửa</a>    
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </main>

        <div class="room-modal" id="detail-modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h2>Chi tiết phòng ${selectedRoom.getRoomNumber()}</h2>
                </div>
                <div class="modal-body">
                    <c:if test="${selectedRoom != null}">
                        <div class="detail-row"><strong>Số phòng:</strong> ${selectedRoom.getRoomNumber()}</div>
                        <div class="detail-row"><strong>Tầng:</strong> ${selectedRoom.getFloor()}</div>
                        <div class="detail-row"><strong>Hạng phòng:</strong> ${roomTypeMap[selectedRoom.getRoomTypeId()]}</div>
                        <div class="detail-row"><strong>Trạng thái:</strong> ${selectedRoom.getStatus()}</div>

                        <c:if test="${selectedRoom.getStatus() == 'Phòng có khách'}">
                            <hr>
                            <h3>Danh sách khách đang lưu trú</h3>
                            <table class="guest-table">
                                <thead>
                                    <tr><th>Họ tên</th><th>Số điện thoại</th><th>CCCD / Passport</th></tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="guest" items="${guestList}">
                                        <tr><td>${guest.getFullName()}</td><td>${guest.getPhone()}</td><td>${guest.getIdNumber()}</td></tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:if>
                    </c:if>
                </div>
                <div class="service-popup-action">
                    <button type="button" class="btn-close" id="btn-close-detail">Đóng</button>
                    <a class="btn-submit" href="RoomEdit?roomNumber=${selectedRoom.getRoomNumber()}" id="btn-edit-from-detail">Sửa</a>
                </div>
            </div>
        </div>

        <div class="room-modal" id="edit-modal">
            <form action="RoomEdit" method="post" id="edit-form" class="modal-content">
                <div class="modal-header">
                    <h2>Chỉnh sửa phòng ${editRoom.getRoomNumber()}</h2>
                </div>
                <div class="modal-body">
                    <c:if test="${editRoom != null}">
                        <input type="hidden" name="roomNumber" value="${editRoom.getRoomNumber()}">

                        <div class="form-group">
                            <label class="input-label">Số phòng</label>
                            <input class="service-popup-input-field" type="text" value="${editRoom.getRoomNumber()}" readonly>
                        </div>

                        <div class="form-group">
                            <label class="input-label">Tầng</label>
                            <input class="service-popup-input-field" type="text" value="${editRoom.getFloor()}" readonly>
                        </div>

                        <div class="form-group">
                            <label class="input-label">Trạng thái</label>
                            <select class="service-popup-input-field" name="status">
                                <option value="Phòng trống" ${editRoom.getStatus() == 'Phòng trống' ? 'selected' : ''}>Phòng trống</option>
                                <option value="Phòng có khách" ${editRoom.getStatus() == 'Phòng có khách' ? 'selected' : ''}>Phòng có khách</option>
                                <option value="Đang dọn dẹp" ${editRoom.getStatus() == 'Đang dọn dẹp' ? 'selected' : ''}>Đang dọn dẹp</option>
                                <option value="Đang bảo trì" ${editRoom.getStatus() == 'Đang bảo trì' ? 'selected' : ''}>Đang bảo trì</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label class="input-label">Hạng phòng</label>
                            <select class="service-popup-input-field" name="roomTypeId">
                                <c:forEach var="rt" items="${roomTypeList}">
                                    <option value="${rt.getRoomTypeId()}" ${editRoom.getRoomTypeId() == rt.getRoomTypeId() ? 'selected' : ''}>${rt.getTypeName()}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </c:if>
                </div>
                <div class="service-popup-action">
                    <button type="button" class="btn-close" id="btn-close-edit">Huỷ</button>
                    <button type="submit" class="btn-submit">Lưu thay đổi</button>
                </div>
            </form>
        </div>

        <script src="${pageContext.request.contextPath}/view/assets/javascript/room-management.js"></script>
    </body>
</html>
