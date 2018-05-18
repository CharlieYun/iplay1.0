package com.iminer.biz.system
/**
 * PrivilegeSystems：系统信息
 */
class PrivilegeSystems {
    static mapping = {
         table 'privilege_biz_systems'
         // version is set to false, because this isn't available by default for legacy databases
         version false
         // In case a sequence is needed, changed the identity generator for the following code:
//       id generator:'sequence', column:'id', params:[sequence:'privilege_biz_systems_sequence']
         id generator:'identity', column:'id'
    }
    Integer id
    String chineseName
    String englishName
    String version
    String introduction
    String systemCode

	static hasMany = [persons:PrivilegeSystemPerson,models:PrivilegeSystemModel]
	
	
    static constraints = {
        id(max: 2147483647)
        chineseName(size: 0..30)
        englishName(size: 0..20)
        version(size: 0..10)
        introduction()
        systemCode(size: 0..10)
    }
	
    String toString() {
        return "${id}" 
    }
}
