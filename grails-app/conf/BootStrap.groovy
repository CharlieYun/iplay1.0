import groovy.sql.Sql

import java.util.concurrent.ThreadPoolExecutor

import org.joda.time.DateTime
import projectRequirementService.ProjectRequirementService

import projectRequirementService.ProjectRequirementService

import com.iminer.utils.Common

class BootStrap {
	
	def projectRequirementService ;
	def dataSource_iplay ;

    def init = { servletContext ->
		
		ProjectRequirementService
		
		ThreadPoolExecutor threadPool = Common.getThreadPool();
		
		new Thread(new Runnable() {
			final ProjectRequirementService projectRequirementService1=projectRequirementService
			
			@Override
			public void run() {
				
				while(true){
					
					try {
						
						def dataSource = new Sql(dataSource_iplay)
						String sql = "SELECT * FROM basic_project_requirement_info WHERE object_type <> 10 AND (requirement_state = 2 OR requirement_state = 5) AND (is_execute <> 1 OR is_execute IS NULL) ORDER BY id" ;
						dataSource.rows(sql).each {requirement ->
							threadPool.execute(new Runnable(){
								@Override
								public void run(){
									if(requirement.object_type == 4){
										System.out.println(new DateTime().toString("yyyy-MM-dd hh:mm:ss")+"正在运行名称为"+requirement.name+"的这个需求！");
										long startTime=System.currentTimeMillis();   //获取开始时间
										projectRequirementService1.runRequirementByMovie(requirement.id);
										long endTime=System.currentTimeMillis(); //获取结束时间
										System.out.println(new DateTime().toString("yyyy-MM-dd hh:mm:ss")+"名称为"+requirement.name+"的这个需求运行结束！用时："+((endTime-startTime)/1000)+"秒");
									}
									else if(requirement.object_type == 5 || requirement.object_type == 680){
										System.out.println(new DateTime().toString("yyyy-MM-dd hh:mm:ss")+"正在运行名称为"+requirement.name+"的这个需求！");
										long startTime=System.currentTimeMillis();   //获取开始时间
										
										projectRequirementService1.runRequirement(requirement.id);
										long endTime=System.currentTimeMillis(); //获取结束时间
										System.out.println(new DateTime().toString("yyyy-MM-dd hh:mm:ss")+"名称为"+requirement.name+"的这个需求运行结束！用时："+((endTime-startTime)/1000)+"秒");
									}
									else if(requirement.object_type == 7){
										System.out.println(new DateTime().toString("yyyy-MM-dd hh:mm:ss")+"正在运行名称为"+requirement.name+"的这个需求！");
										long startTime=System.currentTimeMillis();   //获取开始时间
										projectRequirementService1.runRequirementByStar(requirement.id);
										long endTime=System.currentTimeMillis(); //获取结束时间
										System.out.println(new DateTime().toString("yyyy-MM-dd hh:mm:ss")+"名称为"+requirement.name+"的这个需求运行结束！用时："+((endTime-startTime)/1000)+"秒");
									}
								}
							})
						}
						 System.out.println(new DateTime().toString("hh:mm:ss")+"线程池目前运行多少个："+threadPool.getActiveCount());
						
						Thread.sleep(1000L*1*10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		
    }
    def destroy = {
    }
}
