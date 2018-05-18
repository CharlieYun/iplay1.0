var ids = [];
var names = [];
$(function(){
	toChangeRequirementContent()		//默认加载电视剧需求
	var objtye=$("#objType").val();
	//搜索框自动补全功能
	objectInfoAutoComplete('group_search',objtye,'imgAndNameAndYear',undefined,ids,function(id,name){
		ids.push(id);
		names.push(name);
		$("#group_search").val("");
		$("#group_search_id").append("<span style='border: 1px solid; padding: 5px;display: inline-block;margin: 0 5px 5px;' id="+id+">"+name+"&nbsp;<a onclick='teleplay_data_id_delete("+id+")' class='glyphicon glyphicon-remove'></a></span>");
    });
	ids.push($("#ides").val())
	names.push($("#namess").val())
});

//关闭窗口方法
function toCloseProjectRequirement(){
	this.close();
}
//默认加载跳转相应需求页面
function toChangeRequirementContent(){
	var projectRequirementId = $("#projectRequirementId").val();
	var uri = "ajaxGetTeleplayRequirement";
	var selectId = $("#objectType option:selected").val();
	if(selectId == 4){
		$("#objType").val("");//清空  
		$("#objType").val(4)
		uri = "ajaxGetMovieRequirementTemplate"
	}else if(selectId == 7){
		$("#objType").val("");//清空  
		$("#objType").val(7)
		uri = "ajaxGetStarRequirementTemplate"
	}else if(selectId == 10){
		uri = "ajaxGetSpecialRequirementTemplate"
	}else if(selectId == 680){	//综艺节目
		$("#objType").val("");//清空  
		$("#objType").val(680)
		uri = "ajaxGetRequirementVarietyShowTemplate"
	}else if(selectId == 5){	//电视剧
		$("#objType").val("");//清空  
		$("#objType").val(5)
		uri = "ajaxGetTeleplayRequirementTemplate"
	}
	$.ajax({
		type:"post",
		url:projectURI.defcon+"/projectRequirement/"+uri+"?viewAction=edit&projectRequirementId="+projectRequirementId,
		async:false,
		success:function(data){
			$("#teleplayRequirementDiv").html(data);
		}
	})
}

//需求对象变更事件
function toChangeRequirementContent1(){
	var projectRequirementId = $("#projectRequirementId").val();
	var uri = "ajaxGetTeleplayRequirement";
	var selectId = $("#objectType option:selected").val();
	$("#requirementName").val("")
	$("#begin_time").val("")
	$("#end_time").val("")
	$("#group_search").val("")
	if(ids!=""){
//		alert("不空")
//		alert(ids)
		var resultId=ids[0].split(",")
		var resultname=names[0].split(",")
		for(var i=0;i<resultId.length;i++){
			teleplay_data_id_delete(resultId[i],resultname[i])
		}
	}
	var selectId = $("#objectType option:selected").val();
	if(selectId == 4){
		$("#objType").val("");//清空  
		$("#objType").val(4)
		uri = "ajaxGetMovieRequirementTemplate"
	}else if(selectId == 7){
		$("#objType").val("");//清空  
		$("#objType").val(7)
		uri = "ajaxGetStarRequirementTemplate"
	}else if(selectId == 10){
		uri = "ajaxGetSpecialRequirementTemplate"
	}else if(selectId == 680){	//综艺节目
		$("#objType").val("");//清空  
		$("#objType").val(680)
		uri = "ajaxGetRequirementVarietyShowTemplate"
	}else if(selectId == 5){	//电视剧
		$("#objType").val("");//清空  
		$("#objType").val(5)
		uri = "ajaxGetTeleplayRequirementTemplate"
	}
	$.ajax({
		type:"post",
		url:projectURI.defcon+"/projectRequirement/"+uri+"?projectRequirementId="+projectRequirementId,
		async:false,
		success:function(data){
			$("#teleplayRequirementDiv").html(data);
			//搜索框自动补全功能
			objectInfoAutoComplete('group_search',selectId,'imgAndNameAndYear',undefined,ids,function(id,name){
		    	ids.push(id);
		    	names.push(name);
				$("#group_search").val("");
				$("#group_search_id").append("<span style='border: 1px solid; padding: 5px;display: inline-block;margin: 0 5px 5px;' id="+id+">"+name+"&nbsp;<a onclick='teleplay_data_id_delete("+id+")' class='glyphicon glyphicon-remove'></a></span>");
	   		});
		}
	})
}

function teleplay_data_id_delete(id,name){
	var indexId = ids.indexOf(id);
	ids.splice(indexId,1);
	names.splice(indexId,1);
	$("#"+id).remove();
}
var showFlag = false ;
function toJudgeIsCinema(){
	if($("input[hvalue='cinema']").is(':checked') && showFlag == false){
		$("#cinemaDiv").show();
		showFlag = true ;
	}
	else if($("input[hvalue='cinema']").is(':checked') == false){
		showFlag = false ;
		$("#cinemaDiv").hide();
	}
}

//保存需求信息0-暂存，2-进行中,3-已完成
function editProjectRequirementInfo(prStatue){
	
	if(!$("#requirementName").val()){
		alert("请输入需求名称!");
		$("#requirementName").focus();
		return false;
	}
	if(!$("#begin_time").val()){
		alert("请选择开始时间!");
		$("#begin_time").focus();
		return false;
	}
	if($("#begin_time").val() > $("#end_time").val() ){
		alert("开始时间不能大于结束时间");
		return false;
	}
	if(!$("#end_time").val()){
		alert("请选择结束时间!");
		$("#end_time").focus();
		return false;
	}
	
	var queryIds = "";
	for(var i=0;i<ids.length;i++){
		if(ids[i] != ""){
			queryIds += ids[i] + ","
		}
	}
	if(queryIds != ""){
		queryIds = queryIds.substring(0,queryIds.length-1)
	}
	
	var selectId = $("#objectType option:selected").val();
	$("#objectIds").val(queryIds);
	if(selectId == 10){
		$("#requirement_state").val(prStatue==0?0:1);
		updateProjectRequirementTemplateInfoSpecial();
		return ;
	}
	else if(selectId == 7 || selectId == 4){
		if(ids.length == 0){
			alert("请添加数据类型信息")
			return false;
		}
		$("#requirement_state").val(prStatue);
		updateProjectRequirementTemplateInfoStar();
		return ;
	}
	
	if(ids.length == 0){
		alert("请添加数据类型信息")
		return false;
	}
	
	var objtye= $("#objectType option:selected").val();
	if(objtye==5){	//电视剧
		var requirement='0'	//用于判断是否勾选需求信息 0-未勾选，1已勾选
		//电视剧35城收视率
		var values35="";
		var valuesIds35="";
		$("input[name='ratings35']:checked").each(function(){
			var temp=new Array()
			temp=$(this).val().split("|")
			var value=temp[0]
			var valueid=temp[1]
			values35+=value+","
			valuesIds35+=valueid+","
			requirement='1'
		})
		values35=values35.substring(0, values35.length-1)
		valuesIds35=valuesIds35.substring(0, valuesIds35.length-1)
		
		//电视剧50城收视率
		var values50="";
		var valuesIds50="";
		$("input[name='ratings50']:checked").each(function(){
			var temp=new Array()
			temp=$(this).val().split("|")
			var value=temp[0]
			var valueid=temp[1]
			values50+=value+","
			valuesIds50+=valueid+","
			requirement='1'
		})
		values50=values50.substring(0, values50.length-1)
		valuesIds50=valuesIds50.substring(0, valuesIds50.length-1)
		
		//电视剧市场份额
		var valuesPortion="";
		var valuesIdsPortion="";
		$("input[name='teleplayPortion']:checked").each(function(){
			var temp=new Array()
			temp=$(this).val().split("|")
			var value=temp[0]
			var valueid=temp[1]
			valuesPortion+=value+","
			valuesIdsPortion+=valueid+","
			requirement='1'
		})
		valuesPortion=valuesPortion.substring(0, valuesPortion.length-1)
		valuesIdsPortion=valuesIdsPortion.substring(0, valuesIdsPortion.length-1)
		//电视观众构成
		//if(document.getElementById("chkThree").checked){
			
		//}
		//网络播放量
		var valuesAmount="";
		var valuesIdsAmount="";
		$("input[name='teleplayAmount']:checked").each(function(){
			var temp=new Array()
			temp=$(this).val().split("|")
			var value=temp[0]
			var valueid=temp[1]
			valuesAmount+=value+","
			valuesIdsAmount+=valueid+","
			requirement='1'
		})
		valuesAmount=valuesAmount.substring(0, valuesAmount.length-1)
		valuesIdsAmount=valuesIdsAmount.substring(0, valuesIdsAmount.length-1)
		//影响力
		var valuesInfluence="";
		var valuesIdsInfluence="";
		$("input[name='influence']:checked").each(function(){
			var temp=new Array()
			temp=$(this).val().split("|")
			var value=temp[0]
			var valueid=temp[1]
			valuesInfluence+=value+","
			valuesIdsInfluence+=valueid+","
			requirement='1'
		})
		valuesInfluence=valuesInfluence.substring(0, valuesInfluence.length-1)
		valuesIdsInfluence=valuesIdsInfluence.substring(0, valuesIdsInfluence.length-1)
		//口碑评估
		var valuesReputation="";
		var valuesIdsReputation="";
		$("input[name='reputation']:checked").each(function(){
			var temp=new Array()
			temp=$(this).val().split("|")
			var value=temp[0]
			var valueid=temp[1]
			valuesReputation+=value+","
			valuesIdsReputation+=valueid+","
			requirement='1'
		})
		valuesReputation=valuesReputation.substring(0, valuesReputation.length-1)
		valuesIdsReputation=valuesIdsReputation.substring(0, valuesIdsReputation.length-1)
		//主创及表演评估
			var valuesWritten="";
			var valuesIdsWritten="";
			$("input[name='written']:checked").each(function(){
				var temp=new Array()
				temp=$(this).val().split("|")
				var value=temp[0]
				var valueid=temp[1]
				valuesWritten+=value+","
				valuesIdsWritten+=valueid+","
				requirement='1'
			})
			valuesWritten=valuesWritten.substring(0, valuesWritten.length-1)
			valuesIdsWritten=valuesIdsWritten.substring(0, valuesIdsWritten.length-1)
		if(requirement=='0'){
			alert("请勾选要查询的数据需求信息")
			return false;
		}
		
		//需求名称
		var requirementName=$("#requirementName").val()
//			需求开始时间
		var begin_time=$("#begin_time").val()
//			需求结束时间
		var end_time=$("#end_time").val()
		//查询对象类型 2-电视剧，4-电影，6-综艺，8-明星
		var objectType=$('select[name=objectType]').val()
		var projectRequirementId=$("#projectRequirementId").val()
		$.ajax({
			type:"post",
			url:projectURI.defcon+"/projectRequirement/editProjectRequirementTemplateInfo",
			data: {queryIds:queryIds,requirementName:requirementName,begin_time:begin_time,end_time:end_time,objectType:objectType,prStatue:prStatue,valuesPortion:valuesPortion,valuesIdsPortion:valuesIdsPortion,valuesAmount:valuesAmount,valuesIdsAmount:valuesIdsAmount,valuesInfluence:valuesInfluence,valuesIdsInfluence:valuesIdsInfluence,valuesReputation:valuesReputation,valuesIdsReputation:valuesIdsReputation,valuesWritten:valuesWritten,valuesIdsWritten:valuesIdsWritten,projectRequirementId:projectRequirementId,objtye:objtye,values35:values35,valuesIds35:valuesIds35,values50:values50,valuesIds50:valuesIds50},
			success:function(msg){
				alert(msg);
				window.opener.location.reload();
				toCloseProjectRequirement()
			}
		});
	}
	if(objtye==680){		//综艺
		//综艺收视率
		var values="";
		var valuesIds="";
		$("input[name='teleplayRequire']:checked").each(function(){
			var temp=new Array()
			temp=$(this).val().split("|")
			var value=temp[0]
			var valueid=temp[1]
			values+=value+","
			valuesIds+=valueid+","
			requirement='1'
		})
		values=values.substring(0, values.length-1)
		valuesIds=valuesIds.substring(0, valuesIds.length-1)
		
		//综艺市场份额
		var valuesPortion="";
		var valuesIdsPortion="";
		$("input[name='teleplayPortion']:checked").each(function(){
			var temp=new Array()
			temp=$(this).val().split("|")
			var value=temp[0]
			var valueid=temp[1]
			valuesPortion+=value+","
			valuesIdsPortion+=valueid+","
			requirement='1'
		})
		valuesPortion=valuesPortion.substring(0, valuesPortion.length-1)
		valuesIdsPortion=valuesIdsPortion.substring(0, valuesIdsPortion.length-1)
		
		//电视观众构成
		
		//网络播放量
		var valuesAmount="";
		var valuesIdsAmount="";
		$("input[name='entertainmentAmount']:checked").each(function(){
			var temp=new Array()
			temp=$(this).val().split("|")
			var value=temp[0]
			var valueid=temp[1]
			valuesAmount+=value+","
			valuesIdsAmount+=valueid+","
			requirement='1'
		})
		valuesAmount=valuesAmount.substring(0, valuesAmount.length-1)
		valuesIdsAmount=valuesIdsAmount.substring(0, valuesIdsAmount.length-1)
		
		//影响力
		var valuesInfluence="";
		var valuesIdsInfluence="";
		$("input[name='influence']:checked").each(function(){
			var temp=new Array()
			temp=$(this).val().split("|")
			var value=temp[0]
			var valueid=temp[1]
			valuesInfluence+=value+","
			valuesIdsInfluence+=valueid+","
			requirement='1'
		})
		valuesInfluence=valuesInfluence.substring(0, valuesInfluence.length-1)
		valuesIdsInfluence=valuesIdsInfluence.substring(0, valuesIdsInfluence.length-1)
		//口碑评估
		var valuesReputation="";
		var valuesIdsReputation="";
		$("input[name='reputation']:checked").each(function(){
			var temp=new Array()
			temp=$(this).val().split("|")
			var value=temp[0]
			var valueid=temp[1]
			valuesReputation+=value+","
			valuesIdsReputation+=valueid+","
			requirement='1'
		})
		valuesReputation=valuesReputation.substring(0, valuesReputation.length-1)
		valuesIdsReputation=valuesIdsReputation.substring(0, valuesIdsReputation.length-1)
		//主创及表演评估
		var valuesWritten="";
		var valuesIdsWritten="";
		$("input[name='entertainmentWritten']:checked").each(function(){
			var temp=new Array()
			temp=$(this).val().split("|")
			var value=temp[0]
			var valueid=temp[1]
			valuesWritten+=value+","
			valuesIdsWritten+=valueid+","
			requirement='1'
		})
		valuesWritten=valuesWritten.substring(0, valuesWritten.length-1)
		valuesIdsWritten=valuesIdsWritten.substring(0, valuesIdsWritten.length-1)
		
		if(requirement=='0'){
			alert("请勾选要查询的数据需求信息")
			return false;
		}
		
		//需求名称
		var requirementName=$("#requirementName").val()
//				需求开始时间
		var begin_time=$("#begin_time").val()
//				需求结束时间
		var end_time=$("#end_time").val()
		//查询对象类型 2-电视剧，4-电影，6-综艺，8-明星
		var objectType=$('select[name=objectType]').val()
		var projectRequirementId=$("#projectRequirementId").val()
		$.ajax({
			type:"post",
			url:projectURI.defcon+"/projectRequirement/editProjectRequirementTemplateInfo",
			data: {queryIds:queryIds,requirementName:requirementName,begin_time:begin_time,end_time:end_time,objectType:objectType,values:values,valuesIds:valuesIds,prStatue:prStatue,valuesPortion:valuesPortion,valuesIdsPortion:valuesIdsPortion,valuesAmount:valuesAmount,valuesIdsAmount:valuesIdsAmount,valuesInfluence:valuesInfluence,valuesIdsInfluence:valuesIdsInfluence,valuesReputation:valuesReputation,valuesIdsReputation:valuesIdsReputation,valuesWritten:valuesWritten,valuesIdsWritten:valuesIdsWritten,objtye:objtye,projectRequirementId:projectRequirementId},
			success:function(msg){
				alert(msg);
				window.opener.location.reload();
				toCloseProjectRequirement()
			}
		});
	}
}

function updateProjectRequirementTemplateInfoSpecial(){
	$.ajax({
		type:"post",
		url:projectURI.defcon+"/projectRequirement/updateProjectRequirementTemplateInfoSpecial",
		data: $("#myForm").serialize(),   
		success:function(msg){
			alert(msg);
			window.opener.location.reload();
			toCloseProjectRequirement()
		}
	});
	
}

function updateProjectRequirementTemplateInfoStar(){
	$.ajax({
		type:"post",
		url:projectURI.defcon+"/projectRequirement/updateProjectRequirementTemplateInfoStar",
		data: $("#myForm").serialize(),   
		success:function(msg){
			alert(msg);
			window.opener.location.reload();
			toCloseProjectRequirement()
		}
	});
}