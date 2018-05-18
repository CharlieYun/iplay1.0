package com.iminer.biz.cache;

import java.util.HashMap;

/**
 * SystemBlackAndWhiteList：系统黑白名单
 * @author HTYang
 * @date 2015-12-25 下午1:24:21
 */
public class SystemBlackAndWhiteList {

	// 单例模式
	private static SystemBlackAndWhiteList backWhiteList = null;
	
	// 全局静态变量，key：白名单(2)、黑名单(1),value:IP数组
	private static HashMap<String, HashMap<String, String[]>> blackWhiteMap = new HashMap<String, HashMap<String, String[]>>();

	
	public static HashMap<String, HashMap<String, String[]>> getBlackWhiteMap() {
		return blackWhiteMap;
	}

	public static void setBlackWhiteMap(HashMap<String, HashMap<String, String[]>> blackWhiteMap) {
		SystemBlackAndWhiteList.blackWhiteMap = blackWhiteMap;
	}



	/**
	 * getInstance:获取对象
	 * 
	 * @return
	 */
	public static synchronized SystemBlackAndWhiteList getInstance() {
		if(backWhiteList == null) {
			backWhiteList = new SystemBlackAndWhiteList();
		}
		return backWhiteList;
	}
}
