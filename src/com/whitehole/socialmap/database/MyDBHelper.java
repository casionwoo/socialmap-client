package com.whitehole.socialmap.database;

import java.util.*;

import com.whitehole.socialmap.network.*;

import android.content.*;
import android.database.sqlite.*;
import android.util.*;

public class MyDBHelper extends SQLiteOpenHelper {
	public static final String DATABASENAME = "socialmap";
	
	
	public MyDBHelper(Context context) {
		super(context, "myDB.db", null, 6);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("CREATE TABLE " + DATABASENAME + " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
		+ SPicture.PICTUREID + " TEXT, "
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
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + DATABASENAME);
		onCreate(db);
	}

	/**
	 * 네트워크 함수로 부터 받아온 정보를 DB에 추가하여 저장하는 함수
	 * @param mHelper
	 * @param sp
	 */
	public static void pushDataToDB(MyDBHelper mHelper, ArrayList<SPicture> sp){
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
	public static void deleteDataOnDB(MyDBHelper mHelper){
		SQLiteDatabase db;
		
		db = mHelper.getWritableDatabase();
		
		db.delete(DATABASENAME, null, null);
		//mHelper.close();
	}
	
	public static void deleteFriendsDataOnDB(MyDBHelper mHelper, ArrayList<FriendsList> friends){
		SQLiteDatabase db;
		
		db = mHelper.getWritableDatabase();
		
		//FriendsID가 null 이면 모든 친구 데이터를 지움
		if(friends == null)
			db.delete(DATABASENAME, SPicture.ISPUBLIC + " = 2", null);
		else{
			for(FriendsList s : friends){
				db.delete(DATABASENAME, SPicture.OWNERID + " = '" + s.id + "'", null);
			}
		}
		
		//mHelper.close();
	}
	
	public static void deletePictureOnDB(MyDBHelper mHelper, String picID){
		SQLiteDatabase db;
		
		db = mHelper.getWritableDatabase();
		
		if(picID != null && !picID.equals(""))
			db.delete(DATABASENAME, SPicture.PICTUREID + " = '" + picID + "'", null);
	}
	
	public static void modifyIsPublic(MyDBHelper mHelper, String picID, int isPublic){
		SQLiteDatabase db;
		ContentValues values = new ContentValues();
		
		values.put(SPicture.ISPUBLIC, isPublic);
		
		db = mHelper.getWritableDatabase();
		
		db.update(DATABASENAME, values, SPicture.PICTUREID + " = '" + picID + "'", null);
	}
	
	public static void modifyUserThumnailOnDB(MyDBHelper mHelper, String userID, String ownerThumURL){
		SQLiteDatabase db;
		ContentValues values = new ContentValues();
		
		values.put(SPicture.OWNERTHUMURL, ownerThumURL);
		
		db = mHelper.getWritableDatabase();
		
		db.update(DATABASENAME, values, SPicture.OWNERID + " = '" + userID + "'", null);
	}
}
