<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
<% String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
 %>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title></title>
</head>
<body>
<form action="upload" method="post" enctype="multipart/form-data">
	<input name="myFile" type="file" />
	<input name="username" type="text"  placeholder="请输入用户名" />
	<button>上传</button>
</form>
</body>
</html>