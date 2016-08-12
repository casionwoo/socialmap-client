// 사용방법
// UpdatedAfterTime 변수 만든후
// 변수.AddAfterTime(최근 저장된 makeTime)를 실행하면
// ArrayList<SPicture>가 리턴, 추가 사진 없을시엔 null 리턴
package com.whitehole.socialmap.network;

import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;

public class UpdateAfterTime extends Activity{
	// url to make request	
	private static final String URL_PREFIX_PHOTOS= "https://graph.facebook.com/me?fields=photos.limit(0)" +
			".type(uploaded).fields(id,link,picture,place,source,created_time,from,name)&access_token=";
	private static final String URL_PREFIX_THUMBNAIL = "https://graph.facebook.com/";
	
	String getthumUrl = null;		// thumb 정보 가져오는 graph url	
	String getphotourl = null;		// photo 정보 가져오는 graph url
	//RequestAsyncTask getrequest;

	
	// 실행함수 리턴값 : 모든사진  매개변수 : public사진만
    public ArrayList<SPicture> AddAfterTime(long ctime) throws JSONException, InterruptedException, ExecutionException, IOException{
    	String created_time = ChangeDate(ctime);
    	
        Session session = Session.getActiveSession();

        if (session != null && session.isOpened()) {
        	getphotourl = URL_PREFIX_PHOTOS+session.getAccessToken();
        }
        
        // 내 사진 받아오기
        GetJsonAsyncTask getJsonAsyncTask = new GetJsonAsyncTask();
        getJsonAsyncTask.execute(getphotourl);
        StringWriter json_data = getJsonAsyncTask.get();
        ArrayList<SPicture> my_pic = new ArrayList<SPicture>();
    	my_pic = getMyPicJson(json_data, created_time);
    	
    	if(my_pic.size()>0){
	    	// thumbnail 받아오기
	    	getthumUrl = URL_PREFIX_THUMBNAIL+ my_pic.get(0).ownerID + "?fields=picture";
	    	GetJsonAsyncTask getThumJson = new GetJsonAsyncTask();
	    	getThumJson.execute(getthumUrl);
	    	StringWriter thum_data = getThumJson.get();
	    	my_pic.get(0).ownerThumURL = getMyThumbnail(thum_data);
	    	
//	        for(int i=1; i<my_pic.size(); i++){        	
//	        	my_pic.get(i).ownerThumURL = my_pic.get(0).ownerThumURL;
//	        	Log.e("thumb check", my_pic.get(i).ownerThumURL);
//	        }
	        
	        for(SPicture s: my_pic){
	        	s.ownerThumURL = my_pic.get(0).ownerThumURL;
	        }
	        getIsPublic(my_pic);
    	}
        
		return my_pic;
    }

    // long타입을 calendar로 바꾸기
    private String ChangeDate(long ctime) {
		// TODO Auto-generated method stub
	    Calendar cl = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
	    cl.clear();
	    cl.setTimeInMillis(ctime);
	    String ct = cl.get(GregorianCalendar.YEAR) + "-";
	    
	    // 달
	    if(cl.get(GregorianCalendar.MONTH)<10)
	    	ct += "0" + (cl.get(GregorianCalendar.MONTH)+1) + "-";
	    else
	    	ct += (cl.get(GregorianCalendar.MONTH)+1) + "-";
	    
	    // 일
	    if(cl.get(GregorianCalendar.DAY_OF_MONTH)<10)
	    	ct += "0" + cl.get(GregorianCalendar.DAY_OF_MONTH) + "T";
	    else
	    	ct += cl.get(GregorianCalendar.DAY_OF_MONTH) + "T";
	    
	    //시
	    if(cl.get(GregorianCalendar.HOUR_OF_DAY)<10)
	    	ct += "0" + cl.get(GregorianCalendar.HOUR_OF_DAY) + ":";
	    else
	    	ct += cl.get(GregorianCalendar.HOUR_OF_DAY) + ":";
	    
	    //분
	    if(cl.get(GregorianCalendar.MINUTE)<10)
	    	ct += "0" + cl.get(GregorianCalendar.MINUTE) + ":";
	    else
	    	ct += cl.get(GregorianCalendar.MINUTE) + ":";

	    //초
	    if(cl.get(GregorianCalendar.MINUTE)<10)
	    	ct += "0" + cl.get(GregorianCalendar.SECOND) + "+0000";
	    else
	    	ct += cl.get(GregorianCalendar.SECOND) + "+0000";
	    

    	Log.e("check create", "" + ct);
    	
		return ct;
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
	private ArrayList<SPicture> getMyPicJson(StringWriter json_data, String created_time) throws JSONException, InterruptedException, ExecutionException{        
    	
    	ArrayList<SPicture> me = new ArrayList<SPicture>();
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
			Log.e("arr_data", arr_data.toString());
			
			for(int i=0; i< arr_data.length(); i++){
		    	SPicture mypic = new SPicture();	
				JSONObject data = arr_data.getJSONObject(i);		// picture 정보
				
				
				// 위치정보 없을시 continue
				if(data.optJSONObject("place")==null)
					continue;
				
				// json이 최신사진부터 오기때문에 매개변수로 넘어온 created_time보다 이전이면 곧바로 종료
				if(created_time.compareTo(data.getString("created_time")) > 0){
					Log.e("compared", "jsontime: " + data.getString("created_time") + "\n get time at App: " + created_time);
					break;
				}
				Log.e("compared2", created_time + "\n " + data.getString("created_time"));				
				
				// 사진 id
				mypic.pictureID = data.getString("id");
				Log.e("pID", mypic.pictureID);
										
				// 큰사진 url
				mypic.imageURL = data.getString("source");
		
				// 작은 사진 url
				mypic.smallImageURL = data.getString("picture");
				
				// 위치정보
				JSONObject place = data.getJSONObject("place");
				JSONObject location = place.getJSONObject("location");
				mypic.latitude = location.getDouble("latitude");
				mypic.longitude = location.getDouble("longitude");
		
				mypic.placeName = place.getString("name");
				
				// 댓글 url
				mypic.commentURL = data.getString("link");
				
				
//				// 작성된 시간
//				String cTime = data.getString("created_time");
//				Calendar calendar = DateParser.getCalendar(cTime);
//				long secondtime = calendar.getTimeInMillis();
//				Date cDate = new Date(secondtime);
//				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.KOREA);
//				mypic.makeTimeS = formatter.format(cDate);
//				Calendar koreacl = DateParser.getCalendar(mypic.makeTimeS);
//				mypic.makeTime = koreacl.getTimeInMillis(); 
				
				// 작성된 시간
				mypic.makeTimeS = data.getString("created_time");
				Calendar calendar = DateParser.getCalendar(mypic.makeTimeS);
				mypic.makeTime = calendar.getTimeInMillis();
	
				
				// 사진과 같이 있는 글
				mypic.record = data.optString("name");
							
				// 사진 소유자 이름
				JSONObject from = data.getJSONObject("from");
				mypic.ownerName = from.getString("name");
				
				// 사진 소유자 id
				mypic.ownerID = from.getString("id");
	
				me.add(mypic);	
			
			}
			JSONObject paging;
			
			if(next == null)
				paging = photos.getJSONObject("paging");
			else
				paging = Json.getJSONObject("paging");			
			next = paging.optString("next");
			
		}while(!next.isEmpty());
		
		
		// 올린 사진이 없을때
//		if(me.size() == 0)
//			return null;
		
		return me;
    }
    
    // json에서 thumbnailurl정보 받아서 string 리턴
	private String getMyThumbnail(StringWriter thum_data) throws JSONException{
		String profileUrl = new String();
		JSONObject Jsonthum = new JSONObject(thum_data.toString());
		JSONObject picture = Jsonthum.getJSONObject("picture");
		JSONObject datathum = picture.getJSONObject("data");
		profileUrl = datathum.getString("url");
		
		return profileUrl;
	}
	

	// public 조사
	private ArrayList<SPicture> getIsPublic(ArrayList<SPicture> me) throws JSONException, IOException{
		String singlequery = getQuery(me);
		ArrayList<SPicture> pub = new ArrayList<SPicture>();
		
		String qry = GetFqlJson(singlequery);
		Log.e("qry", qry);
		
		// Json 파서		
		JSONObject json = new JSONObject(qry);
		JSONArray arr_public = json.getJSONArray("data");
		
		// public 검사
		for(int i=0; i<me.size(); i++){
			JSONObject data = arr_public.getJSONObject(i);
			if(data.getString("value").equals("EVERYONE")){
				me.get(i).isPublic = 1;
				pub.add(me.get(i));
			}
		}
		
		return pub;
	}

	// 쿼리 만들기
	private String getQuery(ArrayList<SPicture> me) {
		// TODO Auto-generated method stub
		String q = "SELECT value FROM privacy WHERE id=";

		for(int i=0; i<me.size(); i++){
			if(i > 0)
				q += " OR id=";
			q += me.get(i).pictureID;
		}
		
		return q;
	}

	private String GetFqlJson(String singlequery) {
		// TODO Auto-generated method stub
				Bundle params = new Bundle();
				params.putString("q", singlequery);
				Session session = Session.getActiveSession();
				
				
				// fql 결과 json 받아오기
				final Request request = new Request(session, "/fql", params, HttpMethod.GET
						, new Request.Callback() {
					
					@Override
					public void onCompleted(Response response) {
						// TODO Auto-generated method stub
					}
				});
				
				//BatchAsync를 UI 쓰레드에서 실행 하도록 변경
//				runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//							getrequest = Request.executeBatchAsync(request);
//					}
//				});
//				
//				while(getrequest==null){
//					try {
//						Thread.sleep(100);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
				RequestAsyncTask getrequest = Request.executeBatchAsync(request);
				
				List<Response> getquery = new ArrayList<Response>();
				try {
					getquery = getrequest.get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// response를 String으로 받아서 Json형식에 맞게 바꿔서 받아옴
				String fql = fqlParse(getquery.get(0).toString());
		return fql;
	}

	// fql에서 받은 string 불필요한 부분 잘라내기
	private String fqlParse(String fql) {
		// TODO Auto-generated method stub
		String[] str = fql.split("state=");
		String[] json = str[1].split("\\}, error:");
		
		return json[0];
	}
}
