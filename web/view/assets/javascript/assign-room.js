
function confirmUnassign() {
    if (confirm("Bạn có chắc chắn?\n\nDữ liệu khách lưu trú ở phòng này sẽ bị xóa và phòng sẽ được trả về trạng thái Trống!")) {
        document.getElementById("unassignRoomForm").submit();
    }
}

document.addEventListener("DOMContentLoaded", function () {
    const roomCards = document.querySelectorAll(".room-card");
    const guestInput = document.getElementById("currentRoomGuests");
    const container = document.getElementById("guestFieldsContainer");

    // Hàm tự sinh ô nhập khách chuẩn 
    function generateGuestFields(count) {
        if (!container) return;

        const placeholder = document.getElementById("placeholderFormText");
        if (placeholder) {
            placeholder.style.display = "none";
        }

        const oldGroups = container.querySelectorAll(".guest-profile-group");
        oldGroups.forEach(g => g.remove());

        count = parseInt(count) || 1;

        for (let i = 1; i <= count; i++) {
            const group = document.createElement("div");
            group.className = "guest-profile-group";
            group.innerHTML = `
                <h5 class="profile-group-header">Khách lưu trú số ${i}</h5>
                <div class="form-field-block">
                    <label class="field-label">Họ và tên *</label>
                    <input type="text" name="stayFullName" required placeholder="Nhập họ và tên..." class="field-input-text" />
                </div>
                <div class="form-field-row">
                    <div class="field-col">
                        <label class="field-label">Số điện thoại</label>
                        <input type="text" name="stayPhone" placeholder="Không bắt buộc..." class="field-input-text" />
                    </div>
                    <div class="field-col">
                        <label class="field-label">Số CCCD / Hộ chiếu</label>
                        <input type="text" name="stayIdNumber" placeholder="Bỏ trống nếu là trẻ em..." class="field-input-text" />
                    </div>
                </div>
            `;
            container.appendChild(group);
        }
    }

    if (guestInput) {
        guestInput.addEventListener("input", function () {
            const maxCapacity = parseInt(guestInput.getAttribute("max")) || 2;
            let count = parseInt(guestInput.value) || 1;

            if (count > maxCapacity) {
                alert("Số lượng người vượt quá sức chứa tối đa (" + maxCapacity + " người) của phòng này!");
                guestInput.value = maxCapacity;
                count = maxCapacity;
            }
            if (count < 1) {
                guestInput.value = 1;
                count = 1;
            }
            
            const placeholder = document.getElementById("placeholderFormText");
            if (placeholder && placeholder.style.display === "none") {
                generateGuestFields(count);
            }
        });
    }

    // Xử lý sự kiện CLICK chọn thẻ phòng
    roomCards.forEach(card => {
        card.addEventListener("click", function (e) {
            const radio = card.querySelector(".select-radio");
            const assignFormContainer = document.getElementById("assignRoomFormContainer");
            const detailContainer = document.getElementById("overviewRoomDetailContainer");
            const placeholder = document.getElementById("overviewPlaceholder");
            const detailContent = document.getElementById("overviewDetailContent");
            const rightTitle = document.getElementById("rightPanelTitle");


            if (assignFormContainer) {
                
                // Trạng thái 1: Click vào "Phòng trống" -> Mở Form điền tên khách
                if (radio && card.classList.contains("status-available")) {
                    document.querySelectorAll(".room-card").forEach(c => c.classList.remove("selected-active"));
                    card.classList.add("selected-active");
                    radio.checked = true;

                    assignFormContainer.style.display = "block";
                    if(detailContainer) detailContainer.style.display = "none";

                    generateGuestFields(guestInput ? guestInput.value : 1);
                    return; 
                }
                
                // Trạng thái 2: Click vào "Phòng có khách" thuộc CHÍNH ĐƠN NÀY -> Mở chức năng HỦY GÁN
                const currentBookingCodeInput = document.getElementById("currentAssigningBookingCode");
                const currentBookingCode = currentBookingCodeInput ? currentBookingCodeInput.value : null;
                const roomBookingCode = card.getAttribute("data-view-code");
                
                console.log("Mã đơn đang thao tác:", currentBookingCode);
                console.log("Mã đơn của thẻ phòng:", roomBookingCode);
                console.log("Phòng có màu xám (occupied) không:", card.classList.contains("status-occupied"));
                
                if (card.classList.contains("status-occupied") && currentBookingCode && roomBookingCode === currentBookingCode) {
                    // Ẩn Form nhập khách, Bật bảng Chi tiết lên
                    assignFormContainer.style.display = "none";
                    if(detailContainer) detailContainer.style.display = "block";
                    
                    // Nạp số phòng vào Form Hủy Gán và cho nút đỏ hiện lên
                    const unassignForm = document.getElementById("unassignRoomForm");
                    const unassignInput = document.getElementById("unassignRoomNumber");
                    if (unassignForm && unassignInput) {
                        unassignInput.value = card.getAttribute("data-view-number");
                        unassignForm.style.display = "block";
                    }
                    // KHÔNG return ở đây để chạy tiếp xuống Luồng 2 (Đổ dữ liệu)
                } else {
                    return; // Click phòng khác -> Chặn
                }
            }

   
            if (detailContent && placeholder) {
                document.querySelectorAll(".room-card").forEach(c => c.classList.remove("selected-active"));
                card.classList.add("selected-active");

                if (detailContainer && !assignFormContainer) {
                    detailContainer.style.display = "block";
                }

                const roomNo = card.getAttribute("data-view-number");
                if (rightTitle) rightTitle.textContent = "CHI TIẾT PHÒNG " + roomNo;

                document.getElementById("viewRoomNumber").textContent = roomNo;
                document.getElementById("viewRoomType").textContent = card.getAttribute("data-view-type");
                document.getElementById("viewRoomStatus").textContent = card.getAttribute("data-view-status");

                const code = card.getAttribute("data-view-code");
                const guestsStr = card.getAttribute("data-view-guests");
                const phoneStr = card.getAttribute("data-view-phone");
                const idNumberStr = card.getAttribute("data-view-id");

                if (card.classList.contains("status-occupied")) {
                    const codeRow = document.getElementById("viewBookingCodeRow");
                    if (codeRow) codeRow.style.display = "";
                    document.getElementById("viewBookingCode").textContent = code;

                    const tableContainer = document.getElementById("viewGuestsTableContainer");
                    const tableBody = document.getElementById("viewGuestsTableBody");

                    if (tableContainer && tableBody && guestsStr && guestsStr !== "--") {
                        tableBody.innerHTML = ""; 
                        
                        const guestsArray = guestsStr.split(", ");
                        const phoneArray = phoneStr ? phoneStr.split(", ") : [];
                        const idArray = idNumberStr ? idNumberStr.split(", ") : [];

                        for (let i = 0; i < guestsArray.length; i++) {
                            const row = document.createElement("tr");
                            row.style.borderBottom = "1px solid #ebd9b4";
                            row.innerHTML = `
                                <td style="padding: 10px 5px; color: #1a446c; font-weight: bold;">${i + 1}</td>
                                <td style="padding: 10px 5px; font-weight: 600; color: #bfa15f;">${guestsArray[i] || "--"}</td>
                                <td style="padding: 10px 5px; color: #334155;">${phoneArray[i] || "Không có"}</td>
                                <td style="padding: 10px 5px; color: #334155;">${idArray[i] || "Không có"}</td>
                            `;
                            tableBody.appendChild(row);
                        }
                        tableContainer.style.display = "block"; 
                    }
                } else {
                    const codeRow = document.getElementById("viewBookingCodeRow");
                    if (codeRow) codeRow.style.display = "none";
                    const guestTable = document.getElementById("viewGuestsTableContainer");
                    if(guestTable) guestTable.style.display = "none";
                    
                    const unassignForm = document.getElementById("unassignRoomForm");
                    if(unassignForm) unassignForm.style.display = "none";
                }

                placeholder.style.display = "none";
                detailContent.style.display = "block";
            }
        });
    });

   const hiddenStatusEl = document.getElementById("serverStatus");
    if (hiddenStatusEl && hiddenStatusEl.value) {
        const status = hiddenStatusEl.value;
        if (status === 'success') {
            alert("Thành Công! Thao tác đã được lưu.");
        } else if (status === 'error') {
            alert("Thất Bại! Có lỗi xảy ra, vui lòng thử lại.");
        }
    }
});