var ids = [];
var names = [];
$(function(){
		ajaxGetTeleplayRequirement()		//默认加载电视剧需求
		var objtye=$("#objType").val();
		//搜索框自动补全功能
		objectInfoAutoComplete('group_search',objtye,'imgAndNameAndYear',undefined,ids,function(id,name){
    	ids.push(id);
    	names.push(name);
		$("#group_search").val("");
		$("#group_search_id").append("<span style='border: 1px solid; padding: 5px;display: inline-block;margin: 0 5px 5px;' id="+id+">"+name+"&nbsp;<a onclick='teleplay_data_id_delete("+id+")' class='glyphicon glyphicon-remove'></a></span>");
    });
});

function teleplay_data_id_delete(id){
	var indexId = ids.indexOf(id);
	ids.splice(indexId,1);
	names.splice(indexId,1);
	$("#"+id).remove();
}

//用户电视收视率是否显示需求信息方法
function teleplaySSLTF(chkId,trId){
	if(document.getElementById(chkId).checked){
		document.getElementById(trId).style.display=""; 
	}else{
		document.getElementById(trId).style.display="none"; 
	}
}

//加载电视剧需求信息
function ajaxGetTeleplayRequirement(){
	$.ajax({
		type:"post",
		url:projectURI.defcon+"/projectRequirement/ajaxGetTeleplayRequirement/",
		async:false,
		success:function(data){
			$("#objType").val(5)
			$("#requirementDiv").html(data);
		}
	})
}

//关闭窗口方法
function toCloseProjectRequirement(){
	this.close();
}

function saveProjectRequirementInfoSpecial(){
	$.ajax({
		type:"post",
		url:projectURI.defcon+"/projectRequirement/saveProjectRequirementInfoSpecial",
		data: $("#myForm").serialize(),   
		success:function(msg){
			alert(msg);
			window.opener.location.reload();
			toCloseProjectRequirement()
		}
	});
	
}

function saveProjectRequirementInfoStar(){
	$.ajax({
		type:"post",
		url:projectURI.defcon+"/projectRequirement/saveProjectRequirementInfoStar",
		data: $("#myForm").serialize(),   
		success:function(msg){
			alert(msg);
			window.opener.location.reload();
			toCloseProjectRequirement()
		}
	});
}


//保存需求信息0-暂存，2-进行中,3-已完成
function saveProjectRequirementInfo(prStatue){
	
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
		saveProjectRequirementInfoSpecial();
		return ;
	}
	else if(selectId == 7 || selectId == 4){
		if(ids.length == 0){
			alert("请添加数据类型信息")
			return false;
		}
		$("#requirement_state").val(prStatue);
		saveProjectRequirementInfoStar();
		return ;
	}
	
	if(ids.length == 0){
		alert("请添加数据类型信息")
		return false;
	}
	
	var objtye=$("#objType").val();
	if(objtye==5){	//电视剧
		var requirement='0'	//用于判断是否勾选需求信息 0-未勾选，1已勾选
		//电视剧35城收视率
		var rating35="";
		// 电视剧50城收视率
		var rating50="";
		$("input[name='ratings']:checked").each(function(){
			var temp=new Array()
			temp=$(this).val().split("|")
			var value=temp[0]
			if(value=='35'){
				rating35 = temp[1]
			}
			else{
				rating50 = temp[1]
			}
			requirement='1'
		})
		
		//电视剧35城市场份额
		var portion35="";
		//电视剧50城市场份额
		var portion50="";
		$("input[name='portion']:checked").each(function(){
			var temp=new Array()
			temp=$(this).val().split("|")
			var value=temp[0]
			if(value=='35'){
				portion35 = temp[1]
			}
			else{
				portion50 = temp[1]
			}
			requirement='1'
		})
		
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
		
		//主创及表演评估-导演
		var valuesdirection="";
		var valuesIdsdirection="";
		$("input[name='direction']:checked").each(function(){
			var temp=new Array()
			temp=$(this).val().split("|")
			var value=temp[0]
			var valueid=temp[1]
			valuesdirection+=value+","
			valuesIdsdirection+=valueid+","
			requirement='1'
		})
//		valuesWritten=valuesWritten.substring(0, valuesWritten.length-1)
//		valuesIdsWritten=valuesIdsWritten.substring(0, valuesIdsWritten.length-1)
		valuesdirection=valuesdirection.substring(0, valuesdirection.length-1)
		valuesIdsdirection=valuesIdsdirection.substring(0, valuesIdsdirection.length-1)
		
		//主创及表演评估-编剧
		var valuesscriptwriter="";
		var valuesIdsscriptwriter="";
		$("input[name='scriptwriter']:checked").each(function(){
			var temp=new Array()
			temp=$(this).val().split("|")
			var value=temp[0]
			var valueid=temp[1]
			valuesscriptwriter+=value+","
			valuesIdsscriptwriter+=valueid+","
			requirement='1'
		})
//		valuesWritten=valuesWritten.substring(0, valuesWritten.length-1)
//		valuesIdsWritten=valuesIdsWritten.substring(0, valuesIdsWritten.length-1)
		valuesscriptwriter=valuesscriptwriter.substring(0, valuesscriptwriter.length-1)
		valuesIdsscriptwriter=valuesIdsscriptwriter.substring(0, valuesIdsscriptwriter.length-1)
		
		//主创及表演评估-演员
		var valuescomedienne="";
		var valuesIdscomedienne="";
		$("input[name='comedienne']:checked").each(function(){
			var temp=new Array()
			temp=$(this).val().split("|")
			var value=temp[0]
			var valueid=temp[1]
			valuescomedienne+=value+","
			valuesIdscomedienne+=valueid+","
			requirement='1'
		})
//		valuesWritten=valuesWritten.substring(0, valuesWritten.length-1)
//		valuesIdsWritten=valuesIdsWritten.substring(0, valuesIdsWritten.length-1)
		valuescomedienne=valuescomedienne.substring(0, valuescomedienne.length-1)
		valuesIdscomedienne=valuesIdscomedienne.substring(0, valuesIdscomedienne.length-1)
		
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
		
		var projectID=$("#project_id").val()
		
		$.ajax({
			type:"post",
			url:projectURI.defcon+"/projectRequirement/saveProjectRequirementInfo",
			
			data: {queryIds:queryIds,requirementName:requirementName,begin_time:begin_time,end_time:end_time,objectType:objectType,prStatue:prStatue,valuesAmount:valuesAmount,valuesIdsAmount:valuesIdsAmount,valuesInfluence:valuesInfluence,valuesIdsInfluence:valuesIdsInfluence,valuesReputation:valuesReputation,valuesIdsReputation:valuesIdsReputation,projectID:projectID,objtye:objtye,rating35:rating35,rating50:rating50,portion35:portion35,portion50:portion50,valuesdirection:valuesdirection,valuesIdsdirection:valuesIdsdirection,valuesscriptwriter:valuesscriptwriter,valuesIdsscriptwriter:valuesIdsscriptwriter,valuescomedienne:valuescomedienne,valuesIdscomedienne:valuesIdscomedienne},
			success:function(msg){
				alert(msg);
				window.opener.location.reload();
				toCloseProjectRequirement()
			}
		});
	}
	if(objtye==680){		//综艺
		//电视剧35城收视率
		var rating35="";
		// 电视剧50城收视率
		var rating50="";
		$("input[name='ratings']:checked").each(function(){
			var temp=new Array()
			temp=$(this).val().split("|")
			var value=temp[0]
			if(value=='35'){
				rating35 = temp[1]
			}
			else{
				rating50 = temp[1]
			}
			requirement='1'
		})
		
		//电视剧35城市场份额
		var portion35="";
		//电视剧50城市场份额
		var portion50="";
		$("input[name='portion']:checked").each(function(){
			var temp=new Array()
			temp=$(this).val().split("|")
			var value=temp[0]
			if(value=='35'){
				portion35 = temp[1]
			}
			else{
				portion50 = temp[1]
			}
			requirement='1'
		})
		
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
		
		//主创及表演评估 -主持人
		var valueshoster="";
		var valuesIdshoster="";
		$("input[name='hoster']:checked").each(function(){
			var temp=new Array()
			temp=$(this).val().split("|")
			var value=temp[0]
			var valueid=temp[1]
			valueshoster+=value+","
			valuesIdshoster+=valueid+","
			requirement='1'
		})
		valueshoster=valueshoster.substring(0, valueshoster.length-1)
		valuesIdshoster=valuesIdshoster.substring(0, valuesIdshoster.length-1)
		
		//主创及表演评估 -嘉宾
		var valuesguester="";
		var valuesIdsguester="";
		$("input[name='guester']:checked").each(function(){
			var temp=new Array()
			temp=$(this).val().split("|")
			var value=temp[0]
			var valueid=temp[1]
			valuesguester+=value+","
			valuesIdsguester+=valueid+","
			requirement='1'
		})
		valuesguester=valuesguester.substring(0, valuesguester.length-1)
		valuesIdsguester=valuesIdsguester.substring(0, valuesIdsguester.length-1)
		
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
		
		var projectID=$("#project_id").val()
		$.ajax({
			type:"post",
			url:projectURI.defcon+"/projectRequirement/saveProjectRequirementInfo",
			data: {queryIds:queryIds,requirementName:requirementName,begin_time:begin_time,end_time:end_time,objectType:objectType,prStatue:prStatue,rating35:rating35,rating50:rating50,portion35:portion35,portion50:portion50,valuesAmount:valuesAmount,valuesIdsAmount:valuesIdsAmount,valuesInfluence:valuesInfluence,valuesIdsInfluence:valuesIdsInfluence,valuesReputation:valuesReputation,valuesIdsReputation:valuesIdsReputation,projectID:projectID,objtye:objtye,valueshoster:valueshoster,valuesIdshoster:valuesIdshoster,valuesguester:valuesguester,valuesIdsguester:valuesIdsguester},
			success:function(msg){
				alert(msg);
				window.opener.location.reload();
				toCloseProjectRequirement()
			}
		});
	}
}

function toChangeRequirementContent(){
	var uri = "";
	var selectId = $("#objectType option:selected").val();
	if(selectId == 4){
		$("#objType").val("");//清空  
		$("#objType").val(4)
		uri = "ajaxGetMovieRequirement"
	}else if(selectId == 7){
		$("#objType").val("");//清空  
		$("#objType").val(7)
		uri = "ajaxGetStarRequirement"
	}else if(selectId == 10){
		uri = "ajaxGetSpecialRequirement"
	}else if(selectId == 680){	//综艺节目
		$("#objType").val("");//清空  
		$("#objType").val(680)
		uri = "ajaxGetRequirementVarietyShow"
	}else if(selectId == 5){	//电视剧
		$("#objType").val("");//清空  
		$("#objType").val(5)
		uri = "ajaxGetTeleplayRequirement"
	}
	$.ajax({
		type:"post",
		url:projectURI.defcon+"/projectRequirement/"+uri,
		async:false,
		success:function(data){
			$("#requirementDiv").html(data);
			objtye=$("#objType").val();
			//搜索框自动补全功能
			objectInfoAutoComplete('group_search',objtye,'imgAndNameAndYear',undefined,ids,function(id,name){
		    	ids.push(id);
		    	names.push(name);
				$("#group_search").val("");
				$("#group_search_id").append("<span style='border: 1px solid; padding: 5px;display: inline-block;margin: 0 5px 5px;' id="+id+">"+name+"&nbsp;<a onclick='teleplay_data_id_delete("+id+")' class='glyphicon glyphicon-remove'></a></span>");
	   		});
		}
	})
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
