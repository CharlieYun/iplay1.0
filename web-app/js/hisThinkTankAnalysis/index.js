/**
 * 历史智库-交互分析相关JS
 */
$(function(){
	
	//收视率、播放量范围限制
	$("#ratingStart,#ratingEnd").blur(function(){
		var rex = /^(([1-9]\d*(\.\d+)?)|(0\.\d+))$/
		var startValue = $("#ratingStart").val()
		var endValue = $("#ratingEnd").val()
		if(startValue != "" && !rex.test(startValue)){
			$("#ratingStart").val("")
			$(this).siblings(".error_tip").show()
			return
		}
		if(endValue != "" && !rex.test(endValue)){
			$("#ratingEnd").val("")
			$(this).siblings(".error_tip").show()
			return
		}
		$(this).siblings(".error_tip").hide()
		if(endValue != "" && startValue != "" && parseFloat(endValue) < parseFloat(startValue)){
			if($(this).attr("id").indexOf("Start") > -1){//开始输入框失去焦点
				$("#ratingStart").val(endValue)
			}else if($(this).attr("id").indexOf("End") > -1){//开始输入框失去焦点
				$("#ratingEnd").val(startValue)
			}
		}
	})
	$("#playNumStart,#playNumEnd").blur(function(){
		var rex = /^[1-9]\d*([\u4e07]|(\u5343\u4e07)|[\u4ebf])?$/
		var startValue = $("#playNumStart").val()
		var endValue = $("#playNumEnd").val()
		if(startValue != "" && !rex.test(startValue)){
			$("#playNumStart").val("")
			$(this).siblings(".error_tip").show()
			return
		}
		if(endValue != "" && !rex.test(endValue)){
			$("#playNumEnd").val("")
			$(this).siblings(".error_tip").show()
			return
		}
		$(this).siblings(".error_tip").hide()
		var startValueTemp,endValueTemp
		if(startValue != ""){
			startValueTemp = converPlayNum(startValue)
//			if(startValue.indexOf("亿") > -1){
//				startValueTemp = startValue.replace("亿","")+"00000000"
//			}else if(startValue.indexOf("千万") > -1){
//				startValueTemp = startValue.replace("千万","")+"0000000"
//			}else if(startValue.indexOf("万") > -1){
//				startValueTemp = startValue.replace("万","")+"0000"
//			}else{
//				startValueTemp = startValue
//			}
		}
		if(endValue != ""){
			endValueTemp = converPlayNum(endValue)
//			if(endValue.indexOf("亿") > -1){
//				endValueTemp = endValue.replace("亿","")+"00000000"
//			}else if(endValue.indexOf("千万") > -1){
//				endValueTemp = endValue.replace("千万","")+"0000000"
//			}else if(endValue.indexOf("万") > -1){
//				endValueTemp = endValue.replace("万","")+"0000"
//			}else{
//				endValueTemp = endValue
//			}
		}
		if(endValueTemp && startValueTemp && parseFloat(endValueTemp) < parseFloat(startValueTemp)){
			if($(this).attr("id").indexOf("Start") > -1){//开始输入框失去焦点
				$("#playNumStart").val(endValue)
			}else if($(this).attr("id").indexOf("End") > -1){//开始输入框失去焦点
				$("#playNumEnd").val(startValue)
			}
		}
	})
	
	//分类鼠标移入、移出事件
	$(document).on("mouseover mouseout",".analysis_search_condition .pConditionTag",function(event){
		var thisValue = $(this).attr("data-value").replace("p_","")
		if(event.type=='mouseover'){//移入
			$(this).siblings(".c_"+thisValue).show()
		}else{//移出
			$(this).siblings(".c_"+thisValue).hide()
		}
	})
	
	//浮动选择元素移入、移出事件
	$(document).on("mouseover mouseout",".float_check_container",function(event){
		if(event.type=='mouseover'){//移入
			$(this).show()
		}else{//移出
			$(this).hide()
		}
	})
	
	//主创人员选择按钮点击事件
	$("#creatorContainer button").click(function(){
		$(this).addClass("active")
		$(this).parent().siblings().find("button").removeClass("active")
		$("#creatorType").val($(this).attr("data-value"))
	})
	//主创人员搜索提示
	creatorInfoAutoComplete("searchCreator",$("#creatorType").val(),"normal",8,[],selectCreatorCallbackFun)
	//时间范围日历框
	$('#timeRangeStart,#timeRangeEnd').datetimepicker({
		minView: "month", //选择日期后，不会再跳转去选择时分秒 　　
		format: "yyyy-mm-dd", //选择日期后，文本框显示的日期格式 　　
		language: 'zh-CN', //汉化 　　
		autoclose:true //选择日期后自动关闭
	})
	
	//勾选框选择事件
	$(document).on("change",".float_check_container input[type='checkbox']",function(){
		var thisValue = $(this).attr("data-value")
		if($(this).is(":checked")){//选中
			if(thisValue == "all"){//全选
				$(this).parent().siblings("label").find("input[type='checkbox']").prop("checked",true).attr("disabled","disabled")
				var thisDataName = $(this).attr("data-name").split("_")
				//删除已选的子元素
				$("#selected_container span").each(function(){
					if($(this).find("i").attr("data-value").indexOf("p_"+thisDataName[1]+"_c") > -1){
						$(this).remove()
					}
				})
				$("#selected_container").append("<span class='selected_condition'>"+thisDataName[0]+"(全部)<i class='glyphicon glyphicon-remove' data-value='p_"+thisDataName[1]+"_all'></i></span>")
			}else{
				var thisText = $(this).parent().text()
				$("#selected_container").append("<span class='selected_condition'>"+thisText+"<i class='glyphicon glyphicon-remove' data-value='"+thisValue+"'></i></span>")
			}
		}else{//取消选中
			if(thisValue == "all"){//全选
				$(this).parent().siblings("label").find("input[type='checkbox']").prop("checked",false).removeAttr("disabled")
				var thisDataName = $(this).attr("data-name").split("_")
				$("#selected_container i[data-value='p_"+thisDataName[1]+"_all']").parent().remove()
			}else{
				$("#selected_container i[data-value='"+thisValue+"']").parent().remove()
			}
		}
	})
	
	//已选择条件删除事件
	$(document).on("click","#selected_container i",function(){
		var thisValue = $(this).attr("data-value")
		if(thisValue.indexOf("_all") > -1){//全部类型
			var thisValueTemp = thisValue.replace("_all","").replace("p_","")
			$(".analysis_search_condition .c_"+thisValueTemp+" input[type='checkbox']").prop("checked",false).removeAttr("disabled")
		}else{
			$(".float_check_container input[type='checkbox'][data-value='"+thisValue+"']").prop("checked",false)
		}
		$(this).parent().remove()
	})
	
	//table表格排序点击事件
	$(document).on("click","#tableContent .sort",function(){
		var thisColumnIndex = $(this).prevAll("th").length

		var allSortTrTag = []
		$(this).parents("table").find("tbody tr").each(function(index){
			allSortTrTag[index] = $(this).clone(true)
		})
		
		if((!$(this).find("i").hasClass("glyphicon-chevron-up") && !$(this).find("i").hasClass("glyphicon-chevron-down")) || $(this).find("i").hasClass("glyphicon-chevron-up")){//降序
			allSortTrTag.sort(function(a,b){
				if(b.find("td:eq("+thisColumnIndex+")").text() == a.find("td:eq("+thisColumnIndex+")").text()){
					return 0
				}else if(b.find("td:eq("+thisColumnIndex+")").text() > a.find("td:eq("+thisColumnIndex+")").text()){
					return 1
				}else if(b.find("td:eq("+thisColumnIndex+")").text() < a.find("td:eq("+thisColumnIndex+")").text()){
					return -1
				}
//				return b.find("td:eq("+thisColumnIndex+")").text() - a.find("td:eq("+thisColumnIndex+")").text()
			})
			$(this).find("i").removeClass("glyphicon-chevron-up").addClass("glyphicon-chevron-down")
		}else if($(this).find("i").hasClass("glyphicon-chevron-down")){//升序
			allSortTrTag.sort(function(a,b){
				if(a.find("td:eq("+thisColumnIndex+")").text() == b.find("td:eq("+thisColumnIndex+")").text()){
					return 0
				}else if(a.find("td:eq("+thisColumnIndex+")").text() > b.find("td:eq("+thisColumnIndex+")").text()){
					return 1
				}else if(a.find("td:eq("+thisColumnIndex+")").text() < b.find("td:eq("+thisColumnIndex+")").text()){
					return -1
				}
//				return a.find("td:eq("+thisColumnIndex+")").text() - b.find("td:eq("+thisColumnIndex+")").text()
			})
			$(this).find("i").removeClass("glyphicon-chevron-down").addClass("glyphicon-chevron-up")
		}
		$(this).siblings(".sort").find("i").removeClass("glyphicon-chevron-up").removeClass("glyphicon-chevron-down")
		$(this).parents("table").find("tbody tr").remove()
		for(var i=0;i<allSortTrTag.length;i++){
			$(this).parents("table").find("tbody").append(allSortTrTag[i])
		}
	})
})

/**
 * 搜索主创选择回调函数
 * @param selectId : 选择主创ID
 * @param selectName : 选择主创名称
 */
function selectCreatorCallbackFun(selectId,selectName){
	var typeButton = $("#searchCreator").parent().siblings().find("button[class*='active']")
	var creatorType = typeButton.attr("data-value")
	var creatorTypeName = typeButton.text()
	$("#selected_container").append("<span class='selected_condition'>"+selectName+"("+creatorTypeName+")<i class='glyphicon glyphicon-remove' data-value='creator_"+creatorType+"_"+selectId+"'></i></span>")
	$("#searchCreator,#searchCreator-objId").val("")
}

/**
 * 转换播放量
 * @param playNum : 播放量(可能包含单位)
 */
function converPlayNum(playNum){
	var playNumTemp = ""
	if(playNum.indexOf("亿") > -1){
		playNumTemp = playNum.replace("亿","")+"00000000"
	}else if(playNum.indexOf("千万") > -1){
		playNumTemp = playNum.replace("千万","")+"0000000"
	}else if(playNum.indexOf("万") > -1){
		playNumTemp = playNum.replace("万","")+"0000"
	}else{
		playNumTemp = playNum
	}
	return playNumTemp
}

/**
 * 获取搜索条件
 */
function getSearchCondition(){
	//是否有筛选条件
	var hasSelectCondition = false
	//题材分类
	var themeArr = new Array()
	$("#themeContainer input[type='checkbox']").each(function(){
		if($(this).attr("data-value") != "all" && $(this).is(":checked")){
			themeArr.push($(this).attr("data-value"))
		}
	})
	if(themeArr.length > 0){
		hasSelectCondition = true
	}
	//平台分类
	var platformArr = new Array()
	$("#platformContainer input[type='checkbox']").each(function(){
		if($(this).attr("data-value") != "all" && $(this).is(":checked")){
			platformArr.push($(this).attr("data-value"))
		}
	})
	if(platformArr.length > 0){
		hasSelectCondition = true
	}
	//受众分类
	var audienceArr = new Array()
	$("#audienceContainer input[type='checkbox']").each(function(){
		if($(this).attr("data-value") != "all" && $(this).is(":checked")){
			audienceArr.push($(this).attr("data-value"))
		}
	})
	if(audienceArr.length > 0){
		hasSelectCondition = true
	}
	//主创分类
	var creatorArr = new Array()
	$("#selected_container span i").each(function(){
		if($(this).attr("data-value").indexOf("creator") > -1){
			creatorArr.push($(this).attr("data-value"))
		}
	})
	if(creatorArr.length > 0){
		hasSelectCondition = true
	}
	//收视率范围
	var ratingStart = $("#ratingStart").val()
	if(ratingStart && ratingStart != ""){
		hasSelectCondition = true
	}
	var ratingEnd = $("#ratingEnd").val()
	if(ratingEnd && ratingEnd != ""){
		hasSelectCondition = true
	}
	//播放量范围
	var playNumStart = converPlayNum($("#playNumStart").val())
	if(playNumStart && playNumStart != ""){
		hasSelectCondition = true
	}
	var playNumEnd = converPlayNum($("#playNumEnd").val())
	if(playNumEnd && playNumEnd != ""){
		hasSelectCondition = true
	}
	//时间范围
	var timeRangeStart = $("#timeRangeStart").val()
	if(timeRangeStart && timeRangeStart != ""){
		hasSelectCondition = true
	}
	var timeRangeEnd = $("#timeRangeEnd").val()
	if(timeRangeEnd && timeRangeEnd != ""){
		hasSelectCondition = true
	}
	return {hasSelectCondition:hasSelectCondition,themeArr:themeArr.toString(),platformArr:platformArr.toString(),audienceArr:audienceArr.toString(),creatorArr:creatorArr.toString(),
		ratingStart:ratingStart,ratingEnd:ratingEnd,playNumStart:playNumStart,playNumEnd:playNumEnd,timeRangeStart:timeRangeStart,timeRangeEnd:timeRangeEnd}
}

/**
 * 根据搜索条件获取分析信息
 */
function ajaxSearchAnalysisInfoByCondition(){
	var condition = getSearchCondition()
	if(condition.hasSelectCondition){
		$("#tableContent").empty()
		$("#tableContentLoading").show()
		$.ajax({
			type:"post",
			url:projectURITemp.defcon+"/hisThinkTankAnalysis/ajaxSearchAnalysisInfoByCondition",
			data:condition,
			success:function(data){
//				$("#tableContent").empty().append(data).show().siblings().hide()
//				$("#tableContentLoading").hide()
				var evaluateNum = data.evaluateNum
				var teleplayIdArr = data.teleplayIdArr
				var chartData = data.analysisResult.chartData
				if(evaluateNum == 0){
					if(teleplayIdArr.length > 0){
						$("#resultContainer #oneDimensionContent").html("电视剧总数："+teleplayIdArr.length).show().siblings().hide()
					}else{
						$("#resultContainer #emptyContent").show()
					}
				}else{
					$("#resultContainer #chartContent").empty().show()
					if(evaluateNum == 1){
						drawPieChart(chartData)
					}else if(evaluateNum == 2){
						drawBarChart(chartData,"chartContent")
					}else if(evaluateNum == 3){
						var ulTabStr = "<ul class='nav nav-tabs'>"
						var chartDivArr = new Array()
						var chartDivIdArr = new Array()
						for(var key in chartData){
							var keyArr = key.split("|")
							ulTabStr += "<li role='presentation'><a data-value='"+key+"' href='javascript:void(0)' onclick='exchangeCreatorTag(this)'>"+chartData[key].creatorName+"</a></li>"
							chartDivArr.push("<div id='"+key+"' style='width:978px;height:300px;display:none;'></div>")
							chartDivIdArr.push({
								elementId:key,
								chartData:chartData[key].chartData
							})
						}
						ulTabStr += "</ul>"
						$("#resultContainer #chartContent").append(ulTabStr)
						$("#resultContainer #chartContent ul li:eq(0)").addClass("active")
						//添加图标div
						for(var i=0;i<chartDivArr.length;i++){
							$("#resultContainer #chartContent").append(chartDivArr[i])
							drawBarChart(chartDivIdArr[i].chartData,chartDivIdArr[i].elementId)
							if(i == 0){
								$(document.getElementById(chartDivIdArr[i].elementId)).show()
							}
						}
					}
				}
			}
		})
		
		//显示表格信息
		$("#tableContent").empty()
		$("#tableContentLoading").show()
		$.ajax({
			type:"post",
			url:projectURITemp.defcon+"/hisThinkTankAnalysis/ajaxSearchTeleplayAnalysisInfoByCondition",
			data:condition,
			success:function(data){
				$("#tableContent").empty().append(data).show()
				$("#tableContentLoading").hide()
			}
		})
	}
}

/**
 * 切换主创人员tab标签
 * @param obj : 当前点击对象
 */
function exchangeCreatorTag(obj){
	var thisValue = $(obj).attr("data-value")
	$(obj).parent().addClass("active").siblings().removeClass("active")
	$(document.getElementById(thisValue)).show().siblings("div").hide()
}

function showOrHideMoreActorInfo(obj,type){
	var tbodyTag = $(obj).parents("tbody")
	if(type == "show"){
		tbodyTag.find(".actors").show()
		$(obj).attr("onclick","showOrHideMoreActorInfo(this,'hide')").find("i").removeClass("glyphicon-chevron-down").addClass("glyphicon-chevron-up")
	}else if(type == "hide"){
		tbodyTag.find(".actors:gt(10)").hide()
		$(obj).attr("onclick","showOrHideMoreActorInfo(this,'show')").find("i").removeClass("glyphicon-chevron-up").addClass("glyphicon-chevron-down")
	}
}

/**
 * 绘制饼状图
 * @param chartData : 图表数据
 */
function drawPieChart(chartData){
	require.config({
	    paths: {
	        echarts: projectURI.defcon + '/js/echarts'
	    }
	});
	require(
	    [
	        'echarts',
	        'echarts/chart/pie',
	    ],
	    function (ec) {
	        //--- 折柱 ---
	        var myChart = ec.init(document.getElementById('chartContent'));
	        myChart.setOption({
    		    tooltip : {
    		        trigger: 'item',
    		        formatter: "{a} <br/>{b} : {c} ({d}%)"
    		    },
    		    calculable : true,
    		    series : [
    		        {
    		            name:'',
    		            type:'pie',
    		            radius : '55%',
    		            center: ['50%', '60%'],
    		            data:chartData
    		        }
    		    ]
	        });
	    }
	);
}

/**
 * 绘制多为条形图
 * @param chartData : 图表数据
 * @param elementId : 容器元素ID
 */
function drawBarChart(chartData,elementId){
	var legendArr = new Array()
	var yaxisArr = new Array()
	var seriesDataMap = {}
	var seriesDataArr = new Array()
	for(var i=0;i<chartData.length;i++){
		if(legendArr.indexOf(chartData[i].legendName) < 0){
			legendArr.push(chartData[i].legendName)
		}
		if(yaxisArr.indexOf(chartData[i].categoryName) < 0){
			yaxisArr.push(chartData[i].categoryName)
		}
		var thisLegendData = seriesDataMap[chartData[i].legendName]
		if(thisLegendData == undefined){
			thisLegendData = []
		}
		thisLegendData.push(chartData[i].countNum)
		seriesDataMap[chartData[i].legendName] = thisLegendData
	}
	for(var key in seriesDataMap){
		seriesDataArr.push({
			name:key,
			type:'bar',
			stack:'总量',
			data:seriesDataMap[key]
		})
	}
	//画图
	require.config({
	    paths: {
	        echarts: projectURI.defcon + '/js/echarts'
	    }
	});
	require(
	    [
	        'echarts',
	        'echarts/chart/bar',
	    ],
	    function (ec) {
	        //--- 折柱 ---
	        var myChart = ec.init(document.getElementById(elementId));
	        myChart.setOption({
	        	 tooltip : {
        	        trigger: 'axis',
        	        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
        	            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
        	        }
        	    },
        	    legend: {
        	        data:legendArr
        	    },
        	    calculable : true,
        	    xAxis : [
    	             {
    	                 type : 'category',
    	                 data : yaxisArr
    	             }
    	         ],
    	         yAxis : [
	                  {
	                      type : 'value'
	                  }
	             ],
	             series:seriesDataArr
	        });
	    }
	);
}