package com.iminer.biz.system


class PrivilegePerson {
	/**
	 * PrivilegePerson：系统用户信息
	 * 
	 */
	static mapping = {
		table 'privilege_person'
		// version is set to false, because this isn't available by default for legacy databases
		version false
		// In case a sequence is needed, changed the identity generator for the following code:
//       id generator:'sequence', column:'id', params:[sequence:'privilege_person_sequence']
		id generator:'identity', column:'id'
   }
   Long id
   Long version
   Boolean accountExpired
   Boolean accountLocked
   Boolean enabled
   String password
   Boolean passwordExpired
   String username
   String comAddr
   String comTel
   String company
   String email
   String relName
   String tel
   Boolean accountConfirmed
   Date beginDate
   Date endDate
   Integer organizationId
   Date createTime
   
   static belongsTo = [PrivilegeSystems]
   static hasMany = [systems:PrivilegeSystemPerson]

   static constraints = {
	   id(max: 9223372036854775807L)
	   version(nullable: true, max: 9223372036854775807L)
	   accountExpired(nullable: true)
	   accountLocked(nullable: true)
	   enabled(nullable: true)
	   password(size: 1..255, blank: false)
	   passwordExpired(nullable: true)
	   username(size: 0..255)
	   comAddr(size: 0..255)
	   comTel(size: 0..255)
	   company(size: 0..255)
	   email(size: 1..255, blank: false)
	   relName(size: 0..255)
	   tel(size: 1..255, blank: false)
	   accountConfirmed(nullable: true)
	   beginDate(nullable: true)
	   endDate(nullable: true)
	   organizationId(nullable: true, max: 2147483647)
	   createTime(nullable: true)
   }
   String toString() {
	   return "${id}"
   }
}
