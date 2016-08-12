package com.whitehole.socialmap;

import java.io.IOException;

import com.novoda.imageloader.core.ImageManager;
import com.novoda.imageloader.core.LoaderSettings;
import com.novoda.imageloader.core.cache.LruBitmapCache;
import com.novoda.imageloader.core.loader.Loader;
import com.novoda.imageloader.core.model.ImageTag;
import com.novoda.imageloader.core.model.ImageTagFactory;
import com.whitehole.socialmap.R;
import com.whitehole.socialmap.etc.*;
import com.whitehole.socialmap.network.SPicture;

import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import android.app.Activity;
import android.content.*;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.PorterDuff.Mode;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.widget.*;

public class ListViewPhotosActivity extends Activity {

	ApplicationClass applicationClass;
	static final int LOCAL=0, FACEBOOK=1, LABEL=4, ALLVIEW=3;
	//이미지 로더 변수
    static ImageManager imageManager;
	Loader imageLoader;
	ImageTagFactory imageTagFactory;
	ContentResolver cr;
	Cursor iCursor;
	int posNum;					//그리드뷰의 항목 개수
	
	ImageAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_view_photos);
		
		applicationClass = (ApplicationClass)getApplicationContext();
		
		//이미지 로더 셋팅
        LoaderSettings settings = new LoaderSettings.SettingsBuilder().withCacheManager(new LruBitmapCache(this))
      	      .withDisconnectOnEveryCall(true).build(this);

  	    imageManager = new ImageManager(this, settings);
  	    imageLoader = imageManager.getLoader();
  	    
  	    imageTagFactory = new ImageTagFactory(this, R.drawable.noimage);
  	    imageTagFactory.setErrorImageId(R.drawable.noimage);
  	    
  	    cr = getContentResolver();
  	    
  	    
  	    
  	    Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.gridfadein);
  	    
  	    GridLayoutAnimationController c = new GridLayoutAnimationController(anim);
  	    c.setDirection(GridLayoutAnimationController.DIRECTION_TOP_TO_BOTTOM|GridLayoutAnimationController.DIRECTION_LEFT_TO_RIGHT);
  	    c.setDirectionPriority(GridLayoutAnimationController.PRIORITY_ROW);
  	    c.setColumnDelay(0.5f);
  	    
  	    // TODO: 라벨별 보기 개수 추가!!!
  	  	GridView gridView = (GridView) findViewById(R.id.photos_grid);
  	  	adapter = new ImageAdapter(this, LOCAL);
  	  	
  	  	 	  	
  	  	posNum = applicationClass.localClu.clusters.size() + 
  	  		applicationClass.facebookClu.clusters.size() +
  	  		applicationClass.favoriteClu.clusters.size();
  	  	
  	  	if(applicationClass.allviewPhotos != null)
  	  		posNum += applicationClass.allviewPhotos.size();
  	    
  	  	gridView.setAdapter(adapter);

  	  	gridView.setLayoutAnimation(c);
  	  	
  	  	gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				String data = "";
				
				if(arg2 < applicationClass.localClu.clusters.size()){
					data = LOCAL + ":" + arg2;
				}
				else if(applicationClass.facebookClu.clusters.size() > 0 &&
						arg2 < applicationClass.facebookClu.clusters.size() + applicationClass.localClu.clusters.size()){
					
					int modPosition = arg2;
					
					if(applicationClass.localClu.clusters.size() > 0)
						modPosition = arg2 - applicationClass.localClu.clusters.size();
					
					data = FACEBOOK + ":" + modPosition;
				}
				else if(applicationClass.favoriteClu.clusters.size() > 0 &&
						arg2 < applicationClass.facebookClu.clusters.size() + 
						applicationClass.localClu.clusters.size() + 
						applicationClass.favoriteClu.clusters.size()){	
					
					int modPosition = arg2;
					
					if(applicationClass.localClu.clusters.size() > 0)
						modPosition = arg2 - applicationClass.localClu.clusters.size();
					
					if(applicationClass.facebookClu.clusters.size() > 0)
						modPosition = modPosition - applicationClass.facebookClu.clusters.size();
					
					data = LABEL + ":" + modPosition;
				}
				else if(arg2 < posNum){
					
					int modPosition = arg2;
					
					if(applicationClass.localClu.clusters.size() > 0)
						modPosition = arg2 - applicationClass.localClu.clusters.size();
					
					// 라벨링 개수도 빼야함!
					if(applicationClass.favoriteClu.clusters.size() > 0)
						modPosition = modPosition - applicationClass.favoriteClu.clusters.size();
					
					if(applicationClass.facebookClu.clusters.size() > 0)
						modPosition = modPosition - applicationClass.facebookClu.clusters.size();
					
					data = ALLVIEW + ":" + modPosition;
				}
				
				Intent intent = new Intent(ListViewPhotosActivity.this, ListViewPhotosDetailActivity.class);
				
				intent.putExtra("data", data);
				
				startActivity(intent);
			}
		});
  	  	
  	  	
		//내 로컬 사진이 선택되어 있는 경우
//		if(applicationClass.selectedGroup == null || applicationClass.selectedGroup[0] == 1){
//			TextView textView = (TextView) findViewById(R.id.local_photos);
//			textView.setText("내 핸드폰 사진");
//			GridView gridView = (GridView) findViewById(R.id.local_photos_grid);
//			
//			ImageAdapter adapter = new ImageAdapter(this, LOCAL);
//			gridView.setAdapter(adapter);
//			
//			gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//				@Override
//				public void onItemClick(AdapterView<?> arg0, View arg1,
//						int arg2, long arg3) {
//					
//					
//				}
//			});
//		}
//		
//		//나 또는 친구의 페북 사진이 선택되어 있는 경우
//		if(applicationClass.facebookClu != null && applicationClass.selectedGroup != null && (applicationClass.selectedGroup[1] == 1 || applicationClass.selectedGroup[2] == 1)){
//			TextView textView = (TextView) findViewById(R.id.facebook_photos);
//			textView.setText("페이스북 사진");
//			GridView gridView = (GridView) findViewById(R.id.facebook_photos_grid);
//			
//			ImageAdapter adapter = new ImageAdapter(this, FACEBOOK);
//			gridView.setAdapter(adapter);
//			
//			gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//				@Override
//				public void onItemClick(AdapterView<?> arg0, View arg1,
//						int arg2, long arg3) {
//					
//					
//				}
//			});
//		}
//		
//		//라벨 사진이 선택되어 있는 경우
//		if(applicationClass.selectedGroup != null && applicationClass.selectedGroup[3] == 1){
//			TextView textView = (TextView) findViewById(R.id.Label_photos);
//			textView.setText("라벨 사진");
//			GridView gridView = (GridView) findViewById(R.id.Label_photos_grid);
//			
//			ImageAdapter adapter = new ImageAdapter(this, LABEL);
//			gridView.setAdapter(adapter);
//			
//			gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//				@Override
//				public void onItemClick(AdapterView<?> arg0, View arg1,
//						int arg2, long arg3) {
//					
//					
//				}
//			});
//		}
//		
//		//전체 사용자 보기 사진이 선택되어 있는 경우
//		if(applicationClass.selectedGroup != null && applicationClass.selectedGroup[4] == 1){
//			TextView textView = (TextView) findViewById(R.id.all_view_photos);
//			textView.setText("전체 앱 사용자 사진");
//			GridView gridView = (GridView) findViewById(R.id.all_view_grid);
//			
//			ImageAdapter adapter = new ImageAdapter(this, ALLVIEW);
//			gridView.setAdapter(adapter);
//			
//			gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//				@Override
//				public void onItemClick(AdapterView<?> arg0, View arg1,
//						int arg2, long arg3) {
//					
//					
//				}
//			});
//		}
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.list_view_photos, menu);
//		return true;
//	}
	
	public static Bitmap getFramedBitmap(Bitmap bitmap, final int frameColor) {
        Bitmap output = null;

        if(bitmap != null){
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
   
            Canvas canvas = new Canvas(output);
         
            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);
            final float roundPx = 10; // round range : pixel
            
            //paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            
            //액자
            paint.setStrokeWidth(9);
            paint.setColor(frameColor);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            
        }
        return output;
    }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if(applicationClass.modPictureGrid){
			posNum = applicationClass.localClu.clusters.size() + 
		  	  		applicationClass.facebookClu.clusters.size() +
		  	  		applicationClass.favoriteClu.clusters.size();
		  	  	
	  	  	if(applicationClass.allviewPhotos != null)
	  	  		posNum += applicationClass.allviewPhotos.size();
	  	  	
			applicationClass.modPictureGrid = false;		
			
//			iCursor = applicationClass.locCur;
			if(posNum > 0)
				adapter.notifyDataSetChanged();
			else
				finish();
		}
		
	}

	public class ImageAdapter extends BaseAdapter {

		//int from;
		
		public ImageAdapter(Context mContext, int from) {
			super();
			this.mContext = mContext;
			//this.from = from;
		}

		@Override
		public int getCount() {

//			if(from == LOCAL)
//				return applicationClass.localClu.clusters.size();
//			else if(from == FACEBOOK)
//				return applicationClass.facebookClu.clusters.size();
//			else if(from == LABEL)
//				return 0;
//			else
//				return applicationClass.allviewPhotos.size();
			
			return posNum;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			ImageView iv = new ImageView(mContext);
			
			iv.setAdjustViewBounds(true);
			//iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
			iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
			
			iv.setLayoutParams(new GridView.LayoutParams(applicationClass.thumWidth * 4/3, applicationClass.thumWidth * 4/3));
			
			//if(from == LOCAL){
			if(arg0 < applicationClass.localClu.clusters.size()){
				//int idx = applicationClass.localClu.clusters.get(arg0).cluCenterIdx;
				//applicationClass.localClu.clusters.get(arg0).points.get(idx);
				
				//0번째가 대표사진일 것이므로...
				int pos = applicationClass.localClu.clusters.get(arg0).points.get(0).cursorPosition;
				
				iCursor = applicationClass.locCur;
				
				iCursor.moveToPosition(pos);
				
				Bitmap bit = Images.Thumbnails.getThumbnail(cr, iCursor.getInt(iCursor.getColumnIndex(BaseColumns._ID)), Images.Thumbnails.MICRO_KIND, null);
				
				int degree = getExifOrientation(iCursor.getString(iCursor.getColumnIndex(ImageColumns.DATA)));
				bit = getRotatedBitmap(bit, degree);
				
				bit = getFramedBitmap(bit, Color.WHITE);
				//Uri uri = Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, iCursor.getString(iCursor.getColumnIndex(MediaStore.Images.Thumbnails._ID)));
				iv.setImageBitmap(bit);
				
//				iv.setImageURI(uri);
			}
			//else if(from == FACEBOOK){
			else if(applicationClass.facebookClu.clusters.size() > 0 &&
					arg0 < applicationClass.facebookClu.clusters.size() + applicationClass.localClu.clusters.size()){
				
//				int idx = applicationClass.facebookClu.clusters.get(arg0).cluCenterIdx;
//				applicationClass.facebookClu.clusters.get(arg0).points.get(idx);
				int modPosition = arg0;
				if(applicationClass.localClu.clusters.size() > 0)
					modPosition = arg0 - applicationClass.localClu.clusters.size();
				
				//0번째가 대표사진일 것이므로...
				int pos = applicationClass.facebookClu.clusters.get(modPosition).points.get(0).cursorPosition;
				
				iCursor = applicationClass.FacCur;
				
				iCursor.moveToPosition(pos);
				
				String url = iCursor.getString(iCursor.getColumnIndex(SPicture.SMALLIMAGEURL));
				
				ImageTag tag = imageTagFactory.build(url);
            	iv.setTag(tag);
            	
            	//이미지 로더에 로드
                imageLoader.load(iv);
			}
			else if(applicationClass.favoriteClu.clusters.size() > 0 &&
					arg0 < applicationClass.facebookClu.clusters.size() + 
					applicationClass.localClu.clusters.size() +
					applicationClass.favoriteClu.clusters.size()){
				
				int modPosition = arg0;
				
				if(applicationClass.localClu.clusters.size() > 0)
					modPosition = arg0 - applicationClass.localClu.clusters.size();
				
				if(applicationClass.facebookClu.clusters.size() > 0)
					modPosition = modPosition - applicationClass.facebookClu.clusters.size();
				
				//0번째가 대표사진일 것이므로...
				int pos = applicationClass.favoriteClu.clusters.get(modPosition).points.get(0).cursorPosition;
				
				iCursor = applicationClass.favCur;
				
				iCursor.moveToPosition(pos);
				
				String url = iCursor.getString(iCursor.getColumnIndex(SPicture.SMALLIMAGEURL));
				
				ImageTag tag = imageTagFactory.build(url);
            	iv.setTag(tag);
            	
            	//이미지 로더에 로드
                imageLoader.load(iv);
			}
			else if(arg0 < posNum){
				
				int modPosition = arg0;
				
				if(applicationClass.localClu.clusters.size() > 0)
					modPosition = arg0 - applicationClass.localClu.clusters.size();
				
				//라벨링 개수도 빼야함!
				if(applicationClass.favoriteClu.clusters.size() > 0)
					modPosition = modPosition - applicationClass.favoriteClu.clusters.size();
				
				if(applicationClass.facebookClu.clusters.size() > 0)
					modPosition = modPosition - applicationClass.facebookClu.clusters.size();
				
				SPicture spic = applicationClass.allviewPhotos.get(modPosition);
				
				String url = spic.smallImageURL;
				
				ImageTag tag = imageTagFactory.build(url);
            	iv.setTag(tag);
            	
            	//이미지 로더에 로드
                imageLoader.load(iv);
			}

			return iv;
		}
		
		private Context mContext;
	}
	
	/**
	 * 이미지를 특정각도로 회전하는 함수
	 * @param bitmap
	 * @param degrees
	 * @return
	 */
	public synchronized static Bitmap getRotatedBitmap(Bitmap bitmap,
			int degrees) {

		if (degrees != 0 && bitmap != null)
		{

			Matrix m = new Matrix();

			m.setRotate(degrees, (float) bitmap.getWidth() / 2,
					(float) bitmap.getHeight() / 2);

			try
			{

				Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0,
						bitmap.getWidth(), bitmap.getHeight(), m, true);

				if (bitmap != b2)
				{

					bitmap.recycle();

					bitmap = b2;

				}

			}

			catch (OutOfMemoryError ex)
			{
				// We have no memory to rotate. Return the original bitmap.
			}

		}
		return bitmap;
	}
	
	/**
	 * 이미지의 오리엔테이션 정보를 얻는 함수
	 * @param filepath
	 * @return
	 */
	public synchronized static int getExifOrientation(String filepath) {
//	public synchronized static int GetOrientation(int orientation) {
		int degree = 0;
		ExifInterface exif = null;

		try {
			exif = new ExifInterface(filepath);
		} catch (IOException e) {
			Log.e("GetExifOrientation", "cannot read exif");
			e.printStackTrace();
		}

		if (exif != null) {
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION, -1);

			if (orientation != -1) {
				// We only recognize a subset of orientation tag values.
				switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;

				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;

				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
				}

			}
		}

		return degree;
	}
}
