<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">

        <meta name="viewport"
              content="width=device-width, initial-scale=1.0">

        <title>Gửi thông tin đặt cọc - La Mer Hotel</title>

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
            <div class="booking-progress">
                <div class="progress-item completed">
                    <div class="progress-number">1</div>
                    <div class="progress-name">
                        THÔNG TIN ĐẶT PHÒNG
                    </div>
                </div>
                <div class="progress-line completed"></div>

                <div class="progress-item active">
                    <div class="progress-number">2</div>

                    <div class="progress-name">
                        GỬI THÔNG TIN ĐẶT CỌC
                    </div>
                </div>

                <div class="progress-line"></div>

                <div class="progress-item disabled">
                    <div class="progress-number">3</div>

                    <div class="progress-name">
                        HOÀN TẤT ĐẶT PHÒNG
                    </div>
                </div>
            </div>

            <c:if test="${empty booking and not empty error}">
                <div class="booking-error">
                    <strong>Thông báo:</strong>
                    <c:out value="${error}"/>
                </div>
            </c:if>

            <c:if test="${not empty booking}">

                <c:choose>
                    <c:when test="${hasPayment}">
                        <div class="hold-time-box">
                            <div>
                                <strong>
                                    Đã gửi thông tin giao dịch
                                </strong>

                                <span>
                                    Giao dịch đang chờ lễ tân kiểm tra
                                    và xác nhận
                                </span>
                            </div>

                            <div>ĐÃ GỬI</div>
                        </div>
                    </c:when>

                    <c:when test="${booking.status eq 'Đã hủy'
                                    or remainingSeconds <= 0}">

                            <div class="hold-time-box expired">
                                <div>
                                    <strong>
                                        Đã hết thời gian giữ phòng
                                    </strong>

                                    <span>
                                        Đơn không còn đủ điều kiện gửi
                                        thông tin thanh toán
                                    </span>
                                </div>

                                <div id="countdown">
                                    HẾT THỜI GIAN
                                </div>
                            </div>
                    </c:when>

                    <c:otherwise>
                        <div class="hold-time-box">
                            <div>
                                <strong>
                                    Thời gian giữ phòng còn lại
                                </strong>

                                <span>
                                    Hoàn tất gửi thông tin giao dịch
                                    trước khi thời gian kết thúc
                                </span>
                            </div>

                            <div id="countdown"
                                 data-expires-at="${expiresAtMillis}"
                                 data-server-now="${serverNowMillis}">
                                00:00
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>

                <div class="payment-layout">

                    <section class="payment-main-card"
                             id="paymentSection">

                        <div class="payment-title">
                            <div>
                                <h1>
                                    GỬI THÔNG TIN ĐẶT CỌC
                                </h1>

                                <p>
                                    Sau khi chuyển khoản, vui lòng nhập
                                    tên người chuyển và mã giao dịch hoặc
                                    mã tham chiếu để khách sạn kiểm tra.
                                </p>
                            </div>
                        </div>

                        <div class="payment-warning">
                            Vui lòng chuyển khoản đúng số tiền đặt cọc
                            và ghi chính xác mã đặt phòng trong nội dung
                            chuyển khoản.
                        </div>

                        <div class="bank-card">

                            <div class="bank-information">
                                <h2>
                                    THÔNG TIN CHUYỂN KHOẢN
                                </h2>

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
                                        <fmt:formatNumber
                                            value="${booking.depositAmount}"
                                            type="number"
                                            maxFractionDigits="0"/>
                                        VND
                                    </strong>
                                </div>

                                <div class="bank-row">
                                    <span>Nội dung chuyển khoản:</span>

                                    <strong>
                                        <c:out value="${booking.bookingCode}"/>
                                    </strong>
                                </div>

                                <div class="booking-code-note">
                                    Mã đặt phòng:

                                    <strong>
                                        <c:out value="${booking.bookingCode}"/>
                                    </strong>.

                                    Vui lòng nhập chính xác mã này trong
                                    nội dung chuyển khoản.
                                </div>
                            </div>

                            <div class="qr-payment">
                                <strong>
                                    QUÉT QR ĐỂ THANH TOÁN
                                </strong>

                                <div class="qr-image-box">
                                    <img src="https://i.ibb.co/GQZ9XWjM/36eec7bc-5c63-4d80-881e-2bdabccbc226.png"
                                         alt="QR thanh toán"
                                         onerror="this.style.display='none';
                                         document.getElementById('qrFallback').style.display='flex';">

                                    <div id="qrFallback"
                                         class="qr-fallback">
                                        QR<br>PAYMENT
                                    </div>
                                </div>

                                <span>
                                    Nội dung:
                                    <c:out value="${booking.bookingCode}"/>
                                </span>
                            </div>
                        </div>

                        <c:if test="${booking.status eq 'Đã hủy'
                                      or (not hasPayment
                                      and remainingSeconds <= 0)}">

                              <div class="payment-expired-box"
                                   id="expiredMessage">

                                  <h3>
                                      ĐÃ HẾT THỜI GIAN GIỮ PHÒNG
                                  </h3>

                                  <p>
                                      Đơn đặt phòng đã bị hủy tự động vì
                                      chưa gửi thông tin thanh toán trong
                                      thời gian giữ phòng 15 phút.
                                  </p>

                                  <a href="${pageContext.request.contextPath}/quick-booking">
                                      ĐẶT PHÒNG LẠI
                                  </a>
                              </div>
                        </c:if>

                        <c:if test="${hasPayment}">
                            <div class="payment-submitted-box">
                                <h3>
                                    THÔNG TIN GIAO DỊCH ĐÃ ĐƯỢC GỬI
                                </h3>

                                <p>
                                    Thông tin giao dịch đã được ghi nhận.
                                    Giao dịch đang chờ lễ tân kiểm tra
                                    và xác nhận.
                                </p>

                                <a href="${pageContext.request.contextPath}/booking-success?bookingCode=${booking.bookingCode}">
                                    XEM KẾT QUẢ ĐẶT PHÒNG
                                </a>
                            </div>
                        </c:if>

                        <c:if test="${not hasPayment
                                      and booking.status ne 'Đã hủy'
                                      and remainingSeconds > 0}">

                              <form action="${pageContext.request.contextPath}/booking-payment"
                                    method="post"
                                    id="paymentForm"
                                    novalidate>

                                  <input type="hidden"
                                         name="bookingCode"
                                         value="${booking.bookingCode}">

                                  <div class="upload-title">
                                      <span>▤</span>
                                      NHẬP THÔNG TIN GIAO DỊCH
                                  </div>

                                  <div id="paymentMessageArea">

                                      <c:if test="${not empty error}">
                                          <div class="booking-error"
                                               id="serverPaymentError">

                                              <strong>Thông báo:</strong>
                                              <c:out value="${error}"/>
                                          </div>
                                      </c:if>

                                      <div id="pageMessage"
                                           class="payment-information-note payment-page-message">
                                      </div>
                                  </div>

                                  <div class="transaction-proof-field">

                                      <label for="transactionProof">
                                          Tên người chuyển - Mã giao dịch /
                                          Mã tham chiếu
                                          <span>*</span>
                                      </label>

                                      <input type="text"
                                             id="transactionProof"
                                             name="paymentProof"
                                             class="transaction-proof-input"
                                             maxlength="100"
                                             value="${fn:escapeXml(param.paymentProof)}"
                                             placeholder="Ví dụ: NGUYEN VAN AN - FT26123456789"
                                             autocomplete="off"
                                             required>

                                      <small>
                                          Nhập theo đúng định dạng:
                                          <strong>
                                              Tên người chuyển - Mã giao dịch
                                          </strong>.
                                      </small>
                                  </div>

                                  <div class="payment-information-note">
                                      Sau khi gửi thông tin, lễ tân sẽ đối chiếu
                                      tên người chuyển, mã giao dịch, số tiền và
                                      nội dung chuyển khoản trước khi xác nhận đơn.
                                  </div>

                                  <div class="payment-actions">
                                      <a href="${pageContext.request.contextPath}/search"
                                         class="payment-back-button">

                                          ← CHỌN PHÒNG KHÁC
                                      </a>

                                      <button type="submit"
                                              class="payment-submit-button"
                                              id="submitPayment">

                                          GỬI THÔNG TIN GIAO DỊCH
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
                                <h3>
                                    <c:out value="${roomType.typeName}"/>
                                </h3>

                                <p>
                                    Giường:
                                    ${roomType.bedCount} x
                                    ${roomType.bedType}
                                </p>

                                <p>
                                    Sức chứa:
                                    ${roomType.capacity} khách/phòng
                                </p>

                                <p>
                                    Diện tích:
                                    ${roomType.areaSqm} m²
                                </p>
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
                            <span>
                                Đơn giá (1 phòng/1 đêm):
                            </span>

                            <strong>
                                <fmt:formatNumber
                                    value="${booking.bookedPricePerNight}"
                                    type="number"
                                    maxFractionDigits="0"/>
                                VND
                            </strong>
                        </div>

                        <div class="summary-row">
                            <span>Tạm tính:</span>

                            <strong>
                                <fmt:formatNumber
                                    value="${totalAmount}"
                                    type="number"
                                    maxFractionDigits="0"/>
                                VND
                            </strong>
                        </div>

                        <div class="payment-deposit-row">
                            <span>Đặt cọc hôm nay:</span>

                            <strong>
                                <fmt:formatNumber
                                    value="${booking.depositAmount}"
                                    type="number"
                                    maxFractionDigits="0"/>
                                VND
                            </strong>
                        </div>

                        <div class="summary-divider"></div>

                        <div class="payment-status-row">
                            <span>Trạng thái hiện tại:</span>

                            <c:choose>
                                <c:when test="${booking.status eq 'Đã hủy'
                                                or (not hasPayment
                                                and remainingSeconds <= 0)}">

                                        <strong class="status-cancelled"
                                                id="paymentStatusText">
                                            Đã hủy do hết hạn
                                        </strong>
                                </c:when>

                                <c:when test="${hasPayment}">
                                    <strong class="status-waiting"
                                            id="paymentStatusText">
                                        Đã gửi - Chờ xác nhận
                                    </strong>
                                </c:when>

                                <c:otherwise>
                                    <strong class="status-unpaid"
                                            id="paymentStatusText">
                                        Chưa thanh toán
                                    </strong>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <div class="summary-divider"></div>

                        <div class="booking-benefits">

                            <div class="benefit-item">
                                <div class="benefit-icon">✓</div>

                                <strong>Bảo mật thông tin</strong>

                                <span>
                                    Thông tin của bạn được bảo vệ
                                </span>
                            </div>

                            <div class="benefit-item">
                                <div class="benefit-icon">☎</div>

                                <strong>Hỗ trợ 24/7</strong>

                                <span>
                                    Đội ngũ chăm sóc luôn sẵn sàng
                                </span>
                            </div>

                            <div class="benefit-item">
                                <div class="benefit-icon">◷</div>

                                <strong>Xác nhận nhanh</strong>

                                <span>
                                    Kiểm tra trong thời gian sớm nhất
                                </span>
                            </div>
                        </div>
                    </aside>
                </div>
            </c:if>
        </main>

        <jsp:include page="/view/common/footer.jsp"/>

        <script>
            const pageMessage =
                    document.getElementById("pageMessage");

            const paymentMessageArea =
                    document.getElementById("paymentMessageArea");

            const paymentForm =
                    document.getElementById("paymentForm");

            const transactionProof =
                    document.getElementById("transactionProof");

            const submitPayment =
                    document.getElementById("submitPayment");

            function scrollToPaymentMessage() {
                if (!paymentMessageArea) {
                    return;
                }

                const navbarOffset = 120;

                const top =
                        paymentMessageArea.getBoundingClientRect().top
                        + window.scrollY
                        - navbarOffset;

                window.scrollTo({
                    top: top,
                    behavior: "smooth"
                });
            }

            function showPageMessage(message, isError) {
                if (!pageMessage) {
                    return;
                }

                pageMessage.textContent = message;
                pageMessage.style.display = "block";

                pageMessage.style.borderLeftColor =
                        isError ? "#c63f3f" : "#c39747";

                pageMessage.style.background =
                        isError ? "#fff1f1" : "#fbf7ee";

                pageMessage.style.color =
                        isError ? "#a52a2a" : "#657178";

                scrollToPaymentMessage();
            }

            const serverPaymentError =
                    document.getElementById("serverPaymentError");

            if (serverPaymentError) {
                window.addEventListener("load", function () {
                    setTimeout(scrollToPaymentMessage, 100);
                });
            }

            const countdown =
                    document.getElementById("countdown");

            if (countdown && countdown.dataset.expiresAt) {
                const expiresAtMillis =
                        Number(countdown.dataset.expiresAt);

                const serverNowMillis =
                        Number(countdown.dataset.serverNow);

                const serverClientOffset =
                        serverNowMillis - Date.now();

                let countdownInterval;
                let expiredHandled = false;

                function getCurrentServerTimeMillis() {
                    return Date.now() + serverClientOffset;
                }

                function getRemainingSeconds() {
                    return Math.ceil(
                            (expiresAtMillis
                                    - getCurrentServerTimeMillis()) / 1000
                            );
                }

                function disablePaymentWhenExpired() {
                    if (expiredHandled) {
                        return;
                    }

                    expiredHandled = true;

                    countdown.textContent =
                            "HẾT THỜI GIAN";

                    countdown.classList.add("expired");

                    if (submitPayment) {
                        submitPayment.disabled = true;

                        submitPayment.textContent =
                                "ĐÃ HẾT THỜI GIAN";
                    }

                    if (transactionProof) {
                        transactionProof.disabled = true;
                    }

                    const paymentStatusText =
                            document.getElementById("paymentStatusText");

                    if (paymentStatusText) {
                        paymentStatusText.textContent =
                                "Đã hủy do hết hạn";

                        paymentStatusText.className =
                                "status-cancelled";
                    }

                    showPageMessage(
                            "Đã hết thời gian giữ phòng. "
                            + "Bạn không thể gửi thông tin giao dịch.",
                            true
                            );
                }

                function updateCountdown() {
                    const remainingSeconds =
                            getRemainingSeconds();

                    if (remainingSeconds <= 0) {
                        clearInterval(countdownInterval);
                        disablePaymentWhenExpired();
                        return;
                    }

                    const minutes =
                            Math.floor(remainingSeconds / 60);

                    const seconds =
                            remainingSeconds % 60;

                    countdown.textContent =
                            String(minutes).padStart(2, "0")
                            + ":"
                            + String(seconds).padStart(2, "0");
                }

                updateCountdown();

                countdownInterval =
                        setInterval(updateCountdown, 1000);

                window.addEventListener("pageshow", updateCountdown);

                document.addEventListener("visibilitychange", function () {
                    if (!document.hidden) {
                        updateCountdown();
                    }
                });
            }

            if (paymentForm) {
                paymentForm.addEventListener(
                        "submit",
                        function (event) {

                            const proofValue =
                                    transactionProof.value.trim();

                            if (proofValue === "") {
                                event.preventDefault();

                                showPageMessage(
                                        "Vui lòng nhập tên người chuyển "
                                        + "và mã giao dịch.",
                                        true
                                        );

                                transactionProof.focus();
                                return;
                            }

                            const separatorIndex =
                                    proofValue.indexOf("-");

                            if (separatorIndex <= 0
                                    || separatorIndex
                                    >= proofValue.length - 1) {

                                event.preventDefault();

                                showPageMessage(
                                        "Vui lòng nhập đúng dạng: "
                                        + "Tên người chuyển - Mã giao dịch.",
                                        true
                                        );

                                transactionProof.focus();
                                return;
                            }

                            const senderName =
                                    proofValue
                                    .substring(0, separatorIndex)
                                    .trim();

                            const transactionCode =
                                    proofValue
                                    .substring(separatorIndex + 1)
                                    .trim();

                            if (senderName.length < 2) {
                                event.preventDefault();

                                showPageMessage(
                                        "Tên người chuyển không hợp lệ.",
                                        true
                                        );

                                transactionProof.focus();
                                return;
                            }

                            if (transactionCode.length < 4) {
                                event.preventDefault();

                                showPageMessage(
                                        "Mã giao dịch hoặc mã tham chiếu "
                                        + "không hợp lệ.",
                                        true
                                        );

                                transactionProof.focus();
                                return;
                            }

                            transactionProof.value =
                                    senderName + " - " + transactionCode;

                            submitPayment.disabled = true;

                            submitPayment.textContent =
                                    "ĐANG GỬI...";
                        }
                );
            }
        </script>
    </body>
</html>