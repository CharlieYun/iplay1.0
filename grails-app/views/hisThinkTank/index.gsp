<!DOCTYPE html>
<html>
<head>
<title>历史智库</title>
<meta name="layout" content="main"/>
<script src="${grailsApplication.config.grails.app.name}/js/autoComplete/autoComplete.js" ></script>
<script src="${grailsApplication.config.grails.app.name}/js/hisThinkTankBasicInfo/hisThinkTankBasicInfo.js" ></script>


<script>
var objectId=0
$(function(){
	objectInfoAutoComplete('group_search1',5,'imgAndNameAndYear',undefined,undefined,function(id){
		objectId=id
		var ttype=$("#type").val()
		location.href="${ grailsApplication.config.grails.app.name}/hisThinkTank/show/"+objectId+"?type="+ttype+"&objectType="+${params.objectType}
    });
	objectInfoAutoComplete('group_search2',5,'imgAndNameAndYear',undefined,undefined,function(id){
		objectId=id
		var ttype=$("#type").val()
		location.href="${ grailsApplication.config.grails.app.name}/hisThinkTank/show/"+objectId+"?type="+ttype+"&objectType="+${params.objectType}
    });
	objectInfoAutoComplete('group_search3',4,'imgAndNameAndYear',undefined,undefined,function(id){
		objectId=id
		var ttype=$("#type").val()
		location.href="${ grailsApplication.config.grails.app.name}/hisThinkTank/show/"+objectId+"?type="+ttype+"&objectType="+${params.objectType}
    });
});

//日期单击事件
function serachY(aid,avalue){
	var ttype=$("#type").val()
	location.href="${ grailsApplication.config.grails.app.name}/hisThinkTank/index?type="+ttype+"&aid="+aid+"&avalue="+avalue
}

function searchObject1(){
	var ttype=$("#type").val()
	if(objectId==0){
		$("#group_search1").val("抱歉！！您输入的电视剧不存在，请重新输入！")
		$("#group_search1").focus()
		return false;
	}else{
		location.href="${ grailsApplication.config.grails.app.name}/hisThinkTank/show/"+objectId+"?type="+ttype
	}
}

function searchObject2(){
	var ttype=$("#type").val()
	if(objectId==0){
		$("#group_search2").val("抱歉！！您输入的网剧不存在，请重新输入！")
		$("#group_search2").focus()
		return false;
	}else{
		location.href="${ grailsApplication.config.grails.app.name}/hisThinkTank/show/"+objectId+"?type="+ttype
	}
}

function searchObject3(){
	var ttype=$("#type").val()
	if(objectId==0){
		$("#group_search3").val("抱歉！！您输入的电影不存在，请重新输入！")
		$("#group_search3").focus()
		return false;
	}else{
		location.href="${ grailsApplication.config.grails.app.name}/hisThinkTank/show/"+objectId+"?type="+ttype
	}
}


</script>
</head>
<body>
<input type="hidden" value="${ttype }" id="type"/>
		<div class="tabbable" id="tabs-136492">
			<ul class="nav nav-tabs">
				<li class="<g:if test="${params.type == '1' }">active</g:if>">
					<a href="${ grailsApplication.config.grails.app.name}/hisThinkTank/index?type=1" data-toggle="modal">电视剧</a>
				</li >
				<li class="<g:if test="${params.type == '2' }">active</g:if>">
					<a href="${ grailsApplication.config.grails.app.name}/hisThinkTank/index?type=2" data-toggle="modal">网剧</a>
				</li>
				<li class="<g:if test="${params.type == '3' }">active</g:if>">
					<a href="${ grailsApplication.config.grails.app.name}/hisThinkTank/index?type=3" data-toggle="modal">电影</a>
				</li>
			</ul>
			<div class="tab-content">
				<div class="${ttpActive }" id="${teleplayPanel }">
					<form class="navbar-form navbar-left" role="search">
						<div class="form-group">
							<input class="form-control" id="group_search1" type="text" style="width: 700px;">
						</div> 
						<button type="button" class="btn btn-default" onclick="searchObject1()">
							搜索
						</button>
						<a href="${ grailsApplication.config.grails.app.name}/hisThinkTankAnalysis/index" class="dropdown-toggle" >交互筛选</a>
					</form>
					<div class="container-fluid">
						<div class="row">
							<div class="col-md-12" id="serachYear">
								   按年代
								 <a href="javascript:void(0)" onclick="serachY('all','all')">全部</a>
								 <a href="javascript:void(0)" onclick="serachY('2015-01-01','2015-12-31')">2015</a>
								 <a href="javascript:void(0)" onclick="serachY('2014-01-01','2014-12-31')">2014</a>
								 <a href="javascript:void(0)" onclick="serachY('2013-01-01','2013-12-31')">2013</a>
								 <a href="javascript:void(0)" onclick="serachY('2012-01-01','2012-12-31')">2012</a>
								 <a href="javascript:void(0)" onclick="serachY('2011-01-01','2011-12-31')">2011</a>
								 <a href="javascript:void(0)" onclick="serachY('2010-01-01','2010-12-31')">2010</a>
							</div>
						</div>
					</div>
				
					<div class="row">
						<div class="col-md-12">
							<div class="row">
							<g:each in="${teleplayInstanceList}" status="i" var="it">
								<div class="col-md-3">
									<div class="thumbnail" style="height: 253px;text-align: center;">
										<img src="${ it?.img?(grailsApplication.config.grails.app.imgpath+it?.img):grailsApplication.config.grails.app.name+'/images/noMovePic02.png'} " style="height: 200px;width:150px">
										<a href="show/${it?.id }?type=${ttype }&objectType=5"style="height:auto;width:100%;margin-top: 10px;">${it?.name }</a>
									</div>
								</div>
							</g:each>
							</div>
						</div>
					</div>
			 	 	<bs:normalPageTag totalCount="${teleplayInstanceTotal }"/>
				</div>
				<div class="${ntpActive }" id="${networkPanel }">
					<form class="navbar-form navbar-left" role="search">
						<div class="form-group">
							<input class="form-control" id="group_search2" type="text" style="width: 700px;">
						</div> 
						<button type="button" class="btn btn-default" onclick="searchObject2()">
							搜索
						</button>
						<a href="${ grailsApplication.config.grails.app.name}/hisThinkTankAnalysis/index" class="dropdown-toggle" >交互筛选</a>
					</form>
					<div class="container-fluid">
						<div class="row">
							<div class="col-md-12">
								     按年代
								 <a href="javascript:void(0)" onclick="serachY('all','all')">全部</a>
								 <a href="javascript:void(0)" onclick="serachY('2015-01-01','2015-12-31')">2015</a>
								 <a href="javascript:void(0)" onclick="serachY('2014-01-01','2014-12-31')">2014</a>
								 <a href="javascript:void(0)" onclick="serachY('2013-01-01','2013-12-31')">2013</a>
								 <a href="javascript:void(0)" onclick="serachY('2012-01-01','2012-12-31')">2012</a>
							</div>
						</div>
					</div>
					<div class="row">
						<g:each in="${teleplayInstanceList}" status="i" var="it">
							<div class="col-md-3">
								<div class="thumbnail" style="height: 253px;text-align: center;">
									<img src="${ it?.img?(grailsApplication.config.grails.app.imgpath+it?.img):grailsApplication.config.grails.app.name+'/images/noMovePic02.png'} " style="height: 200px;width:150px">
									<a href="show/${it?.id }?type=${ttype }&objectType=5" style="height:auto;width:100%;margin-top: 10px;">${it?.name }</a>
								</div>
							</div>
						</g:each>
					</div>
					<bs:normalPageTag totalCount="${teleplayInstanceTotal }"/>
				</div>
				
				<div class="${mtpActive }" id="${moviePanel }">
					<form class="navbar-form navbar-left" role="search">
						<div class="form-group">
							<input class="form-control" id="group_search3" type="text" style="width: 700px;">
						</div> 
						<button type="button" class="btn btn-default" onclick="searchObject3()">
							搜索
						</button>
						<a href="${ grailsApplication.config.grails.app.name}/hisThinkTankAnalysis/index" class="dropdown-toggle" >交互筛选</a>
					</form>
					<div class="container-fluid">
						<div class="row">
							<div class="col-md-12">
								    按年代
								 <a href="javascript:void(0)" onclick="serachY('all','all')">全部</a>
								 <a href="javascript:void(0)" onclick="serachY('2015-01-01','2015-12-31')">2015</a>
								 <a href="javascript:void(0)" onclick="serachY('2014-01-01','2014-12-31')">2014</a>
								 <a href="javascript:void(0)" onclick="serachY('2013-01-01','2013-12-31')">2013</a>
								 <a href="javascript:void(0)" onclick="serachY('2012-01-01','2012-12-31')">2012</a>
							</div>
						</div>
					</div>
					<div class="row">
						<g:each in="${movieInstanceList}" status="i" var="it">
							<div class="col-md-3">
								<div class="thumbnail" style="height: 253px;text-align: center;">
									<img src="${ it?.img?(grailsApplication.config.grails.app.imgpath+it?.img):grailsApplication.config.grails.app.name+'/images/noMovePic02.png'} " style="height: 200px;width:150px">
									<a href="show/${it?.id }?type=${ttype }&objectType=4" style="height:auto;width:100%;margin-top: 10px;">${it?.name }</a>
								</div>
							</div>
						</g:each>
					</div>
					<bs:normalPageTag totalCount="${movieInstanceTotal }"/>
				</div>
			</div>
		</div>
</body>
</html>