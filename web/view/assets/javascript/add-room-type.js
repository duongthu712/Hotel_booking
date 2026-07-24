/**
 * Author: ThuDNM-HE204370
 * Date created: 16/06/2026
 * Purpose: JavaScript logic for add room type.
 */

document.addEventListener("DOMContentLoaded", function () {

    const container = document.getElementById("imageFieldsContainer");
    const btnAdd = document.getElementById("btnAddNewImageField");
    function bindUpload(fileInput, previewImg, urlInput, fileName) {

        if (!fileInput || !previewImg || !urlInput) {
            return;
        }

        fileInput.addEventListener("change", function () {

            const file = this.files[0];

            if (!file) {
                return;
            }

            if (!file.type.startsWith("image/")) {
                alert("Vui lòng chọn file ảnh.");
                this.value = "";
                return;
            }

            const reader = new FileReader();

            reader.onload = function (e) {

                previewImg.src = e.target.result;
                previewImg.style.display = "block";

                // Base64 đưa vào input để Servlet lấy request.getParameterValues()
                urlInput.value = e.target.result;

                if (fileName) {
                    fileName.textContent = file.name;
                }

            };

            reader.readAsDataURL(file);

        });

    }
    bindUpload(
            document.getElementById("mainImageFile"),
            document.getElementById("mainPreview"),
            document.getElementById("mainImageUrl"),
            document.getElementById("mainImageName")
            );
    document.querySelectorAll(".img-group").forEach(function (group) {

        const file = group.querySelector(".sub-image-file");
        const preview = group.querySelector(".preview-image");
        const input = group.querySelector(".image-url-hidden");
        const fileName = group.querySelector(".file-name");

        if (file && preview && input) {
            bindUpload(file, preview, input, fileName);
        }

        const btnDelete = group.querySelector(".btn-delete");

        if (btnDelete) {
            btnDelete.addEventListener("click", function () {
                group.remove();
            });
        }

    });
    if (btnAdd && container) {

        btnAdd.addEventListener("click", function () {

            const div = document.createElement("div");

            div.className = "img-group mb-2";

            div.innerHTML = `
                <div class="input-section">

                    <input type="file"
                           class="sub-image-file mb-2"
                           accept="image/*">

                    <span class="file-name"
                          style="margin-left:10px;color:#666"></span>

                    <div class="mt-2 mb-2">
                        <img class="preview-image"
                             style="display:none;
                                    width:180px;
                                    border-radius:8px;
                                    border:1px solid #ddd;">
                    </div>

                    <div style="display:flex;gap:10px;align-items:center;">

                        <input type="text"
                               name="imageUrls"
                               class="image-url-hidden input-field"
            placeholder="Nhập đường dẫn ảnh phụ (URL)..." 
                               readonly>

                        <button type="button"
                                class="btn-delete">
                            Xóa
                        </button>

                    </div>

                </div>
            `;

            container.appendChild(div);

            bindUpload(
                    div.querySelector(".sub-image-file"),
                    div.querySelector(".preview-image"),
                    div.querySelector(".image-url-hidden"),
                    div.querySelector(".file-name")
                    );

            div.querySelector(".btn-delete").addEventListener("click", function () {
                div.remove();
            });

        });

    }

});