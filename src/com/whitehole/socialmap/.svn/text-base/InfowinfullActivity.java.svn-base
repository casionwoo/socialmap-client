package com.whitehole.socialmap;

import com.novoda.imageloader.core.*;
import com.novoda.imageloader.core.cache.*;
import com.novoda.imageloader.core.loader.*;
import com.novoda.imageloader.core.model.*;
import com.whitehole.socialmap.R;
import com.whitehole.socialmap.etc.ApplicationClass;

import android.net.*;
import android.os.Bundle;
import android.app.Activity;
import android.app.Application;
import android.content.*;
import android.graphics.*;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.*;

public class InfowinfullActivity extends Activity {

	static ImageManager imageManager;
	Loader imageLoader;
	ImageTagFactory imageTagFactory;
	ApplicationClass applicationClass;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_infowinfull);
		
		applicationClass = (ApplicationClass)getApplicationContext();
		
		LoaderSettings settings = new LoaderSettings.SettingsBuilder().withCacheManager(new LruBitmapCache(this))
	      	      .withDisconnectOnEveryCall(true).build(this);

  	    imageManager = new ImageManager(this, settings);
  	    imageLoader = imageManager.getLoader();
  	    
  	    imageTagFactory = new ImageTagFactory(this, R.drawable.noimage);
  	    imageTagFactory.setErrorImageId(R.drawable.noimage);
		
		Intent intent = getIntent();
		
		String path = intent.getStringExtra("path");
		final String commentURL = intent.getStringExtra("comment");
		final String from = intent.getStringExtra("from");
		final String what = intent.getStringExtra("what");
		final String picId = intent.getStringExtra("picId");
		//BitmapFactory.Options opt = new BitmapFactory.Options();
		
		//opt.inSampleSize = 4;
		
		
		
		//Bitmap bmp = BitmapFactory.decodeFile(path);

		ImageView iview = (ImageView) findViewById(R.id.infofull);
		ImageView tview = (ImageView) findViewById(R.id.owner_thum);
		
		
		
		//iview.setImageBitmap(bmp);
		
		ImageTag tag = imageTagFactory.build(path);
		iview.setTag(tag);
        //이미지 로더에 로드
        imageLoader.load(iview);
		
        ImageTag tag2 = imageTagFactory.build(intent.getStringExtra("ownerThum"));
        tview.setTag(tag2);
        //이미지 로더에 로드
        imageLoader.load(tview);
        
        
//		Display currentDisplay = getWindowManager().getDefaultDisplay();
//		int dw = currentDisplay.getWidth();
//		int dh = currentDisplay.getHeight();
//		// 이미지가 아니라 이미지의 치수를 로드한다.
//		BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
//		bmpFactoryOptions.inJustDecodeBounds = true;
//		Bitmap bmp = BitmapFactory.decodeFile(path, bmpFactoryOptions);
//		int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) dh);
//		int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) dw);
//	
//		// 두 비율 다 1보다 크면 이미지의 가로, 세로 중 한쪽이 화면보다 크다.
//		if (heightRatio > 1 && widthRatio > 1) {
//			if (heightRatio > widthRatio) {
//				// 높이 비율이 더 커서 그에 따라 맞춘다.
//				bmpFactoryOptions.inSampleSize = heightRatio;
//			}
//			else
//			{
//				// 너비 비율이 더 커서 그에 따라 맞춘다.
//				bmpFactoryOptions.inSampleSize = widthRatio;
//			}
//		}
//		// 실제로 디코딩한다.
//		bmpFactoryOptions.inJustDecodeBounds = false;
//		bmp = BitmapFactory.decodeFile(path, bmpFactoryOptions);
		// 이미지를 표시한다.
//		iview.setImageBitmap(bmp);
		
		Button favorite = (Button) findViewById(R.id.favorite);
		Button reply = (Button) findViewById(R.id.reply);
		
		//라벨 보기가 아닌 경우
		if(!what.equals("4") && applicationClass.modPictureGrid == false){
		
			favorite.setText(getResources().getString(R.string.favorites));
			
			favorite.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {

					if(from.equals("ListView")){
						applicationClass.modPictureGrid = true;
					}
					
					applicationClass.modifyPicture = true;
					
					//전체 보기 사진인 경우
					if(applicationClass.tmpSPicture != null){
						applicationClass.alluserfavList.add(applicationClass.tmpSPicture);
						
						applicationClass.tmpSPicture = null;
					}
					//전체 보기 사진이 아닌 경우
					else{
						applicationClass.addPicIds.add(picId);
						
						//TODO: db에 반영하기?
					}
					
					Toast.makeText(InfowinfullActivity.this, R.string.labeling_completed,
			                Toast.LENGTH_SHORT).show();
				}
			});
		}
		//라벨 보기인 경우 담기 버튼을 숨김
		else{
			favorite.setVisibility(View.INVISIBLE);
		}
		
		
		reply.setText(getResources().getString(R.string.comment));
		reply.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				//		안드로이드 웹 페이지 호출
				Uri uri = Uri.parse(commentURL);
				Intent it = new Intent(Intent.ACTION_VIEW,uri); startActivity(it);
			}
		});
		
		TextView record = (TextView) findViewById(R.id.record);
		record.setText(intent.getStringExtra("record"));
		
		TextView owner_name = (TextView) findViewById(R.id.owner_name);
		owner_name.setText(intent.getStringExtra("ownerName"));
	}

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		applicationClass.tmpSPicture = null;
	}
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.activity_infowinfull, menu);
//		return true;
//	}

}
