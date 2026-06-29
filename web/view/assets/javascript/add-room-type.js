document.addEventListener("DOMContentLoaded", function () {

    const container = document.getElementById("imageFieldsContainer");
    const btnAdd = document.getElementById("btnAddNewImageField");

    // Hàm xử lý chung: Đọc file, hiện preview và điền chuỗi Base64 vào input
    function bindUpload(fileInput, preview, hiddenInput) {
        fileInput.addEventListener("change", function () {
            const file = this.files[0];
            if (!file) return;

            if (!file.type.startsWith("image/")) {
                alert("Vui lòng chọn file ảnh hợp lệ.");
                this.value = "";
                return;
            }

            const reader = new FileReader();
            reader.onload = function (e) {
                // Hiển thị ảnh preview
                preview.src = e.target.result;
                preview.style.display = "block";
                
                // Gán chuỗi Base64 vào ô input URL
                hiddenInput.value = e.target.result;
            };
            reader.readAsDataURL(file);
        });
    }

    // 1. Xử lý ảnh chính (Main Image)
    const mainFile = document.getElementById("mainImageFile");
    const mainPreview = document.getElementById("mainPreview");
    const mainUrl = document.getElementById("mainImageUrl");

    if (mainFile && mainPreview && mainUrl) {
        bindUpload(mainFile, mainPreview, mainUrl);
    }

    // 2. Xử lý ảnh phụ hiện có (nếu có từ trước khi load trang)
    document.querySelectorAll(".img-group").forEach(group => {
        const file = group.querySelector(".sub-image-file");
        const preview = group.querySelector(".preview-image");
        const hidden = group.querySelector(".image-url-hidden");
        
        if (file && preview && hidden) {
            bindUpload(file, preview, hidden);
        }
    });

    // 3. Xử lý sự kiện thêm ảnh phụ mới
    if (btnAdd && container) {
        btnAdd.addEventListener("click", function () {
            const div = document.createElement("div");
            div.className = "img-group mb-2";
            
            div.innerHTML = `
                <div style="flex-grow: 1;">
                    <input type="file" class="sub-image-file mb-2" accept="image/*">
                    
                    <div class="mt-1 mb-2">
                        <img class="preview-image" style="display:none; width: 260px; border-radius: 8px; border: 1px solid #ddd;">
                    </div>
                    
                    <input type="text" name="imageUrls" class="image-url-hidden input-field" 
                           placeholder="Nhập đường dẫn ảnh phụ (URL)...">
                </div>
                <button type="button" class="btn-delete">Xóa</button>
            `;

            container.appendChild(div);

            // Gán sự kiện cho input file mới thêm vào
            bindUpload(
                div.querySelector(".sub-image-file"),
                div.querySelector(".preview-image"),
                div.querySelector(".image-url-hidden")
            );

            // Gán sự kiện cho nút xóa
            div.querySelector(".btn-delete").addEventListener("click", function () {
                div.remove();
            });
        });
    }
    
    // Gán sự kiện xóa cho các nút Xóa đã có sẵn từ trước (khi load trang)
    document.querySelectorAll(".btn-delete").forEach(btn => {
        btn.addEventListener("click", function () {
            this.parentElement.remove();
        });
    });

});