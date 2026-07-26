<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    request.setAttribute("dateFormatter", dateFormatter);
%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đánh giá khách hàng | La Mer Hotel</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/navbar.css?v=<%= System.currentTimeMillis() %>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/feedback-list.css?v=<%= System.currentTimeMillis() %>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/footer.css?v=<%= System.currentTimeMillis() %>">
    </head>

    <body>
        <header class="feedback-top">
            <jsp:include page="/view/common/navbar.jsp"/>
        </header>

        <main class="feedback-page">
            <c:if test="${not empty feedbackSuccessMessage}">
                <div id="feedbackSuccessMessage" class="feedback-success-message" role="status" aria-live="polite">
                    <span class="feedback-success-icon">✓</span>
                    <span class="feedback-success-text"><c:out value="${feedbackSuccessMessage}"/></span>
                    <button type="button" class="feedback-message-close" aria-label="Đóng thông báo" onclick="closeFeedbackMessage()">×</button>
                </div>
            </c:if>

            <section class="feedback-header">
                <h1 class="feedback-title">ĐÁNH GIÁ TỪ KHÁCH HÀNG</h1>
                <p class="feedback-subtitle">Những chia sẻ chân thực từ khách hàng đã lưu trú tại La Mer Hotel.</p>
            </section>

            <section class="feedback-summary">
                <div class="summary-left">
                    <div class="average-score">${averageRating}<span>/5</span></div>
                    <div class="average-stars">★★★★★</div>
                    <div class="total-review">Dựa trên ${totalFeedbacks} đánh giá</div>
                </div>

                <div class="summary-center">
                    <div class="rating-row">
                        <span>5 ★</span>
                        <div class="rating-bar"><div class="rating-fill" style="width: ${rating5Percent}%"></div></div>
                        <span>${rating5}</span>
                    </div>

                    <div class="rating-row">
                        <span>4 ★</span>
                        <div class="rating-bar"><div class="rating-fill" style="width: ${rating4Percent}%"></div></div>
                        <span>${rating4}</span>
                    </div>

                    <div class="rating-row">
                        <span>3 ★</span>
                        <div class="rating-bar"><div class="rating-fill" style="width: ${rating3Percent}%"></div></div>
                        <span>${rating3}</span>
                    </div>

                    <div class="rating-row">
                        <span>2 ★</span>
                        <div class="rating-bar"><div class="rating-fill" style="width: ${rating2Percent}%"></div></div>
                        <span>${rating2}</span>
                    </div>

                    <div class="rating-row">
                        <span>1 ★</span>
                        <div class="rating-bar"><div class="rating-fill" style="width: ${rating1Percent}%"></div></div>
                        <span>${rating1}</span>
                    </div>
                </div>

                <div class="summary-right">
                    <h3>Đã từng lưu trú?</h3>
                    <p>Bạn chỉ có thể đánh giá sau khi hoàn thành kỳ nghỉ.</p>
                    <a href="${pageContext.request.contextPath}/feedback-submission" class="btn-write-review">VIẾT ĐÁNH GIÁ</a>
                </div>
            </section>

            <section class="review-list">
                <c:choose>
                    <c:when test="${empty feedbacks}">
                        <div class="empty-review">Chưa có đánh giá nào.</div>
                    </c:when>

                    <c:otherwise>
                        <c:forEach items="${feedbacks}" var="fb">
                            <article class="review-card">
                                <div class="review-avatar">
                                    <c:choose>
                                        <c:when test="${not empty fb.guestName}">${fb.guestName.substring(0, 1)}</c:when>
                                        <c:otherwise>K</c:otherwise>
                                    </c:choose>
                                </div>

                                <div class="review-content">
                                    <div class="review-head">
                                        <div class="guest-name">
                                            <c:choose>
                                                <c:when test="${not empty fb.guestName}"><c:out value="${fb.guestName}"/></c:when>
                                                <c:otherwise>Khách hàng</c:otherwise>
                                            </c:choose>
                                        </div>

                                        <div class="review-date">
                                            <c:choose>
                                                <c:when test="${not empty fb.submittedAt}">${fb.submittedAt.format(dateFormatter)}</c:when>
                                                <c:otherwise>Chưa có ngày</c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>

                                    <div class="review-stars">
                                        <c:forEach begin="1" end="5" var="star">
                                            <c:choose>
                                                <c:when test="${star <= fb.rating}">★</c:when>
                                                <c:otherwise>☆</c:otherwise>
                                            </c:choose>
                                        </c:forEach>
                                    </div>

                                    <div class="review-comment"><c:out value="${fb.comment}"/></div>
                                </div>
                            </article>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </section>

            <c:if test="${totalPages > 1}">
                <nav class="simple-pagination" aria-label="Phân trang đánh giá">
                    <c:choose>
                        <c:when test="${totalPages <= 5}">
                            <c:forEach begin="1" end="${totalPages}" var="pageNumber">
                                <c:choose>
                                    <c:when test="${currentPage == pageNumber}">
                                        <span class="simple-page-link active" aria-current="page">${pageNumber}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <a class="simple-page-link"
                                           href="${pageContext.request.contextPath}/feedback-list?page=${pageNumber}">
                                            ${pageNumber}
                                        </a>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </c:when>

                        <c:otherwise>
                            <c:choose>
                                <c:when test="${currentPage == 1}">
                                    <span class="simple-page-link active" aria-current="page">1</span>
                                </c:when>
                                <c:otherwise>
                                    <a class="simple-page-link"
                                       href="${pageContext.request.contextPath}/feedback-list?page=1">
                                        1
                                    </a>
                                </c:otherwise>
                            </c:choose>

                            <c:choose>
                                <c:when test="${currentPage == 2}">
                                    <span class="simple-page-link active" aria-current="page">2</span>
                                </c:when>
                                <c:otherwise>
                                    <a class="simple-page-link"
                                       href="${pageContext.request.contextPath}/feedback-list?page=2">
                                        2
                                    </a>
                                </c:otherwise>
                            </c:choose>

                            <c:choose>
                                <c:when test="${currentPage <= 3}">
                                    <c:choose>
                                        <c:when test="${currentPage == 3}">
                                            <span class="simple-page-link active" aria-current="page">3</span>
                                        </c:when>
                                        <c:otherwise>
                                            <a class="simple-page-link"
                                               href="${pageContext.request.contextPath}/feedback-list?page=3">
                                                3
                                            </a>
                                        </c:otherwise>
                                    </c:choose>

                                    <span class="simple-page-ellipsis">...</span>

                                    <c:forEach begin="${totalPages - 1}" end="${totalPages}" var="pageNumber">
                                        <a class="simple-page-link"
                                           href="${pageContext.request.contextPath}/feedback-list?page=${pageNumber}">
                                            ${pageNumber}
                                        </a>
                                    </c:forEach>
                                </c:when>

                                <c:when test="${currentPage >= totalPages - 2}">
                                    <span class="simple-page-ellipsis">...</span>

                                    <c:forEach begin="${totalPages - 2}" end="${totalPages}" var="pageNumber">
                                        <c:choose>
                                            <c:when test="${currentPage == pageNumber}">
                                                <span class="simple-page-link active" aria-current="page">${pageNumber}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <a class="simple-page-link"
                                                   href="${pageContext.request.contextPath}/feedback-list?page=${pageNumber}">
                                                    ${pageNumber}
                                                </a>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </c:when>

                                <c:otherwise>
                                    <span class="simple-page-ellipsis">...</span>

                                    <span class="simple-page-link active" aria-current="page">${currentPage}</span>

                                    <a class="simple-page-link"
                                       href="${pageContext.request.contextPath}/feedback-list?page=${currentPage + 1}">
                                        ${currentPage + 1}
                                    </a>

                                    <c:if test="${currentPage + 1 < totalPages - 1}">
                                        <span class="simple-page-ellipsis">...</span>
                                    </c:if>

                                    <c:forEach begin="${totalPages - 1}" end="${totalPages}" var="pageNumber">
                                        <a class="simple-page-link"
                                           href="${pageContext.request.contextPath}/feedback-list?page=${pageNumber}">
                                            ${pageNumber}
                                        </a>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </c:otherwise>
                    </c:choose>
                </nav>
            </c:if>
        </main>

        <jsp:include page="/view/common/footer.jsp"/>

        <script>
            const FEEDBACK_MESSAGE_DURATION_MS = 4000;
            const FEEDBACK_MESSAGE_TRANSITION_MS = 300;

            function closeFeedbackMessage() {
                const message = document.getElementById("feedbackSuccessMessage");

                if (!message) {
                    return;
                }

                message.classList.add("hide");

                window.setTimeout(function () {
                    message.remove();
                }, FEEDBACK_MESSAGE_TRANSITION_MS);
            }

            document.addEventListener("DOMContentLoaded", function () {
                const message = document.getElementById("feedbackSuccessMessage");

                if (message) {
                    window.setTimeout(closeFeedbackMessage, FEEDBACK_MESSAGE_DURATION_MS);
                }
            });

            window.addEventListener("pageshow", function (event) {
                if (!event.persisted) {
                    return;
                }

                const message = document.getElementById("feedbackSuccessMessage");

                if (message) {
                    message.remove();
                }
            });
        </script>
    </body>
</html>