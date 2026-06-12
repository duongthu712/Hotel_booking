document.addEventListener("DOMContentLoaded", function () {
    const modal = document.getElementById("service-modal");
    const btnCreate = document.getElementById("btn-create");
    const btnClose = document.getElementById("btn-close");
    const form = document.getElementById("service-form");
    const modalTitle = document.getElementById("modal-title");

    //Get status from body
    const isEditMode = document.body.getAttribute("data-edit-mode") === "true";

    //Open and close modal
    function toggleModal(show) {
        if (show) {
            modal.classList.add("active");
        } else {
            modal.classList.remove("active");
            form.reset();
            //if in modal edit, enter cancel then back to list services
            if (isEditMode) {
                window.location.href = "ServiceList";
            }
        }
    }

    //Open create modal
    if (btnCreate) {
        btnCreate.addEventListener("click", () => {
            modalTitle.innerText = "Thêm dịch vụ mới";
            form.action = "ServiceCreate";
            form.reset();
            toggleModal(true);
        });
    }

    //Open edit modal
    if (isEditMode) {
        modalTitle.innerText = "Cập nhật dịch vụ";
        form.action = "ServiceEdit";
        toggleModal(true);
    }

    //Close modal
    if (btnClose) {
        btnClose.addEventListener("click", () => toggleModal(false));
    }
});


