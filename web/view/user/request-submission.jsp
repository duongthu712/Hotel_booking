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

        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/navbar.css?v=<%= System.currentTimeMillis() %>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/footer.css?v=<%= System.currentTimeMillis() %>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/guest-request.css?v=<%=System.currentTimeMillis()%>">

        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
        <script src="${pageContext.request.contextPath}/view/assets/javascript/guest-request.js?v=<%= System.currentTimeMillis() %>" charset="UTF-8"></script>
    </head>
    <body>

        <jsp:include page="/view/common/navbar.jsp"/>

        <main class="request-wrapper">
            <div class="request-header-card">
                <h1>Gửi yêu cầu</h1>
                <p>Vui lòng chọn loại yêu cầu và cung cấp thông tin chi tiết để chúng tôi hỗ trợ bạn nhanh chóng.</p>

                <div class="booking-mini-summary">
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

                    <div class="summary-item">
                        <span>Thông tin mã đơn</span>
                        <strong class="text-gold">${booking.bookingCode}</strong>
                        <strong class="summary-subtext">Số lượng: ${booking.numRooms} phòng</strong>
                    </div>

                    <div class="summary-item">
                        <span>Phòng</span>
                        <strong>${booking.roomTypeName}</strong>
                    </div>

                    <div class="summary-item">
                        <span>Ngày nhận phòng</span>
                        <strong>${booking.checkinDate}</strong>
                    </div>

                    <div class="summary-item">
                        <span>Ngày trả phòng</span>
                        <strong id="lblOldCheckout">${booking.checkoutDate}</strong>
                    </div>

                    <div class="summary-item">
                        <span>Yêu cầu chỉnh sửa</span>
                        <strong id="sumRequestType" class="text-gold">—</strong>
                    </div>
                </div>
            </div>

            <form action="${pageContext.request.contextPath}/guest-request" method="POST" id="requestForm">
                <input type="hidden" name="bookingId" value="${booking.bookingId}">
                <input type="hidden" name="guestId" value="${booking.guestId}">
                <input type="hidden" name="roomTypeId" value="${booking.roomTypeId}">
                <input type="hidden" name="numRooms" id="numRooms" value="${booking.numRooms}">

                <input type="hidden" name="checkInDate" id="checkInDate" value="${booking.checkinDate}">

                <input type="hidden" name="oldCheckoutDate" value="${booking.checkoutDate}">
                <input type="hidden" name="bookingCode" value="${booking.bookingCode}">
                <input type="hidden" id="oldBasePrice" value="${booking.bookedPricePerNight}">
                <input type="hidden" name="checkOutDate" value="${booking.checkoutDate}">
                <input type="hidden" id="bookingStatus" value="${booking.status}">
                <input type="hidden" name="email" value="${param.email != null ? param.email : booking.guest.email}">

                <div class="request-layout">
                    <div>
                        <label class="section-label">Loại yêu cầu <span class="required">*</span></label>
                        <div class="request-type-options">
                            <label class="type-card-option ${booking.status != 'Đã xác nhận' ? 'disabled-option' : ''}">
                                <input type="radio" name="requestType" value="Đổi hạng phòng"> <div class="card-content">
                                    <strong>Thay đổi hạng phòng</strong>
                                </div>
                            </label>

                            <label class="type-card-option ${booking.status == 'Đã trả phòng' || booking.status == 'Chờ xử lý' ? 'disabled-option' : ''}">
                                <input type="radio" name="requestType" value="Gia hạn phòng"> <div class="card-content">
                                    <strong>Gia hạn thời gian ở</strong>
                                </div>
                            </label>

                            <label class="type-card-option ${booking.status == 'Đã nhận phòng' || booking.status == 'Đã trả phòng' ? 'disabled-option' : ''}">
                                <input type="radio" name="requestType" value="Hủy đặt phòng"> <div class="card-content">
                                    <strong>Hủy đặt phòng</strong>
                                </div>
                            </label>
                        </div>
                    </div>

                    <div class="dynamic-tab-content">

                        <div id="tab-change-room" class="tab-pane">
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
                                            <span>Tổng số đêm lưu trú:</span>
                                            <strong id="summaryTotalNights">... đêm</strong>
                                        </div>
                                        <div class="price-table-row">
                                            <span>Tổng tiền đơn cũ:</span>
                                            <strong id="totalOldPrice">0 VND</strong>
                                        </div>
                                        <div class="price-table-row">
                                            <span>Tổng tiền đơn mới (dự kiến):</span>
                                            <strong id="totalNewPrice">0 VND</strong>
                                        </div>
                                        <div class="price-table-row" style="border-top: 1px dashed #cbd5e1; margin-top: 5px; padding-top: 5px;">
                                            <span>Chênh lệch cần thanh toán:</span>
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

                        <div id="tab-extend-stay" class="tab-pane" style="display: none;">
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
                                            <span>Đơn giá phòng gốc (1 đêm):</span>
                                            <strong><fmt:formatNumber value="${booking.bookedPricePerNight}" type="number"/> VND</strong>
                                        </div>
                                        <div class="price-table-row">
                                            <span>Số đêm hiện tại:</span>
                                            <strong id="summaryNightsOld">0 đêm</strong>
                                        </div>
                                        <div class="price-table-row">
                                            <span>Số đêm sau gia hạn:</span>
                                            <strong id="summaryNightsNew">0 đêm</strong>
                                        </div>
                                        <div class="price-table-row" style="border-top: 1px dashed #cbd5e1; margin-top: 5px; padding-top: 5px;">
                                            <span>Số đêm ở thêm:</span>
                                            <strong id="summaryNightsExtra" class="text-gold">0 đêm</strong>
                                        </div>
                                        <div class="price-table-row">
                                            <span>Chi phí phát sinh ước tính:</span>
                                            <strong id="sumCostDiffExtend" class="text-gold">0 đ</strong>
                                        </div>
                                    </div>
                                </div>
                                <div class="pane-right">
                                    <div class="form-group">
                                        <label>Lý do gia hạn <span class="required">*</span></label>
                                        <textarea name="reason_details_extend" id="reasonDetailsExtend" placeholder="Vui lòng cung cấp lý do điều chỉnh thời gian ở..."></textarea>
                                    </div>
                                    <div class="info-alert-box">
                                        Lưu ý: Việc gia hạn thời gian lưu trú phụ thuộc hoàn toàn vào quỹ phòng trống của khách sạn tại thời điểm bộ phận lễ tân tiến hành rà soát đơn của bạn.
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div id="tab-cancel-booking" class="tab-pane" style="display: none;">
                            <label class="section-label text-danger">Thông tin hủy đặt phòng</label>
                            <div class="tab-content-grid">
                                <div class="pane-left">
                                    <!-- Dòng hiển thị số giờ còn lại minh bạch, tinh tế -->
                                    <p style="margin-bottom: 12px; font-size: 13.5px; color: #475569;">
                                        Thời gian tính từ lúc gửi đơn đến mốc 14:00 ngày check-in còn lại: <strong id="lblHoursRemaining" style="color: #0f172a;">— giờ</strong>
                                    </p>

                                    <table class="policy-table" style="width: 100%; border-collapse: collapse; margin-bottom: 20px; font-size: 14px; color: #334155;">
                                        <table class="policy-table" style="width: 100%; border-collapse: collapse; margin-bottom: 20px; font-size: 14px; color: #334155;">
                                            <thead>
                                                <tr style="background-color: #f1f5f9; text-align: left; border-bottom: 2px solid #cbd5e1;">
                                                    <th style="padding: 12px 10px;">Mốc thời gian gửi yêu cầu hủy</th>
                                                    <th style="padding: 12px 10px; text-align: center;">Phí phạt hủy (% Tổng đơn)</th>
                                                    <th style="padding: 12px 10px; text-align: center;">Mức hoàn trả tiền cọc</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <tr id="policy-row-72" style="border-bottom: 1px solid #e2e8f0;">
                                                    <td style="padding: 12px 10px;">&gt;= 72h trước 14:00 ngày check-in</td>
                                                    <td style="padding: 12px 10px; text-align: center;">0%</td>
                                                    <td style="padding: 12px 10px; text-align: center;">Hoàn 100% tiền cọc</td>
                                                </tr>
                                                <tr id="policy-row-48" style="border-bottom: 1px solid #e2e8f0;">
                                                    <td style="padding: 12px 10px;">&gt;= 48h - &lt; 72h trước 14:00 ngày check-in</td>
                                                    <td style="padding: 12px 10px; text-align: center;">30%</td>
                                                    <td style="padding: 12px 10px; text-align: center;">Hoàn 70% tiền cọc</td>
                                                </tr>
                                                <tr id="policy-row-24" style="border-bottom: 1px solid #e2e8f0;">
                                                    <td style="padding: 12px 10px;">&gt;= 24h - &lt; 48h trước 14:00 ngày check-in</td>
                                                    <td style="padding: 12px 10px; text-align: center;">50%</td>
                                                    <td style="padding: 12px 10px; text-align: center;">Hoàn 50% tiền cọc</td>
                                                </tr>
                                                <tr id="policy-row-0" style="border-bottom: 1px solid #e2e8f0;">
                                                    <td style="padding: 12px 10px;">&lt; 24h trước 14:00 ngày check-in</td>
                                                    <td style="padding: 12px 10px; text-align: center;">70%</td>
                                                    <td style="padding: 12px 10px; text-align: center;">Hoàn 30% tiền cọc</td>
                                                </tr>
                                            </tbody>
                                        </table>

                                        <label class="section-label" style="font-size: 14px; margin-bottom: 10px;">Dự kiến hoàn tiền cọc</label>
                                        <div class="info-price-table">
                                            <div class="price-table-row">
                                                <span>Tổng giá trị phòng:</span>
                                                <strong id="cancelTotalBooking">0 VND</strong>
                                            </div>
                                            <div class="price-table-row">
                                                <span style="color: #2563eb;">Tiền cọc thực tế đã đóng (30%):</span>
                                                <strong id="cancelDepositValue" style="color: #2563eb;">0 VND</strong>
                                            </div>
                                            <div class="price-table-row">
                                                <span class="text-danger">Phí hủy tính toán (phạt dựa trên tổng đơn):</span>
                                                <strong id="cancelFeeValue" class="text-danger">0 VND</strong>
                                            </div>
                                            <div class="price-table-row" style="border-top: 1px solid #cbd5e1; padding-top: 10px;">
                                                <span id="lblRefundStatusText" style="color: #22c55e; font-weight: 600;">Tiền cọc hoàn lại dự kiến:</span>
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

                    <div class="form-actions">
                        <button type="submit" class="btn-submit-request">GỬI YÊU CẦU</button>
                    </div>
                </div>
            </form>
        </main>

        <jsp:include page="/view/common/footer.jsp"/>

        <c:if test="${not empty param.status}">
            <script>
                document.addEventListener("DOMContentLoaded", function () {
                    const status = "${param.status}";

                    const statusConfigs = {
                        "cancel_failed": {
                            title: "Hủy đơn thất bại",
                            text: "Hệ thống xử lý cơ sở dữ liệu gặp sự cố bất ngờ. Vui lòng kiểm tra lại lý do hủy hoặc thử lại sau.",
                            icon: "error"
                        },
                        "request_success": {
                            title: "Gửi yêu cầu thành công!",
                            text: "Hệ thống đã ghi nhận đơn chỉnh sửa của bạn. Bộ phận Lễ tân sẽ rà soát và xử lý trong thời gian sớm nhất.",
                            icon: "success"
                        },
                        "duplicate_pending_error": {
                            title: "Yêu cầu đang chờ xử lý",
                            text: "Đơn hàng này đã có một yêu cầu khác đang nằm trong danh sách chờ duyệt của Lễ tân.",
                            icon: "warning"
                        },
                        "request_failed_no_room": {
                            title: "Hết phòng trống",
                            text: "Hạng phòng hoặc khoảng thời gian bạn chọn hiện tại hệ thống đã hết buồng trống.",
                            icon: "error"
                        },
                        "request_failed": {
                            title: "Gửi thất bại",
                            text: "Hệ thống xử lý gặp sự cố bất ngờ. Vui lòng kiểm tra lại thông tin.",
                            icon: "error"
                        },
                        "system_error": {
                            title: "Lỗi hệ thống",
                            text: "Vui lòng liên hệ bộ phận hỗ trợ kỹ thuật để được trợ giúp.",
                            icon: "error"
                        }
                    };

                    if (statusConfigs[status]) {
                        Swal.fire({
                            ...statusConfigs[status],
                            confirmButtonColor: '#2c3e46'
                        }).then(() => {
                            const cleanUrl = window.location.protocol + "//" + window.location.host + window.location.pathname + window.location.search.replace(/([\?&])status=[^&]*(&|$)/, '$1').replace(/[\?&]$/, '');
                            window.history.replaceState({}, document.title, cleanUrl);
                        });
                    }
                });
            </script>
        </c:if>
    </body>
</html>