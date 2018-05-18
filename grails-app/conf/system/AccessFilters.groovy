package system

import com.iminer.biz.cache.SystemBlackAndWhiteList;


/**
 * AccessFilters：系统过滤器
 * @author   fangjie fangjie@iminer.com
 * @version  V1.0
 * @date 	 2015-4-22 下午7:36:01
 */
class AccessFilters {
	
	def loginService

	def filters = {
		all(controller:'*',action:'*' ) {
			before = {
				if(!"checkLogin".equals(actionName) && session?.user == null){
					redirect(controller:'login',action:'checkLogin')
				}
			}
			after = { Map model ->
			}
			afterView = { Exception e ->
			}
			
		}
		
		/*all(controller:'*',action:'*' ) {
			//请求前
			before = {
//				session.user=null;
				//---写入事件日志和内容---
				def userHm=getUserAgert(request)
				log.info("\t${controllerName}\t${actionName}\t${params}\t${session?.user?session?.user?.id:null}\t${userHm?.ip}\t${userHm?.ipstr}\t${userHm?.referer}\t${userHm?.useragent}\t${System.currentTimeMillis()}")
				//--
				//请求某个具体功能
				if(actionName && controllerName){
					//检查系统黑白名单
					if(!controllerName.equals('error') && !actionName.equals('requestLimitExpection') && !controllerName.equals('login') && !actionName.equals('updateSystemBlackAndWhiteList')){
						def requestIp = userHm.ip
						def isInBlackList = false
						SystemBlackAndWhiteList sbawl = SystemBlackAndWhiteList.getInstance()
						def blackWhiteMap = sbawl.getBlackWhiteMap()
						if(requestIp in blackWhiteMap?.get("2")?.get("ip")){
							isInBlackList = true
						}else if(session?.user && session?.user?.id in blackWhiteMap?.get("2")?.get("userId")){
							isInBlackList = true
						}else{
							requestIp in blackWhiteMap?.get("2")?.get("ipRange").each {
								def ipAddressRange = it?.split("-")
								if(requestIp >= ipAddressRange[0] && requestIp <= ipAddressRange[1]){
									isInBlackList = true
								}
							}
						}
						if(isInBlackList){
							redirect(uri:"/error/requestLimitExpection")
							return false
						}
					}
					//禁用票房模块
//					if(controllerName.equals('boxOffice')){
//						redirect(uri:'/')
//						return false
//					}
					def isAjaxRequest = request.getHeader("x-requested-with") != null && request.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")
					//用户登录过期且不是访问登录页面
					if(!session.user && !controllerName.equals('login')&&!controllerName.equals('register')&&!controllerName.equals('error') && !actionName.equals('requestLimitExpection')){
						//判断请求类型是否为ajax，是则在头信息中添加超时
						if(isAjaxRequest){
							response.setHeader("timeout","true")
							response.setHeader("appname", grailsApplication.config.grails.app.name)
							response.sendError(500)
						}
						redirect(uri:'/')
						return false
					}else if(!controllerName.equals('login') && !controllerName.equals('error')&&!controllerName.equals('register')){
						//验证当前用户是否有访问该链接的权限

						def requestUrl = "/"+controllerName+"/"+actionName
						def accessUrl = session.userRequestUrl
						def isHavePermission = false
						for(int i=0;i<accessUrl.size();i++){
							if(accessUrl[i].request_url.indexOf(requestUrl) > -1){
								isHavePermission = true
							}
						}
						if(isHavePermission){
							//判断是否有对象限制
							def restrictModel = ['reputation','expect','audienceAnalysis']
							if((controllerName in restrictModel) && params.type && params.id && session.userRestrictObjects?.restrictMovie.size() > 0){
								def requestObjectId = params.id as int
								def isInRestrict = false
								session.userRestrictObjects?.restrictMovie.each {
									if(it == requestObjectId){
										isInRestrict = true
									}
								}
								if(isInRestrict){
									return
								}else{
									if("reputation".equals(controllerName) || "expect".equals(controllerName)){
										redirect(uri:"/"+controllerName+"/index")
									}else{
										redirect(uri:session.userModel[0].request_url)
									}
									return false
								}
							}else{
								return
							}
						}else{
							redirect(controller:'login',action:'checkLogin')
						}
					}
				}else if(session.user){//session存在，但请求地址不完整
					redirect(controller:'login',action:'checkLogin')
				}
			}
			after = { Map model ->
			}
			afterView = { Exception e ->
			}
		}*/
	}
	
	def getUserAgert(def httpRequest){
		def ip=httpRequest.getRemoteAddr()
		def ipstr=httpRequest.getHeader("x-forwarded-for")
		def referer=httpRequest.getHeader("referer")
		def userAgent=httpRequest.getHeader("user-Agent")
		return [ip:ip,ipstr:ipstr,referer:referer,useragent:userAgent]
	}
}
