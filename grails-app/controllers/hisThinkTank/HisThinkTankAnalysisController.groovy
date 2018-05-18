package hisThinkTank

import grails.converters.JSON

/**
 * HisThinkTankAnalysisController：历史智库交互分析筛选
 * @author   fangjie fangjie@iminer.com
 * @version  V1.0
 * @date 	 2016-1-4 下午3:27:55
 */
class HisThinkTankAnalysisController {

	def hisThinkTankAnalysisService
	
	/**
	 * index:首页
	 * @return
	 */
	def index(){
		def condition = hisThinkTankAnalysisService.getSearchConditionInfo()
		[condition:condition]
	}
	
	/**
	 * ajaxSearchAnalysisInfoByCondition:ajax请求，根据条件获取分析数据
	 * @return
	 */
	def ajaxSearchAnalysisInfoByCondition(){
		def result = hisThinkTankAnalysisService.getAnalysisInfoByCondition(params)
		render result as JSON
	}
	
	/**
	 * ajaxSearchAnalysisInfoByCondition:ajax请求，根据条件获取分析数据显示表格信息
	 * @return
	 */
	def ajaxSearchTeleplayAnalysisInfoByCondition(){
		def teleplayInfo = hisThinkTankAnalysisService.getAnalysisTableInfoByCondition(params)
		render(view:"analysisTableInfo",model:[teleplayInfo:teleplayInfo])
	}
}
