<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>注册结果页</title>
</head>
<body>
<c:if test="${not empty message }">${message}</c:if>
尊敬的${registerForm.username } 您已注册成功。
</body>
</html>