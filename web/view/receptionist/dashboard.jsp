<%-- 
    Author: ThuDNM-HE204370 
    Date created: 23/06/2026 
    Purpose: Receptionist dashboard.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/view/staff/header.jsp" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Giao diện Lễ tân - Dashboard Thống Kê</title>
        <link href="${pageContext.request.contextPath}/view/assets/css/receptionist-dashboard.css?v=<%=System.currentTimeMillis()%>" rel="stylesheet" type="text/css">
    </head>
    <body>
        <jsp:include page="/view/staff/navbar.jsp" />
        
        <div class="dashboard-container">
            
            <section class="metrics-grid">
                <div class="metric-card arrivals">
                    <h3>Chờ Check-in Hôm Nay</h3>
                    <p class="number">${dashboardData.totalArrivalsToday}</p>
                </div>
                <div class="metric-card departures">
                    <h3>Dự Kiến Check-out Hôm Nay</h3>
                    <p class="number">${dashboardData.totalDeparturesToday}</p>
                </div>
                <div class="metric-card in-house">
                    <h3>Giao dịch chờ xác nhận</h3>
                    <p class="number">${dashboardData.pendingDeposits}</p>
                </div>
                <div class="metric-card pending-req">
                    <h3>Yêu Cầu Chờ Xử Lý</h3>
                    <p class="number">${dashboardData.pendingRequests}</p>
                </div>
            </section>

            <section class="room-status-grid">
                <div class="status-box status-available">
                    <span>Phòng Trống:</span>
                    <strong>${dashboardData.roomStatusCount['Phòng trống']}</strong>
                </div>
                <div class="status-box status-occupied">
                    <span>Đang Có Khách:</span>
                    <strong>${dashboardData.roomStatusCount['Phòng có khách']}</strong>
                </div>
                <div class="status-box status-cleaning">
                    <span>Đang Dọn Dẹp:</span>
                    <strong>${dashboardData.roomStatusCount['Đang dọn dẹp']}</strong>
                </div>
                <div class="status-box status-maintenance">
                    <span>Bảo Trì:</span>
                    <strong>${dashboardData.roomStatusCount['Đang bảo trì']}</strong>
                </div>
            </section>

            <div class="lists-wrapper">
                
                <section class="table-section">
                    <h2>DANH SÁCH CHECK-IN HÔM NAY</h2>
                    <div class="table-responsive">
                        <table>
                            <thead>
                                <tr>
                                    <th>Mã Booking</th>
                                    <th>Khách Hàng</th>
                                    <th>Số ĐT</th>
                                    <th>Hạng Phòng</th>
                                    <th style="text-align: center;">Số Phòng Đặt</th>
                                    <th>Yêu Cầu Đặc Biệt</th>
                                    <th>Chi Tiết / Mốc Giờ</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${dashboardData.checkInTodayList}" var="checkin" varStatus="status">
                                    <c:if test="${status.index < 10}">
                                        <tr>
                                            <td><strong>${checkin.bookingCode}</strong></td>
                                            <td>${checkin.guestFullName}</td>
                                            <td>${checkin.guestPhone}</td>
                                            <td>${checkin.roomTypeName}</td>
                                            <td style="text-align: center;">${checkin.numRooms}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${checkin.requestType eq 'Nhận phòng muộn'}">
                                                        <span class="badge-req-custom badge-style-late">Nhận phòng muộn</span>
                                                    </c:when>
                                                    <c:when test="${checkin.requestType eq 'Nhận phòng sớm'}">
                                                        <span class="badge-req-custom badge-style-early">Nhận phòng sớm</span>
                                                    </c:when>
                                                    <c:when test="${not empty checkin.requestType}">
                                                        <span class="badge badge-approved">${checkin.requestType}</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-muted">-</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="req-details-text">
                                                <c:out value="${checkin.requestDetails}" default="-"/>
                                            </td>
                                        </tr>
                                    </c:if>
                                </c:forEach>
                                <c:if test="${empty dashboardData.checkInTodayList}">
                                    <tr><td colspan="7" class="text-center">Không có khách nào dự kiến đến hôm nay.</td></tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                    <div class="view-all-footer">
                        <a href="${pageContext.request.contextPath}/checkin" class="view-all-link">
                            Xem tất cả danh sách Check-in <span class="arrow-icon">→</span>
                        </a>
                    </div>
                </section>

                <section class="table-section">
                    <h2>DANH SÁCH CHECK-OUT HÔM NAY</h2>
                    <div class="table-responsive">
                        <table>
                            <thead>
                                <tr>
                                    <th>Mã Booking</th>
                                    <th>Khách Hàng</th>
                                    <th>Số ĐT</th>
                                    <th>Hạng Phòng</th>
                                    <th style="text-align: center;">Số Phòng</th>
                                    <th>Yêu Cầu Đặc Biệt</th>
                                    <th>Chi Tiết / Mốc Giờ</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${dashboardData.checkOutTodayList}" var="checkout" varStatus="status">
                                    <c:if test="${status.index < 10}">
                                        <tr>
                                            <td><strong>${checkout.bookingCode}</strong></td>
                                            <td>${checkout.guestFullName}</td>
                                            <td>${checkout.guestPhone}</td>
                                            <td>${checkout.roomTypeName}</td>
                                            <td style="text-align: center;">${checkout.numRooms}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${checkout.requestType eq 'Trả phòng muộn'}">
                                                        <span class="badge-req-custom badge-style-late">Trả phòng muộn</span>
                                                    </c:when>
                                                    <c:when test="${not empty checkout.requestType}">
                                                        <span class="badge badge-late">${checkout.requestType}</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-muted">-</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="req-details-text">
                                                <c:out value="${checkout.requestDetails}" default="-"/>
                                            </td>
                                        </tr>
                                    </c:if>
                                </c:forEach>
                                <c:if test="${empty dashboardData.checkOutTodayList}">
                                    <tr><td colspan="7" class="text-center">Không có phòng nào làm thủ tục trả phòng hôm nay.</td></tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                    <div class="view-all-footer">
                        <a href="${pageContext.request.contextPath}/Checkout" class="view-all-link">
                            Xem tất cả danh sách Check-out <span class="arrow-icon">→</span>
                        </a>
                    </div>
                </section>

                
            </div>
        </div>
    </body>
</html>