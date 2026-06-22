document.addEventListener("DOMContentLoaded", function () {

    function toggleModal(modal, show) {
        if (!modal) return;
        if (show) {
            modal.classList.add("show");
            modal.style.display = "flex";
        } else {
            modal.classList.remove("show");
            modal.style.display = "none";
        }
    }

    const paymentDetailModal = document.getElementById("payment-detail-modal");
    const btnCloseDetail     = document.getElementById("btn-close-detail");
    const proofImg           = document.getElementById("proof-img");
    const proofWrapper       = document.getElementById("payment-proof-wrapper");
    const proofUrlLink       = document.getElementById("proof-url-link");
    const detailBookingCode  = document.getElementById("detail-booking-code");
    const detailGuestName    = document.getElementById("detail-guest-name");
    const detailAmount       = document.getElementById("detail-amount");
    const detailSubmittedAt  = document.getElementById("detail-submitted-at");
    const detailStatus       = document.getElementById("detail-status");
    const detailVerifiedBy   = document.getElementById("detail-verified-by");
    const verifyNotes        = document.getElementById("verify-notes");
    const verifyDepositId    = document.getElementById("verify-deposit-id");
    const rejectDepositId    = document.getElementById("reject-deposit-id");
    const verifyNotesHidden  = document.getElementById("verify-notes-hidden");
    const rejectNotes        = document.getElementById("reject-notes");
    const verificationForm   = document.getElementById("verification-form");

    window.openPaymentDetailModal = function (depositId) {
        const row = document.querySelector(`tr[data-deposit-id="${depositId}"]`);
        if (!row) return;

        const bookingCode = row.dataset.bookingCode || '-';
        const guestName   = row.dataset.guestName   || '-';
        const amount      = row.dataset.amount       || '0';
        const submittedAt = row.dataset.submittedAt  || '-';
        const status      = row.dataset.status       || '-';
        const verifiedBy  = row.dataset.verifiedBy   || '-';
        const proofUrl    = row.dataset.proofUrl     || '';
        const notes       = row.dataset.notes        || '';

        const displayStatus = status === 'Đã phê duyệt' ? 'Đã duyệt' : status;

        if (detailBookingCode) detailBookingCode.innerText = bookingCode;
        if (detailGuestName)   detailGuestName.innerText   = guestName;
        if (detailAmount)      detailAmount.innerText       = new Intl.NumberFormat('vi-VN').format(amount) + ' đ';
        if (detailSubmittedAt) detailSubmittedAt.innerText  = submittedAt;
        if (detailVerifiedBy)  detailVerifiedBy.innerText   = verifiedBy;

        if (detailStatus) {
            detailStatus.innerText  = displayStatus;
            detailStatus.className  = 'info-value ' + (
                status === 'Chờ xử lý'    ? 'status-pending'  :
                status === 'Đã phê duyệt' ? 'status-approved' : 'status-rejected'
            );
        }

        if (proofUrl) {
            if (proofImg)     proofImg.src          = proofUrl;
            if (proofUrlLink) proofUrlLink.innerText = proofUrl;
            if (proofWrapper) proofWrapper.style.display = "block";
        } else {
            if (proofWrapper) proofWrapper.style.display = "none";
        }

        if (verifyNotes) verifyNotes.value = notes;
        if (verifyDepositId) verifyDepositId.value = depositId;
        if (rejectDepositId) rejectDepositId.value = depositId;

        const isPending = status === 'Chờ xử lý';
        const btnReject = document.querySelector(".btn-reject");
        const btnSubmit = document.querySelector(".btn-submit");
        if (verificationForm) verificationForm.style.display = isPending ? "block" : "none";
        if (btnReject)        btnReject.style.display        = isPending ? "inline-block" : "none";
        if (btnSubmit)        btnSubmit.style.display        = isPending ? "inline-block" : "none";

        toggleModal(paymentDetailModal, true);
    };

    if (btnCloseDetail) {
        btnCloseDetail.addEventListener("click", () => {
            toggleModal(paymentDetailModal, false);
            if (verifyNotes) verifyNotes.value = "";
        });
    }

    window.prepareReject = function () {
        const notes = verifyNotes ? verifyNotes.value : "";
        if (rejectNotes) rejectNotes.value = notes;
        return confirm("Bạn có chắc muốn từ chối khoản thanh toán này?");
    };

    const verifyForm = document.querySelector("form[action='DepositPaymentVerify']");
    if (verifyForm) {
        verifyForm.addEventListener("submit", function () {
            const notes = verifyNotes ? verifyNotes.value : "";
            if (verifyNotesHidden) verifyNotesHidden.value = notes;
        });
    }

    document.querySelectorAll(".hotel-popup").forEach(modal => {
        modal.addEventListener("click", (e) => {
            if (e.target === modal) toggleModal(modal, false);
        });
    });
});