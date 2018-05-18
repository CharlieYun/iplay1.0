package com.iminer.biz


class ArtistTeleplayRole {

    static mapping = {
         table 'basic_artist_teleplay_role'
         // version is set to false, because this isn't available by default for legacy databases
         version false
         // In case a sequence is needed, changed the identity generator for the following code:
//       id generator:'sequence', column:'id', params:[sequence:'basic_artist_teleplay_role_sequence']
         id generator:'identity', column:'id'
         artistTeleplay column:'artist_teleplay_id'
    }
    Integer id
    String roleName
    // Relation
    ArtistTeleplay artistTeleplay
	static belongsTo = [artistTeleplay:ArtistTeleplay]
    static constraints = {
        id(max: 2147483647)
        roleName(size: 0..100)
        artistTeleplay()
    }
    String toString() {
        return "${id}" 
    }
}
