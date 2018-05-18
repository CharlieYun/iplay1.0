package com.iminer.biz


class Artist {

    static mapping = {
		 dynamicInsert true
		 dynamicUpdate true
		 table 'basic_artist_info'
         //version false
//		 movies joinTable : 'basic_artist_movie'
//		 teleplaies joinTable : 'basic_artist_teleplay'
		 careers joinTable :[name:'basic_artist_career', key:'artist_id']
//		 movieDirectors joinTable:[name:'basic_artist_movie',key:'artist_id']
//		 movieScriptwriters joinTable:[name:'basic_artist_movie',key:'artist_id']
    }
	static belongsTo = Category 
	/*
	 * careers:从业人员职业
	 */
//	static hasMany=[movies:Movie,teleplaies:Teleplay,careers:Category,movieDirectors:Movie,movieScriptwriters:Movie,artistBrands:ArtistBrand]
	static hasMany=[//movies:ArtistMovie,movieDirectors:ArtistMovie,movieScriptwriters:ArtistMovie,
					teleplaies:ArtistTeleplay,
					careers:Category]
					//artistBrands:ArtistBrand,relations:ArtistRelation]
//	static mappedBy = [movies:'artists',movieDirectors:'directors',movieScriptwriters:'scriptwriters']
	
    Integer id
    String name
    String gender
    String stageName
    CountryArea area		//所属地区
	CountryArea country	    //所属国家
    String hometown
    String chineseAlias
	String generalAlias
    String foreignName
    String foreignAlias
    String homepage
    String blog
    String weibo
    String tieba
    String birthday
    Integer constellation
    String religion
    String height
    String weight
    Integer bloodType
    String vitalStatistics
    String college
    String interest
    String family
    String description
    String memorabilia
    String indexAlpha
    Integer needEdit
    String rawSourceIds
    Long version = 0
    Short filter
	String helpWords
	String otherChNames
	String otherFoNames
	String imdbNo
	SortedSet brands
	Boolean loveState
	Integer maritalState
	Integer childrenNum
	String children
	Integer paid
	String similarWords
	String stopWord
	String togetherWord
	Date dateCreated
	Date lastUpdated
	Integer isAmbiguity = 0
	Integer discern = 0
	String nationId
	String fullAlpha
	String initialAlpha
	
	String isConfirm
	
	static namedQueries={
		getAllByFilter{
			eq('filter',1.asType(short))
			order('indexAlpha','asc')
		}
		searchByNameOrAlias{
			key ->
			or{
				like('name','%'+key+'%')
				like('chineseAlias','%'+key+'%')
			}
			
			
			and{
				order('lastUpdated','desc')
				order('id','desc')
			}
		}
		searchByNameOrAliasAndFilter{
			key,filter ->
			or{
				like('name','%'+key+'%')
				like('chineseAlias','%'+key+'%')
			}
			
			if(filter){
				if(filter=='null' || filter==0){
					or{
						isNull('filter')
						eq("filter",'0')
					}
				}else{
					eq("filter",filter)
				}
			}
			
			and{
				order('lastUpdated','desc')
				order('id','desc')
			}
		}
		
		queryByConditions{
			
			params ->
			like('name','%'+params.searchValue)
			if(params?.ifFilter){
				eq('filter',params?.ifFilter as short)
			}
			and{
				order('lastUpdated','desc')
				order('id','desc')
			}
			
		}
		
		
		//初始化查询条件，根据menu参数查询相应信息
		getSearchCondition{params ->
			//艺人
			if("sex".equals(params.key)){
				eq("gender",params.value)
			}
			//地区
//			if("area".equals(params.key)){
//				//参数为0，按照地区为'中国大陆'查询
//				if("0".equals(params.value)){
//					def area = CountryArea.findByCondition("大陆").get()
//					eq("area",area)
//				}
//				//参数为1，按照地区为'香港'查询
//				if("1".equals(params.value)){
//					def area = CountryArea.findByCondition("香港").get()
//					eq("area",area)
//				}
//				//参数为2，按照地区为'台湾'查询
//				if("2".equals(params.value)){
//					def area = CountryArea.findByCondition("台湾").get()
//					eq("area",area)
//				}
//			}
			//职业
			if("career".equals(params.key)){
				//参数为0，按照职业为'演员'查询
				if("0".equals(params.value)){
					params.career = '演员'
					careers{
						eq("name",params.career)
					}
				}
				//参数为1，按照职业为'歌手'查询
				if("1".equals(params.value)){
					params.career = '歌星'
					careers{
						eq("name",params.career)
					}
				}
				//参数为2，按照职业为'主持人'查询
				if("2".equals(params.value)){
					params.career = '主持人'
					careers{
						eq("name",params.career)
					}
				}
				//参数为3，按照职业为'导演'查询
				if("3".equals(params.value)){
					params.career = '导演'
					careers{
						eq("name",params.career)
					}
				}
				//参数为4，按照职业为'编剧'查询
				if("4".equals(params.value)){
					params.career = '编剧'
					careers{
						eq("name",params.career)
					}
				}
			}
			and{
				order('lastUpdated','desc')
				order('id','desc')
				order("filter","desc")
			}
		}
		//根据条件查询
		findByCondition{params ->
			//从业人员关键字
			if(!params.name && !params.area && !params.country &&  !params.filter){
				eq("filter",1 as short)
				//从业人员职业
				if(params.career){
					careers{
							eq("name",params.career)
						}
				}
				order("lastUpdated","desc")
			}else{
				
				if(params.name){
					or{
						like("name","%"+params.name+"%")
						like("chineseAlias","%"+params.name+"%")
					}
				}
				//从业人员地区
//				if(params.area){
//					area{
//						eq("id",params.area as int)
//					}
//				}
//				if(params.country){
//					country{
//						eq("id",params.country as int)
//					}
//				}
				//从业人员职业
				if(params.career){
					careers{
							eq("name",params.career)
						}
				}
				if(params.filter){
					if("1".equals(params.filter)){
						eq("filter",params.filter as short)
					}else{
						or{
							eq("filter",params.filter as short)
							isNull("filter")
						}
					}
				}
				and{
				order('lastUpdated','desc')
				order('id','desc')
				order("filter","desc")
				}
			}
		}
		
		//查询分组
		fingByGroup{ element ->
			projections {
				groupProperty(element)
				rowCount()
			}
		}
	}
	
    static constraints = {
        id(max: 2147483647)
        name(size: 1..500, blank: false)
        gender( blank: false)
        rawSourceIds(nullable: true)
		area(nullable: true)
		country(nullable: true)
		otherChNames(nullable: true)
        stageName(size: 0..500,nullable: true)
        hometown(size: 0..500,nullable: true)
        chineseAlias(size: 0..500,nullable: true)
		generalAlias(size: 0..500,nullable:true)
        foreignName(size: 0..500,nullable: true)
        foreignAlias(size: 0..500,nullable: true)
        homepage(size: 0..500,nullable: true)
        blog(size: 0..500,nullable: true)
        weibo(size: 0..500,nullable: true)
        tieba(size: 0..500,nullable: true)
        birthday(size: 0..500,nullable: true)
        constellation(nullable: true)
        religion(size: 0..500,nullable: true)
        height(size: 0..500,nullable: true)
        weight(size: 0..500,nullable: true)
        bloodType(nullable: true)
        vitalStatistics(size: 0..300,nullable: true)
        college(size: 0..250,nullable: true)
        interest(nullable: true)
        family(size: 0..250,nullable: true)
        description(nullable: true)
        memorabilia(nullable: true)
        indexAlpha(size: 1..2, blank: false)
        version(max: 9223372036854775807L,nullable: true)
        filter(nullable: true)
		helpWords(nullable: true)
		otherFoNames(nullable: true)
		imdbNo(nullable: true)
		loveState(nullable: true)
		maritalState(nullable: true)
		childrenNum(nullable: true)
		children(nullable: true)
		paid(nullable: true)
		
		nationId(nullable:true)
		similarWords(nullable:true)
		stopWord(nullable:true)
		togetherWord(nullable:true)
		dateCreated(nullable:true)
		lastUpdated(nullable:true)
		isConfirm(nullable:true)
		fullAlpha(nullable:true)
		initialAlpha(nullable:true)
    }
}
