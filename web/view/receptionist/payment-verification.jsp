<%-- 
    Document   : payment-verification
    Created on : May 27, 2026, 10:45:41 PM
    Author     : Minh Thu
    Editor     : LinhLTHE200306
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page import="model.StaffAccount" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/common.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/payment-verification.css" type="text/css">
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Xác minh thanh toán đặt cọc</title>
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
                <form action="DepositPaymentList" method="GET" class="search-form">
                    <input type="text" name="keyword" class="search-input" 
                           placeholder="Tìm kiếm theo mã đặt phòng hoặc tên khách..." value="${keyword}">

                    <select name="status" class="filter-select">
                        <option value="" ${empty status ? 'selected' : ''}>Chờ xử lý</option>
                        <option value="all" ${status == 'all' ? 'selected' : ''}>Tất cả</option>
                        <option value="Đã phê duyệt" ${status == 'Đã duyệt' ? 'selected' : ''}>Đã phê duyệt</option>
                        <option value="Đã từ chối" ${status == 'Đã từ chối' ? 'selected' : ''}>Đã từ chối</option>
                    </select>

                    <button type="submit" class="search-btn">Lọc</button>
                    <a href="DepositPaymentList" class="reset-btn">Làm mới</a>
                </form>
            </div>

            <table class="data-table">
                <thead class="data-table-thead">
                    <tr>
                        <th class="col-stt">STT</th>
                        <th class="col-booking">Mã đặt phòng</th>
                        <th class="col-guest">Tên khách</th>
                        <th class="col-amount">Số tiền cọc</th>
                        <th class="col-date">Ngày gửi</th>
                        <th class="col-status">Trạng thái</th>
                        <th class="col-action">Hành động</th>
                    </tr>
                </thead>

                <tbody class="data-table-tbody">
                    <c:forEach var="payment" items="${paymentList}" varStatus="loop">
                        <tr data-deposit-id="${payment.depositId}"
                            data-booking-id="${payment.bookingId}"
                            data-proof-url="${payment.paymentProofUrl}"
                            data-notes="${payment.notes}">
                            <td class="col-stt">${(currentPage - 1) * 10 + loop.index + 1}</td>
                            <td class="col-booking">${bookingCodeMap[payment.getBookingId()]}</td>
                            <td class="col-guest">${guestNameMap[payment.getBookingId()]}</td>
                            <td class="col-amount"><fmt:formatNumber value="${payment.amount}" type="number" pattern="#,###" />đ</td>
                            <td class="col-date">
                                ${payment.submittedAt.toLocalTime().toString().substring(0, 5)}
                                ${payment.submittedAt.getDayOfMonth()}/${payment.submittedAt.getMonthValue()}/${payment.submittedAt.getYear()}
                            </td>
                            <td class="col-status">
                                <div class="payment-status ${payment.verificationStatus == 'Chờ xử lý' ? 'status-pending' : (payment.verificationStatus == 'Đã phê duyệt' ? 'status-approved' : 'status-rejected')}">
                                    ${payment.verificationStatus == 'Đã phê duyệt' ? 'Đã duyệt' : payment.verificationStatus}
                                </div>
                            </td>
                            <td class="btn-action">
                                <button type="button" class="btn-view" onclick="openPaymentDetailModal(${payment.depositId})">Chi tiết</button>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
                <c:if test="${empty paymentList}">
                    <tr>
                        <td colspan="7" class="empty-message">
                            Không tìm thấy khoản đặt cọc.
                        </td>
                    </tr>
                </c:if>
            </table>

            <div class="pagination">
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <a href="DepositPaymentList?page=${i}&keyword=${keyword}&status=${status}" 
                       class="${currentPage == i ? 'active' : ''}">${i}</a>
                </c:forEach>
            </div>

            <div class="hotel-popup" id="payment-detail-modal">
                <div class="popup-content payment-detail-content">
                    <h2 class="popup-title">Chi tiết thanh toán</h2>

                    <div class="payment-detail-layout">
                        <div class="payment-proof-column">
                            <div class="payment-proof-image" id="payment-proof-wrapper">
                                <img src="" alt="Payment Proof" id="proof-img">
                            </div>
                        </div>

                        <div class="payment-info-column">
                            <div class="payment-info">
                                <div class="info-row">
                                    <span class="info-label">Mã đặt phòng:</span>
                                    <span class="info-value" id="detail-booking-code"></span>
                                </div>
                                <div class="info-row">
                                    <span class="info-label">Tên khách:</span>
                                    <span class="info-value" id="detail-guest-name"></span>
                                </div>
                                <div class="info-row">
                                    <span class="info-label">Số tiền cọc:</span>
                                    <span class="info-value" id="detail-amount"></span>
                                </div>
                                <div class="info-row">
                                    <span class="info-label">Ngày gửi:</span>
                                    <span class="info-value" id="detail-submitted-at"></span>
                                </div>
                                <div class="info-row">
                                    <span class="info-label">Trạng thái:</span>
                                    <span class="info-value" id="detail-status"></span>
                                </div>
                            </div>

                            <div class="verification-form" id="verification-form">
                                <div class="form-group">
                                    <label class="input-label">Ghi chú</label>
                                    <textarea id="verify-notes" class="popup-input-field" rows="3" placeholder="Nhập ghi chú..."></textarea>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="popup-action">
                        <button type="button" class="btn-close" id="btn-close-detail">Đóng</button>
                        <form action="DepositPaymentReject" method="POST" style="display: inline-block; margin: 0;">
                            <input type="hidden" name="depositId" id="reject-deposit-id">
                            <input type="hidden" name="notes" id="reject-notes">
                            <input type="hidden" name="page" value="${currentPage}">
                            <input type="hidden" name="keyword" value="${keyword}">
                            <input type="hidden" name="status" value="${status}">
                            <button type="submit" class="btn-reject" onclick="return prepareReject()">Từ chối</button>
                        </form>
                        <form action="DepositPaymentVerify" method="POST" style="display: inline-block; margin: 0;">
                            <input type="hidden" name="depositId" id="verify-deposit-id">
                            <input type="hidden" name="notes" id="verify-notes-hidden">
                            <input type="hidden" name="page" value="${currentPage}">
                            <input type="hidden" name="keyword" value="${keyword}">
                            <input type="hidden" name="status" value="${status}">
                            <button type="submit" class="btn-submit">Xác nhận</button>
                        </form>
                    </div>
                </div>
            </div>


        </main>

        <script src="<%=request.getContextPath()%>/view/assets/javascript/payment-verification.js"></script>
    </body>

</html>
