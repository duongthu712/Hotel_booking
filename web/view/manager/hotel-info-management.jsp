<%-- 
    Document   : hotel-info-management
    Created on : May 27, 2026, 10:49:38 PM
    Author     : Minh Thu
    Editor     : LinhLTHE200306
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page import="model.StaffAccount" %>
<%@page import="model.HotelInfo" %>
<%@page import="model.HotelImage" %>
<%@page import="model.HotelNews" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/common.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/hotel-info-management.css" type="text/css">
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý thông tin khách sạn</title>
    </head>

    <body data-edit-mode="${not empty newsToEdit ? 'true' : 'false'}"
          data-create-mode="${not empty openCreateModal ? 'true' : 'false'}"
          data-info-edit-mode="${not empty infoEditMode ? 'true' : 'false'}">

        <%@ include file="/view/staff/header.jsp" %>
        <%@ include file="/view/staff/navbar.jsp" %>

        <main class="content-container">

            <c:if test="${not empty sessionScope.successMessage}">
                <div class="alert-message alert-success">
                    ${sessionScope.successMessage}
                </div>
                <c:remove var="successMessage" scope="session"/>
            </c:if>

            <c:if test="${not empty sessionScope.errorMessage}">
                <div class="alert-message alert-error">
                    ${sessionScope.errorMessage}
                </div>
                <c:remove var="errorMessage" scope="session"/>
            </c:if>

            <div class="hotel-info-section">
                <button id="btn-hotel-info" class="btn-primary btn-hotel-info">
                    Thông tin khách sạn
                </button>
            </div>

            <div class="hotel-popup ${not empty openInfoModal ? 'show' : ''}" 
                 id="info-view-modal"
                 ${not empty openInfoModal ? 'style="display: flex;"' : ''}>
                <div class="popup-content info-popup-content">
                    <h2 class="popup-title">Thông tin khách sạn</h2>

                    <div class="info-display">
                        <div class="info-row">
                            <span class="info-label">Tên khách sạn:</span>
                            <span class="info-value">${hotelInfo.getHotelName()}</span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">Mô tả:</span>
                            <span class="info-value">${hotelInfo.getDescription()}</span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">Giờ check-in:</span>
                            <span class="info-value">${hotelInfo.getCheckinTime()}</span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">Giờ check-out:</span>
                            <span class="info-value">${hotelInfo.getCheckoutTime()}</span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">Địa chỉ:</span>
                            <span class="info-value">${hotelInfo.getAddress()}</span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">URL địa chỉ:</span>
                            <span class="info-value">${hotelInfo.getAddressUrl()}</span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">Điện thoại:</span>
                            <span class="info-value">${hotelInfo.getPhone()}</span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">Email:</span>
                            <span class="info-value">${hotelInfo.getEmail()}</span>
                        </div>
                    </div>

                    <div class="popup-action">
                        <button type="button" class="btn-close" id="btn-close-info">Đóng</button>
                        <button type="button" class="btn-submit" id="btn-edit-info">Chỉnh sửa</button>
                    </div>
                </div>
            </div>

            <div class="hotel-popup ${not empty infoEditMode ? 'show' : ''}" 
                 id="info-edit-modal"
                 ${not empty infoEditMode ? 'style="display: flex;"' : ''}>
                <form action="HotelInfoUpdate" method="POST" id="info-edit-form" class="popup-content info-popup-content">
                    <h2 class="popup-title">Chỉnh sửa thông tin khách sạn</h2>

                    <c:if test="${not empty infoErrorMessage}">
                        <div class="alert-message alert-error">
                            ${infoErrorMessage}
                        </div>
                    </c:if>

                    <div class="form-group">
                        <label class="input-label">Tên khách sạn*</label>
                        <input type="text" name="hotelName" class="popup-input-field" 
                               value="${hotelInfo.getHotelName()}" required>
                    </div>

                    <div class="form-group">
                        <label class="input-label">Mô tả</label>
                        <textarea name="description" class="popup-input-field" rows="3">${hotelInfo.getDescription()}</textarea>
                    </div>

                    <div class="form-group">
                        <label class="input-label">Giờ check-in</label>
                        <input type="time" name="checkinTime"" class="popup-input-field" 
                               value="${hotelInfo.getCheckinTime()}" required>
                    </div>

                    <div class="form-group">
                        <label class="input-label">Giờ check-out</label>
                        <input type="time" name="checkoutTime" class="popup-input-field" 
                               value="${hotelInfo.getCheckoutTime()}" required>
                    </div>

                    <div class="form-group">
                        <label class="input-label">Địa chỉ*</label>
                        <input type="text" name="address" class="popup-input-field" 
                               value="${hotelInfo.getAddress()}" required>
                    </div>

                    <div class="form-group">
                        <label class="input-label">URL địa chỉ</label>
                        <input type="text" name="addressUrl" class="popup-input-field" 
                               value="${hotelInfo.getAddressUrl()}">
                    </div>

                    <div class="form-group">
                        <label class="input-label">Điện thoại*</label>
                        <input type="text" name="phone" class="popup-input-field" 
                               value="${hotelInfo.getPhone()}" required>
                    </div>

                    <div class="form-group">
                        <label class="input-label">Email*</label>
                        <input type="getEmail()" name="email" class="popup-input-field" 
                               value="${hotelInfo.getEmail()}" required>
                    </div>

                    <div class="popup-action">
                        <button type="button" class="btn-close" id="btn-close-info-edit">Huỷ</button>
                        <button type="submit" class="btn-submit">Lưu thay đổi</button>
                    </div>
                </form>
            </div>

            <div class="images-section">
                <h3 class="section-title">Hình ảnh khách sạn</h3>

                <div class="images-grid">
                    <div class="image-card">
                        <div class="image-wrapper">
                            <img src="${bannerImage.getImageUrl()}" alt="Banner" class="hotel-image banner-image">
                            <button type="button" class="btn-edit-image" 
                                    onclick="openImageEditModal(${bannerImage.getImageId()}, '${bannerImage.getImageUrl()}', 'Banner')">
                                Chỉnh sửa
                            </button>
                        </div>
                        <span class="image-label">Ảnh nền</span>
                    </div>

                    <c:forEach var="img" items="${smallImages}" varStatus="loop">
                        <div class="image-card">
                            <div class="image-wrapper">
                                <img src="${img.getImageUrl()}" alt="Ảnh ${loop.index + 1}" class="hotel-image">
                                <button type="button" class="btn-edit-image" 
                                        onclick="openImageEditModal(${img.getImageId()}, '${img.getImageUrl()}', 'Ảnh nhỏ ${loop.index + 1}')">
                                    Chỉnh sửa
                                </button>
                            </div>
                            <span class="image-label">Ảnh nhỏ ${loop.index + 1}</span>
                        </div>
                    </c:forEach>
                </div>
            </div>

            <div class="hotel-popup" id="image-edit-modal">
                <form action="HotelImageUpdate" method="POST" id="image-edit-form" class="popup-content">
                    <h2 class="popup-title" id="image-modal-title">Chỉnh sửa ảnh</h2>

                    <input type="hidden" name="imageId" id="edit-image-id">
                    <div class="form-group">
                        <label class="input-label">Tải ảnh</label>
                        <input type="file" id="upImage">
                    </div>
                    <div class="form-group">
                        <label class="input-label">Link ảnh</label>
                        <input type="text" name="imageUrl" id="edit-image-url" class="popup-input-field" required>
                    </div>

                    <div class="popup-action">
                        <button type="button" class="btn-close" id="btn-close-image">Huỷ</button>
                        <button type="submit" class="btn-submit">Lưu</button>
                    </div>
                </form>
            </div>

            <div class="news-section" id="news-section">
                <h3 class="section-title">Tin tức & Sự kiện</h3>

                <div class="search-container">
                    <form action="HotelInfo" method="GET" class="search-form" id="news-filter-form">
                        <input type="text" name="keyword" class="search-input" 
                               placeholder="Tìm kiếm theo tiêu đề..." value="${keyword}">

                        <select name="status" class="filter-select">
                            <option value="all" ${status == 'all' || empty status ? 'selected' : ''}>Tất cả</option>
                            <option value="active" ${status == 'active' ? 'selected' : ''}>Đang hoạt động</option>
                            <option value="inactive" ${status == 'inactive' ? 'selected' : ''}>Ngừng hoạt động</option>
                        </select>

                        <button type="submit" class="search-btn">Lọc</button>
                        <a href="HotelInfo" class="reset-btn">Làm mới</a>
                    </form>

                    <div class="header-action">
                        <button id="btn-create-news" class="btn-primary">Thêm bài viết mới</button>
                    </div>
                </div>

                <table class="data-table">
                    <thead class="data-table-thead">
                        <tr>
                            <th class="col-stt">STT</th>
                            <th class="col-title">Tiêu đề</th>
                            <th class="col-content">Nội dung</th>
                            <th class="col-status">Trạng thái</th>
                            <th class="col-action">Hành động</th>
                        </tr>
                    </thead>

                    <tbody class="data-table-tbody">
                        <c:forEach var="news" items="${newsList}" varStatus="loop">
                            <tr class="${news.isActive() ? '' : 'row-inactive'}" 
                                data-news-id="${news.getNewsId()}"
                                data-image-url="${news.getImageUrl()}"
                                data-full-content="${news.getContent()}"
                                data-title="${news.getTitle()}">
                                <td class="col-stt">${(currentPage - 1) * 10 + loop.index + 1}</td>
                                <td class="col-title">
                                    <a href="javascript:void(0)" class="news-title-link" 
                                       onclick="openNewsDetailModal(${news.getNewsId()})">
                                        ${news.getTitle()}
                                    </a>
                                </td>
                                <td class="col-content">
                                    ${news.getContent().length() > 100 ? news.getContent().substring(0, 100).concat('...') : news.getContent()}
                                </td>
                                <td class="col-status">
                                    <div class="news-status ${news.isActive() ? 'status-active' : 'status-inactive'}">
                                        ${news.isActive() ? 'HOẠT ĐỘNG' : 'TẠM DỪNG'}
                                    </div>
                                </td>
                                <td class="btn-action">
                                    <a class="btn-edit" href="HotelNewsEdit?newsId=${news.getNewsId()}">Sửa</a>
                                    <form action="HotelNewsDelete" method="post" style="display: inline-block; margin: 0;">
                                        <input type="hidden" name="newsId" value="${news.getNewsId()}">
                                        <button type="submit" onclick="return confirm('Bạn có chắc muốn xóa bài viết ${news.getTitle()}?')">Xóa</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                    <c:if test="${empty newsList}">
                        <tr>
                            <td colspan="7" class="empty-message">
                                Không tìm thấy tin tức.
                            </td>
                        </tr>
                    </c:if>
                </table>

                <div class="pagination">
                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <a href="HotelInfo?page=${i}&keyword=${keyword}&status=${status}#news-section" 
                           class="${currentPage == i ? 'active' : ''}">${i}</a>
                    </c:forEach>
                </div>
            </div>

            <div class="hotel-popup" id="news-detail-modal">
                <div class="popup-content news-detail-content">
                    <h2 class="popup-title" id="detail-news-title"></h2>

                    <div class="news-detail-image" id="detail-news-image-wrapper">
                        <img src="" alt="News Image" id="detail-news-img">
                    </div>

                    <div class="news-detail-content" id="detail-news-content"></div>

                    <div class="news-detail-meta">
                        <span id="detail-news-status"></span>
                        <span id="detail-news-date"></span>
                    </div>

                    <div class="popup-action">
                        <button type="button" class="btn-close" id="btn-close-detail">Đóng</button>
                        <button type="button" class="btn-submit" id="btn-edit-from-detail">Chỉnh sửa</button>
                    </div>
                </div>
            </div>

            <div class="hotel-popup ${not empty openCreateModal ? 'show' : ''}" 
                 id="news-create-modal"
                 ${not empty openCreateModal ? 'style="display: flex;"' : ''}>
                <form action="HotelNewsCreate" method="POST" id="news-create-form" class="popup-content">
                    <h2 class="popup-title">Thêm bài viết mới</h2>

                    <c:if test="${not empty createErrorMessage}">
                        <div class="alert-message alert-error">
                            ${createErrorMessage}
                        </div>
                    </c:if>

                    <div class="form-group">
                        <label class="input-label">Tiêu đề*</label>
                        <input type="text" name="title" class="popup-input-field" 
                               value="${keepTitle}" required>
                    </div>

                    <div class="form-group">
                        <label class="input-label">Nội dung*</label>
                        <textarea name="content" class="popup-input-field" rows="5" required>${keepContent}</textarea>
                    </div>

                    <input type="hidden" name="imageId" id="edit-image-id">
                    <div class="form-group">
                        <label class="input-label">Tải ảnh</label>
                        <input type="file" id="upImage">
                    </div>
                    <div class="form-group">
                        <label class="input-label">Link ảnh</label>
                        <input type="text" name="imageUrl" id="edit-image-url" class="popup-input-field" 
                               value="${keepImageUrl}">
                    </div>

                    <div class="popup-action">
                        <button type="button" class="btn-close" id="btn-close-create">Huỷ</button>
                        <button type="submit" class="btn-submit">Lưu</button>
                    </div>
                </form>
            </div>

            <div class="hotel-popup ${not empty newsToEdit ? 'show' : ''}" 
                 id="news-edit-modal"
                 ${not empty newsToEdit ? 'style="display: flex;"' : ''}>
                <form action="HotelNewsEdit" method="POST" id="news-edit-form" class="popup-content">
                    <h2 class="popup-title">Chỉnh sửa bài viết</h2>

                    <input type="hidden" name="newsId" value="${newsToEdit.getNewsId()}">

                    <c:if test="${not empty editErrorMessage}">
                        <div class="alert-message alert-error">
                            ${editErrorMessage}
                        </div>
                    </c:if>

                    <div class="form-group">
                        <label class="input-label">Tiêu đề*</label>
                        <input type="text" name="title" class="popup-input-field" 
                               value="${newsToEdit.getTitle()}" required>
                    </div>

                    <div class="form-group">
                        <label class="input-label">Nội dung*</label>
                        <textarea name="content" class="popup-input-field" rows="5" required>${newsToEdit.getContent()}</textarea>
                    </div>

                    <input type="hidden" name="imageId" id="edit-image-id">
                    <div class="form-group">
                        <label class="input-label">Tải ảnh</label>
                        <input type="file" id="upImage">
                    </div>
                    <div class="form-group">
                        <label class="input-label">Link ảnh</label>
                        <input type="text" name="imageUrl" id="edit-image-url" class="popup-input-field" 
                               value="${newsToEdit.getImageUrl()}">
                    </div>

                    <div class="form-group toggle-row">
                        <label class="input-label">Trạng thái hoạt động</label>
                        <label class="toggle-switch">
                            <input type="checkbox" name="active" value="true" 
                                   ${newsToEdit.isActive() ? 'checked' : ''}>
                            <span class="toggle-slider"></span>
                        </label>
                    </div>

                    <div class="popup-action">
                        <button type="button" class="btn-close" id="btn-close-edit">Huỷ</button>
                        <button type="submit" class="btn-submit">Lưu thay đổi</button>
                    </div>
                </form>
            </div>

        </main>

        <script src="<%=request.getContextPath()%>/view/assets/javascript/hotel-info-management.js"></script>
        <script src="<%=request.getContextPath()%>/view/assets/javascript/upload-img.js"></script>
    </body>
</html>
