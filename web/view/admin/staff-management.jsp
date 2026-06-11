<%-- 
    Document   : staff-management
    Created on : May 27, 2026, 10:54:03 PM
    Author     : Minh Thu
    Editor     : LinhLTHE200306
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.StaffAccount"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/common.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/view/assets/css/staff-management.css" type="text/css">
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Quản lý nhân viên</title>
    </head>

    <body data-edit-mode="${not empty editStaff}" data-detail-mode="${not empty selectedStaff}">
        <%@ include file="/view/staff/header.jsp" %>
        <%@ include file="/view/staff/navbar.jsp" %>

        <main class="content-container">


            <c:if test="${not empty sessionScope.errorMessage}">
                <div class="alert-message alert-error">
                    ${sessionScope.errorMessage}
                </div>
                <c:remove var="errorMessage" scope="session"/>
            </c:if>

            <c:if test="${not empty sessionScope.successMessage}">
                <div class="alert-message alert-success">
                    ${sessionScope.successMessage}
                </div>
                <c:remove var="successMessage" scope="session"/>
            </c:if>

            <div class="filter-bar">
                <form action="StaffAccountList" method="GET" class="filter-form">
                    <input type="text" name="searchText" class="filter-input" placeholder="Tìm theo tên, email..." value="${searchText}">

                    <select name="roleFilter" class="filter-input">
                        <option value="ALL" ${roleFilter == 'ALL' ? 'selected' : ''}>Tất cả chức vụ</option>
                        <option value="Lễ tân" ${roleFilter == 'Lễ tân' ? 'selected' : ''}>Lễ tân</option>
                        <option value="Quản lý" ${roleFilter == 'Quản lý' ? 'selected' : ''}>Quản lý</option>
                        <option value="Quản trị viên" ${roleFilter == 'Quản trị viên' ? 'selected' : ''}>Quản trị viên</option>
                    </select>

                    <button type="submit" class="btn-filter">Tìm kiếm</button>
                </form>
                <div class="header-action">
                    <a href="StaffAccountCreate" class="btn-primary">Thêm nhân viên mới</a>
                </div>
            </div>

            <div>
                <table class="data-table">
                    <thead class="data-table-thead">
                        <tr>
                            <th class="col-id">ID</th>
                            <th class="col-name">Họ tên</th>
                            <th class="col-username">Tên đăng nhập</th>
                            <th class="col-email">Email</th>
                            <th class="col-phone">Số điện thoại</th>
                            <th class="col-role">Chức vụ</th>
                            <th class="col-status">Trạng thái</th>
                            <th class="col-action">Hành động</th>
                        </tr>
                    </thead>
                    <tbody class="data-table-tbody">
                        <c:forEach var="s" items="${staffList}">
                            <tr>
                                <td class="col-id">${s.getStaffId()}</td>
                                <%-- Bấm vào tên để xem chi tiết --%>
                                <td class="col-name">
                                    <a href="StaffAccountDetail?staffId=${s.getStaffId()}" class="staff-name-link">
                                        ${s.getFullName()}
                                    </a>
                                </td>
                                <td class="col-username">${s.getUsername()}</td>
                                <td class="col-email">${s.getEmail()}</td>
                                <td class="col-phone">${s.getPhone()}</td>
                                <td class="col-role">
                                    <div class="staffRole">${s.getRole()}</div>
                                </td>
                                <td class="col-status">
                                    <div class="staffAct ${s.isActive() ? 'status-active' : 'status-inactive'}">
                                        ${s.isActive() ? 'ACTIVE' : 'INACTIVE'}
                                    </div>
                                </td>
                                <td class="btn-action">
                                    <a class="btn-edit" href="StaffAccountEdit?staffId=${s.getStaffId()}">Sửa</a>
                                    <form action="StaffAccountDelete" method="post" style="display: inline-block; margin: 0;">
                                        <input type="hidden" name="staffId" value="${s.getStaffId()}">
                                        <button type="submit" onclick="return confirm('Bạn có chắc muốn xoá nhân viên ${s.getFullName()}?')">Xoá</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

                <div class="pagination">
                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <a href="StaffAccountList?searchText=${searchText}&roleFilter=${roleFilter}&page=${i}" 
                           class="${currentPage == i ? 'active' : ''}">
                            ${i}
                        </a>
                    </c:forEach>
                </div>
            </div>
        </main>

        <div class="staff-modal" id="detail-modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h2>Chi tiết nhân viên</h2>
                </div>
                <div class="modal-body">
                    <c:if test="${selectedStaff != null}">
                        <div class="detail-row"><strong>ID:</strong> ${selectedStaff.getStaffId()}</div>
                        <div class="detail-row"><strong>Họ tên:</strong> ${selectedStaff.getFullName()}</div>
                        <div class="detail-row"><strong>Tên đăng nhập:</strong> ${selectedStaff.getUsername()}</div>
                        <div class="detail-row"><strong>Email:</strong> ${selectedStaff.getEmail()}</div>
                        <div class="detail-row"><strong>Số điện thoại:</strong> ${selectedStaff.getPhone()}</div>
                        <div class="detail-row"><strong>Chức vụ:</strong> ${selectedStaff.getRole()}</div>
                        <div class="detail-row"><strong>Trạng thái:</strong> ${selectedStaff.isActive() ? 'Đang hoạt động' : 'Ngừng hoạt động'}</div>
                        <div class="detail-row"><strong>Ngày tạo:</strong> <fmt:formatDate value="${selectedStaff.getCreatedAt()}" pattern="dd/MM/yyyy HH:mm"/></div>
                    </c:if>
                </div>
                <div class="service-popup-action">
                    <button type="button" class="btn-close" id="btn-close-detail">Đóng</button>
                    <a class="btn-submit" href="StaffAccountEdit?staffId=${selectedStaff.getStaffId()}">Sửa</a>
                </div>
            </div>
        </div>

        <div class="staff-modal" id="edit-modal">
            <form action="StaffAccountEdit" method="post" id="edit-form" class="modal-content">
                <div class="modal-header">
                    <h2>Chỉnh sửa nhân viên</h2>
                </div>
                <div class="modal-body">
                    <c:if test="${editStaff != null}">
                        <input type="hidden" name="staffId" value="${editStaff.getStaffId()}">

                        <div class="form-group">
                            <label class="input-label">ID</label>
                            <input class="service-popup-input-field" type="text" value="${editStaff.getStaffId()}" readonly>
                        </div>

                        <div class="form-group">
                            <label class="input-label">Tên đăng nhập</label>
                            <input class="service-popup-input-field" type="text" value="${editStaff.getUsername()}" readonly>
                        </div>

                        <div class="form-group">
                            <label class="input-label">Họ tên</label>
                            <input class="service-popup-input-field" type="text" name="fullName" value="${editStaff.getFullName()}" required>
                        </div>

                        <div class="form-group">
                            <label class="input-label">Email</label>
                            <input class="service-popup-input-field" type="email" name="email" value="${editStaff.getEmail()}" required>
                        </div>

                        <div class="form-group">
                            <label class="input-label">Số điện thoại</label>
                            <input class="service-popup-input-field" type="text" name="phone" value="${editStaff.getPhone()}">
                        </div>

                        <div class="form-group">
                            <label class="input-label">Chức vụ</label>
                            <select class="service-popup-input-field" name="role">
                                <option value="Lễ tân" ${editStaff.getRole() == 'Lễ tân' ? 'selected' : ''}>Lễ tân</option>
                                <option value="Quản lý" ${editStaff.getRole() == 'Quản lý' ? 'selected' : ''}>Quản lý</option>
                                <option value="Quản trị viên" ${editStaff.getRole() == 'Quản trị viên' ? 'selected' : ''}>Quản trị viên</option>
                            </select>
                        </div>

                        <div class="form-group toggle-row">
                            <label class="input-label">Trạng thái hoạt động</label>
                            <label class="toggle-switch">
                                <input type="checkbox" name="active" value="true" ${editStaff.isActive() ? 'checked' : ''}>
                                <span class="toggle-slider"></span>
                            </label>
                        </div>
                    </c:if>
                </div>
                <div class="service-popup-action">
                    <button type="button" class="btn-close" id="btn-close-edit">Huỷ</button>
                    <button type="submit" class="btn-submit">Lưu thay đổi</button>
                </div>
            </form>
        </div>

        <script src="${pageContext.request.contextPath}/view/assets/javascript/staff-account-management.js"></script>
    </body>
</html>
