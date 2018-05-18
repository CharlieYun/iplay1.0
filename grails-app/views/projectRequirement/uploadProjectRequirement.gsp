<!DOCTYPE html>
<html>
<head>
<title>数据导出</title>
<meta name="layout" content="mainWithOutTop" />
<script type="text/javascript">

function upload(){
		$("form:first").submit();
}

function toCloseProject(){

	this.close();
}


</script>


</head>
<body>
<div class="toEditProject" >
		<div class="head">
			<div style="float: left;margin-left: 10px;color: #fff;font-size: 20px;margin-top: 15px;">上传项目需求文档</div>
			<div style="float: right;margin-right: 10px;margin-top: 10px;">
				<button type="button" class="btn btn-primary btn-sm" style="width: 80px;" onclick="upload()">保存</button>
				<button type="button" class="btn btn-primary btn-sm" style="width: 80px;" onclick="toCloseProject()">取消</button>
			</div>	
		</div>
		<div style="margin-top: 15px;text-align: center;">
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:form action="doUploadProjectRequirement" id="myForm" method="post" enctype="multipart/form-data">
				<input type="file" name="requirementExcel" />
				<input type="hidden" name="requirement_id" value="${params.requirement_id }" />
			</g:form>
		</div>	
</div>		
</body>
</html>