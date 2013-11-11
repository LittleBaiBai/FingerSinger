package com.game.fingersinger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

/**
 * �?: HttpClientUtils ???�?: TODO �????: 康�?��?? kangxuming@1000chi.com ??��??: 2012-10-30
 * �????1:53:55
 */
public class ProxyHttpUtils {

	/**
	 * ??��??: postRequest ???�?: TODO ??????: @param host ??????: @param port ??????: @param url
	 * ??????: @param params ??????: @return �????: String �?�? �????: 康�?��??
	 * kangxuming@1000chi.com ??��??: 2012-10-30 �????1:56:31
	 * @throws Exception 
	 */
	public static String postRequest(String url, String params) {
		String result = new String();
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		httppost.setHeader("Content-Type", "text/plain;charset=UTF-8");
		HttpEntity httpEntity = null;
		try {
			httpEntity = new StringEntity(params, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		httppost.setEntity(httpEntity);
		try {
			Log.v("ProxyHttpUtils", "??????请�??�???��??=???" + url + "???�?�?");
//			Log.v("ProxyHttpUtils", "请�??Header=???" + httppost.getHeaders("Content-Type").toString() + "???");
			//Log.v("ProxyHttpUtils", "????????��???????��?��??�?�???��??�?" + httppost.toString());
			HttpResponse response = httpclient.execute(httppost);
			Log.v("ProxyHttpUtils", "??????请�??�???��??=???" + url + "???�????");
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, "UTF-8");
				Log.v("ProxyHttpUtils", "??��?????�?�??????????=???" + result + "???");
			} else {
				Log.v("ProxyHttpUtils", "??��?????�?�????失败=???" + result + "???");
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static boolean getDescription(String url) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("Content-Type", "text/plain;charset=UTF-8");
		Log.v("ProxyHttpUtils", "url: " + url);
		try {
//			Log.v("ProxyHttpUtils", "请�??Header=???" + httppost.getHeaders("Content-Type").toString() + "???");
			//Log.v("ProxyHttpUtils", "????????��???????��?��??�?�???��??�?" + httppost.toString());
			httpclient.execute(httpGet);
			Log.v("ProxyHttpUtils", "getDescription�???��??=???" + url + "???�????");
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
		
	}
}
