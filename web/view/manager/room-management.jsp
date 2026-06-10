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

    <body data-edit-mode="${not empty editRoom}" data-detail-mode="${not empty selectedRoom}">
        <%@ include file="/view/staff/header.jsp" %>
        <%@ include file="/view/staff/navbar.jsp" %>

        <main class="content-container">
            <div class="header-action">
                <h1 class="header-title">Quản lý phòng</h1>
            </div>

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
                    <%-- Kiểm tra xem có phòng nào thuộc tầng này trong roomList không --%>
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

        <%-- Modal Chi tiết --%>
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
                        <h3>Danh sách khách đang lưu trú</h3>
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

        <%-- Modal Chỉnh sửa --%>
        <div class="room-modal" id="edit-modal">
            <form action="RoomEdit" method="post" id="edit-form" class="popup-content">
                <h2 class="service-popup-title">Chỉnh sửa phòng ${editRoom.getRoomNumber()}</h2>

                <input type="hidden" name="roomNumber" value="${editRoom.getRoomNumber()}">
                <input type="hidden" name="page" value="${currentPage}">
                <input type="hidden" name="roomTypeId" value="${selectedRoomTypeId}">
                <input type="hidden" name="keyword" value="${keyword}">

                <c:if test="${editRoom != null}">
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

                <div class="service-popup-action">
                    <button type="button" class="btn-close" id="btn-close-edit">Huỷ</button>
                    <button type="submit" class="btn-submit">Xác nhận lưu</button>
                </div>
            </form>
        </div>

        <script src="${pageContext.request.contextPath}/view/assets/javascript/room-management.js"></script>
    </body>
</html>