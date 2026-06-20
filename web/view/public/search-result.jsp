
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <title>Kết Quả Tìm Kiếm Phòng - La Mer Hotel</title>

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/view/assets/css/search-result.css?v=<%= System.currentTimeMillis() %>">

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/view/assets/css/navbar.css?v=<%= System.currentTimeMillis() %>">

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/view/assets/css/homepage.css?v=<%= System.currentTimeMillis() %>">

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/view/assets/css/footer.css?v=<%= System.currentTimeMillis() %>">
    </head>

    <body>
        <jsp:include page="/view/common/navbar.jsp"/>

        <div class="room-list-page">

            <!-- Thanh tìm kiếm -->
            <div class="search-bar"
                 style="margin: 0 0 40px 0; width: 100%;">

                <form action="${pageContext.request.contextPath}/search"
                      method="GET"
                      style="display: flex; width: 100%; gap: 20px; flex-wrap: wrap;">

                    <input type="date"
                           name="checkIn"
                           id="checkInResult"
                           value="${param.checkIn}"
                           required>

                    <input type="date"
                           name="checkOut"
                           id="checkOutResult"
                           value="${param.checkOut}"
                           required>

                    <input type="number"
                           name="roomQuantity"
                           value="${not empty param.roomQuantity
                                    ? param.roomQuantity
                                    : 1}"
                           min="1"
                           required
                           placeholder="Số lượng phòng">

                    <select name="roomTypeId">
                        <option value="all"
                                <c:if test="${empty param.roomTypeId
                                              or param.roomTypeId eq 'all'}">
                                      selected
                                </c:if>>
                            Tất cả loại phòng
                        </option>

                        <c:forEach var="item"
                                   items="${allRoomTypesList}">

                            <option value="${item.roomTypeId}"
                                    <c:if test="${param.roomTypeId
                                                  eq item.roomTypeId.toString()}">
                                          selected
                                    </c:if>>
                                ${item.typeName}
                            </option>
                        </c:forEach>
                    </select>

                    <button type="submit">
                        CẬP NHẬT TÌM KIẾM
                    </button>
                </form>
            </div>

            <!-- Nội dung tóm tắt tìm kiếm -->
            <div class="search-summary-text">
                <c:choose>
                    <c:when test="${not empty param.checkIn
                                    and not empty param.checkOut}">

                            <fmt:parseDate value="${param.checkIn}"
                                           pattern="yyyy-MM-dd"
                                           var="parsedCheckIn"/>

                            <fmt:parseDate value="${param.checkOut}"
                                           pattern="yyyy-MM-dd"
                                           var="parsedCheckOut"/>

                            <c:set var="formattedCheckIn">
                                <fmt:formatDate value="${parsedCheckIn}"
                                                pattern="dd/MM/yyyy"/>
                            </c:set>

                            <c:set var="formattedCheckOut">
                                <fmt:formatDate value="${parsedCheckOut}"
                                                pattern="dd/MM/yyyy"/>
                            </c:set>

                            <c:set var="reqRooms"
                                   value="${not empty param.roomQuantity
                                            ? param.roomQuantity
                                            : 1}"/>

                            <c:choose>
                                <c:when test="${not empty param.roomTypeId
                                                and param.roomTypeId ne 'all'
                                                and not empty availableRoomTypes}">

                                        <c:set var="selectedTypeName"
                                               value="${availableRoomTypes[0].typeName}"/>

                                        Hạng phòng
                                        <strong>${selectedTypeName}</strong>
                                        sẵn sàng đón tiếp Quý khách với số lượng
                                        <strong>${reqRooms} phòng</strong>
                                        từ ngày
                                        <strong>${formattedCheckIn}</strong>
                                        đến
                                        <strong>${formattedCheckOut}</strong>:
                                </c:when>

                                <c:otherwise>
                                    Tìm thấy các
                                    <strong>hạng phòng</strong>
                                    trống đáp ứng đủ nhu cầu
                                    <strong>${reqRooms} phòng</strong>
                                    của Quý khách từ ngày
                                    <strong>${formattedCheckIn}</strong>
                                    đến
                                    <strong>${formattedCheckOut}</strong>:
                                </c:otherwise>
                            </c:choose>
                    </c:when>

                    <c:otherwise>
                        Chào mừng bạn đến với La Mer. Dưới đây là
                        <strong>
                            tất cả các hạng phòng hiện có
                        </strong>
                        tại khách sạn:
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Danh sách phòng -->
            <div class="room-list-grid">
                <c:choose>
                    <c:when test="${not empty availableRoomTypes}">
                        <c:forEach var="room"
                                   items="${availableRoomTypes}">

                            <div class="room-card-horizontal">

                                <!-- Hình ảnh -->
                                <div class="room-card-img-box">
                                    <img src="${not empty room.imageUrl
                                                ? room.imageUrl[0]
                                                : 'https://placehold.co/600x400?text=La+Mer+Room'}"
                                         alt="${room.typeName}">
                                </div>

                                <!-- Nội dung -->
                                <div class="room-card-details">

                                    <div class="room-card-header">
                                        <h3 class="room-title-text">
                                            ${room.typeName}
                                        </h3>

                                        <div class="room-price-tag">
                                            <fmt:formatNumber
                                                value="${room.basePrice}"
                                                type="currency"
                                                currencySymbol=""
                                                maxFractionDigits="0"/>
                                            VND

                                            <span>/ ĐÊM</span>
                                        </div>
                                    </div>

                                    <div class="room-card-desc">
                                        ${room.description}
                                    </div>

                                    <div class="room-amenities-container">
                                        <div class="room-core-specs">

                                            <span class="badge-core-capacity">
                                                Sức chứa:
                                                ${room.capacity} khách
                                            </span>

                                            <span class="badge-core-info">
                                                Giường:
                                                ${room.bedCount} x
                                                ${room.bedType}
                                            </span>

                                            <span class="badge-core-info">
                                                Diện tích:
                                                ${room.areaSqm} m²
                                            </span>

                                        </div>
                                    </div>

                                    <!-- Các nút thao tác -->
                                    <div class="room-card-footer-bar">
                                        <div class="room-actions-group">

                                            <!-- Xem chi tiết -->
                                            <c:url var="roomDetailUrl"
                                                   value="/room-detail">

                                                <c:param name="id"
                                                         value="${room.roomTypeId}"/>

                                                <c:if test="${not empty param.checkIn}">
                                                    <c:param name="checkIn"
                                                             value="${param.checkIn}"/>
                                                </c:if>

                                                <c:if test="${not empty param.checkOut}">
                                                    <c:param name="checkOut"
                                                             value="${param.checkOut}"/>
                                                </c:if>

                                                <c:param name="roomQuantity"
                                                         value="${not empty param.roomQuantity
                                                                  ? param.roomQuantity
                                                                  : 1}"/>
                                            </c:url>

                                            <a href="${roomDetailUrl}"
                                               class="btn-action-view">
                                                Xem chi tiết
                                            </a>

                                            <!-- Đặt phòng -->
                                            <c:choose>

                                                <%-- Đã tìm kiếm và có đủ ngày --%>
                                                <c:when test="${not empty param.checkIn
                                                                and not empty param.checkOut}">

                                                        <c:url var="bookingFormUrl"
                                                               value="/booking-form">

                                                            <c:param name="source"
                                                                     value="search"/>

                                                            <c:param name="roomTypeId"
                                                                     value="${room.roomTypeId}"/>

                                                            <c:param name="checkIn"
                                                                     value="${param.checkIn}"/>

                                                            <c:param name="checkOut"
                                                                     value="${param.checkOut}"/>

                                                            <c:param name="roomQuantity"
                                                                     value="${not empty param.roomQuantity
                                                                              ? param.roomQuantity
                                                                              : 1}"/>

                                                            <c:param name="numGuests"
                                                                     value="${not empty param.numGuests
                                                                              ? param.numGuests
                                                                              : 1}"/>
                                                        </c:url>

                                                        <a href="${bookingFormUrl}"
                                                           class="btn-action-book">
                                                            Đặt Phòng
                                                        </a>
                                                </c:when>

                                                <%-- Chưa tìm kiếm, quay về Quick Booking --%>
                                                <c:otherwise>
                                                    <c:url var="quickBookingUrl"
                                                           value="/quick-booking">

                                                        <c:param name="roomTypeId"
                                                                 value="${room.roomTypeId}"/>
                                                    </c:url>

                                                    <a href="${quickBookingUrl}"
                                                       class="btn-action-book">
                                                        Đặt Phòng
                                                    </a>
                                                </c:otherwise>
                                            </c:choose>

                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:when>

                    <c:otherwise>
                        <div class="empty-result-box">
                            <h3 style="
                                font-family: 'Playfair Display', serif;
                                font-size: 22px;
                                color: #2c3e46;
                                margin-bottom: 10px;">

                                Không tìm thấy phòng phù hợp
                            </h3>

                            <p style="
                               font-size: 14px;
                               color: #777;">

                                Rất tiếc, La Mer đã hết phòng trống thích hợp
                                với khoảng thời gian hoặc số lượng phòng bạn
                                yêu cầu. Bạn hãy thử đổi ngày xem nhé!
                            </p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <jsp:include page="/view/common/footer.jsp"/>

        <script src="${pageContext.request.contextPath}/view/assets/javascript/booking-calendar.js">
        </script>
    </body>
</html>
