<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>登录</title>
<script src="jquery-1.12.4.min.js"></script>
<script>

	window.onload=function(){
		var img = document.querySelector("[src=captchaCreate]");
		img.onclick=function(){
			img.src="captchaCreate?date="+new Date().getTime();
		}
	}
	
	$(function(){
		$("[name=captcha]").blur(function(){
			var captchaVal = $(this).val();
			$.ajax("checkCaptcha",{
				dataType:"json",
				data:{"captcha":captchaVal},
				type:"post",
				success:function(result){
					$(".hint").remove();
					if(result.success){
						$("<span>").addClass("hint").text("√").insertAfter("#captchaImg");
					}else{
						$("<span>").addClass("hint").text("x").insertAfter("#captchaImg");
					}
				},
				error:function(xhr,e){
					console.error(e);
				}
				
			});
		});
	});

</script>
</head>
<body>
<form action="login" method="post">
	<div>
		<input type="text" name=username/>
	</div>
	<div>
		<input type="password" name=password/>
	</div>
	
	<div>
		<input type="text" name="captcha"/>
		<img src="captchaCreate" id="captchaImg"/>
	</div>
	<div>
		<input type="submit" />
	</div>
</form>
</body>
</html>