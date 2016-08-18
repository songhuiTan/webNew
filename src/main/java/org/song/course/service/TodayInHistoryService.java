package org.song.course.service;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 历史上的今天查询服务
 * 
 * @author liufeng
 * @date 2013-10-16
 * 
 */
public class TodayInHistoryService {

	/**
	 * 发起http get请求获取网页源代码
	 * 
	 * @param requestUrl
	 * @return
	 */
	private static String httpRequest(String requestUrl) {
		StringBuffer buffer = null;

		try {
			buffer = toGet(requestUrl);//使用httpClient
//			// 建立连接
//			URL url = new URL(requestUrl);
//			HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
//			httpUrlConn.setDoInput(true);
//			httpUrlConn.setRequestMethod("GET");
//
//			// 获取输入流
//			InputStream inputStream = httpUrlConn.getInputStream();
//			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
//			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//
//			// 读取返回结果
//			buffer = new StringBuffer();
//			String str = null;
//			while ((str = bufferedReader.readLine()) != null) {
//				buffer.append(str);
//			}
//			// 释放资源
//			bufferedReader.close();
//			inputStreamReader.close();
//			inputStream.close();
//			httpUrlConn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}
	
	
	/** 
     * 发送 get请求 
     */  
    private static StringBuffer toGet(String requestUrl) {  
        HttpClient httpclient = new DefaultHttpClient();  
        HttpEntity entity = null;
        try {  
            // 创建httpget.  
            HttpGet httpget = new HttpGet(requestUrl);  
            System.out.println("executing request " + httpget.getURI());  
            // 执行get请求.  
            HttpResponse response = httpclient.execute(httpget);  
            // 获取响应实体  
            entity = response.getEntity();  
            System.out.println("--------------------------------------");  
            // 打印响应状态  
            System.out.println(response.getStatusLine());  
            if (entity != null) {  
                // 打印响应内容长度  
//                System.out.println("Response content length: "  
//                        + entity.getContentLength());  
//                // 打印响应内容  
//                System.out.println("Response content: "  
//                        + EntityUtils.toString(entity));  
                return new StringBuffer(EntityUtils.toString(entity));
            }  
            System.out.println("------------------------------------");  
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (ParseException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            // 关闭连接,释放资源  
            httpclient.getConnectionManager().shutdown();  
        }  
        
        return null;
        
    }  
  


	/**
	 * 从html中抽取出历史上的今天信息
	 * 
	 * @param html
	 * @return
	 */
	private static String extract(String html) {
		StringBuffer buffer = null;
		// 日期标签：区分是昨天还是今天
		buffer = new StringBuffer();
		String dateTag = getMonthDay(0);
		buffer.append("≡≡ ").append("历史上的").append(dateTag).append(" ≡≡").append("\n\n");
		
		Document doc = Jsoup.parse(html);
		Elements content = doc.getElementsByClass("listren");
		Elements links = content.get(0).getElementsByTag("li");
		for (Element link : links) {
		  String linkHref = link.getElementsByTag("a").get(0).attr("title");
//		  System.out.println(""+linkHref);
		  buffer.append(linkHref).append("\n\n");
		}
//		String dateTag = getMonthDay(0);
		//通过正则表达式解析 发现无法完全匹配，
//		Pattern p = Pattern.compile("(.*)(<div class=\"listren\">)(.*?)(</div>)(.*)");
//		
//		Matcher m = p.matcher(html);
//		Matcher m1 = p.matcher("<div class='listren'> </div>");
//		System.out.println(m.find()); 
//		
//		
//	    while(m.find()){  
//	    	System.out.println(m.group(0)+"\n");  
//	    }
//		if (m.matches()) {
//			buffer = new StringBuffer();
//			if (m.group(3).contains(getMonthDay(-1)))
//				dateTag = getMonthDay(-1);
//
//			// 拼装标题
//			buffer.append("≡≡ ").append("历史上的").append(dateTag).append(" ≡≡").append("\n\n");
//
//			// 抽取需要的数据
//			for (String info : m.group(3).split("  ")) {
//				info = info.replace(dateTag, "").replace("（图）", "").replaceAll("</?[^>]+>", "").trim();
//				// 在每行末尾追加2个换行符
//				if (!"".equals(info)) {
//					buffer.append(info).append("\n\n");
//				}
//			}
//		}
		// 将buffer最后两个换行符移除并返回
		return (null == buffer) ? null : buffer.substring(0, buffer.lastIndexOf("\n\n"));
	}

	/**
	 * 获取前/后n天日期(M月d日)
	 * 
	 * @return
	 */
	private static String getMonthDay(int diff) {
		DateFormat df = new SimpleDateFormat("M月d日");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR, diff);
		return df.format(c.getTime());
	}

	/**
	 * 封装历史上的今天查询方法，供外部调用
	 * 
	 * @return
	 */
	public static String getTodayInHistoryInfo() {
		// 获取网页源代码
		String html = httpRequest("http://www.rijiben.com/");
		// 从网页中抽取信息
		String result = extract(html);

		return result;
	}

	/**
	 * 通过main在本地测试
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
//		String source="<div class=\"hdwiki_tmml\"><a class=\" &nbsp;FCK__AnchorC\" name=\"1\">是大家是否</a></div><div class=\"hdwiki_tmml\"><a name=\"2\">士大夫士大夫</a></div> ";  
//        StringBuilder result=new StringBuilder();  
//        System.out.println("=======开始匹配文字内容========");  
//        String patternStrs="(<div class=\"hdwiki_tmml\"><a.+?>)(.+?)(</a></div>)";  
//        Pattern pattern=Pattern.compile(patternStrs);  
//        Matcher matcher=pattern.matcher(source);  
//        while(matcher.find()){  
//            result.append(matcher.group(2)+"\n");  
//        }
//		String source="<div class=\"hdwiki_tmml\"><a class=\" &nbsp;FCK__AnchorC\" name=\"1\">是大家是否</a></div><div class=\"hdwiki_tmml\"><a name=\"2\">士大夫士大夫</a></div> ";  
//        StringBuilder result=new StringBuilder();  
//        System.out.println("=======开始匹配文字内容========");  
//        String patternStrs="(<div class=\"hdwiki_tmml\"><a.+?>)(.+?)(</a></div>)";  
//        Pattern pattern=Pattern.compile(patternStrs);  
//        Matcher matcher=pattern.matcher(source);  
//        while(matcher.find()){  
//            result.append(matcher.group(2)+"\n");  
//        }  
//        System.out.println(result.toString());  
//          
//        System.out.println("=======开始匹配name属性值========");  
//        String patternName="(<div class=\"hdwiki_tmml\"><a.+?)name=\"(.+?)\">(.+?</a></div>)";  
//        pattern=Pattern.compile(patternName);  
//        matcher=pattern.matcher(source);  
//        result=result.delete(0, result.length());  
//        while(matcher.find()){  
//            result.append(matcher.group(2)+"\n");  
//        }  
//        System.out.println(result.toString());  
		
//		String info = getTodayInHistoryInfo();
//		System.out.println(info);
		
		String io="翻译";
		byte[] iob=io.getBytes();
		try {
			String ioUTF= new String(iob,"utf-8");
			System.out.println(ioUTF);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		
		System.exit(0);
	}
}