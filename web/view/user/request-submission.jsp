<%-- 
    Document   : request-submission
    Created on : May 27, 2026, 10:44:34 PM
    Author     : Minh Thu
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Gửi Yêu Cầu Chỉnh Sửa Đơn - La Mer Hotel</title>

        <!-- Đồng bộ file CSS hệ thống giao diện khách hàng -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/navbar.css?v=<%= System.currentTimeMillis() %>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/footer.css?v=<%= System.currentTimeMillis() %>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/walk-in-booking.css?v=<%=System.currentTimeMillis()%>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/guest-request.css?v=<%=System.currentTimeMillis()%>">

        <!-- Nhúng thư viện thông báo SweetAlert2 -->
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
        <script src="${pageContext.request.contextPath}/view/assets/javascript/guest-request.js?v=<%= System.currentTimeMillis() %>"></script>
    </head>
    <body>

        <!-- Đồng bộ thanh điều hướng chung của khách hàng -->
        <jsp:include page="/view/common/navbar.jsp"/>

        <main class="request-wrapper">
            <!-- TIÊU ĐỀ TRANG CHUẨN UI MẪU -->
            <div class="request-header-card">
                <h1>Gửi yêu cầu</h1>
                <p>Vui lòng chọn loại yêu cầu và cung cấp thông tin chi tiết để chúng tôi hỗ trợ bạn nhanh chóng.</p>

                <!-- THANH TÓM TẮT THÔNG TIN ĐƠN ĐẶT PHÒNG TRẢI NGANG (GRID LAYOUT) -->
                <div class="booking-mini-summary">
                    <!-- Cột 1: Thông tin khách hàng kết nối từ đối tượng Guest -->
                    <div class="summary-item">
                        <span>Khách đặt phòng</span>
                        <c:choose>
                            <c:when test="${booking.guest != null}">
                                <strong>${booking.guest.fullName}</strong>
                                <strong class="summary-subtext">${booking.guest.phone}</strong>
                            </c:when>
                            <c:otherwise>
                                <strong>Khách vãng lai</strong>
                                <strong class="summary-subtext">N/A</strong>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Cột 2: Mã đặt phòng và Số lượng phòng -->
                    <div class="summary-item">
                        <span>Thông tin mã đơn</span>
                        <strong class="text-gold">${booking.bookingCode}</strong>
                        <strong class="summary-subtext">Số lượng: ${booking.numRooms} phòng</strong>
                    </div>

                    <!-- Cột 3: Hạng phòng thêm hiện tại -->
                    <div class="summary-item">
                        <span>Phòng</span>
                        <strong>${booking.roomTypeName}</strong>
                    </div>

                    <!-- Cột 4: Mốc thời gian nhận phòng -->
                    <div class="summary-item">
                        <span>Ngày nhận phòng</span>
                        <strong>${booking.checkinDate}</strong>
                    </div>

                    <!-- Cột 5: Mốc thời gian trả phòng cũ -->
                    <div class="summary-item">
                        <span>Ngày trả phòng</span>
                        <strong id="lblOldCheckout">${booking.checkoutDate}</strong>
                    </div>

                    <!-- Cột 6: Trạng thái Yêu cầu hiển thị đồng bộ -->
                    <div class="summary-item">
                        <span>Yêu cầu chỉnh sửa</span>
                        <strong id="sumRequestType" class="text-gold">—</strong>
                    </div>
                </div>
            </div>

            <form action="${pageContext.request.contextPath}/guest-request" method="POST" id="requestForm">
                <!-- Các tham số ẩn đồng bộ khóa ngoại và truyền dữ liệu sang Servlet -->
                <input type="hidden" name="bookingId" value="${booking.bookingId}">
                <input type="hidden" name="guestId" value="${booking.guestId}">
                <input type="hidden" name="roomTypeId" value="${booking.roomTypeId}">
                <input type="hidden" name="numRooms" id="numRooms" value="${booking.numRooms}">
                <input type="hidden" name="checkInDate" value="${booking.checkinDate}">
                <input type="hidden" name="oldCheckoutDate" value="${booking.checkoutDate}">
                <input type="hidden" name="bookingCode" value="${booking.bookingCode}">
                <input type="hidden" id="oldBasePrice" value="${booking.bookedPricePerNight}">
                <input type="hidden" name="checkOutDate" value="${booking.checkoutDate}">
                
                <!-- BỔ SUNG QUAN TRỌNG: Giữ lại email để khi quay về trang booking-detail không bị mất dữ liệu tra cứu -->
                <input type="hidden" name="email" value="${param.email != null ? param.email : booking.guest.email}">

                <div class="request-layout">
                    <!-- KHỐI LỰA CHỌN M TRẬN LOẠI YÊU CẦU TRẢI RỘNG -->
                    <div>
                        <label class="section-label">Loại yêu cầu <span class="required">*</span></label>
                        <div class="request-type-options">
                            <label class="type-card-option">
                                <input type="radio" name="requestType" value="Đổi hạng phòng" checked>
                                <div class="card-content">
                                    <strong>Thay đổi hạng phòng</strong>
                                    <small>Yêu cầu thay đổi sang hạng phòng khác.</small>
                                </div>
                            </label>

                            <label class="type-card-option">
                                <input type="radio" name="requestType" value="Gia hạn phòng">
                                <div class="card-content">
                                    <strong>Gia hạn thời gian ở</strong>
                                    <small>Yêu cầu ở thêm hoặc rút ngắn thời gian trả phòng.</small>
                                </div>
                            </label>

                            <label class="type-card-option">
                                <input type="radio" name="requestType" value="Hủy đặt phòng">
                                <div class="card-content">
                                    <strong>Hủy đặt phòng</strong>
                                    <small>Yêu cầu hủy đơn đặt phòng hiện tại.</small>
                                </div>
                            </label>
                        </div>
                    </div>

                    <!-- CHI TIẾT CÁC TAB NỘI DUNG PHẲNG -->
                    <div class="dynamic-tab-content">

                        <!-- TAB 1: THAY ĐỔI HẠNG PHÒNG -->
                        <div id="tab-change-room" class="tab-pane">
                            <!-- HIỂN THỊ THÔNG BÁO LỖI HẾT PHÒNG KHI BACK-END KIỂM TRA THẤT BẠI TRẢ VỀ (TĨNH, KHÔNG DÙNG AJAX) -->
                            <c:if test="${param.status eq 'request_failed'}">
                                <div class="error-status-banner">
                                    Hạng phòng mong muốn hiện tại đã hết phòng trống trong khoảng thời gian này hoặc hệ thống xử lý gặp sự cố. Vui lòng chọn hạng phòng khác.
                                </div>
                            </c:if>
                            <c:if test="${param.status eq 'duplicate_pending_error'}">
                                <div class="error-status-banner">
                                    Đơn hàng hiện tại đang có một yêu cầu thay đổi khác nằm ở trạng thái "Chờ xử lý". Vui lòng chờ bộ phận Lễ tân duyệt đơn trước khi gửi yêu cầu tiếp theo.
                                </div>
                            </c:if>

                            <label class="section-label">Thay đổi hạng phòng</label>
                            <div class="tab-content-grid">
                                <div class="pane-left">
                                    <div class="form-group">
                                        <label>Hạng phòng hiện tại</label>
                                        <input type="text" value="${booking.roomTypeName}" readonly style="background-color: #f1f5f9;">
                                    </div>
                                    <div class="form-group">
                                        <label>Hạng phòng mong muốn <span class="required">*</span></label>
                                        <select name="targetRoomTypeId" id="targetRoomTypeId">
                                            <c:forEach items="${roomTypesList}" var="type">
                                                <option value="${type.roomTypeId}" data-price="${type.basePrice}">
                                                    ${type.typeName} (<fmt:formatNumber value="${type.basePrice}" type="number"/>đ/đêm)
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="info-price-table">
                                        <div class="price-table-row">
                                            <span>Giá phòng hiện tại (1 đêm):</span>
                                            <strong><fmt:formatNumber value="${booking.bookedPricePerNight}" type="number"/> VND</strong>
                                        </div>
                                        <div class="price-table-row">
                                            <span>Chi chi phí biến động ước tính:</span>
                                            <strong id="sumCostDiff" class="text-gold">0 đ</strong>
                                        </div>
                                    </div>
                                </div>
                                <div class="pane-right">
                                    <div class="form-group">
                                        <label>Lý do thay đổi <span class="required">*</span></label>
                                        <textarea name="reason_details" id="reasonDetailsChange" placeholder="Vui lòng nhập lý do thay đổi hạng phòng..." required></textarea>
                                    </div>
                                    <div class="info-alert-box">
                                        Lưu ý: Yêu cầu của bạn sẽ được gửi đến bộ phận lễ tân để kiểm tra tình trạng phòng trống thực tế trước khi phê duyệt. Hệ thống sẽ thông báo kết quả sau khi đối soát xong.
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- TAB 2: GIA HẠN / RÚT NGẮN THỜI GIAN Ở -->
                        <div id="tab-extend-stay" class="tab-pane" style="display: none;">
                            <!-- HIỂN THỊ THÔNG BÁO LỖI HẾT PHÒNG KHI GIA HẠN THẤT BẠI -->
                            <c:if test="${param.status eq 'request_failed'}">
                                <div class="error-status-banner">
                                    Khách sạn không còn đủ buồng trống cho hạng phòng hiện tại vào những đêm bạn muốn gia hạn ở thêm. Vui lòng chọn mốc thời gian khác.
                                </div>
                            </c:if>

                            <label class="section-label">Gia hạn thời gian ở</label>
                            <div class="tab-content-grid">
                                <div class="pane-left">
                                    <div class="form-group">
                                        <label>Ngày trả phòng hiện tại</label>
                                        <input type="text" value="${booking.checkoutDate}" readonly style="background-color: #f1f5f9;">
                                    </div>
                                    <div class="form-group">
                                        <label>Ngày trả phòng mới <span class="required">*</span></label>
                                        <input type="date" name="newCheckoutDate" id="newCheckoutDate">
                                    </div>
                                    <div class="info-price-table">
                                        <div class="price-table-row">
                                            <span>Đơn giá phòng gốc:</span>
                                            <strong><fmt:formatNumber value="${booking.bookedPricePerNight}" type="number"/> VND</strong>
                                        </div>
                                        <div class="price-table-row">
                                            <span>Chi phí phát sinh/giảm trừ:</span>
                                            <strong id="sumCostDiffExtend" class="text-gold">0 đ</strong>
                                        </div>
                                    </div>
                                </div>
                                <div class="pane-right">
                                    <div class="form-group">
                                        <label>Lý do gia hạn/rút ngắn <span class="required">*</span></label>
                                        <textarea name="reason_details_extend" id="reasonDetailsExtend" placeholder="Vui lòng cung cấp lý do điều chỉnh thời gian ở..."></textarea>
                                    </div>
                                    <div class="info-alert-box">
                                        Lưu ý: Việc gia hạn thời gian lưu trú phụ thuộc hoàn toàn vào quỹ phòng trống của khách sạn tại thời điểm bộ phận lễ tân tiến hành rà soát đơn của bạn.
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- TAB 3: HỦY ĐƠN ĐẶT PHÒNG -->
                        <div id="tab-cancel-booking" class="tab-pane" style="display: none;">
                            <label class="section-label text-danger">Thông tin hủy đặt phòng</label>
                            <div class="tab-content-grid">
                                <div class="pane-left">
                                    <div class="info-alert-box" style="background-color: #fff5f5; border-left: 4px solid #ef4444; color: #b91c1c; margin-bottom: 15px;">
                                        Chính sách hủy phòng: Phí hủy được tính dựa trên thời gian thực tế khách gửi đơn đối soát với mốc 14:00 ngày nhận phòng.
                                    </div>

                                    <!-- BẢNG CHÍNH SÁCH ĐỐI SOÁT CHUẨN UI MẪU -->
                                    <table class="policy-table" style="width: 100%; border-collapse: collapse; margin-bottom: 20px; font-size: 14px;">
                                        <thead>
                                            <tr style="background-color: #f1f5f9; text-align: left; border-bottom: 2px solid #cbd5e1;">
                                                <th style="padding: 10px;">Thời điểm hủy (trước 14:00 ngày check-in)</th>
                                                <th style="padding: 10px; text-align: center;">Phí hủy</th>
                                                <th style="padding: 10px; text-align: center;">Hoàn lại</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr id="policy-row-72" style="border-bottom: 1px solid #e2e8f0;">
                                                <td style="padding: 10px;">&gt;= 72h trước</td>
                                                <td style="padding: 10px; text-align: center; color: #22c55e; font-weight: 600;">0%</td>
                                                <td style="padding: 10px; text-align: center;">100%</td>
                                            </tr>
                                            <tr id="policy-row-48" style="border-bottom: 1px solid #e2e8f0;">
                                                <td style="padding: 10px;">&gt;= 48h - &lt; 72h</td>
                                                <td style="padding: 10px; text-align: center; color: #ea580c; font-weight: 600;">30%</td>
                                                <td style="padding: 10px; text-align: center;">70%</td>
                                            </tr>
                                            <tr id="policy-row-24" style="border-bottom: 1px solid #e2e8f0;">
                                                <td style="padding: 10px;">&gt;= 24h - &lt; 48h</td>
                                                <td style="padding: 10px; text-align: center; color: #ea580c; font-weight: 600;">50%</td>
                                                <td style="padding: 10px; text-align: center;">50%</td>
                                            </tr>
                                            <tr id="policy-row-0" style="border-bottom: 1px solid #e2e8f0;">
                                                <td style="padding: 10px;">&lt; 24h</td>
                                                <td style="padding: 10px; text-align: center; color: #ef4444; font-weight: 600;">70%</td>
                                                <td style="padding: 10px; text-align: center;">30%</td>
                                            </tr>
                                        </tbody>
                                    </table>

                                    <!-- HỘP THÔNG BÁO KẾT QUẢ ĐỐI SOÁT ĐỘNG -->
                                    <div id="cancelAlertStatus" class="info-alert-box" style="background-color: #f0fdf4; border-left: 4px solid #22c55e; color: #166534; margin-bottom: 20px;">
                                        <span id="cancelAlertText">Đang tính toán chính sách áp dụng...</span>
                                    </div>

                                    <!-- DỰ KIẾN HOÀN TIỀN -->
                                    <label class="section-label" style="font-size: 14px; margin-bottom: 10px;">Dự kiến hoàn tiền</label>
                                    <div class="info-price-table">
                                        <div class="price-table-row">
                                            <span>Tổng giá trị phòng:</span>
                                            <strong id="cancelTotalBooking"><fmt:formatNumber value="${booking.bookedPricePerNight * booking.numRooms}" type="number"/> VND</strong>
                                        </div>
                                        <div class="price-table-row">
                                            <span class="text-danger">Phí hủy tính toán:</span>
                                            <strong id="cancelFeeValue" class="text-danger">0 VND</strong>
                                        </div>
                                        <div class="price-table-row" style="border-top: 1px solid #cbd5e1; padding-top: 10px;">
                                            <span style="color: #22c55e;">Tiền hoàn lại dự kiến:</span>
                                            <strong id="cancelRefundValue" style="color: #22c55e;">0 VND</strong>
                                        </div>
                                    </div>
                                </div>

                                <div class="pane-right">
                                    <div class="form-group">
                                        <label>Lý do hủy đơn phòng <span class="required">*</span></label>
                                        <textarea name="reason_details_cancel" id="reasonDetailsCancel" placeholder="Vui lòng nhập lý do hủy đặt phòng... (Ví dụ: Thay đổi kế hoạch công tác, việc cá nhân đột xuất...)" required></textarea>
                                    </div>
                                    <div class="form-group" style="display: flex; align-items: flex-start; gap: 10px; margin-top: 20px;">
                                        <input type="checkbox" id="chkPolicyAgree" style="width: auto; margin-top: 4px;" required>
                                        <label for="chkPolicyAgree" style="font-weight: normal; font-size: 13.5px; color: #475569; cursor: pointer;">
                                            Tôi đã đọc và hoàn toàn đồng ý với chính sách hủy phòng của khách sạn.
                                        </label>
                                    </div>
                                    <div class="info-alert-box" style="margin-top: 20px;">
                                        Lưu ý: Yêu cầu hủy của bạn sẽ được chuyển đến ban quản lý và bộ phận kế toán kiểm tra giao dịch đặt cọc gốc. Tiền hoàn trả thực tế sẽ được xử lý từ 3-7 ngày làm việc.
                                    </div>
                                </div>
                            </div>
                        </div>

                    </div>

                    <!-- KHỐI HÀNH ĐỘNG: NÚT SUBMIT CĂN GIỮA DƯỚI ĐÁY THEO ĐÚNG MẪU UI -->
                    <div class="form-actions">
                        <button type="submit" class="btn-submit-request">GỬI YÊU CẦU</button>
                    </div>
                </div>
            </form>
        </main>

        <!-- Đồng bộ chân trang chung của khách hàng -->
        <jsp:include page="/view/common/footer.jsp"/>
    </body>
</html>