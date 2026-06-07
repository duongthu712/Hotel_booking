/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.StaffAccount;

public class AuthorizationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String contextPath = req.getContextPath();
        String uri = req.getRequestURI();
        HttpSession session = req.getSession(false);
        // kiem tra nguoi dung da dang nhap hay chua
        if (session == null || session.getAttribute("staff") == null) {
            res.sendRedirect(contextPath + "/login");
            return;
        }
        StaffAccount staff = (StaffAccount) session.getAttribute("staff");
        String role = staff.getRole().trim();
        boolean allowed = false;
        // admin
        if (uri.startsWith(contextPath + "/view/admin/")) {
            allowed = role.equalsIgnoreCase("Quản trị viên");
        //manager
        } else if (uri.startsWith(contextPath + "/view/manager/")) {
            allowed = role.equalsIgnoreCase("Quản lý");
        //le tan
        } else if (uri.startsWith(contextPath + "/view/receptionist/")) {
            allowed = role.equalsIgnoreCase("Lễ tân");
        }
        //access denied
        if (!allowed) {
            res.sendRedirect(contextPath + "/access-denied");
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}