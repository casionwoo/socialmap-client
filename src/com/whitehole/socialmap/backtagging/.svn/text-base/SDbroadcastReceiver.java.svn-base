package com.whitehole.socialmap.backtagging;

import java.io.*;

import android.app.*;
import android.content.*;
import android.database.*;
import android.location.*;
import android.media.*;
import android.net.*;
import android.provider.MediaStore.*;
import android.provider.MediaStore.Images.*;
import android.sax.*;
import android.util.*;
import android.widget.*;

public class SDbroadcastReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context, Tag_Service.class);
		
		
//		String action = intent.getAction();
//		if(action.equals(Intent.ACTION_CAMERA_BUTTON)){
//			Log.d("BR", "SD카드에 사진이 추가되었음!");
//			Toast.makeText(context, "카메라 버튼!", Toast.LENGTH_SHORT).show();
//			
//			
//		}
		//Log.d("Test", "START OF NewPhotoReceiver");
		  
		//Uri uri = intent.getData();

		//Toast.makeText(context, "Photo taken - " + uri, Toast.LENGTH_SHORT).show();

		//Log.d("Test", "[onReceive] URI - " + uri);
		
		//서비스 실행
		context.startService(i);
	}

}