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
</head>
<body>
<h2>修改电影信息</h2>
<form action="/modifyMovie" method="POST">
    <input type="hidden" name="movieId" value="${movie.movieId}">
    <input type="text" name="movieName" value="${movie.movieName}"  placeholder="电影名字"/> <br>
    <input type="text" name="director" value="${movie.direct}"  placeholder="导演"/> <br>
    <input type="text" name="protagonist"  value="${movie.protagonist}"  placeholder="主演"/><br>
    <input type="date" name="releaseDate"   value="<fmt:formatDate value='${movie.releaseDate}' pattern='yyyy-MM-dd'/>"  placeholder="上映时间"/><br>
    <select name="language">
        <option <c:if test="${movie.language=='zh'}">selected</c:if> value="zh">国语</option>
        <option <c:if test="${movie.language=='en'}">selected</c:if> value="en">英语</option>
        <option <c:if test="${movie.language=='fr'}">selected</c:if> value="fr">法语</option>
    </select><br>
    <input type="submit" value="修改"/>
    <a href="/movieList">
        返回
    </a>
</form>

</body>
</html>
