package com.whitehole.socialmap.hotcool;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.*;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.novoda.imageloader.core.ImageManager;
import com.novoda.imageloader.core.LoaderSettings;
import com.novoda.imageloader.core.cache.LruBitmapCache;
import com.novoda.imageloader.core.loader.Loader;
import com.novoda.imageloader.core.model.ImageTag;
import com.novoda.imageloader.core.model.ImageTagFactory;
import com.whitehole.socialmap.*;
import com.whitehole.socialmap.etc.*;
import com.whitehole.socialmap.hotcool.CoolClickActivity.CoolImageAdapter;

public class SelectedHotImg extends Activity {

	//이미지로더 변수
	CoolImageAdapter cAdapter;
	static ImageManager imageManager;
	Loader imageLoader;
	ImageTagFactory imageTagFactory;
	ApplicationClass applicationClass;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		applicationClass = (ApplicationClass)getApplicationContext();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(0));

		//이미지 로더 셋팅
		LoaderSettings settings = new LoaderSettings.SettingsBuilder().withCacheManager(new LruBitmapCache(this))
				.withDisconnectOnEveryCall(true).build(this);

		imageManager = new ImageManager(this, settings);
		imageLoader = imageManager.getLoader();

		imageTagFactory = new ImageTagFactory(this, R.drawable.ic_launcher);
		imageTagFactory.setErrorImageId(R.drawable.ic_launcher);	
		
		setContentView(R.layout.sub_hotcool_selectedhotimg);
//		Intent intent = getIntent();
//		String imgurl = intent.getStringExtra("imgurl");
//		String placename = intent.getStringExtra("placename");
//		String ownername = intent.getStringExtra("ownername");
//		String ownerthumurl = intent.getStringExtra("ownerthumurl");
//		String record = intent.getStringExtra("record");
		
		TextView splacename=(TextView)findViewById(R.id.shotplacename);
		try {
			splacename.setText(URLDecoder.decode(applicationClass.tmpHotSPicture.placeName, "EUC-KR"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TextView sownername = (TextView)findViewById(R.id.shotownername);
		try {
			sownername.setText(URLDecoder.decode(applicationClass.tmpHotSPicture.ownerName, "EUC-KR"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TextView srecord = (TextView)findViewById(R.id.srecord);
		try {
			srecord.setText(URLDecoder.decode(applicationClass.tmpHotSPicture.record, "EUC-KR"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// HOT Img 로드
		ImageView shotimg = (ImageView) findViewById(R.id.selectedhotimage);
		ImageTag tag = imageTagFactory.build(applicationClass.tmpHotSPicture.imageURL);
		shotimg.setTag(tag);
		//이미지 로더에 로드
		imageLoader.load(shotimg);
		
		// 소유자 Img 로드
		ImageView sownerimg = (ImageView) findViewById(R.id.selectedownerimg);
		tag = imageTagFactory.build(applicationClass.tmpHotSPicture.smallImageURL);
		sownerimg.setTag(tag);
		//이미지 로더에 로드
		imageLoader.load(sownerimg);
		
		final String url = applicationClass.tmpHotSPicture.commentURL;
		
		Button link = (Button) findViewById(R.id.hot_comment);
		
		link.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				//	안드로이드 웹 페이지 호출
				Uri uri = Uri.parse(url);
				Intent it = new Intent(Intent.ACTION_VIEW,uri); startActivity(it);
			}
		});
		
		Button favorite = (Button) findViewById(R.id.hot_label);
		
		favorite.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				applicationClass.modifyPicture = true;
				
				applicationClass.alluserfavList.add(applicationClass.tmpHotSPicture);

				applicationClass.tmpHotSPicture = null;
				
				Toast.makeText(SelectedHotImg.this, R.string.labeling_completed,
		                Toast.LENGTH_SHORT).show();
			}
		});
		
		
		
	}
}