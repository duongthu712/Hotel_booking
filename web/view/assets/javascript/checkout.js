document.addEventListener("DOMContentLoaded", function () {
    const invoiceForm = document.getElementById("invoiceForm");

    // Format số thành tiền VND
    function formatCurrency(value) {
        return new Intl.NumberFormat("vi-VN").format(Math.round(value)) + " đ";
    }

    // Parse chuỗi tiền về số
    function parseCurrency(value) {
        if (typeof value === "number") {
            return value;
        }
        return parseFloat(value.replace(/[^0-9.-]+/g, "")) || 0;
    }

    // Cập nhật tổng tiền bên phải mỗi khi số lượng thay đổi
    function updateSummary() {
        const hiddenRoomCharges = document.getElementById("hiddenRoomCharges");
        if (!hiddenRoomCharges) return;

        let servicesTotal = 0;
        let damagesTotal = 0;

        document.querySelectorAll("input[name='serviceQuantity']").forEach(function (input) {
            const qty = parseInt(input.value) || 0;
            const unitPrice = parseFloat(input.dataset.unitPrice) || 0;
            const freeQty = parseInt(input.dataset.isFree) || 0;
            servicesTotal += Math.max(0, qty - freeQty) * unitPrice;
        });

        document.querySelectorAll("input[name='damageQuantity']").forEach(function (input) {
            const qty = parseInt(input.value) || 0;
            const unitPrice = parseFloat(input.dataset.unitPrice) || 0;
            damagesTotal += qty * unitPrice;
        });

        const roomCharges = parseFloat(hiddenRoomCharges.value) || 0;

        const deposit = Math.abs(parseCurrency(
            document.getElementById("summaryDeposit").textContent
        ));

        const total = roomCharges + servicesTotal + damagesTotal;
        const remaining = Math.max(0, total - deposit);

        document.getElementById("summaryServices").textContent = formatCurrency(servicesTotal);
        document.getElementById("summaryDamages").textContent = formatCurrency(damagesTotal);
        document.getElementById("summaryTotal").textContent = formatCurrency(total);
        document.getElementById("summaryRemaining").textContent = formatCurrency(remaining);
    }

    // Tính thành tiền 1 dịch vụ theo index
    function calculateService(index) {
        const input = document.getElementById("serviceQty_" + index);
        const qty = parseInt(input.value) || 0;
        const unitPrice = parseFloat(input.dataset.unitPrice) || 0;
        const freeQty = parseInt(input.dataset.isFree) || 0;
        const total = Math.max(0, qty - freeQty) * unitPrice;
        document.getElementById("serviceTotal_" + index).textContent = formatCurrency(total);
        updateSummary();
    }

    // Tính thành tiền 1 tiện nghi hư hỏng theo index
    function calculateAmenity(index) {
        const input = document.getElementById("amenityQty_" + index);
        const qty = parseInt(input.value) || 0;
        const unitPrice = parseFloat(input.dataset.unitPrice) || 0;
        document.getElementById("amenityTotal_" + index).textContent = formatCurrency(qty * unitPrice);
        updateSummary();
    }

    // Tăng/giảm số lượng khi bấm nút +/-
    window.changeQty = function (type, index, delta) {
        const input = document.getElementById(type + "Qty_" + index);
        let value = (parseInt(input.value) || 0) + delta;
        if (value < 0) value = 0;
        if (input.max && value > parseInt(input.max)) value = parseInt(input.max);
        input.value = value;
        if (type === "service") {
            calculateService(index);
        } else {
            calculateAmenity(index);
        }
    };

    // Lọc dịch vụ/tiện nghi theo tên khi gõ vào ô tìm kiếm
    function setupSearch(inputId, tableId) {
        const input = document.getElementById(inputId);
        const table = document.getElementById(tableId);
        if (!input || !table) return;
        input.addEventListener("input", function () {
            const keyword = this.value.toLowerCase().trim();
            table.querySelectorAll("tbody tr").forEach(function (row) {
                const name = (row.dataset.name || "").toLowerCase();
                row.style.display = name.includes(keyword) ? "" : "none";
            });
        });
    }

    // Kiểm tra phương thức thanh toán trước khi submit
    function validateForm() {
        const paymentMethod = document.querySelector("select[name='paymentMethod']").value;
        if (!paymentMethod) {
            alert("Vui lòng chọn phương thức thanh toán.");
            return false;
        }
        return true;
    }

    setupSearch("serviceSearch", "serviceTable");
    setupSearch("amenitySearch", "amenityTable");

    updateSummary();

    if (invoiceForm) {
        invoiceForm.addEventListener("submit", function (e) {
            if (!validateForm()) {
                e.preventDefault();
            }
        });
    }

    document.querySelectorAll(".qty-input").forEach(function (input) {
        input.addEventListener("keydown", function (e) {
            if (e.key === "-" || e.key === "e") {
                e.preventDefault();
            }
        });
    });
});