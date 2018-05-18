package com.iminer.mongodb;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.iminer.moncluster.ReportMongoDao;
import com.iminer.moncluster.partition.DefaultEntRecordCollectionPartition;
import com.iminer.moncluster.partition.DefaultMovieRecordCollectionPartition;
import com.iminer.moncluster.partition.DefaultStarRecordCollectionPartition;
import com.iminer.moncluster.partition.DefaultStarRecordCollectionPartition2;
import com.iminer.moncluster.partition.DefaultTelRecordCollectionPartition;
import com.iminer.moncluster.partition.IRecordCollectionPartition;
import com.iminer.moncluster.partition.TestDefaultEntRecordCollectionPartition;
import com.iminer.moncluster.partition.TestDefaultMovieRecordCollectionPartition;
import com.iminer.moncluster.partition.TestDefaultStarRecordCollectionPartition;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class MongoDbConnectConfig {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}


	private static Logger logger = Logger.getLogger(MongoDbConnectConfig.class);
	
	public final static String MYSQL_DB="mysql";
	public final static String MONGO_DB="";
	/**
	 * 获取dbcollection type 类型:明星
	 */
	public final static String MONGO_STAR_TYPE = "star";
	
	/**
	 * 获取dbcollection type 类型:明星
	 */
	public final static String MONGO_STAR_TYPE_2 = "star_2";
	
	/**
	 * 获取dbcollection type 类型:电影
	 */
	public final static String MONGO_MOVIE_TYPE = "movie";
	
	/**
	 * 电影mongodb链接ip
	 */
	public final static String MONGO_MOVIE_CONNECT_URL = "192.168.32.90";
	
	/**
	 * 电影mongodb链接端口
	 */
	public final static int MONGO_MOVIE_CONNECT_PORT = 40000;
	
	/**
	 * 电影mongodb链接用户名
	 */
	public final static String MONGO_MOVIE_CONNECT_USER = "cweibo";
	
	/**
	 * 电影mongodb链接密码
	 */
	public final static String MONGO_MOVIE_CONNECT_PASS = "cdm6db+1de*v";
	
	/**
	 * 获取dbcollection type 类型:综艺
	 */
	public final static String MONGO_ENT_TYPE = "entertainment";
	
	/**
	 * 获取mongo type 
	 */
	public final static String MONGO_TYPE_TEST = "test";
	
	/**
	 * 获取mongo type 
	 */
	public final static String MONGO_TYPE_DEFAULT = "default";

	/**
	 * 获取dbcollection type 类型:电视剧
	 */
	public final static String MONGO_TEL_TYPE = "tv";
	
	private static MongoClient mc = null;
    
	static{


	}
    /**
     * 根据id，type获取DBCollection
     * @param id 集群id（如明星/电影）
     * @param type
     * @return
     */
    public static DBCollection getMongoDb(int id, String type){
    	
		return getMongoDb(id, type, null);
	}
    
    /**
     * 根据id，type获取DBCollection
     * @param id 集群id（如明星/电影）
     * @param type
     * @return
     */
    public static DBCollection getMongoDb(int id, String type, String mongoType){
    	
    	if (StringUtils.isEmpty(type))
    		throw new IllegalArgumentException("Type can't be null!");
    	
		return getMongoDbClient(type, mongoType).getDBCollection(id);
	}
	
	/**
	 * 获得mongodb集群中明星分表策略
	 * @return
	 */
    private static ReportMongoDao getMongoDbClient(String type, String mongoType){
    	
		
		IRecordCollectionPartition dsrc = null;
		
		if (mongoType == MONGO_TYPE_TEST) {
		
			if (type.equalsIgnoreCase(MONGO_STAR_TYPE)) {
				dsrc = new TestDefaultStarRecordCollectionPartition();
			} else if (type.equalsIgnoreCase(MONGO_MOVIE_TYPE)) {
				dsrc = new TestDefaultMovieRecordCollectionPartition();
			} else if (type.equalsIgnoreCase(MONGO_ENT_TYPE)) {
				dsrc = new TestDefaultEntRecordCollectionPartition();
			} else {
				throw new IllegalArgumentException("Wrong type!");
			}
			
    	} else {
    		
    		if (type.equalsIgnoreCase(MONGO_STAR_TYPE)) {
				dsrc = new DefaultStarRecordCollectionPartition();
			}else if (type.equalsIgnoreCase(MONGO_STAR_TYPE_2)) {
				dsrc = new DefaultStarRecordCollectionPartition2();
			}else if (type.equalsIgnoreCase(MONGO_MOVIE_TYPE)) {
				dsrc = new DefaultMovieRecordCollectionPartition();
			} else if (type.equalsIgnoreCase(MONGO_ENT_TYPE)) {
				dsrc = new DefaultEntRecordCollectionPartition();
			} else if (type.equalsIgnoreCase("tv")){
				dsrc = new DefaultTelRecordCollectionPartition();
			} else {
				throw new IllegalArgumentException("Wrong type!");
			}
    	}
		try {
			mc = new MongoClient(MONGO_MOVIE_CONNECT_URL, MONGO_MOVIE_CONNECT_PORT);
		}catch(Exception e) {
			// TODO: handle exception
		}
		ReportMongoDao dao=ReportMongoDao.getInstance(dsrc,mc,MONGO_MOVIE_CONNECT_USER,MONGO_MOVIE_CONNECT_PASS);
		return dao;
	}

}
