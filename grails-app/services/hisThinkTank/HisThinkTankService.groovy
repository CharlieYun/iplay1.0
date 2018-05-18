package hisThinkTank

import groovy.sql.Sql

class HisThinkTankService {

	def dataSource
	
	def getAllTeleplayCount(select_hql,paramArr){
		
		def source = new Sql(dataSource)
		
		def allCount = source.firstRow(select_hql,paramArr);
		
		return allCount?.myCount ;
		
	}	
}
