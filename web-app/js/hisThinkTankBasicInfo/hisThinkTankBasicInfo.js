var typ=$("#typ").val()
if(typ==1){
	//显示电视剧图表信息
	require.config({
	    paths: {
	        echarts: projectURI.defcon + '/js/echarts'
	    }
	});
	require(
	    [
	        'echarts',
	        'echarts/chart/bar',
	        'echarts/chart/line'
	    ],
	    function (ec) {
	    	var zoomStartPercent = 0
	    	var zoomEndPercent = 100
	    	if(p['ppList'].length > 0){
	    		var startIndex = p['ppList'].length > 30?p['ppList'].length-31:0
	    		zoomStartPercent = startIndex/p['ppList'].length*100
	    	}
	        //--- 折柱 ---
	        var myChart = ec.init(document.getElementById('teleplayPraise'));
	        myChart.setOption({
	        	grid:{
	        		borderWidth : 0
	        	},
	        	tooltip : {
	                trigger: 'axis'
	            },
	            dataZoom: {
	    			show: true,
	    			realtime: false,
	    			// height:300,
	    			start: zoomStartPercent, //展示开始处(百分比)
	    			end: zoomEndPercent,	//展示结束处(百分比)
	                showDetail:true,//是否显示详情
	    		},
	            legend: {
	                data:['好评率']
	            },
	            toolbox: {
	                show : true,
	                feature : {
	                    mark : {show: true},
	                    dataView : {show: true, readOnly: false},
	                    magicType : {show: true, type: ['line', 'bar', 'stack', 'tiled']},
	                    restore : {show: true},
	                    saveAsImage : {show: true}
	                }
	            },
	            calculable : false,
	            xAxis : [
	                {
	                    type : 'category',
	                    boundaryGap : false,
	                    splitLine: {show:false},
	                    data : p['pdList']
	                }
	            ],
	            yAxis : [
	                {
	                    type : 'value'
	                }
	            ],
	            series : [
	                {
	                    name:'好评率',
	                    type:'line',
	                    stack: '总量',
	                    data:p['ppList']
	                }
	            ]
	        });
	        
	        zoomStartPercent = 0
	    	zoomEndPercent = 100
	    	if(hrList['hotRateList'].length > 0){
	    		var startIndex = hrList['hotRateList'].length > 30?hrList['hotRateList'].length-31:0
	    		zoomStartPercent = startIndex/hrList['hotRateList'].length*100
	    	}
	        var myChart1 = ec.init(document.getElementById('hotRate'));
	        myChart1.setOption({
	        	grid:{
	        		borderWidth : 0
	        	},
	        	tooltip : {
	                trigger: 'axis'
	            },
	            dataZoom: {
	    			show: true,
	    			realtime: false,
	    			// height:300,
	    			start: zoomStartPercent, //展示开始处(百分比)
	    			end: zoomEndPercent,	//展示结束处(百分比)
	                showDetail:true,//是否显示详情
	    		},
	            legend: {
	                data:['媒体关注度']
	            },
	            toolbox: {
	                show : true,
	                feature : {
	                    mark : {show: true},
	                    dataView : {show: true, readOnly: false},
	                    magicType : {show: true, type: ['line', 'bar', 'stack', 'tiled']},
	                    restore : {show: true},
	                    saveAsImage : {show: true}
	                }
	            },
	            calculable : true,
	            xAxis : [
	                {
	                    type : 'category',
	                    boundaryGap : false,
	                    splitLine: {show:false},
	                    data : hrList['hotRatePublicDateList']
	                }
	            ],
	            yAxis : [
	                {
	                    type : 'value'
	                }
	            ],
	            series : [
	                {
	                    name:'媒体关注度',
	                    type:'line',
	                    stack: '总量',
	                    data:hrList['hotRateList']
	                }
	            ]
	        });
	        
	        zoomStartPercent = 0
	    	zoomEndPercent = 100
	    	if(hrList['publicInfluenceList'].length > 0){
	    		var startIndex = hrList['publicInfluenceList'].length > 30?hrList['publicInfluenceList'].length-31:0
	    		zoomStartPercent = startIndex/hrList['publicInfluenceList'].length*100
	    	}
	        var myChart2 = ec.init(document.getElementById('publicInfluence'));
	        myChart2.setOption({
	        	grid:{
	        		borderWidth : 0
	        	},
	        	tooltip : {
	                trigger: 'axis'
	            },
	            dataZoom: {
	    			show: true,
	    			realtime: false,
	    			// height:300,
	    			start: zoomStartPercent, //展示开始处(百分比)
	    			end: zoomEndPercent,	//展示结束处(百分比)
	                showDetail:true,//是否显示详情
	    		},
	            legend: {
	                data:['公众影响力']
	            },
	            toolbox: {
	                show : true,
	                feature : {
	                    mark : {show: true},
	                    dataView : {show: true, readOnly: false},
	                    magicType : {show: true, type: ['line', 'bar', 'stack', 'tiled']},
	                    restore : {show: true},
	                    saveAsImage : {show: true}
	                }
	            },
	            calculable : true,
	            xAxis : [
	                {
	                    type : 'category',
	                    boundaryGap : false,
	                    splitLine: {show:false},
	                    data : hrList['hotRatePublicDateList']
	                }
	            ],
	            yAxis : [
	                {
	                    type : 'value'
	                }
	            ],
	            series : [
	                {
	                    name:'公众影响力',
	                    type:'line',
	                    stack: '总量',
	                    data:hrList['publicInfluenceList']
	                }
	            ]
	        });
	        
	        //收视率
	        zoomStartPercent = 0
	    	zoomEndPercent = 100
	    	
	    	var yDataAll = [];
			var legendData = [];
			var xDate = [];
	    	
	    	for(var i in t35List){
	    		var yData = {};
				yData.id = i;
				yData.name = t35List[i].channel_name;
				yData.type = 'line';
				yData.data = t35List[i].teleplay_audience35;
				legendData.push(yData.name);
				yDataAll.push(yData);
				xDate = t35List[i].teleplay_audience_date;
	    	}
	    	
	    	if(yDataAll.length == 0){
	    		yDataAll = [{}]
	    	}
	    	
	    	if(xDate.length > 0){
	    		var startIndex = xDate.length > 30?xDate.length-31:0
	    		zoomStartPercent = startIndex/xDate.length*100
	    	}
	        var myChart3 = ec.init(document.getElementById('telAudienceRating'));
	        myChart3.setOption({
	        	grid:{
	        		borderWidth : 0
	        	},
		        tooltip : {
		            trigger: 'axis'
		        },
	            dataZoom: {
	    			show: true,
	    			realtime: false,
	    			// height:300,
	    			start: zoomStartPercent, //展示开始处(百分比)
	    			end: zoomEndPercent,	//展示结束处(百分比)
	                showDetail:true,//是否显示详情
	    		},
		        legend: {
		            data:legendData
		        },
		        toolbox: {
		            show : true,
		            feature : {
		                mark : {show: true},
		                dataView : {show: true, readOnly: false},
		                magicType : {show: true, type: ['line', 'bar', 'stack', 'tiled']},
		                restore : {show: true},
		                saveAsImage : {show: true}
		            }
		        },
		        calculable : false,
		        xAxis : [
		            {
		                type : 'category',
		                boundaryGap : false,
	                    splitLine: {show:false},
		                data : xDate
		            }
		        ],
		        yAxis : [
		            {
		                type : 'value'
		            }
		        ],
		        series : yDataAll
	        });
	    }
	);
}else if(typ==2){			
	//显示网剧图表信息
	require.config({
	    paths: {
	        echarts: projectURI.defcon + '/js/echarts'
	    }
	});
	require(
	    [
	        'echarts',
	        'echarts/chart/bar',
	        'echarts/chart/line'
	    ],
	    function (ec) {
	    	var zoomStartPercent = 0
	    	var zoomEndPercent = 100
	    	if(p['ppList'].length > 0){
	    		var startIndex = p['ppList'].length > 30?p['ppList'].length-31:0
	    		zoomStartPercent = startIndex/p['ppList'].length*100
	    	}
	        //--- 折柱 ---
	        var myChart = ec.init(document.getElementById('teleplayPraise'));
	        myChart.setOption({
	        	grid:{
	        		borderWidth : 0
	        	},
	        	tooltip : {
	                trigger: 'axis'
	            },
	            dataZoom: {
	    			show: true,
	    			realtime: false,
	    			// height:300,
	    			start: zoomStartPercent, //展示开始处(百分比)
	    			end: zoomEndPercent,	//展示结束处(百分比)
	                showDetail:true,//是否显示详情
	    		},
	            legend: {
	                data:['好评率']
	            },
	            toolbox: {
	                show : true,
	                feature : {
	                    mark : {show: true},
	                    dataView : {show: true, readOnly: false},
	                    magicType : {show: true, type: ['line', 'bar', 'stack', 'tiled']},
	                    restore : {show: true},
	                    saveAsImage : {show: true}
	                }
	            },
	            calculable : true,
	            xAxis : [
	                {
	                    type : 'category',
	                    boundaryGap : false,
	                    splitLine: {show:false},
	                    data : p['pdList']
	                }
	            ],
	            yAxis : [
	                {
	                    type : 'value'
	                }
	            ],
	            series : [
	                {
	                    name:'好评率',
	                    type:'line',
	                    stack: '总量',
	                    data:p['ppList']
	                }
	            ]
	        });
	        
	        zoomStartPercent = 0
	    	zoomEndPercent = 100
	    	if(hrList['hotRateList'].length > 0){
	    		var startIndex = hrList['hotRateList'].length > 30?hrList['hotRateList'].length-31:0
	    		zoomStartPercent = startIndex/hrList['hotRateList'].length*100
	    	}
	        var myChart1 = ec.init(document.getElementById('hotRate'));
	        myChart1.setOption({
	        	grid:{
	        		borderWidth : 0
	        	},
	        	tooltip : {
	                trigger: 'axis'
	            },
	            dataZoom: {
	    			show: true,
	    			realtime: false,
	    			// height:300,
	    			start: zoomStartPercent, //展示开始处(百分比)
	    			end: zoomEndPercent,	//展示结束处(百分比)
	                showDetail:true,//是否显示详情
	    		},
	            legend: {
	                data:['媒体关注度']
	            },
	            toolbox: {
	                show : true,
	                feature : {
	                    mark : {show: true},
	                    dataView : {show: true, readOnly: false},
	                    magicType : {show: true, type: ['line', 'bar', 'stack', 'tiled']},
	                    restore : {show: true},
	                    saveAsImage : {show: true}
	                }
	            },
	            calculable : true,
	            xAxis : [
	                {
	                    type : 'category',
	                    boundaryGap : false,
	                    splitLine: {show:false},
	                    data : hrList['hotRatePublicDateList']
	                }
	            ],
	            yAxis : [
	                {
	                    type : 'value'
	                }
	            ],
	            series : [
	                {
	                    name:'媒体关注度',
	                    type:'line',
	                    stack: '总量',
	                    data:hrList['hotRateList']
	                }
	            ]
	        });
	        
	        zoomStartPercent = 0
	    	zoomEndPercent = 100
	    	if(hrList['publicInfluenceList'].length > 0){
	    		var startIndex = hrList['publicInfluenceList'].length > 30?hrList['publicInfluenceList'].length-31:0
	    		zoomStartPercent = startIndex/hrList['publicInfluenceList'].length*100
	    	}
	        var myChart2 = ec.init(document.getElementById('publicInfluence'));
	        myChart2.setOption({
	        	grid:{
	        		borderWidth : 0
	        	},
	        	tooltip : {
	                trigger: 'axis'
	            },
	            dataZoom: {
	    			show: true,
	    			realtime: false,
	    			// height:300,
	    			start: zoomStartPercent, //展示开始处(百分比)
	    			end: zoomEndPercent,	//展示结束处(百分比)
	                showDetail:true,//是否显示详情
	    		},
	            legend: {
	                data:['公众影响力']
	            },
	            toolbox: {
	                show : true,
	                feature : {
	                    mark : {show: true},
	                    dataView : {show: true, readOnly: false},
	                    magicType : {show: true, type: ['line', 'bar', 'stack', 'tiled']},
	                    restore : {show: true},
	                    saveAsImage : {show: true}
	                }
	            },
	            calculable : true,
	            xAxis : [
	                {
	                    type : 'category',
	                    boundaryGap : false,
	                    splitLine: {show:false},
	                    data : hrList['hotRatePublicDateList']
	                }
	            ],
	            yAxis : [
	                {
	                    type : 'value'
	                }
	            ],
	            series : [
	                {
	                    name:'公众影响力',
	                    type:'line',
	                    stack: '总量',
	                    data:hrList['publicInfluenceList']
	                }
	            ]
	        });
	        
	        //播放量
//	        zoomStartPercent = 0
//	    	zoomEndPercent = 100
//	    	
//	    	var yDataAll = [];
//			var legendData = [];
//			var xDate = [];
//			
//			for(var i in networkDramaPlayList){
//	    		var yData = {};
//				yData.id = i;
//				yData.name = networkDramaPlayList[i].networkDramaPlay_name;
//				yData.type = 'line';
//				yData.data = networkDramaPlayList[i].networkDramaPlay_value;
//				legendData.push(yData.name);
//				yDataAll.push(yData);
//				xDate = networkDramaPlayList[i].networkDramaPlay_date;
//	    	}
//			if(yDataAll.length == 0){
//	    		yDataAll = [{}]
//	    	}
//	    	
//	    	if(xDate.length > 0){
//	    		var startIndex = xDate.length > 30?xDate.length-31:0
//	    		zoomStartPercent = startIndex/xDate.length*100
//	    	}
//	    	
//	    	
//	    	//收视率
	        zoomStartPercent = 0
	    	zoomEndPercent = 100
	    	
	    	var yDataAll = [];
			var legendData = [];
			var xDate = [];
	    	
	    	for(var i in networkDramaPlayList){
	    		var yData = {};
				yData.id = i;
				yData.name = networkDramaPlayList[i].channel_name;
				yData.type = 'line';
				yData.data = networkDramaPlayList[i].teleplay_audience35;
				legendData.push(yData.name);
				yDataAll.push(yData);
				xDate = networkDramaPlayList[i].teleplay_audience_date;
	    	}
	    	
	    	if(yDataAll.length == 0){
	    		yDataAll = [{}]
	    	}
	    	
	    	if(xDate.length > 0){
	    		var startIndex = xDate.length > 30?xDate.length-31:0
	    		zoomStartPercent = startIndex/xDate.length*100
	    	}
			
	        var myChart3 = ec.init(document.getElementById('telAudienceRating'));
	        myChart3.setOption({
	        	grid:{
	        		borderWidth : 0
	        	},
	        	tooltip : {
	                trigger: 'axis'
	            },
	            dataZoom: {
	    			show: true,
	    			realtime: false,
	    			// height:300,
	    			start: zoomStartPercent, //展示开始处(百分比)
	    			end: zoomEndPercent,	//展示结束处(百分比)
	                showDetail:true,//是否显示详情
	    		},
	            legend: {
	                data:legendData
	            },
	            toolbox: {
	                show : true,
	                feature : {
	                    mark : {show: true},
	                    dataView : {show: true, readOnly: false},
	                    magicType : {show: true, type: ['line', 'bar', 'stack', 'tiled']},
	                    restore : {show: true},
	                    saveAsImage : {show: true}
	                }
	            },
	            calculable : false,
	            xAxis : [
	                {
	                    type : 'category',
	                    boundaryGap : false,
	                    splitLine: {show:false},
	                    data : xDate
	                }
	            ],
	            yAxis : [
	                {
	                    type : 'value',
                    	axisLabel : {
                            formatter: '{value} M'
                        }
	                }
	            ],
	            series : yDataAll
	        });
	    }
	);
}else{			
	//电影信息
	require.config({
	    paths: {
	        echarts: projectURI.defcon + '/js/echarts'
	    }
	});
	require(
	    [
	        'echarts',
	        'echarts/chart/bar',
	        'echarts/chart/line'
	    ],
	    function (ec) {
	    	var zoomStartPercent = 0
	    	var zoomEndPercent = 100
	    	if(boList['boxOfficeDate'].length > 0){
	    		var startIndex = boList['boxOfficeDate'].length > 30?boList['boxOfficeDate'].length-31:0
	    		zoomStartPercent = startIndex/boList['boxOfficeDate'].length*100
	    	}	
	        //--- 折柱 ---
	    	var myChart3 = ec.init(document.getElementById('movieBoxOffice'));
	        myChart3.setOption({
	        	grid:{
	        		borderWidth : 0
	        	},
	        	tooltip : {
	                trigger: 'axis'
	            },
	            dataZoom: {
	    			show: true,
	    			realtime: false,
	    			// height:300,
	    			start: zoomStartPercent, //展示开始处(百分比)
	    			end: zoomEndPercent,	//展示结束处(百分比)
	                showDetail:true,//是否显示详情
	    		},
	            legend: {
	                data:['票房信息']
	            },
	            toolbox: {
	                show : true,
	                feature : {
	                    mark : {show: true},
	                    dataView : {show: true, readOnly: false},
	                    magicType : {show: true, type: ['line', 'bar', 'stack', 'tiled']},
	                    restore : {show: true},
	                    saveAsImage : {show: true}
	                }
	            },
	            calculable : true,
	            xAxis : [
	                {
	                    type : 'category',
	                    boundaryGap : false,
	                    splitLine: {show:false},
	                    data : boList['boxOfficeDate']
	                }
	            ],
	            yAxis : [
	                {
	                    type : 'value'
	                }
	            ],
	            series : [
	                {
	                    name:'票房信息',
	                    type:'line',
	                    stack: '总量',
	                    data:boList['boxOffice']
	                }
	            ]
	        });
	    	
	        zoomStartPercent = 0
	    	zoomEndPercent = 100
	    	if(p['pdList'].length > 0){
	    		var startIndex = p['ppList'].length > 30?p['ppList'].length-31:0
	    		zoomStartPercent = startIndex/p['ppList'].length*100
	    	}
	        var myChart = ec.init(document.getElementById('moviePraise'));
	        myChart.setOption({
	        	grid:{
	        		borderWidth : 0
	        	},
	        	tooltip : {
	                trigger: 'axis'
	            },
	            dataZoom: {
	    			show: true,
	    			realtime: false,
	    			// height:300,
	    			start: zoomStartPercent, //展示开始处(百分比)
	    			end: zoomEndPercent,	//展示结束处(百分比)
	                showDetail:true,//是否显示详情
	    		},
	            legend: {
	                data:['电影好评率']
	            },
	            toolbox: {
	                show : true,
	                feature : {
	                    mark : {show: true},
	                    dataView : {show: true, readOnly: false},
	                    magicType : {show: true, type: ['line', 'bar', 'stack', 'tiled']},
	                    restore : {show: true},
	                    saveAsImage : {show: true}
	                }
	            },
	            calculable : true,
	            xAxis : [
	                {
	                    type : 'category',
	                    boundaryGap : false,
	                    splitLine: {show:false},
	                    data : p['pdList']
	                }
	            ],
	            yAxis : [
	                {
	                    type : 'value'
	                }
	            ],
	            series : [
	                {
	                    name:'电影好评率',
	                    type:'line',
	                    stack: '总量',
	                    data:p['ppList']
	                }
	            ]
	        });
	        
	        zoomStartPercent = 0
	    	zoomEndPercent = 100
	    	if(hrList['hotRateList'].length > 0){
	    		var startIndex = hrList['hotRateList'].length > 30?hrList['hotRateList'].length-31:0
	    		zoomStartPercent = startIndex/hrList['hotRateList'].length*100
	    	}
	        var myChart1 = ec.init(document.getElementById('hotRate'));
	        myChart1.setOption({
	        	grid:{
	        		borderWidth : 0
	        	},
	        	tooltip : {
	                trigger: 'axis'
	            },
	            dataZoom: {
	    			show: true,
	    			realtime: false,
	    			// height:300,
	    			start: zoomStartPercent, //展示开始处(百分比)
	    			end: zoomEndPercent,	//展示结束处(百分比)
	                showDetail:true,//是否显示详情
	    		},
	            legend: {
	                data:['媒体关注度']
	            },
	            toolbox: {
	                show : true,
	                feature : {
	                    mark : {show: true},
	                    dataView : {show: true, readOnly: false},
	                    magicType : {show: true, type: ['line', 'bar', 'stack', 'tiled']},
	                    restore : {show: true},
	                    saveAsImage : {show: true}
	                }
	            },
	            calculable : true,
	            xAxis : [
	                {
	                    type : 'category',
	                    boundaryGap : false,
	                    splitLine: {show:false},
	                    data : hrList['hotRatePublicDateList']
	                }
	            ],
	            yAxis : [
	                {
	                    type : 'value'
	                }
	            ],
	            series : [
	                {
	                    name:'媒体关注度',
	                    type:'line',
	                    stack: '总量',
	                    data:hrList['hotRateList']
	                }
	            ]
	        });
	        
	        zoomStartPercent = 0
	    	zoomEndPercent = 100
	    	if(hrList['publicInfluenceList'].length > 0){
	    		var startIndex = hrList['publicInfluenceList'].length > 30?hrList['publicInfluenceList'].length-31:0
	    		zoomStartPercent = startIndex/hrList['publicInfluenceList'].length*100
	    	}
	        var myChart2 = ec.init(document.getElementById('publicInfluence'));
	        myChart2.setOption({
	        	grid:{
	        		borderWidth : 0
	        	},
	        	tooltip : {
	                trigger: 'axis'
	            },
	            dataZoom: {
	    			show: true,
	    			realtime: false,
	    			// height:300,
	    			start: zoomStartPercent, //展示开始处(百分比)
	    			end: zoomEndPercent,	//展示结束处(百分比)
	                showDetail:true,//是否显示详情
	    		},
	            legend: {
	                data:['公众影响力']
	            },
	            toolbox: {
	                show : true,
	                feature : {
	                    mark : {show: true},
	                    dataView : {show: true, readOnly: false},
	                    magicType : {show: true, type: ['line', 'bar', 'stack', 'tiled']},
	                    restore : {show: true},
	                    saveAsImage : {show: true}
	                }
	            },
	            calculable : true,
	            xAxis : [
	                {
	                    type : 'category',
	                    boundaryGap : false,
	                    splitLine: {show:false},
	                    data : hrList['hotRatePublicDateList']
	                }
	            ],
	            yAxis : [
	                {
	                    type : 'value'
	                }
	            ],
	            series : [
	                {
	                    name:'公众影响力',
	                    type:'line',
	                    stack: '总量',
	                    data:hrList['publicInfluenceList']
	                }
	            ]
	        });
	        
	    }
	);
}
