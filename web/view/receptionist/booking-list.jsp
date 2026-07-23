<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <jsp:include page="/view/staff/header.jsp" />
        <meta charset="UTF-8">
        <title>Quản lý đặt phòng | La Mer Hotel</title>

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/view/assets/css/booking-list.css?v=<%= System.currentTimeMillis() %>">
    </head>

    <body>
        <jsp:include page="/view/staff/navbar.jsp" />

        <c:set var="ctx" value="${pageContext.request.contextPath}" />

        <main class="booking-page">

            <form class="booking-filter" method="get" action="${ctx}/booking-list">

                <div class="filter-row filter-row-1">
                    <input class="filter-control search-control"
                           type="text"
                           name="keyword"
                           value="${keyword}"
                           placeholder="Tìm theo mã booking, tên khách, SĐT hoặc email">

                    <select class="filter-control" name="status">
                        <option value="" ${empty status ? 'selected="selected"' : ''}>
                            Trạng thái booking
                        </option>
                        <option value="Chờ xử lý" ${status == 'Chờ xử lý' ? 'selected="selected"' : ''}>
                            Chờ xử lý
                        </option>
                        <option value="Đã xác nhận" ${status == 'Đã xác nhận' ? 'selected="selected"' : ''}>
                            Đã xác nhận
                        </option>
                        <option value="Đã nhận phòng" ${status == 'Đã nhận phòng' ? 'selected="selected"' : ''}>
                            Đã nhận phòng
                        </option>
                        <option value="Đã trả phòng" ${status == 'Đã trả phòng' ? 'selected="selected"' : ''}>
                            Đã trả phòng
                        </option>
                        <option value="Đã hủy" ${status == 'Đã hủy' ? 'selected="selected"' : ''}>
                            Đã hủy
                        </option>
                    </select>

                    <select class="filter-control" name="paymentStatus">
                        <option value="" ${empty paymentStatus ? 'selected="selected"' : ''}>
                            Trạng thái thanh toán
                        </option>
                        <option value="Chưa thanh toán" ${paymentStatus == 'Chưa thanh toán' ? 'selected="selected"' : ''}>
                            Chưa thanh toán
                        </option>
                        <option value="Đã đặt cọc" ${paymentStatus == 'Đã đặt cọc' ? 'selected="selected"' : ''}>
                            Đã đặt cọc
                        </option>
                        <option value="Đã thanh toán" ${paymentStatus == 'Đã thanh toán' ? 'selected="selected"' : ''}>
                            Đã thanh toán
                        </option>
                    </select>

                    <select class="filter-control" name="source">
                        <option value="" ${empty source ? 'selected="selected"' : ''}>
                            Kênh đặt phòng
                        </option>
                        <option value="Đặt phòng trực tuyến" ${source == 'Đặt phòng trực tuyến' ? 'selected="selected"' : ''}>
                            Đặt online
                        </option>
                        <option value="Đặt phòng tại quầy" ${source == 'Đặt phòng tại quầy' ? 'selected="selected"' : ''}>
                            Đặt tại quầy
                        </option>
                    </select>
                </div>

                <div class="filter-row filter-row-2">
                    <select class="filter-control" name="roomTypeId">
                        <option value="all" ${filterRoomTypeId == 'all' ? 'selected="selected"' : ''}>
                            Hạng phòng đã đặt
                        </option>

                        <c:forEach var="rt" items="${roomTypes}">
                            <option value="${rt.roomTypeIdText}" ${filterRoomTypeId == rt.roomTypeIdText ? 'selected="selected"' : ''}>
                                ${rt.typeName}
                            </option>
                        </c:forEach>
                    </select>

                    <select class="filter-control" name="filterStaffId">
                        <option value="all" ${filterStaffId == 'all' ? 'selected="selected"' : ''}>
                            Lễ tân phụ trách
                        </option>

                        <c:forEach var="s" items="${staffList}">
                            <option value="${s.staffIdText}" ${filterStaffId == s.staffIdText ? 'selected="selected"' : ''}>
                                ${s.fullName}
                            </option>
                        </c:forEach>
                    </select>

                    <input class="filter-control"
                           type="text"
                           name="roomNumber"
                           value="${roomNumber}"
                           placeholder="Tìm theo số phòng được gán hoặc Chưa gán">

                    <input class="filter-control"
                           type="text"
                           name="dateFilter"
                           value="${dateFilterDisplay}"
                           placeholder="Ngày nhận/trả phòng dự kiến: dd/mm/yyyy">
                </div>

                <div class="filter-row filter-row-3">
                    <select class="filter-control sort-control" name="sort">
                        <option value="newest" ${sort == 'newest' || empty sort ? 'selected="selected"' : ''}>
                            Sắp xếp theo ngày tạo: Mới nhất
                        </option>
                        <option value="oldest" ${sort == 'oldest' ? 'selected="selected"' : ''}>
                            Sắp xếp theo ngày tạo: Cũ nhất
                        </option>
                        <option value="checkinAsc" ${sort == 'checkinAsc' ? 'selected="selected"' : ''}>
                            Sắp xếp theo ngày nhận phòng gần nhất
                        </option>
                        <option value="checkoutAsc" ${sort == 'checkoutAsc' ? 'selected="selected"' : ''}>
                            Sắp xếp theo ngày trả phòng gần nhất
                        </option>
                    </select>

                    <div class="filter-buttons">
                        <button type="submit" class="blue-btn">
                            Tìm kiếm
                        </button>

                        <a href="${ctx}/booking-list" class="blue-btn">
                            Làm mới
                        </a>
                    </div>

                    <a href="${ctx}/walk-in-booking" class="add-booking-btn">
                        THÊM ĐẶT PHÒNG TẠI QUẦY
                    </a>
                </div>

            </form>

            <div class="booking-table-wrap">
                <table class="booking-table">
                    <thead>
                        <tr>
                            <th>STT</th>
                            <th>MÃ BOOKING</th>
                            <th>KHÁCH HÀNG</th>
                            <th>PHÒNG / HẠNG PHÒNG</th>
                            <th>THỜI GIAN THEO ĐƠN</th>
                            <th>THỜI GIAN THỰC TẾ</th>
                            <th>TRẠNG THÁI</th>
                            <th>THANH TOÁN</th>
                            <th>LỄ TÂN</th>
                            <th>HÀNH ĐỘNG</th>
                        </tr>
                    </thead>

                    <tbody>
                        <c:choose>
                            <c:when test="${empty bookingList}">
                                <tr>
                                    <td colspan="${bookingTableColumnCount}" class="empty-row">
                                        Không có dữ liệu đặt phòng.
                                    </td>
                                </tr>
                            </c:when>

                            <c:otherwise>
                                <c:forEach var="b" items="${bookingList}" varStatus="loop">
                                    <tr>
                                        <td>${(currentPage - 1) * pageSize + loop.index + 1}</td>

                                        <td>
                                            <a class="booking-code js-booking-detail"
                                               href="${ctx}/staff-booking-detail?bookingId=${b.bookingId}"
                                               data-url="${ctx}/staff-booking-detail?bookingId=${b.bookingId}">
                                                ${b.bookingCode}
                                            </a>
                                        </td>

                                        <td>${b.guestName}</td>

                                        <td>
                                            <div class="room-type-line">
                                                ${b.roomTypeName}
                                            </div>

                                            <div class="room-number-line">
                                                ${b.numRooms} phòng -
                                                <c:choose>
                                                    <c:when test="${not empty b.roomNumbers && b.roomNumbers != 'Chưa gán'}">
                                                        ${b.roomNumbers}
                                                    </c:when>
                                                    <c:otherwise>
                                                        Chưa gán
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </td>

                                        <td>
                                            <div>CI dự kiến: ${b.checkinDateText}</div>
                                            <div>CO dự kiến: ${b.checkoutDateText}</div>
                                        </td>

                                        <td>
                                            <div>
                                                CI thực tế:
                                                <c:choose>
                                                    <c:when test="${not empty b.actualCheckinTime}">
                                                        ${b.actualCheckinTime}
                                                    </c:when>
                                                    <c:otherwise>-</c:otherwise>
                                                </c:choose>
                                            </div>

                                            <div>
                                                CO thực tế:
                                                <c:choose>
                                                    <c:when test="${not empty b.actualCheckoutTime}">
                                                        ${b.actualCheckoutTime}
                                                    </c:when>
                                                    <c:otherwise>-</c:otherwise>
                                                </c:choose>
                                            </div>
                                        </td>

                                        <td>
                                            <c:choose>
                                                <c:when test="${b.bookingStatus == 'Chờ xử lý'}">
                                                    <span class="badge status-pending">${b.bookingStatus}</span>
                                                </c:when>

                                                <c:when test="${b.bookingStatus == 'Đã xác nhận'}">
                                                    <span class="badge status-confirmed">${b.bookingStatus}</span>
                                                </c:when>

                                                <c:when test="${b.bookingStatus == 'Đã nhận phòng'}">
                                                    <span class="badge status-checked-in">${b.bookingStatus}</span>
                                                </c:when>

                                                <c:when test="${b.bookingStatus == 'Đã trả phòng'}">
                                                    <span class="badge status-checked-out">${b.bookingStatus}</span>
                                                </c:when>

                                                <c:when test="${b.bookingStatus == 'Đã hủy'}">
                                                    <span class="badge status-cancelled">${b.bookingStatus}</span>
                                                </c:when>

                                                <c:otherwise>
                                                    <span class="badge status-default">${b.bookingStatus}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>

                                        <td>
                                            <c:choose>
                                                <c:when test="${b.paymentStatus == 'Chưa thanh toán'}">
                                                    <span class="badge payment-unpaid">${b.paymentStatus}</span>
                                                </c:when>

                                                <c:when test="${b.paymentStatus == 'Đã đặt cọc'}">
                                                    <span class="badge payment-deposit">${b.paymentStatus}</span>
                                                </c:when>

                                                <c:when test="${b.paymentStatus == 'Đã thanh toán'}">
                                                    <span class="badge payment-paid">${b.paymentStatus}</span>
                                                </c:when>

                                                <c:otherwise>
                                                    <span class="badge payment-default">${b.paymentStatus}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>

                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty b.staffName}">
                                                    ${b.staffName}
                                                </c:when>
                                                <c:otherwise>-</c:otherwise>
                                            </c:choose>
                                        </td>

                                        <td class="action-cell">
                                            <c:set var="isPending" value="${b.bookingStatus == 'Chờ xử lý'}" />
                                            <c:set var="isConfirmed" value="${b.bookingStatus == 'Đã xác nhận'}" />
                                            <c:set var="isCheckedIn" value="${b.bookingStatus == 'Đã nhận phòng'}" />
                                            <c:set var="isCheckedOut" value="${b.bookingStatus == 'Đã trả phòng'}" />
                                            <c:set var="isCancelled" value="${b.bookingStatus == 'Đã hủy'}" />

                                            <c:set var="canAnyRequest" value="${!isCheckedOut && !isCancelled}" />
                                            <c:set var="canExtendRequest" value="${isConfirmed || isCheckedIn}" />
                                            <c:set var="canUpgradeRequest" value="${isConfirmed}" />
                                            <c:set var="canCancelRequest" value="${isPending || isConfirmed}" />
                                            <c:set var="canOtherRequest" value="${isConfirmed || isCheckedIn}" />

                                            <div class="action-buttons">
                                                <c:choose>
                                                    <c:when test="${canAnyRequest}">
                                                        <div class="request-action-wrap">
                                                            <button type="button" class="action-btn main-btn request-toggle">
                                                                Yêu cầu
                                                            </button>

                                                            <div class="request-action-menu">
                                                                <c:if test="${canExtendRequest}">
                                                                    <a class="js-counter-request"
                                                                       href="${ctx}/counter-request?bookingId=${b.bookingId}&type=extend"
                                                                       data-url="${ctx}/counter-request?bookingId=${b.bookingId}&type=extend">
                                                                        Gia hạn ngày ở
                                                                    </a>
                                                                </c:if>

                                                                <c:if test="${canUpgradeRequest}">
                                                                    <a class="js-counter-request"
                                                                       href="${ctx}/counter-request?bookingId=${b.bookingId}&type=upgrade"
                                                                       data-url="${ctx}/counter-request?bookingId=${b.bookingId}&type=upgrade">
                                                                        Thay đổi hạng phòng
                                                                    </a>
                                                                </c:if>

                                                                <c:if test="${canCancelRequest}">
                                                                    <a class="js-counter-request"
                                                                       href="${ctx}/counter-request?bookingId=${b.bookingId}&type=cancel"
                                                                       data-url="${ctx}/counter-request?bookingId=${b.bookingId}&type=cancel">
                                                                        Hủy booking
                                                                    </a>
                                                                </c:if>

                                                                <c:if test="${canOtherRequest}">
                                                                    <a class="js-counter-request"
                                                                       href="${ctx}/counter-request?bookingId=${b.bookingId}&type=other"
                                                                       data-url="${ctx}/counter-request?bookingId=${b.bookingId}&type=other">
                                                                        Yêu cầu khác
                                                                    </a>
                                                                </c:if>
                                                            </div>
                                                        </div>
                                                    </c:when>

                                                    <c:otherwise>
                                                        <span class="action-btn disabled-btn">Yêu cầu</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>

            <div class="table-footer">
                <div>
                    Hiển thị
                    <c:choose>
                        <c:when test="${totalBookings == 0}">0</c:when>
                        <c:otherwise>${(currentPage - 1) * pageSize + 1}</c:otherwise>
                    </c:choose>
                    đến
                    <c:choose>
                        <c:when test="${currentPage * pageSize > totalBookings}">
                            ${totalBookings}
                        </c:when>
                        <c:otherwise>
                            ${currentPage * pageSize}
                        </c:otherwise>
                    </c:choose>
                    trong tổng số ${totalBookings} đặt phòng
                </div>

                <c:if test="${totalPages > 1}">
                    <div class="pagination">
                        <c:choose>
                            <c:when test="${currentPage > 1}">
                                <a class="page-btn"
                                   href="${ctx}/booking-list?${pagingQuery}&page=${currentPage - 1}">
                                    ‹ Trước
                                </a>
                            </c:when>
                            <c:otherwise>
                                <span class="page-btn disabled">‹ Trước</span>
                            </c:otherwise>
                        </c:choose>

                        <c:set var="windowSize" value="${paginationWindowSize}" />
                        <c:set var="startPage" value="${currentPage - windowSize}" />
                        <c:set var="endPage" value="${currentPage + windowSize}" />

                        <c:if test="${startPage < 1}">
                            <c:set var="startPage" value="1" />
                        </c:if>

                        <c:if test="${endPage > totalPages}">
                            <c:set var="endPage" value="${totalPages}" />
                        </c:if>

                        <c:if test="${startPage > 1}">
                            <a class="page-btn" href="${ctx}/booking-list?${pagingQuery}&page=1">
                                1
                            </a>

                            <c:if test="${startPage > 2}">
                                <span class="page-dot">...</span>
                            </c:if>
                        </c:if>

                        <c:forEach begin="${startPage}" end="${endPage}" var="p">
                            <c:choose>
                                <c:when test="${p == currentPage}">
                                    <span class="page-btn active">${p}</span>
                                </c:when>
                                <c:otherwise>
                                    <a class="page-btn" href="${ctx}/booking-list?${pagingQuery}&page=${p}">
                                        ${p}
                                    </a>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>

                        <c:if test="${endPage < totalPages}">
                            <c:if test="${endPage < totalPages - 1}">
                                <span class="page-dot">...</span>
                            </c:if>

                            <a class="page-btn" href="${ctx}/booking-list?${pagingQuery}&page=${totalPages}">
                                ${totalPages}
                            </a>
                        </c:if>

                        <c:choose>
                            <c:when test="${currentPage < totalPages}">
                                <a class="page-btn"
                                   href="${ctx}/booking-list?${pagingQuery}&page=${currentPage + 1}">
                                    Sau ›
                                </a>
                            </c:when>
                            <c:otherwise>
                                <span class="page-btn disabled">Sau ›</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:if>
            </div>

        </main>

        <div id="bookingPopupModal" class="booking-detail-modal">
            <div class="booking-detail-backdrop" onclick="closeBookingPopup(false)"></div>

            <iframe id="bookingPopupFrame"
                    class="booking-detail-frame"
                    src=""
                    frameborder="0">
            </iframe>
        </div>

        <script>
            var POPUP_CLOSE_DELAY_MS = Number("${popupCloseDelayMs}");
            var REQUEST_MENU_SAFE_GAP_PX = Number("${requestMenuSafeGapPx}");

            function openBookingPopup(url) {
                var modal = document.getElementById("bookingPopupModal");
                var frame = document.getElementById("bookingPopupFrame");

                frame.src = url;
                modal.classList.add("show");
                document.body.classList.add("modal-open");
            }

            function closeBookingPopup(reloadPage) {
                var modal = document.getElementById("bookingPopupModal");
                var frame = document.getElementById("bookingPopupFrame");

                modal.classList.remove("show");
                document.body.classList.remove("modal-open");

                setTimeout(function () {
                    frame.src = "";

                    if (reloadPage === true) {
                        window.location.reload();
                    }
                }, POPUP_CLOSE_DELAY_MS);
            }

            function closeBookingDetailPopup() {
                closeBookingPopup(false);
            }

            function closeCounterRequestPopup() {
                closeBookingPopup(true);
            }

            document.addEventListener("DOMContentLoaded", function () {
                var popupLinks = document.querySelectorAll(".js-booking-detail, .js-counter-request");

                popupLinks.forEach(function (link) {
                    link.addEventListener("click", function (event) {
                        event.preventDefault();

                        var url = this.getAttribute("data-url");
                        openBookingPopup(url);
                    });
                });

                var toggles = document.querySelectorAll(".request-toggle");

                toggles.forEach(function (button) {
                    button.addEventListener("click", function (event) {
                        event.stopPropagation();

                        var wrap = this.closest(".request-action-wrap");
                        var tableWrap = this.closest(".booking-table-wrap");

                        document.querySelectorAll(".request-action-wrap.open").forEach(function (item) {
                            if (item !== wrap) {
                                item.classList.remove("open");
                                item.classList.remove("open-up");
                            }
                        });

                        var willOpen = !wrap.classList.contains("open");

                        wrap.classList.remove("open");
                        wrap.classList.remove("open-up");

                        if (!willOpen) {
                            return;
                        }

                        wrap.classList.add("open");

                        var menu = wrap.querySelector(".request-action-menu");

                        if (!menu) {
                            return;
                        }

                        var menuHeight = menu.offsetHeight;
                        var buttonRect = button.getBoundingClientRect();

                        var tableBottom;

                        if (tableWrap) {
                            tableBottom = tableWrap.getBoundingClientRect().bottom;
                        } else {
                            tableBottom = window.innerHeight;
                        }

                        var spaceBelowInTable = tableBottom - buttonRect.bottom;

                        if (spaceBelowInTable < menuHeight + REQUEST_MENU_SAFE_GAP_PX) {
                            wrap.classList.add("open-up");
                        }
                    });
                });

                document.addEventListener("click", function () {
                    document.querySelectorAll(".request-action-wrap.open").forEach(function (item) {
                        item.classList.remove("open");
                        item.classList.remove("open-up");
                    });
                });
            });

            document.addEventListener("keydown", function (event) {
                if (event.key === "Escape") {
                    closeBookingPopup(false);
                }
            });
        </script>
    </body>
</html>