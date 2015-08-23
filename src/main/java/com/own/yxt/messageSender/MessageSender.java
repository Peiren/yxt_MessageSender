package com.own.yxt.messageSender;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;

import net.sf.json.JSONObject;

import org.apache.http.Header;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
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
import org.apache.http.util.EntityUtils;

public class MessageSender {
	
	private final static String LOGIN_PATH = "http://www.youxuetong.com/main/doUserLogin.do";
	private final static String LOGIN_ENTITY = "loginName={0}&sysId=6&loginPwd={1}";
	
	private final static String SAVE_ROLE_PATH = "http://www.youxuetong.com/roles/saverolesmes.do";
	private final static String SAVE_ROLE_QUERY = "accountid={0}&type=1&userid={1}&toflag=2";
	
	private final static String SEND_MESSAGE_PATH = "http://www.youxuetong.com/sendMsg/sendToGroup.do";
	private final static String SEND_MESSAGE_ENTITY = "studentCode={0}&messageType=2&sign=0&presetsign=班主任&signature={1}老师&scheduled=0&fixTime=&smsReceipt=0&content={2}";
	
	private final static String MESSAGE_CONTENT = "暑期快乐，注意防暑降温，多吃瓜果蔬菜。";
	
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
			JSONObject user = loginYxt(userInfo.getString("loginName"),userInfo.getString("loginPwd"));
			if(user != null){
				saveRole(user.getString("userid"),user.getString("accountid"));
				sendMessage(userInfo.getString("studentCode"),userInfo.getString("userName"),userInfo.getInt("messageCount"));
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			cookieStore.clear();
		}
	}
	
	private JSONObject loginYxt(String loginName, String loginPwd) throws URISyntaxException{
		postRequest = new HttpPost(LOGIN_PATH);
		postRequest.addHeader("Host", "www.youxuetong.com");
		postRequest.addHeader("Connection", "keep-alive");
		postRequest.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		postRequest.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.65 Safari/537.36");
		postRequest.addHeader("X-Requested-With", "XMLHttpRequest");
		postRequest.addHeader("Accept-Encoding", "gzip, deflate");
		postRequest.addHeader("Accept-Language", "en");
		postRequest.addHeader("Content-Type"," application/x-www-form-urlencoded; charset=UTF-8");
		String payload = MessageFormat.format(LOGIN_ENTITY,loginName,loginPwd);
		StringEntity entity = new StringEntity(payload,ContentType.create("text/plain", "UTF-8"));
		postRequest.setEntity(entity);
		
		try {
			response = httpClient.execute(postRequest);
			showHttpMessage(postRequest);
			showHttpMessage(response);
			
			return JSONObject.fromObject(resPayload).getJSONObject("userobj");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private void saveRole(String userId, String accountId) throws URISyntaxException{
		URI saveRoleuri = new URIBuilder(SAVE_ROLE_PATH).setCustomQuery(MessageFormat.format(SAVE_ROLE_QUERY, accountId, userId)).build();
		getRequest = new HttpGet(saveRoleuri);
		
		try {
			response = httpClient.execute(getRequest);
			showHttpMessage(getRequest);
			showHttpMessage(response);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendMessage(String studentCode, String signature,int messageCount) throws URISyntaxException{
		URI sendMessageURI = new URIBuilder(SEND_MESSAGE_PATH).build();
		postRequest.setURI(sendMessageURI);
		String basicPayloaod = MessageFormat.format(SEND_MESSAGE_ENTITY, studentCode, signature, MESSAGE_CONTENT);
		int i = 0;
		messageCount+=i;
		for(; i <= messageCount; i++){
			StringEntity payload = new StringEntity( basicPayloaod+i ,ContentType.create("text/plain", "UTF-8"));
			postRequest.setEntity(payload);
			try {
				Thread.sleep(3000);
				response = httpClient.execute(postRequest);
				showHttpMessage(postRequest);
				showHttpMessage(response);
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void showHttpMessage(HttpMessage message) throws ParseException, IOException{
		
		if(message instanceof HttpRequest){
			System.out.println(((HttpRequest) message).getRequestLine());
		}
		else if(message instanceof HttpResponse){			
			System.out.println(((HttpResponse) message).getStatusLine());
		}
		
		for(Header header : message.getAllHeaders()){
			System.out.println(header.getName()+": "+header.getValue());
		}

		System.out.println();
		
		if(message instanceof HttpPost){
			System.out.println(EntityUtils.toString(((HttpPost) message).getEntity()));
		}
		else if(message instanceof HttpGet){
			System.out.println("");
		}
		else if(message instanceof HttpResponse){			
			resPayload = EntityUtils.toString(((HttpResponse) message).getEntity());
			System.out.println(resPayload);
		}
		
		System.out.println("----------------------------------------------------------------------------------------------");
	}
}
