<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/view/staff/header.jsp" />
        <title>Đặt Phòng Tại Quầy</title>
        <link href="${pageContext.request.contextPath}/view/assets/css/walk-in-booking.css?v=<%=System.currentTimeMillis()%>" rel="stylesheet" type="text/css">
    </head>
    <body>
        <jsp:include page="/view/staff/navbar.jsp" />

        <div class="walkin-container">
            <div class="walkin-search-card">
                <h3 class="walkin-title">TÌM PHÒNG TRỐNG</h3>
                <form action="${pageContext.request.contextPath}/walk-in-booking" method="GET" class="walkin-search-form">
                    <div class="walkin-search-field">
                        <label>Ngày đến</label>
                        <input type="date" id="checkIn" name="checkInDate" value="${param.checkInDate}" required>
                    </div>
                    <div class="walkin-search-field">
                        <label>Ngày đi</label>
                        <input type="date" id="checkOut" name="checkOutDate" value="${param.checkOutDate}" required>
                    </div>
                    <button type="submit" class="walkin-search-btn">Tìm kiếm</button>
                </form>
            </div>

            <h3 class="walkin-title" style="margin-top: 40px; text-align: center;">DANH SÁCH HẠNG PHÒNG</h3>
            <table class="walkin-result-table">
                <thead>
                    <tr>
                        <th>Hạng phòng</th>
                        <th>Mô tả</th>
                        <th>Giá/đêm</th>
                            <c:if test="${isSearching}">
                            <th>Số lượng trống</th>
                            <th>Hành động</th>
                            </c:if>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${allRoomTypes}" var="rt">
                        <tr>
                            <td><strong>${rt.typeName}</strong></td>
                            <td style="font-size: 13px; max-width: 300px;">
                                ${rt.description.length() > 100 ? rt.description.substring(0, 100).concat('...') : rt.description}
                            </td>
                            <td>
                                <fmt:formatNumber value="${rt.basePrice}" type="number" groupingUsed="true"/> VNĐ
                            </td>

                            <c:if test="${isSearching}">
                                <td style="color: ${rt.availableRooms == 0 ? '#d63c3c' : '#28a745'}; font-weight: bold;">
                                    ${rt.availableRooms == -1 ? '-' : (rt.availableRooms == 0 ? 'Hết phòng' : rt.availableRooms)}
                                </td>
                                <td>
                                    <c:if test="${rt.availableRooms > 0}">
                                        <a href="create-booking?typeId=${rt.roomTypeId}&in=${param.checkInDate}&out=${param.checkOutDate}" 
                                           class="walkin-search-btn" style="text-decoration: none; padding: 5px 15px;">Đặt ngay</a>
                                    </c:if>
                                </td>
                            </c:if>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <script src="${pageContext.request.contextPath}/view/assets/javascript/booking-calendar.js"></script>
    </body>
</html>