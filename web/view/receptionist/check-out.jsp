<%-- 
    Document   : check-out
    Created on : May 27, 2026, 10:47:01 PM
    Author     : Minh Thu
    Editor     : LinhLTHE200306
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="model.StaffAccount" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Trả phòng</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/common.css" type="text/css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/checkout.css" type="text/css">
    </head>

    <body>
        <%@ include file="/view/staff/header.jsp" %>
        <%@ include file="/view/staff/navbar.jsp" %>

        <main class="content-container">

            <!-- Alert messages -->
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

            <!-- Search -->
            <div class="search-container">
                <form action="Checkout" method="GET" class="search-form">
                    <div class="search-input-wrapper">
                        <input type="text" name="keyword" class="search-input" 
                               placeholder="Tìm theo tên khách hoặc số phòng..." value="${keyword}">
                    </div>
                    <button type="submit" class="search-btn">Tìm kiếm</button>
                    <c:if test="${not empty keyword}">
                        <a href="Checkout" class="reset-btn">Xóa lọc</a>
                    </c:if>
                </form>
            </div>

            <!-- Checkout Table -->
            <div class="checkout-table-section">
                <c:choose>
                    <c:when test="${not empty roomList}">
                        <table class="data-table checkout-table" id="checkoutTable">
                            <thead class="data-table-thead">
                                <tr>
                                    <th class="col-booking-id">Mã đặt phòng</th>
                                    <th class="col-room">Số phòng</th>
                                    <th class="col-guest">Tên khách đại diện</th>
                                    <th class="col-checkin">Ngày check-in</th>
                                    <th class="col-checkout">Ngày check-out</th>
                                    <th class="col-action">Thao tác</th>
                                </tr>
                            </thead>
                            <tbody class="data-table-tbody">
                                <c:forEach var="room" items="${roomList}" varStatus="loop">
                                    <tr class="room-row" 
                                        data-booking-id="${room.bookingId}"
                                        data-room-id="${room.roomId}">
                                        <td class="col-booking-id">
                                            <span class="booking-code">${room.bookingCode}</span>
                                        </td>
                                        <td class="col-room">
                                            <span class="room-number">Phòng ${room.roomNumber}</span>
                                        </td>
                                        <td class="col-guest">${room.guestName}</td>
                                        <td class="col-checkin">${room.checkinDate}</td>
                                        <td class="col-checkout">${room.checkoutDate}</td>
                                        <td class="col-action">
                                            <form action="Checkout" method="POST" class="checkout-form-inline">
                                                <input type="hidden" name="selectedRoom" value="${room.roomId}">
                                                <button type="submit" class="btn-checkout">Checkout</button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-message">
                            <c:choose>
                                <c:when test="${not empty keyword}">
                                    Không tìm thấy phòng nào phù hợp với từ khóa "<strong>${keyword}</strong>".
                                </c:when>
                                <c:otherwise>
                                    Không có phòng nào cần checkout hôm nay.
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

        </main>

        <script src="<%=request.getContextPath()%>/view/assets/javascript/alert.js"></script>
        <script src="${pageContext.request.contextPath}/view/assets/javascript/checkout.js"></script>
    </body>
</html>

