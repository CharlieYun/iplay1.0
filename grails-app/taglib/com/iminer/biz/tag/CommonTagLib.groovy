package com.iminer.biz.tag

import groovy.sql.Sql

import org.apache.commons.lang.StringUtils
import org.joda.time.DateTime



class CommonTagLib {
	
	def dataSource_operation ;
	def dataSource_iplay
	//数据源
	def dataSource ;
	
	// 实体标签select
	def deptSelect={attrs->
		
		StringBuilder sb = new StringBuilder("<select name=\""+attrs["name"]+"\"  id=\""+attrs["id"]+"\" ");
		
		if(attrs["disabled"]){
			sb.append("disabled = \"disabled\"");
		}
		
		sb.append(">")
		
		def source = new Sql(dataSource_operation)
		
		
		
//		List<String> depts = ["市场部","研究中心","技术研发","数据管理","产品部"];
		
		source.rows("SELECT * FROM privilege_organization WHERE NAME IS NOT NULL AND NAME <> '' ORDER BY pid").each {
			String checked = ""
			if((it.id).equals(attrs["value"]+""))
			checked = "selected = \"selected\"";
			sb.append("<option value=\""+it.id+"\" "+checked+">").append(it.name).append("</option>");
		}
		
		sb.append("</select>");
		out<<sb.toString();
	}
	
	/**
	 * 根据字典配置得到下拉框标签
	 */
	def dictionarySelect={attrs->
		StringBuilder sb = new StringBuilder("<select name=\""+attrs["name"]+"\" id=\""+attrs["id"]+"\" ");
		if(attrs["disabled"]){
			sb.append("disabled=\"disabled\"");
		}
		if(attrs["method"]){
			sb.append(attrs["method"])
		}
		sb.append(">");
		def dataSource = new Sql(dataSource_iplay);
		String sql="select dictionaryValue,dictionaryText from basic_dictionary where dictionaryKey='"+attrs["dicKey"]+"' order by dictionaryRank ASC";
		if(attrs["default"]){
			sb.append("<option value=\"\">").append(attrs["default"]).append("</option>");
		}
		dataSource.rows(sql).each {
			String checked="";
			if(it.dictionaryValue.equals(attrs["value"]+""))
			checked="selected=\"selected\"";
			sb.append("<option value=\"").append(it.dictionaryValue).append("\"").append(checked).append(">").append(it.dictionaryText).append("</option>");
		}
		sb.append("</select>");
		out<<(sb.toString());
	}
	
	
	def dictionaryText={attrs->
		
		def source = new Sql(dataSource_iplay)
		
		StringBuffer sb = new StringBuffer();
		if (!StringUtils.isEmpty(attrs.value as String)&&!StringUtils.isEmpty(attrs.key)) {
			def dictionary = source.firstRow("select * From basic_dictionary where dictionaryKey = ? and dictionaryValue = ? ",[attrs.key,attrs.value])
			sb.append(dictionary?.dictionaryText);
		}
		out<<sb.toString();
	}
	
	
	
	// 需求标签
	def demandCheck={attrs->
		StringBuffer sb = new StringBuffer();
		def source = new Sql(dataSource_iplay)
		if (!StringUtils.isEmpty(attrs.name)&&!StringUtils.isEmpty(attrs.key)) {
			if (StringUtils.isEmpty(attrs.name)) {
				attrs.name = "";
			}
			if (StringUtils.isEmpty(attrs.key)) {
				attrs.key = "";
			}
			def disabled = "" ;
			if("disabled".equals(attrs.disabled)){
				 disabled = "disabled=\"disabled\"";
			}
			def key=attrs.key;
			def sql="SELECT id,display_text,value FROM `code_localtion_requirement` where groupName='"+key+"' and is_show=1 and parent_id<>0 ";
			def result=source.rows(sql);
			String disabledStr="";
			for(int i=0;i<result.size();i++) {
				String text = result.get(i).get("display_text");
				String value= result.get(i).get("value");
				String id=result.get(i).get("id");
				String value_id=value+"|"+id
				String brStr="";
				if((i+1)%5==0){
					brStr="<br/>"
				}
//				String[] values = attrs.key.split(",");
				String[] values
				boolean isSel = false ;
				if(StringUtils.isNotEmpty(attrs.values)){
					values = attrs.values.split(",");
					for (int j = 0; j < values.length; j++) {
						if (values[j].trim().equals(id)){
							sb.append("<label style='margin-left:10px;'><input type='checkbox' "+disabled+" name='"+attrs.name+"' value='"+value_id
	
									+"'  checked='checked'  />"+text+"</label>&nbsp;&nbsp;"+brStr);
							isSel = true ;
							break;
						}
					}
				}
				if(!isSel){
					sb.append("<label style='margin-left:10px;'><input type='checkbox' "+disabled+" name='"+attrs.name+"' value='"+value_id
							+"'/>"+text+"</label>&nbsp;&nbsp;"+brStr);
				}
			}
		}
		out<<sb.toString();
		
	}
	
	def demandCheckTest={attrs->
		StringBuffer sb = new StringBuffer();
		def source = new Sql(dataSource_iplay)
		if (!StringUtils.isEmpty(attrs.name)&&!StringUtils.isEmpty(attrs.groupName)) {
			if (StringUtils.isEmpty(attrs.name)) {
				attrs.name = "";
			}
			if (StringUtils.isEmpty(attrs.groupName)) {
				attrs.groupName = "";
			}
			def disabled = "" ;
			if("disabled".equals(attrs.disabled))
			 	disabled = "disabled=\"disabled\"";
			
			def method=""
			if(!"".equals(attrs.method) && attrs.method!=null){
				method = attrs.method;
			}	 
				 
			def groupName=attrs.groupName
			def sql="SELECT id,display_text,value FROM `code_localtion_requirement` where groupName='"+groupName+"' and parent_id<>0 and is_show = 1";
			def result=source.rows(sql);
			String disabledStr="";
			for(int i=0;i<result.size();i++) {
				String text = result.get(i).get("display_text");
				String codeId=result.get(i).get("id");
				String hvalue=result.get(i).get("value");
				String brStr="";
				if((i+1)%5==0){
					brStr="<br/>";
				}
				String[] values = attrs.values.split(",");
				boolean isSel = false ;
				for (int j = 0; j < values.length; j++) {
					if (values[j].trim().equals(codeId)){
						sb.append("<label style='margin-left:10px;'><input type='checkbox' "+disabled+" "+method+" name='"+attrs.name+"' hvalue='"+hvalue+"'  value='"+codeId

								+"'  checked='checked'  />"+text+"</label>&nbsp;&nbsp;"+brStr);
						isSel = true ;
						break;
					}
				}
				if(!isSel){
					sb.append("<label style='margin-left:10px;'><input type='checkbox' "+disabled+" "+method+" name='"+attrs.name+"' hvalue='"+hvalue+"' value='"+codeId
							+"'/>"+text+"</label>&nbsp;&nbsp;"+brStr);
				}
			}
		}
		out<<sb.toString();
	}
	
	
	
	def personInfo={attrs->
		StringBuffer sb = new StringBuffer();
		if(!StringUtils.isEmpty(attrs.personId as String)) {
			def dataSource_operation = new Sql(dataSource_operation);
			String sql = "SELECT p.id,p.name,p.common_duties,o.name AS deptName  FROM operation.`operation_person` p ,operation.`privilege_organization` o WHERE p.organization_id = o.id AND p.id = '${attrs.personId}'";
			def result = dataSource_operation.firstRow(sql);
			sb.append(result[attrs.name]);
		}
		out<<sb.toString();
	}
	
	
	// 根据部门ID查找该部门的所有人员
	def personsByDeptId={attrs->
		
		
		StringBuffer sb = new StringBuffer();
		if (!StringUtils.isEmpty(attrs.name)&&!StringUtils.isEmpty(attrs.deptId)) {
			if (StringUtils.isEmpty(attrs.value)) {
				attrs.value = "";
			}
			if (StringUtils.isEmpty(attrs.event)) {
				attrs.event = "";
			}
			
			def dataSource_operation = new Sql(dataSource_operation);

			
			
			String disabledStr="";
			if(!StringUtils.isEmpty(attrs.disabled)){
				if("disabled".equals(attrs.disabled)
						||"true".equals(attrs.disabled)){
					disabledStr=" disabled='disabled'";
				}
			}
			String sql = "SELECT id,name FROM operation_person WHERE organization_id IN (SELECT id FROM privilege_organization WHERE id like '${attrs.deptId}%') order by organization_id ";
			// 获得所有operation的用户
			int i = 0 ;
			dataSource_operation.rows(sql).each{
				
				String brStr="";
				if((i+1)%5==0){
					brStr="<br/>";
				}
				String[] values = attrs.value.split(",");
				boolean isSel = false ;
				for (int j = 0; j < values.length; j++) {
					if (values[j].trim().equals(it.id as String) || "checkedAll".equals(attrs.defaultChecked)){
						sb.append("<label><input type='checkbox' "+attrs.event+disabledStr+" name='"+attrs.name+"' value='"+it.id
								+"'  checked='checked'  />"+it.name+"</label>&nbsp;&nbsp;"+brStr);
						isSel = true ;
						break;
					}
				}
				if(!isSel){
					sb.append("<label><input type='checkbox' "+attrs.event+disabledStr+" name='"+attrs.name+"' value='"+it.id
							+"'/>"+it.name+"</label>&nbsp;&nbsp;"+brStr);
				}
				i++;
			}
		}
		out<<sb.toString();
		
	}

	def namesOfObjectIds={attrs->
		StringBuilder sb = new StringBuilder("");
		def source = new Sql(dataSource);
		if(attrs.objectType==null||"".equals(attrs.objectType) || attrs.objectIds == null || "".equals(attrs.objectIds)){
			out<<sb.toString();
		}
		def objectType = attrs.objectType ;
		def objectIds = attrs.objectIds?.split(",")
		if(objectType==4){
			
			for (String objectId : objectIds) {
				String select_sql = "select  * from basic_movie_info where id = ${objectId}"
				def movie = source.firstRow(select_sql);
				if(movie!=null)
				sb.append("<span style='border: 1px solid; padding: 5px;display: inline-block;margin: 0 5px 5px;' id="+objectId+">"+movie.name+"&nbsp;<a onclick='teleplay_data_id_delete("+objectId+")' class='glyphicon glyphicon-remove'></a></span>");
			}
		}
		else if(objectType == 5){
			for (String objectId : objectIds) {
				String select_sql = "select  * from basic_teleplay_info where id = ${objectId}"
				def movie = source.firstRow(select_sql);
				if(movie!=null)
				sb.append("<span style='border: 1px solid; padding: 5px;display: inline-block;margin: 0 5px 5px;' id="+objectId+">"+movie.name+"&nbsp;<a onclick='teleplay_data_id_delete("+objectId+")' class='glyphicon glyphicon-remove'></a></span>");
			}
		}else if(objectType ==7 ){
			for (String objectId : objectIds) {
				String select_sql = "select  * from basic_artist_info where id = ${objectId}"
				def movie = source.firstRow(select_sql);
				if(movie!=null)
				sb.append("<span style='border: 1px solid; padding: 5px;display: inline-block;margin: 0 5px 5px;' id="+objectId+">"+movie.name+"&nbsp;<a onclick='teleplay_data_id_delete("+objectId+")' class='glyphicon glyphicon-remove'></a></span>");
			}
		}
		else if(objectType == 680){
			for (String objectId : objectIds) {
				String select_sql = "select  * from basic_entertainment_info where id = ${objectId}"
				def movie = source.firstRow(select_sql);
				if(movie!=null)
				sb.append("<span style='border: 1px solid; padding: 5px;display: inline-block;margin: 0 5px 5px;' id="+objectId+">"+movie.name+"&nbsp;<a onclick='teleplay_data_id_delete("+objectId+")' class='glyphicon glyphicon-remove'></a></span>");
			}
		}
		
		out<<sb.toString();
		
	}
	
	def fmtDateTime={attrs->
		if(attrs.value == null || "".equals(attrs.value)){
			out<<""
		}
		else{
			DateTime dt = new DateTime(attrs.value);
			if(attrs.fmtStr  == null || "".equals(attrs.fmtStr)){
				out << dt.toString("yyyy-MM-dd");
			}
			else{
				out << dt.toString(fmtStr);
			}
		}
	}
	
}
