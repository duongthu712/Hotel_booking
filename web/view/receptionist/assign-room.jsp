<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/view/staff/header.jsp" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Sơ Đồ Lưới & Gán Phòng - Receptionist</title>
        <link href="${pageContext.request.contextPath}/view/assets/css/assign-room.css?v=<%=System.currentTimeMillis()%>" rel="stylesheet" type="text/css">
        <link href="${pageContext.request.contextPath}/view/assets/css/check-in.css?v=<%=System.currentTimeMillis()%>" rel="stylesheet" type="text/css">
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    </head>
    <body>

        <jsp:include page="/view/staff/navbar.jsp" />

        <div class="assign-room-wrapper">

            <c:if test="${not empty sessionScope.notification}">
                <input type="hidden" id="serverStatus" value="${sessionScope.notificationType}" />
                <c:remove var="notification" scope="session"/>
                <c:remove var="notificationType" scope="session"/>
            </c:if>

            <form action="${pageContext.request.contextPath}/assign-room" method="GET" class="filter-form-wrapper">
                <c:if test="${not empty targetBookingId}">
                    <input type="hidden" name="bookingId" value="${targetBookingId}" />
                </c:if>

                <div class="filter-bar-container">
                    <div class="filter-group room-type-width">
                        <label for="filterRoomTypeName">Hạng phòng:</label>
                        <select name="filterRoomTypeName" id="filterRoomTypeName">
                            <option value="all">Tất cả hạng phòng</option>
                            <c:forEach items="${activeRoomTypes}" var="typeName">
                                <option value="${typeName}" ${param.filterRoomTypeName eq typeName ? 'selected' : ''}>
                                    ${typeName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="filter-group floor-width">
                        <label for="filterFloor">Tầng:</label>
                        <select name="filterFloor" id="filterFloor">
                            <option value="all" ${param.filterFloor eq 'all' ? 'selected' : ''}>Tất cả tầng</option>
                            <c:forEach items="${existingFloors}" var="floorNumber">
                                <option value="${floorNumber}" ${(param.filterFloor ne 'all' and param.filterFloor eq floorNumber) ? 'selected' : ''}>
                                    Tầng ${floorNumber}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <button type="submit" class="btn-filter-search">Tìm kiếm</button>
                </div>
            </form>

            <div class="split-layout-container">

                <div class="split-left-panel">
                    <div class="main-card room-matrix-container">
                        <div class="card-header-flex">
                            <c:choose>
                                <c:when test="${not empty targetBookingId}">
                                    <div>
                                        <h3>Chọn Phòng Hạng: ${targetBooking.roomTypeName} (Đơn #${targetBookingId})</h3>
                                        <div class="booking-info-group">
                                            <span class="badge-info-navy">Đặt: ${targetBooking.numRooms} phòng</span>
                                            <span class="badge-info-navy">Đã gán: ${assignedRoomsCount != null ? assignedRoomsCount : 0} phòng</span>
                                            <c:choose>
                                                <c:when test="${(targetBooking.numRooms - (assignedRoomsCount != null ? assignedRoomsCount : 0)) > 0}">
                                                    <span class="badge-info-navy">Còn lại: ${targetBooking.numRooms - (assignedRoomsCount != null ? assignedRoomsCount : 0)} phòng</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge-success-green">Đã đủ phòng</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                    <a href="${pageContext.request.contextPath}/checkin" class="btn-close-view">Hủy luồng</a>
                                </c:when>
                                <c:otherwise>
                                    <h3>Sơ Đồ Trạng Thái Phòng Toàn Khách Sạn</h3>
                                    <span class="stat-label overview-mode-text">Tổng quan</span>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <hr class="divider">

                        <div class="room-grid">
                            <c:forEach items="${roomMatrix}" var="room">
                                <div class="room-card ${room.status eq 'Phòng trống' ? 'status-available' : 
                                                        room.status eq 'Phòng có khách' ? 'status-occupied' : 
                                                        room.status eq 'Đang dọn dẹp' ? 'status-dirty' : 'status-maintenance'}"
                                     data-view-number="${room.roomNumber}"
                                     data-view-type="${room.roomTypeName} (Tầng ${room.floor})"
                                     data-view-status="${room.status}"
                                     data-view-code="${not empty room.currentBookingCode ? room.currentBookingCode : '--'}"
                                     data-view-guests="${not empty room.guestFullName ? room.guestFullName : '--'}"
                                     data-view-phone="${not empty room.guestPhone ? room.guestPhone : '--'}"
                                     data-view-id="${not empty room.guestIdNumber ? room.guestIdNumber : '--'}">

                                    <div class="room-number">Phòng ${room.roomNumber}</div>
                                    <div class="room-type">${room.roomTypeName} (Tầng ${room.floor})</div>
                                    <div class="room-status-badge">${room.status}</div>

                                    <c:choose>
                                        <c:when test="${room.status eq 'Phòng có khách'}">
                                            <div class="room-guests-info">
                                                <strong>Mã:</strong> ${room.currentBookingCode}<br/>
                                                <strong>Khách:</strong> ${fn:substring(room.guestFullName, 0, 20)}${fn:length(room.guestFullName) > 20 ? '...' : ''}
                                            </div>
                                        </c:when>

                                        <c:when test="${room.status eq 'Phòng trống'}">
                                            <c:if test="${not empty targetBookingId}">
                                                <div class="select-wrapper">
                                                    <label class="select-label">
                                                        <input type="radio" name="selectedRoomNumber" value="${room.roomNumber}" 
                                                               form="assignRoomMainForm" data-capacity="${room.capacity}" class="select-radio" required />
                                                        Chọn phòng này
                                                    </label>
                                                </div>
                                            </c:if>
                                        </c:when>

                                        <c:when test="${room.status eq 'Đang dọn dẹp'}">
                                            <div class="room-cleaning-text">Phòng đang vệ sinh</div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="room-locked-text">Tạm thời khóa</div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </div>

                <div class="split-right-panel">
                    <div class="guest-stay-card side-sticky-card">
                        <h4 class="guest-stay-title" id="rightPanelTitle">
                            <c:choose>
                                <c:when test="${not empty targetBookingId}">THÔNG TIN KHÁCH LƯU TRÚ THỰC TẾ</c:when>
                                <c:otherwise>CHI TIẾT PHÒNG KHÁCH SẠN</c:otherwise>
                            </c:choose>
                        </h4>

                        <input type="hidden" id="roomCapacity" value="${not empty targetBooking ? targetBooking.capacity : 2}" />

                        <div id="overviewRoomDetailContainer" style="${not empty targetBookingId ? 'display: none;' : ''}">
                            <div class="select-room-placeholder" id="overviewPlaceholder">
                                Vui lòng chọn một ô phòng từ sơ đồ ma trận phía bên trái để xem thông tin chi tiết.
                            </div>

                            <div id="overviewDetailContent" style="display: none; animation: fadeInModal 0.2s ease-out;">
                                <table class="table-detail-view">
                                    <tr>
                                        <td class="label-bold">Số phòng:</td>
                                        <td class="value-bold" id="viewRoomNumber">--</td>
                                    </tr>
                                    <tr>
                                        <td class="label-bold">Hạng phòng:</td>
                                        <td id="viewRoomType">--</td>
                                    </tr>
                                    <tr>
                                        <td class="label-bold">Trạng thái:</td>
                                        <td id="viewRoomStatus">--</td>
                                    </tr>
                                    <tr id="viewBookingCodeRow">
                                        <td class="label-bold">Mã đơn đặt:</td>
                                        <td class="value-code" id="viewBookingCode">--</td>
                                    </tr>
                                </table>

                                <div id="viewGuestsTableContainer" style="display: none;">
                                    <h5 class="guest-list-title">DANH SÁCH KHÁCH LƯU TRÚ</h5>
                                    <table class="table-guests-list">
                                        <thead>
                                            <tr>
                                                <th>STT</th>
                                                <th>Họ và tên</th>
                                                <th>Số điện thoại</th>
                                                <th>Số CCCD/HC</th>
                                            </tr>
                                        </thead>
                                        <tbody id="viewGuestsTableBody"></tbody>
                                    </table>
                                </div>
                            </div>
                        </div>

                        <c:if test="${not empty targetBookingId}">
                            <form action="${pageContext.request.contextPath}/assign-room" method="POST" id="assignRoomMainForm">
                                <input type="hidden" name="bookingId" value="${targetBookingId}" />

                                <div id="assignRoomFormContainer">
                                    <div class="guest-count-wrapper">
                                        <label class="guest-count-label">
                                            Số lượng khách ở phòng này * <div id="capacityDisplay" class="capacity-alert-text" data-max="${targetBooking.capacity}">
                                                Sức chứa tối đa của phòng này: ${targetBooking.capacity} người
                                            </div>
                                        </label>
                                        <input type="number" id="currentRoomGuests" name="currentRoomGuests" 
                                               min="1" max="${targetBooking.capacity}" value="1" 
                                               class="guest-count-input" />
                                    </div>

                                    <hr class="guest-card-divider">

                                    <div class="guest-fields-scroll">
                                        <div id="guestFieldsContainer">
                                            <div class="select-room-placeholder" id="placeholderFormText">
                                                Chọn một phòng trống từ sơ đồ để bắt đầu nhập hồ sơ lưu trú.
                                            </div>
                                        </div>
                                    </div>

                                    <div class="action-bar-submit-only">
                                        <button type="submit" class="btn-success btn-submit-full btn-filter-search">
                                            XÁC NHẬN GÁN PHÒNG
                                        </button>
                                    </div>
                                </div>
                            </form>
                        </c:if>

                    </div>
                </div>

            </div>
        </div>

        <script src="${pageContext.request.contextPath}/view/assets/javascript/assign-room.js?v=<%=System.currentTimeMillis()%>"></script>
    </body>
</html>