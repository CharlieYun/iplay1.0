package com.iminer.biz.utils

import java.text.DecimalFormat

class RuleTools {
	def totalTableCount=200
	/**
	 * 获得分表名称的后缀名称
	 * @param objectID 对象id
	 * @return 名称为_number
	 */
	def getTableSuffixName(int objectID){
		int num = ((objectID + "").hashCode() & 0x7FFFFFFF) % totalTableCount;
		DecimalFormat df = new DecimalFormat("00");
		String suffixName = "_" + num;//df.format(num);
		return suffixName;
	}
	
	public static void main(String[] x){
		RuleTools rt=new RuleTools();
//		println rt.getTableSuffixName(79383)
//		println rt.getTableSuffixName(58)
		
//		DecimalFormat df = new DecimalFormat("00");
//		println  df.format(124);
	}
}
