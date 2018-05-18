package system

import javax.servlet.http.Cookie

class LoginController {

//    def index() { }
	def loginService
	def permissionService
	
		/**
		 * checkLogin:登录验证
		 * @return
		 */
		def checkLogin(){
			System.out.println("开始"+System.currentTimeMillis());
			def user
			def flashMessage
			try {
				user = loginService.checkLoginUser(params.username, params.password);
			}catch(RuntimeException e) {
				flashMessage = e.getMessage()
			}
			if(user){
				//通过用户名和密码的校验并且成功
				if(params.remuser){
					//记录登录用户名和密码
					Cookie cookie = new Cookie("login_user",params.username);
					cookie.setPath("/")//设置cookie路径，该设置为所有目录共享该cookie
					cookie.setMaxAge(60*60*24*30); //cookie 保存30天
					//Cookie的设置页面要用Response
					response.addCookie(cookie);
				}
				//加载相关内存
				def permissionInfo = permissionService.initSystemPermission('all', user?.id)
				//放入session
				session.userModel = permissionInfo.userModel
				session.userRequestUrl = permissionInfo.userRequestUrl
				session.operationInfo = permissionInfo.operationInfo 
				session.personRole = permissionInfo.personRole
				session.isTechincalSupportAccount = permissionInfo.isTechincalSupportAccount ;
//				println "是否是技术支持："+permissionInfo.isTechincalSupportAccount
//				session.userRestrictObjects = permissionInfo.userRestrictObjects
//				session.userBuyObjects = permissionInfo.userBuyObjects
				//获取默认访问地址
				def modelList = session.userModel
				System.out.println("结束："+System.currentTimeMillis());
				if(modelList){
//					def defaultUri = modelList[0].request_url
					def defaultUri="/project/projectDynamic"
					session.user = user;
					redirect(uri:defaultUri)
				}else{
					render(view:"/index",model:[flash:[message:"系统暂未开通"]])
				}
			}else{
				render(view:"/index",model:[flash:[message:flashMessage]])
			}
		}
		
		/**
		 * loginOut:退出功能
		 * @return
		 */
		def loginOut(){
			session.user = null
			session.userModel = null
			session.userRequestUrl = null
//			session.userRestrictObjects = null
//			session.userBuyObjects = null
			render(view:'/index')
		}
}
