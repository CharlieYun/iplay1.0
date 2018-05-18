package com.iminer.biz.brand

import com.iminer.biz.Artist

/**
 * The BasicArtistBrand entity.
 *
 * @author    
 *
 *
 */
class ArtistBrand {
    static mapping = {
         table 'basic_artist_brand'
         // version is set to false, because this isn't available by default for legacy databases
         version false
         id generator:'identity', column:'id'
		 agencyWay column:'agency_way_id'
		 artist column:'artist_id'
		 brand column:'brand_id'
		 cpId column:'product_id'
		 group column:'group_artist'
    }
	
    Integer id
	Artist artist
	BrandInfo brand
    String representArea
    Integer cost
    String beginDate
    String endDate
	CodeOrganizationBrand agencyWay
	BrandLayObj cpId
	String group
	static belongsTo = [BrandInfo,Artist,BrandLayObj]
	
    static constraints = {
        id(max: 2147483647)
		artist(nullable: true)
        representArea(size: 0..100,nullable:true)
        cost(nullable: true, max: 2147483647)
        beginDate(nullable: true)
        endDate(nullable: true)
        agencyWay(nullable: true)
		cpId(nullable: true)
		group(nullable:true)
    }
    String toString() {
        return "${id}" 
    }
}
