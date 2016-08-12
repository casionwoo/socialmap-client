package com.whitehole.socialmap.network.server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.whitehole.socialmap.login.google.HttpClientService;
import com.whitehole.socialmap.network.SPicture;


public class GetPhotos {

	// url to Server
	private final String URL_SERVER = "http://casionwoo.appspot.com/";
	private final String URL_GET_TOTAL = "rest/totalphotos/get";
	private Gson gson = new Gson();

	private String getphotourl = String.format("%s%s", URL_SERVER,
			URL_GET_TOTAL);
	
	ArrayList<SPicture> sp;
	SSPicture pic;

	public ArrayList<SPicture> GetTotalPhotos(double latN, double lonE, double latS, double lonW) {
		
		String args = String.format("latN=%s&lonE=%s&latS=%s&lonW=%s", latN, lonE, latS, lonW);
		String gettotalphotos = String.format("%s?%s", getphotourl, args);
		
		Log.w("GetTotalPhotos" ,gettotalphotos);
		
		HttpGet request = new HttpGet(gettotalphotos);
		sp = new ArrayList<SPicture>();
		HttpResponse hr = null;
		HttpTask task = new HttpTask() {
//			@Override
//			protected void onPostExecute(HttpResponse result) {
//				try {
//					sp = onReceiveTotalPhotos(result);
//				} catch (IllegalStateException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
		};
		task.execute(request);
		
		try {
			hr = task.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(hr != null){
			OnReceiveTotalPhotosTask hTask = new OnReceiveTotalPhotosTask(){
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
		
		
		for(SPicture s : sp){
			try {
				s.ownerName = URLDecoder.decode(s.ownerName, "EUC-KR");
				s.record = URLDecoder.decode(s.record, "EUC-KR");
				s.placeName = URLDecoder.decode(s.placeName, "EUC-KR");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return sp;
	}
	
	public ArrayList<SPicture> GetClusteredPhotos(String pictureID, double latN, double lonE, double latS, double lonW) {

		String getclusteredphotos = String.format("%s/?Picture_ID=%s&latN=%s&lonE=%s&latS=%s&lonW=%s", getphotourl, pictureID, latN, lonE, latS, lonW);
		Log.i("getclusteredphotos", getclusteredphotos);
		HttpGet request = new HttpGet(getclusteredphotos);
		HttpResponse hr = null;
		HttpTask task = new HttpTask() {
//			@Override
//			protected void onPostExecute(HttpResponse result) {
//				try {
//					sp = onReceiveTotalPhotos(result);
//				} catch (IllegalStateException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
		};
		task.execute(request);
		try {
			hr = task.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(hr != null){
			OnReceiveTotalPhotosTask hTask = new OnReceiveTotalPhotosTask(){
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
		
		for(SPicture s : sp){
			try {
				s.ownerName = URLDecoder.decode(s.ownerName, "EUC-KR");
				s.record = URLDecoder.decode(s.record, "EUC-KR");
				s.placeName = URLDecoder.decode(s.placeName, "EUC-KR");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return sp;
	}
	
	
	private abstract class OnReceiveTotalPhotosTask extends AsyncTask<HttpResponse, Void, ArrayList<SPicture>>{

		@Override
		protected ArrayList<SPicture> doInBackground(HttpResponse... params) {
			
			try {
				sp = onReceiveTotalPhotos(params[0]);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return sp;
		}
		
	}
	
	public ArrayList<SPicture> onReceiveTotalPhotos(HttpResponse result) throws IllegalStateException, IOException
	{
		System.err.println(result.getStatusLine());
		System.err.println(result.getEntity().getContentType());
				
//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
//		result.getEntity().writeTo(bos);
//		Log.i("Message",new String(bos.toByteArray()));
		
		SSPicture pic = gson.fromJson(new InputStreamReader(result.getEntity().getContent()), SSPicture.class);
		
		Log.i("SSPicture", "#n = " +pic.data.size());

		return pic.data;
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
