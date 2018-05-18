<!DOCTYPE html>
<html>
<head>
<title>数据导出</title>
<meta name="layout" content="main" />
<link href="${grailsApplication.config.grails.app.name }/css/common.css" rel="stylesheet" type="text/css"></link>
<script type="text/javascript">
	var isShow=false;
	function toShowProject(){
		window.open('${grailsApplication.config.grails.app.name }/project/addProject', 'addwindow', 'height=800, width=680, top=40, left='+ (window.screen.availWidth-10-1000)/2+', toolbar=no, menubar=no, scrollbars=yes, resizable=no, location=no, status=no')   //该句写成一行代码
	}

	function viewProjectInfo(id){
		window.open('${grailsApplication.config.grails.app.name }/project/viewProject?project_id='+id, 'viewwindow', 'height=800, width=680, top=40, left='+ (window.screen.availWidth-10-1000)/2+', toolbar=no, menubar=no, scrollbars=yes, resizable=no, location=no, status=no')   //该句写成一行代码
	}

	function editProjectInfo(id){
		window.open('${grailsApplication.config.grails.app.name }/project/editProject?project_id='+id, 'editwindow', 'height=800, width=680, top=40, left='+ (window.screen.availWidth-10-1000)/2+', toolbar=no, menubar=no, scrollbars=yes, resizable=no, location=no, status=no')   //该句写成一行代码

	}

	function editProjectAnalyst(id){
		window.open('${grailsApplication.config.grails.app.name }/project/editProjectAnalyst?project_id='+id, 'editwindow', 'height=200, width=680, top=40, left='+ (window.screen.availWidth-10-1000)/2+', toolbar=no, menubar=no, scrollbars=yes, resizable=no, location=no, status=no')   //该句写成一行代码
		
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
		window.location.href = "${ grailsApplication.config.grails.app.name}/project/projectExecute?projectName="+projectName;
		
	}

	function saveProjectInfo(status){
		$("#project_status").val(status);
		var that = this ;
		$.ajax({
			type:"post",
			url:"${ grailsApplication.config.grails.app.name}/project/saveProjectInfo",
			data: $("#myForm").serializeArray(),  
			success:function(msg){
				alert(msg);
				that.toShowProject();
			}
		});
	}

	function delProjectInfo(id){
		if(confirm("确定要删除此条记录吗？")){
			var that = this ;
			$.ajax({
				type:"post",
				url:"${ grailsApplication.config.grails.app.name}/project/delProjectInfo",
				data: {project_id:id},
				success:function(msg){
					alert(msg);
					location.reload();
				}
			});
		}
	}

	function updateProjectStatus(id,status){
		if(confirm("确定要修改词条记录的状态吗？")){
			var that = this ;
			$.ajax({
				type:"post",
				url:"${ grailsApplication.config.grails.app.name}/project/updateProjectStatus",
				data: {project_id:id,project_status:status},
				success:function(msg){
					alert(msg);
					location.reload();
				}
			});
		}
	}

	function saveForProjectTemplate(id){
		if(confirm("确定要将此项目保存为模板么？")){
			var that = this ;
			$.ajax({
				type:"post",
				url:"${ grailsApplication.config.grails.app.name}/project/saveForProjectTemplate",
				data: {project_id:id},
				success:function(msg){
					alert(msg);
					location.reload();
				}
			});
		}
	}

	function checkBoxValue(){
		var chk_value =[]; 
		$('input[name="project_ids"]:checked').each(function(){ 
			chk_value.push($(this).val()); 
		});
		if(chk_value.length==0){
			alert('你还没有选择任何内容！');
			return ;
		}
		window.location.href = "${ grailsApplication.config.grails.app.name}/project/getZip?project_ids="+chk_value ;
	}

	function projectRequirementList(projectId,projectName){

		// 验证该用户是否有权限进入数据需求
		$.ajax({
				type:"post",
				url:"${ grailsApplication.config.grails.app.name}/project/judgePersionHasRequirementPermission",
				data: {project_id:projectId},
				dataType: "json",
				success:function(data){
					var obj = eval(data);
					if(obj.errorCode =="0"){
						alert(obj.errorMsg);
						return ;
					}
					else{
						window.location.href = "${ grailsApplication.config.grails.app.name}/projectRequirement/projectRequirementList?projectId=" +projectId+"&project_name="+projectName;
					}
				}
		});
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
				<button type="button" class="btn btn-default" onclick="toShowProject()" style="margin-left: 40px;">新建项目</button>
				<button type="button" class="btn btn-default" onclick="checkBoxValue()">批量下载文档</button>
			
		</div>
		<div>
			<table class="table table-bordered">
				<tr>
					<th>项目名称</th>
					<th>开始时间</th>
					<th>截止时间</th>
					<th>项目经理</th>
					<th>状态</th>
					<th>操作</th>
				</tr>
					<g:each in="${result }" var="project">
						<tr>
							<td>
								<g:if test="${project.status!=0}">
									<input type="checkbox" name="project_ids" value="${project.id }">
								</g:if>
								<a href="javascript:viewProjectInfo(${project.id })">${project.name }</a>
							</td>
							<td>${project.begin_time }</td>
							<td>${project.end_time }</td>
							<td>${project.manager }</td>
							<td>
								<g:dictionaryText key="project_status" value="${project.status }" />
							</td>
							<td>
								<a href="javascript:projectRequirementList(${project.id },'${project.name }')" >进入需求</a> 
								<g:if test="${project.create_person_id == session?.operationInfo?.id}">
									<g:if test="${project.status==0}">
										<a href="javascript:editProjectInfo(${project.id })">编辑</a>
										<a href="javascript:delProjectInfo(${project.id })">删除</a>
										<a href="javascript:updateProjectStatus(${project.id },1)">提交 </a>									
									</g:if>
									<g:elseif test="${project.status==1 || project.status==4 || project.status==5 }">
										<a href="javascript:updateProjectStatus(${project.id },2)">完成</a>
										<a href="javascript:updateProjectStatus(${project.id },3)">暂停</a>
										<a href="javascript:updateProjectStatus(${project.id },-1)">终止</a>
										<a href="javascript:editProjectAnalyst(${project.id })">分析师修改</a>
										<a href="javascript:saveForProjectTemplate(${project.id })">另存模板</a>
									</g:elseif>
									<g:elseif test="${project.status==3}">
										<a href="javascript:updateProjectStatus(${project.id },4)">恢复 </a>
										<a href="javascript:updateProjectStatus(${project.id },-1)">终止</a>
									</g:elseif>
									<g:elseif test="${project.status==2 || project.status==-1}">
										<a href="javascript:updateProjectStatus(${project.id },5)">重启 </a>
									</g:elseif>
								</g:if>
							</td>
						</tr>
					</g:each>
			</table>
			<bs:normalPageTag totalCount="${myProjectCount }" />
		</div>
	</div>
	
</body>
</html>