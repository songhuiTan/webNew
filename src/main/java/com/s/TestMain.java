package com.s;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
public class TestMain {

	public static void main(String[] args) throws UnsupportedEncodingException {		
//		  System.out.println("UTF-8");
//		  String a = URLEncoder.encode("中文测试", "GBK");//编码
//		  System.out.println(a);
//		  System.out.println(URLDecoder.decode(a,"GBK"));//还原
//		  //下面同理
//		  System.out.println("/nGBK(百度就是用这种)");
//		  a = URLEncoder.encode("中文测试", "GBK");
//		  System.out.println(a);
//		  System.out.println(URLDecoder.decode(a,"GBK"));
//		  URLDecoder.decode("测试日志","GBK");
		  
		  
//		  String s = "清山";  
//		  byte[] b = s.getBytes("utf-8");//编码  
//		  String sa = new String(b, "utf-8");//解码:用什么字符集编码就用什么字符集解码  
//		  System.out.println(System.getProperty("file.encoding"));  
		
		
//		long longTime1 = System.currentTimeMillis();  //获取到的结果表示当时时间距离1970年1月1日0时0分0秒0毫秒的毫秒数
//	    // 1470903974699
//	    System.out.println(longTime1); 	  
//	    long longTime2 = new java.util.Date().getTime();  //获取到的结果表示当时时间距离1970年1月1日0时0分0秒0毫秒的毫秒数
//	    // 1373206143381  
//	    System.out.println(longTime2);  
		
		// 当前时间（距离1970年1月1日0时0分0秒0毫秒的毫秒数）
		long longTime = 1470903974699L;
		
		String stdFormatTime = formatTime(longTime);
		// 2016-08-11 16:26:14
		System.out.println(stdFormatTime);
	}
	
	/**
	 * 将long类型的时间转换成标准格式（yyyy-MM-dd HH:mm:ss）
	 * 
	 * @param longTime
	 * @return
	 */
	public static String formatTime(long longTime) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date(longTime));
	}

}
