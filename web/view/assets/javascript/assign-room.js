function confirmUnassign() {
    if (confirm("Bạn có chắc chắn?\n\nDữ liệu khách lưu trú ở phòng này sẽ bị xóa và phòng sẽ được trả về trạng thái Trống!")) {
        document.getElementById("unassignRoomForm").submit();
    }
}

document.addEventListener("DOMContentLoaded", function () {
    const roomCards = document.querySelectorAll(".room-card");
    const container = document.getElementById("guestFieldsContainer");
    const mainForm = document.getElementById("assignRoomMainForm");
    
    // Nhận diện các ô nhập phân tách đối tượng và nút bấm
    const adultInput = document.getElementById("currentRoomAdults");
    const childInput = document.getElementById("currentRoomChildren");
    const capacityDisplay = document.getElementById("capacityDisplay");
    const submitBtnWrapper = document.getElementById("submitAssignBtnWrapper");

    // Hàm tự sinh ô nhập khách chuẩn
    function generateGuestFields() {
        if (!container) return;

        const placeholder = document.getElementById("placeholderFormText");
        if (placeholder) {
            placeholder.style.display = "none";
        }

        const oldGroups = container.querySelectorAll(".guest-profile-group");
        oldGroups.forEach(g => g.remove());

        // Đọc giá trị số lượng hiện tại từ giao diện
        let adults = parseInt(adultInput ? adultInput.value : 1) || 0;
        let children = parseInt(childInput ? childInput.value : 0) || 0;
        let index = 1;

        // 1. Sinh biểu mẫu nhập thông tin cho Người lớn
        for (let i = 1; i <= adults; i++) {
            createGuestBlock(index++, `Người lớn ${i}`, true);
        }

        // 2. Sinh biểu mẫu nhập thông tin cho Trẻ em
        for (let i = 1; i <= children; i++) {
            createGuestBlock(index++, `Trẻ em ${i}`, false);
        }
    }

    // Hàm phụ trợ sinh cấu trúc HTML cho từng khách hàng
    function createGuestBlock(globalIndex, label, isAdult) {
        const group = document.createElement("div");
        group.className = "guest-profile-group";
        group.setAttribute("data-type", isAdult ? "adult" : "child");

        group.innerHTML = `
            <h5 class="profile-group-header">Khách lưu trú số ${globalIndex} (${label})</h5>
            <div class="form-field-block">
                <label class="field-label">Họ và tên</label>
                <input type="text" name="stayFullName" placeholder="Nhập họ và tên..." class="field-input-text" />
            </div>
            <div class="form-field-row">
                <div class="field-col">
                    <label class="field-label">Số điện thoại</label>
                    <input type="text" name="stayPhone" placeholder="Nhập số điện thoại..." class="field-input-text" />
                </div>
                <div class="field-col">
                    <label class="field-label">Số CCCD / Hộ chiếu</label>
                    <input type="text" name="stayIdNumber" placeholder="Nhập số CCCD/HC..." class="field-input-text" />
                </div>
            </div>
        `;
        container.appendChild(group);
    }

    // Hàm lắng nghe xử lý thay đổi số lượng và ràng buộc hạn mức (Validation)
    function handleCapacityValidation() {
        if (!capacityDisplay) return;
        
        const maxAdults = parseInt(capacityDisplay.getAttribute("data-max-adults")) || 2;
        const maxChildren = parseInt(capacityDisplay.getAttribute("data-max-children")) || 1;

        let adults = parseInt(adultInput.value) || 0;
        let children = parseInt(childInput.value) || 0;

        if (adults > maxAdults) {
            Swal.fire("Vượt quá sức chứa", `Hạng phòng này tối đa chỉ được ${maxAdults} người lớn!`, "warning");
            adultInput.value = maxAdults;
            adults = maxAdults;
        }
        if (adults < 1) {
            adultInput.value = 1;
            adults = 1;
        }

        if (children > maxChildren) {
            Swal.fire("Vượt quá sức chứa", `Hạng phòng này tối đa chỉ được ${maxChildren} trẻ em!`, "warning");
            childInput.value = maxChildren;
            children = maxChildren;
        }
        if (children < 0) {
            childInput.value = 0;
            children = 0;
        }

        const placeholder = document.getElementById("placeholderFormText");
        if (!placeholder || placeholder.style.display === "none") {
            generateGuestFields();
        }
    }

    if (adultInput) adultInput.addEventListener("input", handleCapacityValidation);
    if (childInput) childInput.addEventListener("input", handleCapacityValidation);

    // Xử lý chặn và Validate định dạng chuỗi dữ liệu đầu vào khi bấm SUBMIT
    if (mainForm) {
        mainForm.addEventListener("submit", function (e) {
            const guestGroups = container.querySelectorAll(".guest-profile-group");
            
            const phoneRegex = /^(03|05|07|08|09)\d{8}$/; 
            const idRegex = /^(\d{9}|\d{12})$/;          

            for (let i = 0; i < guestGroups.length; i++) {
                const group = guestGroups[i];
                
                const fullNameInput = group.querySelector("input[name='stayFullName']");
                const phoneInput = group.querySelector("input[name='stayPhone']");
                const idInput = group.querySelector("input[name='stayIdNumber']");

                const fullName = fullNameInput.value.trim();
                const phone = phoneInput.value.trim();
                const idNumber = idInput.value.trim();

                // Bỏ qua kiểm tra nếu hàng này trống trơn hoàn toàn (Cho phép gửi form trống)
                if (!fullName && !phone && !idNumber) {
                    continue; 
                }

                // Nếu có nhập Số điện thoại -> Bắt buộc check đúng định dạng Regex
                if (phone && !phoneRegex.test(phone)) {
                    e.preventDefault();
                    Swal.fire("Sai định dạng SĐT", `Số điện thoại của Khách lưu trú số ${i + 1} không đúng định dạng VN!`, "error");
                    phoneInput.focus();
                    return;
                }

                // Nếu có nhập Số CCCD/Hộ chiếu -> Bắt buộc check đúng định dạng Regex
                if (idNumber && !idRegex.test(idNumber)) {
                    e.preventDefault();
                    Swal.fire("Sai định dạng CCCD", `Số CCCD/Hộ chiếu của Khách lưu trú số ${i + 1} phải chứa đúng 9 hoặc 12 chữ số!`, "error");
                    idInput.focus();
                    return;
                }
            }
        });
    }

    // Xử lý sự kiện CLICK chọn thẻ phòng từ Sơ đồ lưới ma trận
    roomCards.forEach(card => {
        card.addEventListener("click", function (e) {
            const radio = card.querySelector(".select-radio");
            const assignFormContainer = document.getElementById("assignRoomFormContainer");
            const detailContainer = document.getElementById("overviewRoomDetailContainer");
            const placeholder = document.getElementById("overviewPlaceholder");
            const detailContent = document.getElementById("overviewDetailContent");
            const rightTitle = document.getElementById("rightPanelTitle");

            const roomId = card.getAttribute("data-view-id");
            const roomNo = card.getAttribute("data-view-number");
            const status = card.getAttribute("data-view-status");
            const roomBookingCode = card.getAttribute("data-view-code");

            const currentBookingCodeInput = document.getElementById("currentAssigningBookingCode");
            const currentBookingCode = currentBookingCodeInput ? currentBookingCodeInput.value : null;

            if (assignFormContainer) {
                if (radio && card.classList.contains("status-available")) {
                    document.querySelectorAll(".room-card").forEach(c => c.classList.remove("selected-active"));
                    card.classList.add("selected-active");
                    radio.checked = true;

                    assignFormContainer.style.display = "block";
                    if(submitBtnWrapper) submitBtnWrapper.style.display = "block"; 
                    if(detailContainer) detailContainer.style.display = "none";

                    generateGuestFields(); 
                    return; 
                }
                
                if (card.classList.contains("status-occupied") && currentBookingCode && roomBookingCode === currentBookingCode) {
                    assignFormContainer.style.display = "none";
                    if(submitBtnWrapper) submitBtnWrapper.style.display = "none"; 
                    if(detailContainer) detailContainer.style.display = "block";
                    
                    const unassignForm = document.getElementById("unassignRoomForm");
                    const unassignInput = document.getElementById("unassignRoomId");
                    if (unassignForm && unassignInput) {
                        unassignInput.value = roomId;
                        unassignForm.style.display = "block";
                    }
                } else {
                    if(submitBtnWrapper) submitBtnWrapper.style.display = "none"; 
                    return; 
                }
            }

            if (detailContent && placeholder) {
                document.querySelectorAll(".room-card").forEach(c => c.classList.remove("selected-active"));
                card.classList.add("selected-active");

                if (detailContainer && !assignFormContainer) {
                    detailContainer.style.display = "block";
                }

                if (rightTitle) rightTitle.textContent = "CHI TIẾT PHÒNG " + roomNo;

                document.getElementById("viewRoomNumber").textContent = roomNo;
                document.getElementById("viewRoomType").textContent = card.getAttribute("data-view-type");
                document.getElementById("viewRoomStatus").textContent = status;

                const guestsStr = card.getAttribute("data-view-guests");
                const phoneStr = card.getAttribute("data-view-phone");
                const idNumberStr = card.getAttribute("data-view-guest-id");

                if (card.classList.contains("status-occupied")) {
                    const codeRow = document.getElementById("viewBookingCodeRow");
                    if (codeRow) codeRow.style.display = "";
                    document.getElementById("viewBookingCode").textContent = roomBookingCode;

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
            Swal.fire("Thành công", "Thao tác đã được lưu vào hệ thống.", "success");
        } else if (status === 'error') {
            Swal.fire("Thất bại", "Có lỗi xảy ra, vui lòng thử lại sau.", "error");
        }
    }
});

function switchAssignRoomType(bookingId, newRoomTypeId) {
    const contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1));
    let targetUrl = contextPath + "/assign-room?bookingId=" + bookingId + "&overriddenRoomTypeId=" + newRoomTypeId;
    window.location.href = targetUrl;
}