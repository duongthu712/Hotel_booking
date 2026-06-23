<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <title>Hoàn tất đặt phòng - La Mer Hotel</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/navbar.css?v=<%= System.currentTimeMillis() %>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/footer.css?v=<%= System.currentTimeMillis() %>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/booking.css?v=<%= System.currentTimeMillis() %>">
    </head>

    <body>
        <jsp:include page="/view/common/navbar.jsp"/>

        <main class="booking-page">
            <div class="booking-progress">
                <div class="progress-item completed">
                    <div class="progress-number">1</div>
                    <div class="progress-name">THÔNG TIN ĐẶT PHÒNG</div>
                </div>

                <div class="progress-line completed"></div>

                <div class="progress-item completed">
                    <div class="progress-number">2</div>
                    <div class="progress-name">GỬI MINH CHỨNG ĐẶT CỌC</div>
                </div>

                <div class="progress-line completed"></div>

                <div class="progress-item active">
                    <div class="progress-number">3</div>
                    <div class="progress-name">HOÀN TẤT ĐẶT PHÒNG</div>
                </div>
            </div>

            <section class="success-card">
                <div class="success-icon">✓</div>

                <p class="success-label">LA MER HOTEL</p>

                <h1 class="success-main-title">
                    GỬI YÊU CẦU ĐẶT PHÒNG THÀNH CÔNG
                </h1>

                <p class="success-description">
                    Minh chứng đặt cọc của Quý khách đã được gửi thành công.
                    Khách sạn sẽ kiểm tra và xác nhận đơn trong thời gian sớm nhất.
                </p>

                <div class="success-status">
                    <span>Trạng thái hiện tại</span>
                    <strong>Đã đặt cọc - Chờ lễ tân xác nhận</strong>
                </div>

                <div class="success-booking-code">
                    <span>MÃ ĐẶT PHÒNG</span>

                    <div class="booking-code-value">
                        <strong>
                            <c:out value="${booking.bookingCode}"/>
                        </strong>
                    </div>

                    <p>
                        Vui lòng lưu mã này để tra cứu, check-in và làm việc với khách sạn.
                    </p>
                </div>

                <div class="success-note">
                    <strong>Lưu ý:</strong>
                    Tiền đặt cọc đã được hệ thống ghi nhận. Đơn đặt phòng chỉ được
                    xác nhận chính thức sau khi lễ tân kiểm tra và phê duyệt minh chứng.
                </div>
            </section>

            <div class="success-layout">
                <section class="success-information-card">
                    <div class="success-section-title">
                        <span>▤</span>

                        <div>
                            <h2>THÔNG TIN ĐẶT PHÒNG</h2>
                            <p>Thông tin đơn Quý khách vừa gửi</p>
                        </div>
                    </div>

                    <div class="success-room">
                        <img src="${not empty roomType.imageUrl
                                    ? roomType.imageUrl[0]
                                    : 'https://placehold.co/600x400?text=La+Mer+Room'}"
                             alt="${fn:escapeXml(roomType.typeName)}">

                        <div>
                            <h3>
                                <c:out value="${roomType.typeName}"/>
                            </h3>

                            <p>Giường: ${roomType.bedCount} x ${roomType.bedType}</p>
                            <p>Sức chứa: ${roomType.capacity} khách/phòng</p>
                            <p>Diện tích: ${roomType.areaSqm} m²</p>
                        </div>
                    </div>

                    <div class="summary-divider"></div>

                    <div class="success-detail-grid">
                        <div class="success-detail-item">
                            <span>Ngày nhận phòng</span>
                            <strong>${checkInText}</strong>
                        </div>

                        <div class="success-detail-item">
                            <span>Ngày trả phòng</span>
                            <strong>${checkOutText}</strong>
                        </div>

                        <div class="success-detail-item">
                            <span>Số đêm</span>
                            <strong>${numberOfNights} đêm</strong>
                        </div>

                        <div class="success-detail-item">
                            <span>Số lượng phòng</span>
                            <strong>${booking.numRooms} phòng</strong>
                        </div>

                        <div class="success-detail-item">
                            <span>Số khách</span>
                            <strong>${booking.numGuests} khách</strong>
                        </div>

                        <div class="success-detail-item">
                            <span>Nguồn đặt phòng</span>
                            <strong>
                                <c:out value="${booking.source}"/>
                            </strong>
                        </div>
                    </div>
                </section>

                <aside class="success-payment-card">
                    <div class="success-section-title">
                        <span>✓</span>

                        <div>
                            <h2>THÔNG TIN ĐẶT CỌC</h2>
                            <p>Minh chứng đang chờ lễ tân kiểm tra</p>
                        </div>
                    </div>

                    <div class="summary-row">
                        <span>Đơn giá một phòng/đêm</span>

                        <strong>
                            <fmt:formatNumber value="${booking.bookedPricePerNight}"
                                              type="number"
                                              maxFractionDigits="0"/>
                            VND
                        </strong>
                    </div>

                    <div class="summary-row">
                        <span>
                            Tổng tiền
                            (${booking.numRooms} phòng x ${numberOfNights} đêm)
                        </span>

                        <strong>
                            <fmt:formatNumber value="${totalAmount}"
                                              type="number"
                                              maxFractionDigits="0"/>
                            VND
                        </strong>
                    </div>

                    <div class="summary-divider"></div>

                    <div class="success-deposit-row">
                        <span>Số tiền đã đặt cọc</span>

                        <strong>
                            <fmt:formatNumber value="${booking.depositAmount}"
                                              type="number"
                                              maxFractionDigits="0"/>
                            VND
                        </strong>
                    </div>

                    <div class="success-payment-status">
                        <span>Trạng thái thanh toán</span>
                        <strong>Đã đặt cọc</strong>
                    </div>

                    <div class="success-payment-status">
                        <span>Trạng thái minh chứng</span>
                        <strong>Chờ xử lý</strong>
                    </div>

                    <p class="success-payment-message">
                        Sau khi lễ tân phê duyệt minh chứng, trạng thái đơn đặt phòng
                        sẽ chuyển từ “Chờ xử lý” thành “Đã xác nhận”.
                    </p>
                </aside>
            </div>

            <section class="success-contact-card">
                <div class="success-contact-item">
                    <div class="success-contact-icon">✉</div>

                    <div>
                        <strong>Kiểm tra email</strong>

                        <span>
                            Thông tin xác nhận sẽ được gửi đến email khách hàng
                            đã cung cấp.
                        </span>
                    </div>
                </div>

                <div class="success-contact-item">
                    <div class="success-contact-icon">☎</div>

                    <div>
                        <strong>Cần hỗ trợ?</strong>

                        <span>
                            Liên hệ lễ tân để được hỗ trợ về đơn đặt phòng.
                        </span>
                    </div>
                </div>

                <div class="success-contact-item">
                    <div class="success-contact-icon">⌕</div>

                    <div>
                        <strong>Tra cứu đơn</strong>

                        <span>
                            Sử dụng mã đặt phòng để kiểm tra trạng thái đơn.
                        </span>
                    </div>
                </div>
            </section>

            <div class="success-actions">
                <a href="${pageContext.request.contextPath}/home"
                   class="success-home-button">
                    VỀ TRANG CHỦ
                </a>

                <a href="${pageContext.request.contextPath}/quick-booking"
                   class="success-detail-button">
                    ĐẶT PHÒNG KHÁC
                </a>
            </div>
        </main>

        <jsp:include page="/view/common/footer.jsp"/>
    </body>
</html>
