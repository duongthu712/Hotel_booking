package controller;

import dao.FeedbackDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

public class FeedbackListController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        FeedbackDAO dao = new FeedbackDAO();

        int pageSize = 10;
        int page = 1;

        String pageParam = request.getParameter("page");

        if (pageParam != null) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        if (page < 1) {
            page = 1;
        }

        int totalFeedbacks = dao.getTotalFeedbacks();

        int totalPages = (int) Math.ceil((double) totalFeedbacks / pageSize);

        if (totalPages == 0) {
            totalPages = 1;
        }

        if (page > totalPages) {
            page = totalPages;
        }

        int rating5 = dao.countByRating(5);
        int rating4 = dao.countByRating(4);
        int rating3 = dao.countByRating(3);
        int rating2 = dao.countByRating(2);
        int rating1 = dao.countByRating(1);

        double averageRating = dao.getAverageRating();

        request.setAttribute("feedbacks", dao.getFeedbacksByPage(page, pageSize));
        request.setAttribute("averageRating", String.format("%.1f", averageRating));
        request.setAttribute("totalFeedbacks", totalFeedbacks);

        request.setAttribute("rating5", rating5);
        request.setAttribute("rating4", rating4);
        request.setAttribute("rating3", rating3);
        request.setAttribute("rating2", rating2);
        request.setAttribute("rating1", rating1);

        request.setAttribute("rating5Percent", totalFeedbacks == 0 ? 0 : rating5 * 100 / totalFeedbacks);
        request.setAttribute("rating4Percent", totalFeedbacks == 0 ? 0 : rating4 * 100 / totalFeedbacks);
        request.setAttribute("rating3Percent", totalFeedbacks == 0 ? 0 : rating3 * 100 / totalFeedbacks);
        request.setAttribute("rating2Percent", totalFeedbacks == 0 ? 0 : rating2 * 100 / totalFeedbacks);
        request.setAttribute("rating1Percent", totalFeedbacks == 0 ? 0 : rating1 * 100 / totalFeedbacks);

        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        request.getRequestDispatcher("/view/public/feedback-list.jsp")
                .forward(request, response);
    }
}
