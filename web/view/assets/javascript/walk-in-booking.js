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
    const qrDepositAmount = document.getElementById("qrDepositAmount");

    // Thu thập dữ liệu ngày và số phòng từ bộ lọc tìm kiếm phía trên
    const checkInInput = document.getElementById("checkIn");
    const checkOutInput = document.getElementById("checkOut");
    const numRoomsSearchInput = document.getElementById("searchNumRooms");

    // Ô nhập liệu công khai trong Form đặt phòng (Đã cấu hình readonly ở JSP)
    const formNumRoomsInput = document.getElementById("formNumRooms");
    const numGuestsInput = document.getElementById("numGuests");
    const numChildrenInput = document.getElementById("numChildren");

    // Các nhãn báo lỗi inline của bộ ba ô số lượng
    const guestsError = document.getElementById("guests-error-msg");
    const childrenError = document.getElementById("children-error-msg");

    let checkInStr = checkInInput ? checkInInput.value : "";
    let checkOutStr = checkOutInput ? checkOutInput.value : "";

    // Lưu trữ cấu hình định mức của hạng phòng đang chọn
    let currentMaxAdultsPerRoom = 2;
    let currentMaxChildrenPerRoom = 0;
    let currentBasePrice = 0;
    let currentTypeId = "";

    // Hàm tính toán lại tiền và validate sức chứa theo thời gian thực
    async function validateCapacityAndRecalculate() {
        const numRooms = formNumRoomsInput ? (parseInt(formNumRoomsInput.value) || 1) : 1;
        const numGuests = numGuestsInput ? (parseInt(numGuestsInput.value) || 0) : 0;
        const numChildren = numChildrenInput ? (parseInt(numChildrenInput.value) || 0) : 0;

        // Đồng bộ ngược lại ô input hidden "numRooms" cũ để Servlet không bị lỗi đọc thiếu parameter
        const hiddenNumRooms = document.getElementById("numRooms");
        if (hiddenNumRooms) {
            hiddenNumRooms.value = numRooms;
        }

        // Tính toán tổng sức chứa tối đa dựa vào số phòng hiện tại
        const totalMaxAdults = currentMaxAdultsPerRoom * numRooms;
        const totalMaxChildren = currentMaxChildrenPerRoom * numRooms;

        // Kiểm tra định mức số người lớn dựa trên số phòng cố định từ bộ lọc tìm kiếm
        if (guestsError && numGuestsInput) {
            if (numGuests > totalMaxAdults) {
                numGuestsInput.classList.add("input-error-border");
                guestsError.textContent = `Quá tải! ${numRooms} phòng chỉ chứa tối đa ${totalMaxAdults} người lớn. Vui lòng thay đổi "Số phòng cần thuê" ở bộ lọc phía trên và tìm kiếm lại!`;
                guestsError.style.color = "#c92a2a";
                guestsError.style.display = "block";
            } else {
                numGuestsInput.classList.remove("input-error-border");
                guestsError.textContent = `Hợp lệ (Tối đa ${totalMaxAdults} người lớn)`;
                guestsError.style.color = "#2f855a";
                guestsError.style.display = "block";
            }
        }

        // Kiểm tra định mức số trẻ em dựa trên số phòng cố định từ bộ lọc tìm kiếm
        if (childrenError && numChildrenInput) {
            if (numChildren > totalMaxChildren) {
                numChildrenInput.classList.add("input-error-border");
                childrenError.textContent = `Quá tải! Tối đa ${totalMaxChildren} trẻ em cho ${numRooms} phòng. Vui lòng thay đổi số phòng ở bộ lọc phía trên và tìm kiếm lại!`;
                childrenError.style.color = "#c92a2a";
                childrenError.style.display = "block";
            } else {
                numChildrenInput.classList.remove("input-error-border");
                childrenError.textContent = `Hợp lệ (Tối đa ${totalMaxChildren} trẻ em)`;
                childrenError.style.color = "#2f855a";
                childrenError.style.display = "block";
            }
        }

        // Gọi API backend để tính toán tài chính
        try {
            const url = `${window.location.origin}${window.location.pathname}?action=calculate&checkInDate=${checkInStr}&checkOutDate=${checkOutStr}&basePrice=${currentBasePrice}&numRooms=${numRooms}`;
            const response = await fetch(url);
            if (!response.ok) throw new Error("Lỗi gọi API tính toán");
            const data = await response.json();

            if (summaryNights) {
                summaryNights.textContent = data.nights;
            }
            const roomsAndNightsSpan = document.getElementById("summaryRoomsAndNights");
            if (roomsAndNightsSpan) {
                roomsAndNightsSpan.textContent = `${numRooms} phòng x ${data.nights} đêm`;
            }
            if (summaryTotalAmount) {
                summaryTotalAmount.textContent = parseFloat(data.roomCharges).toLocaleString('vi-VN') + " đ";
            }

            // Rẽ nhánh giao diện hiển thị tài chính cọc/ở ngay hôm nay
            if (data.isStayNow) {
                if (financialSection) financialSection.style.display = "none";
                if (depositSummaryRow) depositSummaryRow.style.display = "none";
                if (stayNowNotice) stayNowNotice.style.display = "block";
            } else {
                if (financialSection) financialSection.style.display = "block";
                if (depositSummaryRow) depositSummaryRow.style.display = "flex";
                if (stayNowNotice) stayNowNotice.style.display = "none";

                if (summaryDepositAmount) {
                    summaryDepositAmount.textContent = parseFloat(data.depositAmount).toLocaleString('vi-VN') + " đ";
                }

                // Đồng bộ số tiền đặt cọc vào bảng thông tin ngân hàng hiển thị công khai
                if (qrDepositAmount) {
                    qrDepositAmount.textContent = parseFloat(data.depositAmount).toLocaleString('vi-VN') + " đ";
                }

                // Lấy mã đơn hàng tạm thời gán lên khung nội dung chuyển khoản
                const generatedCode = document.getElementById("walkInForm").querySelector("input[name='bookingCode']")?.value || "LAMER";
                if (qrMemoSpan) {
                    qrMemoSpan.textContent = generatedCode;
                }
            }
        } catch (error) {
            console.error("Lỗi khi fetch dữ liệu tính toán:", error);
        }
    }

    // 4. Lắng nghe sự kiện click nút "Đặt ngay" trên từng hàng hạng phòng
    buttons.forEach(button => {
        button.addEventListener("click", function() {
            currentTypeId = this.getAttribute("data-typeid");
            const typeName = this.getAttribute("data-typename");
            currentMaxAdultsPerRoom = parseInt(this.getAttribute("data-maxadults")) || 2;
            currentMaxChildrenPerRoom = parseInt(this.getAttribute("data-maxchildren")) || 0;
            
            const priceTd = this.closest("tr").querySelector(".room-price");
            currentBasePrice = parseFloat(priceTd.textContent.replace(/[^0-9]/g, ''));
            const numRooms = numRoomsSearchInput ? parseInt(numRoomsSearchInput.value) : 1;

            // Đồng bộ dữ liệu khởi tạo xuống form hiển thị phía dưới (Khóa cứng không cho sửa đổi tự phát)
            if (formNumRoomsInput) formNumRoomsInput.value = numRooms;
            if (displayRoomType) displayRoomType.textContent = typeName;
            if (formRoomTypeId) formRoomTypeId.value = currentTypeId;
            if (formBasePrice) formBasePrice.value = currentBasePrice;
            if (summaryPricePerNight) {
                summaryPricePerNight.textContent = currentBasePrice.toLocaleString('vi-VN') + " đ";
            }

            // Chạy kiểm tra định mức và tính tiền lần đầu tiên khi mở form
            validateCapacityAndRecalculate();

            if (bookingSection) {
                bookingSection.style.display = "block";
                bookingSection.scrollIntoView({ behavior: 'smooth' });
            }
        });
    });

    // Lắng nghe sự thay đổi động của 2 ô cấu hình số khách (Bỏ formNumRoomsInput ra để ép search)
    if (numGuestsInput) numGuestsInput.addEventListener("input", validateCapacityAndRecalculate);
    if (numChildrenInput) numChildrenInput.addEventListener("input", validateCapacityAndRecalculate);


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

    // Kiểm tra chặn gửi dữ liệu Form lên Server nếu tồn tại bất kỳ lỗi nào kể cả quá tải phòng
    if (walkInForm) {
        walkInForm.addEventListener("submit", function(event) {
            const isNameValid = validateName();
            const isPhoneValid = validatePhone();
            const isEmailValid = validateEmail();
            const isAgeValid = validateAge();
            const isIdNumberValid = validateIdNumber();

            // Khóa chặn submit nếu số khách vượt sức chứa cố định của số phòng thuê từ bộ lọc search
            const numRooms = formNumRoomsInput ? (parseInt(formNumRoomsInput.value) || 1) : 1;
            const numGuests = numGuestsInput ? (parseInt(numGuestsInput.value) || 0) : 0;
            const numChildren = numChildrenInput ? (parseInt(numChildrenInput.value) || 0) : 0;

            const totalMaxAdults = currentMaxAdultsPerRoom * numRooms;
            const totalMaxChildren = currentMaxChildrenPerRoom * numRooms;

            let isCapacityValid = true;
            if (numGuests > totalMaxAdults) {
                alert(`Số lượng người lớn vượt quá sức chứa tối đa của ${numRooms} phòng (${totalMaxAdults} người). Vui lòng đổi "Số phòng cần thuê" ở bộ lọc phía trên màn hình và bấm tìm kiếm lại!`);
                if (numGuestsInput) numGuestsInput.focus();
                isCapacityValid = false;
            } else if (numChildren > totalMaxChildren) {
                alert(`Số lượng trẻ em vượt quá giới hạn sức chứa tối đa của ${numRooms} phòng (${totalMaxChildren} trẻ). Vui lòng đổi "Số phòng cần thuê" ở bộ lọc phía trên màn hình và bấm tìm kiếm lại!`);
                if (numChildrenInput) numChildrenInput.focus();
                isCapacityValid = false;
            }
            
            if (!isNameValid || !isPhoneValid || !isEmailValid || !isAgeValid || !isIdNumberValid || !isCapacityValid) {
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
// 11. TỰ ĐỘNG BẬT POP-UP THÔNG BÁO THÀNH CÔNG CHO CẢ 2 LUỒNG ĐẶT PHÒNG
// ==========================================================================
const urlParams = new URLSearchParams(window.location.search);
const status = urlParams.get('status');
const bookingCode = urlParams.get('code');

if ((status === 'future_success' || status === 'stay_now_success') && bookingCode) {
    const msgText = (status === 'stay_now_success') 
        ? "Hệ thống đã ghi nhận đơn đặt phòng nhận ngay hôm nay."
        : "Hệ thống đã ghi nhận đơn đặt giữ chỗ tương lai thành công.";

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

    popup.querySelector(".popup-close-btn").addEventListener("click", function() {
        popup.remove();
        
        if (status === 'stay_now_success') {
            window.location.href = `${window.location.origin}${window.location.pathname.replace('/walk-in-booking', '')}/checkin?searchBookingCode=${bookingCode}&status=created_success`;
        } else {
            const cleanUrl = window.location.protocol + "//" + window.location.host + window.location.pathname;
            window.history.pushState({ path: cleanUrl }, '', cleanUrl);
        }
    });
}