package com.own.yxt.messageSender;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;


public class MessageRecorder implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		HttpRequest reqeust = (HttpRequest)invocation.getArguments()[0];
		
		System.out.println(reqeust.getRequestLine());
		for(Header header : reqeust.getAllHeaders()){
			System.out.println(header.getName()+": "+header.getValue());
		}
		
		System.out.println();
		
		if(reqeust instanceof HttpPost){
			System.out.println(EntityUtils.toString(((HttpPost) reqeust).getEntity()));
		}
		
		CloseableHttpResponse response  = (CloseableHttpResponse)invocation.proceed();
		
		System.out.println("-----------------------------------------------------------------");
		System.out.println(response.getStatusLine());
		for (Header header : response.getAllHeaders()) {
			System.out.println(header.getName() + ":" + header.getValue());
		}
		System.out.println();
		String temp = EntityUtils.toString(response.getEntity(), "gb2312");
		System.out.println(temp);
		response.setEntity(new StringEntity(temp, "gb2312"));
		
		return response;
	}

}
