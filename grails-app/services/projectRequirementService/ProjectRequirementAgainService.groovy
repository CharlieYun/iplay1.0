package projectRequirementService

import groovy.sql.Sql

class ProjectRequirementAgainService {

	
	def dataSource_iplay
	
	def saveProjectRequirementAgain(def params,def userId){
		def dataSource = new Sql(dataSource_iplay);
		String insertSql = "INSERT INTO `iplay`.`basic_project_requirement_again` (`old_requirement_id`,`new_requirement_id`,`again_mes`,`again_time`,`project_id`) VALUES ('${params.old_requirement_id}','${params.new_requirement_id}','${params.again_mes}','"+System.currentTimeMillis()+"','${params.project_id}');" ;
		def result = dataSource.executeInsert(insertSql)
		if(result!=null)return true ;
		return false ;
	}
	
	def getProjectRequirementAgain(projectRequirementId){
		def dataSource = new Sql(dataSource_iplay);
		String querySql = "select  * from basic_project_requirement_again where new_requirement_id = ${projectRequirementId}" ;
		def result = dataSource.firstRow(querySql)
		return result ;
	}
}
