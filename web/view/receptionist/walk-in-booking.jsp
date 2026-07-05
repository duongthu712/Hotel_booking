<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/view/staff/header.jsp" />
        <title>Đặt Phòng Tại Quầy - La Mer Hotel</title>
        <link href="${pageContext.request.contextPath}/view/assets/css/walk-in-booking.css?v=<%=System.currentTimeMillis()%>" rel="stylesheet" type="text/css">
        <script src="${pageContext.request.contextPath}/view/assets/javascript/booking-calendar.js" defer></script>
        <script src="${pageContext.request.contextPath}/view/assets/javascript/walk-in-booking.js" defer></script>
    </head>
    <body>
        <jsp:include page="/view/staff/navbar.jsp" />

        <div class="walkin-container">

            <!-- CARD TÌM KIẾM PHÒNG TRỐNG NÂNG CAO -->
            <div class="walkin-search-card">
                <h3 class="walkin-title">Tìm Phòng Trống</h3>
                <form action="${pageContext.request.contextPath}/walk-in-booking" method="GET" class="walkin-search-form">

                    <div class="walkin-search-field">
                        <label for="checkIn">Ngày đến</label>
                        <input type="date" id="checkIn" name="checkInDate" value="${param.checkInDate}" required>
                    </div>

                    <div class="walkin-search-field">
                        <label for="checkOut">Ngày đi</label>
                        <input type="date" id="checkOut" name="checkOutDate" value="${param.checkOutDate}" required>
                    </div>

                    <div class="walkin-search-field">
                        <label for="roomTypeId">Hạng phòng</label>
                        <select id="roomTypeId" name="roomTypeId">
                            <option value="0">Tất cả loại phòng</option>
                            <c:forEach items="${dropdownRoomTypes}" var="type">
                                <option value="${type.roomTypeId}" ${param.roomTypeId == type.roomTypeId ? 'selected' : ''}>
                                    ${type.roomTypeName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="walkin-search-field">
                        <label for="numRooms">Số phòng cần thuê</label>
                        <input type="number" id="searchNumRooms" name="numRooms" value="${param.numRooms != null ? param.numRooms : 1}" min="1" max="20" required>
                    </div>

                    <button type="submit" class="walkin-search-btn">Tìm kiếm</button>
                </form>
            </div>

            <!-- BẢNG KẾT QUẢ DANH SÁCH HẠNG PHÒNG KHẢ DỤNG -->
            <h3 class="walkin-title" style="margin-top: 20px;">Danh Sách Hạng Phòng</h3>
            <table class="walkin-result-table">
                <thead>
                    <tr>
                        <th style="width: 25%;">Hạng phòng</th>
                        <th style="width: 35%;">Mô tả & Sức chứa</th>
                        <th style="width: 15%;">Giá/đêm</th>
                        <c:if test="${isSearching}">
                            <th style="width: 13%;">Trống</th>
                            <th style="width: 12%;">Hành động</th>
                        </c:if>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${allRoomTypes}" var="rt">
                        <tr>
                            <td><strong class="room-type-name">${rt.roomTypeName}</strong></td>
                            <td>
                                <div class="room-capacity-badges">
                                    <span class="capacity-badge">${rt.maxAdults} Người lớn</span>
                                    <span class="capacity-badge">${rt.maxChildren} Trẻ em</span>
                                </div>
                            </td>
                            <td class="room-price">
                                <fmt:formatNumber value="${rt.basePrice}" type="number" groupingUsed="true"/>đ
                            </td>

                            <c:if test="${isSearching}">
                                <td>
                                    <c:choose>
                                        <c:when test="${rt.availableRoomsCount <= 0}">
                                            <span class="status-empty">Hết phòng</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="status-available">${rt.availableRoomsCount} phòng trống</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:if test="${rt.availableRoomsCount > 0}">
                                        <button type="button" 
                                                class="walkin-action-btn select-room-btn"
                                                data-typeid="${rt.roomTypeId}"
                                                data-typename="${rt.roomTypeName}">
                                            Đặt ngay
                                        </button>
                                    </c:if>
                                </td>
                            </c:if>
                        </tr>
                    </c:forEach>

                    <c:if test="${empty allRoomTypes}">
                        <tr>
                            <td colspan="${isSearching ? 5 : 3}" style="text-align: center; color: #888; padding: 30px;">
                                Không tìm thấy hạng phòng nào đáp ứng đủ số lượng phòng trống yêu cầu.
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>

            <!-- FORM ĐẶT PHÒNG TẠI QUẦY & THANH TOÁN TÍCH HỢP -->
            <div id="quickBookingSection" style="display: none; width: 100%;">

                <div class="booking-progress">
                    <div class="progress-item active">
                        <div class="progress-name">XỬ LÝ LẬP ĐƠN ĐẶT PHÒNG TẠI QUẦY</div>
                    </div>
                </div>

                <form action="${pageContext.request.contextPath}/walk-in-booking" method="POST" id="walkInForm">
                    <!-- Dữ liệu nguồn động ẩn chuyển giao cho Servlet xử lý Backend -->
                    <input type="hidden" id="formRoomTypeId" name="roomTypeId">
                    <input type="hidden" id="formBasePrice" name="basePrice">
                    <input type="hidden" name="checkInDate" value="${param.checkInDate}">
                    <input type="hidden" name="checkOutDate" value="${param.checkOutDate}">
                    <input type="hidden" id="numRooms" name="numRooms" value="${param.numRooms != null ? param.numRooms : 1}">

                    <div class="booking-layout">

                        <!-- VÙNG KHUNG BÊN TRÁI: KHÁCH HÀNG & PHƯƠNG THỨC XÁC MINH -->
                        <div class="booking-left">
                            <section class="booking-section">
                                <div class="summary-title">
                                    <div class="progress-number" style="width:30px; height:30px; font-size:14px;">1</div>
                                    <h2 style="margin-left: 10px;">THÔNG TIN KHÁCH HÀNG</h2>
                                </div>

                                <div class="form-grid">
                                    <div class="form-group">
                                        <label>Họ và tên khách hàng <span>*</span></label>
                                        <input type="text" id="fullName" name="fullName" required placeholder="Nhập họ và tên khách">
                                        <span id="name-error-msg" class="form-error-inline"></span>
                                    </div>
                                    <div class="form-group">
                                        <label>Số điện thoại <span>*</span></label>
                                        <input type="tel" id="phone" name="phone" required maxlength="10" placeholder="Nhập số điện thoại">
                                        <span id="phone-error-msg" class="form-error-inline"></span>
                                    </div>
                                    <div class="form-group">
                                        <label>Email liên hệ <span>*</span></label>
                                        <input type="email" id="email" name="email" required placeholder="Nhập email liên hệ">
                                        <span id="email-error-msg" class="form-error-inline"></span>
                                    </div>
                                    <div class="form-group">
                                        <label>Số CMND/CCCD/Hộ chiếu</label>
                                        <input type="text" name="idNumber" placeholder="Nhập số giấy tờ tùy thân">
                                        <span id="idNumber-error-msg" class="form-error-inline"></span>
                                    </div>
                                    <div class="form-group">
                                        <label>Ngày sinh <span>*</span> <small>(Khách từ 18 tuổi)</small></label>
                                        <input type="date" id="dateOfBirth" name="dateOfBirth" required>
                                        <span id="dob-error-msg" class="form-error-inline"></span>
                                    </div>
                                </div>
                            </section>

                            <!-- KHỐI QUẢN LÝ TÀI CHÍNH (Tự động ẩn bằng JS nếu khách ở luôn hôm nay) -->
                            <section class="booking-section" id="financialVerificationSection">
                                <div class="summary-title">
                                    <div class="progress-number" style="width:30px; height:30px; font-size:14px; background-color:#1a446c;">2</div>
                                    <h2 style="margin-left: 10px;">XÁC MINH GIAO DỊCH ĐẶT CỌC (30%)</h2>
                                </div>

                                <div style="display: flex; gap: 35px; margin-bottom: 20px;">
                                    <label class="payment-method-label">
                                        <input type="radio" name="paymentMethod" value="Tiền mặt" checked onchange="togglePaymentView('cash')">
                                         Thu tiền mặt tại quầy
                                    </label>
                                    <label class="payment-method-label">
                                        <input type="radio" name="paymentMethod" value="Chuyển khoản" onchange="togglePaymentView('qr')">
                                         Khách quét mã VietQR cọc
                                    </label>
                                </div>

                                <div id="cashPaymentGuide">
                                    <strong>Hướng dẫn lễ tân:</strong> Thu trực tiếp số tiền đặt cọc hiển thị ở mục "TIỀN CỌC CẦN THU" bên phải. Kiểm đếm chính xác tiền mặt trước khi lưu đơn.
                                </div>

                                <div id="qrPaymentGateway" style="display: none;">
                                    <p style="font-weight: bold; color: #2d3748;">Yêu cầu khách mở App ngân hàng quét mã QR để chuyển khoản tiền cọc:</p>
                                    <img id="vietQrCode" src="" alt="Mã QR VietQR Động">
                                    <p style="font-size: 12px; color: #718096; margin-top: 8px;">Nội dung bắt buộc: <span id="qrMemo" style="font-weight: bold; color: #3182ce;"></span></p>
                                </div>
                            </section>
                        </div>

                        <!-- VÙNG KHUNG BÊN PHẢI: HÓA ĐƠN TÓM TẮT & SUBMIT ĐƠN -->
                        <aside class="booking-summary">
                            <div class="summary-title">
                                <h2>TÓM TẮT ĐƠN HÀNG</h2>
                            </div>

                            <div class="summary-row">
                                <span>Hạng phòng:</span>
                                <strong id="summaryRoomType" style="color: #1a446c;">-</strong>
                            </div>
                            <div class="summary-row">
                                <span>Thời gian:</span>
                                <span style="font-size: 13px; font-weight: bold;">
                                    <c:choose>
                                        <c:when test="${not empty param.checkInDate && not empty param.checkOutDate}">
                                            <fmt:parseDate value="${param.checkInDate}" pattern="yyyy-MM-dd" var="parsedIn" />
                                            <fmt:parseDate value="${param.checkOutDate}" pattern="yyyy-MM-dd" var="parsedOut" />
                                            <fmt:formatDate value="${parsedIn}" pattern="dd/MM/yyyy" /> 
                                            đến 
                                            <fmt:formatDate value="${parsedOut}" pattern="dd/MM/yyyy" />
                                        </c:when>
                                        <c:otherwise>
                                            -
                                        </c:otherwise>
                                    </c:choose>
                                </span>
                            </div>
                            <div class="summary-row">
                                <span>Số lượng:</span>
                                <strong>${param.numRooms} phòng x <span id="summaryNights">0</span> đêm</strong>
                            </div>
                            <div class="summary-row">
                                <span>Đơn giá/đêm:</span>
                                <strong id="summaryPricePerNight">-</strong>
                            </div>
                            <div class="summary-row">
                                <span>Tổng tiền phòng:</span>
                                <strong id="summaryTotalAmount">0 đ</strong>
                            </div>

                            <div class="summary-divider"></div>

                            <div class="total-row" id="depositSummaryRow">
                                <span style="font-weight: bold; color: #1a446c;">TIỀN CỌC CẦN THU (30%):</span>
                                <strong id="summaryDepositAmount" style="font-size: 18px; color: #c92a2a;">0 đ</strong>
                            </div>

                            <div id="stayNowNotice" style="display: none;">
                                Khách ở luôn hôm nay. Không thu cọc trước, trả 100% tiền phòng khi làm thủ tục Checkout trả phòng.
                            </div>

                            <button type="submit" class="walkin-search-btn" style="width: 100%; margin-top: 20px;">
                                XÁC NHẬN LẬP ĐƠN ĐẶT PHÒNG
                            </button>
                        </aside>

                    </div>
                </form>
            </div>
        </div>
    </body>
</html>