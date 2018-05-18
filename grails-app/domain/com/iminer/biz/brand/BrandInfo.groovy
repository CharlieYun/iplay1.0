package com.iminer.biz.brand

import java.util.Date;

/**
 * The BasicBrandInfo entity.
 *
 * @author  hanty  
 *
 *
 */
class BrandInfo {
    static mapping = {
         table 'basic_brand_info'
         // version is set to false, because this isn't available by default for legacy databases
         version false
         id generator:'identity', column:'id'
		 company  column:'company_id'
		 brandType column:'brand_type'
		 brandDetailType column:'brand_detail_type'
    }
    Integer id
    String name
    String logoPath
    OrganizationInfo company
    Date createDate
	// Relation
	CodeOrganizationBrand brandType
	// Relation
	CodeOrganizationBrand brandDetailType
	String indexAlpha
	Date lastUpdated
	Short filter
	String imdbNo
	String searchKey
	Integer isAmbiguity = 0
	String generalAlias
	String ordinaryAlias
	String alias
	String helpWords
	String antiWords
	String level

	static belongsTo = [company:OrganizationInfo]
	static hasMany = [artistBrands:ArtistBrand]
	
    static constraints = {
        id(max: 2147483647)
        name(size: 0..100,blank:false)
        logoPath(nullable:true,size: 0..500)
        createDate(nullable: true)
        brandType(nullable: false)
        brandDetailType(nullable: false)
		company(nullable: false)
		indexAlpha(size: 1..2, blank: false,nullable:false)
		lastUpdated(nullable:true)
		filter(nullable: true)
		imdbNo(size: 0..100,nullable: true)
		searchKey(nullable:true)
		isAmbiguity(nullable:true)
		generalAlias(nullable: true)
		ordinaryAlias(nullable: true)
//		dateCreated(nullable:true)
		alias(size: 0..500,nullable: true)
		helpWords(nullable:true)
		antiWords(nullable:true)
		level(nullable:true)
		
    }
	
	static namedQueries={
		
		//某机构下的品牌搜索
		queryBehideOrganizationAndByConditions{
			params ->
			if(params.keyWord){
				or{
					like("name","%"+params.keyWord+"%")
					like("alias","%"+params.keyWord+"%")
				}
			}
			if(params.organizationId){
				company{
					eq('id',params.organizationId as int)
				}
			}
			if(params.brandDetailTypes){
				brandDetailType{
//						 idEq(brandDetailType)
					eq('id',params.brandDetailTypes as int)
				}
			}
			if(params.brandTypes){
				brandType{
//						idEq(brandType)
					eq('id',params.brandTypes as int)
				}
			}
			and{
				order('lastUpdated','desc')
				order("filter","desc")
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
		 */
		getBrandByNameAndPropertyAndIndustry{
			name,brandDetailTypes,brandTypes
			 ->
//			 and{
				or{
					like("name","%"+name+"%")
					like("alias","%"+name+"%")
				}
				
				 if(brandDetailTypes){
					 brandDetailType{
//						 idEq(brandDetailType)
						 eq('id',brandDetailTypes as int)
					 }
				 }
				 if(brandTypes){
					 brandType{
//						idEq(brandType)
						 eq('id',brandTypes as int)
					 }
				 }
				 and{
					 order('lastUpdated','desc')
					 order("filter","desc")
					 order('id','desc')
				 }
//			 }
			
		}
		
	}
	
    String toString() {
        return "${id}" 
    }
}
