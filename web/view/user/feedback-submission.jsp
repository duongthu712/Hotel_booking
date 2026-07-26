<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <title>Viết đánh giá | La Mer Hotel</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/navbar.css?v=<%= System.currentTimeMillis() %>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/feedback-submission.css?v=<%= System.currentTimeMillis() %>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/footer.css?v=<%= System.currentTimeMillis() %>">
    </head>

    <body>
        <header class="feedback-submit-top">
            <jsp:include page="/view/common/navbar.jsp"/>
        </header>

        <main class="feedback-submit-page">
            <section class="feedback-submit-card">
                <h1>CHIA SẺ TRẢI NGHIỆM CỦA BẠN</h1>

                <p class="subtitle">
                    Nhập thông tin đặt phòng để gửi đánh giá sau khi hoàn thành kỳ nghỉ.
                </p>

                <c:if test="${not empty error}">
                    <div class="error-box">
                        <c:out value="${error}"/>
                    </div>
                </c:if>

                <c:choose>
                    <c:when test="${showFeedbackForm}">
                        <div class="guest-box">
                            <div class="guest-avatar">
                                <c:choose>
                                    <c:when test="${not empty guestName}">
                                        ${guestName.substring(0, 1)}
                                    </c:when>

                                    <c:otherwise>K</c:otherwise>
                                </c:choose>
                            </div>

                            <div>
                                <span>Khách hàng</span>

                                <strong>
                                    <c:out value="${guestName}"/>
                                </strong>

                                <small>
                                    Mã đặt phòng:
                                    <c:out value="${bookingCode}"/>
                                </small>
                            </div>
                        </div>

                        <form action="${pageContext.request.contextPath}/feedback-submission" method="post" class="feedback-form">
                            <input type="hidden" name="action" value="submit">
                            <input type="hidden" name="bookingId" value="${bookingId}">

                            <label>Bạn đánh giá kỳ nghỉ như thế nào?</label>

                            <div class="star-select">
                                <input type="radio" name="rating" id="star5" value="5" ${ratingInput == 5 ? 'checked' : ''} required>
                                <label for="star5">★</label>

                                <input type="radio" name="rating" id="star4" value="4" ${ratingInput == 4 ? 'checked' : ''}>
                                <label for="star4">★</label>

                                <input type="radio" name="rating" id="star3" value="3" ${ratingInput == 3 ? 'checked' : ''}>
                                <label for="star3">★</label>

                                <input type="radio" name="rating" id="star2" value="2" ${ratingInput == 2 ? 'checked' : ''}>
                                <label for="star2">★</label>

                                <input type="radio" name="rating" id="star1" value="1" ${ratingInput == 1 ? 'checked' : ''}>
                                <label for="star1">★</label>
                            </div>

                            <label for="comment">Chia sẻ trải nghiệm</label>

                            <textarea id="comment"
                                      name="comment"
                                      maxlength="500"
                                      required
                                      placeholder="Hãy chia sẻ cảm nhận của bạn về phòng ốc, dịch vụ, nhân viên..."><c:out value="${commentInput}"/></textarea>

                            <button type="submit">
                                GỬI ĐÁNH GIÁ
                            </button>
                        </form>
                    </c:when>

                    <c:otherwise>
                        <form action="${pageContext.request.contextPath}/feedback-submission" method="post" class="lookup-form">
                            <input type="hidden" name="action" value="lookup">

                            <div class="form-group">
                                <label for="bookingCode">Mã đặt phòng</label>

                                <input type="text"
                                       id="bookingCode"
                                       name="bookingCode"
                                       value="${bookingCodeInput}"
                                       placeholder="Ví dụ: LMHABC123"
                                       required>
                            </div>

                            <div class="form-group">
                                <label for="email">Email đặt phòng</label>

                                <input type="email"
                                       id="email"
                                       name="email"
                                       value="${emailInput}"
                                       placeholder="example@gmail.com"
                                       required>
                            </div>

                            <button type="submit">
                                KIỂM TRA ĐƠN ĐẶT PHÒNG
                            </button>
                        </form>
                    </c:otherwise>
                </c:choose>
            </section>
        </main>

        <jsp:include page="/view/common/footer.jsp"/>
    </body>
</html>