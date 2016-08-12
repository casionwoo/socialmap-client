package com.whitehole.socialmap.login.google;

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.whitehole.socialmap.MainActivity;
import com.whitehole.socialmap.etc.ApplicationClass;
import com.whitehole.socialmap.login.facebook.FacebookLoginActivity;
import com.whitehole.socialmap.network.Messages;

public class LauncherActivity extends Activity {
	public int LOGIN_CODE = 911;
	Account account;
	Button btnLogin;
	Account[] accounts = null;
    public static String serverURL =
            "http://casionwoo.appspot.com/";
    ApplicationClass applicationClass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
// Splash Loading..
		super.onCreate(savedInstanceState);
		//this.setContentView(R.layout.login);
		applicationClass = (ApplicationClass)getApplicationContext();
		AccountManager accountManager = AccountManager.get(this);
		this.accounts = accountManager.getAccountsByType("com.google");
		this.gotAccount(accounts[0]);
		Log.d("account", account.toString());
		
		this.login(true);
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
			LauncherActivity.setUserID(LauncherActivity.this,
					LauncherActivity.this.account.name);
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
			
			
			after = new Intent(LauncherActivity.this, successActivity);
//			after.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			this.isFirstTry = isFirstTry;
		}

		@Override
		protected AuthenticationResult doInBackground(Bundle... bundles) {
			AccountManager accountManager = AccountManager
					.get(LauncherActivity.this);
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
					LauncherActivity.this.startActivityForResult(after,
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

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		switch (requestCode) {
		case Messages.ACT_END:
			this.finish();
			break;
		}
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

}
