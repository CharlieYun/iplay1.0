package projectRequirementService

import groovy.sql.Sql

import java.text.SimpleDateFormat

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.joda.time.DateTime
import org.json.simple.JSONArray
import org.json.simple.JSONValue

import com.iminer.mongodb.MongoDbConnectConfig
import com.iminer.utils.DateTools
import com.iminer.utils.SendEmailUtil
import com.mongodb.BasicDBObject


class ProjectRequirementService {
	def dataSource_iplay
	def dataSource_operation ;
	def dataSource_mycat_reputation
	def dataSource
	def dataSource_ireport
	def dataSource_cobar_reputation
	def dataSource_crawl;
	def projectService ;
	def grailsApplication ;
	def iFastDfs
	
	
	/**
	 * 查询项目中需求总数
	 * @author HTYang
	 * @date 2016-4-15 下午2:53:55
	 * @return
	 */
	def getRequirementCount(params){
		def dataSource = new Sql(dataSource_iplay)
		def myCount = dataSource.firstRow("select count(*) as myCount From basic_project_requirement_info where project_id = '${params.projectId}' ")
		return myCount.myCount;
	}
	
	/**
	 * 查询项目中所有需求信息
	 * @author HTYang
	 * @date 2016-4-15 下午2:57:37
	 * @param max
	 * @param offset
	 * @return
	 */
	def requirementExecute(def max,def offset,params,def personId){
		max = max==null?10:max
		offset = offset==null?0:offset;
		def dataSource = new Sql(dataSource_iplay);
		String sql = "select a.`name` rName,a.object_type,a.operation_date,b.`name` uName,submit_time,CASE a.requirement_state when 0 then '未提交' when 1 then '待审核' when 2 then '进行中' when 3 then '已完成' when 4 then '重跑待审核' when 5 then '重跑进行中' when 6 then '已驳回' when 7 then '重跑已驳回' end as state,a.requirement_state,a.operation_person,a.id rId from basic_project_requirement_info a,operation.operation_person b where a.operation_person=b.id and project_id = '${params.projectId}' and (requirement_state <> 0 or (requirement_state = 0 and operation_person = ${personId})) order by a.id desc limit "+offset+","+max;
		def result = dataSource.rows(sql);
		return result;
		
	}
	
	/**
	 * 得到对象类型列表信息
	 * @author HTYang
	 * @date 2016-4-16 下午5:10:02
	 * @return
	 */
	def getObjectTypeList(){
		def dataSource = new Sql(dataSource_iplay)
		def result = dataSource.rows("select id,object_name from code_localtion_object_type")
		return result;
	}
	
	
	/**
	 * 根据数据需求ID获得该数据需求模板
	 * @author CZZ
	 * @date 2016年4月19日15:21:38
	 * @return
	 */
	def getRequirementById(def requirementId){
		if(requirementId==null)return null;
		def dataSource = new Sql(dataSource_iplay)
		def result = dataSource.firstRow("select * From basic_project_requirement_info where id = '${requirementId}' ")
		return result ;
	}
	
	/**
	 * 更新项目需求的文档
	 * @param requirementId
	 * @param downUrl
	 * @return
	 */
	def updateRequirementDownUrlById(def requirementId ,def downUrl){
		def dataSource = new Sql(dataSource_iplay)
		int changeCount = dataSource.executeUpdate("update basic_project_requirement_info set down_url = ${downUrl} where id = ${requirementId}")
		if(changeCount>0){
			System.out.println("上传excel成功!");
			return true ;
		}
		else{
			return false ;
		}
	}
	
	/**
	 * 保存一份新的需求，并将原来的需求设置为不可查的状态
	 * @param requirementId
	 * @return
	 */
	def saveNewProjectRequirementByOldProjectRequirement(def requirementId){
		def dataSource = new Sql(dataSource_iplay)
		
		def updateSql = " update basic_project_requirement_info set requirement_state = -1 where id = " +  requirementId
		dataSource.executeUpdate(updateSql);
		
		def insertSql = "INSERT INTO basic_project_requirement_info (name,object_type,object_ids,begin_date,end_date,requirement_state,operation_person,operation_date,down_url,project_id,special_requirement_mes,cinemaIds,submit_time) SELECT name,object_type,object_ids,begin_date,end_date,4 as 'requirement_state',operation_person,operation_date,down_url,project_id,special_requirement_mes,cinemaIds,submit_time FROM basic_project_requirement_info where id = " + requirementId;
		def result = dataSource.executeInsert(insertSql);
		def newProjectRequirementId = result.get(0).get(0);
		
		// 将需求具体的需求项放到需求项模板中
		insertSql = "INSERT INTO basic_requiremen_codeRequirement_relation (requeirment_id,codeRequeirment_id) SELECT "+newProjectRequirementId+" as 'requeirment_id',codeRequeirment_id FROM basic_requiremen_codeRequirement_relation where requeirment_id = " +requirementId;
		dataSource.executeInsert(insertSql);
		
		return newProjectRequirementId ;
		
	}
	
	/**
	 * 获得登陆者项目需求模板列表
	 * @param projectName
	 * @param max
	 * @param offset
	 * @param userId
	 * @return
	 */
	def getMyProjectRequirementTemplate(def projectRequirementName,def max,def offset,def userId){
		max = max==null?10:max;
		offset = offset==null?0:offset;
		
		def dataSource = new Sql(dataSource_iplay)
		StringBuilder sql = new StringBuilder("select * From basic_project_requirement_templet_info where operation_person = ${userId}" );
		if(projectRequirementName!=null && (!"".equals(projectRequirementName))){
			sql.append(" and name like '%"+projectRequirementName+"%'");
		}
		sql.append(" order by id desc limit "+offset+","+max);
		def result = dataSource.rows(sql.toString())
		
		return result ;
	}
	
	/**
	 * 获得登陆者创建的项目需求模板数量
	 * @param userId
	 * @param project_name
	 * @return
	 */
	def getMyProjectRequirementTemplateCount(def userId,def projectRequirementName){
		def dataSource = new Sql(dataSource_iplay)
		StringBuilder sql = new StringBuilder("select count(*) as myCount From basic_project_requirement_templet_info where operation_person = ${userId}");
		if(projectRequirementName!=null && (!"".equals(projectRequirementName))){
			sql.append(" and name like '%"+projectRequirementName+"%'");
		}
		def myCount = dataSource.firstRow(sql.toString())
		return myCount.myCount;
	}
	
	/**
	 * 根据数据需求ID获得该数据需求模板
	 * @author CZZ
	 * @date 2016年4月20日17:54:45
	 * @return
	 */
	def getRequirementTemplateById(def requirementId){
		def dataSource = new Sql(dataSource_iplay)
		def result = dataSource.firstRow("select * From basic_project_requirement_templet_info where id = '${requirementId}' ")
		return result ;
	}
	
	/**
	 * 保存项目需求模板
	 * @param request
	 * @param params
	 * @param userId
	 * @return
	 */
	def saveForProjectRequirementTemplate(def request,def params,def userId){
		// 获得项目信息
		def projectRequirementInfo = getRequirementById(params.project_requirement_id)
		// 将项目信息保存到模板信息中。
		String insertSql = "INSERT INTO `iplay`.`basic_project_requirement_templet_info` (`name`,`object_type`,`object_ids`,`begin_date`,`end_date`,`operation_person`,`operation_date`, `special_requirement_mes`) VALUES ('${projectRequirementInfo.name}','${projectRequirementInfo.object_type}','${projectRequirementInfo.object_ids}', '${projectRequirementInfo.begin_date}','${projectRequirementInfo.end_date}', '${userId}','"+new DateTime().toString("yyyy-MM-dd")+"','${projectRequirementInfo.special_requirement_mes}')" ;
		def dataSource = new Sql(dataSource_iplay)
		def result = dataSource.executeInsert(insertSql)
		String projectRequirementTemplateId = result.get(0).get(0);
		// 将需求具体的需求项放到需求项模板中
		insertSql = "INSERT INTO basic_requiremen_templet_codeRequirement_relation (requeirment_templet_id,codeRequeirment_id) SELECT "+projectRequirementTemplateId+" AS 'requeirment_templet_id',codeRequeirment_id FROM basic_requiremen_codeRequirement_relation where requeirment_id = " + params.project_requirement_id;
		dataSource.executeInsert(insertSql);
		
		return "成功"
		
	}
	
	/**
	 * 删除项目需求模板
	 * @param params
	 * @param userId
	 * @return
	 */
	def delProjectRequirementTemplateInfo(def params ,def userId){
		if(userId==null)return "权限不足";
		def dataSource = new Sql(dataSource_iplay)
		def result = dataSource.firstRow("select  * from basic_project_requirement_templet_info where operation_person = ${userId} and id = ${params.project_requirement_id}")
		if(result ==null || result.id==null)return "您不是此模板的创建者" ;
		def flag = dataSource.executeUpdate("delete from basic_project_requirement_templet_info where operation_person = ${userId} and id = ${params.project_requirement_id}")
		// 删除项目需求的需求项
		flag = dataSource.executeUpdate("delete from basic_requiremen_templet_codeRequirement_relation where requeirment_templet_id = ${params.project_requirement_id}")
		return "成功";
	}
	
	/**
	 * 删除项目需求模板
	 * @param params
	 * @param userId
	 * @return
	 */
	def delProjectRequirementInfo(def params ,def userId){
		if(userId==null)return "权限不足";
		def dataSource = new Sql(dataSource_iplay)
		def result = dataSource.firstRow("select  * from basic_project_requirement_info where operation_person = ${userId} and id = ${params.project_requirement_id}")
		if(result ==null || result.id==null)return "您不是此需求的创建者" ;
		def flag = dataSource.executeUpdate("delete from basic_project_requirement_info where operation_person = ${userId} and id = ${params.project_requirement_id}")
		// 删除项目需求的需求项
		flag = dataSource.executeUpdate("delete from basic_requiremen_codeRequirement_relation where requeirment_id = ${params.project_requirement_id}")
		return "成功";
	}
	
	/**
	 * 保存特殊需求
	 * @param params
	 * @param userId
	 * @return
	 */
	def saveProjectRequirementInfoSpecial(def params,def userId){
		
		String insertSql = "INSERT INTO `iplay`.`basic_project_requirement_info` (`name`,`project_id`,`object_type`,`begin_date`,`end_date`,`requirement_state`,`operation_person`,`operation_date`, `special_requirement_mes`) VALUES ('${params.requirementName}','${params.project_id}','${params.objectType}', '${params.begin_time}','${params.end_time}', '${params.requirement_state}', '${userId}','"+new DateTime().toString("yyyy-MM-dd")+"','${params.special_requirement_mes}')" ;
		if("1".equals(params.requirement_state)){
			insertSql = "INSERT INTO `iplay`.`basic_project_requirement_info` (`name`,`project_id`,`object_type`,`begin_date`,`end_date`,`requirement_state`,`operation_person`,`operation_date`, `special_requirement_mes`,`submit_time`) VALUES ('${params.requirementName}','${params.project_id}','${params.objectType}', '${params.begin_time}','${params.end_time}', '${params.requirement_state}', '${userId}','"+new DateTime().toString("yyyy-MM-dd")+"','${params.special_requirement_mes}',"+System.currentTimeMillis()+")" ;
		}
		def dataSource = new Sql(dataSource_iplay)
		def result = dataSource.executeInsert(insertSql);
		
		return "成功";
	}
	
	/**
	 * 保存明星需求
	 * @param params
	 * @param userId
	 * @return
	 */
	def saveProjectRequirementInfoStar(def params,def userId){
		
		String submit_time = "NULL" ; 
		if("2".equals(params.requirement_state)){
			submit_time = System.currentTimeMillis();
		}
		
		String insertSql = "INSERT INTO `iplay`.`basic_project_requirement_info` (`name`,`project_id`,`object_type`,`object_ids`,`begin_date`,`end_date`,`requirement_state`,`operation_person`,`operation_date`,`submit_time`) VALUES ('${params.requirementName}','${params.project_id}','${params.objectType}','${params.objectIds}', '${params.begin_time}','${params.end_time}', '${params.requirement_state}', '${userId}','"+new DateTime().toString("yyyy-MM-dd")+"',"+submit_time+")" ;
		// 判断是否输入了影院信息
		if(params.cinemaIds!=null && (!"".equals(params.cinemaIds))){
			insertSql = "INSERT INTO `iplay`.`basic_project_requirement_info` (`name`,`project_id`,`object_type`,`object_ids`,`begin_date`,`end_date`,`requirement_state`,`operation_person`,`operation_date`,`cinemaIds`,`submit_time`) VALUES ('${params.requirementName}','${params.project_id}','${params.objectType}','${params.objectIds}', '${params.begin_time}','${params.end_time}', '0', '${userId}','"+new DateTime().toString("yyyy-MM-dd")+"','${params.cinemaIds}',"+submit_time+")" ;
		}
		
		
		def dataSource = new Sql(dataSource_iplay);
		def result = dataSource.executeInsert(insertSql);
		// 获得项目需求ID
		String projectRequiementId = result.get(0).get(0);
		def ids = params?.ids ;
		if(ids instanceof String[]){
			ids.each {
				insertSql = "INSERT INTO basic_requiremen_codeRequirement_relation (`requeirment_id`,`codeRequeirment_id`) VALUES ('${projectRequiementId}','${it}');" ;
				dataSource.executeInsert(insertSql);
			}
		}
		else{
			insertSql = "INSERT INTO basic_requiremen_codeRequirement_relation (`requeirment_id`,`codeRequeirment_id`) VALUES ('${projectRequiementId}','${ids}');" ;
			dataSource.executeInsert(insertSql);
		}
		return "成功";
	}
	
	/**
	 * 将数据模板引用到项目中的某个需求中
	 * @param params
	 * @param userId
	 * @return
	 */
	def saveForProjectRequirementWithTemplate(def params,def userId){
		// 获得项目信息
		def projectRequirementTemplateInfo = getRequirementTemplateById(params.projectRequirementId);
		// 将项目信息保存到模板信息中。
		String insertSql = "INSERT INTO `iplay`.`basic_project_requirement_info` "+
		"(`name`,`object_type`,`object_ids`,`requirement_state`,`begin_date`,`end_date`,`operation_person`,`operation_date`,`project_id`, `special_requirement_mes`) VALUES "+
		"('${projectRequirementTemplateInfo.name}','${projectRequirementTemplateInfo.object_type}','${projectRequirementTemplateInfo.object_ids}','0', '${projectRequirementTemplateInfo.begin_date}','${projectRequirementTemplateInfo.end_date}', '${userId}','"+new DateTime().toString("yyyy-MM-dd")+"','${params.projectId}','${projectRequirementTemplateInfo.special_requirement_mes}')" ;
		def dataSource = new Sql(dataSource_iplay)
		def result = dataSource.executeInsert(insertSql)
		String projectRequirementTemplateId = result.get(0).get(0);
		// 将需求具体的需求项放到需求项模板中
		insertSql = "INSERT INTO basic_requiremen_codeRequirement_relation (requeirment_id,codeRequeirment_id) SELECT "+projectRequirementTemplateId+" AS 'requeirment_id',codeRequeirment_id FROM basic_requiremen_templet_codeRequirement_relation where requeirment_templet_id = " + params.projectRequirementId;
		dataSource.executeInsert(insertSql);
		return "成功"
	}
	
	def runRequirementByStar(def projectRequiementId){
		println "开始明星生成excel文件${projectRequiementId}"
		boolean flag = false ;
		DateTools dt=new DateTools()
		Long beginTime = System.currentTimeMillis()
		
		def dataSourceMycatReputation = new Sql(dataSource_mycat_reputation)
		def dataSourceCrawl = new Sql(dataSource_crawl);
		def dataSource= new Sql(dataSource)
		def dataSourceIreport=new Sql(dataSource_ireport)
		def dataSourceCobarReputation=new Sql(dataSource_cobar_reputation)
		def dataSource_Iplay = new Sql(dataSource_iplay)
		
		// 更新需求是否在执行的为1
		dataSource_Iplay.executeUpdate("update basic_project_requirement_info set is_execute = 1 where id = ${projectRequiementId}")
		
		// 创建Excel文件
		SXSSFWorkbook wb = new SXSSFWorkbook();
		// 获得项目的需求信息
		def projectRequirement = this.getRequirementById(projectRequiementId);
		// 获得选中的需求类型
		String sql = "SELECT  * FROM code_localtion_requirement WHERE id IN (SELECT codeRequeirment_id FROM basic_requiremen_codeRequirement_relation WHERE requeirment_id = ${projectRequiementId}) ";
		String[] object_idArr = projectRequirement.object_ids.split(",");
		dataSource_Iplay.rows(sql).each{
			if(it.value.equals("media_attention")){
				Sheet Sheet = wb.createSheet(it.display_text)
				int index = 0 ;
				Row Row = Sheet.createRow(index++);
				Cell cell1 = Row.createCell(0);
				Cell cell2 = Row.createCell(1);
				Cell cell3 = Row.createCell(2);
				Cell cell4 = Row.createCell(3);
				cell1.setCellValue("明星ID");
				cell2.setCellValue("明星名称");
				cell3.setCellValue("媒体关注度");
				cell4.setCellValue("日期");
				for(int i = 0 ; i <object_idArr.length;i++ ){
		
					String object_id = object_idArr[i];
					// 查询明星基本信息
					String select_sql = "select  * from basic_artist_info where id = ${object_id}"
					def artistInfo = dataSource.firstRow(select_sql);
					String artistName = artistInfo.name
					// FIXME:获得明星媒体关注度
					String all_amount_sql = "SELECT hot_rate,record_date FROM domain_artist_hot_records where artist_id = ${object_id} and record_date >= '${projectRequirement.begin_date}' and record_date <= '${projectRequirement.end_date}'" ;
					dataSourceIreport.rows(all_amount_sql).each { artist ->
						Row = Sheet.createRow(index++);
						cell1 = Row.createCell(0)
						cell2 = Row.createCell(1);
						cell3 = Row.createCell(2);
						cell4 = Row.createCell(3);
		
						cell1.setCellValue(object_id);
						cell2.setCellValue(artistName);
						cell3.setCellValue(artist.hot_rate);
						cell4.setCellValue(dt.fmtDate(artist.record_date,"yyyy-MM-dd"));
					}
				}
			}
			else if(it.value.equals("public_influence")){
				Sheet Sheet = wb.createSheet(it.display_text)
				int index = 0 ;
				Row Row = Sheet.createRow(index++);
				Cell cell1 = Row.createCell(0);
				Cell cell2 = Row.createCell(1);
				Cell cell3 = Row.createCell(2);
				Cell cell4 = Row.createCell(3);
				cell1.setCellValue("明星ID");
				cell2.setCellValue("明星名称");
				cell3.setCellValue("公众影响力");
				cell4.setCellValue("日期");
				for(int i = 0 ; i <object_idArr.length;i++ ){
		
					String object_id = object_idArr[i];
					// 查询明星基本信息
					String select_sql = "select  * from basic_artist_info where id = ${object_id}"
					def artistInfo = dataSource.firstRow(select_sql);
					String artistName = artistInfo.name
					// FIXME:获得明星媒体关注度
					String all_amount_sql = "SELECT public_influence,record_date FROM domain_artist_hot_records where artist_id = ${object_id} and record_date >= '${projectRequirement.begin_date}' and record_date <= '${projectRequirement.end_date}'" ;
					dataSourceIreport.rows(all_amount_sql).each { artist ->
						Row = Sheet.createRow(index++);
						cell1 = Row.createCell(0)
						cell2 = Row.createCell(1);
						cell3 = Row.createCell(2);
						cell4 = Row.createCell(3);
		
						cell1.setCellValue(object_id);
						cell2.setCellValue(artistName);
						cell3.setCellValue(artist.public_influence);
						cell4.setCellValue(dt.fmtDate(artist.record_date,"yyyy-MM-dd"));
					}
				}
			}
			
			//-----exposed(门户曝光时长)没有数据 --------
			
			else if(it.value.equals("headline")){
				Sheet Sheet = wb.createSheet(it.display_text)
				int index = 0 ;
				Row Row = Sheet.createRow(index++);
				Cell cell1 = Row.createCell(0);
				Cell cell2 = Row.createCell(1);
				Cell cell3 = Row.createCell(2);
				Cell cell4 = Row.createCell(3);
				cell1.setCellValue("明星ID");
				cell2.setCellValue("明星名称");
				cell3.setCellValue("媒体头条量");
				cell4.setCellValue("日期");
				for(int i = 0 ; i <object_idArr.length;i++ ){
		
					String object_id = object_idArr[i];
					// 查询明星基本信息
					String select_sql = "select  * from basic_artist_info where id = ${object_id}"
					def artistInfo = dataSource.firstRow(select_sql);
					String artistName = artistInfo.name
					// FIXME:获得明星媒体关注度
					String all_amount_sql = "SELECT sum(count) sc,count_date FROM  `domain_expose_7` where object_id  = ${object_id} and count_date >= '${projectRequirement.begin_date}' and count_date <= '${projectRequirement.end_date}' group by count_date" ;
					dataSourceCobarReputation.rows(all_amount_sql).each { artist ->
						Row = Sheet.createRow(index++);
						cell1 = Row.createCell(0)
						cell2 = Row.createCell(1);
						cell3 = Row.createCell(2);
						cell4 = Row.createCell(3);
		
						cell1.setCellValue(object_id);
						cell2.setCellValue(artistName);
						cell3.setCellValue(artist.sc);
						cell4.setCellValue(artist.count_date);
					}
				}
			}
			// 热门文章
			else if(it.value.equals("article") ){
				Sheet Sheet = wb.createSheet(it.display_text)
				int index = 0 ;
				Row Row = Sheet.createRow(index++);
				Cell cell1 = Row.createCell(0);
				Cell cell2 = Row.createCell(1);
				Cell cell3 = Row.createCell(2);
				Cell cell4 = Row.createCell(3);
				Cell cell5 = Row.createCell(4);
				Cell cell6 = Row.createCell(5);
				Cell cell7 = Row.createCell(6);
				Cell cell8 = Row.createCell(7);
				Cell cell9 = Row.createCell(8);
				Cell cell10 = Row.createCell(9);
				Cell cell11 = Row.createCell(10);
				Cell cell12 = Row.createCell(11);
				
				cell1.setCellValue("明星ID");
				cell2.setCellValue("明星名称");
				cell3.setCellValue("文章的url");
				cell4.setCellValue("文章的标题");
				cell5.setCellValue("此文章的相似文章数");
				cell6.setCellValue("阅读数");
				cell7.setCellValue("转载数");
				cell8.setCellValue("收藏数");
				cell9.setCellValue("喜欢数");
				cell10.setCellValue("播放数");
				cell11.setCellValue("日期");
				cell12.setCellValue("关注度");
				
				String STAR_ARTICLE_SERVICE="http://qc.iminer.com:28007/oa";
				
				for(int i = 0 ; i <object_idArr.length;i++ ){
					
					String object_id = object_idArr[i];
					// 查询电影基本信息
					String select_sql = "select  * from basic_artist_info where id = ${object_id}"
					def artistInfo = dataSource.firstRow(select_sql);
					String artistName = artistInfo.name
					def startDate=dt.fmtDate(projectRequirement.begin_date as String, "yyyy-MM-dd").getTime();
					def endDate=dt.fmtDate(projectRequirement.end_date as String, "yyyy-MM-dd").getTime();
					
					def httpurl="${STAR_ARTICLE_SERVICE}?app_key=ireport&&method=getTopNArticles&ontologytypes=434&objectId=${object_id}&objectType=7&beginTime=${startDate}&endTime=${endDate}&maxNum=20";
					URL url=new URL(httpurl);
					URLConnection hpCon=url.openConnection()
					hpCon.connect();
					BufferedReader inbuffer=new BufferedReader(new InputStreamReader(hpCon.getInputStream()))
					String res="";
					String line="";
					while((line=inbuffer.readLine())!=null){
						res+=line;
					}
					inbuffer.close();
					Object obj=JSONValue.parse(res);
					JSONArray array=(JSONArray)obj;
					def articlist=[];
					int articlisCount = 0
					if(array){
						array.each {
							Row = Sheet.createRow(index++);
							cell1 = Row.createCell(0);
							cell2 = Row.createCell(1);
							cell3 = Row.createCell(2);
							cell4 = Row.createCell(3);
							cell5 = Row.createCell(4);
							cell6 = Row.createCell(5);
							cell7 = Row.createCell(6);
							cell8 = Row.createCell(7);
							cell9 = Row.createCell(8);
							cell10 = Row.createCell(9);
							cell11 = Row.createCell(10);
							cell12 = Row.createCell(11);
							
							cell1.setCellValue(object_id);
							cell2.setCellValue(artistName);
							cell3.setCellValue(it.url);
							cell4.setCellValue(it.title);
							cell5.setCellValue(it.SimilarCount);
							cell6.setCellValue(it.nRead);
							cell7.setCellValue(it.nRepost);
							cell8.setCellValue(it.nSubscribe);
							cell9.setCellValue(it.nLike);
							cell10.setCellValue(it.nPlay);
							cell11.setCellValue(it.publish_date);
							cell12.setCellValue(it.hotrate);
						}
					}
				}
			}
			
			// 热门微博
			else if(it.value.equals("microblogging")){
				Sheet Sheet = wb.createSheet(it.display_text)
				int index = 0 ;
				Row Row = Sheet.createRow(index++);
				Cell cell1 = Row.createCell(0);
				Cell cell2 = Row.createCell(1);
				Cell cell3 = Row.createCell(2);
				Cell cell4 = Row.createCell(3);
				Cell cell5 = Row.createCell(4);
				Cell cell6 = Row.createCell(5);
				Cell cell7 = Row.createCell(6);
				Cell cell8 = Row.createCell(7);
				Cell cell9 = Row.createCell(8);
				Cell cell10 = Row.createCell(9);
				Cell cell11 = Row.createCell(10);
				Cell cell12 = Row.createCell(11);
				
				cell1.setCellValue("明星ID");
				cell2.setCellValue("明星名称");
				cell3.setCellValue("文章的url");
				cell4.setCellValue("文章的标题");
				cell5.setCellValue("此文章的相似文章数");
				cell6.setCellValue("阅读数");
				cell7.setCellValue("转载数");
				cell8.setCellValue("收藏数");
				cell9.setCellValue("喜欢数");
				cell10.setCellValue("播放数");
				cell11.setCellValue("日期");
				cell12.setCellValue("关注度");
				
				String STAR_ARTICLE_SERVICE="http://qc.iminer.com:28007/oa";
				
				for(int i = 0 ; i <object_idArr.length;i++ ){
					
					String object_id = object_idArr[i];
					// 查询电影基本信息
					String select_sql = "select  * from basic_artist_info where id = ${object_id}"
					def artistInfo = dataSource.firstRow(select_sql);
					String artistName = artistInfo.name
					
					def startDate=dt.fmtDate(projectRequirement.begin_date as String, "yyyy-MM-dd").getTime();
					def endDate=dt.fmtDate(projectRequirement.end_date  as String, "yyyy-MM-dd").getTime();
					
					def httpurl="${STAR_ARTICLE_SERVICE}?app_key=ireport&&method=getTopNArticles&ontologytypes=424&objectId=${object_id}&objectType=7&beginTime=${startDate}&endTime=${endDate}&maxNum=20";
					URL url=new URL(httpurl);
					URLConnection hpCon=url.openConnection()
					hpCon.connect();
					BufferedReader inbuffer=new BufferedReader(new InputStreamReader(hpCon.getInputStream()))
					String res="";
					String line="";
					while((line=inbuffer.readLine())!=null){
						res+=line;
					}
					inbuffer.close();
					Object obj=JSONValue.parse(res);
					JSONArray array=(JSONArray)obj;
					def articlist=[];
					int articlisCount = 0
					if(array){
						DateTools dtools=new DateTools()
						array.each {
							Row = Sheet.createRow(index++);
							cell1 = Row.createCell(0);
							cell2 = Row.createCell(1);
							cell3 = Row.createCell(2);
							cell4 = Row.createCell(3);
							cell5 = Row.createCell(4);
							cell6 = Row.createCell(5);
							cell7 = Row.createCell(6);
							cell8 = Row.createCell(7);
							cell9 = Row.createCell(8);
							cell10 = Row.createCell(9);
							cell11 = Row.createCell(10);
							cell12 = Row.createCell(11);
							
							cell1.setCellValue(it.object_id);
							cell2.setCellValue(artistName);
							cell3.setCellValue(it.url);
							cell4.setCellValue(it.title);
							cell5.setCellValue(it.SimilarCount);
							cell6.setCellValue(it.nRead);
							cell7.setCellValue(it.nRepost);
							cell8.setCellValue(it.nSubscribe);
							cell9.setCellValue(it.nLike);
							cell10.setCellValue(it.nPlay);
							cell11.setCellValue(it.publish_date);
							cell12.setCellValue(it.hotrate);
						}
					}
				}
			}
			else if(it.value.equals("dimension")){
				Sheet Sheet = wb.createSheet(it.display_text)
				int index = 0 ;
				Row Row = Sheet.createRow(index++);
				Cell cell1 = Row.createCell(0);
				Cell cell2 = Row.createCell(1);
				Cell cell3 = Row.createCell(2);
				Cell cell4 = Row.createCell(3);
				Cell cell5 = Row.createCell(4);
				cell1.setCellValue("明星ID");
				cell2.setCellValue("明星名称");
				cell3.setCellValue("维度名称");
				cell4.setCellValue("提及量");
				cell5.setCellValue("日期");
				for(int i = 0 ; i <object_idArr.length;i++ ){
		
					String object_id = object_idArr[i];
					// 查询明星基本信息
					String select_sql = "select  * from basic_artist_info where id = ${object_id}"
					def artistInfo = dataSource.firstRow(select_sql);
					String artistName = artistInfo.name
					// FIXME:获得明星各维度提及量
					String all_amount_sql = "SELECT evaluate_type,sum(positive_num)+sum(negative_num)+sum(neutral_num) tjl,publish_date FROM `ireport_star_praise_statistic_day` where star_id = ${object_id} and publish_date>='${projectRequirement.begin_date}' and publish_date<='${projectRequirement.end_date}'  GROUP BY star_id,evaluate_type,publish_date order by star_id,publish_date,evaluate_type" ;
					dataSourceMycatReputation.rows(all_amount_sql).each { artist ->
						Row = Sheet.createRow(index++);
						cell1 = Row.createCell(0)
						cell2 = Row.createCell(1);
						cell3 = Row.createCell(2);
						cell4 = Row.createCell(3);
						cell5 = Row.createCell(4);
						
						cell1.setCellValue(object_id);
						cell2.setCellValue(artistName);
						
						String s2="SELECT name FROM  basic_ent_category where cid="+artist.evaluate_type
						def originName=dataSource.firstRow(s2).name
						
						
						cell3.setCellValue(originName);
						cell4.setCellValue(artist.tjl);
						cell5.setCellValue(dt.fmtDate(artist.publish_date,"yyyy-MM-dd"));
					}
				}
			}
			else if(it.value.equals("favorable_rate") && it.groupName.equals("reputation") ){
				Sheet Sheet = wb.createSheet("口碑"+it.display_text)
				int index = 0 ;
				Row Row = Sheet.createRow(index++);
				Cell cell1 = Row.createCell(0);
				Cell cell2 = Row.createCell(1);
				Cell cell3 = Row.createCell(2);
				Cell cell4 = Row.createCell(3);
				Cell cell5 = Row.createCell(4);
				Cell cell6 = Row.createCell(5);
				Cell cell7 = Row.createCell(6);
				Cell cell8 = Row.createCell(7);
				cell1.setCellValue("明星ID");
				cell2.setCellValue("明星名称");
				cell3.setCellValue("维度");
				cell4.setCellValue("好评");
				cell5.setCellValue("差评");
				cell6.setCellValue("中评");
				cell7.setCellValue("好评率");
				cell8.setCellValue("时间");
				for(int i = 0 ; i <object_idArr.length;i++ ){
					
					String object_id = object_idArr[i];
					// 查询电影基本信息
					String select_sql = "select  * from basic_artist_info where id = ${object_id}"
					def artistInfo = dataSource.firstRow(select_sql);
					String artistName = artistInfo.name
					
					// 获得电影好评率
					String all_amount_sql = "SELECT sum(positive_num) hp,sum(negative_num) cp,sum(neutral_num) zp,CONCAT((sum(positive_num)/(sum(positive_num)+sum(negative_num)))*100,'%') hpl,evaluate_type,publish_date from `ireport_star_praise_statistic_day` where star_id = ${object_id} and publish_date>='${projectRequirement.begin_date}' and publish_date<='${projectRequirement.end_date}'  GROUP BY star_id,evaluate_type,publish_date order by star_id,publish_date,evaluate_type" ;
					dataSourceMycatReputation.rows(all_amount_sql).each { artist ->
						Row = Sheet.createRow(index++);
						cell1 = Row.createCell(0);
						cell2 = Row.createCell(1);
						cell3 = Row.createCell(2);
						cell4 = Row.createCell(3);
						cell5 = Row.createCell(4);
						cell6 = Row.createCell(5);
						cell7 = Row.createCell(6);
						cell8 = Row.createCell(7);
						cell1.setCellValue(object_id);
						cell2.setCellValue(artistName);
						
						// 去计算这个维度ID对应的名称
						def evaluateInfo = dataSource.firstRow("select * From ent_domain.`basic_ent_category` where cid = " + artist.evaluate_type);
						cell3.setCellValue(evaluateInfo?.name);
						
						cell4.setCellValue(artist.hp);
						cell5.setCellValue(artist.cp);
						cell6.setCellValue(artist.zp);
						cell7.setCellValue(artist.hpl);
						cell8.setCellValue(dt.fmtDate(artist.publish_date,"yyyy-MM-dd"));
					}
				}
			}
			else if(it.value.equals("hot_word")  ){
				Sheet Sheet = wb.createSheet(it.display_text)
				int index = 0 ;
				Row Row = Sheet.createRow(index++);
				Cell cell1 = Row.createCell(0);
				Cell cell2 = Row.createCell(1);
				Cell cell3 = Row.createCell(2);
				Cell cell4 = Row.createCell(3);
				Cell cell5 = Row.createCell(4);
				cell1.setCellValue("明星ID");
				cell2.setCellValue("明星名称");
				cell3.setCellValue("热词");
				cell4.setCellValue("词频");
				cell5.setCellValue("日期");
				for(int i = 0 ; i <object_idArr.length;i++ ){
					
					String object_id = object_idArr[i];
					// 查询电影基本信息
					String select_sql = "select  * from basic_artist_info where id = ${object_id}"
					def artistInfo = dataSource.firstRow(select_sql);
					String artistName = artistInfo.name
					
					// 获得电影热词
					String all_amount_sql = "SELECT word,publish_date,num FROM `domain_star_hotword_count` where star_id = ${object_id} and publish_date>='${projectRequirement.begin_date}' and publish_date <='${projectRequirement.end_date}' GROUP BY star_id,word,publish_date" ;
					dataSourceMycatReputation.rows(all_amount_sql).each { artist ->
						Row = Sheet.createRow(index++);
						cell1 = Row.createCell(0);
						cell2 = Row.createCell(1);
						cell3 = Row.createCell(2);
						cell4 = Row.createCell(3);
						cell5 = Row.createCell(4);
						cell1.setCellValue(object_id);
						cell2.setCellValue(artistName);
						cell3.setCellValue(artist.word);
						cell4.setCellValue(artist.num);
						cell5.setCellValue(dt.fmtDate(artist.publish_date,"yyyy-MM-dd"));
					}
				}
			}
			else if(it.value.equals("negative_positive")   ){
				Sheet Sheet = wb.createSheet(it.display_text)
				int index = 0 ;
				Row Row = Sheet.createRow(index++);
				Cell cell1 = Row.createCell(0);
				Cell cell2 = Row.createCell(1);
				Cell cell3 = Row.createCell(2);
				Cell cell4 = Row.createCell(3);
				Cell cell5 = Row.createCell(4);
				cell1.setCellValue("明星ID");
				cell2.setCellValue("明星名称");
				cell3.setCellValue("短语");
				cell4.setCellValue("正负");
				cell5.setCellValue("日期");
				for(int i = 0 ; i <object_idArr.length;i++ ){
					
					String object_id = object_idArr[i];
					// 查询明星基本信息
					String select_sql = "select  * from basic_artist_info where id = ${object_id}"
					def artistInfo = dataSource.firstRow(select_sql);
					String artistName = artistInfo.name
					
					// 获得明星正负短语
					String all_amount_sql = "SELECT phrase,CASE emotion_type when 1 then '正' ELSE'负' end et,publish_date FROM `domain_star_phrase_count` where star_id = ${object_id} and publish_date>='${projectRequirement.begin_date}' and publish_date <='${projectRequirement.end_date}' GROUP BY star_id,publish_date,phrase" ;
					dataSourceMycatReputation.rows(all_amount_sql).each { artist ->
						Row = Sheet.createRow(index++);
						cell1 = Row.createCell(0)
						cell2 = Row.createCell(1);
						cell3 = Row.createCell(2);
						cell4 = Row.createCell(3);
						cell5 = Row.createCell(4);
						cell1.setCellValue(object_id);
						cell2.setCellValue(artistName);
						cell3.setCellValue(artist.phrase);
						cell4.setCellValue(artist.et);
						cell5.setCellValue(dt.fmtDate(artist.publish_date,"yyyy-MM-dd"));
					}
				}
			}
			
			else if(it.value.equals("raw")  ){
				Sheet Sheet = wb.createSheet(it.display_text)
				int index = 0 ;
				Row Row = Sheet.createRow(index++);
				Cell cell1 = Row.createCell(0);
				Cell cell2 = Row.createCell(1);
				Cell cell3 = Row.createCell(2);
				Cell cell4 = Row.createCell(3);
				Cell cell5 = Row.createCell(4);
				cell1.setCellValue("明星ID");
				cell2.setCellValue("明星 名称");
				cell3.setCellValue("句子");
				cell4.setCellValue("热词");
				cell5.setCellValue("短语");
				for(int i = 0 ; i <object_idArr.length;i++ ){
					
					String object_id = object_idArr[i];
					// 查询电影基本信息
					String select_sql = "select  * from basic_artist_info where id = ${object_id}"
					def artistInfo = dataSource.firstRow(select_sql);
					String artistName = artistInfo.name
					
					// 获得电影原始内容
					def mongoDB= null 
					
					
					
					java.util.Date nowdate=new java.util.Date();
					String myString = projectRequirement.begin_date as String;
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Date d = sdf.parse(myString);
					Date da= sdf.parse("2015-12-01");
					
					boolean flagF = d.before(da);
					if(flagF){
						mongoDB=MongoDbConnectConfig.getMongoDb(object_id as int, MongoDbConnectConfig.MONGO_STAR_TYPE, null)//getMongoDb(tvId)
					}else{
						mongoDB=MongoDbConnectConfig.getMongoDb(object_id as int, MongoDbConnectConfig.MONGO_STAR_TYPE_2, null)//getMongoDb(tvId)
					}
					
//					mongoDB=MongoDbConnectConfig.getMongoDb(object_id as int, MongoDbConnectConfig.MONGO_STAR_TYPE, null)//getMongoDb(tvId)
					BasicDBObject query=new BasicDBObject();
					query.put("object_id", object_id)
					query.put("object_type","7")
					BasicDBObject lte=new BasicDBObject();
					lte.put('$gte', "${projectRequirement.begin_date} 00:00:00".toString())
					lte.put('$lte', "${projectRequirement.end_date} 23:59:59".toString())
					query.put("time",lte)
					def re = mongoDB.find(query).limit(500)
					def result = re.toArray()
					result.each {
						Row = Sheet.createRow(index++);
						cell1 = Row.createCell(0)
						cell2 = Row.createCell(1);
						cell3 = Row.createCell(2);
						cell4 = Row.createCell(3);
						cell5 = Row.createCell(4);
						
						cell1.setCellValue(object_id);
						cell2.setCellValue(artistName);
						cell3.setCellValue(it.sentence);
						cell4.setCellValue(it.hotWords as String);
						cell5.setCellValue(it?.phrase == null ?"":(it?.phrase as String))
					}
				}
			}
			
		}
		
		//将文件上传到服务器
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		wb.write(out)
		String[] uriArr;
		try {
			uriArr = iFastDfs.upload(out.toByteArray(),"xls",null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		String down_url = (uriArr[0]+"/"+uriArr[1]+"?attname="+projectRequirement.name+".xls");
		wb.close();
		out.close();
		// 将下载地址更新到数据库中
		int changeCount = dataSource_Iplay.executeUpdate("update basic_project_requirement_info set down_url = ${down_url},requirement_state = 3 where id = ${projectRequiementId}")
		println "提交生成excel并保存到数据库成功!${projectRequiementId}"
		
		System.out.println("耗时："+(System.currentTimeMillis() - beginTime));
		
		sendMailForProjectRequirement(projectRequiementId);
		
		return "生成成功!"
		
	}
	
	def runRequirementByMovie(def projectRequiementId){
		println "开始电影生成excel文件${projectRequiementId}"
		DateTools dt=new DateTools()
		Long beginTime = System.currentTimeMillis()
		
		def dataSourceMycatReputation = new Sql(dataSource_mycat_reputation)
		def dataSourceCrawl = new Sql(dataSource_crawl);
		def dataSource= new Sql(dataSource)
		def dataSourceIreport=new Sql(dataSource_ireport)
		def dataSourceCobarReputation=new Sql(dataSource_cobar_reputation)
		def dataSource_Iplay = new Sql(dataSource_iplay)
		
		// 更新需求是否在执行的为1
		dataSource_Iplay.executeUpdate("update basic_project_requirement_info set is_execute = 1 where id = ${projectRequiementId}")
		
		// 创建Excel文件
		SXSSFWorkbook wb = new SXSSFWorkbook();
		// 获得项目的需求信息
		def projectRequirement = this.getRequirementById(projectRequiementId);
		// 获得选中的需求类型
		String sql = "SELECT  * FROM code_localtion_requirement WHERE id IN (SELECT codeRequeirment_id FROM basic_requiremen_codeRequirement_relation WHERE requeirment_id = ${projectRequiementId}) ";
		String[] object_idArr = projectRequirement.object_ids.split(",");
		dataSource_Iplay.rows(sql).each{
			//--------------电影票房-----------------------
			System.out.println(it.value);
			
			// 查询总票房
			if(it.value.equals("total_box_office")){
				getMovieTotalBoxOfficeSheetWithGPFlowData(wb, it, object_idArr, dataSource, projectRequirement, dataSourceIreport,dataSourceCrawl)
			}
			else if(it.value.equals("province") ){
				getMovieProvinceSheetWithGPFlowData(wb, it, object_idArr, dataSource, projectRequirement, dataSourceIreport,dataSourceCrawl)
			}
			else if(it.value.equals("city")){
				getMovieCitySheetWithGPFlowData(wb, it, object_idArr, dataSource, projectRequirement, dataSourceIreport,dataSourceCrawl)
			}/**/
			else if(it.value.equals("cinema") ){
				getMovieCinemaSheetWithGPFlowData(wb, it, object_idArr, dataSource, dataSourceCrawl,dataSourceIreport, projectRequirement)
			}
			else if(it.value.equals("theatres")  ){
				getMovieTheatresSheetWithGPFlowData(wb, it, object_idArr, dataSource, dataSourceCrawl,dataSourceIreport, projectRequirement)
			}
			else if(it.value.equals("totoal_season") ){
				getMovieTotoalSeasonSheet(wb, it, object_idArr, dataSource, dataSourceCrawl, projectRequirement)
			}
			else if(it.value.equals("total_people") ){
				getMovieTotalPeopleSheet(wb, it, object_idArr, dataSource, dataSourceCrawl, projectRequirement)
			}
			//--------------电影网络播放量-----------------------
			
			else if(it.value.equals("all_amount") ){
				getMovieAllAmountSheet(wb, it, object_idArr, dataSource, dataSourceCrawl, projectRequirement)
			}
			
			else if(it.value.equals("web_amount")){
				getMovieWebAmountSheet(wb, it, object_idArr, dataSource, dataSourceCrawl, projectRequirement)
			}
			
			//--------------电影影响力-----------------------

			
			else if(it.value.equals("media_attention")   ){
				getMovieMediaAttentionSheet(wb, it, object_idArr, dataSource,dataSourceCobarReputation, projectRequirement, dt)
			}
			
			else if(it.value.equals("public_influence") ){
				getMoviePublicInfluenceSheet(wb, it, object_idArr, dataSource,dataSourceCobarReputation, projectRequirement, dt)
			}
			
			//-----exposed(门户曝光时长)没有数据 --------
			
			
			else if(it.value.equals("headline")  ){
				Sheet Sheet = wb.createSheet(it.display_text)
				int index = 0 ;
				Row Row = Sheet.createRow(index++);
				Cell cell1 = Row.createCell(0);
				Cell cell2 = Row.createCell(1);
				Cell cell3 = Row.createCell(2);
				Cell cell4 = Row.createCell(3);
				cell1.setCellValue("电影ID");
				cell2.setCellValue("电影名称");
				cell3.setCellValue("媒体头条量");
				cell4.setCellValue("日期");
				for(int i = 0 ; i <object_idArr.length;i++ ){
					
					String object_id = object_idArr[i];
					// 查询电影基本信息
					String select_sql = "select  * from basic_movie_info where id = ${object_id}"
					def movieInfo = dataSource.firstRow(select_sql);
					String movieName = movieInfo.name
					
					// 获得电影媒体头条量
					String all_amount_sql = "SELECT sum(count) sc,count_date FROM  `domain_expose_4` where object_id  = ${object_id} and count_date >= '${projectRequirement.begin_date}' and count_date <= '${projectRequirement.end_date}' group by count_date" ;
					dataSourceCobarReputation.rows(all_amount_sql).each { movie ->
						Row = Sheet.createRow(index++);
						cell1 = Row.createCell(0)
						cell2 = Row.createCell(1);
						cell3 = Row.createCell(2);
						cell4 = Row.createCell(3);
						
						cell1.setCellValue(object_id);
						cell2.setCellValue(movieName);
						cell3.setCellValue(movie.sc);
						cell4.setCellValue(movie.count_date);
					}
				}
			}
			// 热门文章
			else if(it.value.equals("article") ){
				Sheet Sheet = wb.createSheet(it.display_text)
				int index = 0 ;
				Row Row = Sheet.createRow(index++);
				Cell cell1 = Row.createCell(0);
				Cell cell2 = Row.createCell(1);
				Cell cell3 = Row.createCell(2);
				Cell cell4 = Row.createCell(3);
				Cell cell5 = Row.createCell(4);
				Cell cell6 = Row.createCell(5);
				Cell cell7 = Row.createCell(6);
				Cell cell8 = Row.createCell(7);
				Cell cell9 = Row.createCell(8);
				Cell cell10 = Row.createCell(9);
				Cell cell11 = Row.createCell(10);
				Cell cell12 = Row.createCell(11);
				
				cell1.setCellValue("电影ID");
				cell2.setCellValue("电影名称");
				cell3.setCellValue("文章的url");
				cell4.setCellValue("文章的标题");
				cell5.setCellValue("此文章的相似文章数");
				cell6.setCellValue("阅读数");
				cell7.setCellValue("转载数");
				cell8.setCellValue("收藏数");
				cell9.setCellValue("喜欢数");
				cell10.setCellValue("播放数");
				cell11.setCellValue("日期");
				cell12.setCellValue("关注度");
				
				String STAR_ARTICLE_SERVICE="http://qc.iminer.com:28007/oa";
				
				for(int i = 0 ; i <object_idArr.length;i++ ){
					
					String object_id = object_idArr[i];
					// 查询电影基本信息
					String select_sql = "select  * from basic_movie_info where id = ${object_id}"
					def movieInfo = dataSource.firstRow(select_sql);
					String movieName = movieInfo.name
					def startDate=dt.fmtDate(projectRequirement.begin_date as String, "yyyy-MM-dd").getTime();
					def endDate=dt.fmtDate(projectRequirement.end_date as String, "yyyy-MM-dd").getTime();
					
					def httpurl="${STAR_ARTICLE_SERVICE}?app_key=ireport&&method=getTopNArticles&ontologytypes=434&objectId=${object_id}&objectType=4&beginTime=${startDate}&endTime=${endDate}&maxNum=20";
					URL url=new URL(httpurl);
					URLConnection hpCon=url.openConnection()
					hpCon.connect();
					BufferedReader inbuffer=new BufferedReader(new InputStreamReader(hpCon.getInputStream()))
					String res="";
					String line="";
					while((line=inbuffer.readLine())!=null){
						res+=line;
					}
					inbuffer.close();
					Object obj=JSONValue.parse(res);
					JSONArray array=(JSONArray)obj;
					def articlist=[];
					int articlisCount = 0
					if(array){
						DateTools dtools=new DateTools()
						array.each {
							Row = Sheet.createRow(index++);
							cell1 = Row.createCell(0);
							cell2 = Row.createCell(1);
							cell3 = Row.createCell(2);
							cell4 = Row.createCell(3);
							cell5 = Row.createCell(4);
							cell6 = Row.createCell(5);
							cell7 = Row.createCell(6);
							cell8 = Row.createCell(7);
							cell9 = Row.createCell(8);
							cell10 = Row.createCell(9);
							cell11 = Row.createCell(10);
							cell12 = Row.createCell(11);
							
							cell1.setCellValue(object_id);
							cell2.setCellValue(movieName);
							cell3.setCellValue(it.url);
							cell4.setCellValue(it.title);
							cell5.setCellValue(it.SimilarCount);
							cell6.setCellValue(it.nRead);
							cell7.setCellValue(it.nRepost);
							cell8.setCellValue(it.nSubscribe);
							cell9.setCellValue(it.nLike);
							cell10.setCellValue(it.nPlay);
							cell11.setCellValue(it.publish_date);
							cell12.setCellValue(it.hotrate);
						}
					}
				}
			}
			
			// 热门微博
			else if(it.value.equals("microblogging")){
				Sheet Sheet = wb.createSheet(it.display_text)
				int index = 0 ;
				Row Row = Sheet.createRow(index++);
				Cell cell1 = Row.createCell(0);
				Cell cell2 = Row.createCell(1);
				Cell cell3 = Row.createCell(2);
				Cell cell4 = Row.createCell(3);
				Cell cell5 = Row.createCell(4);
				Cell cell6 = Row.createCell(5);
				Cell cell7 = Row.createCell(6);
				Cell cell8 = Row.createCell(7);
				Cell cell9 = Row.createCell(8);
				Cell cell10 = Row.createCell(9);
				Cell cell11 = Row.createCell(10);
				Cell cell12 = Row.createCell(11);
				
				cell1.setCellValue("电影ID");
				cell2.setCellValue("电影名称");
				cell3.setCellValue("文章的url");
				cell4.setCellValue("文章的标题");
				cell5.setCellValue("此文章的相似文章数");
				cell6.setCellValue("阅读数");
				cell7.setCellValue("转载数");
				cell8.setCellValue("收藏数");
				cell9.setCellValue("喜欢数");
				cell10.setCellValue("播放数");
				cell11.setCellValue("日期");
				cell12.setCellValue("关注度");
				
				String STAR_ARTICLE_SERVICE="http://qc.iminer.com:28007/oa";
				
				for(int i = 0 ; i <object_idArr.length;i++ ){
					
					String object_id = object_idArr[i];
					// 查询电影基本信息
					String select_sql = "select  * from basic_movie_info where id = ${object_id}"
					def movieInfo = dataSource.firstRow(select_sql);
					String movieName = movieInfo.name
					
					def startDate=dt.fmtDate(projectRequirement.begin_date as String, "yyyy-MM-dd").getTime();
					def endDate=dt.fmtDate(projectRequirement.end_date  as String, "yyyy-MM-dd").getTime();
					
					def httpurl="${STAR_ARTICLE_SERVICE}?app_key=ireport&&method=getTopNArticles&ontologytypes=424&objectId=${object_id}&objectType=4&beginTime=${startDate}&endTime=${endDate}&maxNum=20";
					URL url=new URL(httpurl);
					URLConnection hpCon=url.openConnection()
					hpCon.connect();
					BufferedReader inbuffer=new BufferedReader(new InputStreamReader(hpCon.getInputStream()))
					String res="";
					String line="";
					while((line=inbuffer.readLine())!=null){
						res+=line;
					}
					inbuffer.close();
					Object obj=JSONValue.parse(res);
					JSONArray array=(JSONArray)obj;
					def articlist=[];
					int articlisCount = 0
					if(array){
						DateTools dtools=new DateTools()
						array.each {
							Row = Sheet.createRow(index++);
							cell1 = Row.createCell(0);
							cell2 = Row.createCell(1);
							cell3 = Row.createCell(2);
							cell4 = Row.createCell(3);
							cell5 = Row.createCell(4);
							cell6 = Row.createCell(5);
							cell7 = Row.createCell(6);
							cell8 = Row.createCell(7);
							cell9 = Row.createCell(8);
							cell10 = Row.createCell(9);
							cell11 = Row.createCell(10);
							cell12 = Row.createCell(11);
							
							cell1.setCellValue(it.object_id);
							cell2.setCellValue(movieName);
							cell3.setCellValue(it.url);
							cell4.setCellValue(it.title);
							cell5.setCellValue(it.SimilarCount);
							cell6.setCellValue(it.nRead);
							cell7.setCellValue(it.nRepost);
							cell8.setCellValue(it.nSubscribe);
							cell9.setCellValue(it.nLike);
							cell10.setCellValue(it.nPlay);
							cell11.setCellValue(it.publish_date);
							cell12.setCellValue(it.hotrate);
						}
					}
				}
			}
			
			//-----口碑评估 ------
			
			
			else if(it.value.equals("dimension")  && it.groupName.equals("reputation")){
				Sheet Sheet = wb.createSheet(it.display_text)
				int index = 0 ;
				Row Row = Sheet.createRow(index++);
				Cell cell1 = Row.createCell(0);
				Cell cell2 = Row.createCell(1);
				Cell cell3 = Row.createCell(2);
				Cell cell4 = Row.createCell(3);
				Cell cell5 = Row.createCell(4);
				cell1.setCellValue("电影ID");
				cell2.setCellValue("电影名称");
				cell3.setCellValue("维度");
				cell4.setCellValue("提及量");
				cell5.setCellValue("时间");
				for(int i = 0 ; i <object_idArr.length;i++ ){
					
					String object_id = object_idArr[i];
					// 查询电影基本信息
					String select_sql = "select  * from basic_movie_info where id = ${object_id}"
					def movieInfo = dataSource.firstRow(select_sql);
					String movieName = movieInfo.name
					
					// 获得电影各维度提及量
					String all_amount_sql = "SELECT evaluate_type,sum(positive_num)+sum(negative_num)+sum(neutral_num) tjl,publish_date FROM `ireport_movie_praise_statistic_day` where movie_id = ${object_id} and publish_date>='${projectRequirement.begin_date}' and publish_date<='${projectRequirement.end_date}'  GROUP BY movie_id,evaluate_type,publish_date order by movie_id,publish_date,evaluate_type" ;
					dataSourceMycatReputation.rows(all_amount_sql).each { movie ->
						Row = Sheet.createRow(index++);
						cell1 = Row.createCell(0)
						cell2 = Row.createCell(1);
						cell3 = Row.createCell(2);
						cell4 = Row.createCell(3);
						cell5 = Row.createCell(4);
						
						cell1.setCellValue(object_id);
						cell2.setCellValue(movieName);
						
						String s2="SELECT name FROM  basic_ent_category where cid="+movie.evaluate_type
						def originName=dataSource.firstRow(s2).name
						
						
						cell3.setCellValue(originName);
						cell4.setCellValue(movie.tjl);
						cell5.setCellValue(dt.fmtDate(movie.publish_date,"yyyy-MM-dd"));
					}
				}
			}
			else if(it.value.equals("favorable_rate") && it.groupName.equals("reputation") ){
				Sheet Sheet = wb.createSheet("口碑"+it.display_text)
				int index = 0 ;
				Row Row = Sheet.createRow(index++);
				Cell cell1 = Row.createCell(0);
				Cell cell2 = Row.createCell(1);
				Cell cell3 = Row.createCell(2);
				Cell cell4 = Row.createCell(3);
				Cell cell5 = Row.createCell(4);
				Cell cell6 = Row.createCell(5);
				Cell cell7 = Row.createCell(6);
				Cell cell8 = Row.createCell(7);
				cell1.setCellValue("电影ID");
				cell2.setCellValue("电影名称");
				cell3.setCellValue("维度");
				cell4.setCellValue("好评");
				cell5.setCellValue("差评");
				cell6.setCellValue("中评");
				cell7.setCellValue("好评率");
				cell8.setCellValue("时间");
				for(int i = 0 ; i <object_idArr.length;i++ ){
					
					String object_id = object_idArr[i];
					// 查询电影基本信息
					String select_sql = "select  * from basic_movie_info where id = ${object_id}"
					def movieInfo = dataSource.firstRow(select_sql);
					String movieName = movieInfo.name
					
					// 获得电影好评率
					String all_amount_sql = "SELECT sum(positive_num) hp,sum(negative_num) cp,sum(neutral_num) zp,CONCAT((sum(positive_num)/(sum(positive_num)+sum(negative_num)))*100,'%') hpl,evaluate_type,publish_date from `ireport_movie_praise_statistic_day` where movie_id = ${object_id} and publish_date>='${projectRequirement.begin_date}' and publish_date<='${projectRequirement.end_date}'  GROUP BY movie_id,evaluate_type,publish_date order by movie_id,publish_date,evaluate_type" ;
					dataSourceMycatReputation.rows(all_amount_sql).each { movie ->
						Row = Sheet.createRow(index++);
						cell1 = Row.createCell(0);
						cell2 = Row.createCell(1);
						cell3 = Row.createCell(2);
						cell4 = Row.createCell(3);
						cell5 = Row.createCell(4);
						cell6 = Row.createCell(5);
						cell7 = Row.createCell(6);
						cell8 = Row.createCell(7);
						cell1.setCellValue(object_id);
						cell2.setCellValue(movieName);
						
						// 去计算这个维度ID对应的名称
						def evaluateInfo = dataSource.firstRow("select * From ent_domain.`basic_ent_category` where cid = " + movie.evaluate_type);
						cell3.setCellValue(evaluateInfo?.name);
						
						cell4.setCellValue(movie.hp);
						cell5.setCellValue(movie.cp);
						cell6.setCellValue(movie.zp);
						cell7.setCellValue(movie.hpl);
						cell8.setCellValue(dt.fmtDate(movie.publish_date,"yyyy-MM-dd"));
					}
				}
			}
			else if(it.value.equals("hot_word")  ){
				Sheet Sheet = wb.createSheet(it.display_text)
				int index = 0 ;
				Row Row = Sheet.createRow(index++);
				Cell cell1 = Row.createCell(0);
				Cell cell2 = Row.createCell(1);
				Cell cell3 = Row.createCell(2);
				Cell cell4 = Row.createCell(3);
				cell1.setCellValue("电影ID");
				cell2.setCellValue("电影名称");
				cell3.setCellValue("热词");
				cell4.setCellValue("日期");
				for(int i = 0 ; i <object_idArr.length;i++ ){
					
					String object_id = object_idArr[i];
					// 查询电影基本信息
					String select_sql = "select  * from basic_movie_info where id = ${object_id}"
					def movieInfo = dataSource.firstRow(select_sql);
					String movieName = movieInfo.name
					
					// 获得电影热词
					String all_amount_sql = "SELECT word,publish_date FROM `domain_movie_hotwords_aspects_count` where movie_id = ${object_id} and publish_date>='${projectRequirement.begin_date}' and publish_date <='${projectRequirement.end_date}' GROUP BY movie_id,word,publish_date" ;
					dataSourceMycatReputation.rows(all_amount_sql).each { movie ->
						Row = Sheet.createRow(index++);
						cell1 = Row.createCell(0);
						cell2 = Row.createCell(1);
						cell3 = Row.createCell(2);
						cell4 = Row.createCell(3);
						cell1.setCellValue(object_id);
						cell2.setCellValue(movieName);
						cell3.setCellValue(movie.word);
						cell4.setCellValue(dt.fmtDate(movie.publish_date,"yyyy-MM-dd"));
					}
				}
			}
			else if(it.value.equals("negative_positive")   ){
				Sheet Sheet = wb.createSheet(it.display_text)
				int index = 0 ;
				Row Row = Sheet.createRow(index++);
				Cell cell1 = Row.createCell(0);
				Cell cell2 = Row.createCell(1);
				Cell cell3 = Row.createCell(2);
				Cell cell4 = Row.createCell(3);
				Cell cell5 = Row.createCell(4);
				cell1.setCellValue("电影ID");
				cell2.setCellValue("电影名称");
				cell3.setCellValue("短语");
				cell4.setCellValue("正负");
				cell5.setCellValue("日期");
				for(int i = 0 ; i <object_idArr.length;i++ ){
					
					String object_id = object_idArr[i];
					// 查询电影基本信息
					String select_sql = "select  * from basic_movie_info where id = ${object_id}"
					def movieInfo = dataSource.firstRow(select_sql);
					String movieName = movieInfo.name
					
					// 获得电影正负短语
					String all_amount_sql = "SELECT phrase,CASE emotion_type when 1 then '正' ELSE'负' end et,publish_date FROM `domain_movie_aspects_count` where movie_id = ${object_id} and publish_date>='${projectRequirement.begin_date}' and publish_date <='${projectRequirement.end_date}' GROUP BY movie_id,publish_date,phrase" ;
					dataSourceMycatReputation.rows(all_amount_sql).each { movie ->
						Row = Sheet.createRow(index++);
						cell1 = Row.createCell(0)
						cell2 = Row.createCell(1);
						cell3 = Row.createCell(2);
						cell4 = Row.createCell(3);
						cell5 = Row.createCell(4);
						cell1.setCellValue(object_id);
						cell2.setCellValue(movieName);
						cell3.setCellValue(movie.phrase);
						cell4.setCellValue(movie.et);
						cell5.setCellValue(dt.fmtDate(movie.publish_date,"yyyy-MM-dd"));
					}
				}
			}
			
			else if(it.value.equals("raw")  ){
				Sheet Sheet = wb.createSheet(it.display_text)
				int index = 0 ;
				Row Row = Sheet.createRow(index++);
				Cell cell1 = Row.createCell(0);
				Cell cell2 = Row.createCell(1);
				Cell cell3 = Row.createCell(2);
				Cell cell4 = Row.createCell(3);
				Cell cell5 = Row.createCell(4);
				cell1.setCellValue("电影ID");
				cell2.setCellValue("电影名称");
				cell3.setCellValue("句子");
				cell4.setCellValue("热词");
				cell5.setCellValue("短语");
				for(int i = 0 ; i <object_idArr.length;i++ ){
					
					String object_id = object_idArr[i];
					// 查询电影基本信息
					String select_sql = "select  * from basic_movie_info where id = ${object_id}"
					def movieInfo = dataSource.firstRow(select_sql);
					String movieName = movieInfo.name
					
					// 获得电影原始内容
					def mongoDB= MongoDbConnectConfig.getMongoDb(object_id as int, MongoDbConnectConfig.MONGO_MOVIE_TYPE, null)//getMongoDb(tvId)
					BasicDBObject query=new BasicDBObject();
					query.put("object_id", object_id)
					query.put("object_type","4")
					BasicDBObject lte=new BasicDBObject();
					lte.put('$gte', "${projectRequirement.begin_date} 00:00:00".toString())
					lte.put('$lte', "${projectRequirement.end_date} 23:59:59".toString())
					query.put("time",lte)
					def re = mongoDB.find(query).limit(500)
					def result = re.toArray()
					result.each {
						Row = Sheet.createRow(index++);
						cell1 = Row.createCell(0)
						cell2 = Row.createCell(1);
						cell3 = Row.createCell(2);
						cell4 = Row.createCell(3);
						cell5 = Row.createCell(4);
						
						cell1.setCellValue(object_id);
						cell2.setCellValue(movieName);
						cell3.setCellValue(it.sentence);
						cell4.setCellValue(it.hotWords as String);
						cell5.setCellValue(it?.phrase == null ?"":(it?.phrase as String))
					}
				}
			}
			
			// 导演的提及量
			else if(it.value.equals("dimension") && it.groupName.equals("direction") ){
				getMovieDirectionDimension(wb, object_idArr, dataSource, projectRequirement, dataSourceMycatReputation, dt)
			}
			// 导演的好评率
			else if(it.value.equals("favorable_rate") && it.groupName.equals("direction") ){
				getMovieDirectionFavorableRate(wb, object_idArr,  dataSource, projectRequirement, dataSourceMycatReputation, dt)
			}
			// 编辑的提及量
			else if(it.value.equals("dimension") && it.groupName.equals("scriptwriter") ){
				getMovieScriptwriterDimension(wb, object_idArr, dataSource, projectRequirement, dataSourceMycatReputation, dt)
			}
			// 编辑的好评率
			else if(it.value.equals("favorable_rate") && it.groupName.equals("scriptwriter") ){
				getMovieScriptwriterFavorableRate(wb, object_idArr, dataSource, projectRequirement, dataSourceMycatReputation, dt)
			}
			// 演员的提及量
			else if(it.value.equals("dimension") && it.groupName.equals("comedienne") ){
				getMovieComedienneDimension(wb, object_idArr, dataSource, projectRequirement, dataSourceMycatReputation, dt)
			}
			// 演员的好评率
			else if(it.value.equals("favorable_rate") && it.groupName.equals("comedienne") ){
				getMovieComedienneFavorableRate(wb, object_idArr, dataSource, projectRequirement, dataSourceMycatReputation, dt)
			}
		}
		/*
		// 获得是否勾选了主创信息
		String writtenCountSql = "SELECT COUNT(*) AS mycount FROM basic_requiremen_codeRequirement_relation WHERE requeirment_id = ${projectRequiementId} AND codeRequeirment_id IN (SELECT id FROM code_localtion_requirement WHERE VALUE IN ('direction','scriptwriter','comedienne')) ";
		def myCount = dataSource_Iplay.firstRow(writtenCountSql);
		int writtenCount = myCount.myCount ;
		
		if(writtenCount>0){	//主创及表演评估
			int sta=0			//用于判断提及量或者好评率是否被选中
			Sheet Sheet = wb.createSheet("主创及表演评估");
			def index=0;
			Row Row = Sheet.createRow(index++);
			Cell cell1 = Row.createCell(0);
			Cell cell2 = Row.createCell(1);
			Cell cell3 = Row.createCell(2);
			Cell cell4 = Row.createCell(3);
			Cell cell5 = Row.createCell(4);
			Cell cell6 = Row.createCell(5);
			Cell cell7 = Row.createCell(6);
			Cell cell8 = Row.createCell(7);
			Cell cell9 = Row.createCell(8);
			
			cell1.setCellValue("电影ID");
			cell2.setCellValue("电影名称");
			cell3.setCellValue("明星ID");
			cell3.setCellValue("明星主创信息");
			cell4.setCellValue("姓名");
			cell5.setCellValue("维度");
			cell6.setCellValue("好评");
			cell7.setCellValue("差评");
			cell8.setCellValue("提及量(数字)/好评率(百分比)");
			cell9.setCellValue("上映日期");
			
			String sql1 = "SELECT  * FROM code_localtion_requirement WHERE id IN (SELECT codeRequeirment_id FROM basic_requiremen_codeRequirement_relation WHERE requeirment_id = ${projectRequiementId}) AND VALUE IN ('direction','scriptwriter','comedienne') AND groupName = 'written'"
			String sql2 = "SELECT  * FROM code_localtion_requirement WHERE id IN (SELECT codeRequeirment_id FROM basic_requiremen_codeRequirement_relation WHERE requeirment_id = ${projectRequiementId}) AND VALUE IN ('dimension','favorable_rate') AND groupName = 'written'"
			for(int i = 0 ; i <object_idArr.length;i++ ){
				
				String object_id = object_idArr[i];
				// 查询电影基本信息
				String select_sql = "select  * from basic_movie_info where id = ${object_id}"
				def movieInfo = dataSource.firstRow(select_sql);
				String movieName = movieInfo.name ;
				String moviePublishTime = movieInfo.publish_time ;
				
				dataSource_Iplay.rows(sql1).each {it1->
					if(it1.value.trim().equals("direction")){		//导演
						String s1="select a.artist_id,b.`name`dyName from basic_artist_movie a,basic_artist_info b where a.artist_id = b.id and a.relation=19 and a.movie_id = ${object_id}"
						def result1=dataSource.rows(s1)
						dataSource_Iplay.rows(sql2).each{it2->
							if(it2.value.trim().equals("dimension")){		//提及量
								sta=1
								result1.each {
									String s2="SELECT star_id,origin_type,publish_date,sum(positive_num) hp,sum(negative_num) cp,sum(positive_num)+sum(negative_num) tjl FROM `ireport_star_praise_statistic_day` where star_id ="+it.artist_id+" and publish_date>='${projectRequirement.begin_date}' and publish_date <='${projectRequirement.end_date}' GROUP BY origin_type,publish_date"
									def result2=dataSourceMycatReputation.rows(s2)
									result2.each { res->
										String s3="SELECT name FROM  basic_ent_category where cid="+res.origin_type
										def originName=dataSource.firstRow(s3).name
										Row = Sheet.createRow(index++);
										cell1 = Row.createCell(0);
										cell2 = Row.createCell(1);
										cell3 = Row.createCell(2);
										cell4 = Row.createCell(3);
										cell5 = Row.createCell(4);
										cell6 = Row.createCell(5);
										cell7 = Row.createCell(6);
										cell8 = Row.createCell(7);
										cell9 = Row.createCell(8);
										
										cell1.setCellValue(object_id);
										cell2.setCellValue(movieName);
										cell3.setCellValue(it.artist_id);
										cell3.setCellValue("导演");
										cell4.setCellValue(it.dyName);
										cell5.setCellValue(originName);
										cell6.setCellValue(res.hp);
										cell7.setCellValue(res.cp);
										cell8.setCellValue(res.tjl);
										cell9.setCellValue(dt.fmtDate(res.publish_date,"yyyy-MM-dd"));
									}
								}
							}else if(it2.value.trim().equals("favorable_rate")){		//好评率
								sta=1
								result1.each {
									String s2="SELECT star_id,origin_type,publish_date,sum(positive_num) hp,sum(negative_num) cp,CONCAT((sum(positive_num)/(sum(positive_num)+sum(negative_num)))*100,'%') hpl FROM `ireport_star_praise_statistic_day` where star_id ="+it.artist_id+" and publish_date>='${projectRequirement.begin_date}' and publish_date <='${projectRequirement.end_date}' GROUP BY origin_type,publish_date"
									def result2=dataSourceMycatReputation.rows(s2)
									result2.each { res->
										String s3="SELECT name FROM  basic_ent_category  where cid="+res.origin_type
										def originName=dataSource.firstRow(s3).name
										Row = Sheet.createRow(index++);
										cell1 = Row.createCell(0);
										cell2 = Row.createCell(1);
										cell3 = Row.createCell(2);
										cell4 = Row.createCell(3);
										cell5 = Row.createCell(4);
										cell6 = Row.createCell(5);
										cell7 = Row.createCell(6);
										cell8 = Row.createCell(7);
										cell9 = Row.createCell(8);
										
										cell1.setCellValue(object_id);
										cell2.setCellValue(movieName);
										cell3.setCellValue(it.artist_id);
										cell3.setCellValue("导演");
										cell4.setCellValue(it.dyName);
										cell5.setCellValue(originName);
										cell6.setCellValue(res.hp);
										cell7.setCellValue(res.cp);
										cell8.setCellValue(res.hpl);
										cell9.setCellValue(dt.fmtDate(res.publish_date,"yyyy-MM-dd"));
									}
								}
							}
						}
						//sta=0代表提及量或好评率没有被选中，这时需要把导演信息导出~
						if(sta==0){
							result1.each{
								Row = Sheet.createRow(index++);
								cell1 = Row.createCell(0);
								cell2 = Row.createCell(1);
								cell3 = Row.createCell(2);
								cell4 = Row.createCell(3);
								cell5 = Row.createCell(4);
								cell6 = Row.createCell(5);
								cell7 = Row.createCell(6);
								cell8 = Row.createCell(7);
								cell9 = Row.createCell(8);
								
								cell1.setCellValue(object_id);
								cell2.setCellValue(movieName);
								cell3.setCellValue(it.artist_id);
								cell3.setCellValue("导演");
								cell4.setCellValue(it.dyName);
								cell9.setCellValue(moviePublishTime);
							}
						}
					}
					else if(it1.value.trim().equals("scriptwriter")){		//编辑
						String s1="select a.artist_id,b.`name`dyName from basic_artist_movie a,basic_artist_info b where a.artist_id = b.id and a.relation=18 and a.movie_id = ${object_id}"
						def result1=dataSource.rows(s1)
						dataSource_Iplay.rows(sql2).each{it2->
							if(it2.value.trim().equals("dimension")){		//提及量
								sta=1
								result1.each {
									String s2="SELECT star_id,origin_type,publish_date,sum(positive_num) hp,sum(negative_num) cp,sum(positive_num)+sum(negative_num) tjl FROM `ireport_star_praise_statistic_day` where star_id ="+it.artist_id+" and publish_date>='${projectRequirement.begin_date}' and publish_date <='${projectRequirement.end_date}' GROUP BY origin_type,publish_date"
									def result2=dataSourceMycatReputation.rows(s2)
									result2.each { res->
										String s3="SELECT name FROM  basic_ent_category  where cid="+res.origin_type
										def originName=dataSource.firstRow(s3).name
										Row = Sheet.createRow(index++);
										cell1 = Row.createCell(0);
										cell2 = Row.createCell(1);
										cell3 = Row.createCell(2);
										cell4 = Row.createCell(3);
										cell5 = Row.createCell(4);
										cell6 = Row.createCell(5);
										cell7 = Row.createCell(6);
										cell8 = Row.createCell(7);
										cell9 = Row.createCell(8);
										
										cell1.setCellValue(object_id);
										cell2.setCellValue(movieName);
										cell3.setCellValue(it.artist_id);
										cell3.setCellValue("导演");
										cell4.setCellValue(it.dyName);
										cell5.setCellValue(originName);
										cell6.setCellValue(res.hp);
										cell7.setCellValue(res.cp);
										cell8.setCellValue(res.tjl);
										cell9.setCellValue(dt.fmtDate(res.publish_date,"yyyy-MM-dd"));
									}
								}
							}else if(it2.value.trim().equals("favorable_rate")){		//好评率
								sta=1
								result1.each {
									String s2="SELECT star_id,origin_type,publish_date,sum(positive_num) hp,sum(negative_num) cp,CONCAT((sum(positive_num)/(sum(positive_num)+sum(negative_num)))*100,'%') hpl FROM `ireport_star_praise_statistic_day` where star_id ="+it.artist_id+" and publish_date>='${projectRequirement.begin_date}' and publish_date <='${projectRequirement.end_date}' GROUP BY origin_type,publish_date"
									def result2=dataSourceMycatReputation.rows(s2)
									result2.each { res->
										String s3="SELECT name FROM  basic_ent_category  where cid="+res.origin_type
										def originName=dataSource.firstRow(s3).name
										Row = Sheet.createRow(index++);
										cell1 = Row.createCell(0);
										cell2 = Row.createCell(1);
										cell3 = Row.createCell(2);
										cell4 = Row.createCell(3);
										cell5 = Row.createCell(4);
										cell6 = Row.createCell(5);
										cell7 = Row.createCell(6);
										cell8 = Row.createCell(7);
										cell9 = Row.createCell(8);
										
										cell1.setCellValue(object_id);
										cell2.setCellValue(movieName);
										cell3.setCellValue(it.artist_id);
										cell3.setCellValue("编剧");
										cell4.setCellValue(it.dyName);
										cell5.setCellValue(originName);
										cell6.setCellValue(res.hp);
										cell7.setCellValue(res.cp);
										cell8.setCellValue(res.hpl);
										cell9.setCellValue(dt.fmtDate(res.publish_date,"yyyy-MM-dd"));
									}
								}
							}
						}
						
						//sta=0代表提及量或好评率没有被选中，这时需要把导演信息导出~
						if(sta==0){
							result1.each{
								Row = Sheet.createRow(index++);
								cell1 = Row.createCell(0);
								cell2 = Row.createCell(1);
								cell3 = Row.createCell(2);
								cell4 = Row.createCell(3);
								cell5 = Row.createCell(4);
								cell6 = Row.createCell(5);
								cell7 = Row.createCell(6);
								cell8 = Row.createCell(7);
								cell9 = Row.createCell(8);
								
								cell1.setCellValue(object_id);
								cell2.setCellValue(movieName);
								cell3.setCellValue(it.artist_id);
								cell3.setCellValue("编剧");
								cell4.setCellValue(it.dyName);
								cell9.setCellValue(moviePublishTime);
							}
						}
					}
					else if(it1.value.trim().equals("comedienne")){		//演员
						String s1="select a.artist_id,b.`name`dyName from basic_artist_movie a,basic_artist_info b where a.artist_id = b.id and a.relation=16 and a.movie_id = ${object_id}"
						def result1=dataSource.rows(s1)
						dataSource_Iplay.rows(sql2).each{it2->
							if(it2.value.trim().equals("dimension")){		//提及量
								sta=1
								result1.each {
									String s2="SELECT star_id,origin_type,publish_date,sum(positive_num) hp,sum(negative_num) cp,sum(positive_num)+sum(negative_num) tjl FROM `ireport_star_praise_statistic_day` where star_id ="+it.artist_id+" and publish_date>='${projectRequirement.begin_date}' and publish_date <='${projectRequirement.end_date}' GROUP BY origin_type,publish_date"
									def result2=dataSourceMycatReputation.rows(s2)
									result2.each { res->
										String s3="SELECT name FROM  basic_ent_category  where cid="+res.origin_type
										def originName=dataSource.firstRow(s3).name
										Row = Sheet.createRow(index++);
										cell1 = Row.createCell(0);
										cell2 = Row.createCell(1);
										cell3 = Row.createCell(2);
										cell4 = Row.createCell(3);
										cell5 = Row.createCell(4);
										cell6 = Row.createCell(5);
										cell7 = Row.createCell(6);
										cell8 = Row.createCell(7);
										cell9 = Row.createCell(8);
										
										cell1.setCellValue(object_id);
										cell2.setCellValue(movieName);
										cell3.setCellValue(it.artist_id);
										cell3.setCellValue("演员");
										cell4.setCellValue(it.dyName);
										cell5.setCellValue(originName);
										cell6.setCellValue(res.hp);
										cell7.setCellValue(res.cp);
										cell8.setCellValue(res.tjl);
										cell9.setCellValue(dt.fmtDate(res.publish_date,"yyyy-MM-dd"));
									}
								}
							}else if(it2.value.trim().equals("favorable_rate")){		//好评率
								sta=1
								result1.each {
									String s2="SELECT star_id,origin_type,publish_date,sum(positive_num) hp,sum(negative_num) cp,CONCAT((sum(positive_num)/(sum(positive_num)+sum(negative_num)))*100,'%') hpl FROM `ireport_star_praise_statistic_day` where star_id ="+it.artist_id+" and publish_date>='${projectRequirement.begin_date}' and publish_date <='${projectRequirement.end_date}' GROUP BY origin_type,publish_date"
									def result2=dataSourceMycatReputation.rows(s2)
									result2.each { res->
										String s3="SELECT name FROM  basic_ent_category  where cid="+res.origin_type
										def originName=dataSource.firstRow(s3).name
										Row = Sheet.createRow(index++);
										cell1 = Row.createCell(0);
										cell2 = Row.createCell(1);
										cell3 = Row.createCell(2);
										cell4 = Row.createCell(3);
										cell5 = Row.createCell(4);
										cell6 = Row.createCell(5);
										cell7 = Row.createCell(6);
										cell8 = Row.createCell(7);
										cell9 = Row.createCell(8);
										
										cell1.setCellValue(object_id);
										cell2.setCellValue(movieName);
										cell3.setCellValue(it.artist_id);
										cell3.setCellValue("演员");
										cell4.setCellValue(it.dyName);
										cell5.setCellValue(originName);
										cell6.setCellValue(res.hp);
										cell7.setCellValue(res.cp);
										cell8.setCellValue(res.hpl);
										cell9.setCellValue(dt.fmtDate(res.publish_date,"yyyy-MM-dd"));
									}
								}
							}
						}
						//sta=0代表提及量或好评率没有被选中，这时需要把导演信息导出~
						if(sta==0){
							result1.each{
								Row = Sheet.createRow(index++);
								cell1 = Row.createCell(0);
								cell2 = Row.createCell(1);
								cell3 = Row.createCell(2);
								cell4 = Row.createCell(3);
								cell5 = Row.createCell(4);
								cell6 = Row.createCell(5);
								cell7 = Row.createCell(6);
								cell8 = Row.createCell(7);
								cell9 = Row.createCell(8);
								
								cell1.setCellValue(object_id);
								cell2.setCellValue(movieName);
								cell3.setCellValue(it.artist_id);
								cell3.setCellValue("演员");
								cell4.setCellValue(it.dyName);
								cell9.setCellValue(moviePublishTime);
							}
						}
					}
				}
			}
		}
		*/
//		System.out.println("一共多少个sheet："+wb.getSheets().length)
		
		
		//将文件上传到服务器
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		wb.write(out);
		String[] uriArr;
		try {
			uriArr = iFastDfs.upload(out.toByteArray(),"xls",null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		String down_url = (uriArr[0]+"/"+uriArr[1]+"?attname="+projectRequirement.name+".xls");
		wb.close();
		out.close();
		// 将下载地址更新到数据库中
		int changeCount = dataSource_Iplay.executeUpdate("update basic_project_requirement_info set down_url = ${down_url},requirement_state = 3 where id = ${projectRequiementId}")
		println "提交生成excel并保存到数据库成功!${projectRequiementId}";
		
		System.out.println("耗时："+(System.currentTimeMillis() - beginTime));
		
		sendMailForProjectRequirement(projectRequiementId);
		
		return "生成成功!"
	}

	private getMovieDirectionDimension(SXSSFWorkbook wb, String[] object_idArr, Sql dataSource, projectRequirement, Sql dataSourceMycatReputation, DateTools dt) {
		Sheet Sheet = wb.createSheet("导演的提及量");
		def index=0;
		Row Row = Sheet.createRow(index++);
		Cell cell1 = Row.createCell(0);
		Cell cell2 = Row.createCell(1);
		Cell cell3 = Row.createCell(2);
		Cell cell4 = Row.createCell(3);
		Cell cell5 = Row.createCell(4);
		Cell cell6 = Row.createCell(5);
		Cell cell7 = Row.createCell(6);
		Cell cell8 = Row.createCell(7);
		Cell cell9 = Row.createCell(8);
		Cell cell10 = Row.createCell(9);

		cell1.setCellValue("电影ID");
		cell2.setCellValue("电影名称");
		cell3.setCellValue("明星ID");
		cell3.setCellValue("明星主创信息");
		cell4.setCellValue("姓名");
		cell5.setCellValue("维度");
		cell6.setCellValue("好评");
		cell7.setCellValue("差评");
		cell8.setCellValue("中评");
		cell9.setCellValue("提及量(数字)");
		cell10.setCellValue("上映日期");
		
		for(int i = 0 ; i <object_idArr.length;i++ ){
			String object_id = object_idArr[i];
			// 查询电影基本信息
			String select_sql = "select  * from basic_movie_info where id = ${object_id}"
			def movieInfo = dataSource.firstRow(select_sql);
			String movieName = movieInfo.name ;
			String moviePublishTime = movieInfo.publish_time ;
	
			String s1="select a.artist_id,b.`name`dyName from basic_artist_movie a,basic_artist_info b where a.artist_id = b.id and a.relation=19 and a.movie_id = ${object_id}"
			def result1=dataSource.rows(s1).each {
				String s2="SELECT star_id,evaluate_type,publish_date,sum(positive_num) hp,sum(negative_num) cp,sum(neutral_num) zp,sum(positive_num)+sum(negative_num)+sum(neutral_num) tjl FROM `ireport_star_praise_statistic_day` where star_id ="+it.artist_id+" and publish_date>='${projectRequirement.begin_date}' and publish_date <='${projectRequirement.end_date}' GROUP BY evaluate_type,publish_date"
				def result2=dataSourceMycatReputation.rows(s2)
				result2.each { res->
					String s3="SELECT name FROM  basic_ent_category where cid="+res.evaluate_type
					def originName=dataSource.firstRow(s3).name
					Row = Sheet.createRow(index++);
					cell1 = Row.createCell(0);
					cell2 = Row.createCell(1);
					cell3 = Row.createCell(2);
					cell4 = Row.createCell(3);
					cell5 = Row.createCell(4);
					cell6 = Row.createCell(5);
					cell7 = Row.createCell(6);
					cell8 = Row.createCell(7);
					cell9 = Row.createCell(8);
					cell10 = Row.createCell(9);
	
					cell1.setCellValue(object_id);
					cell2.setCellValue(movieName);
					cell3.setCellValue(it.artist_id);
					cell3.setCellValue("导演");
					cell4.setCellValue(it.dyName);
					cell5.setCellValue(originName);
					cell6.setCellValue(res.hp);
					cell7.setCellValue(res.cp);
					cell8.setCellValue(res.zp);
					cell9.setCellValue(res.tjl);
					cell10.setCellValue(dt.fmtDate(res.publish_date,"yyyy-MM-dd"));
				}
			}
		}
	}

	private getMovieDirectionFavorableRate(SXSSFWorkbook wb, String[] object_idArr, Sql dataSource, projectRequirement, Sql dataSourceMycatReputation, DateTools dt) {
		Sheet Sheet = wb.createSheet("导演的好评率");
		def index=0;
		Row Row = Sheet.createRow(index++);
		Cell cell1 = Row.createCell(0);
		Cell cell2 = Row.createCell(1);
		Cell cell3 = Row.createCell(2);
		Cell cell4 = Row.createCell(3);
		Cell cell5 = Row.createCell(4);
		Cell cell6 = Row.createCell(5);
		Cell cell7 = Row.createCell(6);
		Cell cell8 = Row.createCell(7);
		Cell cell9 = Row.createCell(8);
		Cell cell10 = Row.createCell(9);

		cell1.setCellValue("电影ID");
		cell2.setCellValue("电影名称");
		cell3.setCellValue("明星ID");
		cell3.setCellValue("明星主创信息");
		cell4.setCellValue("姓名");
		cell5.setCellValue("维度");
		cell6.setCellValue("好评");
		cell7.setCellValue("差评");
		cell8.setCellValue("中评");
		cell9.setCellValue("好评率(百分比)");
		cell10.setCellValue("上映日期");
		for(int i = 0 ; i <object_idArr.length;i++ ){
			String object_id = object_idArr[i];
			// 查询电影基本信息
			String select_sql = "select  * from basic_movie_info where id = ${object_id}"
			def movieInfo = dataSource.firstRow(select_sql);
			String movieName = movieInfo.name ;
			String moviePublishTime = movieInfo.publish_time ;
	
			String s1="select a.artist_id,b.`name`dyName from basic_artist_movie a,basic_artist_info b where a.artist_id = b.id and a.relation=19 and a.movie_id = ${object_id}"
			def result1=dataSource.rows(s1).each {
				String s2="SELECT star_id,evaluate_type,publish_date,sum(positive_num) hp,sum(negative_num) cp,sum(neutral_num) zp,CONCAT((sum(positive_num)/(sum(positive_num)+sum(negative_num)))*100,'%') hpl FROM `ireport_star_praise_statistic_day` where star_id ="+it.artist_id+" and publish_date>='${projectRequirement.begin_date}' and publish_date <='${projectRequirement.end_date}' GROUP BY evaluate_type,publish_date"
				def result2=dataSourceMycatReputation.rows(s2)
				result2.each { res->
					String s3="SELECT name FROM  basic_ent_category where cid="+res.evaluate_type
					def originName=dataSource.firstRow(s3).name
					Row = Sheet.createRow(index++);
					cell1 = Row.createCell(0);
					cell2 = Row.createCell(1);
					cell3 = Row.createCell(2);
					cell4 = Row.createCell(3);
					cell5 = Row.createCell(4);
					cell6 = Row.createCell(5);
					cell7 = Row.createCell(6);
					cell8 = Row.createCell(7);
					cell9 = Row.createCell(8);
					cell10 = Row.createCell(9);
	
					cell1.setCellValue(object_id);
					cell2.setCellValue(movieName);
					cell3.setCellValue(it.artist_id);
					cell3.setCellValue("导演");
					cell4.setCellValue(it.dyName);
					cell5.setCellValue(originName);
					cell6.setCellValue(res.hp);
					cell7.setCellValue(res.cp);
					cell8.setCellValue(res.zp);
					cell9.setCellValue(res.hpl);
					cell10.setCellValue(dt.fmtDate(res.publish_date,"yyyy-MM-dd"));
				}
			}
		}
	}

	private getMovieScriptwriterDimension(SXSSFWorkbook wb, String[] object_idArr, Sql dataSource, projectRequirement, Sql dataSourceMycatReputation, DateTools dt) {
		int sta=0			//用于判断提及量或者好评率是否被选中
		Sheet Sheet = wb.createSheet("编辑的提及量");
		def index=0;
		Row Row = Sheet.createRow(index++);
		Cell cell1 = Row.createCell(0);
		Cell cell2 = Row.createCell(1);
		Cell cell3 = Row.createCell(2);
		Cell cell4 = Row.createCell(3);
		Cell cell5 = Row.createCell(4);
		Cell cell6 = Row.createCell(5);
		Cell cell7 = Row.createCell(6);
		Cell cell8 = Row.createCell(7);
		Cell cell9 = Row.createCell(8);
		Cell cell10 = Row.createCell(9);

		cell1.setCellValue("电影ID");
		cell2.setCellValue("电影名称");
		cell3.setCellValue("明星ID");
		cell3.setCellValue("明星主创信息");
		cell4.setCellValue("姓名");
		cell5.setCellValue("维度");
		cell6.setCellValue("好评");
		cell7.setCellValue("差评");
		cell8.setCellValue("中评");
		cell9.setCellValue("提及量(数字)");
		cell10.setCellValue("上映日期");
		for(int i = 0 ; i <object_idArr.length;i++ ){
			String object_id = object_idArr[i];
			// 查询电影基本信息
			String select_sql = "select  * from basic_movie_info where id = ${object_id}"
			def movieInfo = dataSource.firstRow(select_sql);
			String movieName = movieInfo.name ;
			String moviePublishTime = movieInfo.publish_time ;
	
			String s1="select a.artist_id,b.`name`dyName from basic_artist_movie a,basic_artist_info b where a.artist_id = b.id and a.relation=18 and a.movie_id = ${object_id}"
			def result1=dataSource.rows(s1).each {
				String s2="SELECT star_id,evaluate_type,publish_date,sum(positive_num) hp,sum(negative_num) cp,sum(neutral_num) zp,sum(positive_num)+sum(negative_num)+sum(neutral_num) tjl FROM `ireport_star_praise_statistic_day` where star_id ="+it.artist_id+" and publish_date>='${projectRequirement.begin_date}' and publish_date <='${projectRequirement.end_date}' GROUP BY evaluate_type,publish_date"
				def result2=dataSourceMycatReputation.rows(s2)
				result2.each { res->
					String s3="SELECT name FROM  basic_ent_category where cid="+res.evaluate_type
					def originName=dataSource.firstRow(s3).name
					Row = Sheet.createRow(index++);
					cell1 = Row.createCell(0);
					cell2 = Row.createCell(1);
					cell3 = Row.createCell(2);
					cell4 = Row.createCell(3);
					cell5 = Row.createCell(4);
					cell6 = Row.createCell(5);
					cell7 = Row.createCell(6);
					cell8 = Row.createCell(7);
					cell9 = Row.createCell(8);
					cell10 = Row.createCell(9);
	
					cell1.setCellValue(object_id);
					cell2.setCellValue(movieName);
					cell3.setCellValue(it.artist_id);
					cell3.setCellValue("编剧");
					cell4.setCellValue(it.dyName);
					cell5.setCellValue(originName);
					cell6.setCellValue(res.hp);
					cell7.setCellValue(res.cp);
					cell8.setCellValue(res.zp);
					cell9.setCellValue(res.tjl);
					cell10.setCellValue(dt.fmtDate(res.publish_date,"yyyy-MM-dd"));
				}
			}
		}
	}

	private getMovieScriptwriterFavorableRate(SXSSFWorkbook wb, String[] object_idArr, Sql dataSource, projectRequirement, Sql dataSourceMycatReputation, DateTools dt) {
		Sheet Sheet = wb.createSheet("编辑的好评率");
		def index=0;
		Row Row = Sheet.createRow(index++);
		Cell cell1 = Row.createCell(0);
		Cell cell2 = Row.createCell(1);
		Cell cell3 = Row.createCell(2);
		Cell cell4 = Row.createCell(3);
		Cell cell5 = Row.createCell(4);
		Cell cell6 = Row.createCell(5);
		Cell cell7 = Row.createCell(6);
		Cell cell8 = Row.createCell(7);
		Cell cell9 = Row.createCell(8);
		Cell cell10 = Row.createCell(9);

		cell1.setCellValue("电影ID");
		cell2.setCellValue("电影名称");
		cell3.setCellValue("明星ID");
		cell3.setCellValue("明星主创信息");
		cell4.setCellValue("姓名");
		cell5.setCellValue("维度");
		cell6.setCellValue("好评");
		cell7.setCellValue("差评");
		cell8.setCellValue("中评");
		cell9.setCellValue("好评率(百分比)");
		cell10.setCellValue("上映日期");
		for(int i = 0 ; i <object_idArr.length;i++ ){
			String object_id = object_idArr[i];
			// 查询电影基本信息
			String select_sql = "select  * from basic_movie_info where id = ${object_id}"
			def movieInfo = dataSource.firstRow(select_sql);
			String movieName = movieInfo.name ;
			String moviePublishTime = movieInfo.publish_time ;
	
			String s1="select a.artist_id,b.`name`dyName from basic_artist_movie a,basic_artist_info b where a.artist_id = b.id and a.relation=18 and a.movie_id = ${object_id}"
			def result1=dataSource.rows(s1).each {
				String s2="SELECT star_id,evaluate_type,publish_date,sum(positive_num) hp,sum(negative_num) cp,sum(neutral_num) zp,CONCAT((sum(positive_num)/(sum(positive_num)+sum(negative_num)))*100,'%') hpl FROM `ireport_star_praise_statistic_day` where star_id ="+it.artist_id+" and publish_date>='${projectRequirement.begin_date}' and publish_date <='${projectRequirement.end_date}' GROUP BY evaluate_type,publish_date"
				def result2=dataSourceMycatReputation.rows(s2)
				result2.each { res->
					String s3="SELECT name FROM  basic_ent_category where cid="+res.evaluate_type
					def originName=dataSource.firstRow(s3).name
					Row = Sheet.createRow(index++);
					cell1 = Row.createCell(0);
					cell2 = Row.createCell(1);
					cell3 = Row.createCell(2);
					cell4 = Row.createCell(3);
					cell5 = Row.createCell(4);
					cell6 = Row.createCell(5);
					cell7 = Row.createCell(6);
					cell8 = Row.createCell(7);
					cell9 = Row.createCell(8);
					cell10 = Row.createCell(9);
	
					cell1.setCellValue(object_id);
					cell2.setCellValue(movieName);
					cell3.setCellValue(it.artist_id);
					cell3.setCellValue("编剧");
					cell4.setCellValue(it.dyName);
					cell5.setCellValue(originName);
					cell6.setCellValue(res.hp);
					cell7.setCellValue(res.cp);
					cell8.setCellValue(res.zp);
					cell9.setCellValue(res.hpl);
					cell10.setCellValue(dt.fmtDate(res.publish_date,"yyyy-MM-dd"));
				}
			}
		}
	}

	private getMovieComedienneDimension(SXSSFWorkbook wb, String[] object_idArr, Sql dataSource, projectRequirement, Sql dataSourceMycatReputation, DateTools dt) {
		Sheet Sheet = wb.createSheet("演员的提及量");
		def index=0;
		Row Row = Sheet.createRow(index++);
		Cell cell1 = Row.createCell(0);
		Cell cell2 = Row.createCell(1);
		Cell cell3 = Row.createCell(2);
		Cell cell4 = Row.createCell(3);
		Cell cell5 = Row.createCell(4);
		Cell cell6 = Row.createCell(5);
		Cell cell7 = Row.createCell(6);
		Cell cell8 = Row.createCell(7);
		Cell cell9 = Row.createCell(8);
		Cell cell10 = Row.createCell(9);

		cell1.setCellValue("电影ID");
		cell2.setCellValue("电影名称");
		cell3.setCellValue("明星ID");
		cell3.setCellValue("明星主创信息");
		cell4.setCellValue("姓名");
		cell5.setCellValue("维度");
		cell6.setCellValue("好评");
		cell7.setCellValue("差评");
		cell8.setCellValue("中评");
		cell9.setCellValue("提及量(数字)");
		cell10.setCellValue("上映日期");
		
		for(int i = 0 ; i <object_idArr.length;i++ ){
			String object_id = object_idArr[i];
			// 查询电影基本信息
			String select_sql = "select  * from basic_movie_info where id = ${object_id}"
			def movieInfo = dataSource.firstRow(select_sql);
			String movieName = movieInfo.name ;
			String moviePublishTime = movieInfo.publish_time ;
	
			String s1="select a.artist_id,b.`name`dyName from basic_artist_movie a,basic_artist_info b where a.artist_id = b.id and a.relation=16 and a.movie_id = ${object_id}"
			def result1=dataSource.rows(s1).each {
				String s2="SELECT star_id,evaluate_type,publish_date,sum(positive_num) hp,sum(negative_num) cp,sum(neutral_num) zp,sum(positive_num)+sum(negative_num)+sum(neutral_num) tjl FROM `ireport_star_praise_statistic_day` where star_id ="+it.artist_id+" and publish_date>='${projectRequirement.begin_date}' and publish_date <='${projectRequirement.end_date}' GROUP BY evaluate_type,publish_date"
				def result2=dataSourceMycatReputation.rows(s2)
				result2.each { res->
					String s3="SELECT name FROM  basic_ent_category where cid="+res.evaluate_type
					def originName=dataSource.firstRow(s3).name
					Row = Sheet.createRow(index++);
					cell1 = Row.createCell(0);
					cell2 = Row.createCell(1);
					cell3 = Row.createCell(2);
					cell4 = Row.createCell(3);
					cell5 = Row.createCell(4);
					cell6 = Row.createCell(5);
					cell7 = Row.createCell(6);
					cell8 = Row.createCell(7);
					cell9 = Row.createCell(8);
					cell10 = Row.createCell(9);
	
					cell1.setCellValue(object_id);
					cell2.setCellValue(movieName);
					cell3.setCellValue(it.artist_id);
					cell3.setCellValue("演员");
					cell4.setCellValue(it.dyName);
					cell5.setCellValue(originName);
					cell6.setCellValue(res.hp);
					cell7.setCellValue(res.cp);
					cell8.setCellValue(res.zp);
					cell9.setCellValue(res.tjl);
					cell10.setCellValue(dt.fmtDate(res.publish_date,"yyyy-MM-dd"));
				}
			}
		}
	}

	private getMovieComedienneFavorableRate(SXSSFWorkbook wb, String[] object_idArr, Sql dataSource, projectRequirement, Sql dataSourceMycatReputation, DateTools dt) {
		Sheet Sheet = wb.createSheet("演员的好评率");
		def index=0;
		Row Row = Sheet.createRow(index++);
		Cell cell1 = Row.createCell(0);
		Cell cell2 = Row.createCell(1);
		Cell cell3 = Row.createCell(2);
		Cell cell4 = Row.createCell(3);
		Cell cell5 = Row.createCell(4);
		Cell cell6 = Row.createCell(5);
		Cell cell7 = Row.createCell(6);
		Cell cell8 = Row.createCell(7);
		Cell cell9 = Row.createCell(8);
		Cell cell10 = Row.createCell(9);

		cell1.setCellValue("电影ID");
		cell2.setCellValue("电影名称");
		cell3.setCellValue("明星ID");
		cell3.setCellValue("明星主创信息");
		cell4.setCellValue("姓名");
		cell5.setCellValue("维度");
		cell6.setCellValue("好评");
		cell7.setCellValue("差评");
		cell8.setCellValue("中评");
		cell9.setCellValue("好评率(百分比)");
		cell10.setCellValue("上映日期");
		for(int i = 0 ; i <object_idArr.length;i++ ){

			String object_id = object_idArr[i];
			// 查询电影基本信息
			String select_sql = "select  * from basic_movie_info where id = ${object_id}"
			def movieInfo = dataSource.firstRow(select_sql);
			String movieName = movieInfo.name ;
			String moviePublishTime = movieInfo.publish_time ;
	
			String s1="select a.artist_id,b.`name`dyName from basic_artist_movie a,basic_artist_info b where a.artist_id = b.id and a.relation=16 and a.movie_id = ${object_id}"
			def result1=dataSource.rows(s1).each {
				String s2="SELECT star_id,evaluate_type,publish_date,sum(positive_num) hp,sum(negative_num) cp,sum(neutral_num) zp,CONCAT((sum(positive_num)/(sum(positive_num)+sum(negative_num)))*100,'%') hpl FROM `ireport_star_praise_statistic_day` where star_id ="+it.artist_id+" and publish_date>='${projectRequirement.begin_date}' and publish_date <='${projectRequirement.end_date}' GROUP BY evaluate_type,publish_date"
				def result2=dataSourceMycatReputation.rows(s2)
				result2.each { res->
					String s3="SELECT name FROM  basic_ent_category where cid="+res.evaluate_type
					def originName=dataSource.firstRow(s3).name
					Row = Sheet.createRow(index++);
					cell1 = Row.createCell(0);
					cell2 = Row.createCell(1);
					cell3 = Row.createCell(2);
					cell4 = Row.createCell(3);
					cell5 = Row.createCell(4);
					cell6 = Row.createCell(5);
					cell7 = Row.createCell(6);
					cell8 = Row.createCell(7);
					cell9 = Row.createCell(8);
					cell10 = Row.createCell(9);
	
					cell1.setCellValue(object_id);
					cell2.setCellValue(movieName);
					cell3.setCellValue(it.artist_id);
					cell3.setCellValue("演员");
					cell4.setCellValue(it.dyName);
					cell5.setCellValue(originName);
					cell6.setCellValue(res.hp);
					cell7.setCellValue(res.cp);
					cell8.setCellValue(res.zp);
					cell9.setCellValue(res.hpl);
					cell10.setCellValue(dt.fmtDate(res.publish_date,"yyyy-MM-dd"));
				}
			}
		}
	}

	private getMoviePublicInfluenceSheet(SXSSFWorkbook wb, groovy.sql.GroovyRowResult it, String[] object_idArr, Sql dataSource,Sql dataSource1, projectRequirement, DateTools dt) {
		Sheet Sheet = wb.createSheet(it.display_text)
		int index = 0 ;
		Row Row = Sheet.createRow(index++);
		Cell cell1 = Row.createCell(0);
		Cell cell2 = Row.createCell(1);
		Cell cell3 = Row.createCell(2);
		Cell cell4 = Row.createCell(3);
		cell1.setCellValue("电影ID");
		cell2.setCellValue("电影名称");
		cell3.setCellValue("公众影响力");
		cell4.setCellValue("日期");
		for(int i = 0 ; i <object_idArr.length;i++ ){

			String object_id = object_idArr[i];
			// 查询电影基本信息
			String select_sql = "select  * from basic_movie_info where id = ${object_id}"
			def movieInfo = dataSource.firstRow(select_sql);
			String movieName = movieInfo.name

			// 获得电影公众影响力
			String all_amount_sql = "SELECT hot_rate,record_date FROM domain_movie_public_hot_records where movie_id = ${object_id} and record_date >= '${projectRequirement.begin_date}' and record_date <= '${projectRequirement.end_date}'" ;
			dataSource1.rows(all_amount_sql).each { movie ->
				Row = Sheet.createRow(index++);
				cell1 = Row.createCell(0)
				cell2 = Row.createCell(1);
				cell3 = Row.createCell(2);
				cell4 = Row.createCell(3);

				cell1.setCellValue(object_id);
				cell2.setCellValue(movieName);
				cell3.setCellValue(movie.hot_rate);
				cell4.setCellValue(dt.fmtDate(movie.record_date,"yyyy-MM-dd"));
			}
		}
	}

	private getMovieMediaAttentionSheet(SXSSFWorkbook wb, groovy.sql.GroovyRowResult it, String[] object_idArr, Sql dataSource, Sql dataSource1, projectRequirement, DateTools dt) {
		Sheet Sheet = wb.createSheet(it.display_text)
		int index = 0 ;
		Row Row = Sheet.createRow(index++);
		Cell cell1 = Row.createCell(0);
		Cell cell2 = Row.createCell(1);
		Cell cell3 = Row.createCell(2);
		Cell cell4 = Row.createCell(3);
		cell1.setCellValue("电影ID");
		cell2.setCellValue("电影名称");
		cell3.setCellValue("媒体关注度");
		cell4.setCellValue("日期");
		for(int i = 0 ; i <object_idArr.length;i++ ){

			String object_id = object_idArr[i];
			// 查询电影基本信息
			String select_sql = "select  * from basic_movie_info where id = ${object_id}"
			def movieInfo = dataSource.firstRow(select_sql);
			String movieName = movieInfo.name

			// 获得电影票房信息
			String all_amount_sql = "SELECT hot_rate,record_date FROM domain_movie_media_hot_records where movie_id = ${object_id} and record_date >= '${projectRequirement.begin_date}' and record_date <= '${projectRequirement.end_date}'" ;
			dataSource1.rows(all_amount_sql).each { movie ->
				Row = Sheet.createRow(index++);
				cell1 = Row.createCell(0)
				cell2 = Row.createCell(1);
				cell3 = Row.createCell(2);
				cell4 = Row.createCell(3);

				cell1.setCellValue(object_id);
				cell2.setCellValue(movieName);
				cell3.setCellValue(movie.hot_rate);
				cell4.setCellValue(dt.fmtDate(movie.record_date,"yyyy-MM-dd"));
			}
		}
	}
	
	/**
	 * 电影网络总播放量
	 * @param wb
	 * @param it
	 * @param object_idArr
	 * @param dataSource
	 * @param dataSourceCrawl
	 * @param projectRequirement
	 * @return
	 */
	private getMovieAllAmountSheet(SXSSFWorkbook wb, groovy.sql.GroovyRowResult it, String[] object_idArr, Sql dataSource, Sql dataSourceCrawl, projectRequirement) {
		System.out.println("生成网站播放量");
		Sheet Sheet = wb.createSheet(it.display_text);
		DateTools dt=new DateTools()
		int index = 0 ;
		Row Row = Sheet.createRow(index++)
		Cell cell1 = Row.createCell(0);
		Cell cell2 = Row.createCell(1);
		Cell cell3 = Row.createCell(2);
		Cell cell4 = Row.createCell(3);
		cell1.setCellValue("电影ID");
		cell2.setCellValue("电影名称");
		cell3.setCellValue("网络播放量");
		cell4.setCellValue("日期");
		
		int days = 0;
		try{
			String dayStr = dt.cntTimeDifference(projectRequirement.begin_date as String,projectRequirement.end_date as String,"yyyy-MM-dd","d")
			days = Integer.parseInt(dayStr);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		for(int i = 0 ; i <object_idArr.length;i++ ){

			String object_id = object_idArr[i];
			// 查询电影基本信息
			String select_sql = "select  * from basic_movie_info where id = ${object_id}"
			def movieInfo = dataSource.firstRow(select_sql);
			String movieName = movieInfo.name
			def movieYear = movieInfo.year ;
			
			for(int j = 0 ; j < days ; j++){
				def yesterdayNum = 0	//前一天的播放量
				SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd")
				Calendar today = Calendar.getInstance();
				today.setTime(format.parse(projectRequirement.begin_date as String));
				today.add(Calendar.DAY_OF_MONTH,j);
				Calendar yesterday =Calendar.getInstance();		//前一天
				yesterday.setTime(format.parse(projectRequirement.begin_date as String));
				yesterday.add(Calendar.DAY_OF_MONTH,j-1);
				def source_domain ;
				
				def margin = 0 ;
				
				// 获得总表中的网络播放量(增量)
				String all_amount_sql = "SELECT SUM(a.play_num) play_num,DATE_FORMAT(max(a.update_time),'%Y-%m-%d') maxDate,source_domain FROM (SELECT b.id,play_num ,update_time,source_domain FROM domain_iplay_movie_stat_info a,basic_iplay_movie_info b WHERE b.id=a.iplay_id and b.movie_name = '${movieName}' and b.publish_date = ${movieYear} AND a.play_num>0 and DATE_FORMAT(a.update_time,'%Y-%m-%d')>='"+format.format(yesterday.getTime())+"' and DATE_FORMAT(a.update_time,'%Y-%m-%d')<='"+format.format(today.getTime())+"' ORDER BY a.update_time DESC ) a GROUP BY source_domain,DATE_FORMAT(a.update_time,'%Y-%m-%d') order by source_domain, a.update_time" ;
				dataSourceCrawl.rows(all_amount_sql).each { movie ->
					
					if(format.format(yesterday.getTime()).equals(movie.maxDate)){	// 昨天的播放量
						yesterdayNum=movie.play_num;
						source_domain = movie.source_domain ;
					}else if(format.format(today.getTime()).equals(movie.maxDate)){	// 今天的播放量
						if(source_domain!=null && source_domain.equals(movie.source_domain)){
							margin += (movie.play_num-yesterdayNum) ;
						}
						else{
							margin += movie.play_num ;
						}
					}
				}
					
				Row = Sheet.createRow(index++);
				cell1 = Row.createCell(0)
				cell2 = Row.createCell(1);
				cell3 = Row.createCell(2);
				cell4 = Row.createCell(3);

				cell1.setCellValue(object_id);
				cell2.setCellValue(movieName);
				cell3.setCellValue(margin);
				cell4.setCellValue(format.format(yesterday.getTime()));
			}
		}
	}
	
	/**
	 * 分渠道网站播放量
	 * @param wb
	 * @param it
	 * @param object_idArr
	 * @param dataSource
	 * @param dataSourceCrawl
	 * @param projectRequirement
	 * @return
	 */
	private getMovieWebAmountSheet(SXSSFWorkbook wb, groovy.sql.GroovyRowResult it, String[] object_idArr, Sql dataSource, Sql dataSourceCrawl, projectRequirement) {
		System.out.println("生成分渠道网站播放量");
		Sheet Sheet = wb.createSheet(it.display_text)
		DateTools dt=new DateTools()
		int index = 0 ;
		Row Row = Sheet.createRow(index++);
		Cell cell1 = Row.createCell(0);
		Cell cell2 = Row.createCell(1);
		Cell cell3 = Row.createCell(2);
		Cell cell4 = Row.createCell(3);
		Cell cell5 = Row.createCell(4);
		cell1.setCellValue("电影ID");
		cell2.setCellValue("电影名称");
		cell3.setCellValue("网站");
		cell4.setCellValue("网络播放量");
		cell5.setCellValue("日期");
		
		int days = 0;
		try{
			String dayStr = dt.cntTimeDifference(projectRequirement.begin_date as String,projectRequirement.end_date as String,"yyyy-MM-dd","d")
			days = Integer.parseInt(dayStr);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		for(int i = 0 ; i <object_idArr.length;i++ ){

			String object_id = object_idArr[i];
			// 查询电影基本信息
			String select_sql = "select  * from basic_movie_info where id = " + object_id
			def movieInfo = dataSource.firstRow(select_sql);
			String movieName = movieInfo.name
			def movieYear = movieInfo.year ;

			// 获得电影票房信息
			for(int j = 0 ; j < days ; j++){
				def yesterdayNum = 0	//前一天的播放量
				SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd")
				Calendar today = Calendar.getInstance();
				today.setTime(format.parse(projectRequirement.begin_date as String));
				today.add(Calendar.DAY_OF_MONTH,j);
				Calendar yesterday =Calendar.getInstance();		//前一天
				yesterday.setTime(format.parse(projectRequirement.begin_date as String));
				yesterday.add(Calendar.DAY_OF_MONTH,j-1);
				def source_domain ;
				
				
				def margin = 0 ;
				// 获得总表中的网络播放量(增量)
				String all_amount_sql = "SELECT SUM(a.play_num) play_num,DATE_FORMAT(max(a.update_time),'%Y-%m-%d') maxDate,source_domain FROM (SELECT b.id,play_num ,update_time,source_domain FROM domain_iplay_movie_stat_info a,basic_iplay_movie_info b WHERE b.id=a.iplay_id and b.movie_name = '"+movieName+"' and b.publish_date = "+movieYear+" AND a.play_num>0 and DATE_FORMAT(a.update_time,'%Y-%m-%d')>='"+format.format(yesterday.getTime())+"' and DATE_FORMAT(a.update_time,'%Y-%m-%d')<='"+format.format(today.getTime())+"' ORDER BY a.update_time DESC ) a GROUP BY source_domain,DATE_FORMAT(a.update_time,'%Y-%m-%d') order by source_domain, a.update_time" ;
				dataSourceCrawl.rows(all_amount_sql).each { movie ->
					
					if(format.format(yesterday.getTime()).equals(movie.maxDate)){	// 昨天的播放量
						yesterdayNum=movie.play_num;
						source_domain = movie.source_domain ;
					}else if(format.format(today.getTime()).equals(movie.maxDate)){	// 今天的播放量
						if(source_domain!=null && source_domain.equals(movie.source_domain)){
							margin = (movie.play_num-yesterdayNum) ;
						}
						else{
							margin = movie.play_num ;
						}
						
						Row = Sheet.createRow(index++);
						cell1 = Row.createCell(0)
						cell2 = Row.createCell(1);
						cell3 = Row.createCell(2);
						cell4 = Row.createCell(3);
						cell5 = Row.createCell(4);
		
						cell1.setCellValue(object_id);
						cell2.setCellValue(movieName);
						cell3.setCellValue(movie.source_domain);
						cell4.setCellValue(margin);
						cell5.setCellValue(format.format(yesterday.getTime()));
					}
				}
					
			}
		}
	}

	private getMovieTotalPeopleSheet(SXSSFWorkbook wb, groovy.sql.GroovyRowResult it, String[] object_idArr, Sql dataSource, Sql dataSourceCrawl, projectRequirement) {
		Sheet Sheet = wb.createSheet(it.display_text)
		int index = 0 ;
		Row Row = Sheet.createRow(index++);
		Cell cell1 = Row.createCell(0);
		Cell cell2 = Row.createCell(1);
		Cell cell3 = Row.createCell(2);
		Cell cell4 = Row.createCell(3);
		cell1.setCellValue("电影ID");
		cell2.setCellValue("电影名称");
		cell3.setCellValue("人次");
		cell4.setCellValue("日期");
		for(int i = 0 ; i <object_idArr.length;i++ ){

			String object_id = object_idArr[i];
			// 查询电影基本信息
			String select_sql = "select  * from basic_movie_info where id = " + object_id
			def movieInfo = dataSource.firstRow(select_sql);
			String movieName = movieInfo.name
			def publishTime = movieInfo.publish_time ;

			// 获得电影的film_code
			def boxOfficeMovie = dataSourceCrawl.firstRow("SELECT * FROM  ent_crawl.`boxOffice_movie` WHERE film_name = '"+movieName+"' AND release_date = '"+publishTime+"'");
			String filmCode = boxOfficeMovie?.film_code ;

			// 获得电影票房信息
			String all_amount_sql = "SELECT SUM(total_people) AS total_people,boxOffice_date FROM boxOffice_cinema_movie_relation where film_code = '"+filmCode+"' and boxOffice_date >= '"+projectRequirement.begin_date+"' and boxOffice_date <= '"+projectRequirement.end_date+"' group by boxOffice_date" ;
			dataSourceCrawl.rows(all_amount_sql).each { movie ->
				Row = Sheet.createRow(index++);
				cell1 = Row.createCell(0)
				cell2 = Row.createCell(1);
				cell3 = Row.createCell(2);
				cell4 = Row.createCell(3);

				cell1.setCellValue(object_id);
				cell2.setCellValue(movieName);
				cell3.setCellValue(movie.total_people);
				cell4.setCellValue(movie.boxOffice_date);
			}
		}
	}

	private getMovieTotoalSeasonSheet(SXSSFWorkbook wb, groovy.sql.GroovyRowResult it, String[] object_idArr, Sql dataSource, Sql dataSourceCrawl, projectRequirement) {
		Sheet Sheet = wb.createSheet(it.display_text)
		int index = 0 ;
		Row Row = Sheet.createRow(index++);
		Cell cell1 = Row.createCell(0);
		Cell cell2 = Row.createCell(1);
		Cell cell3 = Row.createCell(2);
		Cell cell4 = Row.createCell(3);
		cell1.setCellValue("电影ID");
		cell2.setCellValue("电影名称");
		cell3.setCellValue("场次");
		cell4.setCellValue("日期");
		for(int i = 0 ; i <object_idArr.length;i++ ){

			String object_id = object_idArr[i];
			// 查询电影基本信息
			String select_sql = "select  * from basic_movie_info where id = " + object_id
			def movieInfo = dataSource.firstRow(select_sql);
			String movieName = movieInfo.name
			def publishTime = movieInfo.publish_time ;

			// 获得电影的film_code
			def boxOfficeMovie = dataSourceCrawl.firstRow("SELECT * FROM  ent_crawl.`boxOffice_movie` WHERE film_name = '"+movieName+"' AND release_date = '"+publishTime+"'");
			String filmCode = boxOfficeMovie?.film_code ;

			// 获得电影票房信息
			String all_amount_sql = "SELECT SUM(hottesta) AS hottesta,boxOffice_date FROM boxOffice_cinema_movie_relation where film_code = '"+filmCode+"' and boxOffice_date >= '"+projectRequirement.begin_date+"' and boxOffice_date <= '"+projectRequirement.end_date+"' group by boxOffice_date" ;
			dataSourceCrawl.rows(all_amount_sql).each { movie ->
				Row = Sheet.createRow(index++);
				cell1 = Row.createCell(0)
				cell2 = Row.createCell(1);
				cell3 = Row.createCell(2);
				cell4 = Row.createCell(3);

				cell1.setCellValue(object_id);
				cell2.setCellValue(movieName);
				cell3.setCellValue(movie.hottesta);
				cell4.setCellValue(movie.boxOffice_date);
			}
		}
	}

	@Deprecated
	private getMovieTheatresSheet(SXSSFWorkbook wb, groovy.sql.GroovyRowResult it, String[] object_idArr, Sql dataSource, Sql dataSourceCrawl, projectRequirement) {
		Sheet Sheet = wb.createSheet(it.display_text)
		int index = 0 ;
		Row Row = Sheet.createRow(index++);
		Cell cell1 = Row.createCell(0);
		Cell cell2 = Row.createCell(1);
		Cell cell3 = Row.createCell(2);
		Cell cell4 = Row.createCell(3);
		Cell cell5 = Row.createCell(4);
		Cell cell6 = Row.createCell(5);
		Cell cell7 = Row.createCell(6);
		cell1.setCellValue("电影ID");
		cell2.setCellValue("电影名称");
		cell3.setCellValue("票房");
		cell4.setCellValue("场次");
		cell5.setCellValue("人次");
		cell6.setCellValue("院线");
		cell7.setCellValue("日期");
		for(int i = 0 ; i <object_idArr.length;i++ ){

			String object_id = object_idArr[i]
			// 查询电影基本信息
			String select_sql = "select  * from basic_movie_info where id = " + object_id
			def movieInfo = dataSource.firstRow(select_sql);
			String movieName = movieInfo.name
			def publishTime = movieInfo.publish_time ;
			
			// 获得电影的film_code
			def boxOfficeMovie = dataSourceCrawl.firstRow("SELECT * FROM  ent_crawl.`boxOffice_movie` WHERE film_name = '"+movieName+"' AND release_date = '"+publishTime+"'");
			String filmCode = boxOfficeMovie?.film_code ;
			
			// 查询电影的下映日期
			select_sql = "select  mapping_date from basic_movie_time_check where movie_id = " + object_id
			def movieTimeCheckInfo = dataSource.firstRow(select_sql)
			DateTools dt = new DateTools();
			String dayStr = dt.cntTimeDifference(projectRequirement.end_date as String, movieTimeCheckInfo.mapping_date as String, "yyyy-MM-dd", "d");
			if(dayStr!=null && Long.parseLong(dayStr)<0){
				projectRequirement.end_date = movieTimeCheckInfo.mapping_date ;
			}
			
			// 获得电影的film_code
//			def boxOfficeMovie = dataSourceCrawl.firstRow("SELECT * FROM  ent_crawl.`boxOffice_movie` WHERE film_name = '${movieName}' AND release_date = '${publishTime}'");
//			String filmCode = boxOfficeMovie.film_code ;

			// 获得电影票房信息
			String all_amount_sql = "SELECT SUM(r.total_box_office) AS total_box_office,r.opens,r.boxOffice_date,SUM(hottesta) AS hottesta,SUM(total_people) AS total_people FROM boxOffice_cinema_movie_relation  r WHERE r.film_code = "+filmCode+" and r.boxOffice_date >= '"+projectRequirement.begin_date+"' and r.boxOffice_date <= '"+projectRequirement.end_date+"' group by r.boxOffice_date,r.opens" ;
			dataSourceCrawl.rows(all_amount_sql).each { movie ->
				Row = Sheet.createRow(index++);
				cell1 = Row.createCell(0)
				cell2 = Row.createCell(1);
				cell3 = Row.createCell(2);
				cell4 = Row.createCell(3);
				cell5 = Row.createCell(4);
				cell6 = Row.createCell(5);
				cell7 = Row.createCell(6);

				cell1.setCellValue(object_id);
				cell2.setCellValue(movieName);
				cell3.setCellValue(movie.total_box_office);
				cell4.setCellValue(movie.hottesta);
				cell5.setCellValue(movie.total_people);
				cell6.setCellValue(movie.opens);
				cell7.setCellValue(movie.boxOffice_date);
			}
		}
	}
	
	/**
	 * 根据GP生成的中间表计算某电影的分院线票房信息
	 * @param wb
	 * @param it
	 * @param object_idArr
	 * @param dataSource
	 * @param projectRequirement
	 * @param dataSourceIreport
	 * @param dataSourceCrawl
	 * @return
	 */
	private getMovieTheatresSheetWithGPFlowData(SXSSFWorkbook wb, groovy.sql.GroovyRowResult it, String[] object_idArr, Sql dataSource, Sql dataSourceCrawl,Sql dataSourceIreport, projectRequirement) {
		Sheet Sheet = wb.createSheet(it.display_text)
		int index = 0 ;
		Row Row = Sheet.createRow(index++);
		Cell cell1 = Row.createCell(0);
		Cell cell2 = Row.createCell(1);
		Cell cell3 = Row.createCell(2);
		Cell cell4 = Row.createCell(3);
		Cell cell5 = Row.createCell(4);
		Cell cell6 = Row.createCell(5);
		Cell cell7 = Row.createCell(6);
		cell1.setCellValue("电影ID");
		cell2.setCellValue("电影名称");
		cell3.setCellValue("票房");
		cell4.setCellValue("场次");
		cell5.setCellValue("人次");
		cell6.setCellValue("院线");
		cell7.setCellValue("日期");
		for(int i = 0 ; i <object_idArr.length;i++ ){

			String object_id = object_idArr[i]
						
			// 获得电影票房信息
			String all_amount_sql = "SELECT opens_name,movie_name,movie_id,SUM(hottesta) as hottesta,SUM(total_people) as total_people,SUM(total_box_office) as total_box_office,box_office_date FROM `boxOffice_cinema_province_city_date_info` bcpcdi,ent_domain.`basic_cinema_info` bci WHERE bcpcdi.cinema_code = bci.cinema_code AND  movie_id = "+object_id+" AND  box_office_date>= '"+projectRequirement.begin_date+"' AND box_office_date<= '"+projectRequirement.end_date+"' GROUP BY box_office_date,opens_name ORDER BY box_office_date,opens_name"
			
			dataSourceIreport.rows(all_amount_sql).each { movie ->
				Row = Sheet.createRow(index++);
				cell1 = Row.createCell(0)
				cell2 = Row.createCell(1);
				cell3 = Row.createCell(2);
				cell4 = Row.createCell(3);
				cell5 = Row.createCell(4);
				cell6 = Row.createCell(5);
				cell7 = Row.createCell(6);

				cell1.setCellValue(object_id);
				cell2.setCellValue(movie.movie_name);
				cell3.setCellValue(movie.total_box_office);
				cell4.setCellValue(movie.hottesta);
				cell5.setCellValue(movie.total_people);
				cell6.setCellValue(movie.opens_name);
				cell7.setCellValue(movie.box_office_date);
			}
		}
	}

	@Deprecated
	private getMovieCinemaSheet(SXSSFWorkbook wb, groovy.sql.GroovyRowResult it, String[] object_idArr, Sql dataSource, Sql dataSourceCrawl, projectRequirement) {
		Sheet Sheet = wb.createSheet(it.display_text)
		int index = 0 ;
		Row Row = Sheet.createRow(index++);
		Cell cell1 = Row.createCell(0);
		Cell cell2 = Row.createCell(1);
		Cell cell3 = Row.createCell(2);
		Cell cell4 = Row.createCell(3);
		Cell cell5 = Row.createCell(4);
		Cell cell6 = Row.createCell(5);
		Cell cell7 = Row.createCell(6);
		Cell cell8 = Row.createCell(7);
		Cell cell9 = Row.createCell(8);
		Cell cell10 = Row.createCell(9);
		cell1.setCellValue("电影ID");
		cell2.setCellValue("电影名称");
		cell3.setCellValue("票房");
		cell4.setCellValue("场次");
		cell5.setCellValue("人次");
		cell6.setCellValue("影院");
		cell7.setCellValue("院线");
		cell8.setCellValue("城市");
		cell9.setCellValue("省份");
		cell10.setCellValue("日期");
		for(int i = 0 ; i <object_idArr.length;i++ ){

			String object_id = object_idArr[i];
			// 查询电影基本信息
			String select_sql = "select  * from basic_movie_info where id = " + object_id
			def movieInfo = dataSource.firstRow(select_sql);
			String movieName = movieInfo.name
			def publishTime = movieInfo.publish_time ;
			
			// 获得电影的film_code
			def boxOfficeMovieInfo = dataSourceCrawl.firstRow("SELECT * FROM  ent_crawl.`boxOffice_movie` WHERE film_name = '"+movieName+"' AND release_date = '"+publishTime+"'");
			String filmCode = boxOfficeMovieInfo?.film_code ;

			// 查询电影的下映日期
//			select_sql = "select  mapping_date from basic_movie_time_check where id = ${object_id}"
//			def movieTimeCheckInfo = dataSource.firstRow(select_sql);
//			DateTools dt = new DateTools();
//			String dayStr = dt.cntTimeDifference(projectRequirement.end_date as String, movieTimeCheckInfo.mapping_date as String, "yyyy-MM-dd", "d");
//			if(dayStr!=null && Long.parseLong(dayStr)>0){
//				projectRequirement.end_date = movieTimeCheckInfo.mapping_date ;
//			}
			
//			List<Map<String,String>> listOfCinemaBoxOffice = new ArrayList<HashMap<String,String>>();
			
			// 获得电影的film_code
//			dataSourceCrawl.rows("SELECT * FROM  ent_crawl.`boxOffice_movie` WHERE film_name = '${movieName}' AND release_date = '${publishTime}'").each {boxOfficeMovie->
//				String filmCode = boxOfficeMovie.film_code ;
//				System.out.println("filmCode="+filmCode);
//				// 获得电影票房信息
//				String all_amount_sql = "SELECT * FROM boxOffice_cinema_movie_relation where film_code = '${filmCode}' and boxOffice_date >= '${projectRequirement.begin_date}' and boxOffice_date <= '${projectRequirement.end_date}'" ;
//				// 如果指定了需要查某几个影院，则就只查那几个影院。
//				if(projectRequirement.cinemaIds!=null && (!"".equals(projectRequirement.cinemaIds))){
//					all_amount_sql = "SELECT * FROM boxOffice_cinema_movie_relation where film_code = '${filmCode}' and boxOffice_date >= '${projectRequirement.begin_date}' and boxOffice_date <= '${projectRequirement.end_date}' and cinema_code in (${projectRequirement.cinemaIds})" ;
//				}
//				dataSourceCrawl.rows(all_amount_sql).each { movie ->
////					System.out.println(movie.cinema_name+"->"+movie.boxOffice_date+"->"+movie.total_box_office);
//					boolean isAdd = true ;
//					for (Map<String,String> oldMap : listOfCinemaBoxOffice) {
//						if(oldMap.get("boxOffice_date")!=null && oldMap.get("boxOffice_date").equals(movie.boxOffice_date as String) && oldMap.get("cinema_name")!=null && oldMap.get("cinema_name").equals(movie.cinema_name)){
//							isAdd = false ;
//							try{
//								Long total_box_office = Long.parseLong(oldMap.get("total_box_office")) + movie.total_box_office ;
//								oldMap.put("total_box_office", total_box_office as String);
//							}catch(Exception e){
//								e.printStackTrace();
//							}
//						}
//					}
//					if(isAdd){
//						Map<String,String> map = new HashMap<String,String>();
//						map.put("boxOffice_date",movie.boxOffice_date);
//						map.put("total_box_office",movie.total_box_office);
//						map.put("cinema_name",movie.cinema_name);
//						map.put("opens",movie.opens);
//						listOfCinemaBoxOffice.add(map);
//					}
//				}
//			}
//			for (Map<String,String> movie : listOfCinemaBoxOffice) {
//				Row = Sheet.createRow(index++);
//				cell1 = Row.createCell(0)
//				cell2 = Row.createCell(1);
//				cell3 = Row.createCell(2);
//				cell4 = Row.createCell(3);
//				cell5 = Row.createCell(4);
//				cell6 = Row.createCell(5);
//
//				cell1.setCellValue(object_id);
//				cell2.setCellValue(movieName);
//				cell3.setCellValue(movie.get("total_box_office"));
//				cell4.setCellValue(movie.get("cinema_name"));
//				cell5.setCellValue(movie.get("opens"));
//				cell6.setCellValue(movie.get("boxOffice_date"));
//			}
			
			String sql = "SELECT r.cinema_name,r.opens,r.total_box_office,r.boxOffice_date,r.cinema_code,r.hottesta ,r.total_people FROM boxOffice_cinema_movie_relation r WHERE r.film_code = "+filmCode+" and r.boxOffice_date >= '"+projectRequirement.begin_date+"' and r.boxOffice_date <= '"+projectRequirement.end_date+"'";
//			 如果指定了需要查某几个影院，则就只查那几个影院。
			if(projectRequirement.cinemaIds!=null && (!"".equals(projectRequirement.cinemaIds))){
				sql = "SELECT r.cinema_name,r.opens,r.total_box_office,r.boxOffice_date,r.cinema_code,r.hottesta ,r.total_people FROM boxOffice_cinema_movie_relation r WHERE r.film_code = "+filmCode+" and r.boxOffice_date >= '"+projectRequirement.begin_date+"' and r.boxOffice_date <= '"+projectRequirement.end_date+"' and r.cinema_code in (${projectRequirement.cinemaIds})" ;
			}
			dataSourceCrawl.rows(sql).each {boxOfficeMovie->
				try{
//					System.out.println(index);
					Row = Sheet.createRow(index++)
					cell1 = Row.createCell(0)
					cell2 = Row.createCell(1);
					cell3 = Row.createCell(2);
					cell4 = Row.createCell(3);
					cell5 = Row.createCell(4);
					cell6 = Row.createCell(5);
					cell7 = Row.createCell(6);
					cell8 = Row.createCell(7);
					cell9 = Row.createCell(8);
					cell10 = Row.createCell(9);
	
					cell1.setCellValue(object_id);
					cell2.setCellValue(movieName);
					cell3.setCellValue(boxOfficeMovie.total_box_office);
					cell4.setCellValue(boxOfficeMovie.hottesta);
					cell5.setCellValue(boxOfficeMovie.total_people);
					cell6.setCellValue(boxOfficeMovie.cinema_name);
					cell7.setCellValue(boxOfficeMovie.opens);
					
					def provinceInfo = dataSource.firstRow("SELECT province_name FROM ent_domain.code_location_province p,ent_domain.basic_cinema_info c WHERE p.pid = c.province_id and c.cinema_code = " + boxOfficeMovie.cinema_code);
					def cityInfo = dataSource.firstRow("SELECT city_name FROM ent_domain.code_location_city city,ent_domain.basic_cinema_info c WHERE city.cid = c.city_id and c.cinema_code = " + boxOfficeMovie.cinema_code);
					
					
					cell8.setCellValue(cityInfo.city_name);
					cell9.setCellValue(provinceInfo.province_name);
					cell10.setCellValue(boxOfficeMovie.boxOffice_date);
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			
			
		}
	}
	
	
	/**
	 * 根据GP生成的中间表计算某电影的分影院票房信息
	 * @param wb
	 * @param it
	 * @param object_idArr
	 * @param dataSource
	 * @param projectRequirement
	 * @param dataSourceIreport
	 * @param dataSourceCrawl
	 * @return
	 */
	private getMovieCinemaSheetWithGPFlowData(SXSSFWorkbook wb, groovy.sql.GroovyRowResult it, String[] object_idArr, Sql dataSource, Sql dataSourceCrawl,Sql dataSourceIreport, projectRequirement) {
		Sheet Sheet = wb.createSheet(it.display_text)
		int index = 0 ;
		Row Row = Sheet.createRow(index++);
		Cell cell1 = Row.createCell(0);
		Cell cell2 = Row.createCell(1);
		Cell cell3 = Row.createCell(2);
		Cell cell4 = Row.createCell(3);
		Cell cell5 = Row.createCell(4);
		Cell cell6 = Row.createCell(5);
		Cell cell7 = Row.createCell(6);
		Cell cell8 = Row.createCell(7);
		Cell cell9 = Row.createCell(8);
		Cell cell10 = Row.createCell(9);
		Cell cell11 = Row.createCell(10);
		cell1.setCellValue("电影ID");
		cell2.setCellValue("电影名称");
		cell3.setCellValue("票房");
		cell4.setCellValue("场次");
		cell5.setCellValue("人次");
		cell6.setCellValue("影院Code");
		cell7.setCellValue("影院");
		cell8.setCellValue("院线");
		cell9.setCellValue("城市");
		cell10.setCellValue("省份");
		cell11.setCellValue("日期");
		for(int i = 0 ; i <object_idArr.length;i++ ){

			String object_id = object_idArr[i];
			
			String sql = "SELECT bcpcdi.city_name,bcpcdi.province_name,opens_name,bcpcdi.cinema_code ,bcpcdi.cinema_name,bcpcdi.cinema_code,bcpcdi.movie_name,bcpcdi.movie_id,bcpcdi.hottesta,bcpcdi.total_people,bcpcdi.total_box_office,bcpcdi.box_office_date FROM `boxOffice_cinema_province_city_date_info` bcpcdi,ent_domain.`basic_cinema_info` bci WHERE bcpcdi.cinema_code = bci.cinema_code AND  movie_id = "+object_id+" AND  box_office_date>= '"+projectRequirement.begin_date+"' AND box_office_date<= '"+projectRequirement.end_date+"' ORDER BY box_office_date,cinema_code";
//			 如果指定了需要查某几个影院，则就只查那几个影院。
			if(projectRequirement.cinemaIds!=null && (!"".equals(projectRequirement.cinemaIds))){
				sql = "SELECT bcpcdi.city_name,bcpcdi.province_name,opens_name,bcpcdi.cinema_code ,bcpcdi.cinema_name,bcpcdi.cinema_code,bcpcdi.movie_name,bcpcdi.movie_id,bcpcdi.hottesta,bcpcdi.total_people,bcpcdi.total_box_office,bcpcdi.box_office_date FROM `boxOffice_cinema_province_city_date_info` bcpcdi,ent_domain.`basic_cinema_info` bci WHERE bcpcdi.cinema_code = bci.cinema_code AND  movie_id = "+object_id+" and bcpcdi.cinema_code in ("+projectRequirement.cinemaIds+") AND  box_office_date>= '"+projectRequirement.begin_date+"' AND box_office_date<= '"+projectRequirement.end_date+"' ORDER BY box_office_date,cinema_code" ;
			}
			dataSourceIreport.rows(sql).each {boxOfficeMovie->
				try{
//					System.out.println(index);
					Row = Sheet.createRow(index++)
					cell1 = Row.createCell(0)
					cell2 = Row.createCell(1);
					cell3 = Row.createCell(2);
					cell4 = Row.createCell(3);
					cell5 = Row.createCell(4);
					cell6 = Row.createCell(5);
					cell7 = Row.createCell(6);
					cell8 = Row.createCell(7);
					cell9 = Row.createCell(8);
					cell10 = Row.createCell(9);
					cell11 = Row.createCell(10);
	
					cell1.setCellValue(object_id);
					cell2.setCellValue(boxOfficeMovie.movie_name);
					cell3.setCellValue(boxOfficeMovie.total_box_office);
					cell4.setCellValue(boxOfficeMovie.hottesta);
					cell5.setCellValue(boxOfficeMovie.total_people);
					cell6.setCellValue(boxOfficeMovie.cinema_code);
					cell7.setCellValue(boxOfficeMovie.cinema_name);
					cell8.setCellValue(boxOfficeMovie.opens_name);
					
					cell9.setCellValue(boxOfficeMovie.city_name);
					cell10.setCellValue(boxOfficeMovie.province_name);
					cell11.setCellValue(boxOfficeMovie.box_office_date);
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			
			
		}
	}

	@Deprecated
	private getMovieCitySheet(SXSSFWorkbook wb, groovy.sql.GroovyRowResult it, String[] object_idArr, Sql dataSource, projectRequirement, Sql dataSourceIreport,Sql dataSourceCrawl) {
		Sheet Sheet = wb.createSheet(it.display_text)
		int index = 0 ;
		Row Row = Sheet.createRow(index++);
		Cell cell1 = Row.createCell(0);
		Cell cell2 = Row.createCell(1);
		Cell cell3 = Row.createCell(2);
		Cell cell4 = Row.createCell(3);
		Cell cell5 = Row.createCell(4);
		Cell cell6 = Row.createCell(5);
		Cell cell7 = Row.createCell(6);
		cell1.setCellValue("电影ID");
		cell2.setCellValue("电影名称");
		cell3.setCellValue("票房");
		cell4.setCellValue("场次");
		cell5.setCellValue("人次");
		cell6.setCellValue("城市");
		cell7.setCellValue("日期");
		for(int i = 0 ; i <object_idArr.length;i++ ){

			String object_id = object_idArr[i];
			// 查询电影基本信息
			String select_sql = "select  * from basic_movie_info where id = " + object_id
			def movieInfo = dataSource.firstRow(select_sql);
			String movieName = movieInfo.name
			def publishTime = movieInfo.publish_time ;
			
			// 获得电影的film_code
			def boxOfficeMovie = dataSourceCrawl.firstRow("SELECT * FROM  ent_crawl.`boxOffice_movie` WHERE film_name = '"+movieName+"' AND release_date = '"+publishTime+"'");
			String filmCode = boxOfficeMovie?.film_code ;
			
			// 获得电影票房信息
			String all_amount_sql = "SELECT * FROM ireport_movie_city_box_office where movie_id = "+object_id+" and record_date >= '"+projectRequirement.begin_date+"' and record_date <= '"+projectRequirement.end_date+"'" ;
			dataSourceIreport.rows(all_amount_sql).each { movie ->
				Row = Sheet.createRow(index++);
				cell1 = Row.createCell(0)
				cell2 = Row.createCell(1);
				cell3 = Row.createCell(2);
				cell4 = Row.createCell(3);
				cell5 = Row.createCell(4);
				cell6 = Row.createCell(5);
				cell7 = Row.createCell(6);

				cell1.setCellValue(movie.movie_id);
				cell2.setCellValue(movieName);
				cell3.setCellValue(movie.box_office_num);

				def cityId = movie.city_id;
				def cityInfo = dataSource.firstRow("SELECT * FROM code_location_city where cid = "+cityId);

				String otherInfo_sql = "SELECT SUM(hottesta) AS hottesta ,SUM(total_people) AS total_people FROM boxOffice_cinema_movie_relation r,ent_domain.basic_cinema_info c where c.cinema_code = r.cinema_code and c.city_id = "+cityId+" and r.film_code = '"+filmCode+"' and r.boxOffice_date = '"+movie.record_date+"' "
				def hot = dataSourceCrawl.firstRow(otherInfo_sql);
				
				
				cell4.setCellValue(hot.hottesta);
				cell5.setCellValue(hot.total_people);
				cell6.setCellValue(cityInfo.city_name);
				cell7.setCellValue(movie.record_date);
			}
		}
	}
	/**
	 * 根据GP生成的中间表计算某电影的分城市票房信息
	 * @param wb
	 * @param it
	 * @param object_idArr
	 * @param dataSource
	 * @param projectRequirement
	 * @param dataSourceIreport
	 * @param dataSourceCrawl
	 * @return
	 */
	private getMovieCitySheetWithGPFlowData(SXSSFWorkbook wb, groovy.sql.GroovyRowResult it, String[] object_idArr, Sql dataSource, projectRequirement, Sql dataSourceIreport,Sql dataSourceCrawl) {
		Sheet Sheet = wb.createSheet(it.display_text)
		int index = 0 ;
		Row Row = Sheet.createRow(index++);
		Cell cell1 = Row.createCell(0);
		Cell cell2 = Row.createCell(1);
		Cell cell3 = Row.createCell(2);
		Cell cell4 = Row.createCell(3);
		Cell cell5 = Row.createCell(4);
		Cell cell6 = Row.createCell(5);
		Cell cell7 = Row.createCell(6);
		cell1.setCellValue("电影ID");
		cell2.setCellValue("电影名称");
		cell3.setCellValue("票房");
		cell4.setCellValue("场次");
		cell5.setCellValue("人次");
		cell6.setCellValue("城市");
		cell7.setCellValue("日期");
		for(int i = 0 ; i <object_idArr.length;i++ ){

			String object_id = object_idArr[i];
			
			// 获得电影票房信息
			String all_amount_sql = "SELECT * FROM ireport_movie_city_box_office where movie_id = "+object_id+" and record_date >= '"+projectRequirement.begin_date+"' and record_date <= '"+projectRequirement.end_date+"'" ;
			
			all_amount_sql = "SELECT province_name,province_id,city_name,city_id,movie_name,movie_id,SUM(hottesta) as hottesta,SUM(total_people) as total_people,SUM(total_box_office) as total_box_office,box_office_date FROM `boxOffice_cinema_province_city_date_info` WHERE movie_id = "+object_id+" AND  box_office_date>= '"+projectRequirement.begin_date+"' AND box_office_date<= '"+projectRequirement.end_date+"' GROUP BY box_office_date,city_id  ORDER BY box_office_date,city_id" ;
			
			dataSourceIreport.rows(all_amount_sql).each { movie ->
				Row = Sheet.createRow(index++);
				cell1 = Row.createCell(0)
				cell2 = Row.createCell(1);
				cell3 = Row.createCell(2);
				cell4 = Row.createCell(3);
				cell5 = Row.createCell(4);
				cell6 = Row.createCell(5);
				cell7 = Row.createCell(6);

				cell1.setCellValue(movie.movie_id);
				cell2.setCellValue(movie.movie_name);
				cell3.setCellValue(movie.total_box_office);

				cell4.setCellValue(movie.hottesta);
				cell5.setCellValue(movie.total_people);
				cell6.setCellValue(movie.city_name);
				cell7.setCellValue(movie.box_office_date);
			}
		}
	}
	
	@Deprecated
	private getMovieProvinceSheet(SXSSFWorkbook wb, groovy.sql.GroovyRowResult it, String[] object_idArr, Sql dataSource, projectRequirement, Sql dataSourceIreport,Sql dataSourceCrawl) {
		Sheet Sheet = wb.createSheet(it.display_text)
		int index = 0 ;
		Row Row = Sheet.createRow(index++);
		Cell cell1 = Row.createCell(0);
		Cell cell2 = Row.createCell(1);
		Cell cell3 = Row.createCell(2);
		Cell cell4 = Row.createCell(3);
		Cell cell5 = Row.createCell(4);
		Cell cell6 = Row.createCell(5);
		Cell cell7 = Row.createCell(6);
		cell1.setCellValue("电影ID");
		cell2.setCellValue("电影名称");
		cell3.setCellValue("票房");
		cell4.setCellValue("场次");
		cell5.setCellValue("人次");
		cell6.setCellValue("省份");
		cell7.setCellValue("日期");
		for(int i = 0 ; i <object_idArr.length;i++ ){

			String object_id = object_idArr[i];
			// 查询电影基本信息
			String select_sql = "select  * from basic_movie_info where id = "+object_id
			def movieInfo = dataSource.firstRow(select_sql);
			String movieName = movieInfo.name
			def publishTime = movieInfo.publish_time ;
			
			// 获得电影的film_code
			def boxOfficeMovie = dataSourceCrawl.firstRow("SELECT * FROM  ent_crawl.`boxOffice_movie` WHERE film_name = '"+movieName+"' AND release_date = '"+publishTime+"'");
			String filmCode = boxOfficeMovie?.film_code ;
			
			// 获得电影票房信息
			String all_amount_sql = "SELECT * FROM ireport_movie_province_box_office where movie_id = "+object_id+" and record_date >= '"+projectRequirement.begin_date+"' and record_date <= '"+projectRequirement.end_date+"'" ;
			dataSourceIreport.rows(all_amount_sql).each { movie ->
				Row = Sheet.createRow(index++);
				cell1 = Row.createCell(0)
				cell2 = Row.createCell(1);
				cell3 = Row.createCell(2);
				cell4 = Row.createCell(3);
				cell5 = Row.createCell(4);
				cell6 = Row.createCell(5);
				cell7 = Row.createCell(6);

				cell1.setCellValue(movie.movie_id);
				cell2.setCellValue(movieName);
				cell3.setCellValue(movie.box_office_num);

				def provinceId = movie.province_id;
				def provinceInfo = dataSource.firstRow("SELECT * FROM code_location_province where pid = "+provinceId);

				String otherInfo_sql = "SELECT SUM(hottesta) AS hottesta ,SUM(total_people) AS total_people FROM boxOffice_cinema_movie_relation r,ent_domain.basic_cinema_info c where c.cinema_code = r.cinema_code and c.province_id = "+provinceId+" and r.film_code = '"+filmCode+"' and r.boxOffice_date = '"+movie.record_date+"' "
				def hot = dataSourceCrawl.firstRow(otherInfo_sql);
				
				
				cell4.setCellValue(hot.hottesta);
				cell5.setCellValue(hot.total_people);
				cell6.setCellValue(provinceInfo.province_name);
				cell7.setCellValue(movie.record_date);
			}
		}
	}
	
	/**
	 * 根据GP生成的中间表计算某电影的分省分票房信息
	 * @param wb
	 * @param it
	 * @param object_idArr
	 * @param dataSource
	 * @param projectRequirement
	 * @param dataSourceIreport
	 * @param dataSourceCrawl
	 * @return
	 */
	private getMovieProvinceSheetWithGPFlowData(SXSSFWorkbook wb, groovy.sql.GroovyRowResult it, String[] object_idArr, Sql dataSource, projectRequirement, Sql dataSourceIreport,Sql dataSourceCrawl) {
		Sheet Sheet = wb.createSheet(it.display_text)
		int index = 0 ;
		Row Row = Sheet.createRow(index++);
		Cell cell1 = Row.createCell(0);
		Cell cell2 = Row.createCell(1);
		Cell cell3 = Row.createCell(2);
		Cell cell4 = Row.createCell(3);
		Cell cell5 = Row.createCell(4);
		Cell cell6 = Row.createCell(5);
		Cell cell7 = Row.createCell(6);
		cell1.setCellValue("电影ID");
		cell2.setCellValue("电影名称");
		cell3.setCellValue("票房");
		cell4.setCellValue("场次");
		cell5.setCellValue("人次");
		cell6.setCellValue("省份");
		cell7.setCellValue("日期");
		for(int i = 0 ; i <object_idArr.length;i++ ){

			String object_id = object_idArr[i];
			
			// 获得电影票房信息
			String all_amount_sql = "SELECT * FROM ireport_movie_province_box_office where movie_id = "+object_id+" and record_date >= '"+projectRequirement.begin_date+"' and record_date <= '"+projectRequirement.end_date+"'" ;
			
			
			all_amount_sql = "SELECT * FROM `boxOffice_movie_province_date_info` WHERE movie_id = "+object_id+" AND  box_office_date>= '"+projectRequirement.begin_date+"' AND box_office_date<= '"+projectRequirement.end_date+"' ORDER BY box_office_date,province_id" ;
			
			dataSourceIreport.rows(all_amount_sql).each { movie ->
				Row = Sheet.createRow(index++);
				cell1 = Row.createCell(0)
				cell2 = Row.createCell(1);
				cell3 = Row.createCell(2);
				cell4 = Row.createCell(3);
				cell5 = Row.createCell(4);
				cell6 = Row.createCell(5);
				cell7 = Row.createCell(6);

				cell1.setCellValue(movie.movie_id);
				cell2.setCellValue(movie.movie_name);
				cell3.setCellValue(movie.total_box_office);

				cell4.setCellValue(movie.hottesta);
				cell5.setCellValue(movie.total_people);
				cell6.setCellValue(movie.province_name);
				cell7.setCellValue(movie.box_office_date);
			}
		}
	}
	
	@Deprecated
	private getMovieTotalBoxOfficeSheet(SXSSFWorkbook wb, groovy.sql.GroovyRowResult it, String[] object_idArr, Sql dataSource, projectRequirement, Sql dataSourceIreport, Sql dataSourceCrawl) {
		DateTools dt=new DateTools()
		Sheet Sheet = wb.createSheet(it.display_text)
		int index = 0 ;
		Row Row = Sheet.createRow(index++);
		Cell cell1 = Row.createCell(0);
		Cell cell2 = Row.createCell(1);
		Cell cell3 = Row.createCell(2);
		Cell cell4 = Row.createCell(3);
		Cell cell5 = Row.createCell(4);
		Cell cell6 = Row.createCell(5);
		cell1.setCellValue("电影ID");
		cell2.setCellValue("电影名称");
		cell3.setCellValue("总票房");
		cell4.setCellValue("总场次")
		cell5.setCellValue("总人次")
		cell6.setCellValue("日期");
		for(int i = 0 ; i <object_idArr.length;i++ ){

			String object_id = object_idArr[i];
			// 查询电影基本信息
			String select_sql = "select  * from basic_movie_info where id = "+object_id
			def movieInfo = dataSource.firstRow(select_sql);
			String movieName = movieInfo.name
			def publishTime = movieInfo.publish_time ;
			
			// 获得电影的film_code
			def boxOfficeMovie = dataSourceCrawl.firstRow("SELECT * FROM  ent_crawl.`boxOffice_movie` WHERE film_name = '"+movieName+"' AND release_date = '"+publishTime+"'");
			String filmCode = boxOfficeMovie?.film_code ;
			
			// 获得电影票房信息
			String all_amount_sql = "SELECT * FROM ireport_movie_box_office where movie_id = "+object_id+" and record_date >= '"+projectRequirement.begin_date+"' and record_date <= '"+projectRequirement.end_date+"'" ;
			dataSourceIreport.rows(all_amount_sql).each { movie ->
				Row = Sheet.createRow(index++);
				cell1 = Row.createCell(0)
				cell2 = Row.createCell(1);
				cell3 = Row.createCell(2);
				cell4 = Row.createCell(3);
				cell5 = Row.createCell(4);
				cell6 = Row.createCell(5);
				
				String otherInfo_sql = "SELECT SUM(hottesta) AS hottesta ,SUM(total_people) AS total_people FROM boxOffice_cinema_movie_relation where film_code = '"+filmCode+"' and boxOffice_date = '"+movie.record_date+"' "
				def hot = dataSourceCrawl.firstRow(otherInfo_sql);
				
				cell1.setCellValue(movie.movie_id);
				cell2.setCellValue(movieName);
				cell3.setCellValue(movie.box_office_num);
				cell4.setCellValue(hot.hottesta);
				cell5.setCellValue(hot.total_people);
				cell6.setCellValue(dt.fmtDate(movie.record_date,"yyyy-MM-dd"));
			}
		}
	}
	
	
	/**
	 * 根据GP生成的中间表计算某电影的总票房信息
	 * @param wb
	 * @param it
	 * @param object_idArr
	 * @param dataSource
	 * @param projectRequirement
	 * @param dataSourceIreport
	 * @param dataSourceCrawl
	 * @return
	 */
	private getMovieTotalBoxOfficeSheetWithGPFlowData(SXSSFWorkbook wb, groovy.sql.GroovyRowResult it, String[] object_idArr, Sql dataSource, projectRequirement, Sql dataSourceIreport, Sql dataSourceCrawl) {
		DateTools dt=new DateTools()
		Sheet Sheet = wb.createSheet(it.display_text)
		int index = 0 ;
		Row Row = Sheet.createRow(index++);
		Cell cell1 = Row.createCell(0);
		Cell cell2 = Row.createCell(1);
		Cell cell3 = Row.createCell(2);
		Cell cell4 = Row.createCell(3);
		Cell cell5 = Row.createCell(4);
		Cell cell6 = Row.createCell(5);
		cell1.setCellValue("电影ID");
		cell2.setCellValue("电影名称");
		cell3.setCellValue("总票房");
		cell4.setCellValue("总场次")
		cell5.setCellValue("总人次")
		cell6.setCellValue("日期");
		for(int i = 0 ; i <object_idArr.length;i++ ){

			String object_id = object_idArr[i];
			
			// 获得电影票房信息
			String all_amount_sql = "SELECT * FROM ireport_movie_box_office where movie_id = "+object_id+" and record_date >= '"+projectRequirement.begin_date+"' and record_date <= '"+projectRequirement.end_date+"'" ;
			
			all_amount_sql = "SELECT movie_name,movie_id,SUM(hottesta) as hottesta,SUM(total_people) as total_people,SUM(total_box_office) as total_box_office,box_office_date FROM boxOffice_cinema_province_city_date_info WHERE movie_id = "+object_id+" AND  box_office_date>= '"+projectRequirement.begin_date+"' AND box_office_date<= '"+projectRequirement.end_date+"' GROUP BY box_office_date " ;
			
			
			dataSourceIreport.rows(all_amount_sql).each { movie ->
				Row = Sheet.createRow(index++);
				cell1 = Row.createCell(0)
				cell2 = Row.createCell(1);
				cell3 = Row.createCell(2);
				cell4 = Row.createCell(3);
				cell5 = Row.createCell(4);
				cell6 = Row.createCell(5);
				
				cell1.setCellValue(movie.movie_id);
				cell2.setCellValue(movie.movie_name);
				cell3.setCellValue(movie.total_box_office);
				cell4.setCellValue(movie.hottesta);
				cell5.setCellValue(movie.total_people);
				cell6.setCellValue(movie.box_office_date);
			}
		}
	}
	
	
	/**
	 * 查询所有待审核的项目，包含重新申请的和（特殊项目带审核状态）
	 * @param projectId
	 * @return
	 */
	def projectRequirementForCheck(def projectId){
		def dataSource = new Sql(dataSource_iplay)
		def result = dataSource.rows("select a.`name` rName,a.operation_date,b.`name` uName,submit_time,CASE a.requirement_state when 0 then '未提交' when 1 then '待审核' when 2 then '进行中' when 3 then '已完成' when 4 then '重跑待审核' when 5 then '重跑进行中' when 6 then '已驳回' when 7 then '重跑已驳回' end as state,a.requirement_state,a.operation_person,a.id rId from basic_project_requirement_info a,operation.operation_person b where a.operation_person=b.id and project_id = '${projectId}' and (requirement_state = 4 or (object_type = 10 and requirement_state = 1)) order by a.id desc ")
		return result;
	}
	
	def getProjectRequirementForCheckCount(def projectId){
		def dataSource = new Sql(dataSource_iplay)
		def result = dataSource.firstRow("select count(*) as myCount from basic_project_requirement_info a,operation.operation_person b where a.operation_person=b.id and project_id = '${projectId}' and (requirement_state = 4 or (object_type = 10 and requirement_state = 1)) ")
		return result.myCount;
	}
	
	/**
	 * 查询某个项目所有正在进行中的项目需求
	 * @param projectId
	 * @return
	 */
	def projectRequirementForUnderway(def projectId){
		def dataSource = new Sql(dataSource_iplay)
		def result = dataSource.rows("select a.`name` rName,a.object_type,a.operation_date,b.`name` uName,submit_time,CASE a.requirement_state when 0 then '未提交' when 1 then '待审核' when 2 then '进行中' when 3 then '已完成' when 4 then '重跑待审核' when 5 then '重跑进行中' when 6 then '已驳回' when 7 then '重跑已驳回' end as state,a.requirement_state,a.operation_person,a.id rId from basic_project_requirement_info a,operation.operation_person b where a.operation_person=b.id and project_id = '${projectId}' and (requirement_state = 2 or requirement_state = 5) order by a.id desc ")
		return result;
	}
	
	def getProjectRequirementForUnderwayCount(def projectId){
		def dataSource = new Sql(dataSource_iplay)
		def result = dataSource.firstRow("select count(*) as myCount from basic_project_requirement_info a,operation.operation_person b where a.operation_person=b.id and project_id = '${projectId}' and (requirement_state = 2 or requirement_state = 5) ")
		return result.myCount;
	}
	
	/**
	 * 查询某个项目所有已经完成的项目需求
	 * @param projectId
	 * @return
	 */
	def projectRequirementForOver(def projectId){
		def dataSource = new Sql(dataSource_iplay)
		def result = dataSource.rows("select a.`name` rName,a.operation_date,b.`name` uName,submit_time,CASE a.requirement_state when 0 then '未提交' when 1 then '待审核' when 2 then '进行中' when 3 then '已完成' when 4 then '重跑待审核' when 5 then '重跑进行中' when 6 then '已驳回' when 7 then '重跑已驳回' end as state,a.requirement_state,a.operation_person,a.id rId from basic_project_requirement_info a,operation.operation_person b where a.operation_person=b.id and project_id = '${projectId}' and (requirement_state = 3 or requirement_state = 7 ) order by a.id desc ")
		return result;
	}
	
	def getProjectRequirementForOverCount(def projectId){
		def dataSource = new Sql(dataSource_iplay)
		def result = dataSource.firstRow("select count(*) as myCount from basic_project_requirement_info a,operation.operation_person b where a.operation_person=b.id and project_id = '${projectId}' and (requirement_state = 3 or requirement_state = 7) ")
		return result.myCount;
	}
	
	
	/**
	 * 查询我创建的项目需求
	 * @param projectId
	 * @param createUserId
	 * @return
	 */
	def projectRequirementForMyCreate(def projectId,def createUserId){
		def dataSource = new Sql(dataSource_iplay)
		def result = dataSource.rows("select a.`name` rName,a.operation_date,b.`name` uName,submit_time,CASE a.requirement_state when 0 then '未提交' when 1 then '待审核' when 2 then '进行中' when 3 then '已完成' when 4 then '重跑待审核' when 5 then '重跑进行中' when 6 then '已驳回' when 7 then '重跑已驳回' end as state,a.requirement_state,a.operation_person,a.id rId from basic_project_requirement_info a,operation.operation_person b where a.operation_person=b.id and project_id = '${projectId}' and operation_person = ${createUserId}   order by a.id desc ")
		return result;
	}
	
	def getProjectRequirementForMyCreateCount(def projectId,def createUserId){
		def dataSource = new Sql(dataSource_iplay)
		def result = dataSource.firstRow("select count(*) as myCount from basic_project_requirement_info a,operation.operation_person b where a.operation_person=b.id and project_id = '${projectId}' and operation_person = ${createUserId} ")
		return result.myCount;
	}
	
	/**
	 * 查询正在重跑的项目需求
	 * @param projectId
	 * @return
	 */
	def projectRequirementForRepeatRun(def projectId){
		def dataSource = new Sql(dataSource_iplay)
		def result = dataSource.rows("select a.`name` rName,a.operation_date,b.`name` uName,submit_time,CASE a.requirement_state when 0 then '未提交' when 1 then '待审核' when 2 then '进行中' when 3 then '已完成' when 4 then '重跑待审核' when 5 then '重跑进行中' when 6 then '已驳回' when 7 then '重跑已驳回' end as state,a.requirement_state,a.operation_person,a.id rId from basic_project_requirement_info a,operation.operation_person b where a.operation_person=b.id and project_id = '${projectId}' and (requirement_state = 4 or requirement_state = 5 ) order by a.id desc ")
		return result;
	}
	def getProjectRequirementForRepeatRunCount(def projectId){
		def dataSource = new Sql(dataSource_iplay)
		def result = dataSource.firstRow("select count(*) as myCount from basic_project_requirement_info a,operation.operation_person b where a.operation_person=b.id and project_id = '${projectId}' and (requirement_state = 4 or requirement_state = 5 ) ")
		return result.myCount;
	}
	
	
	
	/**
	 * 根据项目数据需求的ID更新项目数据需求状态
	 * @param projectRequirementId
	 * @param status
	 * @return
	 */
	def updateProjectRequirementStatus(def projectRequirementId,def status){
		def dataSource = new Sql(dataSource_iplay);
		String updateSubmitTimeStr = ""
		if("2".equals(status)){
			updateSubmitTimeStr = " , submit_time = " + System.currentTimeMillis(); 
		}
		def myCount = dataSource.executeUpdate("update basic_project_requirement_info set requirement_state = ${status} " + updateSubmitTimeStr + " where id = '${projectRequirementId}' ")
		if(myCount>0){
			// 如果状态为3的情况下，则发送邮件。
			if("3".equals(status)){
				this.sendMailForProjectRequirement(projectRequirementId);
			}
			return "操作成功";
		}
		else{
			return "操作失败"
		}
	}
	
	def getProjectRequirementUrlsByIds(def params){
		def dataSource = new Sql(dataSource_iplay)
		def list = [] ;
		String sql = "select * From basic_project_requirement_info where id in (${params.requirement_ids})";
		dataSource.rows(sql).each {
			if(it.down_url!=null &&(! "".equals(it.down_url))){
				list.add(it.down_url);
			}
		}
		return list ;
	}
	
	def getCodeRequirementTemplateIdsByRequirementId(def params){
		def dataSource = new Sql(dataSource_iplay)
		StringBuilder sb=  new StringBuilder("");
		String sql = "select * From basic_requiremen_templet_codeRequirement_relation where requeirment_templet_id =${params.projectRequirementId}";
		dataSource.rows(sql).each {
			if(it.codeRequeirment_id!=null &&(! "".equals(it.codeRequeirment_id))){
				sb.append(it.codeRequeirment_id).append(",");
			}
		}
		if(sb.toString().length()==0)return "";
		return sb.toString().substring(0,sb.toString().length()-1);
	}
	
	def getCodeRequirementIdsByRequirementId(def params){
		def dataSource = new Sql(dataSource_iplay)
		StringBuilder sb=  new StringBuilder("");
		String sql = "select * From basic_requiremen_codeRequirement_relation where requeirment_id =${params.projectRequirementId}";
		dataSource.rows(sql).each {
			if(it.codeRequeirment_id!=null &&(! "".equals(it.codeRequeirment_id))){
				sb.append(it.codeRequeirment_id).append(",");
			}
		}
		if(sb.toString().length()==0)return "";
		return sb.toString().substring(0,sb.toString().length()-1);
	}
	
	/**
	 * 保存需求信息
	 * @author HTYang
	 * @date 2016-4-22 上午12:06:49
	 * @param params
	 * @return
	 */
	def saveRequiement(def params,def object_type,def userID,def time){
		
		String submit_time = "NULL" 
		if("2".equals(params.prStatue)){
			submit_time = System.currentTimeMillis();
		}
		
		String insertSql= "insert into basic_project_requirement_info (`name`,object_type,object_ids,begin_date,end_date,requirement_state,operation_person,operation_date,project_id,`submit_time`) VALUES ('${params.requirementName}',${object_type},'${params.queryIds}','${params.begin_time}','${params.end_time}','${params.prStatue}',${userID},'${time}',${params.projectID},${submit_time})"
		def dataSource = new Sql(dataSource_iplay)
		def result = dataSource.executeInsert(insertSql);
		String projectRequiementId = result.get(0).get(0);
		
		if(params.rating35){	//35城收视率
			insertSql = "INSERT INTO basic_requiremen_codeRequirement_relation (`requeirment_id`,`codeRequeirment_id`) VALUES ('${projectRequiementId}','${params.rating35}');" ;
			dataSource.executeInsert(insertSql);
		}
		
		if(params.rating50){	//50城收视率
			insertSql = "INSERT INTO basic_requiremen_codeRequirement_relation (`requeirment_id`,`codeRequeirment_id`) VALUES ('${projectRequiementId}','${params.rating50}');" ;
			dataSource.executeInsert(insertSql);
		}
		
		if(params.portion35){	//35城市场份额
			insertSql = "INSERT INTO basic_requiremen_codeRequirement_relation (`requeirment_id`,`codeRequeirment_id`) VALUES ('${projectRequiementId}','${params.portion35}');" ;
			dataSource.executeInsert(insertSql);
		}
		
		if(params.portion50){	//50城市场份额
			insertSql = "INSERT INTO basic_requiremen_codeRequirement_relation (`requeirment_id`,`codeRequeirment_id`) VALUES ('${projectRequiementId}','${params.portion50}');" ;
			dataSource.executeInsert(insertSql);
		}
		
		if(params.valuesIdsAmount){	//网络播放量
			def s=params.valuesIdsAmount.split(",");
			for(int i=0;i<s.length;i++){
				String requeirmentId = s[i]
				insertSql = "INSERT INTO basic_requiremen_codeRequirement_relation (`requeirment_id`,`codeRequeirment_id`) VALUES ('${projectRequiementId}','${requeirmentId}');" ;
				dataSource.executeInsert(insertSql);
			}
		}
		
		if(params.valuesIdsInfluence){	//影响力
			def s=params.valuesIdsInfluence.split(",");
			for(int i=0;i<s.length;i++){
				String requeirmentId = s[i]
				insertSql = "INSERT INTO basic_requiremen_codeRequirement_relation (`requeirment_id`,`codeRequeirment_id`) VALUES ('${projectRequiementId}','${requeirmentId}');" ;
				dataSource.executeInsert(insertSql);
			}
		}
		
		if(params.valuesIdsReputation){	//口碑
			def s=params.valuesIdsReputation.split(",");
			for(int i=0;i<s.length;i++){
				String requeirmentId = s[i]
				insertSql = "INSERT INTO basic_requiremen_codeRequirement_relation (`requeirment_id`,`codeRequeirment_id`) VALUES ('${projectRequiementId}','${requeirmentId}');" ;
				dataSource.executeInsert(insertSql);
			}
		}
		
		if(params.valuesIdsdirection){	//导演
			def s=params.valuesIdsdirection.split(",");
			for(int i=0;i<s.length;i++){
				String requeirmentId = s[i]
				insertSql = "INSERT INTO basic_requiremen_codeRequirement_relation (`requeirment_id`,`codeRequeirment_id`) VALUES ('${projectRequiementId}','${requeirmentId}');" ;
				dataSource.executeInsert(insertSql);
			}
		}
		
		if(params.valuesIdsscriptwriter){	//编剧
			def s=params.valuesIdsscriptwriter.split(",");
			for(int i=0;i<s.length;i++){
				String requeirmentId = s[i]
				insertSql = "INSERT INTO basic_requiremen_codeRequirement_relation (`requeirment_id`,`codeRequeirment_id`) VALUES ('${projectRequiementId}','${requeirmentId}');" ;
				dataSource.executeInsert(insertSql);
			}
		}
		
		if(params.valuesIdscomedienne){	//演员
			def s=params.valuesIdscomedienne.split(",");
			for(int i=0;i<s.length;i++){
				String requeirmentId = s[i]
				insertSql = "INSERT INTO basic_requiremen_codeRequirement_relation (`requeirment_id`,`codeRequeirment_id`) VALUES ('${projectRequiementId}','${requeirmentId}');" ;
				dataSource.executeInsert(insertSql);
			}
		}
		
		if(params.valuesIdshoster){	//主持人
			def s=params.valuesIdshoster.split(",");
			for(int i=0;i<s.length;i++){
				String requeirmentId = s[i]
				insertSql = "INSERT INTO basic_requiremen_codeRequirement_relation (`requeirment_id`,`codeRequeirment_id`) VALUES ('${projectRequiementId}','${requeirmentId}');" ;
				dataSource.executeInsert(insertSql);
			}
		}
		
		if(params.valuesIdsguester){	//嘉宾
			def s=params.valuesIdsguester.split(",");
			for(int i=0;i<s.length;i++){
				String requeirmentId = s[i]
				insertSql = "INSERT INTO basic_requiremen_codeRequirement_relation (`requeirment_id`,`codeRequeirment_id`) VALUES ('${projectRequiementId}','${requeirmentId}');" ;
				dataSource.executeInsert(insertSql);
			}
		}
//		if(params.valuesIdsWritten){	//主创及表演评估
//			def s=params.valuesIdsWritten.split(",");
//			for(int i=0;i<s.length;i++){
//				String requeirmentId = s[i]
//				insertSql = "INSERT INTO basic_requiremen_codeRequirement_relation (`requeirment_id`,`codeRequeirment_id`) VALUES ('${projectRequiementId}','${requeirmentId}');" ;
//				dataSource.executeInsert(insertSql);
//			}
//		}
		return projectRequiementId
	}
	
	/**
	 * 后台运行数据需求信息
	 * @author HTYang
	 * @date 2016-4-26 下午3:33:50
	 * @param projectRequiementId
	 * @return
	 */
	def runRequirement(def projectRequiementId){
		try{
		System.out.println("开始执行：${projectRequiementId}");
//		DateTools dt=new DateTools();
		SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dff1 = new SimpleDateFormat("HH:mm:ss");
		SXSSFWorkbook wb = new SXSSFWorkbook();
		def dataSourceMycatReputation = new Sql(dataSource_mycat_reputation)
		def dataSourc= new Sql(dataSource)
		def dataSourceIreport=new Sql(dataSource_ireport)
		def dataSourceCobarReputation=new Sql(dataSource_cobar_reputation)
		def dataSource = new Sql(dataSource_iplay)
		
		// 更新需求是否在执行的为1
		dataSource.executeUpdate("update basic_project_requirement_info set is_execute = 1 where id = ${projectRequiementId}")
		
		String sqlStr="SELECT a.object_type,a.object_ids,a.name,c.groupName,a.begin_date,a.end_date FROM basic_project_requirement_info a,`basic_requiremen_codeRequirement_relation` b,code_localtion_requirement c where a.id=b.requeirment_id and b.codeRequeirment_id=c.id and a.id=${projectRequiementId} GROUP BY c.groupName"
		def groupbyNames=dataSource.rows(sqlStr)
		def requirementName
		
		// 判断是否有收视率的信息在里面
		boolean isHasRatings35 = false ;
		boolean isHasRatings50 = false ;
		boolean isHasPortion35 = false ;
		boolean isHasPortion50 = false ;
		groupbyNames.each {
			
			if(it.groupName.equals("ratings")){
				
				sqlStr = "SELECT c.id,c.value FROM basic_requiremen_codeRequirement_relation r,code_localtion_requirement c WHERE r.codeRequeirment_id = c.id AND r.requeirment_id = ${projectRequiementId} AND c.groupName = 'ratings'";
				dataSource.rows(sqlStr).each {codeValue ->
					//35城收视率
					if(codeValue.value.equals("35")){
						sqlStr="SELECT * FROM ent_domain.teleplay_entertainment_movie_rating_info where object_id in ("+it.object_ids+") and object_type="+it.object_type+"  and play_date>='"+it.begin_date+"' and play_date<='"+it.end_date+"' order by object_id,play_date"
						isHasRatings35 = true
					}	
					//50城收视率
					if(codeValue.value.equals("50")){
						sqlStr="SELECT * FROM ent_domain.teleplay_entertainment_movie_rating_info where object_id in ("+it.object_ids+") and object_type="+it.object_type+"  and play_date>='"+it.begin_date+"' and play_date<='"+it.end_date+"' order by object_id,play_date"
						isHasRatings50 = true ;
					}	
				}
				
			
			}
			else if(it.groupName.equals("portion")){
				sqlStr = "SELECT c.id,c.value FROM basic_requiremen_codeRequirement_relation r,code_localtion_requirement c WHERE r.codeRequeirment_id = c.id AND r.requeirment_id = ${projectRequiementId} AND c.groupName = 'portion'";
				dataSource.rows(sqlStr).each {codeValue ->
					if(codeValue.value.equals("35")){
						sqlStr="SELECT * FROM ent_domain.teleplay_entertainment_movie_rating_info where object_id in ("+it.object_ids+") and object_type="+it.object_type+"  and play_date>='"+it.begin_date+"' and play_date<='"+it.end_date+"' order by object_id,play_date"
						isHasPortion35 = true ;
					}
					
					if(codeValue.value.equals("50")){
						sqlStr="SELECT * FROM ent_domain.teleplay_entertainment_movie_rating_info where object_id in ("+it.object_ids+") and object_type="+it.object_type+"  and play_date>='"+it.begin_date+"' and play_date<='"+it.end_date+"' order by object_id,play_date"
						isHasPortion50 = true ;
					}
				}
			}
		}
		
		// 如果需要导出收视率的数据
		if(isHasRatings35 || isHasRatings50 || isHasPortion35 || isHasPortion50){
			
			def projectRequirmentTemp = dataSourc.firstRow("SELECT * FROM iplay.`basic_project_requirement_info` WHERE id = ${projectRequiementId}");
			
			
			def sanwucity=dataSourc.rows(sqlStr)
			Sheet Sheet = wb.createSheet("分天收视率受众信息");
			def index=0;
			int cellIndex = 0;
			Row Row = Sheet.createRow(index++);
			Cell cell_tel_id = Row.createCell(cellIndex++);
			Cell cell_tel_name = Row.createCell(cellIndex++);
			Cell cell_channel_name = Row.createCell(cellIndex++);
			Cell cell_play_date = Row.createCell(cellIndex++);
			Cell cell_begin_time = Row.createCell(cellIndex++);
			Cell cell_end_time = Row.createCell(cellIndex++);
			if(projectRequirmentTemp.object_type==4){
				cell_tel_id.setCellValue("电视剧ID");
				cell_tel_name.setCellValue("电视剧名称");
			}
			else{
				cell_tel_id.setCellValue("综艺ID");
				cell_tel_name.setCellValue("综艺名称");
			}
			cell_channel_name.setCellValue("电视台名称");
			cell_play_date.setCellValue("收视率日期");
			cell_begin_time.setCellValue("开始时间");
			cell_end_time.setCellValue("结束时间");
			// 如果导出勾选了35城收视率
			if(isHasRatings35){
				Cell cell_35_rating_4plus = Row.createCell(cellIndex++);
				Cell cell_35_rating_man = Row.createCell(cellIndex++);
				Cell cell_35_rating_woman = Row.createCell(cellIndex++);
				Cell cell_35_rating_414 = Row.createCell(cellIndex++);
				Cell cell_35_rating_1524 = Row.createCell(cellIndex++);
				Cell cell_35_rating_2529 = Row.createCell(cellIndex++);
				Cell cell_35_rating_3034 = Row.createCell(cellIndex++);
				Cell cell_35_rating_3539 = Row.createCell(cellIndex++);
				Cell cell_35_rating_4044 = Row.createCell(cellIndex++);
				Cell cell_35_rating_4549 = Row.createCell(cellIndex++);
				Cell cell_35_rating_50plus = Row.createCell(cellIndex++);
				Cell cell_35_rating_all_uneducated = Row.createCell(cellIndex++);
				Cell cell_35_rating_all_elementary = Row.createCell(cellIndex++);
				Cell cell_35_rating_all_junior_high = Row.createCell(cellIndex++);
				Cell cell_35_rating_all_senior_high = Row.createCell(cellIndex++);
				Cell cell_35_rating_all_university = Row.createCell(cellIndex++);
				Cell cell_35_viewingPeople_4plus = Row.createCell(cellIndex++);
				Cell cell_35_viewingPeople_man = Row.createCell(cellIndex++);
				Cell cell_35_viewingPeople_woman = Row.createCell(cellIndex++);
				Cell cell_35_viewingPeople_414 = Row.createCell(cellIndex++);
				Cell cell_35_viewingPeople_1524 = Row.createCell(cellIndex++);
				Cell cell_35_viewingPeople_2529 = Row.createCell(cellIndex++);
				Cell cell_35_viewingPeople_3034 = Row.createCell(cellIndex++);
				Cell cell_35_viewingPeople_3539 = Row.createCell(cellIndex++);
				Cell cell_35_viewingPeople_4044 = Row.createCell(cellIndex++);
				Cell cell_35_viewingPeople_4549 = Row.createCell(cellIndex++);
				Cell cell_35_viewingPeople_50plus = Row.createCell(cellIndex++);
				Cell cell_35_viewingPeople_all_uneducated = Row.createCell(cellIndex++);
				Cell cell_35_viewingPeople_all_elementary = Row.createCell(cellIndex++);
				Cell cell_35_viewingPeople_all_junior_high = Row.createCell(cellIndex++);
				Cell cell_35_viewingPeople_all_senior_high = Row.createCell(cellIndex++);
				Cell cell_35_viewingPeople_all_university = Row.createCell(cellIndex++);
				
				
				cell_35_rating_4plus.setCellValue("35城四岁以上所有人收视率");
				cell_35_rating_man.setCellValue("35城男收视率");
				cell_35_rating_woman.setCellValue("35城女收视率");
				cell_35_rating_414.setCellValue("35城4-14岁收视率");
				cell_35_rating_1524.setCellValue("35城15-24岁收视率");
				cell_35_rating_2529.setCellValue("35城25-29岁收视率");
				cell_35_rating_3034.setCellValue("35城30-34岁收视率");
				cell_35_rating_3539.setCellValue("35城35-39岁收视率");
				cell_35_rating_4044.setCellValue("35城40-44岁收视率");
				cell_35_rating_4549.setCellValue("35城45-49岁收视率");
				cell_35_rating_50plus.setCellValue("35城50岁以上收视率");
				cell_35_rating_all_uneducated.setCellValue("35城未受过教育收视率");
				cell_35_rating_all_elementary.setCellValue("35城小学收视率");
				cell_35_rating_all_junior_high.setCellValue("35城初中收视率");
				cell_35_rating_all_senior_high.setCellValue("35城高中收视率");
				cell_35_rating_all_university.setCellValue("35城大学及以上收视率");
				cell_35_viewingPeople_4plus.setCellValue("35城4+收视千人");
				cell_35_viewingPeople_man.setCellValue("35城男收视千人");
				cell_35_viewingPeople_woman.setCellValue("35城女收视千人");
				cell_35_viewingPeople_414.setCellValue("35城4-14收视千人");
				cell_35_viewingPeople_1524.setCellValue("35城15-24收视千人");
				cell_35_viewingPeople_2529.setCellValue("35城25-29收视千人");
				cell_35_viewingPeople_3034.setCellValue("35城30-34收视千人");
				cell_35_viewingPeople_3539.setCellValue("35城35-39收视千人");
				cell_35_viewingPeople_4044.setCellValue("35城40-44收视千人");
				cell_35_viewingPeople_4549.setCellValue("35城45-49收视千人");
				cell_35_viewingPeople_50plus.setCellValue("35城50+收视千人");
				cell_35_viewingPeople_all_uneducated.setCellValue("35城未受过教育收视千人");
				cell_35_viewingPeople_all_elementary.setCellValue("35城小学收视千人");
				cell_35_viewingPeople_all_junior_high.setCellValue("35城初中收视千人");
				cell_35_viewingPeople_all_senior_high.setCellValue("35城高中收视千人");
				cell_35_viewingPeople_all_university.setCellValue("35城大学及以上收视千人");
			}
			
			// 如果导出勾选了50城收视率
			if(isHasRatings50){
				Cell cell_50_rating_4plus = Row.createCell(cellIndex++);
				Cell cell_50_rating_man = Row.createCell(cellIndex++);
				Cell cell_50_rating_woman = Row.createCell(cellIndex++);
				Cell cell_50_rating_414 = Row.createCell(cellIndex++);
				Cell cell_50_rating_1524 = Row.createCell(cellIndex++);
				Cell cell_50_rating_2529 = Row.createCell(cellIndex++);
				Cell cell_50_rating_3034 = Row.createCell(cellIndex++);
				Cell cell_50_rating_3539 = Row.createCell(cellIndex++);
				Cell cell_50_rating_4044 = Row.createCell(cellIndex++);
				Cell cell_50_rating_4549 = Row.createCell(cellIndex++);
				Cell cell_50_rating_50plus = Row.createCell(cellIndex++);
				Cell cell_50_rating_all_uneducated = Row.createCell(cellIndex++);
				Cell cell_50_rating_all_elementary = Row.createCell(cellIndex++);
				Cell cell_50_rating_all_junior_high = Row.createCell(cellIndex++);
				Cell cell_50_rating_all_senior_high = Row.createCell(cellIndex++);
				Cell cell_50_rating_all_university = Row.createCell(cellIndex++);
				Cell cell_50_viewingPeople_4plus = Row.createCell(cellIndex++);
				Cell cell_50_viewingPeople_man = Row.createCell(cellIndex++);
				Cell cell_50_viewingPeople_woman = Row.createCell(cellIndex++);
				Cell cell_50_viewingPeople_414 = Row.createCell(cellIndex++);
				Cell cell_50_viewingPeople_1524 = Row.createCell(cellIndex++);
				Cell cell_50_viewingPeople_2529 = Row.createCell(cellIndex++);
				Cell cell_50_viewingPeople_3034 = Row.createCell(cellIndex++);
				Cell cell_50_viewingPeople_3539 = Row.createCell(cellIndex++);
				Cell cell_50_viewingPeople_4044 = Row.createCell(cellIndex++);
				Cell cell_50_viewingPeople_4549 = Row.createCell(cellIndex++);
				Cell cell_50_viewingPeople_50plus = Row.createCell(cellIndex++);
				Cell cell_50_viewingPeople_all_uneducated = Row.createCell(cellIndex++);
				Cell cell_50_viewingPeople_all_elementary = Row.createCell(cellIndex++);
				Cell cell_50_viewingPeople_all_junior_high = Row.createCell(cellIndex++);
				Cell cell_50_viewingPeople_all_senior_high = Row.createCell(cellIndex++);
				Cell cell_50_viewingPeople_all_university = Row.createCell(cellIndex++);
				
				
				cell_50_rating_4plus.setCellValue("50城四岁以上所有人收视率");
				cell_50_rating_man.setCellValue("50城男收视率");
				cell_50_rating_woman.setCellValue("50城女收视率");
				cell_50_rating_414.setCellValue("50城4-14岁收视率");
				cell_50_rating_1524.setCellValue("50城15-24岁收视率");
				cell_50_rating_2529.setCellValue("50城25-29岁收视率");
				cell_50_rating_3034.setCellValue("50城30-34岁收视率");
				cell_50_rating_3539.setCellValue("50城35-39岁收视率");
				cell_50_rating_4044.setCellValue("50城40-44岁收视率");
				cell_50_rating_4549.setCellValue("50城45-49岁收视率");
				cell_50_rating_50plus.setCellValue("50城50岁以上收视率");
				cell_50_rating_all_uneducated.setCellValue("50城未受过教育收视率");
				cell_50_rating_all_elementary.setCellValue("50城小学收视率");
				cell_50_rating_all_junior_high.setCellValue("50城初中收视率");
				cell_50_rating_all_senior_high.setCellValue("50城高中收视率");
				cell_50_rating_all_university.setCellValue("50城大学及以上收视率");
				cell_50_viewingPeople_4plus.setCellValue("50城4+收视千人");
				cell_50_viewingPeople_man.setCellValue("50城男收视千人");
				cell_50_viewingPeople_woman.setCellValue("50城女收视千人");
				cell_50_viewingPeople_414.setCellValue("50城4-14收视千人");
				cell_50_viewingPeople_1524.setCellValue("50城15-24收视千人");
				cell_50_viewingPeople_2529.setCellValue("50城25-29收视千人");
				cell_50_viewingPeople_3034.setCellValue("50城30-34收视千人");
				cell_50_viewingPeople_3539.setCellValue("50城35-39收视千人");
				cell_50_viewingPeople_4044.setCellValue("50城40-44收视千人");
				cell_50_viewingPeople_4549.setCellValue("50城45-49收视千人");
				cell_50_viewingPeople_50plus.setCellValue("50城50+收视千人");
				cell_50_viewingPeople_all_uneducated.setCellValue("50城未受过教育收视千人");
				cell_50_viewingPeople_all_elementary.setCellValue("50城小学收视千人");
				cell_50_viewingPeople_all_junior_high.setCellValue("50城初中收视千人");
				cell_50_viewingPeople_all_senior_high.setCellValue("50城高中收视千人");
				cell_50_viewingPeople_all_university.setCellValue("50城大学及以上收视千人");
			}
			
			// 如果35市场份额
			if(isHasPortion35){
				Cell cell_35_marketShare_4plus = Row.createCell(cellIndex++);
				Cell cell_35_marketShare_man = Row.createCell(cellIndex++);
				Cell cell_35_marketShare_woman = Row.createCell(cellIndex++);
				Cell cell_35_marketShare_414 = Row.createCell(cellIndex++);
				Cell cell_35_marketShare_1524 = Row.createCell(cellIndex++);
				Cell cell_35_marketShare_2529 = Row.createCell(cellIndex++);
				Cell cell_35_marketShare_3034 = Row.createCell(cellIndex++);
				Cell cell_35_marketShare_3539 = Row.createCell(cellIndex++);
				Cell cell_35_marketShare_4044 = Row.createCell(cellIndex++);
				Cell cell_35_marketShare_4549 = Row.createCell(cellIndex++);
				Cell cell_35_marketShare_50plus = Row.createCell(cellIndex++);
				Cell cell_35_marketShare_all_uneducated = Row.createCell(cellIndex++);
				Cell cell_35_marketShare_all_elementary = Row.createCell(cellIndex++);
				Cell cell_35_marketShare_all_junior_high = Row.createCell(cellIndex++);
				Cell cell_35_marketShare_all_senior_high = Row.createCell(cellIndex++);
				Cell cell_35_marketShare_all_university = Row.createCell(cellIndex++);
				
				cell_35_marketShare_4plus.setCellValue("35城4+市场份额");
				cell_35_marketShare_man.setCellValue("35城男市场份额");
				cell_35_marketShare_woman.setCellValue("35城女市场份额");
				cell_35_marketShare_414.setCellValue("35城4-14市场份额");
				cell_35_marketShare_1524.setCellValue("35城15-24市场份额");
				cell_35_marketShare_2529.setCellValue("35城25-29市场份额");
				cell_35_marketShare_3034.setCellValue("35城30-34市场份额");
				cell_35_marketShare_3539.setCellValue("35城35-39市场份额");
				cell_35_marketShare_4044.setCellValue("35城40-44市场份额");
				cell_35_marketShare_4549.setCellValue("35城45-49市场份额");
				cell_35_marketShare_50plus.setCellValue("35城50+市场份额");
				cell_35_marketShare_all_uneducated.setCellValue("35城未受过教育市场份额");
				cell_35_marketShare_all_elementary.setCellValue("35城小学市场份额");
				cell_35_marketShare_all_junior_high.setCellValue("35城初中市场份额");
				cell_35_marketShare_all_senior_high.setCellValue("35城高中市场份额");
				cell_35_marketShare_all_university.setCellValue("35城大学及以上市场份额");
			}
			
			// 如果50市场份额
			if(isHasPortion35){
				Cell cell_50_marketShare_4plus = Row.createCell(cellIndex++);
				Cell cell_50_marketShare_man = Row.createCell(cellIndex++);
				Cell cell_50_marketShare_woman = Row.createCell(cellIndex++);
				Cell cell_50_marketShare_414 = Row.createCell(cellIndex++);
				Cell cell_50_marketShare_1524 = Row.createCell(cellIndex++);
				Cell cell_50_marketShare_2529 = Row.createCell(cellIndex++);
				Cell cell_50_marketShare_3034 = Row.createCell(cellIndex++);
				Cell cell_50_marketShare_3539 = Row.createCell(cellIndex++);
				Cell cell_50_marketShare_4044 = Row.createCell(cellIndex++);
				Cell cell_50_marketShare_4549 = Row.createCell(cellIndex++);
				Cell cell_50_marketShare_50plus = Row.createCell(cellIndex++);
				Cell cell_50_marketShare_all_uneducated = Row.createCell(cellIndex++);
				Cell cell_50_marketShare_all_elementary = Row.createCell(cellIndex++);
				Cell cell_50_marketShare_all_junior_high = Row.createCell(cellIndex++);
				Cell cell_50_marketShare_all_senior_high = Row.createCell(cellIndex++);
				Cell cell_50_marketShare_all_university = Row.createCell(cellIndex++);
				
				cell_50_marketShare_4plus.setCellValue("50城4+市场份额");
				cell_50_marketShare_man.setCellValue("50城男市场份额");
				cell_50_marketShare_woman.setCellValue("50城女市场份额");
				cell_50_marketShare_414.setCellValue("50城4-14市场份额");
				cell_50_marketShare_1524.setCellValue("50城15-24市场份额");
				cell_50_marketShare_2529.setCellValue("50城25-29市场份额");
				cell_50_marketShare_3034.setCellValue("50城30-34市场份额");
				cell_50_marketShare_3539.setCellValue("50城35-39市场份额");
				cell_50_marketShare_4044.setCellValue("50城40-44市场份额");
				cell_50_marketShare_4549.setCellValue("50城45-49市场份额");
				cell_50_marketShare_50plus.setCellValue("50城50+市场份额");
				cell_50_marketShare_all_uneducated.setCellValue("50城未受过教育市场份额");
				cell_50_marketShare_all_elementary.setCellValue("50城小学市场份额");
				cell_50_marketShare_all_junior_high.setCellValue("50城初中市场份额");
				cell_50_marketShare_all_senior_high.setCellValue("50城高中市场份额");
				cell_50_marketShare_all_university.setCellValue("50城大学及以上市场份额");
			}
			
			sanwucity.each { its3 ->
				cellIndex = 0 ;
				
				// 查询实体的名称
				
				String s1="select name from basic_teleplay_info where id="+its3.object_id
				if(projectRequirmentTemp.object_type==680){
					s1="select name from basic_entertainment_info where id="+its3.object_id
				}
				def objectName=dataSourc.firstRow(s1).name
				s1="SELECT channel_name FROM `basic_tvsou_channel_info` where id="+its3.channel_id
				def channelName=dataSourc.firstRow(s1).channel_name
				
				Row = Sheet.createRow(index++);
				cell_tel_id = Row.createCell(cellIndex++);
				cell_tel_name = Row.createCell(cellIndex++);
				cell_channel_name = Row.createCell(cellIndex++);
				cell_play_date = Row.createCell(cellIndex++);
				cell_begin_time = Row.createCell(cellIndex++);
				cell_end_time = Row.createCell(cellIndex++);
				
				cell_tel_id.setCellValue(its3.object_id);
				cell_tel_name.setCellValue(objectName);
				cell_channel_name.setCellValue(channelName);
				cell_play_date.setCellValue(its3.play_date);
				cell_begin_time.setCellValue(its3.begin_date);
				cell_end_time.setCellValue(its3.end_date);
				
				if(isHasRatings35){
					Cell cell_35_rating_4plus = Row.createCell(cellIndex++);
					Cell cell_35_rating_man = Row.createCell(cellIndex++);
					Cell cell_35_rating_woman = Row.createCell(cellIndex++);
					Cell cell_35_rating_414 = Row.createCell(cellIndex++);
					Cell cell_35_rating_1524 = Row.createCell(cellIndex++);
					Cell cell_35_rating_2529 = Row.createCell(cellIndex++);
					Cell cell_35_rating_3034 = Row.createCell(cellIndex++);
					Cell cell_35_rating_3539 = Row.createCell(cellIndex++);
					Cell cell_35_rating_4044 = Row.createCell(cellIndex++);
					Cell cell_35_rating_4549 = Row.createCell(cellIndex++);
					Cell cell_35_rating_50plus = Row.createCell(cellIndex++);
					Cell cell_35_rating_all_uneducated = Row.createCell(cellIndex++);
					Cell cell_35_rating_all_elementary = Row.createCell(cellIndex++);
					Cell cell_35_rating_all_junior_high = Row.createCell(cellIndex++);
					Cell cell_35_rating_all_senior_high = Row.createCell(cellIndex++);
					Cell cell_35_rating_all_university = Row.createCell(cellIndex++);
					Cell cell_35_viewingPeople_4plus = Row.createCell(cellIndex++);
					Cell cell_35_viewingPeople_man = Row.createCell(cellIndex++);
					Cell cell_35_viewingPeople_woman = Row.createCell(cellIndex++);
					Cell cell_35_viewingPeople_414 = Row.createCell(cellIndex++);
					Cell cell_35_viewingPeople_1524 = Row.createCell(cellIndex++);
					Cell cell_35_viewingPeople_2529 = Row.createCell(cellIndex++);
					Cell cell_35_viewingPeople_3034 = Row.createCell(cellIndex++);
					Cell cell_35_viewingPeople_3539 = Row.createCell(cellIndex++);
					Cell cell_35_viewingPeople_4044 = Row.createCell(cellIndex++);
					Cell cell_35_viewingPeople_4549 = Row.createCell(cellIndex++);
					Cell cell_35_viewingPeople_50plus = Row.createCell(cellIndex++);
					Cell cell_35_viewingPeople_all_uneducated = Row.createCell(cellIndex++);
					Cell cell_35_viewingPeople_all_elementary = Row.createCell(cellIndex++);
					Cell cell_35_viewingPeople_all_junior_high = Row.createCell(cellIndex++);
					Cell cell_35_viewingPeople_all_senior_high = Row.createCell(cellIndex++);
					Cell cell_35_viewingPeople_all_university = Row.createCell(cellIndex++);
					
					
					cell_35_rating_4plus.setCellValue(its3.get("35_rating_4plus"));
					cell_35_rating_man.setCellValue(its3.get("35_rating_man"));
					cell_35_rating_woman.setCellValue(its3.get("35_rating_woman"));
					cell_35_rating_414.setCellValue(its3.get("35_rating_414"));
					cell_35_rating_1524.setCellValue(its3.get("35_rating_1524"));
					cell_35_rating_2529.setCellValue(its3.get("35_rating_2529"));
					cell_35_rating_3034.setCellValue(its3.get("35_rating_3034"));
					cell_35_rating_3539.setCellValue(its3.get("35_rating_3539"));
					cell_35_rating_4044.setCellValue(its3.get("35_rating_4044"));
					cell_35_rating_4549.setCellValue(its3.get("35_rating_4549"));
					cell_35_rating_50plus.setCellValue(its3.get("35_rating_50plus"));
					cell_35_rating_all_uneducated.setCellValue(its3.get("35_rating_all_uneducated"));
					cell_35_rating_all_elementary.setCellValue(its3.get("35_rating_all_elementary"));
					cell_35_rating_all_junior_high.setCellValue(its3.get("35_rating_all_junior_high"));
					cell_35_rating_all_senior_high.setCellValue(its3.get("35_rating_all_senior_high"));
					cell_35_rating_all_university.setCellValue(its3.get("35_rating_all_university"));
					cell_35_viewingPeople_4plus.setCellValue(its3.get("35_viewingPeople_4plus"));
					cell_35_viewingPeople_man.setCellValue(its3.get("35_viewingPeople_man"));
					cell_35_viewingPeople_woman.setCellValue(its3.get("35_viewingPeople_woman"));
					cell_35_viewingPeople_414.setCellValue(its3.get("35_viewingPeople_414"));
					cell_35_viewingPeople_1524.setCellValue(its3.get("35_viewingPeople_1524"));
					cell_35_viewingPeople_2529.setCellValue(its3.get("35_viewingPeople_2529"));
					cell_35_viewingPeople_3034.setCellValue(its3.get("35_viewingPeople_3034"));
					cell_35_viewingPeople_3539.setCellValue(its3.get("35_viewingPeople_3539"));
					cell_35_viewingPeople_4044.setCellValue(its3.get("35_viewingPeople_4044"));
					cell_35_viewingPeople_4549.setCellValue(its3.get("35_viewingPeople_4549"));
					cell_35_viewingPeople_50plus.setCellValue(its3.get("35_viewingPeople_50plus"));
					cell_35_viewingPeople_all_uneducated.setCellValue(its3.get("35_viewingPeople_all_uneducated"));
					cell_35_viewingPeople_all_elementary.setCellValue(its3.get("35_viewingPeople_all_elementary"));
					cell_35_viewingPeople_all_junior_high.setCellValue(its3.get("35_viewingPeople_all_junior_high"));
					cell_35_viewingPeople_all_senior_high.setCellValue(its3.get("35_viewingPeople_all_senior_high"));
					cell_35_viewingPeople_all_university.setCellValue(its3.get("35_viewingPeople_all_university"));
				}
				
				if(isHasRatings50){
					Cell cell_50_rating_4plus = Row.createCell(cellIndex++);
					Cell cell_50_rating_man = Row.createCell(cellIndex++);
					Cell cell_50_rating_woman = Row.createCell(cellIndex++);
					Cell cell_50_rating_414 = Row.createCell(cellIndex++);
					Cell cell_50_rating_1524 = Row.createCell(cellIndex++);
					Cell cell_50_rating_2529 = Row.createCell(cellIndex++);
					Cell cell_50_rating_3034 = Row.createCell(cellIndex++);
					Cell cell_50_rating_3539 = Row.createCell(cellIndex++);
					Cell cell_50_rating_4044 = Row.createCell(cellIndex++);
					Cell cell_50_rating_4549 = Row.createCell(cellIndex++);
					Cell cell_50_rating_50plus = Row.createCell(cellIndex++);
					Cell cell_50_rating_all_uneducated = Row.createCell(cellIndex++);
					Cell cell_50_rating_all_elementary = Row.createCell(cellIndex++);
					Cell cell_50_rating_all_junior_high = Row.createCell(cellIndex++);
					Cell cell_50_rating_all_senior_high = Row.createCell(cellIndex++);
					Cell cell_50_rating_all_university = Row.createCell(cellIndex++);
					Cell cell_50_viewingPeople_4plus = Row.createCell(cellIndex++);
					Cell cell_50_viewingPeople_man = Row.createCell(cellIndex++);
					Cell cell_50_viewingPeople_woman = Row.createCell(cellIndex++);
					Cell cell_50_viewingPeople_414 = Row.createCell(cellIndex++);
					Cell cell_50_viewingPeople_1524 = Row.createCell(cellIndex++);
					Cell cell_50_viewingPeople_2529 = Row.createCell(cellIndex++);
					Cell cell_50_viewingPeople_3034 = Row.createCell(cellIndex++);
					Cell cell_50_viewingPeople_3539 = Row.createCell(cellIndex++);
					Cell cell_50_viewingPeople_4044 = Row.createCell(cellIndex++);
					Cell cell_50_viewingPeople_4549 = Row.createCell(cellIndex++);
					Cell cell_50_viewingPeople_50plus = Row.createCell(cellIndex++);
					Cell cell_50_viewingPeople_all_uneducated = Row.createCell(cellIndex++);
					Cell cell_50_viewingPeople_all_elementary = Row.createCell(cellIndex++);
					Cell cell_50_viewingPeople_all_junior_high = Row.createCell(cellIndex++);
					Cell cell_50_viewingPeople_all_senior_high = Row.createCell(cellIndex++);
					Cell cell_50_viewingPeople_all_university = Row.createCell(cellIndex++);
					
					
					cell_50_rating_4plus.setCellValue(its3.get("50_rating_4plus"));
					cell_50_rating_man.setCellValue(its3.get("50_rating_man"));
					cell_50_rating_woman.setCellValue(its3.get("50_rating_woman"));
					cell_50_rating_414.setCellValue(its3.get("50_rating_414"));
					cell_50_rating_1524.setCellValue(its3.get("50_rating_1524"));
					cell_50_rating_2529.setCellValue(its3.get("50_rating_2529"));
					cell_50_rating_3034.setCellValue(its3.get("50_rating_3034"));
					cell_50_rating_3539.setCellValue(its3.get("50_rating_3539"));
					cell_50_rating_4044.setCellValue(its3.get("50_rating_4044"));
					cell_50_rating_4549.setCellValue(its3.get("50_rating_4549"));
					cell_50_rating_50plus.setCellValue(its3.get("50_rating_50plus"));
					cell_50_rating_all_uneducated.setCellValue(its3.get("50_rating_all_uneducated"));
					cell_50_rating_all_elementary.setCellValue(its3.get("50_rating_all_elementary"));
					cell_50_rating_all_junior_high.setCellValue(its3.get("50_rating_all_junior_high"));
					cell_50_rating_all_senior_high.setCellValue(its3.get("50_rating_all_senior_high"));
					cell_50_rating_all_university.setCellValue(its3.get("50_rating_all_university"));
					cell_50_viewingPeople_4plus.setCellValue(its3.get("50_viewingPeople_4plus"));
					cell_50_viewingPeople_man.setCellValue(its3.get("50_viewingPeople_man"));
					cell_50_viewingPeople_woman.setCellValue(its3.get("50_viewingPeople_woman"));
					cell_50_viewingPeople_414.setCellValue(its3.get("50_viewingPeople_414"));
					cell_50_viewingPeople_1524.setCellValue(its3.get("50_viewingPeople_1524"));
					cell_50_viewingPeople_2529.setCellValue(its3.get("50_viewingPeople_2529"));
					cell_50_viewingPeople_3034.setCellValue(its3.get("50_viewingPeople_3034"));
					cell_50_viewingPeople_3539.setCellValue(its3.get("50_viewingPeople_3539"));
					cell_50_viewingPeople_4044.setCellValue(its3.get("50_viewingPeople_4044"));
					cell_50_viewingPeople_4549.setCellValue(its3.get("50_viewingPeople_4549"));
					cell_50_viewingPeople_50plus.setCellValue(its3.get("50_viewingPeople_50plus"));
					cell_50_viewingPeople_all_uneducated.setCellValue(its3.get("50_viewingPeople_all_uneducated"));
					cell_50_viewingPeople_all_elementary.setCellValue(its3.get("50_viewingPeople_all_elementary"));
					cell_50_viewingPeople_all_junior_high.setCellValue(its3.get("50_viewingPeople_all_junior_high"));
					cell_50_viewingPeople_all_senior_high.setCellValue(its3.get("50_viewingPeople_all_senior_high"));
					cell_50_viewingPeople_all_university.setCellValue(its3.get("50_viewingPeople_all_university"));
				}
				
				if(isHasPortion35){
					
					Cell cell_35_marketShare_4plus = Row.createCell(cellIndex++);
					Cell cell_35_marketShare_man = Row.createCell(cellIndex++);
					Cell cell_35_marketShare_woman = Row.createCell(cellIndex++);
					Cell cell_35_marketShare_414 = Row.createCell(cellIndex++);
					Cell cell_35_marketShare_1524 = Row.createCell(cellIndex++);
					Cell cell_35_marketShare_2529 = Row.createCell(cellIndex++);
					Cell cell_35_marketShare_3034 = Row.createCell(cellIndex++);
					Cell cell_35_marketShare_3539 = Row.createCell(cellIndex++);
					Cell cell_35_marketShare_4044 = Row.createCell(cellIndex++);
					Cell cell_35_marketShare_4549 = Row.createCell(cellIndex++);
					Cell cell_35_marketShare_50plus = Row.createCell(cellIndex++);
					Cell cell_35_marketShare_all_uneducated = Row.createCell(cellIndex++);
					Cell cell_35_marketShare_all_elementary = Row.createCell(cellIndex++);
					Cell cell_35_marketShare_all_junior_high = Row.createCell(cellIndex++);
					Cell cell_35_marketShare_all_senior_high = Row.createCell(cellIndex++);
					Cell cell_35_marketShare_all_university = Row.createCell(cellIndex++);
					
					cell_35_marketShare_4plus.setCellValue(its3.get("35_marketShare_4plus"));
					cell_35_marketShare_man.setCellValue(its3.get("35_marketShare_man"));
					cell_35_marketShare_woman.setCellValue(its3.get("35_marketShare_woman"));
					cell_35_marketShare_414.setCellValue(its3.get("35_marketShare_414"));
					cell_35_marketShare_1524.setCellValue(its3.get("35_marketShare_1524"));
					cell_35_marketShare_2529.setCellValue(its3.get("35_marketShare_2529"));
					cell_35_marketShare_3034.setCellValue(its3.get("35_marketShare_3034"));
					cell_35_marketShare_3539.setCellValue(its3.get("35_marketShare_3539"));
					cell_35_marketShare_4044.setCellValue(its3.get("35_marketShare_4044"));
					cell_35_marketShare_4549.setCellValue(its3.get("35_marketShare_4549"));
					cell_35_marketShare_50plus.setCellValue(its3.get("35_marketShare_50plus"));
					cell_35_marketShare_all_uneducated.setCellValue(its3.get("35_marketShare_all_uneducated"));
					cell_35_marketShare_all_elementary.setCellValue(its3.get("35_marketShare_all_university"));
					cell_35_marketShare_all_junior_high.setCellValue(its3.get("35_marketShare_all_elementary"));
					cell_35_marketShare_all_senior_high.setCellValue(its3.get("35_marketShare_all_junior_high"));
					cell_35_marketShare_all_university.setCellValue(its3.get("35_marketShare_all_senior_high"));
				}
				
				if(isHasPortion50){
					
					Cell cell_50_marketShare_4plus = Row.createCell(cellIndex++);
					Cell cell_50_marketShare_man = Row.createCell(cellIndex++);
					Cell cell_50_marketShare_woman = Row.createCell(cellIndex++);
					Cell cell_50_marketShare_414 = Row.createCell(cellIndex++);
					Cell cell_50_marketShare_1524 = Row.createCell(cellIndex++);
					Cell cell_50_marketShare_2529 = Row.createCell(cellIndex++);
					Cell cell_50_marketShare_3034 = Row.createCell(cellIndex++);
					Cell cell_50_marketShare_3539 = Row.createCell(cellIndex++);
					Cell cell_50_marketShare_4044 = Row.createCell(cellIndex++);
					Cell cell_50_marketShare_4549 = Row.createCell(cellIndex++);
					Cell cell_50_marketShare_50plus = Row.createCell(cellIndex++);
					Cell cell_50_marketShare_all_uneducated = Row.createCell(cellIndex++);
					Cell cell_50_marketShare_all_elementary = Row.createCell(cellIndex++);
					Cell cell_50_marketShare_all_junior_high = Row.createCell(cellIndex++);
					Cell cell_50_marketShare_all_senior_high = Row.createCell(cellIndex++);
					Cell cell_50_marketShare_all_university = Row.createCell(cellIndex++);
					
					cell_50_marketShare_4plus.setCellValue(its3.get("50_marketShare_4plus"));
					cell_50_marketShare_man.setCellValue(its3.get("50_marketShare_man"));
					cell_50_marketShare_woman.setCellValue(its3.get("50_marketShare_woman"));
					cell_50_marketShare_414.setCellValue(its3.get("50_marketShare_414"));
					cell_50_marketShare_1524.setCellValue(its3.get("50_marketShare_1524"));
					cell_50_marketShare_2529.setCellValue(its3.get("50_marketShare_2529"));
					cell_50_marketShare_3034.setCellValue(its3.get("50_marketShare_3034"));
					cell_50_marketShare_3539.setCellValue(its3.get("50_marketShare_3539"));
					cell_50_marketShare_4044.setCellValue(its3.get("50_marketShare_4044"));
					cell_50_marketShare_4549.setCellValue(its3.get("50_marketShare_4549"));
					cell_50_marketShare_50plus.setCellValue(its3.get("50_marketShare_50plus"));
					cell_50_marketShare_all_uneducated.setCellValue(its3.get("50_marketShare_all_uneducated"));
					cell_50_marketShare_all_elementary.setCellValue(its3.get("50_marketShare_all_university"));
					cell_50_marketShare_all_junior_high.setCellValue(its3.get("50_marketShare_all_elementary"));
					cell_50_marketShare_all_senior_high.setCellValue(its3.get("50_marketShare_all_junior_high"));
					cell_50_marketShare_all_university.setCellValue(its3.get("50_marketShare_all_senior_high"));
				}
			}
		}
		
		
		groupbyNames.each {
			requirementName=it.name
			if(it.object_type==5){	//电视剧
				String STAR_ARTICLE_SERVICE="http://qc.iminer.com:28007/oa"
				
				if(it.groupName.equals("teleplay_audience")){	//观众构成
				
				}
				if(it.groupName.equals("teleplay_amount")){	//网络播放量
					// 查询移动需要查询多少天的记录 
					DateTools dt=new DateTools()
					int days = 0;
					try{
						String dayStr = dt.cntTimeDifference(it.begin_date as String,it.end_date as String,"yyyy-MM-dd","d")
						days = Integer.parseInt(dayStr);
					}catch(Exception e){
						e.printStackTrace();
					}
					sqlStr="SELECT c.`value` FROM basic_project_requirement_info a,`basic_requiremen_codeRequirement_relation` b,code_localtion_requirement c where a.id=b.requeirment_id and b.codeRequeirment_id=c.id and a.id=${projectRequiementId} and c.groupName='"+it.groupName+"' and c.parent_id<>0 order by c.id"
					def resultOne=dataSource.rows(sqlStr)
					resultOne.each {its1 ->
						if(its1.value.trim().equals("all_amount")){		//总播放量
							def ids=it.object_ids.split(",")
							def index=0;
							Sheet Sheet = wb.createSheet("网络分天总播放量信息");
							Row Row = Sheet.createRow(index++);
							Cell cell1 = Row.createCell(0);
							Cell cell2 = Row.createCell(1);
							Cell cell3 = Row.createCell(2);
							Cell cell4 = Row.createCell(3);
							cell1.setCellValue("电视剧ID");
							cell2.setCellValue("电视剧名称");
							cell3.setCellValue("网络播放量");
							cell4.setCellValue("日期");
							for(int j = 0 ; j < ids.length ; j++ ){
//								System.out.println("执行总网络播放量的电视剧ID："+ids[j])
								// 查询该电视剧的名称
								String select_sql = "select  * from basic_teleplay_info where id = "+ids[j]
								def teleplayInfo = dataSourc.firstRow(select_sql)
								
								// 获得该电视剧在哪些渠道播放。
								select_sql = "SELECT * FROM basic_teleplay_crawl_info WHERE teleplay_object_id = "+ids[j];
								def crawl_teleplay_info_Rows = dataSourc.rows(select_sql);
								
								for(int k = 0 ; k <= days ; k++){
//									System.out.println("执行总网络播放量的电视剧ID："+ids[j]+"，第"+k+"天的数据");
									def yesterdayNum = 0	//前一天的播放量
									SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd")
									Calendar today = Calendar.getInstance();
									today.setTime(format.parse(it.begin_date as String));
									today.add(Calendar.DAY_OF_MONTH,k);
									Calendar yesterday =Calendar.getInstance();		//前一天
									yesterday.setTime(format.parse(it.begin_date as String));
									yesterday.add(Calendar.DAY_OF_MONTH,k-1);
									def teleplay_id ;
									
									def margin = 0 
									crawl_teleplay_info_Rows.each {crawlInfo ->
										boolean flag = true ; // 每个渠道只能在有分集或没有分集的其中一个表内
										// 查询没有分集的网络播放量
										String all_amount_sql = "SELECT MAX(total_play_num) play_num,DATE_FORMAT(MAX(crawl_time),'%Y-%m-%d') maxDate FROM basic_teleplay_crawl_statistical WHERE teleplay_id = " + crawlInfo.id + " and DATE_FORMAT(crawl_time,'%Y-%m-%d')>'"+format.format(yesterday.getTime())+"' and DATE_FORMAT(crawl_time,'%Y-%m-%d')<='"+format.format(today.getTime())+"' group by DATE_FORMAT(crawl_time,'%Y-%m-%d')" ;
										System.out.println("执行的sql"+all_amount_sql);
										dataSourc.rows(all_amount_sql).each { teleplayData ->
											flag =false; 
											teleplayData.play_num = teleplayData.play_num?:0
											if(format.format(yesterday.getTime()).equals(teleplayData.maxDate)){	// 昨天的播放量
												yesterdayNum=teleplayData.play_num;
											}else if(format.format(today.getTime()).equals(teleplayData.maxDate)){	// 今天的播放量
												margin += (teleplayData.play_num-yesterdayNum) 
											}
										}
										if(flag){
											// 查询分集的网络播放量
//											all_amount_sql = "SELECT SUM(play_number) play_num,DATE_FORMAT(MAX(create_time),'%Y-%m-%d') maxDate FROM (SELECT MAX(play_number) play_number,MAX(create_time)create_time,detail_id FROM basic_teleplay_crawl_detail_data GROUP BY detail_id) a WHERE detail_id IN (SELECT  id FROM basic_teleplay_crawl_detail_info WHERE  teleplay_id = " + crawlInfo.id + " ) and DATE_FORMAT(create_time,'%Y-%m-%d')>'"+format.format(yesterday.getTime())+"' and DATE_FORMAT(create_time,'%Y-%m-%d')<='"+format.format(today.getTime())+"' GROUP BY DATE_FORMAT(create_time,'%Y-%m-%d')"
											all_amount_sql = "select sum(a.bpn) play_num,a.bct maxDate from (select max(b.play_number) bpn,DATE_FORMAT(max(b.create_time),'%Y-%m-%d') bct,b.detail_id From basic_teleplay_crawl_detail_info a ,basic_teleplay_crawl_detail_data b where a.teleplay_id=" + crawlInfo.id + " and a.id=b.detail_id and DATE_FORMAT(create_time,'%Y-%m-%d')>'"+format.format(yesterday.getTime())+"' and DATE_FORMAT(create_time,'%Y-%m-%d')<='"+format.format(today.getTime())+"' GROUP BY b.detail_id) a GROUP BY a.bct";
											System.out.println("执行的sql"+all_amount_sql);
											dataSourc.rows(all_amount_sql).each { teleplayData ->
												teleplayData.play_num = teleplayData.play_num?:0
												if(format.format(yesterday.getTime()).equals(teleplayData.maxDate)){	// 昨天的播放量
													yesterdayNum=teleplayData.play_num;
												}else if(format.format(today.getTime()).equals(teleplayData.maxDate)){	// 今天的播放量
													margin += (teleplayData.play_num-yesterdayNum)
												}
											}
										}
									}
									Row = Sheet.createRow(index++);
									cell1 = Row.createCell(0);
									cell2 = Row.createCell(1);
									cell3 = Row.createCell(2);
									cell4 = Row.createCell(3);
									cell1.setCellValue(teleplayInfo.id);
									cell2.setCellValue(teleplayInfo.name);
									cell3.setCellValue(margin);
									cell4.setCellValue(format.format(today.getTime()));
								}
							}
						}
						
						
						if(its1.value.trim().equals("web_amount")){		//分网站播放量
//							def ids=[38756,39910,40464,40492,40494,40504,40608,40686,40758,40812,41162,41172,41174,41302,41324,41438,41448,41484,41496,41508,41544,41744,41872,41878,42016,42254,42318,42328,42348,42506,42554,43820,43980,43990,43992,44014,44098,44154,44180,44310,44404,44416,44430,44432,44556,44572,44576,44594,44658,44688,44700,44706,44798,45000,45030,45098,45128,45144,45160,45162,45170,45172,45184,45206,45238,45262,45310,45312,45314,45320,45346,45370,45372,45374,45376,45388] as List
							def ids=it.object_ids.split(",")
							def index=0;
							Sheet Sheet = wb.createSheet("网络分天分网站播放量信息");
							Row Row = Sheet.createRow(index++);
							Cell cell1 = Row.createCell(0);
							Cell cell2 = Row.createCell(1);
							Cell cell3 = Row.createCell(2);
							Cell cell4 = Row.createCell(3);
							Cell cell5 = Row.createCell(4);
							cell1.setCellValue("电视剧ID");
							cell2.setCellValue("电视剧名称");
							cell3.setCellValue("网站");
							cell4.setCellValue("网络播放量");
							cell5.setCellValue("日期");
//							for(int j = 0 ; j < ids.size() ; j++ ){
							for(int j = 0 ; j < ids.length ; j++ ){
								// 查询该电视剧的名称
								String select_sql = "select  * from basic_teleplay_info where id = "+ids[j]
								def teleplayInfo = dataSourc.firstRow(select_sql)
								
								// 获得该电视剧在哪些渠道播放。
								select_sql = "SELECT * FROM basic_teleplay_crawl_info WHERE teleplay_object_id = "+ids[j];
								def crawl_teleplay_info_Rows = dataSourc.rows(select_sql);
								
								for(int k = 0 ; k <= days ; k++){
									def yesterdayNum = 0	//前一天的播放量
									SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd")
									Calendar today = Calendar.getInstance();
									today.setTime(format.parse(it.begin_date as String));
									today.add(Calendar.DAY_OF_MONTH,k);
									Calendar yesterday =Calendar.getInstance();		//前一天
									yesterday.setTime(format.parse(it.begin_date as String));
									yesterday.add(Calendar.DAY_OF_MONTH,k-1);
									def teleplay_id ;
									
									def margin = 0
									crawl_teleplay_info_Rows.each {crawlInfo ->
										boolean flag = true ; // 每个渠道只能在有分集或没有分集的其中一个表内
										// 查询没有分集的网络播放量
										String all_amount_sql = "SELECT MAX(total_play_num) play_num,DATE_FORMAT(MAX(crawl_time),'%Y-%m-%d') maxDate FROM basic_teleplay_crawl_statistical WHERE teleplay_id = " + crawlInfo.id + " and DATE_FORMAT(crawl_time,'%Y-%m-%d')>'"+format.format(yesterday.getTime())+"' and DATE_FORMAT(crawl_time,'%Y-%m-%d')<='"+format.format(today.getTime())+"' group by DATE_FORMAT(crawl_time,'%Y-%m-%d')" ;
										System.out.println("执行的sql"+all_amount_sql);
										dataSourc.rows(all_amount_sql).each { teleplayData ->
											flag =false;
											teleplayData.play_num = teleplayData.play_num?:0
											if(format.format(yesterday.getTime()).equals(teleplayData.maxDate)){	// 昨天的播放量
												yesterdayNum=teleplayData.play_num;
											}else if(format.format(today.getTime()).equals(teleplayData.maxDate)){	// 今天的播放量
												margin = (teleplayData.play_num-yesterdayNum)
												Row = Sheet.createRow(index++);
												cell1 = Row.createCell(0);
												cell2 = Row.createCell(1);
												cell3 = Row.createCell(2);
												cell4 = Row.createCell(3);
												cell5 = Row.createCell(4);
												cell1.setCellValue(teleplayInfo.id);
												cell2.setCellValue(teleplayInfo.name);
												cell3.setCellValue(crawlInfo.source_domain);
												cell4.setCellValue(margin);
												cell5.setCellValue(format.format(today.getTime()));
											}
										}
										if(flag){
											// 查询分集的网络播放量
//											all_amount_sql = "SELECT SUM(play_number) play_num,DATE_FORMAT(MAX(create_time),'%Y-%m-%d') maxDate FROM (SELECT MAX(play_number) play_number,MAX(create_time)create_time,detail_id FROM basic_teleplay_crawl_detail_data GROUP BY detail_id) a WHERE detail_id IN (SELECT  id FROM basic_teleplay_crawl_detail_info WHERE  teleplay_id = " + crawlInfo.id + " ) and DATE_FORMAT(create_time,'%Y-%m-%d')>='"+format.format(yesterday.getTime())+"' and DATE_FORMAT(create_time,'%Y-%m-%d')<='"+format.format(today.getTime())+"' GROUP BY DATE_FORMAT(create_time,'%Y-%m-%d')"
											all_amount_sql = "select sum(a.bpn) play_num,a.bct maxDate from (select max(b.play_number) bpn,DATE_FORMAT(max(b.create_time),'%Y-%m-%d') bct,b.detail_id From basic_teleplay_crawl_detail_info a ,basic_teleplay_crawl_detail_data b where a.teleplay_id=" + crawlInfo.id + " and a.id=b.detail_id and DATE_FORMAT(create_time,'%Y-%m-%d')>'"+format.format(yesterday.getTime())+"' and DATE_FORMAT(create_time,'%Y-%m-%d')<='"+format.format(today.getTime())+"' GROUP BY b.detail_id) a GROUP BY a.bct";
											System.out.println("执行的sql"+all_amount_sql);
											dataSourc.rows(all_amount_sql).each { teleplayData ->
												teleplayData.play_num = teleplayData.play_num?:0
												if(format.format(yesterday.getTime()).equals(teleplayData.maxDate)){	// 昨天的播放量
													yesterdayNum=teleplayData.play_num;
												}else if(format.format(today.getTime()).equals(teleplayData.maxDate)){	// 今天的播放量
													margin = (teleplayData.play_num-yesterdayNum)
													Row = Sheet.createRow(index++);
													cell1 = Row.createCell(0);
													cell2 = Row.createCell(1);
													cell3 = Row.createCell(2);
													cell4 = Row.createCell(3);
													cell5 = Row.createCell(4);
													cell1.setCellValue(teleplayInfo.id);
													cell2.setCellValue(teleplayInfo.name);
													cell3.setCellValue(crawlInfo.source_domain);
													cell4.setCellValue(margin);
													cell5.setCellValue(format.format(today.getTime()));
												}
											}
										}
									}
								}
							}
						}
					}
				}
				if(it.groupName.equals("influence")){	//影响力
					sqlStr="SELECT c.`value` FROM basic_project_requirement_info a,`basic_requiremen_codeRequirement_relation` b,code_localtion_requirement c where a.id=b.requeirment_id and b.codeRequeirment_id=c.id and a.id=${projectRequiementId} and c.groupName='"+it.groupName+"' and c.parent_id<>0 order by c.id"
					def resultOne=dataSource.rows(sqlStr)
					resultOne.each { its1 ->
						if(its1.value.trim().equals("media_attention")){		//媒体关注度
							sqlStr="SELECT b.id,b.`name`,a.hot_rate,a.record_date FROM `domain_teleplay_hot_records` a,basic_teleplay_info b where a.teleplay_id=b.id and a.teleplay_id in ("+it.object_ids+") and a.record_date>='"+it.begin_date+"' and a.record_date<='"+it.end_date+"'"
							def result=dataSourceIreport.rows(sqlStr)
							Sheet Sheet = wb.createSheet("媒体关注度");
							def index=0;
							Row Row = Sheet.createRow(index++);
							Cell cell1 = Row.createCell(0);
							Cell cell2 = Row.createCell(1);
							Cell cell3 = Row.createCell(2);
							Cell cell4 = Row.createCell(3);
							cell1.setCellValue("电视剧ID");
							cell2.setCellValue("电视剧名称");
							cell3.setCellValue("媒体关注度");
							cell4.setCellValue("日期");
							result.each { its2 ->
								Row = Sheet.createRow(index++);
								cell1 = Row.createCell(0);
								cell2 = Row.createCell(1);
								cell3 = Row.createCell(2);
								cell4 = Row.createCell(3);
								cell1.setCellValue(its2.id);
								cell2.setCellValue(its2.name);
								cell3.setCellValue(its2.hot_rate);
								
								Date date4=its2.record_date
								String date4_1 = dff.format(date4);
								cell4.setCellValue(date4_1);
							}
						}
						if(its1.value.trim().equals("public_influence")){		//公众影响力
							sqlStr="SELECT b.id,b.`name`,a.public_influence,a.record_date FROM `domain_teleplay_hot_records` a,basic_teleplay_info b where a.teleplay_id=b.id and a.teleplay_id in ("+it.object_ids+") and a.record_date>='"+it.begin_date+"' and a.record_date<='"+it.end_date+"'"
							def result=dataSourceIreport.rows(sqlStr)
							Sheet Sheet = wb.createSheet("公众影响力");
							def index=0;
							Row Row = Sheet.createRow(index++);
							Cell cell1 = Row.createCell(0);
							Cell cell2 = Row.createCell(1);
							Cell cell3 = Row.createCell(2);
							Cell cell4 = Row.createCell(3);
							cell1.setCellValue("电视剧ID");
							cell2.setCellValue("电视剧名称");
							cell3.setCellValue("公众影响力");
							cell4.setCellValue("日期");
							result.each {
								Row = Sheet.createRow(index++);
								cell1 = Row.createCell(0);
								cell2 = Row.createCell(1);
								cell3 = Row.createCell(2);
								cell4 = Row.createCell(3);
								cell1.setCellValue(it.id);
								cell2.setCellValue(it.name);
								cell3.setCellValue(it.public_influence);
								
								Date date4=it.record_date
								String date4_1 = dff.format(date4);
								cell4.setCellValue(date4_1);
								
//								cell4.setCellValue(dt.fmtDate(it.record_date as String,"yyyy-MM-dd"));
							}
							
						}
						
						if(its1.value.trim().equals("exposed")){		//门户曝光时长
							
						}
						
						if(its1.value.trim().equals("headline")){		//媒体头条量
							sqlStr="SELECT object_id,sum(count) sc,count_date FROM `domain_expose_5` where object_id in ("+it.object_ids+") and count_date>='"+it.begin_date+"' and count_date<='"+it.end_date+"' GROUP BY object_id,count_date order by object_id,count_date desc "
							def result=dataSourceCobarReputation.rows(sqlStr)
							Sheet Sheet = wb.createSheet("媒体头条量");
							def index=0;
							Row Row = Sheet.createRow(index++);
							Cell cell1 = Row.createCell(0);
							Cell cell2 = Row.createCell(1);
							Cell cell3 = Row.createCell(2);
							Cell cell4 = Row.createCell(3);
							cell1.setCellValue("电视剧ID");
							cell2.setCellValue("电视剧名称");
							cell3.setCellValue("媒体头条量");
							cell4.setCellValue("日期");
							result.each { its2 ->
								String s1="select name from basic_teleplay_info where id="+its2.object_id
								def objectName=dataSourc.firstRow(s1).name
								Row = Sheet.createRow(index++);
								cell1 = Row.createCell(0);
								cell2 = Row.createCell(1);
								cell3 = Row.createCell(2);
								cell4 = Row.createCell(3);
								cell1.setCellValue(its2.object_id);
								cell2.setCellValue(objectName);
								cell3.setCellValue(its2.sc);
								
								
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
								Date date4 = sdf.parse(its2.count_date);
								String date4_1 = dff.format(date4);
								cell4.setCellValue(date4_1);
								
//								cell4.setCellValue(dt.fmtDate(its2.count_date as String,"yyyy-MM-dd"));
							}
						}
						if(its1.value.trim().equals("article")){		//热门文章
							DateTools dt=new DateTools()
							def ids=it.object_ids.split(",")
							def startDate=dt.fmtDate(it.begin_date as String, "yyyy-MM-dd").getTime();
							def endDate=dt.fmtDate(it.end_date as String, "yyyy-MM-dd").getTime();
							Sheet Sheet = wb.createSheet("热门文章");
							def index=0;
							Row Row = Sheet.createRow(index++);
							Cell cell1 = Row.createCell(0);
							Cell cell2 = Row.createCell(1);
							Cell cell3 = Row.createCell(2);
							Cell cell4 = Row.createCell(3);
							Cell cell5 = Row.createCell(4);
							Cell cell6 = Row.createCell(5);
							Cell cell7 = Row.createCell(6);
							Cell cell8 = Row.createCell(7);
							Cell cell9 = Row.createCell(8);
							Cell cell10 = Row.createCell(9);
							Cell cell11 = Row.createCell(10);
							Cell cell12 = Row.createCell(11);
							
							cell1.setCellValue("电视剧ID");
							cell2.setCellValue("电视剧名称");
							cell3.setCellValue("文章的url");
							cell4.setCellValue("文章的标题");
							cell5.setCellValue("此文章的相似文章数");
							cell6.setCellValue("阅读数");
							cell7.setCellValue("转载数");
							cell8.setCellValue("收藏数");
							cell9.setCellValue("喜欢数");
							cell10.setCellValue("播放数");
							cell11.setCellValue("日期");
							cell12.setCellValue("关注度");
							for(int j=0;j<ids.length;j++){
								def id=ids[j]
								def httpurl="${STAR_ARTICLE_SERVICE}?app_key=ireport&&method=getTopNArticles&ontologytypes=434&objectId=${id}&objectType=5&beginTime=${startDate}&endTime=${endDate}&maxNum=20";
								URL url=new URL(httpurl);
								URLConnection hpCon=url.openConnection()
								hpCon.connect();
								BufferedReader inbuffer=new BufferedReader(new InputStreamReader(hpCon.getInputStream()))
								String res="";
								String line="";
								while((line=inbuffer.readLine())!=null){
									res+=line;
								}
								inbuffer.close();
								Object obj=JSONValue.parse(res);
								JSONArray array=(JSONArray)obj;
								def articlist=[];
								int articlisCount = 0
								String s1="select name from basic_teleplay_info where id="+id
								def objectName=dataSourc.firstRow(s1).name
								if(array){
									DateTools dtools=new DateTools()
									array.each { its2 ->
										Row = Sheet.createRow(index++);
										cell1 = Row.createCell(0);
										cell2 = Row.createCell(1);
										cell3 = Row.createCell(2);
										cell4 = Row.createCell(3);
										cell5 = Row.createCell(4);
										cell6 = Row.createCell(5);
										cell7 = Row.createCell(6);
										cell8 = Row.createCell(7);
										cell9 = Row.createCell(8);
										cell10 = Row.createCell(9);
										cell11 = Row.createCell(10);
										cell12 = Row.createCell(11);
										
										cell1.setCellValue(its2.object_id);
										cell2.setCellValue(objectName);
										cell3.setCellValue(its2.url);
										cell4.setCellValue(its2.title);
										cell5.setCellValue(its2.SimilarCount);
										cell6.setCellValue(its2.nRead);
										cell7.setCellValue(its2.nRepost);
										cell8.setCellValue(its2.nSubscribe);
										cell9.setCellValue(its2.nLike);
										cell10.setCellValue(its2.nPlay);
										
										SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
										Date date11 = sdf.parse(its2.publish_date);
										String date11_1 = dff.format(date11);
										cell11.setCellValue(date11_1);
										
//										cell11.setCellValue(dt.fmtDate(its2.publish_date as String,"yyyy-MM-dd"));
										cell12.setCellValue(its2.hotrate);
									}
								}
							}
						}
						if(its1.value.trim().equals("microblogging")){		//热门微博
							DateTools dt=new DateTools()
							def ids=it.object_ids.split(",")
							def startDate=dt.fmtDate(it.begin_date as String, "yyyy-MM-dd").getTime();
							def endDate=dt.fmtDate(it.end_date as String, "yyyy-MM-dd").getTime();
							Sheet Sheet = wb.createSheet("热门微博");
							def index=0;
							Row Row = Sheet.createRow(index++);
							Cell cell1 = Row.createCell(0);
							Cell cell2 = Row.createCell(1);
							Cell cell3 = Row.createCell(2);
							Cell cell4 = Row.createCell(3);
							Cell cell5 = Row.createCell(4);
							Cell cell6 = Row.createCell(5);
							Cell cell7 = Row.createCell(6);
							Cell cell8 = Row.createCell(7);
							Cell cell9 = Row.createCell(8);
							Cell cell10 = Row.createCell(9);
							Cell cell11 = Row.createCell(10);
							Cell cell12 = Row.createCell(11);
							
							cell1.setCellValue("电视剧ID");
							cell2.setCellValue("电视剧名称");
							cell3.setCellValue("文章的url");
							cell4.setCellValue("文章的标题");
							cell5.setCellValue("此文章的相似文章数");
							cell6.setCellValue("阅读数");
							cell7.setCellValue("转载数");
							cell8.setCellValue("收藏数");
							cell9.setCellValue("喜欢数");
							cell10.setCellValue("播放数");
							cell11.setCellValue("日期");
							cell12.setCellValue("关注度");
							
							for(int j=0;j<ids.length;j++){
								def id=ids[j]
								def httpurl="${STAR_ARTICLE_SERVICE}?app_key=ireport&&method=getTopNArticles&ontologytypes=424&objectId=${id}&objectType=5&beginTime=${startDate}&endTime=${endDate}&maxNum=20";
								URL url=new URL(httpurl);
								URLConnection hpCon=url.openConnection()
								hpCon.connect();
								BufferedReader inbuffer=new BufferedReader(new InputStreamReader(hpCon.getInputStream()))
								String res="";
								String line="";
								while((line=inbuffer.readLine())!=null){
									res+=line;
								}
								inbuffer.close();
								Object obj=JSONValue.parse(res);
								JSONArray array=(JSONArray)obj;
								def articlist=[];
								int articlisCount = 0
								String s1="select name from basic_teleplay_info where id="+id
								def objectName=dataSourc.firstRow(s1).name
								if(array){
									DateTools dtools=new DateTools()
									array.each { its2 ->
										Row = Sheet.createRow(index++);
										cell1 = Row.createCell(0);
										cell2 = Row.createCell(1);
										cell3 = Row.createCell(2);
										cell4 = Row.createCell(3);
										cell5 = Row.createCell(4);
										cell6 = Row.createCell(5);
										cell7 = Row.createCell(6);
										cell8 = Row.createCell(7);
										cell9 = Row.createCell(8);
										cell10 = Row.createCell(9);
										cell11 = Row.createCell(10);
										cell12 = Row.createCell(11);
										
										cell1.setCellValue(its2.object_id);
										cell2.setCellValue(objectName);
										cell3.setCellValue(its2.url);
										cell4.setCellValue(its2.title);
										cell5.setCellValue(its2.SimilarCount);
										cell6.setCellValue(its2.nRead);
										cell7.setCellValue(its2.nRepost);
										cell8.setCellValue(its2.nSubscribe);
										cell9.setCellValue(its2.nLike);
										cell10.setCellValue(its2.nPlay);
										
										SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
										Date date11 = sdf.parse(its2.publish_date);
										String date11_1 = dff.format(date11);
										cell11.setCellValue(date11_1);
										
//										cell11.setCellValue(dt.fmtDate(its2.publish_date as String,"yyyy-MM-dd"));
										cell12.setCellValue(its2.hotrate);
									}
								}
							}
						}
					}
				}
				if(it.groupName.equals("reputation")){	//口碑
					sqlStr="SELECT c.`value` FROM basic_project_requirement_info a,`basic_requiremen_codeRequirement_relation` b,code_localtion_requirement c where a.id=b.requeirment_id and b.codeRequeirment_id=c.id and a.id=${projectRequiementId} and c.groupName='"+it.groupName+"' and c.parent_id<>0 order by c.id"
					def resultOne=dataSource.rows(sqlStr)
					resultOne.each { its1 ->
						if(its1.value.trim().equals("dimension")){		//各维度提及量
							sqlStr="SELECT teleplay_id,evaluate_type,sum(positive_num)+sum(negative_num)+sum(neutral_num) tjl,publish_date FROM `ireport_teleplay_praise_statistic_day` where teleplay_id in ("+it.object_ids+") and publish_date>='"+it.begin_date+"' and publish_date<='"+it.end_date+"'  GROUP BY teleplay_id,evaluate_type,publish_date order by teleplay_id,publish_date,evaluate_type"
							def result=dataSourceMycatReputation.rows(sqlStr)
							Sheet Sheet = wb.createSheet("各维度提及量");
							def index=0;
							Row Row = Sheet.createRow(index++);
							Cell cell1 = Row.createCell(0);
							Cell cell2 = Row.createCell(1);
							Cell cell3 = Row.createCell(2);
							Cell cell4 = Row.createCell(3);
							Cell cell5 = Row.createCell(4);
							cell1.setCellValue("电视剧ID");
							cell2.setCellValue("电视剧名称");
							cell3.setCellValue("维度");
							cell4.setCellValue("提及量");
							cell5.setCellValue("时间");
							result.each { its2 ->
								String s1="select name from basic_teleplay_info where id="+its2.teleplay_id
								def objectName=dataSourc.firstRow(s1).name
								String s2="SELECT name FROM  basic_ent_category where cid="+its2.evaluate_type
								def originName=dataSourc.firstRow(s2).name
								Row = Sheet.createRow(index++);
								cell1 = Row.createCell(0);
								cell2 = Row.createCell(1);
								cell3 = Row.createCell(2);
								cell4 = Row.createCell(3);
								cell5 = Row.createCell(4);
								cell1.setCellValue(its2.teleplay_id);
								cell2.setCellValue(objectName);
								cell3.setCellValue(originName);
								cell4.setCellValue(its2.tjl);
								
								Date date5=its2.publish_date
								String date5_1 = dff.format(date5);
								cell5.setCellValue(date5_1);
								
//								cell5.setCellValue(dt.fmtDate(its2.publish_date as String,"yyyy-MM-dd"));
							}
						}
						if(its1.value.trim().equals("favorable_rate")){		//好评率
							sqlStr="SELECT teleplay_id,sum(positive_num) hp,sum(negative_num) cp,sum(neutral_num) zp,CONCAT((sum(positive_num)/(sum(positive_num)+sum(negative_num)))*100,'%') hpl,evaluate_type,publish_date FROM `ireport_teleplay_praise_statistic_day` where teleplay_id in ("+it.object_ids+") and publish_date>='"+it.begin_date+"' and publish_date<='"+it.end_date+"' GROUP BY teleplay_id,publish_date,evaluate_type order by teleplay_id,publish_date"
							def result=dataSourceMycatReputation.rows(sqlStr)
							Sheet Sheet = wb.createSheet("好评率");
							def index=0;
							Row Row = Sheet.createRow(index++);
							Cell cell1 = Row.createCell(0);
							Cell cell2 = Row.createCell(1);
							Cell cell3 = Row.createCell(2);
							Cell cell4 = Row.createCell(3);
							Cell cell5 = Row.createCell(4);
							Cell cell6 = Row.createCell(5);
							Cell cell7 = Row.createCell(6);
							Cell cell8 = Row.createCell(7);
							cell1.setCellValue("电视剧ID");
							cell2.setCellValue("电视剧名称");
							cell3.setCellValue("维度");
							cell4.setCellValue("好评");
							cell5.setCellValue("差评");
							cell6.setCellValue("中评");
							cell7.setCellValue("好评率");
							cell8.setCellValue("时间");
							result.each {its2 ->
								String s1="select name from basic_teleplay_info where id="+its2.teleplay_id
								def objectName=dataSourc.firstRow(s1).name
								Row = Sheet.createRow(index++);
								cell1 = Row.createCell(0);
								cell2 = Row.createCell(1);
								cell3 = Row.createCell(2);
								cell4 = Row.createCell(3);
								cell5 = Row.createCell(4);
								cell6 = Row.createCell(5);
								cell7 = Row.createCell(6);
								cell8 = Row.createCell(7);
								
								cell1.setCellValue(its2.teleplay_id);
								cell2.setCellValue(objectName);
								
								// 去计算这个维度ID对应的名称
								def evaluateInfo = dataSourc.firstRow("select * From ent_domain.`basic_ent_category` where cid = " + its2.evaluate_type);
								cell3.setCellValue(evaluateInfo?.name);
								
								cell4.setCellValue(its2.hp);
								cell5.setCellValue(its2.cp);
								cell6.setCellValue(its2.zp);
								cell7.setCellValue(its2.hpl);
								
								Date date6=(Date)its2.publish_date
								String date6_1 = dff.format(date6);
								cell8.setCellValue(date6_1);
								
//								cell6.setCellValue(dt.fmtDate(its2.publish_date as String,"yyyy-MM-dd"));
							}
						}
						if(its1.value.trim().equals("hot_word")){		//热词
							sqlStr="SELECT teleplay_id,word,publish_date FROM `domain_teleplay_hotword_count` where teleplay_id in ("+it.object_ids+") and publish_date>='"+it.begin_date+"' and publish_date <='"+it.end_date+"' GROUP BY teleplay_id,word,publish_date"
							def result=dataSourceMycatReputation.rows(sqlStr)
							Sheet Sheet = wb.createSheet("热词");
							def index=0;
							Row Row = Sheet.createRow(index++);
							Cell cell1 = Row.createCell(0);
							Cell cell2 = Row.createCell(1);
							Cell cell3 = Row.createCell(2);
							Cell cell4 = Row.createCell(3);
							cell1.setCellValue("电视剧ID");
							cell2.setCellValue("电视剧名称");
							cell3.setCellValue("热词");
							cell4.setCellValue("日期");
							result.each { its2 ->
								String s1="select name from basic_teleplay_info where id="+its2.teleplay_id
								def objectName=dataSourc.firstRow(s1).name
								Row = Sheet.createRow(index++);
								cell1 = Row.createCell(0);
								cell2 = Row.createCell(1);
								cell3 = Row.createCell(2);
								cell4 = Row.createCell(3);
								cell1.setCellValue(its2.teleplay_id);
								cell2.setCellValue(objectName);
								cell3.setCellValue(its2.word);
								
								Date date4=(Date)its2.publish_date
								String date4_1 = dff.format(date4);
								cell4.setCellValue(date4_1);
								
//								cell4.setCellValue(dt.fmtDate(its2.publish_date as String,"yyyy-MM-dd"));
							}
						}
						if(its1.value.trim().equals("negative_positive")){		//正负短语
							sqlStr="select teleplay_id,phrase,CASE emotion_type when 1 then '正' ELSE '负' end et,publish_date from domain_teleplay_phrase_count where teleplay_id in ("+it.object_ids+") and publish_date>='"+it.begin_date+"' and publish_date <='"+it.end_date+"' GROUP BY teleplay_id,publish_date,phrase"
							def result=dataSourceMycatReputation.rows(sqlStr)
							Sheet Sheet = wb.createSheet("正负短语");
							def index=0;
							Row Row = Sheet.createRow(index++);
							Cell cell1 = Row.createCell(0);
							Cell cell2 = Row.createCell(1);
							Cell cell3 = Row.createCell(2);
							Cell cell4 = Row.createCell(3);
							Cell cell5 = Row.createCell(4);
							cell1.setCellValue("电视剧ID");
							cell2.setCellValue("电视剧名称");
							cell3.setCellValue("短语");
							cell4.setCellValue("正负");
							cell5.setCellValue("日期");
							result.each { its2 ->
								String s1="select name from basic_teleplay_info where id="+its2.teleplay_id
								def objectName=dataSourc.firstRow(s1).name
								Row = Sheet.createRow(index++);
								cell1 = Row.createCell(0);
								cell2 = Row.createCell(1);
								cell3 = Row.createCell(2);
								cell4 = Row.createCell(3);
								cell5 = Row.createCell(4);
								cell1.setCellValue(its2.teleplay_id);
								cell2.setCellValue(objectName);
								cell3.setCellValue(its2.phrase);
								cell4.setCellValue(its2.et);
								
								Date date5=(Date)its2.publish_date
								String date5_1 = dff.format(date5);
								cell5.setCellValue(date5_1);
								
//								cell5.setCellValue(dt.fmtDate(its2.publish_date as String,"yyyy-MM-dd"));
							}
						}
						if(its1.value.trim().equals("raw")){		//原始内容
							Sheet Sheet = wb.createSheet("原始内容");
							def index=0;
							Row Row = Sheet.createRow(index++);
							Cell cell1 = Row.createCell(0);
							Cell cell2 = Row.createCell(1);
							Cell cell3 = Row.createCell(2);
							Cell cell4 = Row.createCell(3);
							Cell cell5 = Row.createCell(4);
							cell1.setCellValue("电视剧ID");
							cell2.setCellValue("电视剧名称");
							cell3.setCellValue("句子");
							cell4.setCellValue("热词");
							cell5.setCellValue("短语");
							
							def ids=it.object_ids.split(",")
							for(int j=0;j<ids.length;j++){
								String s1="select name from basic_teleplay_info where id="+ids[j]
								def objectName=dataSourc.firstRow(s1).name
								def mongoDB= MongoDbConnectConfig.getMongoDb(ids[j] as int, "tv", null)//getMongoDb(tvId)
								BasicDBObject query=new BasicDBObject();
								query.put("object_id", ids[j])
								query.put("object_type","5")
								BasicDBObject lte=new BasicDBObject();
								lte.put('$gte', "${it.begin_date} 00:00:00".toString())
								lte.put('$lte', "${it.end_date} 23:59:59".toString())
								query.put("time",lte)
								def re = mongoDB.find(query).limit(500)
								def result = re.toArray()
								result.each { its2 ->
									Row = Sheet.createRow(index++);
									cell1 = Row.createCell(0);
									cell2 = Row.createCell(1);
									cell3 = Row.createCell(2);
									cell4 = Row.createCell(3);
									cell5 = Row.createCell(4);
									
									cell1.setCellValue(ids[j]);
									cell2.setCellValue(objectName);
									cell3.setCellValue(its2.sentence);
									cell4.setCellValue(its2.hotWords as String);
									cell5.setCellValue(its2?.phrase == null ?"":(its2?.phrase as String))
								}
							}
						}
					}
				}
				
				if(it.groupName.equals("direction")){	//主创及表演评估-导演
					Sheet Sheet = wb.createSheet("主创及表演评估-导演");
					def index=0;
					Row Row = Sheet.createRow(index++);
					Cell cell1 = Row.createCell(0);
					Cell cell2 = Row.createCell(1);
					Cell cell3 = Row.createCell(2);
					Cell cell4 = Row.createCell(3);
					Cell cell5 = Row.createCell(4);
					Cell cell6 = Row.createCell(5);
					Cell cell7 = Row.createCell(6);
					Cell cell8 = Row.createCell(7);
					Cell cell9 = Row.createCell(8);
					Cell cell10 = Row.createCell(9);
					Cell cell11 = Row.createCell(10);
					Cell cell12 = Row.createCell(11);

					cell1.setCellValue("电视剧ID");
					cell2.setCellValue("电视剧名称");
					cell3.setCellValue("明星ID");
					cell4.setCellValue("明星主创信息");
					cell5.setCellValue("姓名");
					cell6.setCellValue("维度");
					cell7.setCellValue("好评");
					cell8.setCellValue("差评");
					cell9.setCellValue("中评");
					cell10.setCellValue("提及量");
					cell11.setCellValue("好评率");
					cell12.setCellValue("日期");
					String s1="select a.id,a.`name` teleplayName,b.artist_id,c.`name` mxName,DATE_FORMAT(a.publish_time,'%Y-%m-%d') publishTime from basic_teleplay_info a ,basic_artist_teleplay b,basic_artist_info c where a.id=b.teleplay_id and b.relation=19 and b.artist_id=c.id and a.id in ("+it.object_ids+") "
					def result1=dataSourc.rows(s1)
					result1.each{ its3 ->		//循环导演
						String s2="SELECT star_id,evaluate_type,publish_date,sum(positive_num) hp,sum(negative_num) cp,sum(neutral_num) zp,sum(positive_num)+sum(negative_num)+sum(neutral_num) tjl,CONCAT((sum(positive_num)/(sum(positive_num)+sum(negative_num)))*100,'%') hpl FROM `ireport_star_praise_statistic_day` where star_id ="+its3.artist_id+" and publish_date>='"+it.begin_date+"' and publish_date <='"+it.end_date+"' GROUP BY evaluate_type,publish_date"
						def result2=dataSourceMycatReputation.rows(s2)
						result2.each { res->
							String s3="SELECT name FROM  basic_ent_category where cid="+res.evaluate_type
							def originName=dataSourc.firstRow(s3).name
							Row = Sheet.createRow(index++);
							cell1 = Row.createCell(0);
							cell2 = Row.createCell(1);
							cell3 = Row.createCell(2);
							cell4 = Row.createCell(3);
							cell5 = Row.createCell(4);
							cell6 = Row.createCell(5);
							cell7 = Row.createCell(6);
							cell8 = Row.createCell(7);
							cell9 = Row.createCell(8);
							cell10 = Row.createCell(9);
							cell11 = Row.createCell(10);
							cell12 = Row.createCell(11);

							cell1.setCellValue(its3.id);
							cell2.setCellValue(its3.teleplayName);
							cell3.setCellValue(its3.artist_id);
							cell4.setCellValue("导演");
							cell5.setCellValue(its3.mxName);
							cell6.setCellValue(originName);
							cell7.setCellValue(res.hp);
							cell8.setCellValue(res.cp);
							cell9.setCellValue(res.zp);
							cell10.setCellValue("提及量："+res.tjl);
							if(res.tjl as int ==0){
								cell11.setCellValue("好评率：0");
							}else{
								cell11.setCellValue("好评率："+res.hpl);
							}
							
							String date11_1 = dff.format(res.publish_date);
							cell12.setCellValue(date11_1);
						}
					}
					
				}
				
				if(it.groupName.equals("scriptwriter")){	//主创及表演评估-编剧
					Sheet Sheet = wb.createSheet("主创及表演评估-编剧");
					def index=0;
					Row Row = Sheet.createRow(index++);
					Cell cell1 = Row.createCell(0);
					Cell cell2 = Row.createCell(1);
					Cell cell3 = Row.createCell(2);
					Cell cell4 = Row.createCell(3);
					Cell cell5 = Row.createCell(4);
					Cell cell6 = Row.createCell(5);
					Cell cell7 = Row.createCell(6);
					Cell cell8 = Row.createCell(7);
					Cell cell9 = Row.createCell(8);
					Cell cell10 = Row.createCell(9);
					Cell cell11 = Row.createCell(10);
					Cell cell12 = Row.createCell(11);

					cell1.setCellValue("电视剧ID");
					cell2.setCellValue("电视剧名称");
					cell3.setCellValue("明星ID");
					cell4.setCellValue("明星主创信息");
					cell5.setCellValue("姓名");
					cell6.setCellValue("维度");
					cell7.setCellValue("好评");
					cell8.setCellValue("差评");
					cell9.setCellValue("中评");
					cell10.setCellValue("提及量");
					cell11.setCellValue("好评率");
					cell12.setCellValue("日期");
					String s1="select a.id,a.`name` teleplayName,b.artist_id,c.`name` mxName,DATE_FORMAT(a.publish_time,'%Y-%m-%d') publishTime from basic_teleplay_info a ,basic_artist_teleplay b,basic_artist_info c where a.id=b.teleplay_id and b.relation=18 and b.artist_id=c.id and a.id in ("+it.object_ids+") "
					def result1=dataSourc.rows(s1)
					result1.each{ its3 ->		//循环编剧
						String s2="SELECT star_id,evaluate_type,publish_date,sum(positive_num) hp,sum(negative_num) cp,sum(neutral_num) zp,sum(positive_num)+sum(negative_num)+sum(neutral_num) tjl,CONCAT((sum(positive_num)/(sum(positive_num)+sum(negative_num)))*100,'%') hpl FROM `ireport_star_praise_statistic_day` where star_id ="+its3.artist_id+" and publish_date>='"+it.begin_date+"' and publish_date <='"+it.end_date+"' GROUP BY evaluate_type,publish_date"
						def result2=dataSourceMycatReputation.rows(s2)
						result2.each { res->
							String s3="SELECT name FROM  basic_ent_category where cid="+res.evaluate_type
							def originName=dataSourc.firstRow(s3).name
							Row = Sheet.createRow(index++);
							cell1 = Row.createCell(0);
							cell2 = Row.createCell(1);
							cell3 = Row.createCell(2);
							cell4 = Row.createCell(3);
							cell5 = Row.createCell(4);
							cell6 = Row.createCell(5);
							cell7 = Row.createCell(6);
							cell8 = Row.createCell(7);
							cell9 = Row.createCell(8);
							cell10 = Row.createCell(9);
							cell11 = Row.createCell(10);
							cell12 = Row.createCell(11);

							cell1.setCellValue(its3.id);
							cell2.setCellValue(its3.teleplayName);
							cell3.setCellValue(its3.artist_id);
							cell4.setCellValue("编剧");
							cell5.setCellValue(its3.mxName);
							cell6.setCellValue(originName);
							cell7.setCellValue(res.hp);
							cell8.setCellValue(res.cp);
							cell9.setCellValue(res.zp);
							cell10.setCellValue("提及量："+res.tjl);
							if(res.tjl as int ==0){
								cell11.setCellValue("好评率：0");
							}else{
								cell11.setCellValue("好评率："+res.hpl);
							}
							String date11_1 = dff.format(res.publish_date);
							cell12.setCellValue(date11_1)
						}
					}
				}
				
				if(it.groupName.equals("comedienne")){	//主创及表演评估-演员
					Sheet Sheet = wb.createSheet("主创及表演评估-演员");
					def index=0;
					Row Row = Sheet.createRow(index++);
					Cell cell1 = Row.createCell(0);
					Cell cell2 = Row.createCell(1);
					Cell cell3 = Row.createCell(2);
					Cell cell4 = Row.createCell(3);
					Cell cell5 = Row.createCell(4);
					Cell cell6 = Row.createCell(5);
					Cell cell7 = Row.createCell(6);
					Cell cell8 = Row.createCell(7);
					Cell cell9 = Row.createCell(8);
					Cell cell10 = Row.createCell(9);
					Cell cell11 = Row.createCell(10);
					Cell cell12 = Row.createCell(11);

					cell1.setCellValue("电视剧ID");
					cell2.setCellValue("电视剧名称");
					cell3.setCellValue("明星ID");
					cell4.setCellValue("明星主创信息");
					cell5.setCellValue("姓名");
					cell6.setCellValue("维度");
					cell7.setCellValue("好评");
					cell8.setCellValue("差评");
					cell9.setCellValue("中评");
					cell10.setCellValue("提及量");
					cell11.setCellValue("好评率");
					cell12.setCellValue("日期");
					String s1="select a.id,a.`name` teleplayName,b.artist_id,c.`name` mxName,DATE_FORMAT(a.publish_time,'%Y-%m-%d') publishTime from basic_teleplay_info a ,basic_artist_teleplay b,basic_artist_info c where a.id=b.teleplay_id and b.relation=16 and b.artist_id=c.id and a.id in ("+it.object_ids+") "
					def result1=dataSourc.rows(s1)
					result1.each{ its3 ->		//循环演员
						String s2="SELECT star_id,evaluate_type,publish_date,sum(positive_num) hp,sum(negative_num) cp,sum(neutral_num) zp,sum(positive_num)+sum(negative_num)+sum(neutral_num) tjl,CONCAT((sum(positive_num)/(sum(positive_num)+sum(negative_num)))*100,'%') hpl FROM `ireport_star_praise_statistic_day` where star_id ="+its3.artist_id+" and publish_date>='"+it.begin_date+"' and publish_date <='"+it.end_date+"' GROUP BY evaluate_type,publish_date"
						def result2=dataSourceMycatReputation.rows(s2)
						result2.each { res->
							String s3="SELECT name FROM  basic_ent_category where cid="+res.evaluate_type
							def originName=dataSourc.firstRow(s3).name
							Row = Sheet.createRow(index++);
							cell1 = Row.createCell(0);
							cell2 = Row.createCell(1);
							cell3 = Row.createCell(2);
							cell4 = Row.createCell(3);
							cell5 = Row.createCell(4);
							cell6 = Row.createCell(5);
							cell7 = Row.createCell(6);
							cell8 = Row.createCell(7);
							cell9 = Row.createCell(8);
							cell10 = Row.createCell(9);
							cell11 = Row.createCell(10);
							cell12 = Row.createCell(11);

							cell1.setCellValue(its3.id);
							cell2.setCellValue(its3.teleplayName);
							cell3.setCellValue(its3.artist_id);
							cell4.setCellValue("演员");
							cell5.setCellValue(its3.mxName);
							cell6.setCellValue(originName);
							cell7.setCellValue(res.hp);
							cell8.setCellValue(res.cp);
							cell9.setCellValue(res.zp);
							cell10.setCellValue("提及量："+res.tjl);
							if(res.tjl as int ==0){
								cell11.setCellValue("好评率：0");
							}else{
								cell11.setCellValue("好评率："+res.hpl);
							}
							String date11_1 = dff.format(res.publish_date);
							cell12.setCellValue(date11_1);
						}
					}
				}
				
//====================================================================================================================================================================================================
//====================================================================================================================================================================================================
//====================================================================================================================================================================================================
//====================================================================================================================================================================================================
			}else if(it.object_type==680){		//综艺
				String STAR_ARTICLE_SERVICE="http://qc.iminer.com:28007/oa";
				
				if(it.groupName.equals("teleplay_audience")){	//观众构成
					
				}
				if(it.groupName.equals("entertainment_amount")){	//网络播放量
					// 查询移动需要查询多少天的记录
					DateTools dt=new DateTools()
					int days = 0;
					try{
						String dayStr = dt.cntTimeDifference(it.begin_date as String,it.end_date as String,"yyyy-MM-dd","d")
						days = Integer.parseInt(dayStr);
					}catch(Exception e){
						e.printStackTrace();
					}
					sqlStr="SELECT c.`value` FROM basic_project_requirement_info a,`basic_requiremen_codeRequirement_relation` b,code_localtion_requirement c where a.id=b.requeirment_id and b.codeRequeirment_id=c.id and a.id=${projectRequiementId} and c.groupName='"+it.groupName+"' order by c.id"
					def resultOne=dataSource.rows(sqlStr)
					resultOne.each {its1 ->
						if(its1.value.trim().equals("all_amount")){		//总播放量
							System.out.println("总播放量");
							def ids=it.object_ids.split(",")
							Sheet Sheet = wb.createSheet("综艺分天总播放量信息");
							def index=0;
							Row Row = Sheet.createRow(index++);
							Cell cell1 = Row.createCell(0);
							Cell cell2 = Row.createCell(1);
							Cell cell3 = Row.createCell(2);
							Cell cell4 = Row.createCell(3);
							Cell cell5 = Row.createCell(4);
							cell1.setCellValue("综艺ID");
							cell2.setCellValue("综艺名称");
							cell3.setCellValue("网络播放量");
							cell4.setCellValue("增量");
							cell5.setCellValue("日期");
							for(int j=0;j<ids.length;j++){
								
								// 获得该综艺节目的基础信息
								String select_sql = "SELECT * FROM basic_entertainment_crawl_info WHERE entertainment_id = "+ids[j];
								def crawl_entertainment_info = dataSourc.firstRow(select_sql);
								
								for(int k = 0 ; k <= days ; k++){
									def yesterdayNum = 0	//前一天的播放量
									SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd")
									Calendar today = Calendar.getInstance();
									today.setTime(format.parse(it.begin_date as String));
									today.add(Calendar.DAY_OF_MONTH,k);
									Calendar yesterday =Calendar.getInstance();		//前一天
									yesterday.setTime(format.parse(it.begin_date as String));
									yesterday.add(Calendar.DAY_OF_MONTH,k-1);
									def domain ;
									def allNumber = 0;
									
									def margin = 0
									// 查询没有分集的网络播放量
//									String all_amount_sql = "SELECT MAX(play_num) play_num,DATE_FORMAT(MAX(update_date),'%Y-%m-%d') maxDate,domain FROM basic_entertainment_statistic_by_hour WHERE entertainment_id = "+crawl_entertainment_info.id+"  and DATE_FORMAT(update_date,'%Y-%m-%d')>='"+format.format(yesterday.getTime())+"' and DATE_FORMAT(update_date,'%Y-%m-%d')<='"+format.format(today.getTime())+"' GROUP BY domain,DATE_FORMAT(update_date,'%Y-%m-%d')" ;
//									System.out.println("执行的sql"+all_amount_sql);
//									dataSourc.rows(all_amount_sql).each { entertainmentData ->
//										if(format.format(yesterday.getTime()).equals(entertainmentData.maxDate)){	// 昨天的播放量
//											yesterdayNum=entertainmentData.play_num;
//											domain = entertainmentData.domain
//										}else if(format.format(today.getTime()).equals(entertainmentData.maxDate)){	// 今天的播放量
//											if(domain!=null && domain.equals(entertainmentData.domain))
//												margin += (entertainmentData.play_num-yesterdayNum)
//											else
//												margin += entertainmentData.play_num
//												
//											domain = null
//										}
//									}
									// 查询分集的网络播放量
									String all_amount_sql = "SELECT SUM(play_num) play_num,DATE_FORMAT(MAX(update_date),'%Y-%m-%d') maxDate,domain FROM (SELECT MAX(play_num) play_num,update_date,domain FROM (SELECT  play_num,update_date,domain,branch_id FROM basic_entertainment_branch_statistic_by_hour a,basic_entertainment_branch_crawl_info_by_hour b WHERE a.branch_id = b.id AND b.entertainment_id = "+crawl_entertainment_info.id+" and DATE_FORMAT(update_date,'%Y-%m-%d')>'"+format.format(yesterday.getTime())+"' and DATE_FORMAT(update_date,'%Y-%m-%d')<='"+format.format(today.getTime())+"') c GROUP BY branch_id,DATE_FORMAT(update_date,'%Y-%m-%d')) c GROUP BY domain,DATE_FORMAT(update_date,'%Y-%m-%d')"
//									System.out.println("执行的sql"+all_amount_sql);
									dataSourc.rows(all_amount_sql).each { entertainmentData ->
										if(format.format(yesterday.getTime()).equals(entertainmentData.maxDate)){	// 昨天的播放量
											yesterdayNum=entertainmentData.play_num;
											domain = entertainmentData.domain
										}else if(format.format(today.getTime()).equals(entertainmentData.maxDate)){	// 今天的播放量
											allNumber += entertainmentData.play_num;
											if(domain!=null && domain.equals(entertainmentData.domain))
												margin = (entertainmentData.play_num-yesterdayNum)
												if(margin<0){
													margin=0;
												}
											else
												margin = entertainmentData.play_num
												if(margin<0){
													margin=0;
												}
											domain = null
										}
									}
									Row = Sheet.createRow(index++);
									cell1 = Row.createCell(0);
									cell2 = Row.createCell(1);
									cell3 = Row.createCell(2);
									cell4 = Row.createCell(3);
									cell5 = Row.createCell(4);
									cell1.setCellValue(ids[j]);
									cell2.setCellValue(crawl_entertainment_info.name);
									cell3.setCellValue(allNumber);
									cell4.setCellValue(margin);
									cell5.setCellValue(format.format(today.getTime()));
								}
							}
						}
						
						if(its1.value.trim().equals("web_amount")){		//分网站播放量
//							def ids=[2442,2724,3098,3102,3132,3142,3148,3150,3152,3164,3170,3184,3188,3192,2,26,3198,3202,3206,3208,3214,3246,3280,3294,3296,3298,3300,3302,3304,3308,3310,3314,3318,3320,3322,3328,3330,3332,3334,3336,3340,3342,3344,3346,3348,3350,3352,3354,3358,3360,3362,3372,3374,3376,3378,3380,3382,3386,3388,3390,3392,3394,3396,3398,3400,3402,3404,3408,3412,3414,3416,3418,3420,3424,1952,2568,2848,2850,2872,2996,3130,3140,3186,3200,3306,3426,8,6,744,34,32,1878,382,3092,14,48,18,2736,260,308,24,3434] as List
							
							System.out.println("分网站播放量");
							def ids=it.object_ids.split(",")
							Sheet Sheet = wb.createSheet("综艺分天分网站播放量信息");
							def index=0;
							Row Row = Sheet.createRow(index++);
							Cell cell1 = Row.createCell(0);
							Cell cell2 = Row.createCell(1);
							Cell cell3 = Row.createCell(2);
							Cell cell4 = Row.createCell(3);
							Cell cell5 = Row.createCell(4);
							Cell cell6 = Row.createCell(5);
							cell1.setCellValue("综艺ID");
							cell2.setCellValue("综艺名称");
							cell3.setCellValue("网站");
							cell4.setCellValue("网络播放量");
							cell5.setCellValue("增量");
							cell6.setCellValue("日期");
							for(int j=0;j<ids.length;j++){
								System.out.println("ID:"+ids[j]);
								// 获得该综艺节目的基本信息
								String select_sql = "SELECT * FROM basic_entertainment_info WHERE id = "+ids[j];
								def crawl_entertainment_info = dataSourc.firstRow(select_sql);
								
								for(int k = 0 ; k <= days ; k++){
									System.out.println("天数："+k);
									def yesterdayNum = 0	//前一天的播放量
									SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd")
									Calendar today = Calendar.getInstance();
									today.setTime(format.parse(it.begin_date as String));
									today.add(Calendar.DAY_OF_MONTH,k);
									Calendar yesterday =Calendar.getInstance();		//前一天
									yesterday.setTime(format.parse(it.begin_date as String));
									yesterday.add(Calendar.DAY_OF_MONTH,k-1);
									def domain ;
									
									def margin = 0
									// 查询没有分集的网络播放量
//									String all_amount_sql = "SELECT MAX(play_num) play_num,DATE_FORMAT(MAX(update_date),'%Y-%m-%d') maxDate,domain FROM basic_entertainment_statistic_by_hour WHERE entertainment_id = "+crawl_entertainment_info.id+"  and DATE_FORMAT(update_date,'%Y-%m-%d')>='"+format.format(yesterday.getTime())+"' and DATE_FORMAT(update_date,'%Y-%m-%d')<='"+format.format(today.getTime())+"' GROUP BY domain,DATE_FORMAT(update_date,'%Y-%m-%d')" ;
//									System.out.println("执行的sql"+all_amount_sql);
//									dataSourc.rows(all_amount_sql).each { entertainmentData ->
//										if(format.format(yesterday.getTime()).equals(entertainmentData.maxDate)){	// 昨天的播放量
//											yesterdayNum=entertainmentData.play_num;
//											domain = entertainmentData.domain
//										}else if(format.format(today.getTime()).equals(entertainmentData.maxDate)){	// 今天的播放量
//											if(domain!=null && domain.equals(entertainmentData.domain))
//												margin = (entertainmentData.play_num-yesterdayNum)
//											else
//												margin = entertainmentData.play_num
//												
//											domain = null
//											Row = Sheet.createRow(index++);
//											cell1 = Row.createCell(0);
//											cell2 = Row.createCell(1);
//											cell3 = Row.createCell(2);
//											cell4 = Row.createCell(3);
//											cell5 = Row.createCell(4);
//											cell1.setCellValue(ids[j]);
//											cell2.setCellValue(crawl_entertainment_info.name);
//											cell3.setCellValue(entertainmentData.domain);
//											cell4.setCellValue(margin);
//											cell5.setCellValue(format.format(yesterday.getTime()));
//										}
//									}
									// 查询分集的网络播放量
									String all_amount_sql = "SELECT SUM(play_num) play_num,DATE_FORMAT(MAX(update_date),'%Y-%m-%d') maxDate,domain FROM (SELECT MAX(play_num) play_num,update_date,domain FROM (SELECT play_num,update_date,domain,branch_id FROM basic_entertainment_branch_statistic_by_hour a,basic_entertainment_branch_crawl_info_by_hour b,basic_entertainment_crawl_info c WHERE a.branch_id = b.id AND b.entertainment_id =c.id and c.entertainment_id= "+crawl_entertainment_info.id+" and DATE_FORMAT(update_date,'%Y-%m-%d')>'"+format.format(yesterday.getTime())+"' and DATE_FORMAT(update_date,'%Y-%m-%d')<='"+format.format(today.getTime())+"') c GROUP BY branch_id,DATE_FORMAT(update_date,'%Y-%m-%d')) c GROUP BY domain,DATE_FORMAT(update_date,'%Y-%m-%d')"
//									System.out.println("执行的sql"+all_amount_sql);
									dataSourc.rows(all_amount_sql).each { entertainmentData ->
										if(format.format(yesterday.getTime()).equals(entertainmentData.maxDate)){	// 昨天的播放量
											yesterdayNum=entertainmentData.play_num;
											domain = entertainmentData.domain
											margin =  0- yesterdayNum ;
										}else if(format.format(today.getTime()).equals(entertainmentData.maxDate)){	// 今天的播放量
											if(domain!=null && domain.equals(entertainmentData.domain))
												margin = (entertainmentData.play_num-yesterdayNum)
												if(margin<0){
													margin=0;
												}
											else
												margin = entertainmentData.play_num
												if(margin<0){
													margin=0;
												}
												
											domain = null
											 // 覆盖掉昨天的数据
											Row = Sheet.createRow(index++);
											cell1 = Row.createCell(0);
											cell2 = Row.createCell(1);
											cell3 = Row.createCell(2);
											cell4 = Row.createCell(3);
											cell5 = Row.createCell(4);
											cell6 = Row.createCell(5);
											cell1.setCellValue(ids[j]);
											cell2.setCellValue(crawl_entertainment_info.name);
											cell3.setCellValue(entertainmentData.domain);
											cell4.setCellValue(entertainmentData.play_num);
											cell5.setCellValue(margin);
											cell6.setCellValue(format.format(today.getTime()));
//											cell1.setCellValue(res.id);
//											cell2.setCellValue(res.name);
//											cell3.setCellValue(res.domain);
//											cell4.setCellValue(res.play_num-yesterdayNum);
//											
//											String date5_1 = dff.format(res.maxDate);
//											cell5.setCellValue(date5_1);
//											yesterdayNum=res.play_num
										}
									}
								}
							}
						}
							if(its1.value.trim().equals("instalments_amount")){		//分期播放量
								System.out.println("分期播放量");
								def ids=it.object_ids.split(",")
								Sheet Sheet = wb.createSheet("综艺分天分期播放量信息");
								def index=0;
								Row Row = Sheet.createRow(index++);
								Cell cell1 = Row.createCell(0);
								Cell cell2 = Row.createCell(1);
								Cell cell3 = Row.createCell(2);
								Cell cell4 = Row.createCell(3);
								Cell cell5 = Row.createCell(4);
								Cell cell6 = Row.createCell(5);
								cell1.setCellValue("综艺ID");
								cell2.setCellValue("综艺名称");
								cell3.setCellValue("分期名称");
								cell4.setCellValue("网络播放量");
								cell5.setCellValue("增量");
								cell6.setCellValue("日期");
								for(int j=0;j<ids.length;j++){
									// 获得该综艺节目的基础信息
									String select_sql = "SELECT * FROM basic_entertainment_crawl_info WHERE entertainment_id = "+ids[j];
									def crawl_entertainment_info = dataSourc.firstRow(select_sql);

									// 查询该综艺节目有哪些期。
									select_sql = "SELECT broadcast_date FROM basic_entertainment_branch_crawl_info_by_hour WHERE entertainment_id = "+crawl_entertainment_info.id+" GROUP BY broadcast_date";
									def crawl_entertainment_info_Rows = dataSourc.rows(select_sql);

									for(int k = 0 ; k <= days ; k++){
										def yesterdayNum = 0	//前一天的播放量
										SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd")
										Calendar today = Calendar.getInstance();
										today.setTime(format.parse(it.begin_date as String));
										today.add(Calendar.DAY_OF_MONTH,k);
										Calendar yesterday =Calendar.getInstance();		//前一天
										yesterday.setTime(format.parse(it.begin_date as String));
										yesterday.add(Calendar.DAY_OF_MONTH,k-1);
										def domain ;

										crawl_entertainment_info_Rows.each {broadcast_date ->
											// 查询分期的网络播放量
											def margin = 0
											def allNumber = 0;
											String all_amount_sql = "SELECT SUM(play_num) play_num,DATE_FORMAT(MAX(update_date),'%Y-%m-%d') maxDate,domain FROM (SELECT MAX(play_num) play_num,update_date,domain FROM (SELECT  play_num,update_date,domain,branch_id FROM basic_entertainment_branch_statistic_by_hour a,basic_entertainment_branch_crawl_info_by_hour b WHERE a.branch_id = b.id AND b.entertainment_id = "+crawl_entertainment_info.id+" and DATE_FORMAT(update_date,'%Y-%m-%d')>'"+format.format(yesterday.getTime())+"' and DATE_FORMAT(update_date,'%Y-%m-%d')<='"+format.format(today.getTime())+"' and b.broadcast_date = '"+broadcast_date.broadcast_date+"') c GROUP BY branch_id,DATE_FORMAT(update_date,'%Y-%m-%d')) c GROUP BY domain,DATE_FORMAT(update_date,'%Y-%m-%d') "
//											System.out.println("执行的sql"+all_amount_sql);
											dataSourc.rows(all_amount_sql).each { entertainmentData ->
												if(format.format(yesterday.getTime()).equals(entertainmentData.maxDate)){	// 昨天的播放量
													yesterdayNum=entertainmentData.play_num;
													domain = entertainmentData.domain
													margin += (0-yesterdayNum)
												}else if(format.format(today.getTime()).equals(entertainmentData.maxDate)){	// 今天的播放量
													allNumber+=entertainmentData.play_num
													if(domain!=null && domain.equals(entertainmentData.domain))
													margin += (entertainmentData.play_num)
													else
													margin += entertainmentData.play_num

													domain = null
												}
												sqlStr="SELECT a.bid FROM (SELECT d.id,d.`name`,b.id bid,b.title,b.broadcast_date,c.play_num,c.update_date FROM basic_entertainment_info d,basic_entertainment_crawl_info a,basic_entertainment_branch_crawl_info_by_hour b,basic_entertainment_branch_statistic_by_hour c WHERE d.id="+ids[j]+" AND a.id=b.entertainment_id AND b.id=c.branch_id AND d.id=a.entertainment_id AND c.play_num>0 and DATE_FORMAT(c.update_date,'%Y-%m-%d')>='"+it.begin_date+"' and DATE_FORMAT(c.update_date,'%Y-%m-%d')<='"+it.end_date+"' ORDER BY c.update_date DESC ) a GROUP BY a.bid"
												def bids=dataSourc.rows(sqlStr)
												bids.each { bid ->
													//								for(int k=0;k<bid.split(",").length;k++){
													sqlStr="SELECT a.id,a.`name`,a.bid,a.title,a.broadcast_date,max(a.play_num) play_num,max(a.update_date) maxDate FROM (SELECT d.id,d.`name`,b.id bid,b.title,b.broadcast_date,c.play_num,c.update_date FROM basic_entertainment_info d,basic_entertainment_crawl_info a,basic_entertainment_branch_crawl_info_by_hour b,basic_entertainment_branch_statistic_by_hour c WHERE d.id ="+ids[j]+" AND a.id=b.entertainment_id AND b.id=c.branch_id AND d.id=a.entertainment_id AND c.play_num>0 and DATE_FORMAT(c.update_date,'%Y-%m-%d')>='"+format.format(yesterday.getTime())+"' and DATE_FORMAT(c.update_date,'%Y-%m-%d')<='"+it.end_date+"' and b.id="+bid.bid+" ORDER BY c.update_date DESC ) a GROUP BY a.bid,DATE_FORMAT(a.update_date,'%Y-%m-%d') "
													def result=dataSourc.rows(sqlStr)
													result.each { res ->
														if(yesterdayNum==null){	//第一次
															yesterdayNum=res.play_num
														}else{				//第二次及以后
															Row = Sheet.createRow(index++);
															cell1 = Row.createCell(0);
															cell2 = Row.createCell(1);
															cell3 = Row.createCell(2);
															cell4 = Row.createCell(3);
															cell5 = Row.createCell(4);
															cell1.setCellValue(res.id);
															cell2.setCellValue(res.name);
															cell3.setCellValue(res.title);
															cell4.setCellValue(res.play_num-yesterdayNum);

															String date5_1 = dff.format(res.maxDate);
															cell5.setCellValue(date5_1);
															yesterdayNum=res.play_num
														}
														Row = Sheet.createRow(index++);
														cell1 = Row.createCell(0);
														cell2 = Row.createCell(1);
														cell3 = Row.createCell(2);
														cell4 = Row.createCell(3);
														cell5 = Row.createCell(4);
														cell6 = Row.createCell(5);
														cell1.setCellValue(ids[j]);
														cell2.setCellValue(crawl_entertainment_info.name);
														cell3.setCellValue((broadcast_date.broadcast_date as String)+"期");
														cell4.setCellValue(allNumber);
														cell5.setCellValue(margin);
														cell6.setCellValue(format.format(today.getTime()));
													}
												}
											}
										}
									}
								}
						}
					}
				}
				if(it.groupName.equals("influence")){	//影响力
					sqlStr="SELECT c.`value` FROM basic_project_requirement_info a,`basic_requiremen_codeRequirement_relation` b,code_localtion_requirement c where a.id=b.requeirment_id and b.codeRequeirment_id=c.id and a.id=${projectRequiementId} and c.groupName='"+it.groupName+"' and c.parent_id<>0 order by c.id"
					def resultOne=dataSource.rows(sqlStr)
					resultOne.each { its1 ->
						if(its1.value.trim().equals("media_attention")){		//媒体关注度
							sqlStr="SELECT b.id,b.`name`,a.hot_rate,a.record_date FROM `domain_entertainment_hot_records` a,basic_entertainment_info b where a.entertainment_id=b.id and a.entertainment_id in ("+it.object_ids+") and a.record_date>='"+it.begin_date+"' and a.record_date<='"+it.end_date+"'"
							def result=dataSourceIreport.rows(sqlStr)
							Sheet Sheet = wb.createSheet("媒体关注度");
							def index=0;
							Row Row = Sheet.createRow(index++);
							Cell cell1 = Row.createCell(0);
							Cell cell2 = Row.createCell(1);
							Cell cell3 = Row.createCell(2);
							Cell cell4 = Row.createCell(3);
							cell1.setCellValue("综艺ID");
							cell2.setCellValue("综艺名称");
							cell3.setCellValue("媒体关注度");
							cell4.setCellValue("日期");
							result.each { its2 ->
								Row = Sheet.createRow(index++);
								cell1 = Row.createCell(0);
								cell2 = Row.createCell(1);
								cell3 = Row.createCell(2);
								cell4 = Row.createCell(3);
								cell1.setCellValue(its2.id);
								cell2.setCellValue(its2.name);
								cell3.setCellValue(its2.hot_rate);
								
								Date date4=(Date)its2.record_date
								String date4_1 = dff.format(date4);
								cell4.setCellValue(date4_1);
								
//								cell4.setCellValue(dt.fmtDate(its2.record_date as String,"yyyy-MM-dd"));
							}
						}
						if(its1.value.trim().equals("public_influence")){		//公众影响力
							sqlStr="SELECT b.id,b.`name`,a.public_influence,a.record_date FROM `domain_entertainment_hot_records` a,basic_entertainment_info b where a.entertainment_id=b.id and a.entertainment_id in ("+it.object_ids+") and a.record_date>='"+it.begin_date+"' and a.record_date<='"+it.end_date+"'"
							def result=dataSourceIreport.rows(sqlStr)
							Sheet Sheet = wb.createSheet("公众影响力");
							def index=0;
							Row Row = Sheet.createRow(index++);
							Cell cell1 = Row.createCell(0);
							Cell cell2 = Row.createCell(1);
							Cell cell3 = Row.createCell(2);
							Cell cell4 = Row.createCell(3);
							cell1.setCellValue("综艺ID");
							cell2.setCellValue("综艺名称");
							cell3.setCellValue("公众影响力");
							cell4.setCellValue("日期");
							result.each {
								Row = Sheet.createRow(index++);
								cell1 = Row.createCell(0);
								cell2 = Row.createCell(1);
								cell3 = Row.createCell(2);
								cell4 = Row.createCell(3);
								cell1.setCellValue(it.id);
								cell2.setCellValue(it.name);
								cell3.setCellValue(it.public_influence);
								
								Date date4=(Date)it.record_date
								String date4_1 = dff.format(date4);
								cell4.setCellValue(date4_1);
								
//								cell4.setCellValue(dt.fmtDate(it.record_date as String,"yyyy-MM-dd"));
							}
							
						}
						
						if(its1.value.trim().equals("exposed")){		//门户曝光时长
							
						}
						
						if(its1.value.trim().equals("headline")){		//媒体头条量
							sqlStr="SELECT object_id,sum(count) sc,count_date FROM `domain_expose_680` where object_id in ("+it.object_ids+") and count_date>='"+it.begin_date+"' and count_date<='"+it.end_date+"' GROUP BY object_id,count_date order by object_id,count_date desc "
							def result=dataSourceCobarReputation.rows(sqlStr)
							Sheet Sheet = wb.createSheet("媒体头条量");
							def index=0;
							Row Row = Sheet.createRow(index++);
							Cell cell1 = Row.createCell(0);
							Cell cell2 = Row.createCell(1);
							Cell cell3 = Row.createCell(2);
							Cell cell4 = Row.createCell(3);
							cell1.setCellValue("综艺ID");
							cell2.setCellValue("综艺名称");
							cell3.setCellValue("媒体头条量");
							cell4.setCellValue("日期");
							result.each { its2 ->
								String s1="select name from basic_entertainment_info where id="+its2.object_id
								def objectName=dataSourc.firstRow(s1).name
								Row = Sheet.createRow(index++);
								cell1 = Row.createCell(0);
								cell2 = Row.createCell(1);
								cell3 = Row.createCell(2);
								cell4 = Row.createCell(3);
								cell1.setCellValue(its2.object_id);
								cell2.setCellValue(objectName);
								cell3.setCellValue(its2.sc);
								
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
								Date date4 = sdf.parse(its2.count_date);
								String date4_1 = dff.format(date4);
								cell4.setCellValue(date4_1);
								
//								cell4.setCellValue(dt.fmtDate(its2.count_date as String,"yyyy-MM-dd"));
							}
						}
						if(its1.value.trim().equals("article")){		//热门文章
							def ids=it.object_ids.split(",")
							DateTools dt=new DateTools()
							def startDate=dt.fmtDate(it.begin_date as String, "yyyy-MM-dd").getTime();
							def endDate=dt.fmtDate(it.end_date as String, "yyyy-MM-dd").getTime();
							Sheet Sheet = wb.createSheet("热门文章");
							def index=0;
							Row Row = Sheet.createRow(index++);
							Cell cell1 = Row.createCell(0);
							Cell cell2 = Row.createCell(1);
							Cell cell3 = Row.createCell(2);
							Cell cell4 = Row.createCell(3);
							Cell cell5 = Row.createCell(4);
							Cell cell6 = Row.createCell(5);
							Cell cell7 = Row.createCell(6);
							Cell cell8 = Row.createCell(7);
							Cell cell9 = Row.createCell(8);
							Cell cell10 = Row.createCell(9);
							Cell cell11 = Row.createCell(10);
							Cell cell12 = Row.createCell(11);
							
							cell1.setCellValue("综艺ID");
							cell2.setCellValue("综艺名称");
							cell3.setCellValue("文章的url");
							cell4.setCellValue("文章的标题");
							cell5.setCellValue("此文章的相似文章数");
							cell6.setCellValue("阅读数");
							cell7.setCellValue("转载数");
							cell8.setCellValue("收藏数");
							cell9.setCellValue("喜欢数");
							cell10.setCellValue("播放数");
							cell11.setCellValue("日期");
							cell12.setCellValue("关注度");
							for(int j=0;j<ids.length;j++){
								def id=ids[j]
								def httpurl="${STAR_ARTICLE_SERVICE}?app_key=ireport&&method=getTopNArticles&ontologytypes=434&objectId=${id}&objectType=680&beginTime=${startDate}&endTime=${endDate}&maxNum=20";
								URL url=new URL(httpurl);
								URLConnection hpCon=url.openConnection()
								hpCon.connect();
								BufferedReader inbuffer=new BufferedReader(new InputStreamReader(hpCon.getInputStream()))
								String res="";
								String line="";
								while((line=inbuffer.readLine())!=null){
									res+=line;
								}
								inbuffer.close();
								Object obj=JSONValue.parse(res);
								JSONArray array=(JSONArray)obj;
								def articlist=[];
								int articlisCount = 0
								String s1="select name from basic_entertainment_info where id="+id
								def objectName=dataSourc.firstRow(s1).name
								if(array){
									DateTools dtools=new DateTools()
									array.each { its2 ->
										Row = Sheet.createRow(index++);
										cell1 = Row.createCell(0);
										cell2 = Row.createCell(1);
										cell3 = Row.createCell(2);
										cell4 = Row.createCell(3);
										cell5 = Row.createCell(4);
										cell6 = Row.createCell(5);
										cell7 = Row.createCell(6);
										cell8 = Row.createCell(7);
										cell9 = Row.createCell(8);
										cell10 = Row.createCell(9);
										cell11 = Row.createCell(10);
										cell12 = Row.createCell(11);
										
										cell1.setCellValue(its2.object_id);
										cell2.setCellValue(objectName);
										cell3.setCellValue(its2.url);
										cell4.setCellValue(its2.title);
										cell5.setCellValue(its2.SimilarCount);
										cell6.setCellValue(its2.nRead);
										cell7.setCellValue(its2.nRepost);
										cell8.setCellValue(its2.nSubscribe);
										cell9.setCellValue(its2.nLike);
										cell10.setCellValue(its2.nPlay);
										
										SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
										Date date11= sdf.parse(its2.publish_date);
										String date11_1 = dff.format(date11);
										cell11.setCellValue(date11_1);
										
//										cell11.setCellValue(dt.fmtDate(its2.publish_date as String,"yyyy-MM-dd"));
										cell12.setCellValue(its2.hotrate);
									}
								}
							}
						}
						if(its1.value.trim().equals("microblogging")){		//热门微博
							def ids=it.object_ids.split(",")
							DateTools dt=new DateTools()
							def startDate=dt.fmtDate(it.begin_date as String, "yyyy-MM-dd").getTime();
							def endDate=dt.fmtDate(it.end_date as String, "yyyy-MM-dd").getTime();
							Sheet Sheet = wb.createSheet("热门微博");
							def index=0;
							Row Row = Sheet.createRow(index++);
							Cell cell1 = Row.createCell(0);
							Cell cell2 = Row.createCell(1);
							Cell cell3 = Row.createCell(2);
							Cell cell4 = Row.createCell(3);
							Cell cell5 = Row.createCell(4);
							Cell cell6 = Row.createCell(5);
							Cell cell7 = Row.createCell(6);
							Cell cell8 = Row.createCell(7);
							Cell cell9 = Row.createCell(8);
							Cell cell10 = Row.createCell(9);
							Cell cell11 = Row.createCell(10);
							Cell cell12 = Row.createCell(11);
							
							cell1.setCellValue("综艺ID");
							cell2.setCellValue("综艺名称");
							cell3.setCellValue("文章的url");
							cell4.setCellValue("文章的标题");
							cell5.setCellValue("此文章的相似文章数");
							cell6.setCellValue("阅读数");
							cell7.setCellValue("转载数");
							cell8.setCellValue("收藏数");
							cell9.setCellValue("喜欢数");
							cell10.setCellValue("播放数");
							cell11.setCellValue("日期");
							cell12.setCellValue("关注度");
							
							for(int j=0;j<ids.length;j++){
								def id=ids[j]
								def httpurl="${STAR_ARTICLE_SERVICE}?app_key=ireport&&method=getTopNArticles&ontologytypes=424&objectId=${id}&objectType=680&beginTime=${startDate}&endTime=${endDate}&maxNum=20";
								URL url=new URL(httpurl);
								URLConnection hpCon=url.openConnection()
								hpCon.connect();
								BufferedReader inbuffer=new BufferedReader(new InputStreamReader(hpCon.getInputStream()))
								String res="";
								String line="";
								while((line=inbuffer.readLine())!=null){
									res+=line;
								}
								inbuffer.close();
								Object obj=JSONValue.parse(res);
								JSONArray array=(JSONArray)obj;
								def articlist=[];
								int articlisCount = 0
								String s1="select name from basic_entertainment_info where id="+id
								def objectName=dataSourc.firstRow(s1).name
								if(array){
									DateTools dtools=new DateTools()
									array.each { its2 ->
										Row = Sheet.createRow(index++);
										cell1 = Row.createCell(0);
										cell2 = Row.createCell(1);
										cell3 = Row.createCell(2);
										cell4 = Row.createCell(3);
										cell5 = Row.createCell(4);
										cell6 = Row.createCell(5);
										cell7 = Row.createCell(6);
										cell8 = Row.createCell(7);
										cell9 = Row.createCell(8);
										cell10 = Row.createCell(9);
										cell11 = Row.createCell(10);
										cell12 = Row.createCell(11);
										
										cell1.setCellValue(its2.object_id);
										cell2.setCellValue(objectName);
										cell3.setCellValue(its2.url);
										cell4.setCellValue(its2.title);
										cell5.setCellValue(its2.SimilarCount);
										cell6.setCellValue(its2.nRead);
										cell7.setCellValue(its2.nRepost);
										cell8.setCellValue(its2.nSubscribe);
										cell9.setCellValue(its2.nLike);
										cell10.setCellValue(its2.nPlay);
										
										SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
										Date date11= sdf.parse(its2.publish_date);
										String date11_1 = dff.format(date11);
										cell11.setCellValue(date11_1);
										
//										cell11.setCellValue(dt.fmtDate(its2.publish_date as String,"yyyy-MM-dd"));
										cell12.setCellValue(its2.hotrate);
									}
								}
							}
						}
					}
				}
				if(it.groupName.equals("reputation")){	//口碑
					sqlStr="SELECT c.`value` FROM basic_project_requirement_info a,`basic_requiremen_codeRequirement_relation` b,code_localtion_requirement c where a.id=b.requeirment_id and b.codeRequeirment_id=c.id and a.id=${projectRequiementId} and c.groupName='"+it.groupName+"' and c.parent_id<>0 order by c.id"
					def resultOne=dataSource.rows(sqlStr)
					resultOne.each { its1 ->
						if(its1.value.trim().equals("dimension")){		//各维度提及量
							sqlStr="SELECT entertainment_id,evaluate_type,sum(positive_num)+sum(negative_num)+sum(neutral_num) tjl,publish_date FROM `ireport_entertainment_praise_statistic_day` where entertainment_id in ("+it.object_ids+") and publish_date>='"+it.begin_date+"' and publish_date<='"+it.end_date+"'  GROUP BY entertainment_id,evaluate_type,publish_date order by entertainment_id,publish_date,evaluate_type"
							def result=dataSourceMycatReputation.rows(sqlStr)
							Sheet Sheet = wb.createSheet("各维度提及量");
							def index=0;
							Row Row = Sheet.createRow(index++);
							Cell cell1 = Row.createCell(0);
							Cell cell2 = Row.createCell(1);
							Cell cell3 = Row.createCell(2);
							Cell cell4 = Row.createCell(3);
							Cell cell5 = Row.createCell(4);
							cell1.setCellValue("综艺ID");
							cell2.setCellValue("综艺名称");
							cell3.setCellValue("维度");
							cell4.setCellValue("提及量");
							cell5.setCellValue("时间");
							result.each { its2 ->
								String s1="select name from basic_entertainment_info where id="+its2.entertainment_id
								def objectName=dataSourc.firstRow(s1).name
								String s2="SELECT name FROM  basic_ent_category where cid="+its2.evaluate_type
								def originName=dataSourc.firstRow(s2).name
								Row = Sheet.createRow(index++);
								cell1 = Row.createCell(0);
								cell2 = Row.createCell(1);
								cell3 = Row.createCell(2);
								cell4 = Row.createCell(3);
								cell5 = Row.createCell(4);
								cell1.setCellValue(its2.entertainment_id);
								cell2.setCellValue(objectName);
								cell3.setCellValue(originName);
								cell4.setCellValue(its2.tjl);
								
								Date date5=(Date)its2.publish_date
								String date5_1 = dff.format(date5);
								cell5.setCellValue(date5_1);
								
//								cell5.setCellValue(dt.fmtDate(its2.publish_date as String,"yyyy-MM-dd"));
							}
						}
						if(its1.value.trim().equals("favorable_rate")){		//好评率
							sqlStr="SELECT entertainment_id,sum(positive_num) hp,sum(negative_num) cp,sum(neutral_num) zp,CONCAT((sum(positive_num)/(sum(positive_num)+sum(negative_num)))*100,'%') hpl,publish_date,evaluate_type FROM `ireport_entertainment_praise_statistic_day` where entertainment_id in ("+it.object_ids+") and publish_date>='"+it.begin_date+"' and publish_date<='"+it.end_date+"' GROUP BY entertainment_id,publish_date,evaluate_type order by entertainment_id,publish_date"
							def result=dataSourceMycatReputation.rows(sqlStr)
							Sheet Sheet = wb.createSheet("好评率");
							def index=0;
							Row Row = Sheet.createRow(index++);
							Cell cell1 = Row.createCell(0);
							Cell cell2 = Row.createCell(1);
							Cell cell3 = Row.createCell(2);
							Cell cell4 = Row.createCell(3);
							Cell cell5 = Row.createCell(4);
							Cell cell6 = Row.createCell(5);
							Cell cell7 = Row.createCell(6);
							Cell cell8 = Row.createCell(7);
							cell1.setCellValue("综艺ID");
							cell2.setCellValue("综艺名称");
							cell3.setCellValue("维度");
							cell4.setCellValue("好评");
							cell5.setCellValue("差评");
							cell6.setCellValue("中评");
							cell7.setCellValue("好评率");
							cell8.setCellValue("时间");
							result.each {its2 ->
								String s1="select name from basic_entertainment_info where id="+its2.entertainment_id
								def objectName=dataSourc.firstRow(s1).name
								Row = Sheet.createRow(index++);
								cell1 = Row.createCell(0);
								cell2 = Row.createCell(1);
								cell3 = Row.createCell(2);
								cell4 = Row.createCell(3);
								cell5 = Row.createCell(4);
								cell6 = Row.createCell(5);
								cell7 = Row.createCell(6);
								cell8 = Row.createCell(7);
								cell1.setCellValue(its2.entertainment_id);
								cell2.setCellValue(objectName);
								
								// 去计算这个维度ID对应的名称
								def evaluateInfo = dataSource.firstRow("select * From ent_domain.`basic_ent_category` where  cid = " + its2.evaluate_type);
								cell3.setCellValue(evaluateInfo?.name);
								
								cell4.setCellValue(its2.hp);
								cell5.setCellValue(its2.cp);
								cell6.setCellValue(its2.zp);
								cell7.setCellValue(its2.hpl);
								
								Date date6=(Date)its2.publish_date
								String date6_1 = dff.format(date6);
								cell8.setCellValue(date6_1);
								
//								cell6.setCellValue(dt.fmtDate(its2.publish_date as String,"yyyy-MM-dd"));
							}
						}
						if(its1.value.trim().equals("hot_word")){		//热词
							sqlStr="SELECT entertainment_id,word,publish_date FROM `domain_entertainment_hotword_count` where entertainment_id in ("+it.object_ids+") and publish_date>='"+it.begin_date+"' and publish_date <='"+it.end_date+"' GROUP BY entertainment_id,word,publish_date"
							def result=dataSourceMycatReputation.rows(sqlStr)
							Sheet Sheet = wb.createSheet("热词");
							def index=0;
							Row Row = Sheet.createRow(index++);
							Cell cell1 = Row.createCell(0);
							Cell cell2 = Row.createCell(1);
							Cell cell3 = Row.createCell(2);
							Cell cell4 = Row.createCell(3);
							cell1.setCellValue("综艺ID");
							cell2.setCellValue("综艺名称");
							cell3.setCellValue("热词");
							cell4.setCellValue("日期");
							result.each { its2 ->
								String s1="select name from basic_entertainment_info where id="+its2.entertainment_id
								def objectName=dataSourc.firstRow(s1).name
								Row = Sheet.createRow(index++);
								cell1 = Row.createCell(0);
								cell2 = Row.createCell(1);
								cell3 = Row.createCell(2);
								cell4 = Row.createCell(3);
								cell1.setCellValue(its2.entertainment_id);
								cell2.setCellValue(objectName);
								cell3.setCellValue(its2.word);
								
								Date date4=(Date)its2.publish_date
								String date4_1 = dff.format(date4);
								cell4.setCellValue(date4_1);
								
//								cell4.setCellValue(dt.fmtDate(its2.publish_date as String,"yyyy-MM-dd"));
							}
						}
						if(its1.value.trim().equals("negative_positive")){		//正负短语
							sqlStr="select entertainment_id,phrase,CASE emotion_type when 1 then '正' ELSE '负' end et,publish_date from domain_entertainment_phrase_count where entertainment_id in ("+it.object_ids+") and publish_date>='"+it.begin_date+"' and publish_date <='"+it.end_date+"' GROUP BY entertainment_id,publish_date,phrase"
							def result=dataSourceMycatReputation.rows(sqlStr)
							Sheet Sheet = wb.createSheet("正负短语");
							def index=0;
							Row Row = Sheet.createRow(index++);
							Cell cell1 = Row.createCell(0);
							Cell cell2 = Row.createCell(1);
							Cell cell3 = Row.createCell(2);
							Cell cell4 = Row.createCell(3);
							Cell cell5 = Row.createCell(4);
							cell1.setCellValue("综艺ID");
							cell2.setCellValue("综艺名称");
							cell3.setCellValue("短语");
							cell4.setCellValue("正负");
							cell5.setCellValue("日期");
							result.each { its2 ->
								String s1="select name from basic_entertainment_info where id="+its2.entertainment_id
								def objectName=dataSourc.firstRow(s1).name
								Row = Sheet.createRow(index++);
								cell1 = Row.createCell(0);
								cell2 = Row.createCell(1);
								cell3 = Row.createCell(2);
								cell4 = Row.createCell(3);
								cell5 = Row.createCell(4);
								cell1.setCellValue(its2.entertainment_id);
								cell2.setCellValue(objectName);
								cell3.setCellValue(its2.phrase);
								cell4.setCellValue(its2.et);
								
								Date date5=(Date)its2.publish_date
								String date5_1 = dff.format(date5);
								cell5.setCellValue(date5_1);
								
//								cell5.setCellValue(dt.fmtDate(its2.publish_date as String,"yyyy-MM-dd"));
							}
						}
						if(its1.value.trim().equals("raw")){		//原始内容
							Sheet Sheet = wb.createSheet("原始内容");
							def index=0;
							Row Row = Sheet.createRow(index++);
							Cell cell1 = Row.createCell(0);
							Cell cell2 = Row.createCell(1);
							Cell cell3 = Row.createCell(2);
							Cell cell4 = Row.createCell(3);
							Cell cell5 = Row.createCell(4);
							cell1.setCellValue("综艺ID");
							cell2.setCellValue("综艺名称");
							cell3.setCellValue("句子");
							cell4.setCellValue("热词");
							cell5.setCellValue("短语");
							
							def ids=it.object_ids.split(",")
							for(int j=0;j<ids.length;j++){
								String s1="select name from basic_entertainment_info where id="+ids[j]
								def objectName=dataSourc.firstRow(s1).name
								def mongoDB= MongoDbConnectConfig.getMongoDb(ids[j] as int, "entertainment", null)//getMongoDb(tvId)
								BasicDBObject query=new BasicDBObject();
								query.put("object_id", ids[j])
								query.put("object_type","680")
								BasicDBObject lte=new BasicDBObject();
								lte.put('$gte', "${it.begin_date} 00:00:00".toString())
								lte.put('$lte', "${it.end_date} 23:59:59".toString())
								query.put("time",lte)
								def re = mongoDB.find(query).limit(500)
								def result = re.toArray()
								result.each { its2 ->
									Row = Sheet.createRow(index++);
									cell1 = Row.createCell(0);
									cell2 = Row.createCell(1);
									cell3 = Row.createCell(2);
									cell4 = Row.createCell(3);
									cell5 = Row.createCell(4);
									
									cell1.setCellValue(ids[j]);
									cell2.setCellValue(objectName);
									cell3.setCellValue(its2.sentence);
									cell4.setCellValue(its2.hotWords as String);
									cell5.setCellValue(its2?.phrase == null ?"":(its2?.phrase as String))
								}
							}
						}
					}
				}
				
				if(it.groupName.equals("hoster")){ //主创及表演评估-主持人
					Sheet Sheet = wb.createSheet("主创及表演评估-主持人")
					def index=0;
					Row Row = Sheet.createRow(index++);
					Cell cell1 = Row.createCell(0);
					Cell cell2 = Row.createCell(1);
					Cell cell3 = Row.createCell(2);
					Cell cell4 = Row.createCell(3);
					Cell cell5 = Row.createCell(4);
					Cell cell6 = Row.createCell(5);
					Cell cell7 = Row.createCell(6);
					Cell cell8 = Row.createCell(7);
					Cell cell9 = Row.createCell(8);
					Cell cell10 = Row.createCell(9);
					Cell cell11 = Row.createCell(10);
					Cell cell12 = Row.createCell(11);

					cell1.setCellValue("综艺ID");
					cell2.setCellValue("综艺名称");
					cell3.setCellValue("明星ID");
					cell4.setCellValue("明星主创信息");
					cell5.setCellValue("姓名");
					cell6.setCellValue("维度");
					cell7.setCellValue("好评");
					cell8.setCellValue("差评");
					cell9.setCellValue("中评");
					cell10.setCellValue("提及量");
					cell11.setCellValue("好评率");
					cell12.setCellValue("日期");
					String s1="select a.id,a.`name` enterName,b.artist_id,c.`name` mxName,a.first_broadcast_date from basic_entertainment_info a ,basic_artist_enterainment b,basic_artist_info c where a.id=b.enterainment_id and b.relation=1126 and b.artist_id=c.id and a.id in ("+it.object_ids+") "
					def result1=dataSourc.rows(s1)
					result1.each { its3 ->	//循环主持人
						String s2="SELECT star_id,evaluate_type,publish_date,sum(positive_num) hp,sum(negative_num) cp,sum(neutral_num) zp,sum(positive_num)+sum(negative_num)+sum(neutral_num) tjl,CONCAT((sum(positive_num)/(sum(positive_num)+sum(negative_num)))*100,'%') hpl FROM `ireport_star_praise_statistic_day` where star_id ="+its3.artist_id+" and publish_date>='"+it.begin_date+"' and publish_date <='"+it.end_date+"' GROUP BY evaluate_type,publish_date"
						def result2=dataSourceMycatReputation.rows(s2)
						result2.each { res->
							String s3="SELECT name FROM  basic_ent_category where cid="+res.evaluate_type
							def originName=dataSourc.firstRow(s3).name
							Row = Sheet.createRow(index++);
							cell1 = Row.createCell(0);
							cell2 = Row.createCell(1);
							cell3 = Row.createCell(2);
							cell4 = Row.createCell(3);
							cell5 = Row.createCell(4);
							cell6 = Row.createCell(5);
							cell7 = Row.createCell(6);
							cell8 = Row.createCell(7);
							cell9 = Row.createCell(8);
							cell10 = Row.createCell(9);
							cell11 = Row.createCell(10);
							cell12 = Row.createCell(11);

							cell1.setCellValue(its3.id);
							cell2.setCellValue(its3.enterName);
							cell3.setCellValue(its3.artist_id);
							cell4.setCellValue("主持人");
							cell5.setCellValue(its3.mxName);
							cell6.setCellValue(originName);
							cell7.setCellValue(res.hp);
							cell8.setCellValue(res.cp);
							cell9.setCellValue(res.zp);
							cell10.setCellValue("提及量："+res.tjl);
							if(res.tjl as int ==0){
								cell11.setCellValue("好评率：0");
							}else{
								cell11.setCellValue("好评率："+res.hpl);
							}
							String date11_1 = dff.format(res.publish_date);
							cell12.setCellValue(date11_1);
						}
					}
				}
				
				if(it.groupName.equals("guester")){ //主创及表演评估-嘉宾
					Sheet Sheet = wb.createSheet("主创及表演评估-嘉宾")
					def index=0;
					Row Row = Sheet.createRow(index++);
					Cell cell1 = Row.createCell(0);
					Cell cell2 = Row.createCell(1);
					Cell cell3 = Row.createCell(2);
					Cell cell4 = Row.createCell(3);
					Cell cell5 = Row.createCell(4);
					Cell cell6 = Row.createCell(5);
					Cell cell7 = Row.createCell(6);
					Cell cell8 = Row.createCell(7);
					Cell cell9 = Row.createCell(8);
					Cell cell10 = Row.createCell(9);
					Cell cell11 = Row.createCell(10);
					Cell cell12 = Row.createCell(11);
						
					cell1.setCellValue("综艺ID");
					cell2.setCellValue("综艺名称");
					cell3.setCellValue("明星ID");
					cell4.setCellValue("明星主创信息");
					cell5.setCellValue("姓名");
					cell6.setCellValue("维度");
					cell7.setCellValue("好评");
					cell8.setCellValue("差评");
					cell9.setCellValue("中评");
					cell10.setCellValue("提及量");
					cell11.setCellValue("好评率");
					cell12.setCellValue("日期");
					
					String s1="select a.id,a.`name` enterName,b.artist_id,c.`name` mxName,a.first_broadcast_date from basic_entertainment_info a ,basic_artist_enterainment b,basic_artist_info c where a.id=b.enterainment_id and b.relation=1126 and b.artist_id=c.id and a.id in ("+it.object_ids+") "
					def result1=dataSourc.rows(s1)
					result1.each { its3 ->	//循环嘉宾
						String s2="SELECT star_id,evaluate_type,publish_date,sum(positive_num) hp,sum(negative_num) cp,sum(neutral_num) zp,sum(positive_num)+sum(negative_num)+sum(neutral_num) tjl,CONCAT((sum(positive_num)/(sum(positive_num)+sum(negative_num)))*100,'%') hpl FROM `ireport_star_praise_statistic_day` where star_id ="+its3.artist_id+" and publish_date>='"+it.begin_date+"' and publish_date <='"+it.end_date+"' GROUP BY evaluate_type,publish_date"
						def result2=dataSourceMycatReputation.rows(s2)
						result2.each { res->
							String s3="SELECT name FROM  basic_ent_category where cid="+res.evaluate_type
							def originName=dataSourc.firstRow(s3).name
							Row = Sheet.createRow(index++);
							cell1 = Row.createCell(0);
							cell2 = Row.createCell(1);
							cell3 = Row.createCell(2);
							cell4 = Row.createCell(3);
							cell5 = Row.createCell(4);
							cell6 = Row.createCell(5);
							cell7 = Row.createCell(6);
							cell8 = Row.createCell(7);
							cell9 = Row.createCell(8);
							cell10 = Row.createCell(9);
							cell11 = Row.createCell(10);
							cell12 = Row.createCell(11);

							cell1.setCellValue(its3.id);
							cell2.setCellValue(its3.enterName);
							cell3.setCellValue(its3.artist_id);
							cell4.setCellValue("嘉宾");
							cell5.setCellValue(its3.mxName);
							cell6.setCellValue(originName);
							cell7.setCellValue(res.hp);
							cell8.setCellValue(res.cp);
							cell9.setCellValue(res.zp);
							cell10.setCellValue("提及量："+res.tjl);
							if(res.tjl as int ==0){
								cell11.setCellValue("好评率：0");
							}else{
								cell11.setCellValue("好评率："+res.hpl);
							}
							String date11_1 = dff.format(res.publish_date);
							cell12.setCellValue(date11_1);
						}
					}
				}
			}
		}
		
		
		//将文件上传到服务器
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		wb.write(out);
		String[] uriArr;
		try {
			uriArr = iFastDfs.upload(out.toByteArray(),"xls",null)
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		String down_url = (uriArr[0]+"/"+uriArr[1]+"?attname="+requirementName+".xls");
		wb.close();
		out.close();
		// 将下载地址更新到数据库中
		int changeCount = dataSource.executeUpdate("update basic_project_requirement_info set down_url = ${down_url},requirement_state=3 where id = ${projectRequiementId}")
		println "提交生成excel并保存到数据库成功!${projectRequiementId}"
		}catch(Exception e){
			println "----------------->"+e.getMessage()
			e.printStackTrace();
			
		}
		sendMailForProjectRequirement(projectRequiementId);
		
	}
	
	/**
	 * 当需求完成的时候发送邮件.
	 * @param projectRequirementId
	 */
	public void sendMailForProjectRequirement(def projectRequirementId){
		// 获得需求信息
		def projectRequirement = this.getRequirementById(projectRequirementId);
		// 获得项目信息
		def project = projectService.getProjectInfo(projectRequirement.project_id);
		// 获得用户信息
		def dataSource_operation = new Sql(dataSource_operation);
		String sql = "SELECT p.id,p.name,p.email FROM operation.`operation_person` p  WHERE  p.id = '${projectRequirement.operation_person}'";
		def result = dataSource_operation.firstRow(sql);
		String content = "${result.name}，你好，你的《${project.name}》项目的<${projectRequirement.name}>数据需求已完成。请前往数据需求处查看并下载，或点击以下链接直接下载：<a href='"+grailsApplication.config.grails.app.nginx+"/"+projectRequirement.down_url+"'>《点击下载》</a>";
		// 发送邮件提醒用户获得信息
		List<String> destEmailArr = new ArrayList<String>();
		destEmailArr.add(result.email)
		SendEmailUtil.sendEmail(projectRequirement.name+"数据需求完成通知", content, destEmailArr);
		
		System.out.println("邮件发送成功");
	}
	
	/**
	 * 得到需求基础信息及对象ID和名称
	 * @author HTYang
	 * @date 2016-5-4 下午4:20:06
	 * @param requirementId
	 * @return
	 */
	def getRequirementNameById(def requirementId){
		
		def rMap=[:]
		if(requirementId==null)return null;
		def dataSourceIplay = new Sql(dataSource_iplay)
		def dataSourc= new Sql(dataSource)
		def result = dataSourceIplay.firstRow("select  id,`name`,object_type,object_ids,begin_date,end_date,requirement_state,operation_person,operation_date,down_url,project_id,special_requirement_mes,is_execute,cinemaIds,submit_time From basic_project_requirement_info where id = '${requirementId}' ")
		def ids		//ID数组
		String names=""	//对象名称数组
		def sqlStr	//sql语句
		if(result.object_type==4){ //电影
			ids=result.object_ids.split(",")
			ids.each { ides ->
				sqlStr="select name from basic_movie_info where id="+ides
				def result2=dataSourc.rows(sqlStr)
				result2.each { nam ->
					names+="'"+nam.name+"',"
				}
			}
		}else if(result.object_type==5){	//电视剧
			ids=result.object_ids.split(",")
			ids.each { ides ->
				sqlStr="select name from basic_teleplay_info where id="+ides
				def result2=dataSourc.rows(sqlStr)
				result2.each { nam ->
					names+="'"+nam.name+"',"
				}
			}
		}else if(result.object_type==7){	//明星
			ids=result.object_ids.split(",")
			ids.each { ides ->
				sqlStr="select name from basic_artist_info where id="+ides
				def result2=dataSourc.rows(sqlStr)
				result2.each { nam ->
					names+="'"+nam.name+"',"
				}
			}
		}else if(result.object_type==680){	//综艺
			ids=result.object_ids.split(",")
			ids.each { ides ->
				sqlStr="select name from basic_entertainment_info where id="+ides
				def result2=dataSourc.rows(sqlStr)
				result2.each { nam ->
					names+="'"+nam.name+"',"
				}
			}
		}
		rMap.put("result", result);
		rMap.put("names", names.length()>0?names?.substring(0, names.length()-1):"")
		return rMap ;
	}
	
	
	/**
	 * 得到需求模板基础信息及对象ID和名称
	 * @author czz
	 * @date 2016-5-4 下午4:20:06
	 * @param requirementId
	 * @return
	 */
	def getRequirementTemplateNameById(def requirementId){
		
		def rMap=[:]
		if(requirementId==null)return null;
		def dataSourceIplay = new Sql(dataSource_iplay)
		def dataSourc= new Sql(dataSource)
		def result = dataSourceIplay.firstRow("select  id,`name`,object_type,object_ids,begin_date,end_date,operation_person,operation_date,special_requirement_mes,cinemaIds From basic_project_requirement_templet_info where id = '${requirementId}' ")
		def ids		//ID数组
		String names=""	//对象名称数组
		def sqlStr	//sql语句
		if(result.object_type==4){ //电影
			ids=result.object_ids.split(",")
			ids.each { ides ->
				sqlStr="select name from basic_movie_info where id="+ides
				def result2=dataSourc.rows(sqlStr)
				result2.each { nam ->
					names+="'"+nam.name+"',"
				}
			}
		}else if(result.object_type==5){	//电视剧
			ids=result.object_ids.split(",")
			ids.each { ides ->
				sqlStr="select name from basic_teleplay_info where id="+ides
				def result2=dataSourc.rows(sqlStr)
				result2.each { nam ->
					names+="'"+nam.name+"',"
				}
			}
		}else if(result.object_type==7){	//明星
			ids=result.object_ids.split(",")
			ids.each { ides ->
				sqlStr="select name from basic_artist_info where id="+ides
				def result2=dataSourc.rows(sqlStr)
				result2.each { nam ->
					names+="'"+nam.name+"',"
				}
			}
		}else if(result.object_type==680){	//综艺
			ids=result.object_ids.split(",")
			ids.each { ides ->
				sqlStr="select name from basic_entertainment_info where id="+ides
				def result2=dataSourc.rows(sqlStr)
				result2.each { nam ->
					names+="'"+nam.name+"',"
				}
			}
		}
		rMap.put("result", result);
		rMap.put("names", names.length()>0?names?.substring(0, names.length()-1):"")
		return rMap ;
	}
	
	/**
	 * 更新需求信息
	 * @author HTYang
	 * @date 2016-5-5 下午1:55:24
	 * @param params
	 * @param object_type
	 * @param userID
	 * @param time
	 * @return
	 */
	def editRequiement(def params,def object_type,def userID){
		def mes="保存成功！！！"
		try{
			String submit_time = "NULL" ;
			if("2".equals(params.prStatue)){
				submit_time = System.currentTimeMillis();
			}
			String updateSql= "update basic_project_requirement_info set `name`='${params.requirementName}',object_type=${object_type},object_ids='${params.queryIds}',begin_date='${params.begin_time}',end_date='${params.end_time}',requirement_state='${params.prStatue}',operation_person=${userID} ,submit_time=${submit_time} where id=${params.projectRequirementId} "
			def dataSource = new Sql(dataSource_iplay)
			dataSource.execute(updateSql);
			updateSql="delete from basic_requiremen_codeRequirement_relation where requeirment_id=${params.projectRequirementId}"
			dataSource.execute(updateSql);
			
			
			if(params.rating35){	//35城收视率
				updateSql = "INSERT INTO basic_requiremen_codeRequirement_relation (`requeirment_id`,`codeRequeirment_id`) VALUES ('${params.projectRequirementId}','${params.rating35}');" ;
				dataSource.executeInsert(updateSql);
			}
			
			if(params.rating50){	//50城收视率
				updateSql = "INSERT INTO basic_requiremen_codeRequirement_relation (`requeirment_id`,`codeRequeirment_id`) VALUES ('${params.projectRequirementId}','${params.rating50}');" ;
				dataSource.executeInsert(updateSql);
			}
			
			if(params.portion35){	//35城市场份额
				updateSql = "INSERT INTO basic_requiremen_codeRequirement_relation (`requeirment_id`,`codeRequeirment_id`) VALUES ('${params.projectRequirementId}','${params.portion35}');" ;
				dataSource.executeInsert(updateSql);
			}
			
			if(params.portion50){	//50城市场份额
				updateSql = "INSERT INTO basic_requiremen_codeRequirement_relation (`requeirment_id`,`codeRequeirment_id`) VALUES ('${params.projectRequirementId}','${params.portion50}');" ;
				dataSource.executeInsert(updateSql);
			}
			
			if(params.valuesIdsAmount){	//网络播放量
				def s=params.valuesIdsAmount.split(",");
				for(int i=0;i<s.length;i++){
					String requeirmentId = s[i]
					updateSql = "INSERT INTO basic_requiremen_codeRequirement_relation (`requeirment_id`,`codeRequeirment_id`) VALUES ('${params.projectRequirementId}','${requeirmentId}');" ;
					dataSource.executeInsert(updateSql);
				}
			}
			
			if(params.valuesIdsInfluence){	//影响力
				def s=params.valuesIdsInfluence.split(",");
				for(int i=0;i<s.length;i++){
					String requeirmentId = s[i]
					updateSql = "INSERT INTO basic_requiremen_codeRequirement_relation (`requeirment_id`,`codeRequeirment_id`) VALUES ('${params.projectRequirementId}','${requeirmentId}');" ;
					dataSource.executeInsert(updateSql);
				}
			}
			
			if(params.valuesIdsReputation){	//口碑
				def s=params.valuesIdsReputation.split(",");
				for(int i=0;i<s.length;i++){
					String requeirmentId = s[i]
					updateSql = "INSERT INTO basic_requiremen_codeRequirement_relation (`requeirment_id`,`codeRequeirment_id`) VALUES ('${params.projectRequirementId}','${requeirmentId}');" ;
					dataSource.executeInsert(updateSql);
				}
			}
			
			if(params.valuesIdsdirection){	//导演
				def s=params.valuesIdsdirection.split(",");
				for(int i=0;i<s.length;i++){
					String requeirmentId = s[i]
					updateSql = "INSERT INTO basic_requiremen_codeRequirement_relation (`requeirment_id`,`codeRequeirment_id`) VALUES ('${params.projectRequirementId}','${requeirmentId}');" ;
					dataSource.executeInsert(updateSql);
				}
			}
			
			if(params.valuesIdsscriptwriter){	//编剧
				def s=params.valuesIdsscriptwriter.split(",");
				for(int i=0;i<s.length;i++){
					String requeirmentId = s[i]
					updateSql = "INSERT INTO basic_requiremen_codeRequirement_relation (`requeirment_id`,`codeRequeirment_id`) VALUES ('${params.projectRequirementId}','${requeirmentId}');" ;
					dataSource.executeInsert(updateSql);
				}
			}
			
			if(params.valuesIdscomedienne){	//演员
				def s=params.valuesIdscomedienne.split(",");
				for(int i=0;i<s.length;i++){
					String requeirmentId = s[i]
					updateSql = "INSERT INTO basic_requiremen_codeRequirement_relation (`requeirment_id`,`codeRequeirment_id`) VALUES ('${params.projectRequirementId}','${requeirmentId}');" ;
					dataSource.executeInsert(updateSql);
				}
			}
			 
			if(params.valuesIdshoster){	//主持人
				def s=params.valuesIdshoster.split(",");
				for(int i=0;i<s.length;i++){
					String requeirmentId = s[i]
					updateSql = "INSERT INTO basic_requiremen_codeRequirement_relation (`requeirment_id`,`codeRequeirment_id`) VALUES ('${params.projectRequirementId}','${requeirmentId}');" ;
					dataSource.executeInsert(updateSql);
				}
			}
			
			if(params.valuesIdsguester){	//嘉宾
				def s=params.valuesIdsguester.split(",");
				for(int i=0;i<s.length;i++){
					String requeirmentId = s[i]
					updateSql = "INSERT INTO basic_requiremen_codeRequirement_relation (`requeirment_id`,`codeRequeirment_id`) VALUES ('${params.projectRequirementId}','${requeirmentId}');" ;
					dataSource.executeInsert(updateSql);
				}
			}
			
//			if(params.valuesIdsWritten){	//主创及表演评估
//				def s=params.valuesIdsWritten.split(",");
//				for(int i=0;i<s.length;i++){
//					String requeirmentId = s[i]
//					updateSql = "INSERT INTO basic_requiremen_codeRequirement_relation (`requeirment_id`,`codeRequeirment_id`) VALUES ('${params.projectRequirementId}','${requeirmentId}');" ;
//					dataSource.executeInsert(updateSql);
//				}
//			}
		}catch(Exception e){
			mes="更新失败，请联系管理员！！"
			e.printStackTrace()
		}
		return mes
	}
	
	
	/**
	 * 更新需求模板信息
	 * @param params
	 * @param object_type
	 * @param userID
	 * @param time
	 * @return
	 */
	def editProjectRequirementTemplateInfo(def params,def object_type,def userID){
		def mes="保存成功！！！"
		try{
			String updateSql= "update basic_project_requirement_templet_info set `name`='${params.requirementName}',object_type=${object_type},object_ids='${params.queryIds}',begin_date='${params.begin_time}',end_date='${params.end_time}' where id=${params.projectRequirementId} "
			def dataSource = new Sql(dataSource_iplay)
			dataSource.execute(updateSql);
			updateSql="delete from basic_requiremen_templet_codeRequirement_relation where requeirment_templet_id=${params.projectRequirementId}"
			dataSource.execute(updateSql);
			
			
			if(params.valuesIds35){	//35城收视率
				def s=params.valuesIds35.split(",");
				for(int i=0;i<s.length;i++){
					String requeirmentId = s[i]
					updateSql = "INSERT INTO basic_requiremen_templet_codeRequirement_relation (`requeirment_templet_id`,`codeRequeirment_id`) VALUES ('${params.projectRequirementId}','${requeirmentId}');" ;
					dataSource.executeInsert(updateSql);
				}
			}
			
			if(params.valuesIds50){	//50城收视率
				def s=params.valuesIds50.split(",");
				for(int i=0;i<s.length;i++){
					String requeirmentId = s[i]
					updateSql = "INSERT INTO basic_requiremen_templet_codeRequirement_relation (`requeirment_templet_id`,`codeRequeirment_id`) VALUES ('${params.projectRequirementId}','${requeirmentId}');" ;
					dataSource.executeInsert(updateSql);
				}
			}
			
			if(params.valuesIdsPortion){	//市场份额
				def s=params.valuesIdsPortion.split(",");
				for(int i=0;i<s.length;i++){
					String requeirmentId = s[i]
					updateSql = "INSERT INTO basic_requiremen_templet_codeRequirement_relation (`requeirment_templet_id`,`codeRequeirment_id`) VALUES ('${params.projectRequirementId}','${requeirmentId}');" ;
					dataSource.executeInsert(updateSql);
				}
			}
			
			if(params.valuesIdsAmount){	//网络播放量
				def s=params.valuesIdsAmount.split(",");
				for(int i=0;i<s.length;i++){
					String requeirmentId = s[i]
					updateSql = "INSERT INTO basic_requiremen_templet_codeRequirement_relation (`requeirment_templet_id`,`codeRequeirment_id`) VALUES ('${params.projectRequirementId}','${requeirmentId}');" ;
					dataSource.executeInsert(updateSql);
				}
			}
			
			if(params.valuesIdsInfluence){	//影响力
				def s=params.valuesIdsInfluence.split(",");
				for(int i=0;i<s.length;i++){
					String requeirmentId = s[i]
					updateSql = "INSERT INTO basic_requiremen_templet_codeRequirement_relation (`requeirment_templet_id`,`codeRequeirment_id`) VALUES ('${params.projectRequirementId}','${requeirmentId}');" ;
					dataSource.executeInsert(updateSql);
				}
			}
			
			if(params.valuesIdsReputation){	//口碑
				def s=params.valuesIdsReputation.split(",");
				for(int i=0;i<s.length;i++){
					String requeirmentId = s[i]
					updateSql = "INSERT INTO basic_requiremen_templet_codeRequirement_relation (`requeirment_templet_id`,`codeRequeirment_id`) VALUES ('${params.projectRequirementId}','${requeirmentId}');" ;
					dataSource.executeInsert(updateSql);
				}
			}
			
			if(params.valuesIdsdirection){	//导演
				def s=params.valuesIdsdirection.split(",");
				for(int i=0;i<s.length;i++){
					String requeirmentId = s[i]
					updateSql = "INSERT INTO basic_requiremen_templet_codeRequirement_relation (`requeirment_templet_id`,`codeRequeirment_id`) VALUES ('${params.projectRequirementId}','${requeirmentId}');" ;
					dataSource.executeInsert(updateSql);
				}
			}
			
			if(params.valuesIdsscriptwriter){	//编剧
				def s=params.valuesIdsscriptwriter.split(",");
				for(int i=0;i<s.length;i++){
					String requeirmentId = s[i]
					updateSql = "INSERT INTO basic_requiremen_templet_codeRequirement_relation (`requeirment_templet_id`,`codeRequeirment_id`) VALUES ('${params.projectRequirementId}','${requeirmentId}');" ;
					dataSource.executeInsert(updateSql);
				}
			}
			
			if(params.valuesIdscomedienne){	//演员
				def s=params.valuesIdscomedienne.split(",");
				for(int i=0;i<s.length;i++){
					String requeirmentId = s[i]
					updateSql = "INSERT INTO basic_requiremen_templet_codeRequirement_relation (`requeirment_templet_id`,`codeRequeirment_id`) VALUES ('${params.projectRequirementId}','${requeirmentId}');" ;
					dataSource.executeInsert(updateSql);
				}
			}
			
			if(params.valuesIdshoster){	//主持人
				def s=params.valuesIdshoster.split(",");
				for(int i=0;i<s.length;i++){
					String requeirmentId = s[i]
					updateSql = "INSERT INTO basic_requiremen_templet_codeRequirement_relation (`requeirment_templet_id`,`codeRequeirment_id`) VALUES ('${params.projectRequirementId}','${requeirmentId}');" ;
					dataSource.executeInsert(updateSql);
				}
			}
			
			if(params.valuesIdsguester){	//嘉宾
				def s=params.valuesIdsguester.split(",");
				for(int i=0;i<s.length;i++){
					String requeirmentId = s[i]
					updateSql = "INSERT INTO basic_requiremen_templet_codeRequirement_relation (`requeirment_templet_id`,`codeRequeirment_id`) VALUES ('${params.projectRequirementId}','${requeirmentId}');" ;
					dataSource.executeInsert(updateSql);
				}
			}
			
//			if(params.valuesIdsWritten){	//主创及表演评估
//				def s=params.valuesIdsWritten.split(",");
//				for(int i=0;i<s.length;i++){
//					String requeirmentId = s[i]
//					updateSql = "INSERT INTO basic_requiremen_templet_codeRequirement_relation (`requeirment_templet_id`,`codeRequeirment_id`) VALUES ('${params.projectRequirementId}','${requeirmentId}');" ;
//					dataSource.executeInsert(updateSql);
//				}
//			}
		}catch(Exception e){
			mes="更新失败，请联系管理员！！"
			e.printStackTrace()
		}
		return mes
	}
	
	/**
	 * 保存特殊需求
	 * @param params
	 * @param userId
	 * @return
	 */
	def updateProjectRequirementInfoSpecial(def params,def userId){
		String submit_time = "NULL" ;
		if("1".equals(params.requirement_state)){
			submit_time = System.currentTimeMillis();
		}
		String updateSql = "update `iplay`.`basic_project_requirement_info` set `name` = '${params.requirementName}',`object_type` = '${params.objectType}',`begin_date` = '${params.begin_time}' ,`end_date` = '${params.end_time}',`special_requirement_mes` = '${params.special_requirement_mes}',requirement_state='${params.requirement_state}',submit_time=${submit_time} where id = ${params.projectRequirementId}" ;
		def dataSource = new Sql(dataSource_iplay)
		def result = dataSource.executeUpdate(updateSql);
		return "成功";
	}
	/**
	 * 保存特殊需求模板
	 * @param params
	 * @param userId
	 * @return
	 */
	def updateProjectRequirementTemplateInfoSpecial(def params,def userId){
		String updateSql = "update `iplay`.`basic_project_requirement_templet_info` set `name` = '${params.requirementName}',`object_type` = '${params.objectType}',`begin_date` = '${params.begin_time}' ,`end_date` = '${params.end_time}',`special_requirement_mes` = '${params.special_requirement_mes}' where id = ${params.projectRequirementId}" ;
		def dataSource = new Sql(dataSource_iplay)
		def result = dataSource.executeUpdate(updateSql);
		return "成功";
	}
	
	/**
	 * 保存明星需求
	 * @param params
	 * @param userId
	 * @return
	 */
	def updateProjectRequirementInfoStar(def params,def userId){
		
		String submit_time = "NULL" ;
		if("2".equals(params.requirement_state)){
			submit_time = System.currentTimeMillis();
		}
		String updateSql = "update `iplay`.`basic_project_requirement_info` set `name` = '${params.requirementName}',`object_type` = '${params.objectType}',`object_ids` = '${params.objectIds}',`begin_date` = '${params.begin_time}',`end_date` = '${params.end_time}',requirement_state='${params.requirement_state}',submit_time=${submit_time} where  id = ${params.projectRequirementId} " ;
		// 判断是否输入了影院信息
		if(params.cinemaIds!=null && (!"".equals(params.cinemaIds))){
			updateSql = "update `iplay`.`basic_project_requirement_info` set `name` = '${params.requirementName}',`object_type` = '${params.objectType}',`object_ids` = '${params.objectIds}',`begin_date` = '${params.begin_time}',`end_date` = '${params.end_time}',requirement_state='${params.requirement_state}',`cinemaIds` = '${params.cinemaIds}',submit_time=${submit_time} where  id = ${params.projectRequirementId} " ;
		}
		
		def dataSource = new Sql(dataSource_iplay);
		def result = dataSource.executeUpdate(updateSql)
		
		// 删除本需求的所有码表
		updateSql="delete from basic_requiremen_codeRequirement_relation where requeirment_id=${params.projectRequirementId}"
		dataSource.execute(updateSql);
		
		// 获得项目需求ID
		String projectRequiementId = params.projectRequirementId
		def ids = params?.ids ;
		if(ids instanceof String[]){
			ids.each {
				updateSql = "INSERT INTO basic_requiremen_codeRequirement_relation (`requeirment_id`,`codeRequeirment_id`) VALUES ('${projectRequiementId}','${it}');" ;
				dataSource.executeInsert(updateSql);
			}
		}
		else{
			updateSql = "INSERT INTO basic_requiremen_codeRequirement_relation (`requeirment_id`,`codeRequeirment_id`) VALUES ('${projectRequiementId}','${ids}');" ;
			dataSource.executeInsert(updateSql);
		}
		return "成功";
	}
	
	/**
	 * 保存明星需求模板
	 * @param params
	 * @param userId
	 * @return
	 */
	def updateProjectRequirementTemplateInfoStar(def params,def userId){
		
		String updateSql = "update `iplay`.`basic_project_requirement_templet_info` set `name` = '${params.requirementName}',`object_type` = '${params.objectType}',`object_ids` = '${params.objectIds}',`begin_date` = '${params.begin_time}',`end_date` = '${params.end_time}' where  id = ${params.projectRequirementId} " ;
		// 判断是否输入了影院信息
		if(params.cinemaIds!=null && (!"".equals(params.cinemaIds))){
			updateSql = "update `iplay`.`basic_project_requirement_templet_info` set `name` = '${params.requirementName}',`object_type` = '${params.objectType}',`object_ids` = '${params.objectIds}',`begin_date` = '${params.begin_time}',`end_date` = '${params.end_time}',`cinemaIds` = '${params.cinemaIds}' where  id = ${params.projectRequirementId} " ;
		}
		
		def dataSource = new Sql(dataSource_iplay);
		def result = dataSource.executeUpdate(updateSql)
		
		// 删除本需求的所有码表
		updateSql="delete from basic_requiremen_templet_codeRequirement_relation where requeirment_templet_id=${params.projectRequirementId}"
		dataSource.execute(updateSql);
		
		// 获得项目需求ID
		String projectRequiementId = params.projectRequirementId
		def ids = params?.ids ;
		if(ids instanceof String[]){
			ids.each {
				updateSql = "INSERT INTO basic_requiremen_templet_codeRequirement_relation (`requeirment_templet_id`,`codeRequeirment_id`) VALUES ('${projectRequiementId}','${it}');" ;
				dataSource.executeInsert(updateSql);
			}
		}
		else{
			updateSql = "INSERT INTO basic_requiremen_templet_codeRequirement_relation (`requeirment_templet_id`,`codeRequeirment_id`) VALUES ('${projectRequiementId}','${ids}');" ;
			dataSource.executeInsert(updateSql);
		}
		return "成功";
	}
	
	
	def shareProjectRequirementTemplateToAnthorAnalyst(def request,def params){
		// 保存分析师信息
		def dataSource = new Sql(dataSource_iplay)
		def analysts = params?.analysts ;
		if(analysts instanceof String[]){
			for (String analystId : params.analysts) {
				String insertSql = "INSERT INTO `iplay`.`basic_project_requirement_templet_info` (`name`,`begin_date`,`end_date`,`object_type`,`object_ids`,`operation_person`,`operation_date`,`special_requirement_mes`,`cinemaIds`) SELECT `name`,`begin_date`,`end_date`,`object_type`,`object_ids`,${analysts} as `operation_person`,`operation_date`,`special_requirement_mes`,`cinemaIds` FROM basic_project_requirement_templet_info WHERE id =  ${params?.id}" ;
				def result = dataSource.executeInsert(insertSql);
				String requirementId = result.get(0).get(0);
				insertSql = "INSERT INTO `iplay`.`basic_requiremen_templet_codeRequirement_relation` (`requeirment_templet_id`, `codeRequeirment_id`) SELECT ${requirementId} as `requeirment_templet_id`, `codeRequeirment_id` FROM basic_requiremen_templet_codeRequirement_relation WHERE requeirment_templet_id =  " + params?.id;
				dataSource.executeInsert(insertSql);
			}
		}
		else{
			String insertSql = "INSERT INTO `iplay`.`basic_project_requirement_templet_info` (`name`,`begin_date`,`end_date`,`object_type`,`object_ids`,`operation_person`,`operation_date`,`special_requirement_mes`,`cinemaIds`) SELECT `name`,`begin_date`,`end_date`,`object_type`,`object_ids`,${analysts} as `operation_person`,`operation_date`,`special_requirement_mes`,`cinemaIds` FROM basic_project_requirement_templet_info WHERE id =  ${params?.id}" ;
			def result = dataSource.executeInsert(insertSql);
			String requirementId = result.get(0).get(0)
			insertSql = "INSERT INTO `iplay`.`basic_requiremen_templet_codeRequirement_relation` (`requeirment_templet_id`, `codeRequeirment_id`) SELECT ${requirementId} as `requeirment_templet_id`, `codeRequeirment_id` FROM basic_requiremen_templet_codeRequirement_relation WHERE requeirment_templet_id =  " + params?.id;
			dataSource.executeInsert(insertSql);
		}
		return "成功!"
	}
	
}