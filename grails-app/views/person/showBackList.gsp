<!DOCTYPE html>
<html>
<head>
<title>数据导出</title>
<meta name="layout" content="mainWithOutTop" />
<link href="${grailsApplication.config.grails.app.name }/css/common.css" rel="stylesheet" type="text/css"></link>
<script type="text/javascript">
	function toBack(id,name,deptName,duties){

	 	window.opener.disposeBackInfo(id,name,deptName,duties); 
        window.close(); 
		
	}	
	function toQuery(){
		window.location.href="${ grailsApplication.config.grails.app.name}/person/showBackList?name="+$("#name").val();
	}	
</script>
	
</head>
<body>
	<div  style="margin-top: 20px; width: 100%;margin-bottom: 20px;margin-left: 20px;">
				
	 	<input type="text" value="${params.name }" name="name" id="name" onkeypress="getKey(event);" class="form-control" placeholder="请输入名称" id="query_project_name" style="width: 400px;display: inline;">
	 	<button type="button" class="btn btn-default" onclick="toQuery()">查询</button>
		
	</div>
	<table class="table table-bordered">
		<tr>
			<th>名称</th>
			<th>部门</th>
			<th>职位</th>
			<th>操作</th>
		</tr>
			<g:each in="${result }" var="person">
				<tr>
					<td>
						${person.name }
					</td>
					<td>${person.deptName }</td>
					<td>${person.common_duties }</td>
					<td>
						<a href="javascript:toBack('${person.id }','${person.name }','${person.deptName }','${person.common_duties }')" >带回</a>
					</td>
				</tr>
			</g:each>
	</table>
	<bs:normalPageTag totalCount="${myCount }" />
</body>
</html>