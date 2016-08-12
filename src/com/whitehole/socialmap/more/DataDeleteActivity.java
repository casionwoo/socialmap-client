package com.whitehole.socialmap.more;

import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.whitehole.socialmap.R;
import com.whitehole.socialmap.etc.ApplicationClass;
import com.whitehole.socialmap.etc.ThreadExcutor;
import com.whitehole.socialmap.network.server.PostDeleteSever;

public class DataDeleteActivity extends Activity {

	Handler mhandler;
	PostDeleteSever ps;
	ApplicationClass applicationClass;
	ConnectivityManager mgr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data_delete);
		
		mgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		
		applicationClass = (ApplicationClass)getApplicationContext();
		
		mhandler = new Handler();
		
		TextView tv = (TextView) findViewById(R.id.data_delete_text);
		tv.setText(getResources().getString(R.string.data_delete_notice));
		
		Button btn = (Button) findViewById(R.id.data_delete_btn);
		btn.setText(getResources().getString(R.string.delete_data));
		
		ps = new PostDeleteSever();
		
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				new AlertDialog.Builder(DataDeleteActivity.this)
				.setTitle(getResources().getString(R.string.delete_data))
				.setIcon(R.drawable.socialmapappicon)
				.setMessage(getResources().getString(R.string.delete_data_question))
				.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(
							DialogInterface dialog,
							int which) {
						
						if(applicationClass.session != null && applicationClass.session.isOpened()
								 && mgr.getActiveNetworkInfo() != null && !TextUtils.isEmpty(applicationClass.userID)){
							ThreadExcutor.execute(new Runnable() {
					    		@Override
								public void run() {
					    			
					    			mhandler.post(new Runnable() {
										
										@Override
										public void run() {
									    	
											Log.w("DataDeleteActivity", applicationClass.userID);
											
									    	//TODO: 서버로 부터 데이터 삭제 요청
									    	try {
												ps.DeleteAllPhotos(applicationClass.userID);
											} catch (UnsupportedEncodingException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
									    	
										}
					    			});
					    		}
							});
						}
						
						
						Toast.makeText(DataDeleteActivity.this, R.string.deleted_data_completed,
				                Toast.LENGTH_SHORT).show();					
					
					}
					
				})
				.setNegativeButton(getResources().getString(R.string.no), null)
				.show();
				
			}
		});
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.data_delete, menu);
//		return true;
//	}

}
