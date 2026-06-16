<%-- 
    Document   : room-management
    Created on : May 27, 2026, 10:50:23 PM
    Author     : Minh Thu
    Editor     : LinhLTHE200306
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.StaffAccount"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/common.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/room-management.css" type="text/css">
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Quản lý phòng</title>
    </head>

    <body data-detail-mode="${not empty selectedRoom}" data-edit-mode="${not empty editRoom}">
        <%@ include file="/view/staff/header.jsp" %>
        <%@ include file="/view/staff/navbar.jsp" %>

        <main class="content-container">

            <div class="search-container">
                <form action="RoomList" method="GET" class="search-form">
                    <select name="roomTypeId" class="search-input">
                        <option value="">Tất cả hạng phòng</option>
                        <c:forEach var="rt" items="${roomTypeList}">
                            <option value="${rt.getRoomTypeId()}"${selectedRoomTypeId == rt.getRoomTypeId() ? 'selected' : ''}>
                                ${rt.getTypeName()}
                            </option>
                        </c:forEach>
                    </select>
                    <input type="text" name="keyword" class="search-input" placeholder="Tìm số phòng..." value="${keyword}">
                    <button type="submit" class="search-btn">Tìm kiếm</button>
                    <a href="RoomList" class="reset-btn">Làm mới</a>
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

            <div class="room-map">
                <c:forEach begin="1" end="5" var="floor">
                    <c:set var="hasRoom" value="false"/>
                    <c:forEach var="room" items="${roomList}">
                        <c:if test="${room.getFloor() == floor}">
                            <c:set var="hasRoom" value="true"/>
                        </c:if>
                    </c:forEach>

                    <c:if test="${hasRoom}">
                        <div class="floor-section">
                            <h2 class="floor-title">Tầng ${floor}</h2>
                            <div class="room-grid">
                                <c:forEach var="room" items="${roomList}">
                                    <c:if test="${room.getFloor() == floor}">
                                        <div class="room-card">
                                            <div class="room-number">Phòng ${room.getRoomNumber()}</div>
                                            <div class="room-type">${roomTypeMap[room.getRoomTypeId()]}</div>
                                            <div class="room-status">
                                                <c:choose>
                                                    <c:when test="${room.getStatus() == 'Phòng trống'}">
                                                        <span class="status-empty">${room.getStatus()}</span>
                                                    </c:when>
                                                    <c:when test="${room.getStatus() == 'Phòng có khách'}">
                                                        <span class="status-occupied">${room.getStatus()}</span>
                                                    </c:when>
                                                    <c:when test="${room.getStatus() == 'Đang dọn dẹp'}">
                                                        <span class="status-cleaning">${room.getStatus()}</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="status-maintenance">${room.getStatus()}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <div class="room-action">
                                                <a class="btn-edit" href="RoomDetail?roomNumber=${room.getRoomNumber()}&page=${currentPage}&roomTypeId=${selectedRoomTypeId}&keyword=${keyword}">Chi tiết</a>
                                                <a class="btn-edit" href="RoomEdit?roomNumber=${room.getRoomNumber()}&page=${currentPage}&roomTypeId=${selectedRoomTypeId}&keyword=${keyword}">Sửa</a>
                                            </div>
                                        </div>
                                    </c:if>
                                </c:forEach>
                            </div>
                        </div>
                    </c:if>
                </c:forEach>
            </div>

            <div class="pagination">
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <a href="RoomList?page=${i}&roomTypeId=${selectedRoomTypeId}&keyword=${keyword}" class="${currentPage == i ? 'active' : ''}">${i}</a>
                </c:forEach>
            </div>
        </main>

        <div class="room-modal" id="detail-modal">
            <div class="popup-content">
                <h2 class="service-popup-title">Chi tiết phòng ${selectedRoom.getRoomNumber()}</h2>

                <c:if test="${selectedRoom != null}">
                    <div class="form-group">
                        <label class="input-label">Số phòng</label>
                        <input class="service-popup-input-field" type="text" value="${selectedRoom.getRoomNumber()}" readonly>
                    </div>

                    <div class="form-group">
                        <label class="input-label">Tầng</label>
                        <input class="service-popup-input-field" type="text" value="${selectedRoom.getFloor()}" readonly>
                    </div>

                    <div class="form-group">
                        <label class="input-label">Hạng phòng</label>
                        <input class="service-popup-input-field" type="text" value="${roomTypeMap[selectedRoom.getRoomTypeId()]}" readonly>
                    </div>

                    <div class="form-group">
                        <label class="input-label">Trạng thái</label>
                        <input class="service-popup-input-field" type="text" value="${selectedRoom.getStatus()}" readonly>
                    </div>

                    <c:if test="${selectedRoom.getStatus() == 'Phòng có khách'}">
                        <hr>
                        <h3 class="guest-popup-list">Danh sách khách đang lưu trú</h3>
                        <table class="guest-table data-table">
                            <thead class="data-table-thead">
                                <tr>
                                    <th>Họ tên</th>
                                    <th>Số điện thoại</th>
                                    <th>CCCD / Passport</th>
                                </tr>
                            </thead>
                            <tbody class="data-table-tbody">
                                <c:forEach var="guest" items="${guestList}">
                                    <tr>
                                        <td>${guest.getFullName()}</td>
                                        <td>${guest.getPhone()}</td>
                                        <td>${guest.getIdNumber()}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:if>
                </c:if>
                        

                <div class="service-popup-action">
                    <button type="button" class="btn-close" id="btn-close-detail">Đóng</button>
                    <a class="btn-submit" href="RoomEdit?roomNumber=${selectedRoom.getRoomNumber()}&page=${currentPage}&roomTypeId=${selectedRoomTypeId}&keyword=${keyword}">Sửa</a>
                </div>
            </div>
        </div>

        <div class="room-modal" id="edit-modal">
            <form action="" method="POST" id="edit-form" class="popup-content">
                <h2 class="service-popup-title" id="edit-modal-title">Chỉnh sửa phòng</h2>

                <input type="hidden" name="page" value="${currentPage}">
                <input type="hidden" name="filterRoomTypeId" value="${selectedRoomTypeId}">
                <input type="hidden" name="keyword" value="${keyword}">

                <input type="hidden" name="roomNumber" id="editRoomNumber" value="${editRoom.getRoomNumber()}">

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
                    <select class="service-popup-input-field" name="status" id="editStatus">
                        <c:choose>
                            <c:when test="${editRoom.getStatus() == 'Phòng trống'}">
                                <option value="Phòng trống" selected>Phòng trống</option>
                                <option value="Đang dọn dẹp">Đang dọn dẹp</option>
                                <option value="Đang bảo trì">Đang bảo trì</option>
                            </c:when>
                            <c:when test="${editRoom.getStatus() == 'Đang dọn dẹp'}">
                                <option value="Phòng trống">Phòng trống</option>
                                <option value="Đang dọn dẹp" selected>Đang dọn dẹp</option>
                                <option value="Đang bảo trì">Đang bảo trì</option>
                            </c:when>
                            <c:when test="${editRoom.getStatus() == 'Đang bảo trì'}">
                                <option value="Phòng trống">Phòng trống</option>
                                <option value="Đang dọn dẹp">Đang dọn dẹp</option>
                                <option value="Đang bảo trì" selected>Đang bảo trì</option>
                            </c:when>
                            <c:otherwise>
                                <option value="Phòng có khách" selected>Phòng có khách</option>
                            </c:otherwise>
                        </c:choose>
                    </select>
                </div>

                <div class="form-group">
                    <label class="input-label">Hạng phòng</label>
                    <select class="service-popup-input-field" name="roomTypeId" id="editRoomTypeId">
                        <c:forEach var="rt" items="${roomTypeList}">
                            <option value="${rt.getRoomTypeId()}" ${editRoom.getRoomTypeId() == rt.getRoomTypeId() ? 'selected' : ''}>${rt.getTypeName()}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="service-popup-action">
                    <button type="button" class="btn-close" id="btn-close-edit">Huỷ</button>
                    <button type="submit" class="btn-submit">Xác nhận lưu</button>
                </div>
            </form>
        </div>

        <script src="${pageContext.request.contextPath}/view/assets/javascript/room-management.js"></script>
    </body>
</html>