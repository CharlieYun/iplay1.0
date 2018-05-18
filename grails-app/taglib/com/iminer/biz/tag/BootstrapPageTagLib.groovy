package com.iminer.biz.tag

class BootstrapPageTagLib {
	static namespace = 'bs'
	
	/**
	 * 普通分页标签
	 */
	def normalPageTag = { attr ->
		def showtotal=attr.boolean('showtotal')?:false
		def showSkip=attr.boolean('showSkip')?:false
		def total = attr.int('totalCount') ?: 0
		def action = (attr.action ?: (params.action ?: ""))
		def controller = (attr.controller ?: (params.controller ?: ""))
		def offset = params.int('offset') ?: 0
		def max = params.int('max')

		if (!offset) offset = (attr.int('offset') ?: 0)
		if (!max) max = (attr.int('max') ?: 10)
		
		int current = (offset / max) + 1
		int totalPage = (total % max)>0?(total/max+1):(total/max)
		
		int Length = 11	//最大长度
		int maxLength = attr.int('maxLength')?attr.int('maxLength')+1:Length+1
		int bothCurrent = 2 //当前页左右最多显示多少个
		int midLength = 6 //间隔多大时出现中间值           必须大于5
		int lessThanBothCurrent = 2 //不得小于bothCurrent
		
		//点击链接前缀
		String linkPrefix = '/'+grailsApplication.metadata['app.name']+'/'+controller+'/'+action+'?max='+max
		//点击链接相关请求参数
		String linkParams = ""
		params.each{ k,v ->
			if(k != 'controller' && k != 'action' && k != 'offset' && k != 'max'){
				linkParams += '&'+k+'='+v
			}
		}
		
		String outStr = ""
		//页数小总长度
		if(total&&totalPage<=maxLength){
			outStr += '<nav><ul class="pagination">'
			//上一页按钮
			String prePageLink = ""
			if(current == 1){
				outStr += '<li class="disabled"><a href="#">&laquo;</a></li>'
			}else{
				String prevOffset = '&offset='+((current - 2)*max)
				outStr += '<li><a href="'+linkPrefix+prevOffset+linkParams+'">&laquo;</a></li>'
			}
			//详细页码
			(1..totalPage).each {
				String currentOffset = '&offset='+((it - 1)*max)
				if(it == current){
					outStr += '<li class="active"><a href="#">'+it+'<span class="sr-only">(current)</span></a></li>'
				}else{
					outStr += '<li><a href="'+linkPrefix+currentOffset+linkParams+'">'+it+'<span class="sr-only">(current)</span></a></li>'
				}
			}
			//下一页按钮
			if(current == totalPage){
				outStr += '<li class="disabled"><a href="#">&raquo;</a></li>'
			}else{
				String lastOffset = '&offset='+(current*max)
				outStr += '<li><a href="'+linkPrefix+lastOffset+linkParams+'">&raquo;</a></li>'
			}
		}
		
		if(total&&totalPage>maxLength){
			outStr += '<nav><ul class="pagination">'
			
			if(current == 1){
				outStr += "<li class=\"disabled\"><a href=\"#\">&laquo;</a></li>"
			}else{
				String prevOffset = '&offset='+((current - 2)*max)
				outStr += '<li><a href="'+linkPrefix+prevOffset+linkParams+'">&laquo;</a></li>'
			}
			
			if((current-1)>bothCurrent){
				
				String firstOffset = '&offset=0'
				outStr += '<li><a href="'+linkPrefix+firstOffset+linkParams+'">1<span class="sr-only">(current)</span></a></li>'
				
				if(((current-bothCurrent)-1)>bothCurrent)//-------
					outStr += "<li><a href='#'>...</a></li>"
				if(((current-bothCurrent)-1)>midLength){//-------
					String currentOffset = "&offset=${(((current-bothCurrent-1)/2).asType(int)-1)*max}"
					outStr += '<li><a href="'+linkPrefix+currentOffset+linkParams+'">'+((current-bothCurrent-1)/2).asType(int)+'<span class="sr-only">(current)</span></a></li>'
				}
				int left = (current-bothCurrent)
				if(current>bothCurrent) left -= lessThanBothCurrent
				if(left<=1) left = 1
			}else{
				int i = current-1
				if((current-1)<=0)i=1
				(1..i).each {
					String currentOffset = '&offset='+((it - 1)*max)
					if(current == it){
						outStr += '<li class="active"><a href="#">'+it+'<span class="sr-only">(current)</span></a></li>'
					}else{
						outStr += '<li><a href="'+linkPrefix+currentOffset+linkParams+'">'+it+'<span class="sr-only">(current)</span></a></li>'
					}
					
				}
			}
			if(current!=1&&current!=totalPage)
				outStr += '<li class="active"><a href="#">'+current+'<span class="sr-only">(current)</span></a></li>'
			
			if((totalPage-current)>bothCurrent){
				int right = (current+bothCurrent)
				if(right>=totalPage) right = totalPage-1
				int index = 0
				((current+1)..right).each {
					index++
					if(current == it){
						outStr += '<li class="active"><a href="#">'+current+'<span class="sr-only">(current)</span></a></li>'
					}else {
						String currentOffset = '&offset='+((it - 1)*max)
						outStr += '<li><a href="'+linkPrefix+currentOffset+linkParams+'">'+it+'<span class="sr-only">(current)</span></a></li>'
					}
				}
				if((totalPage-(current+bothCurrent))>midLength){//------
					String currentOffset = '&offset='+((Math.round((totalPage-(current+index))/2+current+index)-1)*max)
					outStr += '<li><a href="'+linkPrefix+currentOffset+linkParams+'">'+(Math.round((totalPage-(current+index))/2+current+index))+'<span class="sr-only">(current)</span></a></li>'
				}
				if((totalPage-(current+bothCurrent))>bothCurrent)//-------
					outStr += "<li><a href='#'>...</a></li>"
				
				String lastOffset = '&offset='+((totalPage-1)*max)
				outStr += '<li><a href="'+linkPrefix+lastOffset+linkParams+'">'+totalPage+'<span class="sr-only">(current)</span></a></li>'
			}else{
				int i = current+1
				if((current+1)>=totalPage) i=totalPage
				(i..totalPage).each {
					if(current == it){
						outStr += '<li class="active"><a href="#">'+current+'<span class="sr-only">(current)</span></a></li>'
					}else{
						String currentOffset = '&offset='+((it-1)*max)
						outStr += '<li><a href="'+linkPrefix+currentOffset+linkParams+'">'+it+'<span class="sr-only">(current)</span></a></li>'
					}
				}
			}
			//下一页按钮
			if(current == totalPage){
				outStr += '<li class="disabled"><a href="#">&raquo;</a></li>'
			}else{
				String lastOffset = '&offset='+(current*max)
				outStr += '<li><a href="'+linkPrefix+lastOffset+linkParams+'">&raquo;</a></li>'
			}
		}
		def strSkip="";
		if(showSkip){
			strSkip="<li><a>到第&nbsp;&nbsp;<input type='text' style='width:30px;height:20px;border:none;text-align:right' value='${current}' onkeypress='if(event.keyCode==13) {var num=(this.value-1)*${max};if(num<0||this.value>${totalPage})return ;location.href=\"${linkPrefix}&offset=\"+num+\"${linkParams}\"}'/>页</a> </li>";
		}
		def pageJumpTabStr="";
		if(showtotal){
			pageJumpTabStr = "<li><a>${current}/${totalPage}(总数:${total})</a></li></ul></nav>"
		}else{
			pageJumpTabStr ="</ul></nav>";
		}
		
		out << outStr+strSkip+pageJumpTabStr
	
	}
}
