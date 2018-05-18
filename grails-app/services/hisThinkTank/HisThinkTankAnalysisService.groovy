package hisThinkTank

import java.lang.Double;

import groovy.sql.Sql

import org.apache.commons.lang.StringUtils

import com.iminer.biz.Teleplay
import com.iminer.utils.DateTools

/**
 * HisThinkTankAnalysisService：历史智库交互分析筛选
 * @author   fangjie fangjie@iminer.com
 * @version  V1.0
 * @date 	 2016-1-4 下午3:31:28
 */
class HisThinkTankAnalysisService {

	def dataSource
	
	def dataSource_mycat_reputation
	
	def marketdataService
	
	//性别受众码表pid
	private static int sexAudienceCodePid = 152
	//年龄受众码表pid
	private static int ageAudienceCodePid = 154
	//学历受众码表pid
	private static int educationAudienceCodePid = 156
	//收入受众码表pid
	private static int incomeAudienceCodePid = 160
	//导演关系码表ID
	private static int directorPid = 19
	//演出关系码表ID
	private static int actorPid = 16
	//编剧关系码表ID
	private static int scriptwriterPid = 18
	
	/**
	 * getSearchConditionInfo:获取搜索条件信息
	 * @return
	 */
	def getSearchConditionInfo(){
		def source = new Sql(dataSource)
		//题材分类
		String select_theme_sql = "SELECT a.theme_id as p_id,a.theme_name as p_name,b.theme_id as c_id,b.theme_name as c_name FROM `basic_ent_theme` a,basic_ent_theme b WHERE a.pid=0 AND b.pid=a.theme_id"
		def themeInfo = [:]
		source.rows(select_theme_sql).each {
			def thisTheme = themeInfo.get(it?.p_id)?:[:]
			thisTheme.put("p_name",it?.p_name)
			def childrenTheme = thisTheme.get("children")?:[]
			childrenTheme.add(['c_id':it?.c_id,'c_name':it?.c_name])
			thisTheme.put("children",childrenTheme)
			themeInfo.put(it?.p_id, thisTheme)
		}
		//平台分类
		String select_platform_sql = "SELECT id,channel_name AS `name`,'tv' AS type,'电视' AS type_name FROM `basic_tvsou_channel_info`  where monitoring_status=1 "+
							"UNION ALL "+
							"SELECT id,`name`,'net_tv' AS type,'网视' AS type_name FROM `basic_site_info` where type=1 "
		def platformInfo = [:]
		source.rows(select_platform_sql).each {
			def thisPlatform = platformInfo.get(it?.type)?:[]
			thisPlatform.add(['id':it?.id,'name':it?.name,'type_name':it?.type_name])
			platformInfo.put(it?.type,thisPlatform)
		}
		//年龄、学历、收入
		String select_audience_sql ="SELECT id AS c_id,rate_name AS `c_name`,pid AS p_id,'性别' AS p_name FROM `domain_teleplay_audience_code` WHERE pid=${sexAudienceCodePid} "+
									"UNION ALL  "+
									"SELECT id AS c_id,rate_name AS `c_name`,pid AS p_id,'年龄' AS type_name FROM `domain_teleplay_audience_code` WHERE pid=${ageAudienceCodePid} "+
									"UNION ALL  "+
									"SELECT id AS c_id,rate_name AS `c_name`,pid AS p_id,'学历' AS type_name FROM `domain_teleplay_audience_code` WHERE pid=${educationAudienceCodePid} "+
									"UNION ALL  "+
									"SELECT id AS c_id,rate_name AS `c_name`,pid AS p_id,'收入' AS type_name FROM `domain_teleplay_audience_code` WHERE pid=${incomeAudienceCodePid} "
		def audienceInfo = [:]
		source.rows(select_audience_sql).each {
			def thisAudience = audienceInfo.get(it?.p_id)?:[:]
			thisAudience.put("p_name",it?.p_name)
			def childrenAudience = thisAudience.get("children")?:[]
			childrenAudience.add(['c_id':it?.c_id,'c_name':it?.c_name])
			thisAudience.put("children",childrenAudience)
			audienceInfo.put(it?.p_id, thisAudience)
		}
		[themeInfo:themeInfo,platformInfo:platformInfo,audienceInfo:audienceInfo]
	}
	
	/**
	 * getAnalysisInfoByCondition:根据条件获取分析数据
	 * @param params : 相关查询条件
	 * @return
	 */
	def getAnalysisInfoByCondition(params){
		def source = new Sql(dataSource)
		def result = []
		//进行条件筛选
		def teleplayIdArr = getTeleplayInfoByCondition(params)
		def evaluateNum = 0
		//题材分类
		evaluateNum += params.themeArr?1:0
		def themeMatchInfo = [:]
		if(params.themeArr){
			def themeIds = ""
			params.themeArr.split(",").each {
				if(it){
					themeIds += it?.split("_")[3] + ","
				}
			}
			themeIds = themeIds?themeIds.substring(0, themeIds.length() - 1):""
//			String select_sql = "SELECT a.theme_two_id,b.theme_name,a.object_id FROM basic_theme_relation a,basic_ent_theme b WHERE a.theme_two_id in (${themeIds}) AND a.theme_two_id=b.theme_id AND a.object_type=5"
			String select_sql = "select a.classification_one,b.`name`,a.teleplay_id from basic_teleplay_classification a , code_teleplay_classification b where a.classification_one in (${themeIds}) and a.classification_one=b.cid"
			
			source.rows(select_sql).each {
				def teleplayIds = themeMatchInfo.get(it?.classification_one+"|"+it?.name)?:[]
				teleplayIds.add(it?.teleplay_id)
				themeMatchInfo.put(it?.classification_one+"|"+it?.name,teleplayIds)
			} 
			//求交集
			themeMatchInfo.each {k,v ->
				def intersection = v.intersect(teleplayIdArr)
				themeMatchInfo.put(k, ['ids':intersection,'countNum':intersection.size()])
			}
		}
		//平台分类
		evaluateNum += params.platformArr?1:0
		def platformMatchInfo = [:]
		if(params.platformArr){
			def tvIds = ""
			def netTvIds = ""
			params.platformArr.split(",").each {
				if(it && it.indexOf("net_tv") > -1){
					netTvIds += it?.split("_")[4] + ","
				}else if(it && it.indexOf("tv") > -1){
					tvIds += it?.split("_")[3] + ","
				}
			}
			tvIds = tvIds?tvIds.substring(0, tvIds.length() - 1):""
			netTvIds = netTvIds?netTvIds.substring(0, netTvIds.length() - 1):""
			String select_tv_sql = "SELECT a.channel_id,b.channel_name,a.teleplay_id FROM basic_teleplay_channel a,basic_tvsou_channel_info b WHERE a.channel_id=b.id AND a.channel_id in (${tvIds})"
			String select_nettv_sql = "SELECT a.site_id,b.`name`,a.object_id FROM basic_site_relation a,basic_site_info b WHERE a.site_id=b.id AND a.object_type=5 AND a.site_id IN (${netTvIds})"
			if(tvIds){
				source.rows(select_tv_sql).each {
					def teleplayIds = platformMatchInfo.get("tv|"+it?.channel_id+"|"+it?.channel_name)?:[]
					teleplayIds.add(it?.teleplay_id)
					platformMatchInfo.put("tv|"+it?.channel_id+"|"+it?.channel_name,teleplayIds)
				}
			}
			if(netTvIds){
				source.rows(select_nettv_sql).each {
					def teleplayIds = platformMatchInfo.get("nettv|"+it?.site_id+"|"+it?.name)?:[]
					teleplayIds.add(it?.object_id)
					platformMatchInfo.put("nettv|"+it?.site_id+"|"+it?.name,teleplayIds)
				}
			}
			//求交集
			platformMatchInfo.each {k,v ->
				def intersection = v.intersect(teleplayIdArr)
				platformMatchInfo.put(k, ['ids':intersection,'countNum':intersection.size()])
			}
		}
		//主创分类
		evaluateNum += params.creatorArr?1:0
		def creatorMatchInfo = [:]
		if(params.creatorArr){
			def directorIds = ""
			def actorIds = ""
			def scriptwriterIds = ""
			params.creatorArr.split(",").each {
				if(it && it.indexOf("director") > -1){
					directorIds += it?.split("_")[2] + ","
				}else if(it && it.indexOf("actor") > -1){
					actorIds += it?.split("_")[2] + ","
				}else if(it && it.indexOf("scriptwriter") > -1){
					scriptwriterIds += it?.split("_")[2] + ","
				}
			}
			directorIds = directorIds?directorIds.substring(0, directorIds.length() - 1):""
			actorIds = actorIds?actorIds.substring(0, actorIds.length() - 1):""
			scriptwriterIds = scriptwriterIds?scriptwriterIds.substring(0, scriptwriterIds.length() - 1):""
			String select_sql = ""
			if(directorIds){
				select_sql += "SELECT a.artist_id,b.`name`,a.teleplay_id,'导演' as type_name FROM basic_artist_teleplay a,basic_artist_info b WHERE a.relation=${directorPid} AND a.artist_id=b.id AND artist_id IN (${directorIds}) "
			}
			if(actorIds){
				select_sql += select_sql?" union ":""
				select_sql += "SELECT a.artist_id,b.`name`,a.teleplay_id,'演员' as type_name FROM basic_artist_teleplay a,basic_artist_info b WHERE a.relation=${actorPid} AND a.artist_id=b.id AND artist_id IN (${actorIds}) "
			}
			if(scriptwriterIds){
				select_sql += select_sql?" union ":""
				select_sql += "SELECT a.artist_id,b.`name`,a.teleplay_id,'编剧' as type_name FROM basic_artist_teleplay a,basic_artist_info b WHERE a.relation=${scriptwriterPid} AND a.artist_id=b.id AND artist_id IN (${scriptwriterIds}) "
			}
			source.rows(select_sql).each {
				def teleplayIds = creatorMatchInfo.get(it?.artist_id+"|"+it?.name+"|"+it?.type_name)?:[]
				teleplayIds.add(it?.teleplay_id)
				creatorMatchInfo.put(it?.artist_id+"|"+it?.name+"|"+it?.type_name,teleplayIds)
			}
			//求交集
			creatorMatchInfo.each {k,v ->
				def intersection = v.intersect(teleplayIdArr)
				creatorMatchInfo.put(k, ['ids':intersection,'countNum':intersection.size()])
			}
		}
		def analysisResult = [:]
		if(evaluateNum == 3){//三个维度都有,二维图
			analysisResult.put("sourceData", ['themeInfo':themeMatchInfo,'platformInfo':platformMatchInfo,'creatorInfo':creatorMatchInfo])
			def showData = [:]
			creatorMatchInfo.each {k,v ->
				def thisCreatorIds = v.ids
				def thisCreatorName = k.split("\\|")[1]+"("+k.split("\\|")[2]+")"
				def thisCreatorShowData = []
				themeMatchInfo.each {tk,tv ->
					def themeName = tk.split("\\|")[1]
					def ids = tv.ids.intersect(thisCreatorIds)
					platformMatchInfo.each {pk,pv ->
						def platformName = pk.split("\\|")[2]
						def pids = pv.ids
						def intersection = pids.intersect(ids)
						thisCreatorShowData.add(['categoryName':themeName,'legendName':platformName,'countNum':intersection.size()])
					}
				}
				showData.put(k, ['creatorName':thisCreatorName,'chartData':thisCreatorShowData])
			}
			analysisResult.put("chartData", showData)
		}else if(evaluateNum == 2){//两个维度,二维图
			analysisResult.put("sourceData", ['themeInfo':themeMatchInfo,'platformInfo':platformMatchInfo,'creatorInfo':creatorMatchInfo])
			def showData = []
			if(themeMatchInfo.size() > 0 && platformMatchInfo.size() > 0 && creatorMatchInfo.size() == 0){//数据+平台
				themeMatchInfo.each {k,v ->
					def themeName = k.split("\\|")[1]
					def ids = v.ids
					platformMatchInfo.each {pk,pv ->
						def platformName = pk.split("\\|")[2]
						def pids = pv.ids
						def intersection = pids.intersect(ids)
						showData.add(['categoryName':themeName,'legendName':platformName,'countNum':intersection.size()])
					}
				}
			}else if(themeMatchInfo.size() > 0 && platformMatchInfo.size() == 0 && creatorMatchInfo.size() > 0){//数据+主创
				themeMatchInfo.each {k,v ->
					def themeName = k.split("\\|")[1]
					def ids = v.ids
					creatorMatchInfo.each {pk,pv ->
						def creatorName = pk.split("\\|")[1]+"("+pk.split("\\|")[2]+")"
						def pids = pv.ids
						def intersection = pids.intersect(ids)
						showData.add(['categoryName':themeName,'legendName':creatorName,'countNum':intersection.size()])
					}
				}
			}else if(themeMatchInfo.size() == 0 && platformMatchInfo.size() > 0 && creatorMatchInfo.size() > 0){//平台+主创
				platformMatchInfo.each {k,v ->
					def platformName = k.split("\\|")[2]
					def ids = v.ids
					creatorMatchInfo.each {pk,pv ->
						def creatorName = pk.split("\\|")[1]+"("+pk.split("\\|")[2]+")"
						def pids = pv.ids
						def intersection = pids.intersect(ids)
						showData.add(['categoryName':platformName,'legendName':creatorName,'countNum':intersection.size()])
					}
				}
			}
			analysisResult.put("chartData", showData)
		}else if(evaluateNum == 1){//一个维度，饼状图
			if(themeMatchInfo.size() > 0){
				analysisResult.put("sourceData", themeMatchInfo)
				def showData = []
				themeMatchInfo.each {k,v ->
					def kArr = k.split("\\|")
					showData.add(['name':kArr[1],'value':v.countNum])
				}
				analysisResult.put("chartData", showData)
			}else if(platformMatchInfo.size() > 0){
				analysisResult.put("sourceData", platformMatchInfo)
				def showData = []
				platformMatchInfo.each {k,v ->
					def kArr = k.split("\\|")
					showData.add(['name':kArr[2],'value':v.countNum])
				}
				analysisResult.put("chartData", showData)
			}else if(creatorMatchInfo.size() > 0){
				analysisResult.put("sourceData", creatorMatchInfo)
				def showData = []
				creatorMatchInfo.each {k,v ->
					def kArr = k.split("\\|")
					showData.add(['name':kArr[1]+"("+kArr[2]+")",'value':v.countNum])
				}
				analysisResult.put("chartData", showData)
			}
		}
		[evaluateNum:evaluateNum,teleplayIdArr:teleplayIdArr,analysisResult:analysisResult]
	}
	
	/**
	 * getTeleplayInfoByCondition:根据条件获取电视剧信息
	 * @param condition ： 相关条件
	 * @return
	 */
	def getTeleplayInfoByCondition(def condition){
		
		def dataSourceMycatReputation = new Sql(dataSource_mycat_reputation)
		
		def source = new Sql(dataSource)
		def teleplayIdArr = []
		String select_all_sql = "SELECT id FROM basic_teleplay_info WHERE discern=1 "
		teleplayIdArr = source.rows(select_all_sql)?.id
		//受众分类
		if(condition.audienceArr){
			def codeIds = ""
			condition.audienceArr.split(",").each {
				codeIds += it?.split("_")[3] + ","
			}
			codeIds = codeIds?codeIds.substring(0, codeIds.length() - 1):""
			if(codeIds){
				String select_audience_sql = "SELECT DISTINCT b.teleplay_id "+
											"FROM domain_teleplay_audience_code a,domain_teleplay_audience_composition b "+
											"WHERE a.rate_type=b.rate_type AND a.id IN (${codeIds}) "
				def result = source.rows(select_audience_sql)
				teleplayIdArr = teleplayIdArr.intersect(result?.teleplay_id)
			}else{
				teleplayIdArr = teleplayIdArr.intersect([])
			}
		}
		//收视率范围
//		if(condition.ratingStart || condition.ratingEnd){
//			String select_ratingrange_sql = "select object_id from domain_teleplay_audience_rating where 1=1 "
//			if(condition.ratingStart){
//				select_ratingrange_sql += "and rate_num >=${condition.ratingStart} "
//			}
//			if(condition.ratingEnd){
//				select_ratingrange_sql += "and rate_num <=${condition.ratingEnd} "
//			}
//			def result = dataSourceMycatReputation.rows(select_ratingrange_sql)
//			teleplayIdArr = teleplayIdArr.intersect(result?.object_id)
//		}
		//播放数范围
		if(condition.playNumStart || condition.playNumEnd){
			String select_playrange_sql = "SELECT DISTINCT a.teleplay_object_id "+
										"FROM basic_teleplay_crawl_info a,basic_teleplay_crawl_statistical b "+
										"WHERE a.id=b.teleplay_id "
			if(condition.playNumStart){
				select_playrange_sql += "and b.total_play_num>= ${condition.playNumStart} "
			}
			if(condition.playNumEnd){
				select_playrange_sql += "and b.total_play_num<= ${condition.playNumEnd} "
			}
			def result = source.rows(select_playrange_sql)
			teleplayIdArr = teleplayIdArr.intersect(result?.teleplay_object_id)
		}
		//时间范围
		if(condition.timeRangeStart || condition.timeRangeEnd){
			String select_timerange_sql = "select id from basic_teleplay_info where discern=1 "
			if(condition.timeRangeStart){
				select_timerange_sql += "and publish_time >=${condition.timeRangeStart} "
			}
			if(condition.timeRangeEnd){
				select_timerange_sql += "and publish_time <=${condition.timeRangeEnd} "
			}
			def result = source.rows(select_timerange_sql)
			teleplayIdArr = teleplayIdArr.intersect(result?.id)
		}
		return teleplayIdArr
	}
	
	
	/**
	 * getAnalysisTableInfoByCondition:根据条件获取分析数据，表格形式展现
	 * @param params : 相关查询参数
	 * @return
	 */
	def getAnalysisTableInfoByCondition(params){
		
		def dataSourceMycatReputation = new Sql(dataSource_mycat_reputation)
		
		def source = new Sql(dataSource)
		def teleplayIdArr = []
		//全部电视剧
		String select_all_sql = "SELECT id FROM basic_teleplay_info WHERE discern=1 "
		teleplayIdArr = source.rows(select_all_sql)?.id
		//题材分类
		if(params.themeArr){
			def themeIds = ""
			params.themeArr.split(",").each {
				if(it){
					themeIds += it?.split("_")[3] + ","
				}
			}
			themeIds = themeIds?themeIds.substring(0, themeIds.length() - 1):""
//			String select_sql = "SELECT a.object_id FROM basic_theme_relation a,basic_ent_theme b WHERE a.theme_two_id in (${themeIds}) AND a.theme_two_id=b.theme_id AND a.object_type=5 GROUP BY object_id"
			String select_sql ="select a.teleplay_id from basic_teleplay_classification a , code_teleplay_classification b where a.classification_one in (${themeIds}) and a.classification_one=b.cid GROUP BY a.teleplay_id"
			def themeTeleplayIds = source.rows(select_sql)?.teleplay_id
			teleplayIdArr = themeTeleplayIds.intersect(teleplayIdArr)
		}
		//平台分类
		if(params.platformArr){
			def tvIds = ""
			def netTvIds = ""
			params.platformArr.split(",").each {
				if(it && it.indexOf("net_tv") > -1){
					netTvIds += it?.split("_")[4] + ","
				}else if(it && it.indexOf("tv") > -1){
					tvIds += it?.split("_")[3] + ","
				}
			}
			tvIds = tvIds?tvIds.substring(0, tvIds.length() - 1):""
			netTvIds = netTvIds?netTvIds.substring(0, netTvIds.length() - 1):""
			String select_sql = ""
			if(tvIds){
				select_sql += "SELECT a.teleplay_id FROM basic_teleplay_channel a,basic_tvsou_channel_info b WHERE a.channel_id=b.id AND a.channel_id in (${tvIds}) GROUP BY a.teleplay_id"
			}
			if(netTvIds){
				select_sql += select_sql?" union ":""
				select_sql += "SELECT a.object_id AS teleplay_id FROM basic_site_relation a,basic_site_info b WHERE a.site_id=b.id AND a.object_type=5 AND a.site_id IN (${netTvIds}) GROUP BY a.object_id"
			}
			def platformTeleplayIds = source.rows(select_sql)?.teleplay_id
			teleplayIdArr = platformTeleplayIds.intersect(teleplayIdArr)
		}
		//主创分类
		if(params.creatorArr){
			def directorIds = ""
			def actorIds = ""
			def scriptwriterIds = ""
			params.creatorArr.split(",").each {
				if(it && it.indexOf("director") > -1){
					directorIds += it?.split("_")[2] + ","
				}else if(it && it.indexOf("actor") > -1){
					actorIds += it?.split("_")[2] + ","
				}else if(it && it.indexOf("scriptwriter") > -1){
					scriptwriterIds += it?.split("_")[2] + ","
				}
			}
			directorIds = directorIds?directorIds.substring(0, directorIds.length() - 1):""
			actorIds = actorIds?actorIds.substring(0, actorIds.length() - 1):""
			scriptwriterIds = scriptwriterIds?scriptwriterIds.substring(0, scriptwriterIds.length() - 1):""
			String select_sql = ""
			if(directorIds){
				select_sql += "SELECT a.teleplay_id FROM basic_artist_teleplay a,basic_artist_info b WHERE a.relation=${directorPid} AND a.artist_id=b.id AND artist_id IN (${directorIds}) group by a.teleplay_id "
			}
			if(actorIds){
				select_sql += select_sql?" union ":""
				select_sql += "SELECT a.teleplay_id FROM basic_artist_teleplay a,basic_artist_info b WHERE a.relation=${actorPid} AND a.artist_id=b.id AND artist_id IN (${actorIds}) group by a.teleplay_id "
			}
			if(scriptwriterIds){
				select_sql += select_sql?" union ":""
				select_sql += "SELECT a.teleplay_id FROM basic_artist_teleplay a,basic_artist_info b WHERE a.relation=${scriptwriterPid} AND a.artist_id=b.id AND artist_id IN (${scriptwriterIds}) group by a.teleplay_id "
			}
			def creatorTeleplayIds = source.rows(select_sql)?.teleplay_id
			teleplayIdArr = creatorTeleplayIds.intersect(teleplayIdArr)
		}
		//受众分类
		if(params.audienceArr){
			def codeIds = ""
			params.audienceArr.split(",").each {
				codeIds += it?.split("_")[3] + ","
			}
			codeIds = codeIds?codeIds.substring(0, codeIds.length() - 1):""
			if(codeIds){
				String select_audience_sql = "SELECT DISTINCT b.teleplay_id "+
											"FROM domain_teleplay_audience_code a,domain_teleplay_audience_composition b "+
											"WHERE a.rate_type=b.rate_type AND a.id IN (${codeIds}) "
				def result = source.rows(select_audience_sql)
				teleplayIdArr = teleplayIdArr.intersect(result?.teleplay_id)
			}else{
				teleplayIdArr = teleplayIdArr.intersect([])
			}
		}
		//收视率范围
//		if(params.ratingStart || params.ratingEnd){
//			double ratingStart=  params.ratingStart.toDouble()
//			double ratingEnd= Double.parseDouble(params.ratingEnd.toString())
//			double rs=ratingStart/100
//			double re= ratingEnd/100
//			String select_ratingrange_sql = "select object_id from domain_teleplay_audience_rating where 1=1 "
//			if(params.ratingStart){
//				select_ratingrange_sql += "and rate_num >="+rs+" "
//			}
//			if(params.ratingEnd){
//				select_ratingrange_sql += "and rate_num <= "+re+" "
//			}
//			def result = dataSourceMycatReputation.rows(select_ratingrange_sql)
//			teleplayIdArr = teleplayIdArr.intersect(result?.object_id)
//		}
		//播放数范围
		if(params.playNumStart || params.playNumEnd){
			String select_playrange_sql = "SELECT DISTINCT a.teleplay_object_id "+
										"FROM basic_teleplay_crawl_info a,basic_teleplay_crawl_statistical b "+
										"WHERE a.id=b.teleplay_id "
			if(params.playNumStart){
				select_playrange_sql += "and b.total_play_num>= ${params.playNumStart} "
			}
			if(params.playNumEnd){
				select_playrange_sql += "and b.total_play_num<= ${params.playNumEnd} "
			}
			def result = source.rows(select_playrange_sql)
			teleplayIdArr = teleplayIdArr.intersect(result?.teleplay_object_id)
		}
		//时间范围
		if(params.timeRangeStart || params.timeRangeEnd){
			String select_timerange_sql = "select id from basic_teleplay_info where discern=1 "
			if(params.timeRangeStart){
				select_timerange_sql += "and publish_time >=${params.timeRangeStart} "
			}
			if(params.timeRangeEnd){
				select_timerange_sql += "and publish_time <=${params.timeRangeEnd} "
			}
			def result = source.rows(select_timerange_sql)
			teleplayIdArr = teleplayIdArr.intersect(result?.id)
		}
		//电视剧详细信息
		def teleplayInfo = []
		teleplayIdArr.each {
			def info = marketdataService.getTeleplayInfo(Teleplay.get(it))
			//平均收视率
			info.put("avgRateNum", getAvgRatingByTeleplay(it))
			//总播放量
			info.put("totalPlayNum", getTotalPlayNumByTeleplay(it))
			teleplayInfo.add(info)
		}
		return teleplayInfo
	}
	
	/**
	 * getAvgRatingByTeleplay:根据电视剧获取平均收视率
	 * @param teleplayId : 电视剧ID
	 * @return
	 */
	def getAvgRatingByTeleplay(def teleplayId){
		def dataSourceMycatReputation = new Sql(dataSource_mycat_reputation)
		
		def source= new Sql(dataSource)
		
		String sql_play_date = "SELECT publish_time from basic_teleplay_info where id = ${teleplayId} "
		
		def playDate = source.rows(sql_play_date)
		
		def publishTime
		
		def playDateBakeMin
		
		if(playDate){
			playDate.each {
				if(it.publish_time){
					publishTime = it.publish_time
					DateTools dt =  new DateTools()
					playDateBakeMin = dt.fmtDate(publishTime, "yyyy-MM-dd", "yyyy-MM-dd")
				}else{
					publishTime = '1970-01-01'
				}
			}
		}else{
			publishTime = '1970-01-01'
		}
		
		String select_premiere_platform = "select channel_id from basic_teleplay_channel where premiere_platform = 0 and teleplay_id = ${teleplayId}"
		
		def premierePlatform = source.rows(select_premiere_platform)
		
		
		
		if(premierePlatform){
			def ci = StringUtils.join( premierePlatform.channel_id.toArray(),",")
//			String select_sql = "SELECT AVG(rate_num) AS avg_rate_num FROM domain_teleplay_audience_rating "+
//				"WHERE object_id=${teleplayId} AND channel_id in (${ci}) and start_time >= '19:15:00'  and end_time <= '21:45:00' "
			
			String select_sql = "SELECT AVG(rateNum) as avg_rate_num from (SELECT date_format(b.record_date,'%Y-%m-%d') as record_date,avg(b.rate_num) as rateNum from (select episodes,channel_id,record_date,start_time,rate_num from (select episodes,channel_id,record_date,start_time,rate_num from domain_teleplay_audience_rating where object_id=${teleplayId} and channel_id in (${ci}) and object_type = 5 and rate_type='00350001' and record_date >= '${playDateBakeMin}' ORDER BY episodes,channel_id,record_date,start_time) as a GROUP BY a.episodes,a.channel_id) as b group by b.record_date order by b.record_date) as c"
			
			
			def result = dataSourceMycatReputation.rows(select_sql)
			if(result.get(0).avg_rate_num != null){
				def avgRateNum = result.get(0).avg_rate_num
				return String.format("%.3f", avgRateNum)
			}else{
				return "-"
			}
		}else{
			return ""
		}
		
	}
	
	
	def getTotalPlayNumByTeleplay(def teleplayId){
		def source = new Sql(dataSource)
		String select_sql = "SELECT total_play_num FROM `basic_teleplay_crawl_statistical` WHERE teleplay_id=${teleplayId} "+
							"AND crawl_time=(SELECT MAX(crawl_time) AS record_date FROM basic_teleplay_crawl_statistical WHERE teleplay_id=${teleplayId})"
		def result = source.rows(select_sql)
		if(result){
			def totalPlayNum = result.get(0).total_play_num
			return totalPlayNum
		}else{
			return ""
		}
	}
}
