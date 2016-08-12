package com.whitehole.socialmap.more;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import org.json.*;

import com.whitehole.socialmap.R;
import com.whitehole.socialmap.database.*;
import com.whitehole.socialmap.etc.*;
import com.whitehole.socialmap.login.facebook.*;
import com.whitehole.socialmap.network.*;
import com.whitehole.socialmap.network.server.*;
import com.facebook.*;

import android.*;
import android.os.*;
import android.app.*;
import android.util.Log;
import android.view.View;
import android.content.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;


public class MoreMainActivity extends Activity implements OnItemClickListener {
	//페이스북 로그인/로그아웃 버튼을 위한 변수 선언
	private UiLifecycleHelper uiHelper;
	ProgressDialog dialog;
	MyDBHelper mHelper = new MyDBHelper(this);
	ArrayList<SPicture> sp;
	GetInfo info = new GetInfo();
	SharedPreferences pref;
	SharedPreferences.Editor edit;
	ArrayList<SPicture> fsp;
	
	Handler mhandler;
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        //onSessionStateChange(session, state, exception);
	    	session = Session.getActiveSession();
	    	pref = getSharedPreferences("Pref", 0);
	    	edit = pref.edit();
	    	ApplicationClass applicationClass = (ApplicationClass)getApplicationContext();
		    
	    	if (session != null && session.isOpened()) {
		    	applicationClass.session = session;
		    	
		    	mhandler = new Handler();
	
		    	//showDialog(0);
  
		    	// 사진 받아오기 함수
		    	ThreadExcutor.execute(new Runnable() {
		    		@Override
					public void run() {
		    			
		    			mhandler.post(new Runnable() {
							
							@Override
							public void run() {
		    			
				    			MyHandlerThread ht = new MyHandlerThread(MoreMainActivity.this, "wait", "Please wait while loading...");
						    	ht.start();
				    			
				    			//현재 시간을 기록						
								edit.putLong("friendsDeletedTime", System.currentTimeMillis());
								
								edit.commit();
				    			
				    			loadData();
				    					
				    			
				    			//TODO: 우리서버로 보내기
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
				    			
				    			//TODO: 최후 업뎃 시간 업데이트		
								edit.putLong("facebookLastUpdate", System.currentTimeMillis());
								
								edit.commit();
								
								//dialog.dismiss();
								
								
								
				    			//handler.sendEmptyMessage(0);  // 작업이 끝나면 이 핸들러를 호출
								
								MyHandlerThread.stop(ht);
							}
		    			});
		    		}
		    	});
			    	
		    }
		    else{
		    	applicationClass.session = null;
		    	//MyDBHelper.deleteDataOnDB(mHelper);
		    	
		    	//TODO: 최후 업뎃 시간 초기화	
				edit.putLong("facebookLastUpdate", 0);
				edit.putBoolean("NextInterlock", false);
				
				edit.commit();
				
				applicationClass.userID = "";
				applicationClass.appID = "";
		    }
	    	 
	    }
	};
	
	@Override
    protected Dialog onCreateDialog(int id) {
		
		dialog = new ProgressDialog(MoreMainActivity.this);
		dialog.setMessage("Please wait while loading...");
		dialog.setIndeterminate(true);
		//dialog.setCancelable(true);
		
		return dialog;
	}
	
//	private Handler handler = new Handler() {
//		  public void handleMessage(Message msg) {
//		  
//			  dialog.dismiss(); // 이게 프로그래스 바를 없애는 부분이다
//		  }
//		 
//		};

	private void loadData() {
//		runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
			if(!isFinishing()) {
					//정보를 초기화하기 위하여 기존 DB 데이터 날려버리고 새로 받기
					MyDBHelper.deleteDataOnDB(mHelper);
					
					// 네트워크 함수 사용하여 내 페북 정보 가져와서 담기
					fsp = new ArrayList<SPicture>();
					
					try {
						sp = info.getmyPictures();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
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
//			
//		});
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_activity_main);
        		
        uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
	    LoginButton btn = (LoginButton) findViewById(R.id.facebooklogin);
	    btn.setReadPermissions(Arrays.asList("user_photos", "friends_photos", "read_stream", "read_friendlists"));
       
        // ��� ����͸� �����.
        ArrayAdapter<CharSequence> Adapter = ArrayAdapter.createFromResource(this, R.array.morearrays, android.R.layout.simple_list_item_1);
        
        // ���� �ƿ��� ����Ʈ �並 �����Ѵ�.
        ListView list = (ListView)findViewById(R.id.morelist);

        // ����͸� ����Ʈ�信 �����Ѵ�
        list.setAdapter(Adapter);
        
		// ������ Ŭ�� �̺�Ʈ ó���� ���� �����ʸ� ����Ѵ�.
		// �� Ŭ����(MainActivity)���� OnItemClickListener�� �����Ͽ����Ƿ�, this(�� Ŭ����)�� �Ķ���ͷ� ����Ѵ�.
        list.setOnItemClickListener(this);

        // listview ���� �� �Ӽ�
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setDivider(new ColorDrawable(Color.BLACK));
        list.setDividerHeight(2);
    }
    
    @Override
	public void onResume() {
	    super.onResume();
	    
	    uiHelper.onResume();
//	    ApplicationClass applicationClass = (ApplicationClass)getApplicationWindowToken();
//    	applicationClass.session = null;
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
	    uiHelper.onDestroy();
	    mHelper.close();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		// TODO Auto-generated method stub
		
		Intent intent;
		
		// Ŭ���� ��ġ�� ��� �Ѿ�� ������ ����
		switch(position)
		{
		case 0:
			intent = new Intent(this, sub_notice_activity.class);
			startActivity(intent);
			break;
		case 1:
			intent = new Intent(this, sub_help_activity.class);
			startActivity(intent);
			break;
		case 2:
			intent = new Intent(this, Sub_Option_Activity.class);
			startActivity(intent);
			break;
		case 3:
			intent = new Intent(this, DataDeleteActivity.class);
			startActivity(intent);
			break;
		}
		
	}
    
       
}
