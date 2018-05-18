<!DOCTYPE html>
<html>
<head>
<title>数据导出</title>
<meta name="layout" content="mainWithOutTop" />
<script type="text/javascript">



function toCloseProject(){

	this.close();
}

function saveProjectAnalystInfo(status){
	$("#project_status").val(status);
	var that = this ;
	$.ajax({
		type:"post",
		url:"${ grailsApplication.config.grails.app.name}/projectRequirement/shareProjectRequirementTemplateToAnthorAnalyst",
		data: $("#myForm").serialize(),  
		success:function(msg){
			alert(msg);
			window.opener.location.reload();
			that.close();
		}
	});
}


</script>


</head>
<body>
<div class="toEditProject" >
		<div class="head">
			<div style="float: left;margin-left: 10px;color: #fff;font-size: 20px;margin-top: 15px;">分享项目需求模板</div>
			<div style="float: right;margin-right: 10px;margin-top: 10px;">
				<button type="button" class="btn btn-primary btn-sm" style="width: 80px;" onclick="saveProjectAnalystInfo(0)">保存</button>
				<button type="button" class="btn btn-primary btn-sm" style="width: 80px;" onclick="toCloseProject()">取消</button>
			</div>	
		</div>
		<div style="margin-top: 15px;text-align: center;">
			<form id="myForm">
				<input type="hidden" name="id" value="${requirementId }">
				<table style="width: 100%">
					<tr>
						<td>
							分析师：<br />
						</td>
						<td colspan="3" style="text-align: left;">
							<g:personsByDeptId name="analysts" deptId="010d" />
						</td>
					</tr>
					
					<tr>
						<td colspan="4" style="height: 30px;">
							<div class="project_line"></div>
						</td>
					</tr>
					
				</table>
			</form>
				
			
		</div>	
</div>		
</body>
</html>