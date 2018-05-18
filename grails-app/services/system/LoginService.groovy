package system
import com.iminer.biz.system.PrivilegePerson
import com.iminer.biz.system.PrivilegeSystemPerson
import com.iminer.biz.system.PrivilegeSystems
import com.iminer.utils.DateTools

class LoginService {
	def dataSource
	def grailsApplication
	
//    def serviceMethod() {
//
//    }
	/**
	 * checkLoginUser:验证登录用户有效性
	 * @param userName ： 用户名
	 * @param password ： 密码
	 * @return
	 */
	def checkLoginUser(def userName,def password){
		
		if(!userName || !password){
			throw new RuntimeException("用户名或密码错误 ")
		}
		//验证用户有效性
		def user = PrivilegePerson.findByUsernameAndPassword(userName,password)
		if(!user){
			throw new RuntimeException("用户名或密码错误 ")
		}
		//验证用户有效期
		DateTools dt=new DateTools();
		if(user.endDate){
			if(dt.getDate("yyyy-MM-dd") > dt.fmtDate(user.endDate, "yyyy-MM-dd")){
				throw new RuntimeException("该用户已过使用期")
			}
		}
		if(user.beginDate){
			if(dt.getDate("yyyy-MM-dd") < dt.fmtDate(user.beginDate, "yyyy-MM-dd")){
				throw new RuntimeException("该用户没有开通使用权")
			}
		}
		//验证用户所属系统
//		def systemCode = grailsApplication.config.grails.app.systemcode
//		def systemInfo = PrivilegeSystems.findAllBySystemCode(systemCode)
//		if(systemInfo.size() > 0){
//			def ps = PrivilegeSystemPerson.findAllByPersonAndSystem(user,systemInfo.get(0))
//			if(!ps){
//				throw new RuntimeException("该用户没有开通使用权")
//			}
//		}else{
//			throw new RuntimeException("该用户没有开通使用权")
//		}
		return user;
	}
}
