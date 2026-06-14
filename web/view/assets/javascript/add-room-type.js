/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/javascript.js to edit this template
 */
document.addEventListener("DOMContentLoaded", function () {
    const container = document.getElementById("imageFieldsContainer");
    const btnAdd = document.getElementById("btnAddNewImageField");

    if (btnAdd && container) {
        btnAdd.addEventListener("click", function () {
            // Tạo khối bọc nhóm ảnh phụ mới
            const divGroup = document.createElement("div");
            divGroup.className = "img-group mb-2";

            // Chèn cấu trúc ô Input và nút Xóa phẳng chuẩn CSS Luxury
            divGroup.innerHTML = `
                <input type="url" name="imageUrls" class="input-field input-grow" placeholder="Nhập đường dẫn ảnh phụ..."/ required>
                <button type="button" class="btn-delete">Xóa</button>
            `;

            // Thêm khối mới vào container
            container.appendChild(divGroup);

            // Bắt sự kiện xóa cho nút vừa tạo độc lập
            const btnRemove = divGroup.querySelector(".btn-delete");
            btnRemove.addEventListener("click", function () {
                divGroup.remove();
            });
        });
    }
});

