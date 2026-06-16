<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="model.Booking" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<%
    Booking bookingData = (Booking) request.getAttribute("booking");
    long numberOfNights = 0;
    BigDecimal totalAmount = BigDecimal.ZERO;
    String checkInText = "";
    String checkOutText = "";

    if (bookingData != null) {
        numberOfNights = bookingData.getCheckoutDate().toEpochDay()
                - bookingData.getCheckinDate().toEpochDay();

        totalAmount = bookingData.getBookedPricePerNight()
                .multiply(BigDecimal.valueOf(bookingData.getNumRooms()))
                .multiply(BigDecimal.valueOf(numberOfNights));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        checkInText = bookingData.getCheckinDate().format(formatter);
        checkOutText = bookingData.getCheckoutDate().format(formatter);
    }

    request.setAttribute("numberOfNights", numberOfNights);
    request.setAttribute("totalAmount", totalAmount);
    request.setAttribute("checkInText", checkInText);
    request.setAttribute("checkOutText", checkOutText);
%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Gửi minh chứng đặt cọc - La Mer Hotel</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/navbar.css?v=<%= System.currentTimeMillis() %>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/footer.css?v=<%= System.currentTimeMillis() %>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/booking.css?v=<%= System.currentTimeMillis() %>">
    </head>

    <body>
        <jsp:include page="/view/common/navbar.jsp"/>

        <main class="booking-page">
            <div class="booking-progress">
                <div class="progress-item completed">
                    <div class="progress-number">1</div>
                    <div class="progress-name">THÔNG TIN ĐẶT PHÒNG</div>
                </div>

                <div class="progress-line completed"></div>

                <div class="progress-item active">
                    <div class="progress-number">2</div>
                    <div class="progress-name">GỬI MINH CHỨNG ĐẶT CỌC</div>
                </div>

                <div class="progress-line"></div>

                <div class="progress-item disabled">
                    <div class="progress-number">3</div>
                    <div class="progress-name">HOÀN TẤT ĐẶT PHÒNG</div>
                </div>
            </div>

            <c:if test="${not empty booking}">
                <c:choose>
                    <c:when test="${hasPayment}">
                        <div class="hold-time-box">
                            <div>
                                <strong>Đã gửi minh chứng đặt cọc</strong>
                                <span>Minh chứng đang chờ lễ tân kiểm tra và xác nhận</span>
                            </div>
                            <div>ĐÃ GỬI</div>
                        </div>
                    </c:when>

                    <c:when test="${booking.status eq 'Đã hủy' or remainingSeconds <= 0}">
                        <div class="hold-time-box expired">
                            <div>
                                <strong>Đã hết thời gian giữ phòng</strong>
                                <span>Đơn không còn đủ điều kiện gửi minh chứng thanh toán</span>
                            </div>
                            <div id="countdown">HẾT THỜI GIAN</div>
                        </div>
                    </c:when>

                    <c:otherwise>
                        <div class="hold-time-box">
                            <div>
                                <strong>Thời gian giữ phòng còn lại</strong>
                                <span>Hoàn tất gửi minh chứng trước khi thời gian kết thúc</span>
                            </div>
                            <div id="countdown" data-seconds="${remainingSeconds}">00:00</div>
                        </div>
                    </c:otherwise>
                </c:choose>

                <div class="payment-layout">
                    <section class="payment-main-card" id="paymentSection">
                        <div class="payment-title">
                            <div class="payment-title-icon">⇧</div>

                            <div>
                                <h1>GỬI MINH CHỨNG ĐẶT CỌC</h1>
                                <p>Sau khi chuyển khoản, vui lòng tải ảnh biên lai hoặc ảnh chụp màn hình giao dịch.</p>
                            </div>
                        </div>

                        <div id="paymentMessageArea">
                            <c:if test="${not empty error}">
                                <div class="booking-error" id="serverPaymentError">
                                    <strong>Thông báo:</strong>
                                    <c:out value="${error}"/>
                                </div>
                            </c:if>

                            <div id="pageMessage" class="payment-information-note"
                                 style="display:none; margin:0 0 20px 0;"></div>
                        </div>

                        <div class="payment-warning">
                            Vui lòng chuyển khoản đúng số tiền đặt cọc và ghi chính xác mã đặt phòng trong nội dung chuyển khoản.
                        </div>

                        <div class="bank-card">
                            <div class="bank-information">
                                <h2>THÔNG TIN CHUYỂN KHOẢN</h2>

                                <div class="bank-row">
                                    <span>Ngân hàng:</span>
                                    <strong>Vietcombank</strong>
                                </div>

                                <div class="bank-row">
                                    <span>Số tài khoản:</span>
                                    <strong>123456789999</strong>
                                </div>

                                <div class="bank-row">
                                    <span>Chủ tài khoản:</span>
                                    <strong>LA MER HOTEL</strong>
                                </div>

                                <div class="bank-row">
                                    <span>Số tiền đặt cọc:</span>
                                    <strong class="deposit-money">
                                        <fmt:formatNumber value="${booking.depositAmount}" type="number" maxFractionDigits="0"/>
                                        VND
                                    </strong>
                                </div>

                                <div class="bank-row">
                                    <span>Nội dung chuyển khoản:</span>
                                    <strong><c:out value="${booking.bookingCode}"/></strong>
                                </div>

                                <div class="booking-code-note">
                                    Mã đặt phòng:
                                    <strong><c:out value="${booking.bookingCode}"/></strong>.
                                    Vui lòng lưu mã này để tra cứu đơn đặt phòng.
                                </div>
                            </div>

                            <div class="qr-payment">
                                <strong>QUÉT QR ĐỂ THANH TOÁN</strong>

                                <div class="qr-image-box">
                                    <img src="${pageContext.request.contextPath}/view/assets/images/payment-qr.png"
                                         alt="QR thanh toán"
                                         onerror="this.style.display='none'; document.getElementById('qrFallback').style.display='flex';">

                                    <div id="qrFallback" class="qr-fallback">
                                        QR<br>PAYMENT
                                    </div>
                                </div>

                                <span>Nội dung: <c:out value="${booking.bookingCode}"/></span>
                            </div>
                        </div>

                        <c:if test="${booking.status eq 'Đã hủy' or (not hasPayment and remainingSeconds <= 0)}">
                            <div class="payment-expired-box" id="expiredMessage">
                                <h3>ĐÃ HẾT THỜI GIAN GIỮ PHÒNG</h3>
                                <p>Đơn đặt phòng đã bị hủy tự động vì chưa gửi minh chứng trong thời gian giữ phòng 15 phút.</p>

                                <a href="${pageContext.request.contextPath}/quick-booking">
                                    ĐẶT PHÒNG LẠI
                                </a>
                            </div>
                        </c:if>

                        <c:if test="${hasPayment}">
                            <div class="payment-submitted-box">
                                <h3>MINH CHỨNG ĐÃ ĐƯỢC GỬI</h3>
                                <p>Tiền đặt cọc đã được ghi nhận. Minh chứng đang chờ lễ tân xác nhận.</p>

                                <a href="${pageContext.request.contextPath}/booking-success?bookingCode=${booking.bookingCode}">
                                    XEM KẾT QUẢ ĐẶT PHÒNG
                                </a>
                            </div>
                        </c:if>

                        <c:if test="${not hasPayment and booking.status ne 'Đã hủy' and remainingSeconds > 0}">
                            <form action="${pageContext.request.contextPath}/booking-payment"
                                  method="post" enctype="multipart/form-data"
                                  id="paymentForm" novalidate>

                                <input type="hidden" name="bookingCode" value="${booking.bookingCode}">

                                <div class="upload-title">
                                    <span>⇧</span>
                                    TẢI MINH CHỨNG CHUYỂN KHOẢN
                                </div>

                                <label for="paymentProof" class="upload-box" id="uploadBox">
                                    <div class="upload-placeholder" id="uploadPlaceholder">
                                        <div class="upload-icon">⇧</div>
                                        <strong>Chọn ảnh biên lai hoặc ảnh chụp màn hình giao dịch</strong>
                                        <span>Hỗ trợ JPG, JPEG, PNG · Tối đa 5MB</span>
                                    </div>

                                    <div class="selected-file" id="selectedFile">
                                        <div class="selected-file-icon">✓</div>

                                        <div>
                                            <strong id="selectedFileName"></strong>
                                            <span id="selectedFileSize"></span>
                                        </div>

                                        <button type="button" id="removeFile">×</button>
                                    </div>
                                </label>

                                <input type="file" id="paymentProof" name="paymentProof"
                                       accept=".jpg,.jpeg,.png">

                                <div class="payment-information-note">
                                    Sau khi gửi minh chứng, trạng thái thanh toán sẽ chuyển thành đã đặt cọc.
                                    Lễ tân sẽ kiểm tra và xác nhận đơn đặt phòng sau.
                                </div>

                                <div class="payment-actions">
                                    <a href="${pageContext.request.contextPath}/search"
                                       class="payment-back-button">
                                        ← CHỌN PHÒNG KHÁC
                                    </a>

                                    <button type="submit" class="payment-submit-button" id="submitPayment">
                                        GỬI MINH CHỨNG
                                    </button>
                                </div>

                                <div class="payment-security">
                                    🔒 Thông tin thanh toán của bạn được bảo mật
                                </div>
                            </form>
                        </c:if>
                    </section>

                    <aside class="payment-summary-card">
                        <div class="summary-title">
                            <span>▤</span>
                            <h2>CHI TIẾT ĐẶT PHÒNG</h2>
                        </div>

                        <div class="room-summary">
                            <img src="${not empty roomType.imageUrl
                                        ? roomType.imageUrl[0]
                                        : 'https://placehold.co/600x400?text=La+Mer+Room'}"
                                 alt="${fn:escapeXml(roomType.typeName)}">

                            <div class="room-summary-info">
                                <h3><c:out value="${roomType.typeName}"/></h3>
                                <p>Giường: ${roomType.bedCount} x ${roomType.bedType}</p>
                                <p>Sức chứa: ${roomType.capacity} khách/phòng</p>
                                <p>Diện tích: ${roomType.areaSqm} m²</p>
                            </div>
                        </div>

                        <div class="summary-divider"></div>

                        <div class="summary-row">
                            <span>Ngày nhận phòng:</span>
                            <strong>${checkInText}</strong>
                        </div>

                        <div class="summary-row">
                            <span>Ngày trả phòng:</span>
                            <strong>${checkOutText}</strong>
                        </div>

                        <div class="summary-row">
                            <span>Số đêm:</span>
                            <strong>${numberOfNights} đêm</strong>
                        </div>

                        <div class="summary-row">
                            <span>Số lượng phòng:</span>
                            <strong>${booking.numRooms} phòng</strong>
                        </div>

                        <div class="summary-row">
                            <span>Số khách:</span>
                            <strong>${booking.numGuests} khách</strong>
                        </div>

                        <div class="summary-divider"></div>

                        <div class="summary-row">
                            <span>Đơn giá (1 phòng/1 đêm):</span>
                            <strong>
                                <fmt:formatNumber value="${booking.bookedPricePerNight}" type="number" maxFractionDigits="0"/>
                                VND
                            </strong>
                        </div>

                        <div class="summary-row">
                            <span>Tạm tính:</span>
                            <strong>
                                <fmt:formatNumber value="${totalAmount}" type="number" maxFractionDigits="0"/>
                                VND
                            </strong>
                        </div>

                        <div class="payment-deposit-row">
                            <span>Đặt cọc hôm nay:</span>
                            <strong>
                                <fmt:formatNumber value="${booking.depositAmount}" type="number" maxFractionDigits="0"/>
                                VND
                            </strong>
                        </div>

                        <div class="summary-divider"></div>

                        <div class="payment-status-row">
                            <span>Trạng thái hiện tại:</span>

                            <c:choose>
                                <c:when test="${booking.status eq 'Đã hủy' or (not hasPayment and remainingSeconds <= 0)}">
                                    <strong class="status-cancelled">Đã hủy do hết hạn</strong>
                                </c:when>

                                <c:when test="${hasPayment}">
                                    <strong class="status-waiting">Đã đặt cọc - Chờ xác nhận</strong>
                                </c:when>

                                <c:otherwise>
                                    <strong class="status-unpaid">Chưa thanh toán</strong>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <div class="summary-divider"></div>

                        <div class="booking-benefits">
                            <div class="benefit-item">
                                <div class="benefit-icon">✓</div>
                                <strong>Bảo mật thông tin</strong>
                                <span>Thông tin của bạn được bảo vệ</span>
                            </div>

                            <div class="benefit-item">
                                <div class="benefit-icon">☎</div>
                                <strong>Hỗ trợ 24/7</strong>
                                <span>Đội ngũ chăm sóc luôn sẵn sàng</span>
                            </div>

                            <div class="benefit-item">
                                <div class="benefit-icon">◷</div>
                                <strong>Xác nhận nhanh</strong>
                                <span>Kiểm tra trong thời gian sớm nhất</span>
                            </div>
                        </div>
                    </aside>
                </div>
            </c:if>
        </main>

        <jsp:include page="/view/common/footer.jsp"/>

        <script>
            const pageMessage = document.getElementById("pageMessage");
            const paymentMessageArea = document.getElementById("paymentMessageArea");

            function scrollToPaymentMessage() {
                if (!paymentMessageArea)
                    return;

                const navbarOffset = 120;
                const top = paymentMessageArea.getBoundingClientRect().top
                        + window.scrollY - navbarOffset;

                window.scrollTo({top: top, behavior: "smooth"});
            }

            function showPageMessage(message, isError) {
                if (!pageMessage)
                    return;

                pageMessage.textContent = message;
                pageMessage.style.display = "block";
                pageMessage.style.borderLeftColor = isError ? "#c63f3f" : "#c39747";
                pageMessage.style.background = isError ? "#fff1f1" : "#fbf7ee";
                pageMessage.style.color = isError ? "#a52a2a" : "#657178";

                scrollToPaymentMessage();
            }

            const serverPaymentError = document.getElementById("serverPaymentError");

            if (serverPaymentError) {
                window.addEventListener("load", function () {
                    setTimeout(scrollToPaymentMessage, 100);
                });
            }

            const countdown = document.getElementById("countdown");

            if (countdown && countdown.dataset.seconds) {
                let remainingSeconds = Number(countdown.dataset.seconds);
                let countdownInterval;

                function disablePaymentWhenExpired() {
                    const submitPayment = document.getElementById("submitPayment");
                    const paymentProof = document.getElementById("paymentProof");
                    const removeFile = document.getElementById("removeFile");
                    const uploadBox = document.getElementById("uploadBox");

                    countdown.textContent = "HẾT THỜI GIAN";
                    countdown.classList.add("expired");

                    if (submitPayment) {
                        submitPayment.disabled = true;
                        submitPayment.textContent = "ĐÃ HẾT THỜI GIAN";
                    }

                    if (paymentProof)
                        paymentProof.disabled = true;
                    if (removeFile)
                        removeFile.disabled = true;

                    if (uploadBox) {
                        uploadBox.style.pointerEvents = "none";
                        uploadBox.style.opacity = "0.55";
                    }

                    showPageMessage(
                            "Đã hết thời gian giữ phòng. Đơn sẽ bị hủy và bạn không thể gửi minh chứng thanh toán.",
                            true
                            );
                }

                function updateCountdown() {
                    if (remainingSeconds <= 0) {
                        clearInterval(countdownInterval);
                        disablePaymentWhenExpired();
                        return;
                    }

                    const minutes = Math.floor(remainingSeconds / 60);
                    const seconds = remainingSeconds % 60;

                    countdown.textContent = String(minutes).padStart(2, "0")
                            + ":" + String(seconds).padStart(2, "0");

                    remainingSeconds--;
                }

                updateCountdown();
                countdownInterval = setInterval(updateCountdown, 1000);
            }

            const paymentForm = document.getElementById("paymentForm");
            const paymentProof = document.getElementById("paymentProof");
            const selectedFile = document.getElementById("selectedFile");
            const uploadPlaceholder = document.getElementById("uploadPlaceholder");
            const removeFile = document.getElementById("removeFile");

            if (paymentProof) {
                paymentProof.addEventListener("change", function () {
                    const file = paymentProof.files[0];

                    if (!file)
                        return;

                    const maximumSize = 5 * 1024 * 1024;
                    const extension = file.name.split(".").pop().toLowerCase();
                    const allowedExtensions = ["jpg", "jpeg", "png"];

                    if (!allowedExtensions.includes(extension)) {
                        paymentProof.value = "";
                        selectedFile.style.display = "none";
                        uploadPlaceholder.style.display = "flex";

                        showPageMessage(
                                "Minh chứng chỉ chấp nhận file JPG, JPEG hoặc PNG.",
                                true
                                );
                        return;
                    }

                    if (file.size > maximumSize) {
                        paymentProof.value = "";
                        selectedFile.style.display = "none";
                        uploadPlaceholder.style.display = "flex";

                        showPageMessage("File minh chứng không được vượt quá 5MB.", true);
                        return;
                    }

                    document.getElementById("selectedFileName").textContent = file.name;
                    document.getElementById("selectedFileSize").textContent
                            = (file.size / 1024 / 1024).toFixed(2) + " MB";

                    uploadPlaceholder.style.display = "none";
                    selectedFile.style.display = "flex";

                    showPageMessage("Đã chọn file minh chứng: " + file.name, false);
                });
            }

            if (removeFile) {
                removeFile.addEventListener("click", function (event) {
                    event.preventDefault();

                    paymentProof.value = "";
                    selectedFile.style.display = "none";
                    uploadPlaceholder.style.display = "flex";

                    showPageMessage("Đã xóa file minh chứng vừa chọn.", false);
                });
            }

            if (paymentForm) {
                paymentForm.addEventListener("submit", function (event) {
                    if (!paymentProof.files || paymentProof.files.length === 0) {
                        event.preventDefault();
                        showPageMessage("Vui lòng tải minh chứng thanh toán.", true);
                    }
                });
            }
        </script>
    </body>
</html>