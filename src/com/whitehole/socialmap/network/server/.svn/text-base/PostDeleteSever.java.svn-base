package com.whitehole.socialmap.network.server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.*;
import org.json.JSONException;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.whitehole.socialmap.login.google.HttpClientService;
import com.whitehole.socialmap.network.SPicture;

public class PostDeleteSever {
	
	// url to Server
	private final String URL_SERVER = "http://casionwoo.appspot.com/";
	private final String URL_PHOTO_SERVER = "rest/facebook/postphotos";
	private final String URL_DELETE_SERVER = "rest/delete/photo";
	private final String URL_ALL_DELETE_SERVER = "rest/delete/photos";
	

	private String putphotourl = String.format("%s%s", URL_SERVER,
			URL_PHOTO_SERVER); // Sending photo to Server
	private String deletephotourl = String.format("%s%s", URL_SERVER,
			URL_DELETE_SERVER); // Delete photo
	private String deleteallphotourl = String.format("%s%s", URL_SERVER,
			URL_ALL_DELETE_SERVER); // Delete all photo
	
	/**
	 * num=0 이면 최초 로그인, num=1이면 추가해야하는 사진들
	 * 사용자가 로그아웃 해도 소셜맵 서버의 사진은 지워지지 않으므로, num=0일때,
	 * 사용자가 모든 사진을 서버로 보내면 서버는 기존에 저장되어 있는 사용자의 사진과 비교하여
	 * 추가된 사진만 서버에 저장하게 된다.
	 * @param my_public_pic
	 * @param num
	 * @throws IOException
	 * @throws JSONException
	 */
	public void PostPhotos(ArrayList<SPicture> my_public_pic, int num) throws IOException,
			JSONException {
		
		for(SPicture s : my_public_pic){
			
			s.ownerName = URLEncoder.encode( s.ownerName, "EUC-KR");
			
			if(s.record.length() > 500){
				s.record = s.record.substring(0, 496);
				s.record = s.record + "...";
			}
				
				
			s.record = URLEncoder.encode( s.record, "EUC-KR");
			s.placeName = URLEncoder.encode( s.placeName, "EUC-KR");
		}
		
		
		Gson gson = new Gson();
		//Log.i("gson", gson.toJson(new SSPicture(0, my_public_pic)));
		HttpPost httppost;
		
		HttpParams prams = new BasicHttpParams();		
			
		if(num == 0)
			prams.setBooleanParameter("isFirst", true);
		else
			prams.setBooleanParameter("isFirst", false);
			
		httppost = new HttpPost(putphotourl);
		
		StringEntity se = new StringEntity(gson.toJson(new SSPicture(0,
				my_public_pic)));
		httppost.setEntity(se);
		httppost.setParams(prams);
		httppost.setHeader("Accept", "application/json");
		HttpTask task = new HttpTask() {
			@Override
			protected void onPostExecute(HttpResponse result) {
				onCheckResult(result);
			}
		};
		task.execute(httppost);
	}
	
	public void DeletePhotos(ArrayList<String> delete_pic) throws IOException,
	JSONException {
		Gson gson = new Gson();
		//Log.i("gson", gson.toJson(new SSPicture(my_public_pic)));
		HttpPost httppost = new HttpPost(deletephotourl);
		
		StringEntity se = new StringEntity(gson.toJson(new SSPicture(
				delete_pic))); // pictureID
		/*StringEntity se = new StringEntity( new SSPicture(
				delete_pic).sdata.toString()); */
		httppost.setEntity(se);
		httppost.setHeader("Accept", "application/json");

		HttpTask task = new HttpTask() {
			@Override
			protected void onPostExecute(HttpResponse result) {
				onCheckResult(result);
			}
		};
		task.execute(httppost);
	}
	
	public void DeleteAllPhotos(String ownerID) throws UnsupportedEncodingException{
		Gson gson = new Gson();
		
		HttpGet httpget = new HttpGet(deleteallphotourl + "?ownerId=" + ownerID);
		
		httpget.setHeader("Accept", "application/json");
		
		HttpTask task = new HttpTask() {
			@Override
			protected void onPostExecute(HttpResponse result) {
				onCheckResult(result);
			}
		};
		task.execute(httpget);
	}
	
	protected void onCheckResult(HttpResponse result) {
		System.err.println(result.getStatusLine());
		System.err.println(result.getStatusLine().getStatusCode());
		System.err.println(result.getEntity().getContentType());
		
		/* result.getEntity().getContentType() 값에 따라 수행 박장군 힘을내게!!!!!!! */
	}

	private abstract class HttpTask extends
			AsyncTask<HttpUriRequest, Object, HttpResponse> {
		@Override
		protected HttpResponse doInBackground(HttpUriRequest... params) {
			try {
				return HttpClientService.get().execute(params[0]);
			} catch (ClientProtocolException e) {
				Log.e("Http", "protocol error", e);
			} catch (IOException e) {
				Log.e("Http", "io error", e);
			}
			cancel(true);
			return null;
		}
	}
}