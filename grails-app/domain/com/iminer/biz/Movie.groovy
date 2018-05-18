package com.iminer.biz

import com.iminer.biz.brand.OrganizationMovie


class Movie {
	 static mapping = {
		dynamicInsert true
		dynamicUpdate true
         table 'basic_movie_info'
         // version is set to false, because this isn't available by default for legacy databases
         version false
         id generator:'identity', column:'id'
		 //关联类型表
		 movieTypes joinTable : 'basic_movie_type'
		 //关联语言表
		 movieLanguages joinTable : 'basic_movie_language'
//		 artists joinTable : 'basic_artist_movie'
//		 directors joinTable : [name:'basic_artist_movie', key:'movie_id']
//		 scriptwriters joinTable : [name:'basic_artist_movie', key:'movie_id']
		 countries joinTable : [name:'basic_movie_country',key:'movie_id']
		// organizations joinTable : [name:'basic_organization_movie',key:'organization_id']
    }
	
	static belongsTo = [Category,CountryArea]//,Artist]
//	static hasMany=[movieLanguages:Category,movieTypes:Category,artists:Artist,directors:Artist,scriptwriters:Artist,countries:CountryArea]
	static hasMany=[organizationMovie:OrganizationMovie,movieLanguages:Category,movieTypes:Category,artists:ArtistMovie,directors:ArtistMovie,scriptwriters:ArtistMovie,countries:CountryArea]
	
    Integer id
    String name
    Short countryId
    String area
    String alias
    String story
    String behindScene
    String homepage
    String year
	Integer publishFlag=0
    String publishTime
    String lengthTime
    String grade
    String indexAlpha
    Integer needEidt
    String rawSourceIds
    Long version
    String country
    Float rate
    Short filter
    String helpWords
	String antiWords
    String playState
    String imdbNo
    String foreignName
	String scriptwriter
	String director
	String searchKey
	Date reflectTime
	Integer isAmbiguity = 0
	String generalAlias
	String ordinaryAlias
	Date dateCreated
	Date lastUpdated
	Integer discern = 0
	String fullAlpha
	String initialAlpha
	Integer boxOfficeMillion
	
	String isConfirm
	Integer movieType = 0
	
	Integer productionCost

//	static searchable =
//	 {
//		only:["name","year","director"]
//	}
	
    static constraints = {
        id(max: 2147483647)
        name(size: 1..100, blank: false)
        countryId(nullable: true)
        area(size: 0..100,nullable: true)
        alias(size: 0..500,nullable: true)
        story(nullable: true)
        behindScene(nullable: true)
        homepage(size: 0..300,nullable: true)
        year(size: 1..4, blank: false)
        publishTime(size: 0..500,nullable: true)
        lengthTime(size: 0..300,nullable: true)
        grade(size: 0..500,nullable: true)
        indexAlpha(size: 1..2,nullable: true)
        needEidt(max: 2147483647,nullable:true)
        rawSourceIds(size: 0..1000,nullable:true)
        version(max: 9223372036854775807L)
        country(size: 1..255,nullable: true)
        rate(nullable: true)
        filter(nullable: true)
        helpWords(size: 0..256,nullable: true)
		antiWords(size: 0..256,nullable: true)
        playState(size: 0..50,nullable: true)
        imdbNo(size: 0..100,nullable: true)
        foreignName(size: 0..100,nullable: true)
		scriptwriter(nullable:true)
		director(nullable:true)
		searchKey(nullable:true)
		reflectTime(nullable: true)
		generalAlias(nullable: true)
		ordinaryAlias(nullable: true)
		dateCreated(nullable:true)
		lastUpdated(nullable:true)
		fullAlpha(nullable:true)
		initialAlpha(nullable:true)
		boxOfficeMillion(nullable:true,max: 2147483647)
		isConfirm(nullable:true)
		movieType(nullable:true)
		productionCost(max: 2147483647)
    }
	
	static namedQueries = {
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
		//默认list
		startQuerylist{
			and{
				order('lastUpdated','desc')
				order('id','desc')
			}
			eq('filter',1 as short)
		}
		
		//返回按字段分组及数量
		getGroupByColumn {
			
			col ->
			
			projections {
				groupProperty(col)
				rowCount()
			}
			order(col,'desc')
		}
		searchByNameOrAlias{
			key ->
			or{
				like('name','%'+key+'%')
				like('alias','%'+key+'%')
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
				like('alias','%'+key+'%')
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
		//多条件查询
		getListByConditions{
			params ->
			
			if(params?.movieType){
				eq('movieType',params.movieType as int)
			}
			//按年代
			if(params?.year){
				eq('year', params.year)
			}
			//按国家
			if(params?.country){
				countries{
					idEq(Integer.parseInt(params.country))
				}
//				eq('countryId',params?.country as short)
			}
			//按语言
			if(params?.language){
				movieLanguages{
					idEq(Integer.parseInt(params.language))
				}
			}
			//按类型
			if(params?.type){
				movieTypes
				{
					idEq(Integer.parseInt(params.type))
				}
			}
			//按关键字
			if(params?.keyWord){
				or{
					like('name','%'+params.keyWord+'%')
					like('alias','%'+params.keyWord+'%')
					like('generalAlias','%'+params.keyWord+'%')
					like('ordinaryAlias','%'+params.keyWord+'%')
				}
				
			}
			//按地区
			if(params?.area){
				eq('area',params?.area)
			}
			//按filter
			if(params?.filter){
				eq('filter',params?.filter as short)
			}
			if(params?.isAmbiguity){
				eq('isAmbiguity',params?.isAmbiguity as int)
			}
			if(params?.discern){
				eq('discern',params?.discern as int)
			}
			and{
				order('lastUpdated','desc')
				order('id','desc')
				order("filter","desc")
			}
		}
		
		getByAlpha{params ->
			eq("indexAlpha",params.indexAlpha)
			eq("area",params.area)
			and{
				order('lastUpdated','desc')
				order('id','desc')
			}
		}
		
		getByName{params ->
			like("name","%${params.query}%")
			and{
				order('lastUpdated','desc')
				order('id','desc')
			}
		}
	}
    String toString() {
        return "${id}" 
    }
}
