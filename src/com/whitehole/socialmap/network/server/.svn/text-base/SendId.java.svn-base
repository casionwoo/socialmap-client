package com.whitehole.socialmap.network.server;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONException;

import com.whitehole.socialmap.login.google.HttpClientService;

import android.os.AsyncTask;
import android.util.Log;


public class SendId {

	// url to Server
	private final String URL_SERVER = "http://casionwoo.appspot.com/";
	private final String URL_ID_THUMB= "rest/thumbnail/update";

	 // Sending photo to Server
	
	public void SendIdAndThumb(String ownerID, String ownerThumURL) throws JSONException, IOException{
		
		String IDANDTHUMB= "/?ownerID="+ownerID+"&ownerThumURL="+ownerThumURL;
		String sendidthumburl = String.format("%s%s%s", URL_SERVER,
				URL_ID_THUMB, IDANDTHUMB);
		
		HttpGet httpget = new HttpGet(sendidthumburl);
		
		
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
