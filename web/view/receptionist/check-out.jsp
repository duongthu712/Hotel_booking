<%-- 
    Document   : check-out
    Created on : May 27, 2026, 10:47:01 PM
    Author     : Minh Thu
    Editor     : LinhLTHE200306
--%>
<%-- 
    Document   : check-out
    Created on : Jun 28, 2026
    Author     : LinhLTHE200306
    Luồng mới: Checkbox table, multi-booking checkout
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
                        <a href="Checkout" class="btn-clear">Xóa lọc</a>
                    </c:if>
                </form>
            </div>

            <!-- Checkout Form -->
            <form id="checkoutForm" action="Checkout" method="POST">
                <div class="checkout-table-section">
                    <c:choose>
                        <c:when test="${not empty roomList}">
                            <table class="data-table checkout-table" id="checkoutTable">
                                <thead class="data-table-thead">
                                    <tr>
                                        <th class="col-checkbox">
                                            <input type="checkbox" id="selectAll" title="Chọn tất cả">
                                        </th>
                                        <th class="col-room">Phòng</th>
                                        <th class="col-guest">Tên khách</th>
                                        <th class="col-checkout">Check-out dự kiến</th>
                                        <th class="col-booking">Mã đặt phòng</th>
                                    </tr>
                                </thead>
                                <tbody class="data-table-tbody">
                                    <c:forEach var="room" items="${roomList}" varStatus="loop">
                                        <tr class="room-row" 
                                            data-booking-id="${room.bookingId}"
                                            data-room-id="${room.roomId}">
                                            <td class="col-checkbox">
                                                <input type="checkbox" name="selectedRooms" 
                                                       value="${room.roomId}" 
                                                       class="room-checkbox"
                                                       data-booking-id="${room.bookingId}">
                                            </td>
                                            <td class="col-room">
                                                <span class="room-number">Phòng ${room.roomNumber}</span>
                                            </td>
                                            <td class="col-guest">${room.guestName}</td>
                                            <td class="col-checkout">
                                                ${room.checkoutDate}
                                            </td>
                                            <td class="col-booking">
                                                <span class="booking-code">${room.bookingCode}</span>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>

                            <div class="checkout-actions-bar">
                                <button type="submit" id="btnCheckout" class="btn-checkout-primary" disabled>
                                    Checkout các phòng đã chọn (<span id="selectedCount">0</span>)
                                </button>
                            </div>
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
            </form>

        </main>

                    
        <script src="<%=request.getContextPath()%>/view/assets/javascript/alert.js"></script>
        <script src="${pageContext.request.contextPath}/view/assets/javascript/checkout.js"></script>
    </body>
</html>