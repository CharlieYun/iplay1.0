/**autocomplete相关CSS引用**/

var projectURITemp = { version: "1.0.0" ,defcon:"/iplay"};

document.write('<link type="text/css" rel="stylesheet" href="'+projectURITemp.defcon+'/css/autoComplete/jquery.ui.autocomplete.css">')
document.write('<link type="text/css" rel="stylesheet" href="'+projectURITemp.defcon+'/css/autoComplete/jquery.ui.theme.css">')
document.write('<link type="text/css" rel="stylesheet" href="'+projectURITemp.defcon+'/css/autoComplete/jquery.ui.menu.css">')

/**autocomplete相关JS引用**/
document.write("<script type='text/javascript' src='"+projectURITemp.defcon+"/js/jquery-ui.min.js'></script>");


/**
 * 实体对象信息搜索
 * @param objId : 补全元素id
 * @param objectType : 对象类型
 * @param showType : 显示类型(normal:'常规，只显示名称';detail:'详细，显示导演等信息',nameAndYear:'名称+年份')
 * @param subLength : 截取长度
 * @param repeatArr : 去重元素数组
 * @param selectCallBackFun : 选择回调函数
 */
function objectInfoAutoComplete(objId,objectType,showType,subLength,repeatArr,selectCallBackFun){
	$( "#"+objId).autocomplete({
		minLength: 1,//输入最小长度触发
		delay:0, //响应延迟，毫秒为单位
		//autoFocus:true,//是否自动选中第一个
		//disabled:true,//停止complete
		//position:{ my : "right top", at: "right bottom" },//提示框出现位置
		scroll:true,
//		blurCallBackFun:movieInfoBlur,//绑定失去焦点事件
		source: function(request,response){
			$( "#"+objId).data( "ui-autocomplete" ).options.isRequest = true
			if($("#"+objId+"-objId").length > 0){
				$("#"+objId+"-objId").val("")
			}
			var excludeMovieIds = ""
			if(repeatArr != undefined && repeatArr.length > 0){
				for(var i=0;i<repeatArr.length;i++){
					if(repeatArr[i] != ""){
						excludeMovieIds += repeatArr[i] + ","
					}
				}
			}
			if(excludeMovieIds != ""){
				excludeMovieIds = excludeMovieIds.substring(0,excludeMovieIds.length-1)
			}
			$.ajax({
				type:"post",
				url:projectURITemp.defcon+"/common/autoComplete",
				data:{autoKey:request.term,objectType:objectType,excludeMovieIds:excludeMovieIds},
				success:function(data){
					$( "#"+objId).data( "ui-autocomplete" ).options.resultNum = data.length
					response( $.map( data, function( item ) {
						return {
							id: item.id,
							name: item.name,
							image:item.image,
							year:item.year,
							directors:item.directors,
							show_directors:item.show_directors,
							actors:item.actors,
							show_actors:item.show_actors
						}
					}));
				}
			})
		},
		/**
		focus: function( event, ui ) {
			$( "#"+objId ).val( ui.item.name );
			var objIdIndex = objId.split("-")
			if(objIdIndex.length > 1 && $("#selectObjectId-"+objIdIndex[1]).length > 0){
				$("#selectObjectId-"+objIdIndex[1]).val(ui.item.id)
			}
			return false;
		},**/
		select: function( event, ui ) {
			$( "#"+objId).data( "ui-autocomplete" ).options.needClose=true
			$( "#"+objId).val( ui.item.name );
			if($("#"+objId+"-objId").length > 0){
				$("#"+objId+"-objId").val(ui.item.id)
			}
			if(selectCallBackFun != undefined && selectCallBackFun != null && typeof(selectCallBackFun) == "function"){
				selectCallBackFun(ui.item.id,ui.item.name)
			}
			return false;
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		if(showType == "detail"){
			ul.attr("id","searchMoves")
			var autoKey = $( "#"+objId).val()
			var name = item.name.replace(autoKey,"<font color='red'>"+autoKey+"</font>")
			var appendStr = "<a href='javascript:void(0)' style='padding:0;height:52px;'><img width='34' height='49' src='"+item.image+"' alt='' />" +
							"<div><p>"+name
			if(item.year != undefined){
				appendStr += "（"+item.year+"）"
			}
			appendStr += "</p><p title='"+item.directors+"'>导演："+item.show_directors+"</p><p title='"+item.actors+"'>主演："+item.show_actors+"<span>电影</span></p></div></a>"
			return $( "<li>" )
				.append(appendStr)
				.appendTo( ul );
		}else if(showType == "normal"){
			var autoKey = $( "#"+objId).val()
			var name = item.name
			if(subLength != undefined){
				name = item.name.length > subLength ?item.name.substring(0,subLength-1)+"...":item.name
			}
			name = name.replace(autoKey,"<font color='red'>"+autoKey+"</font>")
			return $( "<li>" )
				.append( "<a href='javascript:void(0)'><span style='width:100%' class='auto_content_span' title='"+item.name+"' data-value='"+item.id+"'>"+name+"</span></a>" )
				.appendTo( ul );
		}else if(showType == "nameAndYear"){
			var movieYear = item.year
			if(!movieYear){
				movieYear = ""
			}else{
				movieYear = "("+movieYear+")"
			}
			var autoKey = $( "#"+objId).val()
			var showName = item.name + movieYear
			if(subLength != undefined){
				showName = showName.length > subLength ?showName.substring(0,subLength-1)+"...":showName
			}
			showName = showName.replace(autoKey,"<font color='red'>"+autoKey+"</font>")
//			var name = item.name.length > subLength ?item.name.substring(0,subLength-1)+"...":item.name
//			name = name.replace(autoKey,"<font color='red'>"+autoKey+"</font>")
			return $( "<li>" )
				.append( "<a href='javascript:void(0)'><span style='width:100%' class='auto_content_span' title='"+item.name+movieYear+"' data-value='"+item.id+"'>"+showName+"</span></a>" )
				.appendTo( ul );
		}else if(showType == "imgAndNameAndYear"){
			var movieYear = item.year
			if(!movieYear){
				movieYear = ""
			}else{
				movieYear = "("+movieYear+")"
			}
			var autoKey = $( "#"+objId).val()
			var showName = item.name + movieYear
			if(subLength != undefined){
				showName = showName.length > subLength ?showName.substring(0,subLength-1)+"...":showName
			}
			showName = showName.replace(autoKey,"<font color='red'>"+autoKey+"</font>")
			return $( "<li>" )
				.append( "<a href='javascript:void(0)' class='float:left'><img width='34' height='40' src='"+item.image+"' alt='' /><span style='' class='auto_content_span' title='"+item.name+movieYear+"' data-value='"+item.id+"'>"+showName+"</span></a>" )
				.appendTo( ul );
		}
	};

	//绑定改变事件
	$( "#"+objId).blur(function(){
		if($( "#"+objId).val() == "" && $("#"+objId+"-objId").length > 0){
			$("#"+objId+"-objId").val("")
		}
	})
}



/**
 * 主创对象信息搜索
 * @param objId : 补全元素id
 * @param detailType : 主创类型
 * @param showType : 显示类型(normal:'常规，只显示名称';detail:'详细，显示导演等信息',nameAndYear:'名称+年份')
 * @param subLength : 截取长度
 * @param repeatArr : 去重元素数组
 * @param selectCallBackFun : 选择回调函数
 */
function creatorInfoAutoComplete(objId,detailType,showType,subLength,repeatArr,selectCallBackFun){
	$( "#"+objId).autocomplete({
		minLength: 1,//输入最小长度触发
		delay:0, //响应延迟，毫秒为单位
		//autoFocus:true,//是否自动选中第一个
		//disabled:true,//停止complete
		//position:{ my : "right top", at: "right bottom" },//提示框出现位置
		scroll:true,
//		blurCallBackFun:movieInfoBlur,//绑定失去焦点事件
		source: function(request,response){
			$( "#"+objId).data( "ui-autocomplete" ).options.isRequest = true
			if($("#"+objId+"-objId").length > 0){
				$("#"+objId+"-objId").val("")
			}
			var excludeMovieIds = ""
			if(repeatArr != undefined && repeatArr.length > 0){
				for(var i=0;i<repeatArr.length;i++){
					if(repeatArr[i] != ""){
						excludeMovieIds += repeatArr[i] + ","
					}
				}
			}
			if(excludeMovieIds != ""){
				excludeMovieIds = excludeMovieIds.substring(0,excludeMovieIds.length-1)
			}
			$.ajax({
				type:"post",
				url:projectURITemp.defcon+"/common/autoComplete",
				data:{autoKey:request.term,objectType:'creator',detailType:detailType,excludeMovieIds:excludeMovieIds},
				success:function(data){
					$( "#"+objId).data( "ui-autocomplete" ).options.resultNum = data.length
					response( $.map( data, function( item ) {
						return {
							id: item.id,
							name: item.name,
							image:item.image,
							year:item.year,
							directors:item.directors,
							show_directors:item.show_directors,
							actors:item.actors,
							show_actors:item.show_actors
						}
					}));
				}
			})
		},
		/**
		focus: function( event, ui ) {
			$( "#"+objId ).val( ui.item.name );
			var objIdIndex = objId.split("-")
			if(objIdIndex.length > 1 && $("#selectObjectId-"+objIdIndex[1]).length > 0){
				$("#selectObjectId-"+objIdIndex[1]).val(ui.item.id)
			}
			return false;
		},**/
		select: function( event, ui ) {
			$( "#"+objId).data( "ui-autocomplete" ).options.needClose=true
			$( "#"+objId).val( ui.item.name );
			if($("#"+objId+"-objId").length > 0){
				$("#"+objId+"-objId").val(ui.item.id)
			}
			if(selectCallBackFun != undefined && selectCallBackFun != null && typeof(selectCallBackFun) == "function"){
				selectCallBackFun(ui.item.id,ui.item.name)
			}
			return false;
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		if(showType == "detail"){
			ul.attr("id","searchMoves")
			var autoKey = $( "#"+objId).val()
			var name = item.name.replace(autoKey,"<font color='red'>"+autoKey+"</font>")
			var appendStr = "<a href='javascript:void(0)' style='padding:0;height:52px;'><img width='34' height='49' src='"+item.image+"' alt='' />" +
							"<div><p>"+name
			if(item.year != undefined){
				appendStr += "（"+item.year+"）"
			}
			appendStr += "</p><p title='"+item.directors+"'>导演："+item.show_directors+"</p><p title='"+item.actors+"'>主演："+item.show_actors+"<span>电影</span></p></div></a>"
			return $( "<li>" )
				.append(appendStr)
				.appendTo( ul );
		}else if(showType == "normal"){
			if(subLength == undefined){
				subLength = 11
			}
			var autoKey = $( "#"+objId).val()
			var name = item.name.length > subLength ?item.name.substring(0,subLength-1)+"...":item.name
			name = name.replace(autoKey,"<font color='red'>"+autoKey+"</font>")
			return $( "<li>" )
				.append( "<a href='javascript:void(0)'><span style='width:100%' class='auto_content_span' title='"+item.name+"' data-value='"+item.id+"'>"+name+"</span></a>" )
				.appendTo( ul );
		}else if(showType == "nameAndYear"){
			if(subLength == undefined){
				subLength = 11
			}
			var movieYear = item.year
			if(!movieYear){
				movieYear = ""
			}else{
				movieYear = "("+movieYear+")"
			}
			var autoKey = $( "#"+objId).val()
			var showName = item.name + movieYear
			showName = showName.length > subLength ?showName.substring(0,subLength-1)+"...":showName
			showName = showName.replace(autoKey,"<font color='red'>"+autoKey+"</font>")
//			var name = item.name.length > subLength ?item.name.substring(0,subLength-1)+"...":item.name
//			name = name.replace(autoKey,"<font color='red'>"+autoKey+"</font>")
			return $( "<li>" )
				.append( "<a href='javascript:void(0)'><span style='width:100%' class='auto_content_span' title='"+item.name+movieYear+"' data-value='"+item.id+"'>"+showName+"</span></a>" )
				.appendTo( ul );
		}else if(showType == "imgAndNameAndYear"){
			if(subLength == undefined){
				subLength = 11
			}
			var movieYear = item.year
			if(!movieYear){
				movieYear = ""
			}else{
				movieYear = "("+movieYear+")"
			}
			var autoKey = $( "#"+objId).val()
			var showName = item.name + movieYear
			showName = showName.length > subLength ?showName.substring(0,subLength-1)+"...":showName
			showName = showName.replace(autoKey,"<font color='red'>"+autoKey+"</font>")
			return $( "<li>" )
				.append( "<a href='javascript:void(0)' class='float:left'><img width='34' height='49' src='"+item.image+"' alt='' /><span style='width:100%' class='auto_content_span' title='"+item.name+movieYear+"' data-value='"+item.id+"'>"+showName+"</span></a>" )
				.appendTo( ul );
		}
	};

	//绑定改变事件
	$( "#"+objId).blur(function(){
		if($( "#"+objId).val() == "" && $("#"+objId+"-objId").length > 0){
			$("#"+objId+"-objId").val("")
		}
	})
}