package dal;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    
    public static void sendBookingCreated(String toEmail, String guestName, String phone,
            String idNumber, LocalDate dateOfBirth, String bookingCode,
            LocalDate checkinDate, LocalDate checkoutDate, int numRooms,
            int numGuests, BigDecimal depositAmount, int holdMinutes,
            String paymentUrl) throws Exception {

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

        String subject = "La Mer Hotel - Thông tin đặt phòng";

        String idNumberText = idNumber == null || idNumber.trim().isEmpty()
                ? "Không cung cấp" : idNumber;

        String dateOfBirthText = dateOfBirth == null
                ? "Không cung cấp" : dateOfBirth.toString();

        String htmlContent = "<div style='font-family: Arial, sans-serif; color: #073842;'>"
                + "<h2>La Mer Hotel - Thông tin đặt phòng</h2>"
                + "<p>Xin chào <strong>" + guestName + "</strong>,</p>"
                + "<p>Chúng tôi đã tiếp nhận thông tin đặt phòng của bạn.</p>"

                + "<h3>Thông tin khách hàng:</h3>"
                + "<ul>"
                + "<li>Họ và tên: <strong>" + guestName + "</strong></li>"
                + "<li>Email: <strong>" + toEmail + "</strong></li>"
                + "<li>Số điện thoại: <strong>" + phone + "</strong></li>"
                + "<li>CCCD/Hộ chiếu: <strong>" + idNumberText + "</strong></li>"
                + "<li>Ngày sinh: <strong>" + dateOfBirthText + "</strong></li>"
                + "</ul>"

                + "<h3>Thông tin đặt phòng:</h3>"
                + "<ul>"
                + "<li>Mã đặt phòng: <strong>" + bookingCode + "</strong></li>"
                + "<li>Ngày nhận phòng: <strong>" + checkinDate + "</strong></li>"
                + "<li>Ngày trả phòng: <strong>" + checkoutDate + "</strong></li>"
                + "<li>Số phòng: <strong>" + numRooms + "</strong></li>"
                + "<li>Số khách: <strong>" + numGuests + "</strong></li>"
                + "<li>Tiền đặt cọc: <strong>" + depositAmount + " VNĐ</strong></li>"
                + "</ul>"

                + "<p>Đơn đặt phòng được giữ trong <strong>" + holdMinutes + " phút</strong>.</p>"
                + "<p>Vui lòng hoàn tất thanh toán trước khi thời gian giữ phòng kết thúc.</p>"

                + "<p style='margin-top: 25px;'>"
                + "<a href='" + paymentUrl + "' style='background-color: #073842; color: white; "
                + "padding: 12px 20px; text-decoration: none; border-radius: 5px;'>"
                + "Tiếp tục thanh toán"
                + "</a>"
                + "</p>"

                + "<p>Bạn có thể mở lại email này để tiếp tục thanh toán mà không cần nhớ mã đặt phòng.</p>"
                + "</div>";

        message.setSubject(MimeUtility.encodeText(subject, "UTF-8", "B"));
        message.setContent(htmlContent, "text/html; charset=UTF-8");

        Transport.send(message);
    }

    public static void sendPaymentSubmitted(String toEmail, String guestName,
                String bookingCode, String transactionReference) throws Exception {

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

            String subject = "La Mer Hotel - Đã nhận thông tin thanh toán";

            String trackingUrl = "http://localhost:9999/Hotel_booking_project/view/user/booking-detail.jsp"
                    + "?bookingCode=" + URLEncoder.encode(bookingCode, StandardCharsets.UTF_8);

            String htmlContent = "<div style='font-family: Arial, sans-serif; color: #073842;'>"
                    + "<h2>La Mer Hotel - Đã nhận thông tin thanh toán</h2>"
                    + "<p>Xin chào <strong>" + guestName + "</strong>,</p>"
                    + "<p>Chúng tôi đã nhận được thông tin thanh toán đặt cọc của bạn.</p>"
                    + "<h3>Thông tin giao dịch:</h3>"
                    + "<ul>"
                    + "<li>Mã đặt phòng: <strong>" + bookingCode + "</strong></li>"
                    + "<li>Mã giao dịch: <strong>" + transactionReference + "</strong></li>"
                    + "<li>Trạng thái: <strong style='color: #ef6c00;'>Đang chờ xử lý</strong></li>"
                    + "</ul>"
                    + "<p>Bạn có thể theo dõi trạng thái đặt phòng bằng nút bên dưới:</p>"
                    + "<p style='margin-top: 25px;'>"
                    + "<a href='" + trackingUrl + "' style='background-color: #073842; color: white; "
                    + "padding: 12px 20px; text-decoration: none; border-radius: 5px;'>"
                    + "Theo dõi trạng thái đặt phòng"
                    + "</a>"
                    + "</p>"
                    + "<p>Vui lòng không gửi lại thông tin thanh toán nhiều lần.</p>"
                    + "<p>La Mer Hotel sẽ liên hệ với bạn khi có thông tin mới.</p>"
                    + "</div>";

            message.setSubject(MimeUtility.encodeText(subject, "UTF-8", "B"));
            message.setContent(htmlContent, "text/html; charset=UTF-8");

            Transport.send(message);
        }
}
