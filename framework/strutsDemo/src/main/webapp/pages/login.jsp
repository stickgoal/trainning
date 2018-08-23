<%--
  Created by IntelliJ IDEA.
  User: Lucas
  Date: 2017-01-19
  Time: 17:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags"%>

<html>
<head>
    <title>欢迎登录~</title>
</head>
<body>
<h1>Struts 2 Hello World Example</h1>

<s:form action="welcome">
    <s:textfield name="username" label="Username" />
    <s:password name="password" label="Password" />
    <s:submit />
</s:form>
</body>
</html>
