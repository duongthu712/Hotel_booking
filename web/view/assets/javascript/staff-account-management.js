document.addEventListener("DOMContentLoaded", function () {
    const detailModal = document.getElementById("detail-modal");
    const editModal = document.getElementById("edit-modal");
    const createModal = document.getElementById("create-modal");

    const btnCloseDetail = document.getElementById("btn-close-detail");
    const btnCloseEdit = document.getElementById("btn-close-edit");
    const btnCloseCreate = document.getElementById("btn-close-create");
    const btnCreate = document.getElementById("btn-create");
    const form = document.getElementById("staff-form");

    const isEditMode = document.body.getAttribute("data-edit-mode") !== "" && document.body.getAttribute("data-edit-mode") !== "false";
    const isDetailMode = document.body.getAttribute("data-detail-mode") !== "" && document.body.getAttribute("data-detail-mode") !== "false";
    const isCreateMode = document.body.getAttribute("data-create-mode") === "true";

    function getFilterParams() {
        const urlParams = new URLSearchParams(window.location.search);
        const page = urlParams.get("page") || "1";
        const searchText = urlParams.get("searchText") || "";
        const roleFilter = urlParams.get("roleFilter") || "ALL";
        return `page=${page}&searchText=${encodeURIComponent(searchText)}&roleFilter=${encodeURIComponent(roleFilter)}`;
    }

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
        if (!modal)
            return;
        if (show) {
            modal.classList.add("show");
            modal.style.display = "flex";
        } else {
            modal.classList.remove("show");
            modal.style.display = "none";
        }
    }

    if (isDetailMode && detailModal) {
        toggleModal(detailModal, true);
    }

    if (isEditMode && editModal) {
        toggleModal(editModal, true);
    }

    if (isCreateMode && createModal) {
        toggleModal(createModal, true);
    }

    if (btnCloseDetail) {
        btnCloseDetail.addEventListener("click", function () {
            toggleModal(detailModal, false);
            window.location.href = "StaffAccountList?" + getFilterParams();
        });
    }

    if (btnCloseEdit) {
        btnCloseEdit.addEventListener("click", function () {

            const form = document.getElementById("edit-form");
            if (form) {
                form.reset();
            }

            toggleModal(editModal, false);

            window.history.replaceState(null, "", "StaffAccountList?" + getFilterParams());
        });
    }

    if (btnCloseCreate) {
        btnCloseCreate.addEventListener("click", function () {
            const form = document.getElementById("create-form");
            if (form) {
                form.reset();
            }

            toggleModal(createModal, false);
        });
    }

    if (btnCreate) {
        btnCreate.addEventListener("click", (e) => {
            e.preventDefault();
            if (form) {
                form.action = "StaffAccountCreate";
                if (!isCreateMode) {
                    form.reset();
                    const inputs = form.querySelectorAll("input:not([type='hidden'])");
                    inputs.forEach(input => input.value = "");
                    const selects = form.querySelectorAll("select");
                    selects.forEach(select => select.selectedIndex = 0);
                }
            }
            toggleModal(createModal, true);
        });
    }
});