<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Chi tiết booking</title>

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/view/assets/css/staff-booking-popup.css?v=<%= System.currentTimeMillis() %>">
    </head>

    <body>
        <div class="detail-overlay">
            <div class="detail-popup">

                <button type="button" class="close-btn"
                        onclick="window.parent.closeBookingDetailPopup();">
                    ×
                </button>

                <div class="popup-title">
                    <h2>CHI TIẾT BOOKING</h2>
                    <div class="title-line">
                        <span></span>
                    </div>
                </div>

                <div class="summary-grid">
                    <div class="summary-card">
                        <div class="summary-icon">⌘</div>
                        <div>
                            <span>Mã booking</span>
                            <strong>${bookingDetail.bookingCode}</strong>
                        </div>
                    </div>

                    <div class="summary-card">
                        <div class="summary-icon blue">●</div>
                        <div>
                            <span>Trạng thái</span>
                            <strong>${bookingDetail.bookingStatus}</strong>
                        </div>
                    </div>

                    <div class="summary-card">
                        <div class="summary-icon blue">▣</div>
                        <div>
                            <span>Thanh toán</span>
                            <strong>${bookingDetail.paymentStatus}</strong>
                        </div>
                    </div>

                    <div class="summary-card">
                        <div class="summary-icon">◎</div>
                        <div>
                            <span>Nguồn</span>
                            <strong>${bookingDetail.source}</strong>
                        </div>
                    </div>
                </div>

                <section class="detail-section">
                    <div class="section-title">
                        THÔNG TIN KHÁCH HÀNG
                    </div>

                    <div class="info-box three-cols">
                        <div class="info-item">
                            <span>Khách hàng</span>
                            <strong>${bookingDetail.guestName}</strong>
                        </div>

                        <div class="info-item">
                            <span>SĐT</span>
                            <strong>${bookingDetail.guestPhone}</strong>
                        </div>

                        <div class="info-item">
                            <span>Email</span>
                            <strong>${bookingDetail.guestEmail}</strong>
                        </div>
                    </div>
                </section>

                <section class="detail-section">
                    <div class="section-title">
                        THÔNG TIN LƯU TRÚ
                    </div>

                    <div class="info-box stay-grid">
                        <div class="info-item">
                            <span>Hạng phòng</span>
                            <strong>${bookingDetail.roomTypeName}</strong>
                        </div>

                        <div class="info-item">
                            <span>Số lượng đặt</span>
                            <strong>${bookingDetail.numRooms} phòng</strong>
                        </div>

                        <div class="info-item">
                            <span>Số phòng</span>
                            <strong>${bookingDetail.roomNumbers}</strong>
                        </div>

                        <div class="info-item">
                            <span>Số khách</span>
                            <strong>
                                ${bookingDetail.numGuests} người
                                <c:if test="${bookingDetail.numChildren > 0}">
                                    , ${bookingDetail.numChildren} trẻ em
                                </c:if>
                            </strong>
                        </div>
                    </div>
                </section>

                <section class="detail-section">
                    <div class="section-title">
                        THỜI GIAN LƯU TRÚ
                    </div>

                    <div class="info-box stay-grid">
                        <div class="info-item">
                            <span>Check-in dự kiến theo đơn</span>
                            <strong>${bookingDetail.checkinDateText}</strong>
                        </div>

                        <div class="info-item">
                            <span>Check-out dự kiến theo đơn</span>
                            <strong>${bookingDetail.checkoutDateText}</strong>
                        </div>

                        <div class="info-item">
                            <span>Check-in thực tế</span>
                            <strong>
                                <c:choose>
                                    <c:when test="${not empty bookingDetail.actualCheckinTime}">
                                        ${bookingDetail.actualCheckinTime}
                                    </c:when>
                                    <c:otherwise>-</c:otherwise>
                                </c:choose>
                            </strong>
                        </div>

                        <div class="info-item">
                            <span>Check-out thực tế</span>
                            <strong>
                                <c:choose>
                                    <c:when test="${not empty bookingDetail.actualCheckoutTime}">
                                        ${bookingDetail.actualCheckoutTime}
                                    </c:when>
                                    <c:otherwise>-</c:otherwise>
                                </c:choose>
                            </strong>
                        </div>
                    </div>
                </section>

                <div class="two-column">
                    <section class="detail-section">
                        <div class="section-title">
                            GIÁ & THANH TOÁN
                        </div>

                        <div class="info-box price-box">
                            <div class="info-item">
                                <span>Đơn giá</span>
                                <strong>
                                    <fmt:formatNumber value="${bookingDetail.bookedPricePerNight}" type="number" groupingUsed="true"/> đ / đêm
                                </strong>
                            </div>

                            <div class="info-item">
                                <span>Tiền cọc</span>
                                <strong>
                                    <fmt:formatNumber value="${bookingDetail.depositAmount}" type="number" groupingUsed="true"/> đ
                                </strong>
                            </div>

                            <div class="info-item">
                                <span>Tổng tạm tính</span>
                                <strong>
                                    <fmt:formatNumber value="${bookingDetail.estimatedTotal}" type="number" groupingUsed="true"/> đ
                                </strong>
                            </div>
                        </div>
                    </section>

                    <section class="detail-section">
                        <div class="section-title">
                            YÊU CẦU / THAY ĐỔI GẦN ĐÂY
                        </div>

                        <div class="request-box">
                            <c:choose>
                                <c:when test="${empty recentRequests}">
                                    <div class="empty-request">
                                        Chưa có yêu cầu thay đổi nào.
                                    </div>
                                </c:when>

                                <c:otherwise>
                                    <c:forEach var="r" items="${recentRequests}">
                                        <div class="request-item">
                                            <div class="request-top">
                                                <span class="request-type">
                                                    ${r.requestType}
                                                </span>

                                                <span class="request-status" data-status="${r.requestStatus}">
                                                    ${r.requestStatus}
                                                </span>

                                                <span class="request-date">
                                                    ${r.submittedAtText}
                                                </span>
                                            </div>

                                            <div class="request-detail">
                                                ${r.requestDetails}
                                            </div>

                                            <c:if test="${not empty r.requestedCheckinText and not fn:contains(r.requestedCheckinText, '1970')}">
                                                <div class="request-sub">
                                                    Check-in yêu cầu: ${r.requestedCheckinText}
                                                </div>
                                            </c:if>

                                            <c:if test="${not empty r.requestedCheckoutText and not fn:contains(r.requestedCheckoutText, '1970')}">
                                                <div class="request-sub">
                                                    Check-out yêu cầu: ${r.requestedCheckoutText}
                                                </div>
                                            </c:if>

                                            <c:if test="${not empty r.targetRoomTypeName}">
                                                <div class="request-sub">
                                                    Hạng phòng muốn đổi: ${r.targetRoomTypeName}
                                                </div>
                                            </c:if>

                                            <c:if test="${not empty r.responseNotes}">
                                                <div class="response-note">
                                                    Phản hồi: ${r.responseNotes}
                                                </div>
                                            </c:if>
                                        </div>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </section>
                </div>

                <div class="popup-footer">
                    <button type="button" class="btn-light"
                            onclick="window.parent.closeBookingDetailPopup();">
                        Đóng
                    </button>
                </div>

            </div>
        </div>
    </body>
</html>