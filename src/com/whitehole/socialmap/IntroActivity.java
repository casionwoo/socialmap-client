package com.whitehole.socialmap;


import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import org.json.*;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.whitehole.socialmap.R;
import com.whitehole.socialmap.database.*;
import com.whitehole.socialmap.etc.*;
import com.whitehole.socialmap.login.facebook.FacebookLoginActivity;
import com.whitehole.socialmap.login.google.HttpClientService;
import com.whitehole.socialmap.login.google.LauncherActivity;
import com.whitehole.socialmap.network.*;
import com.whitehole.socialmap.network.server.*;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.provider.MediaStore.Images.*;
import android.util.*;
import android.widget.*;


public class IntroActivity extends Activity {
	private UiLifecycleHelper uiHelper;
	
	MyDBHelper mHelper = new MyDBHelper(this);
	ArrayList<SPicture> sp;
	GetInfo info = new GetInfo();
	Session session;
	ConnectivityManager mgr;
	ApplicationClass applicationClass;
	SharedPreferences pref;
	int mSelected = 0;
	
	//구글 로그인
	public int LOGIN_CODE = 911;
	Account account;
	Button btnLogin;
	Account[] accounts = null;
    public static String serverURL =
            "http://casionwoo.appspot.com/";
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        //onSessionStateChange(session, state, exception);
	    }
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		sp = new ArrayList<SPicture>();
		mgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
	    session = Session.getActiveSession();
	    
	    pref = getSharedPreferences("Pref", 0);
	    applicationClass = (ApplicationClass)getApplicationContext();
	    
	    // 토스트대신 팝업 창으로 변경
//		Toast.makeText(IntroActivity.this, R.string.intro_notification,
//                Toast.LENGTH_LONG).show();
	    
	    //앱 아이디 가져오기
	    if(session != null && session.isOpened())
	    	applicationClass.appID = session.getApplicationId();
		
		ThreadExcutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					 
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				loadData();
			}
			
		});
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);
		
		
		 //원하는 폰트로 지정
//	    TextView txv = (TextView) findViewById(R.id.intro_text);
//	    Typeface face = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ENBERNF.TTF");
//	    txv.setTypeface(face);
	    //txv.setTextAppearance(getApplicationContext(),);
	    
	    TextView txv = (TextView) findViewById(R.id.intro_text_com);
	    Typeface face = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Roboto-Light.ttf");
	    txv.setTypeface(face);
	}
	
	@Override
	public void onResume() {
	    super.onResume();

	    uiHelper.onResume();
	    
	}
	
	private void loadData() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(!isFinishing()) {
					//Session session = Session.getActiveSession();
					
					// Test
//					SharedPreferences.Editor edit2 = pref.edit();
//					
//					edit2.putString("myGoogleID", "empty");
//					
//					edit2.putBoolean("NextInterlock", false);
//					
//					edit2.commit();
					
					// 세션이 오픈되었고, 인터넷에 연결되어 있다면
					if(session != null && session.isOpened() && mgr.getActiveNetworkInfo() != null){
						//Log.w("network", "connect");
						UpdateAfterTime uat = new UpdateAfterTime();
						long updatedTime = pref.getLong("facebookLastUpdate", 0);
						
						// If the session is open, make an API call to get user data
				        // and define a new callback to handle the response
				        Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
				            @Override
				            public void onCompleted(GraphUser user, Response response) {
				                // If the response is successful
				                if (session == Session.getActiveSession()) {
				                    if (user != null) {
				                    	applicationClass.userID = user.getId();//user id
//				                        profileName = user.getName();//user's profile name
//				                        userNameView.setText(user.getName());
//				                    	Log.w("userId", applicationClass.userID);
				                    }   
				                }   
				            } 
				        }); 
				        Request.executeBatchAsync(request);
						
						
						
						//TODO: 최후 업뎃시간기준으로 그 이후에 페북에 업로드된 사진들을 가져와서 저장
						if(updatedTime != 0){
							ArrayList<SPicture> spPub = new ArrayList<SPicture>();
							
							try {
								sp = uat.AddAfterTime(updatedTime);
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
								if(s.isPublic == 1)
									spPub.add(s);
							}
							
							if(spPub != null && spPub.size() > 0){
								PostDeleteSever pds = new PostDeleteSever();
								
								//TODO: 쓰레드 사용하여 우리 서버로 보냄
								try {
									pds.PostPhotos(spPub, 1);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								//정보를 초기화하기 위하여 기존 DB 데이터 날려버리고 새로 받기
								//MyDBHelper.deleteDataOnDB(mHelper);
							}
							
							//사용자의 최후 업데이트 이후 페이스북에 추가된 새로운 사진을 DB에 추가
							if(sp != null && sp.size() > 0)
								MyDBHelper.pushDataToDB(mHelper, sp);
							
							//TODO: 최후 업뎃 시간 업데이트
							SharedPreferences.Editor edit = pref.edit();
								
							edit.putLong("facebookLastUpdate", System.currentTimeMillis());
							
							edit.commit();
						}

						
						// 날린 시각을 프레퍼런스에 기록하여 날린 시각으로 부터 1시간이 경과했다면
						// 친구의 데이터를 DB에서 날려버리는 코드
						// 만약 프레퍼런스에 현재시간이 기록되어있지 않다면
						if(pref.getLong("friendsDeletedTime", 0) == 0){
							//현재 시간을 기록
							SharedPreferences.Editor edit2 = pref.edit();
							
							edit2.putLong("friendsDeletedTime", System.currentTimeMillis());
							
							edit2.commit();
						}
						else if(System.currentTimeMillis() - pref.getLong("friendsDeletedTime", 0) > 3600000){
							MyDBHelper.deleteFriendsDataOnDB(mHelper, null);
						}
						
						
				    	applicationClass.session = session;
						//startActivity(new Intent(getApplicationContext(), LauncherActivity.class));
				    	googleLuancher();
					}
					//프레퍼런스에 다음연동 기록 되어있다면 그냥 Main 으로 넘김
//					else if(pref.getBoolean("NextInterlock", false)){
//						startActivity(new Intent(getApplicationContext(), MainActivity.class));
//					}
//					else{
//						MyDBHelper.deleteDataOnDB(mHelper);
//						startActivity(new Intent(getApplicationContext(), FacebookLoginActivity.class));
//					}
					else{
						if(mgr.getActiveNetworkInfo() == null)
							Toast.makeText(IntroActivity.this, R.string.network_connect,
					                Toast.LENGTH_SHORT).show();
						
				    	applicationClass.session = null;
						
						MyDBHelper.deleteDataOnDB(mHelper);
						//startActivity(new Intent(getApplicationContext(), LauncherActivity.class));
						googleLuancher();
					}
				}
				else
					finish();
			}
		});
	}
	
	public void googleLuancher(){
		boolean okSign = false;
		AccountManager accountManager = AccountManager.get(this);
		accounts = accountManager.getAccountsByType("com.google");
		
		Log.w("Tets",pref.getString("myGoogleID", "empty"));
		
		//프레퍼런스에 기록되어 있지 않다면
		if(pref.getString("myGoogleID", "empty").equals("empty")){
			
			selectAccount();
		}else{
			for(Account a: accounts){
				// 프레퍼런스에 기록되어있고, 해당 구글 계정이 연동되어 있다면
				if(a.name.equals(pref.getString("myGoogleID", "null"))){
					this.gotAccount(a);
					okSign = true;
					break;
				}
			}
			
			//만일 프레퍼런스에 구글계정이 등록되어 있지만 사용자가 시스템에서 로그아웃 한 경우
			if(okSign == false){
				selectAccount();
			}
			else
				this.login(true);
		}
		//Log.w("google account", accounts[0].name+", "+accounts[0].type);
	}
	
	private void selectAccount(){
		
		//계정이 여러개라면 사용자로부터 계정정보 선택 받기
		if(accounts.length > 1){
			int idx=0;
			String[] accountName = new String[accounts.length];
			
			
			for(Account a: accounts){
				accountName[idx++] = a.name;
			}
			
			
			new AlertDialog.Builder(this)
			.setTitle(getResources().getString(R.string.select_google_account))
			.setIcon(R.drawable.socialmapappicon)
			.setSingleChoiceItems(accountName, 0, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					mSelected = which;
				}
				
			})
			.setPositiveButton("확인", new DialogInterface.OnClickListener() {		
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Log.w("google account", accounts[mSelected].name);
					
					SharedPreferences.Editor edit = pref.edit();
					
					edit.putString("myGoogleID", accounts[mSelected].name);
					
					edit.commit();
					
					gotAccount(accounts[mSelected]);
					
					login(true);
				}
			}).setNegativeButton("취소", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			}).show();
		}
		else{
			SharedPreferences.Editor edit = pref.edit();
			
			edit.putString("myGoogleID", accounts[0].name);
			
			edit.commit();
			
			gotAccount(accounts[0]);
			
			this.login(true);
		}
	}
	
	private enum AuthenticationResult {
		SUCCESS, FAILED, EXPIRED;
	}
	
	private AuthenticationResult authentication(AccountManager accountManager,
			Bundle bundle, boolean isFirstTry) {
		String token = null;
		String accountType = null;
		try {
			token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
			Log.d("token", token);
			accountType = bundle.getString(AccountManager.KEY_ACCOUNT_TYPE);
			// Now you can use the Tasks API...
			HttpClientService.get().loginAppEngine(token);
			LauncherActivity.setUserID(IntroActivity.this,
					IntroActivity.this.account.name);
			Log.i("Login", "SUCCESS");
			return AuthenticationResult.SUCCESS;
		} catch (SecurityException e) {
			if (isFirstTry) {
				Log.i("Login", "EXPIRED");
				accountManager.invalidateAuthToken(accountType, token);
				return AuthenticationResult.EXPIRED;
			}
		} catch (Exception e) {
			Log.e("Login", "error", e);
			
		}
		return AuthenticationResult.FAILED;
	}

	private class AuthenticationTask extends
			AsyncTask<Bundle, Object, AuthenticationResult> implements
			AccountManagerCallback<Bundle> {
		private Intent after = null;
		private boolean isFirstTry = false;

		public AuthenticationTask(Class<? extends Activity> successActivity,
				boolean isFirstTry) {
			
			
			after = new Intent(IntroActivity.this, successActivity);
//			after.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			this.isFirstTry = isFirstTry;
		}

		@Override
		protected AuthenticationResult doInBackground(Bundle... bundles) {
			AccountManager accountManager = AccountManager
					.get(IntroActivity.this);
			return authentication(accountManager, bundles[0], isFirstTry);
		}

		@Override
		protected void onPostExecute(AuthenticationResult result) {
			switch (result) {
			case EXPIRED:
				login(false);
				break;
			case SUCCESS:
				if(!isFinishing()) {
					IntroActivity.this.startActivityForResult(after,
							Messages.ACT_END);
				}
				finish();
				break;
			case FAILED:
				// ���������� ������ ȭ��.
				//TODO 인터넷 연결 하라는 알림
				applicationClass.session = null;
				startActivity(new Intent(getApplicationContext(), MainActivity.class));
				finish();
			}
		}

		@Override
		public void run(AccountManagerFuture<Bundle> future) {
			try {
				execute(future.getResult());
			} catch (OperationCanceledException e) {
				Log.e("Login", "canceled", e);
			} catch (AuthenticatorException e) {
				Log.e("Login", "auth", e);
			} catch (IOException e) {
				Log.e("Login", "I/O(network)", e);
			}
		}
	}

	public void login(final boolean first) {
		
		Log.i("LoginActivity", "login starting....");
		
		AccountManager accountManager = AccountManager.get(this);
		AuthenticationTask callbackTask;
		SharedPreferences pref = getSharedPreferences("Pref", 0);
		
		if((applicationClass.session != null && applicationClass.session.isOpened())
				|| pref.getBoolean("NextInterlock", false)){
			callbackTask = new AuthenticationTask(
					MainActivity.class, first);
		}
		else
			callbackTask = new AuthenticationTask(FacebookLoginActivity.class, first);
		
		
		
		
		accountManager.getAuthToken(this.account, "ah", null, this,
				callbackTask, null);
	}

	protected void gotAccount(final Account account) {
		// TODO ���̵��ʵ忡 �̸��� �ּ�
		this.account = account;
	}
//	this.login(true);

	/**
	 * ?��? ?�이????��.
	 *
	 * @param context
	 * @param value
	 */
	public static void setUserID(final Activity context,
	        final String value) {
	    SharedPreferences pref = context.getSharedPreferences("userID", 0);
	    SharedPreferences.Editor prefEditor = pref.edit();
	    prefEditor.putString("userID", value);
	    prefEditor.commit();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	    
	    switch (requestCode) {
		case Messages.ACT_END:
			this.finish();
			break;
		}
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	    //mHelper.close();
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
}
