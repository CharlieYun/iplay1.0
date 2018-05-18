<!DOCTYPE html>
<html>
<head>
<title>数据导出</title>
<meta name="layout" content="main" />
<link href="${grailsApplication.config.grails.app.name }/css/common.css"
	rel="stylesheet" type="text/css"></link>
	
<script type="text/javascript">
function toShowMoreRequirement(id){
	$(".moreRequirement"+id).css('display' ,'');  
	$("#chaKanGengDuo"+id).css('display','none');
}
function getKey(event)  
{  
    if(event.keyCode==13){  
    	toQueryProject();
    }     
}  
function toQueryProject(){
	var projectName = $("#query_project_name").val();
	var projectName = projectName.trim();
	window.location.href = "${ grailsApplication.config.grails.app.name}/project/projectDynamic?projectName="+projectName;
	
}
$(function(){
	$.ajax({
		type:"post",
		url:projectURI.defcon+"/project/ajaxGetProjectLeftMenu",
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
		 	<input type="text" value="${params.projectName }" onkeypress="getKey(event);" class="form-control" placeholder="请输入项目名称" id="query_project_name" style="width: 400px;display: inline;">
		 	<button type="button" class="btn btn-default" onclick="toQueryProject()">查询</button>
		</div>
		当前有${myProjectCount }个项目
		<div>
	
			<g:each in="${result }" var="project">

				<div>
					<table class="table table-bordered">
						<tr>
							<td colspan="4" style="text-align: left;">
								<span style="font-size: 16px;font-weight: bold;">《${project.name }》</span>&nbsp;项目经理:${project.manager },&nbsp;组员:${project.privyCount }人,&nbsp;时间:${project.begin_time }——${project.end_time }
							</td>
						</tr>
						<g:each in="${project.requirements }" var="requirement" status="index">
						<g:if test="${index < 2 }">
							<tr style="text-align: center;">
								<td>${requirement.rName }</td>
								<td>${requirement.uName }</td>
								<td>${requirement.state }</td>
								<td>${requirement.operation_date }</td>
							</tr>
						</g:if>	
						<g:elseif test="${index == 2 }">
							<tr style="text-align: center;" id = "chaKanGengDuo${project.id }">
								<td colspan="4"><a href="#" onclick="toShowMoreRequirement(${project.id })">查看更多</a></td>
							</tr>
							<tr style="text-align: center;display: none;" class="moreRequirement${project.id }">
								<td>${requirement.rName }</td>
								<td>${requirement.uName }</td>
								<td>${requirement.state }</td>
								<td>${requirement.operation_date }</td>
							</tr>
						</g:elseif>
						<g:else>
							<tr style="text-align: center;display: none;" class="moreRequirement${project.id }">
								<td>${requirement.rName }</td>
								<td>${requirement.uName }</td>
								<td>${requirement.state }</td>
								<td>${requirement.operation_date }</td>
							</tr>
						</g:else>
						</g:each>
						
					</table>
				</div>
			</g:each>
			<bs:normalPageTag totalCount="${myProjectCount }" />
		</div>
	</div>
</body>
</html>