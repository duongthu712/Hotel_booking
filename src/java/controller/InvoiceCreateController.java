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
import java.util.List;
import java.util.Map;
import model.Booking;
import model.Guest;
import model.GuestStay;
import model.HotelInfo;
import model.Invoice;
import model.InvoicePayment;
import model.RoomType;
import model.StaffAccount;

/**
 * @author LinhLTHE200306
 * @version 4.0
 * @since 2026-07-05
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

            Invoice invoice = dao.getInvoiceByBookingId(bookingId);
            if (invoice == null) {
                session.setAttribute("errorMessage", "Chưa có hóa đơn cho booking này.");
                response.sendRedirect(request.getContextPath() + "/Checkout");
                return;
            }

            List<InvoicePayment> payments = dao.getInvoicePaymentsByInvoiceId(invoice.getInvoiceId());

            Integer checkoutRoomId = (Integer) session.getAttribute("checkoutRoomId_" + bookingId);

            List<Map<String, Object>> bookingRooms = dao.getRoomNumbersByBookingId(bookingId);

            List<Map<String, Object>> checkoutRooms = new ArrayList<>();
            if (checkoutRoomId != null) {
                for (Map<String, Object> room : bookingRooms) {
                    if (checkoutRoomId.equals(room.get("roomId"))) {
                        checkoutRooms.add(room);
                        break;
                    }
                }
            }
            if (checkoutRooms.isEmpty() && !bookingRooms.isEmpty()) {
                checkoutRooms.add(bookingRooms.get(0));
                checkoutRoomId = (Integer) bookingRooms.get(0).get("roomId");
            }

            List<Map<String, Object>> existingServices;

            if (checkoutRoomId != null) {
                existingServices = dao.getBookingServicesByRoomId(bookingId, checkoutRoomId);
            } else {
                existingServices = new ArrayList<>();
            }

            Guest guest = dao.getGuestByBookingId(bookingId);
            RoomType roomType = dao.getRoomTypeByBookingId(bookingId);

            List<GuestStay> guestStays = dao.getGuestStaysByBookingId(bookingId);
            List<Map<String, Object>> roomTypeServices = dao.getRoomTypeServicesWithDetails(booking.getRoomTypeId());
            List<Map<String, Object>> roomTypeAmenities = dao.getRoomTypeAmenitiesWithDetails(booking.getRoomTypeId());

            HotelInfoDAO hdao = new HotelInfoDAO();
            HotelInfo hotelInfo = hdao.getHotelInfoById(1);

            String formattedCheckinTime = "14:00:00";
            if (booking.getActualCheckinTime() != null) {
                formattedCheckinTime = booking.getActualCheckinTime().format(TIME_24H);
            }

            long nights = Math.max(1, ChronoUnit.DAYS.between(
                    booking.getCheckinDate(), LocalDateTime.now().toLocalDate()));

            LocalDateTime expectedCheckout = booking.getCheckoutDate().atTime(12, 0);
            double lateChargePerRoom = dao.lateCheckoutSurcharge(expectedCheckout, LocalDateTime.now(),
                    booking.getBookedPricePerNight().doubleValue());
            BigDecimal lateChargeThisRoom = BigDecimal.valueOf(lateChargePerRoom);

            int checkoutRoomCount = 1;

            BigDecimal pureRoomCharges = booking.getBookedPricePerNight()
                    .multiply(BigDecimal.valueOf(nights));

            BigDecimal thisRoomTotalCharge = pureRoomCharges.add(lateChargeThisRoom);

            BigDecimal totalDeposit = booking.getDepositAmount() != null
                    ? booking.getDepositAmount() : BigDecimal.ZERO;
            int numRoomsBooking = booking.getNumRooms();
            BigDecimal depositPerRoom = BigDecimal.ZERO;
            if (numRoomsBooking > 0) {
                depositPerRoom = totalDeposit
                        .divide(BigDecimal.valueOf(numRoomsBooking), 2, RoundingMode.HALF_UP);
            }
            BigDecimal depositThisRoom = depositPerRoom;

            // Tính tổng service đã có của phòng này
            BigDecimal thisRoomServicesTotal = BigDecimal.ZERO;
            for (Map<String, Object> svc : existingServices) {
                BigDecimal totalPrice = (BigDecimal) svc.get("totalPrice");
                if (totalPrice != null) {
                    thisRoomServicesTotal = thisRoomServicesTotal.add(totalPrice);
                }
            }

            // Tổng tiền phòng này = phòng + late + service đã có
            BigDecimal thisRoomGrandTotal = thisRoomTotalCharge.add(thisRoomServicesTotal);

            // Số tiền còn phải trả = tổng - cọc
            BigDecimal remainingForThisRoom = thisRoomGrandTotal.subtract(depositThisRoom);
            if (remainingForThisRoom.compareTo(BigDecimal.ZERO) < 0) {
                remainingForThisRoom = BigDecimal.ZERO;
            }

            LocalDateTime depositVerifiedAt = dao.getDepositVerifiedAt(bookingId);
            String depositVerifiedAtStr = depositVerifiedAt != null
                    ? depositVerifiedAt.format(DISPLAY_FORMATTER) : "N/A";

            request.setAttribute("booking", booking);
            request.setAttribute("invoice", invoice);
            request.setAttribute("payments", payments);
            request.setAttribute("guest", guest);
            request.setAttribute("roomType", roomType);
            request.setAttribute("bookingRooms", bookingRooms);
            request.setAttribute("checkoutRooms", checkoutRooms);
            request.setAttribute("guestStays", guestStays);
            request.setAttribute("hotelInfo", hotelInfo);
            request.setAttribute("nights", nights);
            request.setAttribute("formattedCheckinTime", formattedCheckinTime);
            request.setAttribute("checkinDateDisplay", booking.getCheckinDate().format(DATE_FORMATTER));
            request.setAttribute("checkoutDateDisplay", booking.getCheckoutDate().format(DATE_FORMATTER));
            request.setAttribute("actualCheckoutTime", LocalDateTime.now().format(DISPLAY_FORMATTER));
            request.setAttribute("roomTypeServices", roomTypeServices);
            request.setAttribute("roomTypeAmenities", roomTypeAmenities);
            request.setAttribute("existingServices", existingServices);
            request.setAttribute("depositVerifiedAt", depositVerifiedAtStr);

            request.setAttribute("checkoutRoomCount", checkoutRoomCount);
            request.setAttribute("pureRoomCharges", pureRoomCharges);
            request.setAttribute("lateCharge", lateChargeThisRoom);
            request.setAttribute("thisRoomTotalCharge", thisRoomTotalCharge);
            request.setAttribute("thisRoomServicesTotal", thisRoomServicesTotal);
            request.setAttribute("thisRoomGrandTotal", thisRoomGrandTotal);
            request.setAttribute("depositThisRoom", depositThisRoom);
            request.setAttribute("remainingForThisRoom", remainingForThisRoom);

            request.setAttribute("totalBookingAmount", invoice.getTotalAmount());
            request.setAttribute("totalBookingRemaining", invoice.getRemainingAmount());
            request.setAttribute("checkoutRoomId", checkoutRoomId);

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
                session.setAttribute("errorMessage", "Booking không hợp lệ.");
                response.sendRedirect(request.getContextPath() + "/Checkout");
                return;
            }

            Invoice invoice = dao.getInvoiceByBookingId(bookingId);
            if (invoice == null) {
                session.setAttribute("errorMessage", "Không tìm thấy hóa đơn.");
                response.sendRedirect(request.getContextPath() + "/Checkout");
                return;
            }

            Integer checkoutRoomId = (Integer) session.getAttribute("checkoutRoomId_" + bookingId);
            if (checkoutRoomId == null) {
                List<Map<String, Object>> roomNumbers = dao.getRoomNumbersByBookingId(bookingId);
                for (Map<String, Object> room : roomNumbers) {
                    checkoutRoomId = (Integer) room.get("roomId");
                    break;
                }
            }

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
                        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(chargeQty));
                        dao.insertBookingService(bookingId, checkoutRoomId,
                                Integer.parseInt(roomTypeServiceIds[i]), unitPrice, qty, totalPrice);
                    }
                }
            }

            String[] amenityIds = request.getParameterValues("amenityId");
            String[] damageUnitPrices = request.getParameterValues("damageUnitPrice");
            String[] damageQuantities = request.getParameterValues("damageQuantity");

            if (amenityIds != null) {
                for (int i = 0; i < amenityIds.length; i++) {
                    int qty = Integer.parseInt(damageQuantities[i]);
                    if (qty > 0) {
                        BigDecimal unitPrice = new BigDecimal(damageUnitPrices[i]);
                        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(qty));
                        dao.insertRoomAmenityDamage(bookingId, checkoutRoomId,
                                Integer.parseInt(amenityIds[i]), qty, totalPrice);
                    }
                }
            }

            dao.recalculateInvoice(bookingId);

            String collectAmountStr = request.getParameter("collectAmount");
            if (collectAmountStr != null && !collectAmountStr.isEmpty()) {
                BigDecimal collectAmount = new BigDecimal(collectAmountStr);
                if (collectAmount.compareTo(BigDecimal.ZERO) > 0) {
                    String paymentMethod = request.getParameter("paymentMethod");
                    dao.addInvoicePayment(invoice.getInvoiceId(), bookingId, collectAmount,
                            paymentMethod, "Thanh toán tại quầy", staff.getStaffId());
                }
            }

            session.removeAttribute("checkoutRoomId_" + bookingId);
            session.removeAttribute("checkoutRoomCount_" + bookingId);

            session.setAttribute("successMessage", "Cập nhật hóa đơn thành công!");
            response.sendRedirect(request.getContextPath() + "/BillingList?invoiceId=" + invoice.getInvoiceId());

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/Checkout");
        }
    }
}
