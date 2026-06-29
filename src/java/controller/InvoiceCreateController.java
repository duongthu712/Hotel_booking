package controller;

import dao.CheckoutDAO;
import dao.HotelInfoDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Booking;
import model.BookingService;
import model.Guest;
import model.GuestStay;
import model.HotelInfo;
import model.Invoice;
import model.RoomAmenityDamage;
import model.RoomType;
import model.StaffAccount;

public class InvoiceCreateController extends HttpServlet {

    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter TIME_24H = DateTimeFormatter.ofPattern("HH:mm:ss");
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

        String bookingIdStr = request.getParameter("bookingId");
        if (bookingIdStr == null || bookingIdStr.isEmpty()) {
            session.setAttribute("errorMessage", "Không tìm thấy mã đặt phòng.");
            response.sendRedirect(request.getContextPath() + "/Checkout");
            return;
        }

        try {
            int bookingId = Integer.parseInt(bookingIdStr);
            CheckoutDAO dao = new CheckoutDAO();

            Booking booking = dao.getBookingById(bookingId);
            if (booking == null) {
                session.setAttribute("errorMessage", "Không tìm thấy thông tin đặt phòng.");
                response.sendRedirect(request.getContextPath() + "/Checkout");
                return;
            }

            if (!"Đã nhận phòng".equals(booking.getStatus())
                    && !"Đã trả phòng".equals(booking.getStatus())) {
                session.setAttribute("errorMessage", "Không thể tạo/xem hóa đơn cho booking này.");
                response.sendRedirect(request.getContextPath() + "/Checkout");
                return;
            }

            // Số phòng đang checkout lần này (từ session do CheckoutController set)
            Integer checkoutRoomCount = (Integer) session.getAttribute("checkoutRoomCount_" + bookingId);
            int numRoomsCheckedOut = checkoutRoomCount != null ? checkoutRoomCount : booking.getNumRooms();
            session.removeAttribute("checkoutRoomCount_" + bookingId);

            // Số đêm thực tế
            LocalDateTime actualCheckout = LocalDateTime.now();
            long nights = Math.max(1, ChronoUnit.DAYS.between(
                    booking.getCheckinDate(), actualCheckout.toLocalDate()));

            // Phụ thu trả phòng muộn
            double lateChargePerRoom = dao.lateCheckoutSurcharge(
                    booking.getCheckoutDate().atTime(12, 0),
                    actualCheckout,
                    booking.getBookedPricePerNight().doubleValue());
            double totalLateCharge = lateChargePerRoom * numRoomsCheckedOut;

            // Tiền phòng
            double roomChargesBase = nights
                    * booking.getBookedPricePerNight().doubleValue()
                    * numRoomsCheckedOut;
            double roomCharges = roomChargesBase + totalLateCharge;

            // Tính tiền cọc cho lần checkout này
            BigDecimal totalDeposit = booking.getDepositAmount() != null
                    ? booking.getDepositAmount() : BigDecimal.ZERO;
            BigDecimal depositAlreadyUsed = dao.sumDepositDeductedByBookingId(bookingId);
            BigDecimal depositRemaining = totalDeposit.subtract(depositAlreadyUsed);

            int remainingRooms = dao.countRemainingRooms(bookingId);
            boolean isLastCheckout = remainingRooms == numRoomsCheckedOut;

            BigDecimal depositThisCheckout;
            if (isLastCheckout) {
                depositThisCheckout = depositRemaining;
            } else {
                int totalRooms = booking.getNumRooms();
                if (totalRooms > 0) {
                    depositThisCheckout = totalDeposit
                            .divide(BigDecimal.valueOf(totalRooms), 0, RoundingMode.FLOOR)
                            .multiply(BigDecimal.valueOf(numRoomsCheckedOut));
                    if (depositThisCheckout.compareTo(depositRemaining) > 0) {
                        depositThisCheckout = depositRemaining;
                    }
                } else {
                    depositThisCheckout = BigDecimal.ZERO;
                }
            }

            // Dịch vụ và hư hỏng đã có từ DB
            List<Map<String, Object>> existingServices = dao.getBookingServicesWithNameByBookingId(bookingId);
            List<Map<String, Object>> existingDamages = dao.getRoomAmenityDamagesWithNameByBookingId(bookingId);
            BigDecimal existingServicesTotal = dao.sumAllBookingServices(bookingId);
            BigDecimal existingDamagesTotal = dao.sumAllRoomAmenityDamages(bookingId);

            // Map số lượng đã báo hỏng theo amenity_id
            Map<Integer, Integer> damagedQtyMap = new HashMap<>();
            for (Map<String, Object> dmg : existingDamages) {
                int amenityId = (Integer) dmg.get("amenityId");
                int qty = (Integer) dmg.get("quantityDamaged");
                damagedQtyMap.merge(amenityId, qty, Integer::sum);
            }

            Guest guest = dao.getGuestByBookingId(bookingId);
            RoomType roomType = dao.getRoomTypeByBookingId(bookingId);
            List<Map<String, Object>> bookingRooms = dao.getRoomNumbersByBookingId(bookingId);
            List<GuestStay> guestStays = dao.getGuestStaysByBookingId(bookingId);
            List<Map<String, Object>> roomTypeServices = dao.getRoomTypeServicesWithDetails(booking.getRoomTypeId());
            List<Map<String, Object>> roomTypeAmenities = dao.getRoomTypeAmenitiesWithDetails(booking.getRoomTypeId());

            HotelInfoDAO hdao = new HotelInfoDAO();
            HotelInfo hotelInfo = hdao.getHotelInfoById(1);

            String formattedCheckinTime = "14:00:00";
            if (booking.getActualCheckinTime() != null) {
                formattedCheckinTime = booking.getActualCheckinTime().format(TIME_24H);
            }

            LocalDateTime depositVerifiedAt = dao.getDepositVerifiedAt(bookingId);
            String depositVerifiedAtStr = depositVerifiedAt != null
                    ? depositVerifiedAt.format(DISPLAY_FORMATTER) : "N/A";

            request.setAttribute("booking", booking);
            request.setAttribute("guest", guest);
            request.setAttribute("roomType", roomType);
            request.setAttribute("bookingRooms", bookingRooms);
            request.setAttribute("guestStays", guestStays);
            request.setAttribute("hotelInfo", hotelInfo);
            request.setAttribute("nights", nights);
            request.setAttribute("roomCharges", roomCharges);
            request.setAttribute("roomChargesBase", roomChargesBase);
            request.setAttribute("lateCharge", totalLateCharge);
            request.setAttribute("formattedCheckinTime", formattedCheckinTime);
            request.setAttribute("checkinDateDisplay", booking.getCheckinDate().format(DATE_FORMATTER));
            request.setAttribute("checkoutDateDisplay", booking.getCheckoutDate().format(DATE_FORMATTER));
            request.setAttribute("actualCheckoutTime", LocalDateTime.now().format(DISPLAY_FORMATTER));
            request.setAttribute("roomTypeServices", roomTypeServices);
            request.setAttribute("roomTypeAmenities", roomTypeAmenities);
            request.setAttribute("existingServices", existingServices);
            request.setAttribute("existingDamages", existingDamages);
            request.setAttribute("existingServicesTotal", existingServicesTotal);
            request.setAttribute("existingDamagesTotal", existingDamagesTotal);
            request.setAttribute("damagedQtyMap", damagedQtyMap);
            request.setAttribute("depositAmount", depositThisCheckout);
            request.setAttribute("depositVerifiedAt", depositVerifiedAtStr);
            request.setAttribute("isLastCheckout", isLastCheckout);
            request.setAttribute("totalDeposit", totalDeposit);

            request.getRequestDispatcher("/view/receptionist/invoice.jsp").forward(request, response);
            System.out.println("checkoutRoomCount_" + bookingId + " = " + checkoutRoomCount);
            System.out.println("numRoomsCheckedOut = " + numRoomsCheckedOut);
            System.out.println("remainingRooms = " + remainingRooms);
            System.out.println("isLastCheckout = " + isLastCheckout);
            System.out.println("depositAlreadyUsed = " + depositAlreadyUsed);
            System.out.println("depositThisCheckout = " + depositThisCheckout);
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/Checkout");
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

        try {
            int bookingId = Integer.parseInt(request.getParameter("bookingId"));
            CheckoutDAO dao = new CheckoutDAO();

            Booking booking = dao.getBookingById(bookingId);
            if (booking == null) {
                session.setAttribute("errorMessage", "Booking không hợp lệ để tạo hóa đơn.");
                response.sendRedirect(request.getContextPath() + "/Checkout");
                return;
            }

            Invoice existingInvoice = dao.getUnpaidInvoiceByBookingId(bookingId);
            if (existingInvoice == null && !"Đã nhận phòng".equals(booking.getStatus())) {
                session.setAttribute("errorMessage", "Booking không hợp lệ để tạo hóa đơn.");
                response.sendRedirect(request.getContextPath() + "/Checkout");
                return;
            }

            // Dịch vụ thêm mới từ form
            BigDecimal consumableCharges = BigDecimal.ZERO;
            String[] roomTypeServiceIds = request.getParameterValues("roomTypeServiceId");
            String[] unitPrices = request.getParameterValues("serviceUnitPrice");
            String[] quantities = request.getParameterValues("serviceQuantity");
            String[] isFrees = request.getParameterValues("serviceIsFree");

            if (roomTypeServiceIds != null) {
                for (int i = 0; i < roomTypeServiceIds.length; i++) {
                    int qty = Integer.parseInt(quantities[i]);
                    if (qty > 0) {
                        BigDecimal unitPrice = new BigDecimal(unitPrices[i]);
                        int isFree = Integer.parseInt(isFrees[i]);
                        int numRooms = booking.getNumRooms();
                        int chargeQty = Math.max(0, qty - (isFree * numRooms));
                        consumableCharges = consumableCharges.add(
                                unitPrice.multiply(new BigDecimal(chargeQty)));
                    }
                }
            }

            // Hư hỏng thêm mới từ form
            BigDecimal amenityDamages = BigDecimal.ZERO;
            String[] amenityIds = request.getParameterValues("amenityId");
            String[] damageUnitPrices = request.getParameterValues("damageUnitPrice");
            String[] damageQuantities = request.getParameterValues("damageQuantity");

            if (amenityIds != null) {
                for (int i = 0; i < amenityIds.length; i++) {
                    int qty = Integer.parseInt(damageQuantities[i]);
                    if (qty > 0) {
                        BigDecimal unitPrice = new BigDecimal(damageUnitPrices[i]);
                        amenityDamages = amenityDamages.add(
                                unitPrice.multiply(new BigDecimal(qty)));
                    }
                }
            }

            // Nếu có thêm dịch vụ/hư hỏng mới thì cập nhật invoice
            if (consumableCharges.compareTo(BigDecimal.ZERO) > 0
                    || amenityDamages.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal roomCharges = new BigDecimal(request.getParameter("roomCharges"));
                BigDecimal existingServices = dao.sumAllBookingServices(bookingId);
                BigDecimal existingDamages = dao.sumAllRoomAmenityDamages(bookingId);
                BigDecimal depositDeducted = existingInvoice != null
                        ? existingInvoice.getDepositDeducted() : BigDecimal.ZERO;
                BigDecimal totalConsumable = existingServices.add(consumableCharges);
                BigDecimal totalDamages = existingDamages.add(amenityDamages);
                BigDecimal totalAmount = roomCharges.add(totalConsumable).add(totalDamages);
                BigDecimal remainingAmount = totalAmount.subtract(depositDeducted).max(BigDecimal.ZERO);

                dao.createOrUpdateInvoice(bookingId, roomCharges, totalConsumable, totalDamages,
                        depositDeducted, totalAmount, remainingAmount, staff.getStaffId());
            }

            // Xác nhận thanh toán
            String paymentMethod = request.getParameter("paymentMethod");
            dao.completeInvoicePayment(bookingId, paymentMethod, staff.getStaffId());

            // Update trạng thái phòng và booking
            List<Integer> roomIds = dao.getCheckedOutRoomIdsByBookingId(bookingId);
            dao.updateBookingRoomsCheckoutStatus(roomIds);
            if (dao.isAllRoomsCheckedOut(bookingId)) {
                dao.updateBookingStatusToCheckedOut(bookingId);
            }
            dao.updateRoomStatusAfterCheckout(bookingId, roomIds);

            session.setAttribute("successMessage", "Thanh toán thành công!");
            Invoice inv = dao.getInvoiceByBookingId(bookingId);
            response.sendRedirect(request.getContextPath() + "/BillingList?invoiceId=" + inv.getInvoiceId());

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/Checkout");
        }
    }
}
