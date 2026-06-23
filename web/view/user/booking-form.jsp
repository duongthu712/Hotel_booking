<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%
    if (request.getAttribute("roomType") == null) {
        response.sendRedirect(request.getContextPath() + "/search");
        return;
    }
%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <title>Thông tin đặt phòng - La Mer Hotel</title>

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/view/assets/css/navbar.css?v=<%= System.currentTimeMillis() %>">

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/view/assets/css/footer.css?v=<%= System.currentTimeMillis() %>">

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/view/assets/css/booking.css?v=<%= System.currentTimeMillis() %>">
    </head>

    <body>

        <jsp:include page="/view/common/navbar.jsp"/>

        <main class="booking-page">

            <div class="booking-progress">

                <div class="progress-item active">
                    <div class="progress-number">1</div>
                    <div class="progress-name">THÔNG TIN ĐẶT PHÒNG</div>
                </div>

                <div class="progress-line"></div>

                <div class="progress-item disabled">
                    <div class="progress-number">2</div>
                    <div class="progress-name">GỬI THÔNG TIN ĐẶT CỌC</div>
                </div>

                <div class="progress-line"></div>

                <div class="progress-item disabled">
                    <div class="progress-number">3</div>
                    <div class="progress-name">HOÀN TẤT ĐẶT PHÒNG</div>
                </div>

            </div>

            <c:if test="${not empty error}">
                <div class="booking-error">
                    <strong>Thông báo:</strong>
                    <c:out value="${error}"/>
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/booking-form"
                  method="post"
                  id="bookingForm"
                  novalidate>

                <input type="hidden"
                       name="roomTypeId"
                       value="${roomType.roomTypeId}">

                <input type="hidden"
                       name="checkIn"
                       value="${checkIn}">

                <input type="hidden"
                       name="checkOut"
                       value="${checkOut}">

                <input type="hidden"
                       name="numRooms"
                       value="${numRooms}">

                <input type="hidden"
                       name="numGuests"
                       value="${numGuests}">

                <div class="booking-layout">

                    <div class="booking-left">

                        <section class="booking-section">

                            <div class="section-title">
                                <div class="section-icon">♙</div>

                                <div>
                                    <h2>THÔNG TIN KHÁCH HÀNG</h2>
                                    <p>Vui lòng nhập thông tin để hoàn tất đặt phòng</p>
                                </div>
                            </div>

                            <div class="form-grid">

                                <div class="form-group">
                                    <label for="fullName">
                                        Họ và tên <span>*</span>
                                    </label>

                                    <input type="text"
                                           id="fullName"
                                           name="fullName"
                                           maxlength="100"
                                           placeholder="Nhập họ và tên"
                                           value="${fn:escapeXml(fullName)}">
                                </div>

                                <div class="form-group">
                                    <label for="phone">
                                        Số điện thoại <span>*</span>
                                    </label>

                                    <input type="tel"
                                           id="phone"
                                           name="phone"
                                           maxlength="10"
                                           placeholder="Nhập số điện thoại"
                                           value="${fn:escapeXml(phone)}">
                                </div>

                                <div class="form-group">
                                    <label for="email">
                                        Email <span>*</span>
                                    </label>

                                    <input type="email"
                                           id="email"
                                           name="email"
                                           maxlength="50"
                                           placeholder="Nhập email"
                                           value="${fn:escapeXml(email)}">
                                </div>

                                <div class="form-group">
                                    <label for="idNumber">
                                        Số CMND/CCCD/Hộ chiếu
                                        <small>(tùy chọn)</small>
                                    </label>

                                    <input type="text"
                                           id="idNumber"
                                           name="idNumber"
                                           maxlength="12"
                                           pattern="^([0-9]{12}|[A-Z][0-9]{7,8})$"
                                           placeholder="Ví dụ: 012345678901 hoặc B1234567"
                                           value="${fn:escapeXml(idNumber)}">
                                </div>

                                <div class="form-group">
                                    <label for="dateOfBirth">
                                        Ngày sinh
                                        <small>(tùy chọn)</small>
                                    </label>

                                    <input type="date"
                                           id="dateOfBirth"
                                           name="dateOfBirth"
                                           max="<%= java.time.LocalDate.now().minusYears(18) %>"
                                           value="${dateOfBirth}">

                                    <small>
                                        Nếu nhập ngày sinh, khách đặt phòng phải đủ 18 tuổi.
                                    </small>
                                </div>

                            </div>

                        </section>

                        <section class="booking-section">

                            <div class="section-title">
                                <div class="section-icon">▣</div>

                                <div>
                                    <h2>THÔNG TIN ĐẶT PHÒNG</h2>
                                    <p>Kiểm tra lại ngày lưu trú, số phòng và số khách</p>
                                </div>
                            </div>

                            <div class="booking-fields">

                                <div class="form-group">
                                    <label for="displayCheckIn">Ngày nhận phòng</label>

                                    <input type="date"
                                           id="displayCheckIn"
                                           value="${checkIn}"
                                           disabled>
                                </div>

                                <div class="form-group">
                                    <label for="displayCheckOut">Ngày trả phòng</label>

                                    <input type="date"
                                           id="displayCheckOut"
                                           value="${checkOut}"
                                           disabled>
                                </div>

                                <div class="form-group">
                                    <label for="numberOfNights">Số đêm</label>

                                    <input type="text"
                                           id="numberOfNights"
                                           value="${numberOfNights} đêm"
                                           readonly>
                                </div>

                                <div class="form-group">
                                    <label>Số lượng phòng</label>

                                    <div class="quantity-control">

                                        <button type="button" disabled>
                                            −
                                        </button>

                                        <input type="number"
                                               id="displayNumRooms"
                                               min="1"
                                               value="${numRooms}"
                                               disabled>

                                        <button type="button" disabled>
                                            +
                                        </button>

                                    </div>

                                    <small>
                                        Hiện còn ${availableRooms} phòng khả dụng.
                                    </small>
                                </div>

                                <div class="form-group">
                                    <label for="displayNumGuests">Số khách</label>

                                    <select id="displayNumGuests" disabled>

                                        <c:forEach begin="1"
                                                   end="${roomType.capacity * numRooms}"
                                                   var="guestNumber">

                                            <option value="${guestNumber}"
                                                    ${guestNumber == numGuests ? 'selected' : ''}>
                                                ${guestNumber} khách
                                            </option>

                                        </c:forEach>

                                    </select>

                                    <small id="capacityMessage">
                                        Tối đa ${roomType.capacity * numRooms}
                                        khách cho ${numRooms} phòng.
                                    </small>
                                </div>

                            </div>

                            <button type="submit"
                                    class="continue-button"
                                    ${availableRooms <= 0 ? 'disabled' : ''}>

                                TIẾP TỤC
                                <span>→</span>

                            </button>

                            <div class="privacy-note">
                                🔒 Thông tin của bạn được bảo mật tuyệt đối
                            </div>

                        </section>

                    </div>

                    <aside class="booking-summary">

                        <div class="summary-title">
                            <span>▤</span>
                            <h2>THÔNG TIN ĐẶT PHÒNG</h2>
                        </div>

                        <div class="room-summary">

                            <img src="${not empty roomType.imageUrl
                                        ? roomType.imageUrl[0]
                                        : 'https://placehold.co/600x400?text=La+Mer+Room'}"
                                 alt="${fn:escapeXml(roomType.typeName)}">

                            <div class="room-summary-info">

                                <h3>
                                    <c:out value="${roomType.typeName}"/>
                                </h3>

                                <p>
                                    Giường:
                                    ${roomType.bedCount} x ${roomType.bedType}
                                </p>

                                <p>
                                    Sức chứa:
                                    ${roomType.capacity} khách/phòng
                                </p>

                                <p>
                                    Diện tích:
                                    ${roomType.areaSqm} m²
                                </p>

                            </div>

                        </div>

                        <div class="summary-divider"></div>

                        <div class="summary-row">
                            <span>Ngày nhận phòng</span>
                            <strong>${checkIn}</strong>
                        </div>

                        <div class="summary-row">
                            <span>Ngày trả phòng</span>
                            <strong>${checkOut}</strong>
                        </div>

                        <div class="summary-row">
                            <span>Số đêm</span>
                            <strong>${numberOfNights} đêm</strong>
                        </div>

                        <div class="summary-row">
                            <span>Số lượng phòng</span>
                            <strong>${numRooms} phòng</strong>
                        </div>

                        <div class="summary-row">
                            <span>Số khách</span>
                            <strong>${numGuests} khách</strong>
                        </div>

                        <div class="summary-divider"></div>

                        <div class="summary-row">

                            <span>Đơn giá một phòng/đêm</span>

                            <strong>
                                <fmt:formatNumber value="${roomType.basePrice}"
                                                  type="number"
                                                  maxFractionDigits="0"/>
                                VND
                            </strong>

                        </div>

                        <div class="summary-row">

                            <span>
                                Tạm tính (${numRooms} phòng x ${numberOfNights} đêm)
                            </span>

                            <strong>
                                <fmt:formatNumber value="${totalAmount}"
                                                  type="number"
                                                  maxFractionDigits="0"/>
                                VND
                            </strong>

                        </div>

                        <div class="summary-divider"></div>

                        <div class="total-row">

                            <span>Tổng tiền</span>

                            <strong>
                                <fmt:formatNumber value="${totalAmount}"
                                                  type="number"
                                                  maxFractionDigits="0"/>
                                VND
                            </strong>

                        </div>

                        <div class="deposit-row">

                            <span>Tiền cọc cần thanh toán (30%)</span>

                            <strong>
                                <fmt:formatNumber value="${depositAmount}"
                                                  type="number"
                                                  maxFractionDigits="0"/>
                                VND
                            </strong>

                        </div>

                        <p class="tax-note">
                            Phòng sẽ được giữ trong 15 phút sau khi tiếp tục.
                        </p>

                        <div class="summary-divider"></div>

                        <div class="booking-benefits">

                            <div class="benefit-item">
                                <div class="benefit-icon">✓</div>
                                <strong>Giá tốt nhất</strong>
                                <span>Giá phòng được lấy trực tiếp từ hệ thống</span>
                            </div>

                            <div class="benefit-item">
                                <div class="benefit-icon">◇</div>
                                <strong>Giữ phòng 15 phút</strong>
                                <span>Hoàn tất đặt cọc trong thời gian quy định</span>
                            </div>

                            <div class="benefit-item">
                                <div class="benefit-icon">▢</div>
                                <strong>Thanh toán an toàn</strong>
                                <span>Thông tin được bảo mật</span>
                            </div>

                        </div>

                    </aside>

                </div>

            </form>

        </main>

        <jsp:include page="/view/common/footer.jsp"/>

    </body>
</html>