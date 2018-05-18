<!DOCTYPE html>
<html>
<head>
<title>重跑请求</title>
<meta name="layout" content="mainWithOutTop" />
<script type="text/javascript">

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
				<button type="button" class="btn btn-primary btn-sm" style="width: 80px;" onclick="toCloseProject()">取消</button>
			</div>	
		</div>
		<div style="margin-top: 15px;text-align: center;">
				<table style="width: 100%;">
					<tr>
						<td style="width: 15%;">重跑申请日期：</td>
						<td  style="width: 35%;">
							<g:fmtDateTime value="${again.again_time }"/>
						</td>
						<td style="width: 15%;">&nbsp;</td>
						<td  style="width: 35%;">
							&nbsp;							
						</td>
					</tr>
					<tr>
						<td style="width: 15%;">重跑原因：</td>
						<td colspan="3">
						</td>
					</tr>
					<tr>
						<td colspan="4">
							<textarea style="width: 98%; margin-left:1%; height: 400px;" disabled="disabled" id="overrule_mes" name="overrule_mes">${again.again_mes }</textarea>
						</td>
					</tr>
				</table>
		</div>	
</div>		
</body>
</html>