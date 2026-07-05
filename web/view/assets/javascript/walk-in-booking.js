/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/javascript.js to edit this template
 */
document.addEventListener("DOMContentLoaded", function() {
    const buttons = document.querySelectorAll(".select-room-btn");
    const bookingSection = document.getElementById("quickBookingSection");
    const displayRoomType = document.getElementById("summaryRoomType");
    const formRoomTypeId = document.getElementById("formRoomTypeId");
    const formBasePrice = document.getElementById("formBasePrice");
    const summaryPricePerNight = document.getElementById("summaryPricePerNight");
    const summaryNights = document.getElementById("summaryNights");
    const summaryTotalAmount = document.getElementById("summaryTotalAmount");
    const summaryDepositAmount = document.getElementById("summaryDepositAmount");
    const financialSection = document.getElementById("financialVerificationSection");
    const depositSummaryRow = document.getElementById("depositSummaryRow");
    const stayNowNotice = document.getElementById("stayNowNotice");
    const qrMemoSpan = document.getElementById("qrMemo");

    // 1. Thu thập dữ liệu ngày và số phòng từ bộ lọc tìm kiếm phía trên
    const checkInInput = document.getElementById("checkIn");
    const checkOutInput = document.getElementById("checkOut");
    const numRoomsSearchInput = document.getElementById("searchNumRooms");

    let checkInStr = checkInInput ? checkInInput.value : "";
    let checkOutStr = checkOutInput ? checkOutInput.value : "";

    // 2. Tính toán số đêm lưu trú chuẩn nghiệp vụ
    let nights = 1;
    if (checkInStr && checkOutStr) {
        const date1 = new Date(checkInStr);
        const date2 = new Date(checkOutStr);
        const diffTime = Math.abs(date2 - date1);
        nights = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    }
    if (summaryNights) {
        summaryNights.textContent = nights;
    }

    // 3. Phân luồng kiểm tra khách ở luôn hôm nay hay đặt tương lai
    const todayObj = new Date();
    const yyyy = todayObj.getFullYear();
    const mm = String(todayObj.getMonth() + 1).padStart(2, '0');
    const dd = String(todayObj.getDate()).padStart(2, '0');
    const todayStr = `${yyyy}-${mm}-${dd}`;
    const isStayNow = (checkInStr === todayStr);

    // 4. Lắng nghe sự kiện click nút "Đặt ngay" trên từng hàng hạng phòng
    buttons.forEach(button => {
        button.addEventListener("click", function() {
            const typeId = this.getAttribute("data-typeid");
            const typeName = this.getAttribute("data-typename");
            
            const priceTd = this.closest("tr").querySelector(".room-price");
            const basePrice = parseFloat(priceTd.textContent.replace(/[^0-9]/g, ''));
            const numRooms = numRoomsSearchInput ? parseInt(numRoomsSearchInput.value) : 1;

            // ĐỒNG BỘ: Gán số lượng phòng tìm kiếm xuống ô input ẩn thuộc Form gửi đi để Servlet tiếp nhận
            const hiddenNumRooms = document.getElementById("numRooms");
            if (hiddenNumRooms) {
                hiddenNumRooms.value = numRooms;
            }

            if (displayRoomType) displayRoomType.textContent = typeName;
            if (formRoomTypeId) formRoomTypeId.value = typeId;
            if (formBasePrice) formBasePrice.value = basePrice;
            if (summaryPricePerNight) {
                summaryPricePerNight.textContent = basePrice.toLocaleString('vi-VN') + " đ";
            }

            const calculatedTotal = basePrice * numRooms * nights;
            if (summaryTotalAmount) {
                summaryTotalAmount.textContent = calculatedTotal.toLocaleString('vi-VN') + " đ";
            }

            // 5. Rẽ nhánh giao diện tài chính dựa vào biến cờ hiệu isStayNow
            if (isStayNow) {
                if (financialSection) financialSection.style.display = "none";
                if (depositSummaryRow) depositSummaryRow.style.display = "none";
                if (stayNowNotice) stayNowNotice.style.display = "block";
            } else {
                if (financialSection) financialSection.style.display = "block";
                if (depositSummaryRow) depositSummaryRow.style.display = "flex";
                if (stayNowNotice) stayNowNotice.style.display = "none";

                const calculatedDeposit = Math.round(calculatedTotal * 0.3);
                if (summaryDepositAmount) {
                    summaryDepositAmount.textContent = calculatedDeposit.toLocaleString('vi-VN') + " đ";
                }

                const memoText = "LAMER_DEPOSIT_T" + typeId + "_" + checkInStr.replace(/-/g, '');
                if (qrMemoSpan) qrMemoSpan.textContent = memoText;

                const bankId = "Vietcombank"; 
                const accountNo = "1023456789"; 
                const accountName = "CONG TY TNHH LA MER HOTEL";
                
                const qrUrl = `https://img.vietqr.io/image/${bankId}-${accountNo}-qr_only.png?amount=${calculatedDeposit}&addInfo=${encodeURIComponent(memoText)}&accountName=${encodeURIComponent(accountName)}`;
                
                const qrImg = document.getElementById("vietQrCode");
                if (qrImg) {
                    qrImg.src = qrUrl;
                }
            }

            if (bookingSection) {
                bookingSection.style.display = "block";
                bookingSection.scrollIntoView({ behavior: 'smooth' });
            }
        });
    });

    // ==========================================================================
    // KHU VỰC CHỨC NĂNG VALIDATE THÔNG TIN FORM KHÁCH HÀNG (HIỂN THỊ LỖI INLINE)
    // ==========================================================================
    const walkInForm = document.getElementById("walkInForm");
    const nameInput = document.getElementById("fullName");
    const nameError = document.getElementById("name-error-msg");
    const phoneInput = document.getElementById("phone");
    const phoneError = document.getElementById("phone-error-msg");
    const emailInput = document.getElementById("email");
    const emailError = document.getElementById("email-error-msg");
    const dobInput = document.getElementById("dateOfBirth");
    const dobError = document.getElementById("dob-error-msg");

    const idNumberInput = document.getElementsByName("idNumber")[0];
    let idNumberError = document.getElementById("idNumber-error-msg");
    if (!idNumberError && idNumberInput) {
        idNumberError = document.createElement("span");
        idNumberError.id = "idNumber-error-msg";
        idNumberError.className = "form-error-inline";
        idNumberInput.parentNode.appendChild(idNumberError);
    }

    // 1. Kiểm tra Họ và tên (Chấp nhận mọi định dạng chữ số, không để trống)
    function validateName() {
        if (!nameInput || !nameError) return true;
        const value = nameInput.value.trim();
        
        if (!value) {
            nameInput.classList.add("input-error-border");
            nameError.textContent = "Họ và tên không được phép để trống!";
            nameError.style.display = "block";
            return false;
        }
        
        nameInput.classList.remove("input-error-border");
        nameError.style.display = "none";
        return true;
    }

    // 2. Kiểm tra Số điện thoại (Bắt buộc 10 số, chuẩn đầu số VN)
    function validatePhone() {
        if (!phoneInput || !phoneError) return true;
        const value = phoneInput.value.trim();
        const phoneRegex = /^(0[3|5|7|8|9])[0-9]{8}$/;
        
        if (!value) {
            phoneInput.classList.add("input-error-border");
            phoneError.textContent = "Số điện thoại không được để trống!";
            phoneError.style.display = "block";
            return false;
        }
        
        if (!phoneRegex.test(value)) {
            phoneInput.classList.add("input-error-border");
            phoneError.textContent = "Số điện thoại phải gồm 10 chữ số và bắt đầu bằng đầu số VN (03, 05, 07, 08, 09)!";
            phoneError.style.display = "block";
            return false;
        }
        
        phoneInput.classList.remove("input-error-border");
        phoneError.style.display = "none";
        return true;
    }

    // 3. Kiểm tra định dạng cấu trúc Email
    function validateEmail() {
        if (!emailInput || !emailError) return true;
        const value = emailInput.value.trim();
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        
        if (!value) {
            emailInput.classList.add("input-error-border");
            emailError.textContent = "Email không được phép để trống!";
            emailError.style.display = "block";
            return false;
        }
        
        if (!emailRegex.test(value)) {
            emailInput.classList.add("input-error-border");
            emailError.textContent = "Định dạng cấu trúc địa chỉ Email không hợp lệ!";
            emailError.style.display = "block";
            return false;
        }
        
        emailInput.classList.remove("input-error-border");
        emailError.style.display = "none";
        return true;
    }

    // 4. Kiểm tra điều kiện số tuổi (Đủ từ 18 tuổi trở lên)
    function validateAge() {
        if (!dobInput || !dobError) return true;
        if (!dobInput.value) {
            dobInput.classList.add("input-error-border");
            dobError.textContent = "Vui lòng chọn ngày sinh để xác minh số tuổi!";
            dobError.style.display = "block";
            return false;
        }

        const dob = new Date(dobInput.value);
        const today = new Date();
        
        let age = today.getFullYear() - dob.getFullYear();
        const monthDiff = today.getMonth() - dob.getMonth();
        const dayDiff = today.getDate() - dob.getDate();
        
        if (monthDiff < 0 || (monthDiff === 0 && dayDiff < 0)) {
            age--;
        }
        
        if (age < 18) {
            dobInput.classList.add("input-error-border");
            dobError.textContent = "Khách hàng đứng tên đặt phòng phải từ 18 tuổi trở lên!";
            dobError.style.display = "block";
            return false;
        } else {
            dobInput.classList.remove("input-error-border");
            dobError.style.display = "none";
            return true;
        }
    }

    // 5. Kiểm tra định dạng Số CCCD hoặc Hộ chiếu
    function validateIdNumber() {
        if (!idNumberInput || !idNumberError) return true;
        const value = idNumberInput.value.trim();
        
        if (!value) {
            idNumberInput.classList.remove("input-error-border");
            idNumberError.style.display = "none";
            return true;
        }
        
        const idRegex = /^([0-9]{12}|[A-Z][0-9]{7,8})$/;
        
        if (!idRegex.test(value)) {
            idNumberInput.classList.add("input-error-border");
            idNumberError.textContent = "Số CCCD (12 chữ số) hoặc Số hộ chiếu (Chữ hoa đầu + 7-8 số) không hợp lệ!";
            idNumberError.style.display = "block";
            return false;
        } else {
            idNumberInput.classList.remove("input-error-border");
            idNumberError.style.display = "none";
            return true;
        }
    }

    // Gán sự kiện lắng nghe kiểm tra dữ liệu tức thì
    if (nameInput) nameInput.addEventListener("input", validateName);
    if (phoneInput) phoneInput.addEventListener("input", validatePhone);
    if (emailInput) emailInput.addEventListener("input", validateEmail);
    if (dobInput) dobInput.addEventListener("change", validateAge);
    if (idNumberInput) idNumberInput.addEventListener("input", validateIdNumber);

    // Kiểm tra chặn gửi dữ liệu Form lên Server nếu tồn tại lỗi
    if (walkInForm) {
        walkInForm.addEventListener("submit", function(event) {
            const isNameValid = validateName();
            const isPhoneValid = validatePhone();
            const isEmailValid = validateEmail();
            const isAgeValid = validateAge();
            const isIdNumberValid = validateIdNumber();
            
            if (!isNameValid || !isPhoneValid || !isEmailValid || !isAgeValid || !isIdNumberValid) {
                event.preventDefault();
                
                if (!isNameValid) nameInput.focus();
                else if (!isPhoneValid) phoneInput.focus();
                else if (!isEmailValid) emailInput.focus();
                else if (!isAgeValid) dobInput.focus();
                else if (!isIdNumberValid) idNumberInput.focus();
                
                return false;
            }
        });
    }
});

function togglePaymentView(type) {
    const cashGuide = document.getElementById("cashPaymentGuide");
    const qrGateway = document.getElementById("qrPaymentGateway");
    
    if (type === 'cash') {
        if (cashGuide) cashGuide.style.display = "block";
        if (qrGateway) qrGateway.style.display = "none";
    } else {
        if (cashGuide) cashGuide.style.display = "none";
        if (qrGateway) qrGateway.style.display = "block";
    }
}
// ==========================================================================
    // 11. TỰ ĐỘNG BẬT POP-UP THÔNG BÁO THÀNH CÔNG CHO ĐƠN TƯƠNG LAI
    // ==========================================================================
    // ==========================================================================
    // 11. TỰ ĐỘNG BẬT POP-UP THÔNG BÁO THÀNH CÔNG CHO CẢ 2 LUỒNG ĐẶT PHÒNG
    // ==========================================================================
    const urlParams = new URLSearchParams(window.location.search);
    const status = urlParams.get('status');
    const bookingCode = urlParams.get('code');

    if ((status === 'future_success' || status === 'stay_now_success') && bookingCode) {
        // Xác định dòng mô tả ngắn dựa vào luồng đặt phòng
        const msgText = (status === 'stay_now_success') 
            ? "Hệ thống đã ghi nhận đơn đặt phòng nhận ngay hôm nay."
            : "Hệ thống đã ghi nhận đơn đặt giữ chỗ tương lai thành công.";

        // Tạo khối div Pop-up Modal phủ lên màn hình
        const popup = document.createElement("div");
        popup.className = "walkin-popup-overlay";
        popup.innerHTML = `
            <div class="walkin-popup-content">
                <h3>Lập Đơn Thành Công!</h3>
                <p>${msgText}</p>
                <div class="popup-code-box">
                    <span>Mã đặt phòng:</span>
                    <strong>${bookingCode}</strong>
                </div>
                <button type="button" class="popup-close-btn">
                    ${status === 'stay_now_success' ? 'Tiếp tục xếp phòng vật lý' : 'Đóng thông báo'}
                </button>
            </div>
        `;
        document.body.appendChild(popup);

        // Lắng nghe sự kiện bấm nút đóng Pop-up
        popup.querySelector(".popup-close-btn").addEventListener("click", function() {
            popup.remove();
            
            if (status === 'stay_now_success') {
                // Nếu là khách ở luôn -> Tắt Pop-up xong sẽ tự động chuyển hướng sang trang tiếp đón nhận phòng
                window.location.href = `${window.location.origin}${window.location.pathname.replace('/walk-in-booking', '')}/checkin?searchBookingCode=${bookingCode}&status=created_success`;
            } else {
                // Nếu là khách đặt tương lai -> Ở lại trang và xóa sạch tham số trên URL để tránh lặp Pop-up khi F5
                const cleanUrl = window.location.protocol + "//" + window.location.host + window.location.pathname;
                window.history.pushState({ path: cleanUrl }, '', cleanUrl);
            }
        });
    }