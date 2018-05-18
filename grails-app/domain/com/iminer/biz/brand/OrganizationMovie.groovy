package com.iminer.biz.brand

import com.iminer.biz.Movie
import com.iminer.biz.Category
/**
 * 
 * @author hanty
 *
 */
class OrganizationMovie {
    static mapping = {
         table 'basic_organization_movie'
         // version is set to false, because this isn't available by default for legacy databases
         version false
         id generator:'identity', column:'id'
         organization column:'organization_id'
         movie column:'movie_id'
		 type column:'type'
    }
    Integer id
    Category type
    // Relation
    OrganizationInfo organization
    // Relation
    Movie movie

	static belongsTo = [organization:OrganizationInfo,movie:Movie]
	
    static constraints = {
        id(max: 2147483647)
        type()
        organization()
        movie()
    }
    String toString() {
        return "${id}" 
    }
}
