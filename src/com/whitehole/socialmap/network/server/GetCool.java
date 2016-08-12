package com.whitehole.socialmap.network.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import com.whitehole.socialmap.login.google.HttpClientService;
import com.whitehole.socialmap.network.SPicture;

public class GetCool {
	private final String URL_SERVER = "http://casionwoo.appspot.com/";
	private final String URL_COOL_PLACE = "rest/coolplace/get";
	private final String URL_COOL_REGET = "rest/coolplace/reget";

	String getcoolplace = String.format("%s%s", URL_SERVER, URL_COOL_PLACE);

	CCoolSPicture sp;
	CCoolSPicture ssp;
	SSPicture pic;

	private Gson gson = new Gson();

	public CCoolSPicture GetCoolPlace() {
		Log.i("GetCoolPlace", "Started1111111111111111111111111");
		HttpGet request = new HttpGet(getcoolplace);
		HttpResponse hr = null;

		HttpTask task = new HttpTask() {
//
//			@Override protected void onPostExecute(HttpResponse result) { 
//				try
//				{ 
//					onReceiveHotCoolPlace(result); 
//				} catch (IllegalStateException e)
//
//				{ // TODO Auto-generated catch block e.printStackTrace(); 
//				}
//
//				catch (IOException e) { // TODO Auto-generated catch block
//					e.printStackTrace();  
//				}
//			}
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

		if (hr != null) {
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
		
		for(CoolSPicture cs : sp.getData()){
			for(SPicture s : cs.getPhotos())
			{
				try {
					s.ownerName = URLDecoder.decode(s.ownerName, "EUC-KR");
					s.placeName = URLDecoder.decode(s.placeName, "EUC-KR");
					Log.e("coolownerName", "ownerna=" + s.ownerName);
					Log.e("coolplaceName", "placeName=" + s.placeName);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return sp;
	}

	public CCoolSPicture GetMorePlacePic(String location, int index) {
		String REGETPIC = "/?location=" + location + "&index=" + index;
		String sendregeturl = String.format("%s%s%s", URL_SERVER,
				URL_COOL_REGET, REGETPIC);

		HttpGet request = new HttpGet(sendregeturl);
		HttpResponse hr = null;

		HttpTask task = new HttpTask() {
			/*
			 * @Override protected void onPostExecute(HttpResponse result) { try
			 * { onReceiveHotCoolPlace(result); } catch (IllegalStateException
			 * e) { // TODO Auto-generated catch block e.printStackTrace(); }
			 * catch (IOException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); } }
			 */
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

		if (hr != null) {
			onReceiveMoreCoolPlaceTask hTask = new onReceiveMoreCoolPlaceTask() {
			};
			hTask.execute(hr);
			try {
				ssp = hTask.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		for(CoolSPicture cs : ssp.getData()){
			for(SPicture s : cs.getPhotos())
			{
				try {
					s.ownerName = URLDecoder.decode(s.ownerName, "EUC-KR");
					s.placeName = URLDecoder.decode(s.placeName, "EUC-KR");
					Log.e("morecoolownerName", "ownerna=" + s.ownerName);
					Log.e("morecoolplaceName", "placeName=" + s.placeName);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return ssp;

	}

	private abstract class onReceiveHotCoolPlaceTask extends
	AsyncTask<HttpResponse, Void, CCoolSPicture> {

		@Override
		protected CCoolSPicture doInBackground(HttpResponse... params) {

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

	private abstract class onReceiveMoreCoolPlaceTask extends
	AsyncTask<HttpResponse, Void, CCoolSPicture> {

		@Override
		protected CCoolSPicture doInBackground(HttpResponse... params) {

			try {
				ssp = onReceiveMoreCoolPlace(params[0]);
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

			return ssp;
		}

	}

	public CCoolSPicture onReceiveHotCoolPlace(HttpResponse result)
			throws JsonSyntaxException, JsonIOException, IllegalStateException,
			IOException, JSONException {
		System.err.println(result.getStatusLine());

		if (result.getStatusLine().getStatusCode() == 204) {
			Log.e("err cool check", "" + result.getStatusLine().getStatusCode());
			CCoolSPicture ccs = null;

			return ccs;
		} else {
			System.err.println(result.getEntity().getContentType());

			CCoolSPicture ccs = gson.fromJson(new InputStreamReader(result
					.getEntity().getContent()), CCoolSPicture.class);

//			Log.e("ccs size", "===" + ccs.data.size());
//			Log.e("first check", ccs.toString());
//			Log.e("second check", ccs.data.toString());
			return ccs;
		}

	}

	public CCoolSPicture onReceiveMoreCoolPlace(HttpResponse result)
			throws JsonSyntaxException, JsonIOException, IllegalStateException,
			IOException, JSONException {
		System.err.println(result.getStatusLine());

		if (result.getStatusLine().getStatusCode() == 204) {
			Log.e("err cool check", "" + result.getStatusLine().getStatusCode());
			SPicture tsp = new SPicture();
			tsp.isPublic = 204;
			CoolSPicture tcs = new CoolSPicture(tsp);
			CCoolSPicture tccs = new CCoolSPicture(tcs);
			
			return tccs;
		} else {
			System.err.println(result.getEntity().getContentType());
			Log.e("mccs start", "================");

			// CoolSPicture ccs = gson.fromJson(new
			// InputStreamReader(result.getEntity().getContent()),
			// CoolSPicture.class);
			CCoolSPicture ccs = gson.fromJson(new InputStreamReader(result
					.getEntity().getContent()), CCoolSPicture.class);
			Log.e("mccs check", "check check check");

			try {
				Log.e("mccs check", ccs.toString());
				Log.e("mccs check", ccs.data.toString());
			} catch (NullPointerException e) {
				Log.e("null", "null!!!!!!!!!");
			}

			return ccs;

		}
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
