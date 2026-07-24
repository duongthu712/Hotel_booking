<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.RoomType"%>
<%@ page import="model.RoomAmenity"%>
<%@ page import="model.RoomTypeService"%>
<%@ page import="java.util.List"%>
<%@ page import="java.text.NumberFormat"%>
<%@ page import="java.util.Locale"%>

<%!
    public String html(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    public String js(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("'", "\\'")
                .replace("\n", "")
                .replace("\r", "");
    }

    public String money(Object value) {
        if (value == null) return "0";
        try {
            return NumberFormat.getInstance(new Locale("vi", "VN")).format(value);
        } catch (Exception e) {
            return value.toString();
        }
    }

    public String formatBedInfo(int bedCount, String bedType) {
        if (bedType == null || bedType.trim().isEmpty()) return bedCount + " giường";
        String value = bedType.trim();
        if (value.matches(".*\\d+.*") || value.contains("+")) return value;
        return bedCount + " x " + value;
    }

    public String serviceIcon(String name) {
        if (name == null) return "fa-circle-check";
        String lower = name.toLowerCase();
        if (lower.contains("nước") || lower.contains("lavie") || lower.contains("evian")) return "fa-bottle-water";
        if (lower.contains("trà") || lower.contains("cà phê") || lower.contains("coffee") || lower.contains("g7") || lower.contains("capsule") || lower.contains("nespresso")) return "fa-mug-saucer";
        if (lower.contains("snack") || lower.contains("bánh")) return "fa-cookie-bite";
        if (lower.contains("mì")) return "fa-bowl-food";
        if (lower.contains("sữa")) return "fa-glass-water";
        if (lower.contains("rượu") || lower.contains("vang") || lower.contains("champagne")) return "fa-wine-glass";
        if (lower.contains("trái cây")) return "fa-apple-whole";
        if (lower.contains("coca") || lower.contains("pepsi") || lower.contains("nước ngọt")) return "fa-glass-water";
        return "fa-circle-check";
    }

    public String amenityIcon(String name) {
        if (name == null) return "fa-circle-check";
        String lower = name.toLowerCase();
        if (lower.contains("khăn")) return "fa-bath";
        if (lower.contains("áo choàng")) return "fa-shirt";
        if (lower.contains("dép")) return "fa-shoe-prints";
        if (lower.contains("máy sấy")) return "fa-wind";
        if (lower.contains("ấm")) return "fa-mug-hot";
        if (lower.contains("gối") || lower.contains("chăn")) return "fa-bed";
        if (lower.contains("cốc")) return "fa-mug-saucer";
        if (lower.contains("móc")) return "fa-shirt";
        return "fa-circle-check";
    }
%>

<%
    RoomType room = (RoomType) request.getAttribute("room");
    String error = (String) request.getAttribute("error");
    String checkIn = (String) request.getAttribute("checkIn");
    String checkOut = (String) request.getAttribute("checkOut");
    String dateError = (String) request.getAttribute("dateError");
    String guestRoomWarning = (String) request.getAttribute("guestRoomWarning");
    String guestRoomError = (String) request.getAttribute("guestRoomError");
    String today = (String) request.getAttribute("today");
    String minCheckInDate = (String) request.getAttribute("minCheckInDate");
    String minCheckOutDate = (String) request.getAttribute("minCheckOutDate");

    if (checkIn == null) checkIn = "";
    if (checkOut == null) checkOut = "";
    if (dateError == null) dateError = "";
    if (guestRoomWarning == null) guestRoomWarning = "";
    if (guestRoomError == null) guestRoomError = "";
    if (today == null) today = "";
    if (minCheckInDate == null || minCheckInDate.trim().isEmpty()) minCheckInDate = today;
    if (minCheckOutDate == null || minCheckOutDate.trim().isEmpty()) minCheckOutDate = minCheckInDate;

    Integer numRoomsObj = (Integer) request.getAttribute("numRooms");
    Integer numGuestsObj = (Integer) request.getAttribute("numGuests");
    Integer numChildrenObj = (Integer) request.getAttribute("numChildren");
    Integer availableRoomsObj = (Integer) request.getAttribute("availableRooms");
    Integer maxGuestsByRoomsObj = (Integer) request.getAttribute("maxGuestsByRooms");
    Integer maxAdultsByRoomsObj = (Integer) request.getAttribute("maxAdultsByRooms");
    Integer maxChildrenByRoomsObj = (Integer) request.getAttribute("maxChildrenByRooms");
    Long nightsObj = (Long) request.getAttribute("nights");
    Boolean hasValidDateObj = (Boolean) request.getAttribute("hasValidDate");
    Boolean clearQueryAfterLoadObj = (Boolean) request.getAttribute("clearQueryAfterLoad");

    int numRooms = numRoomsObj == null ? 1 : numRoomsObj;
    int numGuests = numGuestsObj == null ? 1 : numGuestsObj;
    int numChildren = numChildrenObj == null ? 0 : numChildrenObj;
    int availableRooms = availableRoomsObj == null ? -1 : availableRoomsObj;
    int maxGuestsByRooms = maxGuestsByRoomsObj == null ? 1 : maxGuestsByRoomsObj;
    int maxAdultsByRooms = maxAdultsByRoomsObj == null ? 1 : maxAdultsByRoomsObj;
    int maxChildrenByRooms = maxChildrenByRoomsObj == null ? 0 : maxChildrenByRoomsObj;
    long nights = nightsObj == null ? 0 : nightsObj;
    boolean hasValidDate = hasValidDateObj != null && hasValidDateObj;
    boolean clearQueryAfterLoad = clearQueryAfterLoadObj != null && clearQueryAfterLoadObj;

    String pageTitle = room == null ? "Chi tiết phòng - La Mer Hotel" : room.getTypeName() + " - La Mer Hotel";
    List<String> images = room == null ? null : room.getImageUrl();
    String firstImage = "https://placehold.co/700x450?text=La+Mer+Room";

    if (images != null && !images.isEmpty() && images.get(0) != null && !images.get(0).trim().isEmpty()) {
        firstImage = images.get(0);
    }

    String basePrice = "0";
    int capacityPerRoom = 1;
    int adultsPerRoom = 1;
    int childrenPerRoom = 0;

    if (room != null) {
        capacityPerRoom = Math.max(room.getCapacity(), 1);
        adultsPerRoom = room.getNumGuests() > 0 ? room.getNumGuests() : capacityPerRoom;
        childrenPerRoom = Math.max(room.getNumChildren(), 0);

        if (room.getBasePrice() != null) {
            basePrice = room.getBasePrice().toPlainString();
        }
    }

    boolean canBook = hasValidDate
            && availableRooms > 0
            && dateError.isEmpty()
            && guestRoomError.isEmpty()
            && numRooms <= availableRooms;
%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title><%= html(pageTitle) %></title>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/navbar.css">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/footer.css">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/room-detail.css?v=70">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    </head>
    <body>
        <jsp:include page="/view/common/navbar.jsp" />

        <main class="room-detail-wrapper">
            <% if (error != null && !error.trim().isEmpty()) { %>
            <div class="room-error-box server-message">
                <i class="fa-solid fa-circle-exclamation"></i>
                <span><%= html(error) %></span>
            </div>
            <% } %>

            <% if (room != null) { %>
            <section class="room-top-grid">
                <div class="room-gallery-card">
                    <div class="main-image-box">
                        <% if (images != null && images.size() > 1) { %>
                        <button type="button" class="gallery-btn gallery-btn-left" onclick="moveImage(-1)" aria-label="Ảnh trước"></button>
                        <% } %>

                        <div class="image-slider-window">
                            <div class="image-slider-track" id="imageSliderTrack">
                                <% if (images != null && !images.isEmpty()) { %>
                                <% for (int index = 0; index < images.size(); index++) { %>
                                <img class="room-main-image"
                                     src="<%= html(images.get(index)) %>"
                                     alt="<%= html(room.getTypeName()) %>"
                                     onerror="this.onerror=null; this.src='https://placehold.co/700x450?text=La+Mer+Room';">
                                <% } %>
                                <% } else { %>
                                <img class="room-main-image" src="<%= html(firstImage) %>" alt="<%= html(room.getTypeName()) %>">
                                <% } %>
                            </div>
                        </div>

                        <% if (images != null && images.size() > 1) { %>
                        <div class="image-count"><span id="currentImageNumber">1</span> / <%= images.size() %></div>
                        <button type="button" class="gallery-btn gallery-btn-right" onclick="moveImage(1)" aria-label="Ảnh tiếp theo"></button>
                        <% } %>
                    </div>

                    <div class="room-thumbnail-row" id="thumbnailRow">
                        <% if (images != null && !images.isEmpty()) { %>
                        <% for (int index = 0; index < images.size(); index++) { %>
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

                <div class="room-side-panel">
                    <div class="room-summary-card">
                        <div class="room-title-row">
                            <div>
                                <h1><%= html(room.getTypeName()) %></h1>
                                <p><%= html(room.getDescription()) %></p>
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
                                    <strong><%= room.getNumGuests() %> người lớn, <%= room.getNumChildren() %> trẻ em</strong>
                                </div>
                            </div>

                            <div class="room-spec-card">
                                <i class="fa-solid fa-bed"></i>
                                <div><span>Giường</span><strong><%= html(formatBedInfo(room.getBedCount(), room.getBedType())) %></strong></div>
                            </div>

                            <div class="room-spec-card">
                                <i class="fa-solid fa-maximize"></i>
                                <div><span>Diện tích</span><strong><%= room.getAreaSqm() %> m²</strong></div>
                            </div>
                        </div>
                    </div>

                    <form method="get" class="booking-panel" id="bookingPanel">
                        <input type="hidden" name="roomTypeId" value="<%= room.getRoomTypeId() %>">
                        <h2>Kiểm tra phòng & đặt phòng</h2>

                        <div class="booking-date-grid">
                            <div class="booking-field booking-date-field">
                                <label for="checkInInput">Nhận phòng</label>
                                <div class="field-with-icon">
                                    <input type="date"
                                           id="checkInInput"
                                           name="checkIn"
                                           value="<%= html(checkIn) %>"
                                           min="<%= html(minCheckInDate) %>" required>
                                </div>
                            </div>

                            <div class="booking-field booking-date-field">
                                <label for="checkOutInput">Trả phòng</label>
                                <div class="field-with-icon">
                                    <input type="date"
                                           id="checkOutInput"
                                           name="checkOut"
                                           value="<%= html(checkOut) %>"
                                           min="<%= html(minCheckOutDate) %>" required>
                                </div>
                            </div>
                        </div>

                        <div class="booking-number-grid">
                            <div class="booking-field booking-quantity-field">
                                <label for="numGuests">Người lớn</label>
                                <input type="number"
                                       class="booking-number-input"
                                       id="numGuests"
                                       name="numGuests"
                                       value="<%= numGuests %>"
                                       min="1"
                                       step="1"
                                       inputmode="numeric" required>
                            </div>

                            <div class="booking-field booking-quantity-field">
                                <label for="numChildren">Trẻ em</label>
                                <input type="number"
                                       class="booking-number-input"
                                       id="numChildren"
                                       name="numChildren"
                                       value="<%= numChildren %>"
                                       min="0"
                                       step="1"
                                       inputmode="numeric" required>
                            </div>

                            <div class="booking-field booking-quantity-field">
                                <label for="roomQuantity">Số lượng phòng</label>
                                <input type="number"
                                       class="booking-number-input"
                                       id="roomQuantity"
                                       name="numRooms"
                                       value="<%= numRooms %>"
                                       min="1"
                                       step="1"
                                       inputmode="numeric" required>
                            </div>
                        </div>

                        <div class="capacity-note" id="capacityNote">
                            Với <strong id="roomCountText"><%= numRooms %></strong> phòng:
                            tối đa <strong id="maxAdultText"><%= maxAdultsByRooms %></strong> người lớn,
                            <strong id="maxChildText"><%= maxChildrenByRooms %></strong> trẻ em,
                            tổng cộng <strong id="maxGuestText"><%= maxGuestsByRooms %></strong> người.
                        </div>

                        <% if (!guestRoomError.isEmpty()) { %>
                        <div class="availability-box error server-message">
                            <i class="fa-solid fa-circle-exclamation"></i>
                            <span><%= html(guestRoomError) %></span>
                            <em>Chưa hợp lệ</em>
                        </div>
                        <% } else if (!guestRoomWarning.isEmpty()) { %>
                        <div class="availability-box warning server-message">
                            <i class="fa-solid fa-triangle-exclamation"></i>
                            <span><%= html(guestRoomWarning) %></span>
                            <em>Lưu ý</em>
                        </div>
                        <% } %>

                        <% if (!dateError.isEmpty()) { %>
                        <div class="availability-box error server-message">
                            <i class="fa-solid fa-circle-exclamation"></i>
                            <span><%= html(dateError) %></span>
                            <em>Chưa hợp lệ</em>
                        </div>
                        <% } else if (hasValidDate) { %>
                        <div class="availability-box success server-message">
                            <i class="fa-solid fa-circle-check"></i>
                            <span>Còn <strong><%= availableRooms %></strong> phòng khả dụng trong thời gian đã chọn</span>
                            <em><%= nights %> đêm</em>
                        </div>
                        <% } else { %>
                        <div class="availability-box neutral server-message">
                            <i class="fa-regular fa-calendar-days"></i>
                            <span>Vui lòng chọn ngày nhận phòng và trả phòng để kiểm tra phòng trống</span>
                            <em>Chưa kiểm tra</em>
                        </div>
                        <% } %>

                        <div class="booking-price-row">
                            <span>Tạm tính</span>
                            <strong id="totalPrice"><%= hasValidDate ? "0 VND" : "--" %></strong>
                        </div>

                        <p class="booking-tax-note">
                            <%= hasValidDate ? "Giá phòng theo số đêm và số phòng đã chọn" : "Tổng tiền sẽ hiển thị sau khi kiểm tra phòng" %>
                        </p>

                        <div class="booking-action-row">
                            <button type="submit"
                                    formaction="<%= request.getContextPath() %>/room-detail"
                                    class="check-availability-btn"
                                    data-action="check">
                                Kiểm tra phòng
                            </button>

                            <button type="submit"
                                    id="bookButton"
                                    formaction="<%= request.getContextPath() %>/booking-form"
                                    class="room-booking-btn"
                                    data-action="book"
                                    <%= canBook ? "" : "disabled" %>>
                                <i class="fa-solid fa-calendar-check"></i>
                                Đặt phòng ngay
                            </button>
                        </div>
                    </form>
                </div>
            </section>

            <section class="room-info-grid">
                <div class="room-info-card">
                    <h3><i class="fa-solid fa-circle-info"></i>Thông tin phòng</h3>
                    <ul>
                        <li><i class="fa-solid fa-check"></i><span><%= html(formatBedInfo(room.getBedCount(), room.getBedType())) %></span></li>
                        <li><i class="fa-solid fa-check"></i><span>Tối đa <%= room.getNumGuests() %> người lớn mỗi phòng</span></li>
                        <li><i class="fa-solid fa-check"></i><span>Tối đa <%= room.getNumChildren() %> trẻ em mỗi phòng</span></li>
                        <li><i class="fa-solid fa-check"></i><span>Tổng sức chứa tối đa <%= room.getCapacity() %> người mỗi phòng</span></li>
                        <li><i class="fa-solid fa-check"></i><span>Diện tích phòng <%= room.getAreaSqm() %> m²</span></li>
                        <li><i class="fa-solid fa-check"></i><span>Dọn phòng hằng ngày</span></li>
                        <li><i class="fa-solid fa-check"></i><span>Không gian nghỉ dưỡng hiện đại</span></li>
                    </ul>
                </div>

                <div class="room-info-card">
                    <h3><i class="fa-solid fa-mug-saucer"></i>Dịch vụ phòng</h3>
                    <ul>
                        <%
                            List<RoomTypeService> services = room.getRoomTypeServices();
                            boolean hasService = false;

                            if (services != null && !services.isEmpty()) {
                                for (RoomTypeService service : services) {
                                    String serviceName = "";

                                    if (service.getRoomService() != null) {
                                        serviceName = service.getRoomService().getServiceName();
                                    }

                                    if (serviceName == null || serviceName.trim().isEmpty()) {
                                        continue;
                                    }

                                    int totalQuantity = Math.max(service.getQuantity(), 0);
                                    int freeQuantity = Math.max(service.getIsFree(), 0);

                                    hasService = true;
                        %>

                        <li class="room-service-item">
                            <i class="fa-solid <%= serviceIcon(serviceName) %>"></i>

                            <div class="room-service-content">
                                <div class="room-service-main">
                                    <span><%= html(serviceName) %></span>
                                    <strong>x<%= totalQuantity %></strong>
                                </div>

                                <div class="room-service-status">
                                    <% if (freeQuantity > 0) { %>
                                    <span class="free-label">
                                        <i class="fa-solid fa-circle-check"></i>
                                        Miễn phí x<%= freeQuantity %>
                                    </span>
                                    <% } else { %>
                                    <span class="paid-label">
                                        <i class="fa-solid fa-coins"></i>
                                        Có tính phí
                                    </span>
                                    <% } %>
                                </div>
                            </div>
                        </li>

                        <%
                                }
                            }

                            if (!hasService) {
                        %>
                        <li>
                            <i class="fa-solid fa-circle-info"></i>
                            <span>Hạng phòng này chưa có dịch vụ phòng.</span>
                        </li>
                        <% } %>
                    </ul>
                </div>

                <div class="room-info-card wide">
                    <h3><i class="fa-regular fa-star"></i>Tiện nghi phòng</h3>
                    <div class="amenity-grid">
                        <%
                            List<RoomAmenity> amenities = room.getRoomAmenities();
                            if (amenities != null && !amenities.isEmpty()) {
                                for (RoomAmenity amenity : amenities) {
                        %>
                        <div class="amenity-item">
                            <i class="fa-solid <%= amenityIcon(amenity.getAmenityName()) %>"></i>
                            <span><%= html(amenity.getAmenityName()) %></span>
                        </div>
                        <%
                                }
                            } else {
                        %>
                        <div class="amenity-item">
                            <i class="fa-solid fa-circle-info"></i>
                            <span>Đang cập nhật tiện nghi</span>
                        </div>
                        <% } %>
                    </div>
                </div>
            </section>

            <section class="benefit-strip">
                <div class="benefit-item">
                    <i class="fa-solid fa-tag"></i>
                    <div><strong>Giá tốt nhất</strong><span>Cam kết giá tốt nhất khi đặt trực tiếp</span></div>
                </div>
                <div class="benefit-item">
                    <i class="fa-solid fa-shield-halved"></i>
                    <div><strong>Hủy miễn phí</strong><span>Chính sách hủy theo điều kiện đặt phòng</span></div>
                </div>
                <div class="benefit-item">
                    <i class="fa-solid fa-circle-check"></i>
                    <div><strong>Không phí đặt phòng</strong><span>Không tính phí đặt phòng online</span></div>
                </div>
                <div class="benefit-item">
                    <i class="fa-solid fa-headset"></i>
                    <div><strong>Hỗ trợ 24/7</strong><span>Đội ngũ hỗ trợ mọi lúc, mọi nơi</span></div>
                </div>
            </section>
            <% } %>
        </main>

        <jsp:include page="/view/common/footer.jsp" />

        <script>
            const IMAGE_MOVE_PERCENT = 100;
            const IMAGE_TRANSITION_DELAY_MS = 520;
            const MINIMUM_QUANTITY = 1;
            const INTEGER_RADIX = 10;
            const CHECKOUT_MIN_OFFSET_DAYS = 1;
            const DATE_PART_COUNT = 3;
            const MONTH_INDEX_OFFSET = 1;
            const DATE_PAD_LENGTH = 2;

            const imageList = [
            <% if (images != null && !images.isEmpty()) { %>
            <% for (int index = 0; index < images.size(); index++) { %>
            "<%= js(images.get(index)) %>"<%= index < images.size() - 1 ? "," : "" %>
            <% } %>
            <% } %>
            ];

            let currentImageIndex = 0;
            let isSliding = false;

            const basePrice = Number("<%= basePrice %>");
            const nights = Number("<%= nights %>");
            const hasValidDate = <%= hasValidDate ? "true" : "false" %>;
            const capacityPerRoom = Number("<%= capacityPerRoom %>");
            const adultsPerRoom = Number("<%= adultsPerRoom %>");
            const childrenPerRoom = Number("<%= childrenPerRoom %>");
            const minCheckInDate = "<%= js(minCheckInDate) %>";
            const defaultMinCheckOutDate = "<%= js(minCheckOutDate) %>";
            const clearQueryAfterLoad = <%= clearQueryAfterLoad ? "true" : "false" %>;
            const cleanRoomDetailUrl = "<%= request.getContextPath() %>/room-detail?roomTypeId=<%= room == null ? "" : room.getRoomTypeId() %>";

            function updateSliderPosition() {
                const track = document.getElementById("imageSliderTrack");
                if (!track) return;

                track.style.transform = "translateX(-" + (currentImageIndex * IMAGE_MOVE_PERCENT) + "%)";
                updateActiveThumbnail();
                updateImageCount();
                scrollThumbnailIntoView();
            }

            function moveImage(step) {
                if (!imageList || imageList.length <= MINIMUM_QUANTITY || isSliding) return;

                isSliding = true;
                currentImageIndex += step;

                if (currentImageIndex < 0) currentImageIndex = imageList.length - MINIMUM_QUANTITY;
                if (currentImageIndex >= imageList.length) currentImageIndex = 0;

                updateSliderPosition();

                window.setTimeout(function () {
                    isSliding = false;
                }, IMAGE_TRANSITION_DELAY_MS);
            }

            function goToImage(index) {
                if (!imageList || imageList.length === 0 || isSliding) return;
                if (index < 0 || index >= imageList.length || index === currentImageIndex) return;

                isSliding = true;
                currentImageIndex = index;
                updateSliderPosition();

                window.setTimeout(function () {
                    isSliding = false;
                }, IMAGE_TRANSITION_DELAY_MS);
            }

            function updateActiveThumbnail() {
                document.querySelectorAll(".room-thumbnail").forEach(function (thumbnail) {
                    thumbnail.classList.remove("active-thumb");
                });

                const activeThumbnail = document.querySelector(".room-thumbnail[data-index='" + currentImageIndex + "']");
                if (activeThumbnail) activeThumbnail.classList.add("active-thumb");
            }

            function updateImageCount() {
                const counter = document.getElementById("currentImageNumber");
                if (counter) counter.textContent = String(currentImageIndex + MINIMUM_QUANTITY);
            }

            function scrollThumbnailIntoView() {
                const activeThumbnail = document.querySelector(".room-thumbnail[data-index='" + currentImageIndex + "']");

                if (activeThumbnail) {
                    activeThumbnail.scrollIntoView({
                        behavior: "smooth",
                        block: "nearest",
                        inline: "center"
                    });
                }
            }

            function readInteger(inputId, defaultValue) {
                const input = document.getElementById(inputId);
                if (!input) return defaultValue;

                const value = Number.parseInt(input.value, INTEGER_RADIX);
                return Number.isNaN(value) ? defaultValue : value;
            }

            function updateCapacityText() {
                const rooms = Math.max(readInteger("roomQuantity", MINIMUM_QUANTITY), MINIMUM_QUANTITY);
                const roomCountText = document.getElementById("roomCountText");
                const maxAdultText = document.getElementById("maxAdultText");
                const maxChildText = document.getElementById("maxChildText");
                const maxGuestText = document.getElementById("maxGuestText");

                if (roomCountText) roomCountText.textContent = String(rooms);
                if (maxAdultText) maxAdultText.textContent = String(rooms * adultsPerRoom);
                if (maxChildText) maxChildText.textContent = String(rooms * childrenPerRoom);
                if (maxGuestText) maxGuestText.textContent = String(rooms * capacityPerRoom);
            }

            function updateTotalPrice() {
                const totalPriceElement = document.getElementById("totalPrice");
                if (!totalPriceElement) return;

                if (!hasValidDate) {
                    totalPriceElement.textContent = "--";
                    return;
                }

                const rooms = Math.max(readInteger("roomQuantity", MINIMUM_QUANTITY), MINIMUM_QUANTITY);
                const total = basePrice * rooms * nights;
                totalPriceElement.textContent = total.toLocaleString("vi-VN") + " VND";
            }

            function hideServerMessages() {
                document.querySelectorAll(".server-message").forEach(function (message) {
                    message.style.display = "none";
                });
            }

            function markFormChanged() {
                hideServerMessages();

                const bookButton = document.getElementById("bookButton");
                if (bookButton) bookButton.disabled = true;

                updateCapacityText();
                updateTotalPrice();
            }

            function addDays(dateValue, numberOfDays) {
                if (!dateValue) return "";

                const dateParts = dateValue.split("-");
                if (dateParts.length !== DATE_PART_COUNT) return "";

                const year = Number(dateParts[0]);
                const month = Number(dateParts[1]) - MONTH_INDEX_OFFSET;
                const day = Number(dateParts[2]);
                const date = new Date(year, month, day);

                date.setDate(date.getDate() + numberOfDays);

                const resultYear = date.getFullYear();
                const resultMonth = String(date.getMonth() + MONTH_INDEX_OFFSET).padStart(DATE_PAD_LENGTH, "0");
                const resultDay = String(date.getDate()).padStart(DATE_PAD_LENGTH, "0");

                return resultYear + "-" + resultMonth + "-" + resultDay;
            }

            function updateCheckOutMinimum() {
                const checkInInput = document.getElementById("checkInInput");
                const checkOutInput = document.getElementById("checkOutInput");

                if (!checkInInput || !checkOutInput) return;

                const minimumCheckOut = checkInInput.value
                        ? addDays(checkInInput.value, CHECKOUT_MIN_OFFSET_DAYS)
                        : defaultMinCheckOutDate;

                checkOutInput.min = minimumCheckOut;

                if (checkOutInput.value && checkOutInput.value < minimumCheckOut) {
                    checkOutInput.value = "";
                }
            }

            function cleanResultUrl() {
                if (!clearQueryAfterLoad || !window.history.replaceState) return;
                window.history.replaceState({}, document.title, cleanRoomDetailUrl);
            }

            document.addEventListener("DOMContentLoaded", function () {
                const checkInInput = document.getElementById("checkInInput");
                const formInputs = [
                    checkInInput,
                    document.getElementById("checkOutInput"),
                    document.getElementById("numGuests"),
                    document.getElementById("numChildren"),
                    document.getElementById("roomQuantity")
                ];

                formInputs.forEach(function (input) {
                    if (!input) return;

                    input.addEventListener("input", markFormChanged);
                    input.addEventListener("change", markFormChanged);
                });

                if (checkInInput) {
                    checkInInput.min = minCheckInDate;
                    checkInInput.addEventListener("change", updateCheckOutMinimum);
                }

                updateCheckOutMinimum();
                updateCapacityText();
                updateTotalPrice();
                cleanResultUrl();
            });
        </script>
    </body>
</html>