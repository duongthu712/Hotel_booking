<%-- 
    Document   : room-detail
    Created on : May 27, 2026, 10:41:48 PM
    Author     : Minh Thu
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>${room.typeName} - Chi Tiết Phòng</title>
        
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/navbar.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/search-result.css"> 
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/room-detail.css"> 
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/footer.css">
    </head>
    <body>
        <jsp:include page="/view/common/navbar.jsp" />

        <div class="detail-container">
            <!-- BÊN TRÁI: KHỐI ẢNH (Tự co giãn theo tỉ lệ gốc, chống cắt xén) -->
            <div class="detail-left">
                <img src="${not empty room.imageUrl ? room.imageUrl : 'https://placehold.co/600x400?text=La+Mer+Hotel'}" 
                     onerror="this.onerror=null; this.src='https://placehold.co/600x400?text=La+Mer+Room';" 
                     alt="${room.typeName}">
            </div>

            <!-- BÊN PHẢI: KHỐI NỘI DUNG THÔNG TIN -->
            <div class="detail-right">
                <div>
                    <h1 class="detail-title">${room.typeName}</h1>
                    <div class="detail-price">
                        <fmt:formatNumber value="${room.basePrice}" type="currency" currencySymbol="" maxFractionDigits="0"/> VND / ĐÊM
                    </div>
                    <p class="detail-desc">${room.description}</p>
                    
                    <!-- BẢNG THÔNG SỐ PHÒNG -->
                    <div class="spec-grid">
                        <div class="spec-item">Diện tích: <strong>${room.areaSqm} m²</strong></div>
                        <div class="spec-item">Sức chứa: <strong>Tối đa ${room.capacity} khách</strong></div>
                        <div class="spec-item">Cấu trúc giường: <strong>${room.bedCount} x ${room.bedType}</strong></div>
                        <div class="spec-item">Trạng thái: <strong>Sẵn sàng phục vụ</strong></div>
                    </div>

                    <!-- TIỆN ÍCH PHÒNG -->
                    <h3 style="color: #2c3e46; font-size: 15px; margin-bottom: 12px; font-weight: 500;">Dịch vụ phòng bao gồm:</h3>
                    <div class="room-amenities-group" style="margin-bottom: 30px;">
                        <c:forEach var="svc" items="${room.roomServices}">
                            <span class="amenity-badge">${svc.serviceName} (x${svc.quantity})</span>
                        </c:forEach>
                    </div>
                </div>

               
                <div class="book-btn-wrapper">
                    <a href="${pageContext.request.contextPath}/view/user/booking-form.jsp?roomTypeId=${room.roomTypeId}" 
                       class="btn-action-book-detail">
                       TIẾN HÀNH ĐẶT PHÒNG NÀY
                    </a>
                </div>
            </div>
        </div>

        <jsp:include page="/view/common/footer.jsp" />
    </body>
</html>