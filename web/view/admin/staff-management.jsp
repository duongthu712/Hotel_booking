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

    <body data-edit-mode="${not empty editStaff}" 
          data-detail-mode="${not empty selectedStaff}" 
          data-create-mode="${not empty sessionScope.openCreateModal}">
        <%@ include file="/view/staff/header.jsp" %>
        <%@ include file="/view/staff/navbar.jsp" %>

        <main class="content-container">


            <div class="search-container">
                <form action="StaffAccountList" method="GET" class="search-form">
                    <input type="text" name="searchText" class="search-input" placeholder="Tìm theo tên, email..." value="${searchText}">

                    <select name="roleFilter" class="search-input">
                        <option value="ALL" ${roleFilter == 'ALL' ? 'selected' : ''}>Tất cả chức vụ</option>
                        <option value="Lễ tân" ${roleFilter == 'Lễ tân' ? 'selected' : ''}>Lễ tân</option>
                        <option value="Quản lý" ${roleFilter == 'Quản lý' ? 'selected' : ''}>Quản lý</option>
                        <option value="Quản trị viên" ${roleFilter == 'Quản trị viên' ? 'selected' : ''}>Quản trị viên</option>
                    </select>

                    <button type="submit" class="search-btn">Tìm kiếm</button>
                    <a href="StaffAccountList" class="reset-btn">Làm mới</a>
                </form>
                <div class="header-action">
                    <button id="btn-create" class="btn-primary">Thêm nhân viên mới</button>
                </div>
            </div>

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

                                    <a class="btn-edit" href="StaffAccountEdit?staffId=${s.getStaffId()}&page=${currentPage}&searchText=${searchText}&roleFilter=${roleFilter}">Sửa</a>
                                    <form action="StaffAccountDelete" method="post" style="display: inline-block; margin: 0;">

                                    <a class="btn-edit" href="StaffAccountEdit?staffId=${s.getStaffId()}">Sửa</a>
                                    <form action="StaffAccountDelete" method="POST">

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
                <h2 class="staff-popup-title">Chi tiết nhân viên</h2>
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
                    <a class="btn-submit" href="StaffAccountEdit?staffId=${selectedStaff.getStaffId()}&page=${currentPage}&searchText=${searchText}&roleFilter=${roleFilter}">Sửa</a>
                </div>
            </div>
        </div>

        <div class="staff-modal" id="edit-modal">
            <form action="StaffAccountEdit" method="post" id="edit-form" class="modal-content">
                <h2 class="staff-popup-title" id="edit-modal-title">Chỉnh sửa nhân viên</h2>

                <input type="hidden" name="staffId" value="${editStaff.getStaffId()}">
                <input type="hidden" name="page" value="${currentPage}">
                <input type="hidden" name="searchText" value="${searchText}">
                <input type="hidden" name="roleFilter" value="${roleFilter}">

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
                    <select class="service-popup-input-field" name="role" required>
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

                <div class="service-popup-action">
                    <button type="button" class="btn-close" id="btn-close-edit">Huỷ</button>
                    <button type="submit" class="btn-submit">Lưu thay đổi</button>
                </div>
            </form>
        </div>

        <div class="staff-modal" id="create-modal">
            <form action="StaffAccountCreate" method="POST" id="staff-form" class="modal-content">
                <h2 class="staff-popup-title" id="modal-title">Thêm nhân viên mới</h2>

                <c:if test="${not empty sessionScope.errorMessage and not empty sessionScope.openCreateModal}">
                    <div class="alert-message alert-error" style="position: static; margin-bottom: 15px; width: 100%;">
                        ${sessionScope.errorMessage}
                    </div>
                </c:if>

                <div class="form-group">
                    <label class="input-label">Tên đăng nhập*</label>
                    <input class="service-popup-input-field" type="text" name="username" id="username" 
                           placeholder="Nhập tên đăng nhập..." value="${sessionScope.keepUsername}" required>
                </div>

                <div class="form-group">
                    <label class="input-label">Mật khẩu*</label>
                    <input class="service-popup-input-field" type="password" name="password" id="password" 
                           placeholder="Nhập mật khẩu..." required>
                </div>

                <div class="form-group">
                    <label class="input-label">Họ tên*</label>
                    <input class="service-popup-input-field" type="text" name="fullName" id="fullName" 
                           placeholder="Nhập họ tên..." value="${sessionScope.keepFullName}" required>
                </div>

                <div class="form-group">
                    <label class="input-label">Email*</label>
                    <input class="service-popup-input-field" type="email" name="email" id="email" 
                           placeholder="Nhập email..." value="${sessionScope.keepEmail}" required>
                </div>

                <div class="form-group">
                    <label class="input-label">Số điện thoại</label>
                    <input class="service-popup-input-field" type="text" name="phone" id="phone" 
                           placeholder="Nhập số điện thoại..." value="${sessionScope.keepPhone}">
                </div>

                <div class="form-group">
                    <label class="input-label">Chức vụ*</label>
                    <select class="service-popup-input-field" name="role" id="role" required>
                        <option value="">Chọn chức vụ</option>
                        <option value="Lễ tân" ${sessionScope.keepRole == 'Lễ tân' ? 'selected' : ''}>Lễ tân</option>
                        <option value="Quản lý" ${sessionScope.keepRole == 'Quản lý' ? 'selected' : ''}>Quản lý</option>
                        <option value="Quản trị viên" ${sessionScope.keepRole == 'Quản trị viên' ? 'selected' : ''}>Quản trị viên</option>
                    </select>
                </div>

                <div class="service-popup-action">
                    <button type="button" class="btn-close" id="btn-close-create">Huỷ</button>
                    <button type="submit" class="btn-submit">Xác nhận lưu</button>
                </div>
            </form>
        </div>

        <script src="${pageContext.request.contextPath}/view/assets/javascript/staff-account-management.js"></script>
    </body>
</html>