document.addEventListener("DOMContentLoaded", function () {
    // --- 1. XỬ LÝ THÔNG BÁO TỪ SERVER ---
    const urlParams = new URLSearchParams(window.location.search);
    const status = urlParams.get('status');

    if (status) {
        const statusConfigs = {
            "cancel_failed": {title: "Hủy đơn thất bại", text: "Hệ thống xử lý cơ sở dữ liệu gặp sự cố bất ngờ. Vui lòng kiểm tra lại lý do hủy hoặc thử lại sau.", icon: "error"},
            "duplicate_pending_error": {title: "Yêu cầu đang chờ xử lý", text: "Đơn hàng này đã có một yêu cầu khác đang nằm trong danh sách chờ duyệt của Lễ tân.", icon: "warning"},
            "request_failed_no_room": {title: "Hết phòng trống", text: "Hạng phòng hoặc khoảng thời gian bạn chọn hiện tại hệ thống đã hết buồng trống.", icon: "error"},
            "request_failed": {title: "Gửi thất bại", text: "Hệ thống xử lý gặp sự cố bất ngờ. Vui lòng kiểm tra lại thông tin.", icon: "error"},
            "request_success": {title: "Thành công!", text: "Yêu cầu đã được ghi nhận.", icon: "success"},
            "system_error": {title: "Lỗi hệ thống", text: "Vui lòng liên hệ bộ phận hỗ trợ kỹ thuật để được trợ giúp.", icon: "error"}
        };

        if (statusConfigs[status]) {
            Swal.fire({...statusConfigs[status], confirmButtonColor: '#2c3e46'}).then(() => {
                const cleanUrl = window.location.protocol + "//" + window.location.host + window.location.pathname + window.location.search.replace(/([\?&])status=[^&]*(&|$)/, '$1').replace(/[\?&]$/, '');
                window.history.replaceState({}, document.title, cleanUrl);
            });
        }
    }

    // --- 2. CÁC BIẾN GIAO DIỆN ---
    const requestForm = document.getElementById("requestForm");
    if (!requestForm)
        return;

    const radios = document.querySelectorAll("input[name='requestType']");
    const btnSubmit = document.querySelector(".btn-submit-request");
    const bookingStatus = document.getElementById("bookingStatus")?.value || "";

    // Đọc giá trị ngày checkout cũ từ input hidden của JSP để validate
    const oldCheckoutValue = requestForm.querySelector("input[name='oldCheckoutDate']").value;

    // --- 3. LOGIC CHUYỂN TAB & UI ---
    function switchTab(activeTab) {
        const sumRequestType = document.getElementById("sumRequestType");
        if (sumRequestType)
            sumRequestType.textContent = activeTab;

        document.querySelectorAll(".tab-pane").forEach(pane => pane.style.display = "none");

        const targetMap = {
            "Đổi hạng phòng": "tab-change-room",
            "Gia hạn phòng": "tab-extend-stay",
            "Hủy đặt phòng": "tab-cancel-booking"
        };

        const activePane = document.getElementById(targetMap[activeTab]);
        if (activePane)
            activePane.style.display = "block";

        // KÍCH HOẠT TÍNH TOÁN NGAY KHI USER CHUYỂN TAB
        if (activeTab === "Hủy đặt phòng") {
            calculateCancellationDetails();
        } else if (activeTab === "Gia hạn phòng") {
            initDefaultExtensionNights();
        }

        // ĐỒNG BỘ CLASS ACTIVE CHO THẺ CHA (THAY THẾ HOÀN TOÀN CHO :HAS() CỦA CSS PHÁT SINH LỖI)
        radios.forEach(radio => {
            const container = radio.closest('.type-card-option');
            if (container) {
                if (radio.checked) {
                    container.classList.add("active-card");
                } else {
                    container.classList.remove("active-card");
                }
            }
        });
    }

    function updateUIByBookingStatus() {
        const status = bookingStatus.trim();
        radios.forEach(radio => {
            let isBlocked = false;
            if (status.includes("Chờ xử lý"))
                isBlocked = (radio.value !== "Hủy đặt phòng");
            else if (status.includes("Đã nhận phòng"))
                isBlocked = (radio.value !== "Gia hạn phòng");
            else if (status.includes("Đã trả phòng") || status.includes("Đã hủy"))
                isBlocked = true;

            const container = radio.closest('.type-card-option');
            if (container) {
                container.classList.toggle("disabled-option", isBlocked);
            }
            if (isBlocked)
                radio.checked = false;
        });

        // --- LOGIC GIỮ NGUYÊN TAB KHI BỊ LỖI REFRESH ---
        const urlParams = new URLSearchParams(window.location.search);
        const failedType = urlParams.get('failedType');

        if (failedType) {
            const targetRadio = document.querySelector(`input[name='requestType'][value='${failedType}']`);
            if (targetRadio && !targetRadio.closest('.type-card-option').classList.contains('disabled-option')) {
                targetRadio.checked = true;
                switchTab(failedType);
                return;
            }
        }

        const activeRadio = document.querySelector("input[name='requestType']:checked");
        const firstValid = document.querySelector(".type-card-option:not(.disabled-option) input[name='requestType']");

        if (activeRadio) {
            switchTab(activeRadio.value);
        } else if (firstValid) {
            firstValid.checked = true;
            switchTab(firstValid.value);
        }
    }

    // --- 4. TÍNH TOÁN GIÁ (ĐỔI HẠNG PHÒNG) ---
    const checkIn = new Date(requestForm.querySelector("input[name='checkInDate']").value);
    const checkOut = new Date(oldCheckoutValue);
    const totalNights = Math.round((checkOut - checkIn) / (1000 * 60 * 60 * 24));
    const numRooms = parseInt(requestForm.querySelector("input[name='numRooms']").value) || 1;
    const oldPricePerNight = parseFloat(document.getElementById("oldBasePrice").value) || 0;
    const totalOldPrice = oldPricePerNight * totalNights * numRooms;

    const totalOldPriceEl = document.getElementById("totalOldPrice");
    const totalNewPriceEl = document.getElementById("totalNewPrice");
    const sumCostDiffEl = document.getElementById("sumCostDiff");

    if (totalOldPriceEl)
        totalOldPriceEl.textContent = totalOldPrice.toLocaleString('vi-VN') + " VND";
    if (totalNewPriceEl)
        totalNewPriceEl.textContent = totalOldPrice.toLocaleString('vi-VN') + " VND";
    if (document.getElementById("summaryTotalNights"))
        document.getElementById("summaryTotalNights").textContent = totalNights + " đêm";

    const targetRoomSelect = document.getElementById("targetRoomTypeId");
    if (targetRoomSelect) {
        targetRoomSelect.addEventListener("change", function () {
            const selectedOption = this.options[this.selectedIndex];
            const newPricePerNight = parseFloat(selectedOption.getAttribute("data-price"));
            const totalNewPrice = newPricePerNight * totalNights * numRooms;
            const diff = totalNewPrice - totalOldPrice;
            totalNewPriceEl.textContent = totalNewPrice.toLocaleString('vi-VN') + " VND";
            sumCostDiffEl.textContent = (diff >= 0 ? "+" : "") + diff.toLocaleString('vi-VN') + " đ";
        });
    }

    // --- LOGIC TÍNH TOÁN GIA HẠN PHÒNG ---
    const inputNewCheckout = document.getElementById("newCheckoutDate");
    const sumCostDiffExtend = document.getElementById("sumCostDiffExtend");
    const summaryNightsOld = document.getElementById("summaryNightsOld");
    const summaryNightsNew = document.getElementById("summaryNightsNew");
    const summaryNightsExtra = document.getElementById("summaryNightsExtra");

    // Hàm hiển thị số đêm hiện tại ngay lập tức khi load trang/mở tab
    function initDefaultExtensionNights() {
        const checkInDateStr = requestForm.querySelector("input[name='checkInDate']").value;
        const oldCheckoutDateStr = oldCheckoutValue;

        if (checkInDateStr && oldCheckoutDateStr) {
            const checkInDate = new Date(checkInDateStr);
            const oldCheckoutDate = new Date(oldCheckoutDateStr);
            const nightsOld = Math.round((oldCheckoutDate - checkInDate) / (1000 * 60 * 60 * 24));
            
            if (summaryNightsOld && nightsOld > 0) {
                summaryNightsOld.textContent = nightsOld + " đêm";
            }
        }
    }

    if (inputNewCheckout) {
        inputNewCheckout.addEventListener("change", function () {
            const checkIn = new Date(requestForm.querySelector("input[name='checkInDate']").value);
            const oldCheckout = new Date(oldCheckoutValue);
            const newCheckout = new Date(this.value);
            const bookedPricePerNight = parseFloat(document.getElementById("oldBasePrice").value) || 0;
            const numRooms = parseInt(requestForm.querySelector("input[name='numRooms']").value) || 1;

            if (newCheckout > oldCheckout) {
                const diffDays = (d1, d2) => Math.round((d2 - d1) / (1000 * 60 * 60 * 24));

                const nightsOld = diffDays(checkIn, oldCheckout);
                const nightsNew = diffDays(checkIn, newCheckout);
                const nightsExtra = nightsNew - nightsOld;

                summaryNightsOld.textContent = nightsOld + " đêm";
                summaryNightsNew.textContent = nightsNew + " đêm";
                summaryNightsExtra.textContent = nightsExtra + " đêm";

                const extraCost = nightsExtra * bookedPricePerNight * numRooms;
                sumCostDiffExtend.textContent = "+" + extraCost.toLocaleString('vi-VN') + " đ";

                requestForm.querySelector("input[name='checkOutDate']").value = this.value;
            } else {
                initDefaultExtensionNights();
                if (summaryNightsNew) summaryNightsNew.textContent = "0 đêm";
                if (summaryNightsExtra) summaryNightsExtra.textContent = "0 đêm";
                if (sumCostDiffExtend) sumCostDiffExtend.textContent = "0 đ";
                requestForm.querySelector("input[name='checkOutDate']").value = oldCheckoutValue;
            }
        });
    }

    // --- LOGIC TÍNH TOÁN VÀ MAP CHÍNH SÁCH HỦY PHÒNG ---
    function calculateCancellationDetails() {
        const checkInDateEl = document.getElementById("checkInDate");
        if (!checkInDateEl)
            return;

        const checkInDateStr = checkInDateEl.value;
        if (!checkInDateStr)
            return;

        // Thiết lập mốc giờ check-in cố định 14:00 theo chính sách
        const checkInDateTime = new Date(`${checkInDateStr}T14:00:00`);
        const now = new Date();

        // Tính toán số giờ chênh lệch thực tế từ lúc gửi đơn đến khi check-in
        const diffMs = checkInDateTime - now;
        const diffHours = diffMs / (1000 * 60 * 60);

        // HIỂN THỊ SỐ GIỜ CÒN LẠI MINH BẠCH LÊN GIAO DIỆN
        const lblHoursRemaining = document.getElementById("lblHoursRemaining");
        if (lblHoursRemaining) {
            if (diffHours > 0) {
                lblHoursRemaining.textContent = diffHours.toFixed(1) + " giờ";
            } else {
                lblHoursRemaining.textContent = "0 giờ (Đã quá mốc giờ check-in " + Math.abs(diffHours).toFixed(1) + " giờ)";
            }
        }

        // Xác định tỷ lệ hoàn cọc và dòng cần highlight dựa trên số giờ còn lại
        let refundPercent = 0;
        let activeRowId = "policy-row-72";

        if (diffHours >= 72) {
            refundPercent = 1.00;
            activeRowId = "policy-row-72";
        } else if (diffHours >= 48 && diffHours < 72) {
            refundPercent = 0.70;
            activeRowId = "policy-row-48";
        } else if (diffHours >= 24 && diffHours < 48) {
            refundPercent = 0.50;
            activeRowId = "policy-row-24";
        } else {
            refundPercent = 0.30;
            activeRowId = "policy-row-0";
        }

        // Duyệt qua các dòng để xóa các định dạng cũ và làm nổi bật dòng hiện tại (màu xám nhạt cao cấp)
        ["policy-row-72", "policy-row-48", "policy-row-24", "policy-row-0"].forEach(rowId => {
            const row = document.getElementById(rowId);
            if (row) {
                if (rowId === activeRowId) {
                    row.style.backgroundColor = "#f1f5f9";
                    row.style.fontWeight = "bold";
                    row.style.color = "#0f172a";
                } else {
                    row.style.backgroundColor = "";
                    row.style.fontWeight = "normal";
                    row.style.color = "";
                }
            }
        });

        // Tính toán các con số tài chính cụ thể
        const bookedPricePerNight = parseFloat(document.getElementById("oldBasePrice").value) || 0;
        const numRooms = parseInt(requestForm.querySelector("input[name='numRooms']").value) || 1;
        const checkOutDateStr = requestForm.querySelector("input[name='oldCheckoutDate']").value;
        const totalNights = Math.round((new Date(checkOutDateStr) - new Date(checkInDateStr)) / (1000 * 60 * 60 * 24)) || 1;

        const totalBookingValue = bookedPricePerNight * totalNights * numRooms;
        const depositValue = totalBookingValue * 0.30;
        const finalRefund = depositValue * refundPercent;

        // Ghi đè toàn bộ dữ liệu thật lên giao diện
        if (document.getElementById("cancelTotalBooking")) {
            document.getElementById("cancelTotalBooking").textContent = totalBookingValue.toLocaleString('vi-VN') + " VND";
        }
        if (document.getElementById("cancelDepositValue")) {
            document.getElementById("cancelDepositValue").textContent = depositValue.toLocaleString('vi-VN') + " VND";
        }
        if (document.getElementById("cancelFeeValue")) {
            const cancelFeeText = (depositValue - finalRefund).toLocaleString('vi-VN') + " VND";
            const currentPenaltyPercent = ((1 - refundPercent) * 100).toFixed(0);
            document.getElementById("cancelFeeValue").textContent = `${cancelFeeText} (Khấu trừ ${currentPenaltyPercent}% tiền cọc)`;
        }
        if (document.getElementById("cancelRefundValue")) {
            document.getElementById("cancelRefundValue").textContent = finalRefund.toLocaleString('vi-VN') + " VND";
        }
    }

    // --- 5. VALIDATE LOGIC CHUẨN ---
    function validateLogic() {
        const selectedRadio = document.querySelector("input[name='requestType']:checked");
        if (!selectedRadio)
            return "Vui lòng chọn loại yêu cầu.";

        const activeTab = selectedRadio.value;
        if (activeTab === "Đổi hạng phòng") {
            if (targetRoomSelect.value === requestForm.querySelector("input[name='roomTypeId']").value)
                return "Hạng phòng mong muốn phải khác hạng phòng hiện tại.";
            if (!document.getElementById("reasonDetailsChange").value.trim())
                return "Vui lòng nhập lý do thay đổi hạng phòng.";
        } else if (activeTab === "Gia hạn phòng") {
            const newDate = document.getElementById("newCheckoutDate").value;
            if (!newDate)
                return "Vui lòng chọn ngày trả phòng mới.";
            if (new Date(newDate) <= new Date(oldCheckoutValue))
                return "Ngày trả phòng mới phải sau ngày trả phòng hiện tại.";
            if (!document.getElementById("reasonDetailsExtend").value.trim())
                return "Vui lòng nhập lý do gia hạn thời gian lưu trú.";
        } else if (activeTab === "Hủy đặt phòng") {
            if (!document.getElementById("reasonDetailsCancel").value.trim())
                return "Vui lòng nhập đầy đủ lý do hủy đơn đặt phòng.";
            if (!document.getElementById("chkPolicyAgree").checked)
                return "Bạn cần tích chọn xác nhận đồng ý với chính sách hủy phòng.";
        }
        return null;
    }

    // Sự kiện submit qua SweetAlert2
    if (btnSubmit) {
        btnSubmit.addEventListener("click", function (e) {
            e.preventDefault();
            const error = validateLogic();
            if (error) {
                Swal.fire({title: 'Thông tin chưa hợp lệ', text: error, icon: 'warning', confirmButtonColor: '#2c3e46'});
                return;
            }

            const selectedRadio = document.querySelector("input[name='requestType']:checked");
            const isCancel = selectedRadio && selectedRadio.value === "Hủy đặt phòng";

            Swal.fire({
                title: isCancel ? 'Xác nhận hủy đặt phòng?' : 'Xác nhận gửi yêu cầu?',
                text: isCancel ? 'Thao tác hủy đơn sau khi được duyệt sẽ không thể hoàn tác!' : 'Yêu cầu xử lý sẽ được gửi trực tiếp tới bộ phận Lễ tân.',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: isCancel ? '#ef4444' : '#2c3e46',
                confirmButtonText: isCancel ? 'XÁC NHẬN HỦY ĐƠN' : 'ĐỒNG Ý GỬI',
                cancelButtonText: 'HỦY BỎ'
            }).then((result) => {
                if (result.isConfirmed) {
                    requestForm.submit();
                }
            });
        });
    }

    radios.forEach(radio => radio.addEventListener("change", () => switchTab(radio.value)));

    // Khởi chạy tính toán số đêm mặc định cho tab gia hạn khi vừa load trang
    initDefaultExtensionNights();

    // Khởi chạy đồng bộ UI ban đầu
    updateUIByBookingStatus();
});