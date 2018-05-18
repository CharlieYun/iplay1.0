package com.iminer.utils;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Common {
	
	private Common(){}
	
	private static int requirementId ;
	
	public static int getRequirementId(){
		return requirementId;
	}
	
	public static void setRequirementId(int id){
		requirementId = id ;
	}
	
	private final static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(0,30,3,TimeUnit.SECONDS,new SynchronousQueue<Runnable>(),new ThreadPoolExecutor.DiscardPolicy());
	
	public static ThreadPoolExecutor getThreadPool(){
		return threadPool ;
	}

}
