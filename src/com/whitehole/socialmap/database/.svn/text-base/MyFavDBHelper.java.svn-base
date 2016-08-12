package com.whitehole.socialmap.database;

import java.util.*;

import com.whitehole.socialmap.network.*;

import android.content.*;
import android.database.sqlite.*;

public class MyFavDBHelper extends SQLiteOpenHelper {
	public static final String DATABASENAME = "socialmapFav";
	
	public MyFavDBHelper(Context context) {
		super(context, "myFavDB.db", null, 1);
	}
	
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		//기본키는 picture ic
		arg0.execSQL("CREATE TABLE " + DATABASENAME + " ( "+SPicture.PICTUREID+" TEXT PRIMARY KEY, "
				+ SPicture.ISPUBLIC + " INTEGER, "
				+ SPicture.LATITUDE + " REAL, "
				+ SPicture.LONGITUDE + " REAL, "
				+ SPicture.COMMENTURL + " TEXT, "
				+ SPicture.IMAGEURL + " TEXT, "
				+ SPicture.SMALLIMAGEURL + " TEXT, "
				+ SPicture.MAKETIME + " INTEGER, "
				+ SPicture.MAKETIMES + " TEXT, "
				+ SPicture.RECORD + " TEXT, "
				+ SPicture.OWNERNAME + " TEXT, "
				+ SPicture.OWNERID + " TEXT, "
				+ SPicture.OWNERTHUMURL + " TEXT, "
				+ SPicture.PLACENAME + " TEXT);"
				);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		arg0.execSQL("DROP TABLE IF EXISTS " + DATABASENAME);
		onCreate(arg0);
	}
	
	/**
	 * 즐겨찾기 사진을 추가하는 함수
	 * @param mHelper
	 * @param sp
	 */
	public static void pushDataToDB(MyFavDBHelper mHelper, ArrayList<SPicture> sp){
		SQLiteDatabase db;
		ContentValues row;
		
		db = mHelper.getWritableDatabase();
		
		if(sp != null){
			for(SPicture pic: sp){
				
				row = new ContentValues();
				row.put(SPicture.PICTUREID, pic.pictureID);
				row.put(SPicture.ISPUBLIC, pic.isPublic);
				row.put(SPicture.LATITUDE, pic.latitude);
				row.put(SPicture.LONGITUDE, pic.longitude);
				row.put(SPicture.MAKETIME, pic.makeTime);
				row.put(SPicture.MAKETIMES, pic.makeTimeS);
				row.put(SPicture.OWNERNAME, pic.ownerName);
				row.put(SPicture.RECORD, pic.record);
				row.put(SPicture.COMMENTURL, pic.commentURL);
				row.put(SPicture.IMAGEURL, pic.imageURL);
				row.put(SPicture.OWNERTHUMURL, pic.ownerThumURL);
				row.put(SPicture.OWNERID, pic.ownerID);
				row.put(SPicture.SMALLIMAGEURL, pic.smallImageURL);
				row.put(SPicture.PLACENAME, pic.placeName);
				
				db.insert(DATABASENAME, null, row);
			}
		}
		
		//mHelper.close();
	}
	
	/**
	 * DB의 모든 데이터를 지우는 함수
	 * @param mHelper
	 */
	public static void deleteDataOnDB(MyFavDBHelper mHelper){
		SQLiteDatabase db;
		
		db = mHelper.getWritableDatabase();
		
		db.delete(DATABASENAME, null, null);
		//mHelper.close();
	}

	
	public static void deletePictureOnDB(MyFavDBHelper mHelper, String picID){
		SQLiteDatabase db;
		
		db = mHelper.getWritableDatabase();
		
		if(picID != null && !picID.equals(""))
			db.delete(DATABASENAME, SPicture.PICTUREID + " = '" + picID + "'", null);
	}
	
	public static void deletePicturesOnDB(MyFavDBHelper mHelper, ArrayList<String> picIds){
		SQLiteDatabase db;
		
		db = mHelper.getWritableDatabase();
		
		for(String pid : picIds){
			if(pid != null && !pid.equals(""))
				db.delete(DATABASENAME, SPicture.PICTUREID + " = '" + pid + "'", null);
		}
	}
	
	public static void modifyUserThumnailOnDB(MyFavDBHelper mHelper, String userID, String ownerThumURL){
		SQLiteDatabase db;
		ContentValues values = new ContentValues();
		
		values.put(SPicture.OWNERTHUMURL, ownerThumURL);
		
		db = mHelper.getWritableDatabase();
		
		db.update(DATABASENAME, values, SPicture.OWNERID + " = '" + userID + "'", null);
	}
}
