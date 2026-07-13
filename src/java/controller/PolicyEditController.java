package controller;

import dao.HotelPolicyDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.HotelPolicy;
import model.StaffAccount;

/**
 * @author LinhLTHE200306
 * @version 1.0
 * @since 2026-07-09
 */
public class PolicyEditController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");

        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int policyId = Integer.parseInt(request.getParameter("policyId"));

            String page = request.getParameter("page");
            String keyword = request.getParameter("keyword");
            String filterType = request.getParameter("filterType");

            HotelPolicyDAO dao = new HotelPolicyDAO();
            HotelPolicy policy = dao.getHotelPolicyById(policyId);

            session.setAttribute("policyToEdit", policy);
            session.setAttribute("editPolicyType", policy.getPolicyType()); 
            response.sendRedirect(buildRedirectUrl(request, page, keyword, filterType));

        } catch (Exception e) {
            session.setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/PolicyList");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String policyIdStr = request.getParameter("policyId");
        String policyName = request.getParameter("policyName").trim();
        String policyType = request.getParameter("policyType");
        String description = request.getParameter("description").trim();
        String activeStr = request.getParameter("active");

        String page = request.getParameter("page");
        String keyword = request.getParameter("keyword");
        String filterType = request.getParameter("filterType");

        String errorMsg = dal.InputValidationUtil.validatePolicyInput(policyName, policyType, description);

        if (errorMsg != null) {
            session.setAttribute("errorMessage", errorMsg);
            session.setAttribute("openEditModal", "true");

            int policyId = Integer.parseInt(policyIdStr);
            boolean isActive = "true".equals(activeStr);
            HotelPolicy policyToEditTemp = new HotelPolicy(policyId, policyName, description, policyType, isActive);

            session.setAttribute("policyToEdit", policyToEditTemp);
            session.setAttribute("editPolicyType", policyType);

            response.sendRedirect(buildRedirectUrl(request, page, keyword, filterType));
            return;
        }

        try {
            int policyId = Integer.parseInt(policyIdStr);
            boolean isActive = "true".equals(activeStr);

            HotelPolicy updatedPolicy = new HotelPolicy(policyId, policyName, description, policyType, isActive);

            HotelPolicyDAO dao = new HotelPolicyDAO();
            dao.updateHotelPolicy(updatedPolicy);
            session.setAttribute("successMessage", "Cập nhật chính sách \"" + policyName.trim() + "\" thành công.");
        } catch (Exception e) {
            session.setAttribute("errorMessage", e.getMessage());
            session.setAttribute("openEditModal", "true");

            int policyId = Integer.parseInt(policyIdStr);
            HotelPolicy policyToEditTemp = new HotelPolicy(policyId, policyName, description, policyType, "true".equals(activeStr));
            session.setAttribute("policyToEdit", policyToEditTemp);
            session.setAttribute("editPolicyType", policyType);
        }

        response.sendRedirect(buildRedirectUrl(request, page, keyword, filterType));
    }

    private String buildRedirectUrl(HttpServletRequest request, String page, String keyword, String filterType) {
        StringBuilder url = new StringBuilder(request.getContextPath() + "/PolicyList");
        url.append("?page=").append(page != null && !page.isEmpty() ? page : "1");
        if (keyword != null && !keyword.trim().isEmpty()) {
            try {
                url.append("&keyword=").append(java.net.URLEncoder.encode(keyword.trim(), "UTF-8"));
            } catch (java.io.UnsupportedEncodingException e) {
                url.append("&keyword=").append(keyword.trim());
            }
        }
        if (filterType != null && !filterType.trim().isEmpty()) {
            try {
                url.append("&filterType=").append(java.net.URLEncoder.encode(filterType.trim(), "UTF-8"));
            } catch (java.io.UnsupportedEncodingException e) {
                url.append("&filterType=").append(filterType.trim());
            }
        }
        return url.toString();
    }

    @Override
    public String getServletInfo() {
        return "Hotel Policy Edit Controller";
    }
}