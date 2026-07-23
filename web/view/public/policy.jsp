<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chính sách khách sạn - La Mer</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/policy.css?v=<%= System.currentTimeMillis() %>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/navbar.css?v=<%= System.currentTimeMillis() %>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/homepage.css?v=<%= System.currentTimeMillis() %>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/footer.css?v=<%= System.currentTimeMillis() %>">
        
    </head>

    <body>

            <jsp:include page="/view/common/navbar.jsp" />

        <main class="container">
            <section class="section policy-section">
                <h2>Chính sách & Quy định</h2>
                <p class="section-intro">Để đảm bảo trải nghiệm nghỉ dưỡng tuyệt vời và an toàn nhất, xin vui lòng lưu ý các quy định chung tại La Mer.</p>


                <div class="policy-groups">
                    <c:forEach var="entry" items="${groupedPolicies}">
                        <div class="policy-group">
                            <h3 class="policy-group-title">${entry.key}</h3>
                            <c:forEach var="policy" items="${entry.value}">
                                <div class="policy-item">
                                    <h4>${policy.policyName}</h4>
                                    <p>${policy.description}</p>
                                </div>
                            </c:forEach>
                        </div>
                    </c:forEach>
                </div>

            </section>
        </main>

        <jsp:include page="/view/common/footer.jsp" />
    </body>
</html>