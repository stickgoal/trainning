<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Lucas
  Date: 2017-03-03
  Time: 15:09
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->
    <title>记账</title>

    <!-- Bootstrap -->
    <link href="./css/bootstrap.min.css" rel="stylesheet">

    <!-- HTML5 shim 和 Respond.js 是为了让 IE8 支持 HTML5 元素和媒体查询（media queries）功能 -->
    <!-- 警告：通过 file:// 协议（就是直接将 html 页面拖拽到浏览器中）访问页面时 Respond.js 不起作用 -->
    <!--[if lt IE 9]>
    <script src="https://cdn.bootcss.com/html5shiv/3.7.3/html5shiv.min.js"></script>
    <script src="https://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
    <style>
        body{
            background: #eee;
        }
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
                    <li class="active"><a href="keep.html">记账</a></li>
                    <li><a href="query.html">账单</a></li>
                    <li><a href="stat.html">统计</a></li>
                </ul>
                <ul class="nav navbar-nav navbar-right">
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">${SK_USER.username} <span class="caret"></span></a>
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
        <form action="keep.html" class="form-horizontal">
            <input type="hidden" name="category" />
            <div class="form-group">
                <div class="col-md-6 col-md-offset-2">
                    <div class="btn-group btn-group-justified type" role="group" aria-label="...">
                        <a href="#" class="btn btn-default active" id="income">收入</a>
                        <a href="#" class="btn btn-default" id="outcome">支出</a>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <label class="col-sm-2 control-label">分类</label>
                <div class="col-sm-6" id="income_category">
                    <button class="btn btn-info btn-sm" category="transport">交通</button>
                    <button class="btn btn-danger btn-sm" category="food">食品</button>
                    <button class="btn btn-danger btn-sm" category="food">食品</button>
                    <button class="btn btn-danger btn-sm" category="food">食品</button>
                    <button class="btn btn-danger btn-sm" category="food">食品</button>
                    <button class="btn btn-danger btn-sm" category="food">食品</button>
                    <button class="btn btn-danger btn-sm" category="food">食品</button>
                    <button class="btn btn-danger btn-sm" category="food">食品</button> <button class="btn btn-info btn-sm" category="transport">交通</button>
                    <button class="btn btn-danger btn-sm" category="food">食品</button>
                    <button class="btn btn-danger btn-sm" category="food">食品</button>
                    <button class="btn btn-danger btn-sm" category="food">食品</button>
                    <button class="btn btn-danger btn-sm" category="food">食品</button>
                    <button class="btn btn-danger btn-sm" category="food">食品</button>
                    <button class="btn btn-danger btn-sm" category="food">食品</button>
                    <button class="btn btn-danger btn-sm" category="food">食品</button> <button class="btn btn-info btn-sm" category="transport">交通</button>
                    <button class="btn btn-danger btn-sm" category="food">食品</button>
                    <button class="btn btn-danger btn-sm" category="food">食品</button>
                    <button class="btn btn-danger btn-sm" category="food">食品</button>
                    <button class="btn btn-danger btn-sm" category="food">食品</button>
                    <button class="btn btn-danger btn-sm" category="food">食品</button>
                    <button class="btn btn-danger btn-sm" category="food">食品</button>
                    <button class="btn btn-danger btn-sm" category="food">食品</button>
                </div>
                <div class="col-sm-6" id="outcome_category">
                    <button class="btn btn-danger btn-sm" category="food">食品</button>
                    <button class="btn btn-danger btn-sm" category="food">食品</button>
                    <button class="btn btn-danger btn-sm" category="food">食品</button>
                    <button class="btn btn-danger btn-sm" category="food">食品</button>
                    <button class="btn btn-danger btn-sm" category="food">食品</button>
                    <button class="btn btn-danger btn-sm" category="food">食品</button>
                </div>
            </div>
            <div class="form-group">
                <label for="amount" class="col-sm-2 control-label">金额</label>
                <div class="col-sm-6">
                    <input type="number" class="form-control" name="amount" id="amount" value="0.00" step="1.00" placeholder="请输入金额">
                </div>
            </div>
            <div class="form-group">
                <label class="col-sm-2 control-label">备注</label>
                <div class="col-sm-6">
                    <textarea class="form-control" name="memo" id="memo" cols="60" rows="3"></textarea>
                </div>
            </div>
            <div class="form-group">
                <div class="col-md-6 col-md-offset-4">
                    <button type="submit" class="col-md-4 btn btn-success">记一笔</button>
                </div>
            </div>


        </form>

    </div>





</div>

<!-- jQuery (Bootstrap 的所有 JavaScript 插件都依赖 jQuery，所以必须放在前边) -->
<script src="./js/jquery-1.12.2.min.js"></script>
<!-- 加载 Bootstrap 的所有 JavaScript 插件。你也可以根据需要只加载单个插件。 -->
<script src="./js/bootstrap.min.js"></script>

<script>
    $(function(){
        $("#outcome_category").hide();
        $("[category]").click(function(e){
            e.preventDefault();
            $("[name=category]").val($(this).attr("category"));
        });
        $("#income").click(function(){
            $(".type .btn").removeClass("active");
            $(this).addClass("active");
            $("#income_category").show();
            $("#outcome_category").hide();
        });
        $("#outcome").click(function () {
            $(".type .btn").removeClass("active");
            $(this).addClass("active");
            $("#income_category").hide();
            $("#outcome_category").show();
        })
    });
</script>
</body>
</html>
