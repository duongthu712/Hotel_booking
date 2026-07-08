package controller;

import dao.ManagerDashboardDAO;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
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
 */
public class ManagerDashboardController extends HttpServlet {

    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getInstance(new Locale("vi", "VN"));
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");

        // kiểm tra đăng nhập
        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // lấy tháng/năm từ filter
        String monthParam = request.getParameter("month");
        String yearParam = request.getParameter("year");

        int month;
        int year;

        // nếu không có filter trong URL, redirect về tháng/năm hiện tại
        if (monthParam == null || monthParam.isEmpty() || yearParam == null || yearParam.isEmpty()) {
            month = LocalDate.now().getMonthValue();
            year = LocalDate.now().getYear();
            response.sendRedirect(request.getContextPath() + "/ManagerDashboard?month=" + month + "&year=" + year);
            return;
        }

        // có filter, parse giá trị
        try {
            month = Integer.parseInt(monthParam);
            year = Integer.parseInt(yearParam);
        } catch (NumberFormatException e) {
            // lỗi parse, redirect về tháng/năm hiện tại
            month = LocalDate.now().getMonthValue();
            year = LocalDate.now().getYear();
            response.sendRedirect(request.getContextPath() + "/ManagerDashboard?month=" + month + "&year=" + year);
            return;
        }

        ManagerDashboardDAO dao = new ManagerDashboardDAO();

        // lấy dữ liệu KPI
        BigDecimal todayRevenue = dao.getTodayRevenue();
        BigDecimal monthlyRevenue = dao.getMonthlyRevenue(month, year);
        double occupancyRate = dao.getOccupancyRate();
        int totalBookings = dao.getTotalBookings(month, year);

        // lấy dữ liệu biểu đồ
        Map<Integer, BigDecimal> dailyRevenue = dao.getDailyRevenue(month, year);

        // Tính biểu đồ
        StringBuilder labels = new StringBuilder("[");
        StringBuilder revenues = new StringBuilder("[");
        boolean first = true;
        for (Map.Entry<Integer, BigDecimal> entry : dailyRevenue.entrySet()) {
            if (!first) {
                labels.append(",");
                revenues.append(",");
            }
            labels.append("'").append(entry.getKey()).append("'");
            revenues.append(entry.getValue().toPlainString());
            first = false;
        }
        labels.append("]");
        revenues.append("]");
        request.setAttribute("labelsJson", labels.toString());
        request.setAttribute("revenuesJson", revenues.toString());

        // lấy dữ liệu bảng + format date trong controller
        List<Map<String, Object>> recentBookings = dao.getRecentBookings(month, year);
        for (Map<String, Object> b : recentBookings) {
            Object checkinDateObj = b.get("checkinDate");
            if (checkinDateObj instanceof Date) {
                b.put("checkinDateStr", ((Date) checkinDateObj).toLocalDate().format(DATE_FORMATTER));
            } else if (checkinDateObj != null) {
                b.put("checkinDateStr", checkinDateObj.toString());
            } else {
                b.put("checkinDateStr", "-");
            }
            String status = (String) b.get("status");
            if (status != null) {
                b.put("statusClass", status.replace(" ", "-").replace("/", "-"));
            }
        }

        List<Map<String, Object>> latestReviews = dao.getLatestReviews();
        for (Map<String, Object> r : latestReviews) {
            Object submittedAtObj = r.get("submittedAt");
            if (submittedAtObj instanceof Timestamp) {
                r.put("submittedAtStr", ((Timestamp) submittedAtObj).toLocalDateTime().format(DATE_FORMATTER));
            } else if (submittedAtObj != null) {
                r.put("submittedAtStr", submittedAtObj.toString());
            } else {
                r.put("submittedAtStr", "-");
            }
        }

        List<Map<String, Object>> occupancyByRoomType = dao.getOccupancyByRoomType();

        // đẩy dữ liệu lên JSP
        request.setAttribute("todayRevenue", todayRevenue);
        request.setAttribute("monthlyRevenue", monthlyRevenue);
        request.setAttribute("occupancyRate", occupancyRate);
        request.setAttribute("totalBookings", totalBookings);
        request.setAttribute("dailyRevenue", dailyRevenue);
        request.setAttribute("recentBookings", recentBookings);
        request.setAttribute("latestReviews", latestReviews);
        request.setAttribute("occupancyByRoomType", occupancyByRoomType);
        request.setAttribute("selectedMonth", month);
        request.setAttribute("selectedYear", year);

        request.getRequestDispatcher("/view/manager/dashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Manager Dashboard Controller";
    }
}
