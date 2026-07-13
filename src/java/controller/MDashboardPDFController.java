package controller;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import dao.ManagerDashboardDAO;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.StaffAccount;

/**
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-07-08
 *
 */
public class MDashboardPDFController extends HttpServlet {

    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getInstance(new Locale("vi", "VN"));
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");

        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // lấy tháng/năm
        int month;
        int year;
        try {
            String monthParam = request.getParameter("month");
            String yearParam = request.getParameter("year");
            month = (monthParam != null && !monthParam.isEmpty()) ? Integer.parseInt(monthParam) : LocalDate.now().getMonthValue();
            year = (yearParam != null && !yearParam.isEmpty()) ? Integer.parseInt(yearParam) : LocalDate.now().getYear();
        } catch (NumberFormatException e) {
            month = LocalDate.now().getMonthValue();
            year = LocalDate.now().getYear();
        }

        String monthYearStr = String.format("%02d/%d", month, year);

        ManagerDashboardDAO dao = new ManagerDashboardDAO();

        
        BigDecimal todayRevenue = dao.getTodayRevenue();
        BigDecimal monthlyRevenue = dao.getMonthlyRevenue(month, year);
        double occupancyRate = dao.getOccupancyRate();
        int totalBookings = dao.getTotalBookings(month, year);
        List<Map<String, Object>> recentBookings = dao.getRecentBookings(month, year);
        List<Map<String, Object>> latestReviews = dao.getLatestReviews();
        List<Map<String, Object>> occupancyByRoomType = dao.getOccupancyByRoomType();

        try {
            // cấu hình response PDF
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=dashboard_" + monthYearStr.replace("/", "_") + ".pdf");

            Document document = new Document();
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // font styles
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font sectionFont = new Font(Font.HELVETICA, 14, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 11);

            // header
            document.add(new Paragraph("LA MER HOTEL - BÁO CÁO TỔNG QUAN", titleFont));
            document.add(new Paragraph("Tháng: " + monthYearStr, normalFont));
            document.add(new Paragraph("Người xuất: " + staff.getFullName(), normalFont));
            document.add(new Paragraph("Ngày xuất: " + LocalDate.now().format(DATE_FORMATTER), normalFont));
            document.add(new Paragraph(" "));

            // KPIs
            document.add(new Paragraph("CHỈ SỐ KẾT QUẢ KINH DOANH", sectionFont));
            document.add(new Paragraph("Doanh thu hôm nay: " + formatCurrency(todayRevenue), normalFont));
            document.add(new Paragraph("Doanh thu tháng: " + formatCurrency(monthlyRevenue), normalFont));
            document.add(new Paragraph("Tỷ lệ lấp đầy: " + occupancyRate + "%", normalFont));
            document.add(new Paragraph("Tổng đơn đặt phòng: " + totalBookings, normalFont));
            document.add(new Paragraph(" "));

            // recent bookings
            document.add(new Paragraph("ĐƠN ĐẶT PHÒNG GẦN ĐÂY", sectionFont));
            if (recentBookings.isEmpty()) {
                document.add(new Paragraph("Không có đơn đặt phòng nào.", normalFont));
            } else {
                PdfPTable bookingTable = new PdfPTable(5);
                bookingTable.setWidthPercentage(100);
                bookingTable.addCell("Mã đặt phòng");
                bookingTable.addCell("Khách hàng");
                bookingTable.addCell("Loại phòng");
                bookingTable.addCell("Ngày nhận phòng");
                bookingTable.addCell("Trạng thái");

                for (Map<String, Object> b : recentBookings) {
                    bookingTable.addCell((String) b.get("bookingCode"));
                    bookingTable.addCell((String) b.get("guestName"));
                    bookingTable.addCell((String) b.get("roomType"));
                    bookingTable.addCell(b.get("checkinDate") != null ? b.get("checkinDate").toString() : "-");
                    bookingTable.addCell((String) b.get("status"));
                }
                document.add(bookingTable);
            }
            document.add(new Paragraph(" "));

            // latest reviews
            document.add(new Paragraph("ĐÁNH GIÁ GẦN ĐÂY", sectionFont));
            if (latestReviews.isEmpty()) {
                document.add(new Paragraph("Không có đánh giá nào.", normalFont));
            } else {
                PdfPTable reviewTable = new PdfPTable(3);
                reviewTable.setWidthPercentage(100);
                reviewTable.addCell("Khách hàng");
                reviewTable.addCell("Đánh giá");
                reviewTable.addCell("Ngày");

                for (Map<String, Object> r : latestReviews) {
                    reviewTable.addCell((String) r.get("guestName"));
                    reviewTable.addCell(r.get("rating") + " sao");
                    reviewTable.addCell(r.get("submittedAt") != null ? r.get("submittedAt").toString() : "-");
                }
                document.add(reviewTable);
            }
            document.add(new Paragraph(" "));

            // occupancy by room type
            document.add(new Paragraph("TỶ LỆ LẤP ĐẦY THEO LOẠI PHÒNG", sectionFont));
            if (occupancyByRoomType.isEmpty()) {
                document.add(new Paragraph("Không có dữ liệu.", normalFont));
            } else {
                PdfPTable occTable = new PdfPTable(4);
                occTable.setWidthPercentage(100);
                occTable.addCell("Loại phòng");
                occTable.addCell("Phòng có khách");
                occTable.addCell("Tổng phòng");
                occTable.addCell("Tỷ lệ lấp đầy");

                for (Map<String, Object> o : occupancyByRoomType) {
                    occTable.addCell((String) o.get("typeName"));
                    occTable.addCell(String.valueOf(o.get("occupied")));
                    occTable.addCell(String.valueOf(o.get("total")));
                    occTable.addCell(o.get("occupancyPct") + "%");
                }
                document.add(occTable);
            }

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/ManagerDashboard");
        }
    }

    // format tiền tệ
    private String formatCurrency(BigDecimal amount) {
        return CURRENCY_FORMAT.format(amount) + " VND";
    }

    @Override
    public String getServletInfo() {
        return "Dashboard PDF Export Controller";
    }
}
