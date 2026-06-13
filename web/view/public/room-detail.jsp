<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.RoomType"%>
<%@ page import="model.RoomAmenity"%>
<%@ page import="model.RoomTypeService"%>
<%@ page import="java.util.List"%>
<%@ page import="java.text.NumberFormat"%>
<%@ page import="java.util.Locale"%>

<%!
    public String html(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    public String js(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("'", "\\'")
                .replace("\n", "")
                .replace("\r", "");
    }

    public String money(Object value) {
        if (value == null) {
            return "0";
        }

        try {
            NumberFormat numberFormat
                    = NumberFormat.getInstance(new Locale("vi", "VN"));

            return numberFormat.format(value);

        } catch (Exception exception) {
            return value.toString();
        }
    }

    public String formatBedInfo(int bedCount, String bedType) {
        if (bedType == null || bedType.trim().isEmpty()) {
            return bedCount + " giường";
        }

        String value = bedType.trim();

        /*
         * Nếu DB đã lưu đầy đủ:
         * 1 King Bed + 2 Twin Beds
         * thì không thêm "3 x" vào trước.
         */
        if (value.matches(".*\\d+.*") || value.contains("+")) {
            return value;
        }

        return bedCount + " x " + value;
    }

    public String serviceIcon(String name) {
        if (name == null) {
            return "fa-circle-check";
        }

        String lower = name.toLowerCase();

        if (lower.contains("nước")
                || lower.contains("lavie")
                || lower.contains("evian")) {

            return "fa-bottle-water";
        }

        if (lower.contains("trà")
                || lower.contains("cà phê")
                || lower.contains("coffee")
                || lower.contains("g7")
                || lower.contains("capsule")
                || lower.contains("nespresso")) {

            return "fa-mug-saucer";
        }

        if (lower.contains("snack")
                || lower.contains("bánh")) {

            return "fa-cookie-bite";
        }

        if (lower.contains("mì")) {
            return "fa-bowl-food";
        }

        if (lower.contains("sữa")) {
            return "fa-glass-water";
        }

        if (lower.contains("rượu")
                || lower.contains("vang")
                || lower.contains("champagne")) {

            return "fa-wine-glass";
        }

        if (lower.contains("trái cây")) {
            return "fa-apple-whole";
        }

        if (lower.contains("coca")
                || lower.contains("pepsi")
                || lower.contains("nước ngọt")) {

            return "fa-glass-water";
        }

        return "fa-circle-check";
    }

    public String amenityIcon(String name) {
        if (name == null) {
            return "fa-circle-check";
        }

        String lower = name.toLowerCase();

        if (lower.contains("khăn")) {
            return "fa-bath";
        }

        if (lower.contains("áo choàng")) {
            return "fa-shirt";
        }

        if (lower.contains("dép")) {
            return "fa-shoe-prints";
        }

        if (lower.contains("máy sấy")) {
            return "fa-wind";
        }

        if (lower.contains("ấm")) {
            return "fa-mug-hot";
        }

        if (lower.contains("gối")
                || lower.contains("chăn")) {

            return "fa-bed";
        }

        if (lower.contains("cốc")) {
            return "fa-mug-saucer";
        }

        if (lower.contains("móc")) {
            return "fa-shirt";
        }

        return "fa-circle-check";
    }
%>

<%
    RoomType room
            = (RoomType) request.getAttribute("room");

    String error
            = (String) request.getAttribute("error");

    String checkIn
            = (String) request.getAttribute("checkIn");

    String checkOut
            = (String) request.getAttribute("checkOut");

    String dateError
            = (String) request.getAttribute("dateError");

    String guestRoomWarning
            = (String) request.getAttribute("guestRoomWarning");

    String guestRoomError
            = (String) request.getAttribute("guestRoomError");

    String today
            = (String) request.getAttribute("today");

    if (checkIn == null) {
        checkIn = "";
    }

    if (checkOut == null) {
        checkOut = "";
    }

    if (dateError == null) {
        dateError = "";
    }

    if (guestRoomWarning == null) {
        guestRoomWarning = "";
    }

    if (guestRoomError == null) {
        guestRoomError = "";
    }

    if (today == null) {
        today = "";
    }

    Integer numRoomsObj
            = (Integer) request.getAttribute("numRooms");

    Integer numGuestsObj
            = (Integer) request.getAttribute("numGuests");

    Integer availableRoomsObj
            = (Integer) request.getAttribute("availableRooms");

    Integer maxGuestsByRoomsObj
            = (Integer) request.getAttribute("maxGuestsByRooms");

    Long nightsObj
            = (Long) request.getAttribute("nights");

    Boolean hasValidDateObj
            = (Boolean) request.getAttribute("hasValidDate");

    int numRooms
            = numRoomsObj == null ? 1 : numRoomsObj;

    int numGuests
            = numGuestsObj == null ? 1 : numGuestsObj;

    int availableRooms
            = availableRoomsObj == null ? -1 : availableRoomsObj;

    int maxGuestsByRooms
            = maxGuestsByRoomsObj == null ? 1 : maxGuestsByRoomsObj;

    long nights
            = nightsObj == null ? 0 : nightsObj;

    boolean hasValidDate
            = hasValidDateObj != null && hasValidDateObj;

    String pageTitle
            = "Chi tiết phòng - La Mer Hotel";

    if (room != null) {
        pageTitle = room.getTypeName() + " - La Mer Hotel";
    }

    List<String> images = null;

    if (room != null) {
        images = room.getImageUrl();
    }

    String firstImage
            = "https://placehold.co/700x450?text=La+Mer+Room";

    if (images != null
            && !images.isEmpty()
            && images.get(0) != null
            && !images.get(0).trim().isEmpty()) {

        firstImage = images.get(0);
    }

    String basePrice = "0";
    int capacityPerRoom = 1;

    if (room != null) {
        capacityPerRoom = room.getCapacity();

        if (room.getBasePrice() != null) {
            basePrice = room.getBasePrice().toPlainString();
        }
    }
%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">

        <meta name="viewport"
              content="width=device-width, initial-scale=1.0">

        <title><%= html(pageTitle) %></title>

        <link rel="stylesheet"
              href="<%= request.getContextPath() %>/view/assets/css/navbar.css">

        <link rel="stylesheet"
              href="<%= request.getContextPath() %>/view/assets/css/footer.css">

        <link rel="stylesheet"
              href="<%= request.getContextPath() %>/view/assets/css/room-detail.css?v=63">

        <link rel="stylesheet"
              href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    </head>

    <body>
        <jsp:include page="/view/common/navbar.jsp" />

        <main class="room-detail-wrapper">

            <% if (error != null && !error.trim().isEmpty()) { %>

            <div class="room-error-box server-message">
                <i class="fa-solid fa-circle-exclamation"></i>

                <span>
                    <%= html(error) %>
                </span>
            </div>

            <% } %>

            <% if (room != null) { %>

            <div class="room-breadcrumb">
                <a href="<%= request.getContextPath() %>/home">
                    <i class="fa-solid fa-house"></i>
                </a>

                <span>›</span>

                <a href="<%= request.getContextPath() %>/home">
                    Trang chủ
                </a>

                <span>›</span>

                <a href="<%= request.getContextPath() %>/search">
                    Phòng nghỉ
                </a>

                <span>›</span>

                <span>
                    <%= html(room.getTypeName()) %>
                </span>
            </div>

            <section class="room-top-grid">

                <!-- ===================================================== -->
                <!-- GALLERY BÊN TRÁI -->
                <!-- ===================================================== -->

                <div class="room-gallery-card">

                    <div class="main-image-box">

                        <% if (images != null && images.size() > 1) { %>

                        <button type="button"
                                class="gallery-btn gallery-btn-left"
                                onclick="moveImage(-1)"
                                aria-label="Ảnh trước">
                        </button>

                        <% } %>

                        <div class="image-slider-window">

                            <div class="image-slider-track"
                                 id="imageSliderTrack">

                                <% if (images != null && !images.isEmpty()) { %>

                                <% for (int index = 0;
                                            index < images.size();
                                            index++) { %>

                                <img class="room-main-image"
                                     src="<%= html(images.get(index)) %>"
                                     alt="<%= html(room.getTypeName()) %>"
                                     onerror="this.onerror=null; this.src='https://placehold.co/700x450?text=La+Mer+Room';">

                                <% } %>

                                <% } else { %>

                                <img class="room-main-image"
                                     src="<%= html(firstImage) %>"
                                     alt="<%= html(room.getTypeName()) %>">

                                <% } %>
                            </div>
                        </div>

                        <% if (images != null && images.size() > 1) { %>

                        <div class="image-count">
                            <span id="currentImageNumber">1</span>
                            / <%= images.size() %>
                        </div>

                        <button type="button"
                                class="gallery-btn gallery-btn-right"
                                onclick="moveImage(1)"
                                aria-label="Ảnh tiếp theo">
                        </button>

                        <% } %>
                    </div>

                    <div class="room-thumbnail-row"
                         id="thumbnailRow">

                        <% if (images != null && !images.isEmpty()) { %>

                        <% for (int index = 0;
                                    index < images.size();
                                    index++) { %>

                        <img class="room-thumbnail <%= index == 0 ? "active-thumb" : "" %>"
                             src="<%= html(images.get(index)) %>"
                             alt="<%= html(room.getTypeName()) %>"
                             data-index="<%= index %>"
                             onclick="goToImage(<%= index %>)"
                             onerror="this.style.display='none';">

                        <% } %>

                        <% } %>
                    </div>
                </div>

                <!-- ===================================================== -->
                <!-- THÔNG TIN VÀ ĐẶT PHÒNG BÊN PHẢI -->
                <!-- ===================================================== -->

                <div class="room-side-panel">

                    <div class="room-summary-card">

                        <div class="room-title-row">

                            <div>
                                <h1>
                                    <%= html(room.getTypeName()) %>
                                </h1>

                                <p>
                                    <%= html(room.getDescription()) %>
                                </p>
                            </div>

                            <div class="room-price-box">
                                <%= money(room.getBasePrice()) %> VND

                                <span>/ ĐÊM</span>
                            </div>
                        </div>

                        <div class="room-spec-row">

                            <div class="room-spec-card">
                                <i class="fa-solid fa-users"></i>

                                <div>
                                    <span>Sức chứa</span>

                                    <strong>
                                        <%= room.getCapacity() %>
                                        khách / phòng
                                    </strong>
                                </div>
                            </div>

                            <div class="room-spec-card">
                                <i class="fa-solid fa-bed"></i>

                                <div>
                                    <span>Giường</span>

                                    <strong>
                                        <%= html(
                                                formatBedInfo(
                                                        room.getBedCount(),
                                                        room.getBedType()
                                                )
                                        ) %>
                                    </strong>
                                </div>
                            </div>

                            <div class="room-spec-card">
                                <i class="fa-solid fa-maximize"></i>

                                <div>
                                    <span>Diện tích</span>

                                    <strong>
                                        <%= room.getAreaSqm() %> m²
                                    </strong>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- ================================================= -->
                    <!-- FORM KIỂM TRA PHÒNG -->
                    <!-- ================================================= -->

                    <form method="get"
                          class="booking-panel"
                          id="bookingPanel"
                          novalidate>

                        <input type="hidden"
                               name="roomTypeId"
                               value="<%= room.getRoomTypeId() %>">

                        <h2>Kiểm tra phòng & đặt phòng</h2>

                        <!--
                            Grid 2 cột:
                            hàng 1 = ngày nhận và ngày trả
                            hàng 2 = số khách và số phòng
                        -->

                        <div class="booking-input-grid">

                            <div class="booking-field booking-date-field">

                                <label for="checkInInput">
                                    Nhận phòng
                                </label>

                                <div class="field-with-icon">

                                    <input type="date"
                                           id="checkInInput"
                                           name="checkIn"
                                           value="<%= html(checkIn) %>"
                                           min="<%= html(today) %>">
                                </div>
                            </div>

                            <div class="booking-field booking-date-field">

                                <label for="checkOutInput">
                                    Trả phòng
                                </label>

                                <div class="field-with-icon">

                                    <input type="date"
                                           id="checkOutInput"
                                           name="checkOut"
                                           value="<%= html(checkOut) %>"
                                           min="<%= html(today) %>">
                                </div>
                            </div>

                            <div class="booking-field booking-quantity-field">

                                <label>
                                    Số khách lưu trú
                                </label>

                                <div class="quantity-box">

                                    <button type="button"
                                            onclick="changeGuests(-1)"
                                            aria-label="Giảm số khách">
                                        −
                                    </button>

                                    <input type="text"
                                           id="numGuests"
                                           name="numGuests"
                                           value="<%= numGuests %>"
                                           readonly>

                                    <button type="button"
                                            onclick="changeGuests(1)"
                                            aria-label="Tăng số khách">
                                        +
                                    </button>
                                </div>
                            </div>

                            <div class="booking-field booking-quantity-field">

                                <label>
                                    Số lượng phòng
                                </label>

                                <div class="quantity-box">

                                    <button type="button"
                                            onclick="changeQuantity(-1)"
                                            aria-label="Giảm số phòng">
                                        −
                                    </button>

                                    <input type="text"
                                           id="roomQuantity"
                                           name="numRooms"
                                           value="<%= numRooms %>"
                                           readonly>

                                    <button type="button"
                                            onclick="changeQuantity(1)"
                                            aria-label="Tăng số phòng">
                                        +
                                    </button>
                                </div>
                            </div>
                        </div>

                        <div class="capacity-note"
                             id="capacityNote">

                            Tối đa

                            <strong id="maxGuestText">
                                <%= maxGuestsByRooms %>
                            </strong>

                            khách cho

                            <strong id="roomCountText">
                                <%= numRooms %>
                            </strong>

                            phòng đã chọn.
                        </div>

                        <!--
                            Thông báo do JavaScript tạo.
                            Không lưu vào URL hoặc session.
                        -->

                        <div class="client-message"
                             id="clientMessage"
                             role="alert"
                             aria-live="polite">
                        </div>

                        <!-- ================================================= -->
                        <!-- THÔNG BÁO DO SERVLET TRẢ VỀ -->
                        <!-- ================================================= -->

                        <% if (!guestRoomError.isEmpty()) { %>

                        <div class="availability-box error server-message">
                            <i class="fa-solid fa-circle-exclamation"></i>

                            <span>
                                <%= html(guestRoomError) %>
                            </span>

                            <em>Đã điều chỉnh</em>
                        </div>

                        <% } else if (!guestRoomWarning.isEmpty()) { %>

                        <div class="availability-box warning server-message">
                            <i class="fa-solid fa-triangle-exclamation"></i>

                            <span>
                                <%= html(guestRoomWarning) %>
                            </span>

                            <em>Lưu ý</em>
                        </div>

                        <% } %>

                        <% if (!dateError.isEmpty()) { %>

                        <div class="availability-box error server-message">
                            <i class="fa-solid fa-circle-exclamation"></i>

                            <span>
                                <%= html(dateError) %>
                            </span>

                            <em>Chưa hợp lệ</em>
                        </div>

                        <% } else if (hasValidDate) { %>

                        <div class="availability-box success server-message">
                            <i class="fa-solid fa-circle-check"></i>

                            <span>
                                Còn

                                <strong>
                                    <%= availableRooms %>
                                </strong>

                                phòng khả dụng trong thời gian đã chọn
                            </span>

                            <em>
                                <%= nights %> đêm
                            </em>
                        </div>

                        <% } else { %>

                        <div class="availability-box neutral server-message">
                            <i class="fa-regular fa-calendar-days"></i>

                            <span>
                                Vui lòng chọn ngày nhận phòng và trả
                                phòng để kiểm tra phòng trống
                            </span>

                            <em>Chưa kiểm tra</em>
                        </div>

                        <% } %>

                        <div class="booking-price-row">

                            <span>Tạm tính</span>

                            <strong id="totalPrice">

                                <% if (hasValidDate) { %>

                                0 VND

                                <% } else { %>

                                --

                                <% } %>
                            </strong>
                        </div>

                        <p class="booking-tax-note">

                            <% if (hasValidDate) { %>

                            Giá phòng theo số đêm và số phòng đã chọn

                            <% } else { %>

                            Tổng tiền sẽ hiển thị sau khi kiểm tra phòng

                            <% } %>
                        </p>

                        <div class="booking-action-row">

                            <button type="submit"
                                    formaction="<%= request.getContextPath() %>/room-detail"
                                    class="check-availability-btn"
                                    data-action="check">

                                Kiểm tra phòng
                            </button>

                            <button type="submit"
                                    formaction="<%= request.getContextPath() %>/booking-form"
                                    class="room-booking-btn"
                                    data-action="book"
                                    <%= (!hasValidDate
                                            || availableRooms <= 0
                                            || !dateError.isEmpty()
                                            || numRooms > availableRooms)
                                            ? "disabled" : "" %>>

                                <i class="fa-solid fa-calendar-check"></i>

                                Đặt phòng ngay
                            </button>
                        </div>
                    </form>
                </div>
            </section>

            <!-- ========================================================= -->
            <!-- THÔNG TIN PHÒNG -->
            <!-- ========================================================= -->

            <section class="room-info-grid">

                <div class="room-info-card">

                    <h3>
                        <i class="fa-solid fa-circle-info"></i>
                        Thông tin phòng
                    </h3>

                    <ul>
                        <li>
                            <i class="fa-solid fa-check"></i>

                            <span>
                                <%= html(
                                        formatBedInfo(
                                                room.getBedCount(),
                                                room.getBedType()
                                        )
                                ) %>
                            </span>
                        </li>

                        <li>
                            <i class="fa-solid fa-check"></i>

                            <span>
                                Phòng phù hợp tối đa
                                <%= room.getCapacity() %> khách
                            </span>
                        </li>

                        <li>
                            <i class="fa-solid fa-check"></i>

                            <span>
                                Diện tích phòng
                                <%= room.getAreaSqm() %> m²
                            </span>
                        </li>

                        <li>
                            <i class="fa-solid fa-check"></i>
                            <span>Dọn phòng hằng ngày</span>
                        </li>

                        <li>
                            <i class="fa-solid fa-check"></i>
                            <span>Không gian nghỉ dưỡng hiện đại</span>
                        </li>
                    </ul>
                </div>

                <!-- ===================================================== -->
                <!-- DỊCH VỤ PHÒNG -->
                <!-- ===================================================== -->

                <div class="room-info-card">

                    <h3>
                        <i class="fa-solid fa-mug-saucer"></i>
                        Dịch vụ phòng
                    </h3>

                    <ul>
                        <%
                            List<RoomTypeService> services
                                    = room.getRoomTypeServices();

                            if (services != null
                                    && !services.isEmpty()) {

                                for (RoomTypeService service : services) {

                                    String serviceName = "";
                                    String unitPrice = "0";

                                    int totalQuantity
                                            = Math.max(
                                                    service.getQuantity(),
                                                    0
                                            );

                                    int freeQuantity
                                            = Math.max(
                                                    service.getIsFree(),
                                                    0
                                            );

                                    if (freeQuantity > totalQuantity) {
                                        freeQuantity = totalQuantity;
                                    }

                                    int paidQuantity
                                            = Math.max(
                                                    totalQuantity
                                                    - freeQuantity,
                                                    0
                                            );

                                    if (service.getRoomService() != null) {

                                        serviceName
                                                = service.getRoomService()
                                                        .getServiceName();

                                        unitPrice
                                                = money(
                                                        service.getRoomService()
                                                                .getUnitPrice()
                                                );
                                    }
                        %>

                        <li class="room-service-item">

                            <i class="fa-solid <%= serviceIcon(serviceName) %>"></i>

                            <div class="room-service-content">

                                <div class="room-service-main">

                                    <span>
                                        <%= html(serviceName) %>
                                    </span>

                                    <strong>
                                        x<%= totalQuantity %>
                                    </strong>
                                </div>

                                <div class="room-service-status">

                                    <% if (freeQuantity > 0) { %>

                                    <span class="free-label">
                                        <i class="fa-solid fa-circle-check"></i>

                                        Miễn phí <%= freeQuantity %>
                                    </span>

                                    <% } %>
                                </div>
                            </div>
                        </li>

                        <%
                                }
                            } else {
                        %>

                        <li>
                            <i class="fa-solid fa-circle-info"></i>

                            <span>
                                Đang cập nhật dịch vụ phòng
                            </span>
                        </li>

                        <%
                            }
                        %>
                    </ul>
                </div>

                <!-- ===================================================== -->
                <!-- TIỆN NGHI PHÒNG -->
                <!-- ===================================================== -->

                <div class="room-info-card wide">

                    <h3>
                        <i class="fa-regular fa-star"></i>
                        Tiện nghi phòng
                    </h3>

                    <div class="amenity-grid">

                        <%
                            List<RoomAmenity> amenities
                                    = room.getRoomAmenities();

                            if (amenities != null
                                    && !amenities.isEmpty()) {

                                for (RoomAmenity amenity : amenities) {
                        %>

                        <div class="amenity-item">

                            <i class="fa-solid <%= amenityIcon(amenity.getAmenityName()) %>"></i>

                            <span>
                                <%= html(amenity.getAmenityName()) %>
                            </span>
                        </div>

                        <%
                                }
                            } else {
                        %>

                        <div class="amenity-item">
                            <i class="fa-solid fa-circle-info"></i>

                            <span>
                                Đang cập nhật tiện nghi
                            </span>
                        </div>

                        <%
                            }
                        %>
                    </div>
                </div>
            </section>

            <!-- ========================================================= -->
            <!-- BENEFITS -->
            <!-- ========================================================= -->

            <section class="benefit-strip">

                <div class="benefit-item">
                    <i class="fa-solid fa-tag"></i>

                    <div>
                        <strong>Giá tốt nhất</strong>

                        <span>
                            Cam kết giá tốt nhất khi đặt trực tiếp
                        </span>
                    </div>
                </div>

                <div class="benefit-item">
                    <i class="fa-solid fa-shield-halved"></i>

                    <div>
                        <strong>Hủy miễn phí</strong>

                        <span>
                            Chính sách hủy theo điều kiện đặt phòng
                        </span>
                    </div>
                </div>

                <div class="benefit-item">
                    <i class="fa-solid fa-circle-check"></i>

                    <div>
                        <strong>Không phí đặt phòng</strong>

                        <span>
                            Không tính phí đặt phòng online
                        </span>
                    </div>
                </div>

                <div class="benefit-item">
                    <i class="fa-solid fa-headset"></i>

                    <div>
                        <strong>Hỗ trợ 24/7</strong>

                        <span>
                            Đội ngũ hỗ trợ mọi lúc, mọi nơi
                        </span>
                    </div>
                </div>
            </section>

            <% } %>
        </main>

        <jsp:include page="/view/common/footer.jsp" />

        <script>
            const imageList = [
            <% if (images != null && !images.isEmpty()) { %>
            <% for (int index = 0; index < images.size(); index++) { %>

            "<%= js(images.get(index)) %>"<%= index < images.size() - 1 ? "," : "" %>

            <% } %>
            <% } %>
            ];

            let currentImageIndex = 0;
            let isSliding = false;

            const basePrice
                    = Number("<%= basePrice %>");

            const nights
                    = Number("<%= nights %>");

            const availableRooms
                    = Number("<%= availableRooms %>");

            const hasValidDate
                    = <%= hasValidDate ? "true" : "false" %>;

            const capacityPerRoom
                    = Number("<%= capacityPerRoom %>");

            /* ==========================================================
             IMAGE GALLERY
             ========================================================== */

            function updateSliderPosition() {
                const track
                        = document.getElementById("imageSliderTrack");

                if (!track) {
                    return;
                }

                track.style.transform
                        = "translateX(-"
                        + (currentImageIndex * 100)
                        + "%)";

                updateActiveThumbnail();
                updateImageCount();
                scrollThumbnailIntoView();
            }

            function moveImage(step) {
                if (!imageList
                        || imageList.length <= 1
                        || isSliding) {

                    return;
                }

                isSliding = true;
                currentImageIndex += step;

                if (currentImageIndex < 0) {
                    currentImageIndex
                            = imageList.length - 1;
                }

                if (currentImageIndex >= imageList.length) {
                    currentImageIndex = 0;
                }

                updateSliderPosition();

                window.setTimeout(function () {
                    isSliding = false;
                }, 520);
            }

            function goToImage(index) {
                if (!imageList
                        || imageList.length === 0
                        || isSliding) {

                    return;
                }

                if (index < 0
                        || index >= imageList.length
                        || index === currentImageIndex) {

                    return;
                }

                isSliding = true;
                currentImageIndex = index;

                updateSliderPosition();

                window.setTimeout(function () {
                    isSliding = false;
                }, 520);
            }

            function updateActiveThumbnail() {
                const thumbnails
                        = document.querySelectorAll(".room-thumbnail");

                thumbnails.forEach(function (thumbnail) {
                    thumbnail.classList.remove("active-thumb");
                });

                const activeThumbnail
                        = document.querySelector(
                                ".room-thumbnail[data-index='"
                                + currentImageIndex
                                + "']"
                                );

                if (activeThumbnail) {
                    activeThumbnail.classList.add("active-thumb");
                }
            }

            function updateImageCount() {
                const counter
                        = document.getElementById(
                                "currentImageNumber"
                                );

                if (counter) {
                    counter.textContent
                            = String(currentImageIndex + 1);
                }
            }

            function scrollThumbnailIntoView() {
                const activeThumbnail
                        = document.querySelector(
                                ".room-thumbnail[data-index='"
                                + currentImageIndex
                                + "']"
                                );

                if (activeThumbnail) {
                    activeThumbnail.scrollIntoView({
                        behavior: "smooth",
                        block: "nearest",
                        inline: "center"
                    });
                }
            }

            /* ==========================================================
             MESSAGE
             ========================================================== */

            function escapeHtml(value) {
                const element = document.createElement("div");
                element.textContent = value;
                return element.innerHTML;
            }

            function clearClientMessage() {
                const messageBox
                        = document.getElementById("clientMessage");

                if (!messageBox) {
                    return;
                }

                messageBox.className = "client-message";
                messageBox.innerHTML = "";
            }

            function showClientMessage(type, message) {
                const messageBox
                        = document.getElementById("clientMessage");

                if (!messageBox) {
                    return;
                }

                let iconClass = "fa-circle-info";

                if (type === "error") {
                    iconClass = "fa-circle-exclamation";

                } else if (type === "warning") {
                    iconClass = "fa-triangle-exclamation";

                } else if (type === "success") {
                    iconClass = "fa-circle-check";
                }

                messageBox.className
                        = "client-message show " + type;

                messageBox.innerHTML
                        = "<i class=\"fa-solid "
                        + iconClass
                        + "\"></i>"
                        + "<span>"
                        + escapeHtml(message)
                        + "</span>";
            }

            function hideServerMessages() {
                document.querySelectorAll(".server-message")
                        .forEach(function (message) {
                            message.style.display = "none";
                        });
            }

            function isPageReload() {
                const navigationEntries
                        = window.performance
                        .getEntriesByType("navigation");

                if (navigationEntries.length > 0) {
                    return navigationEntries[0].type === "reload";
                }

                return window.performance.navigation
                        && window.performance.navigation.type === 1;
            }

            function hideAllMessagesOnReload() {
                if (!isPageReload()) {
                    return;
                }

                clearClientMessage();
                hideServerMessages();
            }

            /* ==========================================================
             QUANTITY
             ========================================================== */

            function getRoomQuantity() {
                const input
                        = document.getElementById("roomQuantity");

                const value
                        = Number.parseInt(input.value, 10);

                return Number.isNaN(value) ? 1 : value;
            }

            function getGuestQuantity() {
                const input
                        = document.getElementById("numGuests");

                const value
                        = Number.parseInt(input.value, 10);

                return Number.isNaN(value) ? 1 : value;
            }

            function updateCapacityText() {
                const rooms = getRoomQuantity();
                const maxGuests = rooms * capacityPerRoom;

                const maxGuestText
                        = document.getElementById("maxGuestText");

                const roomCountText
                        = document.getElementById("roomCountText");

                if (maxGuestText) {
                    maxGuestText.textContent
                            = String(maxGuests);
                }

                if (roomCountText) {
                    roomCountText.textContent
                            = String(rooms);
                }
            }

            function normalizeGuestQuantity(showMessage) {
                const guestInput
                        = document.getElementById("numGuests");

                const rooms = getRoomQuantity();
                let guests = getGuestQuantity();

                const maxGuests
                        = rooms * capacityPerRoom;

                if (guests < 1) {
                    guests = 1;
                }

                if (guests > maxGuests) {
                    guests = maxGuests;

                    if (showMessage) {
                        showClientMessage(
                                "error",
                                "Số khách tối đa cho "
                                + rooms
                                + " phòng là "
                                + maxGuests
                                + " khách. Hệ thống đã điều chỉnh lại."
                                );
                    }
                }

                guestInput.value = String(guests);
            }

            function changeGuests(value) {
                clearClientMessage();
                hideServerMessages();

                const guestInput
                        = document.getElementById("numGuests");

                let guests
                        = getGuestQuantity() + value;

                if (guests < 1) {
                    guests = 1;

                    showClientMessage(
                            "warning",
                            "Số khách tối thiểu là 1."
                            );
                }

                guestInput.value = String(guests);

                normalizeGuestQuantity(true);

                const rooms = getRoomQuantity();

                if (getGuestQuantity() < rooms) {
                    showClientMessage(
                            "warning",
                            "Số khách đang nhỏ hơn số phòng. "
                            + "Bạn vẫn có thể tiếp tục nếu đang đặt hộ người khác."
                            );
                }
            }

            function changeQuantity(value) {
                clearClientMessage();
                hideServerMessages();

                const roomInput
                        = document.getElementById("roomQuantity");

                let quantity
                        = getRoomQuantity() + value;

                if (quantity < 1) {
                    quantity = 1;

                    showClientMessage(
                            "warning",
                            "Số lượng phòng tối thiểu là 1."
                            );
                }

                if (hasValidDate
                        && availableRooms >= 0
                        && quantity > availableRooms) {

                    if (availableRooms > 0) {
                        quantity = availableRooms;

                        showClientMessage(
                                "error",
                                "Chỉ còn "
                                + availableRooms
                                + " phòng khả dụng trong thời gian đã chọn."
                                );

                    } else {
                        quantity = 1;

                        showClientMessage(
                                "error",
                                "Hạng phòng này không còn phòng "
                                + "trong thời gian đã chọn."
                                );
                    }
                }

                roomInput.value = String(quantity);

                updateCapacityText();
                normalizeGuestQuantity(true);
                updateTotalPrice();

                const guests = getGuestQuantity();

                if (guests < quantity) {
                    showClientMessage(
                            "warning",
                            "Số khách đang nhỏ hơn số phòng. "
                            + "Bạn vẫn có thể tiếp tục nếu đang đặt hộ người khác."
                            );
                }
            }

            /* ==========================================================
             PRICE
             ========================================================== */

            function updateTotalPrice() {
                const totalPriceElement
                        = document.getElementById("totalPrice");

                if (!totalPriceElement) {
                    return;
                }

                if (!hasValidDate) {
                    totalPriceElement.textContent = "--";
                    return;
                }

                const quantity = getRoomQuantity();

                const total
                        = basePrice
                        * quantity
                        * nights;

                totalPriceElement.textContent
                        = total.toLocaleString("vi-VN")
                        + " VND";
            }

            /* ==========================================================
             VALIDATION
             ========================================================== */

            function validateDates() {
                const checkInInput
                        = document.getElementById("checkInInput");

                const checkOutInput
                        = document.getElementById("checkOutInput");

                const checkInValue = checkInInput.value;
                const checkOutValue = checkOutInput.value;

                if (!checkInValue || !checkOutValue) {
                    showClientMessage(
                            "error",
                            "Vui lòng chọn đầy đủ ngày nhận phòng "
                            + "và ngày trả phòng."
                            );

                    return false;
                }

                const currentDate = new Date();
                currentDate.setHours(0, 0, 0, 0);

                const checkInDate
                        = new Date(checkInValue + "T00:00:00");

                const checkOutDate
                        = new Date(checkOutValue + "T00:00:00");

                if (checkInDate < currentDate) {
                    showClientMessage(
                            "error",
                            "Ngày nhận phòng không được nhỏ hơn ngày hiện tại."
                            );

                    return false;
                }

                if (checkOutDate <= checkInDate) {
                    showClientMessage(
                            "error",
                            "Ngày trả phòng phải sau ngày nhận phòng."
                            );

                    return false;
                }

                return true;
            }

            function validateGuestsAndRooms() {
                const guests = getGuestQuantity();
                const rooms = getRoomQuantity();

                const maxGuests
                        = rooms * capacityPerRoom;

                if (guests > maxGuests) {
                    showClientMessage(
                            "error",
                            "Số khách không được vượt quá "
                            + maxGuests
                            + " khách cho "
                            + rooms
                            + " phòng đã chọn."
                            );

                    return false;
                }

                /*
                 * Không chặn submit khi guests < rooms
                 * vì khách có thể đặt hộ.
                 */
                if (guests < rooms) {
                    showClientMessage(
                            "warning",
                            "Số khách đang nhỏ hơn số phòng. "
                            + "Hệ thống vẫn cho phép tiếp tục vì "
                            + "bạn có thể đang đặt hộ người khác."
                            );
                }

                return true;
            }

            /* ==========================================================
             INIT
             ========================================================== */

            document.addEventListener(
                    "DOMContentLoaded",
                    function () {

                        clearClientMessage();

                        /*
                         * F5 hoặc Ctrl + R:
                         * ẩn mọi thông báo server và client.
                         */
                        hideAllMessagesOnReload();

                        const bookingPanel
                                = document.getElementById("bookingPanel");

                        const checkInInput
                                = document.getElementById("checkInInput");

                        const checkOutInput
                                = document.getElementById("checkOutInput");

                        if (checkInInput) {
                            checkInInput.addEventListener(
                                    "input",
                                    function () {
                                        clearClientMessage();
                                        hideServerMessages();
                                    }
                            );
                        }

                        if (checkOutInput) {
                            checkOutInput.addEventListener(
                                    "input",
                                    function () {
                                        clearClientMessage();
                                        hideServerMessages();
                                    }
                            );
                        }

                        if (bookingPanel) {
                            bookingPanel.addEventListener(
                                    "submit",
                                    function (event) {

                                        clearClientMessage();
                                        hideServerMessages();

                                        if (!validateDates()) {
                                            event.preventDefault();
                                            return;
                                        }

                                        normalizeGuestQuantity(false);

                                        if (!validateGuestsAndRooms()) {
                                            event.preventDefault();
                                        }
                                    }
                            );
                        }

                        updateCapacityText();
                        updateTotalPrice();
                    }
            );
        </script>
    </body>
</html>