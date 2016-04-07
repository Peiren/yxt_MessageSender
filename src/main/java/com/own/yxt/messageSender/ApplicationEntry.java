package com.own.yxt.messageSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component("entry")
public class ApplicationEntry {
	
	@Autowired
	private MessageSender messageSender;
	
	private List<JSONObject> userInfos = new ArrayList<JSONObject>();
	
	public static void main(String args[]) throws IOException{
		GenericXmlApplicationContext context = new GenericXmlApplicationContext();
		context.load("classpath:mes-context.xml");
		context.refresh();
		ApplicationEntry entry = context.getBean("entry", ApplicationEntry.class);
		entry.populateUsers();
		entry.sendMessage();
		context.close();
	}
	
	public void populateUsers() throws IOException{
		String line = null;
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("userInfo.properties");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		while((line = reader.readLine())!= null){
			userInfos.add(JSONObject.fromObject(line));
		}
	}
	
	public void sendMessage() {
		for (int i=0; i<userInfos.size(); i++) {
			messageSender.sendForUser(userInfos.get(i));
		}
	}
}
