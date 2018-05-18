package projectRequirementService

import groovy.sql.Sql

class ProjectRequirementOverruleService {

	def dataSource_iplay
	
	def saveProjectRequirementOverrule(def params,def userId){
		def dataSource = new Sql(dataSource_iplay);
		String insertSql = "INSERT INTO `iplay`.`basic_project_requirement_overrule` (`overrule_person`,`overrule_mes`,`requirement_id`,`overrule_time`,`project_id`,`isSendMail`) VALUES ('${userId}','${params.overrule_mes}','${params.requirement_id}','"+System.currentTimeMillis()+"','${params.project_id}','0');" ;
		def result = dataSource.executeInsert(insertSql);
		if(result!=null)return true ;
		return false ;
	}
	
	def getProjectRequirementOverrule(projectRequirementId){
		def dataSource = new Sql(dataSource_iplay);
		String querySql = "select  * from basic_project_requirement_overrule where requirement_id = ${projectRequirementId}" ;
		def result = dataSource.firstRow(querySql)
		return result ;
	}
	
}
