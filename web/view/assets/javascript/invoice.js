function formatCurrency(num) {
    return new Intl.NumberFormat('vi-VN').format(Math.round(num)) + ' đ';
}

function parseCurrency(str) {
    if (typeof str === 'number')
        return str;
    if (!str)
        return 0;

    return parseFloat(str.replace(/[^0-9.-]+/g, '')) || 0;
}

function changeQty(type, index, delta) {
    const input = document.getElementById(type + 'Qty_' + index);
    let currentVal = parseInt(input.value) || 0;
    let newVal = currentVal + delta;
    if (newVal < 0)
        newVal = 0;
    const maxVal = input.getAttribute('max');
    if (maxVal && newVal > parseInt(maxVal))
        newVal = parseInt(maxVal);
    input.value = newVal;
    if (type === 'service') {
        calculateService(index);
    } else {
        calculateAmenity(index);
    }
}

function calculateService(index) {
    const input = document.getElementById('serviceQty_' + index);
    const qty = parseInt(input.value) || 0;
    const unitPrice = parseFloat(input.dataset.unitPrice) || 0;
    const isFree = parseInt(input.dataset.isFree) || 0;
    const numRooms = parseInt(input.dataset.numRooms) || 1;

    const chargeQty = Math.max(0, qty - (isFree * numRooms));
    const total = chargeQty * unitPrice;

    document.getElementById('serviceTotal_' + index).textContent = formatCurrency(total);

    updateSummary();
}

function calculateAmenity(index) {
    const input = document.getElementById('amenityQty_' + index);
    const qty = parseInt(input.value) || 0;
    const unitPrice = parseFloat(input.dataset.unitPrice) || 0;

    const total = qty * unitPrice;

    document.getElementById('amenityTotal_' + index).textContent = formatCurrency(total);

    updateSummary();
}

// Lưu giá trị gốc (đã render từ server) ngay khi trang load, dùng làm baseline để cộng dồn
let baseServicesTotal = null;
let baseDamagesTotal = null;
let baseRoomCharges = null;
let baseRemaining = null;

function captureBaseline() {
    if (baseServicesTotal === null) {
        baseServicesTotal = parseCurrency(document.getElementById('summaryServices').textContent);
    }
    if (baseDamagesTotal === null) {
        baseDamagesTotal = parseCurrency(document.getElementById('summaryDamages').textContent);
    }
    if (baseRoomCharges === null) {
        const totalNow = parseCurrency(document.getElementById('summaryTotal').textContent);
        let lateChargeBase = 0;
        const lateChargeEl = document.getElementById('summaryLateCharge');
        if (lateChargeEl) {
            lateChargeBase = parseCurrency(lateChargeEl.textContent);
        }
        baseRoomCharges = totalNow - lateChargeBase - baseServicesTotal - baseDamagesTotal;
    }
    if (baseRemaining === null) {
        const remainingEl = document.getElementById("summaryRemaining");
        if (remainingEl) {
            const dataRemaining = remainingEl.getAttribute('data-remaining');
            baseRemaining = dataRemaining ? parseFloat(dataRemaining) : parseCurrency(remainingEl.textContent);
        }
    }
}

function updateSummary() {
    captureBaseline();

    let newServicesTotal = 0;
    document.querySelectorAll('input[name="serviceQuantity"]').forEach((input) => {
        const qty = parseInt(input.value) || 0;
        const unitPrice = parseFloat(input.getAttribute('data-unit-price')) || 0;
        const isFree = parseInt(input.getAttribute('data-is-free')) || 0;
        const numRooms = parseInt(input.getAttribute('data-num-rooms')) || 1;
        newServicesTotal += Math.max(0, qty - (isFree * numRooms)) * unitPrice;
    });

    let newDamagesTotal = 0;
    document.querySelectorAll('input[name="damageQuantity"]').forEach((input) => {
        const qty = parseInt(input.value) || 0;
        const unitPrice = parseFloat(input.getAttribute('data-unit-price')) || 0;
        newDamagesTotal += qty * unitPrice;
    });

    const servicesTotal = baseServicesTotal + newServicesTotal;
    const damagesTotal = baseDamagesTotal + newDamagesTotal;
    let lateChargeTotal = 0;
    const lateChargeEl = document.getElementById('summaryLateCharge');
    if (lateChargeEl) {
        lateChargeTotal = parseCurrency(lateChargeEl.textContent);
    }

    const totalAmount = baseRoomCharges + lateChargeTotal + servicesTotal + damagesTotal;

    document.getElementById('summaryServices').textContent = formatCurrency(servicesTotal);
    document.getElementById('summaryDamages').textContent = formatCurrency(damagesTotal);
    document.getElementById('summaryTotal').textContent = formatCurrency(totalAmount);

    // Cập nhật ước lượng số tiền còn phải thanh toán
    // remaining = totalAmount - đã thanh toán (không trừ cọc)
    const remainingEl = document.getElementById('summaryRemaining');
    if (remainingEl) {
        const addedAmount = newServicesTotal + newDamagesTotal;
        remainingEl.textContent = formatCurrency(baseRemaining + addedAmount);
    }
}

function setupSearch(inputId, tableId) {
    const searchInput = document.getElementById(inputId);
    const table = document.getElementById(tableId);
    if (!searchInput || !table)
        return;
    searchInput.addEventListener('input', function () {
        const keyword = this.value.toLowerCase().trim();
        table.querySelectorAll('tbody tr').forEach(row => {
            const name = row.getAttribute('data-name') || '';
            row.style.display = name.includes(keyword) ? '' : 'none';
        });
    });
}

function validateForm() {
    const collectInput = document.getElementById('collectAmount');
    if (collectInput && collectInput.value && parseFloat(collectInput.value) > 0) {
        const paymentMethod = document.querySelector('select[name="paymentMethod"]');
        if (paymentMethod && !paymentMethod.value) {
            alert('Vui lòng chọn phương thức thanh toán khi thu tiền.');
            return false;
        }
    }
    return true;
}

function validateForm() {
    const collectInput = document.getElementById('collectAmount');
    if (collectInput && collectInput.value.trim()) {
        const collectAmount = parseFloat(collectInput.value.replace(/[^0-9.]/g, ''));
        const remainingEl = document.getElementById('summaryRemaining');
        const remainingAmount = parseCurrency(remainingEl.textContent);

        if (isNaN(collectAmount) || collectAmount <= 0) {
            alert('Vui lòng nhập số tiền hợp lệ.');
            collectInput.focus();
            return false;
        }

        if (collectAmount > remainingAmount) {
            alert('Số tiền thu không được vượt quá ' + formatCurrency(remainingAmount));
            collectInput.focus();
            return false;
        }

        const paymentMethod = document.querySelector('select[name="paymentMethod"]');
        if (paymentMethod && !paymentMethod.value) {
            alert('Vui lòng chọn phương thức thanh toán khi thu tiền.');
            paymentMethod.focus();
            return false;
        }
    }
    return true;
}

document.addEventListener('DOMContentLoaded', function () {
    setupSearch('serviceSearch', 'serviceTable');
    setupSearch('amenitySearch', 'amenityTable');
    captureBaseline();

    const form = document.getElementById('invoiceForm');
    if (form) {
        form.addEventListener('submit', function (e) {
            if (!validateForm())
                e.preventDefault();
        });
    }

    document.querySelectorAll('.qty-input').forEach(input => {
        input.addEventListener('keydown', function (e) {
            if (e.key === '-' || e.key === 'e')
                e.preventDefault();
        });
    });
});