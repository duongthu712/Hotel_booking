<%-- 
    Document   : room-type-management
    Created on : Jun 11, 2026, 2:15:52 PM
    Author     : Minh Thu
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản Lý Loại Phòng - La Mer Hotel</title>

        <jsp:include page="/view/staff/header.jsp" />

        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@fancyapps/ui@5.0/dist/fancybox/fancybox.css"/>

        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/view/assets/css/room-type-list.css?v=<%= System.currentTimeMillis() %>">
    </head>
    <body>
        <jsp:include page="/view/staff/navbar.jsp" />

        <div class="container-fluid">
            <div class="row">
                <main class="col-md-12 ms-sm-auto px-md-4">
                    <div class="management-wrapper">

                        <div class="action-bar">
                            <a href="${pageContext.request.contextPath}/createroomtype"
                               class="btn btn-primary add-room-btn">
                                <i class="fa-solid fa-plus me-2"></i>
                                Thêm Loại Phòng Mới
                            </a>
                        </div>

                        <div class="table-responsive">
                            <table class="table align-middle text-nowrap luxury-table-layout">
                                <thead>
                                    <tr>
                                        <th class="col-id">ID</th>
                                        <th class="col-name">Tên Hạng Phòng</th>
                                        <th class="col-desc">Mô Tả</th>
                                        <th class="col-img">Hình Ảnh</th>
                                        <th class="col-config">Cấu Hình Phòng</th>
                                        <th class="col-price">Giá Cơ Bản</th>
                                        <th class="col-services">Dịch Vụ Đi Kèm</th>
                                        <th class="col-amenities">Tiện Nghi</th> <th class="col-status">Trạng Thái</th>
                                        <th class="col-actions">Thao Tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${not empty roomTypesList}">
                                            <c:forEach var="item" items="${roomTypesList}">
                                                <tr>
                                                    <td class="col-id fw-bold text-muted-id">${item.roomTypeId}</td>
                                                    <td class="col-name text-wrap-normal">
                                                        <span class="room-type-name-text fw-bold">${item.typeName}</span>
                                                    </td>
                                                    <td class="col-desc text-wrap-normal">
                                                        <span class="room-type-desc-text text-muted">${item.description}</span>
                                                    </td>

                                                    <td class="col-img">
                                                        <c:choose>
                                                            <c:when test="${not empty item.imageUrl}">
                                                                <div class="image-preview-container">

                                                                    <a href="${item.imageUrl[0]}" data-fancybox="gallery-${item.roomTypeId}" data-caption="${item.typeName}">
                                                                        <img src="${item.imageUrl[0]}" alt="Room Thumbnail" class="main-thumbnail">
                                                                    </a>

                                                                    <c:if test="${item.imageUrl.size() > 1}">
                                                                        <span class="image-count-badge">+${item.imageUrl.size() - 1} ảnh</span>
                                                                    </c:if>

                                                                    <div style="display: none;">
                                                                        <c:forEach var="imgUrl" items="${item.imageUrl}" varStatus="status">
                                                                            <c:if test="${!status.first}">
                                                                                <a href="${imgUrl}" data-fancybox="gallery-${item.roomTypeId}" data-caption="${item.typeName}">
                                                                                    <img src="${imgUrl}" alt="Hidden Img">
                                                                                </a>
                                                                            </c:if>
                                                                        </c:forEach>
                                                                    </div>

                                                                </div>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <div class="image-preview-container-empty">
                                                                    <img src="https://placehold.co/140x95?text=No+Image" alt="No Image" class="main-thumbnail opacity-50">
                                                                </div>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>

                                                    <td class="col-config">
                                                        <div class="room-config-info">
                                                            <%-- Hiển thị người lớn/trẻ em thay vì chỉ hiển thị tổng --%>
                                                            <div>
                                                                <i class="fa-solid fa-users me-1"></i> 
                                                                <small>Người lớn: ${item.numGuests} | Trẻ em: ${item.numChildren}</small>
                                                            </div>
                                                            <div><i class="fa-solid fa-bed me-1"></i> ${item.bedCount} x ${item.bedType}</div>
                                                            <div class="area-text text-muted"><i class="fa-solid fa-ruler-combined me-1"></i> ${item.areaSqm} m²</div>
                                                        </div>
                                                    </td>
                                                    <td class="col-price fw-bold text-price">
                                                        <fmt:formatNumber value="${item.basePrice}" type="currency" currencySymbol="" maxFractionDigits="0"/>đ
                                                    </td>
                                                    <td class="col-services text-wrap-normal">
                                                        <div class="services-list-container">
                                                            <c:choose>
                                                                <c:when test="${not empty item.roomTypeServices}">
                                                                    <c:forEach var="rts" items="${item.roomTypeServices}">
                                                                        <span class="badge-service-item">
                                                                            <span class="service-name-text">${rts.roomService.serviceName}</span>
                                                                            <div class="service-meta-row">
                                                                                <span class="inner-qty-badge">Tổng: ${rts.quantity}</span>
                                                                                <span class="inner-free-badge">Miễn phí: ${rts.isFree}</span>
                                                                            </div>
                                                                        </span>
                                                                    </c:forEach>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="text-muted-italic">Chưa cấu hình</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </div>
                                                    </td>
                                                    <td class="text-wrap-normal">
                                                        <div class="amenities-list-container">
                                                            <c:choose>
                                                                <c:when test="${not empty item.roomAmenities}">
                                                                    <c:forEach var="amenity" items="${item.roomAmenities}">
                                                                        <span class="badge-service-item" ">
                                                                            <span class="service-name-text" >${amenity.amenityName}</span> 
                                                                            <span class="text-muted" style="font-size:11.5px;">( ${amenity.description})</span>
                                                                        </span>
                                                                    </c:forEach>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="text-muted-italic">Chưa cấu hình</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </div>
                                                    </td>
                                                    <td class="col-status">
                                                        <c:choose>
                                                            <c:when test="${item.isActive()}">
                                                                <span class="status-indicator active"><i class="fa-solid fa-circle me-1"></i> Hoạt động</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="status-indicator hidden"><i class="fa-solid fa-circle me-1"></i> Tạm dừng</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td class="col-actions">
                                                        <div class="btn-group-actions">
                                                            <c:choose>
                                                                <c:when test="${item.isActive()}">
                                                                    <span class="btn-action-disabled">Sửa</span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <a href="${pageContext.request.contextPath}/roomtypeedit?id=${item.roomTypeId}" class="btn-action-edit">Sửa</a>
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <c:choose>
                                                                <c:when test="${item.isActive()}">
                                                                    <a href="${pageContext.request.contextPath}/roomtypedelete?id=${item.roomTypeId}" class="btn-delete">Xóa</a>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="btn-action-disabled">Xóa</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <tr>
                                                <td colspan="9" class="text-center py-5 empty-table-state">Hiện tại chưa có dữ liệu loại phòng nào.</td>
                                            </tr>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </main>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

        <script src="https://cdn.jsdelivr.net/npm/@fancyapps/ui@5.0/dist/fancybox/fancybox.umd.js"></script>
        <script>
            Fancybox.bind("[data-fancybox]", {
                Thumbs: {autoStart: true},
                Toolbar: {display: {left: ["infobar"], right: ["zoom", "close"]}}
            });
        </script>

        <script src="${pageContext.request.contextPath}/view/assets/javascript/room-type-notification.js?v=<%= System.currentTimeMillis() %>"></script>
    </body>
</html>