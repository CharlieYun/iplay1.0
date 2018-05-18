package projectRequirement

import java.text.DateFormat
import java.text.SimpleDateFormat

import org.apache.tools.zip.ZipEntry
import org.apache.tools.zip.ZipOutputStream


class ProjectRequirementController {
	def projectRequirementService;
	def projectService ;
	def dictionaryService ;
	def projectRequirementOverruleService
	def projectRequirementAgainService
	def iFastDfs;
	/**
	 * 跳转到需求列表页面
	 * @author HTYang
	 * @date 2016-4-15 上午11:19:16
	 * @return
	 */
	def projectRequirementList(){
		// 查询需求的总数
		def requirementCount = projectRequirementService.getRequirementCount(params)
		
		// 查询所有已经提交的项目需求 
		def result = projectRequirementService.requirementExecute(params?.max,params?.offset,params,session?.operationInfo?.id);
		// 获得项目信息
		def projectInfo = projectService.getProjectInfo(params.projectId);
		
		[result:result,requirementCount:requirementCount,projectInfo:projectInfo,projectId:params.projectId];
	}
	
	/**
	 * 跳转新增需求页面
	 * @author HTYang
	 * @date 2016-4-16 下午3:19:38
	 * @return
	 */
	def addProjectRequirement(){
		def objectTypeSelect=projectRequirementService.getObjectTypeList()
		[objectTypeSelect:objectTypeSelect,projectName:params.projectName,projectId:params.projectId]
	}
	
	/**
	 * 加载电视剧需求信息
	 * @author HTYang
	 * @date 2016-4-19 下午8:06:05
	 * @return
	 */
	def ajaxGetTeleplayRequirement(){
		// 获得需求信息
		def requirementInfo = projectRequirementService.getRequirementById(params?.projectRequirementId)
		def codeRequirementIds = projectRequirementService.getCodeRequirementIdsByRequirementId(params);
//		println requirementInfo
//		println codeRequirementIds
		[projectRequirement:requirementInfo,codeRequirementIds:codeRequirementIds,viewAction:params.viewAction]
	}
	
	def ajaxGetTeleplayRequirementTemplate(){
		// 获得需求信息
		def requirementInfo = projectRequirementService.getRequirementTemplateById(params?.projectRequirementId)
		def codeRequirementIds = projectRequirementService.getCodeRequirementTemplateIdsByRequirementId(params);
//		println requirementInfo
//		println codeRequirementIds
		render(view:"/projectRequirement/ajaxGetTeleplayRequirement",model:[projectRequirement:requirementInfo,codeRequirementIds:codeRequirementIds,viewAction:params.viewAction])
	}
	
	/**
	 * 打开上传页面
	 * @author CZZ
	 * @date 2016年4月19日15:51:02
	 * @return
	 */
	def uploadProjectRequirement(){
		
	}
	
	/**
	 * 上传文件处理方法
	 * @author CZZ
	 * @date 2016年4月19日15:51:02
	 * @return
	 */
	def doUploadProjectRequirement(){
		def requirementId = params.requirement_id ;
		def requirement = projectRequirementService.getRequirementById(requirementId) ;
		if(requirement==null || requirement.name==null || "".equals(requirement.name)){
			flash.message = '数据需求不存在'
			render(view:'uploadProjectRequirement',params:params)
			return ;
		}
		def name = requirement.name+".xls"
		def excel = request.getFile('requirementExcel')
		if(!excel.empty) {
			String suffix = "xls";
			String[] uriArr;
			try {
				uriArr = iFastDfs.upload2group("G002",excel.getBytes(),suffix,null);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			def downUrl = uriArr[0]+"/"+uriArr[1]+"?attname="+name;
			projectRequirementService.updateRequirementDownUrlById(requirementId,downUrl)
			flash.message = '上传成功!'
			redirect(controller:"projectRequirement",action:'uploadProjectRequirement',params:params);
		}
		else{
			flash.message = '上传文件不能为空!'
			redirect(controller:"projectRequirement",action:'uploadProjectRequirement',params:params);
		}
	}
	
	/**
	 * 保存项目需求信息
	 * @author HTYang
	 * @date 2016-4-20 上午9:48:51
	 * @return
	 */
	def saveProjectRequirementInfo(){
		//系统当前时间
		Date date=new Date()
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		String time=format.format(date);
		def message=""
//		print params.projectID+"projectID"
//		print params.queryIds+"queryIds"
//		print params.requirementName+"requirementName"
//		print params.begin_time+"begin_time"
//		print params.end_time+"end_time"
//		print params.objectType+"objectType"
//		print params.values+"values"
//		print params.valuesIds+"valuesIds"
////		print params.prStatue+"prStatue"
////		
////		
////		//市场份额
//		print params.valuesPortion
//		print params.valuesIdsPortion
////		print params.teleplayPortionTF
////		
////		//网络播放量
//		print params.valuesAmount
//		print params.valuesIdsAmount
////		print params.teleplayAmountTF
////		
////		//影响力
//		print params.valuesInfluence
//		print params.valuesIdsInfluence
////		print params.influenceTF
////		
////		//口碑
//		print params.valuesReputation
//		print params.valuesIdsReputation
////		print params.reputationTF
////		
////		//主创及表演评估
//		print params.valuesWritten
//		print params.valuesIdsWritten
////		print params.writtenTF
//		
//		print params.objtye
		
		def projectRequiementId
//		if(params.prStatue.equals("2")){		//提交状态-已完成
//			if(params.objtye.equals("5")){
//				print "电视剧"
//				projectRequiementId=projectRequirementService.saveRequiement(params,params.objtye,session?.operationInfo?.id,time)//保存电视剧需求信息
//				message=projectRequirementService.runRequirement(projectRequiementId)
//			}
//			if(params.objtye.equals("680")){
//				print "综艺"
//				projectRequiementId=projectRequirementService.saveRequiementVarietyShow(params,params.objtye,session?.operationInfo?.id,time)//保存综艺节目需求信息
//				message=projectRequirementService.runRequirement(projectRequiementId)
//			}
//			message="已提交！！！"
//			projectRequiementId=projectRequirementService.saveRequiement(params,params.objtye,session?.operationInfo?.id,time)//保存综艺节目需求信息
//			projectRequirementService.runRequirement(projectRequiementId)
//			render message
//		}else{									//暂存状态
			projectRequiementId=projectRequirementService.saveRequiement(params,params.objtye,session?.operationInfo?.id,time)//保存需求信息
			message="保存成功！！！"
			render message
//		}
	}
	
	def ajaxGetSpecialRequirementTemplate(){
		// 获得需求信息
		def requirementInfo = projectRequirementService.getRequirementTemplateById(params?.projectRequirementId)
		render(view:"/projectRequirement/ajaxGetSpecialRequirement",model:[projectRequirement:requirementInfo,viewAction:params.viewAction]);
	}
	def ajaxGetStarRequirementTemplate(){
		// 获得需求信息
		def requirementInfo = projectRequirementService.getRequirementTemplateById(params?.projectRequirementId)
		def codeRequirementIds = projectRequirementService.getCodeRequirementTemplateIdsByRequirementId(params);
		render(view:"/projectRequirement/ajaxGetStarRequirement",model:[projectRequirement:requirementInfo,codeRequirementIds:codeRequirementIds,viewAction:params.viewAction]);
	}
	
	def ajaxGetMovieRequirementTemplate(){
		// 获得需求信息
		def requirementInfo = projectRequirementService.getRequirementTemplateById(params?.projectRequirementId);
		def codeRequirementIds = projectRequirementService.getCodeRequirementTemplateIdsByRequirementId(params)
		render(view:"/projectRequirement/ajaxGetMovieRequirement",model:[projectRequirement:requirementInfo,codeRequirementIds:codeRequirementIds,viewAction:params.viewAction]);
	}
	
	def ajaxGetSpecialRequirement(){
		// 获得需求信息
		def requirementInfo = projectRequirementService.getRequirementById(params?.projectRequirementId)
		[projectRequirement:requirementInfo,viewAction:params.viewAction]
	}
	def ajaxGetStarRequirement(){
		// 获得需求信息
		def requirementInfo = projectRequirementService.getRequirementById(params?.projectRequirementId)
		def codeRequirementIds = projectRequirementService.getCodeRequirementIdsByRequirementId(params);
		[projectRequirement:requirementInfo,codeRequirementIds:codeRequirementIds,viewAction:params.viewAction]
	}
	
	def ajaxGetMovieRequirement(){
		// 获得需求信息
		def requirementInfo = projectRequirementService.getRequirementById(params?.projectRequirementId);
		def codeRequirementIds = projectRequirementService.getCodeRequirementIdsByRequirementId(params)
		[projectRequirement:requirementInfo,codeRequirementIds:codeRequirementIds,viewAction:params.viewAction]
	}
	
	def projectRequirementTemplate(){
		def result = projectRequirementService.getMyProjectRequirementTemplate(params?.projectRequirementName,params?.max,params?.offset,session?.operationInfo?.id);
		def myProjectRequirementTemplateCount =  projectRequirementService.getMyProjectRequirementTemplateCount(session?.operationInfo?.id,params?.projectRequirementName);
		[result:result,myProjectRequirementTemplateCount:myProjectRequirementTemplateCount]
	}
	def ajaxGetProjectRequirementLeftMenu(){
		
		// 获得当前登录用户是否为技术人员
		def flag = dictionaryService.thisValueIsBelongKey("technical_support_accounts", session?.operationInfo?.id);
		
		[flag:flag]
		
	}
	/**
	 * 将项目需求保存为模板
	 * @return
	 */
	def saveForProjectRequirementTemplate(){
		def message = projectRequirementService.saveForProjectRequirementTemplate(request,params,session?.operationInfo?.id);
		render message ;
	}
	/**
	 * 删除项目需求模板
	 * @return
	 */
	def delProjectRequirementTemplateInfo(){
		def message = projectRequirementService.delProjectRequirementTemplateInfo(params,session?.operationInfo?.id);
		render message ;
	}
	
	/**
	 * 删除项目需求
	 * @return
	 */
	def delProjectRequirementInfo(){
		def message = projectRequirementService.delProjectRequirementInfo(params,session?.operationInfo?.id);
		render message ;
	}
	
	/**
	 * 保存特殊需求
	 * @return
	 */
	def saveProjectRequirementInfoSpecial(){
		def message = projectRequirementService.saveProjectRequirementInfoSpecial(params,session?.operationInfo?.id);
		render message ;
	}
	
	def saveProjectRequirementInfoStar(){
		def message = projectRequirementService.saveProjectRequirementInfoStar(params,session?.operationInfo?.id);
		render message ;
	}
	
	
	/**
	 * 将数据模板保存为项目数据需求
	 * @return
	 */
	def saveForProjectRequirementWithTemplate(){
		def message = projectRequirementService.saveForProjectRequirementWithTemplate(params,session?.operationInfo?.id);
		render message ;
	}
	/**
	 * 查询所有需要审核的项目
	 * @return
	 */
	def projectRequirementForCheck(){
		def requirementCount = projectRequirementService.getProjectRequirementForCheckCount(params?.projectId);
		// 获得项目信息
		def projectInfo = projectService.getProjectInfo(params.projectId);
		def result = projectRequirementService.projectRequirementForCheck(params?.projectId);
		[result:result,projectName:projectInfo.name,requirementCount:requirementCount]
	}
	
	/**
	 * 查询所有正在进行中的项目需求
	 * @return
	 */
	def projectRequirementForUnderway(){
		
		def requirementCount = projectRequirementService.getProjectRequirementForUnderwayCount(params?.projectId);
		// 获得项目信息
		def projectInfo = projectService.getProjectInfo(params.projectId);
		def result = projectRequirementService.projectRequirementForUnderway(params?.projectId);
		[result:result,projectName:projectInfo.name,requirementCount:requirementCount]
	}
	
	/**
	 * 查询某个项目所有已经完成的项目需求
	 * @param projectId
	 * @return
	 */
	def projectRequirementForOver(){
		
		def requirementCount = projectRequirementService.getProjectRequirementForOverCount(params?.projectId);
		// 获得项目信息
		def projectInfo = projectService.getProjectInfo(params.projectId);
		def result = projectRequirementService.projectRequirementForOver(params?.projectId);
		[result:result,projectName:projectInfo.name,requirementCount:requirementCount]
	}
	
	/**
	 * 查询我创建的项目需求
	 * 
	 */
	def projectRequirementForMyCreate(){
		
		def requirementCount = projectRequirementService.getProjectRequirementForMyCreateCount(params?.projectId,session?.operationInfo?.id);
		// 获得项目信息
		def projectInfo = projectService.getProjectInfo(params.projectId);
		def result = projectRequirementService.projectRequirementForMyCreate(params?.projectId,session?.operationInfo?.id);
		[result:result,projectName:projectInfo.name,requirementCount:requirementCount]
	}
	
	/**
	 * 查看重跑的需求
	 * @return
	 */
	def projectRequirementForRepeatRun(){
		def requirementCount = projectRequirementService.getProjectRequirementForRepeatRunCount(params?.projectId);
		
		// 获得项目信息
		def projectInfo = projectService.getProjectInfo(params.projectId);
		def result = projectRequirementService.projectRequirementForRepeatRun(params?.projectId);
		[result:result,projectName:projectInfo.name,requirementCount:requirementCount]
	}
	
	/**
	 * 更新项目需求的状态
	 * @return
	 */
	def updateProjectRequirementStatus(){
		
		
		def projectRequirement = projectRequirementService.getRequirementById(params?.projectRequirementId);
		
		if(projectRequirement==null){
			render "没有此需求";
			return ;
		}
		def projectInfo = projectService.getProjectInfo(projectRequirement.project_id);
		if(projectInfo==null){
			render "此需求不属于任何项目";
			return ;
		}
		else if(projectInfo!=null && projectInfo.status == -1){
			render "此项目已经终止";
			return ;
		}
		else if(projectInfo!=null && projectInfo.status == 0){
			render "此项目还未提交";
			return ;
		}
		else if(projectInfo!=null && projectInfo.status == 2){
			render "此项目已完成";
			return ;
		}
		else if(projectInfo!=null && projectInfo.status == 3){
			render "此项目已暂停";
			return ;
		}
		
		def message = projectRequirementService.updateProjectRequirementStatus(params?.projectRequirementId,params.status);
//		if(projectRequirement.object_type == 4){
//			projectRequirementService.runRequirementByMovie(projectRequirement.id);
//		}
//		else if(projectRequirement.object_type == 5 || projectRequirement.object_type == 680){
//			projectRequirementService.runRequirement(projectRequirement.id);
//		}
//		else if(projectRequirement.object_type == 7){
//			projectRequirementService.runRequirementByStar(projectRequirement.id);
//		}
		render message ;
	}
	
	/**
	 * 查看项目需求
	 * @return
	 */
	def viewProjectRequirement(){
		def requirementInfo = projectRequirementService.getRequirementById(params?.projectRequirementId);
		[projectRequirement:requirementInfo]
	}
	
	def getZip={
		String fileName = "下载项目打包.zip";
		response.setContentType("application/octet-stream");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Disposition", "inline;filename="+new String( fileName.getBytes("gb2312"), "ISO8859-1" ));
		try {
			OutputStream out = response.getOutputStream();
			ZipOutputStream zos = new ZipOutputStream(out);
			// 获得文件的路径
			def Urls = projectRequirementService.getProjectRequirementUrlsByIds(params);
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
	
	def overruleProjectRequirement(){
		
	}
	
	def viewOverruleProjectRequirement(){
		def result = projectRequirementOverruleService.getProjectRequirementOverrule(params?.projectRequirementId);
		[overrule:result]
	}
	
	def doOverruleProjectRequirement(){
		def projectRequirement = projectRequirementService.getRequirementById(params?.requirement_id);
		
		if(projectRequirement==null){
			render "没有此需求";
			return ;
		}
		def projectInfo = projectService.getProjectInfo(projectRequirement.project_id);
		if(projectInfo==null){
			render "此需求不属于任何项目";
			return ;
		}
		else if(projectInfo!=null && projectInfo.status == -1){
			render "此项目已经终止";
			return ;
		}
		else if(projectInfo!=null && projectInfo.status == 0){
			render "此项目还未提交";
			return ;
		}
		else if(projectInfo!=null && projectInfo.status == 2){
			render "此项目已完成";
			return ;
		}
		else if(projectInfo!=null && projectInfo.status == 3){
			render "此项目已暂停";
			return ;
		}
		
		// 在此处增加插入驳回信息的语句
		def myParams = [:] ;
		myParams.put("overrule_mes", params.overrule_mes);
		myParams.put("requirement_id", params.requirement_id);
		myParams.put("project_id", projectInfo.id);
		boolean flag = projectRequirementOverruleService.saveProjectRequirementOverrule(myParams, session?.operationInfo?.id);
		if(flag){
			int status = projectRequirement.requirement_state == 1 ? 6 : 7 ;
			def message = projectRequirementService.updateProjectRequirementStatus(params?.requirement_id,status);
			render message ;
		}
		else{
			render "驳回失败" ;
		}
	}
	
	def againProjectRequirement(){
	}
	
	def viewAgainProjectRequirement(){
		def result = projectRequirementAgainService.getProjectRequirementAgain(params?.projectRequirementId);
		[again:result]
	}
	
	def doAgainProjectRequirement(){
		
		def projectRequirement = projectRequirementService.getRequirementById(params?.requirement_id);
		
		if(projectRequirement==null){
			render "没有此需求";
			return ;
		}
		def projectInfo = projectService.getProjectInfo(projectRequirement.project_id);
		if(projectInfo==null){
			render "此需求不属于任何项目";
			return ;
		}
		else if(projectInfo!=null && projectInfo.status == -1){
			render "此项目已经终止";
			return ;
		}
		else if(projectInfo!=null && projectInfo.status == 0){
			render "此项目还未提交";
			return ;
		}
		else if(projectInfo!=null && projectInfo.status == 2){
			render "此项目已完成";
			return ;
		}
		else if(projectInfo!=null && projectInfo.status == 3){
			render "此项目已暂停";
			return ;
		}
		
		def newProjectRequirementId = projectRequirementService.saveNewProjectRequirementByOldProjectRequirement(params?.requirement_id);
		// 在此处增加插入重跑信息的语句
		def myParams = [:] 
		myParams.put("old_requirement_id", params?.requirement_id);
		myParams.put("again_mes", params.again_mes);
		myParams.put("project_id", projectInfo.id);
		myParams.put("new_requirement_id", newProjectRequirementId);
		boolean flag = projectRequirementAgainService.saveProjectRequirementAgain(myParams, session?.operationInfo?.id);
		if(flag){
			render "操作成功" ;
		}
		else{
			render "操作失败" ;
		}
		
	}
	
	
	/**
	 * 加载综艺节目需求信息页面
	 * @author HTYang
	 * @date 2016-4-24 下午7:40:00
	 * @return
	 */
	def ajaxGetRequirementVarietyShow(){
		// 获得需求信息
		def requirementInfo = projectRequirementService.getRequirementById(params?.projectRequirementId)
		def codeRequirementIds = projectRequirementService.getCodeRequirementIdsByRequirementId(params);
//		println requirementInfo
//		println codeRequirementIds
		[projectRequirement:requirementInfo,codeRequirementIds:codeRequirementIds,viewAction:params.viewAction]
	}
	
	/**
	 * 编辑跳转页面
	 * @author HTYang
	 * @date 2016-5-4 上午10:42:29
	 * @return
	 */
	def editProjectRequirement(){
		def rMap = projectRequirementService.getRequirementNameById(params?.requirementId);
		[projectRequirement:rMap.get("result"),names:rMap.get("names")]
	}
	
	def editProjectRequirementTemplate(){
		def rMap = projectRequirementService.getRequirementTemplateNameById(params?.requirementId);
		[projectRequirement:rMap.get("result"),names:rMap.get("names")]
	}
	
	
	/**
	 * 更新需求信息
	 * @author HTYang
	 * @date 2016-5-5 下午1:54:05
	 * @return
	 */
	def editProjectRequirementInfo(){
		def message=""
		message=projectRequirementService.editRequiement(params,params.objtye,session?.operationInfo?.id)//保存需求信息
		render message
	}
	
	/**
	 * 更新需求模板信息
	 * @return
	 */
	def editProjectRequirementTemplateInfo(){
		def message=""
		message=projectRequirementService.editProjectRequirementTemplateInfo(params,params.objtye,session?.operationInfo?.id)//保存需求信息
		render message
	}
	
	/**
	 * 保存特殊需求
	 * @param params
	 * @param userId
	 * @return
	 */
	def updateProjectRequirementInfoSpecial(){
		def message = projectRequirementService.updateProjectRequirementInfoSpecial(params, session?.operationInfo?.id) ;
		render message;
	}
	/**
	 * 保存特殊需求模板
	 * @param params
	 * @param userId
	 * @return
	 */
	def updateProjectRequirementTemplateInfoSpecial(){
		def message = projectRequirementService.updateProjectRequirementTemplateInfoSpecial(params, session?.operationInfo?.id) ;
		render message;
	}
	
	/**
	 * 保存明星需求
	 * @param params
	 * @param userId
	 * @return
	 */
	def updateProjectRequirementInfoStar(){
		def message = projectRequirementService.updateProjectRequirementInfoStar(params, session?.operationInfo?.id) ;
		render message;
	}
	
	/**
	 * 保存明星需求模板
	 * @param params
	 * @param userId
	 * @return
	 */
	def updateProjectRequirementTemplateInfoStar(){
		def message = projectRequirementService.updateProjectRequirementTemplateInfoStar(params, session?.operationInfo?.id) ;
		render message;
	}
	
	def shareProjectRequirementTemplate(){
		[requirementId:params.requirementId]
	}
	
	
	def shareProjectRequirementTemplateToAnthorAnalyst(){
		def message = projectRequirementService.shareProjectRequirementTemplateToAnthorAnalyst(request,params);
		render message ;
	}
	
}
