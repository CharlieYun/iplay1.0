package fansbook

import groovy.sql.Sql

import java.util.concurrent.ThreadPoolExecutor

import org.joda.time.DateTime

import com.iminer.utils.Common

class MyJob {
	
//	def projectRequirementService ;
//	def dataSource_iplay ;
//	
//    static triggers = {
//      simple repeatInterval: 1000l*5*10 // 每个5分钟执行一次
//    }
//	
////	 执行项目需求，暂时每5分钟执行一次，一次执行一个任务，待以后可以增加线程来执行多个
//    def execute1() {
//		
//		def dataSource = new Sql(dataSource_iplay) 
//		
//		String sql = "select * from basic_project_requirement_info where object_type <> 10 and (requirement_state = 2 or requirement_state = 5) order by id" ;
//		def requirement = dataSource.firstRow(sql);
//		if(requirement==null){
//			System.out.println(new DateTime().toString("yyyy-MM-dd hh:mm:ss")+"定时器未找到可执行的项目需求");
//			return ;
//		}
//		else if(Common.getRequirementId() == requirement.id){
//			System.out.println(new DateTime().toString("yyyy-MM-dd hh:mm:ss")+"定时器找到可执行的项目需求,但与上次执行的需求相同，已忽略本次执行");
//			return ;
//		}
//		else{
//			System.out.println(new DateTime().toString("yyyy-MM-dd hh:mm:ss")+"定时器找到可执行的项目需求,需求名称为："+requirement.name);
//			Common.setRequirementId(requirement.id)
//		}
//		if(requirement.object_type == 4){
//			projectRequirementService.runRequirementByMovie(requirement.id);
//		}
//		else if(requirement.object_type == 5 || requirement.object_type == 680){
//			projectRequirementService.runVarietyShowRequirement(requirement.id);
//		}
//		else if(requirement.object_type == 7){
//			projectRequirementService.runRequirementByStar(requirement.id);
//		}
//    }
//	
//	def execute() {
//		ThreadPoolExecutor threadPool = Common.getThreadPool();
////		if(threadPool.getActiveCount()>0)return ; // 如果当前有运行的任务，则bu
//		def dataSource = new Sql(dataSource_iplay)
//		String sql = "SELECT * FROM basic_project_requirement_info WHERE object_type <> 10 AND (requirement_state = 2 OR requirement_state = 5) AND (is_execute <> 1 OR is_execute IS NULL) ORDER BY id" ;
//		dataSource.rows(sql).each {requirement ->
//			threadPool.execute(new Runnable(){
//				@Override
//				public void run(){
//					if(requirement.object_type == 4){
//						projectRequirementService.runRequirementByMovie(requirement.id);
//					}
//					else if(requirement.object_type == 5 || requirement.object_type == 680){
//						projectRequirementService.runRequirement(requirement.id);
//					}
//					else if(requirement.object_type == 7){
//						projectRequirementService.runRequirementByStar(requirement.id);
//					}
//				}
//			})
//		}
//		System.out.println(new DateTime().toString("hh:mm:ss")+"线程池目前运行多少个："+threadPool.getActiveCount());
//	}
}
