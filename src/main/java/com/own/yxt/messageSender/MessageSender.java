package com.own.yxt.messageSender;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageSender {
	
	@Autowired
	private Config config;
	
	@Resource(name="httpClient")
	private CloseableHttpClient httpClient;
	
	@Autowired
	private CookieStore cookieStore;

	private HttpGet getRequest;
	private HttpPost postRequest;
	private CloseableHttpResponse response;

	private void initPostReq() {
		postRequest = new HttpPost();
		postRequest.addHeader("Host", "www.youxuetong.com");
		postRequest.addHeader("Connection", "keep-alive");
		postRequest.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		postRequest.addHeader("Accept-Encoding", "gzip, deflate");
		postRequest.addHeader("Accept-Language", "en");
		postRequest.addHeader("Content-Type"," application/x-www-form-urlencoded; charset=UTF-8");
	}
	
	public void sendForUser(JSONObject userInfo){
		if (postRequest == null) {
			initPostReq();
		}
		BasicClientCookie accountid = new BasicClientCookie("accountid", userInfo.getString("accountid"));
		accountid.setDomain("www.youxuetong.com"); accountid.setPath("/");
		BasicClientCookie usertype = new BasicClientCookie("usertype", userInfo.getString("usertype"));
		usertype.setDomain("www.youxuetong.com");usertype.setPath("/");
		BasicClientCookie usercode = new BasicClientCookie("usercode", userInfo.getString("usercode"));
		usercode.setDomain("www.youxuetong.com");usercode.setPath("/");
		cookieStore.addCookie(accountid);
		cookieStore.addCookie(usertype);
		cookieStore.addCookie(usercode);

		try{
			sendMessage(userInfo.getString("studentCode"),userInfo.getString("userName"),userInfo.getInt("messageCount"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} finally{
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			cookieStore.clear();
		}
	}
	
	private void sendMessage(String studentCode, String signature,int messageCount) throws URISyntaxException {
		URI sendMessageURI = new URIBuilder(config.getSendPath()).build();
		postRequest.setURI(sendMessageURI);
		String basicPayloaod = MessageFormat.format(config.getSendPayload(), studentCode, signature, config.getMessageContent());
		int i = 0;
		messageCount+=i;
		for(; i <= messageCount; i++){
			StringEntity payload = new StringEntity( basicPayloaod+i ,ContentType.create("text/plain", "UTF-8"));
			postRequest.setEntity(payload);
			try {
				Thread.sleep(3000);
				response = httpClient.execute(postRequest);
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
