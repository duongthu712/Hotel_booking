<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>${hotelInfo.hotelName} - Trang Chủ</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/navbar.css?v=<%= System.currentTimeMillis() %>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/homepage.css?v=<%= System.currentTimeMillis() %>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/footer.css?v=<%= System.currentTimeMillis() %>">
    </head>

    <body>
        <header class="hero"
                style="background-image:
                linear-gradient(rgba(0,0,0,0.35), rgba(0,0,0,0.55)),
                url('${not empty bgImage ? bgImage : "https://images.squarespace-cdn.com/content/v1/5aadf482aa49a1d810879b88/1626698419120-J7CH9BPMB2YI728SLFPN/1.jpg"}');">

            <jsp:include page="/view/common/navbar.jsp" />

            <div class="hero-content">
                <h1>Nơi biển khơi vỗ về ngày mới</h1>
                <p>Trải nghiệm nghỉ dưỡng sang trọng và thư giãn tuyệt đối tại La Mer.</p>

                <div class="hero-actions">
                    <a href="${pageContext.request.contextPath}/quick-booking"
                       class="booking-btn">Đặt phòng nhanh</a>
                    <a class="btn outline" href="${pageContext.request.contextPath}/search">Khám phá</a>
                </div>
            </div>

            <form class="search-bar" action="${pageContext.request.contextPath}/search" method="GET">
                <input type="date" name="checkIn" id="checkIn" required>
                <input type="date" name="checkOut" id="checkOut" required>
                <input type="number" name="roomQuantity" min="1" value="1" required placeholder="Số lượng phòng">

                <select name="roomTypeId">
                    <option value="all" <c:if test="${empty param.roomTypeId || param.roomTypeId eq 'all'}">selected</c:if>>
                        Tất cả loại phòng
                    </option>
                    <c:forEach var="item" items="${allRoomTypesList}">
                        <option value="${item.roomTypeId}" <c:if test="${param.roomTypeId eq item.roomTypeId.toString()}">selected</c:if>>
                            ${item.typeName}
                        </option>
                    </c:forEach>
                </select>
                <button type="submit">Tìm phòng</button>
            </form>
        </header>

        <main class="container">          
            <section class="section">
                <h2>Tin tức & Sự kiện</h2>

                <div class="gallery-split-container">
                    <c:forEach var="news" items="${top3News}" varStatus="status">
                        <div class="split-row ${status.index % 2 != 0 ? 'reverse' : ''}">
                            <div class="split-image">
                                <img src="${not empty news.imageUrl ? news.imageUrl : 'https://owa.bestprice.vn/images/hotels/uploads/vinpearl-resort-spa-ha-long-63351ff6468e4.jpg'}" alt="${news.title}">
                            </div>
                            <div class="split-content">
                                <span class="split-num">
                                    <fmt:formatDate value="${news.createdAt}" pattern="dd/MM/yyyy"/>
                                </span>
                                <h3 class="split-title">${news.title}</h3>
                                <p class="split-desc">${news.content}</p>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </section>

            <section class="section">
                <h2>Không gian nghỉ dưỡng</h2>
                
                <div class="luxury-gallery">
                    <c:forEach var="img" items="${smallImages}">
                        <div class="gallery-item">
                            <img src="${img.imageUrl}" alt="${img.caption}">
                        </div>
                    </c:forEach>
                </div>
            </section>
            <section class="section">
                <h2>Dịch vụ & tiện nghi</h2>

                <div class="grid">
                <c:choose>
                    <c:when test="${not empty services}">
                        <c:forEach var="service" items="${services}">
                            <div class="card">
                                <h3>${service.serviceName}</h3>
                                <p>${service.description}</p>
                                <span class="price">
                                    <c:choose>
                                        <c:when test="${empty service.unitPrice || service.unitPrice < 1}">
                                            Miễn phí
                                        </c:when>
                                        <c:otherwise>
                                            <fmt:formatNumber value="${service.unitPrice}" type="number"/> ₫
                                        </c:otherwise>
                                    </c:choose>
                                </span>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="card">
                            <h3>Rooftop Pool</h3>
                            <p>Hồ bơi vô cực view biển cực chill.</p>
                            <span class="price">Miễn phí</span>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </section>
        </main>

        <jsp:include page="/view/common/footer.jsp" />
        <script src="${pageContext.request.contextPath}/view/assets/javascript/booking-calendar.js"></script>
    </body>
</html>