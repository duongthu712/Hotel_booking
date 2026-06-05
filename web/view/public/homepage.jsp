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

            <section class="section">
                <h2>Hình ảnh tại La Mer</h2>

                <div class="gallery-grid">
                    <div class="gallery-item">
                        <img src="https://happyvivu.com/wp-content/uploads/2023/11/61683532.jpg" alt="La Mer Resort">
                        <div class="gallery-overlay"></div>
                    </div>
                    <div class="gallery-item">
                        <img src="https://vinpearlresortvietnam.com/wp-content/uploads/Ho-boi-tai-Vinpearl-Resort-Spa-Ha-Long-01.jpg" alt="La Mer Pool">
                        <div class="gallery-overlay"></div>
                    </div>
                    <div class="gallery-item">
                        <img src="https://i.pinimg.com/736x/72/f9/22/72f922ce30fbe13b340da97421603100.jpg" alt="La Mer View">
                        <div class="gallery-overlay"></div>
                    </div>
                    <div class="gallery-item">
                        <img src="https://dynamic-media-cdn.tripadvisor.com/media/photo-o/0d/ff/4c/39/vinpearl-ha-long-bay.jpg?w=1800&h=-1&s=1" alt="La Mer Beach">
                        <div class="gallery-overlay"></div>
                    </div>
                    <div class="gallery-item">
                        <img src="https://i.pinimg.com/736x/56/e0/d8/56e0d8ec5b706d92fe8a65a31462c4ea.jpg" alt="La Mer Luxury Room">
                        <div class="gallery-overlay"></div>
                    </div>
                    <div class="gallery-item">
                        <img src="https://dynamic-media-cdn.tripadvisor.com/media/photo-o/1c/4b/61/84/img-20191227-202635-762.jpg?w=1000&h=-1&s=1">
                        <div class="gallery-overlay"></div>
                    </div>
                </div>
            </section>

        </main>

        <jsp:include page="/view/common/footer.jsp" />
        <script src="${pageContext.request.contextPath}/view/assets/javascript/booking-calendar.js"></script>
    </body>
</html>