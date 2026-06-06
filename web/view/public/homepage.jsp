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

           <section class="section">
    <h2>Trải nghiệm tại La Mer</h2>

    <div class="gallery-split-container">
        <!-- Hàng 1: Ảnh bên TRÁI - Chữ bên PHẢI (Hồ bơi) -->
        <div class="split-row">
            <div class="split-image">
                <img src="https://dynamic-media-cdn.tripadvisor.com/media/photo-o/0d/ff/4c/58/vinpearl-ha-long-bay.jpg?w=1800&h=-1&s=1" alt="The Oasis Pool">
            </div>
            <div class="split-content">
                <span class="split-num">01 / The Oasis</span>
                <h3 class="split-title">Hồ bơi vô cực đại cảnh</h3>
                <p class="split-desc">Đắm mình trong làn nước mát lành và phóng tầm mắt trọn vẹn ra đại dương xa xăm. Nơi ranh giới giữa mây trời và mặt nước như hòa làm một, mang đến những phút giây tĩnh lặng tuyệt đối giữa thiên nhiên biển khơi.</p>
            </div>
        </div>

        <!-- Hàng 2: Ảnh bên PHẢI - Chữ bên TRÁI (Trà chiều bãi biển) -->
        <div class="split-row reverse">
            <div class="split-image">
                <img src="https://i.pinimg.com/736x/56/e0/d8/56e0d8ec5b706d92fe8a65a31462c4ea.jpg" alt="Coastal Afternoon Tea">
            </div>
            <div class="split-content">
                <span class="split-num">02 / Coastal Tea Time</span>
                <h3 class="split-title">Trà chiều bên bờ biển</h3>
                <p class="split-desc">Thưởng thức những tách trà thượng hạng kèm bánh ngọt thủ công khi hoàng hôn buông xuống. Lắng nghe tiếng sóng vỗ về ngày mới và cảm nhận làn gió biển mơn man, tạo nên một buổi chiều lãng mạn khó quên.</p>
            </div>
        </div>

        <!-- Hàng 3: Ảnh bên TRÁI - Chữ bên PHẢI (Spa) -->
        <div class="split-row">
            <div class="split-image">
                <img src="https://i.pinimg.com/736x/19/e9/97/19e997f01976241cada0b14047d47842.jpg" alt="La Mer Spa">
            </div>
            <div class="split-content">
                <span class="split-num">03 / Wellness Center</span>
                <h3 class="split-title">Liệu trình Spa & Massage</h3>
                <p class="split-desc">Nuông chiều cơ thể và đánh thức các giác quan bằng những liệu pháp trị liệu truyền thống kết hợp tinh dầu thảo mộc tự nhiên. Không gian thoảng hương sả chanh dịu nhẹ sẽ rũ bỏ mọi mệt mỏi, tái tạo năng lượng từ sâu bên trong.</p>
            </div>
        </div>
    </div>
</section>
            <section class="section">
    <h2>Không gian nghỉ dưỡng</h2>
   
    <div class="luxury-gallery">

        <div class="gallery-item">
            <img src="https://dynamic-media-cdn.tripadvisor.com/media/photo-o/0a/6b/ad/0c/president-room.jpg?w=2000&h=-1&s=1" alt="">
        </div>

        <div class="gallery-item">
            <img src="https://dynamic-media-cdn.tripadvisor.com/media/photo-o/09/76/5c/f1/vinpearl-ha-long-bay.jpg?w=1600&h=-1&s=1" alt="">
        </div>

        <div class="gallery-item">
            <img src="https://dynamic-media-cdn.tripadvisor.com/media/photo-o/09/76/5c/ee/vinpearl-ha-long-bay.jpg?w=1600&h=-1&s=1" alt="">
        </div>

        <div class="gallery-item">
            <img src="https://statics.vinpearl.com/villa-4-phong-ngu_1725028102.jpg" alt="">
        </div>

        <div class="gallery-item">
            <img src="https://hethongvinpearlresort.com/img_data/images/villa-4-phong-ngu-vinpearl-nha-trang-phong-ngu-giuong-don.jpg" alt="">
        </div>

        <div class="gallery-item">
            <img src="https://vinpearlresortvietnam.com/wp-content/uploads/vinpearl-discovery-sealink-nha-trang-villa-4-bed-pool-view-3.jpg" alt="">
        </div>

    </div>
</section>
        </main>

        <jsp:include page="/view/common/footer.jsp" />
        <script src="${pageContext.request.contextPath}/view/assets/javascript/booking-calendar.js"></script>
    </body>
</html>