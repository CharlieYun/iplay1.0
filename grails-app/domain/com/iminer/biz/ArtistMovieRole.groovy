package com.iminer.biz
/**
 * The BasicArtistMovieRole entity.
 *
 * @author    
 *
 *
 */
class ArtistMovieRole {
    static mapping = {
         table 'basic_artist_movie_role'
         // version is set to false, because this isn't available by default for legacy databases
         version false
         // In case a sequence is needed, changed the identity generator for the following code:
//       id generator:'sequence', column:'id', params:[sequence:'basic_artist_movie_role_sequence']
         id generator:'identity', column:'id'
         artistMovie column:'artist_movie_id'
    }
    Integer id
    String roleName
	String roleAlias
    // Relation
    ArtistMovie artistMovie
	static belongsTo = [artistMovie:ArtistMovie]
    static constraints = {
        id(max: 2147483647)
        roleName(size: 0..100)
		roleAlias(size:0..100,nullable:true)
        artistMovie()
    }
    String toString() {
        return "${id}" 
    }
}
