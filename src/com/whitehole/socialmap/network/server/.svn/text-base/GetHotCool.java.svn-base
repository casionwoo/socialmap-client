package com.whitehole.socialmap.network.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
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

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.whitehole.socialmap.hotcool.PreHotPlace;
import com.whitehole.socialmap.login.google.HttpClientService;
import com.whitehole.socialmap.network.SPicture;

public class GetHotCool {
	private final String URL_SERVER = "http://casionwoo.appspot.com/";
	private final String URL_HOT_PLACE = "rest/hotplace/get";
	private final String URL_PRE_HOT_PLACE = "rest/hotplace/getprevious";

	String gethotplace = String.format("%s%s", URL_SERVER, URL_HOT_PLACE);
	String getprehotplace = String.format("%s%s", URL_SERVER, URL_PRE_HOT_PLACE);
	
	List<SPicture> sp;
	SSPicture pic;

	private Gson gson = new Gson();

	public List<SPicture> GetHotPlace(){

		Log.i("GetHotPlace", "Started1111111111111111111111111");
		HttpGet request = new HttpGet(gethotplace);
		HttpResponse hr = null;

		HttpTask task = new HttpTask() {
		};
		task.execute(request);

		try {
			hr = task.get();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if(hr != null){
			onReceiveHotCoolPlaceTask hTask = new onReceiveHotCoolPlaceTask() {
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
	
	public List<SPicture> GetPreHotPlace(){

		Log.i("GetPreHotPlace", "Started1111111111111111111111111");
		HttpGet request = new HttpGet(getprehotplace);
		HttpResponse hr = null;

		HttpTask task = new HttpTask() {
		};
		task.execute(request);

		try {
			hr = task.get();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if(hr != null){
			onReceiveHotCoolPlaceTask hTask = new onReceiveHotCoolPlaceTask() {
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

	private abstract class onReceiveHotCoolPlaceTask extends AsyncTask<HttpResponse, Void, List<SPicture>>{

		@Override
		protected List<SPicture> doInBackground(HttpResponse... params) {

			try {
				sp = onReceiveHotCoolPlace(params[0]);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonSyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonIOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return sp;
		}

	}	

	private SPicture convertToSpicture(JSONObject json) throws JSONException{
		SPicture spic = new SPicture();
		spic.commentURL = json.getString("commentURL");
		spic.imageURL = json.getString("imageURL");
		spic.latitude = json.getDouble("latitude");
		spic.longitude = json.getDouble("longitude");
		spic.makeTimeS = json.getString("makeTimeS");
		spic.ownerName = json.getString("ownerName");
		spic.ownerThumURL = json.getString("ownerThumURL");
		spic.pictureID = json.getString("pictureID");
		spic.placeName = json.getString("placeName");
		spic.record = json.getString("record");
		spic.smallImageURL = json.getString("smallImageURL");
		
		return spic;
	}
	
	public List<SPicture> onReceiveHotCoolPlace(HttpResponse result) throws JsonSyntaxException, JsonIOException, IllegalStateException, IOException, JSONException
	{

		List<SPicture> spicList = new ArrayList<SPicture>();
		StringWriter writer = new StringWriter();
		System.err.println(result.getStatusLine());
		if(result.getStatusLine().getStatusCode() == 204){
			Log.i("StatusCode 204", "1111111111111111111");
			return spicList;
		}
		
		IOUtils.copy(new InputStreamReader(result.getEntity().getContent(), "UTF-8") , writer);
		
		JSONObject json = new JSONObject(writer.toString());
		
		for(int i = 0 ; i < json.getJSONArray("data").length() ; i++){
			JSONObject jo = (JSONObject) json.getJSONArray("data").get(i);
			spicList.add(convertToSpicture(jo));
		}
		
		return spicList;
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