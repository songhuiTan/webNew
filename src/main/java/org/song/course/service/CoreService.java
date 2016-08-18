package org.song.course.service;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.song.course.message.resp.Article;
import org.song.course.message.resp.Music;
import org.song.course.message.resp.MusicMessage;
import org.song.course.message.resp.NewsMessage;
import org.song.course.message.resp.TextMessage;
import org.song.course.util.MessageUtil;
import org.song.course.util.XiaoqUtil;

/**
 * 核心服务类
 * 
 * @author liufeng
 * @date 2013-05-20
 */
public class CoreService {
	/**
	 * 处理微信发来的请求
	 * 
	 * @param request
	 * @return
	 */
	public static String processRequest(HttpServletRequest request) {
		String respMessage = null;
		try {
			// 默认返回的文本消息内容
			String respContent = "请求处理异常，请稍候尝试！";

			// xml请求解析
			Map<String, String> requestMap = MessageUtil.parseXml(request);

			
			String time=requestMap.get("CreateTime");
			
			System.out.println("sendTime:"+XiaoqUtil.formatTime(time));
			
			
			// 发送方帐号（open_id）
			String fromUserName = requestMap.get("FromUserName");
			// 公众帐号
			String toUserName = requestMap.get("ToUserName");
			// 消息类型
			String msgType = requestMap.get("MsgType");

			// 回复文本消息
			TextMessage textMessage = new TextMessage();
			textMessage.setToUserName(fromUserName);
			textMessage.setFromUserName(toUserName);
			textMessage.setCreateTime(new Date().getTime());
			textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
			textMessage.setFuncFlag(0);

			String[] aArray = new String[]{"1","2","3","4","5"}; 
			// 文本消息
			if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
				respContent = "您发送的是文本消息！";
				String content = requestMap.get("Content").trim();  	
//				logger.log(Level.WARNING,"received content:"+content);
//				respContent=String.valueOf(content.indexOf("翻"));
				respContent=content;
				
				
//////////////////////音乐搜索////////////////////////
				// 如果以“歌曲”2个字开头
				if (content.startsWith("歌曲")) {
					// 将歌曲2个字及歌曲后面的+、空格、-等特殊符号去掉
					String keyWord = content.replaceAll("^歌曲[\\+ ~!@#%^-_=]?", "");
					// 如果歌曲名称为空
					if ("".equals(keyWord)) {
						respContent = getUsage();
					} else {
						String[] kwArr = keyWord.split("@");
						// 歌曲名称
						String musicTitle = kwArr[0];
						// 演唱者默认为空
						String musicAuthor = "";
						if (2 == kwArr.length)
							musicAuthor = kwArr[1];

						// 搜索音乐
						Music music = BaiduMusicService.searchMusic(musicTitle, musicAuthor);
						// 未搜索到音乐
						if (null == music) {
							respContent = "对不起，没有找到你想听的歌曲<" + musicTitle + ">。";
						} else {
							// 音乐消息
							MusicMessage musicMessage = new MusicMessage();
							musicMessage.setToUserName(fromUserName);
							musicMessage.setFromUserName(toUserName);
							musicMessage.setCreateTime(new Date().getTime());
							musicMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_MUSIC);
							musicMessage.setMusic(music);
							respMessage = MessageUtil.musicMessageToXml(musicMessage);
							return respMessage;
						}
					}
				}
//////////////////////音乐搜索////////////////////////
				
				
				
//////////////////////文字翻译////////////////////////
				if (content.startsWith("翻译")) {
					String keyWord = content.replaceAll("^翻译", "").trim();
					if ("".equals(keyWord)) {
						textMessage.setContent(getTranslateUsage());
					} else {
						textMessage.setContent(BaiduTranslateService.translate(keyWord,"auto","auto"));
					}
					respMessage = MessageUtil.textMessageToXml(textMessage);
					return respMessage;
				}
//////////////////////文字翻译////////////////////////
				
				
				
				if(XiaoqUtil.isQqFace(content)) {// 判断用户发送的是否是单个QQ表情
					// 回复文本消息
//					TextMessage textMessage = new TextMessage();
//					textMessage.setToUserName(fromUserName);
//					textMessage.setFromUserName(toUserName);
//					textMessage.setCreateTime(new Date().getTime());
//					textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
//					textMessage.setFuncFlag(0);
					// 用户发什么QQ表情，就返回什么QQ表情
					textMessage.setContent(content);
					// 将文本消息对象转换成xml字符串
					respMessage = MessageUtil.textMessageToXml(textMessage);
					return respMessage;
					
				}else if(Arrays.asList(aArray).contains(content)){// 判断1,2,3,4,5 发送图文信息
					NewsMessage newsMessage = new NewsMessage();
					newsMessage.setToUserName(fromUserName);
					newsMessage.setFromUserName(toUserName);
					newsMessage.setCreateTime(new Date().getTime());
					newsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
					newsMessage.setFuncFlag(0);
					List<Article> articleList = new ArrayList<Article>();
					// 单图文消息
					if ("1".equals(content)) {
						Article article = new Article();
						article.setTitle("微信公众帐号开发教程Java版");
						article.setDescription("柳峰，80后，微信公众帐号开发经验4个月。为帮助初学者入门，特推出此系列教程，也希望借此机会认识更多同行！");
						article.setPicUrl("http://0.xiaoqrobot.duapp.com/images/avatar_liufeng.jpg");
						article.setUrl("http://blog.csdn.net/lyq8479");
						articleList.add(article);
						// 设置图文消息个数
						newsMessage.setArticleCount(articleList.size());
						// 设置图文消息包含的图文集合
						newsMessage.setArticles(articleList);
						// 将图文消息对象转换成xml字符串
						respMessage = MessageUtil.newsMessageToXml(newsMessage);
					}
					// 单图文消息---不含图片
					else if ("2".equals(content)) {
						Article article = new Article();
						article.setTitle("微信公众帐号开发教程Java版");
						// 图文消息中可以使用QQ表情、符号表情
						article.setDescription("柳峰，80后，" + emoji(0x1F6B9)
								+ "，微信公众帐号开发经验4个月。为帮助初学者入门，特推出此系列连载教程，也希望借此机会认识更多同行！\n\n目前已推出教程共12篇，包括接口配置、消息封装、框架搭建、QQ表情发送、符号表情发送等。\n\n后期还计划推出一些实用功能的开发讲解，例如：天气预报、周边搜索、聊天功能等。");
						// 将图片置为空
						article.setPicUrl("");
						article.setUrl("http://blog.csdn.net/lyq8479");
						articleList.add(article);
						newsMessage.setArticleCount(articleList.size());
						newsMessage.setArticles(articleList);
						respMessage = MessageUtil.newsMessageToXml(newsMessage);
					}
					// 多图文消息
					else if ("3".equals(content)) {
						Article article1 = new Article();
						article1.setTitle("微信公众帐号开发教程\n引言");
						article1.setDescription("");
						article1.setPicUrl("http://0.xiaoqrobot.duapp.com/images/avatar_liufeng.jpg");
						article1.setUrl("http://blog.csdn.net/lyq8479/article/details/8937622");

						Article article2 = new Article();
						article2.setTitle("第2篇\n微信公众帐号的类型");
						article2.setDescription("");
						article2.setPicUrl("http://avatar.csdn.net/1/4/A/1_lyq8479.jpg");
						article2.setUrl("http://blog.csdn.net/lyq8479/article/details/8941577");

						Article article3 = new Article();
						article3.setTitle("第3篇\n开发模式启用及接口配置");
						article3.setDescription("");
						article3.setPicUrl("http://avatar.csdn.net/1/4/A/1_lyq8479.jpg");
						article3.setUrl("http://blog.csdn.net/lyq8479/article/details/8944988");

						articleList.add(article1);
						articleList.add(article2);
						articleList.add(article3);
						newsMessage.setArticleCount(articleList.size());
						newsMessage.setArticles(articleList);
						respMessage = MessageUtil.newsMessageToXml(newsMessage);
					}
					// 多图文消息---首条消息不含图片
					else if ("4".equals(content)) {
						Article article1 = new Article();
						article1.setTitle("微信公众帐号开发教程Java版");
						article1.setDescription("");
						// 将图片置为空
						article1.setPicUrl("");
						article1.setUrl("http://blog.csdn.net/lyq8479");

						Article article2 = new Article();
						article2.setTitle("第4篇\n消息及消息处理工具的封装");
						article2.setDescription("");
						article2.setPicUrl("http://avatar.csdn.net/1/4/A/1_lyq8479.jpg");
						article2.setUrl("http://blog.csdn.net/lyq8479/article/details/8949088");

						Article article3 = new Article();
						article3.setTitle("第5篇\n各种消息的接收与响应");
						article3.setDescription("");
						article3.setPicUrl("http://avatar.csdn.net/1/4/A/1_lyq8479.jpg");
						article3.setUrl("http://blog.csdn.net/lyq8479/article/details/8952173");

						Article article4 = new Article();
						article4.setTitle("第6篇\n文本消息的内容长度限制揭秘");
						article4.setDescription("");
						article4.setPicUrl("http://avatar.csdn.net/1/4/A/1_lyq8479.jpg");
						article4.setUrl("http://blog.csdn.net/lyq8479/article/details/8967824");

						articleList.add(article1);
						articleList.add(article2);
						articleList.add(article3);
						articleList.add(article4);
						newsMessage.setArticleCount(articleList.size());
						newsMessage.setArticles(articleList);
						respMessage = MessageUtil.newsMessageToXml(newsMessage);
					}
					// 多图文消息---最后一条消息不含图片
					else if ("5".equals(content)) {
						Article article1 = new Article();
						article1.setTitle("第7篇\n文本消息中换行符的使用");
						article1.setDescription("");
						article1.setPicUrl("http://0.xiaoqrobot.duapp.com/images/avatar_liufeng.jpg");
						article1.setUrl("http://blog.csdn.net/lyq8479/article/details/9141467");

						Article article2 = new Article();
						article2.setTitle("第8篇\n文本消息中使用网页超链接");
						article2.setDescription("");
						article2.setPicUrl("http://avatar.csdn.net/1/4/A/1_lyq8479.jpg");
						article2.setUrl("http://blog.csdn.net/lyq8479/article/details/9157455");

						Article article3 = new Article();
						article3.setTitle("如果觉得文章对你有所帮助，请通过博客留言或关注微信公众帐号xiaoqrobot来支持柳峰！");
						article3.setDescription("");
						// 将图片置为空
						article3.setPicUrl("");
						article3.setUrl("http://blog.csdn.net/lyq8479");

						articleList.add(article1);
						articleList.add(article2);
						articleList.add(article3);
						newsMessage.setArticleCount(articleList.size());
						newsMessage.setArticles(articleList);
						respMessage = MessageUtil.newsMessageToXml(newsMessage);
					}
					return respMessage;
				}
				
				
//				// 接收用户发送的文本消息内容
//				String content = requestMap.get("Content");
//				// 创建图文消息
			}
			// 图片消息
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_IMAGE)) {
				respContent = "您发送的是图片消息！";
			}
			// 地理位置消息
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LOCATION)) {
				respContent = "您发送的是地理位置消息！";
			}
			// 链接消息
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LINK)) {
				respContent = "您发送的是链接消息！";
			}
			// 音频消息
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VOICE)) {
				respContent = "您发送的是音频消息！";
			}
			// 事件推送
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
				// 事件类型
				String eventType = requestMap.get("Event");
				// 订阅
				if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
					respContent = "谢谢您的关注！";
				}
				// 取消订阅
				else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {
					// TODO 取消订阅后用户再收不到公众号发送的消息，因此不需要回复消息
				}
				// 自定义菜单点击事件
				else if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) {
					// TODO 自定义菜单权没有开放，暂不处理该类消息
					// 事件KEY值，与创建自定义菜单时指定的KEY值对应
					String eventKey = requestMap.get("EventKey");
					if (eventKey.equals("11")) {
						respContent = "天气预报菜单项被点击！";
					} else if (eventKey.equals("12")) {
						respContent = "公交查询菜单项被点击！";
					} else if (eventKey.equals("13")) {
						respContent = "周边搜索菜单项被点击！";
					} else if (eventKey.equals("14")) {
//						respContent = "历史上的今天菜单项被点击！";
						respContent = TodayInHistoryService.getTodayInHistoryInfo();
					} else if (eventKey.equals("21")) {
						respContent = "歌曲点播菜单项被点击！";
					} else if (eventKey.equals("22")) {
						respContent = "经典游戏菜单项被点击！";
					} else if (eventKey.equals("23")) {
						respContent = "美女电台菜单项被点击！";
					} else if (eventKey.equals("24")) {
						respContent = "人脸识别菜单项被点击！";
					} else if (eventKey.equals("25")) {
						respContent = "聊天唠嗑菜单项被点击！";
					} else if (eventKey.equals("31")) {
						respContent = "Q友圈菜单项被点击！";
					} else if (eventKey.equals("32")) {
						respContent = "电影排行榜菜单项被点击！";
					} else if (eventKey.equals("33")) {
						respContent = "幽默笑话菜单项被点击！";
					}
				}
			}

			textMessage.setContent(respContent);
			respMessage = MessageUtil.textMessageToXml(textMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return respMessage;
	}
	
	
	/**
	 * emoji表情转换(hex -> utf-16)
	 * @param hexEmoji
	 * @return
	 */
	public static String emoji(int hexEmoji) {
		return String.valueOf(Character.toChars(hexEmoji));
	}
	
	/**
	 * Q译通使用指南
	 * 
	 * @return
	 */
	public static String getTranslateUsage() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(emoji(0xe148)).append("Q译通使用指南").append("\n\n");
		buffer.append("Q译通为用户提供专业的多语言翻译服务，目前支持以下翻译方向：").append("\n");
		buffer.append("    中 -> 英").append("\n");
		buffer.append("    英 -> 中").append("\n");
		buffer.append("    日 -> 中").append("\n\n");
		buffer.append("使用示例：").append("\n");
		buffer.append("    翻译我是中国人").append("\n");
		buffer.append("    翻译dream").append("\n");
		buffer.append("    翻译さようなら").append("\n\n");
//		buffer.append("回复“?”显示主菜单");
		return buffer.toString();
	}
	
	/**
	 * 歌曲点播使用指南
	 * 
	 * @return
	 */
	public static String getUsage() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("歌曲点播操作指南").append("\n\n");
		buffer.append("回复：歌曲+歌名").append("\n");
		buffer.append("例如：歌曲存在").append("\n");
		buffer.append("或者：歌曲存在@汪峰").append("\n\n");
		buffer.append("回复“?”显示主菜单");
		return buffer.toString();
	}
}
