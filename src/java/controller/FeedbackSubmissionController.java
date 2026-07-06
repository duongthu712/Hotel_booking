package controller;

import dao.FeedbackDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Map;

public class FeedbackSubmissionController extends HttpServlet {

    private final FeedbackDAO feedbackDAO = new FeedbackDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String bookingIdRaw = request.getParameter("bookingId");

        if (bookingIdRaw == null || bookingIdRaw.trim().isEmpty()) {
            forwardForm(request, response);
            return;
        }

        int bookingId;

        try {
            bookingId = Integer.parseInt(bookingIdRaw);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Mã đặt phòng không hợp lệ.");
            forwardForm(request, response);
            return;
        }

        Map<String, Object> bookingInfo
                = feedbackDAO.getBookingFeedbackInfo(bookingId);

        if (bookingInfo.isEmpty()) {
            request.setAttribute("error", "Không tìm thấy thông tin đặt phòng.");
            forwardForm(request, response);
            return;
        }

        if (!isValidForFeedback(request, bookingInfo)) {
            forwardForm(request, response);
            return;
        }

        restoreFeedbackForm(request, bookingInfo);
        forwardForm(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if ("lookup".equals(action)) {
            handleLookup(request, response);
            return;
        }

        if ("submit".equals(action)) {
            handleSubmit(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/feedback-submission");
    }

    private void handleLookup(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String bookingCode = getTrimmed(request.getParameter("bookingCode"));
        String email = getTrimmed(request.getParameter("email"));

        request.setAttribute("bookingCodeInput", bookingCode);
        request.setAttribute("emailInput", email);

        if (bookingCode == null || email == null) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ mã đặt phòng và email.");
            forwardForm(request, response);
            return;
        }

        Map<String, Object> bookingInfo
                = feedbackDAO.getBookingFeedbackInfoByCodeAndEmail(
                        bookingCode,
                        email
                );

        if (bookingInfo.isEmpty()) {
            request.setAttribute("error",
                    "Không tìm thấy đơn đặt phòng phù hợp với mã đặt phòng và email.");
            forwardForm(request, response);
            return;
        }

        if (!isValidForFeedback(request, bookingInfo)) {
            forwardForm(request, response);
            return;
        }

        restoreFeedbackForm(request, bookingInfo);
        forwardForm(request, response);
    }

    private void handleSubmit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String bookingIdRaw = request.getParameter("bookingId");
        String ratingRaw = request.getParameter("rating");
        String comment = getTrimmed(request.getParameter("comment"));

        int bookingId;
        int rating;

        try {
            bookingId = Integer.parseInt(bookingIdRaw);
            rating = Integer.parseInt(ratingRaw);
        } catch (Exception e) {
            request.setAttribute("error", "Dữ liệu đánh giá không hợp lệ.");
            forwardForm(request, response);
            return;
        }

        Map<String, Object> bookingInfo
                = feedbackDAO.getBookingFeedbackInfo(bookingId);

        if (bookingInfo.isEmpty()) {
            request.setAttribute("error", "Không tìm thấy thông tin đặt phòng.");
            forwardForm(request, response);
            return;
        }

        if (!isValidForFeedback(request, bookingInfo)) {
            forwardForm(request, response);
            return;
        }

        if (rating < 1 || rating > 5) {
            request.setAttribute("error", "Vui lòng chọn số sao từ 1 đến 5.");
            restoreFeedbackForm(request, bookingInfo);
            forwardForm(request, response);
            return;
        }

        if (comment == null) {
            request.setAttribute("error", "Vui lòng nhập nội dung đánh giá.");
            restoreFeedbackForm(request, bookingInfo);
            forwardForm(request, response);
            return;
        }

        Integer guestId = (Integer) bookingInfo.get("guestId");

        boolean success = feedbackDAO.insertFeedback(
                bookingId,
                guestId,
                rating,
                comment
        );

        if (success) {
            response.sendRedirect(request.getContextPath() + "/feedback-list");
        } else {
            request.setAttribute("error", "Gửi đánh giá thất bại. Vui lòng thử lại.");
            restoreFeedbackForm(request, bookingInfo);
            forwardForm(request, response);
        }
    }

    private boolean isValidForFeedback(HttpServletRequest request,
            Map<String, Object> bookingInfo) {

        String status = (String) bookingInfo.get("status");
        boolean hasFeedback = (Boolean) bookingInfo.get("hasFeedback");

        if (!"Đã trả phòng".equals(status)) {
            request.setAttribute("error",
                    "Bạn chỉ có thể đánh giá sau khi đơn đặt phòng đã trả phòng.");
            return false;
        }

        if (hasFeedback) {
            request.setAttribute("error",
                    "Đơn đặt phòng này đã được đánh giá trước đó.");
            return false;
        }

        return true;
    }

    private void restoreFeedbackForm(HttpServletRequest request,
            Map<String, Object> bookingInfo) {

        request.setAttribute("showFeedbackForm", true);
        request.setAttribute("bookingId", bookingInfo.get("bookingId"));
        request.setAttribute("guestName", bookingInfo.get("guestName"));
        request.setAttribute("bookingCode", bookingInfo.get("bookingCode"));
    }

    private String getTrimmed(String value) {
        if (value == null) {
            return null;
        }

        value = value.trim();

        return value.isEmpty() ? null : value;
    }

    private void forwardForm(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/view/user/feedback-submission.jsp")
                .forward(request, response);
    }
}
