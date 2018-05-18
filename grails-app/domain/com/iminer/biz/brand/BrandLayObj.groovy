package com.iminer.biz.brand

import java.util.Date;

/**
 * hanty
 */
class BrandLayObj {
  static mapping = {
	  table 'basic_brand_lay_obj'
	  version false
	//  id generator:'identity', column:'id'
	  company  column:'organization_id'
	  brand column:'brand_id'
	  name column:'lay_obj_name'
  }
  Integer id
  String name
  OrganizationInfo company
  BrandInfo  brand
  String indexAlpha
  
  static belongsTo = [company:OrganizationInfo,brand:BrandInfo]
  
  static constraints = {
	  id(max: 2147483647)
	  name(size: 0..100)
	  company(nullable: false)
	  brand(nullable: true)
	  indexAlpha(size: 1..2, blank: false)
  }
  
  String toString() {
	  return "${id}"
  }
}
