package dal;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

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
}