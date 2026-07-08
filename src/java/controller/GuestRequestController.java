package controller;

import dao.GuestRequestDAO;
import dao.RoomTypeDAO;
import model.Booking;
import model.RoomType;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "GuestRequestController", urlPatterns = {"/guest-request"})
public class GuestRequestController extends HttpServlet {

    private final GuestRequestDAO requestDAO = new GuestRequestDAO();
    private final RoomTypeDAO roomTypeDAO = new RoomTypeDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String bookingCode = request.getParameter("bookingCode");
        String email = request.getParameter("email");

        if (bookingCode == null || bookingCode.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/booking-detail?status=invalid_code");
            return;
        }

        Booking booking = requestDAO.getBookingBasicInfoByCode(bookingCode);

        // BẢO MẬT: Chặn đứng ngay từ luồng GET nếu đơn đang có request "Chờ xử lý"
        if (booking != null && requestDAO.hasPendingRequest(booking.getBookingId())) {
            dao.BookingDAO bookingDAO = new dao.BookingDAO();
            model.Guest guest = bookingDAO.getGuestByBookingId(booking.getBookingId());
            model.RoomType roomType = roomTypeDAO.getRoomDetailById(booking.getRoomTypeId());
            String status = bookingDAO.getDepositVerificationStatus(booking.getBookingId());

            request.setAttribute("booking", booking);
            request.setAttribute("guest", guest);
            request.setAttribute("bookingStatus", booking.getStatus());
            request.setAttribute("roomType", roomType);
            request.setAttribute("verificationStatus", (status == null) ? "Chưa gửi minh chứng" : status);
            request.setAttribute("searched", true);
            request.setAttribute("status", "duplicate_pending_error");

            request.getRequestDispatcher("/view/user/booking-detail.jsp").forward(request, response);
            return;
        }

        // Nếu booking là null (xử lý fallback hoặc redirect)
        if (booking == null) {
            response.sendRedirect(request.getContextPath() + "/booking-detail?status=not_found");
            return;
        }

        List<RoomType> roomTypesList = roomTypeDAO.getAllRoomTypes();
        request.setAttribute("booking", booking);
        request.setAttribute("roomTypesList", roomTypesList);

        request.getRequestDispatcher("/view/user/request-submission.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int bookingId = Integer.parseInt(request.getParameter("bookingId"));
            int guestId = Integer.parseInt(request.getParameter("guestId"));
            String requestType = request.getParameter("requestType");
            String bookingCode = request.getParameter("bookingCode");
            String email = request.getParameter("email");
            String emailParam = (email != null && !email.trim().isEmpty()) ? "&email=" + email : "";

            // Lấy thông tin mới nhất từ DB để phân quyền
            Booking booking = requestDAO.getBookingBasicInfoByCode(bookingCode);
            String bStatus = booking.getStatus(); // "Chờ xử lý", "Đã xác nhận", "Đã nhận phòng", "Đã trả phòng"

            if ("Đã trả phòng".equals(bStatus) || "Đã hủy".equals(bStatus)) {
                response.sendRedirect(request.getContextPath() + "/booking-detail?status=request_not_allowed");
                return;
            }
            // LOGIC PHÂN QUYỀN CHUẨN
            boolean canCancel = bStatus.equals("Chờ xử lý") || bStatus.equals("Đã xác nhận");
            boolean canChangeRoom = bStatus.equals("Đã xác nhận");
            boolean canExtend = bStatus.equals("Đã xác nhận") || bStatus.equals("Đã nhận phòng");

            if ("Hủy đặt phòng".equals(requestType) && !canCancel) {
                response.sendRedirect(request.getContextPath() + "/booking-detail?status=cannot_cancel");
                return;
            }
            if ("Đổi hạng phòng".equals(requestType) && !canChangeRoom) {
                response.sendRedirect(request.getContextPath() + "/booking-detail?status=cannot_change_room");
                return;
            }
            if ("Gia hạn phòng".equals(requestType) && !canExtend) {
                response.sendRedirect(request.getContextPath() + "/booking-detail?status=cannot_extend");
                return;
            }

            // CHẶN SPAM REQUEST
            if (requestDAO.hasPendingRequest(bookingId)) {
                response.sendRedirect(request.getContextPath() + "/booking-detail?bookingCode=" + bookingCode + emailParam + "&status=duplicate_pending_error");
                return;
            }

            // XỬ LÝ LOGIC INSERT REQUEST
            boolean isSuccess = false;
            String reasonDetails = request.getParameter("reason_details");
            int numRooms = Integer.parseInt(request.getParameter("numRooms"));

            switch (requestType) {
                case "Đổi hạng phòng":
                    int targetRoomTypeId = Integer.parseInt(request.getParameter("targetRoomTypeId"));
                    LocalDate checkIn = LocalDate.parse(request.getParameter("checkInDate"));
                    LocalDate checkOut = LocalDate.parse(request.getParameter("oldCheckoutDate"));

                    // Kiểm tra tính sẵn có: 
                    // Truyền bookingId vào để loại trừ chính đơn hàng đang đổi hạng ra khỏi danh sách chiếm phòng
                    if (requestDAO.checkRoomAvailability(targetRoomTypeId, checkIn, checkOut, numRooms, bookingId)) {
                        isSuccess = requestDAO.insertGuestRequest(bookingId, guestId, requestType, reasonDetails, null, null, targetRoomTypeId);
                    } else {
                        // Redirect về form với status lỗi rõ ràng
                        response.sendRedirect(request.getContextPath() + "/guest-request?bookingCode=" + bookingCode + emailParam + "&status=request_failed_no_room");
                        return;
                    }
                    break;

                case "Gia hạn phòng":
                    LocalDate checkInGoc = booking.getCheckinDate(); // Lấy ngày check-in gốc
                    LocalDate newCheckout = LocalDate.parse(request.getParameter("checkOutDate"));

                    // Kiểm tra toàn bộ dải ngày: từ CheckIn gốc đến Checkout mới
                    // Truyền bookingId vào để loại trừ chính đơn hàng này khỏi phép tính phòng trống
                    boolean isAvailable = requestDAO.checkRoomAvailability(
                            booking.getRoomTypeId(),
                            checkInGoc,
                            newCheckout,
                            numRooms,
                            booking.getBookingId()
                    );

                    if (isAvailable) {
                        isSuccess = requestDAO.insertGuestRequest(
                                bookingId,
                                guestId,
                                requestType,
                                request.getParameter("reason_details_extend"),
                                null,
                                newCheckout,
                                null
                        );
                    } else {
                        response.sendRedirect(request.getContextPath() + "/guest-request?bookingCode=" + bookingCode + emailParam + "&status=request_failed_no_room");
                        return;
                    }
                    break;

                case "Hủy đặt phòng":
                    isSuccess = requestDAO.insertGuestRequest(bookingId, guestId, requestType, request.getParameter("reason_details_cancel"), null, null, null);
                    break;
            }

            if (isSuccess) {
                response.sendRedirect(request.getContextPath() + "/booking-detail?bookingCode=" + bookingCode + emailParam + "&status=request_success");
            } else {
                response.sendRedirect(request.getContextPath() + "/guest-request?bookingCode=" + bookingCode + emailParam + "&status=request_failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/booking-detail?status=system_error");
        }
    }
}
