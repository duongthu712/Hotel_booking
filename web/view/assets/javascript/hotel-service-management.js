document.addEventListener("DOMContentLoaded", function () {
    const modal = document.getElementById("service-modal");
    const btnCreate = document.getElementById("btn-create");
    const btnClose = document.getElementById("btn-close");
    const form = document.getElementById("service-form");
    const modalTitle = document.getElementById("modal-title");

    const alerts = document.querySelectorAll(".alert-message");
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

    const isEditMode = document.body.getAttribute("data-edit-mode") === "true";
    const isCreateMode = document.body.getAttribute("data-create-mode") === "true";

    function getFilterParams() {
        const urlParams = new URLSearchParams(window.location.search);
        const page = urlParams.get("page") || "1";
        const keyword = urlParams.get("keyword") || "";
        return `page=${page}&keyword=${encodeURIComponent(keyword)}`;
    }

    function toggleModal(show) {
        if (!modal) return;
        
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
                window.location.href = "HotelServiceList?" + getFilterParams();
            }
        }
    }

    if (btnCreate) {
        btnCreate.addEventListener("click", () => {
            modalTitle.innerText = "Thêm dịch vụ khách sạn mới";
            if (form) {
                form.action = "HotelServiceCreate";
                if (!isCreateMode) {
                    form.reset();
                    const inputs = form.querySelectorAll("input:not([type='hidden'])");
                    inputs.forEach(input => input.value = "");
                    
                    const textarea = form.querySelector("#description");
                    if (textarea) textarea.value = "";
                }
            }
            toggleModal(true);
        });
    }

    if (isEditMode) {
        modalTitle.innerText = "Chỉnh sửa dịch vụ khách sạn";
        if (form) {
            form.action = "HotelServiceEdit";
        }
        toggleModal(true);
    } else if (isCreateMode) {
        modalTitle.innerText = "Thêm dịch vụ khách sạn mới";
        if (form) {
            form.action = "HotelServiceCreate";
        }
        toggleModal(true);
    }

    if (btnClose) {
        btnClose.addEventListener("click", () => toggleModal(false));
    }
});