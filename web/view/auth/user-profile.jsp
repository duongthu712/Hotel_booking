<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.StaffAccount"%>

<%
    StaffAccount staff = (StaffAccount) session.getAttribute("staff");

    if (staff == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    String username = staff.getUsername() != null ? staff.getUsername() : "";
<<<<<<< Updated upstream
    String fullName = staff.getFullName() != null ? staff.getFullName() : "";
    String email = staff.getEmail() != null ? staff.getEmail() : "";
    String phone = staff.getPhone() != null ? staff.getPhone() : "";
    String role = staff.getRole() != null ? staff.getRole() : "";
=======

    String fullName = request.getAttribute("fullNameValue") != null
            ? (String) request.getAttribute("fullNameValue")
            : (staff.getFullName() != null ? staff.getFullName() : "");

    String email = request.getAttribute("emailValue") != null
            ? (String) request.getAttribute("emailValue")
            : (staff.getEmail() != null ? staff.getEmail() : "");

    String phone = request.getAttribute("phoneValue") != null
            ? (String) request.getAttribute("phoneValue")
            : (staff.getPhone() != null ? staff.getPhone() : "");

    String role = staff.getRole() != null ? staff.getRole() : "";

    boolean showPasswordForm = request.getAttribute("showPasswordForm") != null;
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
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
=======

            <section class="profile-card" id="profile-section">
                <h2>Thông tin nhân viên</h2>

                <% if (request.getAttribute("profileError") != null) { %>
                    <div class="message error-message">
                        <%= request.getAttribute("profileError") %>
                    </div>
                <% } %>

                <% if (request.getAttribute("profileMessage") != null) { %>
                    <div class="message success-message">
                        <%= request.getAttribute("profileMessage") %>
                    </div>
                <% } %>

                <form action="<%= request.getContextPath() %>/profile#profile-section" method="post">
>>>>>>> Stashed changes
                    <div class="form-grid">
                        <div class="form-group">
                            <label>Tên đăng nhập</label>
                            <input type="text" value="<%= username %>" readonly>
                        </div>
<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
                        <div class="form-group">
                            <label>Họ và tên</label>
                            <input type="text" name="fullName" value="<%= fullName %>" required>
                        </div>
<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
                        <div class="form-group">
                            <label>Email</label>
                            <input type="email" name="email" value="<%= email %>" required>
                        </div>
<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
                        <div class="form-group">
                            <label>Số điện thoại</label>
                            <input type="text" name="phone" value="<%= phone %>">
                        </div>
<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
            <section class="profile-card">
=======

            <section class="profile-card" id="password-section">
>>>>>>> Stashed changes
                <div class="security-header">
                    <div>
                        <h2>Đổi mật khẩu</h2>
                        <p class="security-note">Chỉ cập nhật mật khẩu khi cần thiết.</p>
                    </div>
<<<<<<< Updated upstream
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
=======

                    <button type="button"
                            class="btn-outline"
                            id="showPasswordBtn"
                            style="<%= showPasswordForm ? "display:none;" : "" %>">
                        Đổi mật khẩu
                    </button>
                </div>

                <% if (request.getAttribute("passwordError") != null) { %>
                    <div class="message error-message password-message" id="passwordMessageBox">
                        <%= request.getAttribute("passwordError") %>
                    </div>
                <% } %>

                <% if (request.getAttribute("passwordMessage") != null) { %>
                    <div class="message success-message password-message" id="passwordMessageBox">
                        <%= request.getAttribute("passwordMessage") %>
                    </div>
                <% } %>

                <form action="<%= request.getContextPath() %>/profile/change-password#password-section"
                      method="post"
                      id="passwordForm"
                      class="<%= showPasswordForm ? "" : "hidden" %>">

                    <div class="form-grid">
                        <div class="form-group full-width">
                            <label>Mật khẩu hiện tại</label>
                            <input type="password" name="currentPassword" <%= showPasswordForm ? "required" : "" %>>
                        </div>

                        <div class="form-group">
                            <label>Mật khẩu mới</label>
                            <input type="password" name="newPassword" <%= showPasswordForm ? "required" : "" %>>
                        </div>

                        <div class="form-group">
                            <label>Nhập lại mật khẩu mới</label>
                            <input type="password" name="confirmPassword" <%= showPasswordForm ? "required" : "" %>>
                        </div>
                    </div>

>>>>>>> Stashed changes
                    <div class="button-row">
                        <button type="submit" class="btn-primary">Cập nhật mật khẩu</button>
                        <button type="button" class="btn-outline" id="cancelPasswordBtn">Hủy</button>
                    </div>
                </form>
            </section>
        </div>
    </main>

    <script>
        const showPasswordBtn = document.getElementById("showPasswordBtn");
        const cancelPasswordBtn = document.getElementById("cancelPasswordBtn");
        const passwordForm = document.getElementById("passwordForm");
<<<<<<< Updated upstream
=======
        const passwordMessageBox = document.getElementById("passwordMessageBox");
>>>>>>> Stashed changes

        showPasswordBtn.addEventListener("click", function () {
            passwordForm.classList.remove("hidden");
            showPasswordBtn.style.display = "none";

            const passwordInputs = passwordForm.querySelectorAll("input");
            passwordInputs.forEach(input => input.required = true);
<<<<<<< Updated upstream
=======

            if (passwordMessageBox) {
                passwordMessageBox.style.display = "none";
            }
>>>>>>> Stashed changes
        });

        cancelPasswordBtn.addEventListener("click", function () {
            passwordForm.classList.add("hidden");
            showPasswordBtn.style.display = "";

            const passwordInputs = passwordForm.querySelectorAll("input");
            passwordInputs.forEach(input => {
                input.required = false;
                input.value = "";
            });
<<<<<<< Updated upstream
=======

            if (passwordMessageBox) {
                passwordMessageBox.style.display = "none";
            }
>>>>>>> Stashed changes
        });
    </script>
</body>
</html>