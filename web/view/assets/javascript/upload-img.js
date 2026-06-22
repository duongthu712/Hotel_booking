document.addEventListener('DOMContentLoaded', function () {
    const cloudName = 'dkrhg3g2v';
    const uploadPreset = 'ml_default';


    document.addEventListener('change', function (e) {
        if (e.target && e.target.type === 'file') {
            const fileInput = e.target;

            let fileNameContainer = fileInput.parentNode.querySelector('.file-name-display');
            if (!fileNameContainer) {
                fileNameContainer = document.createElement('span');
                fileNameContainer.className = 'file-name-display';
                fileNameContainer.style.marginLeft = '10px';
                fileNameContainer.style.fontSize = '14px';
                fileNameContainer.style.color = 'var(--text-muted)';
                fileNameContainer.style.fontFamily = 'var(--font-logo)';
                fileInput.parentNode.insertBefore(fileNameContainer, fileInput.nextSibling);
            }

        }
    });

    document.addEventListener('submit', async function (e) {
        const form = e.target;

        const fileInput = form.querySelector('input[type="file"]');

        const urlInput = form.querySelector('input[name="imageUrl"]') || form.querySelector('[id*="url"]');

        if (fileInput && fileInput.files.length > 0 && urlInput) {
            e.preventDefault(); // Chặn form lại để đợi up ảnh

            const file = fileInput.files[0];
            const formData = new FormData();
            formData.append('file', file);
            formData.append('upload_preset', uploadPreset);

            // Đổi trạng thái nút submit của chính form đó
            const submitBtn = form.querySelector('button[type="submit"]') || form.querySelector('.btn-submit');
            const originalBtnText = submitBtn ? submitBtn.innerText : "Lưu";
            if (submitBtn) {
                submitBtn.innerText = "Đang tải ảnh...";
                submitBtn.disabled = true;
            }

            try {
                const response = await fetch(`https://api.cloudinary.com/v1_1/${cloudName}/image/upload`, {
                    method: 'POST',
                    body: formData
                });

                const data = await response.json();

                if (data.secure_url) {
                    // Tự động điền link vào ô nhận link của form đó
                    urlInput.value = data.secure_url;
                    form.submit();
                } else {
                    alert('Tải ảnh thất bại!');
                    if (submitBtn) {
                        submitBtn.innerText = originalBtnText;
                        submitBtn.disabled = false;
                    }
                }
            } catch (error) {
                console.error('Error uploading:', error);
                alert('Lỗi kết nối khi tải ảnh!');
                if (submitBtn) {
                    submitBtn.innerText = originalBtnText;
                    submitBtn.disabled = false;
                }
            }
        }
    });
});