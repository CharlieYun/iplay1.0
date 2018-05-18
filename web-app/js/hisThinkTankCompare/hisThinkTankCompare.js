//保存页面电视剧ID
var ids = [];
//保存页面电视剧名
var names = []
//保存图表信息
var tvRatingsComparisonChart;
var tvRatingsComparisonChartInit;
var comparisonChartOfTVplayChart;
var comparisonChartOfTVplayChartInit;
var mediaAttentionChangeChart;
var mediaAttentionChangeChartInit;
var publicInfluenceChangeChart;
var publicInfluenceChangeChartInit;

$(function(){
	//搜索框自动补全功能
	objectInfoAutoComplete('group_search',5,'imgAndNameAndYear',undefined,ids,function(id,name){
    	ids.push(id);
    	names.push(name);
		$("#group_search").val("");
		$("#group_search_id").append("<span style='border: 1px solid; padding: 5px;display: inline-block;margin: 0 5px 5px;' id="+id+">"+name+"&nbsp;<a onclick='teleplay_data_id_delete("+id+")' class='glyphicon glyphicon-remove'></a></span>");
    });
	//checkbox控制echarts数据显示隐藏
	$(document).on("click",".tcmp input[type='checkbox']",function(){
		var thisId = $(this).attr("data-value");
		var checkedNum = 0
		if($(this).is(":checked")){//选中
			checkedNum = $(this).parent().siblings("label").find("input[type='checkbox']:checked").length + 1
		}
		if(checkedNum > 5){
			alert("最多对比5条曲线！")
			$(this).attr("checked",false);
			return
		}
		var checkedIdArr = new Array()
		$(this).parents(".tcmp").find("input[type='checkbox']:checked").each(function(){
			var thisValue = $(this).attr("data-value")
			checkedIdArr.push(thisValue.split("_")[1])
		})
		var sourceOptions = {}
		var chartObj
		if(thisId.indexOf("trcc") > -1){
			sourceOptions = $.extend({},tvRatingsComparisonChart);
			chartObj = tvRatingsComparisonChartInit;
		}else if(thisId.indexOf("ccotp") > -1){
			sourceOptions = $.extend({},comparisonChartOfTVplayChart);
			chartObj = comparisonChartOfTVplayChartInit;
		}else if(thisId.indexOf("macc") > -1){
			sourceOptions = $.extend({},mediaAttentionChangeChart);
			chartObj = mediaAttentionChangeChartInit;
		}else if(thisId.indexOf("picc") > -1){
			sourceOptions = $.extend({},publicInfluenceChangeChart);
			chartObj = publicInfluenceChangeChartInit;
		}
		var sourceOptionSeries = sourceOptions.series;
		var newChartSeriesData = new Array()
		for(var i=0;i<sourceOptionSeries.length;i++){
			if(checkedIdArr.indexOf(sourceOptionSeries[i].id) > -1){
				newChartSeriesData.push(sourceOptionSeries[i])
			}
		}
		sourceOptions.series = newChartSeriesData;
		chartObj.clear();
		chartObj.setOption(sourceOptions);
	});
});

//删除搜索剧目中的电视剧
function teleplay_data_id_delete(id){
	var indexId = ids.indexOf(id);
	ids.splice(indexId,1);
	names.splice(indexId,1);
	$("#"+id).remove();
}

//电视剧对比按钮事件
function teleplay_data_compare(){
	if(ids.length == 0){
		$("#group_teleplay_informations").empty();
		$("#trcc").empty();
		$("#ccotp").empty();
		$("#macc").empty();
		$("#picc").empty();
		$("#tv_ratings_comparison_chart").empty();
		$("#comparison_chart_of_TV_play").empty();
		$("#media_attention_change_chart").empty();
		$("#public_influence_change_chart").empty();
		$("#tv_ratings_comparison_chart").css("display","none");
		$("#comparison_chart_of_TV_play").css("display","none");
		$("#media_attention_change_chart").css("display","none");
		$("#public_influence_change_chart").css("display","none");
	}else{
		//对比等待加载
		var $cantainer = $("body");
		var $loadDiv = $("<div class='loading_div_screen' ></div>");
		if(window.innerHeight > document.body.scrollHeight){
			$loadDiv.css("height",window.innerHeight);
		}else{
			$loadDiv.css("height",document.body.scrollHeight);
		}
		$cantainer.append($loadDiv);
		var $img_div = $("<div class='loading_div_screen_gif'></div>");
		$loadDiv.append($img_div);
		var queryIds = "";
		for(var i=0;i<ids.length;i++){
			if(ids[i] != ""){
				queryIds += ids[i] + ","
			}
		}
		if(queryIds != ""){
			queryIds = queryIds.substring(0,queryIds.length-1)
		}
		$.ajax({
			type:"post",
			url:projectURITemp.defcon+"/hisThinkTankCompare/teleplayDataCompare",
			data:{ids:queryIds},
			success:function(data){
				$("#group_teleplay_informations").empty();
				$("#trcc").empty();
				$("#ccotp").empty();
				$("#macc").empty();
				$("#picc").empty();
				$("#tv_ratings_comparison_chart").empty();
				$("#comparison_chart_of_TV_play").empty();
				$("#media_attention_change_chart").empty();
				$("#public_influence_change_chart").empty();
				//电视剧table数据加载
				$("#group_teleplay_informations").append("<tr id='teleplay_name'><td align=center>剧名</td></tr>");
				$("#group_teleplay_informations").append("<tr id='teleplay_theme_name'><td align=center>分类</td></tr>");
				$("#group_teleplay_informations").append("<tr id='teleplay_channel_name'><td align=center>电视上星首播</td></tr>");
				$("#group_teleplay_informations").append("<tr id='teleplay_channel_play_date'><td align=center>电视首播播出时间</td></tr>");
				$("#group_teleplay_informations").append("<tr id='teleplay_net_name'><td align=center>网络播出平台</td></tr>");
				$("#group_teleplay_informations").append("<tr id='teleplay_premiere_date'><td align=center>网络首播播出时间</td></tr>");
				$("#group_teleplay_informations").append("<tr id='teleplay_average_rate'><td align=center>平均收视率CSM50</td></tr>");
				$("#group_teleplay_informations").append("<tr id='teleplay_cumulative_play'><td align=center>累计播放量</td></tr>");
				$("#group_teleplay_informations").append("<tr id='teleplay_praise_rate'><td align=center>好评率</td></tr>");
				$("#group_teleplay_informations").append("<tr id='teleplay_director'><td align=center>导演</td></tr>");
				$("#group_teleplay_informations").append("<tr id='teleplay_screenwriter'><td align=center>编剧</td></tr>");
				$("#group_teleplay_informations").append("<tr id='teleplay_to_star'><td align=center>主演</td></tr>");
				for(var i=0;i<data.length;i++){
					$("#teleplay_name").append("<td align=center>"+data[i].teleplay_name+"</td>");
					$("#teleplay_theme_name").append("<td align=center>"+data[i].teleplay_theme_name+"</td>");
					$("#teleplay_channel_name").append("<td align=center>"+data[i].teleplay_channel_name+"</td>");
					$("#teleplay_channel_play_date").append("<td align=center>"+data[i].teleplay_channel_play_date+"</td>");
					$("#teleplay_net_name").append("<td align=center>"+data[i].teleplay_net_name+"</td>");
					$("#teleplay_premiere_date").append("<td align=center>"+data[i].teleplay_premiere_date+"</td>");
					$("#teleplay_average_rate").append("<td align=center>"+data[i].teleplay_average_rate+"</td>");
					$("#teleplay_cumulative_play").append("<td align=center>"+data[i].teleplay_cumulative_play+"</td>");
					$("#teleplay_praise_rate").append("<td align=center>"+data[i].teleplay_praise_rate+"</td>");
					$("#teleplay_director").append("<td align=center>"+data[i].teleplay_director+"</td>");
					$("#teleplay_screenwriter").append("<td align=center>"+data[i].teleplay_screenwriter+"</td>");
					$("#teleplay_to_star").append("<td align=center>"+data[i].teleplay_to_star+"</td>");
				}
				$("#group_teleplay_informations").css("display","block");
				//加载图表信息
				allTeleplayCompareDataQuery(queryIds);
				//对比等待删除
				$loadDiv.remove();
			}
		})
	}
}

//加载图表信息，包括电视剧收视率对比图、电视剧播放量对比图、媒体关注度变化图、公众影响力变化图
function allTeleplayCompareDataQuery(queryIds){
	//加载电视剧收视率对比图
	$.ajax({
		type:"post",
		url:projectURITemp.defcon+"/hisThinkTankCompare/tvRatingsComparison",
		data:{ids:queryIds},
		success:function(data){
			
			

			var yDataAll = [];
			var legendData = [];
			var xDate = [];
			for(var i in data){
				for ( var j in data[i]) {
					var yData_35 = {};
					yData_35.id = i;
					yData_35.type = 'line';
//					yData_35.stack = '总量';
					yData_35.name = names[ids.indexOf(parseInt(i))];
					yData_35.data = data[i][j].thirtyFiveRateValueList;
					yData_35.playData = data[i][j].teleplayPlayDate;
					legendData.push(yData_35.name);
					yDataAll.push(yData_35);
					xDate = data[i][j].thirtyFiveAndFiftyRateDateList;
				}
				
				
//				var yData_50 = {};
//				yData_50.id = i;
//				yData_50.name = names[ids.indexOf(parseInt(i))] + "_50";
//				yData_50.type = 'line';
//				yData_50.stack = '总量';
//				yData_50.data = data[i].fiftyRateValueList;
//				yData_50.playData = data[i].teleplayPlayDate;
//				legendData.push(yData_50.name);
//				yDataAll.push(yData_50);
//				var yData_100 = {};
//				yData_100.id = i;
//				yData_100.name = names[ids.indexOf(parseInt(i))] + "_100";
//				yData_100.type = 'line';
//				yData_100.stack = '总量';
//				yData_100.data = data[i].allCityRateValueList;
//				yData_100.playData = data[i].teleplayPlayDate;
//				legendData.push(yData_100.name);
//				yDataAll.push(yData_100);
				
			}
			
			if(yDataAll.length == 0){
	    		yDataAll = [{}]
	    	}
			
			var xDateBake = [];
			for ( var j = 0; j < xDate.length; j++) {
				if((xDate[j] > 0)){
					xDateBake.push('播出第'+xDate[j]+'天');
				}else if(xDate[j] < 0){
					xDateBake.push('首播日前'+Math.abs(xDate[j])+'天');
				}else{
					xDateBake.push('首播日');
				}
			}
			$("#trcc").css("display","block");
			var showList = new Array();
			for(var i=0;i<names.length;i++){
				if(i <= 4){
					$("#trcc").append("<label><input type='checkbox' checked='true' data-value='trcc_"+ids[i]+"'></input>"+names[i]+"</label><br/>");
	    			showList.push(ids[i].toString());
				}else{
					$("#trcc").append("<label><input type='checkbox' data-value='trcc_"+ids[i]+"'></input>"+names[i]+"</label><br/>");
				}
			}
			$("#tv_ratings_comparison_chart").css("display","block");
			showTvRatingsComparisonChart(xDateBake,yDataAll,legendData);
			if(names.length > 4){
				var sourceOptions = {}
	    		var chartObj
    			sourceOptions = $.extend({},tvRatingsComparisonChart);
    			chartObj = tvRatingsComparisonChartInit;
				var sourceOptionSeries = sourceOptions.series;
	    		var newChartSeriesData = new Array()
	    		for(var i=0;i<sourceOptionSeries.length;i++){
	    			if(showList.indexOf(sourceOptionSeries[i].id) > -1){
	    				newChartSeriesData.push(sourceOptionSeries[i])
	    			}
	    		}
	    		sourceOptions.series = newChartSeriesData;
	    		chartObj.clear();
	    		chartObj.setOption(sourceOptions);
			}
		
		}
	})
	//加载电视剧播放量对比图
	$.ajax({
		type:"post",
		url:projectURITemp.defcon+"/hisThinkTankCompare/comparisonChartOfTVplay",
		data:{ids:queryIds},
		success:function(data){

			$("#ccotp").css("display","block");
			for(var i=0;i<names.length;i++){
				$("#ccotp").append("<input type='checkbox'>"+names[i]+"</input>");
			}
			$("#comparison_chart_of_TV_play").css("display","block");
//			showComparisonChartOfTVplayChart();
		}
	})
	//加载媒体关注度变化图
	$.ajax({
		type:"post",
		url:projectURITemp.defcon+"/hisThinkTankCompare/mediaAttentionChange",
		data:{ids:queryIds},
		success:function(data){
			var yDataAll = [];
			var legendData = [];
			var xDate;
			for(var i in data){
				var yData = {};
				yData.id = i;
				yData.name = names[ids.indexOf(parseInt(i))];
				yData.type = 'line';
//				yData.stack = '总量';
				yData.data = data[i].hotRateValueList;
				yData.playData = data[i].teleplayPlayDate;
				legendData.push(yData.name);
				yDataAll.push(yData);
				xDate = data[i].hotRateDateList;
			}
			var xDateBake = [];
			for ( var j = 0; j < xDate.length; j++) {
				if((xDate[j] > 0)){
					xDateBake.push('首播日后'+xDate[j]+'天');
				}else if(xDate[j] < 0){
					xDateBake.push('首播日前'+Math.abs(xDate[j])+'天');
				}else{
					xDateBake.push('首播日');
				}
			}
			$("#macc").css("display","block");
			var showList = new Array();
			for(var i=0;i<names.length;i++){
				if(i <= 4){
					$("#macc").append("<label><input type='checkbox' checked='true' data-value='macc_"+ids[i]+"'></input>"+names[i]+"</label><br/>");
	    			showList.push(ids[i].toString());
				}else{
					$("#macc").append("<label><input type='checkbox' data-value='macc_"+ids[i]+"'></input>"+names[i]+"</label><br/>");
				}
			}
			$("#media_attention_change_chart").css("display","block");
			showMediaAttentionChangeChart(xDateBake,yDataAll,legendData);
			if(names.length > 4){
				var sourceOptions = {}
	    		var chartObj
    			sourceOptions = $.extend({},mediaAttentionChangeChart);
    			chartObj = mediaAttentionChangeChartInit;
				var sourceOptionSeries = sourceOptions.series;
	    		var newChartSeriesData = new Array()
	    		for(var i=0;i<sourceOptionSeries.length;i++){
	    			if(showList.indexOf(sourceOptionSeries[i].id) > -1){
	    				newChartSeriesData.push(sourceOptionSeries[i])
	    			}
	    		}
	    		sourceOptions.series = newChartSeriesData;
	    		chartObj.clear();
	    		chartObj.setOption(sourceOptions);
			}
		}
	})
	//加载公众影响力变化图
	$.ajax({
		type:"post",
		url:projectURITemp.defcon+"/hisThinkTankCompare/publicInfluenceChange",
		data:{ids:queryIds},
		success:function(data){
			var yDataAll = [];
			var legendData = [];
			var xDate;
			for(var i in data){
				var yData = {};
				yData.id = i;
				yData.name = names[ids.indexOf(parseInt(i))];
				yData.type = 'line';
//				yData.stack = '总量';
				yData.data = data[i].publicInfluenceValueList;
				yData.playData = data[i].teleplayPlayDate;
				legendData.push(yData.name);
				yDataAll.push(yData);
				xDate = data[i].publicInfluenceDateList;
			}
			var xDateBake = [];
			for ( var j = 0; j < xDate.length; j++) {
				if((xDate[j] > 0)){
					xDateBake.push('首播日后'+xDate[j]+'天');
				}else if(xDate[j] < 0){
					xDateBake.push('首播日前'+Math.abs(xDate[j])+'天');
				}else{
					xDateBake.push('首播日');
				}
			}
			$("#picc").css("display","block");
			var showList = new Array();
			for(var i=0;i<names.length;i++){
				if(i <= 4){
					$("#picc").append("<label><input type='checkbox'  checked='true' data-value='picc_"+ids[i]+"'></input>"+names[i]+"</label><br/>");
					showList.push(ids[i].toString());
				}else{
					$("#picc").append("<label><input type='checkbox' data-value='picc_"+ids[i]+"'></input>"+names[i]+"</label><br/>");
				}
			}
			$("#public_influence_change_chart").css("display","block");
			showPublicInfluenceChangeChart(xDateBake,yDataAll,legendData);
			if(names.length > 4){
				var sourceOptions = {}
	    		var chartObj
	    		sourceOptions = $.extend({},publicInfluenceChangeChart);
    			chartObj = publicInfluenceChangeChartInit;
				var sourceOptionSeries = sourceOptions.series;
	    		var newChartSeriesData = new Array()
	    		for(var i=0;i<sourceOptionSeries.length;i++){
	    			if(showList.indexOf(sourceOptionSeries[i].id) > -1){
	    				newChartSeriesData.push(sourceOptionSeries[i])
	    			}
	    		}
	    		sourceOptions.series = newChartSeriesData;
	    		chartObj.clear();
	    		chartObj.setOption(sourceOptions);
			}
		}
	})
}
//加载电视剧收视率对比图
function showTvRatingsComparisonChart(xDateBake,yDataAll,legendData){
	var zoomStartPercent = 0
	var zoomEndPercent = 100
	if(yDataAll.length > 0 && yDataAll[0].length !=  undefined){
		var startIndex = yDataAll[0].data.length > 30?yDataAll[0].data.length-31:0
		zoomStartPercent = startIndex/yDataAll[0].data.length*100
	}
	require.config({
	    paths: {
	        echarts: projectURI.defcon + '/js/echarts'
	    }
	});
	require(
	    [
	        'echarts',
	        'echarts/chart/line'
	    ],
	    function (ec) {
	        // --- 折柱 ---
	    	tvRatingsComparisonChartInit = ec.init(document.getElementById('tv_ratings_comparison_chart'));
	        var option = {
	        		title: {
	        	    	text : '电视剧35城收视率对比图'
	        	    },
		        	tooltip : {
		                trigger: 'axis',
		                formatter: function(show){
		                	var returnShow = "";
		                	for ( var showOne = 0; showOne < show.length; showOne++) {
		                		var showDetail = show[showOne];
//		                		var datePlay = "";
//		                		var value;
//		                		var date = new Date(showDetail.series.playData);
//		                		if(showDetail.name == '首播日'){
//		                			datePlay = showDetail.series.playData;
//		                		}else{
//		                			value = Number(showDetail.name.replace(/[^0-9]/ig,"")) - 1;
//		                			if(showDetail.name.indexOf('前') > -1){
//		                				date.setDate(date.getDate() - value)
//		                			}else{
//		                				date.setDate(date.getDate() + value)
//		                			}
//		                		}
//		                		datePlay = date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate();
		                		value = Number(showDetail.name.replace(/[^0-9]/ig,"")) - 1;
		                		
		                		if(showOne == 0){
		                			returnShow += showDetail.name + "<br/>";
		                		}
		                		if(showDetail.series.playData[value] != undefined){
		                			returnShow += showDetail.seriesName + "(" + showDetail.series.playData[value] + ")" + ":" + showDetail.value + "<br/>";
		                		}
							}
		                	return returnShow;
		                }
		            },
		            dataZoom: {
		    			show: true,
		    			realtime: false,
		    			// height:300,
		    			start: zoomStartPercent, //展示开始处(百分比)
		    			end: zoomEndPercent,	//展示结束处(百分比)
		                showDetail:true,//是否显示详情
		    		},
		    		grid:{
		    			x:50,//左边距
		    			y:30,//上边距
		    			x2:40,//右边距
		    			y2:60//下边距
		    		},
		            legend: {
		                data: legendData
		            },
		            calculable : false,
		            xAxis : [
		                {
		                    type : 'category',
		                    boundaryGap : false,
		                    splitLine: {show:false},
		                    data : xDateBake
		                }
		            ],
		            yAxis : [
		                {
		                    type : 'value'
		                }
		            ],
		            series : yDataAll
		        };
	        tvRatingsComparisonChart = option 
	        tvRatingsComparisonChartInit.setOption(option);
	    }
	);
}
//加载电视剧播放量对比图
function showComparisonChartOfTVplayChart(){
	require.config({
	    paths: {
	        echarts: projectURI.defcon + '/js/echarts'
	    }
	});
	require(
	    [
	        'echarts',
	        'echarts/chart/line'
	    ],
	    function (ec) {
	        // --- 折柱 ---
	    	comparisonChartOfTVplayChartInit = ec.init(document.getElementById('comparison_chart_of_TV_play'));
	        var option = {
		        	tooltip : {
		                trigger: 'axis'
		            },
		            legend: {
		                data:['邮件营销','联盟广告','视频广告','直接访问','搜索引擎']
		            },
		            calculable : false,
		            xAxis : [
		                {
		                    type : 'category',
		                    boundaryGap : false,
		                    splitLine: {show:false},
		                    data : xDateBake
		                }
		            ],
		            yAxis : [
		                {
		                    type : 'value',
		                    splitLine: {show:true}
		                }
		            ],
		            series : yDataAll
		        };
	        comparisonChartOfTVplayChart = option;
	        comparisonChartOfTVplayChartInit.setOption(option);
	    }
	);
}
//加载媒体关注度变化图
function showMediaAttentionChangeChart(xDateBake,yDataAll,legendData){
	var zoomStartPercent = 0
	var zoomEndPercent = 100
	if(yDataAll.length > 0){
		var startIndex = yDataAll[0].data.length > 30?yDataAll[0].data.length-31:0
		zoomStartPercent = startIndex/yDataAll[0].data.length*100
	}
	require.config({
	    paths: {
	        echarts: projectURI.defcon + '/js/echarts'
	    }
	});
	require(
	    [
	        'echarts',
	        'echarts/chart/line'
	    ],
	    function (ec) {
	        // --- 折柱 ---
	    	mediaAttentionChangeChartInit = ec.init(document.getElementById('media_attention_change_chart'));
	        var options = {
	        	    title: {
	        	    	text : '媒体关注度变化图'
	        	    },
	        	    legend: {
	        	        data:legendData
	        	    },
		        	tooltip : {
		                trigger: 'axis',
		                formatter: function(show){
		                	var returnShow = "";
		                	for ( var showOne = 0; showOne < show.length; showOne++) {
		                		var showDetail = show[showOne];
		                		var datePlay = "";
		                		var value;
		                		var date = new Date(showDetail.series.playData);
		                		if(showDetail.name == '首播日'){
		                			datePlay = showDetail.series.playData;
		                		}else{
		                			value = Number(showDetail.name.replace(/[^0-9]/ig,""));
		                			if(showDetail.name.indexOf('前') > -1){
		                				date.setDate(date.getDate() - value)
		                			}else{
		                				date.setDate(date.getDate() + value)
		                			}
		                		}
		                		datePlay = date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate();
		                		if(showOne == 0){
		                			returnShow += showDetail.name + "<br/>";
		                		}
		                		returnShow += showDetail.seriesName + "(" + datePlay + ")" + ":" + showDetail.value + "<br/>";
							}
		                	return returnShow;
		                }
		            },
		            dataZoom: {
		    			show: true,
		    			realtime: false,
		    			// height:300,
		    			start: zoomStartPercent, //展示开始处(百分比)
		    			end: zoomEndPercent,	//展示结束处(百分比)
		                showDetail:true,//是否显示详情
		    		},
		    		grid:{
		    			x:50,//左边距
		    			y:30,//上边距
		    			x2:40,//右边距
		    			y2:60//下边距
		    		},
		            calculable : false,
		            xAxis : [
		                {
		                    type : 'category',
		                    boundaryGap : false,
		                    splitLine: {show:false},
		                    data : xDateBake
		                }
		            ],
		            yAxis : [
		                {
		                    type : 'value',
		                    splitLine: {show:true}
		                }
		            ],
		            series : yDataAll
		        };
	        mediaAttentionChangeChart = options
	        mediaAttentionChangeChartInit.setOption(options);
	    }
	);
}
//加载公众影响力变化图
function showPublicInfluenceChangeChart(xDateBake,yDataAll,legendData){
	var zoomStartPercent = 0
	var zoomEndPercent = 100
	if(yDataAll.length > 0){
		var startIndex = yDataAll[0].data.length > 30?yDataAll[0].data.length-31:0
		zoomStartPercent = startIndex/yDataAll[0].data.length*100
	}
	require.config({
	    paths: {
	        echarts: projectURI.defcon + '/js/echarts'
	    }
	});

	require(
	    [
	        'echarts',
	        'echarts/chart/line'
	    ],
	    function (ec) {
	        // --- 折柱 ---
	    	publicInfluenceChangeChartInit = ec.init(document.getElementById('public_influence_change_chart'));
	        var option = {
	        	    title: {
	        	    	text : '公众影响力变化图'
	        	    },
	        	    legend: {
	        	        data:legendData
	        	    },
		        	tooltip : {
		                trigger: 'axis',
		                formatter: function(show){
		                	var returnShow = "";
		                	for ( var showOne = 0; showOne < show.length; showOne++) {
		                		var showDetail = show[showOne];
		                		var datePlay = "";
		                		var value;
		                		var date = new Date(showDetail.series.playData);
		                		if(showDetail.name == '首播日'){
		                			datePlay = showDetail.series.playData;
		                		}else{
		                			value = Number(showDetail.name.replace(/[^0-9]/ig,""));
		                			if(showDetail.name.indexOf('前') > -1){
		                				date.setDate(date.getDate() - value)
		                			}else{
		                				date.setDate(date.getDate() + value)
		                			}
		                		}
		                		datePlay = date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate();
		                		if(showOne == 0){
		                			returnShow += showDetail.name + "<br/>";
		                		}
		                		returnShow += showDetail.seriesName + "(" + datePlay + ")" + ":" + showDetail.value + "<br/>";
							}
		                	return returnShow;
		                }
		            },
		            dataZoom: {
		    			show: true,
		    			realtime: false,
		    			// height:300,
		    			start: zoomStartPercent, //展示开始处(百分比)
		    			end: zoomEndPercent,	//展示结束处(百分比)
		                showDetail:true,//是否显示详情
		    		},
		    		grid:{
		    			x:50,//左边距
		    			y:30,//上边距
		    			x2:40,//右边距
		    			y2:60//下边距
		    		},
		            calculable : false,
		            xAxis : [
		                {
		                	type : 'category',
		                    boundaryGap : false,
		                    splitLine: {show:false},
		                    data : xDateBake
		                }
		            ],
		            yAxis : [
		                {
		                    type : 'value'
		                }
		            ],
		            series : yDataAll
		        };
	        publicInfluenceChangeChart = option;
	        publicInfluenceChangeChartInit.setOption(option);
	    }
	);
}