<!DOCTYPE html>
<html>
<head>
<title>历史智库-对比信息</title>
<meta name="layout" content="main"/>
<link href="${ grailsApplication.config.grails.app.name}/css/loading.css" rel="stylesheet"  type="text/css"></link>
<script src="${grailsApplication.config.grails.app.name}/js/autoComplete/autoComplete.js" ></script>
<script src="${grailsApplication.config.grails.app.name}/js/hisThinkTankCompare/hisThinkTankCompare.js" ></script>
<script src="${grailsApplication.config.grails.app.name}/js/echarts/echarts.js" ></script>
</head>
<body>
	<div class="top_header_compare">
	<div class="col-lg-12">
	<div class="well well-sm">
		<!-- <div id="group_search_id" style="min-height: 27px;">搜索剧目：</div> -->
		<div class="box box-content">
			<div class="row">
				<div class="col-sm-1" style="padding: 0 0 0 8px">搜索剧目：</div>
				<div class="col-sm-11" id="group_search_id"></div>
			</div>
		</div>
	    <div class="input-group">
	      <input id="group_search" type="text" class="form-control" placeholder="Search for...">
	      <span class="input-group-btn">
	        <button class="btn btn-default" type="button" onclick="teleplay_data_compare()">对比</button>
	      </span>
	    </div><!-- /input-group -->
	    <div class="table-responsive">
		    <table id="group_teleplay_informations" style="display: none;" class="table table-striped table-bordered bootstrap-datatable responsive">
		    </table>
	    </div>
	  	<div style="position: relative; display: inline-block;"><div id="trcc" class="tcmp" style="display: block; float: left; height: 100%; width: 140px;display: none;"></div><div id="tv_ratings_comparison_chart" style="height:300px;border:1px solid #ccc;padding:10px;display: none;float: left; width: 810px;"></div></div>
	    <div style="position: relative; display: none;"><div id="ccotp" class="tcmp" style="display: block; float: left; height: 100%; width: 140px;display: none;"></div><div id="comparison_chart_of_TV_play" style="height:300px;border:1px solid #ccc;padding:10px;display: none;float: left; width: 810px;"></div></div>
	    <div style="position: relative; display: inline-block;"><div id="macc" class="tcmp" style="display: block; float: left; height: 100%; width: 140px;display: none;"></div><div id="media_attention_change_chart" style="height:300px;border:1px solid #ccc;padding:10px;display: none;float: left; width: 810px;"></div></div>
	    <div style="position: relative; display: inline-block;"><div id="picc" class="tcmp" style="display: block; float: left; height: 100%; width: 140px;display: none;"></div><div id="public_influence_change_chart" style="height:300px;border:1px solid #ccc;padding:10px;display: none;float: left; width: 810px;"></div></div>
	  </div><!-- /.col-lg-6 -->
	  </div>
	</div>
</body>
</html>