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

            <!-- HIỂN THỊ CẢNH BÁO LỖI BACK-END NẾU CÓ -->
            <c:if test="${param.status == 'over_capacity_error'}">
                <div style="background-color: #fff5f5; border: 1px solid #fed7d7; color: #c53030; padding: 15px; margin-bottom: 20px; border-radius: 6px; font-weight: bold;">
                    Lỗi hệ thống: Số lượng người lớn hoặc trẻ em vượt quá tổng sức chứa của số phòng đã chọn! Vui lòng tăng số lượng phòng hoặc chọn hạng phòng khác lớn hơn.
                </div>
            </c:if>

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
                                    <span class="capacity-badge" data-adults="${rt.maxAdults}">${rt.maxAdults} Người lớn</span>
                                    <span class="capacity-badge" data-children="${rt.maxChildren}">${rt.maxChildren} Trẻ em</span>
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
                                                data-typename="${rt.roomTypeName}"
                                                data-baseprice="${rt.basePrice}"
                                                data-maxadults="${rt.maxAdults}"
                                                data-maxchildren="${rt.maxChildren}">
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
                                        <label>Ngày sinh <span>*</span></label>
                                        <input type="date" id="dateOfBirth" name="dateOfBirth" required>
                                        <span id="dob-error-msg" class="form-error-inline"></span>
                                    </div>

                                    <!-- BỔ SUNG: Ô ĐIỀU CHỈNH SỐ PHÒNG, NGƯỜI LỚN VÀ TRẺ EM TẠI FORM -->
                                    <div class="form-group">
                                        <label>Số lượng phòng thuê <span>*</span></label>
                                        <input type="number" id="formNumRooms" name="numRooms" 
                                               value="${param.numRooms != null ? param.numRooms : 1}" 
                                               readonly 
                                               style="background-color: #e2e8f0; cursor: not-allowed;" required>
                                        <span id="rooms-error-msg" class="form-error-inline"></span>
                                    </div>
                                    <div class="form-group">
                                        <label>Số người lớn <span>*</span></label>
                                        <input type="number" id="numGuests" name="numGuests" value="1" min="1" required>
                                        <span id="guests-error-msg" class="form-error-inline"></span>
                                    </div>
                                    <div class="form-group">
                                        <label>Số trẻ em <span>*</span></label>
                                        <input type="number" id="numChildren" name="numChildren" value="0" min="0" required>
                                        <span id="children-error-msg" class="form-error-inline"></span>
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
                                    <!-- ĐỒNG BỘ 100% THÔNG TIN VÀ CẤU TRÚC NGÂN HÀNG VỚI ONLINE (KHÔNG CÓ UPLOAD) -->
                                    <div class="bank-card" style="display: flex; gap: 20px; text-align: left; background: #fff; padding: 20px; border: 1px dashed #ebd9b4; border-radius: 8px; margin-top: 15px;">

                                        <div class="bank-information" style="flex: 1;">
                                            <h2 style="font-family: 'Lora', serif !important; font-size: 15px; font-weight: 700; color: #1a446c; margin-bottom: 12px; text-transform: uppercase;">
                                                THÔNG TIN CHUYỂN KHOẢN
                                            </h2>
                                            <div class="bank-row" style="margin-bottom: 8px; font-size: 13.5px;">
                                                <span style="color: #718096;">Ngân hàng:</span> <strong>Vietcombank</strong>
                                            </div>
                                            <div class="bank-row" style="margin-bottom: 8px; font-size: 13.5px;">
                                                <span style="color: #718096;">Số tài khoản:</span> <strong>123456789999</strong>
                                            </div>
                                            <div class="bank-row" style="margin-bottom: 8px; font-size: 13.5px;">
                                                <span style="color: #718096;">Chủ tài khoản:</span> <strong>LA MER HOTEL</strong>
                                            </div>
                                            <div class="bank-row" style="margin-bottom: 8px; font-size: 13.5px;">
                                                <span style="color: #718096;">Nội dung chuyển khoản:</span> <strong id="qrMemo" style="color: #1a446c;">-</strong>
                                            </div>
                                            <div class="booking-code-note" style="font-size: 12px; color: #718096; line-height: 1.4; margin-top: 10px; padding-top: 8px; border-top: 1px dashed #ebd9b4;">
                                                <strong>Hướng dẫn:</strong> Hướng dẫn khách quét mã hoặc chuyển khoản đúng nội dung. Sau khi màn hình thiết bị của khách hoặc SMS ngân hàng báo nhận tiền thành công, lễ tân bấm nút xác nhận phía dưới để hoàn tất.
                                            </div>
                                        </div>

                                        <div class="qr-payment" style="width: 160px; text-align: center; display: flex; flex-direction: column; align-items: center; justify-content: center; border-left: 1px dashed #ebd9b4; padding-left: 20px;">
                                            <strong style="font-size: 12px; color: #1a446c; display: block; margin-bottom: 8px; text-transform: uppercase; letter-spacing: 0.5px;">
                                                QUÉT QR ĐỂ THANH TOÁN
                                            </strong>
                                            <div class="qr-image-box" style="width: 130px; height: 160px; display: flex; align-items: center; justify-content: center; background: #fff; border: 1px solid #ebd9b4; padding: 5px; border-radius: 4px;">
                                                <!-- ẢNH QR GẮN CỐ ĐỊNH THỐNG NHẤT VỚI ONLINE -->
                                                <img src="https://i.ibb.co/GQZ9XWjM/36eec7bc-5c63-4d80-881e-2bdabccbc226.png" 
                                                     alt="QR thanh toán" 
                                                     style="max-width: 100%; max-height: 100%; object-fit: contain;"
                                                     onerror="this.style.display='none'; document.getElementById('qrFallbackDesk').style.display='flex';">

                                                <div id="qrFallbackDesk" class="qr-fallback" style="display: none; font-size: 12px; font-weight: bold; color: #718096; text-align: center;">
                                                    QR<br>PAYMENT
                                                </div>
                                            </div>
                                        </div>
                                    </div>
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
                                <strong id="summaryRoomsAndNights">0 phòng x 0 đêm</strong>
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

                            <div id="stayNowNotice" style="display: none; font-size: 13px; color: #2f855a; font-weight: bold; background-color: #f0fff4; padding: 10px; border-radius: 4px; border: 1px solid #c6f6d5; margin-top: 10px;">
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