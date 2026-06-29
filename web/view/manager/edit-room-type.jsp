<%-- 
    Document   : edit-room-type
    Created on : Jun 13, 2026, 2:43:18 PM
    Author     : Minh Thu
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Chỉnh Sửa Hạng Phòng - La Mer Hotel</title>

        <jsp:include page="/view/staff/header.jsp" />

        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/view/assets/css/edit-room-type.css?v=<%= System.currentTimeMillis() %>">
    </head>
    <body>
        <jsp:include page="/view/staff/navbar.jsp" />

        <div class="container-fluid">
            <div class="row">
                <main class="col-md-12 ms-sm-auto px-md-4">
                    <div class="form-box mt-4">

                        <div class="form-header mb-4 border-bottom-luxury pb-3">
                            <h2><i class="fa-solid fa-pen-to-square me-2"></i>Chỉnh Sửa Hạng Phòng</h2>
                            <p>Cập nhật lại thông số cấu hình, album không gian ảnh và thiết lập lại các đặc quyền dịch vụ.</p>
                        </div>



                        <form action="${pageContext.request.contextPath}/roomtypeedit" method="POST">
                            <input type="hidden" name="roomTypeId" value="${roomType.roomTypeId}" />

                            <input type="hidden" id="serverStatus" value="${status}" />
                            <input type="hidden" id="serverInvalidName" value="${invalidName}" />

                            <div class="block-title">1. Thông tin cơ bản</div>
                            <div class="row g-3 mb-4">
                                <div class="col-md-6">
                                    <label class="field-label">Tên hạng phòng <span class="required-star" >*</span></label>
                                    <input type="text" name="typeName" class="input-field" value="${roomType.typeName}" required />
                                </div>
                                <div class="col-md-6">
                                    <label class="field-label">Giá cơ bản (VNĐ / Đêm) <span class="required-star">*</span></label>
                                    <input type="number" name="basePrice" class="input-field" min="0" value="${roomType.basePrice}" required />
                                </div>
                                <div class="col-12">
                                    <label class="field-label">Mô tả chi tiết hạng phòng</label>
                                    <textarea name="description" class="textarea-field" rows="3" >${roomType.description}</textarea>
                                </div>
                            </div>

                            <div class="block-title">2. Thông số cấu hình & Tiện nghi</div>
                            <div class="row g-3 mb-4">
                                <div class="col-md-3">
                                    <label class="field-label">Số người lớn <span class="required-star">*</span></label>
                                    <input type="number" name="num_guests" id="num_guests" class="input-field" min="1" 
                                           value="${not empty roomType ? roomType.numGuests : param.numGuests}" required />
                                </div>
                                <div class="col-md-3">
                                    <label class="field-label">Số trẻ em <span class="required-star">*</span></label>
                                    <input type="number" name="num_children" id="num_children" class="input-field" min="0" 
                                           value="${not empty roomType ? roomType.numChildren : param.numChildren}" required />
                                </div>
                                <div class="col-md-3">
                                    <label class="field-label">Diện tích phòng (m²) <span class="required-star">*</span></label>
                                    <input type="number" step="any" name="areaSqm" class="input-field" min="1" value="${roomType.areaSqm}" required />
                                </div>
                                <div class="col-md-3">
                                    <label class="field-label">Kiểu giường <span class="required-star">*</span></label>
                                    <input type="text" name="bedType" class="input-field" value="${roomType.bedType}" required />
                                </div>
                                <div class="col-md-3">
                                    <label class="field-label">Số lượng giường <span class="required-star">*</span></label>
                                    <input type="number" name="bedCount" class="input-field" min="1" value="${roomType.bedCount}" required />
                                </div>

                                <div class="block-title">3. Bộ sưu tập hình ảnh không gian phòng</div>
                                <div class="mb-4">
                                    <label class="field-label">Đường dẫn ảnh đại diện chính và các góc chụp phụ (URL)</label>

                                    <div id="imageFieldsContainer">
                                        <div class="img-group mb-2">
                                            <span class="badge-main">Ảnh chính</span> <span class="required-star">*</span>
                                            <input type="text" name="imageUrls" class="input-field input-grow" placeholder="Nhập đường dẫn ảnh chính..." 
                                                   value="${not empty roomType.imageUrl ? roomType.imageUrl[0] : ''}" required /> 
                                            
                                        </div>

                                        <c:forEach items="${roomType.imageUrl}" var="imgUrl" varStatus="st">
                                            <c:if test="${!st.first}">
                                                <div class="img-group mb-2">
                                                    <input type="text" name="imageUrls" class="input-field input-grow" placeholder="Nhập đường dẫn ảnh phụ..." value="${imgUrl}"/>
                                                    <button type="button" class="btn-delete" onclick="this.parentElement.remove();">Xóa</button>
                                                </div>
                                            </c:if>
                                        </c:forEach>
                                    </div>

                                    <button type="button" id="btnAddNewImageField" class="btn-add">
                                        <i class="fa-solid fa-plus me-1"></i> Thêm ảnh phụ
                                    </button>
                                </div>

                                <div class="block-title">4. Thiết lập dịch vụ mặc định kèm theo phòng</div>
                                <div class="table-responsive mb-4">
                                    <table class="service-table">
                                        <thead>
                                            <tr>
                                                <th style="width: 40%;">Tên Dịch Vụ Sẵn Có</th>
                                                <th style="width: 30%; text-align: center;">Số Lượng Trang Bị Sẵn Tại Phòng</th>
                                                <th style="width: 30%; text-align: center;">Số Lượng Miễn Phí</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach items="${availableServices}" var="s">
                                                <c:set var="isServed" value="false" />
                                                <c:set var="savedQty" value="1" />
                                                <c:set var="savedFree" value="0" />

                                                <c:forEach items="${roomType.roomTypeServices}" var="rts">
                                                    <c:if test="${rts.serviceId == s.serviceId}">
                                                        <c:set var="isServed" value="true" />
                                                        <c:set var="savedQty" value="${rts.quantity}" />
                                                        <c:set var="savedFree" value="${rts.isFree}" />
                                                    </c:if>
                                                </c:forEach>

                                                <tr>
                                                    <td>
                                                        <label class="check-box-wrapper">
                                                            <input type="checkbox" name="selectedServices" value="${s.serviceId}" 
                                                                   ${isServed ? 'checked' : ''}
                                                                   onchange="document.getElementById('qty_srv_${s.serviceId}').disabled = !this.checked; document.getElementById('free_srv_${s.serviceId}').disabled = !this.checked;" />
                                                            <span>${s.serviceName}</span>
                                                        </label>
                                                    </td>
                                                    <td style="text-align: center;">
                                                        <input type="number" id="qty_srv_${s.serviceId}" name="quantity_${s.serviceId}" value="${savedQty}" min="1" class="qty-input" ${isServed ? '' : 'disabled'} />
                                                    </td>
                                                    <td style="text-align: center;">
                                                        <input type="number" id="free_srv_${s.serviceId}" name="isFree_${s.serviceId}" value="${savedFree}" min="0" class="qty-input" ${isServed ? '' : 'disabled'} />
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>

                                <div class="block-title">5. Thiết lập tiện nghi </div>
                                <div class="table-responsive mb-4">
                                    <table class="service-table">
                                        <thead>
                                            <tr>
                                                <th style="width: 60%;">Tiện Nghi Sẵn Có</th>
                                                <th style="width: 40%; text-align: center;">Số Lượng Trang Bị Sẵn Tại Phòng</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach items="${availableAmenities}" var="a">
                                                <c:set var="hasAmenity" value="false" />
                                                <c:set var="savedAmenityQty" value="1" />

                                                <c:forEach items="${roomType.roomAmenities}" var="ra">
                                                    <c:if test="${ra.amenityId == a.amenityId}">
                                                        <c:set var="hasAmenity" value="true" />
                                                        <c:set var="savedAmenityQty" value="${ra.description}" /> </c:if>
                                                </c:forEach>
                                                <tr>
                                                    <td>
                                                        <label class="check-box-wrapper">
                                                            <input type="checkbox" name="selectedAmenities" value="${a.amenityId}" 
                                                                   ${hasAmenity ? 'checked' : ''}
                                                                   onchange="document.getElementById('qty_amn_${a.amenityId}').disabled = !this.checked;" />
                                                            <span>${a.amenityName}</span>
                                                        </label>
                                                    </td>
                                                    <td style="text-align: center;">
                                                        <input type="number" id="qty_amn_${a.amenityId}" name="quantity_amenity_${a.amenityId}" value="${savedAmenityQty}" min="1" class="qty-input" ${hasAmenity ? '' : 'disabled'} />
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>

                                <div class="status-block">
                                    <label class="check-box-wrapper fw-bold">
                                        <input type="checkbox" name="isActive" value="true" ${roomType.isActive() ? 'checked' : ''} />
                                        <span>Kích hoạt kinh doanh hạng phòng này</span>
                                    </label>
                                </div>

                                <div class="action-box">
                                    <a href="${pageContext.request.contextPath}/roomtypelist" class="btn-link-back">
                                        <i class="fa-solid fa-arrow-left me-1"></i> Quay lại danh sách
                                    </a>
                                    <button type="submit" class="btn-submit">
                                        <i class="fa-solid fa-floppy-disk me-1"></i> Lưu Thay Đổi
                                    </button>
                                </div>

                        </form>
                    </div>
                </main>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script> 
        <script src="${pageContext.request.contextPath}/view/assets/javascript/add-room-type.js?v=<%= System.currentTimeMillis() %>"></script>
        <script src="${pageContext.request.contextPath}/view/assets/javascript/room-type-notification.js?v=<%= System.currentTimeMillis() %>"></script>

        <c:if test="${not empty errorMessage}">
            <script>
                                                                       document.addEventListener("DOMContentLoaded", function () {
                                                                           Swal.fire({
                                                                               title: 'Dữ liệu không hợp lệ!',
                                                                               text: '${errorMessage}',
                                                                               icon: 'error',
                                                                               confirmButtonColor: '#0f4c5c',
                                                                               confirmButtonText: 'Tôi đã hiểu'
                                                                           });
                                                                       });
            </script>
        </c:if>
    </body>
</html>
