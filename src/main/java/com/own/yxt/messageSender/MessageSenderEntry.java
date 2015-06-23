package com.own.yxt.messageSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

public class MessageSenderEntry {
	
	private MessageSender messageSender;
	private List<JSONObject> userInfos;
	
	public MessageSenderEntry(){
		messageSender = new MessageSender();
		userInfos = new ArrayList<JSONObject>();
	}
	
	public static void main(String args[]) throws IOException{
		MessageSenderEntry entry = new MessageSenderEntry();
		entry.populateUsers();
	}
	
	
	public void populateUsers() throws IOException{
		String line = null;
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("userInfo.properties");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		while((line = reader.readLine())!= null){
			userInfos.add(JSONObject.fromObject(line));
		}
		for(JSONObject userInfo : userInfos){
			messageSender.sendForUser(userInfo);
		}
	}
}
