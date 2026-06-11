document.addEventListener("DOMContentLoaded", function () {
    const detailModal = document.getElementById("detail-modal");
    const editModal = document.getElementById("edit-modal");
    const btnCloseDetail = document.getElementById("btn-close-detail");
    const btnCloseEdit = document.getElementById("btn-close-edit");
    const btnCloseCreate = document.getElementById("btn-close-create");
    const form = document.getElementById("staff-form");
    const createModal = document.getElementById("create-modal");
    const btnCreate = document.getElementById("btn-create");
    const isEditMode = document.body.getAttribute("data-edit-mode") === "true";
    const isDetailMode = document.body.getAttribute("data-detail-mode") === "true";

    const alerts = document.querySelectorAll(".alert-message, .error-message, .success-message");

    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.opacity = "0";
            alert.style.transform = "translateY(-10px)";
            alert.style.transition = "opacity 0.3s ease, transform 0.3s ease";

            setTimeout(() => {
                alert.remove();
            }, 300);
        }, 3000);
    });

    function toggleModal(modal, show) {
        if (show) {
            modal.classList.add("show");
        } else {
            modal.classList.remove("show");
        }
    }

    if (isDetailMode && detailModal) {
        toggleModal(detailModal, true);
    }

    if (isEditMode && editModal) {
        toggleModal(editModal, true);
    }

    if (btnCloseDetail) {
        btnCloseDetail.addEventListener("click", function () {
            toggleModal(detailModal, false);
            window.location.href = "StaffAccountList";
        });
    }

    if (btnCloseEdit) {
        btnCloseEdit.addEventListener("click", function () {
            toggleModal(editModal, false);
            window.location.href = "StaffAccountList";
        });
    }

if (btnCloseCreate) {
    btnCloseCreate.addEventListener("click", () => {
        toggleModal(createModal, false);
        window.location.href = "StaffAccountList";
    });
}

    if (btnCreate) {
        btnCreate.addEventListener("click", () => {
            form.action = "StaffAccountCreate";
            form.reset();
            toggleModal(createModal, true);
        });
    }
    
   
});