package com.iminer.biz


class ArtistTeleplay {

    static mapping = {
         table 'basic_artist_teleplay'
         // version is set to false, because this isn't available by default for legacy databases
         version false
         id generator:'identity', column:'id'
         artist column:'artist_id'
         teleplay column:'teleplay_id'
    }
    Integer id
    Integer relation
    // Relation
    Artist artist
    // Relation
    Teleplay teleplay
	static hasMany = [roles:ArtistTeleplayRole]
	
	static belongsTo = [artist:Artist,teleplay:Teleplay]

    static constraints = {
        id(max: 2147483647)
        relation(max: 2147483647)
        artist()
        teleplay()
    }
    String toString() {
        return "${id}" 
    }
}
