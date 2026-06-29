//package controller;
//
//import com.lowagie.text.Document;
//import com.lowagie.text.DocumentException;
//import com.lowagie.text.Font;
//import com.lowagie.text.Paragraph;
//import com.lowagie.text.pdf.PdfPTable;
//import com.lowagie.text.pdf.PdfWriter;
//import dao.CheckoutDAO;
//import java.io.IOException;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import java.math.BigDecimal;
//import java.text.NumberFormat;
//import java.util.List;
//import java.util.Locale;
//import model.Booking;
//import model.BookingRoom;
//import model.Guest;
//import model.Invoice;
//import model.RoomType;
//import model.StaffAccount;
//
///**
// * @author LinhLTHE200306
// * @version 1.0
// * @since 2026-06-21
// *
// * Export invoice as PDF using OpenPDF (com.lowagie)
// */
//public class InvoicePDFController extends HttpServlet {
//
//    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getInstance(new Locale("vi", "VN"));
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        HttpSession session = request.getSession();
//        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
//
//        if (staff == null) {
//            response.sendRedirect(request.getContextPath() + "/login");
//            return;
//        }
//
//        String bookingIdStr = request.getParameter("bookingId");
//        if (bookingIdStr == null || bookingIdStr.isEmpty()) {
//            response.sendRedirect(request.getContextPath() + "/Checkout");
//            return;
//        }
//
//        try {
//            int bookingId = Integer.parseInt(bookingIdStr);
//            CheckoutDAO dao = new CheckoutDAO();
//
//            Invoice invoice = dao.getInvoiceByBookingId(bookingId);
//            if (invoice == null) {
//                response.sendRedirect(request.getContextPath() + "/Checkout");
//                return;
//            }
//
//            Booking booking = dao.getBookingById(bookingId);
//            Guest guest = dao.getGuestByBookingId(bookingId);
//            RoomType roomType = dao.getRoomTypeByBookingId(bookingId);
//            List<BookingRoom> bookingRooms = dao.getBookingRoomsByBookingId(bookingId);
//
//            response.setContentType("application/pdf");
//            response.setHeader("Content-Disposition", "attachment; filename=invoice_" + booking.getBookingCode() + ".pdf");
//
//            Document document = new Document();
//            PdfWriter.getInstance(document, response.getOutputStream());
//            document.open();
//
//            // Title
//            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
//            document.add(new Paragraph("LA MER HOTEL - Hoá đơn thanh toán".toUpperCase(), titleFont));
//            document.add(new Paragraph(" "));
//
//            // Invoice info
//            Font normalFont = new Font(Font.HELVETICA, 12);
//            document.add(new Paragraph("Mã hoá đơn: INV-" + invoice.getInvoiceId(), normalFont));
//            document.add(new Paragraph("Mã Booking: " + booking.getBookingCode(), normalFont));
//            document.add(new Paragraph("Ngày toạ: " + invoice.getPaidAt(), normalFont));
//            document.add(new Paragraph(" "));
//
//            // Guest info
//            document.add(new Paragraph("Thông tin khách hàng".toUpperCase(), titleFont));
//            document.add(new Paragraph("Họ tên: " + guest.getFullName(), normalFont));
//            document.add(new Paragraph("SĐT: " + guest.getPhone(), normalFont));
//            document.add(new Paragraph("Email: " + guest.getEmail(), normalFont));
//            document.add(new Paragraph(" "));
//
//            // Room info
//            document.add(new Paragraph("Thông tin đặt phòng".toUpperCase(), titleFont));
//            document.add(new Paragraph("Loại phòng: " + roomType.getTypeName(), normalFont));
//            document.add(new Paragraph("Số phòng: " + booking.getNumRooms(), normalFont));
//            document.add(new Paragraph("Check-in: " + booking.getCheckinDate(), normalFont));
//            document.add(new Paragraph("Check-out: " + booking.getActualCheckoutTime(), normalFont));
//            document.add(new Paragraph(" "));
//
//            // Payment table
//            document.add(new Paragraph("Chi tiết thanh toán".toUpperCase(), titleFont));
//
//            PdfPTable table = new PdfPTable(2);
//            table.setWidthPercentage(100);
//            table.addCell("Mô tả");
//            table.addCell("Số tiền");
//
//            table.addCell("Tiền phòng");
//            table.addCell(formatCurrency(invoice.getRoomCharges()));
//
//            if (invoice.getConsumableCharges().doubleValue() > 0) {
//                table.addCell("Dịch vụ đã sử dụng");
//                table.addCell(formatCurrency(invoice.getConsumableCharges()));
//            }
//
//            if (invoice.getAmenityDamages().doubleValue() > 0) {
//                table.addCell("Tiện nghi hư hỏng, mất");
//                table.addCell(formatCurrency(invoice.getAmenityDamages()));
//            }
//
//            table.addCell("Tổng tiền".toUpperCase());
//            table.addCell(formatCurrency(invoice.getTotalAmount()));
//
//            table.addCell("Tiền cọc");
//            table.addCell("-" + formatCurrency(invoice.getDepositDeducted()));
//
//            table.addCell("Số tiền phải thanh toán");
//            table.addCell(formatCurrency(invoice.getRemainingAmount()));
//
//            document.add(table);
//            document.add(new Paragraph(" "));
//
//            // Payment info
//            document.add(new Paragraph("Phương thức thanh toán: " + invoice.getPaymentMethod(), normalFont));
//            document.add(new Paragraph("Trạng thái: " + invoice.getPaymentStatus(), normalFont));
//            document.add(new Paragraph("Nhân viên: " + staff.getFullName(), normalFont));
//
//            document.add(new Paragraph(" "));
//            document.add(new Paragraph("Cảm ơn quý khách đã lựa chọn La Mer Hotel!", normalFont));
//
//            document.close();
//
//        } catch (DocumentException e) {
//            e.printStackTrace();
//            response.sendRedirect(request.getContextPath() + "/BillingList?bookingId=" + bookingIdStr);
//        } catch (Exception e) {
//            e.printStackTrace();
//            response.sendRedirect(request.getContextPath() + "/Checkout");
//        }
//    }
//
//    private String formatCurrency(BigDecimal amount) {
//        return CURRENCY_FORMAT.format(amount) + " d";
//    }
//}
