<%-- 
    Document   : policy-management.jsp
    Created on : Jun 3, 2026, 10:57:51 PM
    Author     : Minh Thu
--%>

<%-- 
    Document   : policy-management
    Created on : Jul 9, 2026
    Author     : LinhLTHE200306
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="model.StaffAccount" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/common.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/policy-management.css" type="text/css">
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý chính sách khách sạn</title>
    </head>

    <body data-edit-mode="${not empty policyToEdit}"
          data-create-mode="${not empty openCreateModal ? 'true' : 'false'}">
        <%@ include file="/view/staff/header.jsp" %>
        <%@ include file="/view/staff/navbar.jsp" %>
        <main class="conent-container">
            <div class="search-container">
                <form action="PolicyList" method="GET" class="search-form">
                    <div class="search-group">
                        <input type="text" name="keyword" class="search-input" placeholder="Tìm kiếm theo tiêu đề chính sách..." value="${keyword}">
                    </div>
                    <div class="search-group">
                        <select name="filterType" class="filter-select">
                            <option value="all" ${empty filterType || filterType eq 'all' ? 'selected' : ''}>Tất cả</option>
                            <c:forEach var="type" items="${policyTypeList}">
                                <option value="${type}" ${filterType eq type ? 'selected' : ''}>${type}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <button type="submit" class="search-btn">Tìm kiếm</button>
                    <a href="PolicyList" class="reset-btn">Làm mới</a>
                </form>
                <div class="header-action">
                    <button id="btn-create" class="btn-primary">Thêm chính sách mới</button>          
                </div>
            </div>


            <c:if test="${not empty errorMessage and (empty openCreateModal or openCreateModal eq 'false') and empty policyToEdit}">
                <div class="alert-message alert-error">
                    ${errorMessage}
                </div>
                <c:remove var="errorMessage" scope="session"/>
            </c:if>

            <c:if test="${not empty sessionScope.successMessage}">
                <div class="alert-message alert-success">
                    ${sessionScope.successMessage}
                </div>
                <c:remove var="successMessage" scope="session"/>
            </c:if>

            <div>
                <table class="data-table">
                    <thead class="data-table-thead">
                        <tr>
                            <th class="col-id">STT</th>
                            <th class="col-name">Tên chính sách</th>
                            <th class="col-type">Loại chính sách</th>
                            <th class="col-desc">Nội dung</th>
                            <th class="col-status">Trạng thái</th>
                            <th class="col-action">Hành động</th>
                        </tr>
                    </thead>

                    <tbody class="data-table-tbody">
                        <c:forEach var="plc" items="${policyList}" varStatus="loop">
                            <tr>
                                <td class="col-id">${(currentPage - 1) * 10 + loop.index + 1}</td>
                                <td class="col-name">${plc.getPolicyName()}</td>
                                <td class="col-type">
                                    <div class="srvType">${plc.getPolicyType()}</div>
                                </td>
                                <td class="col-desc">${plc.getDescription()}</td>
                                <td class="col-status">
                                    <div class="srvAct ${plc.isIs_active() ? 'status-active' : 'status-inactive'}">
                                        ${plc.isIs_active() ? 'HOẠT ĐỘNG' : 'TẠM DỪNG'}
                                    </div>
                                </td>
                                <td class="btn-action">
                                    <a class="btn-edit" href="PolicyEdit?policyId=${plc.getPolicyId()}&page=${currentPage}&keyword=${keyword}&filterType=${filterType}">Sửa</a>
                                    <form action="PolicyDelete" method="post" style="display: inline-block; margin: 0;">
                                        <input type="hidden" name="policyId" value="${plc.getPolicyId()}">
                                        <input type="hidden" name="page" value="${currentPage}">
                                        <input type="hidden" name="keyword" value="${keyword}">
                                        <input type="hidden" name="filterType" value="${filterType}">
                                        <button type="submit" onclick="return confirm('Bạn có chắc muốn xoá chính sách ${plc.getPolicyName()}?')">Xoá</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                    <c:if test="${empty policyList}">
                        <tr>
                            <td colspan="6" class="empty-message">
                                Không tìm thấy chính sách.
                            </td>
                        </tr>
                    </c:if>
                </table>

                <div class="pagination">
                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <a href="PolicyList?page=${i}&keyword=${keyword}&filterType=${filterType}" class="${currentPage == i ? 'active' : ''}">${i}</a>
                    </c:forEach>
                </div>
            </div>
        </main>

        <div class="service-popup ${not empty openCreateModal or not empty policyToEdit ? 'show' : ''}" id="policy-modal" 
             ${not empty openCreateModal or not empty policyToEdit ? 'style="display: flex;"' : ''}>

            <form action="${not empty policyToEdit ? 'PolicyEdit' : 'PolicyCreate'}" method="POST" id="policy-form" class="popup-content">
                <div class="service-popup-action">
                    <button class="btn-close" type="button" id="btn-close" style="font-size: 35px; margin-top: -25px; right: 5px;">&times;</button>
                </div>
                <h2 class="service-popup-title" id="modal-title">
                    ${not empty policyToEdit ? 'Chỉnh sửa chính sách' : 'Thêm chính sách mới'}
                </h2>

                <input type="hidden" name="policyId" id="policyId" value="${policyToEdit.getPolicyId()}">
                <input type="hidden" name="page" value="${currentPage}">
                <input type="hidden" name="keyword" value="${keyword}">
                <input type="hidden" name="filterType" value="${filterType}">

                <c:if test="${not empty errorMessage and (openCreateModal eq 'true' or not empty policyToEdit)}">
                    <div class="alert-message alert-error">
                        ${errorMessage}
                    </div>
                    <c:remove var="errorMessage" scope="session"/>
                </c:if>

                <div class="form-group">
                    <label class="input-label">Tên chính sách*</label>
                    <input class="service-popup-input-field" type="text" name="policyName" id="policyName" 
                           placeholder="Nhập tên chính sách..." 
                           value="${not empty policyToEdit ? policyToEdit.getPolicyName() : keepPolicyName}" required>
                </div>

                <div class="form-group">
                    <label class="input-label">Loại chính sách*</label>
                    <select class="service-popup-input-field" name="policyType" id="policyType" required>
                        <option value="" disabled ${empty editPolicyType ? 'selected' : ''}>-- Chọn loại chính sách --</option>
                        <c:forEach var="type" items="${policyTypeList}">
                            <option value="${type}" ${editPolicyType eq type ? 'selected' : ''}>${type}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label class="input-label">Nội dung chính sách*</label>
                    <textarea class="service-popup-input-field" name="description" id="description" 
                              placeholder="Nhập nội dung chi tiết..." required>${not empty policyToEdit ? policyToEdit.getDescription() : keepDescription}</textarea>
                </div>

                <div class="form-group toggle-row">
                    <label class="input-label">Trạng thái áp dụng</label>
                    <label class="toggle-switch">
                        <input type="checkbox" name="active" id="active" value="true" 
                               ${(not empty policyToEdit and policyToEdit.isIs_active()) or (empty policyToEdit and keepActive ne 'false') ? 'checked' : ''}>
                        <span class="toggle-slider"></span>
                    </label>
                </div>

                <div class="service-popup-action">
                    <button class="btn-submit" type="submit">Xác nhận lưu</button>
                </div>
            </form>
        </div>
        <c:remove var="policyToEdit" scope="session"/>
        <c:remove var="openEditModal" scope="session"/>
        <c:remove var="openCreateModal" scope="session"/>
        <c:remove var="keepPolicyName" scope="session"/>
        <c:remove var="keepPolicyType" scope="session"/>
        <c:remove var="keepDescription" scope="session"/>
        <c:remove var="keepActive" scope="session"/>
        <c:remove var="editPolicyType" scope="session"/>
        <script src="<%=request.getContextPath()%>/view/assets/javascript/alert.js"></script>
        <script src="<%=request.getContextPath()%>/view/assets/javascript/policy-management.js"></script>
    </body>
</html>
