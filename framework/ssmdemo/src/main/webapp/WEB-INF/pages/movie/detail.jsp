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
<h2>电影详情</h2>
    <div>
        <ul>
            <li>电影名：${movie.movieName}</li>
            <li>导演：${movie.direct}</li>
            <li>主演：${movie.protagonist}</li>
            <li>上映日期：${movie.releaseDate}</li>
            <li>语言：${movie.language}</li>
        </ul>
    </div>
    <a href="/movieList">
        返回
    </a>

</body>
</html>
