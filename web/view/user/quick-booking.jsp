<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <title>Đặt phòng nhanh - La Mer Hotel</title>

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

            <div class="quick-booking-container">

                <div class="quick-booking-heading">
                    <span>LA MER HOTEL</span>
                    <h1>ĐẶT PHÒNG NHANH</h1>

                    <p>
                        Chọn thời gian lưu trú, hạng phòng, số lượng phòng,
                        số người lớn và số trẻ em.
                    </p>
                </div>

                <c:if test="${not empty error}">
                    <div class="booking-error">
                        <strong>Thông báo:</strong>
                        <c:out value="${error}"/>
                    </div>
                </c:if>

                <div class="booking-left">

                    <section class="booking-section">

                        <div class="section-title">

                            <div class="section-icon">▣</div>

                            <div>
                                <h2>KIỂM TRA PHÒNG TRỐNG</h2>

                                <p>
                                    Hệ thống sẽ kiểm tra phòng và sức chứa trước khi
                                    chuyển sang trang nhập thông tin khách hàng.
                                </p>
                            </div>

                        </div>

                        <form action="${pageContext.request.contextPath}/quick-booking"
                              method="post"
                              id="quickBookingForm"
                              novalidate>

                            <div class="form-grid">

                                <div class="form-group">
                                    <label for="checkIn">
                                        Ngày nhận phòng <span>*</span>
                                    </label>

                                    <input type="date"
                                           id="checkIn"
                                           name="checkIn"
                                           min="${today}"
                                           value="${checkIn}">
                                </div>

                                <div class="form-group">
                                    <label for="checkOut">
                                        Ngày trả phòng <span>*</span>
                                    </label>

                                    <input type="date"
                                           id="checkOut"
                                           name="checkOut"
                                           min="${today}"
                                           value="${checkOut}">
                                </div>

                                <div class="form-group full-row">
                                    <label for="roomTypeId">
                                        Hạng phòng <span>*</span>
                                    </label>

                                    <select id="roomTypeId" name="roomTypeId">

                                        <option value="">
                                            -- Chọn hạng phòng --
                                        </option>

                                        <c:forEach var="room" items="${roomTypes}">

                                            <option value="${room.roomTypeId}"
                                                    data-capacity="${room.capacity}"
                                                    data-adults="${room.numGuests}"
                                                    data-children="${room.numChildren}"
                                                    <c:if test="${room.roomTypeId == param.roomTypeId
                                                                  || room.roomTypeId == selectedRoomTypeId}">
                                                        selected
                                                    </c:if>>

                                                ${room.typeName}
                                                -
                                                ${room.numGuests} người lớn,
                                                ${room.numChildren} trẻ em/phòng
                                                -
                                                <fmt:formatNumber value="${room.basePrice}"
                                                                  type="number"
                                                                  maxFractionDigits="0"/>
                                                VND/đêm

                                            </option>

                                        </c:forEach>

                                    </select>
                                </div>

                                <div class="booking-fields full-row">

                                    <div class="form-group">
                                        <label for="numRooms">
                                            Số lượng phòng <span>*</span>
                                        </label>

                                        <input type="number"
                                               id="numRooms"
                                               name="numRooms"
                                               min="1"
                                               step="1"
                                               inputmode="numeric"
                                               value="${numRooms}">
                                    </div>

                                    <div class="form-group">
                                        <label for="numGuests">
                                            Người lớn <span>*</span>
                                        </label>

                                        <input type="number"
                                               id="numGuests"
                                               name="numGuests"
                                               min="1"
                                               step="1"
                                               inputmode="numeric"
                                               value="${numGuests}">
                                    </div>

                                    <div class="form-group">
                                        <label for="numChildren">
                                            Trẻ em
                                        </label>

                                        <input type="number"
                                               id="numChildren"
                                               name="numChildren"
                                               min="0"
                                               step="1"
                                               inputmode="numeric"
                                               value="${numChildren}">
                                    </div>

                                </div>

                            </div>

                            <div class="quick-booking-note"
                                 id="capacityMessage">
                                Chọn hạng phòng để xem sức chứa tối đa.
                            </div>

                            <div class="quick-booking-note">
                                Sau khi thông tin hợp lệ, hệ thống sẽ chuyển sang
                                trang nhập thông tin khách hàng. Booking chỉ được
                                tạo và bắt đầu giữ phòng sau khi hoàn thành
                                Booking Form.
                            </div>

                            <button type="submit"
                                    class="continue-button">

                                KIỂM TRA VÀ TIẾP TỤC
                                <span>→</span>

                            </button>

                        </form>

                    </section>

                </div>

                <div class="quick-booking-benefits">

                    <div class="quick-benefit-item">
                        <div class="benefit-icon">✓</div>

                        <strong>Kiểm tra phòng trống</strong>

                        <span>
                            Kiểm tra theo đúng ngày và hạng phòng đã chọn.
                        </span>
                    </div>

                    <div class="quick-benefit-item">
                        <div class="benefit-icon">◇</div>

                        <strong>Chọn phòng nhanh</strong>

                        <span>
                            Không cần tìm kiếm qua danh sách nhiều hạng phòng.
                        </span>
                    </div>

                    <div class="quick-benefit-item">
                        <div class="benefit-icon">▢</div>

                        <strong>Thông tin chính xác</strong>

                        <span>
                            Giới hạn người lớn, trẻ em và tổng sức chứa
                            được lấy trực tiếp từ hệ thống.
                        </span>
                    </div>

                </div>

            </div>

        </main>

        <jsp:include page="/view/common/footer.jsp"/>

        <script>
            const checkInInput = document.getElementById("checkIn");
            const checkOutInput = document.getElementById("checkOut");
            const roomTypeSelect = document.getElementById("roomTypeId");
            const numRoomsInput = document.getElementById("numRooms");
            const capacityMessage = document.getElementById("capacityMessage");

            function readPositiveInteger(input) {
                const value = Number.parseInt(input.value, 10);
                return Number.isNaN(value) || value <= 0 ? 0 : value;
            }

            function updateCapacityMessage() {
                const selectedOption =
                        roomTypeSelect.options[roomTypeSelect.selectedIndex];

                const capacity =
                        Number.parseInt(
                                selectedOption.getAttribute("data-capacity"),
                                10
                        );

                const adults =
                        Number.parseInt(
                                selectedOption.getAttribute("data-adults"),
                                10
                        );

                const children =
                        Number.parseInt(
                                selectedOption.getAttribute("data-children"),
                                10
                        );

                const numRooms = readPositiveInteger(numRoomsInput);

                if (numRooms === 0) {
                    capacityMessage.textContent =
                            "Nhập số lượng phòng để xem sức chứa tối đa.";

                    return;
                }

                const maximumAdults = adults * numRooms;
                const maximumChildren = children * numRooms;
                const maximumOccupants = capacity * numRooms;
            }

            roomTypeSelect.addEventListener(
                    "change",
                    updateCapacityMessage
            );

            numRoomsInput.addEventListener(
                    "input",
                    updateCapacityMessage
            );

            checkInInput.addEventListener("change", function () {
                checkOutInput.min = checkInInput.value;

                if (!checkInInput.value) {
                    return;
                }

                if (!checkOutInput.value
                        || checkOutInput.value <= checkInInput.value) {

                    const nextDay =
                            new Date(checkInInput.value + "T00:00:00");

                    nextDay.setDate(nextDay.getDate() + 1);

                    checkOutInput.value =
                            nextDay.toISOString().split("T")[0];
                }
            });

            updateCapacityMessage();
        </script>

    </body>
</html>
