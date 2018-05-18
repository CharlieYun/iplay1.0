package com.iminer.biz.brand

import com.iminer.biz.CountryArea
import com.iminer.biz.Movie

/**
 * The BasicOrganizationInfo entity.
 *
 * @author   hanty 
 *
 *
 */
class OrganizationInfo {
    static mapping = {
		dynamicInsert true
		dynamicUpdate true
         table 'basic_organization_info'
         // version is set to false, because this isn't available by default for legacy databases
         version false
         id generator:'identity', column:'id'
		 brandDetailType column:'brandDetailType'
		 property column:'property'
		 industry column:'industry'
		 countryArea column:'country_id'
		 //movies joinTable : [name:'basic_organization_movie',key:'movie_id']
    }
	static belongsTo = [CodeOrganizationBrand,CountryArea,Movie]
	
    Integer id
    String name
	String alias
    String foreignName
    String contact
    String registerAddress
    String operationAddress
    String contacePhone
    String fax
    String email
    String introduction
    String website
    String logoPath
    String legalPerson
    Date registerDate
	String indexAlpha
	String helpWords
	String antiWords
	Integer needEidt
	Short filter
	String imdbNo
	String searchKey
	Integer isAmbiguity = 0
	String generalAlias
	String ordinaryAlias
//	Date dateCreated
	Date lastUpdated
	// Relation
	CodeOrganizationBrand brandDetailType
	CodeOrganizationBrand industry
	// Relation
	CodeOrganizationBrand property
	// Relation
	CountryArea countryArea

	static hasMany = [brands:BrandInfo,organizationMovie:OrganizationMovie]
	
    static constraints = {
        id(max: 2147483647)
        name(size: 1..100, blank: false)
		alias(size: 0..500,nullable: true)
        foreignName(size: 0..100,blank:true)
        contact(size: 0..200,blank:true)
        registerAddress(size: 0..200,blank:true)
        operationAddress(size: 0..200,blank:true)
        contacePhone(size: 0..100,nullable:true)
        fax(size: 0..100,nullable:true)
        email(size: 0..100,blank:true)
        introduction(blank:true)
        website(size: 0..100,nullable: true)
        logoPath(nullable:true,size: 0..500)
        legalPerson(size: 0..100,blank:true)
        registerDate(nullable: true)
		brandDetailType(nullable: false)
        industry(nullable: false)
        property(nullable: true)
		helpWords(size: 0..256,nullable: true)
		antiWords(size: 0..256,nullable: true)
        countryArea(nullable:true)
		indexAlpha(size: 1..2, blank: false)
		needEidt(max: 2147483647,nullable:true)
		filter(nullable: true)
		imdbNo(size: 0..100,nullable: true)
		searchKey(nullable:true)
		generalAlias(nullable: true)
		ordinaryAlias(nullable: true)
//		dateCreated(nullable:true)
		lastUpdated(nullable:true)
    }
	
	static namedQueries={
		queryByConditions{
			
			params ->
			like('name','%'+params.searchValue)
			if(params?.ifFilter){
				if(params.ifFilter=='0'){
					or{
						eq('filter',params?.ifFilter as short)
						isNull('filter')
					}
				}else{
				
					eq('filter',params?.ifFilter as short)
				}
			}
			and{
				order('lastUpdated','desc')
				order('id','desc')
			}
			
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
		
		//默认list
		startQuerylist{
			and{
				order('lastUpdated','desc')
				order('id','desc')
			}
			eq('filter',1 as short)
		}
		
		/**
		 * 名字、类型、所属行业查询
		 * property
         * brandDetailTypes
         * brandTypes
		 */
		getOrganizationByNameAndPropertyAndIndustry{params ->
				
			or{
					like("name","%"+params.keyWord+"%")
					like("alias","%"+params.keyWord+"%")
				}
			if(params.property){
				property{
					
					eq('id',params.property as int)
				}
			}
			if(params.brandDetailType && params.brandDetailType!='null'){
				 brandDetailType{
				eq('id',params.brandDetailType as int)
				 
			 }
			}	 
	    if(params.industry && params.industry!='null'){
				 industry{
				eq('id',params.industry as int)
				 }
			 
			}

		and{
			order('lastUpdated','desc')
			order("filter","desc")
			order('id','desc')
		}
// **************************************************			
//			
//			if(params.industry){
//				industry{
//					
//					eq('id',params.industry as int)
//				}
//			}
// **************************************************
			
		}
		
	}
	
    String toString() {
        return "${id}" 
    }
}
