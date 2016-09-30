package org.song.course.util;

/**
 * 计算运行时间间隔工具类
 * @author tansonghui
 */
public class CalculatePassTimeUtil {
	public static long startTime=0;
	public static long endTime=0;
	public static long passTime=0;
	
	
	public static void setStar(boolean isprint){
		startTime=System.currentTimeMillis();
		if(isprint){
			System.out.println("CalculatePassTimeUtil:Star"+startTime);
		}
	}

	public static void setEnd(boolean showPassTime,String tag){
		endTime=System.currentTimeMillis();
		passTime=endTime-startTime;
		System.out.println("CalculatePassTimeUtil:End"+endTime);
		if(showPassTime&&tag!=null){
			System.out.println(tag+":"+passTime);
		}
	}
}
