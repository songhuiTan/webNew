package org.song.course.service;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.song.course.message.resp.Music;
import org.song.course.util.CalculatePassTimeUtil;
import org.song.course.util.HttpUtil;

/**
 * 百度音乐搜索API操作类
 * 
 * @author liufeng
 * @date 2013-12-09
 */
public class BaiduMusicService {
	/**
	 * 根据名称和作者搜索音乐
	 * 
	 * @param musicTitle 相关音乐
	 * @return Music
	 */
	public static Music searchMusic(String musicTitle ,String musicAuthor) throws Exception {
		// 百度音乐搜索地址
//		String requestUrl = "http://box.zhangmen.baidu.com/x?op=12&count=1&title={TITLE}$${AUTHOR}$$$$";
//		// 对音乐名称、作者进URL编码
//		requestUrl = requestUrl.replace("{TITLE}", urlEncodeUTF8(musicTitle));
//		requestUrl = requestUrl.replace("{AUTHOR}", urlEncodeUTF8(musicAuthor));
//		// 处理名称、作者中间的空格
//		requestUrl = requestUrl.replaceAll("\\+", "%20");

		// 查询并获取返回结果
//		InputStream inputStream = httpRequest(requestUrl);
		
		// 新百度音乐搜索地址
		String requestUrl = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=webapp_music&method=baidu.ting.search.catalogSug&format=xml&callback=&query={TITLE}%AF&_={TIME}";
		requestUrl = requestUrl.replaceAll("\\+", "%20");
		
		// 对模糊查询URL编码
		requestUrl = requestUrl.replace("{TITLE}", urlEncodeUTF8(musicTitle));//
		requestUrl = requestUrl.replace("{TIME}", String.valueOf(Calendar.getInstance().getTime().getTime()));
		
//		long preTimeL=System.currentTimeMillis();
//		System.out.println(preTimeL);
		
		CalculatePassTimeUtil.setStar(true);
		String t=HttpUtil.doHttpGet(requestUrl);//执行httpget请求得到json字符串
		CalculatePassTimeUtil.setEnd(true,"search");
		CalculatePassTimeUtil.setStar(true);
		
		if(t.contains("errno")){
			Logger logger = Logger.getLogger(BaiduMusicService.class.toString());     
			logger.fatal("baiduSearchError:URL="+requestUrl);
			return null;
		}
		
		t=t.replace("(", "");
		t=t.replace(")", "");
//		System.out.println(t);
		JSONObject resultJson = new JSONObject(t);//百度进行的是模糊搜素，结果有多个，搜索结果可能是对照歌手名字，或者歌手专辑
		
		
		CalculatePassTimeUtil.setEnd(true,"json0");
		CalculatePassTimeUtil.setStar(true);
		
		
		JSONArray array = (JSONArray) resultJson.get("song");
		JSONObject dst = null;//
		JSONObject searchJson =null;
		
		for(int i=0; i<array.length();i++){
			searchJson=array.getJSONObject(i);
			if(searchJson.getString("songname").contains(musicTitle)&&searchJson.getString("artistname").contains(musicAuthor)){//用contains而不用equals
				dst = searchJson;
			}
		}
		if(dst==null){//找不到歌曲名字和歌手名字都对应的，则取第0个
			dst=array.getJSONObject(0);
			musicAuthor="";
		}
		CalculatePassTimeUtil.setEnd(true,"json1");
		CalculatePassTimeUtil.setStar(true);
		//通过songid获得歌曲链接
		String musicUrl = "http://ting.baidu.com/data/music/links?songIds="+dst.getString("songid");
		String dlStr=HttpUtil.doHttpGet(musicUrl);
		
		CalculatePassTimeUtil.setEnd(true,"getsongid");
		CalculatePassTimeUtil.setStar(true);
		
		//测试download接口 
//		String downLoadMusicUrl = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=webapp_music&method=baidu.ting.song.downWeb&format=json&callback=&songid={SONGID}&bit={BIT}&_={TIME}";
//		downLoadMusicUrl = downLoadMusicUrl.replace("{SONGID}", dst.getString("songid"));
//		downLoadMusicUrl = downLoadMusicUrl.replace("{BIT}", "64");
//		downLoadMusicUrl = downLoadMusicUrl.replace("{TIME}", String.valueOf(Calendar.getInstance().getTime().getTime()));
//		String dlStr=HttpUtil.doHttpGet(downLoadMusicUrl);
		
		JSONObject mJsonOb = new JSONObject(dlStr);
		JSONObject mJsonObdata = (JSONObject)mJsonOb.get("data");
		JSONArray mJsonObarray = (JSONArray) mJsonObdata.get("songList");
		JSONObject mJsonOb0 = (JSONObject)mJsonObarray.get(0);
		String songLink=mJsonOb0.getString("songLink");
		//{"songName":"相信自己","albumName":"北京2008年奥运会歌曲专辑","linkCode":22000,"format":"mp3","albumId":2462011,"artistId":"1108","songPicBig":"http://musicdata.baidu.com/data2/pic/997f8fcedb03fb525d637e0c6d348892/115458896/115458896.jpg","version":"","queryId":"2050484","songLink":"http://file.qianqian.com//data2/music/124469202/124469202.mp3?xcode=a3e45c4042e0420f3d7cab37c0a62272&src=\"http%3A%2F%2Fpan.baidu.com%2Fshare%2Flink%3Fshareid%3D642341459%26uk%3D1915931231\"","size":3998088,"rate":128,"lrcLink":"http://musicdata.baidu.com/data2/lrc/13804377/13804377.lrc","copyType":1,"artistName":"成龙","time":249,"relateStatus":"0","songPicSmall":"http://musicdata.baidu.com/data2/pic/c3472b63a7f2f29ad47ea0ebd602bff1/115458928/115458928.jpg","songId":2050484,"songPicRadio":"http://musicdata.baidu.com/data2/pic/e6570ad1bc1ee39450031afe12e68b43/115458876/115458876.jpg","showLink":"http://pan.baidu.com/share/link?shareid=642341459&uk=1915931231","resourceType":"2"}
		if(songLink.contains("\"")){
			songLink=songLink.replace("\"", "");
		}
		CalculatePassTimeUtil.setEnd(true,"json2");
		CalculatePassTimeUtil.setStar(true);
//		System.out.println(songLink);
//		String musicAuthor=mJsonOb0.getString("artistName");
		// 从返回结果中解析出Music
//		Music music = parseMusic(songLink);
		
		
		
		Music music = new Music();
		// 设置普通品质音乐链接
		music.setMusicUrl(songLink);
		// 设置高品质音乐链接
		music.setHQMusicUrl(songLink);
		// 如果music不为null，设置标题和描述
		if (null != music) {
			music.setTitle(musicTitle);
			// 如果作者不为""，将描述设置为作者
			if (!"".equals(musicAuthor))
				music.setDescription(musicAuthor);
			else
				music.setDescription("来自百度音乐");
		}
		return music;
	}

	/**
	 * UTF-8编码
	 * 
	 * @param source
	 * @return
	 */
	private static String urlEncodeUTF8(String source) {
		String result = source;
		try {
			result = java.net.URLEncoder.encode(source, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

//	/**
//	 * 发送http请求取得返回的输入流
//	 * 
//	 * @param requestUrl 请求地址
//	 * @return InputStream
//	 */
//	private static InputStream httpRequest(String requestUrl) {
//		InputStream inputStream = null;
//		try {
//			URL url = new URL(requestUrl);
//			HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
//			httpUrlConn.setDoInput(true);
//			httpUrlConn.setRequestMethod("GET");
//			httpUrlConn.connect();
//			// 获得返回的输入流
//			inputStream = httpUrlConn.getInputStream();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return inputStream;
//	}

//	/**
//	 * 解析音乐参数
//	 * 
//	 * @param inputStream 百度音乐搜索API返回的输入流
//	 * @return Music
//	 */
//	@SuppressWarnings("unchecked")
//	private static Music parseMusic(InputStream inputStream) {
//		Music music = null;
//		try {
//			// 使用dom4j解析xml字符串
//			SAXReader reader = new SAXReader();
//			Document document = reader.read(inputStream);
//			// 得到xml根元素
//			Element root = document.getRootElement();
//			// count表示搜索到的歌曲数
//			String count = root.element("count").getText();
//			// 当搜索到的歌曲数大于0时
//			if (!"0".equals(count)) {
//				// 普通品质
//				List<Element> urlList = root.elements("url");
//				// 高品质
//				List<Element> durlList = root.elements("durl");
//
//				// 普通品质的encode、decode
//				String urlEncode = urlList.get(0).element("encode").getText();
//				String urlDecode = urlList.get(0).element("decode").getText();
//				// 普通品质音乐的URL
//				String url = urlEncode.substring(0, urlEncode.lastIndexOf("/") + 1) + urlDecode;
//				if (-1 != urlDecode.lastIndexOf("&"))
//					url = urlEncode.substring(0, urlEncode.lastIndexOf("/") + 1) + urlDecode.substring(0, urlDecode.lastIndexOf("&"));
//
//				// 默认情况下，高音质音乐的URL 等于 普通品质音乐的URL
//				String durl = url;
//
//				// 判断高品质节点是否存在
//				Element durlElement = durlList.get(0).element("encode");
//				if (null != durlElement) {
//					// 高品质的encode、decode
//					String durlEncode = durlList.get(0).element("encode").getText();
//					String durlDecode = durlList.get(0).element("decode").getText();
//					// 高品质音乐的URL
//					durl = durlEncode.substring(0, durlEncode.lastIndexOf("/") + 1) + durlDecode;
//					if (-1 != durlDecode.lastIndexOf("&"))
//						durl = durlEncode.substring(0, durlEncode.lastIndexOf("/") + 1) + durlDecode.substring(0, durlDecode.lastIndexOf("&"));
//				}
//				music = new Music();
//				// 设置普通品质音乐链接
//				music.setMusicUrl(url);
//				// 设置高品质音乐链接
//				music.setHQMusicUrl(durl);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return music;
//	}

	// 测试方法
	public static void main(String[] args) throws Exception {
		Music music = searchMusic("存在","汪峰");
		System.out.println("音乐名称：" + music.getTitle());
		System.out.println("音乐描述：" + music.getDescription());
		System.out.println("普通品质链接：" + music.getMusicUrl());
		System.out.println("高品质链接：" + music.getHQMusicUrl());
		
		System.exit(0);
	}
}