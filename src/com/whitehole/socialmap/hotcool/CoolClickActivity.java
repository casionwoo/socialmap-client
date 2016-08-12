package com.whitehole.socialmap.hotcool;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.*;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.novoda.imageloader.core.ImageManager;
import com.novoda.imageloader.core.LoaderSettings;
import com.novoda.imageloader.core.cache.LruBitmapCache;
import com.novoda.imageloader.core.loader.Loader;
import com.novoda.imageloader.core.model.ImageTag;
import com.novoda.imageloader.core.model.ImageTagFactory;
import com.whitehole.socialmap.R;
import com.whitehole.socialmap.etc.*;
import com.whitehole.socialmap.network.SPicture;
import com.whitehole.socialmap.network.server.CCoolSPicture;
import com.whitehole.socialmap.network.server.CoolSPicture;
import com.whitehole.socialmap.network.server.GetCool;

public class CoolClickActivity extends Activity{
	Gson gson = new Gson();
	CoolSPicture cs;			// 사진이 저장된 객체 SPicture가 5개 있음
	Gallery gal;
	int index=0;
	Button cbtn;
	Button inbtn;
	Button linkbtn;
	TextView ctxt;
	int current_position;

	GetCool gc = new GetCool();

	//이미지로더 변수
	CoolImageAdapter cAdapter;
	static ImageManager imageManager;
	Loader imageLoader;
	ImageTagFactory imageTagFactory;

	private static int cool_item_w;
	private static int cool_item_h;
	
	ApplicationClass applicationClass;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sub_hotcool_coolgallery);

		applicationClass = (ApplicationClass)getApplicationContext();
		
		cbtn = (Button)findViewById(R.id.coolbutton);
		inbtn = (Button)findViewById(R.id.insertMyFolder);
		linkbtn = (Button)findViewById(R.id.goToLink);
		ctxt = (TextView)findViewById(R.id.coolgallery_tv);


		//이미지 로더 셋팅
		LoaderSettings settings = new LoaderSettings.SettingsBuilder().withCacheManager(new LruBitmapCache(this))
				.withDisconnectOnEveryCall(true).build(this);

		imageManager = new ImageManager(this, settings);
		imageLoader = imageManager.getLoader();

		imageTagFactory = new ImageTagFactory(this, R.drawable.ic_launcher);
		imageTagFactory.setErrorImageId(R.drawable.ic_launcher);

		cool_item_w = getResources().getDimensionPixelSize(R.dimen.cool_gallery_item_w);
		cool_item_h = getResources().getDimensionPixelSize(R.dimen.cool_gallery_item_h);

		GetCS();


		cbtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AddSPicture();
			}
		});
		
		inbtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				applicationClass.modifyPicture = true;
				
				applicationClass.alluserfavList.add(cs.getPhotos().get(current_position));
				
				Toast.makeText(CoolClickActivity.this, R.string.labeling_completed,
		                Toast.LENGTH_SHORT).show();
			}
		});
		
		linkbtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				
//				안드로이드 웹 페이지 호출
				Uri uri = Uri.parse(cs.getPhotos().get(current_position).commentURL);
				Intent it = new Intent(Intent.ACTION_VIEW,uri); startActivity(it);
			}
		});
		

		gal = (Gallery) findViewById(R.id.coolgallery);
		cAdapter = new CoolImageAdapter(this, cs);
		gal.setAdapter(cAdapter);
		gal.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				//        		Toast.makeText(CoolClickActivity.this, position + "번째 그림 선택", Toast.LENGTH_SHORT).show();

				current_position = position;
				ctxt.setText(cs.getPhotos().get(position).placeName);
				Log.e("PlaceName : ", "" + cs.getPhotos().get(position).placeName);
				
				ImageView img = (ImageView) findViewById(R.id.coolselectedimage);
				ImageTag tag = imageTagFactory.build(cs.getPhotos().get(position).imageURL);
				img.setTag(tag);
				//이미지 로더에 로드
				imageLoader.load(img);

				//    	        Log.e("position, size", "po=" + position + "size="+cs.getPhotos().size());
				//    	        if((position+1) == cs.getPhotos().size())
				//    	        	AddSPicture();
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

	}

	protected void AddSPicture() {
		// TODO Auto-generated method stub
		index=cs.getPhotos().size();

//		Log.e("AddSPicture", "Add");
		CCoolSPicture moreccs = gc.GetMorePlacePic(cs.getLocation(), index);
		
//		Log.e("moreccs data size", ""+moreccs.getData().size());
//		Log.e("moreccs data", ""+moreccs.getData().toString());
		if(moreccs.getData().size() != 0){
			CoolSPicture morecs = moreccs.getData().get(0);
			try{
//				Log.e("more che", "out for");
				for(int i=0; i<morecs.photos.size(); i++){
//					Log.e("more che", "in for");
					cs.photos.add(morecs.photos.get(i));
				}
				// 추가 사진을 받은 후 adapter를 갱신
//				Log.e("more che", "complete for");
				cAdapter.notifyDataSetChanged();
			}
			catch(NullPointerException e){
				Toast.makeText(CoolClickActivity.this, "No more pictures", Toast.LENGTH_SHORT).show();
			}
		}
		else{
			Toast.makeText(CoolClickActivity.this, "No more pictures", Toast.LENGTH_SHORT).show();
		}
	}

	// intent로 넘긴 정보를 받아온다
	private void GetCS() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		String coolplace = intent.getStringExtra("coolplace");        
		cs = gson.fromJson(coolplace, CoolSPicture.class);
	}

	class CoolImageAdapter extends BaseAdapter{
		private Context mContext;
		private ArrayList<SPicture> mList;
		private int iBackGround;

		public CoolImageAdapter(Context c, CoolSPicture cs){
			mContext = c;
			mList = cs.photos;
			TypedArray typeArray = obtainStyledAttributes(R.styleable.Gallery1);
			iBackGround = typeArray.getResourceId(R.styleable.Gallery1_android_galleryItemBackground, 0);
			typeArray.recycle();
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ImageView imageView;

			if(convertView == null)
			{
				imageView = new ImageView(mContext);
			}else{
				imageView = (ImageView)convertView;
			}

			ImageTag tag = imageTagFactory.build(mList.get(position).imageURL);
			imageView.setTag(tag);
			//이미지 로더에 로드
			imageLoader.load(imageView);

			imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			imageView.setLayoutParams(new Gallery.LayoutParams(cool_item_w, cool_item_h));
			imageView.setBackgroundResource(iBackGround);

			return imageView;
		}

	}

}


