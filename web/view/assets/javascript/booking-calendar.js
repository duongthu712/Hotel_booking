function setupCalendarLogic(checkInId, checkOutId) {
    const checkInInput = document.getElementById(checkInId);
    const checkOutInput = document.getElementById(checkOutId);

    if (!checkInInput || !checkOutInput) return;

    // 1. Lấy ngày hôm nay
    const today = new Date();
    const yyyy = today.getFullYear();
    const mm = String(today.getMonth() + 1).padStart(2, '0');
    const dd = String(today.getDate()).padStart(2, '0');
    const todayStr = `${yyyy}-${mm}-${dd}`;

    // 2. Không cho chọn ngày quá khứ cho check-in
    checkInInput.min = todayStr;

    // 3. Nếu đã có check-in sẵn (trang result)
    if (checkInInput.value) {
        const currentInDate = new Date(checkInInput.value);
        currentInDate.setDate(currentInDate.getDate() + 1);

        const limitStr = `${currentInDate.getFullYear()}-${String(currentInDate.getMonth() + 1).padStart(2, '0')}-${String(currentInDate.getDate()).padStart(2, '0')}`;

        checkOutInput.min = limitStr;
    }

    // 4. Khi đổi check-in
    checkInInput.addEventListener('change', () => {
        if (checkInInput.value) {
            const nextDay = new Date(checkInInput.value);
            nextDay.setDate(nextDay.getDate() + 1);

            const nextDayStr = `${nextDay.getFullYear()}-${String(nextDay.getMonth() + 1).padStart(2, '0')}-${String(nextDay.getDate()).padStart(2, '0')}`;

            // cập nhật min check-out
            checkOutInput.min = nextDayStr;

            // tự sửa nếu sai
            if (!checkOutInput.value || checkOutInput.value <= checkInInput.value) {
                checkOutInput.value = nextDayStr;
            }
        }
    });
}

// Hàm chạy chính (init)
function initCalendar() {
    setupCalendarLogic('checkIn', 'checkOut');
    setupCalendarLogic('checkInResult', 'checkOutResult');
}

// chạy khi trang load xong
window.onload = initCalendar;