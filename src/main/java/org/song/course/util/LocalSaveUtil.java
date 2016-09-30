package org.song.course.util;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.song.course.pojo.UserLocation;

/**
 * 
 * @author 存内存     后续改为存储为本地文件
 */
public class LocalSaveUtil {
	public static HashMap<String , UserLocation> map = new HashMap<String , UserLocation>();
	
	/**
	 * 获取用户最后一次发送的地理位置
	 * @param request 请求对象
	 * @param openId 用户的OpenID
	 * @return UserLocation
	 */
	public static UserLocation getLastLocation(HttpServletRequest request, String openId) {
		if(map.containsKey(openId)){
			return map.get(openId);
		}
		return null;
	}
	
	/**
	 * 保存用户地理位置
	 * 
	 * @param request 请求对象
	 * @param openId 用户的OpenID
	 * @param lng 用户发送的经度
	 * @param lat 用户发送的纬度
	 * @param bd09_lng 经过百度坐标转换后的经度
	 * @param bd09_lat 经过百度坐标转换后的纬度
	 */
	public static void saveUserLocation(HttpServletRequest request, String openId, String lng, String lat, String bd09_lng, String bd09_lat) {
		UserLocation ulocal=new UserLocation();
		ulocal.setOpenId(openId);
		ulocal.setLng(lng);
		ulocal.setLat(lat);
		ulocal.setBd09Lng(bd09_lng);
		ulocal.setBd09Lat(bd09_lat);
		if(map.containsKey(openId)){
			map.remove(openId);
			
		}
		map.put(openId, ulocal);	
	}
}
