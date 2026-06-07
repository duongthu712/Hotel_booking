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
<html>
<head>
    <meta charset="UTF-8">
    <title>User Profile</title>

    <link rel="stylesheet" href="<%= request.getContextPath() %>/view/assets/css/user-profile.css">
</head>
<body>

    <main class="profile-page">
        <div class="profile-container">

            <div class="profile-header">
                <p class="profile-eyebrow">MY ACCOUNT</p>
                <h1>Staff profile</h1>
                <p class="profile-subtitle">Personal and contact information.</p>
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
                <h2>Profile information</h2>

                <form action="<%= request.getContextPath() %>/profile" method="post">
                    <div class="form-grid">

                        <div class="form-group">
                            <label>Username</label>
                            <input type="text" value="<%= username %>" readonly>
                        </div>

                        <div class="form-group">
                            <label>Full name</label>
                            <input type="text" name="fullName" value="<%= fullName %>" required>
                        </div>

                        <div class="form-group">
                            <label>Email</label>
                            <input type="email" name="email" value="<%= email %>" required>
                        </div>

                        <div class="form-group">
                            <label>Phone</label>
                            <input type="text" name="phone" value="<%= phone %>">
                        </div>

                        <div class="form-group">
                            <label>Role</label>
                            <input type="text" value="<%= role %>" readonly>
                        </div>

                    </div>

                    <div class="button-row">
                        <button type="submit" class="btn-primary">Save changes</button>
                    </div>
                </form>
            </section>

            <section class="profile-card">
                <div class="security-header">
                    <div>
                        <h2>Change password</h2>
                        <p class="security-note">Update your password only when needed.</p>
                    </div>

                    <button type="button" class="btn-outline" id="showPasswordBtn">
                        Change password
                    </button>
                </div>

                <form action="<%= request.getContextPath() %>/profile/change-password"
                      method="post"
                      id="passwordForm"
                      class="hidden">

                    <div class="form-grid">

                        <div class="form-group full-width">
                            <label>Current password</label>
                            <input type="password" name="currentPassword">
                        </div>

                        <div class="form-group">
                            <label>New password</label>
                            <input type="password" name="newPassword">
                        </div>

                        <div class="form-group">
                            <label>Confirm new password</label>
                            <input type="password" name="confirmPassword">
                        </div>

                    </div>

                    <div class="button-row">
                        <button type="submit" class="btn-primary">Update password</button>
                        <button type="button" class="btn-outline" id="cancelPasswordBtn">Cancel</button>
                    </div>
                </form>
            </section>

            <a class="back-link" href="<%= request.getContextPath() %>/login">
                Back to dashboard
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