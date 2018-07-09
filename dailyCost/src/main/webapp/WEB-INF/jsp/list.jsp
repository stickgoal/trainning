<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->
    <title>账单</title>

    <!-- Bootstrap -->
    <link href="./css/bootstrap.min.css" rel="stylesheet">
    <link href="css/bootstrap-datetimepicker.min.css" rel="stylesheet"/>

    <!-- HTML5 shim 和 Respond.js 是为了让 IE8 支持 HTML5 元素和媒体查询（media queries）功能 -->
    <!-- 警告：通过 file:// 协议（就是直接将 html 页面拖拽到浏览器中）访问页面时 Respond.js 不起作用 -->
    <!--[if lt IE 9]>
    <script src="https://cdn.bootcss.com/html5shiv/3.7.3/html5shiv.min.js"></script>
    <script src="https://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
    <style>
        body{ background: #eee; }
       .navbar-brand img{
           width: 30px;
           height: 30px;
       }
    </style>
</head>
<body>
<div class="container-fluid">

    <nav class="navbar navbar-default">
        <div class="container-fluid">
            <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="#">
                    <img src="img/moneybook.svg" alt="">
                </a>
                <p class="navbar-text">每日记账</p>

            </div>

            <!-- Collect the nav links, forms, and other content for toggling -->
            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                <ul class="nav navbar-nav ">
                    <li><a href="keep.html">记账</a></li>
                   <li  class="active"><a href="query.html">账单</a></li>
                   <li><a href="stat.html">统计</a></li>
                </ul>
                <ul class="nav navbar-nav navbar-right">
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">{用户名} <span class="caret"></span></a>
                        <ul class="dropdown-menu">
                            <li><a href="passoword/modify">修改密码</a></li>
                        </ul>
                    </li>
                    <li><button href="logout" class="btn btn-default navbar-btn">退出</button></li>
                </ul>
            </div><!-- /.navbar-collapse -->
        </div><!-- /.container-fluid -->
    </nav>

    <div class="container">
        <form class="form-inline" action="userlist" method="post">
        	<input type="hidden"  name="pageIdx" value="${ pageIdx}"/>	
            <div class="col-md-6">
	            <div class="form-group">
	                <label for="beginTime">时间段</label>
	                <input type="datetime" class="form-control" name="beginTime" id="beginTime" value="${beginTime}" placeholder="选择开始时间">
	            </div>
	            <div class="form-group">
	                <label for="endTime">-</label>
	                <input type="datetime" class="form-control" name="endTime" id="endTime" value="${endTime}"  placeholder="选择结束时间">
	            </div>
            </div>	
            <div class="form-group">
                <label>
                    <input type="radio" name="type" id="income" value="收入" checked>收入
                </label>
                <label>
                    <input type="radio" name="type" id="outcome" value="支出">支出
                </label>
            </div>
            <button type="submit" class="btn btn-default">查询</button>
        </form>

        <table class="table table-hover">
            <thead>
                <tr>
                    <th>编号</th>
                    <th>收支</th>
                    <th>分类</th>
                    <th>金额</th>
                    <th>备注</th>
                    <th>发生时间</th>
                </tr>
            </thead>
            <tbody>
				<c:forEach items="${list }" var="r">
					<tr>
						<td>${r.recordId }</td>
						<td>${r.direction }</td>
						<td>${r.category }</td>
						<td>${r.amount }</td>
						<td>${r.memo }</td>
						<td>${r.occurTime}</td>
					</tr>
				
				</c:forEach>               

            </tbody>
        </table>
				<c:if test="${pageCount>0 }">
        <nav aria-label="Page navigation">
            <ul class="pagination pagination-lg">
                <li>
                    <a  class="pageIndexer"  idx="${idx>1?idx-1:1 }"  href="javascript:void(0);" aria-label="Previous">
                        <span aria-hidden="true">&laquo;</span>
                    </a>
                </li>
				<c:forEach begin="1" end="${pageCount }"  var="idx" >
						<li <c:if test="${pageIdx==idx }"> class="active"</c:if>><a class="pageIndexer"  idx="${idx }"      href="javascript:void(0);">${idx }</a></li>
				</c:forEach>
				

                <li>
                    <a  class="pageIndexer"  idx="${idx< pageCount?idx+1:pageCount }"  aria-label="Next">
                        <span aria-hidden="true">&raquo;</span>
                    </a>
                </li>
            </ul>
        </nav>
				</c:if>               

    </div>





</div>
<!-- jQuery (Bootstrap 的所有 JavaScript 插件都依赖 jQuery，所以必须放在前边) -->
<script src="./js/jquery-1.12.2.min.js"></script>
<!-- 加载 Bootstrap 的所有 JavaScript 插件。你也可以根据需要只加载单个插件。 -->
<script src="./js/bootstrap.min.js"></script>
<script src="./js/bootstrap-datetimepicker.min.js"></script>
<script src="./js/bootstrap-datetimepicker.zh-CN.js"></script>

<script>
$("[type=datetime]").datetimepicker({
    language:"zh-CN",
    minView: 2,
    format:"yyyy-mm-dd",
    autoclose:true
});

$(function(){
	$(".pageIndexer").click(function(e){
		e.preventDefault();
		$("[name=pageIdx]").val($(this).attr("idx"));
		$("form").submit();
	})
})
</script>
</body>
</html>