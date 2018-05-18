package project

import org.apache.tools.zip.ZipEntry
import org.apache.tools.zip.ZipOutputStream
import org.joda.time.DateTime


class ProjectController {
	
	def projectService ;
	def projectRequirementService ;
	
	/**
	 * 获得项目动态信息
	 * @return
	 */
	def projectDynamic(){
//		System.out.println(session?.operationInfo?.id);
		// 查询项目的总数
		def myProjectCount = projectService.getProjectCount(null,null,0,params?.projectName);
		
		// 查询所有已经提交的项目
		def result = projectService.projectDynamic(params?.projectName,params?.max,params?.offset);
		
		result.each{
			// 循环每个项目，查找每个项目的项目成员个数
			def privyCount = projectService.getProjectPrivyCount(it.id);
			it.put("privyCount", privyCount)
			
			// 循环每个项目，查找每个项目所对应的数据需求
			def myParams = [:];
			myParams.put("projectId", it.id);
			def requirements = projectRequirementService.requirementExecute(500,0,myParams,session?.operationInfo?.id);
			it.put("requirements", requirements)
		}
		
		[result:result,myProjectCount:myProjectCount];
		
	}
	
	/**
	 * 获得项目执行信息
	 * @return
	 */
	def projectExecute(){
		
		// 查询项目的总数
		def myProjectCount = projectService.getProjectCount(null,null,null,params?.projectName);
		
		// 查询所有已经提交的项目
		def result = projectService.projectExecute(params?.projectName,params?.max,params?.offset);
		
		[result:result,myProjectCount:myProjectCount];
		
	}
	
	def projectTemplate(){
		// 查询项目模板的总数
		def myProjectCount = projectService.getMyProjectTemplateCount(session?.operationInfo?.id,params?.projectName);
		
		// 查询我创建的所有模板
		def result = projectService.projectTemplate(params?.projectName,params?.max,params?.offset,session?.operationInfo?.id);
		
		[result:result,myProjectCount:myProjectCount];
	}
	
	def addProject(){
		DateTime dt = new DateTime();
		def create_time = dt.toString("yyyy-MM-dd");
		[create_time:create_time]
	}
	
	def viewProject(){
		// 获得项目信息
		def result = projectService.getProjectInfo(params.project_id)
		// 获得项目分析师
		def analysts = projectService.getProjectAnalyst(params);
		// 获得项目利益关系人
		def privys = projectService.getProjectPrivy(params)
		
		[project:result,analysts:analysts,privys:privys]
	}
	
	def editProject(){
		// 获得项目信息
		def result = projectService.getProjectInfo(params.project_id)
		// 获得项目分析师
		def analysts = projectService.getProjectAnalyst(params);
		// 获得项目利益关系人
		def privys = projectService.getProjectPrivy(params)
		
		[project:result,analysts:analysts,privys:privys]
	}
	
	def editProjectAnalyst(){
		// 获得项目分析师
		def analysts = projectService.getProjectAnalyst(params);
		[project_id:params.project_id,analysts:analysts]
	}
	
	def shareProjectTemplate(){
		// 获得项目分析师
		//def analysts = projectService.getProjectTemplateAnalyst(params);
		[project_id:params.project_id]
	}
	
	def editProjectTemplate(){
		// 获得项目信息
		def result = projectService.getProjectTemplateInfo(params)
		// 获得项目分析师
		def analysts = projectService.getProjectTemplateAnalyst(params);
		// 获得项目利益关系人
		def privys = projectService.getProjectTemplatePrivy(params)
		[project:result,analysts:analysts,privys:privys]
	}
	
	
	def judgePersionHasRequirementPermission(){
		
		if(session.isTechincalSupportAccount){
			render "{\"errorCode\":\"1\"}";
			return ;
		}
		
		def projectInfo = projectService.getProjectInfo(params.project_id);
		if(session?.operationInfo?.id!=null && projectInfo.create_person_id == session?.operationInfo?.id){
			render "{\"errorCode\":\"1\"}";
		}
		else{
			boolean flag = projectService.hasPersonInProjectAnalyst(params,session?.operationInfo?.id);
			if(flag){
				render "{\"errorCode\":\"1\"}";
			}
			else{
				render "{\"errorCode\":\"0\",\"errorMsg\":\"您不是此项目的分析师或创建者.\"}";
			}
		}
	}
	
	/**
	 * 保存项目
	 * @return
	 */
	def saveProjectInfo(){
		
		def message = projectService.saveProjectInfo(request,params,session?.operationInfo?.id);
		
		render message ;
		
	}
	
	/**
	 * 更新项目
	 * @return
	 */
	def updateProjectInfo(){
		
		def message = projectService.updateProjectInfo(request,params,session?.operationInfo?.id);
		
		render message ;
		
	}
	
	
	/**
	 * 更新项目模板
	 * @return
	 */
	def updateProjectTemplateInfo(){
		def message = projectService.updateProjectTemplateInfo(request,params,session?.operationInfo?.id);
		
		render message ;
	}
	
	def updateProjectAnalystInfo(){
		def message = projectService.updateProjectAnalystInfo(request,params,session?.operationInfo?.id);
		
		render message ;
	}
	
	def shareProjectTemplateToAnthorAnalyst(){
		def message = projectService.shareProjectTemplateToAnthorAnalyst(request,params);
		render message ;
	}
	

	/**
	 * 删除项目
	 * @return
	 */
	def delProjectInfo(){
		def message = projectService.delProjectInfo(params,session?.operationInfo?.id);
		render message ;
	}
	
	/**
	 * 删除项目模板
	 * @return
	 */
	def delProjectTemplateInfo(){
		def message = projectService.delProjectTemplateInfo(params,session?.operationInfo?.id);
		render message ;
	}
	
	/**
	 * 更新项目状态
	 * @return
	 */
	def updateProjectStatus(){
		def message = projectService.updateProjectStatus(request,params,session?.operationInfo?.id);
		render message ;
	}
	
	/**
	 * 将项目保存为模板
	 * @return
	 */
	def saveForProjectTemplate(){
		def message = projectService.saveForProjectTemplate(request,params,session?.operationInfo?.id);
		render message ;
	}
	
	/**
	 * 引用模板保存为项目
	 * @return
	 */
	def saveForProjectWithTemplate(){
		def message = projectService.saveForProjectWithTemplate(request,params,session?.operationInfo?.id);
		render message ;
	}
	def ajaxGetProjectLeftMenu(){
		
	}
	
	
	def getZip={
		String fileName = new DateTime().toString("yyyy-MM-dd")+"项目文档.zip";
		response.setContentType("application/octet-stream");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Disposition", "inline;filename="+new String( fileName.getBytes("gb2312"), "ISO8859-1" ));
		try {
			OutputStream out = response.getOutputStream();
			ZipOutputStream zos = new ZipOutputStream(out);
			// 获得文件的路径
			def Urls = projectService.getProjectUrlsByIds(params);
			// 将每个文件压缩到zip文件中
			for(int i = 0 ; i < Urls.size();i++){
				String uri = grailsApplication.config.grails.app.nginx+Urls.get(i);
				URL url = new URL(uri);
				// 获得每个需要打包文件的文件名
				String zipEntryName = uri.substring(uri.indexOf("?attname=")+9)
				zos.putNextEntry(new ZipEntry(zipEntryName));
				
				// 读入需要下载的文件的内容，打包到zip文件
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				DataInputStream input = new DataInputStream(conn.getInputStream());
				byte[] buffer = new byte[8192];
				int len;
				while ((len = input.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}
				zos.setEncoding("UTF-8");
				zos.closeEntry();
				input.close();
			}
			// 关闭zip输出流
			zos.close();
			out.flush();
			out.close();
			return "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
