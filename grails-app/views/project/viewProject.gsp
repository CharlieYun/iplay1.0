<!DOCTYPE html>
<html>
<head>
<title>数据导出</title>
<meta name="layout" content="mainWithOutTop" />
<script type="text/javascript">
function toCloseProject(){

	this.close();
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
</script>


</head>
<body>
<div class="toEditProject" >
		<div class="head">
			<div style="float: left;margin-left: 10px;color: #fff;font-size: 20px;margin-top: 15px;">项目文档</div>
			<div style="float: right;margin-right: 10px;margin-top: 10px;">
				<button type="button" class="btn btn-primary btn-sm" style="width: 80px;" onclick="saveForProjectTemplate(${project.id })">复制为模板</button>
				<button type="button" class="btn btn-primary btn-sm" style="width: 80px;" onclick="toCloseProject()">关闭</button>
			</div>	
		</div>
		<div style="margin-top: 15px;text-align: center;">
			<form id="myForm">
				<table style="width: 100%">
					<tr>
						<td style="width: 15%">项目名称:</td>
						<td style="width: 35%">${project.name }</td>
						<td style="width: 15%">项目编号:</td>
						<td style="width: 35%">${project.code }</td>
					</tr>
					<tr>
						<td colspan="4" style="height: 30px;">
							<div class="project_line"></div>
						</td>
					</tr>
					<tr>
						<td>客户名称:</td>
						<td>${project.customer_name }</td>
						<td>项目经理:</td>
						<td>${project.manager }</td>
					</tr>
					
					<tr>
						<td>审核人:</td>
						<td>${project.verifier }</td>
						<td>项目周期:</td>
						<td>
							
						${project.begin_time }
						--
						${project.end_time }
						
						</td>
					</tr>
					
					<tr>
						<td>立项日期:</td>
						<td>
							${project.create_time }
						</td>
						<td>项目状态:</td>
						<td>
							<g:dictionaryText key="project_status" value="${project.status }"/>
						</td>
					</tr>
					
					<tr>
						<td colspan="4" style="height: 30px;">
							<div class="project_line"></div>
						</td>
					</tr>
					
					<tr>
						<td>
							项目描述:<br />
							（背景、目的）
						</td>
						<td colspan="3">
							<textarea rows="5" cols="20" style="width: 90%; height: 100px;" name="desc" disabled="disabled">${project.desc }</textarea>
						</td>
					</tr>
					<tr>
						<td>
							项目目标：<br />
							（质量、工期）
						</td>
						<td colspan="3">
							<textarea rows="5" cols="20" style="width: 90%; height: 100px;" name="target" disabled="disabled">${project.target }</textarea>
						</td>
					</tr>
					<tr>
						<td>
							项目里程碑计划：<br />
							（时间、成果）
						</td>
						<td colspan="3">
							<textarea rows="5" cols="20" style="width: 90%; height: 100px;" name="milestone" disabled="disabled">${project.milestone }</textarea>
						</td>
					</tr>
					<tr>
						<td>
							评价标准：
						</td>
						<td colspan="3">
							<textarea rows="5" cols="20" style="width: 90%; height: 100px;" name="evaluation_criterion" disabled="disabled">${project.evaluation_criterion }</textarea>
						</td>
					</tr>
					<tr>
						<td>
							项目假定与约束条件：<br />
							（非必填）
						</td>
						<td colspan="3">
							<textarea rows="5" cols="20" style="width: 90%; height: 100px;" name="constraint_condition" disabled="disabled">${project.constraint_condition }</textarea>
						</td>
					</tr>
					
					<tr>
						<td colspan="4" style="height: 30px;">
							<div class="project_line"></div>
						</td>
					</tr>
					
					<tr>
						<td>
							分析师：<br />
						</td>
						<td colspan="3" style="text-align: left;">
							<g:personsByDeptId name="analysts" deptId="010d" value="${analysts }" />
						</td>
					</tr>
					
					<tr>
						<td colspan="4" style="height: 30px;">
							<div class="project_line"></div>
						</td>
					</tr>
					
				</table>
					
				<div style="text-align: left;">项目主要利益干系人:</div>
				<table class="table table-bordered" id="privy_table"  style="width: 99%;margin-left: 2px;">
					<tr>
						<th>姓名</th>
						<th>类别</th>
						<th>部门</th>
						<th>职务</th>
					</tr>
					<g:each in="${privys }" var="privy">
						<tr>
							<td>
								<g:personInfo name="name" personId="${privy.person_id }"/>
							</td>
							<td>${privy.project_role }</td>
							<td><g:personInfo name="common_duties" personId="${privy.person_id }"/></td>
							<td><g:personInfo name="deptName" personId="${privy.person_id }"/></td>
							
						</tr>
					</g:each>
				</table>
				
			</form>
				
			<div class="footer">
				<div style="float: left;margin-left: 10px;color: #fff;font-size: 20px;margin-top: 15px;"></div>
				<div style="float: right;margin-right: 10px;margin-top: 20px;">
					<button type="button" class="btn btn-primary btn-sm" style="width: 80px;" onclick="saveForProjectTemplate(${project.id })">复制为模板</button>
					<button type="button" class="btn btn-primary btn-sm" style="width: 80px;" onclick="toCloseProject()">关闭</button>
				</div>	
			</div>
			
		</div>	
</div>		
</body>
</html>