package com.iminer.utils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @author liudongri
 * @version 1.0 2012-8-17
 */
public class DateTools {
	/**
	 * 国际标准
	 * 一周是从 周日开始计算
	 */
	public static int WEEK_OF_ISO=0;
	/**
	 * 中国标准
	 * 一周是重周一开始计算
	 */
	public static int WEEK_OF_GBK=1;
	private int local_GMP=DateTools.WEEK_OF_GBK;
	/**
	 * 构造函数默认的每周是从 周一到周日
	 */
	public DateTools(){
		
	}
	/**
	 * 构造函数
	 * @param GMP 标准类型 WEEK_OF_GBK中国标准 WEEK_OF_ISO国际标准
	 */
	public DateTools(int GMP){
		this.local_GMP=GMP;
	}
	/**
	 * 获得当前时间并且以String类型返回。
	 * @return 
	 * 
	 * @return 返回的时间格式yyyyMMddHHmmss。
	 */
	public String getDate() {
		return getDate("yyyyMMddHHmmss");
	}


	/**
	 * 获得当前时间。时间格式根据fmt进行格式化
	 * 
	 * @param fmt
	 *            日期格式
	 * @return 返回的时间格式根据fmt格式生成
	 */
	public String getDate(String fmt) {
		Date myDate = new Date(System.currentTimeMillis());
		SimpleDateFormat sDateformat = new SimpleDateFormat(fmt);
		return sDateformat.format(myDate).toString();
	}

	

	
	
	
	private Calendar getCal(String strdate, String fmt) {
		Calendar cal = null;
		try {
			if ((strdate != null) && (fmt != null)) {

				SimpleDateFormat nowDate = new SimpleDateFormat(fmt);
				Date d = nowDate.parse(strdate, new ParsePosition(0));
				if (d != null) {
					cal = Calendar.getInstance();
					cal.clear();
					cal.setTime(d);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return cal;
	}

	/**
	 * 计算 当前日期是本年的第几周
	 * 
	 * @param strdate
	 *            计算日期
	 * @param fmt
	 *            日期格式
	 * @return strdate说在年份中有几周，如果strdate的格式为yyyy则默认为当年的1月1日
	 */
	public int getWeekOfYear(String strdate, String fmt) {
		int ret = -1;
		try {
			if ((strdate != null) && (fmt != null)) {

				Calendar cal = getCal(strdate, fmt);
				if (cal != null) {

					ret = cal.get(Calendar.WEEK_OF_YEAR);
					if(this.local_GMP==DateTools.WEEK_OF_GBK&&cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
						ret-=1;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 计算给定日期所在周的全部日期
	 * 
	 * @param strdate
	 *            指定日期
	 * @param oldfmt
	 *            自定日期格式
	 * @param newfmt
	 *            输出格式
	 * @return 结果数组形式输出所在周的日期。指定日期的格式为yyyy或yyyyMM将计算出当年第一周或当月第一周的日期
	 */
	public String[] getWeekDay(String strdate, String oldfmt, String newfmt) {
		String[] weekday = new String[7];
		try {
			if ((strdate != null) && (oldfmt != null) && (newfmt != null)) {
				Calendar cal = getCal(strdate, oldfmt);
				if (cal != null) {
					int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
					dayOfWeek = -dayOfWeek + 1+this.local_GMP;
					if (dayOfWeek > 0) {
						dayOfWeek = -6;
					}
					cal.add(Calendar.DATE, dayOfWeek);
					SimpleDateFormat sdf = new SimpleDateFormat(newfmt);
					weekday[0] = sdf.format(cal.getTime());
					for (int i = 1; i < 7; i++) {
						cal.add(5, 1);
						weekday[i] = sdf.format(cal.getTime());
					}
				}
			}
		} catch (IndexOutOfBoundsException iobe) {
			iobe.printStackTrace();
		}
		return weekday;
	}

	/**
	 * 计算给定周内的全部日期
	 * 
	 * @param year
	 *            年
	 * @param week
	 *            给定第几周
	 * @param newfmt
	 *            返回值格式
	 * @return 结果数组形式输出所在周的日期。
	 */
	public String[] getWeekDate(String year, int week, String newfmt) {
		String[] jweekday = new String[7];
		int addvalue=0;
		if(this.local_GMP==DateTools.WEEK_OF_GBK){
			 addvalue=2;
		}else if(this.local_GMP==DateTools.WEEK_OF_ISO){
			addvalue=1;
		}
		try {
			if ((year != null) && (year.length() == 4) && (week >= 0)
					&& (newfmt != null)) {
				Calendar cal = getCal(year + "0101", "yyyyMMdd");
				if (cal != null) {
					week--;
					cal.add(5, week * 7 - cal.get(7) + addvalue);
					SimpleDateFormat sdf = new SimpleDateFormat(newfmt);
					jweekday[0] = sdf.format(cal.getTime());
					for (int i = 1; i < 7; i++) {
						cal.add(5, 1);
						jweekday[i] = sdf.format(cal.getTime());
					}
				}
			}
		} catch (IndexOutOfBoundsException iobe) {
			iobe.printStackTrace();
		}
		return jweekday;
	}

	/**
	 * 计算指定日期是星期几
	 * 
	 * @param strdate
	 *            指定日期
	 * @param oldfmt
	 *            指定日期格式
	 * @param fmt
	 *            输出格式
	 * @return 返回结果为fmt+结果。
	 */
	public String getDayOfWeek(String strdate, String oldfmt, String fmt) {
		String sWeek = null;
		try {
			if ((strdate != null) && (oldfmt != null) && (fmt != null)) {
				Calendar cal = getCal(strdate, oldfmt);
				if (cal != null) {
					int iWeek = cal.get(7);
					sWeek = fmt + (iWeek - 1 == 0 ? 7 : iWeek - 1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sWeek;
	}

	/**
	 * 计算指定年共有多少周
	 * 
	 * @param year
	 *            指定年 格式yyyy
	 * @return 返回周数
	 */
	public int getWeekNum(String year) {
		int weeknum = -1;
		try {
			if (year != null) {
				Calendar cal = getCal(year + "1231", "yyyyMMdd");
				if (cal != null) {
					if (cal.get(3) == 1)
						cal.add(5, -7);
					weeknum = cal.get(3);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return weeknum;
	}

	/**
	 * 计算两个给定的时间之差
	 * 
	 * @param startdate
	 *            开始日期
	 * @param enddate
	 *            结束日期
	 * @param fmt
	 *            日期格式
	 * @param refmt
	 *            返回值格式 ms毫秒 s秒 m分 h小时 d天
	 * @return 返回值
	 */
	public String cntTimeDifference(String startdate, String enddate,
			String fmt, String refmt) {
		String ret = null;
		try {
			if ((startdate != null) && (enddate != null) && (fmt != null)
					&& (refmt != null)) {
				Date scal = getCal(startdate, fmt).getTime();
				Date ecal = getCal(enddate, fmt).getTime();
				if ((scal == null) || (ecal == null)) {
					return null;
				} else {
					long diffMillis = ecal.getTime() - scal.getTime();

					long diffSecs = diffMillis / 1000L;

					long diffMins = diffMillis / 60000L;

					long diffHours = diffMillis / 3600000L;

					long diffDays = diffMillis / 86400000L;

					if (refmt.equals("ms"))
						ret = Long.toString(diffMillis);
					else if (refmt.equals("s"))
						ret = Long.toString(diffSecs);
					else if (refmt.equals("m"))
						ret = Long.toString(diffMins);
					else if (refmt.equals("h"))
						ret = Long.toString(diffHours);
					else if (refmt.equals("d"))
						ret = Long.toString(diffDays);
					else
						ret = Long.toString(diffHours);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	public static void main(String[] args) {
		DateTools dts = new DateTools();
		String a = dts.cntTimeDifference("2016-02-18", "2016-04-10", "yyyy-MM-dd", "d");
		System.out.println(a);
	}

	/**
	 * 计算指定日期经过多少分钟后的日期
	 * 
	 * @param deftime
	 *            自定日期
	 * @param oldfmt
	 *            日期格式
	 * @param timediff
	 *            分钟为单位
	 * @param newfmt
	 *            返回值的格式
	 * @return 返回日期，timediff >0向前计算，timediff<0向后计算
	 */
	public String getBeforeTime(String deftime, String oldfmt, int timediff,
			String newfmt) {
		String rq = null;
		try {
			if ((deftime != null) && (!deftime.equals(""))) {
				Calendar cal = getCal(deftime, oldfmt);
				if (cal != null) {
					cal.add(Calendar.MINUTE, -timediff);
					SimpleDateFormat sdf = new SimpleDateFormat(newfmt);
					rq = sdf.format(cal.getTime());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rq;
	}

	/**
	 * 计算指定日期经过多少小时后的日期
	 * 
	 * @param deftime
	 *            自定日期
	 * @param oldfmt
	 *            日期格式
	 * @param timediff
	 *            小时为单位
	 * @param newfmt
	 *            返回值的格式
	 * @return 返回日期，timediff >0向前计算，timediff<0向后计算
	 */
	public String getBeforeTimeByH(String deftime, String oldfmt, int timediff,
			String newfmt) {
		String rq = null;
		try {
			if ((deftime != null) && (!deftime.equals(""))) {
				Calendar cal = getCal(deftime, oldfmt);
				if (cal != null) {
					cal.add(12, -timediff * 60);
					SimpleDateFormat sdf = new SimpleDateFormat(newfmt);
					rq = sdf.format(cal.getTime());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rq;
	}

	/**
	 * 计算指定日期经过多少月后的日期
	 * 
	 * @param deftime
	 *            自定日期
	 * @param oldfmt
	 *            日期格式
	 * @param timediff
	 *            月为单位
	 * @param newfmt
	 *            返回值的格式
	 * @return 返回日期，timediff >0向前计算，timediff<0向后计算
	 */
	public String getBeforeTimeByM(String deftime, String oldfmt, int timediff,
			String newfmt) {
		String rq = null;
		try {
			if ((deftime != null) && (!deftime.equals(""))) {
				Calendar cal = getCal(deftime, oldfmt);
				if (cal != null) {
					cal.add(2, -timediff);
					SimpleDateFormat sdf = new SimpleDateFormat(newfmt);
					rq = sdf.format(cal.getTime());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rq;

	}

	/**
	 * 日期格式化
	 * 
	 * @param mydate
	 *            日期
	 * @param oldfmt
	 *            旧格式
	 * @param newfmt
	 *            新格式
	 * @return 返回值的格式根据newfmt根式返回
	 */
	public String fmtDate(String mydate, String oldfmt, String newfmt) {
		String restr = null;
		try {
			if ((mydate != null) && (oldfmt != null) && (newfmt != null)) {
				SimpleDateFormat newDate = new SimpleDateFormat(newfmt);
				Calendar cal = getCal(mydate, oldfmt);
				if (cal != null) {
					restr = newDate.format(cal.getTime());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return restr;

	}
	
	/**
	 * 将文字行的date格式成Date类型
	 * @param mydate
	 * @param fmt
	 * @return
	 */
	public Date fmtDate(String myDate,String fmt){
		
		Date newDate=null;
		if((myDate != null) && (fmt != null)){
			Calendar cal = getCal(myDate, fmt);
			newDate=cal.getTime();
		}
		
		return newDate;
		
	}
	public String fmtDate(Date myDate,String fmt){
		String newDate="";
		SimpleDateFormat sDateformat = new SimpleDateFormat(fmt);
		newDate= sDateformat.format(myDate).toString();
		return newDate;
	}
	
	public static void main1(String[] args){
		DateTools dt=new DateTools(WEEK_OF_ISO);
		//System.out.println(dt.fmtDate(dt.getDate("yyyy-MM-dd"), "yyyy-MM-dd"));
		//Date date=new Date();
		//System.out.println(dt.fmtDate(date, "yyyyMMddHHmmssSSS"));
//		System.out.println(dt.getWeekOfYear("20121202", "yyyyMMdd"));
//		String[] weekday=dt.getWeekDay("20121202", "yyyyMMdd", "yyyy-MM-dd");
//		for(int i=0;i<weekday.length;i++){
//			System.out.println(weekday[i]);
//		}
		System.out.println(dt.getWeekOfYear("2011-12-31", "yyyy-MM-dd"));
		Calendar calendar = Calendar.getInstance();
        Date date = new Date(dt.fmtDate("2011-12-31", "yyyy-MM-dd").getTime());
        calendar.setTime(date);
        int n = calendar.get(Calendar.WEEK_OF_YEAR);
        System.out.println(n);
		
		
	}
	
	/**
	 * 计算某年某月有多少天 
	 * @param year
	 * @return
	 */
	public int getMonthDays(int year,int month){
		if(month>12||month<1){
			throw new RuntimeException("month is error "+month);
		}
		String bigmonth="1,3,5,7,8,10,12";
		String smallmonth="4,6,9,11";
		if(bigmonth.indexOf(month+"")>-1){
			return 31;
		}
		if(smallmonth.indexOf(month+"")>-1){
			return 30;
		}
		if(year%400==0||(year%4==0&&year%100!=0)){
			return 29;
		}else{
			return 28;
		}
	}
}
