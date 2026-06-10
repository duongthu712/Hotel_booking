document.addEventListener("DOMContentLoaded", function () {
    const detailModal = document.getElementById("detail-modal");
    const editModal = document.getElementById("edit-modal");
    const btnCloseDetail = document.getElementById("btn-close-detail");
    const btnCloseEdit = document.getElementById("btn-close-edit");
    const editForm = document.getElementById("edit-form");

    const alerts = document.querySelectorAll(".alert-message");

    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.opacity = "0";
            alert.style.transform = "translateY(-10px)";
            setTimeout(() => alert.remove(), 300);
        }, 3000);
    });

    // Get status from body
    const isEditMode = document.body.getAttribute("data-edit-mode") === "true";
    const isDetailMode = document.body.getAttribute("data-detail-mode") === "true";

    // Lấy params từ URL hiện tại
    const urlParams = new URLSearchParams(window.location.search);
    const page = urlParams.get('page') || '1';
    const roomTypeId = urlParams.get('roomTypeId') || '';
    const keyword = urlParams.get('keyword') || '';

    // Build redirect URL giữ params
    function buildRoomListUrl() {
        let url = 'RoomList?page=' + page;
        if (roomTypeId) url += '&roomTypeId=' + encodeURIComponent(roomTypeId);
        if (keyword) url += '&keyword=' + encodeURIComponent(keyword);
        return url;
    }

    // Open and close modal
    function toggleModal(modal, show) {
        if (!modal) return;
        if (show) {
            modal.classList.add("active");
        } else {
            modal.classList.remove("active");
        }
    }

    // Open detail modal from server (first load)
    if (isDetailMode && detailModal) {
        toggleModal(detailModal, true);
    }

    // Open edit modal from server (first load)
    if (isEditMode && editModal) {
        toggleModal(editModal, true);
        editForm.action = "RoomEdit";
    }

    // Close detail modal - back to list with params
    if (btnCloseDetail) {
        btnCloseDetail.addEventListener("click", function () {
            toggleModal(detailModal, false);
            window.location.href = buildRoomListUrl();
        });
    }

    // Close edit modal - back to list with params
    if (btnCloseEdit) {
        btnCloseEdit.addEventListener("click", function () {
            toggleModal(editModal, false);
            if (isEditMode) {
                window.location.href = buildRoomListUrl();
            }
        });
    }
});