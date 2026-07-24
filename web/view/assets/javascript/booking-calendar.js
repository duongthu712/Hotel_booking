/**
 * Author: ThuDNM-HE204370
 * Date created: 16/06/2026
 * Purpose: JavaScript logic for booking calendar.
 */

window.addEventListener('DOMContentLoaded', () => {
    // Hàm thiết lập logic lịch cho một cặp ô Input cụ thể
    const setupCalendarLogic = (checkInId, checkOutId) => {
        const checkInInput = document.getElementById(checkInId);
        const checkOutInput = document.getElementById(checkOutId);

        // Nếu trang hiện tại không có các ô input này thì bỏ qua 
        if (!checkInInput || !checkOutInput) return;

        //  Lấy ngày hôm nay định dạng yyyy-MM-dd chuẩn hệ thống
        const today = new Date();
        const yyyy = today.getFullYear();
        const mm = String(today.getMonth() + 1).padStart(2, '0');
        const dd = String(today.getDate()).padStart(2, '0');
        const todayStr = `${yyyy}-${mm}-${dd}`;

        //  Gạch bỏ toàn bộ các ngày trong quá khứ của ô Check-In
        checkInInput.min = todayStr;

        //  Nếu ô check in có dữ liệu thì validate ô checkout sau check in 1 ngày
        if (checkInInput.value) {
            const currentInDate = new Date(checkInInput.value);
            currentInDate.setDate(currentInDate.getDate() + 1);
            // Đổi getUTCDate() thành getDate() để không bị lệch múi giờ lùi ngày
            const limitStr = `${currentInDate.getFullYear()}-${String(currentInDate.getMonth() + 1).padStart(2, '0')}-${String(currentInDate.getDate()).padStart(2, '0')}`;
            // Khóa các ngày trước ngày Check-in tại ô Check-out
            checkOutInput.min = limitStr;
        }

        //  Khi ô check in thay đổi 
        checkInInput.addEventListener('change', () => {
            if (checkInInput.value) {
                // Cập nhật lịch check out so với check in 
                const nextDay = new Date(checkInInput.value);
                nextDay.setDate(nextDay.getDate() + 1);

                const nextYyyy = nextDay.getFullYear();
                const nextMm = String(nextDay.getMonth() + 1).padStart(2, '0');
                const nextDd = String(nextDay.getDate()).padStart(2, '0');
                const nextDayStr = `${nextYyyy}-${nextMm}-${nextDd}`;

                // Ép ô Check-Out tối thiểu phải là ngày hôm sau của ngày Check-In mới chọn
                checkOutInput.min = nextDayStr;

                // Tự động nhảy lịch nếu checkout chưa sau check in 1 ngày 
                if (!checkOutInput.value || checkOutInput.value <= checkInInput.value) {
                    checkOutInput.value = nextDayStr;
                }
            }
        });
    };

    // Thực thi kích hoạt cho cả trang chủ (id: checkIn/checkOut) 
    // và trang kết quả (id: checkInResult/checkOutResult)
    setupCalendarLogic('checkIn', 'checkOut');
    setupCalendarLogic('checkInResult', 'checkOutResult');
});