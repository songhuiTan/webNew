package org.song.course.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
//import com.google.gson.Gson;
import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;
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
import org.json.JSONArray;
import org.json.JSONObject;


/**
 * 
 * @author liufeng
 * @date 2013-10-21
 */
public class BaiduTranslateService {
	private static final String UTF8 = "utf-8";
	//申请者开发者id，实际使用时请修改成开发者自己的appid
	private static final String appId = "20160816000026854";

	//申请成功后的证书token，实际使用时请修改成开发者自己的token
	private static final String token = "IXUrYAo2K20Uv3ajc9nD";

	private static final String url = "http://api.fanyi.baidu.com/api/trans/vip/translate";

	//随机数，用于生成md5值，开发者使用时请激活下边第四行代码
	private static final Random random = new Random();
	
	/**
	 * 
	 * @param q  翻译内容
	 * @param from 一般填auto
	 * @param to  一般填auto
	 * @return  返回翻译完成的字符串
	 * @throws Exception
	 */
	public static String translate(String q, String from, String to) throws Exception{
		//用于md5加密
		int salt = random.nextInt(10000);
		String text = null;
		//本演示使用指定的随机数为1435660288
//		int salt = 1435660288;
		
		// 对appId+源文+随机数+token计算md5值
		StringBuilder md5String = new StringBuilder();
		md5String.append(appId).append(q).append(salt).append(token);
		String md5 = DigestUtils.md5Hex(md5String.toString());

		//使用Post方式，组装参数
		HttpPost httpost = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();  
		nvps.add(new BasicNameValuePair("q", q));  
		   nvps.add(new BasicNameValuePair("from", from));  
		   nvps.add(new BasicNameValuePair("to", to));  
		   nvps.add(new BasicNameValuePair("appid", appId));  
		   nvps.add(new BasicNameValuePair("salt", String.valueOf(salt)));  
		   nvps.add(new BasicNameValuePair("sign", md5));  
		httpost.setEntity(new UrlEncodedFormEntity(nvps, UTF8));  
		
		//创建httpclient链接，并执行
//	    CloseableHttpClient httpclient = HttpClients.createDefault();
//	    CloseableHttpResponse response = httpclient.execute(httpost);
		
		HttpClient httpclient = new DefaultHttpClient();  
	    HttpEntity entity = null;
        try {  
            // 执行post请求.  
            HttpResponse response = httpclient.execute(httpost);  
          //对于返回实体进行解析
    		entity = response.getEntity();
    		InputStream returnStream = entity.getContent();
    		BufferedReader reader = new BufferedReader(
    				new InputStreamReader(returnStream, UTF8));
    		StringBuilder result = new StringBuilder();
    		String str = null;
    		while ((str = reader.readLine()) != null) {
    			result.append(str).append("\n");
    		}
    		
    		//转化为json对象，注：Json解析的jar包可选其它
    		JSONObject resultJson = new JSONObject(result.toString());

    		//开发者自行处理错误，本示例失败返回为null
    		try {
    			String error_code = resultJson.getString("error_code");
    			if (error_code != null) {
    				System.out.println("出错代码:" + error_code);
    				System.out.println("出错信息:" + resultJson.getString("error_msg"));
    				return null;
    			}
    		} catch (Exception e) {}
    		
    		//获取返回翻译结果
    		JSONArray array = (JSONArray) resultJson.get("trans_result");
    		JSONObject dst = (JSONObject) array.get(0);
    		text = dst.getString("dst");
    		text = URLDecoder.decode(text, UTF8);
    		if(text.contains(",")){//去掉,
    			text=text.replace(",", "");
    		}
    		
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
		return text;
	}
	
	//实际抛出异常由开发者自己处理
	public static  String translateToEn(String q) throws Exception{
//		ApplicationContext container=new FileSystemXmlApplicationContext("src//spring//resource//baidu.xml");
//		BaiduTranslateDemo baidu = (BaiduTranslateDemo)container.getBean("baidu");
		String result = null;
		try {
			result = translate(q, "auto", "auto");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	
//	/**
//	 * 发起http请求获取返回结果
//	 * 
//	 * @param requestUrl 请求地址
//	 * @return
//	 */
//	
//	public static String httpRequest(String requestUrl) {
//		StringBuffer buffer = new StringBuffer();
//		try {
//			URL url = new URL(requestUrl);
//			HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
//
//			httpUrlConn.setDoOutput(false);
//			httpUrlConn.setDoInput(true);
//			httpUrlConn.setUseCaches(false);
//
//			httpUrlConn.setRequestMethod("GET");
//			httpUrlConn.connect();
//
//			// 将返回的输入流转换成字符串
//			InputStream inputStream = httpUrlConn.getInputStream();
//			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
//			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//
//			String str = null;
//			while ((str = bufferedReader.readLine()) != null) {
//				buffer.append(str);
//			}
//			bufferedReader.close();
//			inputStreamReader.close();
//			// 释放资源
//			inputStream.close();
//			inputStream = null;
//			httpUrlConn.disconnect();
//
//		} catch (Exception e) {
//		}
//		return buffer.toString();
//	}
//
//	/**
//	 * utf编码
//	 * 
//	 * @param source
//	 * @return
//	 */
//	public static String urlEncodeUTF8(String source) {
//		String result = source;
//		try {
//			result = java.net.URLEncoder.encode(source, "utf-8");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		return result;
//	}

//	/**
//	 * 翻译（中->英 英->中 日->中 ）
//	 * 
//	 * @param source
//	 * @return
//	 */
//	public static String translate(String source) {
//		String dst = null;
//
//		// 组装查询地址
//		String requestUrl = "http://api.fanyi.baidu.com/api/trans/vip/translate?client_id=AAAA&q={keyWord}&from=auto&to=auto";
//		// 对参数q的值进行urlEncode utf-8编码
//		requestUrl = requestUrl.replace("AAAA", BAIDUAPPID);
//		requestUrl = requestUrl.replace("{keyWord}", urlEncodeUTF8(source));
//
//		// 查询并解析结果
//		try {
//			// 查询并获取返回结果
//			String json = httpRequest(requestUrl);
//			// 通过Gson工具将json转换成TranslateResult对象
//			//TranslateResult translateResult = new Gson().fromJson(json, TranslateResult.class);
//			// 取出translateResult中的译文
//			dst = "";//translateResult.getTrans_result().get(0).getDst();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		if (null == dst)
//			dst = "翻译系统异常，请稍候尝试！";
//		return dst;
//	}

	public static void main(String[] args) throws Exception {
		// 翻译结果：The network really powerful
		System.out.println(translate("在哪里","auto", "auto"));
	}
}
