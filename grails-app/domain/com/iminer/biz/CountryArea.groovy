package com.iminer.biz

class CountryArea {

    Integer id
	String code
	String name				//国家地区名称
	String type				//类型
	String nameEnglish		//英文别名
	String level
	String parentId
	
	String fullAlpha
	String initialAlpha

	static constraints = {
		fullAlpha(size:0..250,blank:true,nullable:true)
		initialAlpha(size:0..250,blank:true,nullable:true)
	}
	
	
	static mapping = {
		table "code_country_area" 
		movies joinTable : [name:'basic_movie_country',key:'country_id']
	}
	
//	static hasMany = [movies:Movie]
	
	static namedQueries = {
		findByCondition{ element ->
			eq("name",element)
		}
		searchCondition{ searchType ->
			and{
				if(searchType){
					eq("type",searchType)
				}
//				notEqual("id", 1268)
				notEqual("id", 1270)
				notEqual("id", 1271)
			}
		}
	}
}
