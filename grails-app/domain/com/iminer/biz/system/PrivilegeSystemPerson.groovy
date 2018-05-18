package com.iminer.biz.system
/**
 * 
 * PrivilegeSystemPerson：系统用户关联信息
 *
 */
class PrivilegeSystemPerson {
    static mapping = {
         table 'privilege_system_person'
         // version is set to false, because this isn't available by default for legacy databases
         version false
         // In case a sequence is needed, changed the identity generator for the following code:
//       id generator:'sequence', column:'id', params:[sequence:'privilege_system_person_sequence']
         id generator:'identity', column:'id'
         model column:'model_id'
         system column:'system_id'
         person column:'person_id'
    }
    Integer id
	PrivilegeSystemModel model
    // Relation
    PrivilegeSystems system
//    // Relation
    PrivilegePerson person

    static constraints = {
        id(max: 2147483647)
		model()
        system()
        person()
    }
	
    String toString() {
        return "${id}" 
    }
}
