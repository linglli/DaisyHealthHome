package com.imooc.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 百度翻译引擎java示例代码
 */
public class BaiduTranslateDemo{
	
	private static final String UTF8 = "UTF-8";
	
	//申请者开发者id，实际使用时请修改成开发者自己的appid
	private static final String appId = "20161006000029752";

	//申请成功后的证书token，实际使用时请修改成开发者自己的token
	private static final String token = "LWVZVB7WWnqpG0KFpmJe";

	private static final String url = "http://api.fanyi.baidu.com/api/trans/vip/translate";

	//随机数，用于生成md5值，开发者使用时请激活下边第四行代码
	private static final Random random = new Random();
	
	public String translate(String q) throws Exception{
		//用于md5加密
		//int salt = random.nextInt(10000);
		//本演示使用指定的随机数为1435660288
		int salt = 1435660288;
		
		// 对appId+源文+随机数+token计算md5值
		StringBuilder md5String = new StringBuilder();
		md5String.append(appId).append(q).append(salt).append(token);
		String md5 = DigestUtils.md5Hex(md5String.toString());

		//使用Post方式，组装参数
		HttpPost httpost = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();  
		nvps.add(new BasicNameValuePair("q", q));  
		   nvps.add(new BasicNameValuePair("from", "auto"));  
		   nvps.add(new BasicNameValuePair("to", "auto"));  
		   nvps.add(new BasicNameValuePair("appid", appId));  
		   nvps.add(new BasicNameValuePair("salt", String.valueOf(salt)));  
		   nvps.add(new BasicNameValuePair("sign", md5));  
		httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));  

		//创建httpclient链接，并执行
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpResponse response = httpclient.execute(httpost);
	    
	    //对于返回实体进行解析
		HttpEntity entity = response.getEntity();
		InputStream returnStream = entity.getContent();
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(returnStream, UTF8));
		StringBuilder result = new StringBuilder();
		String str = null;
		while ((str = reader.readLine()) != null) {
			result.append(str).append("\n");
		}
		
		//转化为json对象，注：Json解析的jar包可选其它
		JSONObject resultJson =JSONObject.fromObject(result.toString());

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
		String text = dst.getString("dst");
		text = URLDecoder.decode(text, UTF8);

		return text;
	}
	
	//实际抛出异常由开发者自己处理
	public static  String translateToEn(String q) throws Exception{
		BaiduTranslateDemo baidu = new BaiduTranslateDemo();
		
		String result = null;
		try {
			result = baidu.translate(q);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
