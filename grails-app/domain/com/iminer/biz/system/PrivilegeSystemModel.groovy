package com.iminer.biz.system
/**
 * PrivilegeSystemModel：系统模块信息
 * @author   fangjie fangjie@iminer.com
 * @version  V1.0
 * @date 	 2015-4-22 下午2:56:06
 */
class PrivilegeSystemModel {
    static mapping = {
         table 'privilege_system_model'
         // version is set to false, because this isn't available by default for legacy databases
         version false
         // In case a sequence is needed, changed the identity generator for the following code:
//       id generator:'sequence', column:'id', params:[sequence:'privilege_system_model_sequence']
         id generator:'identity', column:'id'
    }
    Integer id
    String showName
    String requestUrl
    Integer orderIndex
    Integer isDefaultRequest
    String remark
	
	static belongsTo = [system:PrivilegeSystems]

    static constraints = {
        id(max: 2147483647)
        showName(size: 0..30)
        requestUrl(size: 0..100)
        orderIndex(nullable: true, max: 2147483647)
        isDefaultRequest(nullable: true, max: 2147483647)
        remark()
    }
	
    String toString() {
        return "${id}" 
    }
}
