<%-- 
    Document   : billing
    Created on : May 27, 2026, 10:46:35 PM
    Author     : Minh Thu
    Editor     : LinhLTHE200306
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Lịch sử thanh toán</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/common.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/billing.css">
    </head>

    <body>
        <%@ include file="/view/staff/header.jsp" %>
        <%@ include file="/view/staff/navbar.jsp" %>

        <main class="content-container">

            <c:if test="${not empty sessionScope.successMessage}">
                <div class="alert-message alert-success">${sessionScope.successMessage}</div>
                <c:remove var="successMessage" scope="session"/>
            </c:if>
            <c:if test="${not empty sessionScope.errorMessage}">
                <div class="alert-message alert-error">${sessionScope.errorMessage}</div>
                <c:remove var="errorMessage" scope="session"/>
            </c:if>

            <form action="BillingList" method="GET" class="filter-bar">
                <div class="filter-group">
                    <label class="filter-label">Mã đặt phòng</label>
                    <input type="text" name="keyword" class="filter-input"
                           placeholder="Nhập Mã đặt phòng" value="${keyword}">
                </div>
                <div class="filter-group">
                    <label class="filter-label">Từ ngày</label>
                    <input type="date" name="fromDate" class="filter-input" value="${fromDate}">
                </div>
                <div class="filter-group">
                    <label class="filter-label">Đến ngày</label>
                    <input type="date" name="toDate" class="filter-input" value="${toDate}">
                </div>
                <div class="filter-group">
                    <label class="filter-label">Trạng thái</label>
                    <select name="status" class="filter-select">
                        <option value="" ${empty status ? 'selected' : ''}>Tất cả</option>
                        <option value="Đã thanh toán" ${status == 'Đã thanh toán' ? 'selected' : ''}>Đã thanh toán</option>
                        <option value="Chưa thanh toán" ${status == 'Chưa thanh toán' ? 'selected' : ''}>Chưa thanh toán</option>
                    </select>
                </div>
                <div class="filter-actions">
                    <button type="submit" class="filter-btn active">Lọc</button>
                    <a href="BillingList" class="reset-btn">Làm mới</a>
                </div>
            </form>

            <div class="billing-layout">

                <div class="billing-main">
                    <c:choose>
                        <c:when test="${not empty invoiceList}">
                            <div class="table-container">
                                <table class="data-table">
                                    <thead class="data-table-thead">
                                        <tr>
                                            <th>Mã đặt phòng</th>
                                            <th>Tiền phòng</th>
                                            <th>Dịch vụ</th>
                                            <th>Hư hỏng</th>
                                            <th>Đã thu</th>
                                            <th>Tổng tiền</th>
                                            <th>Trạng thái</th>
                                            <th>Thao tác</th>
                                        </tr>
                                    </thead>
                                    <tbody class="data-table-tbody">
                                        <c:forEach var="inv" items="${invoiceList}">
                                            <tr class="${not empty selectedInvoice and selectedInvoice.invoice.invoiceId == inv.invoiceId ? 'row-selected' : ''}">
                                                <td>${inv.bookingCode}</td>
                                                <td class="text-right">
                                                    <fmt:formatNumber value="${inv.roomCharges}" type="number" pattern="#,###"/>
                                                </td>
                                                <td class="text-right">
                                                    <fmt:formatNumber value="${inv.consumableCharges}" type="number" pattern="#,###"/>
                                                </td>
                                                <td class="text-right">
                                                    <fmt:formatNumber value="${inv.amenityDamages}" type="number" pattern="#,###"/>
                                                </td>
                                                <td class="text-right">
                                                    <fmt:formatNumber value="${inv.totalPaid}" type="number" pattern="#,###"/>
                                                </td>
                                                <td class="text-right">
                                                    <fmt:formatNumber value="${inv.totalAmount}" type="number" pattern="#,###"/>
                                                </td>
                                                <td class="text-center">
                                                    <span class="invoice-status ${inv.paymentStatus == 'Đã thanh toán' ? 'status-paid' : 'status-unpaid'}">
                                                        ${inv.paymentStatus}
                                                    </span>
                                                </td>

                                                <td class="btn-action">
                                                    <c:url var="viewUrl" value="BillingList">
                                                        <c:param name="invoiceId" value="${inv.invoiceId}"/>
                                                        <c:param name="keyword"   value="${keyword}"/>
                                                        <c:param name="fromDate"  value="${fromDate}"/>
                                                        <c:param name="toDate"    value="${toDate}"/>
                                                        <c:param name="status"    value="${status}"/>
                                                        <c:param name="page"      value="${currentPage}"/>
                                                    </c:url>
                                                    <a href="${viewUrl}" class="icon-btn" title="Xem chi tiết">
                                                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                                        <path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7-11-7-11-7z"/>
                                                        <circle cx="12" cy="12" r="3"/>
                                                        </svg>
                                                    </a>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>

                            <div class="pagination">
                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <a href="BillingList?page=${i}&keyword=${keyword}&fromDate=${fromDate}&toDate=${toDate}&status=${status}"
                                       class="${currentPage == i ? 'active' : ''}">${i}</a>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-message">Không có hóa đơn nào.</div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <aside class="detail-panel" id="invoiceDetailPanel">
                    <c:if test="${not empty selectedInvoice}">
                        <div class="detail-panel-header">
                            <div class="detail-panel-title">
                                <h2>Chi tiết hóa đơn</h2>
                            </div>
                            <c:url var="closeUrl" value="BillingList">
                                <c:param name="keyword"  value="${keyword}"/>
                                <c:param name="fromDate" value="${fromDate}"/>
                                <c:param name="toDate"   value="${toDate}"/>
                                <c:param name="status"   value="${status}"/>
                                <c:param name="page"     value="${currentPage}"/>
                            </c:url>
                            <a href="${closeUrl}" class="btn-close-panel">&times;</a>
                        </div>

                        <div class="detail-section">
                            <h3 class="detail-section-title">Lịch sử thanh toán</h3>
                            <c:forEach var="p" items="${selectedInvoice.payments}">
                                <div class="detail-row">
                                    <span>${p.note}</span>
                                    <span><fmt:formatNumber value="${p.amount}" type="number" pattern="#,###"/> đ</span>
                                </div>
                            </c:forEach>
                            <c:if test="${empty selectedInvoice.payments}">
                                <div class="empty-sub-message">Chưa có khoản thanh toán nào.</div>
                            </c:if>
                        </div>

                        <c:if test="${selectedInvoice.invoice.remainingAmount > 0}">
                            <div class="detail-section">
                                <h3 class="detail-section-title">Thu tiền</h3>
                                <form action="BillingList" method="POST">
                                    <input type="hidden" name="action" value="collectPayment">
                                    <input type="hidden" name="invoiceId" value="${selectedInvoice.invoice.invoiceId}">
                                    <input type="hidden" name="bookingId" value="${selectedInvoice.invoice.bookingId}">
                                    <input type="number" name="amount" placeholder="Số tiền thu"
                                           max="${selectedInvoice.invoice.remainingAmount}" min="0"
                                           class="filter-input" style="margin-bottom:8px;width:100%">
                                    <select name="paymentMethod" class="filter-select" style="margin-bottom:8px;width:100%">
                                        <option value="Tiền mặt">Tiền mặt</option>
                                        <option value="Thẻ ngân hàng">Thẻ ngân hàng</option>
                                        <option value="Chuyển khoản">Chuyển khoản</option>
                                    </select>
                                    <button type="submit" class="btn-panel-primary" style="width:100%">Thu tiền</button>
                                </form>
                            </div>
                        </c:if>

                        <div class="detail-section">
                            <h3 class="detail-section-title">Chi tiết chi phí</h3>
                            <div class="detail-row">
                                <span>Tiền phòng</span>
                                <span><fmt:formatNumber value="${selectedInvoice.invoice.roomCharges}" type="number" pattern="#,###"/> đ</span>
                            </div>
                            <div class="detail-row">
                                <span>Đồ tiêu hao</span>
                                <span><fmt:formatNumber value="${selectedInvoice.invoice.consumableCharges}" type="number" pattern="#,###"/> đ</span>
                            </div>
                            <div class="detail-row">
                                <span>Thiệt hại</span>
                                <span><fmt:formatNumber value="${selectedInvoice.invoice.amenityDamages}" type="number" pattern="#,###"/> đ</span>
                            </div>
                            
                            <div class="detail-row detail-total">
                                <span>Tổng tiền</span>
                                <span><fmt:formatNumber value="${selectedInvoice.invoice.totalAmount}" type="number" pattern="#,###"/> đ</span>
                            </div>
                            <div class="detail-row detail-remaining">
                                <span>Còn phải thanh toán</span>
                                <span class="text-success">
                                    <fmt:formatNumber value="${selectedInvoice.invoice.remainingAmount}" type="number" pattern="#,###"/> đ
                                </span>
                            </div>
                        </div>

                        <div class="detail-panel-actions">
                            <a href="${pageContext.request.contextPath}/InvoicePDF?bookingId=${selectedInvoice.invoice.bookingId}"
                               target="_blank" class="btn-panel-primary">Xuất PDF</a>
                            <a href="${closeUrl}" class="btn-panel-secondary">Đóng</a>
                        </div>
                    </c:if>

                    <c:if test="${empty selectedInvoice}">
                        <div class="detail-panel-empty">
                            <p>Chọn một hóa đơn để xem chi tiết</p>
                        </div>
                    </c:if>
                </aside>

            </div>
        </main>
        <script src="<%=request.getContextPath()%>/view/assets/javascript/alert.js"></script>
        <script src="<%=request.getContextPath()%>/view/assets/javascript/billing.js"></script>
    </body>
</html>