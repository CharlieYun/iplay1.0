package hisThinkTank

import grails.converters.JSON
import groovy.sql.Sql

import com.iminer.biz.ArtistMovie
import com.iminer.biz.ArtistTeleplay
import com.iminer.biz.Movie
import com.iminer.biz.ObjectImages
import com.iminer.biz.Teleplay
import com.iminer.biz.utils.RuleTools
import com.iminer.utils.DateTools
import com.iminer.utils.NumberTools

class MarketdataService {
	def dataSource
	def dataSource_cobar_reputation
	def dataSource_ireport
	def dataSource_mycat_reputation
	private final int teleplayArtistRela = 16 //演员出演电影关系ID
	private final int teleplayDirectorRela = 19 //电影导演关系ID
	private final int teleplayScriptWriterRela = 18 //电影编剧关系ID
	
	private final int movieDirectorRela = 19 //电影导演关系ID
	private final int movieScriptWriterRela = 18 //电影编剧关系ID
	
   /**
	 * 获得电视剧列表信息
	 * @return
	 */
	def getTeleplay(Teleplay teleplay){
		def teleInfo=[:]
		teleInfo.put("id",teleplay?.id)       //电视剧ID
		def img = ObjectImages.queryImage(teleInfo?.id,5,"playBill").list()[0]
		def image = img?img.path:''
		if(image?.contains("\\")){
			image = image?.replaceAll("\\\\","/")
		}
		teleInfo.put("img",image)	//电视剧图片
		teleInfo.put("name",teleplay?.name)	//电视剧名称
		return teleInfo
	}
	
	
	/**
	 * 拼装电视剧详细信息
	 * @return
	 */
	def getTeleplayInfo(Teleplay teleplay){
		def teleInfo=[:]
		DateTools dt = new DateTools()
		teleInfo.put("id",teleplay?.id)       //电视剧ID
		
		def img = ObjectImages.queryImage(teleInfo?.id,5,"playBill").list()[0]
		def image = img?img.path:''
		if(image?.contains("\\")){
			image = image?.replaceAll("\\\\","/")
		}
		
		teleInfo.put("img",image)	//电视剧图片
		teleInfo.put("name",teleplay?.name)	//电视剧名称
		teleInfo.put("publishTime",teleplay?.publishTime)	//电视剧发布时间
		teleInfo.put("productionCost", teleplay?.productionCost) //制片成本
		if(teleplay?.teleplayAttributes==0){
			teleInfo.put("teleplayAttributes", "电视剧") //电视剧属性
		}else if(teleplay?.teleplayAttributes==1){
			teleInfo.put("teleplayAttributes", "网剧") //电视剧属性
		}else if(teleplay?.teleplayAttributes==2){
			teleInfo.put("teleplayAttributes", "网台联播") //电视剧属性
		}else{
			teleInfo.put("teleplayAttributes", "")
		}
		
		//电视剧分类信息
		def source = new Sql(dataSource)
		
		def dataSourceMycatReputation = new Sql(dataSource_mycat_reputation)
		
//		String querySql = "SELECT a.theme_name FROM basic_ent_theme a,basic_theme_relation b where a.theme_id=b.theme_two_id and b.object_type=5 and b.object_id="+teleplay?.id
		String querySql ="select b.`name` from basic_teleplay_classification a , code_teleplay_classification b where a.classification_one=b.cid and a.teleplay_id="+teleplay?.id
		def themeName = source.rows(querySql)
		def tn=""
		themeName?.each{
				tn += it.name
		}
		teleInfo.put("themeName", tn)
		
		//电视剧IP来源信息
		querySql="SELECT a.ip_source_name from basic_intellectual_property_source_info a, basic_intellectual_property_relation b where a.id=b.ip_source_id and b.object_type=5 and b.object_id="+teleplay?.id
		def ipSourceName=source.rows(querySql)
		def ipName=""
		ipSourceName?.each {
			ipName+= it.ip_source_name
		}
		teleInfo.put("ipName", ipName)
		
		//网络播出平台
		querySql="select a.`name` from basic_site_info a ,basic_site_relation b where a.id=b.site_id and b.object_type=5 and b.object_id="+teleplay?.id
		def siteNameList=source.rows(querySql)
		def siteName=""
		int j=0
		siteNameList?.each{
			if(j==0){
				siteName += it.name
			}else{
				siteName += ";"+it.name
			}
			j++
		}
		teleInfo.put("siteName", siteName)
		
		//网络首播平台、网络首播时间
		querySql="SELECT b.`name`,a.premiere_date from basic_site_relation a,basic_site_info b where a.premiere_platform=0 and a.site_id=b.id and a.object_type=5 and a.object_id="+teleplay?.id
		def webList=source.rows(querySql)
		def premName=""		//首播平台
		def premDate=""		//首播时间
		j=0
		webList?.each {
			if(j==0){
				premName+=it.name
				premDate+=it.premiere_date
			}else{
				premName += ";"+it.name
			}
			j++
		}
		teleInfo.put("premName", premName)
		teleInfo.put("premDate", premDate)
		
		//播出方式
		querySql="select b.broadcast_mode from basic_site_relation a,baisc_broadcast_mode_info b where a.object_id="+teleplay?.id+" and a.bbmi_id=b.bbmi_id limit 1"
		def broadModeList=source.rows(querySql)
		def broadMode=""	//播出方式
		broadModeList?.each {
			broadMode= it.broadcast_mode
		}
		teleInfo.put("broadMode", broadMode)
		
		//上星首播平台
		querySql="select a.channel_name,b.premiere_date from basic_tvsou_channel_info a ,basic_teleplay_channel b  where a.id=b.channel_id and b.premiere_platform=0 and  b.teleplay_id="+teleplay?.id
		def channelList=source.rows(querySql)
		def channelName=""
		def channelPremiereData = ""//首播日期
		j=0
		channelList.each {
			if(j==0){
				channelName+=it.channel_name
				channelPremiereData = it.premiere_date
			}else{
				channelName += ";"+it.channel_name
			}
			j++
		}
		teleInfo.put("channelName", channelName)
		teleInfo.put("channelPremiereData", channelPremiereData)
		
		//发行公司 20
		querySql="select organization_name from basic_organization_teleplay where type=20 and teleplay_id="+teleplay?.id
		def organ20=source.rows(querySql)
		def organ20Name=""
		organ20.each {
			organ20Name+=it.organization_name
		}
		teleInfo.put("organ20Name", organ20Name)
		
		//出品公司 21
		querySql="select organization_name from basic_organization_teleplay where type=21 and teleplay_id="+teleplay?.id
		def organ21=source.rows(querySql)
		def organ21Name=""
		organ21.each {
			organ21Name+=it.organization_name
		}
		teleInfo.put("organ21Name", organ21Name)
		
		//电视剧好评率
//		def dataSourceCcobarReputation = new Sql(dataSource_cobar_reputation)
		def dataSourceCcobarReputation = new Sql(dataSource_mycat_reputation)
		RuleTools rt=new RuleTools();
//		def teleplayPraise="ireport_teleplay_praise_statistic"+rt.getTableSuffixName(teleplay?.id)
		def teleplayPraise="ireport_teleplay_praise_statistic"
		querySql="SELECT (sum(positive_num)/(sum(positive_num)+sum(negative_num)))*100 positive_percent FROM "+teleplayPraise+" where teleplay_id="+teleplay?.id+" and pools_num="+teleplay?.id+" GROUP BY pools_num"
		def positivePercent=dataSourceCcobarReputation.rows(querySql)
		def pp
		positivePercent?.each{
			pp = it.positive_percent
		}
		if(pp){
			pp = NumberTools.round(pp, 1)+"%"
		}else{
			pp = '无'
		}
		teleInfo.put("positivePercent", pp)
		
		//电视剧好评率图表
		querySql="SELECT (sum(positive_num)/(sum(positive_num)+sum(negative_num)))*100 positive_percent,record_date FROM "+teleplayPraise+" where teleplay_id="+teleplay?.id+" and pools_num="+teleplay?.id+" GROUP BY record_date order by record_date"
		def ppList=dataSourceCcobarReputation.rows(querySql)
		def ppArrayPP=[]
		def ppArrayPD=[]
		ppList.each {
			if(it.positive_percent == null){
				ppArrayPP.add('-')
			}else{
				ppArrayPP.add(it.positive_percent)
			}
			ppArrayPD.add(dt.fmtDate(it.record_date, "yyyy-MM-dd"))
		}
		def p=new HashMap()
		p.put("ppList", ppArrayPP)
		p.put("pdList", ppArrayPD)
		teleInfo.put("p", p as JSON)
		
		//媒体关注度-公众影响力
		def source_ireport = new Sql(dataSource_ireport)
		querySql="select hot_rate,record_date record_date,public_influence from domain_teleplay_hot_records where teleplay_id="+teleplay?.id+" order by record_date "
		def hotRateList=source_ireport.rows(querySql)
		def hotRateArrayHR=[]
		def hotRatePublicArrayDate=[]	//媒体关注度和公众影响力的日期
		def publicInfluenceArray=[]
		hotRateList.each {
			hotRateArrayHR.add(it.hot_rate)
			hotRatePublicArrayDate.add(dt.fmtDate(it.record_date, "yyyy-MM-dd"))
			publicInfluenceArray.add(it.public_influence)
		}
		def hrList=new HashMap()
		hrList.put("hotRateList", hotRateArrayHR)
		hrList.put("hotRatePublicDateList", hotRatePublicArrayDate)
		hrList.put("publicInfluenceList", publicInfluenceArray)
		teleInfo.put("hrList", hrList as JSON)
		
		
		//电视剧参演的演员
		querySql="select b.`name` aName,a.`name` bName,c.`name` cName,a.description from basic_artist_productions_role a, basic_artist_info b, code_role c where a.artist_id=b.id and a.`level`=c.cid and object_type=5 and object_id="+teleplay?.id
		def artistList=source.rows(querySql)
		teleInfo.put("artistList", artistList)
		
		
//		def timeX = []
//		
//		
//		(-90..90).each {
//			timeX.add(it)
//		}
		
		
		String sql_play_date = "SELECT publish_time from basic_teleplay_info where id = "+teleplay?.id
		
		def playDate = source.rows(sql_play_date)
		
		def playDateBakeMin
		
		def playDateBakeMax
		
		def publishTime
		
		if(playDate){
			playDate.each {
				if(it.publish_time){
					publishTime = it.publish_time
					DateTools dtt =  new DateTools()
					playDateBakeMin = dtt.fmtDate(publishTime, "yyyy-MM-dd", "yyyy-MM-dd")
					playDateBakeMax = dtt.getBeforeTimeByH(publishTime, "yyyy-MM-dd", -90*24, "yyyy-MM-dd")
				}else{
					publishTime = '1970-01-01'
				}
			}
		}else{
			publishTime = '1970-01-01'
		}
		
		def thirtyFiveDataKeyValue
		
		def minRecordTime
		
		def maxRecordTime
		
		def increRecordTime
		
		/**
		 * 获取电视上星首播
		 */
		String sql_channel_id_name = "select id,channel_name from basic_tvsou_channel_info where monitoring_status=1 and id in (select channel_id from basic_teleplay_channel where teleplay_id="+teleplay?.id+")"
		def channelIdName = source.rows(sql_channel_id_name)
		def thirtyFiveRateValue = [:]
		
		if(channelIdName){
			channelIdName.each { its ->
				//电视剧收视率35
//				querySql="SELECT date_format(record_date,'%Y-%m-%d') record_date,avg(rate_num) as rate_num FROM `domain_teleplay_audience_rating` where rate_type='00350001' and channel_id=${its.id}  and start_time >= '19:15:00'  and end_time <= '21:45:00' and object_type = 5 and object_id="+teleplay?.id + " GROUP BY  record_date order by record_date"
				
				//获取日期最大和最小值
				String min_max_time = "select date_format(min(record_date), '%Y-%m-%d') record_date_min,date_format(max(record_date), '%Y-%m-%d') record_date_max from (SELECT date_format(b.record_date,'%Y-%m-%d') as record_date,avg(b.rate_num) as rate_num from (select episodes,channel_id,record_date,start_time,rate_num from (select episodes,channel_id,record_date,start_time,rate_num from domain_teleplay_audience_rating where object_id= "+teleplay?.id + " and channel_id=${its.id} and object_type = 5 and rate_type='00350001' and record_date >= '${playDateBakeMin}' ORDER BY episodes,channel_id,record_date,start_time) as a GROUP BY a.episodes,a.channel_id) as b group by b.record_date order by b.record_date) as c"
				
//				String min_max_time = "select date_format(min(record_date), '%Y-%m-%d') record_date_min,date_format(max(record_date), '%Y-%m-%d') record_date_max from domain_teleplay_audience_rating where object_id="+teleplay?.id + " and channel_id=${its.id} and rate_type='00350001' and object_type = 5 and record_date >= '${playDateBakeMin}' "
				
				def m_m_time = dataSourceMycatReputation.rows(min_max_time)
				
				if(m_m_time){
					m_m_time.each {
						if(it.record_date_min != null || it.record_date_max != null){
							if(minRecordTime == null){
								minRecordTime = it.record_date_min
							}else{
								if(minRecordTime > it.record_date_min){
									minRecordTime = it.record_date_min
								}
							}
							if(maxRecordTime == null){
								maxRecordTime = it.record_date_max
							}else{
								if(maxRecordTime < it.record_date_max){
									maxRecordTime = it.record_date_max
								}
							}
						}
					}
				}
				
				increRecordTime = minRecordTime
				
				def teleplay_audience_date=[]	// 35城市电视剧收视率日期
				
				teleplay_audience_date.clear()
				
				if((increRecordTime != null && maxRecordTime != null) && (increRecordTime < maxRecordTime)){
					while (increRecordTime <= maxRecordTime) {
						teleplay_audience_date.add(increRecordTime)
						increRecordTime = dt.getBeforeTimeByH(increRecordTime, "yyyy-MM-dd", -24, "yyyy-MM-dd")
					}
				}
				
//				querySql = "select date_format(record_date,'%Y-%m-%d') record_date,avg(rate_num) as rate_num from domain_teleplay_audience_rating where object_id="+teleplay?.id + " and channel_id=${its.id} and rate_type='00350001' and object_type = 5 and record_date >= '${playDateBakeMin}' GROUP BY  record_date order by record_date"
//				querySql = "select date_format(record_date,'%Y-%m-%d') record_date,avg(rate_num) as rate_num from domain_teleplay_audience_rating where object_id="+teleplay?.id + " and channel_id=${its.id} and rate_type='00350001' and object_type = 5 and record_date >= '${playDateBakeMin}' GROUP BY  record_date order by record_date"
				querySql = "SELECT date_format(b.record_date,'%Y-%m-%d') as record_date,avg(b.rate_num) as rate_num from (select episodes,channel_id,record_date,start_time,rate_num from (select episodes,channel_id,record_date,start_time,rate_num from domain_teleplay_audience_rating where object_id= "+teleplay?.id + " and channel_id=${its.id} and object_type = 5 and rate_type='00350001' and record_date >= '${playDateBakeMin}' ORDER BY episodes,channel_id,record_date,start_time) as a GROUP BY a.episodes,a.channel_id) as b group by b.record_date order by b.record_date"
				
				
				def teleAudience35List=dataSourceMycatReputation.rows(querySql)
				
				def teleplay_audience35=[] 	//35城市电视剧收视率
//				teleAudience35List.each {
//					teleplay_audience_date.add(it.record_date)
//					teleplay_audience35.add(it.rate_num)
//				}
				
				thirtyFiveDataKeyValue = new HashMap()
				
				if(teleAudience35List){
					teleAudience35List.each {
						thirtyFiveDataKeyValue.put(it.record_date, it.rate_num)
					}
				}
				
				for (int i = 0; i < teleplay_audience_date.size(); i++) {
					if(thirtyFiveDataKeyValue.get(teleplay_audience_date[i].toString())){
						teleplay_audience35.add(String.format("%.2f", thirtyFiveDataKeyValue.get(teleplay_audience_date[i].toString())))
					}else{
						teleplay_audience35.add("-")
					}
				}
				
				def teleAudienceMap=new HashMap()
				teleAudienceMap.put("teleplay_audience_date", teleplay_audience_date)
				teleAudienceMap.put("teleplay_audience35", teleplay_audience35)
				teleAudienceMap.put("channel_name", its.channel_name)
				
//				def sdf = new SimpleDateFormat("yyyy-MM-dd")
//				
//				publishTime = FormatTeleplayPublishTime.format(publishTime)
//				
//				teleAudienceMap.put("teleplayPlayDate", sdf.parse(publishTime))
				
				thirtyFiveRateValue.put(its.id, teleAudienceMap)
			}
		}
		
		teleInfo.put("teleAudienceMap35", thirtyFiveRateValue as JSON)
		
		
//		//电视剧收视率50
//		querySql="SELECT date_format(record_date,'%Y-%m-%d') record_date,rate_num FROM `domain_teleplay_audience_rating` where rate_type='00500001' and object_type = 5 and object_id="+teleplay?.id
//		def teleAudience50List=dataSourceMycatReputation.rows(querySql)
//		def teleplay_audience50=[] 	//50城市电视剧收视率
//		teleAudience50List.each {
//			teleplay_audience50.add(it.rate_num)
//		}
//		teleAudienceMap=new HashMap()
//		teleAudienceMap.put("teleplay_audience_date", teleplay_audience_date)
//		teleAudienceMap.put("teleplay_audience50", teleplay_audience50)
//		teleInfo.put("teleAudienceMap50", teleAudienceMap as JSON)
//		
//		//电视剧收视率100
//		querySql="SELECT date_format(record_date,'%Y-%m-%d') record_date,rate_num FROM `domain_teleplay_audience_rating` where rate_type='00000001' and object_type = 5 and object_id="+teleplay?.id
//		def teleAudience100List=dataSourceMycatReputation.rows(querySql)
//		def teleplay_audience100=[] 	//50城市电视剧收视率
//		teleAudience100List.each {
//			teleplay_audience100.add(it.rate_num)
//		}
//		teleAudienceMap=new HashMap()
//		teleAudienceMap.put("teleplay_audience_date", teleplay_audience_date)
//		teleAudienceMap.put("teleplay_audience100", teleplay_audience100)
//		teleInfo.put("teleAudienceMap100", teleAudienceMap as JSON)
		
		
		//电视剧导演
		def teleplayDirector = ArtistTeleplay.findAllByTeleplayAndRelation(teleplay,teleplayDirectorRela)
		if(teleplayDirector){
			def directors = ''
			teleplayDirector.each {
				directors += it?.artist?.name + "/"
			}
			teleInfo.put("director",directors.substring(0, directors.length()-1))
		}else{
			teleInfo.put("director",'')
		}
		//电视剧编剧
		def teleplayScriptworiter = ArtistTeleplay.findAllByTeleplayAndRelation(teleplay,teleplayScriptWriterRela)
		if(teleplayScriptworiter){
			def scriptwriter = ''
			teleplayScriptworiter.each {
				scriptwriter += it?.artist?.name + "/"
			}
			teleInfo.put("scriptwriter",scriptwriter.substring(0, scriptwriter.length()-1))
		}else{
			teleInfo.put("scriptwriter",'')
		}
		
		def boxOffice=[]
		def boxOfficeDate=[]
		def boList=new HashMap()
		boList.put("boxOffice", boxOffice)
		boList.put("boxOfficeDate", boxOfficeDate)
		
		def networkDramaPlayMap=[:]
		teleInfo.put("networkDramaPlayMap", networkDramaPlayMap as JSON)
		
		
		teleInfo.put("boList", boList as JSON)
		
		return teleInfo
	}
	
	
	/**
	 * 得到网剧详细信息
	 * @author HTYang
	 * @date 2016-3-15 下午2:00:48
	 * @param teleplay
	 * @return
	 */
	def getNetPlayInfo(Teleplay teleplay){
		def teleInfo=[:]
		DateTools dt = new DateTools()
		teleInfo.put("id",teleplay?.id)       //网剧ID
		
		def img = ObjectImages.queryImage(teleInfo?.id,5,"playBill").list()[0]
		def image = img?img.path:''
		if(image?.contains("\\")){
			image = image?.replaceAll("\\\\","/")
		}
		
		teleInfo.put("img",image)	//网剧图片
		teleInfo.put("name",teleplay?.name)	//网剧名称
		teleInfo.put("publishTime",teleplay?.publishTime)	//网剧发布时间
		teleInfo.put("productionCost", teleplay?.productionCost) //制片成本
		if(teleplay?.teleplayAttributes==0){
			teleInfo.put("teleplayAttributes", "电视剧") //电视剧属性
		}else if(teleplay?.teleplayAttributes==1){
			teleInfo.put("teleplayAttributes", "网剧") //网剧属性
		}else if(teleplay?.teleplayAttributes==2){
			teleInfo.put("teleplayAttributes", "网台联播") //电视剧属性
		}else{
			teleInfo.put("teleplayAttributes", "")
		}
		
		//网剧分类信息
		def source = new Sql(dataSource)
		
		def dataSourceMycatReputation = new Sql(dataSource_mycat_reputation)
		
//		String querySql = "SELECT a.theme_name FROM basic_ent_theme a,basic_theme_relation b where a.theme_id=b.theme_two_id and b.object_type=5 and b.object_id="+teleplay?.id
		String querySql ="select b.`name` from basic_teleplay_classification a , code_teleplay_classification b where a.classification_one=b.cid and a.teleplay_id="+teleplay?.id
		def themeName = source.rows(querySql)
		def tn=""
		themeName?.each{
				tn += it.name
		}
		teleInfo.put("themeName", tn)
		
		//网剧IP来源信息
		querySql="SELECT a.ip_source_name from basic_intellectual_property_source_info a, basic_intellectual_property_relation b where a.id=b.ip_source_id and b.object_type=5 and b.object_id="+teleplay?.id
		def ipSourceName=source.rows(querySql)
		def ipName=""
		ipSourceName?.each {
			ipName+= it.ip_source_name
		}
		teleInfo.put("ipName", ipName)
		
		//网络播出平台
		querySql="select a.`name` from basic_site_info a ,basic_site_relation b where a.id=b.site_id and b.object_type=5 and b.object_id="+teleplay?.id
		def siteNameList=source.rows(querySql)
		def siteName=""
		int j=0
		siteNameList?.each{
			if(j==0){
				siteName += it.name
			}else{
				siteName += ";"+it.name
			}
			j++
		}
		teleInfo.put("siteName", siteName)
		
		//网络首播平台、网络首播时间
		querySql="SELECT b.`name`,a.premiere_date from basic_site_relation a,basic_site_info b where a.premiere_platform=0 and a.site_id=b.id and a.object_type=5 and a.object_id="+teleplay?.id
		def webList=source.rows(querySql)
		def premName=""		//首播平台
		def premDate=""		//首播时间
		j=0
		webList?.each {
			if(j==0){
				premName+=it.name
				premDate+=it.premiere_date
			}else{
				premName += ";"+it.name
			}
			j++
		}
		teleInfo.put("premName", premName)
		teleInfo.put("premDate", premDate)
		
		//播出方式
		querySql="select b.broadcast_mode from basic_site_relation a,baisc_broadcast_mode_info b where a.object_id="+teleplay?.id+" and a.bbmi_id=b.bbmi_id limit 1"
		def broadModeList=source.rows(querySql)
		def broadMode=""	//播出方式
		broadModeList?.each {
			broadMode= it.broadcast_mode
		}
		teleInfo.put("broadMode", broadMode)
		
		//上星首播平台
		querySql="select a.channel_name,b.premiere_date from basic_tvsou_channel_info a ,basic_teleplay_channel b  where a.id=b.channel_id and b.premiere_platform=0 and  b.teleplay_id="+teleplay?.id
		def channelList=source.rows(querySql)
		def channelName=""
		def channelPremiereData = ""//首播日期
		j=0
		channelList.each {
			if(j==0){
				channelName+=it.channel_name
				channelPremiereData = it.premiere_date
			}else{
				channelName += ";"+it.channel_name
			}
			j++
		}
		teleInfo.put("channelName", channelName)
		teleInfo.put("channelPremiereData", channelPremiereData)
		
		//发行公司 20
		querySql="select organization_name from basic_organization_teleplay where type=20 and teleplay_id="+teleplay?.id
		def organ20=source.rows(querySql)
		def organ20Name=""
		organ20.each {
			organ20Name+=it.organization_name
		}
		teleInfo.put("organ20Name", organ20Name)
		
		//出品公司 21
		querySql="select organization_name from basic_organization_teleplay where type=21 and teleplay_id="+teleplay?.id
		def organ21=source.rows(querySql)
		def organ21Name=""
		organ21.each {
			organ21Name+=it.organization_name
		}
		teleInfo.put("organ21Name", organ21Name)
		
		//网剧好评率
//		def dataSourceCcobarReputation = new Sql(dataSource_cobar_reputation)
		def dataSourceCcobarReputation = new Sql(dataSource_mycat_reputation)
		RuleTools rt=new RuleTools();
//		def teleplayPraise="ireport_teleplay_praise_statistic"+rt.getTableSuffixName(teleplay?.id)
		def teleplayPraise="ireport_teleplay_praise_statistic"
		querySql="SELECT (sum(positive_num)/(sum(positive_num)+sum(negative_num)))*100 positive_percent FROM "+teleplayPraise+" where teleplay_id="+teleplay?.id+" and pools_num="+teleplay?.id+" GROUP BY pools_num"
		def positivePercent=dataSourceCcobarReputation.rows(querySql)
		def pp
		positivePercent?.each{
			pp = it.positive_percent
		}
		if(pp){
			pp = NumberTools.round(pp, 1)+"%"
		}else{
			pp = '无'
		}
		teleInfo.put("positivePercent", pp)
		
		//网剧好评率图表
		querySql="SELECT (sum(positive_num)/(sum(positive_num)+sum(negative_num)))*100 positive_percent,record_date FROM "+teleplayPraise+" where teleplay_id="+teleplay?.id+" and pools_num="+teleplay?.id+" GROUP BY record_date order by record_date"
		def ppList=dataSourceCcobarReputation.rows(querySql)
		def ppArrayPP=[]
		def ppArrayPD=[]
		ppList.each {
			ppArrayPP.add(it.positive_percent)
			ppArrayPD.add(dt.fmtDate(it.record_date, "yyyy-MM-dd"))
		}
		def p=new HashMap()
		p.put("ppList", ppArrayPP)
		p.put("pdList", ppArrayPD)
		teleInfo.put("p", p as JSON)
		
		//媒体关注度-公众影响力
		def source_ireport = new Sql(dataSource_ireport)
		querySql="select hot_rate,record_date record_date,public_influence from domain_teleplay_hot_records where teleplay_id="+teleplay?.id+" order by record_date "
		def hotRateList=source_ireport.rows(querySql)
		def hotRateArrayHR=[]
		def hotRatePublicArrayDate=[]	//媒体关注度和公众影响力的日期
		def publicInfluenceArray=[]
		hotRateList.each {
			hotRateArrayHR.add(it.hot_rate)
			hotRatePublicArrayDate.add(dt.fmtDate(it.record_date, "yyyy-MM-dd"))
			publicInfluenceArray.add(it.public_influence)
		}
		def hrList=new HashMap()
		hrList.put("hotRateList", hotRateArrayHR)
		hrList.put("hotRatePublicDateList", hotRatePublicArrayDate)
		hrList.put("publicInfluenceList", publicInfluenceArray)
		teleInfo.put("hrList", hrList as JSON)
		
		
		//网剧参演的演员
		querySql="select b.`name` aName,a.`name` bName,c.`name` cName,a.description from basic_artist_productions_role a, basic_artist_info b, code_role c where a.artist_id=b.id and a.`level`=c.cid and object_type=5 and object_id="+teleplay?.id
		def artistList=source.rows(querySql)
		teleInfo.put("artistList", artistList)
		
		
		def timeX = []
		
		
		(-90..90).each {
			timeX.add(it)
		}
		
		
		String sql_play_date = "SELECT publish_time from basic_teleplay_info where id = "+teleplay?.id
		
		def playDate = source.rows(sql_play_date)
		
		def playDateBakeMin
		
		def playDateBakeMax
		
		def publishTime
		
		if(playDate){
			playDate.each {
				if(it.publish_time){
					publishTime = it.publish_time
					DateTools dtt =  new DateTools()
					playDateBakeMin = dtt.getBeforeTimeByH(publishTime, "yyyy-MM-dd", 90*24, "yyyy-MM-dd")
					playDateBakeMax = dtt.getBeforeTimeByH(publishTime, "yyyy-MM-dd", -90*24, "yyyy-MM-dd")
				}else{
					publishTime = '1970-01-01'
				}
			}
		}else{
			publishTime = '1970-01-01'
		}
		
//		def thirtyFiveDataKeyValue
//		
//		/**
//		 * 获取电视上星首播
//		 */
//		String sql_channel_id_name = "select id,channel_name from basic_tvsou_channel_info where monitoring_status=1 and id in (select channel_id from basic_teleplay_channel where teleplay_id="+teleplay?.id+")"
//		def channelIdName = source.rows(sql_channel_id_name)
		def thirtyFiveRateValue = [:]
//		if(channelIdName){
//			channelIdName.each { its ->
//				//电视剧收视率35
////				querySql="SELECT date_format(record_date,'%Y-%m-%d') record_date,avg(rate_num) as rate_num FROM `domain_teleplay_audience_rating` where rate_type='00350001' and channel_id=${its.id}  and start_time >= '19:15:00'  and end_time <= '21:45:00' and object_type = 5 and object_id="+teleplay?.id + " GROUP BY  record_date order by record_date"
//				
//				querySql = "select AVG(rate_num) as rate_num,record_date,DATEDIFF(record_date,'${publishTime}') as date_diff from domain_teleplay_audience_rating where object_id="+teleplay?.id + " and channel_id=${its.id} and rate_type='00350001' and object_type = 5 and record_date between '${playDateBakeMin}' and '${playDateBakeMax}' and start_time >= '19:15:00'  and end_time <= '21:45:00' GROUP BY  record_date order by record_date"
//				
//				
//				def teleAudience35List=dataSourceMycatReputation.rows(querySql)
//				def teleplay_audience_date=[]	// 35城市电视剧收视率日期
//				def teleplay_audience35=[] 	//35城市电视剧收视率
////				teleAudience35List.each {
////					teleplay_audience_date.add(it.record_date)
////					teleplay_audience35.add(it.rate_num)
////				}
//				
//				thirtyFiveDataKeyValue = new HashMap()
//				
//				if(teleAudience35List){
//					teleAudience35List.each {
//						thirtyFiveDataKeyValue.put(it.date_diff.toString(), it.rate_num)
//					}
//				}
//				
//				for (int i = 0; i < timeX.size(); i++) {
//					if(thirtyFiveDataKeyValue.get(timeX[i].toString())){
//						teleplay_audience35.add(thirtyFiveDataKeyValue.get(timeX[i].toString()))
//					}else{
//						teleplay_audience35.add("-")
//					}
//				}
//				
//				def teleAudienceMap=new HashMap()
//				teleAudienceMap.put("teleplay_audience_date", timeX)
//				teleAudienceMap.put("teleplay_audience35", teleplay_audience35)
//				teleAudienceMap.put("channel_name", its.channel_name)
//				
//				def sdf = new SimpleDateFormat("yyyy-MM-dd")
//				
//				publishTime = FormatTeleplayPublishTime.format(publishTime)
//				
//				teleAudienceMap.put("teleplayPlayDate", sdf.parse(publishTime))
//				
//				thirtyFiveRateValue.put(its.id, teleAudienceMap)
//			}
//		}
//		
		teleInfo.put("teleAudienceMap35", thirtyFiveRateValue as JSON)
		
		
//		网剧播放量
//		querySql="select b.id,a.`name` from basic_site_info a ,(select * from basic_teleplay_crawl_info where teleplay_object_id="+teleplay?.id+" GROUP BY source_domain) b where a.domain=b.source_domain"
//		def networkDramaPlayList=source.rows(querySql)
//		def networkDramaPlay_value=[] 	//网剧播放量
//		def networkDramaPlay_date=[] 	//网剧播放量对应日期
//		def networkDramaPlayMap=[:]
//		networkDramaPlayList.each { its->
//			def qSql="select MAX(review_num) maxReviewNum,DATE_FORMAT(crawl_time,'%Y-%m-%d') crawlTime from basic_teleplay_crawl_statistical where teleplay_id="+its.id+" GROUP BY DATE_FORMAT(crawl_time,'%Y-%m-%d')"
//			def nwDPList=source.rows(qSql)
//			nwDPList.each {
//				networkDramaPlay_value.add(it.maxReviewNum)
//				networkDramaPlay_date.add(it.crawlTime)
//			}
//			def nwDPMap=[:]
//			nwDPMap.put("networkDramaPlay_value", networkDramaPlay_value)
//			nwDPMap.put("networkDramaPlay_date", networkDramaPlay_date)
//			nwDPMap.put("networkDramaPlay_name", its.name)
//			networkDramaPlayMap.put(its.id, nwDPMap)
//		}
//		teleInfo.put("networkDramaPlayMap", networkDramaPlayMap as JSON)
		
		
		
		def thirtyFiveDataKeyValue
		
		def minRecordTime
		
		def maxRecordTime
		
		def increRecordTime
		
		/**
		 * 获取电视上星首播
		 */
		querySql="select b.id,a.`name` from basic_site_info a ,(select * from basic_teleplay_crawl_info where teleplay_object_id="+teleplay?.id+" GROUP BY source_domain) b where a.domain=b.source_domain"
		def channelIdName = source.rows(querySql)
		def networkDramaPlayMapPlayNum = [:]
		
		if(channelIdName){
			channelIdName.each { its ->
				
				String min_max_time = "select date_format(min(crawl_time), '%Y-%m-%d') record_date_min,date_format(max(crawl_time), '%Y-%m-%d') record_date_max from basic_teleplay_crawl_statistical where teleplay_id="+its.id+""
				
				//获取日期最大和最小值
//				String min_max_time = "select MAX(total_play_num) maxReviewNum,DATE_FORMAT(crawl_time,'%Y-%m-%d') crawlTime from basic_teleplay_crawl_statistical where teleplay_id="+its.id+" GROUP BY DATE_FORMAT(crawl_time,'%Y-%m-%d')"
				
				
				def m_m_time = source.rows(min_max_time)
				
				if(m_m_time){
					m_m_time.each {
						if(it.record_date_min != null || it.record_date_max != null){
							if(minRecordTime == null){
								minRecordTime = it.record_date_min
							}else{
								if(minRecordTime > it.record_date_min){
									minRecordTime = it.record_date_min
								}
							}
							if(maxRecordTime == null){
								maxRecordTime = it.record_date_max
							}else{
								if(maxRecordTime < it.record_date_max){
									maxRecordTime = it.record_date_max
								}
							}
						}
					}
				}
				
				increRecordTime = minRecordTime
				
				def teleplay_audience_date=[]	// 35城市电视剧收视率日期
				
				teleplay_audience_date.clear()
				
				if((increRecordTime != null && maxRecordTime != null) && (increRecordTime < maxRecordTime)){
					while (increRecordTime <= maxRecordTime) {
						teleplay_audience_date.add(increRecordTime)
						increRecordTime = dt.getBeforeTimeByH(increRecordTime, "yyyy-MM-dd", -24, "yyyy-MM-dd")
					}
				}
				
//				querySql = "SELECT date_format(b.record_date,'%Y-%m-%d') as record_date,avg(b.rate_num) as rate_num from (select episodes,channel_id,record_date,start_time,rate_num from (select episodes,channel_id,record_date,start_time,rate_num from domain_teleplay_audience_rating where object_id= "+teleplay?.id + " and channel_id=${its.id} and object_type = 5 and rate_type='00350001' and record_date >= '${playDateBakeMin}' ORDER BY episodes,channel_id,record_date,start_time) as a GROUP BY a.episodes,a.channel_id) as b group by b.record_date order by b.record_date"
				querySql = "select MAX(total_play_num) maxPlayNum,DATE_FORMAT(crawl_time,'%Y-%m-%d') crawlTime from basic_teleplay_crawl_statistical where teleplay_id="+its.id+" GROUP BY DATE_FORMAT(crawl_time,'%Y-%m-%d') ORDER BY crawlTime"
				
				
				def teleAudience35List=source.rows(querySql)
				
				def teleplay_audience35=[] 	//35城市电视剧收视率
				
				thirtyFiveDataKeyValue = new HashMap()
				
				if(teleAudience35List){
					teleAudience35List.each {
						thirtyFiveDataKeyValue.put(it.crawlTime, it.maxPlayNum/10000)
					}
				}
				
				for (int i = 0; i < teleplay_audience_date.size(); i++) {
					if(thirtyFiveDataKeyValue.get(teleplay_audience_date[i].toString())){
						teleplay_audience35.add(thirtyFiveDataKeyValue.get(teleplay_audience_date[i].toString()))
					}else{
						teleplay_audience35.add("-")
					}
				}
				
				def teleAudienceMap=new HashMap()
				teleAudienceMap.put("teleplay_audience_date", teleplay_audience_date)
				teleAudienceMap.put("teleplay_audience35", teleplay_audience35)
				teleAudienceMap.put("channel_name", its.name)
				
				networkDramaPlayMapPlayNum.put(its.id, teleAudienceMap)
			}
		}
		
		teleInfo.put("networkDramaPlayMap", networkDramaPlayMapPlayNum as JSON)
		
		
		
		//网剧导演
		def teleplayDirector = ArtistTeleplay.findAllByTeleplayAndRelation(teleplay,teleplayDirectorRela)
		if(teleplayDirector){
			def directors = ''
			teleplayDirector.each {
				directors += it?.artist?.name + "/"
			}
			teleInfo.put("director",directors.substring(0, directors.length()-1))
		}else{
			teleInfo.put("director",'')
		}
		//网剧编剧
		def teleplayScriptworiter = ArtistTeleplay.findAllByTeleplayAndRelation(teleplay,teleplayScriptWriterRela)
		if(teleplayScriptworiter){
			def scriptwriter = ''
			teleplayScriptworiter.each {
				scriptwriter += it?.artist?.name + "/"
			}
			teleInfo.put("scriptwriter",scriptwriter.substring(0, scriptwriter.length()-1))
		}else{
			teleInfo.put("scriptwriter",'')
		}
		
		def boxOffice=[]
		def boxOfficeDate=[]
		def boList=new HashMap()
		boList.put("boxOffice", boxOffice)
		boList.put("boxOfficeDate", boxOfficeDate)
		teleInfo.put("boList", boList as JSON)
		
		return teleInfo
	}
	
	/**
	 * 拼装电影信息
	 * @return
	 */
	def getMovie(Movie movie){
		def movieInfo=[:]
		movieInfo.put("id",movie?.id)       //电影ID
		def img = ObjectImages.queryImage(movie?.id,4,"playBill").list()[0]
		def image = img?img.path:''
		if(image?.contains("\\")){
			image = image?.replaceAll("\\\\","/")
		}
		movieInfo.put("movieType", movie?.movieType)
		movieInfo.put("img",image)	//电影图片
		movieInfo.put("name",movie?.name)	//电影名称
		return movieInfo
	}
	
	/**
	 * 拼装电影详细信息
	 * @return
	 */
	def getMovieInfo(Movie movie){
		def movieInfo=[:]
		movieInfo.put("id",movie?.id)       //电影ID
		def img = ObjectImages.queryImage(movie?.id,4,"playBill").list()[0]
		def image = img?img.path:''
		if(image?.contains("\\")){
			image = image?.replaceAll("\\\\","/")
		}
		movieInfo.put("img",image)	//电影图片
		movieInfo.put("name",movie?.name)	//电影名称
		movieInfo.put("publishTime", movie?.publishTime)	//上映日期
		movieInfo.put("productionCost", movie?.productionCost)	//制片成本
		
		//电影类型
		def source = new Sql(dataSource)
		String querySql = "select b.`name` from basic_movie_type a ,basic_ent_category b where a.type_id=b.cid and a.movie_id="+movie?.id
		def movieTypeList=source.rows(querySql)
		def movieT=""
		int j=0
		movieTypeList?.each{
			if(j==0){
				movieT += it.name
			}else{
				movieT += ";"+it.name
			}
			j++
		}
		movieInfo.put("movieType", movieT)
		
		//电影IP来源信息
		querySql="SELECT a.ip_source_name from basic_intellectual_property_source_info a, basic_intellectual_property_relation b where a.id=b.ip_source_id and b.object_type=4 and b.object_id="+movie?.id
		def ipSourceName=source.rows(querySql)
		def ipName=""
		ipSourceName?.each {
			ipName+= it.ip_source_name
		}
		movieInfo.put("ipName", ipName)
		
//		电影档期
		querySql="SELECT b.schedule_name from basic_movie_schedule_relation a , basic_movie_schedule_info b where a.schedule_id=b.schedule_id and a.movie_id="+movie?.id
		def movieScheduleList=source.rows(querySql)
		def movieSchedule=""
		movieScheduleList?.each {
			movieSchedule+= it.schedule_name
		}
		movieInfo.put("movieSchedule", movieSchedule)
		
		//		出品公司 21
		querySql="select organization_name from basic_organization_movie where type=21 and movie_id="+movie?.id
		def organ21=source.rows(querySql)
		def organ21Name=""
		organ21.each {
			organ21Name+=it.organization_name
		}
		movieInfo.put("organ21Name", organ21Name)
		
		//发行公司 20
		querySql="select organization_name from basic_organization_movie where type=20 and movie_id="+movie?.id
		def organ20=source.rows(querySql)
		def organ20Name=""
		organ20.each {
			organ20Name+=it.organization_name
		}
		movieInfo.put("organ20Name", organ20Name)
		
		//网络播出平台
		querySql="select a.`name` from basic_site_info a ,basic_site_relation b where a.id=b.site_id and b.object_type=4 and b.object_id="+movie?.id
		def siteNameList=source.rows(querySql)
		def siteName=""
		siteNameList?.each{
			if(j==0){
				siteName += it.name
			}else{
				siteName += ";"+it.name
			}
			j++
		}
		movieInfo.put("siteName", siteName)
		
		//电影导演
		def movieDirector = ArtistMovie.findAllByMovieAndRelation(movie,movieDirectorRela)
		if(movieDirector){
			def directors = ''
			movieDirector.each {
				directors += it?.artist?.name + "/"
			}
			movieInfo.put("director",directors.substring(0, directors.length()-1))
		}else{
			movieInfo.put("director",'')
		}
		
		//电影编剧
		def movieScriptworiter = ArtistMovie.findAllByMovieAndRelation(movie,movieScriptWriterRela)
		if(movieScriptworiter){
			def scriptwriter = ''
			movieScriptworiter.each {
				scriptwriter += it?.artist?.name + "/"
			}
			movieInfo.put("scriptwriter",scriptwriter.substring(0, scriptwriter.length()-1))
		}else{
			movieInfo.put("scriptwriter",'')
		}
		
		//电影好评率
//		def dataSourceCcobarReputation = new Sql(dataSource_cobar_reputation)
		def dataSourceCcobarReputation = new Sql(dataSource_mycat_reputation)
		RuleTools rt=new RuleTools();
//		def moviePraise="ireport_movie_praise_statistic"+rt.getTableSuffixName(movie?.id)
		def moviePraise="ireport_movie_praise_statistic"
		querySql="SELECT (sum(positive_num)/(sum(positive_num)+sum(negative_num)))*100 positive_percent FROM "+moviePraise+" where movie_id="+movie?.id+" and pools_num="+movie?.id+" GROUP BY pools_num"
		def positivePercent=dataSourceCcobarReputation.rows(querySql)
		def pp
		positivePercent?.each{
			pp = it.positive_percent
		}
		if(pp){
			pp = NumberTools.round(pp, 1)+"%"
		}else{
			pp = '无'
		}
		movieInfo.put("positivePercent", pp)
		
		//电影好评率图表
		querySql="SELECT (sum(positive_num)/(sum(positive_num)+sum(negative_num)))*100 positive_percent,date_format(record_date,'%Y-%m-%d') record_date FROM "+moviePraise+" where movie_id="+movie?.id+" and pools_num="+movie?.id+" GROUP BY record_date order by record_date"
		def ppList=dataSourceCcobarReputation.rows(querySql)
		def ppArrayPP=[]
		def ppArrayPD=[]
		ppList.each {
			ppArrayPP.add(it.positive_percent)
			ppArrayPD.add(it.record_date)
		}
		def p=new HashMap()
		p.put("ppList", ppArrayPP)
		p.put("pdList", ppArrayPD)
		movieInfo.put("p", p as JSON)
		
		//媒体关注度-公众影响力
		def source_ireport = new Sql(dataSource_ireport)
		querySql="select hot_rate,date_format(record_date,'%Y-%m-%d') record_date,public_influence from domain_movie_hot_records where movie_id="+movie?.id+" order by record_date "
		def hotRateList=source_ireport.rows(querySql)
		def hotRateArrayHR=[]
		def hotRatePublicArrayDate=[]	//媒体关注度和公众影响力的日期
		def publicInfluenceArray=[]
		hotRateList.each {
			hotRateArrayHR.add(it.hot_rate)
			hotRatePublicArrayDate.add(it.record_date)
			publicInfluenceArray.add(it.public_influence)
		}
		def hrList=new HashMap()
		hrList.put("hotRateList", hotRateArrayHR)
		hrList.put("hotRatePublicDateList", hotRatePublicArrayDate)
		hrList.put("publicInfluenceList", publicInfluenceArray)
		movieInfo.put("hrList", hrList as JSON)
		
		//票房信息
		querySql="SELECT box_office_num,date_format(record_date,'%Y-%m-%d') record_date FROM ireport_movie_box_office where movie_id="+movie?.id+" order by record_date "
		def boxOfficeList=source_ireport.rows(querySql)
		def boxOffice=[]
		def boxOfficeDate=[]
		boxOfficeList.each {
			boxOffice.add(it.box_office_num)
			boxOfficeDate.add(it.record_date)
		}
		def boList=new HashMap()
		boList.put("boxOffice", boxOffice)
		boList.put("boxOfficeDate", boxOfficeDate)
		movieInfo.put("boList", boList as JSON)
		
		//电影参演的演员
		querySql="select b.`name` aName,a.`name` bName,c.`name` cName,a.description from basic_artist_productions_role a, basic_artist_info b, code_role c where a.artist_id=b.id and a.`level`=c.cid and object_type=4 and object_id="+movie?.id
		def artistList=source.rows(querySql)
		movieInfo.put("artistList", artistList)
		
		def teleplay_audience_date=[]	//城市电视剧收视率日期 35和50城共用同一日期
		def teleplay_audience35=[] 	//35城市电视剧收视率
		def teleAudienceMap=new HashMap()
		teleAudienceMap.put("teleplay_audience_date", teleplay_audience_date)
		teleAudienceMap.put("teleplay_audience35", teleplay_audience35)
		movieInfo.put("teleAudienceMap35", teleAudienceMap as JSON)
		
		def teleplay_audience50=[] 	//50城市电视剧收视率
		teleAudienceMap=new HashMap()
		teleAudienceMap.put("teleplay_audience50", teleplay_audience50)
		movieInfo.put("teleAudienceMap50", teleAudienceMap as JSON)
		
		return movieInfo
	}
}
