package controller;

import dao.StaffAccountDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.StaffAccount;

public class UserProfileController extends HttpServlet {

    private static final String EMAIL_VALID = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String PHONE_VALID = "^0(3|5|7|8|9)[0-9]{8}$";
    private static final String NAME_VALID = "^[\\p{L}\\s'.-]+$";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("staff") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.getRequestDispatcher("/view/auth/user-profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("staff") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        StaffAccount staff = (StaffAccount) session.getAttribute("staff");

        String oldFullName = staff.getFullName() != null
                ? staff.getFullName().trim().replaceAll("\\s+", " ")
                : "";

        String oldEmail = staff.getEmail() != null
                ? staff.getEmail().trim().toLowerCase()
                : "";

        String oldPhone = staff.getPhone() != null
                ? staff.getPhone().trim().replaceAll("\\s+", "")
                : "";

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");

        fullName = fullName != null ? fullName.trim().replaceAll("\\s+", " ") : "";
        email = email != null ? email.trim().toLowerCase() : "";
        phone = phone != null ? phone.trim().replaceAll("\\s+", "") : "";

        if (fullName.isEmpty()) {
            request.setAttribute("profileError", "Họ và tên không được để trống.");
            setOldProfileValues(request, oldFullName, oldEmail, oldPhone);
            request.getRequestDispatcher("/view/auth/user-profile.jsp").forward(request, response);
            return;
        }

        if (fullName.length() < 2 || fullName.length() > 100) {
            request.setAttribute("profileError", "Họ và tên phải từ 2 đến 100 ký tự.");
            setOldProfileValues(request, oldFullName, oldEmail, oldPhone);
            request.getRequestDispatcher("/view/auth/user-profile.jsp").forward(request, response);
            return;
        }

        if (!fullName.matches(NAME_VALID)) {
            request.setAttribute("profileError", "Họ và tên chỉ được chứa chữ cái, khoảng trắng và một số dấu hợp lệ.");
            setOldProfileValues(request, oldFullName, oldEmail, oldPhone);
            request.getRequestDispatcher("/view/auth/user-profile.jsp").forward(request, response);
            return;
        }

        if (email.isEmpty()) {
            request.setAttribute("profileError", "Email không được để trống.");
            setOldProfileValues(request, oldFullName, oldEmail, oldPhone);
            request.getRequestDispatcher("/view/auth/user-profile.jsp").forward(request, response);
            return;
        }

        if (email.length() > 100) {
            request.setAttribute("profileError", "Email không được vượt quá 100 ký tự.");
            setOldProfileValues(request, oldFullName, oldEmail, oldPhone);
            request.getRequestDispatcher("/view/auth/user-profile.jsp").forward(request, response);
            return;
        }

        if (!email.matches(EMAIL_VALID)) {
            request.setAttribute("profileError", "Email không đúng định dạng.");
            setOldProfileValues(request, oldFullName, oldEmail, oldPhone);
            request.getRequestDispatcher("/view/auth/user-profile.jsp").forward(request, response);
            return;
        }

        if (email.startsWith(".") || email.contains("..")) {
            request.setAttribute("profileError", "Email không hợp lệ.");
            setOldProfileValues(request, oldFullName, oldEmail, oldPhone);
            request.getRequestDispatcher("/view/auth/user-profile.jsp").forward(request, response);
            return;
        }

        if (!phone.isEmpty()) {
            if (!phone.matches("[0-9]+")) {
                request.setAttribute("profileError", "Số điện thoại chỉ được chứa chữ số.");
                setOldProfileValues(request, oldFullName, oldEmail, oldPhone);
                request.getRequestDispatcher("/view/auth/user-profile.jsp").forward(request, response);
                return;
            }

            if (phone.length() != 10) {
                request.setAttribute("profileError", "Số điện thoại phải gồm đúng 10 chữ số.");
                setOldProfileValues(request, oldFullName, oldEmail, oldPhone);
                request.getRequestDispatcher("/view/auth/user-profile.jsp").forward(request, response);
                return;
            }

            if (!phone.matches(PHONE_VALID)) {
                request.setAttribute("profileError", "Số điện thoại không đúng định dạng. Số điện thoại Việt Nam phải bắt đầu bằng 03, 05, 07, 08 hoặc 09.");
                setOldProfileValues(request, oldFullName, oldEmail, oldPhone);
                request.getRequestDispatcher("/view/auth/user-profile.jsp").forward(request, response);
                return;
            }

            if (phone.matches("^(\\d)\\1+$")) {
                request.setAttribute("profileError", "Số điện thoại không hợp lệ.");
                setOldProfileValues(request, oldFullName, oldEmail, oldPhone);
                request.getRequestDispatcher("/view/auth/user-profile.jsp").forward(request, response);
                return;
            }

            String subscriberPart = phone.substring(2);

            if (subscriberPart.matches("^(\\d)\\1+$")) {
                request.setAttribute("profileError", "Số điện thoại không hợp lệ.");
                setOldProfileValues(request, oldFullName, oldEmail, oldPhone);
                request.getRequestDispatcher("/view/auth/user-profile.jsp").forward(request, response);
                return;
            }

            if (phone.equals("0123456789")
                    || phone.equals("0987654321")
                    || phone.equals("0900000000")
                    || phone.equals("0911111111")
                    || phone.equals("0922222222")
                    || phone.equals("0933333333")
                    || phone.equals("0944444444")
                    || phone.equals("0955555555")
                    || phone.equals("0966666666")
                    || phone.equals("0977777777")
                    || phone.equals("0988888888")
                    || phone.equals("0999999999")) {
                request.setAttribute("profileError", "Số điện thoại không hợp lệ.");
                setOldProfileValues(request, oldFullName, oldEmail, oldPhone);
                request.getRequestDispatcher("/view/auth/user-profile.jsp").forward(request, response);
                return;
            }
        }

        if (fullName.equals(oldFullName)
                && email.equals(oldEmail)
                && phone.equals(oldPhone)) {
            request.setAttribute("profileError", "Thông tin không có thay đổi.");
            setOldProfileValues(request, oldFullName, oldEmail, oldPhone);
            request.getRequestDispatcher("/view/auth/user-profile.jsp").forward(request, response);
            return;
        }

        StaffAccountDAO dao = new StaffAccountDAO();

        if (!email.equals(oldEmail)
                && dao.isValueExistsForOtherStaff("email", email, staff.getStaffId())) {
            request.setAttribute("profileError", "Email đã được sử dụng bởi tài khoản khác.");
            setOldProfileValues(request, oldFullName, oldEmail, oldPhone);
            request.getRequestDispatcher("/view/auth/user-profile.jsp").forward(request, response);
            return;
        }

        if (!phone.isEmpty()
                && !phone.equals(oldPhone)
                && dao.isValueExistsForOtherStaff("phone", phone, staff.getStaffId())) {
            request.setAttribute("profileError", "Số điện thoại đã được sử dụng bởi tài khoản khác.");
            setOldProfileValues(request, oldFullName, oldEmail, oldPhone);
            request.getRequestDispatcher("/view/auth/user-profile.jsp").forward(request, response);
            return;
        }

        boolean updated = dao.updateProfile(staff.getStaffId(), fullName, email, phone);

        if (!updated) {
            request.setAttribute("profileError", "Không thể cập nhật hồ sơ. Vui lòng thử lại.");
            setOldProfileValues(request, oldFullName, oldEmail, oldPhone);
            request.getRequestDispatcher("/view/auth/user-profile.jsp").forward(request, response);
            return;
        }

        StaffAccount updatedStaff = dao.getStaffById(staff.getStaffId());

        if (updatedStaff != null) {
            session.setAttribute("staff", updatedStaff);
            session.setAttribute("staffId", updatedStaff.getStaffId());
            session.setAttribute("staffRole", updatedStaff.getRole());

            request.setAttribute("profileMessage", "Cập nhật hồ sơ thành công.");
        } else {
            request.setAttribute("profileError", "Cập nhật thành công nhưng không thể tải lại hồ sơ.");
            setOldProfileValues(request, oldFullName, oldEmail, oldPhone);
        }

        request.getRequestDispatcher("/view/auth/user-profile.jsp").forward(request, response);
    }

    private void setOldProfileValues(HttpServletRequest request, String oldFullName, String oldEmail, String oldPhone) {
        request.setAttribute("fullNameValue", oldFullName);
        request.setAttribute("emailValue", oldEmail);
        request.setAttribute("phoneValue", oldPhone);
    }

    @Override
    public String getServletInfo() {
        return "User Profile Controller";
    }
}