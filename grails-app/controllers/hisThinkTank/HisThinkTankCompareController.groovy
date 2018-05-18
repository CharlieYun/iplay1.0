package hisThinkTank
import grails.converters.JSON


/**
 * iplay信息对比控制器
 * @author lizihming
 * @date 2016-1-7 下午3:45:27
 */
class HisThinkTankCompareController {

	def hisThinkTankCompareService
	
    def index() { }
	
	/**
	 * iplay信息对比按钮事件入口
	 * @return
	 */
	def teleplayDataCompare(){
		def result = []
		result = hisThinkTankCompareService.queryTeleplayInformationByIds(params)
		render result as JSON
	}
	
	/**
	 * iplay信息对比按钮事件入口,获取电视剧对比图数据
	 * @return
	 */
	def tvRatingsComparison(){
		def result = []
		result = hisThinkTankCompareService.queryTvRatingsComparisonChart(params)
		render result as JSON
	}
	
	/**
	 * iplay信息对比按钮事件入口,获取电视剧播放量对比图数据
	 * @return
	 */
	def comparisonChartOfTVplay(){
		def result = []
		result = hisThinkTankCompareService.queryComparisonChartOfTVplayChart(params)
		render result as JSON
	}
	
	/**
	 * iplay信息对比按钮事件入口,获取媒体关注度变化图数据
	 * @return
	 */
	def mediaAttentionChange(){
		def result = []
		result = hisThinkTankCompareService.queryMediaAttentionChangeChart(params)
		render result as JSON
	}
	
	/**
	 * iplay信息对比按钮事件入口,获取公众影响力变化图数据
	 * @return
	 */
	def publicInfluenceChange(){
		def result = []
		result = hisThinkTankCompareService.queryPublicInfluenceChangeChart(params)
		render result as JSON
	}
}
