/* * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

window.addEventListener('DOMContentLoaded', () => {
    // Hàm thiết lập logic lịch cho một cặp ô Input cụ thể
    const setupCalendarLogic = (checkInId, checkOutId) => {
        const checkInInput = document.getElementById(checkInId);
        const checkOutInput = document.getElementById(checkOutId);

        // Nếu trang hiện tại không có các ô input này thì bỏ qua (tránh lỗi crash script)
        if (!checkInInput || !checkOutInput) return;

        // 1. Lấy ngày hôm nay định dạng yyyy-MM-dd chuẩn hệ thống
        const today = new Date();
        const yyyy = today.getFullYear();
        const mm = String(today.getMonth() + 1).padStart(2, '0');
        const dd = String(today.getDate()).padStart(2, '0');
        const todayStr = `${yyyy}-${mm}-${dd}`;

        // 2. Gạch bỏ toàn bộ các ngày trong quá khứ của ô Check-In
        checkInInput.min = todayStr;

        // 3. Nếu ô Check-In đã có sẵn dữ liệu cũ (Trang kết quả tìm kiếm)
        if (checkInInput.value) {
            const currentInDate = new Date(checkInInput.value);
            currentInDate.setDate(currentInDate.getDate() + 1);
            // 🔥 ĐÃ SỬA: Đổi getUTCDate() thành getDate() để không bị lệch múi giờ lùi ngày
            const limitStr = `${currentInDate.getFullYear()}-${String(currentInDate.getMonth() + 1).padStart(2, '0')}-${String(currentInDate.getDate()).padStart(2, '0')}`;
            // Khóa các ngày trước ngày Check-in tại ô Check-out
            checkOutInput.min = limitStr;
        }

        // 4. Lắng nghe sự kiện thay đổi ngày của ô Check-In
        checkInInput.addEventListener('change', () => {
            if (checkInInput.value) {
                const nextDay = new Date(checkInInput.value);
                nextDay.setDate(nextDay.getDate() + 1);

                const nextYyyy = nextDay.getFullYear();
                const nextMm = String(nextDay.getMonth() + 1).padStart(2, '0');
                const nextDd = String(nextDay.getDate()).padStart(2, '0');
                const nextDayStr = `${nextYyyy}-${nextMm}-${nextDd}`;

                // Ép ô Check-Out tối thiểu phải là ngày hôm sau của ngày Check-In mới chọn
                checkOutInput.min = nextDayStr;

                // Nếu ngày Check-Out cũ nhỏ hơn hoặc bằng ngày Check-In mới chọn, tự động nhảy lịch
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