package org.song.course.servlet;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.song.course.util.thread.TokenThread;
import org.song.weixin.util.WeixinUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 初始化servlet
 * 
 * @author liuyq
 * @date 2013-05-02
 */
public class InitServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(WeixinUtil.class);

	public void init() throws ServletException {
		// 获取web.xml中配置的参数
		TokenThread.appid = getInitParameter("appid");
		TokenThread.appsecret = getInitParameter("appsecret");

		log.info("weixin api appid:{}", TokenThread.appid);
		log.info("weixin api appsecret:{}", TokenThread.appsecret);

		// 未配置appid、appsecret时给出提示
		if ("".equals(TokenThread.appid) || "".equals(TokenThread.appsecret)) {
			log.error("appid and appsecret configuration error, please check carefully.");
		} else {
			// 启动定时获取access_token的线程
			new Thread(new TokenThread()).start();
		}
	}
}