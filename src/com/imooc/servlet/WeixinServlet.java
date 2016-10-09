package com.imooc.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.DocumentException;

import com.imooc.po.TextMessage;
import com.imooc.util.CheckUtil;
import com.imooc.util.MessageUtil;

public class WeixinServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String signature = req.getParameter("signature");
		String timestamp = req.getParameter("timestamp");
		String nonce = req.getParameter("nonce");
		String echostr = req.getParameter("echostr");
		
		PrintWriter out = resp.getWriter();
		if(CheckUtil.checkSignature(signature, timestamp, nonce)) {
			out.print(echostr);
		}
		else {
			out.print("Validation failure.");
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		try {
			Map<String, String> map = MessageUtil.xmlToMap(req);
			String fromUserName = map.get("FromUserName");
			String toUserName = map.get("ToUserName");
			String msgType = map.get("MsgType");
			String content = map.get("Content");
			
			String message = null;
			System.out.println(msgType);
			if(MessageUtil.MESSAGE_TEXT.equals(msgType)) {
				if("?".equals(content) || "？".equals(content) ||
						"h".equals(content) || "H".equals(content) ||
						"help".equals(content) || "Help".equals(content) ||
						"帮助".equals(content)) {
					message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.menuText());
				}
				else if("2".equals(content)) {
					message = MessageUtil.initNewsMessage(toUserName, fromUserName);
				}
				else {
					TextMessage text = new TextMessage();
					text.setFromUserName(toUserName);
					text.setToUserName(fromUserName);
					text.setMsgType("text");
					text.setCreateTime(new Date().getTime());
					text.setContent("您发送的消息是：" + content);
					message = MessageUtil.textMessageToXML(text);
				}
			}
			else if(MessageUtil.MESSAGE_EVENT.equals(msgType)) {
				String eventType = map.get("Event");
				System.out.println(eventType);
				if(MessageUtil.MESSAGE_SUBSCRIBE.equals(eventType)) {
					message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.menuText());
				}
				else if(MessageUtil.MESSAGE_CLICK.equals(eventType)) {
					message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.menuText());
				}
				else if(MessageUtil.MESSAGE_VIEW.equals(eventType)) {
					String url = map.get("EventKey");
					message = MessageUtil.initText(toUserName, fromUserName, url);
				}
				else if(MessageUtil.MESSAGE_SCANCODE.equals(eventType)) {
					String key = map.get("EventKey");
					message = MessageUtil.initText(toUserName, fromUserName, key);
				}				
			}
			else if(MessageUtil.MESSAGE_LOCATION.equals(msgType)) {
				String label = map.get("Label");
				message = MessageUtil.initText(toUserName, fromUserName, label);
			}
			System.out.println(message);
			out.print(message);
		} catch(DocumentException e) {
			e.printStackTrace();
		} finally {
			out.close();
		}
	}

}
