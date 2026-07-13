/* * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

/**
 * Hệ thống xử lý yêu cầu chỉnh sửa đơn (Lễ tân)
 * Thư mục: view/assets/javascript/request-processing.js
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

   
    // --- 4. LOGIC ĐỐI SOÁT THỜI GIAN THỰC TẾ VÀ ĐỔ DỮ LIỆU ---
    function calculateStaffCancellationDetails() {
        const checkInDateEl = document.getElementById("checkInDate");
        if (!checkInDateEl) return; 
        
        const checkInDateStr = checkInDateEl.value;
        if (!checkInDateStr) return;

        const checkInDateTime = new Date(`${checkInDateStr}T14:00:00`);
        const now = new Date();

        const diffMs = checkInDateTime - now;
        const diffHours = diffMs / (1000 * 60 * 60);

        const lblHoursRemaining = document.getElementById("lblHoursRemaining");
        if (lblHoursRemaining) {
            if (diffHours > 0) {
                lblHoursRemaining.textContent = diffHours.toFixed(1) + " giờ";
            } else {
                lblHoursRemaining.textContent = "0 giờ (Đã quá mốc giờ check-in " + Math.abs(diffHours).toFixed(1) + " giờ)";
            }
        }

        let refundPercent = 0;
        let activeRowId = "policy-row-72";

        if (diffHours >= 72) {
            refundPercent = 1.00;
            activeRowId = "policy-row-72";
        } else if (diffHours >= 48 && diffHours < 72) {
            refundPercent = 0.70;
            activeRowId = "policy-row-48";
        } else if (diffHours >= 24 && diffHours < 48) {
            refundPercent = 0.50;
            activeRowId = "policy-row-24";
        } else {
            refundPercent = 0.30;
            activeRowId = "policy-row-0";
        }

        ["policy-row-72", "policy-row-48", "policy-row-24", "policy-row-0"].forEach(rowId => {
            const row = document.getElementById(rowId);
            if (row) {
                if (rowId === activeRowId) {
                    row.style.backgroundColor = "#f1f5f9"; 
                    row.style.fontWeight = "bold";
                    row.style.color = "#0f172a";
                } else {
                    row.style.backgroundColor = "";
                    row.style.fontWeight = "normal";
                    row.style.color = "";
                }
            }
        });

        const totalOldPriceEl = document.getElementById("totalOldPrice");
        
        if (totalOldPriceEl) {
            const totalBookingValue = parseFloat(totalOldPriceEl.value) || 0;
            const depositValue = totalBookingValue * 0.30; 
            const finalRefund = depositValue * refundPercent; 

            if (document.getElementById("lblTotalBooking")) {
                document.getElementById("lblTotalBooking").textContent = totalBookingValue.toLocaleString('vi-VN') + " VND";
            }
            if (document.getElementById("lblDepositValue")) {
                document.getElementById("lblDepositValue").textContent = depositValue.toLocaleString('vi-VN') + " VND";
            }
            if (document.getElementById("lblCancelFeeValue")) {
                const penaltyPercentText = ((1 - refundPercent) * 100).toFixed(0);
                document.getElementById("lblCancelFeeValue").textContent = (depositValue - finalRefund).toLocaleString('vi-VN') + " VND (Khấu trừ " + penaltyPercentText + "% tiền cọc)";
            }
            if (document.getElementById("lblFinalRefundValue")) {
                document.getElementById("lblFinalRefundValue").textContent = finalRefund.toLocaleString('vi-VN') + " VND";
            }
        }
    }

    calculateStaffCancellationDetails();
}); 