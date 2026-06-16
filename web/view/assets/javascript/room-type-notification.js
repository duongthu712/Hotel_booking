/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/javascript.js to edit this template
 */


document.addEventListener("DOMContentLoaded", function() {
    // 1. Kiểm tra tham số trên URL trước (Dùng cho trang danh sách sau khi redirect)
    const urlParams = new URLSearchParams(window.location.search);
    let status = urlParams.get('status');
    let invalidName = decodeURIComponent(urlParams.get('invalidName') || '');

    // 2. Nếu URL không có, kiểm tra tiếp thẻ ẩn ngầm (Dùng cho trang add khi forward lỗi trùng tên)
    const hiddenStatusEl = document.getElementById("serverStatus");
    const hiddenNameEl = document.getElementById("serverInvalidName");

    if (hiddenStatusEl && hiddenStatusEl.value) {
        status = hiddenStatusEl.value;
    }
    if (hiddenNameEl && hiddenNameEl.value) {
        invalidName = hiddenNameEl.value;
    }

    // Nếu không tìm thấy bất kỳ tín hiệu trạng thái nào thì dừng script
    if (!status) return;

    // Cấu hình khung sườn thông báo hoàng gia phẳng của Vũ
    let config = {
        background: '#F9F5EB',
        color: '#1a446c',
        confirmButtonColor: '#1a446c',
        confirmButtonText: 'Xác nhận',
        timerProgressBar: true
    };

    // 3. PHÂN TÍCH TRẠNG THÁI ĐỂ ĐỔI NỘI DUNG ĐỘNG
    if (status === 'success') {
        config.title = 'Thành Công!';
        config.text = 'Hạng phòng mới đã được khởi tạo và đồng bộ vào hệ thống!';
        config.icon = 'success';
        config.timer = 3500;
    } 
    else if (status === 'updated') {
        config.title = 'Thành Công!';
        config.text = 'Thông tin hạng phòng đã được cập nhật thay đổi thành công!';
        config.icon = 'success';
        config.timer = 3500;
    } 
    else if (status === 'deleted') {
        config.title = 'Thành Công!';
        config.text = 'Hạng phòng đã được chuyển trạng thái dừng kinh doanh thành công!';
        config.icon = 'success';
        config.timer = 3500;
    } 
    // Bắt lỗi trùng tên gọn gàng bằng file JS
    else if (status === 'duplicate') {
        config.title = 'Trùng Tên Hạng Phòng!';
        config.text = `Tên phòng "${invalidName}" đã tồn tại trên hệ thống La Mer! Vui lòng chọn một tên gọi khác.`;
        config.icon = 'error';
        config.confirmButtonText = 'Tôi đã hiểu';
        // Không để thuộc tính timer để admin bắt buộc phải đọc kĩ lỗi
    } 
    else {
        return; 
    }

    // 4. KÍCH HOẠT HIỂN THỊ SWEETALERT2
    Swal.fire(config).then(() => {
        // Xóa tham số URL rác nếu có
        if (urlParams.get('status')) {
            window.history.replaceState({}, document.title, window.location.pathname);
        }
    });
});