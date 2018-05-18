<!DOCTYPE html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
	<head>
		<title>iplay</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	    <meta name="viewport" content="width=device-width, initial-scale=1">
	    <%--<link href="${grailsApplication.config.grails.app.name}/css/bootstrap/bootstrap-cerulean.min.css" rel="stylesheet"><!-- id="bs-css" 用于切换主题样式 --> 
	    <link href="${grailsApplication.config.grails.app.name}/css/bootstrap/charisma-app.css" rel="stylesheet">
	    <link rel="stylesheet" type="text/css" href="${grailsApplication.config.grails.app.name}/css/bootstrap/fullcalendar.css"/>
	    <link rel="stylesheet" type="text/css" href="${grailsApplication.config.grails.app.name}/css/bootstrap/fullcalendar.print.css" media="print"/>
	    <link rel="stylesheet" type="text/css" href="${grailsApplication.config.grails.app.name}/css/bootstrap/chosen.min.css"/>
	    <link rel="stylesheet" type="text/css" href="${grailsApplication.config.grails.app.name}/css/bootstrap/colorbox.css"/>
	    <link rel="stylesheet" type="text/css" href="${grailsApplication.config.grails.app.name}/css/bootstrap/responsive-tables.css"/>
	    <link rel="stylesheet" type="text/css" href="${grailsApplication.config.grails.app.name}/css/bootstrap/bootstrap-tour.min.css"/>
	    <link href="${grailsApplication.config.grails.app.name}/css/bootstrap/jquery.noty.css" rel="stylesheet">
	    <link href="${grailsApplication.config.grails.app.name}/css/bootstrap/noty_theme_default.css" rel="stylesheet">
	    <link href="${grailsApplication.config.grails.app.name}/css/bootstrap/elfinder.min.css" rel="stylesheet">
	    <link href="${grailsApplication.config.grails.app.name}/css/bootstrap/elfinder.theme.css" rel="stylesheet">
	    <link href="${grailsApplication.config.grails.app.name}/css/bootstrap/jquery.iphone.toggle.css" rel="stylesheet">
	    <link href="${grailsApplication.config.grails.app.name}/css/bootstrap/uploadify.css" rel="stylesheet">
	    <link href="${grailsApplication.config.grails.app.name}/css/bootstrap/animate.min.css" rel="stylesheet">
	    <link rel="stylesheet" type="text/css" href="${grailsApplication.config.grails.app.name}/css/bootstrap/self.css"/>--%>
	    <link rel="stylesheet" type="text/css" href="${grailsApplication.config.grails.app.name}/css/bootstrap/bootstrap-datetimepicker.min.css"/>
	    <!-- 加载本地字体文件 -->
	    <link href="${grailsApplication.config.grails.app.name}/css/bootstrap/charisma-app.css" rel="stylesheet">
	    <link href="${grailsApplication.config.grails.app.name}/css/bootstrap/fonts-source.css?201501131529" rel="stylesheet">
	    <link href="${grailsApplication.config.grails.app.name}/css/bootstrap/bootstrap.min.css" rel="stylesheet">
	    <link href="${grailsApplication.config.grails.app.name}/css/bootstrap/bootstrap-theme.min.css" rel="stylesheet">
	    <link href="${grailsApplication.config.grails.app.name}/css/bootstrap/style.css" rel="stylesheet">
	    <link rel="shortcut icon" href="${grailsApplication.config.grails.app.name}/images/alogo.png">
		<link href="${ grailsApplication.config.grails.app.name}/css/hisThinkTank.css" rel="stylesheet"  type="text/css"></link>
		<link href="${grailsApplication.config.grails.app.name }/css/common.css" rel="stylesheet" type="text/css"></link>
		
		<script src="${grailsApplication.config.grails.app.name}/js/jquery-1.11.1.min.js" type="text/javascript" charset="utf-8"></script>
        <%-- 
        <script src="${grailsApplication.config.grails.app.name}/js/bootstrap/jquery.min.js"></script>
        --%>
        <script src="${grailsApplication.config.grails.app.name}/js/bootstrap/bootstrap.min.js" ></script>
        <script src="${grailsApplication.config.grails.app.name}/js/scripts.js"></script>
        <script src="${grailsApplication.config.grails.app.name}/js/iminer_common.js"></script>
        <script src="${grailsApplication.config.grails.app.name}/js/calendar.js"></script>
        
        <%-- 加载可输入型下拉框 --%>
        <script src="${grailsApplication.config.grails.app.name}/js/selectbox/jquery.bgiframe.js" type="text/javascript" charset="utf-8"></script>
        <script src="${grailsApplication.config.grails.app.name}/js/selectbox/jquery.selectbox.js" type="text/javascript" charset="utf-8"></script>
        
		<g:layoutHead/>
		<r:layoutResources />
	</head>
	<body>
		<div class="marketdataTop">
	 		<div class="top_header">
	 			<g:render template="/layouts/iplay_top"></g:render>
	 		</div>
	 	</div>
	 	<div class="marketdataMiddle">
			<div class="top_header">
				<g:layoutBody/>
			</div>
		</div>
		<%-- 
		<script src="${grailsApplication.config.grails.app.name}/js/bootstrap/bootstrap.min.js" ></script>
        <script src="${grailsApplication.config.grails.app.name}/js/bootstrap/jquery.cookie.js"></script>
        <script src="${grailsApplication.config.grails.app.name}/js/bootstrap/moment.min.js" type="text/javascript" charset="utf-8"></script>
        <script src="${grailsApplication.config.grails.app.name}/js/bootstrap/fullcalendar.min.js" type="text/javascript" charset="utf-8"></script>
        <script src="${grailsApplication.config.grails.app.name}/js/bootstrap/jquery.dataTables.min.js"></script>
        <script src="${grailsApplication.config.grails.app.name}/js/bootstrap/chosen.jquery.min.js" type="text/javascript" charset="utf-8"></script>
        <script src="${grailsApplication.config.grails.app.name}/js/bootstrap/jquery.colorbox-min.js" type="text/javascript" charset="utf-8"></script>
        <script src="${grailsApplication.config.grails.app.name}/js/bootstrap/jquery.noty.js"></script>
        <script src="${grailsApplication.config.grails.app.name}/js/bootstrap/responsive-tables.js" type="text/javascript" charset="utf-8"></script>
        <script src="${grailsApplication.config.grails.app.name}/js/bootstrap/bootstrap-tour.min.js" type="text/javascript" charset="utf-8"></script>
        <script src="${grailsApplication.config.grails.app.name}/js/bootstrap/jquery.raty.min.js"></script>
        <script src="${grailsApplication.config.grails.app.name}/js/bootstrap/jquery.iphone.toggle.js"></script>
        <script src="${grailsApplication.config.grails.app.name}/js/bootstrap/jquery.autogrow-textarea.js"></script>
        <script src="${grailsApplication.config.grails.app.name}/js/bootstrap/jquery.uploadify-3.1.min.js"></script>
        <script src="${grailsApplication.config.grails.app.name}/js/bootstrap/jquery.history.js"></script>
        <script src="${grailsApplication.config.grails.app.name}/js/bootstrap/charisma.js"></script>--%>
        <script src="${grailsApplication.config.grails.app.name}/js/bootstrap/bootstrap-datetimepicker.min.js"></script>
        <script src="${grailsApplication.config.grails.app.name}/js/bootstrap/bootstrap-datetimepicker.zh-CN.js"></script>
	</body>
</html>
