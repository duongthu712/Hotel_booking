/**
 * Author: ThuDNM-HE204370
 * Date created: 16/06/2026
 * Purpose: JavaScript logic for request processing.
 */
document.addEventListener("DOMContentLoaded", function () {

    const actionSelect = document.getElementById("action_select");
    const submitBtn = document.getElementById("submit_btn");
    const processForm = document.querySelector(".process-form");
    const responseNotes = document.getElementById("response_notes");

    // --- 1. XỬ LÝ THÔNG BÁO TỰ ĐỘNG TỪ SERVER (TỐI ƯU TRÁNH TREO POPUP) ---
    const urlParams = new URLSearchParams(window.location.search);
    const msg = urlParams.get('status_msg');

    if (msg) {
        const msgConfigs = {
            "approve_success": {
                title: "Xử lý thành công!",
                text: "Hệ thống đã phê duyệt yêu cầu, cập nhật trạng thái đơn đặt phòng và khấu trừ dòng tiền trong hóa đơn tổng kết.",
                icon: "success"
            },
            "reject_success": {
                title: "Đã từ chối đơn",
                text: "Yêu cầu của khách hàng đã chuyển sang trạng thái Từ chối xử lý thành công.",
                icon: "info"
            },
            "not_found": {
                title: "Lỗi dữ liệu",
                text: "Không tìm thấy thông tin chi tiết của mã yêu cầu chỉnh sửa này.",
                icon: "error"
            },
            "no_room": {
                title: "Hết buồng trống",
                text: "Hệ thống kiểm tra khoảng thời gian này hiện không còn đủ phòng trống để thực hiện phê duyệt.",
                icon: "error"
            },
            "error": {
                title: "Xử lý thất bại",
                text: "Quá trình thực thi cơ sở dữ liệu gặp lỗi. Vui lòng kiểm tra lại.",
                icon: "error"
            },
            "system_error": {
                title: "Lỗi hệ thống",
                text: "Hệ thống xử lý hóa đơn và dữ liệu phòng gặp sự cố bất ngờ.",
                icon: "error"
            }
        };

        if (msgConfigs[msg]) {
            if (typeof Swal !== "undefined") {
                // Kích hoạt thông báo đồ họa
                Swal.fire({
                    ...msgConfigs[msg],
                    confirmButtonColor: '#1a446c'
                });

                // Trì hoãn dọn dẹp URL sau 150ms để bảo toàn luồng render của SweetAlert2
                setTimeout(function() {
                    const cleanUrl = window.location.protocol + "//" + window.location.host + window.location.pathname + window.location.search.replace(/([\?&])status_msg=[^&]*(&|$)/, '$1').replace(/[\?&]$/, '');
                    window.history.replaceState({}, document.title, cleanUrl);
                }, 150);
            } else {
                alert(msgConfigs[msg].text);
            }
        }
    }

    // --- 2. CẬP NHẬT TRẠNG THÁI NÚT BẤM KHI THAY ĐỔI SELECT HÀNH ĐỘNG ---
    if (actionSelect && submitBtn) {
        actionSelect.addEventListener("change", function () {
            const value = this.value;
            
            if (value === "approve") {
                submitBtn.style.backgroundColor = "#1a446c";
                submitBtn.style.borderColor = "#1a446c";
                submitBtn.innerText = "Xác nhận Phê duyệt";
            } else if (value === "reject") {
                submitBtn.style.backgroundColor = "#555555";
                submitBtn.style.borderColor = "#555555";
                submitBtn.innerText = "Xác nhận Từ chối";
            }
        });
    }

   

}); 