<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>La Mer Hotel</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/navbar.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/homepage.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/footer.css">
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
                    <a class="btn primary" href="${pageContext.request.contextPath}/view/user/booking-form.jsp">Đặt phòng</a>
                    <a class="btn outline" href="${pageContext.request.contextPath}/search">Khám phá</a>
                </div>
            </div>

            <form class="search-bar" action="${pageContext.request.contextPath}/search" method="GET">
                <input type="date" name="checkIn" id="checkIn" required>

                <input type="date" name="checkOut" id="checkOut" required>
                <input type="number" name="roomQuantity" min="1" value="1" required placeholder="Số lượng phòng">

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
                <button type="submit">Tìm phòng</button>
            </form>
        </header>

        <main class="container">

            <!-- SERVICES -->
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
                                            <c:when test="${service.unitPrice == 0}">Miễn phí</c:when>
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

            <!-- GALLERY -->
            <section class="section">
                <h2>Hình ảnh tại La Mer</h2>

                <div class="gallery">
                    <img src="https://happyvivu.com/wp-content/uploads/2023/11/61683532.jpg">
                    <img src="https://vinpearlresortvietnam.com/wp-content/uploads/Ho-boi-tai-Vinpearl-Resort-Spa-Ha-Long-01.jpg">
                    <img src="https://nanotravel.vn/wp-content/uploads/2022/10/vinpearl-resort-spa-ha-long-9.jpg">
                </div>
            </section>

            <!-- POLICY -->
            <section class="section">
                <h2>Chính sách</h2>

                <div class="grid">
                    <div class="card">
                        <h3>Check-in / Check-out</h3>
                        <p>
                            <c:choose>
                                <c:when test="${not empty hotelDetails}">
                                    Check-in: ${hotelDetails.checkinTime} <br>
                                    Check-out: ${hotelDetails.checkoutTime}
                                </c:when>
                                <c:otherwise>
                                    Check-in 14:00 - Check-out 12:00
                                </c:otherwise>
                            </c:choose>
                        </p>
                    </div>

                    <div class="card">
                        <h3>Quy định</h3>
                        <p>
                            <c:out value="${hotelDetails.policies}" default="Không hút thuốc trong phòng."/>
                        </p>
                    </div>
                </div>
            </section>

        </main>

        <jsp:include page="/view/common/footer.jsp" />
        <script src="${pageContext.request.contextPath}/view/assets/javascript/booking-calendar.js"></script>
    </body>
</html>