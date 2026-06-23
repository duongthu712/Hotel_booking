<%-- 
    Document   : check-out
    Created on : May 27, 2026, 10:47:01 PM
    Author     : Minh Thu
    Editor     : LinhLTHE200306
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page import="model.StaffAccount" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Trả phòng / Check-out</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/common.css" type="text/css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/checkout.css" type="text/css">
    </head>

    <body>
        <%@ include file="/view/staff/header.jsp" %>
        <%@ include file="/view/staff/navbar.jsp" %>

        <main class="content-container">

            <c:if test="${not empty sessionScope.successMessage}">
                <div class="alert-message alert-success">
                    ${sessionScope.successMessage}
                </div>
                <c:remove var="successMessage" scope="session"/>
            </c:if>

            <c:if test="${not empty sessionScope.errorMessage}">
                <div class="alert-message alert-error">
                    ${sessionScope.errorMessage}
                </div>
                <c:remove var="errorMessage" scope="session"/>
            </c:if>
            <div class="search-container">
                <form action="Checkout" method="GET" class="search-form">
                    <div class="search-input-wrapper">
                        <input type="text" name="keyword" class="search-input" 
                               placeholder="Nhập mã booking hoặc tên khách" value="${keyword}">
                    </div>
                    <button type="submit" class="search-btn">Tìm kiếm</button>
                </form>
            </div>

            <c:if test="${empty selectedBooking}">
                <div class="booking-list-section">
                    <c:choose>
                        <c:when test="${not empty bookingList}">
                            <table class="data-table">
                                <thead class="data-table-thead">
                                    <tr>
                                        <th class="col-stt">STT</th>
                                        <th class="col-booking">Mã đặt phòng</th>
                                        <th class="col-guest">Tên khách</th>
                                        <th class="col-room">Phòng</th>
                                        <th class="col-checkin">Nhận phòng</th>
                                        <th class="col-checkout">Trả phòng</th>
                                        <th class="col-action">Hành động</th>
                                    </tr>
                                </thead>
                                <tbody class="data-table-tbody">
                                    <c:forEach var="booking" items="${bookingList}" varStatus="loop">
                                        <tr>
                                            <td class="col-stt">${(currentPage - 1) * 10 + loop.index + 1}</td>
                                            <td class="col-booking">${booking.getBookingCode()}</td>
                                            <td class="col-guest">${guestMap[booking.getBookingId()].getFullName()}</td>
                                            <td class="col-room">${booking.getNumRooms()} phòng</td>
                                            <td class="col-checkin">${checkinDateMap[booking.bookingId]}</td>
                                            <td class="col-checkout">${checkoutDateMap[booking.bookingId]}</td>
                                            <td class="col-action">
                                                <a href="Checkout?bookingId=${booking.getBookingId()}" class="btn-checkout">Check-out</a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>

                            <div class="pagination">
                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <a href="Checkout?page=${i}&keyword=${keyword}" 
                                       class="${currentPage == i ? 'active' : ''}">${i}</a>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-message">Không có đơn đặt phòng.</div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:if>

            <c:if test="${not empty selectedBooking}">
                <div class="checkout-detail">

                    <div class="detail-section">
                        <h3 class="section-title">THÔNG TIN BOOKING</h3>

                        <div class="booking-info-grid">
                            <div class="room-image">
                                <img src="${roomImageUrl}" alt="Room Image">
                            </div>

                            <div class="booking-details">
                                <div class="booking-header">
                                    <span class="booking-code">${selectedBooking.getBookingCode()}</span>
                                    <span class="booking-status status-active">${selectedBooking.getStatus()}</span>
                                </div>

                                <div class="info-grid">
                                    <div class="info-col">
                                        <div class="info-row">
                                            <span class="info-label">Hạng phòng: </span>
                                            <span class="info-value">${roomType.getTypeName()}</span>
                                        </div>

                                        <div class="info-row">
                                            <span class="info-label">Giá phòng/đêm:</span>
                                            <span class="info-value">
                                                <fmt:formatNumber value="${selectedBooking.getBookedPricePerNight()}" type="number" pattern="#,###"/> đ</span>
                                        </div>

                                        <div class="info-row">
                                            <span class="info-label">Số đêm:</span>
                                            <span class="info-value">${nights} đêm</span>
                                        </div>
                                    </div>

                                    <div class="info-col">
                                        <div class="info-row">
                                            <span class="info-label">Check-in:</span>
                                            <span class="info-value">${checkinDateDisplay} ${formattedCheckinTime}</span>
                                        </div>
                                        <div class="info-row">
                                            <span class="info-label">Check-out dự kiến:</span>
                                            <span class="info-value">${checkoutDateDisplay} ${hotelInfo.getCheckoutTime()}</span>
                                        </div>

                                        <div class="info-row">
                                            <span class="info-label">Check-out thực tế:</span>
                                            <span class="info-value">
                                                <input type="text" class="time-input" value="${currentDateTimeDisplay}" readonly>
                                                <input type="hidden" name="actualCheckoutTime" value="${currentDateTimeISO}">
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="detail-section">
                        <h3 class="section-title">THÔNG TIN KHÁCH HÀNG</h3>

                        <div class="guest-info-grid">
                            <div class="info-col">
                                <div class="info-row">
                                    <span class="info-label">Họ và tên</span>
                                    <span class="info-value">${guest.getFullName()}</span>
                                </div>

                                <div class="info-row">
                                    <span class="info-label">Điện thoại</span>
                                    <span class="info-value">${guest.getPhone()}</span>
                                </div>
                            </div>

                            <div class="info-col">
                                <div class="info-row">
                                    <span class="info-label">Quốc tịch</span>
                                    <span class="info-value">${guest.getNationality()}</span>
                                </div>

                                <div class="info-row">
                                    <span class="info-label">Số CMND/Hộ chiếu</span>
                                    <span class="info-value">${guest.getIdNumber()}</span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="detail-section">
                        <h3 class="section-title">THÔNG TIN LƯU TRÚ</h3>

                        <table class="data-table stay-table">
                            <thead>
                                <tr>
                                    <th>Phòng</th>
                                    <th>Họ & tên</th>
                                    <th>SĐT</th>
                                    <th>CCCD/Hộ chiếu</th>
                                </tr>
                            </thead>

                            <tbody>
                                <c:forEach var="stay" items="${guestStays}">
                                    <tr>
                                        <td>
                                            <c:forEach var="br" items="${bookingRooms}">
                                                <c:if test="${br.getBookingRoomId() == stay.getBookingRoomId()}">
                                                    ${br.getRoomNumber()}
                                                </c:if>
                                            </c:forEach>
                                        </td>
                                        <td>${stay.getFullName()}</td>
                                        <td>${stay.getPhone()}</td>
                                        <td>${stay.getIdNumber()}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <div class="payment-summary">
                        <h3 class="section-title">TÓM TẮT THANH TOÁN</h3>

                        <div class="payment-row">
                            <span class="payment-label">Tiền phòng (${nights} đêm)</span>
                            <span class="payment-value"><fmt:formatNumber value="${roomCharges}" type="number" pattern="#,###"/>đ</span>
                        </div>

                        <div class="payment-row">
                            <span class="payment-label">Tiền cọc</span>
                            <span class="payment-value discount">-<fmt:formatNumber value="${selectedBooking.getDepositAmount()}" type="number" pattern="#,###"/>đ</span>
                        </div>

                        <div class="payment-row total-row">
                            <span class="payment-label">TỔNG TIỀN</span>
                            <span class="payment-value total"><fmt:formatNumber value="${roomCharges - selectedBooking.getDepositAmount().doubleValue()}" type="number" pattern="#,###"/>đ</span>
                        </div>
                    </div>

                    <div class="checkout-actions">
                        <a href="${pageContext.request.contextPath}/InvoiceCreate?bookingId=${selectedBooking.bookingId}" 
                           class="btn-checkout-primary">
                            CHECK-OUT (TẠO HÓA ĐƠN)
                        </a>
                        <a href="Checkout" class="btn-cancel-checkout">HỦY TRẢ PHÒNG</a>
                    </div>

                </div>
            </c:if>

        </main>
        <script src="<%=request.getContextPath()%>/view/assets/javascript/alert.js"></script>
        <script src="${pageContext.request.contextPath}/view/assets/javascript/checkout.js"></script>
    </body>
</html>