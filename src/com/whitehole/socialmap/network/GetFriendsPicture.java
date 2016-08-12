package com.whitehole.socialmap.network;

import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.facebook.Session;

public class GetFriendsPicture {
	// url to make request	
		private static final String URL_PREFIX_FACEBOOK= "https://graph.facebook.com/";
		private static final String URL_PREFIX_PHOTOS = "?fields=photos.limit(0).type(uploaded).fields(id,link,picture,place,source,created_time,from,name)&access_token=";
		private static final String URL_PREFIX_THUMBNAIL = "?fields=picture";
		
		String getthumUrl = null;		// thumb 정보 가져오는 graph url	
		String getphotourl = null;		// photo 정보 가져오는 graph url
		
		// 실행함수
	    public ArrayList<SPicture> getPictures(String id) throws JSONException, InterruptedException, ExecutionException, IOException{
	        Session session = Session.getActiveSession();  
	    	
	        if (session != null && session.isOpened()) {
	        	getphotourl = URL_PREFIX_FACEBOOK + id + URL_PREFIX_PHOTOS+session.getAccessToken();
	        }
	        
	        // 사진 받아오기
	        GetJsonAsyncTask getJsonAsyncTask = new GetJsonAsyncTask();
	        getJsonAsyncTask.execute(getphotourl);
	        StringWriter json_data = getJsonAsyncTask.get();
	        
	        ArrayList<SPicture> fr_pic = new ArrayList<SPicture>();
	        fr_pic = getPicJson(json_data);
	        
	        if(fr_pic.size() > 0)
	        {    
	        	// thumbnail 받아오기
	        	//getthumUrl = URL_PREFIX_FACEBOOK+ fr_pic.get(0).ownerID + URL_PREFIX_THUMBNAIL;
		        getthumUrl = URL_PREFIX_FACEBOOK+ id + URL_PREFIX_THUMBNAIL;
	        	GetJsonAsyncTask getThumJson = new GetJsonAsyncTask();
	        	getThumJson.execute(getthumUrl);
	        	StringWriter thum_data = getThumJson.get();        	
	        	fr_pic.get(0).ownerThumURL = getThumbnail(thum_data);
	        	
		        for(int i=1; i<fr_pic.size(); i++){
		        	fr_pic.get(i).ownerThumURL = fr_pic.get(0).ownerThumURL;
		        }
	        }
	        
	        return fr_pic;
	    }

		// 실행함수
	    public ArrayList<SPicture> getPictures(ArrayList<FriendsList> fr_id) throws JSONException, InterruptedException, ExecutionException, IOException{
	        Session session = Session.getActiveSession();  
	    	ArrayList<SPicture> allfr_pic = new ArrayList<SPicture>();
	        
//	        for(int i=0; i<fr_id.size(); i++){
//		        if (session != null && session.isOpened()) {
//		        	getphotourl = URL_PREFIX_FACEBOOK + fr_id.get(i).id + URL_PREFIX_PHOTOS+session.getAccessToken();
//		        }
//		        
//		        // 사진 받아오기
//		        GetJsonAsyncTask getJsonAsyncTask = new GetJsonAsyncTask();
//		        getJsonAsyncTask.execute(getphotourl);
//		        StringWriter json_data = getJsonAsyncTask.get();
//		        
//		        ArrayList<SPicture> fr_pic = new ArrayList<SPicture>();
//		        fr_pic = getPicJson(json_data);
//		        
//		    	
//		        // thumbnail 받아오기
//		        for(int j=0; j<fr_pic.size(); j++){
//		        	if(j>0 && (fr_pic.get(j).ownerID.equals(fr_pic.get(j-1).ownerID))){
//		        		fr_pic.get(j).ownerThumURL = fr_pic.get(j-1).ownerThumURL;
//		        		continue;
//		        	}
//		        	getthumUrl = URL_PREFIX_FACEBOOK+ fr_pic.get(j).ownerID + URL_PREFIX_THUMBNAIL;
//		        	GetJsonAsyncTask getThumJson = new GetJsonAsyncTask();
//		        	getThumJson.execute(getthumUrl);
//		        	StringWriter thum_data = getThumJson.get();
//		        	
//		        	fr_pic.get(j).ownerThumURL = getThumbnail(thum_data);
//		        }
//		        
//		        allfr_pic.addAll(fr_pic);
//	        }
	        
	        for(FriendsList f : fr_id){
	        	if (session != null && session.isOpened()) {
		        	getphotourl = URL_PREFIX_FACEBOOK + f.id + URL_PREFIX_PHOTOS+session.getAccessToken();
		        }
	        	
	        	// 사진 받아오기
		        GetJsonAsyncTask getJsonAsyncTask = new GetJsonAsyncTask();
		        getJsonAsyncTask.execute(getphotourl);
		        StringWriter json_data = getJsonAsyncTask.get();
		        
		        ArrayList<SPicture> fr_pic = new ArrayList<SPicture>();
		        fr_pic = getPicJson(json_data);
		        SPicture preOwner = null;
		        
		        // thumbnail 받아오기
		        for(SPicture ff : fr_pic){
		        	
		        	if(preOwner != null && ff.ownerID.equals(preOwner.ownerID)){
		        		ff.ownerThumURL = preOwner.ownerThumURL;
		        		continue;
		        	}
		        		
		        	
		        	getthumUrl = URL_PREFIX_FACEBOOK+ ff.ownerID + URL_PREFIX_THUMBNAIL;
		        	GetJsonAsyncTask getThumJson = new GetJsonAsyncTask();
		        	getThumJson.execute(getthumUrl);
		        	StringWriter thum_data = getThumJson.get();
		        	
		        	ff.ownerThumURL = getThumbnail(thum_data);
		        	preOwner = ff;
		        }
		        
		        allfr_pic.addAll(fr_pic);
	        }
	        
	        return allfr_pic;
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
		
		// json에서 내사진정보 받아서 SPicture리스트 리턴
	    @SuppressLint("NewApi")
		private ArrayList<SPicture> getPicJson(StringWriter json_data) throws JSONException, InterruptedException, ExecutionException{        
	    	
	    	ArrayList<SPicture> fr = new ArrayList<SPicture>();
			JSONObject Json = new JSONObject(json_data.toString());
			JSONObject photos = Json.getJSONObject("photos");
			JSONArray arr_data = photos.getJSONArray("data");
			String next=null;
			
			do{
				if(next != null){
			        GetJsonAsyncTask getJsonAsyncTask = new GetJsonAsyncTask();
			        getJsonAsyncTask.execute(next);
			        StringWriter next_jsondata = getJsonAsyncTask.get();
			        Json = new JSONObject(next_jsondata.toString());
			        arr_data = Json.getJSONArray("data");		        
				}
				
				for(int i=0; i< arr_data.length(); i++){
			    	SPicture frpic = new SPicture();	
					JSONObject data = arr_data.getJSONObject(i);		// picture 정보
								
					// 위치정보 없을시 continue
					if(data.optJSONObject("place")==null)
						continue;
					
					// 사진 id
					frpic.pictureID = data.getString("id");
											
					// 큰사진 url
					frpic.imageURL = data.getString("source");
			
					// 작은 사진 url
					frpic.smallImageURL = data.getString("picture");
					
					// 위치정보
					JSONObject place = data.getJSONObject("place");
					JSONObject location = place.getJSONObject("location");
					frpic.latitude = location.getDouble("latitude");
					frpic.longitude = location.getDouble("longitude");
			
					frpic.placeName = place.getString("name");
					
					// 댓글 url
					frpic.commentURL = data.getString("link");
					
					
					// 작성된 시간
					frpic.makeTimeS = data.getString("created_time");
					Calendar calendar = DateParser.getCalendar(frpic.makeTimeS);
					//calendar.add(Calendar.HOUR, 9);
					frpic.makeTime = calendar.getTimeInMillis();
					
					
					// 사진과 같이 있는 글
					frpic.record = data.optString("name");					
					
					
					// 사진 소유자 이름
					JSONObject from = data.getJSONObject("from");
					frpic.ownerName = from.getString("name");
					
					// 사진 소유자 id
					frpic.ownerID = from.getString("id");
					
					frpic.isPublic = 2;
	
					fr.add(frpic);	
				
				}
				JSONObject paging;
				
				if(next == null){
					paging = photos.getJSONObject("paging");
					next = paging.optString("next");
				}
				else{
					try{
						paging = Json.getJSONObject("paging");
						next = paging.optString("next");
					}catch(JSONException e){
						next = "";
					}
				}
			}while(next!=null && !next.isEmpty());
			
			return fr;
	    }
	    
	    // json에서 thumbnailurl정보 받아서 string 리턴
		private String getThumbnail(StringWriter thum_data) throws JSONException{
			String profileUrl = new String();
			JSONObject Jsonthum = new JSONObject(thum_data.toString());
			JSONObject picture = Jsonthum.getJSONObject("picture");
			JSONObject datathum = picture.getJSONObject("data");
			profileUrl = datathum.getString("url");
			
			return profileUrl;
		}
}
