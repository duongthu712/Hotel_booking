<%-- 
    Document   : search-result
    Created on : May 27, 2026, 10:41:35 PM
    Author     : Minh Thu
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Kết Quả Tìm Kiếm Phòng - La Mer Hotel</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/navbar.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/homepage.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/search-result.css"> 
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/footer.css">
    </head>
    <body>
        <jsp:include page="/view/common/navbar.jsp" />

        <div class="room-list-page">

            <div class="search-bar" style="margin: 0 0 40px 0; width: 100%;">
                <form action="${pageContext.request.contextPath}/search" method="GET" style="display: flex; width:100%; gap:20px; flex-wrap: wrap;">

                    <input type="date" name="checkIn" id="checkInResult" value="${param.checkIn}" required>
                    <input type="date" name="checkOut" id="checkOutResult" value="${param.checkOut}" required>

                    <input type="number" name="roomQuantity" value="${not empty param.roomQuantity ? param.roomQuantity : 1}" min="1" required placeholder="Số lượng phòng">
                    <select name="roomTypeId">

                        <option value="all"
                                <c:if test="${empty param.roomTypeId || param.roomTypeId eq 'all'}">
                                    selected
                                </c:if>>
                            Tất cả loại phòng
                        </option>

                        <c:forEach var="item" items="${allRoomTypesList}">
                            <option value="${item.roomTypeId}"
                                    <c:if test="${param.roomTypeId eq item.roomTypeId.toString()}">
                                        selected
                                    </c:if>>
                                ${item.typeName}
                            </option>
                        </c:forEach>

                    </select>
                    <button type="submit">CẬP NHẬT TÌM KIẾM</button>
                </form>
            </div>

            <div class="search-summary-text">
                <c:choose>
                    <c:when test="${not empty param.checkIn}">
                        <%-- Parse ngày thô từ URL (yyyy-MM-dd) thành Date Object để format --%>
                        <fmt:parseDate value="${param.checkIn}" pattern="yyyy-MM-dd" var="parsedCheckIn" />
                        <fmt:parseDate value="${param.checkOut}" pattern="yyyy-MM-dd" var="parsedCheckOut" />

                        Đang hiển thị các loại phòng trống có: <strong>${not empty param.roomQuantity ? param.roomQuantity : 1} Phòng</strong> | 
                        Thời gian: <strong><fmt:formatDate value="${parsedCheckIn}" pattern="dd/MM/yyyy" /></strong> 
                        đến <strong><fmt:formatDate value="${parsedCheckOut}" pattern="dd/MM/yyyy" /></strong>
                    </c:when>
                    <c:otherwise>
                        Chào mừng bạn đến với La Mer. Dưới đây là <strong>Tất cả các hạng phòng hiện có</strong> tại khách sạn:
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="room-list-grid">
                <c:choose>
                    <c:when test="${not empty availableRoomTypes}">
                        <c:forEach var="room" items="${availableRoomTypes}">
                            <div class="room-card-horizontal">

                                <div class="room-card-img-box">

                                    <img src="${room.imageUrl}" alt="${room.typeName}">
                                </div>

                                <div class="room-card-details">
                                    <div class="room-card-header">
                                        <h3 class="room-title-text">${room.typeName}</h3>
                                        <div class="room-price-tag">
                                            <fmt:formatNumber value="${room.basePrice}" type="currency" currencySymbol="" maxFractionDigits="0"/> VND
                                            <span>/ ĐÊM</span>
                                        </div>
                                    </div>

                                    <div class="room-card-desc">
                                        ${room.description}
                                    </div>

                                    <div class="room-amenities-group">
                                        <c:forEach var="svc" items="${room.roomServices}">
                                            <span class="amenity-badge">
                                                ${svc.serviceName} (x${svc.quantity})
                                            </span>
                                        </c:forEach>
                                    </div>

                                    <div class="room-card-footer-bar">
                                        <div class="room-actions-group">
                                            <a href="${pageContext.request.contextPath}/room-detail?id=${room.roomTypeId}" class="btn-action-view">
                                                Xem chi tiết
                                            </a>
                                            <a href="${pageContext.request.contextPath}/view/user/booking-form.jsp?roomTypeId=${room.roomTypeId}&checkIn=${param.checkIn}&checkOut=${param.checkOut}&roomQuantity=${not empty param.roomQuantity ? param.roomQuantity : 1}" class="btn-action-book">
                                                Đặt Phòng
                                            </a>
                                        </div>
                                    </div>
                                </div>

                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-result-box">
                            <h3 style="font-family: 'Playfair Display', serif; font-size: 22px; color: #2c3e46; margin-bottom: 10px;">Không tìm thấy phòng phù hợp</h3>
                            <p style="font-size: 14px; color: #777;">Rất tiếc, La Mer đã hết phòng trống thích hợp với khoảng thời gian hoặc số lượng phòng bạn yêu cầu. Bạn hãy thử đổi ngày xem nhé!</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <jsp:include page="/view/common/footer.jsp" />

        <script src="${pageContext.request.contextPath}/view/assets/javascript/booking-calendar.js"></script>
    </body>
</html>