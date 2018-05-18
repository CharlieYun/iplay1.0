<!DOCTYPE html>
<html>
<head>
<title>数据导出</title>
<meta name="layout" content="mainWithOutTop" />
<script src="${grailsApplication.config.grails.app.name}/js/autoComplete/autoComplete.js" ></script>
<script src="${ grailsApplication.config.grails.app.name}/js/projectRequirement/projectRequirementTemplateEdit.js" type="text/javascript" charset="utf-8"></script>
</head>
<body>
<div class="toEditProject" >
		<div class="head">
			<div style="float: left;margin-left: 10px;color: #fff;font-size: 20px;margin-top: 15px;">需求文档</div>
			<div style="float: right;margin-right: 10px;margin-top: 10px;">
				<button type="button" class="btn btn-primary btn-sm" style="width: 80px;" onclick="editProjectRequirementInfo(0)">保存</button>
				<button type="button" class="btn btn-primary btn-sm" style="width: 80px;" onclick="toCloseProjectRequirement()">取消</button>
			</div>	
		</div>
		<div style="margin-top: 15px;text-align:  -moz-left;">
			<form id="myForm">	
				<input type="hidden" name="projectRequirementId" id="projectRequirementId" value="${projectRequirement.id }" />
				<input type="hidden" name="objType" id="objType" value="${projectRequirement.object_type }" />
				<input type="hidden" name="ides" id="ides" value="${projectRequirement.object_ids }" />
				<input type="hidden" name="namess" id="namess" value="${names }" />
				<table style="width: 100%">
					<tr>
						<td style="width: 6%">需求名称:</td>
						<td style="width: 35%"><input type="text" id="requirementName" name="requirementName" value="${projectRequirement.name }"></td>
					</tr>
				</table>
				<div id="objectType" >
					<table style="width: 100%;">
						<tr>
							<td style="width: 15%">搜索对象:</td>
							<td colspan="3"><div id="group_search_id">
								<g:namesOfObjectIds objectType="${projectRequirement.object_type }" objectIds="${projectRequirement.object_ids }"/>
							</div>
							<input type="hidden" id="objectIds" name="objectIds" />
							</td>
						</tr>
						<tr>
							<td style="width: 15%">数据对象:</td>
							<td style="width: 15%">
								<g:dictionarySelect dicKey="project_requirement_object_type" name="objectType" id="objectType" value="${projectRequirement.object_type }" method="onchange='toChangeRequirementContent1()'" />
							</td>	
							<td>
							 <input id="group_search" type="text" style="margin-left:-15px;width:100%;">
							</td>
							<td>日期
							<input type="text" id="begin_time" name="begin_time" value='${projectRequirement.begin_date }' style="width:80px;" readonly="readonly" onclick="SelectDate(this,'yyyy-MM-dd',0,0)"/>
							--
							<input type="text" id="end_time" name="end_time" value="${projectRequirement.end_date }" style="width:80px;" readonly="readonly" onclick="SelectDate(this,'yyyy-MM-dd',0,0)"/>
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
					<button type="button" class="btn btn-primary btn-sm" style="width: 80px;" onclick="editProjectRequirementInfo(0)">保存</button>
					<button type="button" class="btn btn-primary btn-sm" style="width: 80px;" onclick="toCloseProjectRequirement()">取消</button>
				</div>	
			</div>
			
		</div>	
</div>		
</body>
</html>
							