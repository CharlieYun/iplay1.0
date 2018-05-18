<div class="container-fluid">
	<div class="row">
		<div class="col-md-12">
			<nav class="navbar navbar-default navbar-inverse" role="navigation">
				<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
					<div class="mt-one">
					<ul class="nav navbar-nav">
						<li class="dropdown">
							<a href="${ grailsApplication.config.grails.app.name}/hisThinkTank/index?type=1" class="dropdown-toggle">基础信息</a>
						</li>
					</ul>
					</div>
					<div class="mt-two">
					<ul class="nav navbar-nav">
						<li class="dropdown">
							<a href="${ grailsApplication.config.grails.app.name}/hisThinkTankCompare/index"  class="dropdown-toggle" >信息对比</a>
						</li>
					</ul>
					</div>
					<div class="mt-three">
					<ul class="nav navbar-nav">
						<li class="dropdown">
							<a href="${ grailsApplication.config.grails.app.name}/hisThinkTankAnalysis/index" class="dropdown-toggle" >交互筛选</a>
						</li>
					</ul>
					</div>
					
					<div class="mt-four">
					<ul class="nav navbar-nav">
						<li class="dropdown">
							<a href="${ grailsApplication.config.grails.app.name}/project/projectDynamic" class="dropdown-toggle" >数据导出</a>
						</li>
					</ul>
					</div>
					
					<div style="color: white;margin-left:750px;margin-top:15px;"><span onclick="toShow()" >您好，${session?.operationInfo?.name }
						<img src="http://192.168.0.108:8060/operation/operationPerson/getPersonImage?id=${session?.operationInfo?.id }" style="height: 40px;width:40px;margin-top: -7px;">
						</span><a href="${ grailsApplication.config.grails.app.name}/login/loginOut" style="color: thistle; margin-left:10px;">退出</a>
					</div>
				</div>
			</nav>
			<script type="text/javascript">
				var isShow=false;
				function toShow(){
					if(isShow){
						$(".toEditPerson").hide();
					}
					else{
						$(".toEditPerson").show();
					}
					isShow = !isShow;
				}
				function doUpdatePerson(){
					var that = this ;
					$.ajax({
						type:"post",
						url:projectURITemp.defcon+"/person/doUpdatePerson",
						data:{dept:$("#dept").val(),password:$("#password").val(),oldPassword:$("#oldPassword").val(),id:"${ session?.user?.id}"},
						success:function(msg){
								$("#oldPassword").val();
								$("#password").val();
								alert(msg);
								that.toShow();
						}
					});
				}
			</script>
			
			
			<div class="toEditPerson" style="display:none;">
				<div class="head">
					<div style="float: left;margin-left: 10px;color: #fff;font-size: 20px;margin-top: 5px;">
						${session?.operationInfo?.common_duties }
					</div>
					<div style="float: right;margin-right: 10px;margin-top: 3px;">
						<button type="button" class="btn btn-primary btn-sm" style="width: 80px;" onclick="doUpdatePerson()">保存</button>
						<button type="button" class="btn btn-primary btn-sm" style="width: 80px;" onclick="toShow()">关闭</button>
					</div>	
				</div>
				<div style="float: left; width: 120px;align: center;height: 162px;">
					<img src="http://192.168.0.108:8060/operation/operationPerson/getPersonImage?id=${session?.operationInfo?.id }" style="height: 100px;width:100px;margin-top: 31px;margin-left: 20px;">
				</div>
				<div style="float: right;margin-top: 5px; width: 365px;">
					<table style="width: 100%;margin-bottom: 10px;">
						<tr>
							<td style="width: 80px;text-align: center;">用户名：</td>
							<td style="font-size: 30px;font-weight: bold;">${session?.operationInfo?.name }</td>
						</tr>
						<tr style="height: 30px;">
							<td style="width: 120px;text-align: center;">所属部门：</td>
							<td><g:deptSelect name="dept" id="dept" value="${session?.operationInfo?.organization_id }" disabled="disabled"/></td>
						</tr>
						<tr style="height: 30px;">
							<td style="width: 120px;text-align: center;">用户邮箱：</td>
							<td>${session?.operationInfo?.email }</td>
						</tr>
						<tr style="height: 30px;">
							<td style="width: 120px;text-align: center;">原始密码：</td>
							<td><input type="password" id="oldPassword" value="">
							</td>
						</tr>
						<tr style="height: 30px;">
							<td style="width: 120px;text-align: center;">新密码：</td>
							<td><input type="password" id="password" value="">
							</td>
						</tr>
					</table>
				</div>
			</div>
		</div>
	</div>
</div>