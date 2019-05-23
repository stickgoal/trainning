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
<h2>录入电影信息</h2>
<form action="movieAdd" method="POST">
    <input type="text" name="movieName"  placeholder="电影名字"/> <br>
    <input type="text" name="director"  placeholder="导演"/> <br>
    <input type="text" name="protagonist"  placeholder="主演"/><br>
    <input type="date" name="releaseDate"  placeholder="上映时间"/><br>
    <select name="language">
        <option selected value="zh">国语</option>
        <option value="en">英语</option>
        <option value="fr">法语</option>
    </select><br>
    <input type="submit" value="添加"/>
</form>

</body>
</html>
