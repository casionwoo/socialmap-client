package com.whitehole.socialmap.upload;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.whitehole.socialmap.R;
import com.whitehole.socialmap.database.MyDBHelper;
import com.whitehole.socialmap.etc.ApplicationClass;
import com.whitehole.socialmap.etc.MyHandlerThread;
import com.whitehole.socialmap.etc.ThreadExcutor;
import com.whitehole.socialmap.network.CheckPublic;
import com.whitehole.socialmap.network.SPicture;
import com.whitehole.socialmap.network.UpdateAfterTime;
import com.whitehole.socialmap.network.UpdateBeforeTime;
import com.whitehole.socialmap.network.server.PostDeleteSever;

public class PostPicture extends Activity{
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private boolean pendingPublishReauthorization = false;

	private UiLifecycleHelper uiHelper;
	
	ImageView fphoto;				// 갤러리에서 선택한 이미지를 보여주는 이미지뷰
	EditText fedit;					// 페이스북에 올릴 게시글을 적는 부분
	TextView ftxt;	
	//Button gallery;					// 갤러리로 가는 버튼
	Button postphoto;				// 사진을 올리는 버튼
	String privacy;
	Spinner place_spin;
	Spinner privacy_spin;
	Uri photoUri;				
	Bundle params = new Bundle();
	String tempPicturePath;

	ExifInterface exif;
	byte[] bMapArray;
	String description;
	double latitude, longitude;
	String page_id;
	ArrayList<String> pId_list = new ArrayList<String>();
	ArrayList<String> pName = new ArrayList<String>();

    private static int MAX_IMAGE_DIMENSION = 720;
    final static int TAKE_GALLERY = 1;
    
    ApplicationClass applicationClass;
    ConnectivityManager mgr;
    SharedPreferences pref;
    Handler mhandler;
    MyHandlerThread ht;
    MyDBHelper mHelper;
	SQLiteDatabase db;
	FacebookRequestError error;
	
	Bitmap bmp;
	String path;
	boolean locationPic = false;
	Bitmap srcBitmap;
	
	boolean loading = false;
//	//뒤로가기 키 입력
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		
//		if(keyCode == KeyEvent.KEYCODE_BACK)
//		{
//			if(applicationClass.backKey == 1 && 
//					(System.currentTimeMillis() - applicationClass.backTime) > 2000 ){
//				//토스트로 종료하려면 한번더 누르라는 안내 메시지 띄움
//				Toast.makeText(PostPicture.this, R.string.exit_app,
//		                Toast.LENGTH_SHORT).show();
//				applicationClass.backTime = System.currentTimeMillis();
//				
//				
//			}
//			//뒤로가기 버튼 누르고 2초 안에 뒤로가기 버튼 다시 누르는 경우
//			else if(applicationClass.backKey == 1){
//				finish();
//			}
//			
//			return true;
//		}
//		
//		return super.onKeyDown(keyCode, event);
//	}
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	        
//	        ThreadExcutor.execute(new Runnable() {
//	    		@Override
//				public void run() {
	    		
//	    			try {
//						Thread.sleep(1000);
//						 
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
	    			
	    			//Looper.prepare();
	    			//mLoop = Looper.myLooper();
	    			
//			    	mhandler.post(new Runnable() {
//						
//						@Override
//						public void run() {
//							ht = new MyHandlerThread(PostPicture.this, "wait", "Please wait while loading...");
//							ht.start();
////							loading = true;
//						}
//			    	});
//	    		}
//			});
	    }
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_picture);
		
		mHelper = new MyDBHelper(this);
		mhandler = new Handler();
		pref = getSharedPreferences("Pref", 0);
		
		applicationClass = (ApplicationClass)getApplicationContext();
		mgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		
	    uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
		
		fphoto = (ImageView)findViewById(R.id.face_photo);
		fedit = (EditText)findViewById(R.id.face_edit);
		place_spin = (Spinner) findViewById(R.id.place_select);
		privacy_spin = (Spinner) findViewById(R.id.privacy_select);
		//gallery = (Button)findViewById(R.id.go_gallery);
		postphoto = (Button)findViewById(R.id.post_photo);

		Intent i = getIntent();
		path = i.getStringExtra("path");
		latitude = i.getDoubleExtra("lat", 0.0);
		longitude = i.getDoubleExtra("lng", 0.0);
		
		if(!pref.getBoolean("PPicAlert", false)){
			
			
			LayoutInflater adbInflater = LayoutInflater.from(PostPicture.this);
		    View notshowagainLayout = adbInflater.inflate(R.layout.notshowagain, null);
		 
		                               // 체크박스의 값을 읽어 오기 위해서
		    final CheckBox dontShowAgain = (CheckBox)notshowagainLayout.findViewById(R.id.skip);
			
			new AlertDialog.Builder(PostPicture.this)
			.setTitle(getResources().getString(R.string.notices))
			.setIcon(R.drawable.socialmapappicon)
			.setMessage(getResources().getString(R.string.app_activity_option))
			.setView(notshowagainLayout)
			.setPositiveButton(getResources().getString(R.string.go_to_option_page), new DialogInterface.OnClickListener() {
	
				@Override
				public void onClick(
						DialogInterface dialog,
						int which) {
					// 앱 설정 페이지로 이동
					// 안드로이드 웹 페이지 호출
					String url = "https://m.facebook.com/privacy/touch/apps/permissions/?appid=" +
									applicationClass.appID+"&__user=" +
									applicationClass.userID;
					Uri uri = Uri.parse(url);
					Intent it = new Intent(Intent.ACTION_VIEW,uri);
					startActivity(it);
				}
				
			})
			.setNegativeButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
	
				@Override
				public void onClick(
						DialogInterface dialog,
						int which) {

					// 만일 다시보지않음에 체크되어 있다면
					if(dontShowAgain.isChecked()){
						SharedPreferences.Editor edit = pref.edit();
						
						edit.putBoolean("PPicAlert", true);
						
						edit.commit();
					}
				}
				
			})
			.show();
		}
		
		if (savedInstanceState != null) {
		    pendingPublishReauthorization = 
		        savedInstanceState.getBoolean(PENDING_PUBLISH_KEY, false);
		}		
		place_spin.setVisibility(Spinner.INVISIBLE);
		privacyMenu();
		
//		gallery.setOnClickListener(new OnClickListener() {
//            public void onClick(View view) { 
//            	goGallery();
//            }
//        });
		postphoto.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				
				
				
				if (applicationClass.session != null && applicationClass.session.isOpened()
		    			 && mgr.getActiveNetworkInfo() != null) {
					
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
									ht = new MyHandlerThread(PostPicture.this, "wait", "Please wait while loading...");
									ht.start();
									loading = true;
								}
					    	});
			    		}
					});
					
					try {
						PostToFacebook();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		
		settingImage();
	}
	
	private void settingImage(){
//		pName.clear();
//		pId_list.clear();
//		longitude = 0;
//		latitude = 0;
//		page_id="";
//		place_spin.setVisibility(Spinner.INVISIBLE);
		BitmapFactory.Options bounds = new BitmapFactory.Options();

        bounds.inSampleSize = 4;
        
		bmp = BitmapFactory.decodeFile(path, bounds);
		fphoto.setImageBitmap(bmp);
		
		try {
			bMapArray = scaleImage(getApplicationContext(), path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		GetPlaceId();
	}

	private void loadData() {
//		runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				
		
		ArrayList<SPicture> sp = new ArrayList<SPicture>();
		
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
						Cursor facebookTmpCursor;
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
						
						for(SPicture s : sp){
							if(s.isPublic == 1)
								fsp.add(s);
						}
						
						if(sp!=null && sp.size() >0)
							MyDBHelper.pushDataToDB(mHelper, sp);
		
//						//최후 업뎃시간 이전 데이터 업데이트 하기
//						//우선 내 사진 모두를 가져옴
//						db = mHelper.getWritableDatabase();
//						facebookTmpCursor = db.query(MyDBHelper.DATABASENAME, null, SPicture.ISPUBLIC + " = 0 OR "+ SPicture.ISPUBLIC +" = 1", null, null, null, null);
//						
//						while(facebookTmpCursor.moveToNext()){
//							cp.add(new CheckPublic(facebookTmpCursor.getString(facebookTmpCursor.getColumnIndex(SPicture.PICTUREID)),
//									facebookTmpCursor.getInt(facebookTmpCursor.getColumnIndex(SPicture.ISPUBLIC))));
//						}
//						
//						if(cp.size()>0){
//							
//							try {
//								cpAfter = ubt.ModifyBeforeTime(cp);
//							} catch (JSONException e) {
//								e.printStackTrace();
//							} catch (IOException e) {
//								e.printStackTrace();
//							}
//							
//							//4: 삭제됨, 5: 공개 -> 비공개, 6: 비공개 -> 공개
//							for(CheckPublic c : cpAfter){
//								if(c.isPublic == 4){
//									//DB에서 삭제
//									MyDBHelper.deletePictureOnDB(mHelper, c.pictureID);
//									//Log.w("FacebookDelete", "Delete!!!");
//									cp_id.add(c.pictureID);
//								}else if(c.isPublic == 5){
//									//DB의 해당 row ispublic 을 0으로 변경
//									MyDBHelper.modifyIsPublic(mHelper, c.pictureID, 0);
//									//서버에서 삭제 해야할 데이터는 4로 통일
//									//c.isPublic = 4;
//									cp_id.add(c.pictureID);
//									//Log.w("FacebookPrivate", "Private!!!");
//								}else if(c.isPublic == 6){
//									//DB의 해당 row ispublic 을 1으로 변경
//									MyDBHelper.modifyIsPublic(mHelper, c.pictureID, 1);
//									
//									//서버에서는 추가 해야할 데이터는 1로 통일
//									//c.isPublic = 1;
//									//sp에 row 정보 추가하기!
//									facebookTmpCursor = db.query(MyDBHelper.DATABASENAME, null, SPicture.PICTUREID + " = '" + c.pictureID +"'", null, null, null, null);
//									
//									if(facebookTmpCursor.getCount() > 0){
//										facebookTmpCursor.moveToFirst();
//										
//										sp.add(new SPicture(c.pictureID, c.isPublic, 
//												facebookTmpCursor.getDouble(facebookTmpCursor.getColumnIndex(SPicture.LATITUDE)),
//												facebookTmpCursor.getDouble(facebookTmpCursor.getColumnIndex(SPicture.LONGITUDE)),
//												facebookTmpCursor.getString(facebookTmpCursor.getColumnIndex(SPicture.COMMENTURL)),
//												facebookTmpCursor.getString(facebookTmpCursor.getColumnIndex(SPicture.IMAGEURL)),
//												facebookTmpCursor.getString(facebookTmpCursor.getColumnIndex(SPicture.SMALLIMAGEURL)),
//												facebookTmpCursor.getLong(facebookTmpCursor.getColumnIndex(SPicture.MAKETIME)),
//												facebookTmpCursor.getString(facebookTmpCursor.getColumnIndex(SPicture.MAKETIMES)),
//												facebookTmpCursor.getString(facebookTmpCursor.getColumnIndex(SPicture.RECORD)),
//												facebookTmpCursor.getString(facebookTmpCursor.getColumnIndex(SPicture.OWNERNAME)),
//												facebookTmpCursor.getString(facebookTmpCursor.getColumnIndex(SPicture.OWNERID)),
//												facebookTmpCursor.getString(facebookTmpCursor.getColumnIndex(SPicture.OWNERTHUMURL))
//												));
//									}
//									//Log.w("FacebookPublic", "Public!!!");
//								}
//							}
//						}
						
						//sp 와 cpAfter의 ID 집합을 우리 서버로 보냄
						PostDeleteSever pds = new PostDeleteSever();
						
						try {
							if(fsp!=null && fsp.size() >0){
//								pds.PostPhotos(fsp, 1);
								pds.PostPhotos(fsp, 0);
							}
//							if(cpAfter!=null && cpAfter.size() >0){
//								pds.DeletePhotos(cp_id);
//							}
						} catch (IOException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}
						
					}
					
					
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
	
	private void privacyMenu() {
		// TODO Auto-generated method stub
		ArrayList<String> privacy_list = new ArrayList<String>();
		privacy_list.add(getResources().getString(R.string.everyone));
		privacy_list.add(getResources().getString(R.string.all_friends));
		privacy_list.add(getResources().getString(R.string.self));
		// 스피너
		ArrayAdapter<String> bb = new ArrayAdapter<String>(this, 
				android.R.layout.simple_spinner_item, privacy_list);
		bb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		// Spinner의 adapter로 aa를 지정
		privacy_spin.setAdapter(bb);
		privacy_spin.setPrompt(getResources().getString(R.string.privacy_scope));
		privacy_spin.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				switch(position){
				case 0:
					privacy = "EVERYONE";
					break;
				case 1:
					privacy = "ALL_FRIENDS";
					break;
				case 2:
					privacy = "SELF";
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				page_id = "";
			}
		});
	}

	// 갤러리 화면 띄우기
	protected void goGallery() {
		// TODO Auto-generated method stub
		pName.clear();
		pId_list.clear();
		longitude = 0;
		latitude = 0;
		page_id="";
		place_spin.setVisibility(Spinner.INVISIBLE);
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
		startActivityForResult(intent,TAKE_GALLERY);
		// intent는 시작할 Activity, TAKE_GALLERY는 호출한 Activity가 종료 되었을때
		// onActivityResult가 호출되고 이 때 넘어오는 값이다.
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// startActivityForResult로 호출된 Activity가 종료 후 호출된다.
		// requestCode는 startActivityForResult로 호출시 전달한 Code임.
		// result Code는 Activity가 종료될 때 전달되는 Code이다.
		// 해당 Activity가 종료되고 전달되는 data이다.
	    uiHelper.onActivityResult(requestCode, resultCode, data);
		if(requestCode == TAKE_GALLERY && resultCode == Activity.RESULT_OK){
            photoUri = data.getData();
            String path = getRealPathFromURI(photoUri);
            tempPicturePath = path;
            
            try {
            	bMapArray = scaleImage(getApplicationContext(), path);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}            
            // 사진의 position 정보 얻기
            GetInformPosition(path);
		}
	}

	// 이미지 얻기
	private byte[] scaleImage(Context context, String path) throws IOException {
		// TODO Auto-generated method stub
		Uri fileUri = Uri.parse(path);
		String filePath = fileUri.getPath();
		Cursor c = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null,"_data ='" + filePath + "'",null,null);
		c.moveToNext();
		int id = c.getInt(0);
		photoUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,id);

		c.close();
		
		Log.e("URI",photoUri.toString());
		//photoUri = Uri.fromFile(new File(path));
		InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();

        int rotatedWidth, rotatedHeight;
        int orientation = getOrientation(context, photoUri);

        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }

        is = context.getContentResolver().openInputStream(photoUri);
        if (rotatedWidth > MAX_IMAGE_DIMENSION || rotatedHeight > MAX_IMAGE_DIMENSION) {
            float widthRatio = ((float) rotatedWidth) / ((float) MAX_IMAGE_DIMENSION);
            float heightRatio = ((float) rotatedHeight) / ((float) MAX_IMAGE_DIMENSION);
            float maxRatio = Math.max(widthRatio, heightRatio);

            // Create the bitmap from file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            srcBitmap = BitmapFactory.decodeStream(is, null, options);
        } else {
            srcBitmap = BitmapFactory.decodeStream(is);
        }
        is.close();

        /*
         * if the orientation is not 0 (or -1, which means we don't know), we
         * have to do a rotation.
         */
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }

        String type = context.getContentResolver().getType(photoUri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (type.equals("image/png")) {
            srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        } else if (type.equals("image/jpg") || type.equals("image/jpeg")) {
            srcBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        }
        byte[] bMapArray = baos.toByteArray();
        baos.close();
        
        // 이미지 뷰에 이미지 띄우기
        setAlbumImage(srcBitmap);
        
		return bMapArray;
	}

	// 사진의 회전방향 얻기
    public static int getOrientation(Context context, Uri photoUri) {
        /* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

        if (cursor == null || cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        int orientation = cursor.getInt(0);
        cursor.close();

        return orientation;
    }
	
	// 이미지 뷰에 이미지 띄우기
	private void setAlbumImage(Bitmap bmp) {
		/// 읽은 배트맵을 형변환해서 새로 생성
        BitmapDrawable dbmp = new BitmapDrawable( bmp );
		Drawable dr = (Drawable)dbmp ;	/// 그걸 다시 형변환
		fphoto.setImageDrawable( dr ) ; /// 뷰 객체의 백그라운드로 설정
		//iv.setBackgroundDrawable( dr ) ; /// 뷰 객체의 백그라운드로 설정
	}

	// 이미지 파일의 절대 주소 얻기
	private String getRealPathFromURI(Uri photoUri2) {
		// TODO Auto-generated method stub
		String path = "content://media"+photoUri2.getPath();
		Log.e("path", path);
		Cursor c=getContentResolver().query(Uri.parse(path), null, null, null, null);
		c.moveToNext();		
		String filepath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
		Log.e("filepath", filepath);
		
		return filepath;
	}

	// 위치정보 얻기
	private void GetInformPosition(String path) {
		// TODO Auto-generated method stub
		try {
			exif = new ExifInterface(path);
			
			float[] LatLong = new float[2];
			if(exif.getLatLong(LatLong)){
				latitude = LatLong[0];
				longitude = LatLong[1];
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Log.e("위치정보", "latitude : " + latitude + "\n longitude : " + longitude + "\n");
		
		GetPlaceId();
	}
	
	private void GetPlaceId() {
		// TODO Auto-generated method stub
//		String fqlQuery = "SELECT page_id FROM location_post WHERE distance(latitude, longitude, \"" +
//				latitude + "\", \"" + longitude +"\") < 2000";
		String fqlQuery = "SELECT page_id,name FROM place WHERE distance(latitude, longitude, \"" +
				latitude + "\", \"" + longitude +"\") < 300";
		
		Log.e("fqlQuery", fqlQuery);
		
		// 장소 Page_id json 구하기
		String qry = GetFqlJson(fqlQuery);
		Log.e("qry", qry);
		
		// 장소 List 구하기
		try {
			GetPId_List(qry);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// 장소가 존재한다면
		if(longitude != 0 && latitude != 0){
			place_spin.setVisibility(Spinner.VISIBLE);
			// 스피너
			ArrayAdapter<String> aa = new ArrayAdapter<String>(this, 
					android.R.layout.simple_spinner_item, pName);
			aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			// Spinner의 adapter로 aa를 지정
			place_spin.setAdapter(aa);
			place_spin.setPrompt(getResources().getString(R.string.pick_a_place));
			place_spin.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					if(position == pName.size()-1 || !(position < pId_list.size()))
						page_id = "";
					else{
						page_id = pId_list.get(position);
						locationPic = true;
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					page_id = "";
				}
			});
		}
		// 장소의 내역이 없을때
		else{
			ArrayAdapter<String> aa = new ArrayAdapter<String>(this, 
					android.R.layout.simple_spinner_item);
			//aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			// Spinner의 adapter로 aa를 지정
			place_spin.setAdapter(aa);
			place_spin.setPrompt(getResources().getString(R.string.not_existed_place_page));
			page_id="";
			
		}
	}

	@Override
	public void onResume() {
	    super.onResume();
	    
	    // For scenarios where the main activity is launched and user
	    // session is not null, the session state change notification
	    // may not be triggered. Trigger it if it's open/closed.
	    Session session = Session.getActiveSession();
	    if (session != null &&
	           (session.isOpened() || session.isClosed()) ) {
	        onSessionStateChange(session, session.getState(), null);
	    }
	    Log.w("resume", "resume call");
	    
	    if(loading){
	    	Log.w("resume", "resume call loading");
	    	loading = false;
	    	
	    	MyHandlerThread.stop(ht);
	    }

	    uiHelper.onResume();
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	    mHelper.close();
	    bmp.recycle();
	    srcBitmap.recycle();
	    System.gc();
	}

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (pendingPublishReauthorization && 
		        state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
		    pendingPublishReauthorization = false;
		    
//		    if(loading==false){
		    
				ThreadExcutor.execute(new Runnable() {
		    		@Override
					public void run() {
		    		
	//	    			try {
	//						Thread.sleep(1000);
	//						 
	//					} catch (InterruptedException e) {
	//						e.printStackTrace();
	//					}
		    			
		    			//Looper.prepare();
		    			//mLoop = Looper.myLooper();
		    			
				    	mhandler.post(new Runnable() {
							
							@Override
							public void run() {
								ht = new MyHandlerThread(PostPicture.this, "wait", "Please wait while loading...");
								ht.start();
								//loading = true;
							}
				    	});
		    		}
				});
//		    }
		    
		    try {
				PostToFacebook();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
	    uiHelper.onSaveInstanceState(outState);
	}
	// 고려해야 할것 1. 올리기 전에 사진 정보 보내주는거 2. 장소 리스트로 나타내기
	// 3. 올렸다고 메세지 띄우기 4. privacy 선택여부
	// 장소 나타내기에서 json에 있는지 없는지 확인후 있으면 리스트내역, 없을시엔 장소 없음
	// 페이스북에 올리기
	@SuppressLint("NewApi")
	private void PostToFacebook() throws JSONException {
		// TODO Auto-generated method stub
		Session session = Session.getActiveSession();
				
		description = fedit.getText().toString();
		
				
		// Check for publish permissions    
        List<String> permissions = session.getPermissions();
        if (!isSubsetOf(PERMISSIONS, permissions)) {
            pendingPublishReauthorization = true;
            Session.NewPermissionsRequest newPermissionsRequest = new Session
                    .NewPermissionsRequest(this, PERMISSIONS);
        session.requestNewPublishPermissions(newPermissionsRequest);
            return;
        }
		
		Bundle params = new Bundle();
		params.putByteArray("photo", bMapArray);
		params.putString("caption", description);
		//params.putString("description", description);
		
		if(!page_id.isEmpty())
			params.putString("place", page_id);
		
		// value = EVERYONE, ALL_FRIENDS, FRIENDS_OF_FRIENDS, CUSTOM, SELF
		JSONObject jsonObject = new JSONObject();
//		jsonObject.put("description", "Public");
		jsonObject.put("value", privacy);
		params.putString("privacy", jsonObject.toString());
//		params.putString("", )
//		
//        Request.Callback callback= new Request.Callback() {
//            public void onCompleted(Response response) {
//                JSONObject graphResponse = response
//                                           .getGraphObject()
//                                           .getInnerJSONObject();
//                String postId = null;
//                try {
//                    postId = graphResponse.getString("id");
//                } catch (JSONException e) {
//                    Log.i("Post Photo",
//                        "JSON error "+ e.getMessage());
//                }
//                FacebookRequestError error = response.getError();
//                if (error != null) {
//                    Toast.makeText(getApplicationContext(),
//                         error.getErrorMessage(),
//                         Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(getApplicationContext(), 
//                             postId,
//                             Toast.LENGTH_LONG).show();
//                }
//            }
//        };

        Request request = new Request(session, "me/photos", params, 
                              HttpMethod.POST, new Request.Callback() {
								
								@Override
								public void onCompleted(Response response) {
									// TODO Auto-generated method stub
										JSONObject graphResponse = response
			                                       .getGraphObject()
			                                       .getInnerJSONObject();
							            String postId = null;
							            try {
							                postId = graphResponse.getString("id");
							                
							                
							            } catch (JSONException e) {
							                Log.i("Post Photo",
							                    "JSON error "+ e.getMessage());
							            }
							            
							            Log.w("sdfsd", "hhh");
							            error = response.getError();
					//		            if (error != null) {
					//		                Toast.makeText(getApplicationContext(),
					//		                     error.getErrorMessage(),
					//		                     Toast.LENGTH_SHORT).show();
					//		                } else {
					//		                    Toast.makeText(getApplicationContext(), 
					//		                         postId,
					//		                         Toast.LENGTH_LONG).show();
					//		            }
							            
							            if (applicationClass.session != null && applicationClass.session.isOpened()
								    			 && mgr.getActiveNetworkInfo() != null && locationPic) {
											// 사진 받아오기 함수
									    	ThreadExcutor.execute(new Runnable() {
									    		@Override
												public void run() {
									    		
									    			locationPic = false;
					//				    			try {
					//									Thread.sleep(1000);
					//									 
					//								} catch (InterruptedException e) {
					//									e.printStackTrace();
					//								}
									    			
									    			//Looper.prepare();
									    			//mLoop = Looper.myLooper();
									    			
											    	mhandler.post(new Runnable() {
														
														@Override
														public void run() {
															
					//										ht = new MyHandlerThread(PostPicture.this, "wait", "Please wait while loading...");
					//										ht.start();
															
															
					//										long time = System.currentTimeMillis();
					//										while(!updateCompleted){
					//											
					//											Log.w("loading", "loading...");
					//											
					//											try {
					//												Thread.sleep(2000);
					//											} catch (InterruptedException e) {
					//												// TODO Auto-generated catch block
					//												e.printStackTrace();
					//											}
					//											
					//											if(error != null || System.currentTimeMillis() - time > 30000){
					//												updateCompleted = false;
					//												break;
					//											}
					//										}
																if(error != null){
																	
																	Toast.makeText(PostPicture.this, R.string.upload_fail,
															                Toast.LENGTH_SHORT).show();
																	
																}else{
																	
																	//주작업
													    			loadData();
																	
													    			Log.w("LoadData", "end");
													    			
													    			//handler.sendEmptyMessage(1);
													    			
													    			applicationClass.uploadPicture = true;
													    			
													    			
													    			
													    			//mBackHandler.sendEmptyMessage(0);
													    			
													    			//Looper.loop();
													    			
												    				//handler.sendEmptyMessage(0);  // 작업이 끝나면 이 핸들러를 호출
								//									db = mHelper.getReadableDatabase();
								//									facebookCursor = db.query(MyDBHelper.DATABASENAME, null, null, null, null, null, null);
																
													    			Toast.makeText(PostPicture.this, R.string.post_completed,
															                Toast.LENGTH_SHORT).show();
																}
															
															loading = false;
															MyHandlerThread.stop(ht);
															finish();
														}
													});
					
									    		}
									    	});
										}else{
										
											locationPic = false;
											
											if(error != null){	
												Toast.makeText(PostPicture.this, R.string.upload_fail,
										                Toast.LENGTH_SHORT).show();
												
											}else{
												Toast.makeText(PostPicture.this, R.string.post_completed,
										                Toast.LENGTH_SHORT).show();
											}
											
											MyHandlerThread.stop(ht);
											finish();
										}
							            
							            
					}
				}){
	        	
	        };
	
	        RequestAsyncTask task = new RequestAsyncTask(request);
	        task.execute();
	        
        clearAll();
	}

	// 장소 List 구하기
	private void GetPId_List(String qry) throws JSONException {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject(qry);
		JSONArray arr_data = json.getJSONArray("data");
		
		if(arr_data.length() != 0)
		{
			for(int i=0; i<arr_data.length(); i++)
			{
				JSONObject data = arr_data.getJSONObject(i);
				String page_id = data.getString("page_id");
				pId_list.add(page_id);
				
				String page_name = data.getString("name");
				pName.add(page_name);
			}
			pName.add(getResources().getString(R.string.not_select_place));
		}
	}
	
	private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
	    for (String string : subset) {
	        if (!superset.contains(string)) {
	            return false;
	        }
	    }
	    return true;
	}
	
	private String GetFqlJson(String singlequery) {
		// TODO Auto-generated method stub
				Bundle params = new Bundle();
				params.putString("q", singlequery);
				Session session = Session.getActiveSession();
				
				
				// fql 결과 json 받아오기
				final Request request = new Request(session, "/fql", params, HttpMethod.GET
						, new Request.Callback() {
					
					@Override
					public void onCompleted(Response response) {
						// TODO Auto-generated method stub
					}
				});
				
				RequestAsyncTask getrequest = Request.executeBatchAsync(request);
				
				List<Response> getquery = new ArrayList<Response>();
				try {
					getquery = getrequest.get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// response를 String으로 받아서 Json형식에 맞게 바꿔서 받아옴
				String fql = fqlParse(getquery.get(0).toString());
		return fql;
	}


	// fql에서 받은 string 불필요한 부분 잘라내기
	private String fqlParse(String fql) {
		// TODO Auto-generated method stub
		try{
			Log.e("get json fql", fql);
			String[] str = fql.split("state=");
			String[] json = str[1].split("\\}, error:");

			return json[0];
		} catch(RuntimeException e){
			e.printStackTrace();
		}
		return null;
	}
	
	
	// 이미지뷰와 에디터뷰 초기화
	private void clearAll(){
		fphoto.setImageDrawable(null);
		fedit.setText(null);
		pName.clear();
		pId_list.clear();
		longitude = 0;
		latitude = 0;
		page_id="";
		place_spin.setVisibility(Spinner.INVISIBLE);
	}
	
}