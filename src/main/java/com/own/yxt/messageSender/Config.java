package com.own.yxt.messageSender;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Config {

	@Value("${send_path}")
	private String sendPath;
	@Value("${send_payload}")
	private String sendPayload;
	@Value("${message_content}")
	private String messageContent;

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
