document.addEventListener("DOMContentLoaded", function () {
    const roomCards = document.querySelectorAll(".room-card");
    const guestInput = document.getElementById("currentRoomGuests");
    const container = document.getElementById("guestFieldsContainer");

    // Hàm tự sinh ô nhập khách chuẩn theo mảng gửi về doPost
    function generateGuestFields(count) {
        if (!container) return;
        container.innerHTML = ""; 

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

    // Lắng nghe co giãn số lượng khách nhập
    if (guestInput) {
        generateGuestFields(parseInt(guestInput.value) || 1);

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
            generateGuestFields(count);
        });
    }

    // Xử lý sự kiện click phòng rạch ròi 2 chế độ
    roomCards.forEach(card => {
        card.addEventListener("click", function (e) {
            if (e.target.closest("form")) return;

            const radio = card.querySelector(".select-radio");
            const detailContainer = document.getElementById("overviewRoomDetailContainer");
            const placeholder = document.getElementById("overviewPlaceholder");
            const detailContent = document.getElementById("overviewDetailContent");
            const rightTitle = document.getElementById("rightPanelTitle");
            const assignFormContainer = document.getElementById("assignRoomFormContainer");

            // LUỒNG 1: ĐANG GÁN PHÒNG -> Click phòng trống thì tích chọn radio
            if (radio && card.classList.contains("status-available")) {
                document.querySelectorAll(".room-card").forEach(c => c.classList.remove("selected-active"));
                card.classList.add("selected-active");
                radio.checked = true;

                const formPlaceholder = document.getElementById("placeholderFormText");
                if (formPlaceholder) formPlaceholder.style.display = "none";
                if (guestInput) generateGuestFields(guestInput.value);
                return;
            }

            // LUỒNG 2: XEM TỔNG QUAN (Hoặc click phòng có khách) -> Hiện bảng thông tin khách
            if (detailContent && placeholder) {
                document.querySelectorAll(".room-card").forEach(c => c.classList.remove("selected-active"));
                card.classList.add("selected-active");

                if (detailContainer) detailContainer.style.display = "block";
                if (assignFormContainer) assignFormContainer.style.display = "none";

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
                    document.getElementById("viewBookingCodeRow").style.display = "";
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
                    document.getElementById("viewBookingCodeRow").style.display = "none";
                    document.getElementById("viewGuestsTableContainer").style.display = "none";
                }

                placeholder.style.display = "none";
                detailContent.style.display = "block";
            }
        });
    });

    // Thông báo SweetAlert2
    const hiddenStatusEl = document.getElementById("serverStatus");
    if (hiddenStatusEl && hiddenStatusEl.value) {
        const status = hiddenStatusEl.value;
        let config = { background: '#F9F5EB', color: '#1a446c', confirmButtonColor: '#1a446c', confirmButtonText: 'Xác nhận', timerProgressBar: true };
        if (status === 'success') {
            config.title = 'Thành Công!'; config.text = 'Gán phòng thành công!'; config.icon = 'success'; config.timer = 3500;
        } else if (status === 'error') {
            config.title = 'Thất Bại!'; config.text = 'Gán phòng xảy ra sự cố, vui lòng thử lại!'; config.icon = 'error'; config.confirmButtonText = 'Tôi đã hiểu';
        }
        Swal.fire(config);
    }
});