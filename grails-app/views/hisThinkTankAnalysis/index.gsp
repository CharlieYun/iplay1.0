<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<meta name="layout" content="main"/>
<script type="text/javascript" src="${grailsApplication.config.grails.app.name }/js/hisThinkTankAnalysis/index.js"></script>
<script type="text/javascript" src="${grailsApplication.config.grails.app.name }/js/autoComplete/autoComplete.js"></script>
<script type="text/javascript" src="${grailsApplication.config.grails.app.name }/js/echarts/echarts.js"></script>
<title>历史智库-交互分析</title>
</head>
<body>
	<div class="container-fluid box-inner">
		<div class="box-content analysis_search_container">
			<div class="row">
				<div class="box col-md-2">筛选条件:</div>
				<div class="box col-md-10" id="selected_container">
				</div>
			</div>
			<div class="row analysis_search_condition" id="themeContainer">
				<div class="box col-md-2">分类:</div>
				<g:each in="${condition['themeInfo'] }" var="k,v" status="i">
					<div class="box col-md-2 pConditionTag" data-value="p_${k }">${v?.p_name }</div>
					<g:if test="${v?.children }">
						<div class="float_check_container c_${k }" style="display:none;">
							<div style="margin: 10px;">
								<label><input type="checkbox" data-value="all" data-name="${v?.p_name }_${k}"/>全部</label>
								<g:each in="${v?.children }" var="it" status="j">
									<label><input type="checkbox" data-value="p_${k }_c_${it?.c_id}"/>${it?.c_name}</label>
								</g:each>
							</div>
						</div>
					</g:if>
				</g:each>
			</div>
			<div class="row analysis_search_condition" id="platformContainer">
				<div class="box col-md-2">平台分类:</div>
				<g:each in="${condition['platformInfo'] }" var="k,v" status="i">
					<div class="box col-md-2 pConditionTag" data-value="p_${k }">
						<g:if test="${k == 'tv' }">电视</g:if>
						<g:elseif test="${k == 'net_tv' }">网视</g:elseif>
					</div>
					<g:if test="${v }">
						<div class="float_check_container c_${k }" style="display:none;">
							<div style="margin: 10px;">
								<label><input type="checkbox" data-value="all" data-name="${k=='tv'?'电视':'网视' }_${k}"/>全部</label>
								<g:each in="${v }" var="it" status="j">
									<label><input type="checkbox" data-value="p_${k }_c_${it?.id}"/>${it?.name}</label>
								</g:each>
							</div>
						</div>
					</g:if>
				</g:each>
			</div>
			<div class="row analysis_search_condition" id="creatorContainer">
				<div class="box col-md-2">主创分类:</div>
				<div class="box col-md-2">
					<button type="button" class="btn btn-default btn-sm active" data-value="director">导演</button>
				</div>
				<div class="box col-md-2">
					<button type="button" class="btn btn-default btn-sm" data-value="scriptwriter">编剧</button>
				</div>
				<div class="box col-md-2">
					<button type="button" class="btn btn-default btn-sm" data-value="actor">演员</button>
				</div>
				<div class="box col-md-2">
					<input type="text" id="searchCreator"/>
					<input type="hidden" id="searchCreator-objId"/>
					<input type="hidden" id="creatorType" value="director"/>
				</div>
			</div>
			<%-- <div class="row analysis_search_condition" id="audienceContainer">
				<div class="box col-md-2">受众分类:</div>
				<g:each in="${condition['audienceInfo'] }" var="k,v" status="i">
					<div class="box col-md-2 pConditionTag" data-value="p_${k }">${v?.p_name }</div>
					<g:if test="${v?.children }">
						<div class="float_check_container c_${k }" style="display:none;">
							<div style="margin: 10px;">
								<label><input type="checkbox" data-value="all" data-name="${v?.p_name }_${k}"/>全部</label>
								<g:each in="${v?.children }" var="it" status="j">
									<label><input type="checkbox" data-value="p_${k }_c_${it?.c_id}"/>${it?.c_name}</label>
								</g:each>
							</div>
						</div>
					</g:if>
				</g:each>
			</div>--%>
			<div class="row analysis_search_condition">
				<div class="box col-md-2">数据分类:</div>
				<div class="box col-md-4">
					收视率:
					<input id="ratingStart" type="text" style="width:50px;"/>
					%
					---
					<input id="ratingEnd" type="text" style="width:50px;"/>
					%
					<br/><font color="red" class="error_tip" style="display:none">(输入格式有误)</font>
				</div>
				<div class="box col-md-6">
					播放量:
					<input type="text" id="playNumStart" name="playNumStart" style="width:100px;"/>
					---
					<input type="text" id="playNumEnd" name="playNumEnd" style="width:100px;"/>
					<font>(单位可输入:万、千万、亿)</font>
					<br/><font color="red" class="error_tip" style="display:none">(输入格式有误)</font>
				</div>
			</div>
			<div class="row analysis_search_condition">
				<div class="box col-md-2">时间范围:</div>
				<div class="box col-md-8">
					<input id="timeRangeStart" type="text"/>
					to
					<input id="timeRangeEnd" type="text"/>
				</div>
				<div class="box col-md-2">
					<button type="button" class="btn btn-primary btn-sm" onclick="ajaxSearchAnalysisInfoByCondition()">查询</button>
				</div>
			</div>
		</div>
	</div>
	<div class="container-fluid box-inner">
		<div class="box-content analysis_search_container" style="position: relative;">
			<div class="row" id="resultContainer">
				<div id="chartContent" style="width: 100%; height: 300px;display: inline-block;"></div>
				<div id="tableContent" class="table-responsive" style="height: 600px;width: 968px;"></div>
				<div id="tableContentLoading" style="display:none">
					<div style="position: absolute;left:46%;top:40%;font-size: 0.78125rem;font-size:12.5px; z-index: 1;">
						<div><img src="${grailsApplication.config.grails.app.name }/images/load.gif"></div>
						加载中... ...
					</div>
				</div>
				<div id="oneDimensionContent" style="display:none;"></div>
				<div id="emptyContent" style="display:none;">暂无数据</div>
			</div>
		</div>
	</div>
</body>
</html>