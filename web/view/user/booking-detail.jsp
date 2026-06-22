<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <title>Tra cứu đơn đặt phòng - La Mer Hotel</title>

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/view/assets/css/navbar.css?v=<%= System.currentTimeMillis() %>">

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/view/assets/css/footer.css?v=<%= System.currentTimeMillis() %>">

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/view/assets/css/booking.css?v=<%= System.currentTimeMillis() %>">

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/view/assets/css/booking-detail.css?v=<%= System.currentTimeMillis() %>">
    </head>

    <body>
        <jsp:include page="/view/common/navbar.jsp"/>

        <main class="booking-detail-page">

            <section class="detail-search-section">
                <div class="detail-search-heading">
                    <h1>Tra cứu đơn đặt phòng</h1>

                    <p>
                        Nhập mã đặt phòng và email để xem chi tiết đơn của bạn.
                    </p>
                </div>

                <form action="${pageContext.request.contextPath}/booking-detail"
                      method="post"
                      class="detail-search-form"
                      novalidate>

                    <div class="detail-search-field">
                        <label for="bookingCode">Mã đặt phòng</label>

                        <input type="text"
                               id="bookingCode"
                               name="bookingCode"
                               maxlength="12"
                               value="${fn:escapeXml(bookingCode)}"
                               placeholder="Ví dụ: LMHB12AB34CD">
                    </div>

                    <div class="detail-search-field">
                        <label for="email">Email đặt phòng</label>

                        <input type="email"
                               id="email"
                               name="email"
                               maxlength="100"
                               value="${fn:escapeXml(email)}"
                               placeholder="example@gmail.com">
                    </div>

                    <button type="submit" class="detail-search-button">
                        TRA CỨU
                    </button>
                </form>
            </section>

            <c:if test="${not empty error}">
                <div class="detail-error-box">
                    <strong>Thông báo:</strong>
                    <c:out value="${error}"/>
                </div>
            </c:if>

            <c:if test="${searched and not empty booking}">
                <c:set var="proofSent"
                       value="${not empty verificationStatus
                                and verificationStatus ne 'Chưa gửi minh chứng'}"/>

                <c:set var="bookingConfirmed"
                       value="${booking.status eq 'Đã xác nhận'
                                or booking.status eq 'Đã nhận phòng'
                                or booking.status eq 'Đã trả phòng'}"/>

                <c:set var="checkedIn"
                       value="${booking.status eq 'Đã nhận phòng'
                                or booking.status eq 'Đã trả phòng'}"/>

                <c:set var="checkedOut"
                       value="${booking.status eq 'Đã trả phòng'}"/>

                <c:url var="cancelBookingUrl" value="/cancel-booking">
                    <c:param name="bookingCode" value="${booking.bookingCode}"/>
                    <c:param name="email" value="${email}"/>
                </c:url>

                <c:url var="paymentUrl" value="/booking-payment">
                    <c:param name="bookingCode" value="${booking.bookingCode}"/>
                    <c:param name="email" value="${email}"/>
                </c:url>

                <div class="detail-layout">

                    <div class="detail-left-column">

                        <section class="detail-card detail-booking-card">
                            <div class="detail-card-header">
                                <h2>THÔNG TIN ĐƠN ĐẶT PHÒNG</h2>

                                <c:choose>
                                    <c:when test="${booking.status eq 'Đã xác nhận'}">
                                        <span class="detail-status-badge success">
                                            Đã xác nhận
                                        </span>
                                    </c:when>

                                    <c:when test="${booking.status eq 'Đã nhận phòng'}">
                                        <span class="detail-status-badge success">
                                            Đã nhận phòng
                                        </span>
                                    </c:when>

                                    <c:when test="${booking.status eq 'Đã trả phòng'}">
                                        <span class="detail-status-badge completed">
                                            Đã trả phòng
                                        </span>
                                    </c:when>

                                    <c:when test="${booking.status eq 'Đã hủy'}">
                                        <span class="detail-status-badge cancelled">
                                            Đã hủy
                                        </span>
                                    </c:when>

                                    <c:otherwise>
                                        <span class="detail-status-badge waiting">
                                            Chờ xử lý
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <div class="detail-booking-meta">
                                <div class="detail-meta-item">
                                    <span>Mã đặt phòng</span>

                                    <strong class="detail-booking-code">
                                        <c:out value="${booking.bookingCode}"/>
                                    </strong>
                                </div>

                                <div class="detail-meta-item">
                                    <span>Ngày đặt</span>
                                    <strong>${createdAtText}</strong>
                                </div>

                                <div class="detail-meta-item">
                                    <span>Kênh đặt</span>

                                    <strong>
                                        <c:choose>
                                            <c:when test="${not empty booking.source}">
                                                <c:out value="${booking.source}"/>
                                            </c:when>

                                            <c:otherwise>
                                                Đặt phòng trực tuyến
                                            </c:otherwise>
                                        </c:choose>
                                    </strong>
                                </div>

                                <div class="detail-meta-item">
                                    <span>Trạng thái đặt phòng</span>

                                    <strong>
                                        <c:out value="${booking.status}"/>
                                    </strong>
                                </div>
                            </div>

                            <div class="detail-divider"></div>

                            <div class="detail-room-summary">
                                <div class="detail-room-image">
                                    <img src="${not empty roomType.imageUrl
                                                ? roomType.imageUrl[0]
                                                : 'https://placehold.co/600x400?text=La+Mer+Room'}"
                                         alt="${fn:escapeXml(roomType.typeName)}">
                                </div>

                                <div class="detail-room-content">
                                    <h3>
                                        <c:out value="${roomType.typeName}"/>
                                    </h3>

                                    <div class="detail-room-features">
                                        <span>
                                            <b>▣</b>
                                            ${booking.numRooms} phòng
                                        </span>

                                        <span>
                                            <b>♙</b>
                                            ${booking.numGuests} khách
                                        </span>

                                        <span>
                                            <b>▱</b>
                                            ${roomType.areaSqm} m²
                                        </span>

                                        <span>
                                            <b>▤</b>
                                            ${roomType.bedCount} x
                                            <c:out value="${roomType.bedType}"/>
                                        </span>
                                    </div>
                                </div>
                            </div>

                            <div class="detail-divider"></div>

                            <div class="detail-stay-grid">
                                <div class="detail-stay-item">
                                    <span>Ngày nhận phòng</span>
                                    <strong>${checkInText}</strong>
                                </div>

                                <div class="detail-stay-item">
                                    <span>Ngày trả phòng</span>
                                    <strong>${checkOutText}</strong>
                                </div>

                                <div class="detail-stay-item">
                                    <span>Số đêm</span>
                                    <strong>${numberOfNights} đêm</strong>
                                </div>

                                <div class="detail-stay-item">
                                    <span>Đơn giá</span>

                                    <strong>
                                        <fmt:formatNumber
                                            value="${booking.bookedPricePerNight}"
                                            type="number"
                                            maxFractionDigits="0"/> VND
                                    </strong>

                                    <small>/ phòng / đêm</small>
                                </div>

                                <div class="detail-stay-item detail-stay-total">
                                    <span>Tổng tiền</span>

                                    <strong>
                                        <fmt:formatNumber
                                            value="${totalAmount}"
                                            type="number"
                                            maxFractionDigits="0"/> VND
                                    </strong>
                                </div>
                            </div>

                            <c:if test="${booking.status eq 'Đã hủy'}">
                                <div class="detail-cancelled-notice">
                                    <span class="detail-cancelled-icon">×</span>

                                    <div>
                                        <strong>Đơn đặt phòng đã bị hủy</strong>

                                        <c:choose>
                                            <c:when test="${not empty booking.cancellationReason}">
                                                <p>
                                                    Lý do:
                                                <c:out value="${booking.cancellationReason}"/>
                                                </p>
                                            </c:when>

                                            <c:otherwise>
                                                <p>Không có lý do hủy được cung cấp.</p>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </c:if>
                        </section>

                        <div class="detail-information-grid">

                            <section class="detail-card detail-customer-card">
                                <div class="detail-section-title">
                                    <span class="detail-section-icon">♙</span>
                                    <h2>THÔNG TIN KHÁCH HÀNG</h2>
                                </div>

                                <div class="detail-data-list">
                                    <div class="detail-data-row">
                                        <span>Họ và tên</span>

                                        <strong>
                                            <c:choose>
                                                <c:when test="${not empty guest.fullName}">
                                                    <c:out value="${guest.fullName}"/>
                                                </c:when>

                                                <c:otherwise>Chưa cung cấp</c:otherwise>
                                            </c:choose>
                                        </strong>
                                    </div>

                                    <div class="detail-data-row">
                                        <span>Email</span>

                                        <strong>
                                            <c:choose>
                                                <c:when test="${not empty guest.email}">
                                                    <c:out value="${guest.email}"/>
                                                </c:when>

                                                <c:otherwise>Chưa cung cấp</c:otherwise>
                                            </c:choose>
                                        </strong>
                                    </div>

                                    <div class="detail-data-row">
                                        <span>Số điện thoại</span>

                                        <strong>
                                            <c:choose>
                                                <c:when test="${not empty guest.phone}">
                                                    <c:out value="${guest.phone}"/>
                                                </c:when>

                                                <c:otherwise>Chưa cung cấp</c:otherwise>
                                            </c:choose>
                                        </strong>
                                    </div>

                                    <div class="detail-data-row">
                                        <span>Ngày sinh</span>

                                        <strong>
                                            <c:choose>
                                                <c:when test="${not empty dateOfBirthText}">
                                                    <c:out value="${dateOfBirthText}"/>
                                                </c:when>

                                                <c:otherwise>Chưa cung cấp</c:otherwise>
                                            </c:choose>
                                        </strong>
                                    </div>

                                    <div class="detail-data-row">
                                        <span>CCCD/Hộ chiếu</span>

                                        <strong>
                                            <c:choose>
                                                <c:when test="${not empty guest.idNumber}">
                                                    <c:out value="${guest.idNumber}"/>
                                                </c:when>

                                                <c:otherwise>Chưa cung cấp</c:otherwise>
                                            </c:choose>
                                        </strong>
                                    </div>

                                    <div class="detail-data-row">
                                        <span>Quốc tịch</span>

                                        <strong>
                                            <c:choose>
                                                <c:when test="${not empty guest.nationality}">
                                                    <c:out value="${guest.nationality}"/>
                                                </c:when>

                                                <c:otherwise>Chưa cung cấp</c:otherwise>
                                            </c:choose>
                                        </strong>
                                    </div>
                                </div>
                            </section>

                            <section class="detail-card detail-payment-card">
                                <div class="detail-section-title">
                                    <span class="detail-section-icon">₫</span>
                                    <h2>THANH TOÁN &amp; ĐẶT CỌC</h2>
                                </div>

                                <div class="detail-data-list">
                                    <div class="detail-data-row">
                                        <span>Tổng tiền</span>

                                        <strong>
                                            <fmt:formatNumber
                                                value="${totalAmount}"
                                                type="number"
                                                maxFractionDigits="0"/> VND
                                        </strong>
                                    </div>

                                    <div class="detail-data-row">
                                        <span>Số tiền đặt cọc</span>

                                        <strong class="detail-price">
                                            <fmt:formatNumber
                                                value="${booking.depositAmount}"
                                                type="number"
                                                maxFractionDigits="0"/> VND
                                        </strong>
                                    </div>

                                    <div class="detail-data-row">
                                        <span>Phương thức thanh toán</span>
                                        <strong>Chuyển khoản ngân hàng</strong>
                                    </div>

                                    <div class="detail-data-row">
                                        <span>Trạng thái thanh toán</span>

                                        <c:choose>
                                            <c:when test="${booking.paymentStatus eq 'Đã thanh toán'}">
                                                <strong class="detail-text-success">
                                                    Đã thanh toán
                                                </strong>
                                            </c:when>

                                            <c:when test="${booking.paymentStatus eq 'Đã đặt cọc'}">
                                                <strong class="detail-text-success">
                                                    Đã đặt cọc
                                                </strong>
                                            </c:when>

                                            <c:otherwise>
                                                <strong class="detail-text-danger">
                                                    Chưa thanh toán
                                                </strong>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>


                                </div>

                                <c:if test="${booking.status ne 'Đã hủy'
                                              and booking.status ne 'Đã nhận phòng'
                                              and booking.status ne 'Đã trả phòng'}">

                                    <div class="detail-payment-action">
                                        <c:choose>
                                            <c:when test="${empty verificationStatus
                                                            or verificationStatus eq 'Chưa gửi minh chứng'}">

                                                <a href="${paymentUrl}"
                                                   class="detail-payment-button">

                                                    <span>TIẾP TỤC THANH TOÁN</span>
                                                    <span class="detail-payment-arrow">→</span>
                                                </a>

                                                <p class="detail-payment-help">
                                                    Xem thông tin chuyển khoản và gửi
                                                    ảnh minh chứng đặt cọc.
                                                </p>
                                            </c:when>

                                            <c:when test="${verificationStatus eq 'Chờ xử lý'}">
                                                <div class="detail-payment-message waiting">
                                                    <span class="detail-payment-message-icon">
                                                        ◷
                                                    </span>

                                                    <div>
                                                        <strong>Minh chứng đang chờ xử lý</strong>

                                                        <p>
                                                            Khách sạn đang kiểm tra
                                                            minh chứng đặt cọc của bạn.
                                                        </p>
                                                    </div>
                                                </div>
                                            </c:when>

                                            <c:when test="${verificationStatus eq 'Đã phê duyệt'}">
                                                <div class="detail-payment-message approved">
                                                    <span class="detail-payment-message-icon">
                                                        ✓
                                                    </span>

                                                    <div>
                                                        <strong>Đã xác nhận đặt cọc</strong>

                                                        <p>
                                                            Minh chứng thanh toán đã
                                                            được khách sạn phê duyệt.
                                                        </p>
                                                    </div>
                                                </div>
                                            </c:when>
                                        </c:choose>
                                    </div>
                                </c:if>
                            </section>
                        </div>

                        <section class="detail-card detail-timeline-card">
                            <div class="detail-section-title">
                                <span class="detail-section-icon">◷</span>
                                <h2>LỊCH TRÌNH ĐƠN ĐẶT PHÒNG</h2>
                            </div>

                            <div class="detail-timeline-scroll">
                                <div class="detail-timeline">

                                    <div class="detail-timeline-step completed">
                                        <div class="detail-timeline-icon">✓</div>
                                        <strong>Đặt phòng</strong>
                                        <span>${createdAtText}</span>
                                    </div>

                                    <div class="detail-timeline-line completed"></div>

                                    <div class="detail-timeline-step
                                         ${proofSent ? 'completed' : ''}">

                                        <div class="detail-timeline-icon">
                                            <c:choose>
                                                <c:when test="${proofSent}">✓</c:when>
                                                <c:otherwise>2</c:otherwise>
                                            </c:choose>
                                        </div>

                                        <strong>Thanh toán</strong>

                                        <span>
                                            <c:choose>
                                                <c:when test="${proofSent}">
                                                    Đã gửi minh chứng
                                                </c:when>

                                                <c:otherwise>
                                                    Chưa thực hiện
                                                </c:otherwise>
                                            </c:choose>
                                        </span>
                                    </div>

                                    <div class="detail-timeline-line
                                         ${bookingConfirmed ? 'completed' : ''}">
                                    </div>

                                    <div class="detail-timeline-step
                                         ${bookingConfirmed ? 'completed' : ''}">

                                        <div class="detail-timeline-icon">
                                            <c:choose>
                                                <c:when test="${bookingConfirmed}">✓</c:when>
                                                <c:otherwise>3</c:otherwise>
                                            </c:choose>
                                        </div>

                                        <strong>Xác nhận</strong>

                                        <span>
                                            <c:choose>
                                                <c:when test="${bookingConfirmed}">
                                                    Đã xác nhận
                                                </c:when>

                                                <c:otherwise>
                                                    Đang chờ
                                                </c:otherwise>
                                            </c:choose>
                                        </span>
                                    </div>

                                    <div class="detail-timeline-line
                                         ${checkedIn ? 'completed' : ''}">
                                    </div>

                                    <div class="detail-timeline-step
                                         ${checkedIn ? 'completed' : ''}">

                                        <div class="detail-timeline-icon">
                                            <c:choose>
                                                <c:when test="${checkedIn}">✓</c:when>
                                                <c:otherwise>4</c:otherwise>
                                            </c:choose>
                                        </div>

                                        <strong>Nhận phòng</strong>

                                        <span>
                                            <c:choose>
                                                <c:when test="${not empty booking.actualCheckinTime}">
                                                    Thực tế: ${timelineCheckInText}
                                                </c:when>

                                                <c:otherwise>
                                                    Dự kiến: ${timelineCheckInText}
                                                </c:otherwise>
                                            </c:choose>
                                        </span>
                                    </div>

                                    <div class="detail-timeline-line
                                         ${checkedOut ? 'completed' : ''}">
                                    </div>

                                    <div class="detail-timeline-step
                                         ${checkedOut ? 'completed' : ''}">

                                        <div class="detail-timeline-icon">
                                            <c:choose>
                                                <c:when test="${checkedOut}">✓</c:when>
                                                <c:otherwise>5</c:otherwise>
                                            </c:choose>
                                        </div>

                                        <strong>Trả phòng</strong>

                                        <span>
                                            <c:choose>
                                                <c:when test="${not empty booking.actualCheckoutTime}">
                                                    Thực tế: ${timelineCheckOutText}
                                                </c:when>

                                                <c:otherwise>
                                                    Dự kiến: ${timelineCheckOutText}
                                                </c:otherwise>
                                            </c:choose>
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </section>
                    </div>

                    <aside class="detail-right-column">

                        <section class="detail-card detail-request-card">
                            <div class="detail-side-title detail-side-title-row">
                                <h2>YÊU CẦU CỦA BẠN</h2>

                                <span class="detail-request-count">
                                    0 yêu cầu
                                </span>
                            </div>

                            <div class="detail-request-empty">
                                <span class="detail-request-empty-icon">✓</span>

                                <div>
                                    <strong>Chưa có yêu cầu nào</strong>

                                    <p>
                                        Các yêu cầu thay đổi và trạng thái xử lý
                                        sẽ được hiển thị tại đây.
                                    </p>
                                </div>
                            </div>

                            <div class="detail-request-divider"></div>

                            <div class="detail-change-section">
                                <h3>NHỮNG THAY ĐỔI CỦA ĐƠN</h3>

                                <c:choose>
                                    <c:when test="${booking.status eq 'Đã hủy'}">
                                        <div class="detail-change-item cancelled">
                                            <span class="detail-change-icon">×</span>

                                            <div>
                                                <strong>
                                                    Đơn đặt phòng đã được hủy
                                                </strong>

                                                <c:choose>
                                                    <c:when test="${not empty booking.cancellationReason}">
                                                        <p>
                                                            Lý do:
                                                        <c:out value="${booking.cancellationReason}"/>
                                                        </p>
                                                    </c:when>

                                                    <c:otherwise>
                                                        <p>
                                                            Đơn không còn giữ phòng trong
                                                            khoảng thời gian đã đặt.
                                                        </p>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                    </c:when>

                                    <c:otherwise>
                                        <div class="detail-change-empty">
                                            <span class="detail-change-icon">i</span>

                                            <div>
                                                <strong>
                                                    Đơn đặt phòng hiện chưa có thay đổi
                                                </strong>

                                                <p>
                                                    Các thay đổi đã được phê duyệt
                                                    sẽ được cập nhật tại đây.
                                                </p>
                                            </div>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </section>

                        <section class="detail-card detail-management-card">
                            <div class="detail-side-title">
                                <h2>QUẢN LÝ ĐƠN</h2>
                            </div>

                            <div class="detail-management-grid">
                                <c:choose>
                                    <c:when test="${booking.status eq 'Đã xác nhận'}">
                                        <button type="button"
                                                class="detail-management-button">

                                            <span class="detail-management-icon">✎</span>

                                            <span>
                                                <strong>Tạo yêu cầu</strong>
                                                <small>Yêu cầu thay đổi đơn</small>
                                            </span>
                                        </button>
                                    </c:when>

                                    <c:otherwise>
                                        <div class="detail-management-button disabled">
                                            <span class="detail-management-icon">✎</span>

                                            <span>
                                                <strong>Tạo yêu cầu</strong>
                                                <small>Khả dụng sau xác nhận</small>
                                            </span>
                                        </div>
                                    </c:otherwise>
                                </c:choose>

                                <c:choose>
                                    <c:when test="${booking.status eq 'Chờ xử lý'
                                                    or booking.status eq 'Đã xác nhận'}">

                                        <a href="${cancelBookingUrl}"
                                           class="detail-management-button cancel">

                                            <span class="detail-management-icon">×</span>

                                            <span>
                                                <strong>Hủy đặt phòng</strong>
                                                <small>Gửi yêu cầu hủy đơn</small>
                                            </span>
                                        </a>
                                    </c:when>

                                    <c:otherwise>
                                        <div class="detail-management-button cancel disabled">
                                            <span class="detail-management-icon">×</span>

                                            <span>
                                                <strong>Hủy đặt phòng</strong>
                                                <small>Không còn khả dụng</small>
                                            </span>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </section>

                        <section class="detail-card detail-note-card">
                            <div class="detail-note-title">
                                <span>i</span>
                                <h2>THÔNG TIN LƯU Ý</h2>
                            </div>

                            <ul>
                                <li>
                                    Vui lòng có mặt tại khách sạn trước giờ nhận phòng.
                                </li>

                                <li>
                                    Mang theo giấy tờ tùy thân để làm thủ tục nhận phòng.
                                </li>
                            </ul>
                        </section>

                        <section class="detail-card detail-support-card">
                            <div class="detail-side-title">
                                <h2>CẦN HỖ TRỢ?</h2>
                            </div>

                            <p class="detail-support-description">
                                Liên hệ với chúng tôi nếu bạn cần hỗ trợ thêm
                                về đơn đặt phòng.
                            </p>

                            <div class="detail-support-grid">
                                <a href="tel:02363889999"
                                   class="detail-support-item">

                                    <span class="detail-support-icon">☎</span>

                                    <span class="detail-support-content">
                                        <small>Điện thoại</small>
                                        <strong>0236 388 9999</strong>
                                    </span>
                                </a>

                                <a href="mailto:info@lamerhotel.com"
                                   class="detail-support-item">

                                    <span class="detail-support-icon">✉</span>

                                    <span class="detail-support-content">
                                        <small>Email</small>
                                        <strong>info@lamerhotel.com</strong>
                                    </span>
                                </a>
                            </div>
                        </section>
                    </aside>
                </div>

                <div class="detail-security-note">
                    <span>▣</span>

                    <p>
                        Để bảo mật thông tin, vui lòng không chia sẻ mã đặt phòng
                        và email của bạn cho người khác.
                    </p>
                </div>

                <div class="detail-page-actions">
                    <a href="${pageContext.request.contextPath}/booking-detail"
                       class="detail-back-button">
                        QUAY LẠI TRA CỨU
                    </a>

                    <a href="${pageContext.request.contextPath}/home"
                       class="detail-home-button">
                        VỀ TRANG CHỦ
                    </a>
                </div>
            </c:if>
        </main>

        <jsp:include page="/view/common/footer.jsp"/>
    </body>
</html>