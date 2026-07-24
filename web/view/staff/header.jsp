<%-- 
    Document   : header
    Created on : Jun 6, 2026, 10:45:29 AM
    Author     : LinhLTHE200306
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.StaffAccount"%>
<%@page import="dao.HotelInfoDAO"%>

<%
    HotelInfoDAO dao = new HotelInfoDAO();
    String hotelName = dao.getHotelName();
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
        <a href="${pageContext.request.contextPath}/profile"
           class="staff-name">
            <%= headAcc.getFullName() %>
        </a>

        <form action="${pageContext.request.contextPath}/logout"
              method="get"
              style="display: inline;">
            <button type="submit" class="logout-btn">
                Đăng xuất
            </button>
        </form>
    </div>
</header>
<% } %>