<!DOCTYPE html>
<html>
	<head>
		<title>艾漫数据</title>
		<script src="${ grailsApplication.config.grails.app.name}/js/jquery-1.11.1.min.js" type="text/javascript"></script>
		<link href="${ grailsApplication.config.grails.app.name}/css/login.css" rel="stylesheet"  type="text/css"></link>
		<link href="${ grailsApplication.config.grails.app.name}/css/bootstrap/bootstrap.min.css" rel="stylesheet"  type="text/css"></link>
		<link href="${ grailsApplication.config.grails.app.name}/css/bootstrap/bootstrap-theme.min.css" rel="stylesheet"  type="text/css"></link>
		<link href="${ grailsApplication.config.grails.app.name}/css/bootstrap/style.css" rel="stylesheet"  type="text/css"></link>
		
		<script>
			function onSubmit(){
				$("#login_message").empty()
				if(!$("#username").val()){
						$("#usernameTip").empty().text("用户名不能为空")
						return false;
				}else{
					$("#usernameTip").empty()
				}
				if(!$("#password").val()){
					$("#passwordTip").empty().text("密码不能为空")
					return false;
				}else{
					$("#passwordTip").empty()
				}
				return true
			}
		</script>
	</head>
	<body>
	<div class="loginTop">
		<div class="top_header">
			<div class="iminer_introduce">
				<a href="http://www.iminer.com" target="_blank">艾漫官网</a>
			</div>
			<div class="iplay_tittle"></div>
			<div class="iplay_text">iPlay 剧本评估系统</div>
		</div>
	</div>
	<div class="iplay_line"></div>
	<div class="loginMiddle">
	<div class="top_header">
		<div class="log_mid_left"></div>
		<div class="log_mid_right">
			<div class="col-md-10">
			<form class=" form-horizontal" name="form1" action='${ grailsApplication.config.grails.app.name}/login/checkLogin' method='post' id='loginForm' onSubmit="return onSubmit()">
				<div class="form-group">
					 
					<label for="inputEmail3" class="col-sm-3 control-label">
						NAME
					</label>
					<div class="col-sm-7">
						<input class="form-control" id="username" name="username" type="text">
						<span id="usernameTip"></span>
					</div>
				</div>
				<div class="form-group">
					 
					<label for="inputPassword3" class="col-sm-3 control-label">
						PASSWORD
					</label>
					<div class="col-sm-7">
						<input class="form-control" id="password" name="password"  type="password">
						<span id="passwordTip"></span>
					</div>
                    <div class="form-group">
					<div class="col-sm-offset-2 col-sm-10">
						<div class="checkbox">
							<label>
								<input type="checkbox" name="remuser"> Remember me
							</label>
						</div>
						<div style="margin-left:120px;">
                    	<span id="login_message" >${flash?.message}</span>
                  	  	</div>
					</div>
				</div>
				</div>
				<div class="form-group">
					<div class="col-sm-offset-2 col-sm-10">
						<button type="submit" class="btn btn-default">
							Sign in
						</button>
					</div>
				</div>
			</form>
			</div>
		</div>
	</div>
	</div>
	<div class="loginBottom">
		<g:render template="/layouts/login_footer"></g:render>
	</div>
	</body>
</html>
