<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2019-5-22
  Time: 上午 10:59
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>电影列表</title>
    <style>
        .message{
            background-color: lightyellow;
            color:navy;
        }
    </style>
</head>
<body>
<h2>电影列表</h2>

<form action="movieList" method="get">
    <c:if test="${message!=null}">
        <div class="message">${message}</div>
    </c:if>
    <input type="text" name="director" value="${movieQueryForm.director}" placeholder="导演"/>
    <input type="text" name="protagonist" value="${movieQueryForm.protagonist}" placeholder="主演"/>
    <input type="date" name="releaseDateBegin" value="<fmt:formatDate value="${movieQueryForm.releaseDateBegin}" pattern="yyyy-MM-dd"/>" placeholder="开始时间"/>
    <input type="date" name="releaseDateEnd" value="<fmt:formatDate value="${movieQueryForm.releaseDateEnd}" pattern="yyyy-MM-dd"/>" placeholder="结束时间"/>
    <input type="submit" value="搜索"/>
    <a href="movieAdd">新增</a>
</form>
<table>
    <tr>
        <th>电影名字</th>
        <th>导演</th>
        <th>主演</th>
        <th>上映日期</th>
        <th>语言</th>
        <th>操作</th>
    </tr>
    <c:forEach items="${movieList}" var="m">
        <tr>
            <td><a href="detail?movieId=${m.movieId}">${m.movieName}</a></td>
            <td>${m.direct}</td>
            <td>${m.protagonist}</td>
            <td>
                <fmt:formatDate value="${m.releaseDate}" pattern="yyyy-MM-dd"/>
            </td>
            <td>${m.language}</td>
            <td>
                <a href="delMovie?movieId=${m.movieId}">删除</a>
                <a href="modifyMovie?movieId=${m.movieId}">修改</a>
            </td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
