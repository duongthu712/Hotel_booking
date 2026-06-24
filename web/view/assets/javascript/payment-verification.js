document.addEventListener("DOMContentLoaded", function () {

    console.log("payment-verification.js NEW VERSION 10");

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

    const paymentDetailModal =
            document.getElementById("payment-detail-modal");

    const btnCloseDetail =
            document.getElementById("btn-close-detail");

    const proofImg =
            document.getElementById("proof-img");

    const proofWrapper =
            document.getElementById("payment-proof-wrapper");

    const proofNotes =
            document.getElementById("proof-notes");

    const detailBookingCode =
            document.getElementById("detail-booking-code");

    const detailGuestName =
            document.getElementById("detail-guest-name");

    const detailAmount =
            document.getElementById("detail-amount");

    const detailSubmittedAt =
            document.getElementById("detail-submitted-at");

    const detailStatus =
            document.getElementById("detail-status");

    const detailVerifiedBy =
            document.getElementById("detail-verified-by");

    const verifyNotes =
            document.getElementById("verify-notes");

    const verifyDepositId =
            document.getElementById("verify-deposit-id");

    const rejectDepositId =
            document.getElementById("reject-deposit-id");

    const verifyNotesHidden =
            document.getElementById("verify-notes-hidden");

    const rejectNotes =
            document.getElementById("reject-notes");

    const verificationForm =
            document.getElementById("verification-form");

    window.openPaymentDetailModal = function (depositId) {
        const paymentRow = document.querySelector(
                `tr[data-deposit-id="${depositId}"]`
                );

        if (!paymentRow) {
            return;
        }

        const bookingCode =
                paymentRow.getAttribute("data-booking-code") || "";

        const guestName =
                paymentRow.getAttribute("data-guest-name") || "";

        const amount =
                paymentRow.querySelector(".col-amount")?.innerText || "";

        const submittedAt =
                paymentRow.querySelector(".col-date")?.innerText || "";

        const status =
                paymentRow.getAttribute("data-status") || "";

        const verifiedBy =
                paymentRow.getAttribute("data-verified-by") || "-";

        const proofUrl =
                paymentRow.getAttribute("data-proof-url") || "";

        const paymentNotes =
                paymentRow.getAttribute("data-notes") || "";

        const paymentStatus =
                paymentRow.querySelector(".payment-status");

        const statusClass =
                paymentStatus?.classList.contains("status-pending")
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

        if (detailVerifiedBy) {
            detailVerifiedBy.innerText = verifiedBy;
        }

        if (detailStatus) {
            detailStatus.innerText =
                    status === "Đã phê duyệt" ? "Đã duyệt" : status;

            detailStatus.className =
                    statusClass === "pending"
                    ? "status-pending"
                    : (
                            statusClass === "approved"
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
            if (proofImg) {
                proofImg.removeAttribute("src");
            }

            if (proofWrapper) {
                proofWrapper.style.display = "none";
            }
        }

        if (proofNotes) {
            proofNotes.innerText =
                    paymentNotes && paymentNotes.trim() !== ""
                    ? paymentNotes
                    : "Chưa có mã giao dịch / mã tham chiếu.";
        }

        if (verifyDepositId) {
            verifyDepositId.value = depositId;
        }

        if (rejectDepositId) {
            rejectDepositId.value = depositId;
        }

        const btnReject =
                document.querySelector(".btn-reject");

        const btnSubmit =
                document.querySelector(".popup-action .btn-submit");

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
        btnCloseDetail.addEventListener("click", function () {
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

    const verifyForm =
            document.querySelector("form[action='DepositPaymentVerify']");

    if (verifyForm) {
        verifyForm.addEventListener("submit", function () {
            const notes = verifyNotes ? verifyNotes.value : "";

            if (verifyNotesHidden) {
                verifyNotesHidden.value = notes;
            }
        });
    }

    document.querySelectorAll(".hotel-popup").forEach(function (modal) {
        modal.addEventListener("click", function (e) {
            if (e.target === modal) {
                toggleModal(modal, false);
            }
        });
    });
});