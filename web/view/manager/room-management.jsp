<%-- 
    Document   : room-management
    Created on : May 27, 2026, 10:50:23 PM
    Author     : Minh Thu
    Editor     : LinhLTHE200306
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.StaffAccount"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/common.css?v=3" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/room-management.css?v=3" type="text/css">
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Quản lý phòng</title>
    </head>

    <body data-detail-mode="${not empty selectedRoom}" 
          data-edit-mode="${not empty editRoom}"
          data-create-mode="${not empty openCreateModal ? 'true' : 'false'}">
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
                <div class="header-action">
                    <button id="btn-create" class="btn-primary">Thêm phòng mới</button>
                </div>
            </div>

            <c:if test="${not empty errorMessage && empty openCreateModal && empty editRoom}">
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

            <div class="room-map">
                <c:forEach var="floorEntry" items="${floorMap}">
                    <c:set var="floor" value="${floorEntry.key}"/>
                    <c:set var="roomsOnFloor" value="${floorEntry.value}"/>
                    
                    <div class="floor-section">
                        <h2 class="floor-title">Tầng ${floor}</h2>
                        <div class="room-grid">
                            <c:forEach var="room" items="${roomsOnFloor}">
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
                                        <a class="btn-edit" href="RoomDetail?roomNumber=${room.getRoomNumber()}&roomTypeId=${selectedRoomTypeId}&keyword=${keyword}">Chi tiết</a>
                                        <a class="btn-edit" href="RoomEdit?roomNumber=${room.getRoomNumber()}&roomTypeId=${selectedRoomTypeId}&keyword=${keyword}">Sửa</a>
                                        <form action="RoomDelete" method="post" style="display: inline-block; margin: 0;">
                                            <input type="hidden" name="roomNumber" value="${room.getRoomNumber()}">
                                            <input type="hidden" name="roomTypeId" value="${selectedRoomTypeId}">
                                            <input type="hidden" name="keyword" value="${keyword}">
                                            <button type="submit" class="btn-delete" onclick="return confirm('Bạn có chắc muốn xoá phòng ${room.getRoomNumber()}?')">Xoá</button>
                                        </form>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </c:forEach>
                
                <c:if test="${empty floorMap}">
                    <p class="empty-message">Không tìm thấy phòng.</p>
                </c:if>
            </div>

            <%-- KHÔNG CÒN PHÂN TRANG --%>

        </main>

        <%-- DETAIL MODAL --%>
        <div class="room-modal" id="detail-modal">
            <div class="popup-content" style="position: relative;">
                <div class="service-popup-action">
                    <button type="button" class="btn-close" id="btn-close-detail" style="font-size: 35px; margin-top: -25px; right: 5px;">&times;</button>
                </div>
                <h2 class="service-popup-title">Chi tiết phòng ${selectedRoom.getRoomNumber()}</h2>

                <c:if test="${selectedRoom != null}">
                    <div class="detail-row"><strong>Số phòng:</strong> ${selectedRoom.getRoomNumber()}</div>
                    <div class="detail-row"><strong>Tầng:</strong> ${selectedRoom.getFloor()}</div>
                    <div class="detail-row"><strong>Hạng phòng:</strong> ${roomTypeMap[selectedRoom.getRoomTypeId()]}</div>
                    <div class="detail-row"><strong>Trạng thái:</strong> ${selectedRoom.getStatus()}</div>

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
                    <a class="btn-submit" href="RoomEdit?roomNumber=${selectedRoom.getRoomNumber()}&roomTypeId=${selectedRoomTypeId}&keyword=${keyword}">Sửa</a>
                </div>
            </div>
        </div>

        <%-- EDIT MODAL --%>
        <div class="room-modal" id="edit-modal">
            <form action="RoomEdit" method="post" id="edit-form" class="popup-content" style="position: relative;">
                <div class="service-popup-action">
                    <button type="button" class="btn-close" id="btn-close-edit" style="font-size: 35px; margin-top: -25px; right: 5px;">&times;</button>
                </div>
                <h2 class="service-popup-title" id="edit-modal-title">Chỉnh sửa phòng</h2>

                <input type="hidden" name="filterRoomTypeId" value="${selectedRoomTypeId}">
                <input type="hidden" name="keyword" value="${keyword}">
                <input type="hidden" name="roomNumber" id="editRoomNumber" value="${editRoom.getRoomNumber()}">

                <c:if test="${not empty errorMessage && not empty editRoom}">
                    <div class="alert-message alert-error">
                        ${errorMessage}
                    </div>
                    <c:remove var="errorMessage" scope="session"/>
                </c:if>

                <div class="form-group">
                    <label class="input-label">Số phòng hiện tại</label>
                    <input class="service-popup-input-field" type="text" value="${editRoom.getRoomNumber()}" readonly>
                </div>

                <div class="form-group">
                    <label class="input-label">Số phòng mới (để trống nếu không đổi)</label>
                    <input class="service-popup-input-field" type="number" name="newRoomNumber" 
                           placeholder="Nhập số phòng mới...">
                </div>

                <div class="form-group">
                    <label class="input-label">Tầng</label>
                    <input class="service-popup-input-field" type="text" value="${editRoom.getFloor()}" readonly>
                </div>

                <div class="form-group">
                    <label class="input-label">Trạng thái</label>
                    <select class="service-popup-input-field" name="status" id="editStatus">
                        <c:choose>
                            <c:when test="${editRoom.getStatus() == 'Phòng có khách'}">
                                <option value="Phòng có khách" selected>Phòng có khách</option>
                            </c:when>
                            <c:otherwise>
                                <option value="Phòng trống" ${editRoom.getStatus() == 'Phòng trống' ? 'selected' : ''}>Phòng trống</option>
                                <option value="Đang dọn dẹp" ${editRoom.getStatus() == 'Đang dọn dẹp' ? 'selected' : ''}>Đang dọn dẹp</option>
                                <option value="Đang bảo trì" ${editRoom.getStatus() == 'Đang bảo trì' ? 'selected' : ''}>Đang bảo trì</option>
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
                    <button type="submit" class="btn-submit">Lưu thay đổi</button>
                </div>
            </form>
        </div>

        <%-- CREATE MODAL --%>
        <div class="room-modal ${not empty openCreateModal ? 'show' : ''}" id="create-modal" ${not empty openCreateModal ? 'style="display: flex;"' : ''}>
            <form action="RoomCreate" method="POST" id="room-form" class="popup-content" style="position: relative;">
                <div class="service-popup-action">
                    <button type="button" class="btn-close" id="btn-close-create" style="font-size: 35px; margin-top: -25px; right: 5px;">&times;</button>
                </div>
                <h2 class="service-popup-title" id="modal-title">Thêm phòng mới</h2>

                <input type="hidden" name="filterRoomTypeId" value="${selectedRoomTypeId}">
                <input type="hidden" name="keyword" value="${keyword}">

                <c:if test="${not empty errorMessage && not empty openCreateModal}">
                    <div class="alert-message alert-error">
                        ${errorMessage}
                    </div>
                    <c:remove var="errorMessage" scope="session"/>
                </c:if>

                <div class="form-group">
                    <label class="input-label">Số phòng*</label>
                    <input class="service-popup-input-field" type="number" name="roomNumber" id="roomNumber" 
                           placeholder="Nhập số phòng..." value="${keepRoomNumber}" required>
                </div>

                <div class="form-group">
                    <label class="input-label">Tầng*</label>
                    <input class="service-popup-input-field" type="number" name="floor" id="floor" 
                           placeholder="Nhập tầng..." value="${keepFloor}" required>
                </div>

                <div class="form-group">
                    <label class="input-label">Hạng phòng*</label>
                    <select class="service-popup-input-field" name="roomTypeId" id="roomTypeId" required>
                        <option value="">Chọn hạng phòng</option>
                        <c:forEach var="rt" items="${roomTypeList}">
                            <option value="${rt.getRoomTypeId()}" ${keepRoomTypeId == rt.getRoomTypeId() ? 'selected' : ''}>
                                ${rt.getTypeName()}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="service-popup-action">
                    <button type="submit" class="btn-submit">Xác nhận lưu</button>
                </div>
            </form>
        </div>

        <c:remove var="editRoom" scope="session"/>
        <c:remove var="openCreateModal" scope="session"/>
        <c:remove var="keepRoomNumber" scope="session"/>
        <c:remove var="keepFloor" scope="session"/>
        <c:remove var="keepRoomTypeId" scope="session"/>

        <script src="${pageContext.request.contextPath}/view/assets/javascript/alert.js"></script>
        <script src="${pageContext.request.contextPath}/view/assets/javascript/room-management.js"></script>
    </body>
</html>