document.addEventListener("DOMContentLoaded", function () {
    const radios = document.querySelectorAll("input[name='requestType']");
    const sumRequestType = document.getElementById("sumRequestType");
    const sumCostDiffChange = document.getElementById("sumCostDiff");
    const sumCostDiffExtend = document.getElementById("sumCostDiffExtend");
    const tabChangeRoom = document.getElementById("tab-change-room");
    const tabExtendStay = document.getElementById("tab-extend-stay");
    const tabCancelBooking = document.getElementById("tab-cancel-booking");
    const requestForm = document.getElementById("requestForm");
    
    if (!requestForm) return;

    const btnSubmit = document.querySelector(".btn-submit-request");
    const oldCheckoutStr = requestForm.querySelector("input[name='oldCheckoutDate']").value;
    const checkInStr = requestForm.querySelector("input[name='checkInDate']").value;
    const numRooms = parseInt(requestForm.querySelector("input[name='numRooms']").value) || 1;
    const oldPrice = parseFloat(document.getElementById("oldBasePrice").value) || 0;
    const checkOutDateInput = requestForm.querySelector("input[name='checkOutDate']");
    
    const reasonChange = document.getElementById("reasonDetailsChange");
    const reasonExtend = document.getElementById("reasonDetailsExtend");
    const reasonCancel = document.getElementById("reasonDetailsCancel");
    const chkPolicyAgree = document.getElementById("chkPolicyAgree");
    const targetRoomSelect = document.getElementById("targetRoomTypeId");
    
    window.hasUserInteracted = false; 

    function switchTab(activeTab) {
        if (sumRequestType) sumRequestType.textContent = activeTab;
        if (tabChangeRoom) tabChangeRoom.style.display = (activeTab === "Đổi hạng phòng") ? "block" : "none";
        if (tabExtendStay) tabExtendStay.style.display = (activeTab === "Gia hạn phòng") ? "block" : "none";
        if (tabCancelBooking) tabCancelBooking.style.display = (activeTab === "Hủy đặt phòng") ? "block" : "none";
    }

    radios.forEach(radio => {
        radio.addEventListener("change", function () {
            window.hasUserInteracted = true; 
            switchTab(this.value);
        });
    });

    // Hàm kiểm tra logic trước khi submit
    function validateLogic() {
        const activeTab = document.querySelector("input[name='requestType']:checked").value;

        if (activeTab === "Đổi hạng phòng") {
            if (targetRoomSelect.value === requestForm.querySelector("input[name='roomTypeId']").value) 
                return "Hạng phòng mong muốn đang trùng với hạng phòng hiện tại.";
            if (!reasonChange.value.trim()) return "Vui lòng nhập lý do thay đổi hạng phòng.";
        } 
        else if (activeTab === "Gia hạn phòng") {
            const newDate = new Date(document.getElementById("newCheckoutDate").value);
            const oldDate = new Date(oldCheckoutStr);
            if (!document.getElementById("newCheckoutDate").value) return "Vui lòng chọn ngày trả phòng mới.";
            if (newDate <= oldDate) return "Ngày trả phòng mới phải sau ngày trả phòng hiện tại (" + oldCheckoutStr + ").";
            if (!reasonExtend.value.trim()) return "Vui lòng nhập lý do gia hạn thời gian ở.";
        } 
        else if (activeTab === "Hủy đặt phòng") {
            if (!reasonCancel.value.trim()) return "Vui lòng nhập lý do hủy đặt phòng.";
            if (!chkPolicyAgree.checked) return "Bạn cần đồng ý với chính sách hủy phòng.";
        }
        return null;
    }

    // Sự kiện Gửi
    btnSubmit.addEventListener("click", function (e) {
        e.preventDefault();
        
        const error = validateLogic();
        if (error) {
            Swal.fire({ title: 'Thông tin chưa hợp lệ', text: error, icon: 'warning', confirmButtonColor: '#2c3e46' });
            return;
        }

        Swal.fire({
            title: 'Xác nhận gửi yêu cầu?',
            icon: 'question',
            showCancelButton: true,
            confirmButtonColor: '#2c3e46',
            confirmButtonText: 'ĐỒNG Ý GỬI'
        }).then((result) => {
            if (result.isConfirmed) HTMLFormElement.prototype.submit.call(requestForm);
        });
    });

    // Tính toán phụ
    if (targetRoomSelect) targetRoomSelect.addEventListener("change", calculateRoomChangeDiff);
    function calculateRoomChangeDiff() {
        const selectedOption = targetRoomSelect.options[targetRoomSelect.selectedIndex];
        const diff = (parseFloat(selectedOption.getAttribute("data-price")) - oldPrice) * numRooms;
        sumCostDiffChange.textContent = (diff >= 0 ? "+" : "") + diff.toLocaleString('vi-VN') + " đ";
    }

    // Khởi tạo
    const checkedRadio = requestForm.querySelector("input[name='requestType']:checked");
    switchTab(checkedRadio ? checkedRadio.value : "Đổi hạng phòng");
});