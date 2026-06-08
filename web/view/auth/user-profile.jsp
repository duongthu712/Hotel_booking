<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.StaffAccount"%>

<%
    StaffAccount staff = (StaffAccount) session.getAttribute("staff");

    if (staff == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    String username = staff.getUsername() != null ? staff.getUsername() : "";
    String fullName = staff.getFullName() != null ? staff.getFullName() : "";
    String email = staff.getEmail() != null ? staff.getEmail() : "";
    String phone = staff.getPhone() != null ? staff.getPhone() : "";
    String role = staff.getRole() != null ? staff.getRole() : "";
%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Hồ sơ cá nhân</title>

    <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/user-profile.css">
</head>
<body>

    <main class="profile-page">
        <div class="profile-container">
            <div class="profile-header">
                <p class="profile-eyebrow">TÀI KHOẢN CỦA TÔI</p>
                <h1>Hồ sơ nhân viên</h1>
                <p class="profile-subtitle">Thông tin cá nhân và thông tin liên hệ.</p>
            </div>
            <% if (request.getAttribute("error") != null) { %>
                <div class="message error-message">
                    <%= request.getAttribute("error") %>
                </div>
            <% } %>
            <% if (request.getAttribute("message") != null) { %>
                <div class="message success-message">
                    <%= request.getAttribute("message") %>
                </div>
            <% } %>
            <section class="profile-card">
                <h2>Thông tin nhân viên</h2>
                <form action="<%= request.getContextPath() %>/profile" method="post">
                    <div class="form-grid">
                        <div class="form-group">
                            <label>Tên đăng nhập</label>
                            <input type="text" value="<%= username %>" readonly>
                        </div>
                        <div class="form-group">
                            <label>Họ và tên</label>
                            <input type="text" name="fullName" value="<%= fullName %>" required>
                        </div>
                        <div class="form-group">
                            <label>Email</label>
                            <input type="email" name="email" value="<%= email %>" required>
                        </div>
                        <div class="form-group">
                            <label>Số điện thoại</label>
                            <input type="text" name="phone" value="<%= phone %>">
                        </div>
                        <div class="form-group">
                            <label>Vai trò</label>
                            <input type="text" value="<%= role %>" readonly>
                        </div>
                    </div>

                    <div class="button-row">
                        <button type="submit" class="btn-primary">Lưu thay đổi</button>
                    </div>
                </form>
            </section>
            <section class="profile-card">
                <div class="security-header">
                    <div>
                        <h2>Đổi mật khẩu</h2>
                        <p class="security-note">Chỉ cập nhật mật khẩu khi cần thiết.</p>
                    </div>
                    <button type="button" class="btn-outline" id="showPasswordBtn">
                        Đổi mật khẩu
                    </button>
                </div>
                
                <form action="<%= request.getContextPath() %>/profile/change-password"
                      method="post"
                      id="passwordForm"
                      class="hidden">
                    <div class="form-grid">
                        <div class="form-group full-width">
                            <label>Mật khẩu hiện tại</label>
                            <input type="password" name="currentPassword">
                        </div>
                        <div class="form-group">
                            <label>Mật khẩu mới</label>
                            <input type="password" name="newPassword">
                        </div>
                        <div class="form-group">
                            <label>Nhập lại mật khẩu mới</label>
                            <input type="password" name="confirmPassword">
                        </div>
                    </div>
                    <div class="button-row">
                        <button type="submit" class="btn-primary">Cập nhật mật khẩu</button>
                        <button type="button" class="btn-outline" id="cancelPasswordBtn">Hủy</button>
                    </div>
                </form>
            </section>

            <a class="back-link" href="<%= request.getContextPath() %>/login">
                Quay lại trang chính
            </a>
        </div>
    </main>

    <script>
        const showPasswordBtn = document.getElementById("showPasswordBtn");
        const cancelPasswordBtn = document.getElementById("cancelPasswordBtn");
        const passwordForm = document.getElementById("passwordForm");

        showPasswordBtn.addEventListener("click", function () {
            passwordForm.classList.remove("hidden");
            showPasswordBtn.style.display = "none";

            const passwordInputs = passwordForm.querySelectorAll("input");
            passwordInputs.forEach(input => input.required = true);
        });

        cancelPasswordBtn.addEventListener("click", function () {
            passwordForm.classList.add("hidden");
            showPasswordBtn.style.display = "inline-flex";

            const passwordInputs = passwordForm.querySelectorAll("input");
            passwordInputs.forEach(input => {
                input.required = false;
                input.value = "";
            });
        });
    </script>
</body>
</html>