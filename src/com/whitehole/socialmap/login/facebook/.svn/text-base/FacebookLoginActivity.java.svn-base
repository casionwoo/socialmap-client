package com.whitehole.socialmap.login.facebook;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import org.json.*;

import com.whitehole.socialmap.MainActivity;
import com.whitehole.socialmap.MapTestActivity;
import com.whitehole.socialmap.R;
import com.whitehole.socialmap.database.*;
import com.whitehole.socialmap.etc.*;
import com.whitehole.socialmap.network.*;
import com.whitehole.socialmap.network.server.*;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

import android.os.*;
import android.app.*;
import android.content.*;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class FacebookLoginActivity extends Activity {
	private UiLifecycleHelper uiHelper;
	LoginButton btn;
	ProgressDialog dialog;
	MyDBHelper mHelper = new MyDBHelper(this);
	ArrayList<SPicture> sp;
	GetInfo info = new GetInfo();
	SharedPreferences pref;
	ArrayList<SPicture> fsp;
	ApplicationClass applicationClass;
	
	Handler mhandler;
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        //onSessionStateChange(session, state, exception);

		    //showDialog(0);
		    
		    //TODO: 네트워크 처리
		    if (session != null && session.isOpened()) {
		    	mhandler = new Handler();
		    	
		    	applicationClass.session = session;

		    	// 사진 받아오기 함수
		    	ThreadExcutor.execute(new Runnable() {
		    		@Override
					public void run() {
		    			
		    			mhandler.post(new Runnable() {
							
							@Override
							public void run() {
		    			
				    			MyHandlerThread ht = new MyHandlerThread(FacebookLoginActivity.this, "wait", "Please wait while loading...");
						    	ht.start();
				    			
				    			//TODO: public 까지 같이 로딩
				    			loadData();
								
				    			if(fsp!=null && fsp.size() > 0){
				    				PostDeleteSever pds = new PostDeleteSever();
				    				
				    			//TODO: 최초 우리 서버로 모든 public 데이터 전송
				    			//sp를 보내면 됨
				    				try {
										pds.PostPhotos(fsp, 0);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
				    			}
				    			
				    			handler.sendEmptyMessage(0);  // 작업이 끝나면 이 핸들러를 호출
				    			MyHandlerThread.stop(ht);
							}
		    			});
		    		}
		    	});
		    }
	    	
	    }
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_facebook_login);
		
		applicationClass = (ApplicationClass)getApplicationContext();
		
		pref = getSharedPreferences("Pref", 0);
		
		Button next = (Button) findViewById(R.id.nextButton);
		uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
	    btn = (LoginButton) findViewById(R.id.facebooklogin);
	    btn.setReadPermissions(Arrays.asList("user_photos", "user_status", "friends_photos", "read_stream", "read_friendlists"));
	    
	    
	    //다음에 연동 버튼 클릭시 MainActivity로 연결
	    next.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 프레퍼런스에 다음 연동 기록함
				SharedPreferences pref = getSharedPreferences("Pref", 0);
				
				SharedPreferences.Editor edit = pref.edit();
				
				edit.putBoolean("NextInterlock", true);
				
				edit.commit();
				
				startActivity(new Intent(getApplicationContext(), MainActivity.class));
				finish();
			}
		});
		
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.activity_facebook_login, menu);
//		return true;
//	}

	@Override
	public void onResume() {
	    super.onResume();
	    
	    // For scenarios where the main activity is launched and user
	    // session is not null, the session state change notification
	    // may not be triggered. Trigger it if it's open/closed.
//	    Session session = Session.getActiveSession();
//
//	    if (session != null && session.isOpened()) {
//	    	ApplicationClass applicationClass = (ApplicationClass)getApplicationContext();
//	    	applicationClass.session = session;
//	    	
//	    	// 개선 필요!!!
//	    	showDialog(0);
//	    	
//	    	// 사진 받아오기 함수
//	    	ThreadExcutor.execute(new Runnable() {
//	    		@Override
//				public void run() {
//	    			
//	    			loadData();
//					
//	    			handler.sendEmptyMessage(0);  // 작업이 끝나면 이 핸들러를 호출
//	    		}
//	    	});
//	    }
	    uiHelper.onResume();
	}
	
	private Handler handler = new Handler() {
		  public void handleMessage(Message msg) {
			  
			  //TODO 최후 업뎃시간 기록
			  SharedPreferences.Editor edit = pref.edit();
				
			  edit.putLong("facebookLastUpdate", System.currentTimeMillis());
			
			  edit.commit();
			  
			  
			  //dialog.dismiss(); // 이게 프로그래스 바를 없애는 부분이다
			  startActivity(new Intent(getApplicationContext(), MainActivity.class));
			  finish();
				
		  }
	};
	
	private void loadData() {
//		runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
		if(!isFinishing()) {
				//정보를 초기화하기 위하여 기존 DB 데이터 날려버리고 새로 받기
				MyDBHelper.deleteDataOnDB(mHelper);
				
				fsp = new ArrayList<SPicture>();
				
				// 네트워크 함수 사용하여 내 페북 정보 가져와서 담기

				try {
					sp = info.getmyPictures();
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				for(SPicture s : sp){
					if(s.isPublic == 1){
						fsp.add(s);
					}
				}
				
				
				if(sp != null && sp.size() > 0)
					MyDBHelper.pushDataToDB(mHelper, sp);
		}
//			}
//		});
	}
	
	@Override
    protected Dialog onCreateDialog(int id) {
		
		dialog = new ProgressDialog(this);
		dialog.setMessage("Please wait while loading...");
		dialog.setIndeterminate(true);
		//dialog.setCancelable(true);
		
		return dialog;
	}
    
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    mHelper.close();
	    uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
}


