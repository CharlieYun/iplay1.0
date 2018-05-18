package hisThinkTank

import groovy.sql.Sql

import java.text.SimpleDateFormat

import org.apache.commons.lang.StringUtils

import com.iminer.biz.utils.FormatTeleplayPublishTime
import com.iminer.utils.DateTools

/**
 * 处理电视剧信息对比处理模块
 * @author lizihming
 * @date 2016-1-7 下午3:46:00
 */
class HisThinkTankCompareService {

	def dataSource
	
	def dataSource_mycat_reputation
	
	def dataSource_ireport
	
    def serviceMethod() {
	
    }
	
	/**
	 * 根据前端传人的电视剧对比id集，查询各电视剧相对应的数据
	 * @param params
	 * @return
	 */
	def queryTeleplayInformationByIds(params){
		
		def result = []
		
		/**
		 *保存各电视剧id对应的电视剧相关数据
		 */
		def map
		
		/**
		 * 电视剧信息拼接转换器
		 */
		def transfer = ""
		
		def source = new Sql(dataSource)
		
		def dataSourceMycatReputation = new Sql(dataSource_mycat_reputation)
		
		def ids =  params.ids.split(",")
		
		def sdf = new SimpleDateFormat("yyyy-MM-dd")
		
		ids.each {
			
			map = new HashMap()
			
			/**
			 * 获取电视剧名
			 */
			String sql_teleplay_name = "SELECT name from basic_teleplay_info where id = ${it}"
			
			def teleplayName = source.rows(sql_teleplay_name)
			
			if(teleplayName){
				teleplayName.each {
					map.put("teleplay_name", it.name)
				}
			}else{
				map.put("teleplay_name", "无")
			}
			
			
			
			/**
			 * 获取电视剧题材名
			 */
//			String sql_theme_name = "select theme_name from basic_ent_theme where theme_id in (select theme_three_id from basic_theme_relation where object_id=${it})"
			
			String sql_theme_name = "select `name` from code_teleplay_classification where cid in (select classification_one from basic_teleplay_classification where teleplay_id=${it})"
			
			def themeName = source.rows(sql_theme_name)
			
			if(themeName){
				themeName.each {
					map.put("teleplay_theme_name", it.name)
				}
			}else{
				map.put("teleplay_theme_name", "无")
			}
			
			
			
			/**
			 * 获取电视上星首播
			 */
			String sql_channel_name = "select channel_name from basic_tvsou_channel_info where monitoring_status=1 and id in (select channel_id from basic_teleplay_channel where teleplay_id=${it})"
			
			def channelName = source.rows(sql_channel_name)
			
			if(channelName){
				channelName.each {
					transfer += it.channel_name + "/"
					map.put("teleplay_channel_name", transfer.subSequence(0, transfer.length()-1))
				}
				transfer = ""
			}else{
				map.put("teleplay_channel_name", "无")
			}
			
			/**
			 * 获取电视首播播出时间
			 */
			String sql_channel_play_date = "SELECT publish_time from basic_teleplay_info where id = ${it}"
			
			def channelPlayDate = source.rows(sql_channel_play_date)
			
			if(channelPlayDate){
				channelPlayDate.each {
					if(it.publish_time){
						it.publish_time = FormatTeleplayPublishTime.format(it.publish_time)
						it.publish_time = sdf.format(sdf.parse(it.publish_time))
						map.put("teleplay_channel_play_date", it.publish_time.split("-")[0] + "年" + it.publish_time.split("-")[1] + "月" + it.publish_time.split("-")[2] + "日")
					}else{
						map.put("teleplay_channel_play_date", "无")
					}
				}
			}else{
				map.put("teleplay_channel_play_date", "无")
			}
			
			
			/**
			 * 获取电视剧网络播出平台
			 */
			String sql_net_name = "select name from basic_site_info where type=1 and id in (select site_id from basic_site_relation where object_id=${it})"
			
			def netName = source.rows(sql_net_name)
			
			if(netName){
				netName.each {
					transfer += it.name + "、"
					map.put("teleplay_net_name", transfer.subSequence(0, transfer.length()-1))
				}
				transfer = ""
			}else{
				map.put("teleplay_net_name", "无")
			}
			
			
			/**
			 * 获取电视剧网络播出时间
			 */
			String sql_premiere_date = "SELECT min(premiere_date) as premiere_date from basic_site_relation where object_id = ${it} and object_type = 5"
			
			def premiereDate = source.rows(sql_premiere_date)
			
			if(premiereDate){
				premiereDate.each {
					if(it.premiere_date){
						it.premiere_date = FormatTeleplayPublishTime.format(it.premiere_date.toString())
						it.premiere_date = sdf.format(sdf.parse(it.premiere_date))
//						if(it.publish_time.indexOf("，")){
//							it.publish_time = FormatTeleplayPublishTime.format(it.publish_time).split("，")[0]
//							map.put("teleplay_play_date", it.publish_time.split("-")[0] + "年" + it.publish_time.split("-")[1] + "月" + it.publish_time.split("-")[2] + "日")
//						}else{
//							it.publish_time = FormatTeleplayPublishTime.format(it.publish_time)
							map.put("teleplay_premiere_date", it.premiere_date.split("-")[0] + "年" + it.premiere_date.split("-")[1] + "月" + it.premiere_date.split("-")[2] + "日")
//						}
					}else{
						map.put("teleplay_premiere_date", "无")
					}
				}
			}else{
				map.put("teleplay_premiere_date", "无")
			}
			
			
			/**
			 * 获取电视剧平均收视率CSM50
			 */
//			String sql_average_rate = "SELECT publish_time from basic_teleplay_info where id = ${it}"
			String sql_average_rate = "SELECT CONCAT(round(AVG(rate_num),2),'%') arn FROM `domain_teleplay_audience_rating` where object_id =${it} and rate_type like '0050%' GROUP BY object_id"
			
			def averageRate = dataSourceMycatReputation.rows(sql_average_rate)
			
			if(averageRate){
				averageRate.each {
					map.put("teleplay_average_rate", it.arn)
				}
			}else{
				map.put("teleplay_average_rate", "——")
			}
			
			
			/**
			 * 获取电视剧累计播放量
			 */
//			String sql_cumulative_play = "SELECT publish_time from basic_teleplay_info where id = ${it}"
			String sql_cumulative_play = "SELECT sum(z.tpn) ztpn from (SELECT max(total_play_num) tpn,teleplay_id from basic_teleplay_crawl_statistical a where a.teleplay_id in (select b.id from basic_teleplay_crawl_info b where b.teleplay_object_id=${it}) GROUP BY a.teleplay_id) z"
			
			def cumulativePlay = source.rows(sql_cumulative_play)
			
			if(cumulativePlay){
				cumulativePlay.each {
					map.put("teleplay_cumulative_play", it.ztpn)
				}
			}else{
				map.put("teleplay_cumulative_play", "——")
			}
			
			
			/**
			 * 获取电视剧好评率
			 */
			String sql_praise_rate = "select positive_percent FROM ireport_teleplay_praise_statistic where teleplay_id=${it}"
			
			def praiseRate = dataSourceMycatReputation.rows(sql_praise_rate)
			
			if(praiseRate){
				praiseRate.each {
					map.put("teleplay_praise_rate", it.positive_percent + "%")
				}
			}else{
				map.put("teleplay_praise_rate", "——")
			}
			
			
			/**
			 * 获取电视剧导演
			 */
			String sql_director = "select name from basic_artist_info where id in (SELECT artist_id from basic_artist_teleplay WHERE teleplay_id = ${it} and relation = 19)"
			
			def director = source.rows(sql_director)
			
			if(director){
				director.each {
					transfer += it.name + "、"
					map.put("teleplay_director", transfer.subSequence(0, transfer.length()-1))
				}
				transfer = ""
			}else{
				map.put("teleplay_director", "无")
			}
			
			/**
			 * 获取电视剧编剧
			 */
			String sql_screenwriter = "select name from basic_artist_info where id in (SELECT artist_id from basic_artist_teleplay WHERE teleplay_id = ${it} and relation = 18)"
			
			def screenwriter = source.rows(sql_screenwriter)
			
			if(screenwriter){
				screenwriter.each {
					transfer += it.name + "、"
					map.put("teleplay_screenwriter", transfer.subSequence(0, transfer.length()-1))
				}
				transfer = ""
			}else{
				map.put("teleplay_screenwriter", "无")
			}
			
			
			/**
			 * 获取电视剧主演
			 */
			String sql_to_star = "select name from basic_artist_info where id in (SELECT artist_id from basic_artist_teleplay WHERE teleplay_id = ${it} and relation = 16)"
			
			def toStar = source.rows(sql_to_star)
			
			if(toStar){
				toStar.each {
					transfer += it.name + "、"
					map.put("teleplay_to_star", transfer.subSequence(0, transfer.length()-1))
				}
				transfer = ""
			}else{
				map.put("teleplay_to_star", "无")
			}
			
			result.add(map)
		}
		
		return result
	}
	
	/**
	 * 获取电视剧收视率对比图数据
	 * @return
	 */
	def queryTvRatingsComparisonChart(params){
		
		def source = new Sql(dataSource)
		
		def dataSourceMycatReputation = new Sql(dataSource_mycat_reputation)
		
		def ids =  params.ids.split(",")
		
		def allDateValueList = new HashMap()
		
		def timeX = []
		
		def thirtyFiveDataKeyValue
		
		def fiftyDataKeyValue
		
		def allCityDataKeyValue
		
		def m_m_time_x_all
		
		ids.each {
			
			/**
			 * 获取电视剧电视剧收视率35
			 */
			String sql_play_date = "SELECT publish_time from basic_teleplay_info where id = ${it}"
			
			def playDate = source.rows(sql_play_date)
			
			def playDateBakeMin
			
			def playDateBakeMax
			
			def publishTime
			
			if(playDate){
				playDate.each {
					if(it.publish_time){
						publishTime = it.publish_time
						DateTools dt =  new DateTools()
						playDateBakeMin = dt.fmtDate(publishTime, "yyyy-MM-dd", "yyyy-MM-dd")
						playDateBakeMax = dt.getBeforeTimeByH(publishTime, "yyyy-MM-dd", -90*24, "yyyy-MM-dd")
					}else{
						publishTime = '1970-01-01'
					}
				}
			}else{
				publishTime = '1970-01-01'
			}
			
			def channelDateValueList = new HashMap()
			
			/**
			 * 获取电视上星首播
			 */
			String sql_channel_id_name = "select id,channel_name from basic_tvsou_channel_info where monitoring_status=1 and id in (select channel_id from basic_teleplay_channel where teleplay_id=${it})"
			
			def channelIdName = source.rows(sql_channel_id_name)
			
			def channelIdList = []
			
			if(channelIdName){
				channelIdName.each { its ->
					channelIdList.add(its.id)
				}
			}
			
			def minRecordTime
			
			def maxRecordTime
			
			def increRecordTime
			
			if(channelIdList.size() > 0){
				
				def ci = StringUtils.join( channelIdList.toArray(),",")
				
				
//				String max_time_x = "SELECT max(date_diff) as date_diff_tran from (select DATEDIFF(record_date,'${publishTime}') as date_diff from domain_teleplay_audience_rating where object_id=${it} and channel_id in (${ci}) and rate_type='00350001' and object_type = 5 and record_date >= '${playDateBakeMin}' GROUP BY  record_date order by record_date) a"

//				String max_time_x = "select count(1) as date_diff_tran from (SELECT b.record_date as record_date,avg(b.rate_num) as rateNum,DATEDIFF(b.record_date,'${publishTime}') as date_diff from (select episodes,channel_id,record_date,start_time,rate_num from (select episodes,channel_id,record_date,start_time,rate_num from domain_teleplay_audience_rating where object_id=${it} and channel_id in (${ci}) and rate_type='00350001' and record_date >= '${playDateBakeMin}' ORDER BY episodes,channel_id,record_date,start_time) as a GROUP BY a.episodes,a.channel_id) as b group by b.record_date order by b.record_date) as c"
//				
//				
//				def m_m_time_x = dataSourceMycatReputation.rows(max_time_x)
//				
//				if(m_m_time_x.get(0).date_diff_tran){
//					if(m_m_time_x_all == null){
//						m_m_time_x_all = m_m_time_x.get(0).date_diff_tran.toInteger()
//					}else{
//						if(m_m_time_x_all < m_m_time_x.get(0).date_diff_tran.toInteger()){
//							m_m_time_x_all = m_m_time_x.get(0).date_diff_tran.toInteger()
//						}
//					}
//					
//					timeX.clear()
//					
//					(0..m_m_time_x_all).each {
//						timeX.add(it)
//					}
//				}
				
				
				def dateValueList = new HashMap()
				
				def audienceRatingsDate = []
				
				def thirtyFiveRateValue = []
				
				def fiftyRateValue = []
				
				def allCityRateValue = []
				
				def dateValues = []
				
				def sdf = new SimpleDateFormat("yyyy-MM-dd")
				
				/**
				 * 获取电视剧电视剧收视率
				 */
//				String sql_thirtyFive_rate = "select AVG(rate_num) as rateNum,record_date,DATEDIFF(record_date,'${publishTime}') as date_diff from domain_teleplay_audience_rating where object_id=${it} and channel_id in (${ci}) and rate_type='00350001' and object_type = 5 and record_date >= '${playDateBakeMin}'  and start_time >= '19:15:00'  and end_time <= '21:45:00' GROUP BY  record_date order by record_date"
//				String sql_thirtyFive_rate = "SELECT AVG(a.rate_num) as rateNum,a.record_date as record_date,a.date_diff as date_diff from ( select rate_num,record_date,channel_id,DATEDIFF(record_date,'${publishTime}') as date_diff from domain_teleplay_audience_rating where object_id=${it} and channel_id in (${ci}) and rate_type='00350001' and object_type = 5 and record_date >= '${playDateBakeMin}'  and start_time >= '19:15:00'  and end_time <= '21:45:00' GROUP BY  record_date,channel_id order by record_date ) a group by a.record_date"
				String sql_thirtyFive_rate = "SELECT date_format(b.record_date,'%Y-%m-%d') as record_date,avg(b.rate_num) as rateNum from (select episodes,channel_id,record_date,start_time,rate_num from (select episodes,channel_id,record_date,start_time,rate_num from domain_teleplay_audience_rating where object_id=${it} and channel_id in (${ci}) and object_type = 5 and rate_type='00350001' and record_date >= '${playDateBakeMin}' ORDER BY episodes,channel_id,record_date,start_time) as a GROUP BY a.episodes,a.channel_id) as b group by b.record_date order by b.record_date"
				
				def thirtyFiveRate = dataSourceMycatReputation.rows(sql_thirtyFive_rate)
				
				thirtyFiveDataKeyValue = new HashMap()
				
				def increment = 0
				
				if(thirtyFiveRate){
					thirtyFiveRate.each {
						thirtyFiveDataKeyValue.put(increment++, String.format("%.2f", it.rateNum))
						dateValues.add(it.record_date)
					}
				}
				
				for (int i = 0; i < increment; i++) {
					if(thirtyFiveDataKeyValue.get(i)){
						thirtyFiveRateValue.add(thirtyFiveDataKeyValue.get(i))
					}else{
						thirtyFiveRateValue.add("-")
					}
				}
				
				if(timeX.size() < increment){
					timeX.clear()
					(1..increment).each {
						timeX.add(it)
					}
				}
				
				dateValueList.put("thirtyFiveRateValueList", thirtyFiveRateValue)
				
				dateValueList.put("thirtyFiveAndFiftyRateDateList", timeX)
				
				
				
				publishTime = FormatTeleplayPublishTime.format(publishTime)
				
				dateValueList.put("teleplayPlayDate", dateValues)
				
				channelDateValueList.put(it, dateValueList)
			}
			
			allDateValueList.put(it, channelDateValueList)
			
		}
		
		return allDateValueList
	
	}
	
	/**
	 * 获取电视剧播放量对比图数据
	 * @return
	 */
	def queryComparisonChartOfTVplayChart(params){
		def result = []
		
		def dataSourceIreport = new Sql(dataSource_ireport)
		
		def ids =  params.ids.split(",")
		
		ids.each {
		}
		return result
	}
	
	/**
	 * 获取媒体关注度变化图数据
	 * @return
	 */
	def queryMediaAttentionChangeChart(params){
		
		def dataSourceIreport = new Sql(dataSource_ireport)
		
		def source = new Sql(dataSource)
		
		def ids =  params.ids.split(",")
		
		def allDateValueList = new HashMap()
		
		def timeX = []
		
		def dataKeyValue
		
		(-90..90).each {
			timeX.add(it)
		}
		
		
		ids.each {
			
			def dateValueList = new HashMap()
			
			def hotRateDate = []
			
			def hotRateValue = []
			
			/**
			 * 获取电视剧播出时间
			 */
			String sql_play_date = "SELECT publish_time from basic_teleplay_info where id = ${it}"
			
			def playDate = source.rows(sql_play_date)
			
			def playDateBakeMin
			
			def playDateBakeMax
			
			def publishTime
			
			if(playDate){
				playDate.each {
					if(it.publish_time){
						publishTime = it.publish_time
						DateTools dt =  new DateTools()
						playDateBakeMin = dt.getBeforeTimeByH(publishTime, "yyyy-MM-dd", 90*24, "yyyy-MM-dd")
						playDateBakeMax = dt.getBeforeTimeByH(publishTime, "yyyy-MM-dd", -90*24, "yyyy-MM-dd")
					}else{
						publishTime = '1970-01-01'
					}
				}
			}else{
				publishTime = '1970-01-01'
			}
			
			/**
			 * 获取电视剧媒体关注度数据
			 */
			String sql_hot_rate = "select hot_rate,record_date,DATEDIFF(record_date,'${publishTime}') as date_diff from domain_teleplay_hot_records where teleplay_id=${it} and record_date between '${playDateBakeMin}' and '${playDateBakeMax}' order by record_date"
			def hotRate = dataSourceIreport.rows(sql_hot_rate)
//			if(hotRate){
//				hotRate.each {
//					
//					hotRateDate.add(it.record_date.toString())
//					hotRateValue.add(it.hot_rate)
//				}
//			}
//			dateValueList.put("hotRateDateList", hotRateDate)
//			dateValueList.put("hotRateValueList", hotRateValue)
//			dateValueList.put("hotRateDateList", hotRateDate)
//			dateValueList.put("hotRateValueList", hotRateValue)
//			allDateValueList.put(it, dateValueList)
			
			dataKeyValue = new HashMap()
			
			if(hotRate){
				hotRate.each {
					hotRateDate.add(it.record_date.toString())
					dataKeyValue.put(it.date_diff.toString(), it.hot_rate)
				}
			}
			
			for (int i = 0; i < timeX.size(); i++) {
				if(dataKeyValue.get(timeX[i].toString())){
					hotRateValue.add(dataKeyValue.get(timeX[i].toString()))
				}else{
					hotRateValue.add("-")
				}
			}
			dateValueList.put("hotRateValueList", hotRateValue)
			dateValueList.put("hotRateDateList", timeX)
			
			def sdf = new SimpleDateFormat("yyyy-MM-dd")
			
			publishTime = FormatTeleplayPublishTime.format(publishTime)
			
			dateValueList.put("teleplayPlayDate", sdf.parse(publishTime))
			
			allDateValueList.put(it, dateValueList)
		}
		
		
		return allDateValueList
	}
	
	/**
	 * 获取公众影响力变化图数据
	 * @return
	 */
	def queryPublicInfluenceChangeChart(params){
		
		def dataSourceIreport = new Sql(dataSource_ireport)
		
		def source = new Sql(dataSource)
		
		def ids =  params.ids.split(",")
		
		def allDateValueList = new HashMap()
		
		def timeX = []
		
		def dataKeyValue
		
		(-90..90).each {
			timeX.add(it)
		}
		
		ids.each {
			
			def dateValueList = new HashMap()
			
			def publicInfluenceDate = []
			
			def publicInfluenceValue = []
			
			/**
			 * 获取电视剧播出时间
			 */
			String sql_play_date = "SELECT publish_time from basic_teleplay_info where id = ${it}"
			
			def playDate = source.rows(sql_play_date)
			
			def playDateBakeMin
			
			def playDateBakeMax
			
			def publishTime
			
			if(playDate){
				playDate.each {
					if(it.publish_time){
						publishTime = it.publish_time
						DateTools dt =  new DateTools()
						playDateBakeMin = dt.getBeforeTimeByH(publishTime, "yyyy-MM-dd", 90*24, "yyyy-MM-dd")
						playDateBakeMax = dt.getBeforeTimeByH(publishTime, "yyyy-MM-dd", -90*24, "yyyy-MM-dd")
					}else{
						publishTime = '1970-01-01'
					}
				}
			}else{
				publishTime = '1970-01-01'
			}
			
			/**
			 * 获取电视剧公众影响力数据
			 */
//			String sql_public_influence = "select public_influence,record_date from domain_teleplay_hot_records where teleplay_id=${it} order by record_date"
//			def publicInfluence = dataSourceIreport.rows(sql_public_influence)
//			if(publicInfluence){
//				publicInfluence.each {
//					publicInfluenceDate.add(it.record_date)
//					publicInfluenceValue.add(it.public_influence)
//				}
//			}
//			dateValueList.put("publicInfluenceDateList", publicInfluenceDate)
//			dateValueList.put("publicInfluenceValueList", publicInfluenceValue)
//			allDateValueList.put(it, dateValueList)
			
			String sql_public_influence = "select public_influence,record_date,DATEDIFF(record_date,'${publishTime}') as date_diff from domain_teleplay_hot_records where teleplay_id=${it} and record_date between '${playDateBakeMin}' and '${playDateBakeMax}' order by record_date"
			def publicInfluence = dataSourceIreport.rows(sql_public_influence)
			
			dataKeyValue = new HashMap()
			
			if(publicInfluence){
				publicInfluence.each {
					publicInfluenceDate.add(it.record_date.toString())
					dataKeyValue.put(it.date_diff.toString(), it.public_influence)
				}
			}
			
			for (int i = 0; i < timeX.size(); i++) {
				if(dataKeyValue.get(timeX[i].toString())){
					publicInfluenceValue.add(dataKeyValue.get(timeX[i].toString()))
				}else{
					publicInfluenceValue.add("-")
				}
			}
			
			dateValueList.put("publicInfluenceDateList", timeX)
			dateValueList.put("publicInfluenceValueList", publicInfluenceValue)
			
			def sdf = new SimpleDateFormat("yyyy-MM-dd")
			
			publishTime = FormatTeleplayPublishTime.format(publishTime)
			
			dateValueList.put("teleplayPlayDate", sdf.parse(publishTime))
			
			allDateValueList.put(it, dateValueList)
		}
		
		return allDateValueList
	}
}
