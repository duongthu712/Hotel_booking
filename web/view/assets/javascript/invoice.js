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
    let newVal = (parseInt(input.value) || 0) + delta;
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
    document.getElementById('serviceTotal_' + index).textContent = formatCurrency(chargeQty * unitPrice);
    updateSummary();
}

function calculateAmenity(index) {
    const input = document.getElementById('amenityQty_' + index);
    const qty = parseInt(input.value) || 0;
    const unitPrice = parseFloat(input.dataset.unitPrice) || 0;
    document.getElementById('amenityTotal_' + index).textContent = formatCurrency(qty * unitPrice);
    updateSummary();
}

// Baseline — giá trị gốc từ server, chỉ capture 1 lần duy nhất khi trang load
let baseServicesTotal = 0;
let baseDamagesTotal = 0;
let baseRoomCharges = 0;
let baseLateCharge = 0;
let baseDeposit = 0;
let baseRemaining = 0;

function captureBaseline() {
    // Dịch vụ đã có từ server
    baseServicesTotal = parseCurrency(document.getElementById('summaryServices').textContent);

    // Hư hỏng đã có từ server
    baseDamagesTotal = parseCurrency(document.getElementById('summaryDamages').textContent);

    // Late charge
    const lateChargeEl = document.getElementById('summaryLateCharge');
    baseLateCharge = lateChargeEl ? parseCurrency(lateChargeEl.textContent) : 0;

    // Deposit
    const depositEl = document.getElementById('summaryDeposit');
    baseLateCharge = lateChargeEl ? parseCurrency(lateChargeEl.textContent) : 0;
    baseDeposit = depositEl ? parseCurrency(depositEl.textContent) : 0;

    // Room charges = tổng - late - service - damage
    const totalEl = document.getElementById('summaryTotal');
    const total = parseCurrency(totalEl.textContent);
    baseRoomCharges = total - baseLateCharge - baseServicesTotal - baseDamagesTotal;

    // Remaining — lấy từ data-remaining để tránh parse lỗi format
    const remainingEl = document.getElementById('summaryRemaining');
    if (remainingEl) {
        const dataRemaining = remainingEl.getAttribute('data-remaining');
        baseRemaining = dataRemaining ? parseFloat(dataRemaining) : parseCurrency(remainingEl.textContent);
    }
}

function updateSummary() {
    // Tính tổng dịch vụ mới nhập trên form (chưa lưu DB)
    let newServicesTotal = 0;
    document.querySelectorAll('input[name="serviceQuantity"]').forEach(input => {
        const qty = parseInt(input.value) || 0;
        const unitPrice = parseFloat(input.getAttribute('data-unit-price')) || 0;
        const isFree = parseInt(input.getAttribute('data-is-free')) || 0;
        const numRooms = parseInt(input.getAttribute('data-num-rooms')) || 1;
        newServicesTotal += Math.max(0, qty - (isFree * numRooms)) * unitPrice;
    });

    // Tính tổng hư hỏng mới nhập trên form (chưa lưu DB)
    let newDamagesTotal = 0;
    document.querySelectorAll('input[name="damageQuantity"]').forEach(input => {
        const qty = parseInt(input.value) || 0;
        const unitPrice = parseFloat(input.getAttribute('data-unit-price')) || 0;
        newDamagesTotal += qty * unitPrice;
    });

    const servicesTotal = baseServicesTotal + newServicesTotal;
    const damagesTotal = baseDamagesTotal + newDamagesTotal;
    const totalAmount = baseRoomCharges + baseLateCharge + servicesTotal + damagesTotal;
    const newRemaining = Math.max(0, baseRemaining + newServicesTotal + newDamagesTotal);

    // Cập nhật UI
    document.getElementById('summaryServices').textContent = formatCurrency(servicesTotal);
    document.getElementById('summaryDamages').textContent = formatCurrency(damagesTotal);
    document.getElementById('summaryTotal').textContent = formatCurrency(totalAmount);

    const remainingEl = document.getElementById('summaryRemaining');
    if (remainingEl) {
        remainingEl.textContent = formatCurrency(newRemaining);
    }

    // Cập nhật max của input thu tiền theo số tiền còn lại thực tế
    const collectInput = document.getElementById('collectAmount');
    if (collectInput) {
        collectInput.max = Math.round(newRemaining);
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
    if (!collectInput || !collectInput.value.trim())
        return true;

    const collectAmount = parseFloat(collectInput.value.replace(/[^0-9.]/g, ''));
    const maxAllowed = parseFloat(collectInput.max) || 0;

    if (isNaN(collectAmount) || collectAmount <= 0) {
        alert('Vui lòng nhập số tiền hợp lệ.');
        collectInput.focus();
        return false;
    }

    if (collectAmount > maxAllowed) {
        alert('Số tiền thu không được vượt quá ' + formatCurrency(maxAllowed));
        collectInput.focus();
        return false;
    }

    const paymentMethod = document.querySelector('select[name="paymentMethod"]');
    if (paymentMethod && !paymentMethod.value) {
        alert('Vui lòng chọn phương thức thanh toán khi thu tiền.');
        paymentMethod.focus();
        return false;
    }

    return true;
}

document.addEventListener('DOMContentLoaded', function () {
    setupSearch('serviceSearch', 'serviceTable');
    setupSearch('amenitySearch', 'amenityTable');

    // Capture baseline trước, sau đó mới set max
    captureBaseline();

    // Set max ngay từ đầu theo baseRemaining từ server
    const collectInput = document.getElementById('collectAmount');
    if (collectInput) {
        collectInput.max = Math.round(baseRemaining);
    }

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