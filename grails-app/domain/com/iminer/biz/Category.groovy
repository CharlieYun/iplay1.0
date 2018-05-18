package com.iminer.biz

class Category {

   Integer id
	Integer pid
	String name				//分类名称
	Integer displayorder	//权重
	String image			//图
	String navigation		//是否为导航
	String description		//描述
	Integer languagesId
	static constraints = {
		id(max: 2147483647)
		pid(max: 2147483647)
		name(size: 1..50, blank: false)
		displayorder()
		image(size: 1..255, blank: true)
		navigation(blank: true)
		description(blank: true)
		version(max: 9223372036854775807L)
		languagesId(nullable: true, max: 2147483647)
	}
	
	static mapping = {		
		
		table 'basic_ent_category'
		id column:'cid'
		//电影类型和语言关联自定义表
		typeMovies joinTable : [name:'basic_movie_type', key:'type_id']
		languageMovies joinTable : [name:'basic_movie_language', key:'language_id']
		typeTeleplaies joinTable : [name:'basic_teleplay_type', key:'type_id']
		languageTeleplaies joinTable : [name:'basic_teleplay_language', key:'language_id']
//		//综艺节目
//		typeEntertainmentes joinTable : [name:'basic_entertainment_type', key:'type_id']
		//从业人员职业关联自定义表
		artists joinTable:[name:'basic_artist_career', key:'career_id']
		//电视剧版本
		versionTeleplaies joinTable:[name:'basic_teleplay_versions_relation', key:'version_id']
	}
	
	static hasMany=[artists:Artist,
//					typeMovies:Movie,languageMovies:Movie,
					typeTeleplaies:Teleplay,languageTeleplaies:Teleplay,
//					typeEntertainmentes:EntertainmentInfo,
//					mainTypeMaterial:MaterialCrawlInfo,detailTypeMaterial:MaterialCrawlInfo,
					versionTeleplaies:Teleplay
				   ]
	
	static mappedBy = [typeMovies:"movieTypes",languageMovies: "movieLanguages",
						typeTeleplaies:"teleplayTypes",languageTeleplaies: "teleplayLanguages",
//						typeEntertainmentes:"entertainmentTypes",
						mainTypeMaterial:"materialMainType",detailTypeMaterial:"materialDetailType"
					  ]
	
	static namedQueries = {
		
		//按pid查找对象
		findCategoryByPid{
			pid ->
			
			eq('pid',pid)
		}
		
		//按pid查找对象
		getCodeByPidAndName{
			pid ->
			not{
				like('name',"%(已停用)%")
			}
			eq('pid',pid)
		}
		
		//按照id查找对象
		findMovieByCategoryId{
			id ->
			
			idEq(id)
		}
	}
}
