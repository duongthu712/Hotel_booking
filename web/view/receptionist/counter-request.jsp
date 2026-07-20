<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Xử lý yêu cầu tại quầy</title>

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/view/assets/css/counter-request.css?v=<%= System.currentTimeMillis() %>">
    </head>

    <body>
        <div class="counter-overlay">
            <div class="counter-popup">

                <button type="button"
                        class="counter-close"
                        onclick="window.parent.closeBookingPopup(false);">
                    ×
                </button>

                <div class="counter-title">
                    <h2>
                        <c:choose>
                            <c:when test="${requestType == 'extend'}">GIA HẠN NGÀY Ở</c:when>
                            <c:when test="${requestType == 'upgrade'}">THAY ĐỔI HẠNG PHÒNG</c:when>
                            <c:when test="${requestType == 'cancel'}">YÊU CẦU HỦY BOOKING</c:when>
                            <c:otherwise>YÊU CẦU KHÁC</c:otherwise>
                        </c:choose>
                    </h2>

                    <div class="title-line">
                        <span></span>
                    </div>
                </div>

                <c:if test="${not empty error}">
                    <div class="error-message">
                        ${error}
                    </div>
                </c:if>

                <div class="counter-summary">
                    <div class="summary-item">
                        <div class="summary-icon">▣</div>
                        <div>
                            <span>Mã booking</span>
                            <strong>${booking.bookingCode}</strong>
                        </div>
                    </div>

                    <div class="summary-item">
                        <div class="summary-icon">♙</div>
                        <div>
                            <span>Khách hàng</span>
                            <strong>${booking.guestName}</strong>
                        </div>
                    </div>

                    <div class="summary-item">
                        <div class="summary-icon">▤</div>
                        <div>
                            <span>Hạng phòng</span>
                            <strong>${booking.roomTypeName}</strong>
                        </div>
                    </div>

                    <div class="summary-item">
                        <div class="summary-icon">♚</div>
                        <div>
                            <span>Số lượng phòng</span>
                            <strong>${booking.numRooms} phòng</strong>
                        </div>
                    </div>

                    <div class="summary-item">
                        <div class="summary-icon">▣</div>
                        <div>
                            <span>Check-in</span>
                            <strong>${booking.checkinDateText}</strong>
                        </div>
                    </div>

                    <div class="summary-item">
                        <div class="summary-icon">▣</div>
                        <div>
                            <span>Check-out</span>
                            <strong>${booking.checkoutDateText}</strong>
                        </div>
                    </div>

                    <div class="summary-item">
                        <div class="summary-icon">▭</div>
                        <div>
                            <span>Thanh toán</span>
                            <strong>${booking.paymentStatus}</strong>
                        </div>
                    </div>

                    <div class="summary-item">
                        <div class="summary-icon">◇</div>
                        <div>
                            <span>Trạng thái</span>
                            <strong>${booking.bookingStatus}</strong>
                        </div>
                    </div>
                </div>

                <form method="post" action="${pageContext.request.contextPath}/counter-request">
                    <input type="hidden" name="bookingId" value="${booking.bookingId}">
                    <input type="hidden" name="requestType" value="${requestType}">

                    <c:choose>
                        <c:when test="${requestType == 'extend'}">
                            <div class="counter-two-col counter-section">
                                <section class="counter-card">
                                    <div class="section-title">THÔNG TIN HIỆN TẠI</div>

                                    <div class="info-line">
                                        <span>Check-in</span>
                                        <strong>${booking.checkinDateText}</strong>
                                    </div>

                                    <div class="info-line">
                                        <span>Check-out hiện tại</span>
                                        <strong>${booking.checkoutDateText}</strong>
                                    </div>

                                    <div class="info-line">
                                        <span>Số đêm hiện tại</span>
                                        <strong>${totalNights} đêm</strong>
                                    </div>

                                    <div class="info-line">
                                        <span>Đơn giá phòng</span>
                                        <strong>
                                            <fmt:formatNumber value="${booking.bookedPricePerNight}"
                                                              type="number"
                                                              groupingUsed="true"/> đ / đêm
                                        </strong>
                                    </div>

                                    <div class="info-line">
                                        <span>Số lượng phòng</span>
                                        <strong>${booking.numRooms} phòng</strong>
                                    </div>
                                </section>

                                <section class="counter-card"
                                         id="extendCalcBox"
                                         data-current-checkout="${booking.checkoutDateSql}"
                                         data-price="${booking.bookedPricePerNight}"
                                         data-num-rooms="${booking.numRooms}">
                                    <div class="section-title">THÔNG TIN GIA HẠN</div>

                                    <div class="form-row">
                                        <label>Ngày check-out mới</label>
                                        <input type="date"
                                               id="newCheckoutDateInput"
                                               name="newCheckoutDate"
                                               value="${empty newCheckoutDate ? defaultNewCheckoutDate : newCheckoutDate}">
                                    </div>

                                    <div class="info-line">
                                        <span>Số đêm gia hạn</span>
                                        <strong id="extraNightsText">+${extraNights} đêm</strong>
                                    </div>

                                    <div class="info-line">
                                        <span>Đơn giá phát sinh</span>
                                        <strong>
                                            <fmt:formatNumber value="${booking.bookedPricePerNight}"
                                                              type="number"
                                                              groupingUsed="true"/> đ / đêm
                                        </strong>
                                    </div>

                                    <div class="info-line">
                                        <span>Tổng tiền thêm</span>
                                        <strong class="money-text" id="estimatedExtraAmountText">
                                            <fmt:formatNumber value="${estimatedExtraAmount}"
                                                              type="number"
                                                              groupingUsed="true"/> đ
                                        </strong>
                                    </div>

                                    <div class="other-note">
                                        Phụ thu = đơn giá phòng hiện tại × số phòng × số đêm gia hạn.
                                    </div>
                                </section>
                            </div>
                        </c:when>

                        <c:when test="${requestType == 'upgrade'}">
                            <div class="counter-two-col counter-section">
                                <section class="counter-card">
                                    <div class="section-title">THÔNG TIN HIỆN TẠI</div>

                                    <div class="info-line">
                                        <span>Hạng phòng hiện tại</span>
                                        <strong>${booking.roomTypeName}</strong>
                                    </div>

                                    <div class="info-line">
                                        <span>Đơn giá hiện tại</span>
                                        <strong>
                                            <fmt:formatNumber value="${booking.bookedPricePerNight}"
                                                              type="number"
                                                              groupingUsed="true"/> đ / đêm
                                        </strong>
                                    </div>

                                    <div class="info-line">
                                        <span>Số lượng phòng</span>
                                        <strong>${booking.numRooms} phòng</strong>
                                    </div>

                                    <div class="info-line">
                                        <span>Số đêm cả booking</span>
                                        <strong>${totalNights} đêm</strong>
                                    </div>

                                    <div class="info-line">
                                        <span>Số đêm tính nâng hạng</span>
                                        <strong>${upgradeChargeableNights} đêm</strong>
                                    </div>

                                    <div class="info-line">
                                        <span>Tổng tiền hiện tại</span>
                                        <strong>
                                            <fmt:formatNumber value="${booking.estimatedRoomAmount}"
                                                              type="number"
                                                              groupingUsed="true"/> đ
                                        </strong>
                                    </div>

                                    <div class="other-note">
                                        Tổng chênh lệch = chênh lệch đơn giá × số phòng × số đêm còn áp dụng nâng hạng.
                                    </div>
                                </section>

                                <section class="counter-card">
                                    <div class="section-title">CHỌN HẠNG PHÒNG MỚI</div>

                                    <div class="room-option-list">
                                        <c:forEach var="rt" items="${availableRoomTypes}">
                                            <label class="room-option">
                                                <input type="radio"
                                                       name="targetRoomTypeId"
                                                       value="${rt.roomTypeId}">

                                                <div class="room-option-main">
                                                    <strong>${rt.typeName}</strong>
                                                    <span>
                                                        <fmt:formatNumber value="${rt.basePrice}"
                                                                          type="number"
                                                                          groupingUsed="true"/> đ / đêm
                                                    </span>
                                                </div>

                                                <div class="room-option-diff">
                                                    <c:choose>
                                                        <c:when test="${rt.priceDiff >= 0}">
                                                            +<fmt:formatNumber value="${rt.priceDiff}"
                                                                               type="number"
                                                                               groupingUsed="true"/> đ / đêm
                                                        </c:when>
                                                        <c:otherwise>
                                                            <fmt:formatNumber value="${rt.priceDiff}"
                                                                              type="number"
                                                                              groupingUsed="true"/> đ / đêm
                                                        </c:otherwise>
                                                    </c:choose>

                                                    <br>

                                                    <c:choose>
                                                        <c:when test="${rt.upgradeTotal >= 0}">
                                                            Tổng +<fmt:formatNumber value="${rt.upgradeTotal}"
                                                                                    type="number"
                                                                                    groupingUsed="true"/> đ
                                                        </c:when>
                                                        <c:otherwise>
                                                            Tổng <fmt:formatNumber value="${rt.upgradeTotal}"
                                                                                   type="number"
                                                                                   groupingUsed="true"/> đ
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </label>
                                        </c:forEach>
                                    </div>
                                </section>
                            </div>

                            <div class="counter-section">
                                <div class="upgrade-result-grid">
                                    <div class="upgrade-result-item">
                                        <span>Giá hiện tại</span>
                                        <strong>
                                            <fmt:formatNumber value="${booking.bookedPricePerNight}"
                                                              type="number"
                                                              groupingUsed="true"/> đ / đêm
                                        </strong>
                                    </div>

                                    <div class="upgrade-result-item">
                                        <span>Số phòng</span>
                                        <strong>${booking.numRooms} phòng</strong>
                                    </div>

                                    <div class="upgrade-result-item">
                                        <span>Số đêm tính nâng hạng</span>
                                        <strong>${upgradeChargeableNights} đêm</strong>
                                    </div>

                                    <div class="upgrade-result-item">
                                        <span>Chọn hạng mới</span>
                                        <strong>Xem chênh lệch ở danh sách</strong>
                                    </div>
                                </div>
                            </div>
                        </c:when>

                        <c:when test="${requestType == 'cancel'}">
                            <div class="counter-section counter-card">
                                <div class="section-title">THÔNG TIN HỦY BOOKING</div>

                                <div class="cancel-calc-grid"
                                     id="cancelCalcBox"
                                     data-total-rooms="${booking.numRooms}"
                                     data-deposit="${booking.depositAmount}"
                                     data-fee-rate="${cancelFeeRate}">

                                    <div class="cancel-calc-item">
                                        <span>Tổng số phòng đặt</span>
                                        <strong>${booking.numRooms} phòng</strong>
                                    </div>

                                    <div class="cancel-calc-item">
                                        <span>Tiền cọc hiện tại</span>
                                        <strong>
                                            <fmt:formatNumber value="${booking.depositAmount}" type="number" groupingUsed="true"/> đ
                                        </strong>
                                    </div>

                                    <div class="cancel-calc-item">
                                        <span>Cọc mỗi phòng</span>
                                        <strong>
                                            <fmt:formatNumber value="${depositPerRoom}" type="number" groupingUsed="true"/> đ
                                        </strong>
                                    </div>

                                    <div class="cancel-calc-item">
                                        <span>Thời gian trước check-in</span>
                                        <strong>${hoursBeforeCheckin} giờ</strong>
                                    </div>
                                </div>

                                <div class="form-row counter-section">
                                    <label>Số phòng muốn hủy</label>

                                    <select name="cancelRooms" id="cancelRoomsSelect">
                                        <c:forEach begin="1" end="${booking.numRooms}" var="n">
                                            <option value="${n}" ${n == selectedCancelRooms ? 'selected' : ''}>
                                                <c:choose>
                                                    <c:when test="${n == booking.numRooms}">
                                                        Hủy ${n} phòng - Hủy toàn bộ booking
                                                    </c:when>
                                                    <c:otherwise>
                                                        Hủy ${n} phòng, giữ lại ${booking.numRooms - n} phòng
                                                    </c:otherwise>
                                                </c:choose>
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <div class="cancel-calc-grid counter-section">
                                    <div class="cancel-calc-item">
                                        <span>Loại xử lý</span>
                                        <strong id="cancelTypeText">Hủy một phần booking</strong>
                                    </div>

                                    <div class="cancel-calc-item">
                                        <span>Cọc phần hủy</span>
                                        <strong id="cancelDepositText">0 đ</strong>
                                    </div>

                                    <div class="cancel-calc-item">
                                        <span>Phí hủy theo policy</span>
                                        <strong class="danger-text" id="cancelFeeText">0 đ</strong>
                                    </div>

                                    <div class="cancel-calc-item">
                                        <span>Tiền hoàn khách</span>
                                        <strong class="success-text" id="refundAmountText">0 đ</strong>
                                    </div>
                                </div>

                                <div class="cancel-warning">
                                    ${cancelPolicyText}
                                    Tiền hoàn = cọc phần hủy - phí hủy.
                                    Nếu hủy toàn bộ số phòng, booking sẽ chuyển sang trạng thái Đã hủy.
                                    Nếu hủy một phần, hệ thống chỉ giảm số lượng phòng của booking.
                                </div>

                                <c:if test="${booking.bookingStatus != 'Chờ xử lý' && booking.bookingStatus != 'Đã xác nhận'}">
                                    <div class="error-message">
                                        Booking này hiện không đủ điều kiện hủy tại quầy.
                                    </div>
                                </c:if>
                            </div>
                        </c:when>

                        <c:otherwise>
                            <div class="counter-two-col counter-section">
                                <section class="counter-card">
                                    <div class="section-title">THÔNG TIN YÊU CẦU</div>

                                    <div class="info-line">
                                        <span>Loại yêu cầu</span>
                                        <strong>Yêu cầu khác</strong>
                                    </div>

                                    <div class="info-line">
                                        <span>Ảnh hưởng tiền phòng</span>
                                        <strong>Không tự động cộng/trừ</strong>
                                    </div>

                                    <div class="info-line">
                                        <span>Trạng thái sau khi tạo</span>
                                        <strong>Đã phê duyệt</strong>
                                    </div>
                                </section>

                                <section class="counter-card">
                                    <div class="section-title">NỘI DUNG CHI TIẾT</div>

                                    <div class="other-note">
                                        Nhập nội dung yêu cầu ở phần ghi chú bên dưới. Yêu cầu này chỉ lưu lịch sử xử lý, không thay đổi ngày ở, hạng phòng hoặc trạng thái booking.
                                    </div>
                                </section>
                            </div>
                        </c:otherwise>
                    </c:choose>

                    <div class="counter-section">
                        <div class="section-title">
                            <c:choose>
                                <c:when test="${requestType == 'cancel'}">LÝ DO HỦY</c:when>
                                <c:otherwise>GHI CHÚ</c:otherwise>
                            </c:choose>
                        </div>

                        <textarea class="request-note"
                                  name="note"
                                  placeholder="Nhập ghi chú yêu cầu...">${note}</textarea>
                    </div>

                    <div class="counter-footer">
                        <button type="button"
                                class="btn-light"
                                onclick="window.parent.closeBookingPopup(false);">
                            Đóng
                        </button>

                        <button type="submit"
                                class="btn-main">
                            <c:choose>
                                <c:when test="${requestType == 'extend'}">Xác nhận gia hạn</c:when>
                                <c:when test="${requestType == 'upgrade'}">Xác nhận nâng hạng</c:when>
                                <c:when test="${requestType == 'cancel'}">Xác nhận hủy booking</c:when>
                                <c:otherwise>Xác nhận tạo yêu cầu</c:otherwise>
                            </c:choose>
                        </button>
                    </div>
                </form>

            </div>
        </div>
        <script>
            document.addEventListener("DOMContentLoaded", function () {
                initExtendCalc();
                initCancelCalc();
            });

            function initExtendCalc() {
                var extendBox = document.getElementById("extendCalcBox");
                var dateInput = document.getElementById("newCheckoutDateInput");

                if (!extendBox || !dateInput) {
                    return;
                }

                var currentCheckoutText = extendBox.getAttribute("data-current-checkout");
                var price = parseFloat(extendBox.getAttribute("data-price")) || 0;
                var numRooms = parseInt(extendBox.getAttribute("data-num-rooms")) || 1;

                var extraNightsText = document.getElementById("extraNightsText");
                var estimatedExtraAmountText = document.getElementById("estimatedExtraAmountText");

                function parseDate(value) {
                    if (!value) {
                        return null;
                    }

                    var parts = value.split("-");

                    if (parts.length !== 3) {
                        return null;
                    }

                    return new Date(
                            parseInt(parts[0], 10),
                            parseInt(parts[1], 10) - 1,
                            parseInt(parts[2], 10)
                            );
                }

                function formatVnd(value) {
                    value = Math.round(value);
                    return value.toLocaleString("vi-VN") + " đ";
                }

                function updateExtendCalc() {
                    var currentCheckout = parseDate(currentCheckoutText);
                    var newCheckout = parseDate(dateInput.value);

                    if (!currentCheckout || !newCheckout) {
                        extraNightsText.textContent = "+0 đêm";
                        estimatedExtraAmountText.textContent = "0 đ";
                        return;
                    }

                    var oneDay = 24 * 60 * 60 * 1000;
                    var extraNights = Math.round((newCheckout - currentCheckout) / oneDay);

                    if (extraNights < 0) {
                        extraNights = 0;
                    }

                    var totalExtra = price * numRooms * extraNights;

                    extraNightsText.textContent = "+" + extraNights + " đêm";
                    estimatedExtraAmountText.textContent = formatVnd(totalExtra);
                }

                dateInput.addEventListener("change", updateExtendCalc);
                dateInput.addEventListener("input", updateExtendCalc);

                updateExtendCalc();
            }

            function initCancelCalc() {
                var cancelBox = document.getElementById("cancelCalcBox");
                var cancelSelect = document.getElementById("cancelRoomsSelect");

                if (!cancelBox || !cancelSelect) {
                    return;
                }

                var totalRooms = parseInt(cancelBox.getAttribute("data-total-rooms")) || 1;
                var deposit = parseFloat(cancelBox.getAttribute("data-deposit")) || 0;
                var feeRate = parseFloat(cancelBox.getAttribute("data-fee-rate")) || 0;

                var cancelTypeText = document.getElementById("cancelTypeText");
                var cancelDepositText = document.getElementById("cancelDepositText");
                var cancelFeeText = document.getElementById("cancelFeeText");
                var refundAmountText = document.getElementById("refundAmountText");

                function formatVnd(value) {
                    value = Math.round(value);
                    return value.toLocaleString("vi-VN") + " đ";
                }

                function updateCancelCalc() {
                    var cancelRooms = parseInt(cancelSelect.value) || 1;

                    var depositPerRoom = deposit / totalRooms;
                    var cancelDeposit = depositPerRoom * cancelRooms;
                    var cancelFee = cancelDeposit * feeRate;
                    var refundAmount = cancelDeposit - cancelFee;

                    if (cancelRooms === totalRooms) {
                        cancelTypeText.textContent = "Hủy toàn bộ booking";
                    } else {
                        cancelTypeText.textContent = "Hủy " + cancelRooms + " phòng, giữ lại " + (totalRooms - cancelRooms) + " phòng";
                    }

                    cancelDepositText.textContent = formatVnd(cancelDeposit);
                    cancelFeeText.textContent = formatVnd(cancelFee);
                    refundAmountText.textContent = formatVnd(refundAmount);
                }

                cancelSelect.addEventListener("change", updateCancelCalc);
                updateCancelCalc();
            }
        </script>
    </body>
</html>