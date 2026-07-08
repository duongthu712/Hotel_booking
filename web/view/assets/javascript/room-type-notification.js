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

    // 1. Nhóm thông báo thành công thông thường
    if (status === 'success' || status === 'updated' || status === 'deleted') {
        config.title = 'Thành Công!';
        config.icon = 'success';
        config.timer = 3500; 
        
        if (status === 'success') config.text = 'Hạng phòng mới đã được khởi tạo!';
        else if (status === 'updated') config.text = 'Thông tin đã cập nhật!';
        else if (status === 'deleted') config.text = 'Đã xóa hạng phòng thành công!';
    } 
    // 2. GIỮ NGUYÊN POP-UP BÁO SỐ ĐƠN CŨ - ĐỔI LOGIC NÚT BẤM ĐỒNG Ý XÓA
    else if (status === 'conflict') {
        const staying = urlParams.get('staying') || '0';
        const future = urlParams.get('future') || '0';
        
        config.title = 'Hạng phòng đang có giao dịch!';
        config.html = `Hạng phòng này hiện đang được sử dụng:<br><br>
                       • <b>${staying}</b> phòng đang có khách ở.<br>
                       • <b>${future}</b> đơn đặt phòng tương lai.<br><br>
                       <b style="color: #c44;">* Ghi chú: Hạng phòng này chỉ phục vụ các đơn đã đặt từ trước khi xóa hạng phòng.</b>`;
        config.icon = 'warning';
        config.showCancelButton = true;
        config.confirmButtonColor = '#d33'; // ĐÃ SỬA: Thay dấu ":" thành dấu "=" để hết lỗi cú pháp
        config.confirmButtonText = 'Đồng ý xóa hạng phòng';
        config.cancelButtonText = 'Đóng';
        config.cancelButtonColor = '#1a446c';
    } 
    // 3. Thông báo sau khi bấm Đồng ý xóa thành công
    else if (status === 'deleted_with_orders') {
        const staying = urlParams.get('staying') || '0';
        const future = urlParams.get('future') || '0';

        config.title = 'Thành công!';
        config.html = `Đã ngừng kinh doanh hạng phòng thương mại thành công.<br>
                       Hệ thống vẫn phục vụ nốt <b>${staying}</b> đơn tại phòng và <b>${future}</b> đơn cho tương lai đã đặt từ trước khi xóa hạng phòng.`;
        config.icon = 'success';
    }
    else {
        return; 
    }

    Swal.fire(config).then((result) => {
        // Nếu đang ở Pop-up cảnh báo xung đột và chọn "Đồng ý xóa hạng phòng"
        if (status === 'conflict' && result.isConfirmed) {
            // Gọi lại servlet xóa thực sự bằng cách truyền tham số confirm=true
            window.location.href = 'roomtypedelete?id=' + urlParams.get('id') + '&confirm=true';
        } else {
            // Xóa sạch các param trên URL để khi người dùng F5 không bị lặp lại Pop-up
            window.history.replaceState({}, document.title, window.location.pathname);
        }
    });
});