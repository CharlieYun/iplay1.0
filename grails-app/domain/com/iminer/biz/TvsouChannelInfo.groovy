package com.iminer.biz

import java.util.Date;

class TvsouChannelInfo {

  static mapping = {
         table 'basic_tvsou_channel_info'
         // version is set to false, because this isn't available by default for legacy databases
         version false
         // In case a sequence is needed, changed the identity generator for the following code:
//       id generator:'sequence', column:'id', params:[sequence:'basic_tvsou_channel_info_sequence']
         id generator:'identity', column:'id'
    }
    Integer id
    String channelName
    Integer tvNameId
    Date createTime
    String channelUrl
	Integer monitoringStatus

    static constraints = {
        id(max: 2147483647)
        channelName(size: 0..255)
        tvNameId(nullable: true, max: 2147483647)
        createTime(nullable: true)
        channelUrl(size: 0..255)
		monitoringStatus(nullable:true)
    }
    String toString() {
        return "${id}" 
    }
}
