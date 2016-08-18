package org.song.course.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class HttpUtil {
	
	
	/**
	 * 执行get请求
	 * @param purl
	 * @return
	 */
	public static String doHttpGet(String purl) {
	  	HttpClient httpclient = new DefaultHttpClient();  
        HttpEntity entity = null;
        try {  
            // 创建httpget.  
            HttpGet httpget = new HttpGet(purl);  
//	            System.out.println("executing request " + httpget.getURI());  
            // 执行get请求.  
            HttpResponse response = httpclient.execute(httpget);  
            // 获取响应实体  
            entity = response.getEntity();  
//	            System.out.println("--------------------------------------");  
            // 打印响应状态  
//            System.out.println(response.getStatusLine());  
            if (entity != null) {  
                return EntityUtils.toString(entity);
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
	 *  执行post请求
	 * @param purl
	 * @param p
	 * @return
	 * @throws Exception
	 */
	public static String doHttpPost(String purl,Map<String, String> p)  throws Exception{
		
		//使用Post方式，组装参数
		HttpPost httpost = new HttpPost(purl);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();  
		
		for(Map.Entry<String, String> e:p.entrySet()){
			nvps.add(new BasicNameValuePair(e.getKey(), e.getValue()));  
		}
//		nvps.add(new BasicNameValuePair("q", q));  
//		nvps.add(new BasicNameValuePair("from", from));  
//		nvps.add(new BasicNameValuePair("to", to));  
//		nvps.add(new BasicNameValuePair("appid", appId));  
//		nvps.add(new BasicNameValuePair("salt", String.valueOf(salt)));  
//		nvps.add(new BasicNameValuePair("sign", md5));  
		httpost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));  
		
		HttpClient httpclient = new DefaultHttpClient();  
	    HttpEntity entity = null;
        try {  
            // 执行post请求.  
            HttpResponse response = httpclient.execute(httpost);  
            //对于返回实体进行解析
    		entity = response.getEntity();
    		return EntityUtils.toString(entity);
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
}
