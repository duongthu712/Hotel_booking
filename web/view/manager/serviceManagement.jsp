<%-- 
    Document   : service
    Document   : serviceManagement
    Created on : May 27, 2026, 10:50:44 PM
    Author     : Minh Thu
    Editer     : LinhLTHE200306
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.StaffAccount" %>
<%@ include file="/view/staff/header.jsp" %>
<%@ include file="/view/staff/navbar.jsp" %>
<%@page import="models.StaffAccount" %>
<%@page import="models.Service" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Service Management</title>
    </head>
    <body>



        <header>
            <div>
                <p>La Mer</p>
                <span>Quản lý</span>
            </div>
            <div>
                <% StaffAccount acc = (StaffAccount)request.getAttribute("account"); %>
            <% if (acc != null) { %>
            <span><%acc.getFullName()%></span>
                            
            </div>
        </header>
        <form action="ServiceList" method="GET">
            
        </form>
    </body>
</html>
