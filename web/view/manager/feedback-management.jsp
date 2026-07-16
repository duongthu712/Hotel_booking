<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%
    DateTimeFormatter dateTimeFormatter
            = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    request.setAttribute("dateTimeFormatter", dateTimeFormatter);
%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport"
              content="width=device-width, initial-scale=1.0">

        <title>Quản lý đánh giá | La Mer Hotel</title>

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/view/assets/css/common.css?v=<%= System.currentTimeMillis() %>">

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/view/assets/css/feedback-management.css?v=<%= System.currentTimeMillis() %>">
    </head>

    <body>

        <%@ include file="/view/staff/header.jsp" %>
        <%@ include file="/view/staff/navbar.jsp" %>

        <main class="content-container feedback-management-page">

            <header class="management-header">

                <div>
                    <h1>QUẢN LÝ ĐÁNH GIÁ</h1>

                    <p>
                        Theo dõi, tìm kiếm và quản lý các phản hồi
                        được gửi bởi khách hàng.
                    </p>
                </div>

                <a href="${pageContext.request.contextPath}/feedback-list"
                   class="public-feedback-link"
                   target="_blank"
                   rel="noopener noreferrer">
                    XEM TRANG ĐÁNH GIÁ
                </a>

            </header>

            <c:if test="${not empty param.success}">
                <div class="management-message success">
                    <span>✓</span>
                    <c:out value="${param.success}"/>
                </div>
            </c:if>

            <c:if test="${not empty param.error}">
                <div class="management-message error">
                    <span>!</span>
                    <c:out value="${param.error}"/>
                </div>
            </c:if>

            <c:if test="${not empty error}">
                <div class="management-message error">
                    <span>!</span>
                    <c:out value="${error}"/>
                </div>
            </c:if>

            <section class="feedback-statistics-grid">

                <article class="feedback-statistic-card">

                    <div class="statistic-card-head">
                        <span>TỔNG ĐÁNH GIÁ</span>
                        <span class="statistic-icon">▤</span>
                    </div>

                    <strong>${totalFeedbacks}</strong>

                    <small>
                        Tổng phản hồi khách hàng đã gửi
                    </small>

                </article>

                <article class="feedback-statistic-card">

                    <div class="statistic-card-head">
                        <span>ĐIỂM TRUNG BÌNH</span>
                        <span class="statistic-icon">★</span>
                    </div>

                    <strong>
                        ${averageRating}
                        <span class="statistic-unit">/5</span>
                    </strong>

                    <small>
                        Điểm trung bình của tất cả đánh giá
                    </small>

                </article>

                <article class="feedback-statistic-card">

                    <div class="statistic-card-head">
                        <span>ĐANG HIỂN THỊ</span>
                        <span class="statistic-icon">◉</span>
                    </div>

                    <strong>${visibleFeedbacks}</strong>

                    <small>
                        ${visiblePercent}% tổng số đánh giá
                    </small>

                </article>

                <article class="feedback-statistic-card">

                    <div class="statistic-card-head">
                        <span>ĐÃ ẨN</span>
                        <span class="statistic-icon">⊘</span>
                    </div>

                    <strong>${hiddenFeedbacks}</strong>

                    <small>
                        ${hiddenPercent}% tổng số đánh giá
                    </small>

                </article>

            </section>

            <section class="feedback-insight-grid">

                <article class="feedback-insight-card">

                    <div class="insight-card-title">

                        <div>
                            <h2>PHÂN BỐ ĐÁNH GIÁ</h2>
                            <p>Số lượng phản hồi theo mức sao</p>
                        </div>

                        <span class="insight-average">
                            ${averageRating}/5
                        </span>

                    </div>

                    <div class="rating-distribution">

                        <div class="distribution-row">
                            <span class="distribution-label">5 ★</span>

                            <div class="distribution-track">
                                <div class="distribution-fill"
                                     style="width: ${rating5Percent}%;">
                                </div>
                            </div>

                            <strong>${rating5}</strong>
                        </div>

                        <div class="distribution-row">
                            <span class="distribution-label">4 ★</span>

                            <div class="distribution-track">
                                <div class="distribution-fill"
                                     style="width: ${rating4Percent}%;">
                                </div>
                            </div>

                            <strong>${rating4}</strong>
                        </div>

                        <div class="distribution-row">
                            <span class="distribution-label">3 ★</span>

                            <div class="distribution-track">
                                <div class="distribution-fill"
                                     style="width: ${rating3Percent}%;">
                                </div>
                            </div>

                            <strong>${rating3}</strong>
                        </div>

                        <div class="distribution-row">
                            <span class="distribution-label">2 ★</span>

                            <div class="distribution-track">
                                <div class="distribution-fill"
                                     style="width: ${rating2Percent}%;">
                                </div>
                            </div>

                            <strong>${rating2}</strong>
                        </div>

                        <div class="distribution-row">
                            <span class="distribution-label">1 ★</span>

                            <div class="distribution-track">
                                <div class="distribution-fill"
                                     style="width: ${rating1Percent}%;">
                                </div>
                            </div>

                            <strong>${rating1}</strong>
                        </div>

                    </div>

                </article>

                <article class="feedback-insight-card">

                    <div class="insight-card-title">

                        <div>
                            <h2>TRẠNG THÁI HIỂN THỊ</h2>
                            <p>Tình trạng phản hồi trên trang công khai</p>
                        </div>

                    </div>

                    <div class="visibility-summary">

                        <div class="visibility-circle"
                             style="--visible-percent: ${visiblePercent}%;">
                            <div class="visibility-circle-inner">
                                <strong>${visiblePercent}%</strong>
                                <span>Hiển thị</span>
                            </div>
                        </div>

                        <div class="visibility-details">

                            <div class="visibility-detail-row">
                                <span>
                                    <i class="visibility-dot visible"></i>
                                    Đang hiển thị
                                </span>

                                <strong>${visibleFeedbacks}</strong>
                            </div>

                            <div class="visibility-detail-row">
                                <span>
                                    <i class="visibility-dot hidden"></i>
                                    Đã ẩn
                                </span>

                                <strong>${hiddenFeedbacks}</strong>
                            </div>

                            <div class="visibility-progress">

                                <div class="visibility-progress-visible"
                                     style="width: ${visiblePercent}%;">
                                </div>

                                <div class="visibility-progress-hidden"
                                     style="width: ${hiddenPercent}%;">
                                </div>

                            </div>

                        </div>

                    </div>

                </article>

            </section>

            <section class="feedback-list-card">

                <div class="feedback-list-heading">

                    <div>
                        <h2>DANH SÁCH ĐÁNH GIÁ</h2>

                        <p>
                            Tìm thấy
                            <strong>${totalFilteredFeedbacks}</strong>
                            kết quả phù hợp.
                        </p>
                    </div>

                </div>

                <form action="${pageContext.request.contextPath}/feedback-management"
                      method="GET"
                      class="feedback-filter-form">

                    <div class="filter-search-group">

                        <label for="keyword">Tìm kiếm</label>

                        <input type="text"
                               id="keyword"
                               name="keyword"
                               value="${fn:escapeXml(keyword)}"
                               placeholder="Tên khách, email, mã booking hoặc nội dung">

                    </div>

                    <div class="filter-select-group">

                        <label for="rating">Số sao</label>

                        <select id="rating" name="rating">

                            <option value="all"
                                    ${empty selectedRating ? 'selected' : ''}>
                                Tất cả
                            </option>

                            <option value="5"
                                    ${selectedRating == 5 ? 'selected' : ''}>
                                5 sao
                            </option>

                            <option value="4"
                                    ${selectedRating == 4 ? 'selected' : ''}>
                                4 sao
                            </option>

                            <option value="3"
                                    ${selectedRating == 3 ? 'selected' : ''}>
                                3 sao
                            </option>

                            <option value="2"
                                    ${selectedRating == 2 ? 'selected' : ''}>
                                2 sao
                            </option>

                            <option value="1"
                                    ${selectedRating == 1 ? 'selected' : ''}>
                                1 sao
                            </option>

                        </select>

                    </div>

                    <div class="filter-select-group">

                        <label for="visible">Trạng thái</label>

                        <select id="visible" name="visible">

                            <option value="all"
                                    ${empty selectedVisible ? 'selected' : ''}>
                                Tất cả
                            </option>

                            <option value="visible"
                                    ${selectedVisible == true ? 'selected' : ''}>
                                Đang hiển thị
                            </option>

                            <option value="hidden"
                                    ${selectedVisible == false ? 'selected' : ''}>
                                Đã ẩn
                            </option>

                        </select>

                    </div>

                    <div class="filter-select-group">

                        <label for="sort">Sắp xếp</label>

                        <select id="sort" name="sort">

                            <option value="newest"
                                    ${selectedSort eq 'newest' ? 'selected' : ''}>
                                Mới nhất
                            </option>

                            <option value="oldest"
                                    ${selectedSort eq 'oldest' ? 'selected' : ''}>
                                Cũ nhất
                            </option>

                            <option value="rating-high"
                                    ${selectedSort eq 'rating-high' ? 'selected' : ''}>
                                Điểm cao nhất
                            </option>

                            <option value="rating-low"
                                    ${selectedSort eq 'rating-low' ? 'selected' : ''}>
                                Điểm thấp nhất
                            </option>

                        </select>

                    </div>

                    <div class="filter-actions">

                        <button type="submit"
                                class="filter-submit-button">
                            TÌM KIẾM
                        </button>

                        <a href="${pageContext.request.contextPath}/feedback-management"
                           class="filter-reset-button">
                            ĐẶT LẠI
                        </a>

                    </div>

                </form>

                <div class="feedback-table-wrapper">

                    <table class="feedback-management-table">

                        <thead>
                            <tr>
                                <th>Mã đặt phòng</th>
                                <th>Khách hàng</th>
                                <th>Đánh giá</th>
                                <th>Nội dung</th>
                                <th>Ngày gửi</th>
                                <th>Trạng thái</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>

                        <tbody>

                            <c:choose>

                                <c:when test="${empty feedbacks}">

                                    <tr>
                                        <td colspan="7"
                                            class="feedback-empty-cell">

                                            <div class="feedback-empty-state">

                                                <span>★</span>

                                                <strong>
                                                    Không tìm thấy đánh giá
                                                </strong>

                                                <p>
                                                    Hãy thay đổi điều kiện tìm kiếm
                                                    hoặc bộ lọc.
                                                </p>

                                            </div>

                                        </td>
                                    </tr>

                                </c:when>

                                <c:otherwise>

                                    <c:forEach items="${feedbacks}"
                                               var="feedback">

                                        <tr>

                                            <td>
                                                <strong class="booking-code-cell">
                                                    <c:out value="${feedback.bookingCode}"/>
                                                </strong>
                                            </td>

                                            <td>

                                                <div class="feedback-guest-cell">

                                                    <div class="feedback-avatar">

                                                        <c:choose>

                                                            <c:when test="${not empty feedback.guestName}">
                                                                ${fn:substring(feedback.guestName, 0, 1)}
                                                            </c:when>

                                                            <c:otherwise>
                                                                K
                                                            </c:otherwise>

                                                        </c:choose>

                                                    </div>

                                                    <div>

                                                        <strong>
                                                            <c:out value="${feedback.guestName}"/>
                                                        </strong>

                                                        <span>
                                                            <c:out value="${feedback.guestEmail}"/>
                                                        </span>

                                                    </div>

                                                </div>

                                            </td>

                                            <td>

                                                <div class="feedback-rating-cell">

                                                    <div class="feedback-stars">

                                                        <c:forEach begin="1"
                                                                   end="5"
                                                                   var="star">

                                                            <span class="${star <= feedback.rating ? 'active' : ''}">
                                                                ★
                                                            </span>

                                                        </c:forEach>

                                                    </div>

                                                    <strong>
                                                        ${feedback.rating}/5
                                                    </strong>

                                                </div>

                                            </td>

                                            <td>

                                                <div class="feedback-comment-cell">

                                                    <c:choose>

                                                        <c:when test="${empty feedback.comment}">
                                                            <span class="empty-comment">
                                                                Không có nội dung
                                                            </span>
                                                        </c:when>

                                                        <c:when test="${fn:length(feedback.comment) > 100}">
                                                            <c:out value="${fn:substring(feedback.comment, 0, 100)}"/>...
                                                        </c:when>

                                                        <c:otherwise>
                                                            <c:out value="${feedback.comment}"/>
                                                        </c:otherwise>

                                                    </c:choose>

                                                </div>

                                            </td>

                                            <td>

                                                <div class="feedback-date-cell">

                                                    <c:choose>

                                                        <c:when test="${not empty feedback.submittedAt}">
                                                            ${feedback.submittedAt.format(dateTimeFormatter)}
                                                        </c:when>

                                                        <c:otherwise>
                                                            Chưa có ngày
                                                        </c:otherwise>

                                                    </c:choose>

                                                </div>

                                            </td>

                                            <td>

                                                <c:choose>

                                                    <c:when test="${feedback.visible}">
                                                        <span class="feedback-status visible">
                                                            Đang hiển thị
                                                        </span>
                                                    </c:when>

                                                    <c:otherwise>
                                                        <span class="feedback-status hidden">
                                                            Đã ẩn
                                                        </span>
                                                    </c:otherwise>

                                                </c:choose>

                                            </td>

                                            <td>

                                                <div class="feedback-action-group">

                                                    <c:url var="detailUrl"
                                                           value="/feedback-management">

                                                        <c:param name="feedbackId"
                                                                 value="${feedback.feedbackId}"/>

                                                        <c:if test="${not empty keyword}">
                                                            <c:param name="keyword"
                                                                     value="${keyword}"/>
                                                        </c:if>

                                                        <c:if test="${not empty selectedRating}">
                                                            <c:param name="rating"
                                                                     value="${selectedRating}"/>
                                                        </c:if>

                                                        <c:choose>

                                                            <c:when test="${selectedVisible == true}">
                                                                <c:param name="visible"
                                                                         value="visible"/>
                                                            </c:when>

                                                            <c:when test="${selectedVisible == false}">
                                                                <c:param name="visible"
                                                                         value="hidden"/>
                                                            </c:when>

                                                        </c:choose>

                                                        <c:param name="sort"
                                                                 value="${selectedSort}"/>

                                                        <c:param name="page"
                                                                 value="${currentPage}"/>

                                                    </c:url>

                                                    <a href="${detailUrl}"
                                                       class="feedback-action-button view">
                                                        Xem
                                                    </a>

                                                    <c:choose>

                                                        <c:when test="${feedback.visible}">

                                                            <button type="button"
                                                                    class="feedback-action-button hide"
                                                                    data-feedback-id="${feedback.feedbackId}"
                                                                    data-booking-code="${fn:escapeXml(feedback.bookingCode)}"
                                                                    onclick="openHideModal(this)">
                                                                Ẩn
                                                            </button>

                                                        </c:when>

                                                        <c:otherwise>

                                                            <form action="${pageContext.request.contextPath}/feedback-management"
                                                                  method="POST"
                                                                  class="inline-action-form">

                                                                <input type="hidden"
                                                                       name="action"
                                                                       value="show">

                                                                <input type="hidden"
                                                                       name="feedbackId"
                                                                       value="${feedback.feedbackId}">

                                                                <input type="hidden"
                                                                       name="keyword"
                                                                       value="${fn:escapeXml(keyword)}">

                                                                <input type="hidden"
                                                                       name="rating"
                                                                       value="${empty selectedRating ? 'all' : selectedRating}">

                                                                <input type="hidden"
                                                                       name="visible"
                                                                       value="${selectedVisible == true
                                                                                ? 'visible'
                                                                                : selectedVisible == false
                                                                                ? 'hidden'
                                                                                : 'all'}">

                                                                <input type="hidden"
                                                                       name="sort"
                                                                       value="${selectedSort}">

                                                                <input type="hidden"
                                                                       name="page"
                                                                       value="${currentPage}">

                                                                <button type="submit"
                                                                        class="feedback-action-button show"
                                                                        onclick="return confirm('Hiển thị lại đánh giá này trên trang khách hàng?');">
                                                                    Hiện lại
                                                                </button>

                                                            </form>

                                                        </c:otherwise>

                                                    </c:choose>

                                                </div>

                                            </td>

                                        </tr>

                                    </c:forEach>

                                </c:otherwise>

                            </c:choose>

                        </tbody>

                    </table>

                </div>

                <c:if test="${totalPages > 1}">

                    <div class="feedback-pagination">

                        <c:if test="${currentPage > 1}">

                            <c:url var="previousPageUrl"
                                   value="/feedback-management">

                                <c:param name="page"
                                         value="${currentPage - 1}"/>

                                <c:if test="${not empty keyword}">
                                    <c:param name="keyword"
                                             value="${keyword}"/>
                                </c:if>

                                <c:if test="${not empty selectedRating}">
                                    <c:param name="rating"
                                             value="${selectedRating}"/>
                                </c:if>

                                <c:choose>

                                    <c:when test="${selectedVisible == true}">
                                        <c:param name="visible"
                                                 value="visible"/>
                                    </c:when>

                                    <c:when test="${selectedVisible == false}">
                                        <c:param name="visible"
                                                 value="hidden"/>
                                    </c:when>

                                </c:choose>

                                <c:param name="sort"
                                         value="${selectedSort}"/>

                            </c:url>

                            <a href="${previousPageUrl}"
                               class="pagination-button arrow">
                                ‹
                            </a>

                        </c:if>

                        <c:forEach begin="${startPage}"
                                   end="${endPage}"
                                   var="pageNumber">

                            <c:url var="pageUrl"
                                   value="/feedback-management">

                                <c:param name="page"
                                         value="${pageNumber}"/>

                                <c:if test="${not empty keyword}">
                                    <c:param name="keyword"
                                             value="${keyword}"/>
                                </c:if>

                                <c:if test="${not empty selectedRating}">
                                    <c:param name="rating"
                                             value="${selectedRating}"/>
                                </c:if>

                                <c:choose>

                                    <c:when test="${selectedVisible == true}">
                                        <c:param name="visible"
                                                 value="visible"/>
                                    </c:when>

                                    <c:when test="${selectedVisible == false}">
                                        <c:param name="visible"
                                                 value="hidden"/>
                                    </c:when>

                                </c:choose>

                                <c:param name="sort"
                                         value="${selectedSort}"/>

                            </c:url>

                            <a href="${pageUrl}"
                               class="pagination-button ${currentPage == pageNumber ? 'active' : ''}">
                                ${pageNumber}
                            </a>

                        </c:forEach>

                        <c:if test="${currentPage < totalPages}">

                            <c:url var="nextPageUrl"
                                   value="/feedback-management">

                                <c:param name="page"
                                         value="${currentPage + 1}"/>

                                <c:if test="${not empty keyword}">
                                    <c:param name="keyword"
                                             value="${keyword}"/>
                                </c:if>

                                <c:if test="${not empty selectedRating}">
                                    <c:param name="rating"
                                             value="${selectedRating}"/>
                                </c:if>

                                <c:choose>

                                    <c:when test="${selectedVisible == true}">
                                        <c:param name="visible"
                                                 value="visible"/>
                                    </c:when>

                                    <c:when test="${selectedVisible == false}">
                                        <c:param name="visible"
                                                 value="hidden"/>
                                    </c:when>

                                </c:choose>

                                <c:param name="sort"
                                         value="${selectedSort}"/>

                            </c:url>

                            <a href="${nextPageUrl}"
                               class="pagination-button arrow">
                                ›
                            </a>

                        </c:if>

                    </div>

                </c:if>

            </section>

        </main>

        <div class="management-modal"
             id="hideFeedbackModal">

            <div class="management-modal-overlay"
                 onclick="closeHideModal()">
            </div>

            <section class="management-modal-dialog small">

                <button type="button"
                        class="management-modal-close"
                        onclick="closeHideModal()">
                    ×
                </button>

                <div class="modal-warning-icon">
                    !
                </div>

                <h2>ẨN ĐÁNH GIÁ</h2>

                <p class="modal-description">
                    Đánh giá sẽ không còn xuất hiện trên trang công khai.
                    Quản lý vẫn có thể hiển thị lại sau này.
                </p>

                <div class="hide-booking-info">
                    Mã đặt phòng:
                    <strong id="hideBookingCode"></strong>
                </div>

                <form action="${pageContext.request.contextPath}/feedback-management"
                      method="POST"
                      class="hide-feedback-form">

                    <input type="hidden"
                           name="action"
                           value="hide">

                    <input type="hidden"
                           name="feedbackId"
                           id="hideFeedbackId">

                    <input type="hidden"
                           name="keyword"
                           value="${fn:escapeXml(keyword)}">

                    <input type="hidden"
                           name="rating"
                           value="${empty selectedRating ? 'all' : selectedRating}">

                    <input type="hidden"
                           name="visible"
                           value="${selectedVisible == true
                                    ? 'visible'
                                    : selectedVisible == false
                                    ? 'hidden'
                                    : 'all'}">

                    <input type="hidden"
                           name="sort"
                           value="${selectedSort}">

                    <input type="hidden"
                           name="page"
                           value="${currentPage}">

                    <label for="hiddenReason">
                        Lý do ẩn đánh giá
                    </label>

                    <textarea id="hiddenReason"
                              name="hiddenReason"
                              maxlength="255"
                              required
                              placeholder="Nhập lý do ẩn đánh giá..."></textarea>

                    <div class="modal-action-row">

                        <button type="button"
                                class="modal-cancel-button"
                                onclick="closeHideModal()">
                            HỦY
                        </button>

                        <button type="submit"
                                class="modal-confirm-button danger">
                            XÁC NHẬN ẨN
                        </button>

                    </div>

                </form>

            </section>

        </div>

        <c:if test="${openDetailModal and not empty selectedFeedback}">

            <div class="management-modal open"
                 id="feedbackDetailModal">

                <div class="management-modal-overlay"
                     onclick="closeDetailModal()">
                </div>

                <section class="management-modal-dialog detail">

                    <c:url var="closeDetailUrl"
                           value="/feedback-management">

                        <c:if test="${not empty keyword}">
                            <c:param name="keyword"
                                     value="${keyword}"/>
                        </c:if>

                        <c:if test="${not empty selectedRating}">
                            <c:param name="rating"
                                     value="${selectedRating}"/>
                        </c:if>

                        <c:choose>

                            <c:when test="${selectedVisible == true}">
                                <c:param name="visible"
                                         value="visible"/>
                            </c:when>

                            <c:when test="${selectedVisible == false}">
                                <c:param name="visible"
                                         value="hidden"/>
                            </c:when>

                        </c:choose>

                        <c:param name="sort"
                                 value="${selectedSort}"/>

                        <c:param name="page"
                                 value="${currentPage}"/>

                    </c:url>

                    <a href="${closeDetailUrl}"
                       class="management-modal-close">
                        ×
                    </a>

                    <div class="detail-modal-heading">

                        <div>

                            <span class="detail-modal-label">
                                CHI TIẾT ĐÁNH GIÁ
                            </span>

                            <h2>
                                <c:out value="${selectedFeedback.guestName}"/>
                            </h2>

                        </div>

                        <c:choose>

                            <c:when test="${selectedFeedback.visible}">
                                <span class="feedback-status visible">
                                    Đang hiển thị
                                </span>
                            </c:when>

                            <c:otherwise>
                                <span class="feedback-status hidden">
                                    Đã ẩn
                                </span>
                            </c:otherwise>

                        </c:choose>

                    </div>

                    <div class="detail-modal-information">

                        <div class="detail-information-item">

                            <span>Mã đặt phòng</span>

                            <strong>
                                <c:out value="${selectedFeedback.bookingCode}"/>
                            </strong>

                        </div>

                        <div class="detail-information-item">

                            <span>Email khách hàng</span>

                            <strong>
                                <c:out value="${selectedFeedback.guestEmail}"/>
                            </strong>

                        </div>

                        <div class="detail-information-item">

                            <span>Ngày gửi</span>

                            <strong>

                                <c:choose>

                                    <c:when test="${not empty selectedFeedback.submittedAt}">
                                        ${selectedFeedback.submittedAt.format(dateTimeFormatter)}
                                    </c:when>

                                    <c:otherwise>
                                        Chưa có ngày
                                    </c:otherwise>

                                </c:choose>

                            </strong>

                        </div>

                        <div class="detail-information-item">

                            <span>Đánh giá</span>

                            <strong class="detail-rating-value">
                                ${selectedFeedback.rating}/5
                            </strong>

                        </div>

                    </div>

                    <div class="detail-modal-rating">

                        <c:forEach begin="1"
                                   end="5"
                                   var="detailStar">

                            <span class="${detailStar <= selectedFeedback.rating ? 'active' : ''}">
                                ★
                            </span>

                        </c:forEach>

                    </div>

                    <div class="detail-comment-box">

                        <span>Nội dung phản hồi</span>

                        <p>

                            <c:choose>

                                <c:when test="${not empty selectedFeedback.comment}">
                                    <c:out value="${selectedFeedback.comment}"/>
                                </c:when>

                                <c:otherwise>
                                    Khách hàng không để lại nội dung chi tiết.
                                </c:otherwise>

                            </c:choose>

                        </p>

                    </div>

                    <c:if test="${not selectedFeedback.visible}">

                        <div class="hidden-information-box">

                            <div>

                                <span>Thời gian ẩn</span>

                                <strong>

                                    <c:choose>

                                        <c:when test="${not empty selectedFeedback.hiddenAt}">
                                            ${selectedFeedback.hiddenAt.format(dateTimeFormatter)}
                                        </c:when>

                                        <c:otherwise>
                                            Không có thông tin
                                        </c:otherwise>

                                    </c:choose>

                                </strong>

                            </div>

                            <div>

                                <span>Lý do ẩn</span>

                                <strong>

                                    <c:choose>

                                        <c:when test="${not empty selectedFeedback.hiddenReason}">
                                            <c:out value="${selectedFeedback.hiddenReason}"/>
                                        </c:when>

                                        <c:otherwise>
                                            Không có lý do
                                        </c:otherwise>

                                    </c:choose>

                                </strong>

                            </div>

                        </div>

                    </c:if>

                    <div class="detail-modal-actions">

                        <a href="${closeDetailUrl}"
                           class="modal-cancel-button">
                            ĐÓNG
                        </a>

                        <c:choose>

                            <c:when test="${selectedFeedback.visible}">

                                <button type="button"
                                        class="modal-confirm-button danger"
                                        data-feedback-id="${selectedFeedback.feedbackId}"
                                        data-booking-code="${fn:escapeXml(selectedFeedback.bookingCode)}"
                                        onclick="closeDetailModalAndOpenHide(this)">
                                    ẨN ĐÁNH GIÁ
                                </button>

                            </c:when>

                            <c:otherwise>

                                <form action="${pageContext.request.contextPath}/feedback-management"
                                      method="POST">

                                    <input type="hidden"
                                           name="action"
                                           value="show">

                                    <input type="hidden"
                                           name="feedbackId"
                                           value="${selectedFeedback.feedbackId}">

                                    <input type="hidden"
                                           name="keyword"
                                           value="${fn:escapeXml(keyword)}">

                                    <input type="hidden"
                                           name="rating"
                                           value="${empty selectedRating ? 'all' : selectedRating}">

                                    <input type="hidden"
                                           name="visible"
                                           value="${selectedVisible == true
                                                    ? 'visible'
                                                    : selectedVisible == false
                                                    ? 'hidden'
                                                    : 'all'}">

                                    <input type="hidden"
                                           name="sort"
                                           value="${selectedSort}">

                                    <input type="hidden"
                                           name="page"
                                           value="${currentPage}">

                                    <button type="submit"
                                            class="modal-confirm-button"
                                            onclick="return confirm('Hiển thị lại đánh giá này?');">
                                        HIỂN THỊ LẠI
                                    </button>

                                </form>

                            </c:otherwise>

                        </c:choose>

                    </div>

                </section>

            </div>

        </c:if>

        <script>
            const hideFeedbackModal
                    = document.getElementById("hideFeedbackModal");

            const hideFeedbackId
                    = document.getElementById("hideFeedbackId");

            const hideBookingCode
                    = document.getElementById("hideBookingCode");

            const hiddenReason
                    = document.getElementById("hiddenReason");

            function openHideModal(button) {
                hideFeedbackId.value = button.dataset.feedbackId;
                hideBookingCode.textContent = button.dataset.bookingCode;
                hiddenReason.value = "";

                hideFeedbackModal.classList.add("open");
                document.body.classList.add("modal-open");
            }

            function closeHideModal() {
                hideFeedbackModal.classList.remove("open");
                document.body.classList.remove("modal-open");
            }

            function closeDetailModal() {
                const detailModal
                        = document.getElementById("feedbackDetailModal");

                if (detailModal) {
                    detailModal.classList.remove("open");
                }

                document.body.classList.remove("modal-open");
            }

            function closeDetailModalAndOpenHide(button) {
                closeDetailModal();
                openHideModal(button);
            }

            document.addEventListener("keydown", function (event) {
                if (event.key === "Escape") {
                    closeHideModal();
                    closeDetailModal();
                }
            });

            const openedDetailModal
                    = document.getElementById("feedbackDetailModal");

            if (openedDetailModal) {
                document.body.classList.add("modal-open");
            }
        </script>

    </body>
</html>