<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!doctype html>
<html lang="zh">
<jsp:include page="../layout/head.jsp">
    <jsp:param name="title" value="欢迎来到三只松饼"/>
</jsp:include>
<body>
<%@include file="../layout/nav.jsp"%>
<jsp:include page="../layout/sidebar.jsp">
    <jsp:param name="selected" value="book/list"/>
</jsp:include>

<!--content area start-->
<div id="content" class="pmd-content content-area dashboard">

    <div class="container-fluid">
        <div class="section section-custom billinfo">
            <!--section-title -->
            <h2>修改书籍</h2><!--section-title end -->
            <!-- section content start-->
            <form id="validationForm" action="book/edit" method="post">
                <input type="hidden" name="bookId" value="${book.id}">
                <div class="pmd-card pmd-z-depth">
                    <div class="pmd-card-body">
                        <div class="group-fields clearfix row">
                            <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                                <div class="form-group pmd-textfield pmd-textfield-floating-label">
                                    <label for="bookName" class="control-label">
                                        书籍名字*
                                    </label>
                                    <input type="text" id="bookName" value="${book.bookName}" name="bookName" class="form-control"><span class="pmd-textfield-focused"></span>
                                </div>
                            </div>
                        </div>
                        <div class="group-fields clearfix row">
                            <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12">
                                <div class="form-group pmd-textfield pmd-textfield-floating-label">
                                    <label for="stock" class="control-label">
                                        数量*
                                    </label>
                                    <input type="number" id="stock" name="stock" value="${book.stock}" class="form-control"><span class="pmd-textfield-focused"></span>
                                </div>
                            </div>
                            <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12">
                                <div class="form-group pmd-textfield pmd-textfield-floating-label">
                                    <label for="price" class="control-label">
                                        价格*
                                    </label>
                                    <input type="number" id="price" name="price" value="${book.price}" class="form-control"><span class="pmd-textfield-focused"></span>
                                </div>
                            </div>
                        </div>
                        <div class="group-fields clearfix row">
                            <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12">
                                <div class="form-group pmd-textfield pmd-textfield-floating-label">
                                    <label for="stock" class="control-label">
                                        是否上架*
                                    </label>
                                    <label class="pmd-checkbox pmd-checkbox-ripple-effect">
                                        <input type="checkbox" value="true" class="pm-ini" <c:if test="${book.upTime!=null}">checked</c:if> name="up"><span class="pmd-checkbox-label">&nbsp;</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="pmd-card-actions">
                        <a href="javascript:document.forms[0].submit();" class="btn btn-primary next">修改</a>
                        <a href="book/list" class="btn btn-default">返回</a>
                    </div>
                </div> <!-- section content end -->
            </form>
        </div>
    </div>
</div>


<%@include file="../layout/footer.jsp"%>

<%@include file="../layout/script.jsp"%>

</body>
</html>
