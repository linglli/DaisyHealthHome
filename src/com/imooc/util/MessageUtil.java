package com.imooc.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.imooc.po.News;
import com.imooc.po.NewsMessage;
import com.imooc.po.TextMessage;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class MessageUtil {
	public static String MESSAGE_TEXT = "text";
	public static String MESSAGE_NEWS = "news";
	public static String MESSAGE_EVENT = "event";
	public static String MESSAGE_SUBSCRIBE = "subscribe";
	public static String MESSAGE_UNSUBSCRIBE = "unsubscribe";
	public static String MESSAGE_CLICK = "CLICK";
	public static String MESSAGE_VIEW = "VIEW";
	public static String MESSAGE_SCANCODE = "scancode_push";
	public static String MESSAGE_LOCATION = "location";
	
	public static Map<String, String> xmlToMap(HttpServletRequest req) throws IOException, DocumentException {
		Map<String, String> map = new HashMap<String, String>();
		SAXReader reader = new SAXReader();
		
		InputStream ins = req.getInputStream();
		Document doc = reader.read(ins);
		
		Element root = doc.getRootElement();
		
		List<Element> list = root.elements();
		for(Element e: list) {
			map.put(e.getName(), e.getText());
		}
		ins.close();
		return map;		
	}
	
	public static String textMessageToXML(TextMessage textMessage) {
		XStream xstream = new XStream(new StaxDriver());
		xstream.alias("xml", textMessage.getClass());
		return xstream.toXML(textMessage);
	}
	
	
	public static String initText(String toUserName, String fromUserName, String content) {
		TextMessage text = new TextMessage();
		text.setFromUserName(toUserName);
		text.setToUserName(fromUserName);
		text.setMsgType(MessageUtil.MESSAGE_TEXT);
		text.setCreateTime(new Date().getTime());
		text.setContent(content);
		return textMessageToXML(text);
	}
	
	public static String menuText() {
		StringBuffer sb = new StringBuffer();
		sb.append("欢迎您的关注，请按照菜单提示进行相关阅读：\n");
		sb.append("1[饮食]   2[心态]   3[运动]\n");
		sb.append("4[学习]   5[工作]   6[旅行]\n");
		sb.append("回复[?]显示此帮助菜单");
		return sb.toString();
	}
	
	public static String newsMessageToXML(NewsMessage newsMessage) {
		XStream xstream = new XStream(new StaxDriver());
		xstream.alias("xml", newsMessage.getClass());
		xstream.alias("item", new News().getClass());
		return xstream.toXML(newsMessage);
	}
	
	public static String initNewsMessage(String toUserName, String fromUserName) {
		String message = null;
		List<News>newsList = new ArrayList<News>();
		NewsMessage newsMessage = new NewsMessage();
		
		News news = new News();
		news.setTitle("洋兰");
		news.setDescription("很高兴遇到你");
		news.setPicUrl("http://daisyhealthhome.ngrok.cc/DaisyHealthHome/resource/image/flower.jpg");
		news.setUrl("http://daisyhealthhome.ngrok.cc/DaisyHealthHome");
		newsList.add(news);
		newsMessage.setToUserName(toUserName);
		newsMessage.setFromUserName(fromUserName);
		newsMessage.setCreateTime(new Date().getTime());;
		newsMessage.setMsgType(MESSAGE_NEWS);
		newsMessage.setArticles(newsList);
		newsMessage.setArticleCount(newsList.size());
		
		return newsMessageToXML(newsMessage);
	}
}
