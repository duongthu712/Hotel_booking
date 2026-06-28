package controller;

import dao.CheckoutDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.Booking;
import model.Invoice;
import model.StaffAccount;

public class CheckoutController extends HttpServlet {

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

        String keyword = request.getParameter("keyword");

        try {
            CheckoutDAO dao = new CheckoutDAO();

            List<Map<String, Object>> roomList = dao.getRoomsForCheckout(keyword);

            Map<Integer, List<Map<String, Object>>> groupedByBooking = new LinkedHashMap<>();
            for (Map<String, Object> room : roomList) {
                int bookingId = (Integer) room.get("bookingId");
                groupedByBooking.computeIfAbsent(bookingId, k -> new ArrayList<>()).add(room);
            }

            request.setAttribute("roomList", roomList);
            request.setAttribute("groupedByBooking", groupedByBooking);
            request.setAttribute("keyword", keyword);
            request.setAttribute("today", LocalDate.now().format(DATE_FORMATTER));

            request.getRequestDispatcher("/view/receptionist/check-out.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
            request.getRequestDispatcher("/view/receptionist/check-out.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");

        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String[] selectedRoomIds = request.getParameterValues("selectedRooms");
        if (selectedRoomIds == null || selectedRoomIds.length == 0) {
            session.setAttribute("errorMessage", "Vui lòng chọn ít nhất một phòng để checkout.");
            response.sendRedirect(request.getContextPath() + "/Checkout");
            return;
        }

        CheckoutDAO dao = new CheckoutDAO();
        List<String> errorMessages = new ArrayList<>();
        List<Integer> successBookingIds = new ArrayList<>();

        try {
            List<Integer> roomIds = new ArrayList<>();
            for (String rid : selectedRoomIds) {
                roomIds.add(Integer.parseInt(rid));
            }

            List<Map<String, Object>> selectedRoomDetails = dao.getRoomDetailsByRoomIds(roomIds);

            Map<Integer, List<Integer>> bookingRoomIdsMap = new LinkedHashMap<>();
            Map<Integer, List<Integer>> bookingRoomNumbersMap = new LinkedHashMap<>();

            for (Map<String, Object> roomDetail : selectedRoomDetails) {
                int bookingId = (Integer) roomDetail.get("bookingId");
                int roomId = (Integer) roomDetail.get("roomId");
                int roomNumber = (Integer) roomDetail.get("roomNumber");

                bookingRoomIdsMap.computeIfAbsent(bookingId, k -> new ArrayList<>()).add(roomId);
                bookingRoomNumbersMap.computeIfAbsent(bookingId, k -> new ArrayList<>()).add(roomNumber);
            }

            for (Map.Entry<Integer, List<Integer>> entry : bookingRoomIdsMap.entrySet()) {
                int bookingId = entry.getKey();
                List<Integer> bookingRoomIds = entry.getValue();
                List<Integer> bookingRoomNumbers = bookingRoomNumbersMap.get(bookingId);

                try {
                    processSingleBookingCheckout(dao, bookingId, bookingRoomIds, bookingRoomNumbers, staff.getStaffId());
                    successBookingIds.add(bookingId);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    String bookingCode = dao.getBookingCodeById(bookingId);
                    errorMessages.add("Lỗi checkout đơn " + bookingCode + ": " + ex.getMessage());
                }
            }

            if (successBookingIds.isEmpty()) {
                session.setAttribute("errorMessage", String.join("; ", errorMessages));
                response.sendRedirect(request.getContextPath() + "/Checkout");
                return;
            }

            if (!errorMessages.isEmpty()) {
                session.setAttribute("errorMessage", String.join("; ", errorMessages));
            }

            if (successBookingIds.size() == 1) {
                response.sendRedirect(request.getContextPath() + "/InvoiceCreate?bookingId=" + successBookingIds.get(0));
            } else {
                session.setAttribute("successMessage", "Đã tạo " + successBookingIds.size() + " hóa đơn thành công.");
                response.sendRedirect(request.getContextPath() + "/BillingList");
            }

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/Checkout");
        }
    }

    private void processSingleBookingCheckout(CheckoutDAO dao, int bookingId,
            List<Integer> roomIds, List<Integer> roomNumbers, int staffId) throws Exception {

        Booking booking = dao.getBookingById(bookingId);

        LocalDateTime actualCheckout = LocalDateTime.now();
        LocalDateTime expectedCheckout = booking.getCheckoutDate().atTime(12, 0);

        long nights = Math.max(1, ChronoUnit.DAYS.between(
                booking.getCheckinDate(), actualCheckout.toLocalDate()));

        BigDecimal pricePerNight = booking.getBookedPricePerNight() != null
                ? booking.getBookedPricePerNight() : BigDecimal.ZERO;

        BigDecimal roomChargesBase = pricePerNight
                .multiply(BigDecimal.valueOf(nights))
                .multiply(BigDecimal.valueOf(roomIds.size()));

        double lateChargePerRoom = dao.lateCheckoutSurcharge(
                expectedCheckout, actualCheckout, pricePerNight.doubleValue());
        BigDecimal lateCharge = BigDecimal.valueOf(lateChargePerRoom * roomIds.size());
        BigDecimal roomCharges = roomChargesBase.add(lateCharge);

        BigDecimal consumableCharges = dao.sumBookingServicesByRooms(bookingId, roomIds);
        BigDecimal amenityDamages = dao.sumRoomAmenityDamagesByRooms(bookingId, roomIds);

        BigDecimal depositDeducted = booking.getDepositAmount() != null
                ? booking.getDepositAmount() : BigDecimal.ZERO;
        BigDecimal totalAmount = roomCharges.add(consumableCharges).add(amenityDamages);
        BigDecimal remainingAmount = totalAmount.subtract(depositDeducted).max(BigDecimal.ZERO);

        // Chỉ tạo invoice, CHƯA update trạng thái gì cả
        dao.createOrUpdateInvoice(bookingId, roomCharges, consumableCharges, amenityDamages,
                depositDeducted, totalAmount, remainingAmount, staffId);
    }

    private String formatRoomNumbers(List<Integer> roomNumbers) {
        Collections.sort(roomNumbers);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < roomNumbers.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(roomNumbers.get(i));
        }
        return sb.toString();
    }

}
