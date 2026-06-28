document.addEventListener("DOMContentLoaded", function () {
    const detailModal = document.getElementById("detail-modal");
    const editModal = document.getElementById("edit-modal");
    const createModal = document.getElementById("create-modal");
    
    const btnCloseDetail = document.getElementById("btn-close-detail");
    const btnCloseEdit = document.getElementById("btn-close-edit");
    const btnCloseCreate = document.getElementById("btn-close-create");
    const btnCreate = document.getElementById("btn-create");
    
    const editForm = document.getElementById("edit-form");

    const isEditMode = document.body.getAttribute("data-edit-mode") === "true";
    const isDetailMode = document.body.getAttribute("data-detail-mode") === "true";
    const isCreateMode = document.body.getAttribute("data-create-mode") === "true";

    const urlParams = new URLSearchParams(window.location.search);
    const page = urlParams.get('page') || '1';
    const roomTypeId = urlParams.get('roomTypeId') || '';
    const keyword = urlParams.get('keyword') || '';

    function buildRoomListUrl() {
        let url = 'RoomList?page=' + page;
        if (roomTypeId) url += '&roomTypeId=' + encodeURIComponent(roomTypeId);
        if (keyword) url += '&keyword=' + encodeURIComponent(keyword);
        return url;
    }

    function toggleModal(modal, show) {
        if (!modal) return;
        if (show) {
            modal.classList.add("active");
        } else {
            modal.classList.remove("active");
        }
    }

    if (isDetailMode && detailModal) {
        toggleModal(detailModal, true);
    }

    if (isEditMode && editModal) {
        toggleModal(editModal, true);
        editForm.action = "RoomEdit";
    }

    if (isCreateMode && createModal) {
        toggleModal(createModal, true);
    }

    if (btnCreate) {
        btnCreate.addEventListener("click", function () {
            toggleModal(createModal, true);
        });
    }

    // Tất cả nút X đều đóng modal và redirect về list
    if (btnCloseDetail) {
        btnCloseDetail.addEventListener("click", function () {
            toggleModal(detailModal, false);
            window.location.href = buildRoomListUrl();
        });
    }

    if (btnCloseEdit) {
        btnCloseEdit.addEventListener("click", function () {
            toggleModal(editModal, false);
            window.location.href = buildRoomListUrl();
        });
    }

    if (btnCloseCreate) {
        btnCloseCreate.addEventListener("click", function () {
            toggleModal(createModal, false);
            window.location.href = buildRoomListUrl();
        });
    }
});