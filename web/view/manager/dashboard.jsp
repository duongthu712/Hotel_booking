<%-- 
    Document   : dashboard
    Created on : May 27, 2026, 10:49:21 PM
    Author     : Minh Thu
Editor     : LinhLTHE200306
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/common.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/manager-dashboard.css" type="text/css">
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Tổng Quan</title>
    </head>
    <body>
        <%@ include file="/view/staff/header.jsp" %>
        <%@ include file="/view/staff/navbar.jsp" %>

        <main class="content-container">

            <div class="dashboard-header">
                <h2 class="header-title">Tổng quan</h2>
                <div class="header-actions">
                    <form action="${pageContext.request.contextPath}/ManagerDashboard" method="GET" class="filter-form">

                        <select name="month" class="filter-select">
                            <c:forEach begin="1" end="12" var="m">
                                <option value="${m}" ${m eq selectedMonth ? 'selected' : ''}>Tháng ${m}</option>
                            </c:forEach>
                        </select>

                        <c:set var="currentYear" value="<%= java.time.Year.now().getValue() %>"/>
                        <select name="year" class="filter-select">
                            <c:forEach begin="2020" end="${currentYear}" var="y">
                                <option value="${y}" ${y eq selectedYear ? 'selected' : ''}>${y}</option>
                            </c:forEach>
                        </select>

                        <button type="submit" class="search-btn">Lọc</button>
                        <a href="${pageContext.request.contextPath}/MDashboardPDF?month=${selectedMonth}&year=${selectedYear}" class="search-btn">
                            Export PDF
                        </a>
                    </form>
                </div>
            </div>

            <div class="kpi-grid">
                <div class="kpi-card kpi-card-gold">
                    <p class="kpi-label">Doanh thu hôm nay</p>
                    <h3 class="kpi-value">
                        <fmt:formatNumber value="${todayRevenue}" type="number" maxFractionDigits="0"/>
                        <span class="kpi-unit">VND</span>
                    </h3>
                </div>
                <div class="kpi-card kpi-card-navy">
                    <p class="kpi-label">Doanh thu tháng ${selectedMonth}</p>
                    <h3 class="kpi-value">
                        <fmt:formatNumber value="${monthlyRevenue}" type="number" maxFractionDigits="0"/>
                        <span class="kpi-unit">VND</span>
                    </h3>
                </div>
                <div class="kpi-card kpi-card-gold">
                    <p class="kpi-label">Tỷ lệ lấp đầy</p>
                    <h3 class="kpi-value">${occupancyRate}%</h3>
                </div>
                <div class="kpi-card kpi-card-navy">
                    <p class="kpi-label">Tổng đặt phòng</p>
                    <h3 class="kpi-value">${totalBookings}</h3>
                </div>
            </div>

            <div class="chart-section">
                <div class="section-header">
                    <h4 class="section-title">Doanh thu theo ngày - Tháng ${selectedMonth}/${selectedYear}</h4>
                </div>
                <div class="chart-container"
                     data-labels="${labelsJson}"
                     data-revenues="${revenuesJson}">
                    <canvas id="revenueChart"></canvas>
                </div>
            </div>

            <div class="two-col-grid">

                <div class="panel">
                    <h4 class="section-title">Tỷ lệ lấp đầy theo loại phòng</h4>
                    <div class="data-table-wrapper">
                        <table class="data-table dashboard-table">
                            <thead>
                                <tr>
                                    <th>Loại phòng</th>
                                    <th class="text-center">Phòng có khách</th>
                                    <th class="text-center">Tổng phòng</th>
                                    <th class="text-right">Tỷ lệ lấp đầy</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="o" items="${occupancyByRoomType}">
                                    <tr>
                                        <td class="room-type-name">${o.typeName}</td>
                                        <td class="text-center">${o.occupied}</td>
                                        <td class="text-center">${o.total}</td>
                                        <td class="text-right occupancy-value">${o.occupancyPct}%</td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty occupancyByRoomType}">
                                    <tr>
                                        <td colspan="4" class="empty-message">Không có dữ liệu.</td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div class="panel">
                    <div class="panel-header">
                        <h4 class="section-title">Đánh giá gần đây</h4>
                        <a href="${pageContext.request.contextPath}/feedback-list" class="view-all-link">Xem tất cả</a>
                    </div>
                    <table class="data-table dashboard-table">
                        <thead>
                            <tr>
                                <th>Khách hàng</th>
                                <th>Đánh giá</th>
                                <th class="text-right">Ngày</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="r" items="${latestReviews}">
                                <tr>
                                    <td>${r.guestName != null ? r.guestName : 'Khách vãng lai'}</td>
                                    <td>
                                        <div class="star-rating">
                                            <c:forEach begin="1" end="5" var="i">
                                                <span class="star ${i <= r.rating ? 'filled' : ''}">&#9733;</span>
                                            </c:forEach>
                                        </div>
                                    </td>
                                    <td class="text-right">${r.submittedAtStr}</td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty latestReviews}">
                                <tr>
                                    <td colspan="3" class="empty-message">Không có đánh giá nào.</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>

            <div class="panel">
                <div class="panel-header">
                    <h4 class="section-title">Đặt phòng gần đây</h4>
                    <a href="${pageContext.request.contextPath}/booking-list" class="view-all-link">Xem tất cả</a>
                </div>
                <table class="data-table dashboard-table">
                    <thead>
                        <tr>
                            <th>Mã booking</th>
                            <th>Khách hàng</th>
                            <th>Loại phòng</th>
                            <th>Nhận phòng</th>
                            <th>Trạng thái</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="b" items="${recentBookings}">
                            <tr>
                                <td class="booking-code">${b.bookingCode}</td>
                                <td>${b.guestName != null ? b.guestName : 'Khách vãng lai'}</td>
                                <td>${b.roomType}</td>
                                <td>${b.checkinDateStr}</td>
                                <td>
                                    <span class="status-badge status-${b.statusClass}">
                                        ${b.status}
                                    </span>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty recentBookings}">
                            <tr>
                                <td colspan="5" class="empty-message">Không có đặt phòng nào.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>


        </main>

        <script src="${pageContext.request.contextPath}/view/assets/javascript/chart.js"></script>
        <script src="${pageContext.request.contextPath}/view/assets/javascript/manager-dashboard.js"></script>
    </body>
</html>