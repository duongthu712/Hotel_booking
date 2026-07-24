<%-- 
    Author: ThuDNM-HE204370 
    Date created: 23/06/2026 
    Purpose: Footer component.
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<footer class="footer-container">
    <div class="footer-content">

        <div class="footer-col-brand">
            <h2>${not empty hotelInfo.hotelName ? hotelInfo.hotelName : 'La Mer'}</h2>
            <p class="footer-brand-desc">
                ${not empty hotelInfo.description ? hotelInfo.description : 'Sở hữu vị trí kiêu sa bên thềm sóng bãi biển Mỹ Khê, La Mer mang đến không gian nghỉ dưỡng riêng tư nghệ thuật...'}
            </p>
        </div>

        <div class="footer-col-contact">
            <h3>THÔNG TIN LIÊN HỆ</h3>
            <p class="contact-item">
                <strong>Địa chỉ:</strong> 
                <c:choose>
                    <c:when test="${not empty hotelInfo.addressUrl}">
                        <a href="${hotelInfo.addressUrl}" target="_blank" style="color: inherit; text-decoration: none;">
                            ${not empty hotelInfo.address ? hotelInfo.address : '278 Võ Nguyên Giáp, Phước Mỹ, Sơn Trà, Thành phố Đà Nẵng, Việt Nam'}
                        </a>
                    </c:when>
                    <c:otherwise>
                        ${not empty hotelInfo.address ? hotelInfo.address : '278 Võ Nguyên Giáp, Phước Mỹ, Sơn Trà, Thành phố Đà Nẵng, Việt Nam'}
                    </c:otherwise>
                </c:choose>
            </p>
            <p class="contact-item"><strong>Hotline đặt phòng:</strong> ${not empty hotelInfo.phone ? hotelInfo.phone : '+84 (0) 236 388 9999'}</p>
            <p class="contact-item"><strong>Email:</strong> ${not empty hotelInfo.email ? hotelInfo.email : 'reservation@lamerhotel.vn'}</p>
        </div>
    </div>

    <div class="footer-bottom">
        <p>© 2026 ${not empty hotelInfo.hotelName ? hotelInfo.hotelName : 'La Mer'} Hotel. All Rights Reserved.</p>
        <p class="footer-academic-note">Hệ thống Quản lý Nền tảng Đặt phòng trực tuyến • Vận hành bởi La Mer Hospitality Group</p>
    </div>
</footer>