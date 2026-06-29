function formatCurrency(num) {
    return new Intl.NumberFormat('vi-VN').format(Math.round(num)) + ' đ';
}

function parseCurrency(str) {
    if (typeof str === 'number') return str;
    return parseFloat(str.replace(/[^0-9.-]+/g, '')) || 0;
}

function changeQty(type, index, delta) {
    const input = document.getElementById(type + 'Qty_' + index);
    let currentVal = parseInt(input.value) || 0;
    let newVal = currentVal + delta;
    if (newVal < 0) newVal = 0;
    const maxVal = input.getAttribute('max');
    if (maxVal && newVal > parseInt(maxVal)) newVal = parseInt(maxVal);
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
    const unitPrice = parseFloat(input.getAttribute('data-unit-price')) || 0;
    const isFree = parseInt(input.getAttribute('data-is-free')) || 0;
    const numRooms = parseInt(input.getAttribute('data-num-rooms')) || 1;
    const chargeQty = Math.max(0, qty - (isFree * numRooms));
    const total = chargeQty * unitPrice;
    document.getElementById('serviceTotal_' + index).textContent = formatCurrency(total);
    updateSummary();
}

function calculateAmenity(index) {
    const input = document.getElementById('amenityQty_' + index);
    const qty = parseInt(input.value) || 0;
    const unitPrice = parseFloat(input.getAttribute('data-unit-price')) || 0;
    const total = qty * unitPrice;
    document.getElementById('amenityTotal_' + index).textContent = formatCurrency(total);
    updateSummary();
}

function updateSummary() {
    const existingServices = typeof initServicesTotal !== 'undefined' ? initServicesTotal : 0;
    const existingDamages = typeof initDamagesTotal !== 'undefined' ? initDamagesTotal : 0;

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

    const servicesTotal = existingServices + newServicesTotal;
    const damagesTotal = existingDamages + newDamagesTotal;
    const roomCharges = parseFloat(document.getElementById('hiddenRoomCharges').value) || 0;
    const deposit = parseCurrency(document.getElementById('summaryDeposit').textContent);
    const totalAmount = roomCharges + servicesTotal + damagesTotal;
    const remaining = Math.max(0, totalAmount + deposit);

    document.getElementById('summaryServices').textContent = formatCurrency(servicesTotal);
    document.getElementById('summaryDamages').textContent = formatCurrency(damagesTotal);
    document.getElementById('summaryTotal').textContent = formatCurrency(totalAmount);
    document.getElementById('summaryRemaining').textContent = formatCurrency(remaining);
}

function setupSearch(inputId, tableId) {
    const searchInput = document.getElementById(inputId);
    const table = document.getElementById(tableId);
    if (!searchInput || !table) return;
    searchInput.addEventListener('input', function () {
        const keyword = this.value.toLowerCase().trim();
        table.querySelectorAll('tbody tr').forEach(row => {
            const name = row.getAttribute('data-name') || '';
            row.style.display = name.includes(keyword) ? '' : 'none';
        });
    });
}

function validateForm() {
    const paymentMethod = document.querySelector('select[name="paymentMethod"]').value;
    if (!paymentMethod) {
        alert('Vui lòng chọn phương thức thanh toán.');
        return false;
    }
    return true;
}

document.addEventListener('DOMContentLoaded', function () {
    setupSearch('serviceSearch', 'serviceTable');
    setupSearch('amenitySearch', 'amenityTable');
    updateSummary();

    document.getElementById('invoiceForm').addEventListener('submit', function (e) {
        if (!validateForm()) e.preventDefault();
    });

    document.querySelectorAll('.qty-input').forEach(input => {
        input.addEventListener('keydown', function (e) {
            if (e.key === '-' || e.key === 'e') e.preventDefault();
        });
    });
});