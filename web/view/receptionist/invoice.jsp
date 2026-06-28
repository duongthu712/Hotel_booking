<%-- 
    Document   : invoice
    Created on : Jun 28, 2026
    Author     : LinhLTHE200306
    Luồng mới: Hiển thị dịch vụ/hư hỏng đã có từ DB + cho phép nhập thêm
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page import="model.StaffAccount" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Tạo hóa đơn</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/common.css" type="text/css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/invoice.css" type="text/css">
    </head>

    <body>
        <%@ include file="/view/staff/header.jsp" %>
        <%@ include file="/view/staff/navbar.jsp" %>

        <main class="content-container">
            <div class="page-header">
                <a href="${pageContext.request.contextPath}/Checkout" class="btn-back">
                    ← Quay lại
                </a>
            </div>

            <form id="invoiceForm" action="InvoiceCreate" method="POST" class="invoice-form">
                <input type="hidden" name="bookingId" value="${booking.bookingId}">
                <input type="hidden" name="roomCharges" id="hiddenRoomCharges" value="${roomCharges}">

                <div class="invoice-grid">
                    <div class="invoice-left">

                        <div class="card">
                            <h3 class="section-title">THÔNG TIN ĐẶT PHÒNG: 
                                <span class="booking-code">${booking.bookingCode}</span>
                                <span class="booking-status status-active">${booking.status}</span>
                            </h3>
                            <div class="booking-info-grid">
                                <div class="booking-details">

                                    <div class="info-grid-3col">
                                        <div class="info-col">
                                            <div class="info-row">
                                                <span class="info-label">Phòng</span>
                                                <span class="info-value">
                                                    <c:forEach var="br" items="${bookingRooms}" varStatus="loop">${br.roomNumber}
                                                        <c:if test="${!loop.last}">, 
                                                        </c:if></c:forEach>
                                                    </span>
                                                </div>
                                                <div class="info-row">
                                                    <span class="info-label">Loại phòng</span>
                                                    <span class="info-value">${roomType.typeName}</span>
                                            </div>
                                            <div class="info-row">
                                                <span class="info-label">Giá phòng / đêm</span>
                                                <span class="info-value">
                                                    <fmt:formatNumber value="${booking.bookedPricePerNight}" type="number" pattern="#,###"/> đ
                                                </span>
                                            </div>
                                            <div class="info-row">
                                                <span class="info-label">Số đêm</span>
                                                <span class="info-value">${nights} đêm</span>
                                            </div>
                                        </div>
                                        <div class="info-col">
                                            <div class="info-row">
                                                <span class="info-label">Check-in</span>
                                                <span class="info-value">${checkinDateDisplay} ${formattedCheckinTime}</span>
                                            </div>
                                            <div class="info-row">
                                                <span class="info-label">Check-out dự kiến</span>
                                                <span class="info-value">${checkoutDateDisplay} ${hotelInfo.checkoutTime}</span>
                                            </div>
                                            <div class="info-row">
                                                <span class="info-label">Check-out thực tế</span>
                                                <span class="info-value">
                                                    <input type="text" id="actual-checkout-time" class="time-input" value="${actualCheckoutTime}" readonly>
                                                </span>
                                            </div>
                                        </div>
                                        <div class="info-col">
                                            <div class="info-row">
                                                <span class="info-label">Số khách</span>
                                                <span class="info-value">${booking.numGuests} người</span>
                                            </div>
                                            <div class="info-row">
                                                <span class="info-label">Đặt bởi</span>
                                                <span class="info-value">
                                                    <c:choose>
                                                        <c:when test="${not empty guest}">
                                                            ${guest.fullName}
                                                        </c:when>
                                                        <c:otherwise>
                                                            Lễ tân
                                                        </c:otherwise>
                                                    </c:choose>
                                                </span>
                                            </div>
                                            <div class="info-row">
                                                <span class="info-label">Kênh đặt</span>
                                                <span class="info-value">${booking.source}</span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- DỊCH VỤ ĐÃ SỬ DỤNG (hiển thị từ DB) -->
                        <div class="card">
                            <h3 class="section-title">DỊCH VỤ ĐÃ SỬ DỤNG</h3>
                            <div class="table-container">
                                <c:choose>
                                    <c:when test="${not empty existingServices}">
                                        <table class="data-table service-table">
                                            <thead class="data-table-thead">
                                                <tr>
                                                    <th class="col-name">Dịch vụ</th>
                                                    <th class="col-price">Đơn giá</th>
                                                    <th class="col-qty">SL</th>
                                                    <th class="col-total">Thành tiền</th>
                                                </tr>
                                            </thead>
                                            <tbody class="data-table-tbody">
                                                <c:forEach var="svc" items="${existingServices}">
                                                    <tr>
                                                        <td class="col-name">${svc.serviceName}</td>
                                                        <td class="col-price">
                                                            <fmt:formatNumber value="${svc['unitPrice']}" type="number" pattern="#,###"/> đ
                                                        </td>
                                                        <td class="col-qty">${svc.quantityUsed}</td>
                                                        <td class="col-total">
                                                            <fmt:formatNumber value="${svc['totalPrice']}" type="number" pattern="#,###"/> đ
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="empty-sub-message">Không có dịch vụ phát sinh thêm.</div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                        <!-- NHẬP THÊM DỊCH VỤ -->
                        <div class="card">
                            <div class="card-header-with-search">
                                <h3 class="section-title">THÊM DỊCH VỤ (NẾU CÓ)</h3>
                                <div class="search-box">
                                    <input type="text" id="serviceSearch" placeholder="Tìm kiếm dịch vụ..." class="search-input-small">
                                </div>
                            </div>
                            <div class="table-container">
                                <table class="data-table service-table" id="serviceTable">
                                    <thead class="data-table-thead">
                                        <tr>
                                            <th class="col-name">Dịch vụ</th>
                                            <th class="col-price">Đơn giá</th>
                                            <th class="col-qty">SL</th>
                                            <th class="col-total">Thành tiền</th>
                                        </tr>
                                    </thead>
                                    <tbody class="data-table-tbody">
                                        <c:forEach var="svc" items="${roomTypeServices}" varStatus="loop">
                                            <tr class="service-row" data-name="${svc.serviceName.toLowerCase()}">
                                                <td class="col-name">
                                                    <div class="service-name">${svc.serviceName}</div>
                                                </td>
                                                <td class="col-price">
                                                    <fmt:formatNumber value="${svc['unitPrice']}" type="number" pattern="#,###"/> đ
                                                </td>
                                                <td class="col-qty">
                                                    <div class="quantity-control">
                                                        <button type="button" class="qty-btn qty-minus" onclick="changeQty('service', ${loop.index}, -1)">−</button>
                                                        <input type="number" name="serviceQuantity" id="serviceQty_${loop.index}" 
                                                               class="qty-input" value="0" min="0" 
                                                               data-unit-price="${svc.unitPrice}" 
                                                               data-is-free="${svc.isFree}"
                                                               data-num-rooms="${booking.numRooms}"
                                                               onchange="calculateService(${loop.index})">
                                                        <button type="button" class="qty-btn qty-plus" onclick="changeQty('service', ${loop.index}, 1)">+</button>
                                                    </div>
                                                    <input type="hidden" name="serviceId" value="${svc.serviceId}">
                                                    <input type="hidden" name="roomTypeServiceId" value="${svc.roomTypeServiceId}">
                                                    <input type="hidden" name="serviceUnitPrice" value="${svc.unitPrice}">
                                                    <input type="hidden" name="serviceIsFree" value="${svc.isFree}">
                                                </td>
                                                <td class="col-total">
                                                    <span id="serviceTotal_${loop.index}" class="row-total">0 đ</span>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>

                        <!-- TIỆN NGHI HƯ HỎNG ĐÃ GHI NHẬN (hiển thị từ DB) -->
                        <div class="card">
                            <h3 class="section-title">TIỆN NGHI BỊ HƯ HỎNG / MẤT ĐÃ GHI NHẬN</h3>
                            <div class="table-container">
                                <c:choose>
                                    <c:when test="${not empty existingDamages}">
                                        <table class="data-table amenity-table">
                                            <thead class="data-table-thead">
                                                <tr>
                                                    <th class="col-name">Tiện nghi</th>
                                                    <th class="col-price">Đơn giá</th>
                                                    <th class="col-qty">SL hỏng</th>
                                                    <th class="col-total">Thành tiền</th>
                                                </tr>
                                            </thead>
                                            <tbody class="data-table-tbody">
                                                <c:forEach var="dmg" items="${existingDamages}">
                                                    <tr>
                                                        <td class="col-name">${dmg.amenityName}</td>
                                                        <td class="col-price">
                                                            <fmt:formatNumber value="${dmg.unitPrice}" type="number" pattern="#,###"/> đ
                                                        </td>
                                                        <td class="col-qty">${dmg.quantityDamaged}</td>
                                                        <td class="col-total">
                                                            <fmt:formatNumber value="${dmg.totalPrice}" type="number" pattern="#,###"/> đ
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="empty-sub-message">Không có hư hỏng / mất mát nào.</div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                        <!-- NHẬP THÊM HƯ HỎNG -->
                        <div class="card">
                            <div class="card-header-with-search">
                                <h3 class="section-title">THÊM TIỆN NGHI HƯ HỎNG (NẾU CÓ)</h3>
                                <div class="search-box">
                                    <input type="text" id="amenitySearch" placeholder="Tìm kiếm tiện nghi..." class="search-input-small">
                                </div>
                            </div>
                            <div class="table-container">
                                <table class="data-table amenity-table" id="amenityTable">
                                    <thead class="data-table-thead">
                                        <tr>
                                            <th class="col-name">Tiện nghi</th>
                                            <th class="col-price">Đơn giá</th>
                                            <th class="col-qty">SL hỏng</th>
                                            <th class="col-total">Thành tiền</th>
                                        </tr>
                                    </thead>
                                    <tbody class="data-table-tbody">
                                        <c:forEach var="amen" items="${roomTypeAmenities}" varStatus="loop">
                                            <c:set var="alreadyDamaged" value="${damagedQtyMap[amen.amenityId] != null ? damagedQtyMap[amen.amenityId] : 0}"/>
                                            <c:set var="maxAdditional" value="${amen.quantity - alreadyDamaged}"/>
                                            <c:if test="${maxAdditional > 0}">
                                                <tr class="amenity-row" data-name="${amen.amenityName.toLowerCase()}">
                                                    <td class="col-name">
                                                        <div class="amenity-name">${amen.amenityName}</div>
                                                    </td>
                                                    <td class="col-price">
                                                        <fmt:formatNumber value="${amen.unitPrice}" type="number" pattern="#,###"/> đ
                                                    </td>
                                                    <td class="col-qty">
                                                        <div class="quantity-control">
                                                            <button type="button" class="qty-btn qty-minus" onclick="changeQty('amenity', ${loop.index}, -1)">−</button>
                                                            <c:set var="alreadyDamaged" value="${damagedQtyMap[amen.amenityId] != null ? damagedQtyMap[amen.amenityId] : 0}"/>
                                                            <c:set var="maxAdditional" value="${amen.quantity - alreadyDamaged}"/>
                                                            <input type="number" name="damageQuantity" id="amenityQty_${loop.index}" class="qty-input" value="0" min="0" max="${maxAdditional > 0 ? maxAdditional : 0}"       
                                                                   data-unit-price="${amen.unitPrice}"        
                                                                   onchange="calculateAmenity(${loop.index})">
                                                            <button type="button" class="qty-btn qty-plus" onclick="changeQty('amenity', ${loop.index}, 1)">+</button>
                                                        </div>
                                                        <input type="hidden" name="amenityId" value="${amen.amenityId}">
                                                        <input type="hidden" name="damageUnitPrice" value="${amen.unitPrice}">
                                                    </td>
                                                    <td class="col-total">
                                                        <span id="amenityTotal_${loop.index}" class="row-total">0 đ</span>
                                                    </td>
                                                </tr>
                                            </c:if>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>

                    <div class="invoice-right">
                        <div class="card summary-card">
                            <h3 class="section-title">HÓA ĐƠN</h3>

                            <div class="summary-rows">
                                <div class="summary-row">
                                    <span class="summary-label">Tiền phòng (${nights} đêm)</span>
                                    <span class="summary-value" id="summaryRoomCharges">
                                        <fmt:formatNumber value="${roomCharges}" type="number" pattern="#,###"/> đ
                                    </span>
                                </div>
                                <div class="summary-row">
                                    <span class="summary-label">Dịch vụ và sử dụng</span>
                                    <span class="summary-value" id="summaryServices">
                                        <fmt:formatNumber value="${existingServicesTotal}" type="number" pattern="#,###"/> đ
                                    </span>
                                </div>
                                <div class="summary-row">
                                    <span class="summary-label">Tiện nghi hư hỏng/mất</span>
                                    <span class="summary-value" id="summaryDamages">
                                        <fmt:formatNumber value="${existingDamagesTotal}" type="number" pattern="#,###"/> đ
                                    </span>
                                </div>

                                <c:if test="${lateCharge > 0}">
                                    <div class="summary-row">
                                        <span class="summary-label">
                                            Phụ thu trả phòng muộn
                                            <c:choose>
                                                <c:when test="${lateCharge >= booking.bookedPricePerNight.doubleValue() * booking.numRooms}">
                                                    (sau 18:00 — 100%)
                                                </c:when>
                                                <c:otherwise>
                                                    (trước 18:00 — 50%)
                                                </c:otherwise>
                                            </c:choose>
                                        </span>
                                        <span class="summary-value" id="summaryLateCharge">
                                            <fmt:formatNumber value="${lateCharge}" type="number" pattern="#,###"/> đ
                                        </span>
                                    </div>
                                </c:if>

                                <div class="summary-divider"></div>

                                <div class="summary-row total-row">
                                    <span class="summary-label">Tổng tiền</span>
                                    <span class="summary-value total" id="summaryTotal">
                                        <fmt:formatNumber value="${roomCharges + existingServicesTotal + existingDamagesTotal}" type="number" pattern="#,###"/> đ
                                    </span>
                                </div>
                                <div class="summary-row discount-row">
                                    <span class="summary-label">Trừ tiền cọc</span>
                                    <span class="summary-value discount" id="summaryDeposit">
                                        -<fmt:formatNumber value="${depositAmount != null ? depositAmount : 0}" type="number" pattern="#,###"/> đ
                                    </span>
                                </div>

                                <div class="summary-divider"></div>

                                <div class="summary-row final-row">
                                    <span class="summary-label">SỐ TIỀN CẦN THANH TOÁN</span>
                                    <span class="summary-value final" id="summaryRemaining">
                                        <fmt:formatNumber value="${roomCharges + existingServicesTotal + existingDamagesTotal - (depositAmount != null ? depositAmount.doubleValue() : 0)}" type="number" pattern="#,###"/> đ
                                    </span>
                                </div>
                            </div>

                            <div class="deposit-info">
                                <h4 class="sub-title">THÔNG TIN CỌC</h4>
                                <div class="deposit-row">
                                    <span class="deposit-label">Số tiền đã cọc</span>
                                    <span class="deposit-value">
                                        <fmt:formatNumber value="${depositAmount != null ? depositAmount : 0}" type="number" pattern="#,###"/> đ
                                    </span>
                                </div>
                                <div class="deposit-row">
                                    <span class="deposit-label">Hình thức cọc</span>
                                    <span class="deposit-value">Chuyển khoản</span>
                                </div>
                                <div class="deposit-row">
                                    <span class="deposit-label">Ngày cọc</span>
                                    <span class="deposit-value">${depositVerifiedAt}</span>
                                </div>
                                <div class="deposit-row">
                                    <span class="deposit-label">Trạng thái</span>
                                    <span class="deposit-status confirmed">Đã nhận</span>
                                </div>
                            </div>

                            <div class="payment-method">
                                <h4 class="sub-title">PHƯƠNG THỨC THANH TOÁN</h4>
                                <select name="paymentMethod" class="payment-select" required>
                                    <option value="">Chọn phương thức thanh toán</option>
                                    <option value="Tiền mặt">Tiền mặt</option>
                                    <option value="Thẻ ngân hàng">Thẻ ngân hàng</option>
                                    <option value="Chuyển khoản" selected>Chuyển khoản</option>
                                </select>
                            </div>

                            <div class="invoice-actions">
                                <button type="submit" class="btn-checkout-primary">XÁC NHẬN THANH TOÁN</button>
                            </div>
                        </div>
                    </div>
                </div>
            </form>
        </main>
        <script>
            var initServicesTotal = ${existingServicesTotal};
            var initDamagesTotal = ${existingDamagesTotal};
        </script>
        <script src="${pageContext.request.contextPath}/view/assets/javascript/alert.js"></script>
        <script src="${pageContext.request.contextPath}/view/assets/javascript/invoice.js"></script>
    </body>
</html>