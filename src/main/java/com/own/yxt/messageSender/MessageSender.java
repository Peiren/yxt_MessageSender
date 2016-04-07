package com.own.yxt.messageSender;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;

import net.sf.json.JSONObject;

import org.apache.http.HttpMessage;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageSender {
	
	@Autowired
	private Config config;
	
	private CloseableHttpClient httpClient;
	private CookieStore cookieStore;
	private HttpPost postRequest;
	private HttpGet getRequest;
	private CloseableHttpResponse response;
	private String resPayload;

	public MessageSender(){
		cookieStore = new BasicCookieStore();
		httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
	}
	
	public void sendForUser(JSONObject userInfo){

		try{
			JSONObject user = loginYxt(userInfo.getString("loginName"), userInfo.getString("loginPwd"));
			if(user != null){
				saveRole(user.getString("userid"),user.getString("accountid"));
				sendMessage(userInfo.getString("studentCode"),userInfo.getString("userName"),userInfo.getInt("messageCount"));
			}
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
	
	private JSONObject loginYxt(String loginName, String loginPwd) throws URISyntaxException{
		postRequest = new HttpPost(config.getLoginPath());
		postRequest.addHeader("Host", "www.youxuetong.com");
		postRequest.addHeader("Connection", "keep-alive");
		postRequest.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		postRequest.addHeader("Accept-Encoding", "gzip, deflate");
		postRequest.addHeader("Accept-Language", "en");
		postRequest.addHeader("Content-Type"," application/x-www-form-urlencoded; charset=UTF-8");
		String payload = MessageFormat.format(config.getCredential(), loginName, loginPwd);
		StringEntity entity = new StringEntity(payload, ContentType.create("text/plain", "UTF-8"));
		postRequest.setEntity(entity);
		
		try {
			response = httpClient.execute(postRequest);
			return JSONObject.fromObject(resPayload).getJSONObject("userobj");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private void saveRole(String userId, String accountId) throws URISyntaxException{
		URI saveRoleuri = new URIBuilder(config.getRolePath()).setCustomQuery(MessageFormat.format(config.getRoleQuery(), accountId, userId)).build();
		getRequest = new HttpGet(saveRoleuri);
		try {
			response = httpClient.execute(getRequest);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendMessage(String studentCode, String signature,int messageCount) throws URISyntaxException{
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
