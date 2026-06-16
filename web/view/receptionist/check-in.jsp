<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/view/staff/header.jsp" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Thủ tục Nhận Phòng - Check-in</title>
        <link href="${pageContext.request.contextPath}/view/assets/css/check-in.css?v=<%=System.currentTimeMillis()%>" rel="stylesheet" type="text/css">
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

            <div class="search-card">
                <h3>Tìm Kiếm Đơn Đặt Phòng</h3>
                <form action="${pageContext.request.contextPath}/checkin" method="GET">
                    <input type="text" name="searchBookingCode" placeholder="Nhập mã đơn đặt phòng..." value="${param.searchBookingCode}" required />
                    <button type="submit">Tìm Kiếm</button>
                </form>
            </div>

            <c:if test="${not empty booking}">
                <div class="main-card searched-booking-card">
                    <div class="card-header-flex">
                        <h3>Thông Tin Tra Cứu Đơn: ${booking.bookingCode}</h3>
                        <a href="${pageContext.request.contextPath}/checkin" class="btn-close-view">✕ Đóng kết quả xem</a>
                    </div>

                    <div class="info-block">
                        <p><strong>Mã Booking:</strong> ${booking.bookingCode}</p>
                        <p><strong>Khách đặt trực tuyến:</strong> ${booking.guestFullName}</p>
                        <p><strong>Hạng phòng đã đặt:</strong> ${booking.roomTypeName}</p>
                        <p><strong>Số lượng phòng thuê:</strong> ${booking.numRooms} phòng</p>
                        <p><strong>Sức chứa tối đa:</strong> ${booking.capacity} khách/phòng</p>
                        <p><strong>Trạng thái cọc:</strong> ${booking.paymentStatus}</p>
                        <p><strong>Trạng thái đơn:</strong> 
                            <span class="${booking.status eq 'Đã nhận phòng' ? 'badge-success' : (booking.status eq 'Đã hủy' ? 'badge-danger' : 'badge-warning')}">${booking.status}</span>
                        </p>
                        <c:if test="${booking.status eq 'Đã nhận phòng' and not empty booking.actualCheckInTime}">
                            <p><strong>Thời gian nhận phòng:</strong> <span class="text-success-highlight">${fn:substring(booking.actualCheckInTime, 0, 19)}</span></p>
                        </c:if>
                    </div>

                    <c:if test="${not empty booking.requestType}">
                        <div class="switch-block" style="background-color: rgba(26, 68, 108, 0.05); border: 1px solid rgba(26, 68, 108, 0.2); margin-top: 0; margin-bottom: 20px; display: block;">
                            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px;">
                                <span style="font-family: 'Lora', serif; font-size: 14px; font-weight: 700; color: #1a446c; text-transform: uppercase; letter-spacing: 0.5px;">
                                    Yêu Cầu Đặc Biệt Từ Khách Hàng:
                                </span>
                                <span class="${booking.requestStatus eq 'Chờ xử lý' ? 'badge-warning' : 'badge-success'}">
                                    ${booking.requestStatus}
                                </span>
                            </div>
                            <p style="font-family: 'Lora', serif; font-size: 14px; color: #4f6f8f; margin: 0; line-height: 1.5;">
                                <strong>Loại dịch vụ:</strong> ${booking.requestType}<br>
                                <strong>Chi tiết:</strong> ${booking.requestDetails}
                            </p>
                        </div>
                    </c:if>

                    <hr class="divider">

                    <form action="${pageContext.request.contextPath}/checkin" method="POST" id="checkInForm">
                        <input type="hidden" name="bookingId" value="${booking.bookingId}" />
                        <input type="hidden" name="bookingCode" value="${booking.bookingCode}" />
                        <input type="hidden" name="currentGuestId" value="${booking.guestId}" />
                        <input type="hidden" name="maxCapacity" value="${booking.capacity}" />
                        <input type="hidden" name="numRooms" value="${booking.numRooms}" />

                        <label><b>Số khách thực tế *</b></label>
                        <input type="number" name="numGuests" min="1" required placeholder="Nhập số người lưu trú..." value="${not empty param.numGuests ? param.numGuests : booking.numGuests}" />

                        <div class="switch-block <c:if test="${booking.status eq 'Đã nhận phòng' or booking.status eq 'Đã hủy'}">hide-element</c:if>">
                            <input type="checkbox" name="isDifferentGuest" id="isDifferentGuest" value="true">
                            <label for="isDifferentGuest"><b>Khách làm thủ tục nhận phòng là người khác (Người đặt hộ)</b></label>
                        </div>

                        <h5>Hồ Sơ Khách Đại Diện Lưu Trú</h5>

                        <label>Họ và tên *</label>
                        <input type="text" name="idFullName" value="${booking.guestFullName}" required />

                        <label>Số điện thoại</label>
                        <input type="text" name="idPhone" value="${booking.guestPhone}" />

                        <label>Email</label>
                        <input type="email" name="idEmail" value="${booking.guestEmail}" />

                        <label>Số CCCD / Hộ chiếu *</label>
                        <input type="text" name="idNumber" required placeholder="Nhập số giấy tờ tùy thân..." value="${booking.idNumber}" />

                        <label>Ngày sinh *</label>
                        <input type="date" name="dateOfBirth" required value="${booking.dateOfBirth}" />

                        <label>Quốc tịch *</label>
                        <input type="text" name="nationality" value="${not empty booking.nationality ? booking.nationality : 'Việt Nam'}" required />

                        <c:if test="${not empty errorMsg}">
                            <div class="alert-danger alert-centered">
                                ${errorMsg}
                            </div>
                        </c:if>

                        <div class="form-footer footer-buttons-flex">
                            <c:choose>
                                <c:when test="${booking.status eq 'Đã nhận phòng'}">
                                    <button type="submit" name="action" value="checkin" class="btn-success">Cập Nhật Thông Tin Lưu Trú</button>
                                </c:when>
                                <c:when test="${booking.status eq 'Đã hủy'}">
                                    <button type="button" class="btn-success btn-disabled" disabled>Đơn Đã Bị Hủy</button>
                                </c:when>
                                <c:otherwise>
                                    <a href="${pageContext.request.contextPath}/receptionist/assign-room?bookingId=${booking.bookingId}" class="btn-select" style="background-color: #2563eb; text-align: center; line-height: 2.4; padding: 0 20px;">
                                        Gán phòng & Sơ đồ
                                    </a>
                                    <button type="submit" name="action" value="checkin" class="btn-success">Xác Nhận Check-In</button>
                                    <button type="submit" name="action" value="cancel" class="btn-success btn-danger-flat" onclick="return confirm('Bạn có chắc chắn muốn hủy đơn đặt phòng này do khách không đến (No-Show)?');">Hủy Đơn (No-Show)</button>
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
                            <th>Hạng Phòng</th>
                            <th>Trạng Thái</th>
                            <th>Giờ Vào / Giờ Hẹn</th>
                            <th class="text-center">Thao Tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${listToday}" var="b">
                            <tr>
                                <td>${b.bookingCode}</td>
                                <td>
                                    <span class="td-name">${b.guestFullName}</span>
                                    
                                    <c:if test="${b.requestType eq 'Nhận phòng muộn' and b.status eq 'Đã xác nhận' and b.requestStatus eq 'Đã phê duyệt'}">
                                        <span class="badge-success" style="background-color: #e0f2fe; color: #0369a1; border: 1px solid #bae6fd; margin-left: 6px; font-size: 11px; padding: 2px 6px; text-transform: none;">
                                            Đến muộn
                                        </span>
                                    </c:if>

                                    <c:if test="${b.requestType eq 'Nhận phòng sớm' and b.status eq 'Đã xác nhận' and b.requestStatus eq 'Đã phê duyệt'}">
                                        <span class="badge-success" style="background-color: #e0f2fe; color: #0369a1; border: 1px solid #bae6fd; margin-left: 6px; font-size: 11px; padding: 2px 6px; text-transform: none;">
                                            Đến sớm
                                        </span>
                                    </c:if>
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
                                        <c:when test="${b.status eq 'Đã nhận phòng' and not empty b.actualCheckInTime}">
                                            <span class="text-success-time">${fn:substring(b.actualCheckInTime, 11, 19)}</span>
                                        </c:when>
                                        
                                        <c:when test="${b.status eq 'Đã xác nhận' and b.requestType eq 'Nhận phòng muộn' and b.requestStatus eq 'Đã phê duyệt'}">
                                            <span style="color: #0369a1; font-weight: 600;">Hẹn: 21:00</span>
                                        </c:when>

                                        <c:when test="${b.status eq 'Đã xác nhận' and b.requestType eq 'Nhận phòng sớm' and b.requestStatus eq 'Đã phê duyệt'}">
                                            <span style="color: #0369a1; font-weight: 600;">Hẹn: 08:00</span>
                                        </c:when>
                                        
                                        <c:otherwise>
                                            <span class="text-muted-dash">—</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-center">
                                    <div class="table-actions-flex" style="gap: 5px; justify-content: center;">
                                        <a href="${pageContext.request.contextPath}/checkin?searchBookingCode=${b.bookingCode}" class="btn-select">
                                            <c:choose>
                                                <c:when test="${b.status eq 'Đã nhận phòng'}">Xem đơn</c:when>
                                                <c:otherwise>Chọn đơn</c:otherwise>
                                            </c:choose>
                                        </a>

                                        <%-- 🚀 VỊ TRÍ 2: NÚT GÁN PHÒNG NHANH Ở TỪNG DÒNG TRONG BẢNG (CHỈ HIỆN KHI ĐƠN CHƯA NHẬN/HỦY) --%>
                                        <c:if test="${b.status eq 'Đã xác nhận'}">
                                            <a href="${pageContext.request.contextPath}/receptionist/assign-room?bookingId=${b.bookingId}" class="btn-select" style="background-color: #2563eb;">
                                                Gán phòng
                                            </a>
                                            
                                            <form action="${pageContext.request.contextPath}/checkin" method="POST" class="form-inline-table">
                                                <input type="hidden" name="bookingId" value="${b.bookingId}" />
                                                <input type="hidden" name="bookingCode" value="${b.bookingCode}" />
                                                <button type="submit" name="action" value="cancel" class="btn-danger-table-click" onclick="return confirm('⚠️ Bạn có chắc chắn muốn hủy nhanh đơn ${b.bookingCode} (No-Show)?');">Hủy</button>
                                            </form>
                                        </c:if>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty listToday}">
                            <tr>
                                <td colspan="6" class="td-empty">Hôm nay chưa có đơn đặt phòng nào đến.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
        <script src="${pageContext.request.contextPath}/view/assets/js/check-in-validation.js?v=<%=System.currentTimeMillis()%>"></script>
    </body>
</html>