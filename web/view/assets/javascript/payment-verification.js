document.addEventListener("DOMContentLoaded", function () {

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

    function toggleModal(modal, show) {
        if (!modal) {
            return;
        }

        if (show) {
            modal.classList.add("show");
            modal.style.display = "flex";
        } else {
            modal.classList.remove("show");
            modal.style.display = "none";
        }
    }

    const paymentDetailModal = document.getElementById("payment-detail-modal");
    const btnCloseDetail = document.getElementById("btn-close-detail");
    const proofImg = document.getElementById("proof-img");
    const proofWrapper = document.getElementById("payment-proof-wrapper");
    const proofUrlContainer = document.getElementById("proof-url-container");
    const detailBookingCode = document.getElementById("detail-booking-code");
    const detailGuestName = document.getElementById("detail-guest-name");
    const detailAmount = document.getElementById("detail-amount");
    const detailSubmittedAt = document.getElementById("detail-submitted-at");
    const detailStatus = document.getElementById("detail-status");
    const verifyNotes = document.getElementById("verify-notes");
    const verifyDepositId = document.getElementById("verify-deposit-id");
    const rejectDepositId = document.getElementById("reject-deposit-id");
    const verifyNotesHidden = document.getElementById("verify-notes-hidden");
    const rejectNotes = document.getElementById("reject-notes");
    const verificationForm = document.getElementById("verification-form");

    window.openPaymentDetailModal = function (depositId) {
        const paymentRow = document.querySelector(`tr[data-deposit-id="${depositId}"]`);

        if (!paymentRow) {
            return;
        }

        const bookingCode = paymentRow.querySelector(".col-booking")?.innerText || "";
        const guestName = paymentRow.querySelector(".col-guest")?.innerText || "";
        const amount = paymentRow.querySelector(".col-amount")?.innerText || "";
        const submittedAt = paymentRow.querySelector(".col-date")?.innerText || "";
        const status = paymentRow.querySelector(".payment-status")?.innerText || "";
        const proofUrl = paymentRow.getAttribute("data-proof-url") || "";
        const paymentNotes = paymentRow.getAttribute("data-notes") || "";
        const paymentStatus = paymentRow.querySelector(".payment-status");

        const statusClass = paymentStatus?.classList.contains("status-pending")
                ? "pending"
                : (
                        paymentStatus?.classList.contains("status-approved")
                        ? "approved"
                        : "rejected"
                        );

        if (detailBookingCode) {
            detailBookingCode.innerText = bookingCode;
        }

        if (detailGuestName) {
            detailGuestName.innerText = guestName;
        }

        if (detailAmount) {
            detailAmount.innerText = amount;
        }

        if (detailSubmittedAt) {
            detailSubmittedAt.innerText = submittedAt;
        }

        if (detailStatus) {
            detailStatus.innerText = status;

            detailStatus.className = statusClass === "pending"
                    ? "status-pending"
                    : (statusClass === "approved"
                            ? "status-approved"
                            : "status-rejected"
                            );
        }

        if (proofUrl) {
            if (proofImg) {
                proofImg.src = proofUrl;
            }

            if (proofWrapper) {
                proofWrapper.style.display = "block";
            }
        } else {
            if (proofWrapper) {
                proofWrapper.style.display = "none";
            }
        }

        if (proofUrlContainer) {
            proofUrlContainer.innerHTML = `<p style="word-break: break-word; margin-top:8px;">
            ${paymentNotes && paymentNotes.trim() !== "" ? paymentNotes : "Chưa có mã giao dịch / mã tham chiếu." }
        </p> `;
        }

        if (verifyDepositId) {
            verifyDepositId.value = depositId;
        }

        if (rejectDepositId) {
            rejectDepositId.value = depositId;
        }

        const btnReject = document.querySelector(".btn-reject");

        const btnSubmit = document.querySelector(".popup-action .btn-submit");

        if (statusClass !== "pending") {
            if (verificationForm) {
                verificationForm.style.display = "none";
            }

            if (btnReject) {
                btnReject.style.display = "none";
            }

            if (btnSubmit) {
                btnSubmit.style.display = "none";
            }
        } else {
            if (verificationForm) {
                verificationForm.style.display = "block";
            }

            if (btnReject) {
                btnReject.style.display = "inline-block";
            }

            if (btnSubmit) {
                btnSubmit.style.display = "inline-block";
            }
        }

        toggleModal(paymentDetailModal, true);
    };

    if (btnCloseDetail) {
        btnCloseDetail.addEventListener("click", () => {
            toggleModal(paymentDetailModal, false);
            if (verifyNotes) {
                verifyNotes.value = "";
            }
        });
    }

    window.prepareReject = function () {
        const notes = verifyNotes ? verifyNotes.value : "";

        if (rejectNotes) {
            rejectNotes.value = notes;
        }

        return confirm("Bạn có chắc muốn từ chối khoản thanh toán này?");
    };

    const verifyForm = document.querySelector("form[action='DepositPaymentVerify']");

    if (verifyForm) {
        verifyForm.addEventListener("submit", function () {
            const notes = verifyNotes ? verifyNotes.value : "";

            if (verifyNotesHidden) {
                verifyNotesHidden.value = notes;
            }
        });
    }

    document.querySelectorAll(".hotel-popup").forEach(modal => {
        modal.addEventListener("click", (e) => {
            if (e.target === modal) {
                toggleModal(modal, false);
            }
        });
    });
});