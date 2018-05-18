//全局监听ajax方法，session过期后跳转登录页面
$(document).ajaxComplete(function(event, xhr, settings) {
	var sessionstatus = xhr.getResponseHeader("timeout"); // 通过XMLHttpRequest取得响应头，sessionstatus，
	if (sessionstatus && sessionstatus.indexOf("true") > -1) {
		var appname = xhr.getResponseHeader("appname");
		location.href = appname + "/"
	}else if(xhr.status == 500 || xhr.status == 404){//全局监听ajax方法，进行错误处理
		//确认导出弹出层
		var options = {containerId:"sysMessageBox",containerClass:"",alertTitle:"系统提示 ",
				title:"",content:"系统错误！",
				confirmCallBackFun:"",
				maskPopTag:"#popMask"}
		generateConfirmModel(options)
	}
});

var projectURI ={ version: "1.0.0" ,defcon:"/iplay"};

/**解决IE浏览器不支持数组indexOf方法**/
if (!Array.prototype.indexOf){
	 Array.prototype.indexOf = function (item) {
		var len = this.length;
		for (var i = 0; i < len; i++) {
			if (this[i] === item) {
				return i;
			}
		}
		return -1;
	}
}

/**
 * 日期格式化
 *
 */
Date.prototype.Format = function(format){
	var o = { 
		"M+" : this.getMonth()+1, //month 
		"d+" : this.getDate(), //day 
		"h+" : this.getHours(), //hour 
		"m+" : this.getMinutes(), //minute 
		"s+" : this.getSeconds(), //second 
		"q+" : Math.floor((this.getMonth()+3)/3), //quarter 
		"S" : this.getMilliseconds() //millisecond 
	} 

	if(/(y+)/.test(format)) { 
		format = format.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
	} 

	for(var k in o) { 
		if(new RegExp("("+ k +")").test(format)) { 
			format = format.replace(RegExp.$1, RegExp.$1.length==1 ? o[k] : ("00"+ o[k]).substr((""+ o[k]).length)); 
		} 
	} 
	return format; 
}

/**
 * 获取字符串长度，区分汉字、字母、数字等
 */
String.prototype.getLength = function(str){
	var len = 0;
	if(str == undefined){
		return 0;
	}
    for (var i = 0; i < str.length; i++) {
		var a = str.charAt(i);
		if (a.match(/[^\x00-\xff]/ig) != null) //i:表示区分大小写; g:表示全局模式匹配
		{
		    len += 2;
		}
		else
		{
		    len += 1;
		}
    }
    return len;
}

/**
 * 截取字符串长度，区分汉字、字母、数字等
 */
String.prototype.subLength = function(str,lengthLimit){
	var returnStr = ""
	if(str == undefined){
		return "";
	}
	var totalLength = 0
	for (var i = 0; i < str.length; i++) {
		var a = str.charAt(i);
		if (a.match(/[^\x00-\xff]/ig) != null) //i:表示区分大小写; g:表示全局模式匹配
		{
			totalLength += 2
		}
		else
		{
			totalLength += 1
		}
		if(totalLength > lengthLimit){
			break;
		}else{
			returnStr += a
		}
    }
	return returnStr
}

/**
 * 创建rgb格式的随机颜色
 */
function createRandomRGBColor() {
	return 'rgb(' + [
             Math.round(Math.random() * 255),
             Math.round(Math.random() * 255),
             Math.round(Math.random() * 255)
         ].join(',') + ')'
}
