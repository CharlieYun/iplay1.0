package com.iminer.biz

class TeleplayChannel {

   static mapping = {
         table 'basic_teleplay_channel'
         // version is set to false, because this isn't available by default for legacy databases
         version false
         // In case a sequence is needed, changed the identity generator for the following code:
//       id generator:'sequence', column:'id', params:[sequence:'basic_teleplay_channel_sequence']
         id generator:'identity', column:'id'
		 channel column:'channel_id'
		 teleplay column:'teleplay_id'
    }
    Integer id
    TvsouChannelInfo channel
    Teleplay teleplay
	

    static constraints = {
        id(max: 2147483647)
        channel(max: 2147483647)
        teleplay(max: 2147483647)
		
    }
    String toString() {
        return "${id}" 
    }
}
