<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
        <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
            <%@page contentType="text/html" pageEncoding="UTF-8" %>
                <!DOCTYPE html>
                <html>

                <head>
                    <jsp:include page="/view/staff/header.jsp" />
                    <link
                        href="${pageContext.request.contextPath}/view/assets/css/process-request.css?v=<%=System.currentTimeMillis()%>"
                        rel="stylesheet" type="text/css">
                    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
                    <script
                        src="${pageContext.request.contextPath}/view/assets/javascript/request-processing.js?v=<%=System.currentTimeMillis()%>"
                        defer></script>
                </head>

                <body>
                    <jsp:include page="/view/staff/navbar.jsp" />

                    <div class="grid-container">
                        <div class="tab-wrapper">
                            <a href="process-request?type=Tất cả&status=${currentStatus}&searchBookingCode=${searchBookingCode}"
                                class="btn ${currentType == 'Tất cả' ? 'btn-primary' : 'btn-outline-secondary'}">Tất cả
                                yêu cầu</a>
                            <a href="process-request?type=Hủy đặt phòng&status=${currentStatus}&searchBookingCode=${searchBookingCode}"
                                class="btn ${currentType == 'Hủy đặt phòng' ? 'btn-primary' : 'btn-outline-secondary'}">Hủy
                                đơn</a>
                            <a href="process-request?type=Đổi hạng phòng&status=${currentStatus}&searchBookingCode=${searchBookingCode}"
                                class="btn ${currentType == 'Đổi hạng phòng' ? 'btn-primary' : 'btn-outline-secondary'}">Đổi
                                hạng</a>
                            <a href="process-request?type=Gia hạn phòng&status=${currentStatus}&searchBookingCode=${searchBookingCode}"
                                class="btn ${currentType == 'Gia hạn phòng' ? 'btn-primary' : 'btn-outline-secondary'}">Gia
                                hạn</a>
                        </div>

                        <div class="sub-filter">
                            <span class="filter-label">Lọc trạng thái:</span>
                            <a href="process-request?type=${currentType}&status=Tất cả&searchBookingCode=${searchBookingCode}"
                                class="filter-item ${currentStatus == 'Tất cả' ? 'active-filter' : ''}">Tổng</a> |
                            <a href="process-request?type=${currentType}&status=Chờ xử lý&searchBookingCode=${searchBookingCode}"
                                class="filter-item ${currentStatus == 'Chờ xử lý' ? 'active-filter' : ''}">Chờ xử lý</a>
                            |
                            <a href="process-request?type=${currentType}&status=Đã phê duyệt&searchBookingCode=${searchBookingCode}"
                                class="filter-item ${currentStatus == 'Đã phê duyệt' ? 'active-filter' : ''}">Đã
                                duyệt</a> |
                            <a href="process-request?type=${currentType}&status=Đã từ chối&searchBookingCode=${searchBookingCode}"
                                class="filter-item ${currentStatus == 'Đã từ chối' ? 'active-filter' : ''}">Đã từ
                                chối</a>

                            <form action="process-request" method="GET"
                                style="display: inline-block; margin-left: 20px;">
                                <input type="hidden" name="type" value="${currentType}">
                                <input type="hidden" name="status" value="${currentStatus}">
                                <input type="text" name="searchBookingCode" value="${searchBookingCode}"
                                    placeholder="Nhập mã booking..."
                                    style="padding: 5px 10px; border: 1px solid #ccc; border-radius: 4px; font-size: 13px;">
                                <button type="submit"
                                    style="padding: 5px 12px; background: #1a446c; color: #fff; border: none; border-radius: 4px; cursor: pointer;">Lọc</button>
                            </form>

                            <span class="total-badge" style="margin-left: auto;">Tổng hiển thị: ${requestList.size()}
                                đơn</span>
                        </div>

                        <div class="main-content">

                            <div class="col-left">
                                <table class="request-table">
                                    <thead>
                                        <tr>
                                            <th>STT</th>
                                            <th>Mã Booking</th>
                                            <th>Loại yêu cầu</th>
                                            <th>Khách hàng</th>
                                            <th>Thời gian</th>
                                            <th>Trạng thái</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${requestList}" var="r" varStatus="loop">
                                            <tr onclick="window.location.href = 'process-request?action=detail&type=${currentType}&status=${currentStatus}&searchBookingCode=${searchBookingCode}&requestId=${r.requestId}'"
                                                class="${r.requestId == req.requestId ? 'active-row' : ''}">
                                                <td>${loop.index + 1}</td>
                                                <td><strong>${r.bookingCode}</strong></td>
                                                <td>
                                                    <span
                                                        class="request-type-badge ${r.requestType == 'Hủy đặt phòng' ? 'type-cancel' : (r.requestType == 'Đổi hạng phòng' ? 'type-change' : 'type-extend')}">
                                                        ${r.requestType}
                                                    </span>
                                                </td>
                                                <td>${r.guestName}</td>
                                                <td class="td-time">
                                                    <c:if test="${not empty r.formattedTime}">
                                                        ${r.formattedTime}<br><small>${r.formattedDate}</small>
                                                    </c:if>
                                                    <c:if test="${empty r.formattedTime}">N/A</c:if>
                                                </td>
                                                <td>
                                                    <span
                                                        class="badge ${r.status == 'Chờ xử lý' ? 'status-pending' : (r.status == 'Đã phê duyệt' ? 'status-success' : 'status-danger')}">
                                                        ${r.status}
                                                    </span>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>

                            <div class="col-right">
                                <div class="detail-view-card">
                                    <c:choose>
                                        <c:when test="${not empty req}">

                                            <h3 class="detail-title">Thông tin yêu cầu</h3>
                                            <div class="info-grid-container">
                                                <div class="info-group-item">
                                                    <span class="info-label-block">Mã Booking</span>
                                                    <span
                                                        class="info-value-block"><strong>${req.bookingCode}</strong></span>
                                                </div>
                                                <div class="info-group-item">
                                                    <span class="info-label-block">Loại yêu cầu</span>
                                                    <span class="info-value-block">
                                                        <span class="request-type-tag">${req.requestType}</span>
                                                    </span>
                                                </div>
                                                <div class="info-group-item">
                                                    <span class="info-label-block">Khách hàng</span>
                                                    <span class="info-value-block">${req.guestName}</span>
                                                </div>
                                                <div class="info-group-item">
                                                    <span class="info-label-block">SĐT</span>
                                                    <span class="info-value-block">${req.guestPhone}</span>
                                                </div>
                                                <div class="info-group-item">
                                                    <span class="info-label-block">Hạng phòng</span>
                                                    <span class="info-value-block">${req.currentRoomTypeName}</span>
                                                </div>
                                                <div class="info-group-item">
                                                    <span class="info-label-block">Ngày check-in</span>
                                                    <span class="info-value-block">${req.checkInDate} 14:00</span>
                                                </div>
                                                <div class="info-group-item">
                                                    <span class="info-label-block">Ngày check-out</span>
                                                    <span class="info-value-block">${req.checkOutDate} 12:00</span>
                                                </div>
                                                <div class="info-group-item">
                                                    <span class="info-label-block">Số phòng</span>
                                                    <span class="info-value-block">${req.numRooms} Phòng</span>
                                                </div>
                                                <div class="info-group-item">
                                                    <span class="info-label-block">Tổng tiền đơn gốc</span>
                                                    <span class="info-value-block text-navy-bold">
                                                        <fmt:formatNumber value="${req.currentPrice}" type="number" /> đ
                                                    </span>
                                                </div>
                                                <div class="info-group-item"
                                                    style="grid-column: span 3; background-color: #f8fafc; padding: 10px; border-radius: 6px; border-left: 4px solid #cbd5e1; margin-top: 5px;">
                                                    <span class="info-label-block"
                                                        style="font-weight: 600; color: #475569; margin-bottom: 4px;">Lý
                                                        do từ khách hàng:</span>
                                                    <span class="info-value-block"
                                                        style="color: #1e293b; font-style: italic; white-space: pre-line;">
                                                        <c:out
                                                            value="${not empty req.requestDetails ? req.requestDetails : 'Không có lý do cụ thể.'}" />
                                                    </span>
                                                </div>
                                            </div>

                                            <c:choose>

                                                <%-- LOẠI 1: CHÍNH SÁCH HỦY ĐƠN --%>
                                                    <c:when test="${req.requestType == 'Hủy đặt phòng'}">
                                                        <h4 class="section-sub-title">Chính sách hủy phòng (Mốc tính
                                                            14:00 ngày ${req.checkInDate})</h4>

                                                        <p
                                                            style="margin-bottom: 12px; font-size: 13.5px; color: #475569;">
                                                            Thời gian tính từ lúc gửi đơn đến mốc 14:00 ngày check-in
                                                            còn lại: <strong id="lblHoursRemaining"
                                                                style="color: #0f172a;">${cancelDurationText}</strong>
                                                        </p>

                                                        <table class="policy-table"
                                                            style="width: 100%; border-collapse: collapse; margin-bottom: 20px; font-size: 14px; color: #334155;">
                                                            <thead>
                                                                <tr
                                                                    style="background-color: #f1f5f9; text-align: left; border-bottom: 2px solid #cbd5e1;">
                                                                    <th style="padding: 12px 10px;">Mốc thời gian gửi
                                                                        yêu cầu hủy</th>
                                                                    <th style="padding: 12px 10px; text-align: center;">
                                                                        Phí phạt hủy (% Tổng đơn)</th>
                                                                    <th style="padding: 12px 10px; text-align: center;">
                                                                        Mức hoàn trả tiền cọc</th>
                                                                </tr>
                                                            </thead>
                                                            <tbody>
                                                                <tr id="policy-row-72"
                                                                    style="border-bottom: 1px solid #e2e8f0; ${hoursLeft >= 72 ? 'background-color: #f1f5f9; font-weight: bold; color: #0f172a;' : ''}">
                                                                    <td style="padding: 12px 10px;">&gt;= 72h trước
                                                                        14:00 ngày check-in</td>
                                                                    <td style="padding: 12px 10px; text-align: center;">
                                                                        0%</td>
                                                                    <td style="padding: 12px 10px; text-align: center;">
                                                                        Hoàn 100% tiền cọc</td>
                                                                </tr>
                                                                <tr id="policy-row-48"
                                                                    style="border-bottom: 1px solid #e2e8f0; ${hoursLeft >= 48 && hoursLeft < 72 ? 'background-color: #f1f5f9; font-weight: bold; color: #0f172a;' : ''}">
                                                                    <td style="padding: 12px 10px;">&gt;= 48h - &lt; 72h
                                                                        trước 14:00 ngày check-in</td>
                                                                    <td style="padding: 12px 10px; text-align: center;">
                                                                        30%</td>
                                                                    <td style="padding: 12px 10px; text-align: center;">
                                                                        Hoàn 70% tiền cọc</td>
                                                                </tr>
                                                                <tr id="policy-row-24"
                                                                    style="border-bottom: 1px solid #e2e8f0; ${hoursLeft >= 24 && hoursLeft < 48 ? 'background-color: #f1f5f9; font-weight: bold; color: #0f172a;' : ''}">
                                                                    <td style="padding: 12px 10px;">&gt;= 24h - &lt; 48h
                                                                        trước 14:00 ngày check-in</td>
                                                                    <td style="padding: 12px 10px; text-align: center;">
                                                                        50%</td>
                                                                    <td style="padding: 12px 10px; text-align: center;">
                                                                        Hoàn 50% tiền cọc</td>
                                                                </tr>
                                                                <tr id="policy-row-0"
                                                                    style="border-bottom: 1px solid #e2e8f0; ${hoursLeft < 24 ? 'background-color: #f1f5f9; font-weight: bold; color: #0f172a;' : ''}">
                                                                    <td style="padding: 12px 10px;">&lt; 24h trước 14:00
                                                                        ngày check-in</td>
                                                                    <td style="padding: 12px 10px; text-align: center;">
                                                                        70%</td>
                                                                    <td style="padding: 12px 10px; text-align: center;">
                                                                        Hoàn 30% tiền cọc</td>
                                                                </tr>
                                                            </tbody>
                                                        </table>

                                                        <div class="calculation-box">
                                                            <h5 class="calc-title">Tính toán dòng tiền đối soát (Hệ
                                                                thống thực tế)</h5>
                                                            <div class="calc-grid"
                                                                style="font-size: 14px; color: #475569;">
                                                                <div style="margin-bottom: 8px;">
                                                                    <span class="calc-label">Tổng hóa đơn phòng
                                                                        gốc:</span>
                                                                    <strong id="lblTotalBooking"
                                                                        style="float: right; color: #0f172a;"><fmt:formatNumber value="${totalBookingValue}" type="number"/>
                                                                        VND</strong>
                                                                </div>
                                                                <div style="margin-bottom: 8px;">
                                                                    <span class="calc-label"
                                                                        style="color: #2563eb;">Tiền cọc thực tế khách
                                                                        đã đóng (30%):</span>
                                                                    <strong id="lblDepositValue"
                                                                        style="float: right; color: #2563eb;"><fmt:formatNumber value="${depositPaid}" type="number"/>
                                                                        VND</strong>
                                                                </div>
                                                                <div style="margin-bottom: 8px;">
                                                                    <span class="calc-label" style="color: #ef4444;">Phí
                                                                        hủy phạt khấu trừ:</span>
                                                                    <strong id="lblCancelFeeValue"
                                                                        style="float: right; color: #ef4444;"><fmt:formatNumber value="${cancelFeeAmount}" type="number"/>
                                                                        VND (Khấu trừ ${cancelFeePercentText} tiền cọc)</strong>
                                                                </div>
                                                                <div
                                                                    style="border-top: 1px solid #e2e8f0; padding-top: 8px; margin-top: 8px; font-weight: bold;">
                                                                    <span class="calc-label" style="color: #22c55e;">Số
                                                                        tiền cọc hoàn trả thực tế cho khách:</span>
                                                                    <strong id="lblFinalRefundValue"
                                                                        style="float: right; color: #22c55e; font-size: 15px;"><fmt:formatNumber value="${cancelRefundAmount}" type="number"/>
                                                                        VND</strong>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </c:when>

                                                    <%-- LOẠI 2: CHÍNH SÁCH ĐỔI HẠNG PHÒNG --%>
                                                        <c:when test="${req.requestType == 'Đổi hạng phòng'}">
                                                            <h4 class="section-sub-title">Kiểm tra thông tin đổi hạng
                                                                phòng</h4>
                                                            <table class="policy-table">
                                                                <thead>
                                                                    <tr>
                                                                        <th>Hạng phòng mong muốn</th>
                                                                        <th>Đơn giá mới (Dự kiến)</th>
                                                                        <th>Tình trạng phòng hệ thống</th>
                                                                    </tr>
                                                                </thead>
                                                                <tbody>
                                                                    <tr>
                                                                        <td><strong>${req.targetRoomTypeName}</strong>
                                                                        </td>
                                                                        <td>
                                                                            <fmt:formatNumber value="${req.targetPrice}"
                                                                                type="number" /> đ
                                                                        </td>
                                                                        <td>
                                                                            <c:choose>
                                                                                <c:when test="${isAvailable}">
                                                                                    <span class="text-available">✔ Còn
                                                                                        phòng trống</span>
                                                                                </c:when>
                                                                                <c:otherwise>
                                                                                    <span class="text-unavailable">❌ Hết
                                                                                        phòng</span>
                                                                                </c:otherwise>
                                                                            </c:choose>
                                                                        </td>
                                                                    </tr>
                                                                </tbody>
                                                            </table>
                                                        </c:when>

                                                        <%-- LOẠI 3: CHÍNH SÁCH GIA HẠN LƯU TRÚ (ĐÃ ĐƯỢC CSS LẠI ĐẸP NHƯ
                                                            ĐỔI HẠNG) --%>
                                                            <c:when test="${req.requestType == 'Gia hạn phòng'}">
                                                                <h4 class="section-sub-title">Thông tin gia hạn lưu trú
                                                                </h4>

                                                                <%-- Xử lý cắt chuỗi lấy nguyên phần ngày yyyy-MM-dd nếu
                                                                    requestedCheckout chứa ký tự 'T' --%>
                                                                    <c:set var="cleanedNewCheckout"
                                                                        value="${req.requestedCheckout}" />
                                                                    <c:if
                                                                        test="${fn:contains(req.requestedCheckout, 'T')}">
                                                                        <c:set var="cleanedNewCheckout"
                                                                            value="${fn:substringBefore(req.requestedCheckout, 'T')}" />
                                                                    </c:if>

                                                                    <table class="policy-table">
                                                                        <thead>
                                                                            <tr>
                                                                                <th>Ngày Check-out cũ</th>
                                                                                <th>Ngày Check-out mới mong muốn</th>
                                                                                <th>Tình trạng phòng hệ thống</th>
                                                                            </tr>
                                                                        </thead>
                                                                        <tbody>
                                                                            <tr>
                                                                                <td><span
                                                                                        style="color: #64748b;">${req.checkOutDate}</span>
                                                                                </td>
                                                                                <td><strong
                                                                                        style="color: #0050b3;">${cleanedNewCheckout}</strong>
                                                                                </td>
                                                                                <td>
                                                                                    <c:choose>
                                                                                        <c:when test="${isAvailable}">
                                                                                            <span
                                                                                                class="text-available">✔
                                                                                                Còn phòng trống để gia
                                                                                                hạn</span>
                                                                                        </c:when>
                                                                                        <c:otherwise>
                                                                                            <span
                                                                                                class="text-unavailable">❌
                                                                                                Không đủ phòng
                                                                                                trống</span>
                                                                                        </c:otherwise>
                                                                                    </c:choose>
                                                                                </td>
                                                                            </tr>
                                                                        </tbody>
                                                                    </table>
                                                            </c:when>
                                            </c:choose>

                                            <h4 class="section-sub-title" style="margin-top: 25px;">Xử lý yêu cầu</h4>
                                            <form action="process-request" method="POST" class="process-form">
                                                <input type="hidden" name="requestId" value="${req.requestId}">
                                                <input type="hidden" name="type" value="${currentType}">
                                                <input type="hidden" name="status" value="${currentStatus}">
                                                <input type="hidden" name="searchBookingCode"
                                                    value="${searchBookingCode}">

                                                <div class="action-form-layout">
                                                    <div class="form-group-textarea">
                                                        <label for="response_notes" class="form-label">Ghi chú </label>
                                                        <textarea id="response_notes" name="response_notes" rows="3"
                                                            class="form-control"
                                                            placeholder="Nhập lý do xử lý yêu cầu..."></textarea>
                                                    </div>

                                                    <div class="form-group-select">
                                                        <label for="action_select" class="form-label">Hành động</label>
                                                        <select id="action_select" name="action"
                                                            class="form-select-control" required>
                                                            <option value="" disabled selected>Chọn hành động</option>
                                                            <option value="approve" ${(!isAvailable &&
                                                                (req.requestType=='Đổi hạng phòng' ||
                                                                req.requestType=='Gia hạn phòng' )) ? 'disabled' : '' }>
                                                                Phê duyệt yêu cầu</option>
                                                            <option value="reject">Từ chối yêu cầu</option>
                                                        </select>
                                                        <button type="submit" id="submit_btn"
                                                            class="btn btn-primary btn-block-submit"
                                                            style="margin-top: 15px;">
                                                            Xác nhận xử lý
                                                        </button>
                                                    </div>
                                                </div>
                                            </form>
                                        </c:when>

                                        <c:otherwise>
                                            <div class="empty-detail-placeholder">
                                                <p>Vui lòng chọn một đơn từ danh sách bên trái để xem chi tiết thông tin
                                                    và xử lý.</p>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </div>
                </body>

                </html>