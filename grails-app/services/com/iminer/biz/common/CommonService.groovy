package com.iminer.biz.common

import groovy.sql.Sql

import org.springframework.web.context.request.RequestContextHolder

/**
 * CommonService：公共service
 * @author   fangjie fangjie@iminer.com
 * @version  V1.0
 * @date 	 2015-4-22 下午6:04:20
 */
class CommonService {
	
	//数据源
	def dataSource
	//资源注入
	def grailsApplication
	//编剧类型ID
	private static int scriptWriterCategoryId = 18
	//导演类型ID
	private static int directorCategoryId = 19
	//演员类型ID
	private static int actorCategoryId = 16
	
	/**
	 * getSession:获取session
	 * @return
	 */
	def getSession(){
		RequestContextHolder.currentRequestAttributes().getSession()
	}
	
	/**
	 * 获取autocomplete补全数据
	 * @param params :相关参数
	 * @return
	 */
	def getAutoCompleteInfo(params){
		def result = []
		if(params.autoKey && params.objectType){
			def source = new Sql(dataSource)
			if(params.objectType == "4"){
				String select_sql = "SELECT a.id,a.name,b.path,a.name_locate,a.initial_alpha_locate,a.full_alpha_locate,CASE WHEN a.show_year IS NULL THEN a.`year` "+
									"ELSE a.show_year END AS show_year from "+
									"(SELECT a.id,a.`name`,YEAR(DATE_FORMAT(a.publish_time,'%Y-%m-%d')) as show_year,a.`year`, "+
									"CASE "+
									"WHEN LOCATE(?,a.`name`) = 0 THEN  100 "+
									"ELSE LOCATE(?,a.`name`) "+
									"END AS name_locate, "+
									"CASE "+
									"WHEN LOCATE(?,a.`initial_alpha`) = 0 THEN  100 "+
									"ELSE LOCATE(?,a.`initial_alpha`) "+
									"END AS initial_alpha_locate, "+
									"CASE "+
									"WHEN LOCATE(?,a.`full_alpha`) = 0 THEN  100 "+
									"ELSE LOCATE(?,a.`full_alpha`) "+
									"END AS full_alpha_locate "+
									"FROM basic_movie_info a WHERE 1=1 "
									if(!params.excludeMovieIds.equals('')){
										select_sql += "AND a.id not in (${params.excludeMovieIds}) "
									}
									select_sql+="AND (a.`name` like ? or full_alpha like ? OR initial_alpha LIKE ?) "+
									"order by name_locate ASC,initial_alpha_locate ASC,full_alpha_locate ASC,publish_time DESC  LIMIT 5 "+
									") a LEFT JOIN basic_object_images b ON a.id=b.object_id AND b.object_type=${params.objectType} AND b.image_type='playBill' "
				source.rows(select_sql,[params.autoKey,params.autoKey,params.autoKey.toUpperCase(),params.autoKey.toUpperCase(),params.autoKey.toUpperCase(),params.autoKey.toUpperCase(),
										"%"+params.autoKey+"%","%"+params.autoKey.toUpperCase()+"%","%"+params.autoKey.toUpperCase()+"%"]).each {
					def imagePath = ""
					if(it.path){
						imagePath = grailsApplication.config.grails.app.imgpath+it.path
					}else{
						imagePath = grailsApplication.config.grails.app.name+"/images/noMovePic02.png"
					}
					//导演信息
					def directorInfo = getMovieDirectorInfo(it?.id)
					def showDirectorInfo = subMovieDetailInfoLength(directorInfo, 15)
					//演员信息
					def actorInfo = getMovieActorInfo(it?.id)
					def showActorInfo = subMovieDetailInfoLength(actorInfo, 15)
					result.add(["id":it.id,"name":it.name,"image":imagePath,"year":it?.show_year,"directors":showDirectorInfo['showTitle'],"show_directors":showDirectorInfo['showContent'],"actors":showActorInfo['showTitle'],"show_actors":showActorInfo['showContent']])
				}
			}else if(params.objectType == "5"){
				String select_sql = "SELECT a.id,a.name,b.path,a.name_locate,a.initial_alpha_locate,a.full_alpha_locate,CASE WHEN a.show_year IS NULL THEN a.`year` "+
							"ELSE a.show_year END AS show_year from "+
							"(SELECT a.id,a.`name`,CAST(YEAR(DATE_FORMAT(a.publish_time,'%Y-%m-%d')) AS CHAR) as show_year,a.`year`, "+
							"CASE "+
							"WHEN LOCATE(?,a.`name`) = 0 THEN  100 "+
							"ELSE LOCATE(?,a.`name`) "+
							"END AS name_locate, "+
							"CASE "+
							"WHEN LOCATE(?,a.`initial_alpha`) = 0 THEN  100 "+
							"ELSE LOCATE(?,a.`initial_alpha`) "+
							"END AS initial_alpha_locate, "+
							"CASE "+
							"WHEN LOCATE(?,a.`full_alpha`) = 0 THEN  100 "+
							"ELSE LOCATE(?,a.`full_alpha`) "+
							"END AS full_alpha_locate "+
							"FROM basic_teleplay_info a WHERE 1=1 "
							
							if(!params.excludeMovieIds.equals('')){
								select_sql += "AND a.id not in (${params.excludeMovieIds}) "
							}
							
							select_sql += "AND (a.`name` like ? or full_alpha like ? OR initial_alpha LIKE ?) "+
							"order by name_locate ASC,initial_alpha_locate ASC,full_alpha_locate ASC,publish_time DESC  LIMIT 5 "+
							") a LEFT JOIN basic_object_images b ON a.id=b.object_id AND b.object_type=${params.objectType} AND b.image_type='playBill' "
				source.rows(select_sql,[params.autoKey,params.autoKey,params.autoKey.toUpperCase(),params.autoKey.toUpperCase(),params.autoKey.toUpperCase(),params.autoKey.toUpperCase(),
								"%"+params.autoKey+"%","%"+params.autoKey.toUpperCase()+"%","%"+params.autoKey.toUpperCase()+"%"]).each {
					def imagePath = ""
					if(it.path){
						imagePath = grailsApplication.config.grails.app.imgpath+it.path
					}else{
						imagePath = grailsApplication.config.grails.app.name+"/images/noMovePic02.png"
					}
	//				//导演信息
	//				def directorInfo = getMovieDirectorInfo(it?.id)
	//				def showDirectorInfo = subMovieDetailInfoLength(directorInfo, 15)
	//				//演员信息
	//				def actorInfo = getMovieActorInfo(it?.id)
	//				def showActorInfo = subMovieDetailInfoLength(actorInfo, 15)
//					result.add(["id":it.id,"name":it.name,"image":imagePath,"year":it?.show_year,"directors":showDirectorInfo['showTitle'],"show_directors":showDirectorInfo['showContent'],"actors":showActorInfo['showTitle'],"show_actors":showActorInfo['showContent']])
					result.add(["id":it.id,"name":it.name,"image":imagePath,"year":it?.show_year,"directors":"","show_directors":"","actors":"","show_actors":""])
				}
			}else if(params.objectType == "creator"){
				def creatorType = ""
				if("director".equals(params.detailType)){
					creatorType = 11
				}else if("scriptwriter".equals(params.detailType)){
					creatorType = 13
				}else if("actor".equals(params.detailType)){
					creatorType = 10
				}
				String select_sql = "SELECT a.id,a.name,b.path,a.name_locate,a.initial_alpha_locate,a.full_alpha_locate from "+
							"(SELECT a.id,a.`name`, "+
							"CASE "+
							"WHEN LOCATE(?,a.`name`) = 0 THEN  100 "+
							"ELSE LOCATE(?,a.`name`) "+
							"END AS name_locate, "+
							"CASE "+
							"WHEN LOCATE(?,a.`initial_alpha`) = 0 THEN  100 "+
							"ELSE LOCATE(?,a.`initial_alpha`) "+
							"END AS initial_alpha_locate, "+
							"CASE "+
							"WHEN LOCATE(?,a.`full_alpha`) = 0 THEN  100 "+
							"ELSE LOCATE(?,a.`full_alpha`) "+
							"END AS full_alpha_locate "+
							"FROM basic_artist_info a ,basic_artist_career b "+
							"WHERE 1=1 AND a.id=b.artist_id AND b.career_id=${creatorType} AND (a.`name` like ? or full_alpha like ? OR initial_alpha LIKE ?) "+
							"order by name_locate ASC,initial_alpha_locate ASC,full_alpha_locate ASC LIMIT 5 "+
							") a LEFT JOIN basic_object_images b ON a.id=b.object_id AND b.object_type=7 AND b.image_type='playBill' "
				source.rows(select_sql,[params.autoKey,params.autoKey,params.autoKey.toUpperCase(),params.autoKey.toUpperCase(),params.autoKey.toUpperCase(),params.autoKey.toUpperCase(),
								"%"+params.autoKey+"%","%"+params.autoKey.toUpperCase()+"%","%"+params.autoKey.toUpperCase()+"%"]).each {
					def imagePath = ""
					if(it.path){
						imagePath = grailsApplication.config.grails.app.imgpath+it.path
					}else{
						imagePath = grailsApplication.config.grails.app.name+"/images/noMovePic02.png"
					}
					result.add(["id":it.id,"name":it.name,"image":imagePath])
				}
			}else if(params.objectType == "680"){
				String select_sql = "SELECT a.id,a.name,b.path,a.name_locate,a.initial_alpha_locate,a.full_alpha_locate,CASE WHEN a.show_year IS NULL THEN a.`year` "+
									"ELSE a.show_year END AS show_year from "+
									"(SELECT a.id,a.`name`,YEAR(DATE_FORMAT(a.first_broadcast_date,'%Y-%m-%d')) as show_year,a.`year`, "+
									"CASE "+
									"WHEN LOCATE(?,a.`name`) = 0 THEN  100 "+
									"ELSE LOCATE(?,a.`name`) "+
									"END AS name_locate, "+
									"CASE "+
									"WHEN LOCATE(?,a.`initial_alpha`) = 0 THEN  100 "+
									"ELSE LOCATE(?,a.`initial_alpha`) "+
									"END AS initial_alpha_locate, "+
									"CASE "+
									"WHEN LOCATE(?,a.`full_alpha`) = 0 THEN  100 "+
									"ELSE LOCATE(?,a.`full_alpha`) "+
									"END AS full_alpha_locate "+
									"FROM basic_entertainment_info a WHERE 1 = 1 "
									if(!params.excludeMovieIds.equals('')){
										select_sql += "AND a.id not in (${params.excludeMovieIds}) "
									}
									select_sql+="AND (a.`name` like ? or full_alpha like ? OR initial_alpha LIKE ?) "+
									"order by name_locate ASC,initial_alpha_locate ASC,full_alpha_locate ASC,first_broadcast_date DESC  LIMIT 5 "+
									") a LEFT JOIN basic_object_images b ON a.id=b.object_id AND b.object_type=${params.objectType} AND b.image_type='playBill' "
					source.rows(select_sql,[params.autoKey,params.autoKey,params.autoKey.toUpperCase(),params.autoKey.toUpperCase(),params.autoKey.toUpperCase(),params.autoKey.toUpperCase(),
											"%"+params.autoKey+"%","%"+params.autoKey.toUpperCase()+"%","%"+params.autoKey.toUpperCase()+"%"]).each {
						def imagePath = ""
						if(it.path){
							imagePath = grailsApplication.config.grails.app.imgpath+it.path
						}else{
							imagePath = grailsApplication.config.grails.app.name+"/images/noMovePic02.png"
						}
						//导演信息
						def directorInfo = getMovieDirectorInfo(it?.id)
						def showDirectorInfo = subMovieDetailInfoLength(directorInfo, 15)
						//演员信息
						def actorInfo = getMovieActorInfo(it?.id)
						def showActorInfo = subMovieDetailInfoLength(actorInfo, 15)
						result.add(["id":it.id,"name":it.name,"image":imagePath,"year":it?.show_year,"directors":showDirectorInfo['showTitle'],"show_directors":showDirectorInfo['showContent'],"actors":showActorInfo['showTitle'],"show_actors":showActorInfo['showContent']])
					}
				}else if(params.objectType == "7"){
					String select_sql = "SELECT a.id,a.name,b.path,a.name_locate,a.initial_alpha_locate,a.full_alpha_locate from "+
					"(SELECT a.id,a.`name`, "+
					"CASE "+
					"WHEN LOCATE(?,a.`name`) = 0 THEN  100 "+
					"ELSE LOCATE(?,a.`name`) "+
					"END AS name_locate, "+
					"CASE "+
					"WHEN LOCATE(?,a.`initial_alpha`) = 0 THEN  100 "+
					"ELSE LOCATE(?,a.`initial_alpha`) "+
					"END AS initial_alpha_locate, "+
					"CASE "+
					"WHEN LOCATE(?,a.`full_alpha`) = 0 THEN  100 "+
					"ELSE LOCATE(?,a.`full_alpha`) "+
					"END AS full_alpha_locate "+
					"FROM basic_artist_info a WHERE 1=1 "
					if(!params.excludeMovieIds.equals('')){
						select_sql += "AND a.id not in (${params.excludeMovieIds}) "
					}
					
					select_sql +="AND  (a.`name` like ? or full_alpha like ? OR initial_alpha LIKE ?) "+
					"order by name_locate ASC,initial_alpha_locate ASC,full_alpha_locate ASC LIMIT 5 "+
					") a LEFT JOIN basic_object_images b ON a.id=b.object_id AND b.object_type=7 AND b.image_type='playBill' "
					source.rows(select_sql,[params.autoKey,params.autoKey,params.autoKey.toUpperCase(),params.autoKey.toUpperCase(),params.autoKey.toUpperCase(),params.autoKey.toUpperCase(),
						"%"+params.autoKey+"%","%"+params.autoKey.toUpperCase()+"%","%"+params.autoKey.toUpperCase()+"%"]).each {
					def imagePath = ""
					if(it.path){
						imagePath = grailsApplication.config.grails.app.imgpath+it.path
					}else{
						imagePath = grailsApplication.config.grails.app.name+"/images/noMovePic02.png"
					}
					result.add(["id":it.id,"name":it.name,"image":imagePath])
				}
			}
		}
		return result
	}
	
	/**
	 * getMovieDirectorInfo:获取电影导演信息
	 * @param movieId ： 电影ID
	 * @return
	 */
	def getMovieDirectorInfo(def movieId){
		def source = new Sql(dataSource)
		String select_sql = "SELECT b.id,b.`name` "+
							"FROM basic_artist_movie a,basic_artist_info b "+
							"WHERE a.movie_id=? AND a.artist_id=b.id AND a.relation=?"
		return source.rows(select_sql, [movieId,directorCategoryId])
	}
	
	/**
	 * getMovieActorInfo:获取电影演员信息
	 * @param movieId : 电影ID
	 * @return
	 */
	def getMovieActorInfo(def movieId){
		def source = new Sql(dataSource)
		String select_sql = "SELECT b.id,b.`name` "+
							"FROM basic_artist_movie a,basic_artist_info b "+
							"WHERE a.movie_id=? AND a.artist_id=b.id AND a.relation=?"
		return source.rows(select_sql, [movieId,actorCategoryId])
	}
	
	
	/**
	 * subMovieDetailInfoLength:截取电影详细信息相关显示长度
	 * @param infoArr ： 信息list
	 * @param subLength ： 截取长度
	 * @return
	 */
	def subMovieDetailInfoLength(def infoArr,def subLength){
		def showTitle = "" //标签title内容
		def showContent = "" //页面显示内容
		//处理标签title内容
		def totalLength = 0
		infoArr.each {
			showTitle += it?.name+"|"
			totalLength += it?.name.length()
			showContent += "<i>"+it?.name+"</i>|"
		}
		if(showTitle){
			showTitle = showTitle.substring(0, showTitle.length() - 1)
		}
		//处理页面显示内容
		if(totalLength > subLength){
			showContent = ""
			for(int i=0;i<infoArr.size();i++){
				if(subLength - infoArr[i]?.name.length() > 0){
					subLength = subLength - infoArr[i]?.name.length()
					showContent += "<i>"+infoArr[i]?.name+"</i>|"
				}else if(subLength - infoArr[i]?.name.length() == 0){
					subLength = subLength - infoArr[i]?.name.length()
					showContent += "<i>"+infoArr[i]?.name+"</i>..."
				}else{
					if(subLength - 1 >= 0){
						showContent += "<i>"+infoArr[i]?.name.substring(0, subLength-1)+"...</i>|"
					}
					break;
				}
			}
		}
		if(showContent){
			showContent = showContent.substring(0, showContent.length() - 1)
		}
		[showTitle:showTitle,showContent:showContent]
	}
}
