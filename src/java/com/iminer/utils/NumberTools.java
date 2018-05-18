package com.iminer.utils;

import java.math.BigDecimal;

/**
 * 处理一些数据类型的方法的java类
 * @author iminer1
 *
 */
public class NumberTools {

	/**
	 * 根据给定的参数进行进行四舍五入
	 * 
	 * @param num
	 *            要四舍五入的数字
	 * @param roundBit
	 *            四舍五入位数 正数表示：小数点后位数；负数表示：小数前位数
	 * @return 四舍五入后的数字
	 */
	public static double round(double num, int roundBit) {
		int piontBit = 1;
		double numtmp = 0.0D;
		if (roundBit < 0) {
			String tmpstr = "1";
			roundBit = Math.abs(roundBit);
			for (int i = 0; i < roundBit; i++) {
				tmpstr = tmpstr + "0";
			}
			piontBit = Integer.parseInt(tmpstr);
			roundBit = 0;
			num /= piontBit;
		}
		BigDecimal b = new BigDecimal(Double.toString(num));
		BigDecimal one = new BigDecimal("1");
		numtmp = b.divide(one, roundBit, BigDecimal.ROUND_HALF_UP).doubleValue();
		return numtmp * piontBit;
	}
	
	
	
	/**
	 * 根据给定的参数进行进行四舍五入
	 * 
	 * @param num
	 *            四舍五入的数字
	 * @param roundBit
	 *            四舍五入位数 正数表示：小数点后位数；负数表示：小数前位数
	 * @return 四舍五入后的数字
	 */
	public static String roundToStr(double num, int roundBit) {
		int piontBit = 1;
		double numtmp = 0.0D;
		if (roundBit < 0) {
			String tmpstr = "1";
			roundBit = Math.abs(roundBit);
			for (int i = 0; i < roundBit; i++) {
				tmpstr = tmpstr + "0";
			}
			piontBit = Integer.parseInt(tmpstr);
			roundBit = 0;
			num /= piontBit;
		}

		BigDecimal b = new BigDecimal(Double.toString(num));

		BigDecimal one = new BigDecimal("1");
		if (piontBit == 1) {
			return b.divide(one, roundBit, BigDecimal.ROUND_HALF_UP).toString();
		}
		numtmp = b.divide(one, roundBit, BigDecimal.ROUND_HALF_UP).doubleValue();
		return new BigDecimal(numtmp * piontBit).toString();
	}
	
	
	
	
	
	
	
	
	public static void main(String[] args){
		System.out.println(roundToStr(12,2));
	}
	
	
	
	
	
	
	
	
	
}
