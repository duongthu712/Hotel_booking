package dal;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
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

        String htmlContent =
                "<div style='font-family: Arial, sans-serif; color: #073842;'>"
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
    
    //Gửi mail 
        public static void sendDepositVerification(String toEmail, String guestName, String bookingCode, boolean isApproved, String notes) throws Exception {
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
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));

        String subject;
        String htmlContent;

        if (isApproved) {
            subject = "La Mer Hotel - Xác nhận đặt cọc thành công";
            htmlContent =
                "<div style='font-family: Arial, sans-serif; color: #073842;'>"
                + "<h2>La Mer Hotel - Xác nhận đặt cọc</h2>"
                + "<p>Xin chào <strong>" + guestName + "</strong>,</p>"
                + "<p>Chúng tôi đã xác nhận khoản đặt cọc của bạn.</p>"
                + "<p>Mã đơn đặt phòng: <strong>" + bookingCode + "</strong></p>"
                + "<p>Đơn đặt phòng của bạn đã được <strong style='color: #2e7d32;'>XÁC NHẬN</strong>.</p>"
                + (notes != null && !notes.isEmpty() ? "<p><em>Ghi chú: " + notes + "</em></p>" : "")
                + "<p>Chúng tôi rất mong được đón tiếp bạn tại La Mer Hotel!</p>"
                + "</div>";
        } else {
            subject = "La Mer Hotel - Thông báo từ chối đặt cọc";
            htmlContent =
                "<div style='font-family: Arial, sans-serif; color: #073842;'>"
                + "<h2>La Mer Hotel - Thông báo từ chối</h2>"
                + "<p>Xin chào <strong>" + guestName + "</strong>,</p>"
                + "<p>Chúng tôi rất tiếc phải thông báo khoản đặt cọc của bạn không được chấp nhận.</p>"
                + "<p>Mã đơn đặt phòng: <strong>" + bookingCode + "</strong></p>"
                + "<p>Đơn đặt phòng đã bị <strong style='color: #c62828;'>HỦY</strong>.</p>"
                + (notes != null && !notes.isEmpty() ? "<p><em>Lý do: " + notes + "</em></p>" : "")
                + "<p>Vui lòng liên hệ với chúng tôi để biết thêm chi tiết.</p>"
                + "</div>";
        }

        message.setSubject(subject);
        message.setContent(htmlContent, "text/html; charset=UTF-8");
        Transport.send(message);
    }
}