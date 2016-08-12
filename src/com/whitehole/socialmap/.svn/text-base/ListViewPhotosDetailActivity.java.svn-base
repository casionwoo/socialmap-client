package com.whitehole.socialmap;

import java.io.*;
import java.util.ArrayList;

import org.w3c.dom.ls.*;

import com.google.android.gms.maps.model.Marker;
import com.novoda.imageloader.core.*;
import com.novoda.imageloader.core.cache.*;
import com.novoda.imageloader.core.loader.*;
import com.novoda.imageloader.core.model.*;
import com.whitehole.socialmap.R;
import com.whitehole.socialmap.ListViewPhotosActivity.*;
import com.whitehole.socialmap.etc.*;
import com.whitehole.socialmap.network.*;
import com.whitehole.socialmap.network.server.GetPhotos;

import android.media.ExifInterface;
import android.net.*;
import android.os.Bundle;
import android.os.Handler;
import android.provider.*;
import android.provider.MediaStore.*;
import android.provider.MediaStore.Images.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.database.*;
import android.graphics.*;
import android.util.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;

public class ListViewPhotosDetailActivity extends Activity {

	ApplicationClass applicationClass;
	static ImageManager imageManager;
	Loader imageLoader;
	ImageTagFactory imageTagFactory;
	
	int from;
	int index;
	
	ContentResolver cr;
	Cursor iCursor;
	
	Handler mhandler;
	MyHandlerThread ht;
	ArrayList<SPicture> allPicClusterList;
	ConnectivityManager mgr;
	
	GridView gridView;
	ImageAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_view_photos_detail);
				
		applicationClass = (ApplicationClass)getApplicationContext();
		
		Intent intent = getIntent();
		
		String data = intent.getStringExtra("data");
		
		from = Integer.valueOf(data.split(":")[0]);
		index = Integer.valueOf(data.split(":")[1]);
		
		//Log.w("data", ""+from+index+applicationClass.localClu.clusters.get(index).points.size());
		
		LoaderSettings settings = new LoaderSettings.SettingsBuilder().withCacheManager(new LruBitmapCache(this))
	      	      .withDisconnectOnEveryCall(true).build(this);

	    imageManager = new ImageManager(this, settings);
	    imageLoader = imageManager.getLoader();
	    
	    imageTagFactory = new ImageTagFactory(this, R.drawable.noimage);
	    imageTagFactory.setErrorImageId(R.drawable.noimage);
	    
	    cr = getContentResolver();
	    mhandler = new Handler();
	    allPicClusterList = new ArrayList<SPicture>();
	    
	    mgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
	    
	    
	    
	    //TODO: 만약 전체 사용자 보기 사진이면 서버로 부터 대표사진 그룹에 포함되는 모든 사진을 받아와야 함 
	    
	    if(from == Integer.valueOf(ListViewPhotosActivity.ALLVIEW)){
	    	if(applicationClass.session != null && applicationClass.session.isOpened()
	    			 && mgr.getActiveNetworkInfo() != null){
		    	ThreadExcutor.execute(new Runnable() {
		    		@Override
					public void run() {
		    		
	//		    			try {
	//							Thread.sleep(1000);
	//							 
	//						} catch (InterruptedException e) {
	//							e.printStackTrace();
	//						}
		    			
		    			//Looper.prepare();
		    			//mLoop = Looper.myLooper();
		    			
				    	mhandler.post(new Runnable() {
							
							@Override
							public void run() {
								GetPhotos getPotos = new GetPhotos();
								
								ht = new MyHandlerThread(ListViewPhotosDetailActivity.this, "wait", "Please wait while loading...");
								ht.start();
								
								allPicClusterList = getPotos.GetClusteredPhotos(applicationClass.allviewPhotos.get(index).pictureID,
										applicationClass.curLoc.northeast.latitude, 
										applicationClass.curLoc.northeast.longitude, 
										applicationClass.curLoc.southwest.latitude, 
										applicationClass.curLoc.southwest.longitude);
								
								gridView.setAdapter(adapter);
								
								MyHandlerThread.stop(ht);
							}
				    	});
		    		}
		    	});
	    	
	    	
//		    	long time = System.currentTimeMillis();
//				
//				while(allPicClusterList != null && allPicClusterList.size() == 0){
//					if(allPicClusterList == null){
//						allPicClusterList = new ArrayList<SPicture>();
//						break;
//					}
//					if(System.currentTimeMillis() - time > 30000){
//						break;
//					}
//				}
				
				if(allPicClusterList == null)
					allPicClusterList = new ArrayList<SPicture>();
	    	}else{
	    		allPicClusterList = new ArrayList<SPicture>();
	    		gridView.setAdapter(adapter);
	    	}
	    	
	    }
	    
	    
	    
	    gridView = (GridView) findViewById(R.id.grid_photos);
	    
	    adapter = new ImageAdapter(this);
	    
	    Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.gridfadein);
  	    
  	    GridLayoutAnimationController c = new GridLayoutAnimationController(anim);
  	    c.setDirection(GridLayoutAnimationController.DIRECTION_TOP_TO_BOTTOM|GridLayoutAnimationController.DIRECTION_LEFT_TO_RIGHT);
  	    c.setDirectionPriority(GridLayoutAnimationController.PRIORITY_ROW);
  	    c.setColumnDelay(0.5f);
  	    
  	    if(from != Integer.valueOf(ListViewPhotosActivity.ALLVIEW))
  	    	gridView.setAdapter(adapter);

	  	gridView.setLayoutAnimation(c);
	  	
	  	gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				Intent intent = new Intent(ListViewPhotosDetailActivity.this, InfowinfullActivity.class);
				
				if(from == Integer.valueOf(ListViewPhotosActivity.LOCAL)){
					
					iCursor = applicationClass.locCur;
					
					iCursor.moveToPosition(applicationClass.localClu.clusters.get(index).points.get(arg2).cursorPosition);
					
					String path = iCursor.getString(iCursor.getColumnIndex(ImageColumns.DATA));
					
					//로컬갤러리의 경우
					//기존의 안드로이드 상의 이미지 뷰어를 통해 보여줌
//					intent = new Intent();
//					intent.setAction(Intent.ACTION_VIEW);
//					Uri uri = Uri.fromFile(new File(path));
//					intent.setDataAndType(uri, "image/*");
					//intent.setData(uri);
					
					intent = new Intent(ListViewPhotosDetailActivity.this, InfoWinfullLocalActivity.class);
					
					intent.putExtra("path", path);
					intent.putExtra("from", "ListView");
					intent.putExtra("CurIdx", arg2);
					intent.putExtra("lat", iCursor.getDouble(iCursor.getColumnIndex(ImageColumns.LATITUDE)));
					intent.putExtra("lng", iCursor.getDouble(iCursor.getColumnIndex(ImageColumns.LONGITUDE)));
					intent.putExtra("what", ListViewPhotosActivity.LOCAL+"");
					
				}else if(from == Integer.valueOf(ListViewPhotosActivity.FACEBOOK)){
					String path;
					
					iCursor = applicationClass.FacCur;
					
					iCursor.moveToPosition(applicationClass.facebookClu.clusters.get(index).points.get(arg2).cursorPosition);
					
					path = iCursor.getString(iCursor.getColumnIndex(SPicture.IMAGEURL));
					
					// 페북 사진의 경우
					// 풀 이미지를 새로운 액티비티로 보여줌
					
					intent.putExtra("path", path);
					intent.putExtra("comment", iCursor.getString(iCursor.getColumnIndex(SPicture.COMMENTURL)));
					intent.putExtra("record", iCursor.getString(iCursor.getColumnIndex(SPicture.RECORD)));
					intent.putExtra("ownerName", iCursor.getString(iCursor.getColumnIndex(SPicture.OWNERNAME)));
					intent.putExtra("ownerThum", iCursor.getString(iCursor.getColumnIndex(SPicture.OWNERTHUMURL)));
					intent.putExtra("picId", iCursor.getString(iCursor.getColumnIndex(SPicture.PICTUREID)));
					intent.putExtra("what", ListViewPhotosActivity.FACEBOOK+"");
					intent.putExtra("from", "ListView");
				}
				//라벨 데이터 적용
				else if(from == Integer.valueOf(ListViewPhotosActivity.LABEL)){
					String path;
					
					iCursor = applicationClass.favCur;
					
					iCursor.moveToPosition(applicationClass.favoriteClu.clusters.get(index).points.get(arg2).cursorPosition);
					
					path = iCursor.getString(iCursor.getColumnIndex(SPicture.IMAGEURL));
					
					// 페북 사진의 경우
					// 풀 이미지를 새로운 액티비티로 보여줌
					
					intent.putExtra("path", path);
					intent.putExtra("comment", iCursor.getString(iCursor.getColumnIndex(SPicture.COMMENTURL)));
					intent.putExtra("record", iCursor.getString(iCursor.getColumnIndex(SPicture.RECORD)));
					intent.putExtra("ownerName", iCursor.getString(iCursor.getColumnIndex(SPicture.OWNERNAME)));
					intent.putExtra("ownerThum", iCursor.getString(iCursor.getColumnIndex(SPicture.OWNERTHUMURL)));
					intent.putExtra("picId", iCursor.getString(iCursor.getColumnIndex(SPicture.PICTUREID)));
					intent.putExtra("what", ListViewPhotosActivity.LABEL+"");
					intent.putExtra("from", "ListView");
				}
				//서버로 부터 사진 받아와야 함
				else if(from == Integer.valueOf(ListViewPhotosActivity.ALLVIEW)){
					String path = allPicClusterList.get(arg2).imageURL;
					
					intent.putExtra("path", path);
					intent.putExtra("comment", allPicClusterList.get(arg2).commentURL);
					intent.putExtra("record", allPicClusterList.get(arg2).record);
					intent.putExtra("ownerName", allPicClusterList.get(arg2).ownerName);
					intent.putExtra("ownerThum", allPicClusterList.get(arg2).ownerThumURL);
					intent.putExtra("what", ListViewPhotosActivity.ALLVIEW+"");
					intent.putExtra("from", "ListView");
					
					applicationClass.tmpSPicture = allPicClusterList.get(arg2);
				}
				
				
				
				startActivity(intent);
			}
	  		
	  	});
	  	
	  //리스트로 사진 보기 시에도 로컬 사진 삭제 기능 추가!
	  //로컬 사진의 경우 역시 롱 클릭 했을 경우 사진 삭제 기능 제공
	  	if(from == Integer.valueOf(ListViewPhotosActivity.LOCAL)){
		  	gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
	
				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					
					iCursor = applicationClass.locCur;
					iCursor.moveToPosition(applicationClass.localClu.clusters.get(index).points.get(arg2).cursorPosition);
				

					
					final int tmpidx = arg2;
					
					new AlertDialog.Builder(ListViewPhotosDetailActivity.this)
					.setTitle(iCursor.getString(iCursor.getColumnIndex(ImageColumns.TITLE)))
					.setIcon(R.drawable.socialmapappicon)
					.setItems(new String[] {getResources().getString(R.string.delete)}, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//만약 사용자가 해당 사진의 삭제를 선택했다면
							if(which==0){
								new AlertDialog.Builder(ListViewPhotosDetailActivity.this)
								//.setTitle("'" + iCursor.getString(iCursor.getColumnIndex(ImageColumns.TITLE)) + "' 를 삭제")
								.setTitle(getResources().getString(R.string.delete_photo))
								.setIcon(R.drawable.socialmapappicon)
								.setMessage(getResources().getString(R.string.sure_delete))
								.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
	
									@Override
									public void onClick(
											DialogInterface dialog,
											int which) {
										
										applicationClass.modifyPicture = true;
										applicationClass.deletePicPaths.add(iCursor.getString(iCursor.getColumnIndex(ImageColumns.DATA)));
										
										applicationClass.deletePictureGridIdx = -1;
										applicationClass.modPictureGrid = true;
										applicationClass.localClu.clusters.get(index).points.remove(tmpidx);
										
										Toast.makeText(ListViewPhotosDetailActivity.this, R.string.delete_completed,
								                Toast.LENGTH_SHORT).show();
										
										if(applicationClass.localClu.clusters.get(index).points.size() <= 0){
											applicationClass.localClu.clusters.remove(index);
											finish();
										}
										else
											adapter.notifyDataSetChanged();
										
									}
									
								})
								.setNegativeButton(getResources().getString(R.string.no), null)
								.show();
							}
						}
					})
					.setNegativeButton(getResources().getString(R.string.cancel), null)
					.show();
					
					return false;
				}
			});
	  	}else if(from == Integer.valueOf(ListViewPhotosActivity.LABEL)){
	  		
	  		gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
	  			
				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					
					iCursor = applicationClass.favCur;
					iCursor.moveToPosition(applicationClass.favoriteClu.clusters.get(index).points.get(arg2).cursorPosition);
					final int tmpidx = arg2;
					
					new AlertDialog.Builder(ListViewPhotosDetailActivity.this)
					.setTitle(getResources().getString(R.string.delete_label_photo))
					.setIcon(R.drawable.socialmapappicon)
					.setItems(new String[] {getResources().getString(R.string.delete)}, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//만약 사용자가 해당 사진의 삭제를 선택했다면
							if(which==0){
								new AlertDialog.Builder(ListViewPhotosDetailActivity.this)
								.setTitle(getResources().getString(R.string.delete_label_photo))
								.setIcon(R.drawable.socialmapappicon)
								.setMessage(getResources().getString(R.string.sure_delete_label))
								.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
	
									@Override
									public void onClick(
											DialogInterface dialog,
											int which) {
										
										applicationClass.modifyPicture = true;
										
										applicationClass.deletePicIds.add(iCursor.getString(iCursor.getColumnIndex(SPicture.PICTUREID)));
										
										applicationClass.deletePictureGridIdx = -1;
										applicationClass.modPictureGrid = true;
										applicationClass.favoriteClu.clusters.get(index).points.remove(tmpidx);
										
										Toast.makeText(ListViewPhotosDetailActivity.this, R.string.delete_completed,
								                Toast.LENGTH_SHORT).show();
										
										if(applicationClass.favoriteClu.clusters.get(index).points.size() <= 0){
											applicationClass.favoriteClu.clusters.remove(index);
											finish();
										}
										else
											adapter.notifyDataSetChanged();
										
									}
									
								})
								.setNegativeButton(getResources().getString(R.string.no), null)
								.show();
							}
						}
					})
					.setNegativeButton(getResources().getString(R.string.cancel), null)
					.show();
					
					return false;
				}
	  		});
	  		
	  	}
	  	
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(applicationClass.modPictureGrid && applicationClass.deletePictureGridIdx != -1 && applicationClass.deletePicPaths.size() > 0){
			//TODO: 클러스커링에서 삭제된 해당 사진만 임시로 제거, 그리드 뷰 새로고침
//			if(applicationClass.localClu.clusters.get(index).points.size() <= 0){
//				finish();
//			}
			
//			cr.delete(Images.Media.EXTERNAL_CONTENT_URI, ImageColumns.DATA + " = '" + applicationClass.deletePicPaths.get(applicationClass.deletePicPaths.size()-1) +"'", null);
//			applicationClass.locCur = iCursor = cr.query(Images.Media.EXTERNAL_CONTENT_URI, null, Images.Media.LATITUDE +"!=0 OR " + Images.Media.LATITUDE +"!=0", null, "LATITUDE desc, LONGITUDE desc");
			
			
//			applicationClass.localClu.cursor = iCursor;
//			applicationClass.localClu.initSequential();
//			applicationClass.localClu.kMedoids();
			
			
			applicationClass.localClu.clusters.get(index).points.remove(applicationClass.deletePictureGridIdx);
			applicationClass.deletePictureGridIdx = -1;
			
			
			if(applicationClass.localClu.clusters.get(index).points.size() <= 0){
				applicationClass.localClu.clusters.remove(index);
				finish();
			}
			else
				adapter.notifyDataSetChanged();
			
			//applicationClass.deletePictureGrid = false;
			
			
			
		}
		
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.list_view_photos_detail, menu);
//		return true;
//	}

	public class ImageAdapter extends BaseAdapter {
		
		public ImageAdapter(Context mContext) {
			super();
			this.mContext = mContext;
		}

		@Override
		public int getCount() {
			if(from == Integer.valueOf(ListViewPhotosActivity.LOCAL))
				return applicationClass.localClu.clusters.get(index).points.size();
			else if(from == Integer.valueOf(ListViewPhotosActivity.FACEBOOK))
				return applicationClass.facebookClu.clusters.get(index).points.size();
			//라벨 데이터 적용
			else if(from == Integer.valueOf(ListViewPhotosActivity.LABEL))
				return applicationClass.favoriteClu.clusters.get(index).points.size();
			//서버로 부터 사진 받아와야 함
			else if(from == Integer.valueOf(ListViewPhotosActivity.ALLVIEW))
				return allPicClusterList.size();
			return 0;	
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
			iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
			iv.setLayoutParams(new GridView.LayoutParams(applicationClass.thumWidth * 4/3, applicationClass.thumWidth * 4/3));
			
			
			if(from == Integer.valueOf(ListViewPhotosActivity.LOCAL)){
				
				int curpos = applicationClass.localClu.clusters.get(index).points.get(arg0).cursorPosition;
				
				iCursor = applicationClass.locCur;
				
				iCursor.moveToPosition(curpos);
				
				Bitmap bit = Images.Thumbnails.getThumbnail(cr, iCursor.getInt(iCursor.getColumnIndex(BaseColumns._ID)), Images.Thumbnails.MICRO_KIND, null);
				
				int degree = getExifOrientation(iCursor.getString(iCursor.getColumnIndex(ImageColumns.DATA)));
				bit = getRotatedBitmap(bit, degree);
				
				//bit = getFramedBitmap(bit, Color.WHITE);
				//Uri uri = Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, iCursor.getString(iCursor.getColumnIndex(MediaStore.Images.Thumbnails._ID)));
				iv.setImageBitmap(bit);
				
				//Log.w("Test", iCursor.getInt(iCursor.getColumnIndex(BaseColumns._ID))+"");
			}
			else if(from == Integer.valueOf(ListViewPhotosActivity.FACEBOOK)){
				int curpos = applicationClass.facebookClu.clusters.get(index).points.get(arg0).cursorPosition;
				
				iCursor = applicationClass.FacCur;
				
				iCursor.moveToPosition(curpos);
				
				String url = iCursor.getString(iCursor.getColumnIndex(SPicture.SMALLIMAGEURL));
				
				ImageTag tag = imageTagFactory.build(url);
            	iv.setTag(tag);
            	
            	//이미지 로더에 로드
                imageLoader.load(iv);
			}
			//라벨 데이터 적용
			else if(from == Integer.valueOf(ListViewPhotosActivity.LABEL)){
				int curpos = applicationClass.favoriteClu.clusters.get(index).points.get(arg0).cursorPosition;
				
				iCursor = applicationClass.favCur;
				
				iCursor.moveToPosition(curpos);
				
				String url = iCursor.getString(iCursor.getColumnIndex(SPicture.SMALLIMAGEURL));
				
				ImageTag tag = imageTagFactory.build(url);
            	iv.setTag(tag);
            	
            	//이미지 로더에 로드
                imageLoader.load(iv);
			}
			// 서버로 부터 사진 받아와야 함
			else if(from == Integer.valueOf(ListViewPhotosActivity.ALLVIEW)){
				String url = allPicClusterList.get(arg0).smallImageURL;
				
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
