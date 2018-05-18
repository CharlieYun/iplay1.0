package project

import groovy.sql.Sql

import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.util.CellRangeAddress
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.joda.time.DateTime


class ProjectService {

	def dataSource_iplay
	def dataSource_operation
	def grailsApplication
	def iFastDfs;
	
	
	
	/**
	 * 查询我参与的所有的项目
	 * @param userId
	 * @param 没有处于某种状态的记录
	 * @return
	 */
	def getProjectCount(def userId ,def inStatus,def notInStatus,def project_name){
		def dataSource = new Sql(dataSource_iplay)
		StringBuilder sql = new StringBuilder("select count(*) as myCount From basic_project where 1=1 ");
		if(userId!=null){
			sql.append(" and create_person_id = ${userId}");
		}
		if(inStatus!=null){
			sql.append(" and status in (${inStatus})");
		}
		if(notInStatus!=null){
			sql.append(" and status not in (${notInStatus})");
		}
		if(project_name!=null && (!"".equals(project_name))){
			sql.append(" and name like '%"+project_name+"%'");
		}
		def myCount = dataSource.firstRow(sql.toString())
		return myCount.myCount;
		
	}
	
	/**
	 * 查询我创建的所有项目模板
	 * @param userId
	 * @return
	 */
	def getMyProjectTemplateCount(def userId,def project_name){
		def dataSource = new Sql(dataSource_iplay)
		StringBuilder sql = new StringBuilder("select count(*) as myCount From basic_project_template where create_person_id = ${userId}");
		if(project_name!=null && (!"".equals(project_name))){
			sql.append(" and name like '%"+project_name+"%'");
		}
		def myCount = dataSource.firstRow(sql.toString())
		return myCount.myCount;
		
	}
	
	/**
	 * 查询项目动态
	 * @return
	 */
	def projectDynamic(def projectName,def max,def offset){
		
		max = max==null?10:max;
		offset = offset==null?0:offset;
		
		def dataSource = new Sql(dataSource_iplay)
		StringBuilder sql = new StringBuilder("select * From basic_project where status <> 0 " );
		if(projectName!=null && (!"".equals(projectName))){
			sql.append(" and name like '%"+projectName+"%'");
		}
		sql.append(" order by id desc limit "+offset+","+max);
		
		def result = dataSource.rows(sql.toString())
		
		return result ;
		
	}
	
	/**
	 * 获得项目的基本信息
	 * @param params
	 * @return
	 */
	def getProjectInfo(def projectId){
		def dataSource = new Sql(dataSource_iplay)
		
		def result = dataSource.firstRow("select * From basic_project where id = '${projectId}'")
		return result ;
	}
	
	/**
	 * 获得项目模板的基本信息
	 * @param params
	 * @return
	 */
	def getProjectTemplateInfo(params){
		def dataSource = new Sql(dataSource_iplay)
		
		def result = dataSource.firstRow("select * From basic_project_template where id = '${params.project_id}'")
		return result 
	}
	
	/**
	 * 获得项目的分析师
	 * @param params
	 * @return
	 */
	def getProjectAnalyst(params){
		def dataSource = new Sql(dataSource_iplay)
		StringBuilder sb = new StringBuilder();
		
		dataSource.rows("select * From basic_project_analyst where project_id = '${params.project_id}'").each {
			sb.append(it.person_id).append(",");
		}
		if(sb.toString().length()>0){
			return sb.toString().substring(0, sb.toString().length()-1);
		}
		return sb.toString();
	}
	
	/**
	 * 获得某人是否是某个项目的分析师
	 * @param params
	 * @return
	 */
	def hasPersonInProjectAnalyst(params,def personId){
		def dataSource = new Sql(dataSource_iplay)
		StringBuilder sb = new StringBuilder();
		
		def result = dataSource.firstRow("select * From basic_project_analyst where project_id = '${params.project_id}' and person_id = '${personId}'")
		if(result==null || result.id == null){
			return false ;
		}
		return true ;
	}
	
	/**
	 * 获得项目模板的分析师
	 * @param params
	 * @return
	 */
	def getProjectTemplateAnalyst(params){
		def dataSource = new Sql(dataSource_iplay)
		StringBuilder sb = new StringBuilder();
		
		dataSource.rows("select * From basic_project_analyst_template where project_id = '${params.project_id}'").each {
			sb.append(it.person_id).append(",");
		}
		if(sb.toString().length()>0){
			return sb.toString().substring(0, sb.toString().length()-1);
		}
		return sb.toString();
	}
	
	/**
	 * 获得项目的利益关系人
	 * @param params
	 * @return
	 */
	def getProjectPrivy(params){
		def dataSource = new Sql(dataSource_iplay)
		StringBuilder sb = new StringBuilder();
		
		def result = dataSource.rows("select * From basic_project_privy where project_id = '${params.project_id}'")
		return result ;
	}
	
	/**
	 * 获得项目模板的利益关系人
	 * @param params
	 * @return
	 */
	def getProjectTemplatePrivy(params){
		def dataSource = new Sql(dataSource_iplay)
		StringBuilder sb = new StringBuilder();
		
		def result = dataSource.rows("select * From basic_project_privy_template where project_id = '${params.project_id}'")
		return result ;
	}
	
	
	/**
	 * 查询项目执行
	 * @return
	 */
	def projectExecute(def projectName,def max,def offset){
		
		max = max==null?10:max;
		offset = offset==null?0:offset;
		
		def dataSource = new Sql(dataSource_iplay)
		
		StringBuilder sql = new StringBuilder("select * From basic_project where 1=1" );
		if(projectName!=null && (!"".equals(projectName))){
			sql.append(" and name like '%"+projectName+"%'");
		}
		sql.append(" order by id desc limit "+offset+","+max);
		
		def result = dataSource.rows(sql.toString())
		
		return result ;
		
	}
	
	def projectTemplate(def projectName,def max,def offset,def userId){
		max = max==null?10:max;
		offset = offset==null?0:offset;
		
		def dataSource = new Sql(dataSource_iplay)
		StringBuilder sql = new StringBuilder("select * From basic_project_template where create_person_id = ${userId}" );
		if(projectName!=null && (!"".equals(projectName))){
			sql.append(" and name like '%"+projectName+"%'");
		}
		sql.append(" order by id desc limit "+offset+","+max);
		def result = dataSource.rows(sql.toString())
		
		return result ;
	}
	
	/**
	 * 保存项目信息
	 * @param request
	 * @param params
	 * @param userId
	 * @return
	 */
	def saveProjectInfo(def request,def params,def userId){
		if(userId==null)return "请重新登陆";
		// 保持项目信息
		String insertSql = "INSERT INTO `iplay`.`basic_project` (`name`,`code`,`customer_name`, `manager`,`verifier`,`begin_time`,`end_time`,`create_person_id`,`create_time`,`desc`,`target`,`milestone`,`evaluation_criterion`,`constraint_condition`,`status`) VALUES ('${params.name}','${params.code}','${params.customer_name}','${params.manager}','${params.verifier}','${params.begin_time}','${params.end_time}','${userId}','${params.create_time}','${params.desc}','${params.target}','${params.milestone}','${params.evaluation_criterion}','${params.constraint_condition}','${params.status}')" ;
		def dataSource = new Sql(dataSource_iplay)
		def result = dataSource.executeInsert(insertSql);
		if("1".equals(params.status)){
			File file = new File(request.getRealPath("/")+"/excel/projectModel.xls");
			def projectInfo = this.getProjectInfo(result[0][0]);
			this.createProjectExcelFromModel(file,projectInfo);
		}
		// 保存分析师信息
		def analysts = params?.analysts ;
		if(analysts instanceof String[]){
			for (String analystId : params.analysts) {
				String projectId = result.get(0).get(0);
				insertSql = "INSERT INTO basic_project_analyst (`project_id`,`person_id`) VALUES ('${projectId}','${analystId}');" ;
				dataSource.executeInsert(insertSql);
			}
		}
		else{
			String projectId = result.get(0).get(0);
			insertSql = "INSERT INTO basic_project_analyst (`project_id`,`person_id`) VALUES ('${projectId}','${analysts}');" ;
			dataSource.executeInsert(insertSql);
		}
		// 保存项目利益关系人
		if(params.privy_person_id instanceof String){
			String projectId = result.get(0).get(0);
			insertSql = "INSERT INTO basic_project_privy (`project_id`,`person_id`,`project_role`) VALUES ('${projectId}','${params.privy_person_id}','${params.privy_person_role}');" ;
			System.out.println(insertSql);
			dataSource.executeInsert(insertSql);
		}
		else{
			def index = 0 ;
			params.privy_person_id.each {
				String projectId = result.get(0).get(0);
				String role = params.privy_person_role[index];
				insertSql = "INSERT INTO basic_project_privy (`project_id`,`person_id`,`project_role`) VALUES ('${projectId}','${it}','${role}');" ;
				System.out.println(insertSql);
				dataSource.executeInsert(insertSql);
				index++;
			}
		}
		return "成功!"
	}
	
	def updateProjectInfo(def request,def params,def userId){
		if(userId==null)return "请重新登陆";
		if(params?.id==null)return "更新失败" ;
		// 更新项目信息
		String insertSql = "UPDATE `iplay`.`basic_project` SET `name` = '${params.name}', `code` = '${params.code}', `customer_name` = '${params.customer_name}', `manager` = '${params.manager}', `verifier` = '${params.verifier}', `begin_time` = '${params.begin_time}', `end_time` = '${params.end_time}', `desc` = '${params.desc}', `target` = '${params.target}', `milestone` = '${params.milestone}', `evaluation_criterion` = '${params.evaluation_criterion}', `constraint_condition` = '${params.constraint_condition}', `status` = '${params.status}' WHERE `id` = '${params.id}' ";  
		def dataSource = new Sql(dataSource_iplay)
		def result = dataSource.executeUpdate(insertSql);
		if("1".equals(params.status)){
			File file = new File(request.getRealPath("/")+"/excel/projectModel.xls");
			def projectInfo = this.getProjectInfo(result[0][0]);
			this.createProjectExcelFromModel(file,projectInfo);
		}
		
		// 保存分析师信息
		// 删除之前的分析师
		String delSql = "delete from basic_project_analyst where project_id = '${params?.id}'" ;
		dataSource.executeUpdate(delSql);
		// 将新的增加进去
		for (String analystId : params.analysts) {
			insertSql = "INSERT INTO basic_project_analyst (`project_id`,`person_id`) VALUES ('${params?.id}','${analystId}');" ;
			dataSource.executeInsert(insertSql);
		}
		
		// 保存项目利益关系人
		// 删除之前的利益关系人
		delSql = "delete from basic_project_privy where project_id = '${params?.id}'" ;
		dataSource.executeUpdate(delSql);
		// 将新的增加进去
		if(params.privy_person_id instanceof String){
			insertSql = "INSERT INTO basic_project_privy (`project_id`,`person_id`,`project_role`) VALUES ('${params?.id}','${params.privy_person_id}','${params.privy_person_role}');" ;
			System.out.println(insertSql);
			dataSource.executeInsert(insertSql);
		}
		else{
			def index = 0 ;
			params.privy_person_id.each {
				String role = params.privy_person_role[index];
				insertSql = "INSERT INTO basic_project_privy (`project_id`,`person_id`,`project_role`) VALUES ('${params?.id}','${it}','${role}');" ;
				System.out.println(insertSql);
				dataSource.executeInsert(insertSql);
				index++;
			}
		}
		return "成功!"
	}
	
	def updateProjectTemplateInfo(def request,def params,def userId){
		if(userId==null)return "请重新登陆";
		if(params?.id==null)return "更新失败" ;
		// 更新项目信息
		String insertSql = "UPDATE `iplay`.`basic_project_template` SET `name` = '${params.name}', `code` = '${params.code}', `customer_name` = '${params.customer_name}', `manager` = '${params.manager}', `verifier` = '${params.verifier}', `begin_time` = '${params.begin_time}', `end_time` = '${params.end_time}', `desc` = '${params.desc}', `target` = '${params.target}', `milestone` = '${params.milestone}', `evaluation_criterion` = '${params.evaluation_criterion}', `constraint_condition` = '${params.constraint_condition}', `status` = '${params.status}' WHERE `id` = '${params.id}' ";
		def dataSource = new Sql(dataSource_iplay)
		def result = dataSource.executeUpdate(insertSql);
		
		// 保存分析师信息
		// 删除之前的分析师
		String delSql = "delete from basic_project_analyst_template where project_id = '${params?.id}'" ;
		dataSource.executeUpdate(delSql);
		// 将新的增加进去
		for (String analystId : params.analysts) {
			insertSql = "INSERT INTO basic_project_analyst_template (`project_id`,`person_id`) VALUES ('${params?.id}','${analystId}');" ;
			dataSource.executeInsert(insertSql);
		}
		
		// 保存项目利益关系人
		// 删除之前的利益关系人
		delSql = "delete from basic_project_privy_template where project_id = '${params?.id}'" ;
		dataSource.executeUpdate(delSql);
		// 将新的增加进去
		if(params.privy_person_id instanceof String){
			insertSql = "INSERT INTO basic_project_privy_template (`project_id`,`person_id`,`project_role`) VALUES ('${params?.id}','${params.privy_person_id}','${params.privy_person_role}');" ;
			System.out.println(insertSql);
			dataSource.executeInsert(insertSql);
		}
		else{
			def index = 0 ;
			params.privy_person_id.each {
				String role = params.privy_person_role[index];
				insertSql = "INSERT INTO basic_project_privy_template (`project_id`,`person_id`,`project_role`) VALUES ('${params?.id}','${it}','${role}');" ;
				System.out.println(insertSql);
				dataSource.executeInsert(insertSql);
				index++;
			}
		}
		return "成功!"
	}
	
	def updateProjectAnalystInfo(def request,def params,def userId){
		// 保存分析师信息
		def dataSource = new Sql(dataSource_iplay)
		// 删除之前的分析师
		String delSql = "delete from basic_project_analyst where project_id = '${params?.id}'" ;
		dataSource.executeUpdate(delSql);
		// 将新的增加进去
		def analysts = params?.analysts ;
		if(analysts instanceof String[]){
			for (String analystId : params.analysts) {
				String insertSql = "INSERT INTO basic_project_analyst (`project_id`,`person_id`) VALUES ('${params?.id}','${analystId}');" ;
				dataSource.executeInsert(insertSql);
			}
		}
		else{
			String insertSql = "INSERT INTO basic_project_analyst (`project_id`,`person_id`) VALUES ('${params?.id}','${analysts}');" ;
			dataSource.executeInsert(insertSql);
		}
		return "成功!"
	}
	
	
	/**
	 * 分享项目模板给其他分析师 
	 * @param request
	 * @param params
	 * @return
	 */
	def shareProjectTemplateToAnthorAnalyst(def request,def params){
		// 保存分析师信息
		def dataSource = new Sql(dataSource_iplay)
		def analysts = params?.analysts ;
		if(analysts instanceof String[]){
			for (String analystId : params.analysts) {
				String insertSql = "INSERT INTO `iplay`.`basic_project_template`(`name`,`code`,`customer_name`,`manager`,`verifier`,`begin_time`,`end_time`,`create_person_id`,`create_time`,`desc`,`target`,`milestone`,`evaluation_criterion`,`constraint_condition`,`down_url`,`status`)  SELECT `name`,`code`,`customer_name`,`manager`,`verifier`,`begin_time`,`end_time`, ${analysts} as `create_person_id`,`create_time`,`desc`,`target`,`milestone`,`evaluation_criterion`,`constraint_condition`,`down_url`,`status` FROM basic_project_template WHERE id = ${params?.id}" ;
				def result = dataSource.executeInsert(insertSql);
				String projectId = result.get(0).get(0);
				insertSql = "INSERT INTO basic_project_analyst_template (project_id,person_id) SELECT "+projectId+" AS 'project_id',person_id FROM basic_project_analyst_template where project_id = " + params?.id;
				dataSource.executeInsert(insertSql);
				// 将项目的参与者保存到模板中
				insertSql = "INSERT INTO basic_project_privy_template (project_id,person_id,project_role) SELECT "+projectId+" AS 'project_id',person_id,project_role FROM basic_project_privy_template where project_id = " + params?.id
				dataSource.executeInsert(insertSql);
			}
		}
		else{
			String insertSql = "INSERT INTO `iplay`.`basic_project_template`(`name`,`code`,`customer_name`,`manager`,`verifier`,`begin_time`,`end_time`,`create_person_id`,`create_time`,`desc`,`target`,`milestone`,`evaluation_criterion`,`constraint_condition`,`down_url`,`status`)  SELECT `name`,`code`,`customer_name`,`manager`,`verifier`,`begin_time`,`end_time`, ${analysts} as `create_person_id`,`create_time`,`desc`,`target`,`milestone`,`evaluation_criterion`,`constraint_condition`,`down_url`,`status` FROM basic_project_template WHERE id = ${params?.id}" ;
			def result = dataSource.executeInsert(insertSql);
			String projectId = result.get(0).get(0)
			insertSql = "INSERT INTO basic_project_analyst_template (project_id,person_id) SELECT "+projectId+" AS 'project_id',person_id FROM basic_project_analyst_template where project_id = " + params?.id;
			dataSource.executeInsert(insertSql);
			// 将项目的参与者保存到模板中
			insertSql = "INSERT INTO basic_project_privy_template (project_id,person_id,project_role) SELECT "+projectId+" AS 'project_id',person_id,project_role FROM basic_project_privy_template where project_id = " + params?.id
			dataSource.executeInsert(insertSql);
		}
		return "成功!"
		
	}
	
	def saveForProjectTemplate(def request,def params,def userId){
		// 获得项目信息
		def projectInfo = getProjectInfo(params.project_id)
		// 将项目信息保存到模板信息中。
		String insertSql = "INSERT INTO `iplay`.`basic_project_template` (`name`,`code`,`customer_name`, `manager`,`verifier`,`begin_time`,`end_time`,`create_person_id`,`create_time`,`desc`,`target`,`milestone`,`evaluation_criterion`,`constraint_condition`,`status`) VALUES ('${projectInfo.name}','${projectInfo.code}','${projectInfo.customer_name}','${projectInfo.manager}','${projectInfo.verifier}','${projectInfo.begin_time}','${projectInfo.end_time}','${userId}','"+new DateTime().toString("yyyy-MM-dd")+"','${projectInfo.desc}','${projectInfo.target}','${projectInfo.milestone}','${projectInfo.evaluation_criterion}','${projectInfo.constraint_condition}','${projectInfo.status}')" ;
		def dataSource = new Sql(dataSource_iplay)
		def result = dataSource.executeInsert(insertSql);
		String projectId = result.get(0).get(0);
		// 将项目的分析师保存到模板中
		insertSql = "INSERT INTO basic_project_analyst_template (project_id,person_id) SELECT "+projectId+" AS 'project_id',person_id FROM basic_project_analyst where project_id = " + params.project_id;
		dataSource.executeInsert(insertSql);
		// 将项目的参与者保存到模板中
		insertSql = "INSERT INTO basic_project_privy_template (project_id,person_id,project_role) SELECT "+projectId+" AS 'project_id',person_id,project_role FROM basic_project_privy where project_id = " + params.project_id
		dataSource.executeInsert(insertSql);
		
		return "成功"
		
	}
	
	def saveForProjectWithTemplate(def request,def params,def userId){
		// 获得项目信息
		def projectInfo = getProjectTemplateInfo(params)
		// 将项目信息保存到模板信息中。
		String insertSql = "INSERT INTO `iplay`.`basic_project` (`name`,`customer_name`, `manager`,`verifier`,`begin_time`,`end_time`,`create_person_id`,`create_time`,`desc`,`target`,`milestone`,`evaluation_criterion`,`constraint_condition`,`status`) VALUES ('${projectInfo.name}','${projectInfo.customer_name}','${projectInfo.manager}','${projectInfo.verifier}','${projectInfo.begin_time}','${projectInfo.end_time}','${userId}','${projectInfo.create_time}','${projectInfo.desc}','${projectInfo.target}','${projectInfo.milestone}','${projectInfo.evaluation_criterion}','${projectInfo.constraint_condition}','0')" ;
		def dataSource = new Sql(dataSource_iplay)
		def result = dataSource.executeInsert(insertSql);
		String projectId = result.get(0).get(0);
		// 将项目的分析师保存到模板中
		insertSql = "INSERT INTO basic_project_analyst (project_id,person_id) SELECT "+projectId+" AS 'project_id',person_id FROM basic_project_analyst_template where project_id = " + params.project_id;
		dataSource.executeInsert(insertSql);
		// 将项目的参与者保存到模板中
		insertSql = "INSERT INTO basic_project_privy (project_id,person_id,project_role) SELECT "+projectId+" AS 'project_id',person_id,project_role FROM basic_project_privy_template where project_id = " + params.project_id
		dataSource.executeInsert(insertSql);
		
		return "成功"
		
	}
	
	
	
	def delProjectInfo(def params,def userId){
		if(userId==null)return "权限不足";
		def dataSource = new Sql(dataSource_iplay)
		def result = dataSource.firstRow("select  * from basic_project where create_person_id = ${userId} and id = ${params.project_id}")
		if(result ==null || result.id==null)return "您不是此项目的创建者" ;
		def flag = dataSource.executeUpdate("delete from basic_project where create_person_id = ${userId} and id = ${params.project_id}")
		// 删除项目的分析师
		flag = dataSource.executeUpdate("delete from basic_project_analyst where project_id = ${params.project_id}")
		// 删除项目的项目利益关系人
		flag = dataSource.executeUpdate("delete from basic_project_privy where project_id = ${params.project_id}")
		return "成功";
	}
	
	def delProjectTemplateInfo(def params,def userId){
		if(userId==null)return "权限不足";
		def dataSource = new Sql(dataSource_iplay)
		def result = dataSource.firstRow("select  * from basic_project_template where create_person_id = ${userId} and id = ${params.project_id}")
		if(result ==null || result.id==null)return "您不是此模板的创建者" ;
		def flag = dataSource.executeUpdate("delete from basic_project_template where create_person_id = ${userId} and id = ${params.project_id}")
		// 删除项目模板的分析师
		flag = dataSource.executeUpdate("delete from basic_project_analyst_template where project_id = ${params.project_id}")
		// 删除项目模板的项目利益关系人
		flag = dataSource.executeUpdate("delete from basic_project_privy_template where project_id = ${params.project_id}")
		return "成功";
	}
	
	def updateProjectStatus(def request,def params,def userId){
		System.out.println(grailsApplication);
		if(userId==null)return "权限不足";
		def dataSource = new Sql(dataSource_iplay)
		def result = dataSource.firstRow("select  * from basic_project where create_person_id = ${userId} and id = ${params.project_id}")
		if(result ==null || result.id==null)return "您不是此项目的创建者" ;
		def flag = dataSource.executeUpdate("update basic_project set status = ${params.project_status} where create_person_id = ${userId} and id = ${params.project_id}")
		
		
		if("1".equals(params.project_status)){
			File file = new File(request.getRealPath("/")+"/excel/projectModel.xls");
			this.createProjectExcelFromModel(file,result);
		}
		
		return "成功";
	}
	
	def getProjectUrlsByIds(def params){
		def dataSource = new Sql(dataSource_iplay)
		def list = [] ;
		String sql = "select * From basic_project where id in (${params.project_ids})";
		dataSource.rows(sql).each {
			if(it.down_url!=null &&(! "".equals(it.down_url))){
				list.add(it.down_url);
			}
		}
		return list ;
	}
	
	def getProjectPrivyCount(def project_id){
		def dataSource = new Sql(dataSource_iplay)
		def myCount = dataSource.firstRow("select count(*) as myCount From basic_project_privy where project_id = ${project_id} ")
		return myCount.myCount;
	}
	
	/**
	 * 生成项目文档并上传到FastDFS中，并保存到数据库中
	 * @param file
	 */
	private void createProjectExcelFromModel(File file,def result){
		try {
			// 打开HSSFWorkbook
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file))
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFCellStyle cellStyle = wb.createCellStyle();
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 字体居中
			cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
			cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
			cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
			cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
			
			
			HSSFSheet sheet = wb.getSheetAt(0);
			// 设置项目的名称和编号
			HSSFRow row = sheet.getRow(2);
			row.getCell(2).setCellValue(result.name);
			row.getCell(5).setCellValue(result.code);
			// 设置项目的经理和审核人
			HSSFRow row3 = sheet.getRow(3);
			row3.getCell(2).setCellValue(result.manager);
			row3.getCell(5).setCellValue(result.verifier);
			// 设置项目周期和立项日期
			HSSFRow row4 = sheet.getRow(4);
			String begin_end_time = result.begin_time.toString() + "到" + result.end_time.toString();
			row4.getCell(2).setCellValue(begin_end_time);
			row4.getCell(5).setCellValue(result.create_time.toString());
			
			// 设置项目描述
			HSSFRow row7 = sheet.getRow(7);
			row7.getCell(0).setCellValue(result.desc);
			// 设置项目目标
			HSSFRow row9 = sheet.getRow(9);
			row9.getCell(0).setCellValue(result.target);
			// 设置项目里程碑计划
			HSSFRow row11 = sheet.getRow(11);
			row11.getCell(0).setCellValue(result.milestone);
			// 设置项目评价标准
			HSSFRow row13 = sheet.getRow(13);
			row13.getCell(0).setCellValue(result.evaluation_criterion);
			// 项目假定与约束条件
			HSSFRow row15 = sheet.getRow(15);
			row15.getCell(0).setCellValue(result.constraint_condition);
			
			// 项目利益关系人
			int index = 18 ;
			def dataSource = new Sql(dataSource_iplay)
			dataSource.rows("select * From basic_project_privy where project_id = ${result.id}").each {
				def dataSourceOperation = new Sql(dataSource_operation);
				def operationPerson = dataSourceOperation.firstRow("SELECT p.name,p.common_duties,o.name AS deptName FROM operation.`operation_person` p ,operation.`privilege_organization` o WHERE p.organization_id = o.id and p.id = ${it.person_id}")
				// 将最后4行向下移动一行
				sheet.shiftRows(index, index+4, 1)
				
				HSSFRow newRow = sheet.createRow(index);
				CellRangeAddress cra=new CellRangeAddress(index, index, 0, 1);
				CellRangeAddress cra1=new CellRangeAddress(index, index, 3, 4);
				sheet.addMergedRegion(cra);
				sheet.addMergedRegion(cra1);
				HSSFCell cell0 = newRow.createCell(0)
				cell0.setCellStyle(cellStyle);
				cell0.setCellValue(operationPerson.name);
				HSSFCell cell1 = newRow.createCell(2)
				cell1.setCellStyle(cellStyle)
				cell1.setCellValue(it.project_role);
				HSSFCell cell2 = newRow.createCell(3)
				cell2.setCellStyle(cellStyle)
				cell2.setCellValue(operationPerson.deptName);
				HSSFCell cell3 = newRow.createCell(5)
				cell3.setCellStyle(cellStyle)
				cell3.setCellValue(operationPerson.common_duties);
				index++;
			}
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			wb.write(out);
			String[] uriArr;
			try {
//				uriArr = iFastDfs.upload2group("G002",out.toByteArray(),"xls",null);
				uriArr = iFastDfs.upload(out.toByteArray(),"xls",null);
			} catch (Exception e) {
				System.out.println("FastDFSUtils报错了");
				e.printStackTrace();
				return null;
			}
			String down_url = (uriArr[0]+"/"+uriArr[1]+"?attname="+result.name+".xls");
			System.out.println("地址为："+down_url);
//			FileOutputStream fos = new FileOutputStream(new File("D:\\aaa.xls"));
//			wb.write(fos);
			fs.close();
			wb.close();
//			fos.close();
			out.close();
			// 将下载地址更新到数据库中
			int changeCount = dataSource.executeUpdate("update basic_project set down_url = ${down_url} where id = ${result.id}")
			System.out.println("提交生成excel并保存到数据库成功!");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
