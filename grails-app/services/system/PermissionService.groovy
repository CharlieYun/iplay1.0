package system

import groovy.sql.Sql

import com.iminer.biz.cache.Permission
import com.iminer.biz.cache.SystemBlackAndWhiteList;
import com.iminer.utils.DateTools

/**
 * PermissionService：权限相关
 * @author   fangjie fangjie@iminer.com
 * @version  V1.0
 * @date 	 2015-5-18 下午1:37:29
 */
class PermissionService {

	def dataSource
	def dataSource_iplay
	def dataSource_operation ;
	def grailsApplication
	def dictionaryService
	
	/**
	 * initSystemPermission:初始化系统相关权限
	 * @param initType : 初始化类型(all:'全部清除',us:'清除用户系统权限',um:'清除用户模块权限',ur:'清除用户请求链接权限')
	 * @param userId : 用户ID
	 * @return
	 */
	def initSystemPermission(def initType,def userId){
		def userModel = []//用户模块权限
		def userRequestUrl = []//用户请求URL权限
//		def userRestrictObjects = []//用户限制对象信息
		def userBuyObjects = []//用户购买对象信息
		def operationInfo =[:];
		def isTechincalSupportAccount = false ;
		if(!initType || "all".equals(initType)){
//			getSystemUserPermissionCache()
			userModel = getUserModelPermissionCache(userId)
			userRequestUrl = getUserRequestUrlPermissionCache(userId)
			operationInfo = getOperationPerson(userId)
			// 获得当前登录用户是否为技术人员
			isTechincalSupportAccount = dictionaryService.thisValueIsBelongKey("technical_support_accounts", operationInfo?.id);
//			userRestrictObjects = getUserRestrictObjectsCache(userId)
//			userBuyObjects = getUserBuyObjectsCache(userId)
		}else if("us".equals(initType)){
//			getSystemUserPermissionCache()
		}else if("um".equals(initType)){
			userModel = getUserModelPermissionCache(userId)
		}else if("ur".equals(initType)){
			userRequestUrl = getUserRequestUrlPermissionCache(userId)
		}
//		[userModel:userModel,userRequestUrl:userRequestUrl,userRestrictObjects:userRestrictObjects,userBuyObjects:userBuyObjects]
		[userModel:userModel,userRequestUrl:userRequestUrl,operationInfo:operationInfo,isTechincalSupportAccount:isTechincalSupportAccount]
	}
	
	def getOperationPerson(def userId){
		def source = new Sql(dataSource_iplay)
		def source_operation = new Sql(dataSource_operation);
		def userInfo = source.firstRow("select * From privilege_person_operation where person_id = ?", userId)
		if(userInfo==null||userInfo.operation_id==null||userInfo.operation_id==0)
			return null;
		def operationInfo = source_operation.firstRow("select * From operation_person where id = ?",userInfo.operation_id)
		return operationInfo;
		
	}
	
	/**
	 * getUserBuyObjectsCache:获取用户购买对象缓存信息
	 * @param userId ： 用户ID
	 * @return
	 */
	def getUserBuyObjectsCache(def userId){
		def source = new Sql(dataSource)
		DateTools dt = new DateTools()
		def now = dt.getDate("yyyy-MM-dd HH:mm:ss")
		String select_sql = "select object_id,object_type,begin_validity_date,end_validity_date FROM privilege_ifilm_person_object_buy WHERE person_id=?"
		def result = source.rows(select_sql,[userId])
		def initList = []
		result.each {
			if(it?.begin_validity_date && it?.end_validity_date){
				if(dt.fmtDate(it?.begin_validity_date, "yyyy-MM-dd HH:mm:ss") <= now && dt.fmtDate(it?.end_validity_date, "yyyy-MM-dd HH:mm:ss") >= now){
					initList.add(it.object_type+"|"+it.object_id)
				}
			}
		}
		System.out.println("------加载用户购买对象完成,共："+initList.size()+"个------");
//		Permission.initUserModelCache(initList)
		return initList
	}
	
	/**
	 * getUserRestrictObjectsCache:获取用户约束对象缓存信息
	 * @param userId ： 用户ID
	 * @return
	 */
	def getUserRestrictObjectsCache(def userId){
		def source = new Sql(dataSource)
		String select_sql = "select object_id,object_type FROM ireport_ifilm_person_object_restrict WHERE user_id=?"
		def result = source.rows(select_sql,[userId])
		def initMovieList = []
		def initArtistList = []
		def initTeleplayList = []
		def initEnterList = []
		result.each {
			if(it?.object_type == 4){
				initMovieList.add(it?.object_id)
			}else if(it?.object_type == 7){
				initArtistList.add(it?.object_id)
			}else if(it?.object_type == 5){
				initTeleplayList.add(it?.object_id)
			}else if(it?.object_type == 680){
				initEnterList.add(it?.object_id)
			}
		}
		//如果电影无限制访问设置，则默认给出基础库限制范围
		if(initMovieList.size() == 0){
			String select_query_restrict_sql = "select id from basic_movie_info where is_query_restrict=1"
			source.rows(select_query_restrict_sql).each {
				initMovieList.add(it?.id)
			}
		}
		System.out.println("------加载用户约束电影信息完成,共："+initMovieList.size()+"个------");
		System.out.println("------加载用户约束明星信息完成,共："+initArtistList.size()+"个------");
		System.out.println("------加载用户约束电视剧信息完成,共："+initTeleplayList.size()+"个------");
		System.out.println("------加载用户约束综艺信息完成,共："+initEnterList.size()+"个------");
		return ['restrictMovie':initMovieList,'restrictArtist':initArtistList,'restrictTeleplay':initTeleplayList,'restrictEnter':initEnterList]
	}
	
	/**
	 * getSystemUserPermissionCache:获取用户系统权限缓存信息
	 * @param userId : 用户ID
	 * @return
	 */
	def getSystemUserPermissionCache(){
		def source = new Sql(dataSource)
		String select_sql = "SELECT c.id,c.username "+
							"FROM privilege_biz_systems a,privilege_system_person b,privilege_person c "+
							"WHERE a.system_code=? AND b.system_id=a.id AND b.person_id=c.id "+
							"GROUP BY a.id,c.id"
		def result = source.rows(select_sql,[grailsApplication.config.grails.app.systemcode])
		def initList = []
		result.each {
			initList.add(['id':it?.id,'username':it?.username?:''])
		}
		Permission.initSystemUserCache(initList)
	}
	
	/**
	 * getUserModelPermissionCache:获取用户功能模块权限缓存信息
	 * @param userId : 用户ID
	 * @return
	 */
	def getUserModelPermissionCache(def userId){
		def source = new Sql(dataSource)
		String select_sql = "SELECT b.show_name,b.request_url,b.is_default_request "+
							"FROM privilege_system_person a,privilege_system_model b,privilege_biz_systems c "+
							"WHERE a.person_id=? AND a.model_id=b.id AND c.system_code=? "+
							"AND c.id=a.system_id "+
							"AND b.show_type=? "+
							"GROUP BY a.person_id,b.id "+
							"ORDER BY b.order_index"
		def result = source.rows(select_sql,[userId,grailsApplication.config.grails.app.systemcode,0])
		def initList = []
		result.each {
			initList.add(it)
		}
		System.out.println("------加载用户模块权限完成,共："+initList.size()+"个------");
//		Permission.initUserModelCache(initList)
		return initList
	}
	
	/**
	 * getUserRequestUrlPermissionCache:获取用户访问链接权限缓存信息
	 * @return
	 */
	def getUserRequestUrlPermissionCache(def userId){
		def source = new Sql(dataSource)
		String select_sql = "SELECT a.person_id,b.request_url "+
							"FROM privilege_system_person a,privilege_system_model b,privilege_biz_systems c "+
							"WHERE a.person_id=? AND a.model_id=b.id AND c.system_code=? "+
							"AND c.id=a.system_id "+
							"GROUP BY a.person_id,b.id "+
							"ORDER BY b.order_index"
		def result = source.rows(select_sql,[userId,grailsApplication.config.grails.app.systemcode])
		def initList = []
		result.each {
			initList.add(it)
		}
		System.out.println("------加载用户访问链接权限完成,共："+initList.size()+"个------");
//		Permission.initUserRequestCache(initList)
		return initList
	}
	
	/**
	 * reloadSystemCache:刷新缓存
	 * @param loadType : 刷新类型(all:'全部刷新',us:'刷新用户系统权限',um:'刷新用户模块权限',ur:'刷新用户请求链接权限')
	 * @return
	 */
	def reloadSystemCache(def loadType){
		if(!loadType || "all".equals(loadType)){
			Permission.clearSystemUserCache()
			Permission.clearUserModelCache()
			Permission.clearUserRequestCache()
			initSystemPermission("all")
		}else if("us".equals(loadType)){
			Permission.clearSystemUserCache()
			getSystemUserPermissionCache()
		}else if("um".equals(loadType)){
			Permission.clearUserModelCache()
			getUserModelPermissionCache()
		}else if("ur".equals(loadType)){
			Permission.clearUserRequestCache()
			getUserRequestUrlPermissionCache()
		}
	}
	
	/**
	 * distroySystemCache:清除缓存
	 * @param distoryType : 清除类型(all:'全部清除',us:'清除用户系统权限',um:'清除用户模块权限',ur:'清除用户请求链接权限')
	 * @return
	 */
	def distroySystemCache(def distoryType){
		if(!distoryType || "all".equals(distoryType)){
			Permission.clearSystemUserCache()
			Permission.clearUserModelCache()
			Permission.clearUserRequestCache()
		}else if("us".equals(distoryType)){
			Permission.clearSystemUserCache()
		}else if("um".equals(distoryType)){
			Permission.clearUserModelCache()
		}else if("ur".equals(distoryType)){
			Permission.clearUserRequestCache()
		}
	}
	
	/**
	 * initOrUpdateSystemBlackAndWhiteList:初始化、更新系统黑白名单
	 * @return
	 */
	def initOrUpdateSystemBlackAndWhiteList(){
		def source = new Sql(dataSource)
		String select_sql = "SELECT ip_address,ip_address_range,user_id,type FROM privilege_ifilm_blacklist_info"
		def blackWhiteList = [:]
		source.rows(select_sql).each {
			def typeMap = blackWhiteList.get(it?.type.toString())?:['ip':[],'ipRange':[],'userId':[]]
			if(it?.ip_address != null){//IP地址
				typeMap.get("ip").add(it?.ip_address)
			}else if(it?.ip_address_range != null){//IP地址段
				typeMap.get("ipRange").add(it?.ip_address_range)
			}
			if(it?.user_id){
				typeMap.get("userId").add(it?.user_id)
			}
			blackWhiteList.put(it?.type.toString(), typeMap)
		}
		SystemBlackAndWhiteList sbawl = SystemBlackAndWhiteList.getInstance()
		sbawl.setBlackWhiteMap(blackWhiteList)
	}
}
