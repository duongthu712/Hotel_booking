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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Booking;
import model.BookingRoom;
import model.BookingService;
import model.Guest;
import model.GuestStay;
import model.HotelInfo;
import model.Invoice;
import model.Room;
import model.RoomAmenityDamage;
import model.RoomType;
import model.StaffAccount;

/**
 * @author LinhLTHE200306
 * @version 2.0
 * @since 2026-06-28
 */
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

            if (!"Đã nhận phòng".equals(booking.getStatus()) && !"Đã trả phòng".equals(booking.getStatus())) {
                session.setAttribute("errorMessage", "Không thể tạo/xem hóa đơn cho booking này.");
                response.sendRedirect(request.getContextPath() + "/Checkout");
                return;
            }

            Guest guest = dao.getGuestByBookingId(bookingId);
            RoomType roomType = dao.getRoomTypeByBookingId(bookingId);
            String roomImageUrl = dao.getRoomTypeImgByTypeId(roomType.getRoomTypeId());
            List<Map<String, Object>> bookingRooms = dao.getRoomNumbersByBookingId(bookingId);
            List<GuestStay> guestStays = dao.getGuestStaysByBookingId(bookingId);

            HotelInfoDAO hdao = new HotelInfoDAO();
            HotelInfo hotelInfo = hdao.getHotelInfoById(1);

            LocalDateTime actualCheckout = LocalDateTime.now();
            LocalDateTime expectedCheckout = booking.getCheckoutDate().atTime(12, 0);

// Số đêm thực tế theo ngày actual checkout
            long nights = Math.max(1, ChronoUnit.DAYS.between(
                    booking.getCheckinDate(), actualCheckout.toLocalDate()));

// Phụ thu late checkout (50% hoặc 100% giá phòng/phòng)
            double lateChargePerRoom = dao.lateCheckoutSurcharge(
                    expectedCheckout,
                    actualCheckout,
                    booking.getBookedPricePerNight().doubleValue()
            );
            double totalLateCharge = lateChargePerRoom * booking.getNumRooms();

// Tiền phòng = số đêm * giá * số phòng (chưa gộp phụ thu)
            double roomCharges = nights
                    * booking.getBookedPricePerNight().doubleValue()
                    * booking.getNumRooms();

// Tổng tiền phòng thực thu = roomCharges + lateCharge
            double totalRoomCharges = roomCharges + totalLateCharge;

            request.setAttribute("nights", nights);
            request.setAttribute("lateCharge", totalLateCharge);
            request.setAttribute("roomCharges", totalRoomCharges); // truyền tổng để tính invoice
            request.setAttribute("roomChargesBase", roomCharges);  // truyền riêng để hiển thị dòng tiền phòng

            session.removeAttribute("checkout_roomCharges_" + bookingId);
            session.removeAttribute("checkout_nights_" + bookingId);

            java.time.LocalDateTime depositVerifiedAt = dao.getDepositVerifiedAt(bookingId);
            String depositVerifiedAtStr = depositVerifiedAt != null ? depositVerifiedAt.format(DISPLAY_FORMATTER) : "N/A";
            request.setAttribute("depositVerifiedAt", depositVerifiedAtStr);

            List<Map<String, Object>> roomTypeServices = dao.getRoomTypeServicesWithDetails(booking.getRoomTypeId());
            List<Map<String, Object>> roomTypeAmenities = dao.getRoomTypeAmenitiesWithDetails(booking.getRoomTypeId());

            List<Map<String, Object>> existingServices = dao.getBookingServicesWithNameByBookingId(bookingId);
            List<Map<String, Object>> existingDamages = dao.getRoomAmenityDamagesWithNameByBookingId(bookingId);

            // Tính số lượng đã báo hỏng theo từng amenity_id
            Map<Integer, Integer> damagedQtyMap = new HashMap<>();
            for (Map<String, Object> dmg : existingDamages) {
                int amenityId = (Integer) dmg.get("amenityId");
                int qty = (Integer) dmg.get("quantityDamaged");
                damagedQtyMap.merge(amenityId, qty, Integer::sum);
            }
            request.setAttribute("damagedQtyMap", damagedQtyMap);

            String formattedCheckinTime = "14:00:00";
            if (booking.getActualCheckinTime() != null) {
                formattedCheckinTime = booking.getActualCheckinTime().format(TIME_24H);
            }

            BigDecimal existingServicesTotal = dao.sumAllBookingServices(bookingId);
            BigDecimal existingDamagesTotal = dao.sumAllRoomAmenityDamages(bookingId);

            request.setAttribute("booking", booking);
            request.setAttribute("guest", guest);
            request.setAttribute("roomType", roomType);
            request.setAttribute("roomImageUrl", roomImageUrl);
            request.setAttribute("bookingRooms", bookingRooms);
            request.setAttribute("guestStays", guestStays);
            request.setAttribute("hotelInfo", hotelInfo);
            request.setAttribute("nights", nights);
            request.setAttribute("roomCharges", roomCharges);
            request.setAttribute("formattedCheckinTime", formattedCheckinTime);
            request.setAttribute("checkinDateDisplay", booking.getCheckinDate().format(DATE_FORMATTER));
            request.setAttribute("checkoutDateDisplay", booking.getCheckoutDate().format(DATE_FORMATTER));
            request.setAttribute("actualCheckoutTime", LocalDateTime.now().format(DISPLAY_FORMATTER));
            request.setAttribute("roomTypeServices", roomTypeServices);
            request.setAttribute("roomTypeAmenities", roomTypeAmenities);
            request.setAttribute("existingServices", existingServices);
            request.setAttribute("existingDamages", existingDamages);
            request.setAttribute("depositAmount", booking.getDepositAmount());
            request.setAttribute("existingServicesTotal", existingServicesTotal);
            request.setAttribute("existingDamagesTotal", existingDamagesTotal);
            request.getRequestDispatcher("/view/receptionist/invoice.jsp").forward(request, response);

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
            boolean isReopening = existingInvoice != null;

            if (!"Đã nhận phòng".equals(booking.getStatus()) && !isReopening) {
                session.setAttribute("errorMessage", "Booking không hợp lệ để tạo hóa đơn.");
                response.sendRedirect(request.getContextPath() + "/Checkout");
                return;
            }

            BigDecimal roomCharges = new BigDecimal(request.getParameter("roomCharges"));
            BigDecimal depositDeducted = booking.getDepositAmount() != null ? booking.getDepositAmount() : BigDecimal.ZERO;

            List<BookingService> services = new ArrayList<>();
            BigDecimal consumableCharges = BigDecimal.ZERO;

            String[] serviceIds = request.getParameterValues("serviceId");
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
                        BigDecimal totalPrice = unitPrice.multiply(new BigDecimal(chargeQty));

                        BookingService bs = new BookingService();
                        bs.setBookingId(bookingId);
                        bs.setRoomTypeServiceId(Integer.parseInt(roomTypeServiceIds[i]));
                        bs.setUnitPrice(unitPrice);
                        bs.setQuantityUsed(qty);
                        bs.setTotalPrice(totalPrice);
                        services.add(bs);

                        consumableCharges = consumableCharges.add(totalPrice);
                    }
                }
            }

            // Lưu hư hỏng mới nhập
            List<RoomAmenityDamage> damages = new ArrayList<>();
            BigDecimal amenityDamages = BigDecimal.ZERO;

            String[] amenityIds = request.getParameterValues("amenityId");
            String[] damageUnitPrices = request.getParameterValues("damageUnitPrice");
            String[] damageQuantities = request.getParameterValues("damageQuantity");

            if (amenityIds != null) {
                for (int i = 0; i < amenityIds.length; i++) {
                    int qty = Integer.parseInt(damageQuantities[i]);
                    if (qty > 0) {
                        BigDecimal unitPrice = new BigDecimal(damageUnitPrices[i]);
                        BigDecimal totalPrice = unitPrice.multiply(new BigDecimal(qty));

                        RoomAmenityDamage damage = new RoomAmenityDamage();
                        damage.setBookingId(bookingId);
                        damage.setAmenityId(Integer.parseInt(amenityIds[i]));
                        damage.setQuantityDamaged(qty);
                        damage.setTotalPrice(totalPrice);
                        damages.add(damage);

                        amenityDamages = amenityDamages.add(totalPrice);
                    }
                }
            }

            BigDecimal totalAmount = roomCharges.add(consumableCharges).add(amenityDamages);
            BigDecimal remainingAmount = totalAmount.subtract(depositDeducted);
            if (remainingAmount.compareTo(BigDecimal.ZERO) < 0) {
                remainingAmount = BigDecimal.ZERO;
            }

            String paymentMethod = request.getParameter("paymentMethod");

            Invoice invoice = new Invoice();
            invoice.setBookingId(bookingId);
            invoice.setRoomCharges(roomCharges);
            invoice.setConsumableCharges(consumableCharges);
            invoice.setAmenityDamages(amenityDamages);
            invoice.setDepositDeducted(depositDeducted);
            invoice.setTotalAmount(totalAmount);
            invoice.setRemainingAmount(remainingAmount);
            invoice.setPaymentMethod(paymentMethod);
            invoice.setCreatedBy(staff.getStaffId());

            dao.completeInvoicePayment(bookingId, paymentMethod, staff.getStaffId());
            session.setAttribute("successMessage", "Tạo hóa đơn thành công!");

            response.sendRedirect(request.getContextPath() + "/BillingList?invoiceId=" + dao.getInvoiceByBookingId(bookingId).getInvoiceId());

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/Checkout");
        }
    }
}
