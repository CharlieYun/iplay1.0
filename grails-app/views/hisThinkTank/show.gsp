<!DOCTYPE html>
<html>
<head>
<title>基础信息</title>
<meta name="layout" content="main"/>
<script src="${grailsApplication.config.grails.app.name}/js/autoComplete/autoComplete.js" ></script>

<script>
var objectId=0
var ttype=${params.type }
var objectType=${params.objectType }
var t35List=${params.object.teleAudienceMap35}
var p =${params.object.p}
var hrList =${params.object.hrList}
var boList=${params.object.boList}
var networkDramaPlayList=${params.object.networkDramaPlayMap}
$(function(){
	objectInfoAutoComplete('group_search'+ttype,objectType,'imgAndNameAndYear',15,undefined,function(id){
		objectId=id
		location.href="${ grailsApplication.config.grails.app.name}/hisThinkTank/show/"+objectId+"?type="+ttype+"&objectType="+objectType
    });
});

function searchObject1(){
	var ttype=${params.type }
	if(objectId==0){
		$("#group_search1").val("抱歉！！您输入的电视剧不存在，请重新输入！")
		$("#group_search1").focus()
		return false;
	}else{
		location.href="${ grailsApplication.config.grails.app.name}/hisThinkTank/show/"+objectId+"?type="+ttype
	}
}

function searchObject2(){
	var ttype=${params.type }
	if(objectId==0){
		$("#group_search2").val("抱歉！！您输入的网剧不存在，请重新输入！")
		$("#group_search2").focus()
		return false;
	}else{
		location.href="${ grailsApplication.config.grails.app.name}/hisThinkTank/show/"+objectId+"?type="+ttype
	}
}

function searchObject3(){
	var ttype=${params.type }
	if(objectId==0){
		$("#group_search3").val("抱歉！！您输入的电影不存在，请重新输入！")
		$("#group_search3").focus()
		return false;
	}else{
		location.href="${ grailsApplication.config.grails.app.name}/hisThinkTank/show/"+objectId+"?type="+ttype
	}
}

/**
 * 显示或隐藏更多演员信息
 */
function showOrHideMoreActorInfo(obj,type){
	var tbodyTag = $(obj).parents("tbody")
	if(type == "show"){
		tbodyTag.find(".actors").show()
		$(obj).attr("onclick","showOrHideMoreActorInfo(this,'hide')").find("i").removeClass("glyphicon-chevron-down").addClass("glyphicon-chevron-up")
	}else if(type == "hide"){
		tbodyTag.find(".actors:gt(4)").hide()
		$(obj).attr("onclick","showOrHideMoreActorInfo(this,'show')").find("i").removeClass("glyphicon-chevron-up").addClass("glyphicon-chevron-down")
	}
}

</script>

</head>
<body>
	<input type="hidden" id="typ" value="${params.type }">
	<input type="hidden" id="boLis" value="${params.object.boList}">
	
 	<g:if test="${params.type=="1" }"><!-- 1电视剧 -->
 	
 	<div class="marketdataMiddle">
 	<div class="top_header">
		<div class="tabbable" id="tabs-136492">
			<ul class="nav nav-tabs">
				<li class="${params.active }">
					<a href="${ grailsApplication.config.grails.app.name}/hisThinkTank/index?type=1" data-toggle="modal">电视剧</a>
				</li >
				<li class="">
					<a href="${ grailsApplication.config.grails.app.name}/hisThinkTank/index?type=2" data-toggle="modal">网剧</a>
				</li>
				<li class="">
					<a href="${ grailsApplication.config.grails.app.name}/hisThinkTank/index?type=3" data-toggle="modal">电影</a>
				</li>
			</ul>
			<div class="tab-content">
				<div class="${ttpActive }" id="${teleplayPanel }">
					<form class="navbar-form navbar-left"  role="search">
						<div class="form-group">
							<input class="form-control" id="group_search1" type="text" style="width: 700px;" onkeypress="if(event.keyCode==13) {btn1.click();return false;}">
						</div> 
						<button type="button" id="btn1" class="btn btn-default" onclick="searchObject1()" >
							搜索
						</button>
						<button type="button" class="btn btn-link" onclick="window.location.href='${grailsApplication.config.grails.app.name}/hisThinkTankCompare/index'">
							PK
						</button>
					</form>
				</div>
			</div>
		</div>
		
		<div class="header_left">
		<div class="box col-md-12">
			<table class="table table-striped table-bordered bootstrap-datatable responsive" id="infoTable">
				<tr>
					<td style="font-weight: bold;">剧名</td>
					<td>${params.object?.name }</td>
					<td style="font-weight: bold;">分类</td>
					<td>${params.object?.themeName }</td>
				</tr>
				<tr>
					<td style="font-weight: bold;">IP</td>
					<td>${params.object?.ipName }</td>
					<td style="font-weight: bold;">属性</td>
					<td>${params.object?.teleplayAttributes }</td>
				</tr>
				<tr>
					<td style="font-weight: bold;">网络播放平台</td>
					<td>${params.object?.siteName }</td>
					<td style="font-weight: bold;">网络播出方式</td>
					<td>${params.object?.broadMode }</td>
				</tr>
				<tr>
					<td style="font-weight: bold;">电视上星首播平台</td>
					<td colspan="3">${params.object?.channelName }</td>
				</tr>
				<tr>
					<td style="font-weight: bold;">电视上星首播时间</td>
					<!-- <td>${params.object?.channelPremiereData }</td> -->
					<td>${params.object?.publishTime }</td>
					<td style="font-weight: bold;">制片成本</td>
					<td>
					<g:if test="${params.object?.productionCost=='0' }">——
					</g:if>
					<g:if test="${params.object?.productionCost!='0' }">${params.object?.productionCost }
					</g:if>
					</td>
				</tr>
				<tr>
					<td style="font-weight: bold;">出品公司</td>
					<td>${params.object?.organ21Name }</td>
					<td style="font-weight: bold;">发行公司</td>
					<td>${params.object?.organ20Name }</td>
				</tr>
				<tr>
					<td style="font-weight: bold;">导演</td>
					<td>${params.object?.director }</td>
					<td style="font-weight: bold;">编剧</td>
					<td>${params.object?.scriptwriter }</td>
				</tr>
				<tr>
					<td style="font-weight: bold;">演员</td>
					<td style="font-weight: bold;">角色名称</td>
					<td style="font-weight: bold;">角色戏份</td>
					<td style="font-weight: bold;">角色描述</td>
				</tr>
				<g:each in="${params.object?.artistList}" status="i" var="it">
					<tr style="<g:if test="${i>4 }">display:none</g:if>" class="actors">
						<td>${it?.aName }</td>
						<td>${it?.bName }</td>
						<td>${it?.cName }</td>
						<td>
							<textarea rows="2" cols="58" style="resize:none;" disabled="disabled">${it?.description }</textarea>
						</td>
					</tr>
				</g:each>
				<g:if test="${params.object?.artistList?.size() > 5 }">
					<tr>
						<td colspan="4" style="text-align: center;">
							<a href="javascript:void(0)" onclick="showOrHideMoreActorInfo(this,'show')">演员信息<i class="glyphicon glyphicon-chevron-down"></i></a>
						</td>
					</tr>
				</g:if>
			</table>
			 
			<!--<div class="container-fluid">
				<div class="row">
					<div class="col-md-12">
						<ul class="nav navbar-nav">
							<li class="dropdown">
								 <a href="#" class="dropdown-toggle" data-toggle="dropdown">收视率</a>
							</li>
						</ul>
						<ul class="nav navbar-nav">
							<li class="dropdown">
								 <a href="#" class="dropdown-toggle" data-toggle="dropdown">播放量</a>
							</li>
						</ul> 
					</div>
				</div>
			</div>-->
		</div>
		</div>
		<div class="header_right">
			<img src="${ grailsApplication.config.grails.app.imgpath}${params.object?.img } " style="height: 200px;width:150px" >
			<div class="hr-font">好评：${params.object?.positivePercent }</div>
		</div>
	</div>
	</div>
	<div style="width:85%;font-weight: bold;font-size:130%;">电视剧收视率</div>
	<div id="telAudienceRating" style="height:350px;width:85%;display: inline-block;"></div>
	<!-- <div style="width:85%;font-weight: bold;font-size:130%;">电视剧播放量</div>
	<div id="1" style="height:350px;width:85%;display: inline-block;"></div> -->
	<div style="width:85%;font-weight: bold;font-size:130%;">电视剧好评率</div>
	<div id="teleplayPraise" style="height:350px;width:85%;display: inline-block;"></div>
	<div style="width:85%;font-weight: bold;font-size:130%;">电视剧媒体关注度</div>
    <div id="hotRate" style="height:350px;width:85%;display: inline-block;"></div>
    <div style="width:85%;font-weight: bold;font-size:130%;">电视剧公众影响力</div>
    <div id="publicInfluence" style="height:350px;width:85%;display: inline-block;"></div>
 	</g:if>
 	
 	<g:if test="${params.type=="2" }"><!-- 2网剧 -->
 	<script>
 		
 	</script>
 	<div class="marketdataMiddle">
 	<div class="top_header">
		<div class="tabbable" id="tabs-136492">
			<ul class="nav nav-tabs">
				<li class="">
					<a href="${ grailsApplication.config.grails.app.name}/hisThinkTank/index?type=1" data-toggle="modal">电视剧</a>
				</li >
				<li class="${params.active }">
					<a href="${ grailsApplication.config.grails.app.name}/hisThinkTank/index?type=2" data-toggle="modal">网剧</a>
				</li>
				<li class="">
					<a href="${ grailsApplication.config.grails.app.name}/hisThinkTank/index?type=3" data-toggle="modal">电影</a>
				</li>
			</ul>
			<div class="tab-content">
				<div class="${ntpActive }" id="${networkPanel }">
					<form class="navbar-form navbar-left" role="search">
						<div class="form-group">
							<input class="form-control" id="group_search2" type="text" style="width: 700px;" onkeypress="if(event.keyCode==13) {btn2.click();return false;}">
						</div> 
						<button type="button" id="btn2" class="btn btn-default" onclick="searchObject2()">
							搜索
						</button>
						<button type="button" class="btn btn-link" onclick="window.location.href='${grailsApplication.config.grails.app.name}/hisThinkTankCompare/index'">
							PK
						</button>
					</form>
				</div>
			</div>
		</div>
		
		<div class="header_left">
		<div class="box col-md-12">
			<table class="table table-striped table-bordered bootstrap-datatable responsive" id="infoTable">
				<tr>
					<td style="font-weight: bold;">剧名</td>
					<td>${params.object?.name }</td>
					<td style="font-weight: bold;">分类</td>
					<td>${params.object?.themeName }</td>
				</tr>
				<tr>
					<td style="font-weight: bold;">IP</td>
					<td>${params.object?.ipName }</td>
					<td style="font-weight: bold;">属性</td>
					<td>${params.object?.teleplayAttributes }</td>
				</tr>
				<tr>
					<td style="font-weight: bold;">网络播放平台</td>
					<td>${params.object?.siteName }</td>
					<td style="font-weight: bold;">网络播出方式</td>
					<td>${params.object?.broadMode }</td>
				</tr>
				<tr>
					<td style="font-weight: bold;">电视上星首播平台</td>
					<td colspan="3">${params.object?.channelName }</td>
				</tr>
				<tr>
					<td style="font-weight: bold;">电视上星首播时间</td>
					<td>${params.object?.channelPremiereData }</td>
					<td style="font-weight: bold;">制片成本</td>
					<td>${params.object?.productionCost }</td>
				</tr>
				<tr>
					<td style="font-weight: bold;">出品公司</td>
					<td>${params.object?.organ21Name }</td>
					<td style="font-weight: bold;">发行公司</td>
					<td>${params.object?.organ20Name }</td>
				</tr>
				<tr>
					<td style="font-weight: bold;">导演</td>
					<td>${params.object?.director }</td>
					<td style="font-weight: bold;">编剧</td>
					<td>${params.object?.scriptwriter }</td>
				</tr>
				<tr>
					<td style="font-weight: bold;">演员</td>
					<td style="font-weight: bold;">角色名称</td>
					<td style="font-weight: bold;">角色戏份</td>
					<td style="font-weight: bold;">角色描述</td>
				</tr>
				
				<g:each in="${params.object?.artistList}" status="i" var="it">
					<tr style="<g:if test="${i>4 }">display:none</g:if>" class="actors">
						<td>${it?.aName }</td>
						<td>${it?.bName }</td>
						<td>${it?.cName }</td>
						<td>
							<textarea rows="2" cols="58" style="resize:none;" disabled="disabled">${it?.description }</textarea>
						</td>
					</tr>
				</g:each>
				<g:if test="${params.object?.artistList?.size() > 5 }">
					<tr>
						<td colspan="4" style="text-align: center;">
							<a href="javascript:void(0)" onclick="showOrHideMoreActorInfo(this,'show')">演员信息<i class="glyphicon glyphicon-chevron-down"></i></a>
						</td>
					</tr>
				</g:if>
			</table>
		</div>
		</div> 
		<div class="header_right">
			<img src="${ grailsApplication.config.grails.app.imgpath}${params.object?.img } " style="height: 200px;width:150px">			<div class="hr-font">好评：${params.object.positivePercent }</div>
		</div>
	</div>
	</div>
		<!--<div style="width:85%;font-weight: bold;font-size:130%;">网剧收视率</div>
		<div id="telAudienceRating" style="height:350px;width:85%;display: inline-block;"></div> -->
		 <div style="width:85%;font-weight: bold;font-size:130%;">网剧播放量</div>
		<div id="telAudienceRating" style="height:350px;width:85%;display: inline-block;"></div>
		<div style="width:85%;font-weight: bold;font-size:130%;">网剧好评率</div>
		<div id="teleplayPraise" style="height:350px;width:85%;display: inline-block;"></div>
	    <div style="width:85%;font-weight: bold;font-size:130%;">网剧媒体关注度</div>
	    <div id="hotRate" style="height:350px;width:85%;display: inline-block;"></div>
	    <div style="width:85%;font-weight: bold;font-size:130%;">网剧公众影响力</div>
	    <div id="publicInfluence" style="height:350px;width:85%;display: inline-block;"></div>
	    
 	</g:if>
 	
 	<g:if test="${params.type=="3" }"><!-- 3电影 -->
 		<div class="marketdataMiddle">
		 	<div class="top_header">
				<div class="tabbable" id="tabs-136492">
					<ul class="nav nav-tabs">
						<li class="">
							<a href="${ grailsApplication.config.grails.app.name}/hisThinkTank/index?type=1" data-toggle="modal">电视剧</a>
						</li >
						<li class="">
							<a href="${ grailsApplication.config.grails.app.name}/hisThinkTank/index?type=2" data-toggle="modal">网剧</a>
						</li>
						<li class="${params.active }">
							<a href="${ grailsApplication.config.grails.app.name}/hisThinkTank/index?type=3" data-toggle="modal">电影</a>
						</li>
					</ul>
					<div class="tab-content">
						<div class="${mtpActive }" id="${moviePanel }">
							<form class="navbar-form navbar-left" role="search">
								<div class="form-group">
									<input class="form-control" id="group_search3" type="text" style="width: 700px;" onkeypress="if(event.keyCode==13) {btn3.click();return false;}">
								</div> 
								<button type="button" id="btn3" class="btn btn-default" onclick="searchObject3()">
									搜索
								</button>
								<button type="button" class="btn btn-link" onclick="window.location.href='${grailsApplication.config.grails.app.name}/hisThinkTankCompare/index'">
									PK
								</button>
							</form>
						</div>
					</div>
				</div>
				
				<div class="header_left">
					<div class="box col-md-12">
						<table class="table table-striped table-bordered bootstrap-datatable responsive" id="infoTable">
							<tr>
								<td style="font-weight: bold;">剧名</td>
								<td>${params.object?.name }</td>
								<td style="font-weight: bold;">类型</td>
								<td>${params.object?.movieType }</td>
							</tr>
							<tr>
								<td style="font-weight: bold;">IP来源</td>
								<td>${params.object?.ipName }</td>
								<td style="font-weight: bold;">档期</td>
								<td>${params.object?.movieSchedule }</td>
							</tr>
							<tr>
								<td style="font-weight: bold;">出品公司</td>
								<td>${params.object?.organ21Name }</td>
								<td style="font-weight: bold;">发行公司</td>
								<td>${params.object?.organ20Name }</td>
							</tr>
							<tr>
								<td style="font-weight: bold;">上映时间</td>
								<td>${params.object?.publishTime }</td>
								<td style="font-weight: bold;">制片成本</td>
								<td>${params.object?.productionCost }</td>
							</tr>
							<tr>
								<td style="font-weight: bold;">网络播放平台</td>
								<td colspan="3">${params.object?.siteName }</td>
							</tr>
							<tr>
								<td style="font-weight: bold;">导演</td>
								<td>${params.object?.director }</td>
								<td style="font-weight: bold;">编剧</td>
								<td>${params.object?.scriptwriter }</td>
							</tr>
							<tr>
								<td style="font-weight: bold;">演员</td>
								<td style="font-weight: bold;">角色名称</td>
								<td style="font-weight: bold;">角色戏份</td>
								<td style="font-weight: bold;">角色描述</td>
							</tr>
							<g:each in="${params.object?.artistList}" status="i" var="it">
								<tr style="<g:if test="${i>4 }">display:none</g:if>" class="actors">
									<td>${it?.aName }</td>
									<td>${it?.bName }</td>
									<td>${it?.cName }</td>
									<td>
										<textarea rows="2" cols="58" style="resize:none;" disabled="disabled">${it?.description }</textarea>
									</td>
								</tr>
							</g:each>
							<g:if test="${params.object?.artistList?.size() > 5 }">
								<tr>
									<td colspan="4" style="text-align: center;">
										<a href="javascript:void(0)" onclick="showOrHideMoreActorInfo(this,'show')">演员信息<i class="glyphicon glyphicon-chevron-down"></i></a>
									</td>
								</tr>
							</g:if>
						</table>
					</div>
					</div> 
					<div class="header_right">
						<img src="${ grailsApplication.config.grails.app.imgpath}${params.object?.img } " style="height: 200px;width:150px">
						<div class="hr-font">好评：${params.object?.positivePercent }</div>
					</div>
				</div>
				</div>
				<div style="width:85%;font-weight: bold;font-size:130%;">电影票房</div>
				<div id="movieBoxOffice" style="height:350px;width:85%;display: inline-block;"></div>
				<!-- <div style="width:85%;font-weight: bold;font-size:130%;">电影播放量</div>
				<div id="" style="height:350px;width:85%;display: inline-block;"></div> -->
				<div style="width:85%;font-weight: bold;font-size:130%;">电影好评率</div>
		 		<div id="moviePraise" style="height:350px;width:85%;display: inline-block;"></div>
		 		<div style="width:85%;font-weight: bold;font-size:130%;">电影媒体关注度</div>
			    <div id="hotRate" style="height:350px;width:85%;display: inline-block;"></div>
			    <div style="width:85%;font-weight: bold;font-size:130%;">电影公众影响力</div>
			    <div id="publicInfluence" style="height:350px;width:85%;display: inline-block;"></div>
 	</g:if>
 	<script src="${grailsApplication.config.grails.app.name}/js/echarts/echarts.js" ></script>
 	<script src="${grailsApplication.config.grails.app.name}/js/hisThinkTankBasicInfo/hisThinkTankBasicInfo.js" ></script>
</body>
</html>
