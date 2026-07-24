<%-- 
    Author: ThuDNM-HE204370 
    Date created: 23/06/2026 
    Purpose: Check-in page.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
            <!DOCTYPE html>
            <html>

            <head>
                <jsp:include page="/view/staff/header.jsp" />
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
                <title>Thủ tục Nhận Phòng - Check-in</title>
                <link
                    href="${pageContext.request.contextPath}/view/assets/css/check-in.css?v=<%=System.currentTimeMillis()%>"
                    rel="stylesheet" type="text/css">
            </head>

            <body>

                <jsp:include page="/view/staff/navbar.jsp" />

                <div class="container">

                    <div class="stats-row">
                        <div class="stat-box">
                            <div class="stat-info">
                                <span class="stat-num">${totalCheckInToday} Đơn</span>
                                <span class="stat-label">Tổng lượt check-in hôm nay</span>
                            </div>
                        </div>
                        <div class="stat-box">
                            <div class="stat-info">
                                <span class="stat-num">${notCheckedInCount} Đơn</span>
                                <span class="stat-label">Chưa check-in (Đã xác nhận)</span>
                            </div>
                        </div>
                        <div class="stat-box">
                            <div class="stat-info">
                                <span class="stat-num">${checkedInCount} Đơn</span>
                                <span class="stat-label">Đã check-in thành công</span>
                            </div>
                        </div>
                    </div>

                    <!-- Khối thông báo thành công khi cập nhật lịch hẹn -->
                    <c:if test="${param.successTime eq 'true'}">
                        <div class="alert alert-success"
                            style="text-align: center; margin-bottom: 15px; padding: 10px; background-color: #d1fae5; color: #065f46; border-radius: 6px; font-weight: 500; font-family: 'Lora', serif; font-size: 14px;">
                            Cập nhật giờ hẹn đến và gia hạn giữ phòng thành công!
                        </div>
                    </c:if>

                    <div class="search-card">
                        <h3>Tìm Kiếm Đơn Đặt Phòng</h3>
                        <form action="${pageContext.request.contextPath}/checkin" method="GET">
                            <input type="text" name="searchBookingCode" placeholder="Nhập mã đơn đặt phòng..."
                                value="${param.searchBookingCode}" required />
                            <button type="submit">Tìm Kiếm</button>
                        </form>
                    </div>

                    <c:if test="${not empty booking}">
                        <div class="main-card searched-booking-card">
                            <div class="card-header-flex">
                                <h3>Thông Tin Tra Cứu Đơn: ${booking.bookingCode}</h3>
                                <a href="${pageContext.request.contextPath}/checkin" class="btn-close-view">✕ Đóng kết
                                    quả xem</a>
                            </div>

                            <div class="info-block">
                                <p><strong>Mã Booking:</strong> ${booking.bookingCode}</p>
                                <p><strong>Khách đặt trực tuyến:</strong> ${booking.guestFullName}</p>
                                <p><strong>Hạng phòng đã đặt:</strong> ${booking.roomTypeName}</p>
                                <p><strong>Số lượng phòng thuê:</strong> ${booking.numRooms} phòng</p>
                                <p><strong>Sức chứa tối đa:</strong> ${booking.maxAdults} người lớn &
                                    ${booking.maxChildren} trẻ em /phòng</p>
                                <p><strong>Trạng thái cọc:</strong> ${booking.paymentStatus}</p>
                                <p><strong>Trạng thái đơn:</strong>
                                    <span
                                        class="${booking.status eq 'Đã nhận phòng' ? 'badge-success' : (booking.status eq 'Đã hủy' ? 'badge-danger' : 'badge-warning')}">${booking.status}</span>
                                </p>
                                <c:if
                                    test="${booking.status eq 'Đã nhận phòng' and not empty booking.actualCheckInTime}">
                                    <p><strong>Thời gian nhận phòng:</strong> <span
                                            class="text-success-highlight">${fn:substring(booking.actualCheckInTime, 0,
                                            19)}</span></p>
                                </c:if>

                                <!-- Hiển thị lịch hẹn hiện tại nếu có cuộc gọi note trước -->
                                <c:if test="${not empty booking.expectedCheckInTime}">
                                    <p><strong>Lịch hẹn gọi điện:</strong> <span style="font-weight: 600;">Khách báo đến
                                            lúc ${fn:substring(booking.expectedCheckInTime, 0, 5)}</span></p>
                                    <p><strong>Ghi chú cuộc gọi:</strong> <span class="text-muted"
                                            style="font-style: italic; color: #4b5563;">"${booking.callNote}"</span></p>
                                </c:if>

                                <div class="room-progress-block">
                                    <span class="progress-title">Tiến độ gán phòng:</span>
                                    <span class="progress-status-text">
                                        Đã gán <span class="badge-assigned">${booking.assignedRoomsCount} /
                                            ${booking.numRooms} phòng</span>
                                        <c:if test="${booking.assignedRoomsCount > 0}">
                                            (Gồm phòng: <strong
                                                class="assigned-room-list">${booking.assignedRoomList}</strong>)
                                        </c:if>

                                        <c:choose>
                                            <c:when test="${booking.numRooms - booking.assignedRoomsCount > 0}">
                                                <span class="status-missing">➔ Còn thiếu: ${booking.numRooms -
                                                    booking.assignedRoomsCount} phòng</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status-full">➔ Đã đủ phòng!</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                            </div>

                            <c:if test="${booking.status eq 'Đã xác nhận' and not notTodayCheckIn}">
                                <div class="special-request-card">
                                    <div class="request-card-header">
                                        <span class="request-title-text">Ghi nhận cuộc gọi hẹn giờ của Khách</span>
                                    </div>
                                    <form action="${pageContext.request.contextPath}/checkin" method="POST"
                                        style="margin-top: 15px;" onsubmit="return validateExpectedTime(this)">
                                        <input type="hidden" name="bookingId" value="${booking.bookingId}" />
                                        <input type="hidden" name="bookingCode" value="${booking.bookingCode}" />

                                        <div
                                            style="display: flex; gap: 15px; align-items: flex-end; flex-wrap: wrap; width: 100%;">
                                            <div style="flex: 1; min-width: 150px;">
                                                <label
                                                    style="display: block; font-family: 'Lora', serif; font-size: 13.5px; font-weight: 600; margin-bottom: 6px; color: #1a446c;">Giờ
                                                    hẹn đến *</label>
                                                <input type="time" name="expectedTime" required
                                                    style="width: 100%; padding: 10px 14px; border: 1px solid #ebd9b4; border-radius: 4px; font-family: 'Lora', serif; font-size: 14px; color: #1a446c; outline: none; background-color: #ffffff;"
                                                    value="${not empty booking.expectedCheckInTime ? fn:substring(booking.expectedCheckInTime, 0, 5) : ''}" />
                                            </div>
                                            <div style="flex: 3; min-width: 250px;">
                                                <label
                                                    style="display: block; font-family: 'Lora', serif; font-size: 13.5px; font-weight: 600; margin-bottom: 6px; color: #1a446c;">Nội
                                                    dung ghi chú cuộc gọi *</label>
                                                <input type="text" name="note" placeholder="Nhập nhanh lý do..."
                                                    required
                                                    style="width: 100%; padding: 10px 14px; border: 1px solid #ebd9b4; border-radius: 4px; font-family: 'Lora', serif; font-size: 14px; color: #1a446c; outline: none; background-color: #ffffff;"
                                                    value="${not empty booking.callNote ? booking.callNote : ''}" />
                                            </div>
                                            <div style="flex: 1; min-width: 150px; text-align: right;">
                                                <button type="submit" name="action" value="updateExpectedTime"
                                                    class="btn-success"
                                                    style="width: 100%; height: 41px; padding: 0 15px;">
                                                    Đánh dấu lịch hẹn
                                                </button>
                                            </div>
                                        </div>
                                    </form>
                                    <script>
                                        function validateExpectedTime(form) {
                                            const timeInput = form.expectedTime.value;
                                            if (!timeInput) return true;

                                            const now = new Date();
                                            const currentHours = now.getHours();
                                            const currentMinutes = now.getMinutes();

                                            const timeParts = timeInput.split(':');
                                            const inputHours = parseInt(timeParts[0], 10);
                                            const inputMinutes = parseInt(timeParts[1], 10);

                                            if (inputHours < currentHours || (inputHours === currentHours && inputMinutes <= currentMinutes)) {
                                                alert("Lỗi: Giờ hẹn đến phải sau giờ hiện tại!");
                                                return false;
                                            }
                                            return true;
                                        }
                                    </script>
                                </div>
                            </c:if>

                            <c:if test="${not empty booking.requestType}">
                                <div class="special-request-card">
                                    <div class="request-card-header">
                                        <span class="request-title-text">
                                            Yêu Cầu Đặc Biệt Từ Khách Hàng:
                                        </span>
                                        <span
                                            class="${booking.requestStatus eq 'Chờ xử lý' ? 'badge-warning' : 'badge-success'}">
                                            ${booking.requestStatus}
                                        </span>
                                    </div>
                                    <p class="request-content-text">
                                        <strong>Loại dịch vụ:</strong> ${booking.requestType}<br>
                                        <strong>Chi tiết:</strong> ${booking.requestDetails}
                                    </p>
                                </div>
                            </c:if>

                            <hr class="divider">

                            <form action="${pageContext.request.contextPath}/checkin" method="POST" id="checkInForm">
                                <c:set var="inputDisabled" value="${notTodayCheckIn ? 'disabled' : ''}" />
                                <input type="hidden" name="bookingId" value="${booking.bookingId}" />
                                <input type="hidden" name="bookingCode" value="${booking.bookingCode}" />
                                <input type="hidden" name="currentGuestId" value="${booking.guestId}" />
                                <input type="hidden" name="numRooms" value="${booking.numRooms}" />
                                <input type="hidden" name="maxAdults" value="${booking.maxAdults}" />
                                <input type="hidden" name="maxChildren" value="${booking.maxChildren}" />

                                <div class="guest-input-container" style="margin-bottom: 20px;">
                                    <label><b>Số khách thực tế *</b></label>

                                    <div class="guest-input-group">
                                        <label for="numAdults">Người lớn:</label>
                                        <input type="number" id="numAdults" name="numAdults" min="1" required
                                            placeholder="Số người lớn..."
                                            value="${not empty param.numAdults ? param.numAdults : (booking.numGuests != null ? booking.numGuests : 1)}"
                                            ${inputDisabled} />
                                    </div>

                                    <div class="guest-input-group">
                                        <label for="numChildren">Trẻ em:</label>
                                        <input type="number" id="numChildren" name="numChildren" min="0" required
                                            placeholder="Số trẻ em..."
                                            value="${not empty param.numChildren ? param.numChildren : 0}"
                                            ${inputDisabled} />
                                    </div>
                                </div>

                                <div class="switch-block <c:if test=" ${booking.status eq 'Đã nhận phòng' or
                                    booking.status eq 'Đã hủy' }">hide-element
                    </c:if>">
                    <input type="checkbox" name="isDifferentGuest" id="isDifferentGuest" value="true" ${inputDisabled}>
                    <label for="isDifferentGuest"><b>Khách làm thủ tục nhận phòng là người khác (Người đặt
                            hộ)</b></label>
                </div>

                <h5>Hồ Sơ Khách Đại Diện Lưu Trú</h5>

                <label>Họ và tên *</label>
                <input type="text" name="idFullName" value="${booking.guestFullName}" required ${inputDisabled} />

                <label>Số điện thoại</label>
                <input type="text" name="idPhone" value="${booking.guestPhone}" ${inputDisabled} />

                <label>Email liên hệ *</label>
                <input type="email" name="idEmail" value="${not empty booking.guestEmail ? booking.guestEmail : ''}"
                    required placeholder="Nhập email khách hàng..." ${inputDisabled} />

                <label>Số CCCD / Hộ chiếu *</label>
                <input type="text" name="idNumber" required placeholder="Nhập số giấy tờ tùy thân..."
                    value="${booking.idNumber}" ${inputDisabled} />

                <label>Ngày sinh *</label>
                <input type="date" name="dateOfBirth" required value="${booking.dateOfBirth}" ${inputDisabled} />

                <label>Quốc tịch *</label>
                <input type="text" name="nationality"
                    value="${not empty booking.nationality ? booking.nationality : 'Việt Nam'}" required
                    ${inputDisabled} />

                <c:if test="${not empty errorMsg}">
                    <div class="alert-danger alert-centered">
                        ${errorMsg}
                    </div>
                </c:if>

                <div class="form-footer footer-buttons-flex">
                    <c:choose>
                        <c:when test="${notTodayCheckIn}">
                            <button type="button" class="btn-success btn-disabled"
                                style="background-color: #94a3b8; cursor: not-allowed; opacity: 0.7;" disabled>Chưa đến
                                ngày Check-in</button>
                        </c:when>
                        <c:when test="${booking.status eq 'Đã hủy'}">
                            <button type="button" class="btn-success btn-disabled" disabled>Đơn Đã Bị Hủy</button>
                        </c:when>
                        <c:otherwise>
                            <button type="submit" name="action" value="checkin" class="btn-success">
                                Lưu Hồ Sơ Lưu Trú
                            </button>

                            <c:if test="${booking.assignedRoomsCount < booking.numRooms}">
                                <button type="submit" name="action" value="assign" class="btn-success">
                                    Tiến Hành Gán Phòng Tiếp Theo
                                </button>
                            </c:if>

                            <c:if test="${booking.status eq 'Đã xác nhận'}">
                                <button type="submit" name="action" value="cancel" class="btn-success btn-danger-flat"
                                    onclick="return confirm('Bạn có chắc chắn muốn hủy đơn đặt phòng này do khách không đến (No-Show)?');">Hủy
                                    Đơn</button>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                </div>
                </form>
                </div>
                </c:if>

                <div class="decor-card">
                    <h4 class="card-title">DANH SÁCH ĐẶT PHÒNG ĐẾN HÔM NAY</h4>

                    <table class="today-table">
                        <thead>
                            <tr>
                                <th>Mã Đặt Phòng</th>
                                <th>Khách Hàng</th>
                                <th>Số Điện Thoại</th>
                                <th>Hạng Phòng</th>
                                <th>Trạng Thái</th>
                                <th>Giờ Hẹn Đến</th>
                                <th>Giờ Vào Thực Tế</th>
                                <th class="text-center">Thao Tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${listToday}" var="b">
                                <tr>
                                    <td>${b.bookingCode}</td>
                                    <td>
                                        <span class="td-name"
                                            style="display: block; font-weight: 600;">${b.guestFullName}</span>

                                        <c:if
                                            test="${b.requestType eq 'Nhận phòng muộn' and b.status eq 'Đã xác nhận' and b.requestStatus eq 'Đã phê duyệt'}">
                                            <span class="badge-success"
                                                style="background-color: #fee2e2; color: #b91c1c; border: 1px solid #fca5a5; font-size: 11px; padding: 2px 6px; text-transform: none; font-weight: 500; border-radius: 4px; display: inline-block; margin-top: 4px;">
                                                Nhận phòng muộn
                                            </span>
                                        </c:if>

                                        <c:if
                                            test="${b.requestType eq 'Nhận phòng sớm' and b.status eq 'Đã xác nhận' and b.requestStatus eq 'Đã phê duyệt'}">
                                            <span class="badge-success"
                                                style="background-color: #f0fdf4; color: #15803d; border: 1px solid #bbf7d0; font-size: 11px; padding: 2px 6px; text-transform: none; font-weight: 500; border-radius: 4px; display: inline-block; margin-top: 4px;">
                                                Nhận phòng sớm
                                            </span>
                                        </c:if>

                                        <!-- Hiển thị tag trạng thái đã gọi điện đặt lịch (Sử dụng badge-warning của hệ thống) -->
                                        <c:if test="${not empty b.expectedCheckInTime and b.status eq 'Đã xác nhận'}">
                                            <span class="badge-warning"
                                                style="font-size: 11px; padding: 2px 6px; text-transform: none; font-weight: 500; border-radius: 4px; display: inline-block; margin-top: 4px;"
                                                title="Ghi chú cuộc gọi: ${b.callNote}">
                                                Đã hẹn giờ đến
                                            </span>
                                        </c:if>
                                    </td>
                                    <td>
                                        <span class="td-phone"
                                            style="font-size: 14px; color: #475569; font-weight: 500;">
                                            ${b.guestPhone}
                                        </span>
                                    </td>
                                    <td><span class="room-badge">${b.roomTypeName}</span></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${b.status eq 'Đã nhận phòng'}">
                                                <span class="badge-success">${b.status}</span>
                                            </c:when>
                                            <c:when test="${b.status eq 'Đã hủy'}">
                                                <span class="badge-danger badge-canceled-text">${b.status}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge-warning">${b.status}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td class="time-col-text">
                                        <c:choose>
                                            <c:when test="${not empty b.expectedCheckInTime}">
                                                <span style="font-weight: 700;"
                                                    title="Ghi chú cuộc gọi: ${b.callNote}">${fn:substring(b.expectedCheckInTime,
                                                    0, 5)}</span>
                                            </c:when>
                                            <c:when
                                                test="${(b.requestType eq 'Nhận phòng muộn' or b.requestType eq 'Nhận phòng sớm') and b.requestStatus eq 'Đã phê duyệt' and not empty b.requestedCheckIn}">
                                                <span>${fn:substring(b.requestedCheckIn, 11, 16)}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted-dash">—</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td class="time-col-text">
                                        <c:choose>
                                            <c:when
                                                test="${b.status eq 'Đã nhận phòng' and not empty b.actualCheckInTime}">
                                                <span class="text-success-time">${fn:substring(b.actualCheckInTime, 11,
                                                    19)}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted-dash">—</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td class="text-center">
                                        <div class="table-actions-flex" style="gap: 5px; justify-content: center;">
                                            <a href="${pageContext.request.contextPath}/checkin?searchBookingCode=${b.bookingCode}"
                                                class="btn-select">
                                                <c:choose>
                                                    <c:when test="${b.status eq 'Đã nhận phòng'}">Xem đơn</c:when>
                                                    <c:otherwise>Chọn đơn</c:otherwise>
                                                </c:choose>
                                            </a>

                                            <c:if test="${b.status eq 'Đã xác nhận'}">
                                                <form action="${pageContext.request.contextPath}/checkin" method="POST"
                                                    class="form-inline-table">
                                                    <input type="hidden" name="bookingId" value="${b.bookingId}" />
                                                    <input type="hidden" name="bookingCode" value="${b.bookingCode}" />
                                                    <button type="submit" name="action" value="cancel"
                                                        class="btn-danger-table-click"
                                                        onclick="return confirm('Bạn có chắc chắn muốn hủy nhanh đơn ${b.bookingCode} (No-Show)?');">Hủy</button>
                                                </form>
                                            </c:if>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty listToday}">
                                <tr>
                                    <td colspan="8" class="td-empty">Hôm nay chưa có đơn đặt phòng nào đến.</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
                </div>
                <script
                    src="${pageContext.request.contextPath}/view/assets/javascript/check-in.js?v=<%=System.currentTimeMillis()%>"></script>
            </body>

            </html>