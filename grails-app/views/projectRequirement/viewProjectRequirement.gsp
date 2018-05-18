<!DOCTYPE html>
<html>
<head>
<title>数据导出</title>
<meta name="layout" content="mainWithOutTop" />
<script type="text/javascript">
	function saveForProjectRequirementTemplate(project_requirement_id){
		if(confirm("确定要将此需求保存为模板么？")){
			var that = this ;
			$.ajax({
				type:"post",
				url:projectURI.defcon+"/projectRequirement/saveForProjectRequirementTemplate",
				data: {project_requirement_id:project_requirement_id},
				success:function(msg){
					alert(msg);
					location.reload();
				}
			});
		}
	}
	window.onload=function(){
		toChangeRequirementContent();
	}
	//关闭窗口方法
	function toCloseProjectRequirement(){
		this.close();
	}
	function toChangeRequirementContent(){
		var projectRequirementId = $("#projectRequirementId").val();
		var uri = "ajaxGetTeleplayRequirement";
		var selectId = $("#objectType option:selected").val();
		if(selectId == 4){
			uri = "ajaxGetMovieRequirement"
		}else if(selectId == 7){
			uri = "ajaxGetStarRequirement"
		}else if(selectId == 10){
			uri = "ajaxGetSpecialRequirement"
		}else if(selectId == 5){
			uri = "ajaxGetTeleplayRequirement"
		}else if(selectId == 680){
			uri = "ajaxGetRequirementVarietyShow"
		}
		$.ajax({
			type:"post",
			url:projectURI.defcon+"/projectRequirement/"+uri+"?viewAction=view&projectRequirementId="+projectRequirementId,
			async:false,
			success:function(data){
				$("#teleplayRequirementDiv").html(data);
			}
		})
	}
</script>
</head>
<body>
<div class="toEditProject" >
		<div class="head">
			<div style="float: left;margin-left: 10px;color: #fff;font-size: 20px;margin-top: 15px;">需求文档</div>
			<div style="float: right;margin-right: 10px;margin-top: 10px;">
				<button type="button" class="btn btn-primary btn-sm" style="width: 80px;" onclick="saveForProjectRequirementTemplate(${projectRequirement.id })">复制为模板</button>
				<button type="button" class="btn btn-primary btn-sm" style="width: 80px;" onclick="toCloseProjectRequirement()">取消</button>
			</div>	
		</div>
		<div style="margin-top: 15px;text-align:  -moz-left;">
			<form id="myForm">	
				<input type="hidden" name="project_id" value="${projectId }" />
				<input type="hidden" name="projectRequirementId" id="projectRequirementId" value="${projectRequirement.id }" />
				<table style="width: 100%">
					<tr>
						<td style="width: 6%">需求名称:</td>
						<td style="width: 35%">${projectRequirement.name }</td>
					</tr>
				</table>
				<div id="objectType" >
					<table style="width: 100%;">
						<tr>
							<td style="width: 15%">搜索对象:</td>
							<td colspan="3"><div id="group_search_id">
								<g:namesOfObjectIds objectType="${projectRequirement.object_type }" objectIds="${projectRequirement.object_ids }"/>
							</div></td>
						</tr>
						<tr>
							<td style="width: 15%">数据对象:</td>
							<td style="width: 15%">
								<g:dictionarySelect dicKey="project_requirement_object_type" name="objectType" id="objectType" disabled="disabled"  value="${projectRequirement.object_type }" />
							</td>	
							<td>
							</td>
							<td>日期
							${projectRequirement.begin_date }
							--
							${projectRequirement.end_date }
							</td>
						</tr>
						<tr>
							<td colspan="4" style="height: 30px;">
								<div class="project_line"></div>
							</td>
						</tr>
					</table>
					<div id="teleplayRequirementDiv" ></div>
				</div>			
			</form>
			<div class="footer">
				<div style="float: left;margin-left: 10px;color: #fff;font-size: 20px;margin-top: 15px;"></div>
				<div style="float: right;margin-right: 10px;margin-top: 20px;">
					<button type="button" class="btn btn-primary btn-sm" style="width: 80px;" onclick="saveForProjectRequirementTemplate(${projectRequirement.id })">复制为模板</button>
					<button type="button" class="btn btn-primary btn-sm" style="width: 80px;" onclick="toCloseProjectRequirement()">取消</button>
				</div>	
			</div>
			
		</div>	
</div>		
</body>
</html>
							