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
import android.os.AsyncTask;

public abstract class HttpRequest extends AsyncTask<Void, Void, String>{
	public static String URL = "http://hmt.no-ip.org:13009";
	
	private int method = Method.POST;
	private String url = "";
	private List<NameValuePair> parameters = new ArrayList<NameValuePair>();
	private HttpClient http = new DefaultHttpClient();

	public List<NameValuePair> getParameters() {
		return parameters;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getMethod() {
		return method;
	}

	public void setMethod(int method) {
		this.method = method;
	}
	
	private String getResponse(HttpResponse response) throws IllegalStateException, IOException{
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer stringBuffer = new StringBuffer("");
		String line = "";
		String LineSeparator = System.getProperty("line.separator");
		while ((line = bufferedReader.readLine()) != null) {
			 stringBuffer.append(line + LineSeparator); 
		}
		bufferedReader.close();
		return stringBuffer.toString();
	}

	@Override
	protected String doInBackground(Void... params) {
		UrlEncodedFormEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(this.getParameters(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(this.method == Method.POST){
			HttpPost post = new HttpPost(this.getUrl());
			post.setEntity(entity);
			try {
				HttpResponse response = this.http.execute(post);
				return this.getResponse(response);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			// for get method
		}
		return null;
	}
	
	public static class Method{
		public static int POST = 0;
		public static int GET = 1;
	}
}