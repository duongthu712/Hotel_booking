package controller;

import dao.StaffAccountDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.StaffAccount;

public class LoginController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Hiển thị trang đăng nhập và lấy thông báo một lần từ session.
        HttpSession session = request.getSession(false);

        if (session != null) {
            Object successMessage = session.getAttribute("loginSuccessMessage");

            if (successMessage != null) {
                request.setAttribute("success", successMessage.toString());
                session.removeAttribute("loginSuccessMessage");
            }

            Object errorMessage = session.getAttribute("loginErrorMessage");

            if (errorMessage != null) {
                request.setAttribute("error", errorMessage.toString());
                session.removeAttribute("loginErrorMessage");
            }

            Object loginUsername = session.getAttribute("loginUsername");

            if (loginUsername != null) {
                request.setAttribute("username", loginUsername.toString());
                session.removeAttribute("loginUsername");
            }
        }

        String showLogin = request.getParameter("showLogin");

        if ("true".equalsIgnoreCase(showLogin)) {
            request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
            return;
        }

        if (session != null && session.getAttribute("staff") != null) {
            StaffAccount staff = (StaffAccount) session.getAttribute("staff");
            redirectByRole(request, response, staff);
            return;
        }

        request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Kiểm tra thông tin đăng nhập và chuyển hướng theo vai trò.
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        username = username != null ? username.trim() : "";
        password = password != null ? password : "";

        if (username.isEmpty()) {
            redirectWithError(request, response, "Vui lòng nhập tên đăng nhập.", "");
            return;
        }

        if (password.isEmpty()) {
            redirectWithError(request, response, "Vui lòng nhập mật khẩu.", username);
            return;
        }

        try {
            StaffAccountDAO dao = new StaffAccountDAO();
            StaffAccount staff = dao.loginWithHashCheck(username, password);

            if (staff == null) {
                redirectWithError(request, response,
                        "Tên đăng nhập hoặc mật khẩu không đúng.", username);
                return;
            }

            if (!staff.isActive()) {
                redirectWithError(request, response, "Tài khoản đã bị khóa.", username);
                return;
            }

            String role = staff.getRole();

            if (role == null || role.trim().isEmpty()) {
                redirectWithError(request, response,
                        "Tài khoản chưa được phân quyền.", username);
                return;
            }

            HttpSession session = request.getSession();

            session.setMaxInactiveInterval(60 * 60);
            session.setAttribute("staff", staff);
            session.setAttribute("staffId", staff.getStaffId());
            session.setAttribute("staffRole", staff.getRole());

            session.removeAttribute("loginErrorMessage");
            session.removeAttribute("loginUsername");
            session.removeAttribute("loginSuccessMessage");

            redirectByRole(request, response, staff);

        } catch (Exception exception) {
            exception.printStackTrace();
            redirectWithError(request, response, "Lỗi hệ thống. Vui lòng thử lại.", username);
        }
    }

    private void redirectWithError(HttpServletRequest request, HttpServletResponse response,
            String errorMessage, String username) throws IOException {

        // Lưu thông báo lỗi tạm thời rồi chuyển về trang đăng nhập.
        HttpSession session = request.getSession();

        session.setAttribute("loginErrorMessage", errorMessage);
        session.setAttribute("loginUsername", username);

        response.sendRedirect(request.getContextPath() + "/login");
    }

    private void redirectByRole(HttpServletRequest request, HttpServletResponse response,
            StaffAccount staff) throws IOException {

        // Chuyển người dùng đến trang tương ứng với vai trò.
        String role = staff.getRole();

        if (role.equalsIgnoreCase("Lễ tân")) {
            response.sendRedirect(request.getContextPath() + "/receptionist-dashboard");

        } else if (role.equalsIgnoreCase("Quản lý")) {
            response.sendRedirect(request.getContextPath() + "/ManagerDashboard");

        } else if (role.equalsIgnoreCase("Administrator")
                || role.equalsIgnoreCase("Admin")
                || role.equalsIgnoreCase("Quản trị viên")) {

            response.sendRedirect(request.getContextPath() + "/StaffAccountList");

        } else {
            HttpSession session = request.getSession();

            session.removeAttribute("staff");
            session.removeAttribute("staffId");
            session.removeAttribute("staffRole");

            session.setAttribute("loginErrorMessage", "Vai trò không hợp lệ: " + role);

            response.sendRedirect(request.getContextPath() + "/login");
        }
    }

    @Override
    public String getServletInfo() {

        // Trả về mô tả của servlet.
        return "Login Controller";
    }
}
