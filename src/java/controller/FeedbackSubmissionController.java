package controller;

import dao.FeedbackDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

public class FeedbackSubmissionController extends HttpServlet {

    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 5;
    private static final int MAX_COMMENT_LENGTH = 500;

    private static final String ACTION_LOOKUP = "lookup";
    private static final String ACTION_SUBMIT = "submit";
    private static final String CHECKED_OUT_STATUS = "Đã trả phòng";

    private static final String FEEDBACK_FORM_PATH = "/view/user/feedback-submission.jsp";
    private static final String FEEDBACK_LIST_PATH = "/feedback-list";
    private static final String FEEDBACK_SUBMISSION_PATH = "/feedback-submission";

    private static final String SUCCESS_MESSAGE_SESSION_KEY = "feedbackSuccessMessage";
    private static final String SUCCESS_MESSAGE = "Cảm ơn bạn đã gửi đánh giá về trải nghiệm tại La Mer Hotel.";

    private final FeedbackDAO feedbackDAO = new FeedbackDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Hiển thị form tra cứu hoặc form đánh giá theo booking.
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String bookingIdRaw = getTrimmed(request.getParameter("bookingId"));

        if (bookingIdRaw == null) {
            forwardForm(request, response);
            return;
        }

        Integer bookingId = parseInteger(bookingIdRaw);

        if (bookingId == null || bookingId <= 0) {
            request.setAttribute("error", "Mã đặt phòng không hợp lệ.");
            forwardForm(request, response);
            return;
        }

        Map<String, Object> bookingInfo = feedbackDAO.getBookingFeedbackInfo(bookingId);

        if (bookingInfo == null || bookingInfo.isEmpty()) {
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Xử lý tra cứu booking hoặc gửi đánh giá.
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = getTrimmed(request.getParameter("action"));

        if (ACTION_LOOKUP.equals(action)) {
            handleLookup(request, response);
            return;
        }

        if (ACTION_SUBMIT.equals(action)) {
            handleSubmit(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + FEEDBACK_SUBMISSION_PATH);
    }

    private void handleLookup(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Kiểm tra booking dựa trên mã đặt phòng và email.
        String bookingCode = getTrimmed(request.getParameter("bookingCode"));
        String email = getTrimmed(request.getParameter("email"));

        request.setAttribute("bookingCodeInput", bookingCode);
        request.setAttribute("emailInput", email);

        if (bookingCode == null || email == null) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ mã đặt phòng và email.");
            forwardForm(request, response);
            return;
        }

        Map<String, Object> bookingInfo = feedbackDAO.getBookingFeedbackInfoByCodeAndEmail(bookingCode, email);

        if (bookingInfo == null || bookingInfo.isEmpty()) {
            request.setAttribute("error", "Không tìm thấy đơn đặt phòng phù hợp với mã đặt phòng và email.");
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

    private void handleSubmit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Kiểm tra và lưu đánh giá của khách hàng.
        String bookingIdRaw = getTrimmed(request.getParameter("bookingId"));
        String ratingRaw = getTrimmed(request.getParameter("rating"));
        String comment = getTrimmed(request.getParameter("comment"));

        Integer bookingId = parseInteger(bookingIdRaw);
        Integer rating = parseInteger(ratingRaw);

        if (bookingId == null || bookingId <= 0) {
            request.setAttribute("error", "Thông tin đặt phòng không hợp lệ.");
            forwardForm(request, response);
            return;
        }

        Map<String, Object> bookingInfo = feedbackDAO.getBookingFeedbackInfo(bookingId);

        if (bookingInfo == null || bookingInfo.isEmpty()) {
            request.setAttribute("error", "Không tìm thấy thông tin đặt phòng.");
            forwardForm(request, response);
            return;
        }

        restoreFeedbackForm(request, bookingInfo);
        restoreFeedbackInput(request, rating, comment);

        if (!isValidForFeedback(request, bookingInfo)) {
            forwardForm(request, response);
            return;
        }

        if (rating == null || rating < MIN_RATING || rating > MAX_RATING) {
            request.setAttribute("error", "Vui lòng chọn số sao từ " + MIN_RATING + " đến " + MAX_RATING + ".");
            forwardForm(request, response);
            return;
        }

        if (comment == null) {
            request.setAttribute("error", "Vui lòng nhập nội dung đánh giá.");
            forwardForm(request, response);
            return;
        }

        if (comment.length() > MAX_COMMENT_LENGTH) {
            request.setAttribute("error", "Nội dung đánh giá không được vượt quá " + MAX_COMMENT_LENGTH + " ký tự.");
            forwardForm(request, response);
            return;
        }

        Integer guestId = getIntegerValue(bookingInfo.get("guestId"));
        boolean success = feedbackDAO.insertFeedback(bookingId, guestId, rating, comment);

        if (!success) {
            request.setAttribute("error", "Gửi đánh giá thất bại. Đơn đặt phòng có thể đã được đánh giá trước đó.");
            forwardForm(request, response);
            return;
        }

        HttpSession session = request.getSession();
        session.setAttribute(SUCCESS_MESSAGE_SESSION_KEY, SUCCESS_MESSAGE);

        response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + FEEDBACK_LIST_PATH));
    }

    private boolean isValidForFeedback(HttpServletRequest request, Map<String, Object> bookingInfo) {
        // Kiểm tra booking có đủ điều kiện gửi đánh giá hay không.
        String status = String.valueOf(bookingInfo.get("status"));
        boolean hasFeedback = Boolean.TRUE.equals(bookingInfo.get("hasFeedback"));

        if (!CHECKED_OUT_STATUS.equals(status)) {
            request.setAttribute("error", "Bạn chỉ có thể đánh giá sau khi đơn đặt phòng đã trả phòng.");
            return false;
        }

        if (hasFeedback) {
            request.setAttribute("error", "Đơn đặt phòng này đã được đánh giá trước đó.");
            return false;
        }

        return true;
    }

    private void restoreFeedbackForm(HttpServletRequest request, Map<String, Object> bookingInfo) {
        // Khôi phục thông tin booking trên form đánh giá.
        request.setAttribute("showFeedbackForm", true);
        request.setAttribute("bookingId", bookingInfo.get("bookingId"));
        request.setAttribute("guestName", bookingInfo.get("guestName"));
        request.setAttribute("bookingCode", bookingInfo.get("bookingCode"));
    }

    private void restoreFeedbackInput(HttpServletRequest request, Integer rating, String comment) {
        // Giữ lại số sao và nội dung khi form có lỗi.
        request.setAttribute("ratingInput", rating);
        request.setAttribute("commentInput", comment);
    }

    private Integer parseInteger(String value) {
        // Chuyển chuỗi thành số nguyên hợp lệ.
        if (value == null) {
            return null;
        }

        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer getIntegerValue(Object value) {
        // Chuyển giá trị Number trong Map thành Integer.
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        return null;
    }

    private String getTrimmed(String value) {
        // Loại bỏ khoảng trắng và chuyển chuỗi rỗng thành null.
        if (value == null) {
            return null;
        }

        String trimmedValue = value.trim();
        return trimmedValue.isEmpty() ? null : trimmedValue;
    }

    private void forwardForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Chuyển request đến trang nhập đánh giá.
        request.getRequestDispatcher(FEEDBACK_FORM_PATH).forward(request, response);
    }

    @Override
    public String getServletInfo() {
        // Trả về mô tả của servlet.
        return "Feedback Submission Controller";
    }
}
