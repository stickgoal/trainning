<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>注册</title>
<style type="text/css">
.error {
	color: red;
}
</style>
</head>
<body>
	<form:form action="/springmvcDemo/form/regValid" commandName="registerForm" method="POST">
		<form:errors path="username" cssClass="error" />
		<br />
		用户名：<from:input path="username" />
		<br />

		<form:errors path="age" cssClass="error" />
		<br />
		<from:input path="age" placeholder="年龄" />
		<br />
		<form:errors path="birthday" cssClass="error" />
		<br />
		<from:input path="birthday" type="text" />
		<br />

		<form:errors path="gender" cssClass="error" />
		<br />
		性别 <form:radiobutton  path="gender" value="M" />男
		<form:radiobutton  path="gender" value="F"/>女<br />
		<button type="submit">注册</button>
	</form:form>
</body>
</html>