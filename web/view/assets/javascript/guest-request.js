document.addEventListener("DOMContentLoaded", function () {
    const radios = document.querySelectorAll("input[name='requestType']");
    const sumRequestType = document.getElementById("sumRequestType");
    const sumCostDiffChange = document.getElementById("sumCostDiff");
    const tabChangeRoom = document.getElementById("tab-change-room");
    const tabExtendStay = document.getElementById("tab-extend-stay");
    const tabCancelBooking = document.getElementById("tab-cancel-booking");
    const requestForm = document.getElementById("requestForm");

    if (!requestForm)
        return;

    const bookingStatus = document.getElementById("bookingStatus")?.value;

    const btnSubmit = document.querySelector(".btn-submit-request");
    const oldCheckoutStr = requestForm.querySelector("input[name='oldCheckoutDate']").value;
    const numRooms = parseInt(requestForm.querySelector("input[name='numRooms']").value) || 1;
    const oldPrice = parseFloat(document.getElementById("oldBasePrice").value) || 0;

    const reasonChange = document.getElementById("reasonDetailsChange");
    const reasonExtend = document.getElementById("reasonDetailsExtend");
    const reasonCancel = document.getElementById("reasonDetailsCancel");
    const chkPolicyAgree = document.getElementById("chkPolicyAgree");
    const targetRoomSelect = document.getElementById("targetRoomTypeId");

    // Hàm chuyển tab
    function switchTab(activeTab) {
        if (sumRequestType)
            sumRequestType.textContent = activeTab;
        if (tabChangeRoom)
            tabChangeRoom.style.display = (activeTab === "Đổi hạng phòng") ? "block" : "none";
        if (tabExtendStay)
            tabExtendStay.style.display = (activeTab === "Gia hạn phòng") ? "block" : "none";
        if (tabCancelBooking)
            tabCancelBooking.style.display = (activeTab === "Hủy đặt phòng") ? "block" : "none";
    }

    // Hàm khóa giao diện
    function updateUIByBookingStatus() {

        const status = (bookingStatus || "").trim();

        radios.forEach(radio => {
            const val = radio.value;
            let isBlocked = false;

            // Định nghĩa logic chặn
            if (status.includes("Chờ xử lý")) {
                isBlocked = (val !== "Hủy đặt phòng");
            } else if (status.includes("Đã xác nhận")) {
                isBlocked = false;
            } else if (status.includes("Đã nhận phòng")) {
                isBlocked = (val !== "Gia hạn phòng");
            } else if (status.includes("Đã trả phòng")||status.includes("Đã hủy")) {
                isBlocked = true; // Khóa sạch khi đã trả phòng
            }

            const container = radio.closest('.type-card-option');
            if (isBlocked) {
                container.classList.add("disabled-option");
                radio.checked = false; // BẮT BUỘC BỎ CHỌN TẠI ĐÂY
            } else {
                container.classList.remove("disabled-option");
            }
        });

        // Sau khi đã dọn dẹp các radio bị block, ta mới chọn lại tab hợp lệ
        const activeRadio = document.querySelector("input[name='requestType']:checked");
        const firstValid = document.querySelector(".type-card-option:not(.disabled-option) input[name='requestType']");

        if (activeRadio) {
            switchTab(activeRadio.value);
        } else if (firstValid) {
            firstValid.checked = true;
            switchTab(firstValid.value);
        } else {
            // Nếu tất cả bị block (như trường hợp Đã trả phòng), ẩn hết tab
            if (sumRequestType)
                sumRequestType.textContent = "—";
            if (tabChangeRoom)
                tabChangeRoom.style.display = "none";
            if (tabExtendStay)
                tabExtendStay.style.display = "none";
            if (tabCancelBooking)
                tabCancelBooking.style.display = "none";
        }
    }

    // Sự kiện thay đổi tab
    radios.forEach(radio => {
        radio.addEventListener("change", function () {
            switchTab(this.value);
        });
    });

    function validateLogic() {
        const selectedRadio = document.querySelector("input[name='requestType']:checked");
        if (!selectedRadio)
            return "Vui lòng chọn một loại yêu cầu.";
        const activeTab = selectedRadio.value;

        if (activeTab === "Đổi hạng phòng") {
            if (targetRoomSelect.value === requestForm.querySelector("input[name='roomTypeId']").value)
                return "Hạng phòng mong muốn đang trùng với hạng phòng hiện tại.";
            if (!reasonChange.value.trim())
                return "Vui lòng nhập lý do thay đổi hạng phòng.";
        } else if (activeTab === "Gia hạn phòng") {
            const newDate = new Date(document.getElementById("newCheckoutDate").value);
            const oldDate = new Date(oldCheckoutStr);
            if (!document.getElementById("newCheckoutDate").value)
                return "Vui lòng chọn ngày trả phòng mới.";
            if (newDate <= oldDate)
                return "Ngày trả phòng mới phải sau ngày trả phòng hiện tại.";
            if (!reasonExtend.value.trim())
                return "Vui lòng nhập lý do gia hạn.";
        } else if (activeTab === "Hủy đặt phòng") {
            if (!reasonCancel.value.trim())
                return "Vui lòng nhập lý do hủy đặt phòng.";
            if (!chkPolicyAgree.checked)
                return "Bạn cần đồng ý với chính sách hủy phòng.";
        }
        return null;
    }

    btnSubmit.addEventListener("click", function (e) {
        e.preventDefault();
        const error = validateLogic();
        if (error) {
            Swal.fire({title: 'Thông tin chưa hợp lệ', text: error, icon: 'warning', confirmButtonColor: '#2c3e46'});
            return;
        }
        Swal.fire({
            title: 'Xác nhận gửi yêu cầu?',
            icon: 'question',
            showCancelButton: true,
            confirmButtonColor: '#2c3e46',
            confirmButtonText: 'ĐỒNG Ý GỬI'
        }).then((result) => {
            if (result.isConfirmed)
                HTMLFormElement.prototype.submit.call(requestForm);
        });
    });

    if (targetRoomSelect)
        targetRoomSelect.addEventListener("change", function () {
            const selectedOption = targetRoomSelect.options[targetRoomSelect.selectedIndex];
            const diff = (parseFloat(selectedOption.getAttribute("data-price")) - oldPrice) * numRooms;
            sumCostDiffChange.textContent = (diff >= 0 ? "+" : "") + diff.toLocaleString('vi-VN') + " đ";
        });

    updateUIByBookingStatus();
});