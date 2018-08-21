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
        <div class="section section section-custom">
        <div class="pmd-card pmd-z-depth ">
            <div class="pmd-card-body">
                <form class="row" role="form" method="post" action="/book/list">
                    <input type="hidden" name="start" value="${bookQueryForm.start}">
                    <div class="col-sm-3">
                        <div class="form-group pmd-textfield pmd-textfield-floating-label">
                            <input class="form-control" id="exampleInputEmail2" type="text" name="bookName" value="${bookQueryForm.bookName}" placeholder="按书名模糊查找"><span class="pmd-textfield-focused"></span>
                        </div>
                    </div>
                    <div class="col-sm-3">
                        <div class="form-group pmd-textfield pmd-textfield-floating-label">
                            <input class="form-control datepicker" type="text" name="uptimeLow" value="${bookQueryForm.uptimeLow}" placeholder="起始上架日期"><span class="pmd-textfield-focused"></span>
                        </div>
                    </div>
                    <div class="col-sm-3">
                        <div class="form-group pmd-textfield pmd-textfield-floating-label">
                            <input class="form-control datepicker" type="text" name="uptimeHigh" value="${bookQueryForm.uptimeHigh}" placeholder="结束上架日期"><span class="pmd-textfield-focused"></span>
                        </div>
                    </div>
                    <div class="col-sm-3">
                        <button type="submit" class="btn btn-primary btn-sm pmd-ripple-effect">查询</button>
                        <a href="book/add" class="btn btn-danger btn-sm pmd-ripple-effect" role="button">新增</a>
                    </div>
                </form>

        <div class="table-responsive pmd-card pmd-z-depth">
            <table class="table table-mc-red pmd-table .pmd-table-card">
                <thead>
                <tr>
                    <th>书籍ID</th>
                    <th>书名</th>
                    <th>上架日期</th>
                    <th>价格</th>
                    <th>库存量</th>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${books}" var="b" varStatus="vs">
                                    <tr>
                    <td data-title="书籍ID">${b.id}</td>
                    <td data-title="书名">${b.bookName}</td>
                    <td data-title="上架日期"><fmt:formatDate value="${b.upTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    <td data-title="价格">${b.price}</td>
                    <td data-title="库存量">${b.stock}</td>
                    <td class="pmd-table-row-action">
                            <button data-target="#pmd-alert-dialog${vs.index}" data-toggle="modal" class="btn pmd-ripple-effect btn-primary btn-sm pmd-z-depth" type="button">删除</button>

                            <div tabindex="-1" class="modal pmd-modal-center " id="pmd-alert-dialog${vs.index}" style="display: none;" aria-hidden="true">
                                <div class="modal-dialog">
                                    <div class="modal-content">
                                        <div class="modal-body"> 确认删除${b.bookName}?</div>
                                        <div class="pmd-modal-action text-right">
                                            <button data-dismiss="modal" class="btn pmd-ripple-effect btn-primary pmd-btn-flat confirmDelete" data="${b.id}" type="button">是的，请删除</button>
                                            <button data-dismiss="modal" class="btn pmd-ripple-effect btn-default pmd-btn-flat" type="button">以后再说</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        <a href="book/edit?bookId=${b.id}" class="btn pmd-btn-flat pmd-ripple-effect btn-default btn-sm">修改</a>

                    </td>
                </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
            </div>
            <c:if test="${pageCount>0 }">

                <nav aria-label="Page navigation">
                    <ul class="pagination pagination-lg">
                        <li><a class="indexer" href="javascript:void(0)"  idx="${bookQueryForm.start==1?1:bookQueryForm.start-1 }" aria-label="Previous"> <span
                                aria-hidden="true">&laquo;</span>
                        </a></li>
                        <c:forEach begin="1" end="${pageCount }" var="idx">
                            <li <c:if test="${idx==bookQueryForm.start }">class="active"</c:if> ><a class="indexer"  href="javascript:void(0)"    idx="${idx }">${idx }</a></li>
                        </c:forEach>
                        <li><a class="indexer"  href="javascript:void(0)" idx="${bookQueryForm.start==pageCount?pageCount:bookQueryForm.start+1 }"  aria-label="Next"> <span
                                aria-hidden="true">&raquo;</span>
                        </a></li>
                    </ul>
                </nav>
            </c:if>
        </div>
        </div>

    </div>
</div>


<button style="display:none" type="button" data-positionx="left" data-positiony="top" data-duration="5000" data-effect="fadeInUp" data-message="删除成功" data-type="information" data-revert="fadeOutUp" class="btn pmd-ripple-effect btn-info pmd-btn-raised pmd-alert-toggle alert-success">Alert Information</button>
<%@include file="../layout/footer.jsp"%>

<%@include file="../layout/script.jsp"%>

<script type="application/javascript">

    $(function(){
        $(".indexer").click(function(e){
            e.preventDefault();
            //修改form中的隐藏字段pageIdx
            $("[name=start]").val($(this).attr("idx"));
            //提交form
            $("form").submit();
        });

        $("#search").click(function(){
            $("[name=start]").val(null);
        });
    });


    $(".confirmDelete").click(function(){
        var bookId = $(this).attr("data");
        var self =$(this)
        $.ajax('book/delete',{
            data:{'bookId':bookId},
            success:function(){
                alert("删除成功！");
                self.parentsUntil('tbody').remove();
            }
        });
    })


</script>

</body>
</html>
