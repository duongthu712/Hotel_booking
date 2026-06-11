<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.RoomType"%>
<%@ page import="model.RoomAmenity"%>
<%@ page import="model.RoomTypeService"%>
<%@ page import="java.util.List"%>
<%@ page import="java.text.NumberFormat"%>
<%@ page import="java.util.Locale"%>

<%!
    public String html(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    public String js(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\")
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
            NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
            return nf.format(value);
        } catch (Exception e) {
            return value.toString();
        }
    }

    public String serviceIcon(String name) {
        if (name == null) {
            return "fa-circle-check";
        }

        String lower = name.toLowerCase();

        if (lower.contains("nước")) {
            return "fa-bottle-water";
        }

        if (lower.contains("trà") || lower.contains("cà phê")
                || lower.contains("coffee") || lower.contains("g7")) {
            return "fa-mug-saucer";
        }

        if (lower.contains("snack") || lower.contains("bánh") || lower.contains("mì")) {
            return "fa-circle-check";
        }

        if (lower.contains("spa") || lower.contains("massage")) {
            return "fa-spa";
        }

        if (lower.contains("xe")) {
            return "fa-car";
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

        if (lower.contains("gối")) {
            return "fa-bed";
        }

        if (lower.contains("chăn")) {
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
    RoomType room = (RoomType) request.getAttribute("room");
    String error = (String) request.getAttribute("error");

    String checkIn = (String) request.getAttribute("checkIn");
    String checkOut = (String) request.getAttribute("checkOut");
    String dateError = (String) request.getAttribute("dateError");
    String guestRoomWarning = (String) request.getAttribute("guestRoomWarning");
    String guestRoomError = (String) request.getAttribute("guestRoomError");
    String today = (String) request.getAttribute("today");

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

    Integer numRoomsObj = (Integer) request.getAttribute("numRooms");
    Integer numGuestsObj = (Integer) request.getAttribute("numGuests");
    Integer availableRoomsObj = (Integer) request.getAttribute("availableRooms");
    Integer maxGuestsByRoomsObj = (Integer) request.getAttribute("maxGuestsByRooms");
    Long nightsObj = (Long) request.getAttribute("nights");
    Boolean hasValidDateObj = (Boolean) request.getAttribute("hasValidDate");

    int numRooms = numRoomsObj == null ? 1 : numRoomsObj;
    int numGuests = numGuestsObj == null ? 1 : numGuestsObj;
    int availableRooms = availableRoomsObj == null ? -1 : availableRoomsObj;
    int maxGuestsByRooms = maxGuestsByRoomsObj == null ? 1 : maxGuestsByRoomsObj;
    long nights = nightsObj == null ? 0 : nightsObj;
    boolean hasValidDate = hasValidDateObj != null && hasValidDateObj;

    String pageTitle = "Chi tiết phòng - La Mer Hotel";
    if (room != null) {
        pageTitle = room.getTypeName() + " - La Mer Hotel";
    }

    List<String> images = null;
    if (room != null) {
        images = room.getImageUrl();
    }

    String firstImage = "https://placehold.co/700x450?text=La+Mer+Room";
    if (images != null && !images.isEmpty() && images.get(0) != null) {
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
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title><%= html(pageTitle) %></title>

        <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/navbar.css">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/footer.css">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/room-detail.css?v=40">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    </head>

    <body>
        <jsp:include page="/view/common/navbar.jsp" />

        <main class="room-detail-wrapper">

            <% if (error != null && !error.trim().isEmpty()) { %>
            <div class="room-error-box">
                <%= html(error) %>
            </div>
            <% } %>

            <% if (room != null) { %>

            <div class="room-breadcrumb">
                <a href="<%= request.getContextPath() %>/home"><i class="fa-solid fa-house"></i></a>
                <span>›</span>
                <a href="<%= request.getContextPath() %>/home">Trang chủ</a>
                <span>›</span>
                <a href="<%= request.getContextPath() %>/search">Phòng nghỉ</a>
                <span>›</span>
                <span><%= html(room.getTypeName()) %></span>
            </div>

            <section class="room-top-grid">

                <div class="room-gallery-card">
                    <div class="main-image-box">
                        <button type="button"
                                class="gallery-btn gallery-btn-left"
                                onclick="moveImage(-1)"
                                aria-label="Ảnh trước"></button>

                        <div class="image-slider-window">
                            <div class="image-slider-track" id="imageSliderTrack">
                                <% if (images != null && !images.isEmpty()) { %>
                                <% for (int i = 0; i < images.size(); i++) { %>
                                <img class="room-main-image"
                                     src="<%= html(images.get(i)) %>"
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
                            <span id="currentImageNumber">1</span> / <%= images.size() %>
                        </div>
                        <% } %>

                        <button type="button"
                                class="gallery-btn gallery-btn-right"
                                onclick="moveImage(1)"
                                aria-label="Ảnh tiếp theo"></button>
                    </div>

                    <div class="room-thumbnail-row" id="thumbnailRow">
                        <% if (images != null && !images.isEmpty()) { %>
                        <% for (int i = 0; i < images.size(); i++) { %>
                        <img class="room-thumbnail <%= i == 0 ? "active-thumb" : "" %>"
                             src="<%= html(images.get(i)) %>"
                             alt="<%= html(room.getTypeName()) %>"
                             data-index="<%= i %>"
                             onclick="goToImage(<%= i %>)"
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
                                    <strong><%= room.getCapacity() %> khách / phòng</strong>
                                </div>
                            </div>

                            <div class="room-spec-card">
                                <i class="fa-solid fa-bed"></i>
                                <div>
                                    <span>Giường</span>
                                    <strong><%= room.getBedCount() %> x <%= html(room.getBedType()) %></strong>
                                </div>
                            </div>

                            <div class="room-spec-card">
                                <i class="fa-solid fa-maximize"></i>
                                <div>
                                    <span>Diện tích</span>
                                    <strong><%= room.getAreaSqm() %> m²</strong>
                                </div>
                            </div>
                        </div>
                    </div>

                    <form method="get" class="booking-panel" id="bookingPanel">
                        <input type="hidden" name="roomTypeId" value="<%= room.getRoomTypeId() %>">

                        <h2>Kiểm tra phòng & đặt phòng</h2>

                        <div class="booking-input-grid">
                            <div class="booking-field">
                                <label>Nhận phòng</label>
                                <div class="field-with-icon">
                                    <input type="date"
                                           name="checkIn"
                                           value="<%= html(checkIn) %>"
                                           min="<%= html(today) %>"
                                           required>
                                </div>
                            </div>

                            <div class="booking-field">
                                <label>Trả phòng</label>
                                <div class="field-with-icon">
                                    <input type="date"
                                           name="checkOut"
                                           value="<%= html(checkOut) %>"
                                           min="<%= html(today) %>"
                                           required>
                                </div>
                            </div>

                            <div class="booking-field">
                                <label>Số khách lưu trú</label>
                                <div class="quantity-box guest-quantity-box">
                                    <button type="button" onclick="changeGuests(-1)">−</button>
                                    <input type="text" id="numGuests" name="numGuests" value="<%= numGuests %>" readonly>
                                    <button type="button" onclick="changeGuests(1)">+</button>
                                </div>
                            </div>

                            <div class="booking-field">
                                <label>Số lượng phòng</label>
                                <div class="quantity-box">
                                    <button type="button" onclick="changeQuantity(-1)">−</button>
                                    <input type="text" id="roomQuantity" name="numRooms" value="<%= numRooms %>" readonly>
                                    <button type="button" onclick="changeQuantity(1)">+</button>
                                </div>
                            </div>
                        </div>

                        <div class="capacity-note" id="capacityNote">
                            Tối đa <strong id="maxGuestText"><%= maxGuestsByRooms %></strong> khách cho
                            <strong id="roomCountText"><%= numRooms %></strong> phòng đã chọn.
                        </div>

                        <% if (!guestRoomError.isEmpty()) { %>
                        <div class="availability-box error compact-message">
                            <i class="fa-solid fa-circle-exclamation"></i>
                            <span><%= html(guestRoomError) %></span>
                            <em>Đã chỉnh</em>
                        </div>
                        <% } else if (!guestRoomWarning.isEmpty()) { %>
                        <div class="availability-box warning compact-message">
                            <i class="fa-solid fa-triangle-exclamation"></i>
                            <span><%= html(guestRoomWarning) %></span>
                            <em>Lưu ý</em>
                        </div>
                        <% } %>

                        <% if (!dateError.isEmpty()) { %>
                        <div class="availability-box error">
                            <i class="fa-solid fa-circle-exclamation"></i>
                            <span><%= html(dateError) %></span>
                            <em>Chưa hợp lệ</em>
                        </div>
                        <% } else if (hasValidDate) { %>
                        <div class="availability-box success">
                            <i class="fa-solid fa-circle-check"></i>
                            <span>Còn <strong><%= availableRooms %></strong> phòng khả dụng trong thời gian đã chọn</span>
                            <em><%= nights %> đêm</em>
                        </div>
                        <% } else { %>
                        <div class="availability-box neutral">
                            <i class="fa-regular fa-calendar-days"></i>
                            <span>Vui lòng chọn ngày nhận phòng và trả phòng để kiểm tra phòng trống</span>
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
                            Đã bao gồm VAT và phí dịch vụ
                            <% } else { %>
                            Tổng tiền sẽ hiển thị sau khi kiểm tra phòng
                            <% } %>
                        </p>

                        <div class="booking-action-row">
                            <button type="submit"
                                    formaction="<%= request.getContextPath() %>/room-detail"
                                    class="check-availability-btn">
                                Kiểm tra phòng
                            </button>

                            <button type="submit"
                                    formaction="<%= request.getContextPath() %>/booking-form"
                                    class="room-booking-btn"
                                    <%= (!hasValidDate || availableRooms <= 0 || !dateError.isEmpty()) ? "disabled" : "" %>>
                                <i class="fa-solid fa-calendar-check"></i>
                                Đặt phòng ngay
                            </button>
                        </div>

                        <% if (hasValidDate && availableRooms <= 0) { %>
                        <p class="room-booking-warning">
                            Hạng phòng này đã hết phòng trong thời gian bạn chọn.
                        </p>
                        <% } %>
                    </form>
                </div>
            </section>

            <section class="room-info-grid">

                <div class="room-info-card">
                    <h3>
                        <i class="fa-solid fa-circle-info"></i>
                        Thông tin phòng
                    </h3>

                    <ul>
                        <li>
                            <i class="fa-solid fa-check"></i>
                            <span>Giường <%= html(room.getBedType()) %> êm ái</span>
                        </li>

                        <li>
                            <i class="fa-solid fa-check"></i>
                            <span>Phòng phù hợp tối đa <%= room.getCapacity() %> khách</span>
                        </li>

                        <li>
                            <i class="fa-solid fa-check"></i>
                            <span>Diện tích phòng <%= room.getAreaSqm() %> m²</span>
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

                <div class="room-info-card">
                    <h3>
                        <i class="fa-solid fa-mug-saucer"></i>
                        Dịch vụ phòng
                    </h3>

                    <ul>
                        <%
                            List<RoomTypeService> services = room.getRoomTypeServices();
                            if (services != null && !services.isEmpty()) {
                                for (RoomTypeService svc : services) {
                                    String serviceName = "";
                                    String unitPrice = "0";

                                    if (svc.getRoomService() != null) {
                                        serviceName = svc.getRoomService().getServiceName();
                                        unitPrice = money(svc.getRoomService().getUnitPrice());
                                    }
                        %>
                        <li>
                            <i class="fa-solid <%= serviceIcon(serviceName) %>"></i>
                            <span><%= html(serviceName) %></span>
                            <strong>x<%= svc.getQuantity() %></strong>

                            <% if (svc.isIsFree()) { %>
                            <em>Miễn phí</em>
                            <% } else { %>
                            <em><%= unitPrice %> VND</em>
                            <% } %>
                        </li>
                        <%
                                }
                            } else {
                        %>
                        <li>
                            <i class="fa-solid fa-circle-info"></i>
                            <span>Đang cập nhật dịch vụ phòng</span>
                        </li>
                        <%
                            }
                        %>
                    </ul>
                </div>

                <div class="room-info-card wide">
                    <h3>
                        <i class="fa-regular fa-star"></i>
                        Tiện nghi phòng
                    </h3>

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
                        <%
                            }
                        %>
                    </div>
                </div>
            </section>

            <section class="benefit-strip">
                <div class="benefit-item">
                    <i class="fa-solid fa-tag"></i>
                    <div>
                        <strong>Giá tốt nhất</strong>
                        <span>Cam kết giá tốt nhất khi đặt trực tiếp</span>
                    </div>
                </div>

                <div class="benefit-item">
                    <i class="fa-solid fa-shield-halved"></i>
                    <div>
                        <strong>Hủy miễn phí</strong>
                        <span>Chính sách hủy theo điều kiện đặt phòng</span>
                    </div>
                </div>

                <div class="benefit-item">
                    <i class="fa-solid fa-circle-check"></i>
                    <div>
                        <strong>Không phí đặt phòng</strong>
                        <span>Không tính phí đặt phòng online</span>
                    </div>
                </div>

                <div class="benefit-item">
                    <i class="fa-solid fa-headset"></i>
                    <div>
                        <strong>Hỗ trợ 24/7</strong>
                        <span>Đội ngũ hỗ trợ mọi lúc, mọi nơi</span>
                    </div>
                </div>
            </section>

            <% } %>
        </main>

        <jsp:include page="/view/common/footer.jsp" />

        <script>
            const imageList = [
            <% if (images != null && !images.isEmpty()) { %>
            <% for (int i = 0; i < images.size(); i++) { %>
            "<%= js(images.get(i)) %>"<%= i < images.size() - 1 ? "," : "" %>
            <% } %>
            <% } %>
            ];

            let currentImageIndex = 0;
            let isSliding = false;

            const basePrice = Number("<%= basePrice %>");
            const nights = Number("<%= nights %>");
            const availableRooms = Number("<%= availableRooms %>");
            const hasValidDate = <%= hasValidDate ? "true" : "false" %>;
            const capacityPerRoom = Number("<%= capacityPerRoom %>");

            function updateSliderPosition() {
                const track = document.getElementById("imageSliderTrack");

                if (!track) {
                    return;
                }

                track.style.transform = "translateX(-" + (currentImageIndex * 100) + "%)";
                updateActiveThumbnail();
                updateImageCount();
                scrollThumbnailIntoView();
            }

            function moveImage(step) {
                if (!imageList || imageList.length <= 1 || isSliding) {
                    return;
                }

                isSliding = true;
                currentImageIndex += step;

                if (currentImageIndex < 0) {
                    currentImageIndex = imageList.length - 1;
                }

                if (currentImageIndex >= imageList.length) {
                    currentImageIndex = 0;
                }

                updateSliderPosition();

                setTimeout(function () {
                    isSliding = false;
                }, 520);
            }

            function goToImage(index) {
                if (!imageList || imageList.length === 0 || isSliding) {
                    return;
                }

                if (index < 0 || index >= imageList.length || index === currentImageIndex) {
                    return;
                }

                isSliding = true;
                currentImageIndex = index;
                updateSliderPosition();

                setTimeout(function () {
                    isSliding = false;
                }, 520);
            }

            function updateActiveThumbnail() {
                const thumbnails = document.querySelectorAll(".room-thumbnail");

                thumbnails.forEach(function (thumb) {
                    thumb.classList.remove("active-thumb");
                });

                const activeThumb = document.querySelector(".room-thumbnail[data-index='" + currentImageIndex + "']");

                if (activeThumb) {
                    activeThumb.classList.add("active-thumb");
                }
            }

            function updateImageCount() {
                const currentImageNumber = document.getElementById("currentImageNumber");

                if (currentImageNumber) {
                    currentImageNumber.innerText = currentImageIndex + 1;
                }
            }

            function scrollThumbnailIntoView() {
                const activeThumb = document.querySelector(".room-thumbnail[data-index='" + currentImageIndex + "']");

                if (activeThumb) {
                    activeThumb.scrollIntoView({
                        behavior: "smooth",
                        block: "nearest",
                        inline: "center"
                    });
                }
            }

            function getRoomQuantity() {
                const roomInput = document.getElementById("roomQuantity");
                return parseInt(roomInput.value);
            }

            function getGuestQuantity() {
                const guestInput = document.getElementById("numGuests");
                return parseInt(guestInput.value);
            }

            function updateCapacityText() {
                const rooms = getRoomQuantity();
                const maxGuests = rooms * capacityPerRoom;

                const maxGuestText = document.getElementById("maxGuestText");
                const roomCountText = document.getElementById("roomCountText");

                if (maxGuestText) {
                    maxGuestText.innerText = maxGuests;
                }

                if (roomCountText) {
                    roomCountText.innerText = rooms;
                }
            }

            function normalizeGuestQuantity(showAlert) {
                const guestInput = document.getElementById("numGuests");
                const rooms = getRoomQuantity();
                let guests = getGuestQuantity();

                const maxGuests = rooms * capacityPerRoom;

                if (guests < 1) {
                    guests = 1;
                }

                if (guests > maxGuests) {
                    guests = maxGuests;

                    if (showAlert) {
                        alert("Số khách không được vượt quá " + maxGuests + " khách cho " + rooms + " phòng đã chọn.");
                    }
                }

                guestInput.value = guests;
            }

            function changeGuests(value) {
                const guestInput = document.getElementById("numGuests");
                let guests = parseInt(guestInput.value);

                guests += value;

                if (guests < 1) {
                    guests = 1;
                }

                guestInput.value = guests;
                normalizeGuestQuantity(true);
            }

            function changeQuantity(value) {
                const quantityInput = document.getElementById("roomQuantity");
                let quantity = parseInt(quantityInput.value);

                quantity += value;

                if (quantity < 1) {
                    quantity = 1;
                }

                if (hasValidDate && availableRooms > 0 && quantity > availableRooms) {
                    quantity = availableRooms;
                    alert("Chỉ còn " + availableRooms + " phòng khả dụng trong thời gian bạn chọn.");
                }

                if (hasValidDate && availableRooms <= 0) {
                    quantity = 1;
                }

                quantityInput.value = quantity;

                updateCapacityText();
                normalizeGuestQuantity(true);
                updateTotalPrice();
            }

            function updateTotalPrice() {
                const totalPriceElement = document.getElementById("totalPrice");

                if (!hasValidDate) {
                    totalPriceElement.innerText = "--";
                    return;
                }

                const quantity = getRoomQuantity();
                const total = basePrice * quantity * nights;

                totalPriceElement.innerText =
                        total.toLocaleString("vi-VN") + " VND";
            }

            document.addEventListener("DOMContentLoaded", function () {
                const checkInInput = document.querySelector("input[name='checkIn']");
                const checkOutInput = document.querySelector("input[name='checkOut']");
                const bookingPanel = document.getElementById("bookingPanel");

                function validateDates() {
                    if (!checkInInput || !checkOutInput) {
                        return true;
                    }

                    const today = new Date();
                    today.setHours(0, 0, 0, 0);

                    const checkInValue = checkInInput.value;
                    const checkOutValue = checkOutInput.value;

                    if (!checkInValue || !checkOutValue) {
                        alert("Vui lòng chọn đầy đủ ngày nhận phòng và ngày trả phòng.");
                        return false;
                    }

                    const checkInDate = new Date(checkInValue);
                    const checkOutDate = new Date(checkOutValue);

                    if (checkInDate < today) {
                        alert("Ngày nhận phòng không được nhỏ hơn ngày hiện tại.");
                        return false;
                    }

                    if (checkOutDate <= checkInDate) {
                        alert("Ngày trả phòng phải sau ngày nhận phòng.");
                        return false;
                    }

                    return true;
                }

                function validateGuestAndRoom() {
                    const guests = getGuestQuantity();
                    const rooms = getRoomQuantity();
                    const maxGuests = rooms * capacityPerRoom;

                    if (guests > maxGuests) {
                        alert("Số khách không được vượt quá " + maxGuests + " khách cho " + rooms + " phòng đã chọn.");
                        return false;
                    }

                    if (guests < rooms) {
                        return confirm("Số khách đang nhỏ hơn số phòng đặt. Nếu bạn đang đặt hộ người khác, bạn có thể tiếp tục. Bạn có muốn tiếp tục không?");
                    }

                    return true;
                }

                if (bookingPanel) {
                    bookingPanel.addEventListener("submit", function (event) {
                        if (!validateDates()) {
                            event.preventDefault();
                            return;
                        }

                        normalizeGuestQuantity(false);

                        if (!validateGuestAndRoom()) {
                            event.preventDefault();
                        }
                    });
                }

                updateCapacityText();
                normalizeGuestQuantity(false);
                updateTotalPrice();
            });
        </script>
    </body>
</html>