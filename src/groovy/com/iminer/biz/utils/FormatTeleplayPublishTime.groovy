package com.iminer.biz.utils

import com.iminer.utils.DateTools

/**
 * FormatMoviePublishTime：格式化电影上映日期
 * @author   fangjie fangjie@iminer.com
 * @version  V1.0
 * @date 	 2015-6-25 下午5:22:51
 */
class FormatTeleplayPublishTime {

	static def format(String publishTime){
		String defaultTime = ''
		if(publishTime && publishTime != '' && publishTime != 'null'){
			DateTools dt = new DateTools()
			defaultTime = publishTime.split("/")[0]
			if(defaultTime.contains("(")){
				defaultTime = defaultTime.substring(0, defaultTime.indexOf("("))
			}
			String[] timeArray = defaultTime.split("-");
			switch (timeArray.length) {
				case 1:
					defaultTime = defaultTime+"-12-31";
					break;
				case 2:
					defaultTime = defaultTime+"-01";
					def nextMonth = dt.getBeforeTimeByM(defaultTime,'yyyy-MM-dd',-1,'yyyy-MM-dd').split("-")
					defaultTime = dt.getBeforeTimeByH(nextMonth[0]+"-"+nextMonth[1]+"-01",'yyyy-MM-dd',24,'yyyy-MM-dd')
					break;
				default:
					break;
			}
		}
		return defaultTime
	}
	
	public static void main(String[] args) {
		println format('2015-02-01')
	}
}
