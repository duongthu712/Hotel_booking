document.addEventListener("DOMContentLoaded", function () {
    const checkoutForm = document.getElementById("checkoutForm");
    const checkoutTable = document.getElementById("checkoutTable");
    const selectAllCheckbox = document.getElementById("selectAll");
    const btnCheckout = document.getElementById("btnCheckout");
    const selectedCountSpan = document.getElementById("selectedCount");

    if (!checkoutTable) return;

    const allCheckboxes = checkoutTable.querySelectorAll(".room-checkbox");

    // ========== Auto-tick cùng booking_id ==========
    allCheckboxes.forEach(function (checkbox) {
        checkbox.addEventListener("change", function () {
            const bookingId = this.dataset.bookingId;
            const isChecked = this.checked;

            // Chỉ auto-tick khi CHECK, không auto-untick khi bỏ chọn lẻ
            if (isChecked) {
                allCheckboxes.forEach(function (cb) {
                    if (cb.dataset.bookingId === bookingId) {
                        cb.checked = true;
                    }
                });
            }
            // Khi untick: chỉ bỏ đúng phòng đó, không động phòng khác

            updateCounter();
        });
    });

    // ========== Select All ==========
    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener("change", function () {
            const isChecked = this.checked;
            allCheckboxes.forEach(function (cb) {
                cb.checked = isChecked;
            });
            updateCounter();
        });
    }

    // ========== Cập nhật counter ==========
    function updateCounter() {
        const checkedBoxes = checkoutTable.querySelectorAll(".room-checkbox:checked");
        const count = checkedBoxes.length;
        selectedCountSpan.textContent = count;
        btnCheckout.disabled = count === 0;
        if (count > 0) {
            btnCheckout.classList.remove("disabled");
        } else {
            btnCheckout.classList.add("disabled");
        }
    }

    // ========== Validate trước khi submit ==========
    if (checkoutForm) {
        checkoutForm.addEventListener("submit", function (e) {
            const checkedBoxes = checkoutTable.querySelectorAll(".room-checkbox:checked");
            if (checkedBoxes.length === 0) {
                e.preventDefault();
                alert("Vui lòng chọn ít nhất một phòng để checkout.");
                return false;
            }

            const bookingIds = new Set();
            checkedBoxes.forEach(function (cb) {
                bookingIds.add(cb.dataset.bookingId);
            });

            let confirmMsg = "Bạn có chắc muốn checkout " + checkedBoxes.length + " phòng";
            if (bookingIds.size > 1) {
                confirmMsg += " thuộc " + bookingIds.size + " đơn đặt phòng khác nhau";
            }
            confirmMsg += "?";

            if (!confirm(confirmMsg)) {
                e.preventDefault();
                return false;
            }
        });
    }

    updateCounter();
});