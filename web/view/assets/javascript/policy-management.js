document.addEventListener("DOMContentLoaded", function () {
    const modal = document.getElementById("policy-modal");
    const btnCreate = document.getElementById("btn-create");
    const btnClose = document.getElementById("btn-close");
    const form = document.getElementById("policy-form");
    const modalTitle = document.getElementById("modal-title");

    const isEditMode = document.body.getAttribute("data-edit-mode") === "true";
    const isCreateMode = document.body.getAttribute("data-create-mode") === "true";

    function getFilterParams() {
        const urlParams = new URLSearchParams(window.location.search);
        const page = urlParams.get("page") || "1";
        const keyword = urlParams.get("keyword") || "";
        const filterType = urlParams.get("filterType") || "";
        return `page=${page}&keyword=${encodeURIComponent(keyword)}&filterType=${encodeURIComponent(filterType)}`;
    }

    function toggleModal(show) {
        if (!modal)
            return;

        if (show) {
            modal.classList.add("active");
            modal.style.display = "flex";
        } else {
            modal.classList.remove("active");
            modal.style.display = "none";
            if (form) {
                form.reset();
            }
            if (isEditMode || isCreateMode) {
                window.location.href = "PolicyList?" + getFilterParams();
            }
        }
    }

    if (btnCreate) {
        btnCreate.addEventListener("click", () => {
            modalTitle.innerText = "Thêm chính sách mới";
            if (form) {
                form.action = "PolicyCreate";
                if (!isCreateMode) {
                    form.reset();
                    const inputs = form.querySelectorAll(
                            "input:not([type='hidden']):not([type='checkbox'])"
                            );
                    inputs.forEach(input => input.value = "");
                    const textarea = form.querySelector("#description");
                    if (textarea)
                        textarea.value = "";
                    const select = document.getElementById("policyType");
                    if (select)
                        select.value = "";
                    const activeCheckbox = document.getElementById("active");
                    if (activeCheckbox) {
                        activeCheckbox.checked = true;
                        activeCheckbox.value = "true";
                    }
                }
            }
            toggleModal(true);
        });
    }

    if (isEditMode) {
        modalTitle.innerText = "Chỉnh sửa chính sách";
        if (form) {
            form.action = "PolicyEdit";
        }
        toggleModal(true);
    } else if (isCreateMode) {
        modalTitle.innerText = "Thêm chính sách mới";
        if (form) {
            form.action = "PolicyCreate";
        }
        toggleModal(true);
    }

    if (btnClose) {
        btnClose.addEventListener("click", () => {
            const isCurrentlyCreate = form && form.action.includes("PolicyCreate");
            if (isCurrentlyCreate && form) {
                form.reset();
                const inputs = form.querySelectorAll(
                        "input:not([type='hidden']):not([type='checkbox']):not([type='radio'])"
                        );
                inputs.forEach(input => input.value = "");
                const textarea = form.querySelector("textarea");
                if (textarea)
                    textarea.value = "";
                const select = form.querySelector("#policyType");
                if (select)
                    select.value = "";
            }
            toggleModal(false);
            const cleanUrl = "PolicyList?" + getFilterParams();
            window.history.replaceState(null, "", cleanUrl);
        });
    }
});