package controller;

import dao.HotelPolicyDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import model.HotelPolicy;
import model.StaffAccount;

/**
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-07-09
 */
public class PolicyListController extends HttpServlet {

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
        String filterType = request.getParameter("filterType");

        int page = 1;
        try {
            String pageParam = request.getParameter("page");
            if (pageParam != null) {
                page = Integer.parseInt(pageParam);
            }
        } catch (NumberFormatException e) {
            page = 1;
        }

        int recordsPerPage = 10;
        HotelPolicyDAO pDao = new HotelPolicyDAO();
        try {
            List<HotelPolicy> policyList = pDao.getFilteredPolicies(keyword, filterType);
            if (policyList == null) {
                policyList = new ArrayList<>();
            }

            List<String> policyTypeList = new ArrayList<>(Arrays.asList(
                    "Nhận/Trả phòng",
                    "Hủy đặt phòng",
                    "Vật nuôi",
                    "Hút thuốc",
                    "Thanh toán"
            ));

// Thêm các loại từ DB nếu chưa có
            for (String dbType : pDao.getAllPolicyTypes()) {
                if (!policyTypeList.contains(dbType)) {
                    policyTypeList.add(dbType);
                }
            }

            if (!policyTypeList.contains("Khác")) {
                policyTypeList.add("Khác");
            }

            int totalRecords = policyList.size();
            int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);
            if (page < 1) {
                page = 1;
            }
            if (page > totalPages && totalPages > 0) {
                page = totalPages;
            }
            int start = (page - 1) * recordsPerPage;
            int end = Math.min(start + recordsPerPage, totalRecords);

            List<HotelPolicy> pagedList;
            if (totalRecords > 0) {
                pagedList = policyList.subList(start, end);
            } else {
                pagedList = policyList;
            }

            request.setAttribute("policyList", pagedList);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("keyword", keyword);
            request.setAttribute("filterType", filterType);
            request.setAttribute("policyTypeList", policyTypeList);

            request.getRequestDispatcher("/view/manager/policy-management.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("policyList", new ArrayList<>());
            request.setAttribute("currentPage", 1);
            request.setAttribute("totalPages", 1);
            request.setAttribute("keyword", keyword);
            request.setAttribute("filterType", filterType);
            request.setAttribute("policyTypeList", new ArrayList<String>());

            request.getRequestDispatcher("/view/manager/policy-management.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Hotel Policy List Controller";
    }
}
