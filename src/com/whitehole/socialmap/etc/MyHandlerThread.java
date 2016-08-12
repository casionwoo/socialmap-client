package com.whitehole.socialmap.etc;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.HandlerThread;

public class MyHandlerThread extends HandlerThread{

	public Context mContext;

	public String mTitle;

	public String mMsg;

	public ProgressDialog mProgress;

	
	public MyHandlerThread(Context context, String title, String msg) {
		super("myHandlerThread");
		// TODO Auto-generated constructor stub
		
		mContext = context;

        mTitle = title;

        mMsg = msg;
        
        setDaemon(true);
	}

	@Override
	protected void onLooperPrepared() {
		// TODO Auto-generated method stub
		super.onLooperPrepared();
		
		mProgress = new ProgressDialog(mContext);

        mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        mProgress.setTitle(mTitle);

        mProgress.setMessage(mMsg);

        mProgress.setCancelable(false);
        
        mProgress.show();
	}
	
	public static void stop(MyHandlerThread m){
		if(m != null && m.mProgress != null){
		
			m.mProgress.dismiss();
		
			// 대화상자가 사라지기 전까지 대기해 줘야 함
	        try { Thread.sleep(100); } catch (InterruptedException e) {;}
	        
	        if(m.getLooper()!=null)
	        	m.getLooper().quit();
		}
	}
}