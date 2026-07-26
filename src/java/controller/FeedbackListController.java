package controller;

import dao.FeedbackDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Locale;

public class FeedbackListController extends HttpServlet {

    private static final int DEFAULT_PAGE = 1;
    private static final int PAGE_SIZE = 10;
    private static final int MIN_TOTAL_PAGES = 1;

    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 5;
    private static final int PERCENT_BASE = 100;

    private static final String AVERAGE_RATING_FORMAT = "%.1f";
    private static final String FEEDBACK_LIST_PAGE = "/view/public/feedback-list.jsp";

    private static final String SUCCESS_MESSAGE_SESSION_KEY = "feedbackSuccessMessage";
    private static final String SUCCESS_MESSAGE_REQUEST_KEY = "feedbackSuccessMessage";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Lấy danh sách đánh giá, thống kê và thông báo thành công dùng một lần.
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        FeedbackDAO feedbackDAO = new FeedbackDAO();

        int currentPage = parsePage(request.getParameter("page"));
        int totalFeedbacks = feedbackDAO.getTotalFeedbacks();
        int totalPages = calculateTotalPages(totalFeedbacks);

        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        double averageRating = feedbackDAO.getAverageRating();

        request.setAttribute("feedbacks", feedbackDAO.getFeedbacksByPage(currentPage, PAGE_SIZE));
        request.setAttribute("averageRating", String.format(Locale.US, AVERAGE_RATING_FORMAT, averageRating));
        request.setAttribute("totalFeedbacks", totalFeedbacks);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", PAGE_SIZE);

        setRatingStatistics(request, feedbackDAO, totalFeedbacks);
        moveSuccessMessage(request);
        request.getRequestDispatcher(FEEDBACK_LIST_PAGE).forward(request, response);
    }

    private void moveSuccessMessage(HttpServletRequest request) {
        // Chuyển thông báo từ session sang request rồi xóa khỏi session.
        HttpSession session = request.getSession(false);

        if (session == null) {
            return;
        }

        Object successMessage = session.getAttribute(SUCCESS_MESSAGE_SESSION_KEY);

        if (successMessage == null) {
            return;
        }

        request.setAttribute(SUCCESS_MESSAGE_REQUEST_KEY, successMessage);
        session.removeAttribute(SUCCESS_MESSAGE_SESSION_KEY);
    }

    private void setRatingStatistics(HttpServletRequest request, FeedbackDAO feedbackDAO, int totalFeedbacks) {
        // Tính số lượng và tỷ lệ phần trăm cho từng mức đánh giá.
        for (int rating = MAX_RATING; rating >= MIN_RATING; rating--) {
            int ratingCount = feedbackDAO.countByRating(rating);
            int ratingPercent = calculatePercent(ratingCount, totalFeedbacks);

            request.setAttribute("rating" + rating, ratingCount);
            request.setAttribute("rating" + rating + "Percent", ratingPercent);
        }
    }

    private int calculateTotalPages(int totalFeedbacks) {
        // Tính tổng số trang.
        int totalPages = (int) Math.ceil((double) totalFeedbacks / PAGE_SIZE);
        return Math.max(totalPages, MIN_TOTAL_PAGES);
    }

    private int calculatePercent(int value, int total) {
        // Tính tỷ lệ phần trăm và tránh chia cho không.
        if (total <= 0) {
            return 0;
        }

        return value * PERCENT_BASE / total;
    }

    private int parsePage(String pageValue) {
        // Chuyển parameter page thành số trang hợp lệ.
        if (pageValue == null || pageValue.trim().isEmpty()) {
            return DEFAULT_PAGE;
        }

        try {
            int page = Integer.parseInt(pageValue.trim());
            return Math.max(page, DEFAULT_PAGE);
        } catch (NumberFormatException e) {
            return DEFAULT_PAGE;
        }
    }

    @Override
    public String getServletInfo() {
        // Trả về mô tả của servlet.
        return "Feedback List Controller";
    }
}