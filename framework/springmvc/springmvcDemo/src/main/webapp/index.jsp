<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<html>
    <head>
        <title>Upload a file please</title>
    </head>
    <body>
        <h1>Please upload a file</h1>
        <form method="post" action="upload" enctype="multipart/form-data">
            <input type="text" name="username" placeholder="用户名"/>
            <input type="file" name="file"/>
            <input type="submit"/>
        </form>
    </body>
</html>