document.addEventListener("DOMContentLoaded", function () {
    const detailModal = document.getElementById("detail-modal");
    const editModal = document.getElementById("edit-modal");
    const btnCloseDetail = document.getElementById("btn-close-detail");
    const btnCloseDetail2 = document.getElementById("btn-close-detail2");
    const btnCloseEdit = document.getElementById("btn-close-edit");
    const btnCloseEdit2 = document.getElementById("btn-close-edit2");

    // Get status
    const isEditMode = document.body.getAttribute("data-edit-mode") === "true";
    const isDetailMode = document.body.getAttribute("data-detail-mode") === "true";

    // Open and close modal
    function toggleModal(modal, show) {
        if (show) {
            modal.classList.add("show");
        } else {
            modal.classList.remove("show");
        }
    }

    // Open detail modal
    if (isDetailMode && detailModal) {
        toggleModal(detailModal, true);
    }

    // Open edit modal
    if (isEditMode && editModal) {
        toggleModal(editModal, true);
    }

    // Close detail modal
    if (btnCloseDetail) {
        btnCloseDetail.addEventListener("click", function () {
            toggleModal(detailModal, false);
            window.location.href = "StaffAccountList";
        });
    }

    if (btnCloseDetail2) {
        btnCloseDetail2.addEventListener("click", function () {
            toggleModal(detailModal, false);
            window.location.href = "StaffAccountList";
        });
    }

    // Close edit modal
    if (btnCloseEdit) {
        btnCloseEdit.addEventListener("click", function () {
            toggleModal(editModal, false);
            window.location.href = "StaffAccountList";
        });
    }

    if (btnCloseEdit2) {
        btnCloseEdit2.addEventListener("click", function () {
            toggleModal(editModal, false);
            window.location.href = "StaffAccountList";
        });
    }
});


