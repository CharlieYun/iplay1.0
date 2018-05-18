package system

import groovy.sql.Sql

class DictionaryService {
	
	def dataSource_iplay
	
	
	/**
	 * 根据key获得Value
	 * @param key
	 * @return
	 */
	def getValueByKey(def key){
	
		def source = new Sql(dataSource_iplay)
		def result = source.firstRow("select * From basic_dictionary where dictionaryKey like '${key}'");
		
		if(result==null)return "";
		return result.dictionaryValue ;
			
	}
	
	/**
	 * 根据key获得Value
	 * @param key
	 * @return
	 */
	def thisValueIsBelongKey(def key,def value){
	
		def source = new Sql(dataSource_iplay)
		def result = source.firstRow("select * From basic_dictionary where dictionaryKey like '${key}' and dictionaryValue like '${value}'");
		
		if(result==null)return false;
		return true ;
			
	}
	

}
