document.addEventListener("DOMContentLoaded", function () {
    const isDiffCheckbox = document.getElementById("isDifferentGuest");
    const checkInForm = document.getElementById("checkInForm");
    const formFooter = document.querySelector(".form-footer");

    const errorContainer = document.createElement("div");
    errorContainer.className = "js-error-msg";
    errorContainer.style.color = "#dc2626";
    errorContainer.style.fontSize = "13.5px";
    errorContainer.style.fontWeight = "600";
    errorContainer.style.marginBottom = "15px";
    errorContainer.style.textAlign = "center";
    errorContainer.style.width = "100%";
    errorContainer.style.display = "none";
    
    if (formFooter) {
        formFooter.parentNode.insertBefore(errorContainer, formFooter);
    }

    function showJsError(message) {
        errorContainer.innerHTML = "⚠️ " + message;
        errorContainer.style.display = "block";
        errorContainer.scrollIntoView({ behavior: "smooth", block: "center" });
    }

    function clearJsError() {
        errorContainer.innerHTML = "";
        errorContainer.style.display = "none";
    }

    const originalProfile = {
        fullName: document.getElementsByName("idFullName")[0]?.value || "",
        phone: document.getElementsByName("idPhone")[0]?.value || "",
        email: document.getElementsByName("idEmail")[0]?.value || ""
    };

    if (isDiffCheckbox) {
        isDiffCheckbox.addEventListener("change", function () {
            const nameInput = document.getElementsByName("idFullName")[0];
            const phoneInput = document.getElementsByName("idPhone")[0];
            const emailInput = document.getElementsByName("idEmail")[0];
            const idNumberInput = document.getElementsByName("idNumber")[0];
            const dobInput = document.getElementsByName("dateOfBirth")[0];

            clearJsError();

            if (this.checked) {
                nameInput.value = ""; phoneInput.value = ""; emailInput.value = "";
                idNumberInput.value = ""; dobInput.value = "";
                nameInput.focus();
            } else {
                nameInput.value = originalProfile.fullName;
                phoneInput.value = originalProfile.phone;
                emailInput.value = originalProfile.email;
                idNumberInput.value = ""; dobInput.value = "";
            }
        });
    }

    if (checkInForm) {
        checkInForm.addEventListener("submit", function (e) {
            const action = e.submitter ? e.submitter.value : "";
            
            if (action === "cancel") {
                return;
            }

            clearJsError();
            
            const phone = document.getElementsByName("idPhone")[0]?.value.trim() || "";
            const idNumber = document.getElementsByName("idNumber")[0]?.value.trim() || "";
            const dobValue = document.getElementsByName("dateOfBirth")[0]?.value || "";

            const phoneRegex = /^(0[3|5|7|8|9])[0-9]{8}$/;
            if (phone !== "" && !phoneRegex.test(phone)) {
                showJsError("Định dạng số điện thoại không hợp lệ! (Phải gồm 10 chữ số và bắt đầu bằng đầu số VN như 03, 05, 07, 08, 09)");
                e.preventDefault();
                return;
            }

            const idRegex = /^([0-9]{12}|[A-Z][0-9]{7,8})$/;
            if (!idRegex.test(idNumber)) {
                showJsError("Số CCCD hoặc Hộ chiếu không đúng định dạng! (CCCD gồm 12 số, Hộ chiếu gồm 1 chữ cái hoa và 7-8 số)");
                e.preventDefault();
                return;
            }

            if (dobValue === "") {
                showJsError("Vui lòng chọn ngày sinh của khách lưu trú!");
                e.preventDefault();
                return;
            }

            const parts = dobValue.split('-');
            const dob = new Date(parts[0], parts[1] - 1, parts[2]); 
            const today = new Date();

            if (dob > today) {
                showJsError("Ngày sinh không thể lớn hơn ngày hôm nay!");
                e.preventDefault();
                return;
            }

            let age = today.getFullYear() - dob.getFullYear();
            const monthDiff = today.getMonth() - dob.getMonth();
            
            if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < dob.getDate())) {
                age--;
            }

            if (age < 18) {
                showJsError("Khách hàng đại diện làm thủ tục check-in phải từ 18 tuổi trở lên!");
                e.preventDefault();
                return;
            }
        });
    }

    const errorAlert = document.querySelector(".alert-danger");
    if (errorAlert) {
        setTimeout(function () {
            errorAlert.style.transition = "opacity 0.5s ease";
            errorAlert.style.opacity = "0";
            setTimeout(() => errorAlert.remove(), 500);
        }, 5000);
    }
});