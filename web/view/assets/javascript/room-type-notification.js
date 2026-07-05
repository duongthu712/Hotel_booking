document.addEventListener("DOMContentLoaded", function () {
    const urlParams = new URLSearchParams(window.location.search);
    let status = urlParams.get('status');
    if (!status) return;

    let config = {
        background: '#F9F5EB',
        color: '#1a446c',
        confirmButtonColor: '#1a446c',
        confirmButtonText: 'Xác nhận'
        
    };

    // 1. Nhóm các thông báo thành công
    if (status === 'success' || status === 'updated' || status === 'deleted') {
        config.title = 'Thành Công!';
        config.icon = 'success';
        config.timer = 3500; // Tự đóng sau 3.5s
        
        if (status === 'success') config.text = 'Hạng phòng mới đã được khởi tạo!';
        else if (status === 'updated') config.text = 'Thông tin đã cập nhật!';
        else if (status === 'deleted') config.text = 'Đã xóa hạng phòng thành công!';
    } 
    // 2. Nhóm các thông báo lỗi/xung đột
    else if (status === 'conflict') {
        const staying = urlParams.get('staying') || '0';
        const future = urlParams.get('future') || '0';
        config.title = 'Hạng phòng đang có giao dịch!';
        config.html = `Hạng phòng này hiện đang được sử dụng:<br><br>
                       • <b>${staying}</b> phòng đang có khách ở.<br>
                       • <b>${future}</b> đơn đặt phòng tương lai.<br><br>
                       Bạn cần liên hệ xử lý các đơn này trước khi ngừng kinh doanh.`;
        config.icon = 'warning';
        config.showCancelButton = true;
        config.confirmButtonText = 'Đến trang xử lý';
        config.cancelButtonText = 'Đóng';
        config.cancelButtonColor = '#1a446c';
    } 
    else {
        return; // Không hiện gì cả nếu status không khớp
    }

    Swal.fire(config).then((result) => {
        if (status === 'conflict' && result.isConfirmed) {
            window.location.href = 'roomtypeconflict?id=' + urlParams.get('id');
        }
        // Xóa status trên URL sau khi đã hiện xong
        window.history.replaceState({}, document.title, window.location.pathname);
    });
});