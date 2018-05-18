package com.iminer.mongodb

class MongoDBFactory {

	public static getTestMongosDb(){
		return MongoDB.getThisMongosDb("entdb", "dm6db+1de*v", "test")
	}
	
	public static getIrepostArticleDB(){
		return MongoDB.getThisMongosDb("articleuser", "dm6db+1de*v", "ireport_article")
	}
	
	public static getReviewArticleDB(){
		return MongoDB.getOtherMongosDb("118.192.65.74",27017,"iminer", "iminer1234", "reviewTask")
	}
	
	public static getPraise(){
		return MongoDB.getOtherMongosDb("118.192.65.77",12702,"weibo", "dm6db+1de*v", "report")
//		return MongoDB.getOtherMongosDb("192.168.32.77",12702,"weibo", "dm6db+1de*v", "report")
	}
	
	public static getStarPraise(){
//		return MongoDB.getOtherMongosDb("118.192.65.77",12704,"weibo", "dm6db+1de*v", "report7")
		return MongoDB.getOtherMongosDb("118.192.65.77",12704,"weibo", "dm6db+1de*v", "report7")
	}
	
	public static getEntertainmentPraise(){
		//		return MongoDB.getOtherMongosDb("118.192.65.77",12704,"weibo", "dm6db+1de*v", "report7")
				return MongoDB.getOtherMongosDb("118.192.65.77",12704,"weibo", "dm6db+1de*v", "report7")
	
	}
	public static getEntityWordPraise(){
		//		return MongoDB.getOtherMongosDb("118.192.65.77",12704,"weibo", "dm6db+1de*v", "report7")
				return MongoDB.getOtherMongosDb("118.192.65.77",12704,"weibo", "dm6db+1de*v", "report7")
	
	}
			
	public static getLabelMongodb(){
//			return MongoDB.getOtherMongosDb("118.192.65.77",27017,"labeluser", "labeluser123", "object_label")
			return MongoDB.getOtherMongosDb("192.168.32.77",27017,"labeluser", "labeluser123", "object_label")
		}
	
	public static getStarRelationLabelMongodb(){
//		return MongoDB.getOtherMongosDb("118.192.65.77",27017,"labeluser", "labeluser123", "object_label")
		return MongoDB.getOtherMongosDb("192.168.32.77",27017,"labeluser", "labeluser123", "object_label")
	}
		public static getPraiseTest(String ip,int port,String username,String prossword,String dbname){
		return MongoDB.getOtherMongosDb(ip,port,username, prossword, dbname)

	}
	
}
