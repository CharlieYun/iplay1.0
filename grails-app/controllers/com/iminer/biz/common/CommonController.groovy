package com.iminer.biz.common

import grails.converters.JSON

/**
 * 公用类
 * @author Administrator
 *
 */
class CommonController{
	
	def commonService
	
	/**
	 * autocomplete自动补全插件
	 * @return
	 */
	def autoComplete(){
		def result = []
		result = commonService.getAutoCompleteInfo(params)
		render result as JSON
	}
	
}