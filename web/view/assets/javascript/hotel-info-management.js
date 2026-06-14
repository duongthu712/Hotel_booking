document.addEventListener("DOMContentLoaded", function () {
    //Thông báo lỗi
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

//hotel info
    const infoViewModal = document.getElementById("info-view-modal");
    const infoEditModal = document.getElementById("info-edit-modal");
    const btnHotelInfo = document.getElementById("btn-hotel-info");
    const btnCloseInfo = document.getElementById("btn-close-info");
    const btnEditInfo = document.getElementById("btn-edit-info");
    const btnCloseInfoEdit = document.getElementById("btn-close-info-edit");

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

    if (btnHotelInfo) {
        btnHotelInfo.addEventListener("click", () => {
            toggleModal(infoViewModal, true);
        });
    }

    if (btnCloseInfo) {
        btnCloseInfo.addEventListener("click", () => {
            toggleModal(infoViewModal, false);
        });
    }

    if (btnEditInfo) {
        btnEditInfo.addEventListener("click", () => {
            toggleModal(infoViewModal, false);
            toggleModal(infoEditModal, true);
        });
    }

    if (btnCloseInfoEdit) {
        btnCloseInfoEdit.addEventListener("click", () => {
            toggleModal(infoEditModal, false);
            if (document.body.getAttribute("data-info-edit-mode") === "true") {
                window.location.href = "HotelInfo";
            }
        });
    }

    const isInfoEditMode = document.body.getAttribute("data-info-edit-mode") === "true";
    if (isInfoEditMode) {
        toggleModal(infoEditModal, true);
    }

//hotel img
    const imageEditModal = document.getElementById("image-edit-modal");
    const btnCloseImage = document.getElementById("btn-close-image");
    const editImageId = document.getElementById("edit-image-id");
    const editImageUrl = document.getElementById("edit-image-url");

    window.openImageEditModal = function (imageId, imageUrl, label) {
        if (editImageId)
            editImageId.value = imageId;
        if (editImageUrl)
            editImageUrl.value = imageUrl;
        toggleModal(imageEditModal, true);
    };

    if (btnCloseImage) {
        btnCloseImage.addEventListener("click", () => {
            toggleModal(imageEditModal, false);
            if (editImageUrl)
                editImageUrl.value = "";
        });
    }

//hotel news
    const newsDetailModal = document.getElementById("news-detail-modal");
    const btnCloseDetail = document.getElementById("btn-close-detail");
    const btnEditFromDetail = document.getElementById("btn-edit-from-detail");
    const detailNewsTitle = document.getElementById("detail-news-title");
    const detailNewsImg = document.getElementById("detail-news-img");
    const detailNewsImageWrapper = document.getElementById("detail-news-image-wrapper");
    const detailNewsContent = document.getElementById("detail-news-content");
    const detailNewsStatus = document.getElementById("detail-news-status");
    const detailNewsDate = document.getElementById("detail-news-date");
    const content = newsRow.getAttribute("data-full-content") || "";


    let currentDetailNewsId = null;

    window.openNewsDetailModal = function (newsId) {
        const newsRow = document.querySelector(`tr[data-news-id="${newsId}"]`);
        if (!newsRow) {
            window.location.href = `HotelNewsEdit?newsId=${newsId}`;
            return;
        }

        const title = newsRow.querySelector(".col-title a")?.innerText || "";
        const content = newsRow.querySelector(".col-content")?.innerText || "";
        const status = newsRow.querySelector(".news-status")?.innerText || "";
        const statusClass = newsRow.querySelector(".news-status")?.classList.contains("status-active") ? "active" : "inactive";

        const imageUrl = newsRow.getAttribute("data-image-url") || "";

        if (detailNewsTitle)
            detailNewsTitle.innerText = title;
        if (detailNewsContent)
            detailNewsContent.innerText = content;
        if (detailNewsStatus) {
            detailNewsStatus.innerText = status;
            detailNewsStatus.className = statusClass === "active" ? "status-active" : "status-inactive";
        }

        if (imageUrl) {
            if (detailNewsImg)
                detailNewsImg.src = imageUrl;
            if (detailNewsImageWrapper)
                detailNewsImageWrapper.style.display = "block";
        } else {
            if (detailNewsImageWrapper)
                detailNewsImageWrapper.style.display = "none";
        }

        currentDetailNewsId = newsId;
        toggleModal(newsDetailModal, true);
    };

    if (btnCloseDetail) {
        btnCloseDetail.addEventListener("click", () => {
            toggleModal(newsDetailModal, false);
            currentDetailNewsId = null;
        });
    }

    if (btnEditFromDetail) {
        btnEditFromDetail.addEventListener("click", () => {
            if (currentDetailNewsId) {
                window.location.href = `HotelNewsEdit?newsId=${currentDetailNewsId}`;
            }
        });
    }

    const newsCreateModal = document.getElementById("news-create-modal");
    const btnCreateNews = document.getElementById("btn-create-news");
    const btnCloseCreate = document.getElementById("btn-close-create");

    if (btnCreateNews) {
        btnCreateNews.addEventListener("click", () => {
            toggleModal(newsCreateModal, true);
        });
    }

    if (btnCloseCreate) {
        btnCloseCreate.addEventListener("click", () => {
            toggleModal(newsCreateModal, false);
            const isCreateMode = document.body.getAttribute("data-create-mode") === "true";
            if (isCreateMode) {
                window.location.href = "HotelInfo";
            }
        });
    }

    const isCreateMode = document.body.getAttribute("data-create-mode") === "true";
    if (isCreateMode) {
        toggleModal(newsCreateModal, true);
    }

    const newsEditModal = document.getElementById("news-edit-modal");
    const btnCloseEdit = document.getElementById("btn-close-edit");

    if (btnCloseEdit) {
        btnCloseEdit.addEventListener("click", () => {
            toggleModal(newsEditModal, false);
            const isEditMode = document.body.getAttribute("data-edit-mode") === "true";
            if (isEditMode) {
                window.location.href = "HotelInfo";
            }
        });
    }

    const isEditMode = document.body.getAttribute("data-edit-mode") === "true";
    if (isEditMode) {
        toggleModal(newsEditModal, true);
    }

    if (window.location.hash === "#news-section") {
        const newsSection = document.getElementById("news-section");
        if (newsSection) {
            setTimeout(() => {
                newsSection.scrollIntoView({behavior: "smooth", block: "start"});
            }, 200);
        }
    }

    document.querySelectorAll(".hotel-popup").forEach(modal => {
        modal.addEventListener("click", (e) => {
            if (e.target === modal) {
                const isEdit = modal.id === "news-edit-modal" && isEditMode;
                const isCreate = modal.id === "news-create-modal" && isCreateMode;
                const isInfoEdit = modal.id === "info-edit-modal" && isInfoEditMode;

                if (!isEdit && !isCreate && !isInfoEdit) {
                    toggleModal(modal, false);
                }
            }
        });
    });
});