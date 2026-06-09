document.addEventListener("DOMContentLoaded", function () {
    const modal = document.getElementById("amenity-modal");
    const btnCreate = document.getElementById("btn-create");
    const btnClose = document.getElementById("btn-close");
    const form = document.getElementById("amenity-form");
    const modalTitle = document.getElementById("modal-title");

    const alerts = document.querySelectorAll(".alert-message");

    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.opacity = "0";
            alert.style.transform = "translateY(-10px)";

            setTimeout(() => {
                alert.remove();
            }, 300);
        }, 3000);
    });

    //Get status from body
    const isEditMode = document.body.getAttribute("data-edit-mode") === "true";

    //Open and close modal
    function toggleModal(show) {
        if (show) {
            modal.classList.add("active");
        } else {
            modal.classList.remove("active");
            form.reset();
            //if in modal edit, enter cancel then back to list amenitys
            if (isEditMode) {
                window.location.href = "RoomAmenityList";
            }
        }
    }

    //Open create modal
    if (btnCreate) {
        btnCreate.addEventListener("click", () => {
            modalTitle.innerText = "Thêm Tiện nghi mới";
            form.action = "RoomAmenityCreate";
            form.reset();
            toggleModal(true);
        });
    }

    //Open edit modal
    if (isEditMode) {
        modalTitle.innerText = "Cập nhật Tiện nghi";
        form.action = "RoomAmenityEdit";
        toggleModal(true);
    }

    //Close modal
    if (btnClose) {
        btnClose.addEventListener("click", () => toggleModal(false));
    }
});


