package com.iminer.mongodb

import com.iminer.config.AppConfig
import com.iminer.moncluster.ReportMongoDao
import com.iminer.moncluster.partition.DefaultEntRecordCollectionPartition
import com.iminer.moncluster.partition.DefaultMovieRecordCollectionPartition
import com.iminer.moncluster.partition.DefaultStarRecordCollectionPartition
import com.iminer.moncluster.partition.DefaultTelRecordCollectionPartition
import com.mongodb.MongoClient

class getConnectionColony {
	private static MongoClient mc;
	static {
		def url=AppConfig.getRmiURL("aspectUrl")
		mc=new MongoClient(url, 40000);
	}
	def getConnectionByType(def type){
		
		def dsrc=new DefaultMovieRecordCollectionPartition()
		if (type == "star") {
			dsrc = new DefaultStarRecordCollectionPartition();
		} else if (type=="movie") {
			dsrc = new DefaultMovieRecordCollectionPartition();
		}else if (type=="tv"){
			dsrc = new DefaultTelRecordCollectionPartition();
		}else if (type=="ent"){
			dsrc = new DefaultEntRecordCollectionPartition();
//			dsrc = new TestDefaultEntRecordCollectionPartition();
		} else {
			throw new IllegalArgumentException("Wrong type!");
		}
		ReportMongoDao remo=ReportMongoDao.getInstance(dsrc,mc,"cweibo","cdm6db+1de*v");
		return remo
	}
	
}
