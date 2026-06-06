<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chính sách khách sạn - La Mer</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/navbar.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/homepage.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/footer.css">
    </head>

    <body>

        <header class="policy-hero" style="background: #2c3e46; padding-bottom: 20px;">
            <jsp:include page="/view/common/navbar.jsp" />
        </header>

        <main class="container">
            <section class="section policy-section">
                <h2>Chính sách & Quy định</h2>
                <p class="section-intro">Để đảm bảo trải nghiệm nghỉ dưỡng tuyệt vời và an toàn nhất, xin vui lòng lưu ý các quy định chung tại La Mer.</p>

                <div class="policy-grid">
                    <c:choose>
                        <c:when test="${not empty activePoliciesList}">
                            <c:forEach var="policy" items="${activePoliciesList}">
                                <div class="policy-card-item">
                                    <div class="policy-info-content">
                                        <div class="policy-header-row">
                                            <h3 class="policy-title">${policy.policyName}</h3>
                                            <span class="policy-badge">${policy.policyType}</span>
                                        </div>
                                        <p class="policy-desc">${policy.description}</p>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:when>

                        <c:otherwise>
                            <div class="policy-card-item">
                                <div class="policy-info-content">
                                    <div class="policy-header-row">
                                        <h3 class="policy-title">Chính sách Nhận & Trả phòng</h3>
                                        <span class="policy-badge">Giờ giấc</span>
                                    </div>
                                    <p class="policy-desc">Thời gian nhận phòng từ 14:00 và trả phòng trước 12:00 trưa ngày hôm sau. Yêu cầu nhận phòng sớm hoặc trả phòng muộn tùy thuộc vào tình trạng phòng trống và có thể tính thêm phí phụ thu.</p>
                                </div>
                            </div>

                            <div class="policy-card-item">
                                <div class="policy-info-content">
                                    <div class="policy-header-row">
                                        <h3 class="policy-title">Quy định về Thú nuôi</h3>
                                        <span class="policy-badge">Chung</span>
                                    </div>
                                    <p class="policy-desc">Để bảo vệ không gian yên tĩnh và vệ sinh chung cho tất cả các khách lưu trú, khách sạn hiện tại rất tiếc chưa thể tiếp nhận vật nuôi/thú cưng vào khu vực phòng nghỉ và khuôn viên chung.</p>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </section>
        </main>

        <jsp:include page="/view/common/footer.jsp" />
    </body>
</html>