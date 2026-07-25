<%--
    Document   : header
    Created on : Jun 6, 2026, 10:45:29 AM
    Author     : LinhLTHE200306
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.StaffAccount"%>
<%@page import="dao.HotelInfoDAO"%>

<%
    HotelInfoDAO hotelInfoDAO = new HotelInfoDAO();
    String hotelName = hotelInfoDAO.getHotelName();
    StaffAccount headAcc = (StaffAccount) session.getAttribute("staff");
%>

<link rel="stylesheet"
      href="${pageContext.request.contextPath}/view/assets/css/staff-header.css"
      type="text/css">

<% if (headAcc != null) { %>
<header class="staff-header">
    <div class="staff-header-info">
        <p class="staff-header-logo"><%= hotelName %></p>
        <p class="staff-header-role"><%= headAcc.getRole() %></p>
    </div>

    <div class="staff-header-user">
        <a href="${pageContext.request.contextPath}/profile" class="staff-name">
            <%= headAcc.getFullName() %>
        </a>

        <form action="${pageContext.request.contextPath}/logout"
              method="post"
              style="display: inline;">
            <button type="submit" class="logout-btn">
                Đăng xuất
            </button>
        </form>
    </div>
</header>
<% } %>

<script>
    const BACK_FORWARD_NAVIGATION_TYPE = "back_forward";

    window.addEventListener("pageshow", function (event) {
        const navigationEntries = window.performance.getEntriesByType("navigation");
        const navigationType = navigationEntries.length > 0 ? navigationEntries[0].type : "";

        if (event.persisted || navigationType === BACK_FORWARD_NAVIGATION_TYPE) {
            window.location.reload();
        }
    });
</script>