<!DOCTYPE html>
<html>
<head>
<title>数据导出</title>
<meta name="layout" content="mainWithOutTop" />
<script type="text/javascript">



function toCloseProject(){

	this.close();
}

function saveProjectInfo(status){

	
	if(!$("#name").val()){
		alert("请输入项目名称!");
		$("#name").focus();
		return ;
	}
	if(!$("#code").val()){
		alert("请输入项目编号!");
		$("#code").focus();
		return ;
	}
	if(!$("#customer_name").val()){
		alert("请输入客户名称");
		$("#customer_name").focus();
		return ;
	}
	if(!$("#manager").val()){
		alert("请输入项目经理!");
		$("#manager").focus();
		return ;
	}
	if(!$("#verifier").val()){
		alert("请输入审核人!");
		$("#verifier").focus();
		return ;
	}
	if(!$("#begin_time").val()){
		alert("请选择开始时间!");
		$("#begin_time").focus();
		return ;
	}
	if($("#begin_time").val() > $("#end_time").val() ){
		alert("开始时间不能大于结束时间");
		return ;
	}
	if(!$("#end_time").val()){
		alert("请选择结束时间!");
		$("#end_time").focus();
		return ;
	}
	if(!$("#create_time").val()){
		alert("请选择创建时间!");
		$("#create_time").focus();
		return ;
	}
	if(!$("#desc").val()){
		alert("请输入描述信息!");
		$("#desc").focus();
		return ;
	}
	if(!$("#target").val()){
		alert("请输入项目目标!");
		$("#target").focus();
		return ;
	}
	if(!$("#milestone").val()){
		alert("请输入项目里程碑计划!");
		$("#milestone").focus();
		return ;
	}
	if(!$("#evaluation_criterion").val()){
		alert("请输入评价标准!");
		$("#evaluation_criterion").focus();
		return ;
	}
	if(!$("#constraint_condition").val()){
		alert("请输入项目假定与约束条件!");
		$("#constraint_condition").focus();
		return ;
	}

	var chk_value =[]; 
	$('input[name="analysts"]:checked').each(function(){ 
		chk_value.push($(this).val()); 
	}); 
	if(chk_value.length==0){
		alert("请选择项目分析师");
		return ;
	}

	if(!$('input[name="privy_person_id"]').val()){
		alert("请增加项目成员!");
		return ;
	}
	
	$("#project_status").val(status);
	var that = this ;
	$.ajax({
		type:"post",
		url:"${ grailsApplication.config.grails.app.name}/project/saveProjectInfo",
		data: $("#myForm").serialize(),   
		success:function(msg){
			alert(msg);
			window.opener.location.reload();
			that.close();
		}
	});
}

var privyTrIndex = 0 ;
function toAddPrivyTr(){
	var newRow = "<tr id='privy_tr_"+privyTrIndex+"'><td><input type='hidden' name='privy_person_id' id = 'privy_person_id_"+privyTrIndex+"'><input type='text' id = 'privy_person_name_"+privyTrIndex+"' style='width:80%;'><input type='button' onclick='toShowPersonList("+privyTrIndex+")' value='...'/></td>"+
	'<td><g:dictionarySelect name="privy_person_role" dicKey="project_privy_type" /></td>'+
	"<td id = 'privy_person_deptName_"+privyTrIndex+"'>&nbsp;</td><td id = 'privy_person_duties_"+privyTrIndex+"'>&nbsp;</td><td><a href='javascript:toDelPrivyTr("+privyTrIndex+")' >删除</a></td></tr>" ;	
	$('#privy_table').append(newRow);
	privyTrIndex++;
}


function toDelPrivyTr(index){
	$('#privy_tr_'+index).remove();
}

var privy_table_tr = -1 ;
function toShowPersonList(index){
	window.open('${grailsApplication.config.grails.app.name }/person/showBackList', 'toShowPersonList', 'height=400, width=600, top=100, left='+ (window.screen.availWidth-10-600)/2+', toolbar=no, menubar=no, scrollbars=yes, resizable=no, location=no, status=no')   //该句写成一行代码
	privy_table_tr = index ;
}




function disposeBackInfo(id,name,deptName,duties){
	if(privy_table_tr>=0){
		$("#privy_person_id_"+privy_table_tr).val(id);
		$("#privy_person_name_"+privy_table_tr).val(name);
		$("#privy_person_deptName_"+privy_table_tr).text(deptName);
		$("#privy_person_duties_"+privy_table_tr).text(duties);
	}
	privy_table_tr = -1 ;
}



</script>


</head>
<body>
<div class="toEditProject" >
		<div class="head">
			<div style="float: left;margin-left: 10px;color: #fff;font-size: 20px;margin-top: 15px;">项目文档</div>
			<div style="float: right;margin-right: 10px;margin-top: 10px;">
				<button type="button" class="btn btn-primary btn-sm" style="width: 80px;" onclick="saveProjectInfo(1)">提交</button>
				<button type="button" class="btn btn-primary btn-sm" style="width: 80px;" onclick="saveProjectInfo(0)">保存</button>
				<button type="button" class="btn btn-primary btn-sm" style="width: 80px;" onclick="toCloseProject()">取消</button>
			</div>	
		</div>
		<div style="margin-top: 15px;text-align: center;">
			<form id="myForm">
				<table style="width: 100%">
					<tr>
						<td style="width: 15%">项目名称:</td>
						<td style="width: 35%"><input type="text" id="name" name="name"></td>
						<td style="width: 15%">项目编号:</td>
						<td style="width: 35%"><input type="text" id="code" name="code"></td>
					</tr>
					<tr>
						<td colspan="4" style="height: 30px;">
							<div class="project_line"></div>
						</td>
					</tr>
					<tr>
						<td>客户名称:</td>
						<td><input type="text" id="customer_name" name="customer_name"></td>
						<td>项目经理:</td>
						<td><input type="text" id="manager" name="manager"></td>
					</tr>
					
					<tr>
						<td>审核人:</td>
						<td><input type="text" id="verifier" name="verifier"></td>
						<td>项目周期:</td>
						<td>
							
						<input type="text" id="begin_time" name="begin_time" style="width:80px;" readonly="readonly" onclick="SelectDate(this,'yyyy-MM-dd',0,0)"/>
						--
						<input type="text" id="end_time" name="end_time" style="width:80px;" readonly="readonly" onclick="SelectDate(this,'yyyy-MM-dd',0,0)"/>
						
						</td>
					</tr>
					
					<tr>
						<td>立项日期:</td>
						<td>
							<input type="text" id="create_time" name="create_time" value="${create_time }" readonly="readonly" onclick="SelectDate(this,'yyyy-MM-dd',0,0)"/>
						</td>
						<td>项目状态:</td>
						<td>未提交<input type="hidden" name="status" value="1" id="project_status"></td>
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
							<textarea rows="5" cols="20" style="width: 90%; height: 100px;" id="desc" name="desc"></textarea>
						</td>
					</tr>
					<tr>
						<td>
							项目目标：<br />
							（质量、工期）
						</td>
						<td colspan="3">
							<textarea rows="5" cols="20" style="width: 90%; height: 100px;" id="target" name="target"></textarea>
						</td>
					</tr>
					<tr>
						<td>
							项目里程碑计划：<br />
							（时间、成果）
						</td>
						<td colspan="3">
							<textarea rows="5" cols="20" style="width: 90%; height: 100px;" id="milestone" name="milestone"></textarea>
						</td>
					</tr>
					<tr>
						<td>
							评价标准：
						</td>
						<td colspan="3">
							<textarea rows="5" cols="20" style="width: 90%; height: 100px;" id="evaluation_criterion" name="evaluation_criterion"></textarea>
						</td>
					</tr>
					<tr>
						<td>
							项目假定与约束条件：<br />
							（非必填）
						</td>
						<td colspan="3">
							<textarea rows="5" cols="20" style="width: 90%; height: 100px;" id="constraint_condition" name="constraint_condition"></textarea>
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
							<g:personsByDeptId name="analysts" deptId="010d"  defaultChecked="checkedAll" />
						</td>
					</tr>
					
					<tr>
						<td colspan="4" style="height: 30px;">
							<div class="project_line"></div>
						</td>
					</tr>
					
				</table>
					
				<div style="text-align: left;">项目主要利益干系人:</div>
				<div style="text-align: left;"><input type="button" value="增加" onclick="toAddPrivyTr()"></div>
				<table class="table table-bordered" id="privy_table"  style="width: 99%;margin-left: 2px;">
					<tr>
						<th>姓名</th>
						<th>类别</th>
						<th>部门</th>
						<th>职务</th>
						<th>操作</th>
					</tr>
				</table>
				
			</form>
				
			<div class="footer">
				<div style="float: left;margin-left: 10px;color: #fff;font-size: 20px;margin-top: 15px;"></div>
				<div style="float: right;margin-right: 10px;margin-top: 20px;">
					<button type="button" class="btn btn-primary btn-sm" style="width: 80px;" onclick="saveProjectInfo(1)">提交</button>
					<button type="button" class="btn btn-primary btn-sm" style="width: 80px;" onclick="saveProjectInfo(0)">保存</button>
					<button type="button" class="btn btn-primary btn-sm" style="width: 80px;" onclick="toCloseProject()">取消</button>
				</div>	
			</div>
			
		</div>	
</div>		
</body>
</html>
