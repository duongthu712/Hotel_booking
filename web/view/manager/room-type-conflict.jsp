<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Xử lý xung đột Hạng phòng</title>
        <jsp:include page="/view/staff/header.jsp" />
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/view/assets/css/room-type-conflict.css?v=<%= System.currentTimeMillis() %>">
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    </head>
    <body>
        <jsp:include page="/view/staff/navbar.jsp" />

        <div class="management-wrapper">
            <div class="conflict-container">
                <div class="header-row">
                    <h2>Xử lý xóa hạng phòng: ${roomTypeName}</h2>
                    <a href="${pageContext.request.contextPath}/roomtypelist" class="btn-luxury">Quay lại danh sách</a>
                </div>

                <div class="alert alert-info"color: #1a446c;">
                    <strong>Lưu ý:</strong> Hạng phòng này hiện đang có giao dịch chưa hoàn tất. Vui lòng liên hệ khách hàng để xử lý.
                </div>

                <h4>1. Khách đang lưu trú (${stayingList.size()})</h4>
                <table class="table">
                    <thead>
                        <tr><th>Số phòng</th><th>Tên khách</th><th>SĐT</th><th>Ngày trả phòng</th></tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${stayingList}">
                            <tr>
                                <td>${item.roomNumber}</td>
                                <td>${item.fullName}</td>
                                <td>${item.phone}</td>
                                <td>${item.checkoutDate}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

                <h4>2. Đơn đặt tương lai (${futureBookings.size()})</h4>
                <table class="table">
                    <thead>
                        <tr><th>Mã đặt</th><th>Tên khách</th><th>SĐT</th><th>Ngày Check-in</th><th>Hành động</th></tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${futureBookings}">
                            <tr>
                                <td>${item.bookingCode}</td>
                                <td>${empty item.fullName ? 'Chưa gán' : item.fullName}</td>
                                <td>${empty item.phone ? 'N/A' : item.phone}</td>
                                <td>${item.checkinDate}</td>
                                <td><a href="editbooking?code=${item.bookingCode}" class="btn-luxury">Xử lý đơn</a></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

                <div class="mt-5 pt-4 border-top" style="text-align: center;">
                    <form action="roomtypeconflict" method="POST">
                        <input type="hidden" name="action" value="confirmDeactive">
                        <input type="hidden" name="id" value="${param.id}">
                        <button type="submit" class="btn-confirm">Xác nhận ngừng kinh doanh hạng phòng này</button>
                    </form>
                </div>
            </div>
    </body>
</html>