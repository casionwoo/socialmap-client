package com.whitehole.socialmap;

import java.io.IOException;
import java.util.*;

import com.whitehole.socialmap.R;
import com.whitehole.socialmap.etc.*;
import com.whitehole.socialmap.upload.PostPicture;

import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.MediaStore.Images.*;
import android.app.*;
import android.content.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.*;
import android.view.Menu;
import android.view.View;
import android.widget.*;

public class InfoWinfullLocalActivity extends Activity {

	String path, from;
	double lat, lng;
	ApplicationClass applicationClass;
	int pointIdx;
	Bitmap bmp;
	ConnectivityManager mgr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info_winfull_local);
		
		mgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		applicationClass = (ApplicationClass)getApplicationContext();
		
		ImageView iview = (ImageView) findViewById(R.id.info_image);
		
		Intent intent = getIntent();
		
		path = intent.getStringExtra("path");
		lat = intent.getDoubleExtra("lat", 0.0);
		lng = intent.getDoubleExtra("lng", 0.0);
		from = intent.getStringExtra("from");
		pointIdx = intent.getIntExtra("CurIdx", -1);
		
		BitmapFactory.Options bounds = new BitmapFactory.Options();

        bounds.inSampleSize = 4;
		
		//iview.setScaleType(ImageView.ScaleType.CENTER_CROP);
		
		try{
			bmp = BitmapFactory.decodeFile(path);
		}catch (OutOfMemoryError ex)
		{
			bmp = BitmapFactory.decodeFile(path, bounds);
		}
		
		if(applicationClass.thumWidth > bmp.getWidth()){
			Log.w("InfoWinfulllocal", "rr");
			bmp = BitmapFactory.decodeFile(path);
		}
		
		//다음 두 줄은 이미지가 똑바로 안보일 때 해당 이미지를 회전하는 코드
		int degree = getExifOrientation(path);
		bmp = getRotatedBitmap(bmp, degree);
		
		iview.setImageBitmap(bmp);
	
		Button deleteImage = (Button) findViewById(R.id.delete_image);
		deleteImage.setText(getResources().getString(R.string.delete));
		deleteImage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				new AlertDialog.Builder(InfoWinfullLocalActivity.this)
				.setTitle(getResources().getString(R.string.delete_photo))
				.setIcon(R.drawable.socialmapappicon)
				.setItems(new String[] {getResources().getString(R.string.delete)}, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//만약 사용자가 해당 사진의 삭제를 선택했다면
						if(which==0){
							new AlertDialog.Builder(InfoWinfullLocalActivity.this)
							.setTitle(getResources().getString(R.string.delete_photo))
							.setIcon(R.drawable.socialmapappicon)
							.setMessage(getResources().getString(R.string.sure_delete))
							.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

								@Override
								public void onClick(
										DialogInterface dialog,
										int which) {
									
									applicationClass.modifyPicture = true;
									applicationClass.deletePicPaths.add(path);
									
									Toast.makeText(InfoWinfullLocalActivity.this, R.string.delete_completed,
							                Toast.LENGTH_SHORT).show();
									
									if(from.equals("ListView")){
										//TODO: 커서 조정 및 재 클러스터링, 그리드 뷰 새로고침
										applicationClass.modPictureGrid = true;
										applicationClass.deletePictureGridIdx = pointIdx;
									}
									
									finish();
									
								}
								
							})
							.setNegativeButton(getResources().getString(R.string.no), null)
							.show();
						}
					}
				})
				.setNegativeButton(getResources().getString(R.string.cancel), null)
				.show();
				
			}
		});
		
		Button postImage = (Button) findViewById(R.id.upload_image);
		postImage.setText(getResources().getString(R.string.share));
		postImage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(applicationClass.session != null && applicationClass.session.isOpened()
						 && mgr.getActiveNetworkInfo() != null)
				{
					if(applicationClass.appID.length() <= 0)
						applicationClass.appID = applicationClass.session.getApplicationId();
					
					Intent i = new Intent(InfoWinfullLocalActivity.this, PostPicture.class);
					i.putExtra("path", path);
					i.putExtra("lat", lat);
					i.putExtra("lng", lng);
					
					startActivity(i);
				}else
					Toast.makeText(InfoWinfullLocalActivity.this, R.string.uploadFacebookLoginReq,
			                Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		bmp.recycle();
		System.gc();
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.activity_info_winfull_local, menu);
//		return true;
//	}
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
