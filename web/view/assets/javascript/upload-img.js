document.getElementById('image-edit-form').addEventListener('submit', async function (e) {
    const fileInput = document.getElementById('proofImage');
    const urlInput = document.getElementById('edit-image-url');

    // Chỉ xử lý nếu người dùng có chọn file từ máy
    if (fileInput && fileInput.files.length > 0) {
        e.preventDefault(); // Chặn việc submit form lại để chờ up ảnh

        const file = fileInput.files[0];
        const formData = new FormData();
        formData.append('file', file);

        // Cấu hình tài khoản Cloudinary
        formData.append('upload_preset', 'ml_default');
        const cloudName = 'dkrhg3g2v';

        const submitBtn = this.querySelector('.btn-submit');
        const originalBtnText = submitBtn.innerText;
        submitBtn.innerText = "Đang tải ảnh...";
        submitBtn.disabled = true;

        try {
            // 1. Đẩy file ảnh lên Cloudinary
            const response = await fetch(`https://api.cloudinary.com/v1_1/${cloudName}/image/upload`, {
                method: 'POST',
                body: formData
            });

            const data = await response.json();

            if (data.secure_url) {
                urlInput.value = data.secure_url;
                this.submit();
            } else {
                alert('Tải ảnh thất bại!');
                submitBtn.innerText = originalBtnText;
                submitBtn.disabled = false;
            }
        } catch (error) {
            console.error('Error:', error);
            alert('Lỗi kết nối khi tải ảnh!');
            submitBtn.innerText = originalBtnText;
            submitBtn.disabled = false;
        }
    }
});

