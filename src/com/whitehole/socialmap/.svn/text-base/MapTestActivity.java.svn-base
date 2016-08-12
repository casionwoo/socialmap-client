package com.whitehole.socialmap;

import java.io.*;
import java.lang.ref.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

import org.apache.commons.io.*;
import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.json.*;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.database.*;
import android.database.sqlite.*;
import android.graphics.*;
import android.graphics.PorterDuff.Mode;
import android.location.*;
import android.media.*;
import android.net.*;
import android.net.http.*;
import android.os.*;
import android.provider.*;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.ImageView.ScaleType;
import android.widget.AdapterView.*;

import com.facebook.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.*;
import com.google.android.maps.*;
import com.google.android.maps.MapView;
import com.novoda.imageloader.core.*;
import com.novoda.imageloader.core.cache.*;
import com.novoda.imageloader.core.loader.*;
import com.novoda.imageloader.core.model.*;
import com.whitehole.socialmap.R;
import com.whitehole.socialmap.database.*;
import com.whitehole.socialmap.etc.*;
import com.whitehole.socialmap.network.*;
import com.whitehole.socialmap.network.server.*;
import com.whitehole.socialmap.upload.*;

public class MapTestActivity extends FragmentActivity {

	private ApplicationClass applicationClass;
	GoogleMap mGoogleMap;
	
    //위치정보를 공급하는 근원
    String locationProvider;

    //위치 정보 매니져 객체
    LocationManager locationManager;
    
    SupportMapFragment mMapFragment;
    Location location;
    ContentResolver cr;
    Cursor iCursor1, iCursor2, iCursor3, 		//로컬 갤러리 커서
    		facebookCursor, facebookTmpCursor,	//나와 내 친구 페북 커서
    		favCursor; 							//라벨 커서
 
    ArrayList<Marker> markers, tmpMarkers;
    Gallery g;
    ImageAdapter imageAdapter;
//    Point screenPointLeftTop, screenPointRightBottom;
    LatLng southwest, northeast;								// ┌--*
	    														// │  │
	    														// *--┘
    Projection projection;
    LatLngBounds visi;
    float preZoom, cerZoom;
    Clustering clustering, localClustering, facebookClustering, favClustering;
    boolean clusterClick = false;
    int clickClusterIdx=0;
    
    //이미지 로더 변수
    static ImageManager imageManager;
	Loader imageLoader;
	ImageTagFactory imageTagFactory;
	
	// 네트워크 함수로 부터 내 페북 사진 or 친구 사진 리스트를 받아와 저장할 ArrayList
	ArrayList<SPicture> sp;
	MyDBHelper mHelper;
	MyFavDBHelper favHelper;
	
	SQLiteDatabase db, fdb;
	String seletionStr;
	
	int markerfrom=0;	//마커클릭시 해당 마커가 어디 소속인지 알기 위한 변수
						// 0: 로컬 갤러리
						// 1: 페이스북
	//int thumWidth, thumHeight;
	
	final String LOCALNUM = "0", FACEBOOKNUM = "1", ALLUSERNUM = "3", LABELNUM = "4",	INFO_WINDOW = "0", USER_THUM = "1";
	final int LOCALVAL = 0, FACEBOOKVAL = 1, FACEBOOKFRIENDS = 2, LABELVAL = 3, ALLUSERVAL = 4;
	Bitmap infoWBmp = null;
	Bitmap infothumBmp = null;
	
	GetFriendsPicture fList = new GetFriendsPicture();
	GetInfo info = new GetInfo();
	private UiLifecycleHelper uiHelper;
	ProgressDialog dialog;
	
	Boolean finish_loading = true;
	
	BitmapDownloaderTaskInfo preTask, preTaskThum;
	
	//네트워크 매니저
	ConnectivityManager mgr;
	SharedPreferences pref;
	
	View myView;
	
	Looper mLoop;
	Handler mhandler, mhandler2;
	MyHandlerThread ht;
	ArrayList<BitmapDownloaderTask> facebookMarkerTasks;
	
	
	//전체 사용자 보기 변수들
	ArrayList<SPicture> allPicList;
	int clickAllUserIdx = -1;
	ArrayList<SPicture> allPicClusterList;	//모든 사용자 마커를 클릭했을때 해당 마커에 포함되는 사진 리스트
	
	GetPhotos getPotos;
	Marker tmrk;
	
	String tmpStr = "";
	boolean clickMarker = false;
	
//	Thread allViewThread = null;
	GetPotosTask allViewTask = null;
	
	boolean moveCamera = false;
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        //onSessionStateChange(session, state, exception);
	    }
	};
	
	//뒤로가기 키 입력
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			//Log.w("Key", "down!");
			//인포윈도가 보이는 경우
			if(applicationClass.backKey == 0)
			{
				if(preTask != null && preTaskThum != null){
					preTask.cancel(true);
					preTaskThum.cancel(true);
				}
				
				if(clusterClick){
					clusterClick = false;
					
					for(Marker m: markers){
						if(!m.getSnippet().equals(ALLUSERNUM) && 
								m.getTitle().split(":")[1].equals(clickClusterIdx+"") &&
								clustering != null && clustering.from == Integer.valueOf(m.getSnippet())){
							m.setVisible(true);
							m.hideInfoWindow();
							break;
						}
					}
				}
				
				if(clickAllUserIdx != -1){
					for(Marker m: markers){
						if(m.getSnippet().equals(ALLUSERNUM) && 
								m.getTitle().split(":")[1].equals(clickAllUserIdx+"")){
							m.setVisible(true);
							m.hideInfoWindow();
							break;
						}
					}
					
					clickAllUserIdx = -1;
				}
				
				//다른 마커를 클릭한 상태였다면 클릭 전 상태로 다시 복원
				if(applicationClass.session != null && applicationClass.session.isOpened()
						 && mgr.getActiveNetworkInfo() != null)
				{
					for(Marker m: tmpMarkers){
						m.remove();
					}
					tmpMarkers.clear();
				}else{
					//인터넷에 연결되어 있지 않다면, 로컬 마커만 지움
					for(Marker m: (ArrayList<Marker>)tmpMarkers.clone()){
						if(m.getSnippet().equals(LOCALNUM)){
							m.remove();
							tmpMarkers.remove(m);
						}
					}
				}
				
				imageAdapter.notifyDataSetChanged();
				applicationClass.backKey = 1;
				
			}
			//인포윈도는 보이지 않고 깨끗한 상태에서 뒤로가기를 누르는 경우
			else if(applicationClass.backKey == 1 && 
					(System.currentTimeMillis() - applicationClass.backTime) > 2000 ){
				//토스트로 종료하려면 한번더 누르라는 안내 메시지 띄움
				Toast.makeText(MapTestActivity.this, R.string.exit_app,
		                Toast.LENGTH_SHORT).show();
				applicationClass.backTime = System.currentTimeMillis();
				
				
			}
			//뒤로가기 버튼 누르고 2초 안에 뒤로가기 버튼 다시 누르는 경우
			else if(applicationClass.backKey == 1){
				finish();
			}
			
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onCreate(Bundle saveInstanceState){
		super.onCreate(saveInstanceState);
		setContentView(R.layout.activity_map);
				
		allPicList = new ArrayList<SPicture>();
		allPicClusterList = new ArrayList<SPicture>();
		
		mhandler = new Handler();
		mhandler2 = new Handler();
		getPotos = new GetPhotos();
		
		myView = new View(this);
		myView.setFocusable(true);
		myView.setFocusableInTouchMode(true);
		
		facebookMarkerTasks = new ArrayList<MapTestActivity.BitmapDownloaderTask>();
		
		pref = getSharedPreferences("Pref", 0);
		
		mgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(saveInstanceState);
		
		applicationClass = (ApplicationClass)getApplicationContext();
		
		mHelper = new MyDBHelper(this);
		favHelper = new MyFavDBHelper(this);
		
		
		if(!pref.getBoolean("introAlert", false)){
			
			new AlertDialog.Builder(MapTestActivity.this)
			.setTitle(getResources().getString(R.string.notices))
			.setIcon(R.drawable.socialmapappicon)
			.setMessage(getResources().getString(R.string.intro_notification2))
			.setPositiveButton(getResources().getString(R.string.ok), null)
			.setNegativeButton(getResources().getString(R.string.not_see_again), new DialogInterface.OnClickListener() {
	
				@Override
				public void onClick(
						DialogInterface dialog,
						int which) {
	
					SharedPreferences.Editor edit = pref.edit();
					
					edit.putBoolean("introAlert", true);
					
					edit.commit();
				}
				
			})
			.show();
		}
		
//		SharedPreferences pref = getSharedPreferences("Pref", 0);
//		
//		SharedPreferences.Editor edit = pref.edit();
//		
//		edit.putBoolean("NextInterlock", false);
//		
//		edit.commit();

		
		//썸네일의 크기를 저장
    	//Log.w("bitmap size", bit.getWidth()+", "+bit.getHeight());
    	if(applicationClass.thumWidth == 0){
//    		applicationClass.thumWidth = bit.getWidth();
//    		applicationClass.thumHeight = bit.getHeight();
    		
    		Display currentDisplay = getWindowManager().getDefaultDisplay();
    		
    		applicationClass.thumWidth = getResources().getDimensionPixelSize(R.dimen.marker_width);
//    		applicationClass.thumWidth = (currentDisplay.getWidth() <= currentDisplay.getHeight() ? currentDisplay.getWidth() : currentDisplay.getHeight()) / 5;
    		applicationClass.thumHeight = applicationClass.thumWidth;
    		
    		
    		
    	}
		
		Criteria crit = new Criteria();
		crit.setAccuracy(Criteria.NO_REQUIREMENT);
		crit.setAltitudeRequired(false);
		crit.setPowerRequirement(Criteria.NO_REQUIREMENT);
		crit.setCostAllowed(false);
		
		
        //위치 정보 매니져 객체 얻어오기
        locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);

        //위치정보 공급자 얻어오기
        //locationProvider = locationManager.getBestProvider(new Criteria(), true);
        locationProvider = locationManager.getBestProvider(crit, true);
        
        if(locationProvider!=null)
	        //가장 최근의 Location 객체 얻어오기
	        location = locationManager.getLastKnownLocation(locationProvider);
        else
        	Toast.makeText(MapTestActivity.this, R.string.locAgreeNot,
                    Toast.LENGTH_SHORT).show();
				
        try {
			setUpMapIfNeeded();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        //이미지 로더 셋팅
        LoaderSettings settings = new LoaderSettings.SettingsBuilder().withCacheManager(new LruBitmapCache(this))
      	      .withDisconnectOnEveryCall(true).build(this);

  	    imageManager = new ImageManager(this, settings);
  	    imageLoader = imageManager.getLoader();
  	    
  	    imageTagFactory = new ImageTagFactory(this, R.drawable.noimage);
  	    imageTagFactory.setErrorImageId(R.drawable.noimage);
        
        
       
     // 갤러리 리스트
        // Reference the Gallery view
        g = (Gallery) findViewById(R.id.map_gallery);
        // Set the adapter to our custom adapter (below)
 
        imageAdapter = new ImageAdapter(this);
        g.setAdapter(imageAdapter);
        
        //g.setNextFocusDownId(77);
        
        // Set a item click listener, and just Toast the clicked position
        g.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                //Toast.makeText(MapTestActivity.this, "" + position, Toast.LENGTH_SHORT).show();
            	int tmp;
            	Bitmap bit;
        		BitmapDescriptor bitd = null;
        		String imageTitle;
        		Marker tmpMarer;
            	
            	
        		if(!clusterClick && clickAllUserIdx == -1){
//	            	iCursor2.moveToPosition(position);
//	            	String dateTaken = iCursor2.getString(iCursor2.getColumnIndex(ImageColumns.DATE_TAKEN));
//	            	
//	            	for(Marker m : markers){
//	            		if(m.getTitle().equals(dateTaken)){
//	            			
//	            			//해당 마커로 이동하고 인포 윈도 보여주기
//	            			m.showInfoWindow();
	            
	//            			mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(iCursor2.getDouble(iCursor2.getColumnIndex(ImageColumns.LATITUDE)), 
	//            			iCursor2.getDouble(iCursor2.getColumnIndex(ImageColumns.LONGITUDE))), 7.0f));
	            			
	//            			mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(m.getPosition().latitude, 
	//            					m.getPosition().longitude)));
	            			

//	            			break;
//	            		}
//	            	}
            	}
        		else if(clickAllUserIdx != -1){
        			if(applicationClass.session != null && applicationClass.session.isOpened() && mgr.getActiveNetworkInfo() != null)
					{
            			//임시 마커도 있다면 지우고
            			for(Marker mk: tmpMarkers){
            				mk.remove();
            			}
            			tmpMarkers.clear();
					}
        			
        			
        			for(Marker m : markers){
        				
        				if(m.getSnippet().equals(ALLUSERNUM) && 
        						m.getTitle().split(":")[1].equals(clickAllUserIdx+"")){
        				
        					//만일 하단 리스트에서 클릭한 사진이 대표사진이라면
//        					if(clickMarker){
//        						m.setVisible(true);
//	            				m.showInfoWindow();
//
//	            				clickMarker = false;
//	            				
//	            				break;
//        					}
        					
        					bitd = BitmapDescriptorFactory.fromResource(R.drawable.noimage);
            				
            				imageTitle = position + ":" + clickAllUserIdx;
	            			
	            			tmpMarer = mGoogleMap.addMarker(new MarkerOptions().position(m.getPosition()).title(imageTitle).icon(bitd).snippet(m.getSnippet()));
		            		
	            			tmpMarkers.add(tmpMarer);
		            			
	            			m.setVisible(false);
	            				
	            			finish_loading = null;
	            			tmpMarer.showInfoWindow();
            				
	            			Log.w("setOnItemClickListener","position:" + position + ":"+allPicClusterList.get(position).pictureID);
	            			
	            			if(applicationClass.session != null && applicationClass.session.isOpened() && mgr.getActiveNetworkInfo() != null)
							{
		            			finish_loading = true;
	            				BitmapDownloaderTask task = new BitmapDownloaderTask(tmpMarer, 1);
	            				task.execute(allPicClusterList.get(position).smallImageURL);
							}
        				}
        			}
        		}
            	else{
            		
            		if(applicationClass.session != null && applicationClass.session.isOpened() && mgr.getActiveNetworkInfo() != null)
					{
            			//임시 마커도 있다면 지우고
            			for(Marker mk: tmpMarkers){
            				mk.remove();
            			}
            			tmpMarkers.clear();
					}
            		
            		for(Marker m : markers){
	            		if(m.getSnippet().equals(clustering.from + "") && m.getTitle().split(":")[1].equals(clickClusterIdx+"")){
	            			
	            			tmp = clustering.clusters.get(clickClusterIdx).points.get(position).cursorPosition;
	            			iCursor3.moveToPosition(tmp);
	            			
	            			//만일 하단 리스트에서 클릭한 사진이 대표사진이라면
	            			if(tmp == Integer.parseInt(m.getTitle().split(":")[0])){
	            				m.setVisible(true);
	            				m.showInfoWindow();
	
	            				break;
	            			}
	            			
	            			if(m.getSnippet().equals(LOCALNUM)){

		                		bit = Images.Thumbnails.getThumbnail(cr, iCursor3.getInt(iCursor3.getColumnIndex(BaseColumns._ID)), Images.Thumbnails.MICRO_KIND, null);
		                    	
		                    	//bit = Bitmap.createScaledBitmap(bit, bit.getWidth()*2/3, bit.getHeight()*2/3, true);
		            			
		                		bit = Bitmap.createScaledBitmap(bit, applicationClass.thumWidth*2/3, applicationClass.thumWidth*2/3, true);
		                		
		                    	bit = getFramedBitmap(bit, Color.WHITE);
		            			
		            			bitd = BitmapDescriptorFactory.fromBitmap(bit);
		            			
		            			
		            			//해당 점의 커서 포지션을 이미지 타이틀에 저장
		            			//imageTitle = iCursor3.getString(iCursor3.getColumnIndex(ImageColumns.DATE_TAKEN));
		            			
		            			
		            			
		            			//markers.remove(m);
		            			
		            			imageTitle = tmp + ":" + clickClusterIdx;
	            			
		            			tmpMarer = mGoogleMap.addMarker(new MarkerOptions().position(m.getPosition()).title(imageTitle).icon(bitd).snippet(m.getSnippet()));
			            		
		            			tmpMarkers.add(tmpMarer);
			            			
		            			m.setVisible(false);
		            				
		            			tmpMarer.showInfoWindow();
	            			}
	            			else if(m.getSnippet().equals(FACEBOOKNUM) || m.getSnippet().equals(LABELNUM)){
	            				Log.w("Call", "infoWindow call 0");
	            				
	            				bitd = BitmapDescriptorFactory.fromResource(R.drawable.noimage);
	            				
	            				imageTitle = tmp + ":" + clickClusterIdx;
		            			
		            			tmpMarer = mGoogleMap.addMarker(new MarkerOptions().position(m.getPosition()).title(imageTitle).icon(bitd).snippet(m.getSnippet()));
			            		
		            			tmpMarkers.add(tmpMarer);
			            			
		            			m.setVisible(false);
		            				
		            			finish_loading = null;
		            			tmpMarer.showInfoWindow();
	            				
		            			if(applicationClass.session != null && applicationClass.session.isOpened() && mgr.getActiveNetworkInfo() != null)
								{
			            			finish_loading = true;
		            				BitmapDownloaderTask task = new BitmapDownloaderTask(tmpMarer, 1);
		            				task.execute(iCursor3.getString(iCursor3.getColumnIndex(SPicture.SMALLIMAGEURL)));
								}
	            			}
	
	            			break;
	            		}
            		}
            	}
            }
        });
        
        // 하단의 갤러리뷰 리스트에서 특정 사진을 롱클릭하는 경우
        // 해당 사진을 삭제할 수 있도록 제공
        g.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				
				
//				int i=-1, j=0;
//				boolean isCluCenter = false;
				
				for(Marker m : (ArrayList<Marker>)markers.clone()){
//					i++;
					
            		if(m.getSnippet().equals(LOCALNUM) && m.getTitle().split(":")[1].equals(clickClusterIdx+"")){
            			
            			final int tmp = clustering.clusters.get(clickClusterIdx).points.get(arg2).cursorPosition;
        				
        				
            			//만일 대표사진을 롱 클릭했다면
//            			if(tmp == Integer.parseInt(m.getTitle().split(":")[0]))
//            				isCluCenter = true;
//            			else{
//            			//if(m.getSnippet().equals(LOCALNUM)){
//        				
//        				
//	        				// 현재 하단의 리스트에서 한번 선택하여 임시 마커로 보이고 있는 사진을 롱 클릭했는 지 체크
//	        				for(Marker t : tmpMarkers){
//	        					
//	        					if(tmp == Integer.parseInt(t.getTitle().split(":")[0]) && t.getSnippet().equals(LOCALNUM))
//	        						break;
//	        					j++;
//	        				}
//            			}
        				
//            			final int tIdx = j;
//        				final boolean isCluCtr = isCluCenter;
//        				final int idx = i;
        				
        				new AlertDialog.Builder(MapTestActivity.this)
        				.setTitle(iCursor3.getString(iCursor3.getColumnIndex(ImageColumns.TITLE)))
        				.setIcon(R.drawable.socialmapappicon)
        				.setItems(new String[] {getResources().getString(R.string.delete)}, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								//만약 사용자가 해당 사진의 삭제를 선택했다면
								if(which==0){
									
									new AlertDialog.Builder(MapTestActivity.this)
									.setTitle(iCursor3.getString(iCursor3.getColumnIndex(ImageColumns.TITLE)))
									.setIcon(R.drawable.socialmapappicon)
									.setMessage(getResources().getString(R.string.sure_delete))
									.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											
											iCursor3.moveToPosition(tmp);
											
											
											ArrayList<String> arr = new ArrayList<String>();
											
											arr.add(iCursor3.getString(iCursor3.getColumnIndex(ImageColumns.DATA)));
											deleteLocalPhoto(arr, false);
											
										}
										
									})
									.setNegativeButton(getResources().getString(R.string.no), null)
									.show();
									
									
								}
							}
						})
						.setNegativeButton(getResources().getString(R.string.cancel), null)
						.show();
        				break;
        			}else if( m.getSnippet().equals(LABELNUM) && m.getTitle().split(":")[1].equals(clickClusterIdx+"")){
        				final int tmp = clustering.clusters.get(clickClusterIdx).points.get(arg2).cursorPosition;
        				
        				new AlertDialog.Builder(MapTestActivity.this)
        				.setTitle(getResources().getString(R.string.delete_label_photo))
        				.setIcon(R.drawable.socialmapappicon)
        				.setItems(new String[] {getResources().getString(R.string.delete)}, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								//만약 사용자가 해당 사진의 삭제를 선택했다면
								if(which==0){
									
									new AlertDialog.Builder(MapTestActivity.this)
									.setTitle(getResources().getString(R.string.delete_label_photo))
									.setIcon(R.drawable.socialmapappicon)
									.setMessage(getResources().getString(R.string.sure_delete))
									.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											
											iCursor3.moveToPosition(tmp);
											
											ArrayList<String> arr = new ArrayList<String>();
											
											arr.add(iCursor3.getString(iCursor3.getColumnIndex(SPicture.PICTUREID)));
											
											deleteLabelPhoto(arr, false);
										}
										
									})
									.setNegativeButton(getResources().getString(R.string.no), null)
									.show();
									
									
								}
							}
						})
						.setNegativeButton(getResources().getString(R.string.cancel), null)
						.show();
        				
        				break;
        			}
            		//}
				}
				
				
				return false;
			}
		});
	}
	
	public void addLabelPhoto(ArrayList<SPicture> pics){
		MyFavDBHelper.pushDataToDB(favHelper, pics);
		
		fdb = favHelper.getReadableDatabase();
		
		favCursor = fdb.query(MyFavDBHelper.DATABASENAME, null, null, null, null, null, null);
																					
		//라벨 보기 선택 중 이었다면 다시그림
		if(applicationClass.selectedGroup!=null && applicationClass.selectedGroup[3]==1){
			//라벨 마커만 모두 지움
			for(Marker mm : (ArrayList<Marker>)markers.clone()){
				if(mm.getSnippet().equals(LABELNUM)){
					mm.remove();
					markers.remove(mm);
				}
			}
			
			for(Marker mm : (ArrayList<Marker>)tmpMarkers.clone()){
				if(mm.getSnippet().equals(LABELNUM)){
					mm.remove();
					tmpMarkers.remove(mm);
				}
			}
			
			drawLabelThumnailMap();
		}
		
//				Toast.makeText(MapTestActivity.this, R.string.delete_completed,
//			              Toast.LENGTH_SHORT).show();
		
		clusterClick = false;
		imageAdapter.notifyDataSetChanged();
		
	}
	
	public void addLabelPhoto(ArrayList<String> picIds, boolean isGrid){
		
		ArrayList<SPicture> arr = new ArrayList<SPicture>();
		
		Cursor tmpFavCur = null;
		
		Log.w("addLabelPhoto", "pic nums :" + picIds.size());
		
		for(String s : picIds){
			tmpFavCur = db.query(MyDBHelper.DATABASENAME, null, SPicture.PICTUREID + " = '" + s + "'", null, null, null, null);
			
			tmpFavCur.moveToFirst();
			
			arr.add(new SPicture(tmpFavCur.getString(tmpFavCur.getColumnIndex(SPicture.PICTUREID)),
					tmpFavCur.getInt(tmpFavCur.getColumnIndex(SPicture.ISPUBLIC)), 
					tmpFavCur.getDouble(tmpFavCur.getColumnIndex(SPicture.LATITUDE)),
					tmpFavCur.getDouble(tmpFavCur.getColumnIndex(SPicture.LONGITUDE)),
					tmpFavCur.getString(tmpFavCur.getColumnIndex(SPicture.COMMENTURL)),
					tmpFavCur.getString(tmpFavCur.getColumnIndex(SPicture.IMAGEURL)),
					tmpFavCur.getString(tmpFavCur.getColumnIndex(SPicture.SMALLIMAGEURL)),
					tmpFavCur.getLong(tmpFavCur.getColumnIndex(SPicture.MAKETIME)),
					tmpFavCur.getString(tmpFavCur.getColumnIndex(SPicture.MAKETIMES)),
					tmpFavCur.getString(tmpFavCur.getColumnIndex(SPicture.RECORD)),
					tmpFavCur.getString(tmpFavCur.getColumnIndex(SPicture.OWNERNAME)),
					tmpFavCur.getString(tmpFavCur.getColumnIndex(SPicture.OWNERID)),
					tmpFavCur.getString(tmpFavCur.getColumnIndex(SPicture.OWNERTHUMURL)),
					tmpFavCur.getString(tmpFavCur.getColumnIndex(SPicture.PLACENAME))));
		}
		
		if(tmpFavCur != null)
			tmpFavCur.close();
		
		MyFavDBHelper.pushDataToDB(favHelper, arr);
		
		fdb = favHelper.getReadableDatabase();
		
		favCursor = fdb.query(MyFavDBHelper.DATABASENAME, null, null, null, null, null, null);
																							
		//라벨 보기 선택 중 이었다면 다시그림
		if(applicationClass.selectedGroup!=null && applicationClass.selectedGroup[3]==1){
			//라벨 마커만 모두 지움
			for(Marker mm : (ArrayList<Marker>)markers.clone()){
				if(mm.getSnippet().equals(LABELNUM)){
					mm.remove();
					markers.remove(mm);
				}
			}
			
			for(Marker mm : (ArrayList<Marker>)tmpMarkers.clone()){
				if(mm.getSnippet().equals(LABELNUM)){
					mm.remove();
					tmpMarkers.remove(mm);
				}
			}
			
			drawLabelThumnailMap();
		}
		
//		Toast.makeText(MapTestActivity.this, R.string.delete_completed,
//	              Toast.LENGTH_SHORT).show();
		
		clusterClick = false;
		imageAdapter.notifyDataSetChanged();
	}
	
	public void deleteLabelPhoto(ArrayList<String> picIds, boolean isGrid){
		
		MyFavDBHelper.deletePicturesOnDB(favHelper, picIds);
		
		fdb = favHelper.getReadableDatabase();
		
		favCursor = fdb.query(MyFavDBHelper.DATABASENAME, null, null, null, null, null, null);
		
		
		//라벨 마커만 모두 지움
		for(Marker mm : (ArrayList<Marker>)markers.clone()){
			if(mm.getSnippet().equals(LABELNUM)){
				mm.remove();
				markers.remove(mm);
			}
		}
		
		for(Marker mm : (ArrayList<Marker>)tmpMarkers.clone()){
			if(mm.getSnippet().equals(LABELNUM)){
				mm.remove();
				tmpMarkers.remove(mm);
			}
		}
																							
		//다시그림
		drawLabelThumnailMap();
		
		Toast.makeText(MapTestActivity.this, R.string.delete_completed,
	              Toast.LENGTH_SHORT).show();
		
		clusterClick = false;
		imageAdapter.notifyDataSetChanged();
	}
	
	public void deleteLocalPhoto(ArrayList<String> path, boolean isGrid){	
		//path는 삭제할 사진의 절대 경로
		
		for(String s : path){
			//실제 파일에서 해당 사진을 삭제
			File file = new File(s);
			if(file.exists()){
				if(file.delete())
					Log.w("deleteFile","success!");
				else
					Log.w("deleteFile","Fail!");
			}else
				Log.w("deleteFile","Not exists!");	
			
			
			//media 커서에서 해당 사진의 정보를 지움
			cr.delete(Images.Media.EXTERNAL_CONTENT_URI, ImageColumns.DATA + " = '" + s +"'", null);
		}
			
		//iCursor3.close();
		//커서 다시 할당받고
		iCursor1 = cr.query(Images.Media.EXTERNAL_CONTENT_URI, null, Images.Media.LATITUDE +"!=0 OR " + Images.Media.LATITUDE +"!=0", null, "LATITUDE desc, LONGITUDE desc");
		iCursor3 = iCursor2 = iCursor1;
		
		
		//만일 삭제하려는 사진이 대표 사진이라면 마커 지우고
//		if(isCluCtr){
//			markers.get(idx).remove();
//			markers.remove(idx);
//		}
		// 만일 삭제하려는 사진이 대표사진이 아니고 임시마커에 존재한다면
		// 임시마커의 사진정보를 지우고 대표 사진 visible
//		else if(tIdx < tmpMarkers.size()){
//			markers.get(idx).setVisible(true);
//			tmpMarkers.get(tIdx).setVisible(false);
//			//tmpMarkers.remove(tIdx);
//			g.setSelection(0);
//		}
		
		//로컬 마커만 모두 지움
		for(Marker mm : (ArrayList<Marker>)markers.clone()){
			if(mm.getSnippet().equals(LOCALNUM)){
				mm.remove();
				markers.remove(mm);
			}
		}
		
		for(Marker mm : (ArrayList<Marker>)tmpMarkers.clone()){
			if(mm.getSnippet().equals(LOCALNUM)){
				mm.remove();
				tmpMarkers.remove(mm);
			}
		}
		
		
		//다시 그림
		reDrawGallaryThumnailMap();
		
		
		if(!isGrid){
			markerfrom = 0;
			clustering = localClustering;
			clusterClick = false;
		}
		//만일 그리드 뷰에서 해당 사진을 삭제했을 경우
		else{
			clusterClick = false;
		}
		Toast.makeText(MapTestActivity.this, R.string.delete_completed,
              Toast.LENGTH_SHORT).show();
		imageAdapter.notifyDataSetChanged();
	}
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
//		Log.w("resume", "resume !!!");
		Log.w("onResume", "applicationClass.addPicIds.size(): " + applicationClass.addPicIds.size());
		
		
		// OptionViewActivity로 부터 체크 선택 정보 받기: Application Class에서 가져오기
		
		if(applicationClass.selectedGroup != null 
				&& applicationClass.selectedGroup[0]==0 
				&& localClustering != null){
			localClustering.clusters.clear();
		}
		
		if((applicationClass.selectedGroup == null || 
				applicationClass.selectedGroup[1] == 0 && applicationClass.selectedGroup[2] == 0) && 
				facebookClustering != null){
			facebookClustering.clusters.clear();
		}
		
		if((applicationClass.selectedGroup == null || 
				applicationClass.selectedGroup[3] == 0) && 
				(favClustering != null)){
			favClustering.clusters.clear();
		}
		
		if((applicationClass.selectedGroup == null || 
				applicationClass.selectedGroup[4] == 0) && 
				allPicList !=null){
			allPicList.clear();
		}
		
		if(applicationClass.modifyPicture){
			
			if(applicationClass.deletePicPaths.size() > 0){
				deleteLocalPhoto(applicationClass.deletePicPaths, true);
				
				applicationClass.deletePicPaths.clear();
			}
			
			//추가 해야할 라벨 데이터 있음
			if(applicationClass.addPicIds.size() > 0){
				addLabelPhoto(applicationClass.addPicIds, true);
				applicationClass.addPicIds.clear();
			}
			
			//삭제 해야할 라벨 데이터 있음
			if(applicationClass.deletePicIds.size() > 0){
				deleteLabelPhoto(applicationClass.deletePicIds, true);
				applicationClass.deletePicPaths.clear();
			}
			
			//라벨에 추가할 전체 사용자 사진 있음
			if(applicationClass.alluserfavList.size() > 0){
				addLabelPhoto(applicationClass.alluserfavList);
				applicationClass.alluserfavList.clear();
			}
			
			applicationClass.modifyPicture = false;
		}

		// 리스트로 사진 보기 시에도 로컬 사진 삭제 기능 추가!
//		if(applicationClass.reDrawGall){
//			
//			showDialog(0);
//			
//			ThreadExcutor.execute(new Runnable() {
//	    		@Override
//				public void run() {
//			
//					//커서 다시 할당받고
//					iCursor1 = cr.query(Images.Media.EXTERNAL_CONTENT_URI, null, Images.Media.LATITUDE +"!=0 OR " + Images.Media.LATITUDE +"!=0", null, "LATITUDE desc, LONGITUDE desc");
//					iCursor3 = iCursor2 = iCursor1;
//					
//					applicationClass.reDrawGall = false;
//					
//					//로컬 마커만 모두 지움
//					for(Marker mm : (ArrayList<Marker>)markers.clone()){
//						if(mm.getSnippet().equals(LOCALNUM)){
//							mm.remove();
//							markers.remove(mm);
//						}
//					}
//					
//					//tmp 마커도 로컬만 모두 지움
//					for(Marker mm : (ArrayList<Marker>)tmpMarkers.clone()){
//						if(mm.getSnippet().equals(LOCALNUM)){
//							mm.remove();
//							tmpMarkers.remove(mm);
//						}
//					}
//					
//					imageAdapter.notifyDataSetChanged();
//					
//					//다시 그림
//					reDrawGallaryThumnailMap();
//			
//					dialog.dismiss();
//	    		}
//			});
//		}
		
//		if(applicationClass.friendsData != null)
//			Log.w("test", applicationClass.friendsData.get(0)+"");
		
		if(applicationClass.isSelectOptionView || applicationClass.uploadPicture){
			// 사진 다시 그리는 코드 수행
			//Log.w("test", applicationClass.isSelectOptionView+"");
			
			if(applicationClass.uploadPicture){	
//				db = mHelper.getReadableDatabase();
//				facebookCursor = db.query(MyDBHelper.DATABASENAME, null, null, null, null, null, null);				
				
				applicationClass.uploadPicture = false;
				
				if(applicationClass.selectedGroup != null &&
						(applicationClass.selectedGroup[1] == 1 ||
						applicationClass.selectedGroup[2] == 1 ||
						applicationClass.selectedGroup[4] == 1)){
					//Log.w("Test", "ggg");
					ht = new MyHandlerThread(MapTestActivity.this, "wait", "Please wait while loading...");
					ht.start();
				
				
					handler.sendEmptyMessage(2);  // 작업이 끝나면 이 핸들러를 호출
				}
				
			}else{
				showDialog(0);
			
				ThreadExcutor.execute(new Runnable() {
		    		@Override
					public void run() {
		    			
		    			if(applicationClass.selectedGroup != null && 
		    					applicationClass.session != null &&
		    					applicationClass.session.isOpened()
		    					&& mgr.getActiveNetworkInfo() != null
		    					&& applicationClass.selectedGroup[FACEBOOKFRIENDS] == 1)
		    				loadFriendsData();
		    			//TODO 네트워크 연결하라는 알림 팝업
		    			else
		    				;
	
		    			handler.sendEmptyMessage(0);  // 작업이 끝나면 이 핸들러를 호출
		    		}
				});	
			}
			
			
		}
			
	}
	
	/**
	 * 친구 사진 보기를 선택한 경우 수행되는 코드 
	 */
	public void loadFriendsData(){
		// 친구 사진이 선택된 경우 선택한 친구의 사진을 받아옴
		// 친구의 데이터가 이미 DB에 저장되어 있다면 넘어가고, 없다면 네트워크로부터 받아오는 코드
		db = mHelper.getReadableDatabase();
		seletionStr = "";
		
		for(FriendsList f : applicationClass.friendsData ){
			facebookTmpCursor = db.query(MyDBHelper.DATABASENAME, null, SPicture.OWNERID + " = '"+ f.id +"'", null, null, null, null);
			
			if(facebookTmpCursor.getCount() == 0){
				try {
					sp = fList.getPictures(f.id);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				MyDBHelper.pushDataToDB(mHelper, sp);
			}
			
			//맵에 그리기 위하여 DB에 요청할 친구리스트 selection 만들기
			if(seletionStr.length() == 0){
				seletionStr += SPicture.OWNERID + " = '" + f.id + "'";
			}
			else
				seletionStr += " OR " + SPicture.OWNERID + " = '" + f.id + "'";
		}
		
	}
	
	/**
	 * 사용자가 새로고침을 선택했을 때 수행되는 코드
	 */
	public void reloadFriendsData(){
		if(!isFinishing()) {
//			db = mHelper.getReadableDatabase();
			
			try {
				sp = fList.getPictures(applicationClass.friendsData);
			} catch (JSONException e) {
	
				e.printStackTrace();
			} catch (InterruptedException e) {
			
				e.printStackTrace();
			} catch (ExecutionException e) {
			
				e.printStackTrace();
			} catch (IOException e) {
			
				e.printStackTrace();
			}
			
			if(sp != null && sp.size() > 0){
				MyDBHelper.deleteFriendsDataOnDB(mHelper, applicationClass.friendsData);
				
				MyDBHelper.pushDataToDB(mHelper, sp);
				
				SharedPreferences pref = getSharedPreferences("Pref", 0);
				
				//현재 시간을 기록
				SharedPreferences.Editor edit = pref.edit();
				
				edit.putLong("friendsDeletedTime", System.currentTimeMillis());
				
				edit.commit();
				
				db = mHelper.getReadableDatabase();
				facebookCursor = db.query(MyDBHelper.DATABASENAME, null, null, null, null, null, null);
			}
			
			//handler.sendEmptyMessage(0);  // 작업이 끝나면 이 핸들러를 호출
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.activity_map, menu);

		super.onCreateOptionsMenu(menu);
//		MenuItem item=menu.add(0, 1, 0, R.string.map_menu_setting1);
//		menu.add(0, 2, 0, R.string.map_menu_setting2);
//		menu.add(0, 3, 0, R.string.map_menu_setting3);
		//menu.add(0, 1, 0, R.string.map_menu_setting5);
			
		//내 사진 보기
		//SubMenu my_show = menu.addSubMenu(R.string.map_menu_setting0);
		
		//R.string.map_menu_setting0
		//R.string.map_menu_setting6
		//R.string.map_menu_setting1
		
		//새로고침
		menu.add(0, 2, 0, R.string.map_menu_setting0).setIcon(R.drawable.menu_refresh).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				
				//showDialog(0);
//				mhandler = new Handler();
				
		    	if (applicationClass.selectedGroup != null && applicationClass.session != null && applicationClass.session.isOpened()
		    			 && mgr.getActiveNetworkInfo() != null) {
	
			    	// 사진 받아오기 함수
			    	ThreadExcutor.execute(new Runnable() {
			    		@Override
						public void run() {
			    		
//			    			try {
//								Thread.sleep(1000);
//								 
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
			    			
			    			//Looper.prepare();
			    			//mLoop = Looper.myLooper();
			    			
					    	
					    	mhandler.post(new Runnable() {
								
								@Override
								public void run() {
									ht = new MyHandlerThread(MapTestActivity.this, "wait", "Please wait while loading...");
									ht.start();
									
									//주작업
									if(applicationClass.selectedGroup[1]==1)
										loadData();
									
					    			Log.w("LoadData", "end//////");
					    			
					    			if(applicationClass.friendsData != null && applicationClass.friendsData.size() > 0)
					    				reloadFriendsData();			
					    			
//					    			if(applicationClass.selectedGroup != null){
//					    				// 이전에 그렸던 마커를 모두 지우고
//					    				for (Marker m : markers) {
//					    					m.remove();
//					    				}
//					    	
//					    				markers.clear();
//					    	
//					    				// 임시 마커도 있다면 지우고
//					    				for (Marker m : tmpMarkers) {
//					    					m.remove();
//					    				}
//					    				tmpMarkers.clear();
//					    	
//					    				// 선택한 것에 맞게 다시 그림
//					    				reDrawThumnailMap();
//					    				
//					    				clusterClick = false;
//					    				
//					    				imageAdapter.notifyDataSetChanged();
//					    				
//					    				applicationClass.isSelectOptionView = false;
//					    			}
					    			
					    			handler.sendEmptyMessage(2);
					    			
					    			//MyHandlerThread.stop(ht);
					    			
					    			//mBackHandler.sendEmptyMessage(0);
					    			
					    			//Looper.loop();
					    			
				    				//handler.sendEmptyMessage(0);  // 작업이 끝나면 이 핸들러를 호출
									
								}
							});

			    		}
			    	});
		    	
		    	}
		    	else{
		    		//dialog.dismiss(); // 이게 프로그래스 바를 없애는 부분이다
		    		
		    		if(mgr.getActiveNetworkInfo() == null)
						Toast.makeText(MapTestActivity.this, R.string.network_connect,
				                Toast.LENGTH_SHORT).show();
		    	}
				
				return false;
			}
		});
		
		//리스트로 사진보기
		menu.add(0, 1, 0, R.string.map_menu_setting6).setIcon(R.drawable.menu_list).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				visi = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;
		        northeast = visi.northeast;
		        southwest = visi.southwest;
				
		        ArrayList<Cluster> tmpClusters = new ArrayList<Cluster>();
		        ArrayList<Cluster> tmpFBClusters = new ArrayList<Cluster>();
		        ArrayList<Cluster> tmpFvClusters = new ArrayList<Cluster>();
		        
				//현재 보이는 바운드 안의 대표사진 클러스터만 전송
		        if(localClustering != null){
			        for(Cluster tclu : localClustering.clusters){
			        	if(visi.contains(tclu.points.get(0).latlng))
			        		tmpClusters.add(tclu);
			        }
		        }
		        
		        if(facebookClustering != null){
			        for(Cluster tclu : facebookClustering.clusters){
			        	if(visi.contains(tclu.points.get(0).latlng))
			        		tmpFBClusters.add(tclu);
			        }
		        }
		        
		        if(favClustering != null){
			        for(Cluster tclu : favClustering.clusters){
			        	if(visi.contains(tclu.points.get(0).latlng))
			        		tmpFvClusters.add(tclu);
			        }
		        }
		        
		        //클러스터링 정보 저장
				applicationClass.localClu = new Clustering(tmpClusters);
				applicationClass.facebookClu = new Clustering(tmpFBClusters);
				applicationClass.favoriteClu = new Clustering(tmpFvClusters);
				
				// 전체보기 시 대표사진 저장
				applicationClass.allviewPhotos = allPicList;
				
				applicationClass.curLoc = visi;
				applicationClass.locCur = iCursor1;
				applicationClass.FacCur = facebookCursor;
				applicationClass.favCur = favCursor;
				
				Intent intent = new Intent(MapTestActivity.this, ListViewPhotosActivity.class);
				
				startActivity(intent);
	
				return false;
			}
		});

		//다른 사진 보기
//		SubMenu show = menu.addSubMenu(R.string.map_menu_setting1);
//		show.add(R.string.map_menu_setting2);
//		show.add(R.string.map_menu_setting3);
//		show.add(R.string.map_menu_setting4);
		
		//보기 메뉴
		menu.add(0, 3, 0, R.string.map_menu_setting1).setIcon(R.drawable.menu_view).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(MapTestActivity.this, OptionViewActivity.class);
				
				startActivity(intent);

				return false;
			}
			
		});
		return true;
	}
	
	private void loadData() {
//		runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				
				if(!isFinishing()) {
					//정보를 초기화하기 위하여 기존 DB 데이터 날려버리고 새로 받기
					//MyDBHelper.deleteDataOnDB(mHelper);
					UpdateAfterTime uat = new UpdateAfterTime();
					UpdateBeforeTime ubt = new UpdateBeforeTime();
					long updatedTime = pref.getLong("facebookLastUpdate", 0);
					ArrayList<CheckPublic> cp = new ArrayList<CheckPublic>();
					ArrayList<CheckPublic> cpAfter = new ArrayList<CheckPublic>();
					
					// 네트워크 함수 사용하여 내 페북 정보 가져와서 담기
					//최후 업뎃시간기준으로 그 이후에 페북에 업로드된 사진들을 가져와서 저장
					if(updatedTime != 0){
					
						ArrayList<String> cp_id = new ArrayList<String>();
						ArrayList<SPicture> fsp = new ArrayList<SPicture>();
						
						try {
							//sp = info.getmyPictures();
							sp = uat.AddAfterTime(updatedTime);
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						
						
						if(sp!=null && sp.size() >0){
							
							for(SPicture s : sp){
								if(s.isPublic == 1)
									fsp.add(s);
							}
							
							MyDBHelper.pushDataToDB(mHelper, sp);
						}
		
						//최후 업뎃시간 이전 데이터 업데이트 하기
						//우선 내 사진 모두를 가져옴
						facebookTmpCursor = db.query(MyDBHelper.DATABASENAME, null, SPicture.ISPUBLIC + " = 0 OR "+ SPicture.ISPUBLIC +" = 1", null, null, null, null);
						
						while(facebookTmpCursor.moveToNext()){
							cp.add(new CheckPublic(facebookTmpCursor.getString(facebookTmpCursor.getColumnIndex(SPicture.PICTUREID)),
									facebookTmpCursor.getInt(facebookTmpCursor.getColumnIndex(SPicture.ISPUBLIC))));
						}
						
						if(cp.size()>0){
							
							try {
								cpAfter = ubt.ModifyBeforeTime(cp);
							} catch (JSONException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
							
							//4: 삭제됨, 5: 공개 -> 비공개, 6: 비공개 -> 공개
							for(CheckPublic c : cpAfter){
								if(c.isPublic == 4){
									//DB에서 삭제
									MyDBHelper.deletePictureOnDB(mHelper, c.pictureID);
									//Log.w("FacebookDelete", "Delete!!!");
									cp_id.add(c.pictureID);
								}else if(c.isPublic == 5){
									//DB의 해당 row ispublic 을 0으로 변경
									MyDBHelper.modifyIsPublic(mHelper, c.pictureID, 0);
									//서버에서 삭제 해야할 데이터는 4로 통일
									//c.isPublic = 4;
									cp_id.add(c.pictureID);
									//Log.w("FacebookPrivate", "Private!!!");
								}else if(c.isPublic == 6){
									//DB의 해당 row ispublic 을 1으로 변경
									MyDBHelper.modifyIsPublic(mHelper, c.pictureID, 1);
									
									//서버에서는 추가 해야할 데이터는 1로 통일
									//c.isPublic = 1;
									//sp에 row 정보 추가하기!
									facebookTmpCursor = db.query(MyDBHelper.DATABASENAME, null, SPicture.PICTUREID + " = '" + c.pictureID +"'", null, null, null, null);
									
									if(facebookTmpCursor.getCount() > 0){
										facebookTmpCursor.moveToFirst();
										
										sp.add(new SPicture(c.pictureID, c.isPublic, 
												facebookTmpCursor.getDouble(facebookTmpCursor.getColumnIndex(SPicture.LATITUDE)),
												facebookTmpCursor.getDouble(facebookTmpCursor.getColumnIndex(SPicture.LONGITUDE)),
												facebookTmpCursor.getString(facebookTmpCursor.getColumnIndex(SPicture.COMMENTURL)),
												facebookTmpCursor.getString(facebookTmpCursor.getColumnIndex(SPicture.IMAGEURL)),
												facebookTmpCursor.getString(facebookTmpCursor.getColumnIndex(SPicture.SMALLIMAGEURL)),
												facebookTmpCursor.getLong(facebookTmpCursor.getColumnIndex(SPicture.MAKETIME)),
												facebookTmpCursor.getString(facebookTmpCursor.getColumnIndex(SPicture.MAKETIMES)),
												facebookTmpCursor.getString(facebookTmpCursor.getColumnIndex(SPicture.RECORD)),
												facebookTmpCursor.getString(facebookTmpCursor.getColumnIndex(SPicture.OWNERNAME)),
												facebookTmpCursor.getString(facebookTmpCursor.getColumnIndex(SPicture.OWNERID)),
												facebookTmpCursor.getString(facebookTmpCursor.getColumnIndex(SPicture.OWNERTHUMURL)),
												facebookTmpCursor.getString(facebookTmpCursor.getColumnIndex(SPicture.PLACENAME))
												));
									}
									//Log.w("FacebookPublic", "Public!!!");
								}
							}
						}
						
						for(String s : cp_id){
							Log.w("cp_id", s);
						}
						
						
						//sp 와 cpAfter의 ID 집합을 우리 서버로 보냄
						PostDeleteSever pds = new PostDeleteSever();
						
						try {
							if(fsp!=null && fsp.size() >0){
								pds.PostPhotos(fsp, 1);
							}
							if(cpAfter!=null && cpAfter.size() >0){
								pds.DeletePhotos(cp_id);
							}
						} catch (IOException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}
						
					}
					
					db = mHelper.getReadableDatabase();
					facebookCursor = db.query(MyDBHelper.DATABASENAME, null, null, null, null, null, null);
					
					//최후 업뎃 시간 업데이트
					SharedPreferences.Editor edit = pref.edit();
					
					edit.putLong("facebookLastUpdate", System.currentTimeMillis());
					
					edit.commit();
					
//					Log.w("handler", "call");
//					if(applicationClass.friendsData == null || applicationClass.friendsData.size() <= 0)
//						handler.sendEmptyMessage(0);  // 작업이 끝나면 이 핸들러를 호출
				}
//			}
//		});
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			//Log.w("handler", "call");
			if(applicationClass.selectedGroup != null){
				// 이전에 그렸던 마커를 모두 지우고
				for (Marker m : markers) {
					m.remove();
				}
	
				markers.clear();
	
				// 임시 마커도 있다면 지우고
				for (Marker m : tmpMarkers) {
					m.remove();
				}
				tmpMarkers.clear();
	
				// 선택한 것에 맞게 다시 그림
				reDrawThumnailMap();
				
				clusterClick = false;
				clickAllUserIdx = -1;
				
				imageAdapter.notifyDataSetChanged();
				
				applicationClass.isSelectOptionView = false;
			}

			if(msg.what != 1)
				dialog.dismiss(); // 이게 프로그래스 바를 없애는 부분이다
			
			if(msg.what == 2)
				MyHandlerThread.stop(ht);
			
//			if(mLoop != null)
//				mLoop.quit();
		}
	};
	
	
	
	@Override
    protected Dialog onCreateDialog(int id) {
		
		dialog = new ProgressDialog(this);
		dialog.setMessage("Please wait while loading...");
		dialog.setIndeterminate(true);
		//dialog.setCancelable(true);
		
		return dialog;
	}
    
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
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
	    iCursor1.close();
	    iCursor2.close();
	    iCursor3.close();
	    facebookCursor.close();
	    facebookTmpCursor.close();
	    favCursor.close();
	    
	    
	    if(applicationClass.locCur != null)
	    	applicationClass.locCur.close();
	    
	    if(applicationClass.FacCur != null)
	    	applicationClass.FacCur.close();
	    
	    db.close();
	    fdb.close();
	    mHelper.close();
	    favHelper.close();
	    uiHelper.onDestroy();
	    
        System.gc();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
	
	private void setUpMapIfNeeded() throws IOException {
		
		markers = new ArrayList<Marker>();
		tmpMarkers = new ArrayList<Marker>();
		
		cr = getContentResolver();
		iCursor1 = cr.query(Images.Media.EXTERNAL_CONTENT_URI, null, Images.Media.LATITUDE +"!=0 OR " + Images.Media.LATITUDE +"!=0", null, "LATITUDE desc, LONGITUDE desc");
		//iCursor1 = cr.query(Images.Media.EXTERNAL_CONTENT_URI, null, Images.Media.LATITUDE +"!=0 OR " + Images.Media.LATITUDE +"!=0", null, Images.Media.DATE_TAKEN + " asc");
		iCursor3 = iCursor2 = iCursor1;
		
		//페이스북 사진 로드를 위해서 커서 할당
		db = mHelper.getWritableDatabase();
		facebookTmpCursor = facebookCursor = db.query(MyDBHelper.DATABASENAME, null, null, null, null, null, null);
		
		fdb = favHelper.getWritableDatabase();
		favCursor = fdb.query(MyFavDBHelper.DATABASENAME, null, null, null, null, null, null);
		
		// Do a null check to confirm that we have not already instantiated the map.
		if (mGoogleMap == null) {        
			mGoogleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();        
			// Check if we were successful in obtaining the map.        
			if (mGoogleMap != null) {            
				// The Map is verified. It is now safe to manipulate the map.
				
				
				mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){

					@Override
					public boolean onMarkerClick(Marker arg0) {
						applicationClass.backKey = 0;
						Log.w("marker click", "!!!1");
						//해당 사진을 중심으로 해당사진 클릭할 때마다 카메라 살짝 줌
						//mGoogleMap.moveCamera(CameraUpdateFactory.zoomBy(1.0f));
						
						//mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(9.0f));
						
						//기본 이미지 리스트 갱신
            			//imageAdapter.notifyDataSetChanged();
						
						//곂치는 좌표의 이미지들을 하단의 리스트로 보여주기
						//marker.showInfoWindow();
						
						//다른 마커를 클릭한 상태였다면 클릭 전 상태로 다시 복원
						if(applicationClass.session != null && applicationClass.session.isOpened()
								 && mgr.getActiveNetworkInfo() != null){
							for(Marker m: tmpMarkers){
								m.remove();
							}
							tmpMarkers.clear();
						}else{
							//인터넷에 연결되어 있지 않다면, 로컬 마커만 지움
							for(Marker m: (ArrayList<Marker>)tmpMarkers.clone()){
								if(m.getSnippet().equals(LOCALNUM)){
									m.remove();
									tmpMarkers.remove(m);
								}
							}
						}
						
						//전체 사용자 보기가 아닌 경우
						if(clickAllUserIdx == -1 && !arg0.getSnippet().equals(ALLUSERNUM)){
							Log.w("marker click", "!!!2:"+clickClusterIdx);
							for(Marker m: markers){
								if(!m.getSnippet().equals(ALLUSERNUM) &&
										m.getTitle().split(":")[1].equals(clickClusterIdx+"")){
									m.setVisible(true);
									
									Log.w("marker click", "!!!3");
									break;
								}
							}
							
							if(clickAllUserIdx != 1)
								clickAllUserIdx = -1;
							
							clusterClick = true;
							clickClusterIdx = Integer.valueOf(arg0.getTitle().split(":")[1]);
						}else{
							
							if(clickAllUserIdx != -1){
								for(Marker m: markers){
									if(m.getSnippet().equals(ALLUSERNUM) &&
											m.getTitle().split(":")[1].equals(clickAllUserIdx+"")){
										m.setVisible(true);
										break;
									}
								}
							}
							
							if(clusterClick)
								clusterClick = false;
							
							clickAllUserIdx = Integer.valueOf(arg0.getTitle().split(":")[1]);
							clickMarker = true;
						}
						
						
						
						//클릭한 마커가 로컬 갤러리 사진인 경우
						if(arg0.getSnippet().equals(LOCALNUM)){
							iCursor3 = iCursor1;
							clustering = localClustering;
							markerfrom = 0;
							
						}
						//페이스북 사진인 경우
						else if(arg0.getSnippet().equals(FACEBOOKNUM)){
							finish_loading = true;
							iCursor3 = facebookCursor;
							clustering = facebookClustering;
							markerfrom = 1;
						}
						//라벨 사진일 경우
						else if(arg0.getSnippet().equals(LABELNUM)){
							finish_loading = true;
							iCursor3 = favCursor;
							clustering = favClustering;
							markerfrom = 1;
						}
						//전체 사용자 보기인 경우
						else if(arg0.getSnippet().equals(ALLUSERNUM)){
							
							if(!moveCamera){
								finish_loading = true;
								markerfrom = 3;
								
								// 서버로부터 하단 리스트 이미지 정보 갤러리 뷰에 받아와서 연결
								allPicClusterList.clear();
								
								tmrk = arg0;
								
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
													ht = new MyHandlerThread(MapTestActivity.this, "wait", "Please wait while loading...");
													ht.start();
													
													allPicClusterList = getPotos.GetClusteredPhotos(allPicList.get(Integer.valueOf(tmrk.getTitle().split(":")[1])).pictureID,
															visi.northeast.latitude, 
															visi.northeast.longitude, 
															visi.southwest.latitude, 
															visi.southwest.longitude);
													
													g.setSelection(0);
													imageAdapter.notifyDataSetChanged();
													
	//												long time = System.currentTimeMillis();
	//												
	//												while(allPicClusterList != null && allPicClusterList.size() == 0){
	//													Log.w("MapTestActivity", allPicClusterList+"");
	//													
	//													if(allPicClusterList == null)
	//														break;
	//													
	//													if(System.currentTimeMillis() - time > 30000)
	//														break;
	//												}
													
													MyHandlerThread.stop(ht);
												}
									    	});
							    		}
						    		});
								}else
									allPicClusterList.clear();
								
								arg0.showInfoWindow();
							}
							
							return true;
						}
						
						g.setSelection(0);
						
//						if(!arg0.getSnippet().equals(ALLUSERNUM))
//							g.setSelection(clustering.clusters.get(clickClusterIdx).cluCenterIdx);
						
						arg0.showInfoWindow();
						
						if(!arg0.getSnippet().equals(ALLUSERNUM))
							imageAdapter.notifyDataSetChanged();
						//g.focusSearch(View.FOCUS_DOWN);
						
						return true;
					}
					
				});
				
				mGoogleMap.setMyLocationEnabled(true);
				mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
				mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
				mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);
				
				if(location != null){
					
					mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 10.0f));
					Log.i(ACTIVITY_SERVICE, "Latitude:" + location.getLatitude() + " Longtitude: " + location.getLongitude());
				}
				else{
					location = mGoogleMap.getMyLocation();
					
					if(location != null){
						mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 10.0f));
						Log.i(ACTIVITY_SERVICE, "Latitude:" + location.getLatitude() + " Longtitude: " + location.getLongitude());
					}
				}
				
				visi = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;
		        northeast = visi.northeast;
		        southwest = visi.southwest;
				
				//인포 윈도우 꾸미기
				mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter(){

					@Override
					public View getInfoContents(Marker arg0) {
						
					Log.w("Test", "getInfoWindowContents function call!");
						
						// Getting view from the layout file info_window
						View view = getLayoutInflater().inflate(R.layout.info_window, null);
						//String parsePosition;
						
						//로컬 갤러리 사진인가?
						if(arg0.getSnippet().equals("0")){
							
							iCursor1.moveToPosition(Integer.valueOf(arg0.getTitle().split(":")[0]));
							
							Bitmap bm = Images.Thumbnails.getThumbnail(cr, iCursor1.getInt(iCursor1.getColumnIndex(BaseColumns._ID)), Images.Thumbnails.MICRO_KIND, null);
							
							
							//다음 두 줄은 이미지가 똑바로 안보일 때 해당 이미지를 회전하는 코드
				        	int degree = getExifOrientation(iCursor1.getString(iCursor1.getColumnIndex(ImageColumns.DATA)));
				        	bm = getRotatedBitmap(bm, degree);
							
				        	
							//bm = Bitmap.createScaledBitmap(bm, bm.getWidth()*4/3, bm.getHeight()*4/3, true);
							//bm = Bitmap.createScaledBitmap(bm, applicationClass.thumWidth*4/3, applicationClass.thumWidth*4/3, true);
							
							
							TextView title = (TextView) view.findViewById(R.id.window_title);
							title.setText("Title: " + iCursor1.getString(iCursor1.getColumnIndex(ImageColumns.TITLE)));
							
							ImageView iv = (ImageView) view.findViewById(R.id.window_image);
							
							iv.setImageBitmap(bm);
							
							
							
	//						Bitmap output = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
	//						Canvas canvas = new Canvas(output);
	//						
	//						canvas.drawARGB(0, 255, 255, 255);
	//						view.draw(canvas);
						}
						//페이스북 사진인가? 또는 전체 사용자 보기 사진인가? 또는 라벨 사진인가?
						else if(arg0.getSnippet().equals("1") || arg0.getSnippet().equals("3") || arg0.getSnippet().equals(LABELNUM)){
							
							//TextView title = (TextView) view.findViewById(R.id.window_title);
							//title.setText(facebookCursor.getString(facebookCursor.getColumnIndex(SPicture.RECORD)));
							
							TextView userName = (TextView) view.findViewById(R.id.user_name);
							ImageView iv = (ImageView) view.findViewById(R.id.window_image);
							
							String urli, urlt;
							
							if(arg0.getSnippet().equals("1") || arg0.getSnippet().equals(LABELNUM)){
								iCursor3.moveToPosition(Integer.valueOf(arg0.getTitle().split(":")[0]));
								
								userName.setText(iCursor3.getString(iCursor3.getColumnIndex(SPicture.OWNERNAME)));
																				
								urli = iCursor3.getString(iCursor3.getColumnIndex(SPicture.SMALLIMAGEURL));
								urlt = iCursor3.getString(iCursor3.getColumnIndex(SPicture.OWNERTHUMURL));
							}
							else{
								
								if(allPicClusterList == null)
									return view;
								
								Log.w("MapTestActivity", "waiting...");
								
								int idx = Integer.valueOf(arg0.getTitle().split(":")[0]);
								
								if(allPicClusterList.size() > 0 && idx != 0 && idx < allPicClusterList.size()){
									userName.setText(allPicClusterList.get(idx).ownerName);
									
									urli = allPicClusterList.get(idx).smallImageURL;
									urlt = allPicClusterList.get(idx).ownerThumURL;
								}
								else{
									idx = Integer.valueOf(arg0.getTitle().split(":")[1]);
									
									userName.setText(allPicList.get(idx).ownerName);
									
									urli = allPicList.get(idx).smallImageURL;
									urlt = allPicList.get(idx).ownerThumURL;
								}
									
								
								
							}
							
							if(tmpStr.equals("finish")){
								tmpStr="";
								iv.setImageBitmap(infoWBmp);
								
								infoWBmp = null;
								infothumBmp = null;
								
								if(preTask != null)
									preTask.cancel(true);
								
								if(preTaskThum != null)
									preTaskThum.cancel(true);
								
								return view;
							}
							
							
//			    			ImageTag tag = imageTagFactory.build(url);
//			            	iv.setTag(tag);
			                //이미지 로더에 로드
//			                imageLoader.load(iv);
							
							//유저 썸네일 이미지 불러오기
							ImageView userThumnail = (ImageView) view.findViewById(R.id.user_thumnail);
							//iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
							if(finish_loading == null || applicationClass.session == null ||
									!applicationClass.session.isOpened()){
								iv.setImageResource(R.drawable.noimage);
							}
							else if(mgr.getActiveNetworkInfo() == null){
								iv.setImageResource(R.drawable.noimage);
								Toast.makeText(MapTestActivity.this, R.string.network_connect,
						                Toast.LENGTH_SHORT).show();
							}
							else if(finish_loading || (infoWBmp == null && infothumBmp == null)){
								
								if(preTask != null && preTaskThum != null){
									preTask.cancel(true);
									preTaskThum.cancel(true);
								}
								
								iv.setImageResource(R.drawable.noimage);
								
								preTask = new BitmapDownloaderTaskInfo(iv, arg0, INFO_WINDOW);
								preTask.execute(urli);
								preTaskThum = new BitmapDownloaderTaskInfo(userThumnail, arg0, USER_THUM);
								preTaskThum.execute(urlt);
							}
							else if(infoWBmp != null){
								iv.setImageBitmap(infoWBmp);
								//infoWBmp = null;
								if(infothumBmp != null){
									userThumnail.setImageBitmap(infothumBmp);
									
									preTask.cancel(true);
									preTaskThum.cancel(true);
									
									infoWBmp = null;
									infothumBmp = null;
								}
							}
							else{
								iv.setImageResource(R.drawable.noimage);
								userThumnail.setImageBitmap(infothumBmp);
								
								//Log.w("load Bitmap", "Load!" + finish_loading);
							}
							
							
//							if(infoWBmp == null && infothumBmp == null){
//								iv.setImageResource(R.drawable.ic_launcher);
//								
//								BitmapDownloaderTaskInfo task = new BitmapDownloaderTaskInfo(iv, arg0, INFO_WINDOW);
//								task.execute(urli);
//								
//								BitmapDownloaderTaskInfo task_thum = new BitmapDownloaderTaskInfo(userThumnail, arg0, USER_THUM);
//								task_thum.execute(urlt);
//							}
//							else if(infoWBmp != null){
//								iv.setImageBitmap(infoWBmp);
//								//infoWBmp = null;
//								if(infothumBmp != null){
//									userThumnail.setImageBitmap(infothumBmp);
//									infoWBmp = null;
//									infothumBmp = null;
//								}
//							}
//							else{
//								iv.setImageResource(R.drawable.ic_launcher);
//								userThumnail.setImageBitmap(infothumBmp);
//							}
							
							
							
						}
						
						
						return view;
					}

					@Override
					public View getInfoWindow(Marker marker) {
						return null;
					}
					
				});
				mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){

					@Override
					public void onMapClick(LatLng point) {
						
						applicationClass.backKey = 1;
						
						Log.i(ACTIVITY_SERVICE, "map click!*************");
						if(preTask != null && preTaskThum != null){
							preTask.cancel(true);
							preTaskThum.cancel(true);
						}
						
						if(clusterClick){
							clusterClick = false;
							
							for(Marker m: markers){
								if(!m.getSnippet().equals(ALLUSERNUM) && 
										m.getTitle().split(":")[1].equals(clickClusterIdx+"") &&
									clustering != null && clustering.from == Integer.valueOf(m.getSnippet())){
									
									m.setVisible(true);
									m.hideInfoWindow();
									break;
								}
							}
						}
						
						if(clickAllUserIdx != -1){
							for(Marker m: markers){
								if(m.getSnippet().equals(ALLUSERNUM) && 
										m.getTitle().split(":")[1].equals(clickAllUserIdx+"")){
									m.setVisible(true);
									m.hideInfoWindow();
									break;
								}
							}
							
							clickAllUserIdx = -1;
						}
						
						//다른 마커를 클릭한 상태였다면 클릭 전 상태로 다시 복원
						if(applicationClass.session != null && applicationClass.session.isOpened()
								 && mgr.getActiveNetworkInfo() != null)
						{
							for(Marker m: tmpMarkers){
								m.remove();
							}
							tmpMarkers.clear();
						}else{
							//인터넷에 연결되어 있지 않다면, 로컬 마커만 지움
							for(Marker m: (ArrayList<Marker>)tmpMarkers.clone()){
								if(m.getSnippet().equals(LOCALNUM)){
									m.remove();
									tmpMarkers.remove(m);
								}
							}
						}
						
						imageAdapter.notifyDataSetChanged();
					}
					
				});
				
				mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener(){

					@Override
					public void onInfoWindowClick(Marker arg0) {
						
						//로컬갤러리 사진인가?
						if(arg0.getSnippet().equals("0")){
							//사진 확대하여 보여주기
							iCursor1.moveToPosition(Integer.valueOf(arg0.getTitle().split(":")[0]));
							
							String path = iCursor1.getString(iCursor1.getColumnIndex(ImageColumns.DATA));
			
							//로컬갤러리의 경우
							//기존의 안드로이드 상의 이미지 뷰어를 통해 보여줌
//							Intent intent = new Intent();
//							intent.setAction(Intent.ACTION_VIEW);
//							Uri uri = Uri.fromFile(new File(path));
//							intent.setDataAndType(uri, "image/*");
							//intent.setData(uri);
//							startActivity(intent);	
							
							Intent intent = new Intent(MapTestActivity.this, InfoWinfullLocalActivity.class);
							
							intent.putExtra("path", path);
							intent.putExtra("from", "MapTest");
							intent.putExtra("lat", iCursor1.getDouble(iCursor1.getColumnIndex(ImageColumns.LATITUDE)));
							intent.putExtra("lng", iCursor1.getDouble(iCursor1.getColumnIndex(ImageColumns.LONGITUDE)));
							
							startActivity(intent);	
						}
						//페이스북 사진인가? 또는 전체보기 사진인가? 또는 라벨 사진인가?
						else if(arg0.getSnippet().equals("1") || arg0.getSnippet().equals("3") || arg0.getSnippet().equals(LABELNUM)){
							// 대화상자 나오게 하여 폴더로 담기, 쿨, 댓글 달러 가기 표시
							String path;
							Intent intent = new Intent(MapTestActivity.this, InfowinfullActivity.class);
							
							
							
							if(arg0.getSnippet().equals("1") || arg0.getSnippet().equals(LABELNUM)){
								iCursor3.moveToPosition(Integer.valueOf(arg0.getTitle().split(":")[0]));
								
								path = iCursor3.getString(iCursor3.getColumnIndex(SPicture.IMAGEURL));
								
								intent.putExtra("path", path);
								
								// 페북 사진의 경우
								// 풀 이미지를 새로운 액티비티로 보여줌
								
								intent.putExtra("comment", iCursor3.getString(iCursor3.getColumnIndex(SPicture.COMMENTURL)));
								intent.putExtra("record", iCursor3.getString(iCursor3.getColumnIndex(SPicture.RECORD)));
								intent.putExtra("ownerName", iCursor3.getString(iCursor3.getColumnIndex(SPicture.OWNERNAME)));
								intent.putExtra("ownerThum", iCursor3.getString(iCursor3.getColumnIndex(SPicture.OWNERTHUMURL)));
								intent.putExtra("picId", iCursor3.getString(iCursor3.getColumnIndex(SPicture.PICTUREID)));
							}
							//전체보기 사진의 경우
							else{
								int idx = Integer.valueOf(arg0.getTitle().split(":")[0]);
								path = allPicClusterList.get(idx).imageURL;
								
								intent.putExtra("path", path);
								
								intent.putExtra("comment", allPicClusterList.get(idx).commentURL);
								intent.putExtra("record", allPicClusterList.get(idx).record);
								intent.putExtra("ownerName", allPicClusterList.get(idx).ownerName);
								intent.putExtra("ownerThum", allPicClusterList.get(idx).ownerThumURL);
								
								applicationClass.tmpSPicture = allPicClusterList.get(idx);
							}
							
							intent.putExtra("what", arg0.getSnippet());
							intent.putExtra("from", "MapTest");
							//Log.w("record", facebookCursor.getString(facebookCursor.getColumnIndex(SPicture.RECORD)));
							
							startActivity(intent);
						}
						
					}
					
				});
				
				 
				mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener(){

					@Override
					public void onCameraChange(CameraPosition arg0) {
						//applicationClass.backKey = 0;
						
						Log.w("CameraChange", "change");
						
						visi = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;
				        northeast = visi.northeast;
				        southwest = visi.southwest;
						
				        //카메라 체인지 되면 무조건 서버에서 사진 받아옴!!!
				        
				        cerZoom = arg0.zoom;
					
//						줌 했을 경우만 다시 그림 NO! -> 항상 다시 그림
				        //전체보기인 경우 줌 했을 시
					    if(applicationClass.selectedGroup == null || 
					    		applicationClass.selectedGroup[4] != 1 ||
					    		(cerZoom != preZoom && applicationClass.selectedGroup[4] == 1)){
					    	
					    	//사진 로드중인 스레드 모두 cancel
					    	if(allViewTask != null){
								allViewTask.cancel(true);
								allViewTask = null;
							}
					    	
					    	
					    	for(BitmapDownloaderTask b : facebookMarkerTasks){
					    		b.cancel(false);
					    	}
					    	facebookMarkerTasks.clear();
					    	
							for(Marker m: markers){
								m.remove();
							}
							markers.clear();
							
							if(clusterClick)
								clusterClick = false;
							
							if(clickAllUserIdx != -1)
								clickAllUserIdx = -1;
							
							//다른 마커를 클릭한 상태였다면 클릭 전 상태로 다시 복원
							for(Marker m: tmpMarkers){
								m.remove();
							}
							tmpMarkers.clear();
						
//							for(Marker m: markers){
//								if(m.getTitle().split(":")[1].equals(clickClusterIdx+""))
//									m.setVisible(true);
//							}
							
							reDrawThumnailMap();
					    }
					    //전체 보기 일 때 카메라만 이동시
					    else if(cerZoom == preZoom && applicationClass.selectedGroup!=null && applicationClass.selectedGroup[4] == 1){
					    	moveCamera = true;
					    	
					    	//사진 로드중인 스레드 모두 cancel
					    	if(allViewTask != null){
								allViewTask.cancel(true);
								allViewTask = null;
							}
					    	
					    	
					    	
//					    	if(clickAllUserIdx != -1)
//					    		clickAllUserIdx = -1;
//					    	
					    	for(BitmapDownloaderTask b : facebookMarkerTasks){
					    		b.cancel(false);
					    	}
					    	facebookMarkerTasks.clear();
					    	
					    	reDrawThumnailMap();
					    }
				       
				        //Log.i(ACTIVITY_SERVICE, "northwest location: "+visi.northeast + ", " +visi.southwest +"******************");
					    
					    //해당 대표 사진의 인포 윈도 꺼져 있을 때만 전체 리스트 그림
					    if(!clusterClick && clickAllUserIdx == -1){
				        	
//					        if(visi.northeast.longitude < 0 && visi.southwest.longitude > 0){
//					        	iCursor2 = cr.query(Images.Media.EXTERNAL_CONTENT_URI, null, "( " + Images.Media.LATITUDE +" !=0 OR " + Images.Media.LONGITUDE +"!=0 )" + " AND "
//									+ Images.Media.LATITUDE+" >= "+southwest.latitude + " AND ( "
//									+ Images.Media.LONGITUDE+" >= "+southwest.longitude + " OR "
//									+ Images.Media.LONGITUDE+" <= "+northeast.longitude + " ) AND "
//									+ Images.Media.LATITUDE+" <= "+northeast.latitude
//									, null, "LATITUDE desc, LONGITUDE desc");
//					        }
//					        else{
//					        	iCursor2 = cr.query(Images.Media.EXTERNAL_CONTENT_URI, null, "( " + Images.Media.LATITUDE +" !=0 OR " + Images.Media.LONGITUDE +"!=0 )" + " AND "
//									+ Images.Media.LATITUDE+" >= "+southwest.latitude + " AND "
//									+ Images.Media.LONGITUDE+" >= "+southwest.longitude + " AND "
//									+ Images.Media.LATITUDE+" <= "+northeast.latitude + " AND "
//									+ Images.Media.LONGITUDE+" <= "+northeast.longitude, null, "LATITUDE desc, LONGITUDE desc");
//					        }
//							
				        	//다음 조건으로 전체 리스트 아예 그리지 않음
//				        	iCursor2 = cr.query(Images.Media.EXTERNAL_CONTENT_URI, null, Images.Media.LATITUDE +" =0 AND " + Images.Media.LATITUDE +"!=0", null, "LATITUDE desc, LONGITUDE desc");
//				        	
//				        	iCursor2.moveToFirst();
							
							imageAdapter.notifyDataSetChanged();
				        }
				        
						preZoom = cerZoom;
					}
					
				});
				
				
				//drawGallaryThumnailMap();
			}    
		}
		
//		startManagingCursor(iCursor1);
//		startManagingCursor(iCursor2);
//		startManagingCursor(iCursor3);
//		startManagingCursor(facebookCursor);
//		startManagingCursor(facebookTmpCursor);
	}
	
	private void reDrawThumnailMap(){
		//내 로컬 사진 보기 선택 했거나 맨 처음 초기 화면 일때
		if(applicationClass.selectedGroup == null)
			reDrawGallaryThumnailMap();
		else{
			if(applicationClass.selectedGroup[0]==1)
				reDrawGallaryThumnailMap();
		
			//내 페이스북 사진 보기 선택시
			if(applicationClass.selectedGroup[1]==1 || applicationClass.selectedGroup[2]==1)
				drawFacebookThumnailMap();
				
			//라벨 별 보기 선택 시
			if(applicationClass.selectedGroup[3]==1)
				drawLabelThumnailMap();
			
			//전체 보기 선택 시
			if(applicationClass.selectedGroup[4]==1)
				drawAllUserTumnailMap();
		}
	}
	
	private void reDrawGallaryThumnailMap(){
		Bitmap bit;
		BitmapDescriptor bitd;
		String imageTitle;
		CluPoint p;
		int clusterIdx = -1;
		
		//double delta = -visi.southwest.longitude;
		
		//만약 경도 360, -360을 화면에 포함하고 있을때
		if(visi.southwest.longitude > 0 && visi.northeast.longitude < 0){
//			iCursor1 = cr.query(Images.Media.EXTERNAL_CONTENT_URI, null, Images.Media.LONGITUDE + "+" + delta + " >= " +(visi.southwest.longitude + delta) + " AND "
//																	+ Images.Media.LONGITUDE +"+"  + delta + " <= " +(visi.northeast.longitude + delta) + " AND "
//																	+ Images.Media.LATITUDE + " >= " + visi.southwest.latitude + " AND " + Images.Media.LATITUDE + " <= " + visi.northeast.latitude, null, "LATITUDE desc, LONGITUDE desc");
			
			iCursor1 = cr.query(Images.Media.EXTERNAL_CONTENT_URI, null, "( " + Images.Media.LATITUDE +" !=0 OR " + Images.Media.LATITUDE +" !=0 ) AND ( " 
					+ Images.Media.LONGITUDE + " >= " + visi.southwest.longitude + " OR "
					+ Images.Media.LONGITUDE + " <= " + visi.northeast.longitude + ") AND "
					+ Images.Media.LATITUDE + " >= " + visi.southwest.latitude + " AND "
					+ Images.Media.LATITUDE + " <= " + visi.northeast.latitude, null, "LATITUDE desc, LONGITUDE desc");
			
		}else{
			iCursor1 = cr.query(Images.Media.EXTERNAL_CONTENT_URI, null, "( " + Images.Media.LATITUDE +" !=0 OR " + Images.Media.LATITUDE +" !=0 ) AND "
					+ Images.Media.LONGITUDE + " >= " + visi.southwest.longitude + " AND "
					+ Images.Media.LONGITUDE + " <= " + visi.northeast.longitude + " AND "
					+ Images.Media.LATITUDE + " >= " + visi.southwest.latitude + " AND "
					+ Images.Media.LATITUDE + " <= " + visi.northeast.latitude, null, "LATITUDE desc, LONGITUDE desc");
		}
		
		if(iCursor1.getCount()!=0){
			//iCursor3은 위치정보있는 모든 사진을 가리키고 있음
			localClustering = new Clustering(iCursor1, Math.abs(northeast.longitude - southwest.longitude)/10, 0);
			
			
			localClustering.kMedoids();
        
		
	        for(Cluster cc : localClustering.clusters){
	        	clusterIdx++;
	        	
	        	p = cc.points.get(cc.cluCenterIdx);
	        	//p = cc.points.get(0);
	        	
	        	iCursor1.moveToPosition(p.cursorPosition);
	        	
	        	bit = Images.Thumbnails.getThumbnail(cr, iCursor1.getInt(iCursor1.getColumnIndex(BaseColumns._ID)), Images.Thumbnails.MICRO_KIND, null);
	        		        	        	
	        	bit = Bitmap.createScaledBitmap(bit, applicationClass.thumWidth * 2/3, applicationClass.thumWidth * 2/3, true);
	        	
	        	
	        	
	        	//다음 두 줄은 이미지가 똑바로 안보일 때 해당 이미지를 회전하는 코드
	        	int degree = getExifOrientation(iCursor1.getString(iCursor1.getColumnIndex(ImageColumns.DATA)));
	        	bit = getRotatedBitmap(bit, degree);
				
	        	
	        	
	        	bit = getFramedBitmap(bit, Color.WHITE);
				
				bitd = BitmapDescriptorFactory.fromBitmap(bit);
				//bitd = BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher);
				
				//해당 점의 커서 포지션을 이미지 타이틀에 저장
				//imageTitle = iCursor3.getString(iCursor3.getColumnIndex(ImageColumns.DATE_TAKEN));
				imageTitle = p.cursorPosition + ":" + clusterIdx;
				
				markers.add(mGoogleMap.addMarker(new MarkerOptions().position(p.latlng).title(imageTitle).icon(bitd).snippet(LOCALNUM)));
				
				//Log.i(ACTIVITY_SERVICE, p.cursorPosition + "***" + cc.points.size());
	        }
		}
		
//		iCursor2 = cr.query(Images.Media.EXTERNAL_CONTENT_URI, null, Images.Media.TITLE + " > 'IMG044'", null, null);
//		
//		while(iCursor2.moveToNext()){
//			Log.w("DB Test", iCursor2.getString(iCursor2.getColumnIndex(ImageColumns.TITLE)));
//		}
	}
	
	//현재 보이는 화면과 대응되는  지도 영역 안의 사진들만 지도에 띄움 
	private void drawGallaryThumnailMap() throws IOException{
		
		Bitmap bit;
		BitmapDescriptor bitd;
		String imageTitle;
		double imageLatitude;
		double imageLongitude;
		//String[] proj = {Images.Media._ID, Images.Media.DATA, Images.Media.DISPLAY_NAME, Images.Media.SIZE};

		
		
//		cr = getContentResolver();		
//		
//		//LONGITUDE가 큰 순으로 정렬
//		iCursor1 = cr.query(Images.Media.EXTERNAL_CONTENT_URI, null, Images.Media.LATITUDE +"!=0 OR " + Images.Media.LATITUDE +"!=0", null, "LATITUDE desc, LONGITUDE desc");
//		iCursor3 = iCursor1;
		
		
//		bit = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
//		bitd = BitmapDescriptorFactory.fromBitmap(bit);
//		Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(40.66417, -73.93861)).title("hahaha").snippet("Population: 776733").icon(bitd));
//		marker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(38.895, -77.03667)).title("hahaha").snippet("Population: 776733").icon(bitd));
//		marker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(38.895, -77.03667)).title("hahaha").snippet("Population: 776733").icon(bitd));
		
//		ContentValues values = new ContentValues();
//		values.put("LATITUDE", 38.895);
//		values.put("LONGITUDE", -77.03667);
		//values.put("LATITUDE", 36.788013);
		//values.put("LONGITUDE", 126.144204);
		
		//Log.i(ALARM_SERVICE, iCursor.getCount()+"");
		
		//좌표가 0인 사진이 나올때까지만 탐색 
		while(iCursor1.moveToNext()){
			
			imageLatitude = iCursor1.getDouble(iCursor1.getColumnIndex(ImageColumns.LATITUDE));
			imageLongitude = iCursor1.getDouble(iCursor1.getColumnIndex(ImageColumns.LONGITUDE));
			
			
			//임시 테스트
//			String str = iCursor1.getString(iCursor1.getColumnIndex(ImageColumns.TITLE));
//			
//			if(str.equals("IMG065") || str.equals("IMG072") || str.equals("IMG076")){
//				Log.i(ACTIVITY_SERVICE, str + ": lat:" + imageLatitude + "lng: " + imageLongitude);
//			}
			
			
				//bit = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
				
				bit = Images.Thumbnails.getThumbnail(cr, iCursor1.getInt(iCursor1.getColumnIndex(BaseColumns._ID)), Images.Thumbnails.MICRO_KIND, null);
				
				//bit = getRoundedBitmap(bit);
				
				bit = Bitmap.createScaledBitmap(bit, bit.getWidth()*2/3, bit.getHeight()*2/3, true);
				
				bit = getFramedBitmap(bit, Color.WHITE);
				
				bitd = BitmapDescriptorFactory.fromBitmap(bit);
				
				
				//imageTitle = iCursor.getString(iCursor.getColumnIndex(MediaColumns.TITLE));
				// 타이틀에 찍은 시간을 저장
				imageTitle = iCursor1.getString(iCursor1.getColumnIndex(ImageColumns.DATE_TAKEN));
				
				//snippet: 내 갤러리 사진인 경우에는 0, 페이스북인 경우에는 1
				markers.add(mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(imageLatitude, imageLongitude)).title(imageTitle).icon(bitd).snippet("0")));
				//mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(imageLatitude, imageLongitude)).title(imageTitle).icon(bitd).snippet("0"));
				
				//Log.i(ALARM_SERVICE, ">>>>>>>>>>>>>"+imageLatitude+":"+imageLongitude+"<<<<<<<<<<<<<<<<<<<<<<<<");
		}
		
		
		
		// 임시 테스트
//		LatLngBounds test = new LatLngBounds(new LatLng(-2, -2), new LatLng(2, 2));
//		
//		if(test.contains(new LatLng(-2, -2))){
//			Log.i(ACTIVITY_SERVICE, "bound!!!!!!!!!!!!!!");
//		}
		
		
		//mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(91, 0)));
		
		// 이미지의 위치정보 수정: 도 -> 도분초로 바꾼뒤 적용해 줘야함, 그리고 exif 테그 변환 후 media 디비도 업데이트 해줘야함
		// 아래 함수 참소
//		iCursor1 = cr.query(Images.Media.EXTERNAL_CONTENT_URI, null, Images.Media.TITLE +"='IMG044'", null, null);
//		iCursor1.moveToFirst();

//		String path = iCursor1.getString(iCursor1.getColumnIndex(ImageColumns.DATA));
//		
//		ExifInterface ei = new ExifInterface(path);
//		
//		//만리포의 좌표
//		ei.setAttribute(ExifInterface.TAG_GPS_LATITUDE, latlocConvert(36.788013));
//		ei.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, latlocConvert(126.144204));
//		ei.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "North Latitude");
//		ei.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "East Longitude");
//		ei.saveAttributes();
//
//		Log.i(ACTIVITY_SERVICE, "lat:" + ei.getAttribute(ExifInterface.TAG_GPS_LATITUDE) + "lng: " + ei.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
		
		
		//사진의 LONGITUDE가 100이상인 사진의 위치정보를 워싱턴으로 바꾸기
		//cr.update(Images.Media.EXTERNAL_CONTENT_URI, values, Images.Media.LONGITUDE+" > 100", null);
//		cr.update(Images.Media.EXTERNAL_CONTENT_URI, values, Images.Media.TITLE+"='IMG044'", null);
//		cr.update(Images.Media.EXTERNAL_CONTENT_URI, values, Images.Media.TITLE+"='IMG043'", null);
//		cr.update(Images.Media.EXTERNAL_CONTENT_URI, values, Images.Media.TITLE+"='IMG042'", null);
//		cr.update(Images.Media.EXTERNAL_CONTENT_URI, values, Images.Media.TITLE+"='IMG041'", null);
//		cr.update(Images.Media.EXTERNAL_CONTENT_URI, values, Images.Media.TITLE+"='IMG040'", null);
//		cr.update(Images.Media.EXTERNAL_CONTENT_URI, values, Images.Media.TITLE+"='IMG039'", null);
//		cr.update(Images.Media.EXTERNAL_CONTENT_URI, values, Images.Media.TITLE+"='IMG013'", null);
		
//		startManagingCursor(iCursor1);
//		startManagingCursor(iCursor3);
	}
	
	private void drawFacebookThumnailMap(){
		int clusterIdx = -1;
		
		//Bitmap bit;
		BitmapDescriptor bitd;
		String imageTitle;
		CluPoint p;
		
		db = mHelper.getReadableDatabase();
		
		String selectionAdd;
		
		//만약 경도 360, -360을 화면에 포함하고 있을때
		if(visi.southwest.longitude > 0 && visi.northeast.longitude < 0)
			selectionAdd = "( " + SPicture.LONGITUDE + " >= " + visi.southwest.longitude + " OR "
					+ SPicture.LONGITUDE + " <= " + visi.northeast.longitude + ") AND "
					+ SPicture.LATITUDE + " >= " + visi.southwest.latitude + " AND "
					+ SPicture.LATITUDE + " <= " + visi.northeast.latitude;
		else
			selectionAdd = SPicture.LONGITUDE + " >= " + visi.southwest.longitude + " AND "
					+ SPicture.LONGITUDE + " <= " + visi.northeast.longitude + " AND "
					+ SPicture.LATITUDE + " >= " + visi.southwest.latitude + " AND "
					+ SPicture.LATITUDE + " <= " + visi.northeast.latitude;
		
		//선택한 항목에 따라 DB에서 알맞는 정보를 가져옴
		if(applicationClass.selectedGroup[FACEBOOKVAL]==1 && applicationClass.selectedGroup[FACEBOOKFRIENDS]==1){
			facebookCursor = db.query(MyDBHelper.DATABASENAME, null, " ( " + SPicture.ISPUBLIC + " != " + FACEBOOKFRIENDS + " OR " + seletionStr + " ) AND " + selectionAdd, null, null, null, null);
		}else if(applicationClass.selectedGroup[FACEBOOKVAL]==1){
			facebookCursor = db.query(MyDBHelper.DATABASENAME, null, SPicture.ISPUBLIC + " != " + FACEBOOKFRIENDS + " AND " + selectionAdd, null, null, null, null);
		}else{
			facebookCursor = db.query(MyDBHelper.DATABASENAME, null, " ( " + seletionStr + " ) AND " + selectionAdd, null, null, null, null);
		}
		
		if(facebookCursor.getCount()!=0){
			facebookClustering = new Clustering(facebookCursor, Math.abs(northeast.longitude - southwest.longitude)/10, 1);
			
			facebookClustering.kMedoids();
			
			//Log.w("fffff", "dfsdfsdfsdff");
			
			for(Cluster cc : facebookClustering.clusters){
	        	clusterIdx++;
	        	
	        	//네트워크 스레드 사용하여 마커 그리기
	        	p = cc.points.get(cc.cluCenterIdx);
	        	
	        	facebookCursor.moveToPosition(p.cursorPosition);
	        	
	        	//bit = Bitmap.createScaledBitmap(bit, thumWidth, thumHeight, true);
	        	//bit = getFramedBitmap(bit, Color.BLUE);
	        	
	        	//bitd = BitmapDescriptorFactory.fromBitmap(bit);
	        	bitd = BitmapDescriptorFactory.fromResource(R.drawable.noimage);
	        	
	        	//해당 점의 커서 포지션을 이미지 타이틀에 저장
				//imageTitle = iCursor3.getString(iCursor3.getColumnIndex(ImageColumns.DATE_TAKEN));
				imageTitle = p.cursorPosition + ":" + clusterIdx;

				//일단 tmpMarkers에 저장해두고
				Marker m = mGoogleMap.addMarker(new MarkerOptions().position(p.latlng).title(imageTitle).icon(bitd).snippet(FACEBOOKNUM));
				tmpMarkers.add(m);
				
				if(applicationClass.session != null &&
						applicationClass.session.isOpened() && mgr.getActiveNetworkInfo() != null){
				
					//Log.w("session", applicationClass.session+"");
					
					BitmapDownloaderTask task = new BitmapDownloaderTask(m, 0);
					task.execute(facebookCursor.getString(facebookCursor.getColumnIndex(SPicture.SMALLIMAGEURL)));
					facebookMarkerTasks.add(task);
				}
			}
		}
		
		//Log.w("fffff", "dfsdfsdfsdff");
		
	}
	
	private void drawLabelThumnailMap(){
		int clusterIdx = -1;
		
		//Bitmap bit;
		BitmapDescriptor bitd;
		String imageTitle;
		CluPoint p;
		
		fdb = favHelper.getReadableDatabase();
		
		//만약 경도 360, -360을 화면에 포함하고 있을때
		if(visi.southwest.longitude > 0 && visi.northeast.longitude < 0){
//					iCursor1 = cr.query(Images.Media.EXTERNAL_CONTENT_URI, null, Images.Media.LONGITUDE + "+" + delta + " >= " +(visi.southwest.longitude + delta) + " AND "
//																			+ Images.Media.LONGITUDE +"+"  + delta + " <= " +(visi.northeast.longitude + delta) + " AND "
//																			+ Images.Media.LATITUDE + " >= " + visi.southwest.latitude + " AND " + Images.Media.LATITUDE + " <= " + visi.northeast.latitude, null, "LATITUDE desc, LONGITUDE desc");
			favCursor = fdb.query(MyFavDBHelper.DATABASENAME, null, "( " + SPicture.LONGITUDE + " >= " + visi.southwest.longitude + " OR "
					+ SPicture.LONGITUDE + " <= " + visi.northeast.longitude + ") AND "
					+ SPicture.LATITUDE + " >= " + visi.southwest.latitude + " AND "
					+ SPicture.LATITUDE + " <= " + visi.northeast.latitude, null, null, null, null);
			
		}else{
			favCursor = fdb.query(MyFavDBHelper.DATABASENAME, null, SPicture.LONGITUDE + " >= " + visi.southwest.longitude + " AND "
					+ SPicture.LONGITUDE + " <= " + visi.northeast.longitude + " AND "
					+ SPicture.LATITUDE + " >= " + visi.southwest.latitude + " AND "
					+ SPicture.LATITUDE + " <= " + visi.northeast.latitude, null, null, null, null);

		}
		
		
//		favCursor = fdb.query(MyFavDBHelper.DATABASENAME, null, null, null, null, null, null);
		
		if(favCursor.getCount()!=0){
			favClustering = new Clustering(favCursor, Math.abs(northeast.longitude - southwest.longitude)/10, 4);
			
			favClustering.kMedoids();
			
			//Log.w("fffff", "dfsdfsdfsdff");
			
			for(Cluster cc : favClustering.clusters){
	        	clusterIdx++;
	        	
	        	//네트워크 스레드 사용하여 마커 그리기
	        	p = cc.points.get(cc.cluCenterIdx);
	        	
	        	favCursor.moveToPosition(p.cursorPosition);
	        	
	        	//bit = Bitmap.createScaledBitmap(bit, thumWidth, thumHeight, true);
	        	//bit = getFramedBitmap(bit, Color.BLUE);
	        	
	        	//bitd = BitmapDescriptorFactory.fromBitmap(bit);
	        	bitd = BitmapDescriptorFactory.fromResource(R.drawable.noimage);
	        	
	        	//해당 점의 커서 포지션을 이미지 타이틀에 저장
				//imageTitle = iCursor3.getString(iCursor3.getColumnIndex(ImageColumns.DATE_TAKEN));
				imageTitle = p.cursorPosition + ":" + clusterIdx;

				//일단 tmpMarkers에 저장해두고
				Marker m = mGoogleMap.addMarker(new MarkerOptions().position(p.latlng).title(imageTitle).icon(bitd).snippet(LABELNUM));
				tmpMarkers.add(m);
				
				if(applicationClass.session != null &&
						applicationClass.session.isOpened() && mgr.getActiveNetworkInfo() != null){
				
					//Log.w("session", applicationClass.session+"");
					
					BitmapDownloaderTask task = new BitmapDownloaderTask(m, 0);
					task.execute(favCursor.getString(favCursor.getColumnIndex(SPicture.SMALLIMAGEURL)));
					facebookMarkerTasks.add(task);
				}
			}
		}
		
		//Log.w("fffff", "dfsdfsdfsdff");
	}
	
	class GetPotosTask extends AsyncTask<Void, Void, ArrayList<SPicture>>{

		@Override
		protected ArrayList<SPicture> doInBackground(Void... arg0) {
			return getPotos.GetTotalPhotos(visi.northeast.latitude, 
	    			visi.northeast.longitude, 
	    			visi.southwest.latitude, 
	    			visi.southwest.longitude);
		}

		@Override
		protected void onPostExecute(ArrayList<SPicture> result) {
			super.onPostExecute(result);
			
			//다른 마커를 클릭한 상태였다면 클릭 전 상태로 다시 복원
//			if(clusterClick){
//				clusterClick = false;
//				
//				for(Marker m: markers){
//					if(!m.getSnippet().equals(ALLUSERNUM) && 
//							m.getTitle().split(":")[1].equals(clickClusterIdx+"") &&
//						clustering != null && clustering.from == Integer.valueOf(m.getSnippet())){
//						
//						m.setVisible(true);
//						m.hideInfoWindow();
//						break;
//					}
//					//전체 보기 마커만 지움
//					else if(m.getSnippet().equals(ALLUSERNUM))
//						m.remove();
//				}
//			}
			
			
			//************* 다음 작업은 카메라 상하좌우 이동시
			if(moveCamera)
			{	    
		    	for(BitmapDownloaderTask b : facebookMarkerTasks){
		    		b.cancel(false);
		    	}
		    	facebookMarkerTasks.clear();
				
				if(clickAllUserIdx != -1)
					clickAllUserIdx = -1;
				
				allPicClusterList.clear();
				
				//전체 보기 마커만 지움
				for(Marker m: markers){
					if(m.getSnippet().equals(ALLUSERNUM))
						m.remove();
				}
				
				//전체보기 마커만 지움
				for(Marker m: tmpMarkers){
					if(m.getSnippet().equals(ALLUSERNUM))
						m.remove();
				}
				//tmpMarkers.clear();
				
				if(!clusterClick)
					imageAdapter.notifyDataSetChanged();
				
				moveCamera = false;
			}
			
			//**************
			
			allPicList = result;
			
			//TODO: Test
			for(SPicture s: allPicList){
				Log.w("MapTestActicity", s.pictureID + ": " + s.isPublic);
			}
			
			
			//현재 화면의 두 점을 서버로 보내고, 서버로 부터 화면 영역안의 사진들을 받아옴	
			ThreadExcutor.execute(new Runnable() {
	    		@Override
				public void run() {
//			allViewThread = new Thread() {
//				@Override
//				public void run() {
					
//			    			try {
//								Thread.sleep(1000);
//								 
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
	    			
	    			//Looper.prepare();
	    			//mLoop = Looper.myLooper();
					
					
//					try{
//						allPicList = getPotos.GetTotalPhotos(visi.northeast.latitude, 
//				    			visi.northeast.longitude, 
//				    			visi.southwest.latitude, 
//				    			visi.southwest.longitude);
//					}catch (Exception e) {
//						e.printStackTrace();
//					}
					
					

			    	mhandler.post(new Runnable() {
						
						@Override
						public void run() {
							
//							ht = new MyHandlerThread(MapTestActivity.this, "wait", "Please wait while loading...");
//							ht.start();
			
							
//							Log.w("MapTest", "Load");
//							allPicList = getPotos.GetTotalPhotos(visi.northeast.latitude, 
//					    			visi.northeast.longitude, 
//					    			visi.southwest.latitude, 
//					    			visi.southwest.longitude);
//							
//							Log.w("MapTest", "LoadFinish");
							
							//전체 사용자 보기 사진들은 보라색으로 표시
							int i=-1;
							BitmapDescriptor bitd;
							String imageTitle;
							if(allPicList != null){
								for(SPicture s: allPicList){
									i++;
									bitd = BitmapDescriptorFactory.fromResource(R.drawable.noimage);
									
									imageTitle = 0 + ":" + i;
									
									//일단 tmpMarkers에 저장해두고
									Marker m = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(s.latitude, s.longitude)).title(imageTitle).icon(bitd).snippet(ALLUSERNUM));
									tmpMarkers.add(m);
									
									if(applicationClass.session != null &&
											applicationClass.session.isOpened() && mgr.getActiveNetworkInfo() != null){
									
										//Log.w("session", applicationClass.session+"");
										
										BitmapDownloaderTask task = new BitmapDownloaderTask(m, 0);
										task.execute(s.smallImageURL);
										facebookMarkerTasks.add(task);
									}
									
								}
							}else
								allPicList = new ArrayList<SPicture>();
							
//							MyHandlerThread.stop(ht);
						}
			    	});
	    		}
			});
			
		}
		
		
		
	}
	
	
	private void drawAllUserTumnailMap(){
//		LatLngBounds llb = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;
//		
//		double northEastLat = llb.northeast.latitude;
//		double northEastLng = llb.northeast.longitude;
//		double southWestLat = llb.southwest.latitude;
//		double southWestLng = llb.southwest.longitude;
		
		
//		allPicList = new ArrayList<SPicture>();
//		allPicClusterList = new ArrayList<SPicture>();
		
//		if(allViewThread != null && allViewThread.isAlive()){
//			allViewThread.stop();
//		}

		Log.w("MapTest", "Load");
		
		allViewTask = new GetPotosTask();
		
		allViewTask.execute();
//		try {
//			allPicList = allViewTask.get();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		Log.w("MapTest", "LoadFinish");
		
		//allViewThread.start();
	}
	
	//도 포맷 -> 도분초 포맷으로 바꾸기
	public static String latlocConvert(double coordinate) {
        if (coordinate < -180.0 || coordinate > 180.0 ||
            Double.isNaN(coordinate)) {
            throw new IllegalArgumentException("coordinate=" + coordinate);
        }
        StringBuilder sb = new StringBuilder();
        // Handle negative values
        if (coordinate < 0) {
            sb.append('-');
            coordinate = -coordinate;
        }
        int degrees = (int) Math.floor(coordinate);
        sb.append(degrees);
        sb.append("/1,");
        coordinate -= degrees;
        coordinate *= 60.0;
        int minutes = (int) Math.floor(coordinate);
        sb.append(minutes);
        sb.append("/1,");
        coordinate -= minutes;
        coordinate *= 60.0;
        sb.append(coordinate);
        sb.append("/1000");
        return sb.toString();
    }
	
	public static Bitmap getRoundedBitmap(Bitmap bitmap) {
        Bitmap output = null;
        if(bitmap != null){
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            
            Canvas canvas = new Canvas(output);
            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);
            final float roundPx = 10; // 라운드 범위 : 픽셀
            
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
        }
        return output;
    }
	

	
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
	
	public class ImageAdapter extends BaseAdapter {
        int mGalleryItemBackground;
        
        
        public ImageAdapter(Context c) {
//        	Log.i(ACTIVITY_SERVICE, "northwest location: "+visi.northeast + ", " +visi.southwest +"******************");
        	
        	
        	//iCursor2 = cr.query(Images.Media.EXTERNAL_CONTENT_URI, null, Images.Media.LATITUDE +" =0 AND " + Images.Media.LATITUDE +"!=0", null, "LATITUDE desc, LONGITUDE desc");
        	
//        	iCursor2 = cr.query(Images.Media.EXTERNAL_CONTENT_URI, null, Images.Media.LATITUDE +" !=0 OR " + Images.Media.LONGITUDE +"!=0", null, "LATITUDE desc, LONGITUDE desc");
//        	iCursor2 = cr.query(Images.Media.EXTERNAL_CONTENT_URI, null, "( " + Images.Media.LATITUDE +" !=0 OR " + Images.Media.LONGITUDE +"!=0 )" + " AND "
//        																	+ Images.Media.LATITUDE+" >= "+southwest.latitude + " AND "
//        																	+ Images.Media.LONGITUDE+" >= "+southwest.longitude + " AND "
//        																	+ Images.Media.LATITUDE+" <= "+northeast.latitude + " AND "
//        																	+ Images.Media.LONGITUDE+" <= "+northeast.longitude
//        	, null, "LATITUDE desc, LONGITUDE desc");
        
        	//iCursor2.moveToFirst();
			
            mContext = c;
            // See res/values/attrs.xml for the <declare-styleable> that defines
            // Gallery1.
            TypedArray a = obtainStyledAttributes(R.styleable.Gallery1);
            mGalleryItemBackground = a.getResourceId(
                    R.styleable.Gallery1_android_galleryItemBackground, 0);
            a.recycle();
 
        }

        public int getCount() {
        	if(!clusterClick && clickAllUserIdx == -1)
        		return 0;
//        	else 
        	else if(clickAllUserIdx == -1)
        		return clustering.clusters.get(clickClusterIdx).points.size();
        	else
        		return allPicClusterList.size();
        }

        
        
        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i = new ImageView(mContext);
   
	            if(!clusterClick && clickAllUserIdx == -1){
	//            	iCursor2.moveToPosition(position);
	//            	
	//	            i.setImageBitmap(Images.Thumbnails.getThumbnail(cr, iCursor2.getInt(iCursor2.getColumnIndex(BaseColumns._ID)), Images.Thumbnails.MICRO_KIND, null));
	            }
	            else{
	            	
	            	if(markerfrom != 3)
	            		iCursor3.moveToPosition(clustering.clusters.get(clickClusterIdx).points.get(position).cursorPosition);
	            	
	            	//아래 네줄로 변경

	            	i.setAdjustViewBounds(true);
	            	//i.setScaleType(ImageView.ScaleType.CENTER_CROP);
	            	
	//            	if(clustering.clusters.get(clickClusterIdx).cluCenterIdx == position)
	//            		i.setId(77);
	            	

	            	i.setScaleType(ImageView.ScaleType.FIT_XY);
	            	//i.setScaleType(ImageView.ScaleType.MATRIX);
	                i.setLayoutParams(new Gallery.LayoutParams(applicationClass.thumWidth * 4/3, applicationClass.thumWidth * 4/3));
//	                i.setHorizontalScrollBarEnabled(true);
//	                i.setMaxHeight(applicationClass.thumWidth * 4/3);
//	                i.setMaxHeight(applicationClass.thumWidth * 4/3);
	                // The preferred Gallery item background
	                i.setBackgroundResource(mGalleryItemBackground);
	                
	                
	                //클릭한 마커가 로컬갤러리 소속일 경우
	                if(markerfrom == 0){
	                	//다음 두 줄은 이미지가 똑바로 안보일 때 해당 이미지를 회전하는 코드
	    	        	int degree = getExifOrientation(iCursor3.getString(iCursor3.getColumnIndex(ImageColumns.DATA)));
	    	        	
	                	i.setImageBitmap(getRotatedBitmap(Images.Thumbnails.getThumbnail(cr, iCursor3.getInt(iCursor3.getColumnIndex(BaseColumns._ID)), Images.Thumbnails.MICRO_KIND, null), degree));
	                	
//	                	i.setImageBitmap(Images.Thumbnails.getThumbnail(cr, iCursor3.getInt(iCursor3.getColumnIndex(BaseColumns._ID)), Images.Thumbnails.MICRO_KIND, null));
	                }
	                //클릭한 마커가 페이스북 소속일 경우
	                else if(markerfrom == 1){
	                	//이미지 로더 사용
		            	//String url = "http://img3.imageshack.us/img3/6147/toyotaandattpark.jpg";
	                	String url = iCursor3.getString(iCursor3.getColumnIndex(SPicture.SMALLIMAGEURL));
	                	//imageTagFactory.usePreviewImage(thumWidth, thumHeight, true);
		    			ImageTag tag = imageTagFactory.build(url);
		    			
//		    			tag.setPreviewHeight(applicationClass.thumWidth * 4/3);
//		    			tag.setPreviewWidth(applicationClass.thumWidth * 4/3);
		            	
		    			i.setTag(tag);
		                //이미지 로더에 로드
		                imageLoader.load(i);
	                }
	                //클릭한 마커가 전체 사용자 보기 사진일 경우
	                else if(markerfrom == 3){
	                	String url = allPicClusterList.get(position).smallImageURL;
	                	ImageTag tag = imageTagFactory.build(url);
		            	
//	                	tag.setPreviewHeight(applicationClass.thumWidth * 4/3);
//		    			tag.setPreviewWidth(applicationClass.thumWidth * 4/3);
	                	
	                	i.setTag(tag);
		            	
		            	//이미지 로더에 로드
		                imageLoader.load(i);
	                }
	            }
            
            return i;
        }

        private Context mContext;
    }
	
	class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap>{
		//private String url;
		//private final WeakReference<ImageView> imageViewReference;
		private Marker mark;
		int from = -1;			//0: 기본 마커
								//1: 하단 리스트 클릭시 생기는 임시 마커
		
		public BitmapDownloaderTask(Marker mark, int from) {
			this.mark = mark;
			this.from = from;
		}

		// Actual download method. run in the task thread
		@Override
		protected Bitmap doInBackground(String... params) {
			// params comes from execute() call: params[0] is the url.
			return downloadBitmap(params[0]);
		}

		
		//Once the image is downloaded, associates it to the imageView
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if(isCancelled()){
				bitmap = null;
			}
			
//			if(imageViewReference != null){
//				ImageView imageView = imageViewReference.get();
//				if(imageView != null)
//					imageView.setImageBitmap(bitmap);
//			}
			 
			if(mark != null && bitmap != null){
				if(tmpMarkers.indexOf(mark) >= 0){
					tmpMarkers.remove(tmpMarkers.indexOf(mark));
					//Log.w("tmpMarkerTest", "Test remove tmpMarker");
				}
				
				bitmap = Bitmap.createScaledBitmap(bitmap, applicationClass.thumWidth * 2/3, applicationClass.thumHeight * 2/3, true);
				
				//페이스북은 파란색
				if(mark.getSnippet().equals(FACEBOOKNUM))
					bitmap = getFramedBitmap(bitmap, 0xff3C5A91);
				//전체 사용자 보기는 보라색
				else if(mark.getSnippet().equals(ALLUSERNUM)){
					
					int ispub = allPicList.get(Integer.valueOf(mark.getTitle().split(":")[1])).isPublic;
					
					//핫플레이스이면 분홍색
					if(ispub == 7)
						bitmap = getFramedBitmap(bitmap, 0xffff69b4);
					//쿨 플레이스이면 하늘색
					else if(ispub == 8)
						bitmap = getFramedBitmap(bitmap, 0xffafeeee);
					//핫 쿨 둘다면 주황색
					else if(ispub == 9){
						bitmap = getFramedBitmap(bitmap, 0xffff7f50);
					}
					//기본은 보라색
					else
						bitmap = getFramedBitmap(bitmap, 0xff7b68ee);
				}
				//라벨 보기는 초록색
				else if(mark.getSnippet().equals(LABELNUM))
					bitmap = getFramedBitmap(bitmap, 0xff3cb371);
					

				if(from==0)
					markers.add(mGoogleMap.addMarker(new MarkerOptions().position(mark.getPosition()).title(mark.getTitle()).icon(BitmapDescriptorFactory.fromBitmap(bitmap)).snippet(mark.getSnippet())));
				else if(from==1){
					Marker tmpM = mGoogleMap.addMarker(new MarkerOptions().position(mark.getPosition()).title(mark.getTitle()).icon(BitmapDescriptorFactory.fromBitmap(bitmap)).snippet(mark.getSnippet()));
					tmpMarkers.add(tmpM);
					tmpM.showInfoWindow();
					//Log.w("Call", "infoWindow call 1");
				}
				
				mark.remove();
			}
			
			//Log.w("test", "maker draw thread!!!!");
		}
	}
	
	class BitmapDownloaderTask2 extends AsyncTask<String, Void, Bitmap>{

		@Override
		protected Bitmap doInBackground(String... arg0) {
			return downloadBitmap(arg0[0]);
		}
		
	}
	
	class BitmapDownloaderTaskInfo extends AsyncTask<String, Void, Bitmap>{
		//private String url;
		//private final WeakReference<ImageView> imageViewReference;
		private Marker mark;
		ImageView iv;
		String who;
		
		public BitmapDownloaderTaskInfo(ImageView imageView, Marker m, String who) {
			//imageViewReference = new WeakReference<ImageView>(imageView);
			iv = imageView;
			this.mark = m;
			this.who = who;
		}

		// Actual download method. run in the task thread
		@Override
		protected Bitmap doInBackground(String... params) {
			// params comes from execute() call: params[0] is the url.
			
			return downloadBitmap(params[0]);
		}

		
		@Override
		protected void onCancelled() {
			super.onCancelled();
			finish_loading = true;
		}

		//Once the image is downloaded, associates it to the imageView
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if(isCancelled()){
				bitmap = null;
			}
			
			//final Bitmap btp = bitmap;
			final Bitmap tmpBm = bitmap;
			
			Handler h = new Handler();
			h.post(new Runnable() {
					
				@Override
				public void run() {
		
					Bitmap tmpBm2 = tmpBm;
					
					//만일 유저 썸네일 이미지가 변경되었다면
					if(tmpBm == null && !who.equals(INFO_WINDOW)){
						
						tmpBm2 = getOwnerThum(mark);
						
					}
					
					if(mark!=null && tmpBm2 != null){
						
						if(who.equals(INFO_WINDOW)){
							//tmpBm2 = Bitmap.createScaledBitmap(tmpBm2, applicationClass.thumWidth * 4/3, applicationClass.thumHeight * 4/3, true);
			
							//전역 변수에 비트맵 저장
							infoWBmp = tmpBm2;
						}
						else{
							
							tmpBm2 = Bitmap.createScaledBitmap(tmpBm2, applicationClass.thumWidth * 1/4, applicationClass.thumHeight * 1/4, true);
							
							//전역 변수에 비트맵 저장
							infothumBmp = tmpBm2;
						}
						
						finish_loading = false;
						mark.showInfoWindow();
						//Log.w("Call", "infoWindow call 2");
						//Log.w("Test", "bmp Load infoWindow!!!!!");
					}else{
						finish_loading = true;
	
						if(!who.equals(INFO_WINDOW) && infoWBmp == null)
							tmpStr = "finish";
						
						infoWBmp = null;
						infothumBmp = null;
					}
					
				}
			});
			
			
//			if(imageViewReference != null){
//				ImageView imageView = imageViewReference.get();
//				if(imageView != null){
//					imageView.setImageBitmap(bitmap);
//					mark.showInfoWindow();
//				}
//			}
			
//			if(iv != null){
//				iv.setImageBitmap(bitmap);
//				mark.showInfoWindow();
//			}
		}
	}
	
//	private void infoWindowloadbmp(Marker m){
//		m.showInfoWindow();
//	}
	
	// json에서 thumbnailurl정보 받아서 string 리턴
	private String getMyThumbnail(StringWriter thum_data) throws JSONException{
		String profileUrl = new String();
		JSONObject Jsonthum = new JSONObject(thum_data.toString());
		JSONObject picture = Jsonthum.getJSONObject("picture");
		JSONObject datathum = picture.getJSONObject("data");
		profileUrl = datathum.getString("url");
		
		return profileUrl;
	}
	
	// url정보로 json 가져오기
	private class GetJsonAsyncTask extends AsyncTask<String, Integer, StringWriter>{
    	@Override
    	protected StringWriter doInBackground(String...addr){
    		
			StringWriter json_data = new StringWriter();
	    	try{
	    		URL url = new URL(addr[0]);
	    		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
	    		if(conn != null){
	    			conn.setConnectTimeout(10000);
	    			conn.setUseCaches(false);
	    			if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
	    				IOUtils.copy(conn.getInputStream(), json_data,"UTF-8");
	    			}
	    			conn.disconnect();
	    		}
	    	}catch (Exception ex){;}
	    	
    		return json_data;
    	}
    }
	
	private class GetOwnerThumASYNCTask extends AsyncTask<Marker, Void, Bitmap>{
		
		@Override
		protected Bitmap doInBackground(Marker... params) {

			return getOwnerThum(params[0]);
		}
		
		
	}
	
	private Bitmap getOwnerThum(Marker mrk){
		String ownerThumURL = "";	//페이스북으로 새로 받아와서 할당
		
		int cursorIdx = Integer.parseInt(mrk.getTitle().split(":")[0]);
		final String URL_PREFIX_FACEBOOK= "https://graph.facebook.com/";
		final String URL_PREFIX_THUMBNAIL = "?fields=picture";
		boolean isFriends = false;
		GetJsonAsyncTask getThumJson = new GetJsonAsyncTask();
		String ownerID = "";
	
		if(mrk.getSnippet().equals(FACEBOOKNUM)){
			
			
			facebookCursor.moveToPosition(cursorIdx);
			
			if(facebookCursor.getInt(facebookCursor.getColumnIndex(SPicture.ISPUBLIC))==2){
				isFriends = true;
			}
			
			ownerID = facebookCursor.getString(facebookCursor.getColumnIndex(SPicture.OWNERID));
			
			String getthumUrl = URL_PREFIX_FACEBOOK + ownerID + URL_PREFIX_THUMBNAIL;
			
			
			
			
			getThumJson.execute(getthumUrl);
			StringWriter thum_data;
			try {
				thum_data = getThumJson.get();
				ownerThumURL = getMyThumbnail(thum_data);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
	

			if(ownerThumURL.length() > 0){
				MyDBHelper.modifyUserThumnailOnDB(mHelper, ownerID, ownerThumURL);

				db = mHelper.getWritableDatabase();
				
				facebookCursor = db.query(MyDBHelper.DATABASENAME, null, null, null, null, null, null);
				
				//다시 그림
				handler.sendEmptyMessage(1);
			}
		}else if(mrk.getSnippet().equals(LABELNUM)){
			favCursor.moveToPosition(cursorIdx);
			
			ownerID = favCursor.getString(favCursor.getColumnIndex(SPicture.OWNERID));
			
			String getthumUrl = URL_PREFIX_FACEBOOK+ ownerID + URL_PREFIX_THUMBNAIL;
								
			getThumJson.execute(getthumUrl);
			StringWriter thum_data;
			try {
				thum_data = getThumJson.get();
				ownerThumURL = getMyThumbnail(thum_data);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			if(ownerThumURL.length() > 0){
				MyFavDBHelper.modifyUserThumnailOnDB(favHelper, ownerID, ownerThumURL);

				fdb = favHelper.getWritableDatabase();
				
				favCursor = fdb.query(MyFavDBHelper.DATABASENAME, null, null, null, null, null, null);
				
				//다시 그림
				handler.sendEmptyMessage(1);
			}
		}
		else if(mrk.getSnippet().equals(ALLUSERNUM)){
			
			//Log.w("downloadBitmap", "!!!");
			
			if(cursorIdx == 0)
				ownerID = allPicList.get(Integer.parseInt(mrk.getTitle().split(":")[1])).ownerID;
			else
				ownerID = allPicClusterList.get(cursorIdx).ownerID;
			
			
			Log.w("Test", "ownerID: "+ownerID);
			
			String getthumUrl = URL_PREFIX_FACEBOOK+ ownerID + URL_PREFIX_THUMBNAIL;
			
			getThumJson.execute(getthumUrl);
			StringWriter thum_data;
			try {
				thum_data = getThumJson.get();
				
				Log.w("Test", "thum_data: "+thum_data);
				
				ownerThumURL = getMyThumbnail(thum_data);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			if(ownerThumURL.length() > 0){
				int idxx = Integer.parseInt(mrk.getTitle().split(":")[0]);
				allPicClusterList.get(idxx).ownerThumURL = ownerThumURL;
				
				if(idxx == 0)
					allPicList.get(Integer.parseInt(mrk.getTitle().split(":")[1])).ownerThumURL = ownerThumURL;
			}
			
			Log.w("test", "ownerThumURL : "+ownerThumURL);
		}
		
		

		//TODO: 친구의 사진이 아닐때 서버에 해당 사용자의 바뀐 섬네일을 보냄
		//일단 내 페북 사진도 아닐때...
		if(!isFriends && ownerThumURL.length() > 0 && !mrk.getSnippet().equals(FACEBOOKNUM)){
			SendId si = new SendId();
			try {
				si.SendIdAndThumb(ownerID, ownerThumURL);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		if(ownerThumURL.length() > 0){
			
			BitmapDownloaderTask2 bmpTask = new BitmapDownloaderTask2();
			
			bmpTask.execute(ownerThumURL);
			
			try {
				return bmpTask.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	//비동기적인 로드
	public Bitmap downloadBitmap(String url){
		final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		final HttpGet getRequest = new HttpGet(url);
		
		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode != HttpStatus.SC_OK){
				Log.w("ImageDownloader", "Error "+statusCode+"while retriving bitmap from "+url);
				
//				Log.w("downloadBitmap", "hhhhh");
//				Log.w("downloadBitmap", "statusCode:"+statusCode+" mark:"+mark.getSnippet());
//				
//				if(statusCode == HttpStatus.SC_NOT_FOUND && mark != null){
//					//프로필 사진이 변경되었을 경우 처리
//					//페이스북으로 부터 프로필 사진 다시 가져옴
//					//서버로 해당 사용자 프로필 사진 보내고
//					
//					GetOwnerThumASYNCTask gtask = new GetOwnerThumASYNCTask();
//					
//					gtask.execute(mark);
//					
//					
//					return gtask.get();
//					
//					
//				}
//				else
					return null;
					
			}
			
			final HttpEntity entity = response.getEntity();
			if(entity !=null){
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();
					final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
					return bitmap;
				} finally {
					if(inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			//could provide a more explicit error massage for IOException or IllegalStateException
			getRequest.abort();
			Log.w("ImageDownloader","Error while retrieving bitmap from "+ url + ": " + e.toString());
		} finally {
			if(client != null)
				client.close();
		}
		return null;
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
	
//	public synchronized static Bitmap SafeDecodeBitmapFile(String strFilePath) {
//		Log.w("SafeDecodeBitmapFile", "[ImageDownloader] SafeDecodeBitmapFile : " + strFilePath);
//
//		try {
//			File file = new File(strFilePath);
//			if (file.exists() == false) {
//				Log.w("SafeDecodeBitmapFile", "[ImageDownloader] SafeDecodeBitmapFile : File does not exist !!");
//
//				return null;
//			}
//
//			// Max image size
//			//final int IMAGE_MAX_SIZE = GlobalConstants.getMaxImagePixelSize();
//			BitmapFactory.Options bfo = new BitmapFactory.Options();
//			bfo.inJustDecodeBounds = true;
//
//			BitmapFactory.decodeFile(strFilePath, bfo);
//
//			if (bfo.outHeight * bfo.outWidth >= IMAGE_MAX_SIZE * IMAGE_MAX_SIZE) {
//				bfo.inSampleSize = (int) Math.pow(
//						2,
//						(int) Math.round(Math.log(IMAGE_MAX_SIZE
//								/ (double) Math
//										.max(bfo.outHeight, bfo.outWidth))
//								/ Math.log(0.5)));
//			}
//			bfo.inJustDecodeBounds = false;
//			bfo.inPurgeable = true;
//			bfo.inDither = true;
//
//			final Bitmap bitmap = BitmapFactory.decodeFile(strFilePath, bfo);
//
//			int degree = GetExifOrientation(strFilePath);
//
//			return GetRotatedBitmap(bitmap, degree);
//		} catch (OutOfMemoryError ex) {
//			ex.printStackTrace();
//
//			return null;
//		}
//	}
}
