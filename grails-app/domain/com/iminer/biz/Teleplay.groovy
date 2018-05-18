package com.iminer.biz

import java.util.Date;

import com.iminer.domain.operate.log.DomainLog

class Teleplay {

   static mapping = {
		dynamicInsert true
		dynamicUpdate true
         table 'basic_teleplay_info'
         // version is set to false, because this isn't available by default for legacy databases
         version false
         id generator:'identity', column:'id'	 
		 //关联类型表
		 teleplayTypes joinTable : 'basic_teleplay_type'
		 //关联语言表
		 teleplayLanguages joinTable : 'basic_teleplay_language'
		 teleplayVersion joinTable : 'basic_teleplay_versions_relation'
//		 artists joinTable : 'basic_artist_teleplay'
    }
	static auditable = true
	static transients = ['params']
	static belongsTo = [Category]//,Artist]
//	static hasMany=[teleplayLanguages:Category,teleplayTypes:Category,artists:Artist]
	static hasMany=[teleplayLanguages:Category,teleplayTypes:Category,artists:ArtistTeleplay,teleplayVersion:Category]
    Integer id
    String name
    Short country
    String area
    String alias
    String story
    String behindScene
    String homepage
    String year
    String publishTime
    String chapters
    String grade
    Integer needEidt
    String indexAlpha
    String rawSourceIds
    Long version
    Float rate
    Short filter
    String helpWords
	String antiWords
    String lengthTime
    Integer quantity
    String foreignName
    String imdbNo
	String searchKey
	Integer isAmbiguity = 0
	String generalAlias
	String ordinaryAlias
	String issueCompany
	
	String director
	String scriptwriter
	
	Date dateCreated
	Date lastUpdated
	Integer discern = 0
	String fullAlpha
	String initialAlpha
	Integer isSelfProduced = 0
	
	String params
	String productionCost
	Integer teleplayAttributes
	
    static constraints = {
        id(max: 2147483647)
        name(size: 1..100, blank: false)
        country(nullable: true)
        area(size: 0..100)
        alias(size: 0..500,nullable: true)
        story(nullable: true)
        behindScene(nullable: true)
        homepage(size: 0..300,nullable: true)
        year(size: 1..4, blank: false)
        publishTime(size: 0..100)
        chapters(size: 0..100,nullable: true)
        grade(size: 0..500,nullable: true)
        needEidt(nullable: true,max: 2147483647)
        indexAlpha(size: 1..2)
        rawSourceIds(nullable: true,size: 1..1000)
        version(max: 9223372036854775807L)
        rate(nullable: true)
        filter(nullable: true)
        helpWords(size: 0..256,nullable: true)
		antiWords(size: 0..256,nullable: true)
        lengthTime(size: 0..500,nullable: true)
        quantity(nullable: true, max: 2147483647)
        foreignName(size: 0..100,nullable: true)
        imdbNo(size: 0..100,nullable: true)
		searchKey(nullable:true)
		generalAlias(nullable: true)
		ordinaryAlias(nullable: true)
		director(size: 0..100,nullable:true)
		scriptwriter(size: 0..100,nullable:true)
		issueCompany(nullable: true)
		dateCreated(nullable:true)
		lastUpdated(nullable:true)
		fullAlpha(nullable:true)
		initialAlpha(nullable:true)
		productionCost(size: 0..100)
		teleplayAttributes(max: 2147483647)
    }
	
	
	
	def onSave = {
		println "new teleplay inserted"
		// may optionally refer to newState map
	}
	def onDelete = {
		println "teleplay was deleted"
		// may optionally refer to oldState map
	}
	def onChange = { oldMap,newMap ->
		println "teleplay was changed ..."
		oldMap.each({ key, oldVal ->
			if(oldVal != newMap[key]) {
			   println " * $key changed from $oldVal to " + newMap[key]
		   }
	   })
	}
	
	
	static namedQueries={
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
		matchByNameAndAlias{
			mName,mAlias ->
			or{
				eq('name',mName)
				like('alias','%'+mAlias+'%')
			}
			and{
				order('lastUpdated','desc')
				order('id','desc')
			}
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
		//多条件查询
		getListByConditions{
			params ->
			//按年代
			if(params?.year){
				eq('year', params.year)
			}
			//按国家
			if(params?.country){
				eq('country',params?.country as short)
			}
			//按语言
			if(params?.language){
				teleplayLanguages{
					idEq(Integer.parseInt(params.language))
				}
			}
			//按类型
			if(params?.type){
				teleplayTypes
				{
					idEq(Integer.parseInt(params.type))
				}
			}
			//按关键字
			if(params?.keyWord){
				like('name','%'+params.keyWord+'%')
			}
			//是否过滤
			if(params?.filter){
				eq('filter',params.filter as short)
			}
			
			if(params?.area){
				eq('area',params?.area)
			}
			and{
				order('lastUpdated','desc')
				order('id','desc')
			}
		}
		
		queryAllByNameYearAndDirectory{
			name,year ->
			
			if(name){
				eq('name',name)
			}
			if(year){
				eq('year',year)
			}
			
			and{
				order('lastUpdated','desc')
				order('id','desc')
			}
		}
	}
	
    String toString() {
        return "${id}" 
    }
	def save(def m,def updateMapList){
		
		if(updateMapList){
			new DomainLog().saveDomainOperateLog(updateMapList)
		}
		
		this.save(m)
	}
	def save(def updateMapList){
		if(updateMapList==[flush:true]){
			this.save(updateMapList)
		}else{
			if(updateMapList){
				new DomainLog().saveDomainOperateLog(updateMapList)
			}
			this.save()
		}
	}
}
