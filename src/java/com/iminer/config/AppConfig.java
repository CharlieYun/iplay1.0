package com.iminer.config;

import java.util.HashMap;


public class AppConfig {

	
	public AppConfig(){
		init();
	}
	
	
	private  HashMap<String, String> rmilist;
	
	
	private void init(){
			if(rmilist==null){
				rmilist=new HashMap<String, String>();
			}
			rmilist.put("article", "rmi://118.192.65.90:10000/article");
			rmilist.put("relatearticle","rmi://118.192.65.90:10000/relatearticle");
			rmilist.put("relatearticlemultiver", "rmi://118.192.65.90:10000/RelateArticleMultiVer");
			rmilist.put("moviearticle","rmi://118.192.65.90:10000/relatearticleNew");
			rmilist.put("moviemultiver", "rmi://118.192.65.90:10000/RelateArticleMultiVerNew");
			rmilist.put("search", "rmi://118.192.65.90:10000/search");
			
			//口碑中间界面，查看mongo内容，的内外网地址
			rmilist.put("aspectUrl", "118.192.65.90");//127.0.0.1   118.192.65.87
			
	}
	public String getRmiurl(String rmitype){
		return rmilist.get(rmitype);
	}
	
	
	
	
	private static AppConfig appC=new AppConfig();

	public static String getRmiURL(String rmitype){
		return appC.getRmiurl(rmitype);
	}
	
	public static String STAR_ARTICLE_SERVICE="http://qc.iminer.com:28006/oa";
	
	//public static String STAR_ARTICLE_SERVICE="http://192.168.0.127:28006/oa";
	
}
