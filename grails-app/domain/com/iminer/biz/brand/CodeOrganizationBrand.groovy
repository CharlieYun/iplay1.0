package com.iminer.biz.brand
/**
 * The CodeOrganizationBrand entity.
 *
 * @author    hanty
 *
 *
 */
class CodeOrganizationBrand {
   static mapping = {
         table 'code_organization_brand'
         // version is set to false, because this isn't available by default for legacy databases
         version false
         // In case a sequence is needed, changed the identity generator for the following code:
//       id generator:'sequence', column:'cid', params:[sequence:'code_organization_brand_sequence']
         id generator:'identity', column:'cid'
    }
    Integer id
    Integer pid
    String name
    Short displayorder
    String image
    String navigation
    String description
    Long version
    Integer languagesId

    static constraints = {
        id(max: 2147483647)
        pid(max: 2147483647)
        name(size: 1..50, blank: false)
        displayorder(nullable:true)
        image(size: 1..255, blank: true,nullable:true)
        navigation(blank: true,nullable:true)
        description(blank: true,nullable:true)
        version(max: 9223372036854775807L)
        languagesId(nullable:true,max: 2147483647)
    }
    String toString() {
        return "${id}" 
    }
}
