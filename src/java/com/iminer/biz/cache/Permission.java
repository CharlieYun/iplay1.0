package com.iminer.biz.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Permission：权限
 * 
 * @author fangjie fangjie@iminer.com
 * @version V1.0
 * @date 2015-5-18 下午2:23:42
 */
public class Permission {

	//用户系统权限缓存
	private static HashMap<String, Object> systemUser = new HashMap<String, Object>();
	//用户模块权限缓存
	private static List<HashMap<String, Object>> userModel = new ArrayList<HashMap<String, Object>>();
	//用户访问链接权限缓存
	private static List<HashMap<String, Object>> userRequestUrl = new ArrayList<HashMap<String, Object>>();

	/**
	 * initSystemUserCache:初始化系统用户权限信息cache
	 * @param users
	 */
	public static void initSystemUserCache(List<HashMap<String,Object>> users) {
		for(HashMap<String,Object> each : users){
			systemUser.put(each.get("id").toString(), each.get("username"));
		}
		System.out.println("------加载系统权限用户完成,共："+users.size()+"个------");
	}

	/**
	 * initUserModelCache:初始化用户模块权限信息cache
	 * @param modelInfo
	 */
	public static void initUserModelCache(List<HashMap<String,Object>> modelInfo) {
		userModel = modelInfo;
		System.out.println("------加载用户模块权限完成,共："+modelInfo.size()+"个------");
	}

	/**
	 * initUserRequestCache:初始化用户访问链接权限信息cache
	 * @param requestUrlInfo
	 */
	public static void initUserRequestCache(List<HashMap<String,Object>> requestUrlInfo) {
		userRequestUrl = requestUrlInfo;
		System.out.println("------加载用户访问链接权限完成,共："+requestUrlInfo.size()+"个------");
	}

	/**
	 * clearSystemUserCache:清除用户系统权限缓存
	 */
	public static void clearSystemUserCache(){
		systemUser.clear();
	}
	
	/**
	 * clearSystemUserCache:清除用户模块权限缓存
	 */
	public static void clearUserModelCache(){
		userModel.clear();
	}
	
	/**
	 * clearSystemUserCache:清除用户访问权限缓存
	 */
	public static void clearUserRequestCache(){
		userRequestUrl.clear();
	}
}
