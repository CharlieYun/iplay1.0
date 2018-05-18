package com.iminer.biz
/**
 * The BasicArtistMovie entity.
 *
 * @author    
 *
 *
 */
class ArtistMovie {
    static mapping = {
         table 'basic_artist_movie'
         // version is set to false, because this isn't available by default for legacy databases
         version false
         id generator:'identity', column:'id'
         artist column:'artist_id'
         movie column:'movie_id'
    }
    Integer id
    Integer relation
    // Relation
    Artist artist
    // Relation
    Movie movie

	static hasMany = [roles:ArtistMovieRole]
	
	static belongsTo = [artist:Artist,movie:Movie]
	
    static constraints = {
        id(max: 2147483647)
        relation(max: 2147483647)
        artist()
        movie()
    }
    String toString() {
        return "${id}" 
    }
}
