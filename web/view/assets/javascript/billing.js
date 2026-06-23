 const fromDateInput = document.querySelector('input[name="fromDate"]');
    const toDateInput   = document.querySelector('input[name="toDate"]');
    const filterForm    = document.querySelector('form.filter-bar');

    toDateInput.addEventListener('change', function () {
        if (fromDateInput.value && this.value < fromDateInput.value) {
            alert('Ngày kết thúc không được trước ngày bắt đầu.');
            this.value = fromDateInput.value; // reset về bằng fromDate
        }
    });

    fromDateInput.addEventListener('change', function () {
        if (toDateInput.value && toDateInput.value < this.value) {
            toDateInput.value = this.value; // kéo toDate lên bằng fromDate
        }
        toDateInput.min = this.value; // chặn picker không cho chọn trước fromDate
    });

    filterForm.addEventListener('submit', function (e) {
        if (fromDateInput.value && toDateInput.value 
                && toDateInput.value < fromDateInput.value) {
            e.preventDefault();
            alert('Ngày kết thúc không được trước ngày bắt đầu.');
        }
    });