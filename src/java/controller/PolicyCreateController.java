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
public class PolicyCreateController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

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
            session.setAttribute("openCreateModal", "true");
            session.setAttribute("keepPolicyName", policyName);
            session.setAttribute("keepPolicyType", policyType);
            session.setAttribute("keepDescription", description);
            session.setAttribute("keepActive", activeStr);
            session.setAttribute("editPolicyType", policyType);  // ✅ THÊM: để giữ selected trong modal
            response.sendRedirect(buildRedirectUrl(request, page, keyword, filterType));
            return;
        }

        try {
            boolean isActive = "true".equals(activeStr);
            HotelPolicy newPolicy = new HotelPolicy(0, policyName, description, policyType, isActive);

            HotelPolicyDAO dao = new HotelPolicyDAO();
            dao.createHotelPolicy(newPolicy);
            session.setAttribute("successMessage", "Thêm chính sách \"" + policyName.trim() + "\" thành công.");
        } catch (Exception e) {
            session.setAttribute("errorMessage", e.getMessage());
            session.setAttribute("openCreateModal", "true");
            session.setAttribute("keepPolicyName", policyName);
            session.setAttribute("keepPolicyType", policyType);
            session.setAttribute("keepDescription", description);
            session.setAttribute("keepActive", activeStr);
            session.setAttribute("editPolicyType", policyType);  // ✅ THÊM: để giữ selected trong modal
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
        return "Hotel Policy Create Controller";
    }
}
