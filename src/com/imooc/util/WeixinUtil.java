package com.imooc.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.imooc.menu.Button;
import com.imooc.menu.ClickButton;
import com.imooc.menu.Menu;
import com.imooc.menu.ViewButton;
import com.imooc.po.AccessToken;
import com.imooc.trans.Data;
import com.imooc.trans.Parts;
import com.imooc.trans.Symbols;
import com.imooc.trans.TransResult;

import net.sf.json.JSONObject;

public class WeixinUtil {
	private static final String APPID = "wx06da3745d2fb7178";
	private static final String APPSECRET = "f8969e2c39451dbcc79bbd0b178ed02b";
	private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	private static final String CREATE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
	private static final String QUERY_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN";
	public static JSONObject doGetStr(String url) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		JSONObject jsonObject = null;
		try {
			HttpResponse response = httpClient.execute(httpGet);
			//System.out.println(response.toString());
			HttpEntity entity = response.getEntity();
			System.out.println(entity.toString());
			if(entity != null) {
				String result = EntityUtils.toString(entity, "UTF-8");
				//System.out.println(result);
				jsonObject = JSONObject.fromObject(result);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	public static JSONObject doPostStr(String url, String outStr) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		JSONObject jsonObject = null;
		try {
			httpPost.setEntity(new StringEntity(outStr, "UTF-8"));
			HttpResponse response = httpClient.execute(httpPost);
			String result = EntityUtils.toString(response.getEntity(), "UTF-8");
			jsonObject = JSONObject.fromObject(result);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jsonObject;
	}
	
	public static AccessToken getAccessToken() {
		AccessToken token = new AccessToken();
		String url = ACCESS_TOKEN_URL.replace("APPID", APPID).replace("APPSECRET", APPSECRET);
		JSONObject jsonObject = doGetStr(url);
		if(jsonObject != null) {
			token.setToken(jsonObject.getString("access_token"));
			token.setExpiresIn(jsonObject.getInt("expires_in"));
		}
		return token;
	}
	
	public static Menu initMenu() {
		Menu menu = new Menu();
		ClickButton clickBT11 = new ClickButton();
		clickBT11.setName("Click Menu");
		clickBT11.setType("click");
		clickBT11.setKey("11");
		
		ViewButton viewBT21 = new ViewButton();
		viewBT21.setName("View Menu");
		viewBT21.setType("view");
		viewBT21.setUrl("http://www.imooc.com");
		
		ClickButton clickBT31 = new ClickButton();
		clickBT31.setName("Scan Event");
		clickBT31.setType("scancode_push");
		clickBT31.setKey("31");
		
		ClickButton clickBT32 = new ClickButton();
		clickBT32.setName("Location Select");
		clickBT32.setType("location_select");
		clickBT32.setKey("32");
		
		Button button3 = new Button();
		button3.setName("Menu");
		button3.setSub_button(new Button[]{clickBT31, clickBT32});
		
		menu.setButton(new Button[] {clickBT11, viewBT21, button3});
		
		return menu;
	}
	
	public static int createMenu(String token, String menu) {
		int result = 0;
		String url = CREATE_MENU_URL.replace("ACCESS_TOKEN", token);
		JSONObject jsonObject = doPostStr(url, menu);
		if(jsonObject != null) {
			result = jsonObject.getInt("errcode");
		}
		return result;
	}
	
	public static JSONObject queryMenu(String token) {
		String url = QUERY_MENU_URL.replace("ACCESS_TOKEN", token);
		JSONObject jsonObject = doGetStr(url);
		return jsonObject;
	}
	
	public static String translate(String source) {
		String url = "http://api.fanyi.baidu.com/api/trans/vip/translate?q=KEYWORD&from=auto&to=auto"
				+ "appid=APPID&salt=SALT&sign=SIGN";
		
		StringBuffer dst = new StringBuffer();
		try {
			url = url.replace("KEYWORD", URLEncoder.encode(source, "UTF-8"));
			JSONObject jsonObject = doGetStr(url);
			System.out.println(jsonObject);
			String errno = jsonObject.getString("errno");
			
			if("0".equals(errno)) {
				TransResult transResult = (TransResult) JSONObject.toBean(jsonObject, TransResult.class);
				Data data = transResult.getData();
				Symbols symbols = data.getSymbols()[0];
				String phzh = symbols.getPh_zh() == null?"":"中文："+symbols.getPh_zh() + "\n";
				String phen = symbols.getPh_en() == null?"":"英式："+symbols.getPh_en() + "\n";
				String pham = symbols.getPh_am() == null?"":"美式："+symbols.getPh_am() + "\n";
				dst.append(phzh + phen + pham);
				
				Parts[] parts = symbols.getParts();
				String pat = null;
				for(Parts part: parts) {
					pat = (part.getPart() != null && !"".equals(part.getPart()))
							?"["+part.getPart()+"]":"";
					String[] means = part.getMeans();
					dst.append(pat);
					for(String mean: means) {
						dst.append(mean+";");
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return dst.toString();
	}
	
	public static String translateFull(String source) {
		String url = "http://api.fanyi.baidu.com/api/trans/vip/translate?q=KEYWORD&from=auto&to=auto"
				+ "&appid=APPID&salt=SALT&sign=SIGN";
		String appId = "20161006000029752";
		String token = "LWVZVB7WWnqpG0KFpmJe";
		String salt = "1435660288";
		StringBuffer signBuffer = new StringBuffer();
		signBuffer.append(appId).append(source).append(salt).append(token);
		String signMD5 = DigestUtils.md5Hex(signBuffer.toString());
		url = url.replace("KEYWORD", source).replace("APPID", appId).
				replace("SALT", salt).replace("SIGN", signMD5);
		System.out.println(url);
		StringBuffer dst = new StringBuffer();
		try {
			url = URLEncoder.encode(url, "UTF-8");
			url = "http://api.fanyi.baidu.com/api/trans/vip/translate";
			HttpPost httpost = new HttpPost(url);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();  
			nvps.add(new BasicNameValuePair("q", source));  
			   nvps.add(new BasicNameValuePair("from", "auto"));  
			   nvps.add(new BasicNameValuePair("to", "auto"));  
			   nvps.add(new BasicNameValuePair("appid", appId));  
			   nvps.add(new BasicNameValuePair("salt", String.valueOf(salt)));  
			   nvps.add(new BasicNameValuePair("sign", signMD5));  
			httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));  

			//创建httpclient链接，并执行
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(httpost);
		    
		    //对于返回实体进行解析
			HttpEntity entity = response.getEntity();
			InputStream returnStream = entity.getContent();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(returnStream, "UTF-8"));
			StringBuilder result = new StringBuilder();
			String str = null;
			while ((str = reader.readLine()) != null) {
				result.append(str).append("\n");
			}
			
			//转化为json对象，注：Json解析的jar包可选其它
			JSONObject jsonObject =JSONObject.fromObject(result.toString());
			System.out.println(jsonObject);
			String errno = jsonObject.getString("trans_result");
			
			if(!"[]".equals(errno)) {
				TransResult transResult = (TransResult) JSONObject.toBean(jsonObject, TransResult.class);
				Data data = transResult.getData();
				Symbols symbols = data.getSymbols()[0];
				String phzh = symbols.getPh_zh() == null?"":"中文："+symbols.getPh_zh() + "\n";
				String phen = symbols.getPh_en() == null?"":"英式："+symbols.getPh_en() + "\n";
				String pham = symbols.getPh_am() == null?"":"美式："+symbols.getPh_am() + "\n";
				dst.append(phzh + phen + pham);
				
				Parts[] parts = symbols.getParts();
				String pat = null;
				for(Parts part: parts) {
					pat = (part.getPart() != null && !"".equals(part.getPart()))
							?"["+part.getPart()+"]":"";
					String[] means = part.getMeans();
					dst.append(pat);
					for(String mean: means) {
						dst.append(mean+";");
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return dst.toString();
	}
}
