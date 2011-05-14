package com.zabbix;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.provider.ContactsContract.RawContacts.Entity;
import android.util.Log;

public class ZabbixApiAccess {
	
	private static final String ZABBIX_API_PATH = "/zabbix/api_jsonrpc.php";
	private static final String CONTENT_TYPE = "application/json-rpc";
	private String uri;
	private String host;
	private HttpPost httpPost;
	private JSONObject jsonObject = new JSONObject();
	
	public void setHost(String host)
	{
		this.host = host;
		
	}
	
	public void setUri(String uri)
	{
		this.uri = uri;
	}
	
	public String getHost()
	{
		return this.host;
	}
	
	public String getUri()
	{
		return this.uri;
	}
	
	public String makeUri(String host)
	{
		Uri.Builder uriBuilder = new Uri.Builder();
    	uriBuilder.scheme("http");
    	uriBuilder.authority(host);
    	uriBuilder.path(ZABBIX_API_PATH);
    	this.uri = Uri.decode(uriBuilder.build().toString());
    	return this.uri;
	}
	
	public void setHttpPost(String uri)
	{
		httpPost = new HttpPost(uri);
		httpPost.setHeader("Content-type", CONTENT_TYPE);
	}
	
	public void setBasicJSONParams()
	{
		try {
			jsonObject.put("jsonrpc", "2.0");
			jsonObject.put("id", "1");
			
		} catch (JSONException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}	
		
	}
	
	public JSONObject getJsonObject()
	{
		return this.jsonObject;
	}
	
	public void setMethod(String method)
	{
		this.setBasicJSONParams();
		try {
			jsonObject.put("method", method);
		} catch (JSONException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}
	
	public String getMethod()
	{
		try {
			return this.jsonObject.getString("method");
		} catch (JSONException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
			return "error";
		}
	}
	
	
	public String zabbixAuthenticate(String account_name, String pass)
	{
		JSONObject params = new JSONObject();
		try {
			params.put("user", account_name);
			params.put("password", pass);
			jsonObject.put("params", params);
		} catch (JSONException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
		
		StringEntity stringEntity = null ;
		try {
			stringEntity = new StringEntity(jsonObject.toString());
			try {
				Log.e("APIcheck",EntityUtils.toString(stringEntity));
			} catch (ParseException e) {
				// TODO �����������ꂽ catch �u���b�N
				e.printStackTrace();
			} catch (IOException e) {
				// TODO �����������ꂽ catch �u���b�N
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
		httpPost.setEntity(stringEntity);
		DefaultHttpClient httpClient = new DefaultHttpClient();
    	try {
			HttpResponse httpResponse = httpClient.execute(httpPost);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK)
			{
				String entity = EntityUtils.toString(httpResponse.getEntity());
				JSONObject jsonEntity = new JSONObject(entity);
				return jsonEntity.getString("result");
				
			}else
			{
				return "error";
			}
			//return EntityUtils.toString(httpResponse.getEntity());
    	} catch (ClientProtocolException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
			return "error";
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
			return "error";
		} catch (JSONException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
			return "error";
		}
		
	}
	
	private JSONObject apiAccess(String authKey, JSONObject params)
	{
		JSONObject jsonEntity = null;
		try {
			jsonObject.put("jsonrpc", "2.0");
			jsonObject.put("params", params);
			jsonObject.put("auth", authKey);
			jsonObject.put("id", "1");
			StringEntity stringEntity = new StringEntity(jsonObject.toString());
			httpPost.setEntity(stringEntity);
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = httpClient.execute(httpPost);
			
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK)
			{
				String entity = EntityUtils.toString(httpResponse.getEntity());
				Log.e("Response", entity);
				jsonEntity = new JSONObject(entity);
				return jsonEntity;
				
			}else
			{
				return jsonEntity;
			}
		} catch (JSONException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
			return jsonEntity;
		} catch (UnsupportedEncodingException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
			return jsonEntity;
		} catch (ClientProtocolException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
			return jsonEntity;
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
			return jsonEntity;
		}
		
	}
	public ArrayList<Host> getHostList(String authKey, String filter)
	{
		ArrayList<Host> hostList = new ArrayList<Host>();
		
		JSONObject subParams = new JSONObject();
		JSONObject subsubParams = new JSONObject();
		JSONObject response = null;
		
		
		try {
			this.jsonObject.put("method", "host.get");
			subParams.put("extendoutput", "true");
			if ( filter != "all" )
			{
				subsubParams.put("host", "[\""+filter+"\"]");
				subParams.put("filter", subsubParams);
			}
			response = this.apiAccess(authKey, subParams);
			JSONArray resultObject = response.getJSONArray("result");
			
			int count = resultObject.length();
			
			for (int i=0; i<count; i++)
			{
				Host host = new Host();
				host.setHostId(resultObject.getJSONObject(i).getString("hostid"));
				host.setHostName(resultObject.getJSONObject(i).getString("host"));
				//hostList.add(resultObject.getJSONObject(i).getString("host"));
				Log.e("hostID",host.getHostId());
				Log.e("hostName",host.getHostName());
				hostList.add(host);
			}			
			return hostList;
			
		} catch (JSONException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
			return hostList;
		}
		
	}

}