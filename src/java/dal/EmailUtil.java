package dal;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;
import java.time.LocalDate;
import java.util.Properties;

public class EmailUtil {

    public static void sendResetCode(String toEmail, String code) throws Exception {
        // Gmail dùng để GỬI mail
        final String fromEmail = "phuonglinhthcsphuongdien@gmail.com";
        // App Password 16 ký tự, không phải password Gmail thường
        final String appPassword = "pnzf biix zhmo zrxt";

        Properties props = new Properties();

        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, appPassword);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(toEmail)
        );

        message.setSubject("La Mer Hotel - Password Reset Code");

        String htmlContent
                = "<div style='font-family: Arial, sans-serif; color: #073842;'>"
                + "<h2>La Mer Hotel Password Reset</h2>"
                + "<p>We received a request to reset your password.</p>"
                + "<p>Your password reset code is:</p>"
                + "<h1 style='letter-spacing: 5px; color: #073842;'>" + code + "</h1>"
                + "<p>This code will expire in <strong>10 minutes</strong>.</p>"
                + "<p>If you did not request this, please ignore this email.</p>"
                + "</div>";

        message.setContent(htmlContent, "text/html; charset=UTF-8");

        //gui mail
        Transport.send(message);
    }

    public static void sendDepositVerification(String toEmail,
            String guestName,
            String bookingCode,
            String roomType,
            String bedType,
            LocalDate checkinDate,
            LocalDate checkoutDate,
            int numRooms,
            int numGuests,
            boolean isApproved,
            String notes) throws Exception {
        final String fromEmail = "phuonglinhthcsphuongdien@gmail.com";
        final String appPassword = "pnzf biix zhmo zrxt";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, appPassword);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail, "LaMer Hotel"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));

        String subject;
        String htmlContent;

        if (isApproved) {
            subject = "La Mer Hotel - Đã nhận đặt cọc và xác nhận đơn đặt phòng";
            htmlContent = "<div style='font-family: Arial, sans-serif; color: #073842;'>"
                    + "<h2>La Mer Hotel - Xác nhận đặt cọc thành công</h2>"
                    + "<p>Xin chào <strong>" + guestName + "</strong>,</p>"
                    + "<p>Chúng tôi đã nhận được khoản đặt cọc của bạn. Đơn đặt phòng đã được <strong style='color: #2e7d32;'>XÁC NHẬN</strong>.</p>"
                    + "<h3>Thông tin đặt phòng:</h3>"
                    + "<ul>"
                    + "<li>Mã đơn: <strong>" + bookingCode + "</strong></li>"
                    + "<li>Loại phòng: <strong>" + roomType + "</strong></li>"
                    + "<li>Loại giường: <strong>" + bedType + "</strong></li>"
                    + "<li>Số phòng: <strong>" + numRooms + "</strong></li>"
                    + "<li>Số khách: <strong>" + numGuests + "</strong></li>"
                    + "<li>Nhận phòng: <strong>" + checkinDate + "</strong></li>"
                    + "<li>Trả phòng: <strong>" + checkoutDate + "</strong></li>"
                    + "</ul>"
                    + (notes != null && !notes.isEmpty() ? "<p><em>Ghi chú: " + notes + "</em></p>" : "")
                    + "<p>Chúng tôi rất mong được đón tiếp bạn tại La Mer Hotel!</p>"
                    + "</div>";
        } else {
            subject = "La Mer Hotel - Khoản đặt cọc không hợp lệ";
            htmlContent = "<div style='font-family: Arial, sans-serif; color: #073842;'>"
                    + "<h2>La Mer Hotel - Thông báo từ chối</h2>"
                    + "<p>Xin chào <strong>" + guestName + "</strong>,</p>"
                    + "<p>Khoản đặt cọc của bạn <strong style='color: #c62828;'>KHÔNG HỢP LỆ</strong>.</p>"
                    + "<p>Mã đơn đặt phòng: <strong>" + bookingCode + "</strong></p>"
                    + "<p>Đơn đặt phòng đã bị hủy. Vui lòng liên hệ với chúng tôi để biết thêm chi tiết.</p>"
                    + (notes != null && !notes.isEmpty() ? "<p><em>Lý do: " + notes + "</em></p>" : "")
                    + "</div>";
        }

        message.setSubject(MimeUtility.encodeText(subject, "UTF-8", "B"));
        message.setContent(htmlContent, "text/html; charset=UTF-8");
        
        Transport.send(message);
    }
}
