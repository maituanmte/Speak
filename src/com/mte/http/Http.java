package com.mte.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class Http {
	public static class Method {
		public static final int POST = 1;
		public static final int GET = 2;
	}

	private int method = Method.POST;
	public static final String TTS_URL = "http://hmt.no-ip.org:13009";
	private String url = TTS_URL;
	private HttpClient client = new DefaultHttpClient();
	private List<NameValuePair> params = new ArrayList<NameValuePair>();
	private List<OnResponseListener> listeners = new ArrayList<OnResponseListener>();

	public int getMethod() {
		return method;
	}

	public void setMethod(int method) {
		this.method = method;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public HttpClient getClient() {
		return client;
	}

	public void setClient(HttpClient client) {
		this.client = client;
	}

	public List<NameValuePair> getParams() {
		return params;
	}

	public void setParam(NameValuePair param) {
		this.params.add(param);
	}

	public void setParam(String key, String value) {
		this.setParam(new BasicNameValuePair(key, value));
	}

	public void setOnResponseListener(OnResponseListener listener) {
		this.listeners.add(listener);
	}

	public void dispatchResponseListener(String responseText) {
		for (OnResponseListener listener : this.listeners)
			listener.onResponse(responseText);
	}

	private String getResponse(HttpResponse response)
			throws IllegalStateException, IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(response.getEntity().getContent()));
		StringBuffer stringBuffer = new StringBuffer("");
		String line = "";
		String LineSeparator = System.getProperty("line.separator");
		while ((line = bufferedReader.readLine()) != null) {
			stringBuffer.append(line + LineSeparator);
		}
		bufferedReader.close();
		return stringBuffer.toString();
	}

	public void request() {
		UrlEncodedFormEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(this.params, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (this.method == Method.POST) {
			final HttpPost post = new HttpPost(this.getUrl());
			post.setEntity(entity);
			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					HttpResponse response;
					try {
						response = client.execute(post);
						String responseText = getResponse(response);
						dispatchResponseListener(responseText);
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					}
				}
			});
			thread.start();
		}
	}
}
