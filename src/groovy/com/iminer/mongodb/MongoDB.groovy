package com.iminer.mongodb

import com.gmongo.GMongo
import com.mongodb.DBAddress
import com.mongodb.WriteConcern

class MongoDB {
	static DB_NAME = 'test'
	static TABLE_NAME='ireport_object_articles'
	static String unsername="entdb"
	static String password="dm6db+1de*v"
	def mongo, db
	static MongoDB mdb;
	private static dbPool=[:] 
	private final init(def dbname){
		mongo = new GMongo(new DBAddress('192.168.32.43', 27017, dbname))
		mongo.setWriteConcern(WriteConcern.SAFE)
	}
//	def getdb(){
//		db = mdb.mongo.getDB(DB_NAME)
//		db.authenticate(unsername,password.toCharArray());
//		return db
//	}
	
	def getdb(String unsername,String password,dbname){
		db = mdb.mongo.getDB(dbname)
		db.authenticate(unsername,password.toCharArray());
		return db
	}
	
	static synchronized getThisMongosDb(){
		if(!mdb){
			mdb=new MongoDB();
			mdb.init(DB_NAME);
		}
		return mdb.getdb(unsername,password,DB_NAME);

	}
	
	
	
	
	static synchronized getThisMongosDb(String name,String password,String dbname){
		if(!mdb){
			mdb=new MongoDB();
			mdb.init(dbname);
		}
		return mdb.getdb(name,password,dbname);

	}
	
	
	static synchronized getOtherMongosDb(String serviename,int port, String name,String password,String dbname){

		def mongo= dbPool.get(serviename+"|"+port+"|"+"dbname")
		def db
		if(!mongo){
			mongo = new GMongo(new DBAddress(serviename, port, dbname))
			mongo.setWriteConcern(WriteConcern.SAFE)
			db = mongo.getDB(dbname)
			def isconnect= db.authenticate(name,password.toCharArray());
			println isconnect
			if(isconnect){
				dbPool.put(serviename+"|"+port+"|"+"dbname",mongo)
			}else{
				throw new RuntimeException("db is not connect")
			}
		}else{
			db = mongo.getDB(dbname)
//			db.
		}
		return db;
	}
	
	
	
	
	
	
	
	
	
	
	
	

	
}
