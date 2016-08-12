package com.whitehole.socialmap.backtagging;


import java.io.IOException;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;
import android.widget.Toast;

import com.whitehole.socialmap.R;

public class Tag_Service extends Service {
	//위치정보를 공급하는 근원
    String locationProvider;
	int locLoadTime=0;
	ConnectivityManager mgr;
    
    //위치 정보 매니져 객체
    //LocationManager locationManager;
    
    Location loc = null;
    
    boolean mQuit;
	    
    ContentResolver cr;
    
    LocationManager mLocMan;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 위치 서비스 정지
		mLocMan.removeUpdates(mListener);
		mQuit = true;
		
		Log.w("location service", "stop!!!!!!");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		mgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		mQuit = false;
		
		SharedPreferences pref = getSharedPreferences("Pref", 0);
		
		if(pref.getBoolean("Tag", true) && mgr.getActiveNetworkInfo() != null){
			locationUpdate();
		
			if(locationProvider != null){
				ThreadExcutor.execute(new Runnable() {
					@Override
					public void run() {
						
						//for(;mQuit == false;){
		//					try {
		//						//5분 마다 실행
		//						//Thread.sleep(300000);
		//						
		//						//10초
		//						//Thread.sleep(10000);
		//						 
		//					} catch (InterruptedException e) {
		//						e.printStackTrace();
		//					}

							//리스너를 통해 받아올때까지 기다림
							while(loc == null){
								try {
									Thread.sleep(500);
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								locLoadTime++;
								
								//30초가 경과했다면 종료
								if(locLoadTime == 60){
									mLocMan.removeUpdates(mListener);
									break;
								}
							}
							
							try {
								imageTagging();
							} catch (IOException e) {
								e.printStackTrace();
							}
							
							//mLocMan.removeUpdates(mListener);
							//Log.w("location service", "stop!!!!");
						}
						//}
					
				});
			}
		}else if(pref.getBoolean("Tag", true) && mgr.getActiveNetworkInfo() == null)
			Toast.makeText(Tag_Service.this, R.string.network_connect_camera,
	                Toast.LENGTH_SHORT).show();
		
		return START_STICKY;
	}
	
	public void locationUpdate(){
		
		Criteria crit = new Criteria();
		crit.setAccuracy(Criteria.NO_REQUIREMENT);
		crit.setAltitudeRequired(false);
		crit.setPowerRequirement(Criteria.NO_REQUIREMENT);
		crit.setCostAllowed(true);
		
		mLocMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        
        locationProvider = mLocMan.getBestProvider(crit, true);
        
        if(locationProvider != null)
        	mLocMan.requestLocationUpdates(locationProvider, 0, 0, mListener);
        else
        	Toast.makeText(Tag_Service.this, R.string.locAgreeNot,
                    Toast.LENGTH_SHORT).show();
	}

	public void imageTagging() throws IOException{
		long time = 0;
		Cursor iCursor;
		ExifInterface ei;
		String path;
		ContentValues values = new ContentValues();
		
//		Criteria crit = new Criteria();
//		crit.setAccuracy(Criteria.NO_REQUIREMENT);
//		crit.setAltitudeRequired(false);
//		crit.setPowerRequirement(Criteria.NO_REQUIREMENT);
//		crit.setCostAllowed(true);
		
		//위치 정보 매니져 객체 얻어오기
//        locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
//
//        //위치정보 공급자 얻어오기
//        locationProvider = locationManager.getBestProvider(new Criteria(), true);
//        //locationProvider = locationManager.getBestProvider(crit, true);
//
//        //가장 최근의 Location 객체 얻어오기
//        location = locationManager.getLastKnownLocation(locationProvider);
		
        
        
        //TODO: 초 단위 시간 시분초로 변경하여 출력해보기
        
//        if(location != null){
//        	Log.w("current location", "Latitude:" + location.getLatitude() + " Longtitude: " + location.getLongitude());
//        	
//	        // 현재 시각: 시스템의 밀리초 구하기.(국제표준시각(UTC, GMT) 1970/1/1/0/0/0 으로부터 경과한 시각)
//	        time = System.currentTimeMillis() - location.getTime();
//	        //location 변수에 저장되어 있는 시각과 코드 상의 현재 시각을 비교해보기
//	        Log.w("Time", time / 1000.0 + "");
//        }
        
		if(loc == null){
			loc = mLocMan.getLastKnownLocation(locationProvider);
			//Log.w("loc null", "Last Location!!!");
		}
		
        
		cr = getContentResolver();
		
		//사진을 찍은 현재 시각에서 5분전부터 지금까지 찍은 사진들을 얻어오기 
		iCursor = cr.query(Images.Media.EXTERNAL_CONTENT_URI, null, Images.Media.DATE_TAKEN + " >= " + (System.currentTimeMillis() - 300000), null, null);
		
		//iCursor = cr.query(Images.Media.EXTERNAL_CONTENT_URI, null, Images.Media.LATITUDE +" = 0.0 AND " + Images.Media.LONGITUDE +" = 0.0 AND " + Images.Media.DATE_TAKEN + " >= " + (location.getTime() - 300000) , null, null);
		
		//iCursor = cr.query(Images.Media.EXTERNAL_CONTENT_URI, null, Images.Media.LATITUDE +"!=0 OR " + Images.Media.LATITUDE +"!=0", null, "LATITUDE desc, LONGITUDE desc");
		
		//마지막 위치 저장 시각에서 +-10분에 찍은 사진들을 얻어오기
//		iCursor = cr.query(Images.Media.EXTERNAL_CONTENT_URI, null, Images.Media.DATE_TAKEN + " >= " + (location.getTime() - 600000) + " AND "
//																	+ Images.Media.DATE_TAKEN + " <= " + (location.getTime() + 600000), null, null);
		
		//iCursor = cr.query(Images.Media.EXTERNAL_CONTENT_URI, null, Images.Media.LATITUDE +"!=0 OR " + Images.Media.LATITUDE +"!=0", null, "LATITUDE desc, LONGITUDE desc");
		if(loc != null && iCursor != null){
			while(iCursor.moveToNext()){
				
				if(iCursor.getDouble(iCursor.getColumnIndex(Images.Media.LATITUDE)) == 0.0 &&  
				iCursor.getDouble(iCursor.getColumnIndex(Images.Media.LONGITUDE)) == 0.0){
				//파일 고유의 Exif의 위치정보 수정
					path = iCursor.getString(iCursor.getColumnIndex(ImageColumns.DATA));
					ei = new ExifInterface(path);
					
					if(loc.getLatitude()>=0)
						ei.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "North Latitude");
					else
						ei.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "South Latitude");
						
					if(loc.getLongitude()>=0)
						ei.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "East Longitude");
					else
						ei.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "West Longitude");
					
					ei.setAttribute(ExifInterface.TAG_GPS_LATITUDE, latlocConvert(loc.getLatitude()));
					ei.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, latlocConvert(loc.getLongitude()));
		
					ei.saveAttributes();
					
					//media DB의 사진정보도 수정
					
					values.put("LATITUDE", loc.getLatitude());
					values.put("LONGITUDE", loc.getLongitude());	
					
					cr.update(Images.Media.EXTERNAL_CONTENT_URI, values, Images.Media._ID+"='" + iCursor.getShort(iCursor.getColumnIndex(Images.Media._ID)) + "'", null);
//					Log.w("test", iCursor.getString(iCursor.getColumnIndex(ImageColumns.TITLE)));
				}
//				Log.w("test", iCursor.getString(iCursor.getColumnIndex(ImageColumns.TITLE))+"Lat : " + iCursor.getDouble(iCursor.getColumnIndex(Images.Media.LATITUDE)) + "Lng : " + iCursor.getDouble(iCursor.getColumnIndex(Images.Media.LONGITUDE)));
			}
//			
//			//handler.sendEmptyMessage(0);
//			Log.w("modify", "image tag modify completed!!!");
		}
//		Log.w("modify", "image tag modify completed!!!22222");
		//handler.sendEmptyMessage(1);
		
//		//임시코드
		//iCursor = cr.query(Images.Media.EXTERNAL_CONTENT_URI, null, Images.Media.TITLE + " = '1350115916337'", null, null);
		//iCursor.moveToFirst();
		//Log.w("info", iCursor.getDouble(iCursor.getColumnIndex(Images.Media.LATITUDE)) + ", " + iCursor.getDouble(iCursor.getColumnIndex(Images.Media.LONGITUDE)));
		
//		while(iCursor.moveToNext()){
//			if(iCursor.getDouble(iCursor.getColumnIndex(Images.Media.LATITUDE)) > 37.608 && 
//					iCursor.getDouble(iCursor.getColumnIndex(Images.Media.LATITUDE)) < 37.609 &&
//					iCursor.getDouble(iCursor.getColumnIndex(Images.Media.LONGITUDE)) > 126.9 && 
//					iCursor.getDouble(iCursor.getColumnIndex(Images.Media.LONGITUDE)) < 127.0
//					){
//				
//				path = iCursor.getString(iCursor.getColumnIndex(ImageColumns.DATA));
//				ei = new ExifInterface(path);
//				
//				ei.setAttribute(ExifInterface.TAG_GPS_LATITUDE, latlocConvert(0.0));
//				ei.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, latlocConvert(0.0));
//				ei.saveAttributes();
//				
//				values.put("LATITUDE", 0.0);
//				values.put("LONGITUDE", 0.0);	
//				
//				cr.update(Images.Media.EXTERNAL_CONTENT_URI, values, Images.Media._ID+"='" + iCursor.getShort(iCursor.getColumnIndex(Images.Media._ID)) + "'", null);
//				Log.w("test", iCursor.getString(iCursor.getColumnIndex(ImageColumns.TITLE)));
//			}
//		}
	}
	
	LocationListener mListener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			//Log.w("location service", "stop!");
			loc = location;
			mLocMan.removeUpdates(mListener);
			
		}
	};
	
	private Handler handler = new Handler() {
		  public void handleMessage(Message msg) {
		  
			  switch (msg.what) {
			case 0:
				//Toast.makeText(Tag_Service.this, "이미지 테깅 업데이트 완료!", 0).show();
				break;
			case 1:
				//Toast.makeText(Tag_Service.this, "쓰레드가 실행되었음!", 0).show();
				break;
			default:
				break;
			}
		  }
		 
		};
	
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
	
//	class Imagetagging extends Thread {
//		
//	    
//	    public void run(){
//	    	try {
//				Thread.sleep(300000);
//				 
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//	    	
//	    	tagging();
//	    }
//	    
//	    private void tagging(){
//	    	
//	    }
//	}
	
	static class ThreadExcutor {

		public static void execute(final Runnable runnable) {
			new Thread() {
				@Override
				public void run() {
					runnable.run();
				}
			}.start();
		}
		
	}
}
