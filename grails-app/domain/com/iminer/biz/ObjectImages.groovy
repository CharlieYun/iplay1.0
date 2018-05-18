package com.iminer.biz

import java.util.Date;

class ObjectImages {

     static mapping = {
         table 'basic_object_images'
         // version is set to false, because this isn't available by default for legacy databases
//         version false
         id generator:'identity', column:'id'
    }
    Integer id
    Integer objectId
    Integer objectType
    String path
    Integer authorId
    Date createdDate
    Integer width
    Integer height
    String imageType
    Integer displayOrder
    // Relation

    static constraints = {
        id(max: 2147483647)
        objectId(max: 2147483647)
        path(size: 1..256, blank: false)
        authorId(nullable: true, max: 2147483647)
        createdDate()
        width(nullable: true, max: 2147483647)
        height(nullable: true, max: 2147483647)
        imageType(size: 0..16)
        displayOrder(nullable: true)
    }
	
	static namedQueries = {
		//查询作品相关图片
		queryImage{
			objectId,objectType,imageType ->
			if(objectId)
				eq('objectId',objectId)
			if(objectType)
				eq('objectType',objectType)
			if(imageType)
				eq('imageType',imageType)
		}
		
	}
    String toString() {
        return "${id}" 
    }
}
