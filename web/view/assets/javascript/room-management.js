document.addEventListener("DOMContentLoaded", function () {
    const detailModal = document.getElementById("detail-modal");
    const editModal = document.getElementById("edit-modal");
    const btnCloseDetail = document.getElementById("btn-close-detail");
    const btnCloseEdit = document.getElementById("btn-close-edit");
    const btnEditFromDetail = document.getElementById("btn-edit-from-detail");

    // Get status from body
    const isEditMode = document.body.getAttribute("data-edit-mode") === "true";
    const isDetailMode = document.body.getAttribute("data-detail-mode") === "true";

    // Open and close modal
    function toggleModal(modal, show) {
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
    }

    // Close detail modal - back to list
    if (btnCloseDetail) {
        btnCloseDetail.addEventListener("click", function () {
            toggleModal(detailModal, false);
            if (isDetailMode) {
                window.location.href = "RoomList";
            }
        });
    }

    // Close edit modal - back to list
    if (btnCloseEdit) {
        btnCloseEdit.addEventListener("click", function () {
            toggleModal(editModal, false);
            if (isEditMode) {
                window.location.href = "RoomList";
            }
        });
    }

    // Click edit in detail modal -> close detail, open edit
    if (btnEditFromDetail) {
        btnEditFromDetail.addEventListener("click", function (e) {
            // Reload page with RoomEdit?roomNumber=xxx
            // After reload, edit modal will open via data-edit-mode
        });
    }
});