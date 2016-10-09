package com.imooc.test;

import com.imooc.po.AccessToken;
import com.imooc.util.WeixinUtil;

import net.sf.json.JSONObject;

public class WeixinTest {
	public static void main(String[] args) {
		AccessToken token = WeixinUtil.getAccessToken();
		System.out.println("票据：" + token.getToken());
		System.out.println("有效时间：" + token.getExpiresIn());
		
		String menu = JSONObject.fromObject(WeixinUtil.initMenu()).toString();
		int result = WeixinUtil.createMenu(token.getToken(), menu);
		if(result == 0) {
			System.out.println("Created menu successfully.");
		}
		else {
			System.out.println("Error code: " + result);
		}
		
		JSONObject jsonObject = WeixinUtil.queryMenu(token.getToken());
		System.out.println(jsonObject);
		
		//String tranResult = WeixinUtil.translateFull("足球");
		//System.out.println(tranResult);
		
		
		String src = "中国足球";
		String dst = null;
		try {
			dst = BaiduTranslateDemo.translateToEn(src);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(dst == null){
			System.out.println("翻译出错，参考百度错误代码和说明。");
			return;
		}
		System.out.println(src + "：" + dst);
	}
}
