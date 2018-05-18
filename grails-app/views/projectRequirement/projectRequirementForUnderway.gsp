<!DOCTYPE html>
<html>
<head>
<title>数据导出</title>
<meta name="layout" content="main" />
<link href="${grailsApplication.config.grails.app.name }/css/common.css" rel="stylesheet" type="text/css"></link>
<script src="${ grailsApplication.config.grails.app.name}/js/projectRequirement/projectRequirementList.js" type="text/javascript" charset="utf-8"></script>
	
</head>
<body>
	<div id="leftMenu" style="float: left; width: 15%; text-align: center; height: 800px;">
	</div>
	
	<div style="float: right; width: 85%; height: 800px;">
		<input type="hidden" id="projectId" value="${params.projectId }" />
		项目名称：${projectName }
		<table class="table table-bordered">
				<tr>
					<th>需求名称</th>
					<th>提交时间</th>
					<th>提交人员</th>
					<th>需求状态</th>
					<th>操作</th>
				</tr>
					<g:each in="${result }" var="requirement">
						<tr>
							<td>
								<a href="javascript:viewProjectRequirement(${requirement.rId })">${requirement.rName }</a>
							</td>
							<td><g:fmtDateTime value="${requirement.submit_time }" /></td>
							<td>${requirement.uName }</td>
							<td>${requirement.state }</td>
							<td>
								<%-- 如果是技术支持 --%>
								<g:if test="${session?.isTechincalSupportAccount == true}">
									<%-- 如果状态是待审核 --%>
									<g:if test="${requirement.requirement_state==1 }">
										<%-- 如果是特殊需求，则显示审核通过按钮 --%>
										<g:if test="${requirement.object_type == 10}">
											<a href="javascript:updateProjectRequirementStatus(${requirement.rId },2,'确定将此需求通过审核么？')">审核通过</a>	
											<a href="javascript:overruleProjectRequirement(${requirement.rId })">驳回申请</a>
										</g:if>
									</g:if>
									<g:elseif test="${requirement.requirement_state==2 }">
										<g:if test="${requirement.object_type == 10}">
											<a href="javascript:upload(${requirement.rId })">上传文档</a>
											<a href="javascript:updateProjectRequirementStatus(${requirement.rId },3,'确定此需求已经完成了么？')">完成 </a>		
										</g:if>
									</g:elseif>
									<g:elseif test="${requirement.requirement_state==4 }">
										<a href="javascript:viewAgainProjectRequirement(${requirement.rId })">重跑原因</a>
										<a href="javascript:updateProjectRequirementStatus(${requirement.rId },2,'确定将此需求通过审核么？')">审核通过</a>								
										<a href="javascript:overruleProjectRequirement(${requirement.rId })">驳回申请</a>
									</g:elseif>
									<g:elseif test="${requirement.requirement_state==5 }">
										<g:if test="${requirement.object_type == 10}">
											<a href="javascript:upload(${requirement.rId })">上传文档</a>
										</g:if>
									</g:elseif>
								</g:if>
								<%-- 如果不是技术人员 --%>
								<g:else >
									<%-- 如果是本人的数据需求 --%>
									<g:if test="${requirement?.operation_person == session?.operationInfo?.id}">
										<g:if test="${requirement.requirement_state==0}">
											<a href="javascript:delProjectInfo(${requirement.rId })">编辑</a>
											<a href="javascript:delProjectRequirementInfo(${requirement.rId })">删除</a>
											<a href="javascript:updateProjectRequirementStatus(${requirement.rId },2,'确定要提交此需求吗？')">提交 </a>							
										</g:if>
										<g:elseif test="${requirement.requirement_state==1 || requirement.requirement_state==2 || requirement.requirement_state==4 || requirement.requirement_state==5 }">
											<a href="javascript:updateProjectRequirementStatus(${requirement.rId },-1,'确定要终止此需求吗？')">终止</a>
											<g:if test="${requirement.requirement_state==2 }">
												<a href="javascript:saveForProjectRequirementTemplate(${requirement.rId })">另存为模板</a>
											</g:if>
										</g:elseif>
										<g:elseif test="${requirement.requirement_state==3}">
											<a href="javascript:againProjectRequirement(${requirement.rId })">重跑</a>
										</g:elseif>
										<g:elseif test="${requirement.requirement_state==6 || requirement.requirement_state==7}">
											<a href="javascript:againProjectRequirement(${requirement.rId })">重跑</a>
											<a href="javascript:viewOverruleProjectRequirement(${requirement.rId })">查看反馈</a>
										</g:elseif>
									</g:if>
								</g:else>
							</td>
						</tr>
					</g:each>
			</table>
			<bs:normalPageTag totalCount="${requirementCount }" />
	</div>
</body>
</html>