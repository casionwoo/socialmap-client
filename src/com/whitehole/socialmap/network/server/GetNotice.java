package com.whitehole.socialmap.network.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONException;
import org.json.JSONObject;

import com.whitehole.socialmap.login.google.HttpClientService;

import android.os.AsyncTask;
import android.util.Log;


public class GetNotice {

	// url to Server
	private final String URL_SERVER = "http://casionwoo.appspot.com/";
	private final String URL_GET_NOTICE = "rest/notice/get";

	private String getnoticeurl = String.format("%s%s", URL_SERVER,
			URL_GET_NOTICE);

	List<Notice_List> sp;
	
	public List<Notice_List> GetTotalNotice() {

		Log.i("GetNotice", "Started111111111");

		HttpGet request = new HttpGet(getnoticeurl);
		HttpResponse hr = null;

		HttpTask task = new HttpTask() {
			/*@Override
			protected void onPostExecute(HttpResponse result) {
				try {
					onReceiveHotCoolPlace(result);
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}*/
		};
		task.execute(request);
		
		try {
			hr = task.get();
			Log.e("task.get", task.get().toString());
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		if(hr != null){
			onReceiveNoticeTask hTask = new onReceiveNoticeTask(){
			};
			
			hTask.execute(hr);
			try {
				sp = hTask.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return sp;		
	}

	private abstract class onReceiveNoticeTask extends AsyncTask<HttpResponse, Void, List<Notice_List>>{

		@Override
		protected List<Notice_List> doInBackground(HttpResponse... params) {
			
			try {
				sp = onReceiveNotice(params[0]);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return sp;
		}
	}
	private Notice_List convertToNotice(JSONObject json) throws JSONException{
		Notice_List nl = new Notice_List();
		nl.Subject = json.getString("Subject");
		nl.Content = json.getString("Content");
		
		return nl;
	}
	public List<Notice_List> onReceiveNotice(HttpResponse result) throws JSONException, UnsupportedEncodingException, IllegalStateException, IOException {
		// TODO Auto-generated method stub
		List<Notice_List> notice_List = new ArrayList<Notice_List>();
		StringWriter writer = new StringWriter();
		System.err.println(result.getStatusLine());
		System.err.println(result.getEntity().getContentType());
		IOUtils.copy(new InputStreamReader(result.getEntity().getContent(), "UTF-8") , writer);
		
		JSONObject json = new JSONObject(writer.toString());
//		Log.e("json", json.toString());
//		Log.e("json length", ""+json.length());
		if(json.length() <= 1)
		{
			JSONObject jo = json.optJSONObject("data");
//			Log.e("jo", "jo: " + jo.toString());
//			Log.e("jo", "jo length: " + jo.length());
			if(jo.length() != 0)
				notice_List.add(convertToNotice(jo));
		}
		else{
			for(int i = 0 ; i < json.getJSONArray("data").length() ; i++){
				JSONObject jo = (JSONObject) json.getJSONArray("data").get(i);
				notice_List.add(convertToNotice(jo));
			}
		}
		
		return notice_List;
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
