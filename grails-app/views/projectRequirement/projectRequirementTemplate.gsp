<!DOCTYPE html>
<html>
<head>
<title>数据导出</title>
<meta name="layout" content="main" />
<link href="${grailsApplication.config.grails.app.name }/css/common.css" rel="stylesheet" type="text/css"></link>
<script type="text/javascript">

	function editProjectRequirementTemplateInfo(id){
		window.open('${grailsApplication.config.grails.app.name }/projectRequirement/editProjectRequirementTemplate?requirementId='+id, 'editwindow', 'height=800, width=680, top=40, left='+ (window.screen.availWidth-10-1000)/2+', toolbar=no, menubar=no, scrollbars=yes, resizable=no, location=no, status=no')   //该句写成一行代码

	}

	function shareProjectRequirementTemplate(id){
		window.open('${grailsApplication.config.grails.app.name }/projectRequirement/shareProjectRequirementTemplate?requirementId='+id, 'editwindow', 'height=200, width=680, top=40, left='+ (window.screen.availWidth-10-1000)/2+', toolbar=no, menubar=no, scrollbars=yes, resizable=no, location=no, status=no')   //该句写成一行代码

	}
	
	function getKey(event)  
	{  
	    if(event.keyCode==13){  
	    	toQueryProjectTemplate();
	    }     
	} 
	function toQueryProjectTemplate(){
		var projectName = $("#query_project_name").val();
		var projectName = projectName.trim();
		window.location.href = "${ grailsApplication.config.grails.app.name}/projectRequirement/projectRequirementTemplate?projectRequirementName="+projectName;
		
	}

	function delProjectRequirementTemplateInfo(id){
		if(confirm("确定要删除此条记录吗？")){
			var that = this ;
			$.ajax({
				type:"post",
				url:"${ grailsApplication.config.grails.app.name}/projectRequirement/delProjectRequirementTemplateInfo",
				data: {project_requirement_id:id},
				success:function(msg){
					alert(msg);
					location.reload();
				}
			});
		}
	}

	function saveForProjectRequirementWithTemplate(projectRequirementId,projectId){
		if(confirm("确定要引用此模板生成项目需求吗?")){
			var that = this ;
			$.ajax({
				type:"post",
				url:"${ grailsApplication.config.grails.app.name}/projectRequirement/saveForProjectRequirementWithTemplate",
				data: {projectId:projectId,projectRequirementId:projectRequirementId},
				success:function(msg){
					alert(msg);
				}
			});
		}
	}

	$(function(){
		$.ajax({
			type:"post",
			url:projectURI.defcon+"/projectRequirement/ajaxGetProjectRequirementLeftMenu?projectId=${params.projectId}",
			async:false,
			success:function(data){
				$("#leftMenu").html(data);
			}
		})
	})	
</script>
	
</head>
<body>
	<div id="leftMenu" style="float: left; width: 15%; text-align: center; height: 800px;">
	</div>
	
	<div style="float: right; width: 85%; height: 800px;">
		<div  style="margin-top: 20px; width: 100%;margin-bottom: 40px;">
				
			 	<input type="text" value="${params.projectRequirementName }" onkeypress="getKey(event);" class="form-control" placeholder="请输入模板名称" id="query_project_name" style="width: 400px;display: inline;">
			 	<button type="button" class="btn btn-default" onclick="toQueryProjectTemplate()">查询</button>
			
		</div>
		<div>
			<table class="table table-bordered">
				<tr>
					<th>模板名称</th>
					<th>保存时间</th>
					<th>操作</th>
				</tr>
					<g:each in="${result }" var="projectRequirement">
						<tr>
							<td>
								${projectRequirement.name }
							</td>
							<td>${projectRequirement.operation_date }</td>
							<td>
								<a href="javascript:editProjectRequirementTemplateInfo(${projectRequirement.id })">编辑</a>
								<a href="javascript:delProjectRequirementTemplateInfo(${projectRequirement.id })">删除</a>
								<a href="javascript:saveForProjectRequirementWithTemplate(${projectRequirement.id },${params.projectId })">引用</a>
								<a href="javascript:shareProjectRequirementTemplate(${projectRequirement.id })">分享</a>
							</td>
						</tr>
					</g:each>
			</table>
			<bs:normalPageTag totalCount="${myProjectRequirementTemplateCount }" />
		</div>
	</div>
</body>
</html>