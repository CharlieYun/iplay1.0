package system

import groovy.sql.Sql

class PersonService {
	
	def dataSource
	def dataSource_operation
	
	
	def doUpdatePersonPassword(def person_id,def password,def oldPassword){
		
		if(oldPassword==null || password == null || "".equals(oldPassword) || "".equals(password)) {
			return "原始密码和新密码不能为空" ;
		}
		else if(oldPassword.equals(password)){
			return "新密码与原始密码不能一样" ;
		}
		def source = new Sql(dataSource)
		
		String sql = "update privilege_person set password = '${password}' where id = ${person_id} and password = '${oldPassword}'";
		def changeCount = source.executeUpdate(sql);
		return changeCount>0?"更新成功":"原始密码错误";
	}
	
	
	def getPersonList(def params){
		
		def max = params?.max==null?10:params?.max;
		def offset = params?.offset==null?0:params?.offset;
		def name = params?.name == null ? "" : params.name ;
		def source = new Sql(dataSource_operation)
		String sql = "SELECT p.id,p.name,p.common_duties,o.name as deptName  FROM operation.`operation_person` p ,operation.`privilege_organization` o WHERE p.organization_id = o.id and p.name like '%${name}%' limit "+offset+","+max;
		
		return source.rows(sql);
		
	}
	
	def getAllPersonCount(def params){
		def name = params?.name == null ? "" : params.name ;
		def source = new Sql(dataSource_operation)
		String sql = "SELECT count(*) as myCount  FROM operation.`operation_person` WHERE  name like '%${name}%'";
		def myCount = source.firstRow(sql);
		return myCount.myCount ;
	}

}
