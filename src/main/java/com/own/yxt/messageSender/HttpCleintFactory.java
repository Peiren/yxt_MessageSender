package com.own.yxt.messageSender;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class HttpCleintFactory {
	
	public static CloseableHttpClient getClient(CookieStore cookieStore){
		return HttpClients.custom().setDefaultCookieStore(cookieStore).build();
	}
	
}
