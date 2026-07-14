package controller;

import dao.FeedbackDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import model.Feedback;

public class FeedbackManagementController extends HttpServlet {

    private static final int PAGE_SIZE = 10;

    private final FeedbackDAO feedbackDAO = new FeedbackDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String keyword = clean(request.getParameter("keyword"));
        Integer rating = parseRating(request.getParameter("rating"));
        Boolean visible = parseVisible(request.getParameter("visible"));
        String sort = parseSort(request.getParameter("sort"));
        int currentPage = parsePositiveInteger(request.getParameter("page"), 1);

        int totalFilteredFeedbacks = feedbackDAO.countManagerFeedbacks(
                keyword,
                rating,
                visible
        );

        int totalPages = (int) Math.ceil(
                (double) totalFilteredFeedbacks / PAGE_SIZE
        );

        if (totalPages < 1) {
            totalPages = 1;
        }

        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        List<Feedback> feedbacks = feedbackDAO.getManagerFeedbacks(
                keyword,
                rating,
                visible,
                sort,
                currentPage,
                PAGE_SIZE
        );

        Map<String, Object> statistics
                = feedbackDAO.getManagerFeedbackStatistics();

        int totalFeedbacks = getInteger(statistics, "totalFeedbacks");
        int visibleFeedbacks = getInteger(statistics, "visibleFeedbacks");
        int hiddenFeedbacks = getInteger(statistics, "hiddenFeedbacks");

        int rating5 = getInteger(statistics, "rating5");
        int rating4 = getInteger(statistics, "rating4");
        int rating3 = getInteger(statistics, "rating3");
        int rating2 = getInteger(statistics, "rating2");
        int rating1 = getInteger(statistics, "rating1");

        double averageRating = getDouble(
                statistics,
                "averageRating"
        );

        request.setAttribute("feedbacks", feedbacks);

        request.setAttribute("totalFeedbacks", totalFeedbacks);
        request.setAttribute(
                "averageRating",
                String.format("%.1f", averageRating)
        );

        request.setAttribute("visibleFeedbacks", visibleFeedbacks);
        request.setAttribute("hiddenFeedbacks", hiddenFeedbacks);

        request.setAttribute("rating5", rating5);
        request.setAttribute("rating4", rating4);
        request.setAttribute("rating3", rating3);
        request.setAttribute("rating2", rating2);
        request.setAttribute("rating1", rating1);

        request.setAttribute(
                "rating5Percent",
                calculatePercent(rating5, totalFeedbacks)
        );

        request.setAttribute(
                "rating4Percent",
                calculatePercent(rating4, totalFeedbacks)
        );

        request.setAttribute(
                "rating3Percent",
                calculatePercent(rating3, totalFeedbacks)
        );

        request.setAttribute(
                "rating2Percent",
                calculatePercent(rating2, totalFeedbacks)
        );

        request.setAttribute(
                "rating1Percent",
                calculatePercent(rating1, totalFeedbacks)
        );

        request.setAttribute(
                "visiblePercent",
                calculatePercent(visibleFeedbacks, totalFeedbacks)
        );

        request.setAttribute(
                "hiddenPercent",
                calculatePercent(hiddenFeedbacks, totalFeedbacks)
        );

        request.setAttribute(
                "totalFilteredFeedbacks",
                totalFilteredFeedbacks
        );

        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", PAGE_SIZE);

        request.setAttribute("keyword", keyword == null ? "" : keyword);
        request.setAttribute("selectedRating", rating);
        request.setAttribute("selectedVisible", visible);
        request.setAttribute("selectedSort", sort);

        setPaginationRange(request, currentPage, totalPages);

        loadSelectedFeedback(request);

        request.getRequestDispatcher(
                "/view/manager/feedback-management.jsp"
        ).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String action = clean(request.getParameter("action"));
        Integer feedbackId = parseInteger(
                request.getParameter("feedbackId")
        );

        if (feedbackId == null) {
            redirectWithMessage(
                    request,
                    response,
                    "error",
                    "Mã đánh giá không hợp lệ."
            );
            return;
        }

        if ("hide".equals(action)) {
            handleHideFeedback(
                    request,
                    response,
                    feedbackId
            );
            return;
        }

        if ("show".equals(action)) {
            handleShowFeedback(
                    request,
                    response,
                    feedbackId
            );
            return;
        }

        redirectWithMessage(
                request,
                response,
                "error",
                "Thao tác không hợp lệ."
        );
    }

    private void handleHideFeedback(
            HttpServletRequest request,
            HttpServletResponse response,
            int feedbackId)
            throws IOException {

        String hiddenReason = clean(
                request.getParameter("hiddenReason")
        );

        if (hiddenReason == null) {
            redirectWithMessage(
                    request,
                    response,
                    "error",
                    "Vui lòng nhập lý do ẩn đánh giá."
            );
            return;
        }

        if (hiddenReason.length() > 255) {
            redirectWithMessage(
                    request,
                    response,
                    "error",
                    "Lý do ẩn không được vượt quá 255 ký tự."
            );
            return;
        }

        Feedback feedback = feedbackDAO.getFeedbackById(feedbackId);

        if (feedback == null) {
            redirectWithMessage(
                    request,
                    response,
                    "error",
                    "Không tìm thấy đánh giá cần ẩn."
            );
            return;
        }

        if (!feedback.isVisible()) {
            redirectWithMessage(
                    request,
                    response,
                    "error",
                    "Đánh giá này đã được ẩn trước đó."
            );
            return;
        }

        boolean success = feedbackDAO.hideFeedback(
                feedbackId,
                hiddenReason
        );

        if (success) {
            redirectWithMessage(
                    request,
                    response,
                    "success",
                    "Đã ẩn đánh giá khỏi trang khách hàng."
            );
        } else {
            redirectWithMessage(
                    request,
                    response,
                    "error",
                    "Ẩn đánh giá thất bại. Vui lòng thử lại."
            );
        }
    }

    private void handleShowFeedback(
            HttpServletRequest request,
            HttpServletResponse response,
            int feedbackId)
            throws IOException {

        Feedback feedback = feedbackDAO.getFeedbackById(feedbackId);

        if (feedback == null) {
            redirectWithMessage(
                    request,
                    response,
                    "error",
                    "Không tìm thấy đánh giá cần hiển thị."
            );
            return;
        }

        if (feedback.isVisible()) {
            redirectWithMessage(
                    request,
                    response,
                    "error",
                    "Đánh giá này đang được hiển thị."
            );
            return;
        }

        boolean success = feedbackDAO.showFeedback(feedbackId);

        if (success) {
            redirectWithMessage(
                    request,
                    response,
                    "success",
                    "Đã hiển thị lại đánh giá trên trang khách hàng."
            );
        } else {
            redirectWithMessage(
                    request,
                    response,
                    "error",
                    "Hiển thị lại đánh giá thất bại. Vui lòng thử lại."
            );
        }
    }

    private void loadSelectedFeedback(HttpServletRequest request) {
        Integer feedbackId = parseInteger(
                request.getParameter("feedbackId")
        );

        if (feedbackId == null) {
            return;
        }

        Feedback selectedFeedback
                = feedbackDAO.getFeedbackById(feedbackId);

        if (selectedFeedback == null) {
            request.setAttribute(
                    "error",
                    "Không tìm thấy chi tiết đánh giá."
            );
            return;
        }

        request.setAttribute(
                "selectedFeedback",
                selectedFeedback
        );

        request.setAttribute(
                "openDetailModal",
                true
        );
    }

    private void redirectWithMessage(
            HttpServletRequest request,
            HttpServletResponse response,
            String messageType,
            String message)
            throws IOException {

        StringBuilder url = new StringBuilder();

        url.append(
                request.getContextPath()
        );

        url.append(
                "/feedback-management?"
        );

        appendCurrentFilters(request, url);

        if (url.charAt(url.length() - 1) != '?'
                && url.charAt(url.length() - 1) != '&') {

            url.append("&");
        }

        url.append(messageType);
        url.append("=");
        url.append(
                encode(message)
        );

        response.sendRedirect(url.toString());
    }

    private void appendCurrentFilters(
            HttpServletRequest request,
            StringBuilder url) {

        boolean hasParameter = false;

        String keyword = clean(
                request.getParameter("keyword")
        );

        String rating = clean(
                request.getParameter("rating")
        );

        String visible = clean(
                request.getParameter("visible")
        );

        String sort = clean(
                request.getParameter("sort")
        );

        String page = clean(
                request.getParameter("page")
        );

        if (keyword != null) {
            appendQueryParameter(
                    url,
                    "keyword",
                    keyword,
                    hasParameter
            );

            hasParameter = true;
        }

        if (rating != null) {
            appendQueryParameter(
                    url,
                    "rating",
                    rating,
                    hasParameter
            );

            hasParameter = true;
        }

        if (visible != null) {
            appendQueryParameter(
                    url,
                    "visible",
                    visible,
                    hasParameter
            );

            hasParameter = true;
        }

        if (sort != null) {
            appendQueryParameter(
                    url,
                    "sort",
                    sort,
                    hasParameter
            );

            hasParameter = true;
        }

        if (page != null) {
            appendQueryParameter(
                    url,
                    "page",
                    page,
                    hasParameter
            );
        }
    }

    private void appendQueryParameter(
            StringBuilder url,
            String name,
            String value,
            boolean hasParameter) {

        if (hasParameter) {
            url.append("&");
        }

        url.append(name);
        url.append("=");
        url.append(encode(value));
    }

    private String encode(String value) {
        return URLEncoder.encode(
                value,
                StandardCharsets.UTF_8
        );
    }

    private void setPaginationRange(
            HttpServletRequest request,
            int currentPage,
            int totalPages) {

        int startPage = Math.max(
                1,
                currentPage - 2
        );

        int endPage = Math.min(
                totalPages,
                startPage + 4
        );

        if (endPage - startPage < 4) {
            startPage = Math.max(
                    1,
                    endPage - 4
            );
        }

        request.setAttribute("startPage", startPage);
        request.setAttribute("endPage", endPage);
    }

    private int calculatePercent(
            int value,
            int total) {

        if (total <= 0) {
            return 0;
        }

        return (int) Math.round(
                value * 100.0 / total
        );
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }

        value = value.trim();

        return value.isEmpty() ? null : value;
    }

    private Integer parseInteger(String value) {
        value = clean(value);

        if (value == null) {
            return null;
        }

        try {
            int number = Integer.parseInt(value);

            return number > 0 ? number : null;

        } catch (NumberFormatException e) {
            return null;
        }
    }

    private int parsePositiveInteger(
            String value,
            int defaultValue) {

        Integer number = parseInteger(value);

        return number == null
                ? defaultValue
                : number;
    }

    private Integer parseRating(String value) {
        value = clean(value);

        if (value == null
                || "all".equalsIgnoreCase(value)) {

            return null;
        }

        try {
            int rating = Integer.parseInt(value);

            if (rating >= 1 && rating <= 5) {
                return rating;
            }

        } catch (NumberFormatException e) {
            return null;
        }

        return null;
    }

    private Boolean parseVisible(String value) {
        value = clean(value);

        if (value == null
                || "all".equalsIgnoreCase(value)) {

            return null;
        }

        if ("visible".equalsIgnoreCase(value)
                || "true".equalsIgnoreCase(value)
                || "1".equals(value)) {

            return true;
        }

        if ("hidden".equalsIgnoreCase(value)
                || "false".equalsIgnoreCase(value)
                || "0".equals(value)) {

            return false;
        }

        return null;
    }

    private String parseSort(String value) {
        value = clean(value);

        if ("oldest".equals(value)
                || "rating-high".equals(value)
                || "rating-low".equals(value)) {

            return value;
        }

        return "newest";
    }

    private int getInteger(
            Map<String, Object> map,
            String key) {

        Object value = map.get(key);

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        return 0;
    }

    private double getDouble(
            Map<String, Object> map,
            String key) {

        Object value = map.get(key);

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        return 0;
    }
}
