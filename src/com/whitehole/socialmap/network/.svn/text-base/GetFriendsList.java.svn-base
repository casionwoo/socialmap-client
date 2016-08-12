package com.whitehole.socialmap.network;

import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Session;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;


public class GetFriendsList extends Activity {
	// url to make request	
	private static final String URL_PREFIX_FRIENDSLIST = "https://graph.facebook.com/me?fields=friends&&access_token=";
	
	// 실행함수
    public ArrayList<FriendsList> getmyfriends(){
        Session session = Session.getActiveSession();
        String friendsurl = null;
        
        if (session != null && session.isOpened()) {
        	friendsurl = URL_PREFIX_FRIENDSLIST+session.getAccessToken();
        }
        
        // 내 사진 받아오기
        GetJsonAsyncTask getJsonAsyncTask = new GetJsonAsyncTask();
        getJsonAsyncTask.execute(friendsurl);
        StringWriter json_data = null;
		try {
			json_data = getJsonAsyncTask.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        ArrayList<FriendsList> myfriends = new ArrayList<FriendsList>();
        try {
			myfriends = getMyfriends(json_data);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        //Log.e("friends", "id : " + myfriends.get(0).id + "name : " + myfriends.get(0).name);
        
        return myfriends;
    }
    
	// url정보로 json 가져오기
	private class GetJsonAsyncTask extends AsyncTask<String, Integer, StringWriter>{
    	@Override
    	protected StringWriter doInBackground(String...addr){
    		
			StringWriter json_data = new StringWriter();
	    	try{
	    		URL url = new URL(addr[0]);
	    		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
	    		if(conn != null){
	    			conn.setConnectTimeout(10000);
	    			conn.setUseCaches(false);
	    			if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
	    				IOUtils.copy(conn.getInputStream(), json_data,"UTF-8");
	    			}
	    			conn.disconnect();
	    		}
	    	}catch (Exception ex){;}
	    	
    		return json_data;
    	}
    }

	// friendslist 가져오기
    @SuppressLint("NewApi")
	private ArrayList<FriendsList> getMyfriends(StringWriter json_data) throws JSONException, InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
    	ArrayList<FriendsList> myfriends = new ArrayList<FriendsList>();
		JSONObject Json = new JSONObject(json_data.toString());
		JSONObject friends = Json.getJSONObject("friends");
		JSONArray arr_data = friends.getJSONArray("data");
		String next=null;
		
		do{
			if(next != null){
				GetJsonAsyncTask getJsonAsyncTask = new GetJsonAsyncTask();
				getJsonAsyncTask.execute(next);
				StringWriter next_jsondata = getJsonAsyncTask.get();
				Json = new JSONObject(next_jsondata.toString());
				arr_data = Json.getJSONArray("data");
			}
			for(int i=0; i<arr_data.length(); i++){
				FriendsList fr = new FriendsList();
				JSONObject data = arr_data.getJSONObject(i);
				
				fr.id = data.getString("id");
				fr.name = data.getString("name");
				
				myfriends.add(fr);
			}
			JSONObject paging;
			
			if(next == null)
				paging = friends.getJSONObject("paging");
			else
			{
				Log.e("flistpaging", Json.getString("paging"));
				Log.e("flistpaging", Json.toString());
				paging = Json.getJSONObject("paging");
				
			}
			next = paging.optString("next");
			
		}while(!next.isEmpty());
		
		return myfriends;
	}

}
