package com.iminer.biz

import java.util.Date;

class TeleplayCrawlInfo {

   static mapping = {
         table 'basic_teleplay_crawl_info'
         // version is set to false, because this isn't available by default for legacy databases
         version false
         // In case a sequence is needed, changed the identity generator for the following code:
//       id generator:'sequence', column:'id', params:[sequence:'basic_teleplay_crawl_info_sequence']
         id generator:'identity', column:'id'
		 teleplay column:'teleplay_object_id'
		 teleplayversion column:'version'
    }
    Integer id
    String teleplayName
    String publishDate
    String area
    String type
    String mainActor
    Date crawlTime
    String playUrl
    Integer levelNum
    String director
    String sourceDomain
    Integer teleplayTempId
	Teleplay teleplay 
	Integer teleplayversion

    static constraints = {
        id(max: 2147483647)
        teleplayName(size: 1..255, blank: false)
        publishDate(size: 0..20,nullable:true)
        area(size: 0..255)
        type(size: 0..255)
        mainActor(size: 0..255)
        crawlTime(nullable: true)
        playUrl(size: 0..255)
        levelNum(nullable: true, max: 2147483647)
        director(size: 0..255)
        sourceDomain(size: 0..255)
        teleplayTempId(nullable: true, max: 2147483647)
		teleplay(nullable: true)
		teleplayversion(nullable:true)
    }
	static namedQueries = {
		queryAllByTeleplayNameLike{
			
			teleplayName ->
			
			like('teleplayName','%'+teleplayName+'%')
			
		}
		queryByTelepla{
			teleplayId ->
			teleplay{
				idEq(teleplayId)
			}
		}
	}
    String toString() {
        return "${id}" 
    }
}
