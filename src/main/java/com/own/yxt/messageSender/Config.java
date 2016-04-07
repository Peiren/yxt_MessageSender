package com.own.yxt.messageSender;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Config {
	
	@Value("${login_path}")
	private String loginPath;
	@Value("${credential}")
	private String credential;
	@Value("${role_path}")
	private String rolePath;
	@Value("${role_query}")
	private String roleQuery;
	@Value("${send_path}")
	private String sendPath;
	@Value("${send_payload}")
	private String sendPayload;
	@Value("${message_content}")
	private String messageContent;
	
	public String getLoginPath() {
		return loginPath;
	}
	
	public String getCredential() {
		return credential;
	}
	
	public String getRolePath() {
		return rolePath;
	}
	
	public String getRoleQuery() {
		return roleQuery;
	}
	
	public String getSendPath() {
		return sendPath;
	}
	
	public String getSendPayload() {
		return sendPayload;
	}
	
	public String getMessageContent() {
		return messageContent;
	}
}
