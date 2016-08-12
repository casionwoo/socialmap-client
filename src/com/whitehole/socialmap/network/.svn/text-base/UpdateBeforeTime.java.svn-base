// 사용법
// UpdateBeforeTime 변수 생성
// 변수.ModifyBeforeTime(내사진리스트) 을 하면
// CheckPublic 클래스 리스트로 리턴된다
package com.whitehole.socialmap.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.*;
import android.os.Bundle;
import android.util.Log;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;

public class UpdateBeforeTime extends Activity {
	//RequestAsyncTask getrequest;
	
	// 수정된것 4 5 6 바꾸고 모든 리스트 리턴
	// 수정: 수정된 것만 리턴
	public ArrayList<CheckPublic> ModifyBeforeTime(ArrayList<CheckPublic> me) throws JSONException, IOException{

		String singlequery = getQuery(me); 
				
		String qry = GetFqlJson(singlequery);
		// Json 파서		
		JSONObject json = new JSONObject(qry);
		JSONArray arr_public = json.getJSONArray("data");
		
		ArrayList<CheckPublic> meAfter = new ArrayList<CheckPublic>();
		
		
		
		// me와 arr_public의 마지막을 맞춰주기 위하여
		int check=0;
		
		// public 검사
//		for(int i=0; i<me.size(); i++){
//			JSONObject data = arr_public.getJSONObject(i-check);
//			
//			// 삭제됨
//			if(!me.get(i).pictureID.equals(data.getString("id"))){
//				me.get(i).isPublic = 4;
//				check++;
//			}			
//			// public -> private
//			else if(me.get(i).isPublic == 1){
//				if(!data.getString("value").equals("EVERYONE"))
//					me.get(i).isPublic = 5;
//			}			
//			// private -> public
//			else if(me.get(i).isPublic == 0){	
//				if(data.getString("value").equals("EVERYONE"))
//					me.get(i).isPublic = 6;
//			}
//			
//		}
		
		// public 검사
		int i=0;
		for(CheckPublic c : me){
			if(i-check < arr_public.length()){
				JSONObject data = arr_public.getJSONObject(i-check);
			
			
				// 삭제됨
				if(!c.pictureID.equals(data.getString("id"))){
					c.isPublic = 4;
					check++;
					Log.w("UpdateBefore", "Deleted!!!");
				}			
				// public -> private
				else if(c.isPublic == 1 && !data.getString("value").equals("EVERYONE"))
					c.isPublic = 5;
				// private -> public
				else if(c.isPublic == 0 && data.getString("value").equals("EVERYONE"))
					c.isPublic = 6;
				else{
					i++;
					continue;
				}
			}else{
				c.isPublic = 4;
				//Log.w("UpdateBefore", "Deleted!!!");
			}
			
			i++;
			meAfter.add(c);
		}
		
		//return me;
		return meAfter;
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
//		runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				getrequest = Request.executeBatchAsync(request);
//			}
//		});
//		
//		while(getrequest==null){
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
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

	private String getQuery(ArrayList<CheckPublic> me) {
		// TODO Auto-generated method stub
//		String q = "SELECT id, value FROM privacy WHERE id=";
//
//		for(int i=0; i<me.size(); i++){
//			if(i > 0)
//				q += " OR id=";
//			q += me.get(i).pictureID;
//		}

		String q = "";
		
		for(CheckPublic s : me){
			
			if(q.length() == 0)
				q += ("SELECT id, value FROM privacy WHERE id=" + s.pictureID);
			else
				q += " OR id=" + s.pictureID;
 		}
		
		return q;
	}

	// fql에서 받은 string 불필요한 부분 잘라내기
	private String fqlParse(String fql) {
		// TODO Auto-generated method stub
		String[] str = fql.split("state=");
		String[] json = str[1].split("\\}, error:");
		
		return json[0];
	}
	
}
