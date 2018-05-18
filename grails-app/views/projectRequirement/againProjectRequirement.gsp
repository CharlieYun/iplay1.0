<!DOCTYPE html>
<html>
<head>
<title>驳回请求</title>
<meta name="layout" content="mainWithOutTop" />
<script type="text/javascript">

function upload(){
	$.ajax({
		type:"post",
		url:projectURI.defcon+"/projectRequirement/doAgainProjectRequirement",
		data: $("#myForm").serialize(),   
		success:function(msg){
			alert(msg);
			window.opener.location.reload();
			toCloseProject();
		}
	});
}

function toCloseProject(){

	this.close();
}


</script>


</head>
<body>
<div class="toEditProject" >
		<div class="head">
			<div style="float: left;margin-left: 10px;color: #fff;font-size: 20px;margin-top: 15px;">重跑数据需求</div>
			<div style="float: right;margin-right: 10px;margin-top: 10px;">
				<button type="button" class="btn btn-primary btn-sm" style="width: 80px;" onclick="upload()">确定</button>
				<button type="button" class="btn btn-primary btn-sm" style="width: 80px;" onclick="toCloseProject()">取消</button>
			</div>	
		</div>
		<div style="margin-top: 15px;text-align: center;">
			<form  id="myForm" method="post" >
				<div style="width: 100%; margin-left:20px; text-align: left;">重跑原因:</div><br/>
				<textarea style="width: 98%; margin-left:1%; height: 400px;" id="again_mes" name="again_mes"></textarea>
				<input type="hidden" name="requirement_id" value="${params.projectRequirementId }" />
			</form>
		</div>	
</div>		
</body>
</html>